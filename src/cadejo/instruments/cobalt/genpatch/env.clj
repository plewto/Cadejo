;; Generate random envelope curves
;;
(println "-->    cobalt genpatch env")
(ns cadejo.instruments.cobalt.genpatch.env
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.col :as col])
  (:require [cadejo.instruments.cobalt.program :as prog]))

(def approx math/approx)
(def coin math/coin)

;; Random invert value n 
;; 
(defn scoin 
  ([n]
   (let [s (math/coin 0.5 -1 1)]
     (* s n)))
  ([p range]
   (scoin (math/coin p range 0))))


;; map values [min-time max-time alternate1 alternate2 name]
(def env-time-spans {:ultra-fast  [0.000 0.050 :extra-fast :extra-fast :ultra-fast] 
                     :extra-fast  [0.000 0.100 :ultra-fast :fast :extra-fast]
                     :fast        [0.100 0.200 :extra-fast :medium-fast :fast]
                     :medium-fast [0.200 0.400 :fast :medium :medium-fast]
                     :medium      [0.400 0.800 :medium-fast :medium-slow :medium]
                     :medium-slow [0.800 1.600 :medium :slow :medium-slow]
                     :slow        [1.600 3.200 :medium-slow :extra-slow :slow]
                     :extra-slow  [3.200 6.400 :slow :ultra-slow :extra-slow]
                     :ultra-slow  [6.400 12.80 :extra-slow :extra-slow :ultra-slow]})

;; With no argument return random entry from env-time-spans
;;
;; With single span argument s
;;   Return s approx 60%
;;   Return alternate span approx 30%
;;   Return random span approx 10%
;;
(defn pick-time-span 
  ([](let [key (rand-nth (flatten [(repeat   8 :ultra-fast)
                                   (repeat  10 :extra-fast)
                                   (repeat  10 :fast)
                                   (repeat  10 :medium-fast)
                                   (repeat  10 :medium)
                                   (repeat   8 :medium-slow)
                                   (repeat   6 :slow)
                                   (repeat   3 :extra-slow)
                                   (repeat   1 :ultra-slow)]))
           span (key env-time-spans)]
       span))
  ([span]
   (coin 0.60 span (coin 0.60 (get env-time-spans (coin 0.5 (nth span 2)(nth span 3)))(pick-time-span)))))

;; Returns list of envelope stage times.
;; span - value from env-time-spans to inform range of values. If not specified
;;        pick value at random
;; n    - number of values generated. If not specified return 60 
;;
(defn env-times 
  ([span n]
   (print (format ";; Using %s " (nth span 4)))
   (let [acc* (atom [])]
     (dotimes [i n]
       (let [s (pick-time-span span)
             mn (first s)
             mx (second s)
             diff (- mx mn)
             t (+ mn (rand diff))]
         (swap! acc* (fn [q](conj q t)))))
     @acc*))
  ([n](env-times (pick-time-span) n))
  ([](env-times 60)))

(def envelope-styles {:adsr   {:name :adsr  :alternates [:addsr :aadsr :asr :perc :perc2]} 
                      :addsr  {:name :addsr :alternates [:adsr :aadsr :asr :perc :perc2]}
                      :aadsr  {:name :aadsr :alternates [:adsr :addsr :asr :perc :perc2]}
                      :asr    {:name :asr   :alternates [:adsr :perc :perc2]}
                      :perc   {:name :perc  :alternates [:adsr :addsr :perc2]}
                      :perc2  {:name :perc2 :alternates [:adsr :addsr :perc]}
                      :perc3  {:name :perc3 :alternates [:perc :perc2]}})



(defn pick-env-style 
  ([sty]
   (let [alternates (:alternates  sty)]
     (coin 0.60 sty (get envelope-styles (rand-nth alternates)))))
  ([]
   (let [k (rand-nth (keys envelope-styles))]
     (println (format "%s envelopes" (name k)))
     (pick-env-style (get envelope-styles k)))))

;; Returns map with keys for each Cobalt envelope generator and 
;; corresponding values indicating envelope style.
;;
(defn env-styles []
  (let [sty (pick-env-style)
        e [:op1 :op2 :op3 :op4 :op5 :op6 :op7 :op8
                :noise :buzz :xenv :filter]
        acc* (atom {})]
    (doseq [u e]
      (swap! acc* (fn [q](assoc q u (:name (pick-env-style sty))))))
    @acc*))


