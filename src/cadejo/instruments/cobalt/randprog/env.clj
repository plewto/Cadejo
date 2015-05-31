(ns cadejo.instruments.cobalt.randprog.env
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.randprog.config :as config]))

(def ^:private coin math/coin)

(defn- sign [n]
  (* n (coin 0.5 -1 1)))

;; Map entry format [min-time max-time adjacent-times adjacent-times name]
;;
(def ^:private env-time-spans
  {:ultra-fast  [0.000 0.050 :extra-fast :extra-fast :ultra-fast]
   :extra-fast  [0.000 0.100 :ultra-fast :fast :extra-fast]
   :fast        [0.100 0.200 :extra-fast :medium-fast :fast]
   :medium-fast [0.200 0.400 :fast :medium :medium-fast]
   :medium      [0.400 0.800 :medium-fast :medium-slow :medium]
   :medium-slow [0.800 1.600 :medium :slow :medium-slow]
   :slow        [1.600 3.200 :medium-slow :extra-slow :slow]
   :extra-slow  [3.200 6.400 :slow :ultra-slow :extra-slow]
   :ultra-slow  [6.400 12.80 :extra-slow :extra-slow :ultra-slow]})

(defn- build-time-selection-list []
  (flatten [(repeat (* 100 @config/p-env-time-ultra-fast*) :ultra-fast)
            (repeat (* 100 @config/p-env-time-extra-fast*) :extra-fast)
            (repeat (* 100 @config/p-env-time-fast*) :fast)
            (repeat (* 100 @config/p-env-time-medium-fast*) :medium-fast)
            (repeat (* 100 @config/p-env-time-medium*) :medium)
            (repeat (* 100 @config/p-env-time-medium-slow*) :medium-slow)
            (repeat (* 100 @config/p-env-time-slow*) :slow)
            (repeat (* 100 @config/p-env-time-extra-slow*) :extra-slow)
            (repeat (* 100 @config/p-env-time-ultra-slow*) :ultra-slow)]))
  
(def ^:private time-selection-list* (atom (build-time-selection-list)))


;; With no argument returns random element from env-time-spans map
;; With span argument return span or alternate span
;;    alternate span is selected equaly from one of span's adjacent values
;;    and a compleatly random span.
;;
(defn- pick-time-span
  ([](let [key (rand-nth @time-selection-list*)
           span (key env-time-spans)]
       span))
  ([span]
   (coin @config/p-env-alternate-time-span* 
         (let [a1 (nth span 2)
               a2 (nth span 3)
               a3 (pick-time-span)]
           (rand-nth [(a1 env-time-spans)(a2 env-time-spans) a3]))
         span)))


;; Select envelope segment times
;; Returns map {:times [...],  :span name}
;; Where [...] is a list of segment times and name is a keyword
;; representing the most common time span.
;;
(defn- select-env-times []
  (build-time-selection-list)
  (let [n 60
        span (if @config/test-mode* :test (pick-time-span))]
    (if (= span :test)
      {:times (repeat n 0.0)
       :span :test}
      (let [acc* (atom [])]
        (dotimes [i n]
          (let [s (pick-time-span span)
                mn (first s)
                mx (second s)
                diff (- mx mn)
                time (+ mn (rand diff))]
            (swap! acc* (fn [q](conj q time)))))
        {:times @acc*
         :span (last span)}))))
      
               
;; (def alternate-env-styles 
;;   {:adsr  [:addsr :aadsr :asr :perc :perc2]
;;    :addsr [:adsr  :aadsr :asr :perc :perc2]
;;    :asr   [:adsr  :perc  :perc2]
;;    :perc  (flatten [:adsr  :addsr (repeat 6 :perc2)])
;;    :perc2 (flatten [:adsr  :addsr (repeat 6 :perc)])
;;    :perc3 [:perc  :perc2]})

(def ^:private env-style-selection-list* (atom nil))

(defn- build-env-style-selection-list []
  (reset! env-style-selection-list* 
          (flatten [(repeat (* 100 @config/p-env-style-asr*) :asr)
                    (repeat (* 100 @config/p-env-style-adsr*) :adsr)
                    (repeat (* 100 @config/p-env-style-addsr*) :addsr)
                    (repeat (* 100 @config/p-env-style-aadsr*) :aadsr)
                    (repeat (* 100 @config/p-env-style-perc*) :perc)
                    (repeat (* 100 @config/p-env-style-perc2*) :perc2)
                    (repeat (* 100 @config/p-env-style-perc3*) :perc3)])))

(build-env-style-selection-list)

                    

(defn- pick-env-style 
  ([]
   (build-env-style-selection-list)
   (rand-nth @env-style-selection-list*))
  ([style]
   (coin @config/p-env-alternate-style* 
         ;(rand-nth (style alternate-env-styles))
         (rand-nth (style @config/env-style-alternates*))
         style)))

