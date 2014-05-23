(println "\t--> program")

(ns cadejo.instruments.alias.program
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.instruments.alias.constants :as constants :reload true])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg]))

(defonce bank (cadejo.midi.program-bank/program-bank :Alias))
  
(defn save-program 
  ([pnum function-id pname remarks data]
     (.set-program! bank pnum function-id pname data remarks))
  ([pnum pname remarks data]
     (save-program pnum nil pname remarks data))
  ([pnum pname data]
     (save-program pnum pname "" data)))

(defn- third [col]
  (nth col 2))

(defn- continuity-test [dkeys]
  (let [rs* (atom ())]
    (doseq [p constants/alias-parameters]
      (if (ucol/not-member? p dkeys)
        (swap! rs* (fn [n](cons p n)))))
    @rs*))

(defn- spurious-key-test [dkeys]
  (let [rs* (atom ())]
    (doseq [k dkeys]
      (if (ucol/not-member? k constants/alias-parameters)
        (swap! rs* (fn [n](cons k n)))))
    @rs*))

(defn- validity-test [data]
  (let [dkeys (ucol/alist->keys data)
        missing (continuity-test dkeys)
        extra (spurious-key-test dkeys)]
    (if (> (count missing) 0)
      (umsg/warning "Missing keys from Alias data" missing))
    (if (> (count extra) 0)
      (umsg/warning "Extra keys in Alias data" extra))))

;; bspec [index depth]
(defn bus-number [bspec]
  (let [key (first bspec)]
    (if (and (integer? key)(>= key 0)(<= key 8))
      (int key)
      (let [bus-number (get constants/general-bus-map key)]
        (if (not bus-number)
          (do
            (umsg/warning (format "Invalid bus %s" bspec))
            0)
          bus-number)))))

;; bspec [index depth]
(defn control-bus-number [bspec] 
  (let [key (first bspec)]
    (if (and (integer? key)(>= key 0)(<= key 32))
      (int key)
      (let [bus-number (get constants/control-bus-map key)]
        (if (not bus-number)
          (do
            (umsg/warning (format "Invalid bus %s" bspec))
            32)
          bus-number)))))

;; filter-mode
(defn filter-mode [n]
  (if (number? n)
    (float n)
    (let [val (get constants/filter-modes n)]
      (if (not val)
        (do
          (umsg/warning (format "INvalid filter mode " n))
          0.0)
        val))))

(defn dry [mix]
  (list :dry-mix (int mix)))

(defn common [& {:keys [amp port-time cc7->volume]
                 :or {amp 0.20
                      port-time 0.0
                      cc7->volume 0.0}}]
  (list :amp (float amp)
        :port-time (float port-time)
        :cc7->volume (float cc7->volume)))

;; fm  [src depth lag]
(defn osc1-freq [detune & {:keys [bias fm1 fm2]
                           :or {bias 0
                                fm1 [0 0 0]
                                fm2 [0 0 0]}}]
  (list :osc1-detune (float detune)
        :osc1-bias (float bias)
        :osc1-fm1-source (bus-number fm1)
        :osc1-fm1-depth (float (second fm1))
        :osc1-fm1-lag (float (third fm1))
        :osc1-fm2-source (bus-number fm2)
        :osc1-fm2-depth (float (second fm2))
        :osc1-fm2-lag (float (third fm2))))

;; fm  [src depth lag]
(defn osc2-freq [detune & {:keys [bias fm1 fm2]
                           :or {bias 0
                                fm1 [0 0 0]
                                fm2 [0 0 0]}}]
  (list :osc2-detune (float detune)
        :osc2-bias (float bias)
        :osc2-fm1-source (bus-number fm1)
        :osc2-fm1-depth (float (second fm1))
        :osc2-fm1-lag (float (third fm1))
        :osc2-fm2-source (bus-number fm2)
        :osc2-fm2-depth (float (second fm2))
        :osc2-fm2-lag (float (third fm2))))

;; fm  [src depth lag]
(defn osc3-freq [detune & {:keys [bias fm1 fm2]
                           :or {bias 0
                                fm1 [0 0 0]
                                fm2 [0 0 0]}}]
  (list :osc3-detune (float detune)
        :osc3-bias (float bias)
        :osc3-fm1-source (bus-number fm1)
        :osc3-fm1-depth (float (second fm1))
        :osc3-fm1-lag (float (third fm1))
        :osc3-fm2-source (bus-number fm2)
        :osc3-fm2-depth (float (second fm2))
        :osc3-fm2-lag (float (third fm2))))

