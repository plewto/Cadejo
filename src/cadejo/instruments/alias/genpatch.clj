(println "-->    alias genpatch")

(ns cadejo.instruments.alias.genpatch
  (:use [cadejo.instruments.alias.program])
  (:require [cadejo.util.math :as math :reload true])
  (:require [cadejo.instruments.alias.constants :as constants]))

(def coin math/coin)

;; Return -n or +n with prop p of negative
;;
(defn flip 
  ([n p]
     (* (coin p -1 +1) n))
  ([n]
     (flip n 0.5)))

(defn pick-common [p-port]
  (let [p (or p-port 0.1)
        ptime (coin p (rand) 0.0)]
    (common :port-time ptime)))
  


(def envelope-styles '[:asr :adsr :addsr :aadsr :perc :blip])

;; [min-time max-time]
(def envelope-ranges {:very-short  [0.000 0.050]
                      :short       [0.000 0.100]
                      :medium      [0.100 0.500]
                      :long        [0.500 1.000]
                      :very-long   [1.000 4.000]
                      :glacial     [3.000 12.00]})

(defn get-envelope-time [range]
  (let [limits (get envelope-ranges range [0.0 1.0])
        bias (first limits)
        scale (second limits)
        delta (- scale bias)]
    (+ bias (rand delta))))

;; return list [t1 t2 t3 t4]
;;
(defn get-envelope-times 
  ([hint]
     (print " ")
     (let [acc* (atom [])]
       (dotimes [n 4]
         (let [ignore (coin 0.2 true false)]
           (if ignore 
             (let [temp-hint (rand-nth (keys envelope-ranges))]
               (printf "%-11s " temp-hint)
               (swap! acc* (fn [n](conj n (get-envelope-time temp-hint)))))
             (do
               (printf "%-11s " hint)
               (swap! acc* (fn [n](conj n (get-envelope-time hint))))))))
       @acc*))
  ([]
     (get-envelope-times (rand-nth (keys envelope-ranges)))))
          
;; return list [:pk p :bp b :sus s]
;;
(defn pick-envelope-levels 
  ([style]
     (cond (= style :asr)
           [:pk 1.0 :bp 1.0 :sus 1.0]
           
           (= style :adsr)
           (let [bp (+ 0.75 (rand 0.25))]
             [:pk 1.0 :bp bp :sus bp])
           
           (= style :addsr)
           (let [bp (+ 0.90 (rand 0.10))
                 sus (+ 0.70 (rand 0.20))]
             [:pk 1.0 :bp bp :sus sus])
           
           (= style :aadsr)
             [:pk (rand) :bp 1.0 :sus (+ 0.5 (rand 0.5))]
           
           (= style :perc)
           [:pk 1.0 :bp (+ 0.8 (rand 0.2)) :sus (rand 0.3)]
           
           :default ; blip
           (let [bp (rand)
                 range (- 1.0 bp)
                 sus (+ bp (rand range))]
             [:pk 1.0 :bp bp :sus sus])))
  ([]
     (let [sty (rand-nth envelope-styles)]
       (pick-envelope-levels sty))))

(defn pick-envelope 
  ([n style-hint time-hint]
     (let [shint (or style-hint (rand-nth envelope-styles))
           thint (or time-hint (rand-nth (keys envelope-ranges)))]
       (printf ";; env%d %-6s  " n shint)
       (let [invert (coin 0.05 true false)
             args (concat (get-envelope-times thint)
                          (pick-envelope-levels shint)
                          (if (and (or (= n 1)(= n 2)) invert)
                            (do
                              (print " invert")
                              '(:invert 1))))]
         (let [rs (cond (= n 1)
                        (apply #'env1 args)
                        (= n 2)
                        (apply #'env2 args)
                        :default
                        (apply #'env3 args))]
           (println)
           rs))))
  ([n]
     (pick-envelope n nil nil)))
 
;; lfo1 dedicated to vibrato    
(defn pick-vibrato-lfo []
  (let [mn 3.0
        mx 8.0 
        delta (- mx mn)
        freq (+ mn (rand delta))
        wave (coin 0.80 0.5 (+ 0.5 (flip (rand 0.3))))]
    (lfo1 :fm1 [:con freq]
          :fm2 [:off 0]
          :wave1 [:con wave]
          :wave2 [:off 0])))

(defn pick-lfo [n]
  (let [mn 0.001
        mx (+ mn (coin 0.5 4.0 8.0))
        delta (- mx mn)
        f1-src :con
        f1-depth (+ mn (rand delta))
        f2-src (rand-nth (concat (repeat 8 :off)
                                 '[:env1 :env2 :env3 :lfo1
                                   :lfnse :ccb :ccc :gate]))
        f2-depth (if (= f2-src :off) 0 (+ mn (rand delta)))
        w1-src :con
        w2-src (rand-nth (concat (repeat 8 :off)
                                 '[:env1 :env2 :env3 :lfo1
                                   :lfnse :ccb :ccc :gate]))
        w1-depth (coin 0.75 0.5 (rand))
        w2-depth (coin 0.75 0.5 (rand))
        args (list :fm1 [f1-src f1-depth]
                   :fm2 [f2-src f2-depth]
                   :wave1 [w1-src w1-depth]
                   :wave2 [w2-src w2-depth])]
    (apply (if (= n 2) #'lfo2 #'lfo3) args)))


(defn pick-stepper [n]
  (let [trig (coin 0.8 
                   (if (= n 1) :lfo1 :lfo2)
                   (rand-nth '(:lfo3 :div1 :div2 :div :lsnse :gate)))
        reset (coin 0.8 
                    :off
                    (rand-nth (remove (fn [n](= n trig))
                                      '(:lfo1 :lfo2 :lfo3 :div1 :div2 :div :sh :gate))))
        mn (int (min -2 (rand 16)))
        mx (int (max 2 (rand 16)))
        delta (- mx mn)
        step (int (/ delta (max 2 (rand delta))))
        ivalue mn
        bias 0
        scale (float (/ 1.0 delta))
        invert (coin 0.5 -1 +1)
        sfn (get {1 #'stepper1 2 #'stepper2} n)]
    (sfn :trig trig 
         :reset reset
         :min (* invert mn) 
         :max (* invert mx) 
         :step (* invert step)
         :ivalue (* invert ivalue)
         :bias bias
         :scale scale)))

(defn pick-divider1 []
  (let [src (coin 0.80 :off (rand-nth '[:env1 :env2 :env3 :lfo1 :lfo2 
                                        :step1 :step2 :div2 :lfnse :sh
                                        :gate]))
        dpth (if (= src :off) 0.0 (rand))
        pw (coin 0.80 0.5 (rand))
        gamut '[0 1 2 3 4 5 6 7 8 9]
        p1 (flip (rand-nth gamut))
        p3 (flip (rand-nth gamut))
        p5 (flip (rand-nth gamut))
        p7 (flip (rand-nth gamut))
        scale 0.1]
    (divider1 :pw pw :p1 p1 :p3 p3 :p5 p5 :p7 p7
              :bias 0 :scale scale)))

(defn pick-divider2 []
  (let [src (coin 0.80 :off (rand-nth '[:env1 :env2 :env3 :lfo1 :lfo2 
                                        :step1 :step2 :div1 :lfnse :sh
                                        :gate]))
        dpth (if (= src :off) 0.0 (rand))
        pw (coin 0.80 0.5 (rand))
        gamut '[0 1 2 3 4 5 6 7 8 9]
        p2 (flip (rand-nth gamut))
        p4 (flip (rand-nth gamut))
        p6 (flip (rand-nth gamut))
        p8 (flip (rand-nth gamut))
        scale 0.1]
    (divider2 :pw pw :p2 p2 :p4 p4 :p6 p6 :p8 p8
              :bias 0 :scale scale)))
            
(defn pick-lfnoise []
  (let [src (rand-nth (concat (repeat 22 :con)
                              '[:env1 :env2 :env3 :lfo1 :lfo2 :lfo3
                                :cca :ccb :ccc :ccd :press]))
        depth (rand 10)]
    (lfnoise :fm [src depth])))

(defn pick-sample-hold []
  (let [rate (rand 10)
        src (rand-nth (concat (repeat 12 :lfnse)
                              '[:lfo1 :lfo2 :lfo3 
                                :step1 :step2
                                :div1 :div2 :div]))]
    (sh :rate rate
        :src src
        :bias 0
        :scale 1)))
        
(defn pick-matrix []
  (let [pick1 (fn []
                (rand-nth (list :env3 :lfo2 :lfo3
                                :step1 :step2 :div1 :div2 :div
                                :lfnse :sh :period :press 
                                :cca :ccb :ccc :gate)))
        pick2 (fn []
                (let [off (coin 0.80 true false)]
                  (list (if off :con (rand-nth (list :env1 :env2 :env3
                                                     :lfo1 :lfo2 :lfo3
                                                     :step1 :step2 
                                                     :div1 :div2 :div
                                                     :lfnse :sh :press
                                                     :cca :ccb :ccc :ccd :gate)))
                        (if off 1.0 (rand)))))]
    (matrix :a1 [:lfo1 (rand 0.1)]  :a2 [:cca 1]
            :b1 [:env2 1]           :b2 [:con 1]
            :c1 [:env3 1]           :c2 [:con 1]
            :d1 [:lfo2 1]           :d2 [:con 1]
            :e1 [(pick1)(rand)]     :e2 (pick2)
            :f1 [(pick1)(rand)]     :f2 (pick2)
            :g1 [(pick1)(rand)]     :g2 (pick2)
            :h1 [(pick1)(rand)]     :h2 (pick2))))
         
                                
(defn pick-osc-freq [only-harmonics]
  (let [allow-subharmonics (coin 0.2 true false)
        harmonic-gamut (concat (if allow-subharmonics
                                 '(0.5 0.5 0.5 0.75)
                                 '())
                               (repeat 6 1.0)
                               '(1.2 1.25 1.33 1.5 1.667 1.75)
                               '(2 2 2 3 3 4))
        use-harmonics (or only-harmonics (coin 0.5 true false))
        detune (fn [](float (if use-harmonics 
                              (rand-nth harmonic-gamut)
                              (+ 0.50 (rand 3)))))
        bias (fn [] (int (if use-harmonics
                             (coin 0.75 0 (rand 2))
                             (coin 0.75 0 (rand 300)))))
        fm1 [:a 1 0]
        fm2 (fn [] [(coin 0.8 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
                    (coin 0.8 (rand 0.1)(rand 0.9))
                    (coin 0.5 0 (rand 0.1))])]
    (println ";; harmonic gamut " use-harmonics)
    (let [dt1 (detune)
          dt2 (detune)
          dt3 (detune)
          b1 (bias)
          b2 (bias)
          b3 (bias)
          m1 (fm2)
          m2 (fm2)
          m3 (fm2)]
      (printf  ";;\t 1 :detune %5.3f :bias %4d :fm2 %s\n" dt1 b1 m1)
      (printf  ";;\t 2 :detune %5.3f :bias %4d :fm2 %s\n" dt2 b2 m2)
      (printf  ";;\t 3 :detune %5.3f :bias %4d :fm2 %s" dt3 b3 m3)
      (println)
      (concat 
       (osc1-freq dt1 :bias b1 :fm1 fm1 :fm2 m1)
       (osc2-freq dt2 :bias b2 :fm1 fm1 :fm2 m2)
       (osc3-freq dt3 :bias b3 :fm1 fm1 :fm2 m3)))))
     
(defn pick-osc1-wave []
  (let [bias 1.0
        asrc (coin 0.80 :b (rand-nth '(:a :c :d :e :f :g :h)))
        adth (float (coin 0.80 0 (rand 16)))
        bsrc (coin 0.90 :off (rand-nth '(:a :c :d :e :f :g :h)))
        bdth (float (coin 0.5 (rand)(rand 8)))]
    (osc1-wave bias :w1 [asrc adth] :w2 [bsrc bdth])))

(defn pick-osc2-wave []
  (let [bias (+ 0.5 (rand 0.25))
        asrc (coin 0.75 :d :b)
        adth (rand 0.5)
        bsrc (coin 0.80 :off (rand-nth '(:a :c :e :f :g :h)))
        bdth (flip (rand 0.5))]
    (osc2-wave bias :w1 [asrc adth] :w2 [bsrc bdth])))

(defn pick-osc3-wave []
  (let [bias (coin 0.85 (rand)(rand 2))
        asrc (coin 0.80 :b (rand-nth '(:a :c :d :e :f :g :h)))
        adth (float (coin 0.80 0 (coin 0.75 (rand)(rand 2))))
        bsrc (coin 0.90 :off (rand-nth '(:a :c :d :e :f :g :h)))
        bdth (float (if (= bsrc :off) 0 (coin 0.75 (rand)(rand 2))))]
    (osc3-wave bias :w1 [asrc adth] :w2 [bsrc bdth])))

(defn pick-osc-amp [n amp]
  (let [s1 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d1 (if (= s1 :off) 0.0 (rand))
        l1 (if (= s1 :off) 0.0 (coin 0.50 (rand) 0.0))

        s2 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d2 (if (= s2 :off) 0.0 (rand))
        l2 (if (= s2 :off) 0.0 (coin 0.50 (rand) 0.0))
        p (flip (rand))]
    (cond (= n 1)
          (osc1-amp amp :am1 [s1 d1 l1] :am2 [s2 d2 l2] :pan p)
          (= n 2)
          (osc2-amp amp :am1 [s1 d1 l1] :am2 [s2 d2 l2] :pan p)
          :default
          (osc3-amp amp :am1 [s1 d1 l1] :am2 [s2 d2 l2] :pan p))))

(defn pick-noise [amp]
  (let [s1 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d1 (if (= s1 :off) 0.0 (rand))
        l1 (if (= s1 :off) 0.0 (coin 0.50 (rand) 0.0))
        
        s2 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d2 (if (= s2 :off) 0.0 (rand))
        l2 (if (= s2 :off) 0.0 (coin 0.50 (rand) 0.0))
        p (flip (rand))
        cr (rand)
        hp (+ 50 (coin 0.75 0 (rand 4000)))]
    (noise amp 
           :crackle cr :lp 10000 :hp hp
           :am1 [s1 d1 l1]
           :am2 [s2 d2 l2]
           :pan p)))

(defn pick-ringmod [amp]
  (let [car (flip (rand))
        mod -1
        s1 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d1 (if (= s1 :off) 0.0 (rand))
        l1 (if (= s1 :off) 0.0 (coin 0.50 (rand) 0.0))
        
        s2 (coin 0.75 :off (rand-nth '[:a :b :c :d :e :f :g :h]))
        d2 (if (= s2 :off) 0.0 (rand))
        l2 (if (= s2 :off) 0.0 (coin 0.50 (rand) 0.0))
        p (flip (rand))]
    (ringmod amp :carrier car :modulator mod
             :am1 [s1 d1 l2]
             :am2 [s2 d2 l2]
             :pan p)))

(defn pick-distortion [p]
  (let [enable (coin (* 1/2 p) true false)
        mix (float (if enable (flip (rand)) -1))
        bias (float (if enable (rand) 0))
        src (if enable 
              (rand-nth (concat (repeat 8 :off)
                                '(:a :b :c :d :e :f :g :h)))
              :off)
        dpth (float (if (= src :off) 0 (rand)))]
    ;; (println (format ";; clip enable %s  mix %s bias %s src %s dpth %s"
    ;;                 enable mix bias src dpth))
    (clip
     :gain 1.0
     :clip [bias src dpth] 
     :mix mix)))

(defn pick-folder [p]
  (let [enable (coin (* 1/2 p) true false)
        mix (float (if enable (flip (rand)) -1))
        bias (float (if enable (rand) 0))
        src (if enable 
              (rand-nth (concat (repeat 8 :off)
                                '(:a :b :c :d :e :f :g :h)))
              :off)
        dpth (float (if (= src :off) 0 (rand)))]
    ;; (println (format ";; fold enable %s  mix %s bias %s src %s dpth %s"
    ;;                  enable mix bias src dpth))
    (fold
     :gain 1.0
     :clip [bias src dpth] 
     :mix mix)))
                              
(defn pick-filter2 []
  (let [range (rand-nth '(:low :low :low :med :med :high))
        bias (int (cond (= range :low)
                        (+ 50 (rand 800))
                   (= range :med)
                   (+ 800 (rand 4000))
                   :default
                   (+ 4000 (rand 12000))))
        sign (cond (= range :low ) +1
                   (= range :med) (coin 0.75 +1 -1)
                   :default -1)
        src1 (coin 0.8 :c (rand-nth '(:a :b :e :f :g :h)))
        dpth1 (int (* sign (rand (cond (= range :low) 10000
                                       (= range :med) 6000
                                       :default 10000))))
        fm1 [src1 dpth1]
        src2 (coin 0.75 :off (rand-nth (remove (fn [n](= n src1))
                                               '(:a :b :c :d :e :f :g :h))))
        dpth2 (int (* (flip sign)(rand (cond (= range :low) 10000
                                             (= range :med) 6000
                                             :default 10000))))
        fm2 [src2 dpth2]
        res-bias (rand 0.95)
        res-src (coin 0.90 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        res-dpth (if (= res-src :off) 0.0 (flip (rand)))
        res [res-bias res-src res-dpth]
        pan-bias (coin 0.75 (+ 0.25 (rand 0.75))(flip (rand)))
        pan-src (coin 0.80 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        pan-dpth (flip (rand))
        pan [pan-bias pan-src pan-dpth]]
    ;; (println ";; filter2 range " range " freq " bias)
    ;; (println ";;\tfm1 " fm1)
    ;; (println ";;\tfm2 " fm2)
    ;; (println ";;\tres " res)
    ;; (println ";;\tpan " pan)
    (filter2 :freq bias 
             :fm1 fm1
             :fm2 fm2
             :res res
             :pan pan)))

(defn pick-filter1-lp [mode]
  (let [range (rand-nth '(:low :low :low :med :med :high))
        bias (int (cond (= range :low)
                        (+ 50 (rand 800))
                        (= range :med)
                        (+ 800 (rand 4000))
                        :default
                        (+ 4000 (rand 12000))))
        sign (cond (= range :low ) +1
                   (= range :med) (coin 0.75 +1 -1)
                   :default -1)
        src1 (coin 0.8 :c (rand-nth '(:a :b :e :f :g :h)))
        dpth1 (int (* sign (rand (cond (= range :low) 10000
                                       (= range :med) 6000
                                       :default 10000))))
        
        fm1 [src1 dpth1]
        src2 (coin 0.75 :off (rand-nth (remove (fn [n](= n src1))
                                               '(:a :b :c :d :e :f :g :h))))
        dpth2 (int (* (flip sign)(rand (cond (= range :low) 10000
                                             (= range :med) 6000
                                             :default 10000))))
        fm2 [src2 dpth2]
        res-bias (rand 0.95)
        res-src (coin 0.90 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        res-dpth (if (= res-src :off) 0.0 (flip (rand)))
        res [res-bias res-src res-dpth]
        pan-bias (coin 0.75 (+ 0.25 (rand 0.75))(flip (rand)))
        pan-src (coin 0.80 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        pan-dpth (flip (rand))
        pan [pan-bias pan-src pan-dpth]]
    ;; (println ";; filter1 range " range "  freq " bias "  mode " mode) 
    ;; (println ";;\tfm1 " fm1)
    ;; (println ";;\tfm2 " fm2)
    ;; (println ";;\tres " res)
    ;; (println ";;\tpan " pan)
    (filter1 :mode mode
             :freq bias 
             :fm1 fm1
             :fm2 fm2
             :res res
             :pan pan)))   

(defn pick-filter1-hp [mode]
  (let [range (rand-nth '(:high :high :high :med :med :low))
        bias (int (cond (= range :low)
                        (+ 50 (rand 800))
                        (= range :med)
                        (+ 800 (rand 4000))
                        :default
                        (+ 4000 (rand 12000))))
        sign (cond (= range :low ) -1
                   (= range :med) (coin 0.75 -1 +1)
                   :default +1)
        src1 (coin 0.8 :c (rand-nth '(:a :b :e :f :g :h)))
        dpth1 (int (* sign (rand (cond (= range :low) 10000
                                       (= range :med) 6000
                                       :default 10000))))
        
        fm1 [src1 dpth1]
        src2 (coin 0.75 :off (rand-nth (remove (fn [n](= n src1))
                                               '(:a :b :c :d :e :f :g :h))))
        dpth2 (int (* (flip sign)(rand (cond (= range :low) 10000
                                             (= range :med) 6000
                                             :default 10000))))
        fm2 [src2 dpth2]
        res-bias (rand 0.95)
        res-src (coin 0.90 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        res-dpth (if (= res-src :off) 0.0 (flip (rand)))
        res [res-bias res-src res-dpth]
        pan-bias (coin 0.75 (+ 0.25 (rand 0.75))(flip (rand)))
        pan-src (coin 0.80 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        pan-dpth (flip (rand))
        pan [pan-bias pan-src pan-dpth]]
    ;; (println ";; filter1 range " range "  freq " bias "  mode " mode) 
    ;; (println ";;\tfm1 " fm1)
    ;; (println ";;\tfm2 " fm2)
    ;; (println ";;\tres " res)
    ;; (println ";;\tpan " pan)
    (filter1 :mode mode
             :freq bias 
             :fm1 fm1
             :fm2 fm2
             :res res
             :pan pan)))   

(defn pick-filter1 []
  (let [mode (rand-nth (concat (repeat 10 :lp)
                               (repeat  5 :lp*hp)
                               (repeat  4 :hp)
                               (repeat  1 :bp)))]
    (if (or (= mode :lp)(= mode :lp*hp))
      (pick-filter1-lp mode)
      (pick-filter1-hp mode))))

(defn pick-pitchshifter [p]
  (let [enable (coin p true false)
        bias (if enable (rand 4) 1)
        src (if enable (coin 0.5 :off (rand-nth '(:a :b :c :d :e :f :g :h))) :off)
        dpth (if enable (rand 4) 0.0)
        r (coin 0.75 (rand 0.25)(rand))
        s (coin 0.75 (rand 0.25)(rand))
        mix (* -1 (if enable (rand-nth '(0 3 6 9 12 15)) 99))]
    ;println (format ";; pshifter %d :ratio %s" mix [bias src dpth]))
    (pshifter mix :ratio [bias src dpth] :rand r :spread s)))

(defn pick-flanger [p]
  (let [enable (coin p true false)
        mix (* -1 (if enable (rand-nth '(0 3 6 9 12 15)) 99))
        msrc (coin 0.75 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        mdpth (rand 0.75)
        mod [msrc mdpth]
        lfo-rate (coin 0.75 (+ 0.01 (rand 0.1))(rand 5))
        lfo-depth (coin 0.25 0 (rand 0.75))
        lfo [lfo-rate lfo-depth]
        fb (flip (rand) 0.75)
        xmix 0.25]
    ;; (println (format ";; flanger %d :mod %s :lfo %s :fb %s"
    ;;                  mix mod lfo fb))
    (flanger mix :mod mod :lfo lfo :fb fb :xmix xmix)))

(defn pick-echo [p p-sync]
  (let [sync (coin p-sync true false)
        tbase (float (* 1/10 (inc (rand-nth (range 20)))))
        enable1 (coin (* 1/2 p) true false)
        delay1-bias tbase
        delay1-src (coin 0.80 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        delay1-depth (if (= delay1-src :off) 0 (rand))
        gate1-src (coin 0.75 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        gate1-depth (if (= gate1-src :off) 0 (rand))
        fb1 (flip (rand))
        damp1 (rand)
        pan1 -0.50
        mix1 (* -1 (if enable1 (rand-nth '(0 3 6 9 12 15)) 99))

        enable2 (coin (* 1/2 p) true false)
        delay2-bias (if sync 
                      (* tbase (rand-nth '(1/3 1/2 2/3 3/4 5/4 4/3 2)))
                      (float (* 1/10 (inc (rand-nth (range 20)))))) 
        delay2-src (coin 0.80 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        delay2-depth (if (= delay1-src :off) 0 (rand))
        gate2-src (coin 0.75 :off (rand-nth '(:a :b :c :d :e :f :g :h)))
        gate2-depth (if (= gate1-src :off) 0 (rand))
        fb2 (flip (rand))
        damp2 (rand)
        pan2 +0.50
        mix2 (* -1 (if enable2 (rand-nth '(0 3 6 9 12 15)) 99))]
    (concat (echo1 mix1 
                   :delay [delay1-bias delay1-src delay1-depth]
                   :fb fb1 :damp damp1 :pan pan1
                   :gate [gate1-src gate1-depth])
            (echo2 mix2 
                   :delay [delay2-bias delay2-src delay2-depth]
                   :fb fb2 :damp damp2 :pan pan2
                   :gate [gate2-src gate2-depth]))))
        
(defn random-alias-program [& {:keys [env-style 
                                      env-range
                                      only-harmonics
                                      p-port
                                      p-waveshaper
                                      p-pitchshifter
                                      p-flanger
                                      p-echo
                                      p-echo-sync]
                               :or {env-style nil
                                    env-range nil
                                    only-harmonics nil
                                    p-port nil
                                    p-waveshaper 0.25
                                    p-pitchshifter 0.20
                                    p-flanger 0.20
                                    p-echo 0.20
                                    p-echo-sync 0.5}}]
  (let [env-range (or env-range (rand-nth (keys envelope-ranges)))]
    (println)
    (let [main (rand-nth (concat (repeat 3 :osc1)
                                 (repeat 3 :osc2)
                                 (repeat 3 :osc3)
                                 (list :noise :ringmod)))
          o1-amp (if (= main :osc1) 0 (rand-nth '(0 -3 -6 -9 -12 -15 -99)))
          o2-amp (if (= main :osc2) 0 (rand-nth '(0 -3 -6 -9 -12 -15 -99)))
          o3-amp (if (= main :osc3) 0 (rand-nth '(0 -3 -6 -9 -12 -15 -99)))
          n-amp (if (= main :noise) 
                  0 
                  (coin 0.80 -99 (rand-nth '(-3 -6 -9 -12 -15))))
          r-amp (if (= main :ringmod) 
                  0 
                  (coin 0.80 -99 (rand-nth '(-3 -6 -9 -12 -15))))]
      (alias-program 
       (pick-common p-port)
       (pick-envelope 1 env-style env-range)
       (pick-envelope 2 env-style env-range)
       (pick-envelope 3 env-style env-range)
       (pick-vibrato-lfo)
       (pick-lfo 2)
       (pick-lfo 3)
       (pick-stepper 1)
       (pick-stepper 2)
       (pick-divider1)
       (pick-divider2)
       (pick-lfnoise)
       (pick-sample-hold)
       (pick-matrix)
       (pick-osc-freq only-harmonics)
       (pick-osc1-wave)
       (pick-osc2-wave)
       (pick-osc3-wave)
       (pick-osc-amp  1 o1-amp)
       (pick-osc-amp  2 o2-amp)
       (pick-osc-amp  3 o3-amp)
       (pick-noise n-amp)
       (pick-ringmod r-amp)
       (pick-distortion p-waveshaper)
       (pick-folder p-waveshaper)
       (pick-filter1)
       (pick-filter2)
       (dry  (coin 0.9 0 (rand-nth '(-3 -6 -9 -12 -15 -99))))
       (pick-pitchshifter p-pitchshifter)
       (pick-flanger p-flanger)
       (pick-echo p-echo p-echo-sync)))))
  
