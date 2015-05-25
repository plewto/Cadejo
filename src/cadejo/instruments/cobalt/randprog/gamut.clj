(ns cadejo.instruments.cobalt.randprog.gamut
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.randprog.config :as config]))

(def ^:private coin math/coin)
(def ^:private approx math/approx)

(defn- build-gamut-selection-list []
  (flatten [(repeat (* 10 @config/p-gamut-harmonic*) :harmonic)
            (repeat (* 10 @config/p-gamut-semi-harmonic*) :semi-harmonic)
            (repeat (* 10 @config/p-gamut-inharmonic*) :inharmonic)]))

;; Returns keyword selecting one of three possible gamut types 
;; :harmonic :semi-harmonic or :inharmonic
;;
(defn- select-gamut-type []
  (if @config/test-mode*
    :test
    (rand-nth (build-gamut-selection-list))))

;; Generate list of n harmonics skipping every skip one.
;; skip <= 1 --> [1 2 3 4 5 6 7 8 ...]
;; skip = 2  --> [1 3 5 7 ...]
;; skip = 3  --> [1 2 4 5 7 8 ...]
;;
(defn- generate-harmonic-list [n skip]
  (let [f* (atom 1)
        acc* (atom [])]
    (while (< (count @acc*) n)
      (if (or (<= skip 1)(not (zero? (rem @f* skip))))
        (swap! acc* (fn [q](conj q @f*))))
      (swap! f* inc))
    @acc*))

;; Returns sorted list of n random values
;;
(defn- generate-inharmonic-list [n]
  (let [acc* (atom [])]
    (dotimes [i n]
      (swap! acc* (fn [q](conj q (+ 0.125 (rand 12))))))
    (swap! acc* sort)
    @acc*))



;; Create mangled version of harmonic-list hlst
;; The result is a 9-element partial list of operator frequencies
;; [op1 op2 op3 op4 op5 op6 noise1 noise2 buzz]
;; hlst - source list as returned by generate-harmonic-list
;;        (count hlst) must be greater then 10
;; p-cluster - probability an element of hlst will be repeated in result
;;  with random detune applied
;; chorus - maximum random detune, see math/approx
;; scale - scale factor applied to each list element.
;; 

(defn- mangle [hlst p-cluster chorus scale]
  (let [acc* (atom [])
        noise1-swap (coin 0.70 (int (rand 6)) 6)
        noise2-swap (let [q* (atom noise1-swap)]
                      (while (= @q* noise1-swap)
                        (reset! q* (coin 0.7 (int (rand 6)) 7)))
                      @q*)
        buzz-swap (coin 0.70 1 (int (rand 4)))]
    (doseq [f hlst]
      (swap! acc* (fn [q](conj q f)))
      (while (coin (min p-cluster 0.9))
        (swap! acc* (fn [q](conj q (approx f chorus))))))
    (swap! acc* (fn [q](into [] (sort < q))))
    (let [bcc* (atom [])]
      (dotimes [i 6]
        (swap! bcc* (fn [q](conj q (cond (= i noise1-swap)(nth @acc* 6)
                                         (= i noise2-swap)(nth @acc* 7)
                                         :default (nth @acc* i))))))
      (into [](map (fn [q](* (float q) scale))
                   (flatten [(sort < @bcc*)
                             (nth @acc* noise1-swap)
                             (nth @acc* noise2-swap)
                             (nth @acc* buzz-swap)]))) )))

    

;; Scale gamut values such that element pos has value 1.0
;;
(defn- normalize [hlst pos]
  (let [ref (float (nth hlst pos))]
    (println ref)
    (map (fn [q](/ ref q)) hlst)))
  

;; Format partial list for pretty printing
;;
(defn- pp-harmonic-list [hlst]
  (let [sb (StringBuilder. 100)]
    (.append sb ";; [")
    (dotimes [i 6]
      (.append sb (format "%6.4f " (float (nth hlst i))))
      (if (= i 2)(.append sb "  ")))
    (.append sb (format "  :noise1 %6.4f  :noise2 %6.4f  :buzz %6.4f]"
                        (float (nth hlst 6))
                        (float (nth hlst 7))
                        (float (nth hlst 8))))
    (.toString sb)))

;; Format ff ratio and fm bias for pretty printing
;;
(defn- pp-fm-list [fm bias]
  (format ";; FM [%6.4f %6.4f %6.4f %6.4f %6.4f %6.4f] Bias [%+6.2f %+6.2f %+6.2f %+6.2f %+6.2f %+6.2f]"
          (nth fm 0)(nth fm 1)(nth fm 2)(nth fm 3)(nth fm 4)(nth fm 5)
          (nth bias 0)(nth bias 1)(nth bias 2)(nth bias 3)(nth bias 4)(nth bias 5)))

;; Returns map {:gamut [1 2 3 ... noise1 noise2 buzz]  :fm [1 2 3 4 5 6]  :bias [1 2 3 4 5 6]}
;;              
(defn select-gamut [] 
  (let [gtype (select-gamut-type)
        skip (rand-nth [1 2 3 4])
        [plst fmlst fmbias](cond (= gtype :test)
                                 [@config/test-gamut*
                                  (repeat 6 1.0)
                                  (repeat 6 0.0)]

                                 (= gtype :harmonic)
                                 (let [fm* (atom [])
                                       bias* (atom [])]
                                   (dotimes [i 6]
                                     (swap! fm* (fn [q](conj q (rand-nth [0.25 0.5 0.5 0.5 1.0 1.0 1.0 2.0 2.0 3.0]))))
                                     (swap! bias* (fn [q](conj q (coin 0.1 (rand 3) 0)))))
                                 [(mangle (generate-harmonic-list 12 skip) 
                                          @config/p-gamut-cluster* 
                                          0.01 1.0)
                                  (map float @fm*)
                                  (map float @bias*)])
                                 
                                 (= gtype :inharmonic)
                                 (let [fm* (atom [])
                                       bias* (atom [])]
                                   (dotimes [i 6]
                                     (swap! fm* (fn [q](conj q (coin 0.5 1.0 (rand)))))
                                     (swap! bias* (fn [q](conj q (coin 0.05 (rand 100) 0.0)))))
                                   [(normalize (mangle (generate-inharmonic-list 12)
                                                       @config/p-gamut-cluster* 0.1 1.0)
                                               (rand-nth [1 2 3]))
                                    @fm* 
                                    @bias*])
                                 
                                 :default
                                 (let [plst (mangle (generate-harmonic-list 12 skip)
                                                    @config/p-gamut-cluster*
                                                    0.01 1.0)
                                       acc* (atom [])
                                       fm* (atom [])]
                                   (doseq [h plst]
                                     (swap! acc* (fn [q](conj q (coin 0.2 (+ 0.5 (rand 4)) h)))))
                                   (dotimes [i 6]
                                     (swap! fm* (fn [q](conj q (coin 0.80 (rand-nth [0.5 1.0 2.0 3.0])(rand))))))
                                   [plst @fm* (repeat 6 0.0) ]) )
        mx (apply #'max plst)
        glst (if (>= mx 12)(map (fn [q](* 0.5 q)) plst) plst)]
    (if @config/verbose*
      (do (println (format ";; Using %s gamut  skip = %d p-cluster = %3.2f" 
                           (name gtype) skip @config/p-gamut-cluster*))
          (println (pp-harmonic-list glst))
          (println (pp-fm-list fmlst fmbias))))
    [(into [] glst) (into [] fmlst) (into [] fmbias)]))