;; w1 [src depth]    w2 [src depth]
(defn osc1-wave [wave & {:keys [w1 w2]
                         :or {w1 [0 0]
                              w2 [0 0]}}]
  (list :osc1-wave (float wave)
        :osc1-wave1-source (bus-number w1)
        :osc1-wave1-depth (float (second w1))
        :osc1-wave2-source (bus-number w2)
        :osc1-wave2-depth (float (second w2))))

;; w1 [src depth]    w2 [src depth]
(defn osc2-wave [wave & {:keys [w1 w2]
                         :or {w1 [0 0]
                              w2 [0 0]}}]
  (list :osc2-wave (float wave)
        :osc2-wave1-source (bus-number w1)
        :osc2-wave1-depth (float (second w1))
        :osc2-wave2-source (bus-number w2)
        :osc2-wave2-depth (float (second w2))))

;; w1 [src depth]    w2 [src depth]
(defn osc3-wave [wave & {:keys [w1 w2]
                         :or {w1 [0 0]
                              w2 [0 0]}}]
  (list :osc3-wave (float wave)
        :osc3-wave1-source (bus-number w1)
        :osc3-wave1-depth (float (second w1))
        :osc3-wave2-source (bus-number w2)
        :osc3-wave2-depth (float (second w2))))

;; am1 [src depth lag]  am2 [src depth lag]
(defn osc1-amp [db & {:keys [am1 am2 pan]
                      :or {am1 [0 0 0]
                           am2 [0 0 0]
                           pan 0}}]
  (list :osc1-amp db
        :osc1-amp1-src (bus-number am1)
        :osc1-amp1-depth (float (second am1))
        :osc1-amp1-lag (float (third am1))
        :osc1-amp2-src (bus-number am2)
        :osc1-amp2-depth (float (second am2))
        :osc1-amp2-lag (float (third am2))
        :osc1-pan (float pan)))

;; am1 [src depth lag]  am2 [src depth lag]
(defn osc2-amp [db & {:keys [am1 am2 pan]
                      :or {am1 [0 0 0]
                           am2 [0 0 0]
                           pan 0}}]
  (list :osc2-amp db
        :osc2-amp1-src (bus-number am1)
        :osc2-amp1-depth (float (second am1))
        :osc2-amp1-lag (float (third am1))
        :osc2-amp2-src (bus-number am2)
        :osc2-amp2-depth (float (second am2))
        :osc2-amp2-lag (float (third am2))
        :osc2-pan (float pan)))

;; am1 [src depth lag]  am2 [src depth lag]
(defn osc3-amp [db & {:keys [am1 am2 pan]
                      :or {am1 [0 0 0]
                           am2 [0 0 0]
                           pan 0}}]
  (list :osc3-amp db
        :osc3-amp1-src (bus-number am1)
        :osc3-amp1-depth (float (second am1))
        :osc3-amp1-lag (float (third am1))
        :osc3-amp2-src (bus-number am2)
        :osc3-amp2-depth (float (second am2))
        :osc3-amp2-lag (float (third am2))
        :osc3-pan (float pan)))

;; am1 [src depth lag]  am2 [src depth lag]
(defn noise [db & {:keys [crackle lp hp am1 am2 pan]
                   :or {crackle 0
                        lp 10000
                        hp 50
                        am1 [0 0 0]
                        am2 [0 0 0]
                        pan 0}}]
  (list :noise-param (float crackle)
        :noise-lp (int lp)
        :noise-hp (int hp)
        :noise-amp (float db)
        :noise-amp1-src (bus-number am1)
        :noise-amp1-depth (float (second am1))
        :noise-amp1-lag (float (third am1))
        :noise-amp2-src (bus-number am2)
        :noise-amp2-depth (float (second am2))
        :noise-amp2-lag (float (third am2))
        :noise-pan (float pan)))

;; am1 [src depth lag] am2 [src depth lag]
(defn ringmod [db & {:keys [carrier modulator am1 am2 pan]
                     :or {carrier -1
                          modulator -1
                          am1 [0 0 0]
                          am2 [0 0 0]
                          pan 0}}]
  (list :ringmod-amp (float db)
        :ringmod-amp1-src (bus-number am1)
        :ringmod-amp1-depth (float (second am1))
        :ringmod-amp1-lag (float (third am1))
        :ringmod-amp2-src (bus-number am2)
        :ringmod-amp2-depth (float (second am2))
        :ringmod-amp2-lag (float (third am2))
        :ringmod-pan (float pan)
        :ringmod-carrier (float carrier)
        :ringmod-modulator (float modulator)))