(defn- asr [tlst offset]
  (let [a (nth tlst offset)
        r (nth tlst (+ offset 1))]
    [:att a :dcy1 0 :dcy2 0 :rel r :peak 1 :bp 1 :sus 1 :dcy 0]))

(defn- adsr [tlst offset]
  (let [a (nth tlst offset)
        d1 (nth tlst (+ offset 1))
        d2 0.0
        r (nth tlst (+ offset 2))
        pk 1.0
        bp (coin 0.7 (+ 0.5 (rand 0.5))(rand))
        sus bp]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))

(defn- addsr [tlst offset]
 (let [a (nth tlst offset)
       d1 (nth tlst (+ offset 1))
       d2 (nth tlst (+ offset 2))
       r (nth tlst (+ offset 3))
       pk 1.0
       bp (coin 0.7 (+ 0.5 (rand 0.5))(rand))
       sus (rand)]
   [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))


(defn- aadsr [tlst offset]
  (let [a (nth tlst offset)
        d1 (nth tlst (+ offset 1))
        d2 (nth tlst (+ offset 2))
        r (nth tlst (+ offset 3))
        pk (coin 0.7 (+ 0.6 (rand 0.4))(rand))
        bp (coin 0.7 (+ 0.5 (rand 0.5))(rand))
        sus (rand)]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))

(defn- perc [tlst offset]
  (let [a (coin 0.80 0.0 (rand 0.07))
        d1 (nth tlst (+ offset 0))
        d2 0.0
        r (* 2 d1)
        pk 1.0 
        sus (coin 0.5 (rand 0.25)(rand 0.06))
        bp sus]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))

(defn- perc2 [tlst offset]
  (let [a (coin 0.80 0.0 (rand 0.07))
        d1 (coin 0.60 (rand 0.2)(rand 0.5))
        d2 (max 1 (* 2 (nth tlst offset)))
        r (max 1 (* 2 (nth tlst (+ offset 1))))
        pk 1.0 
        bp (+ 0.5 (rand 0.25))
        sus (coin 0.7 0.1 (rand 0.25))]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))

(defn- perc3 [tlst offset]
   (let [a (coin 0.80 0.0 (rand 0.07))
        d1 (max 0.25 (nth tlst offset))
        d2 (max 1 (* 2 (nth tlst (+ offset 1))))
        r (max 1 (* 2 (nth tlst (+ offset 2))))
        pk 1.0 
        bp (+ 0.75 (rand 0.25))
        sus (coin 0.7 0.1 (rand 0.25))]
    [:att a :dcy1 d1 :dcy2 d2 :rel r :peak pk :bp bp :sus sus :dcy d1]))


(defn pitch-env [tlst enable]
  (if enable
    (let [t1 (nth tlst 13)
          t2 (nth tlst 14)
          t3 (nth tlst 15)
          r @config/pitch-env-max-level*
          a0 (sign (rand r))
          a1 (sign (rand r))
          a2 (sign (rand r))
          a3 (sign (rand r))]
      [:a0 a0 :a1 a1 :a2 a2 :a3 a3 :t1 t1 :t2 t2 :t3 t3])
  [:a0 0.0 :a1 0.0 :a2 0.0 :a3 0.0 :t1 1.0 :t2 1.0 :t3 1.0]))


;; Returns map of envelope parameters for each operator and the filter.
;; keys :op1, :op2, ..., :op8, :noise, :buzz and :filter
;; values [:att a :dcy1 d1 :dcy2 d2 :rel r :peak p :bp b :sus s :dcy d1]
;;
(defn select-envelopes [enable-pitch-env]
  (let [[times tspan](let [tsmap (select-env-times)]
                        [(:times tsmap)(:span tsmap)])
        offset* (atom 0)
        style (pick-env-style)
        acc* (atom {})]
    (if @config/verbose*
      (do 
        (println (format ";; Using %s %s envelopes" (name tspan)(name style)))
        (println (format ";; Pitch envelope enabled: %s" enable-pitch-env))))
    (doseq [op [:op1 :op2 :op3 :op4 :op5 :op6 :op7 :op8 :noise :buzz :xenv :filter]]
      (let [s (pick-env-style style)
            env (cond (= s :asr)(asr times @offset*)
                      (= s :adsr)(adsr times @offset*)
                      (= s :addsr)(addsr times @offset*)
                      (= s :aadsr)(aadsr times @offset*)
                      (= s :perc)(perc times @offset*)
                      (= s :perc2)(perc2 times @offset*)
                      :default (perc3 times @offset*))]
        (swap! acc* (fn [q](assoc q op env)))
        (swap! offset* (fn [q](+ q 4)))))
    (assoc @acc* :penv (pitch-env times enable-pitch-env))))