(defn asr [tlist offset]
  (let [a (nth tlist offset)
        r (nth tlist (inc offset))]
    [:att a :dcy1 0 :dcy2 0 :rel r :peak 1 :bp 1 :sus 1 :dcy 0]))
    
(defn adsr [tlist offset]
  (let [a (nth tlist offset)
        d (nth tlist (+ offset 1))
        r (nth tlist (+ offset 2))
        s (float (+ 1/3 (rand 2/3)))]
    [:att a :dcy1 d :dcy2 0 :rel r :peak 1 :bp s :sus s :dcy d]))

(defn addsr [tlist offset]
  (let [a (nth tlist offset)
        d1 (nth tlist (+ 1 offset))
        d2 (nth tlist (+ 2 offset))
        r (nth tlist (+ 3 offset))
        bp (float (coin 0.75 (+ 1/2 (rand 1/2))(rand)))
        sus (float (coin 0.75 (+ 1/2 (rand 1/2))(rand)))]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak 1 :bp bp :sus sus :dcy d1]))

(defn aadsr [tlist offset]
  (let [pk (float (coin 0.75 (+ 1/2 (rand 1/2))(rand)))
        mp (col/alist->map (addsr tlist offset))]
    (col/map->alist (assoc mp :peak pk))))

(defn perc [tlist offset]
  (let [a (coin 0.80 0 (rand 0.07))
        d1 (nth tlist offset)
        rel (* 2 d1)
        sus (coin 0.5 (rand 0.25)(rand 0.06))]
    [:att a :dcy1 d1 :dcy2 0 :rel rel :peak 1 :bp sus :sus sus :dcy d1]))

(defn perc2 [tlist offset]
  (let [a (coin 0.80 0 (rand 0.07))
        d1 (coin 0.60 (rand 0.2)(rand 0.5))
        d2 (max 1 (* 2 (nth tlist offset)))
        rel (max 1 (* 2 (nth tlist (inc offset))))
        bp (float (+ 1/2 (rand 1/4)))
        sus (coin 0.70 0.1 (float (rand 1/4)))]
    [:att a :dcy1 d1 :dcy2 d2 :rel rel :peak 1 :bp bp :sus sus :dcy d2]))

(defn perc3 [tlist offset]
  (let [a (coin 0.80 0 (rand 0.07))
        d1 (max 0.25 (nth tlist offset))
        d2 (max 1 (* 2 (nth tlist (inc offset))))
        rel (max 1 (* 2 (nth tlist (+ 2 offset))))
        bp (float (+ 3/4 (rand 1/4)))
        sus (coin 0.70 0.1 (float (rand 1/4)))]
    [:att a :dcy1 d1 :dcy2 d2 :rel rel :peak 1 :bp bp :sus sus :dcy d2]))
        
(defn gen-envelopes []
  (let [tlist (env-times)
        slist (env-styles)
        acc* (atom {})
        offset* (atom 0)]
    (doseq [[k v] slist]
      (swap! acc* (fn [q](assoc q k 
                                (cond (= v :asr)(asr tlist @offset*)
                                      (= v :adsr)(adsr tlist @offset*)
                                      (= v :addsr)(addsr tlist @offset*)
                                      (= v :aadsr)(aadsr tlist @offset*)
                                      (= v :perc)(perc tlist @offset*)
                                      (= v :perc2)(perc2 tlist @offset*)
                                      :default (perc3 tlist @offset*)))))
      (swap! offset* (fn [q](+ q 4))))
    @acc*))


(defn select-pitch-env []
  (let [arange (rand-nth [1.0 0.5 0.2 0.1])
        trange (coin 0.5 (rand 4)(rand-nth [0.5 0.1]))
        amp (fn [](* (coin 0.5 -1 1) (rand arange)))
        time (fn [](rand trange))]
    (println (format "DEBUG select-pitch-env argange %s  trange %s" arange trange))
    (prog/penv :a0 (amp) :a1 (amp) :a2 (amp) :a3 (amp)
               :t1 (time) :t2 (time) :t3 (time))))
    
        