;; clip [bias src depth]
(defn clip  [& {:keys [gain clip mix]
                :or {mix -1
                     gain 1
                     clip [1 0 0]}}]
  (list :distortion1-pregain (float gain)
        :distortion1-param (float (first clip))
        :distortion1-param-source (bus-number (rest clip))
        :distortion1-param-depth (float (third clip))
        :distortion1-mix (float mix)))

;; clip [bias src depth]
(defn fold [& {:keys [gain clip mix]
                    :or {mix -1
                         gain 1
                         clip [1 0 0]}}]
  (list :distortion2-pregain (float gain)
        :distortion2-param (float (first clip))
        :distortion2-param-source (bus-number (rest clip))
        :distortion2-param-depth (float (third clip))
        :distortion2-mix (float mix)))

;; fm [src depth]
;; res [bias src depth]
;; pan [bias src depth]
(defn filter1 [& {:keys [mode freq fm1 fm2 res pan gain]
                       :or {mode 0
                            freq 10000
                            fm1 [0 0]
                            fm2 [0 0]
                            res [0 0 0]
                            pan [-0.5 0 0]
                            gain 1}}]
  (list :filter1-freq (float freq)
        :filter1-freq1-source (bus-number fm1)
        :filter1-freq1-depth (float (second fm1))
        :filter1-freq2-source (bus-number fm2)
        :filter1-freq2-depth (float (second fm2))
        :filter1-mode (filter-mode mode)
        :filter1-res (float (first res))
        :filter1-res-source (bus-number (rest res))
        :filter1-res-depth (float (third res))
        :filter1-pan (float (first pan))
        :filter1-pan-source (bus-number (rest pan))
        :filter1-pan-depth (float (third pan))
        :filter1-postgain (float gain)))

;; fm [src depth]
;; res [bias src depth]
;; pan [bias src depth]
(defn filter2 [& {:keys [freq fm1 fm2 res pan gain]
                       :or {freq 10000
                            fm1 [0 0]
                            fm2 [0 0]
                            res [0 0 0]
                            pan [-0.5 0 0]
                            gain 1}}]
  (list :filter2-freq (float freq)
        :filter2-freq1-source (bus-number fm1)
        :filter2-freq1-depth (float (second fm1))
        :filter2-freq2-source (bus-number fm2)
        :filter2-freq2-depth (float (second fm2))
        :filter2-res (float (first res))
        :filter2-res-source (bus-number (rest res))
        :filter2-res-depth (float (third res))
        :filter2-pan (float (first pan))
        :filter2-pan-source (bus-number (rest pan))
        :filter2-pan-depth (float (third pan))
        :filter2-postgain (float gain)))
        
(defn env1 [a d1 d2 r  & {:keys [pk bp sus invert]
                          :or {pk 1
                               bp 1
                               sus 1
                               invert 0}}]
  (list :env1-attack (float a)
        :env1-decay1 (float d1)
        :env1-decay2 (float d2)
        :env1-release (float r)
        :env1-peak (float pk)
        :env1-breakpoint (float bp)
        :env1-sustain (float sus)
        :env1-invert (if (or (= invert 0)(not invert)) 0 1)))

(defn env2 [a d1 d2 r  & {:keys [pk bp sus invert]
                          :or {pk 1
                               bp 1
                               sus 1
                               invert 0}}]
  (list :env2-attack (float a)
        :env2-decay1 (float d1)
        :env2-decay2 (float d2)
        :env2-release (float r)
        :env2-peak (float pk)
        :env2-breakpoint (float bp)
        :env2-sustain (float sus)
        :env2-invert (if (or (= invert 0)(not invert)) 0 1)))

(defn env3 [a d1 d2 r  & {:keys [pk bp sus invert]
                          :or {pk 1
                               bp 1
                               sus 1}}]
  (list :env3-attack (float a)
        :env3-decay1 (float d1)
        :env3-decay2 (float d2)
        :env3-release (float r)
        :env3-peak (float pk)
        :env3-breakpoint (float bp)
        :env3-sustain (float sus)))

;; fm [src depth]
;; wave [src depth]
(defn lfo1 [& {:keys [fm1 fm2 wave1 wave2]
               :or {fm1 [1 7.00]
                    fm2 [0 0.00]
                    wave1 [1 0.5]
                    wave2 [0 0]}}]
  (list :lfo1-freq1-source (control-bus-number fm1)
        :lfo1-freq1-depth (float (second fm1))
        :lfo1-freq2-source (control-bus-number fm2)
        :lfo1-freq2-depth (float (second fm2))
        :lfo1-wave1-source (control-bus-number wave1)
        :lfo1-wave1-depth (float (second wave1))
        :lfo1-wave2-source (control-bus-number wave2)
        :lfo1-wave2-depth (float (second wave2))))

;; fm [src depth]
;; wave [src depth]
(defn lfo2 [& {:keys [fm1 fm2 wave1 wave2]
               :or {fm1 [1 7.00]
                    fm2 [0 0.00]
                    wave1 [1 0.5]
                    wave2 [0 0]}}]
  (list :lfo2-freq1-source (control-bus-number fm1)
        :lfo2-freq1-depth (float (second fm1))
        :lfo2-freq2-source (control-bus-number fm2)
        :lfo2-freq2-depth (float (second fm2))
        :lfo2-wave1-source (control-bus-number wave1)
        :lfo2-wave1-depth (float (second wave1))
        :lfo2-wave2-source (control-bus-number wave2)
        :lfo2-wave2-depth (float (second wave2))))

;; fm [src depth]
;; wave [src depth]
(defn lfo3 [& {:keys [fm1 fm2 wave1 wave2]
               :or {fm1 [1 7.00]
                    fm2 [0 0.00]
                    wave1 [1 0.5]
                    wave2 [0 0]}}]
  (list :lfo3-freq1-source (control-bus-number fm1)
        :lfo3-freq1-depth (float (second fm1))
        :lfo3-freq2-source (control-bus-number fm2)
        :lfo3-freq2-depth (float (second fm2))
        :lfo3-wave1-source (control-bus-number wave1)
        :lfo3-wave1-depth (float (second wave1))
        :lfo3-wave2-source (control-bus-number wave2)
        :lfo3-wave2-depth (float (second wave2))))

;; trigger and reset are bus numbers
;; min max step and ivalue are int
;; bias and scale are float
(defn stepper1 [& {:keys [trig reset min max step ivalue bias scale]
                   :or {trig 4 ;; lfo 1
                        reset 0
                        min -10
                        max 10
                        step 1
                        ivalue -10
                        bias 0
                        scale 0.10}}]
  (list :stepper1-trigger (control-bus-number (list trig))
        :stepper1-reset (control-bus-number (list reset))
        :stepper1-min (int min)
        :stepper1-max (int max)
        :stepper1-step (int step)
        :stepper1-reset-value (int ivalue)
        :stepper1-bias (float bias)
        :stepper1-scale (float scale)))

;; trigger and reset are bus numbers
;; min max step and ivalue are int
;; bias and scale are float
(defn stepper2 [& {:keys [trig reset min max step ivalue bias scale]
                   :or {trig 5 ;; lfo 2
                        reset 0
                        min -10
                        max 10
                        step 1
                        ivalue -10
                        bias 0
                        scale 0.10}}]
  (list :stepper2-trigger (control-bus-number (list trig))
        :stepper2-reset (control-bus-number (list reset))
        :stepper2-min (int min)
        :stepper2-max (int max)
        :stepper2-step (int step)
        :stepper2-reset-value (int ivalue)
        :stepper2-bias (float bias)
        :stepper2-scale (float scale)))        

; am [src depth]
(defn divider1 [& {:keys [pw p1 p3 p5 p7 bias am]
                   :or {pw 0.5
                        p1 1.000
                        p3 0.333
                        p5 0.200
                        p7 0.142
                        bias 0
                        am [1 1]}}]
  (list :divider1-pw (float pw)
        :divider1-p1 (float p1)
        :divider1-p3 (float p3)
        :divider1-p5 (float p5)
        :divider1-p7 (float p7)
        :divider1-bias (float bias)
        :divider1-scale-source (control-bus-number am)
        :divider1-scale-depth (float (second am))))

; am [src depth]
(defn divider2 [& {:keys [pw p2 p4 p6 p8 bias am]
                   :or {pw 0.5
                        p2 0.500
                        p4 0.250
                        p6 0.167
                        p8 0.125
                        bias 0
                        am [1 1]}}]
  (list :divider2-pw (float pw)
        :divider2-p2 (float p2)
        :divider2-p4 (float p4)
        :divider2-p6 (float p6)
        :divider2-p8 (float p8)
        :divider2-bias (float bias)
        :divider2-scale-source (control-bus-number am)
        :divider2-scale-depth (float (second am))))

;; fm [src depth]
(defn lfnoise [& {:keys [fm]
                  :or {fm [1 1]}}]
  (list :lfnoise-freq-source (control-bus-number fm)
        :lfnoise-freq-depth (float (second fm))))

(defn sh [& {:keys [rate src bias scale]
             :or {rate 1
                  src 12 ;; lfnoise
                  bias 0
                  scale 1}}]
  (list :sh-source (control-bus-number (list src))
        :sh-rate (float rate)
        :sh-bias (float bias)
        :sh-scale (float scale)))
        
(defn matrix [& {:keys [a1 a2 b1 b2 c1 c2 d1 d2
                        e1 e2 f1 f2 g1 g2 h1 h2]
                 :or {a1 [1 0]
                      a2 [1 0]
                      b1 [1 0]
                      b2 [1 0]
                      c1 [1 0]
                      c2 [1 0]
                      d1 [1 0]
                      d2 [1 0]
                      e1 [1 0]
                      e2 [1 0]
                      f1 [1 0]
                      f2 [1 0]
                      g1 [1 0]
                      g2 [1 0]
                      h1 [1 0]
                      h2 [1 0]}}]
  (list :a-source1 (control-bus-number a1)
        :a-depth1 (float (second a1))
        :a-source2 (control-bus-number a2)
        :a-depth2 (float (second a2))
        :b-source1 (control-bus-number b1)
        :b-depth1 (float (second b1))
        :b-source2 (control-bus-number b2)
        :b-depth2 (float (second b2))
        :c-source1 (control-bus-number c1)
        :c-depth1 (float (second c1))
        :c-source2 (control-bus-number c2)
        :c-depth2 (float (second c2))
        :d-source1 (control-bus-number d1)
        :d-depth1 (float (second d1))
        :d-source2 (control-bus-number d2)
        :d-depth2 (float (second d2))
        :e-source1 (control-bus-number e1)
        :e-depth1 (float (second e1))
        :e-source2 (control-bus-number e2)
        :e-depth2 (float (second e2))
        :f-source1 (control-bus-number f1)
        :f-depth1 (float (second f1))
        :f-source2 (control-bus-number f2)
        :f-depth2 (float (second f2))
        :g-source1 (control-bus-number g1)
        :g-depth1 (float (second g1))
        :g-source2 (control-bus-number g2)
        :g-depth2 (float (second g2))
        :h-source1 (control-bus-number h1)
        :h-depth1 (float (second h1))
        :h-source2 (control-bus-number h2)
        :h-depth2 (float (second h2))))
        
;; ratio [bias src depth]
(defn pshifter [mix & {:keys [ratio rand spread]
                        :or {ratio [2 0 0]  ;; [bias src dpth]
                             rand 0
                             spread 0}}]
  (list :pitchshift-ratio (float (first ratio))
        :pitchshift-ratio-source (bus-number (rest ratio))
        :pitchshift-ratio-depth (float (third ratio))
        :pitchshift-pitch-dispersion (float rand)
        :pitchshift-time-dispersion (float spread)
        :pitchshift-mix (float mix)))

;; mod [src depth]
;; lfo [rate depth]
(defn flanger [mix & {:keys [mod lfo fb xmix]
                      :or {mod [0 0]
                           lfo [0.25 0.1]  ; [rate depth]
                           fb 0.5
                           xmix 0.25}}]
  (list :flanger-mod-source (bus-number mod)
        :flanger-mod-depth (float (second mod))
        :flanger-lfo-amp (float (second lfo))
        :flanger-lfo-rate (float (first lfo))
        :flanger-feedback (float fb)
        :flanger-mix (float mix)
        :flanger-crossmix (float xmix)))

;; delay [bias src depth]
;; gate [src depth]
(defn echo1 [mix & {:keys [delay fb damp gate pan]
                :or {delay [0.125 0 0]
                     fb 0.5
                     damp 0.0
                     pan -0.75
                     gate [0 0]}}]
  (list :echo1-delay (float (first delay))
        :echo1-delay-source (bus-number (rest delay))
        :echo1-delay-depth (float (third delay))
        :echo1-feedback (float fb)
        :echo1-damp (float damp)
        :echo1-amp-source (bus-number gate)
        :echo1-amp-depth (float (second gate))
        :echo1-pan (float pan)
        :echo1-mix mix))

;; delay [bias src depth]
;; gate [src depth]
(defn echo2 [mix & {:keys [delay fb damp gate pan]
                :or {delay [0.125 0 0]
                     fb 0.5
                     damp 0.0
                     pan 0.75
                     gate [0 0]}}]
  (list :echo2-delay (float (first delay))
        :echo2-delay-source (bus-number (rest delay))
        :echo2-delay-depth (float (third delay))
        :echo2-feedback (float fb)
        :echo2-damp (float damp)
        :echo2-amp-source (bus-number gate)
        :echo2-amp-depth (float (second gate))
        :echo2-pan (float pan)
        :echo2-mix mix))

(defn alias-program [& args]
  (let [data (flatten args)]
    (validity-test data)
    data))
