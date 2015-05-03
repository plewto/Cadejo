(println "-->    cobalt genpatch amp")
(ns cadejo.instruments.cobalt.genpatch.amp
  (:require [cadejo.util.math :as math]))

(def test-mode false) ; in test-mode return registration [1 1/2 1/3 1/4 ...]

(def ^:private p-use-fm 0.5)
(def ^:private p-use-high-fm-indexes 0.3)
(def ^:private p-use-noise 0.4)
(def ^:private p-use-buzz 0.4)
(def ^:private clamp math/clamp)
(def ^:private coin math/coin)

(defn- srand 
  ([n]
   (let [s (coin 0.5 -1 1)]
     (* s (rand n))))
  ([]
   (srand 1)))

(defn create-fm-indexes []
  (if test-mode
    [2 2 2 2]
    (let [acc* (atom [])]
      (if (coin p-use-fm)
        (dotimes [i 4]
          (swap! acc* (fn [q](conj q (coin p-use-high-fm-indexes 
                                           (coin 0.90 (rand 8)(rand 100))
                                           (rand 3))))))
        (reset! acc* [0 0 0 0]))
      @acc*)))

;; Returns index of peak partail
;; gamut is used to inform selection.
;; Frequencies between 1 and 3 have highewst priority
;; Frequencies less then 1 have less priority
;; Frequencies bwetween 4 and 7 have less priority
;; Frequencies 8 and above are thrown out.
;;
(defn- pick-peak-partial-index [gamut]
  (let [acc* (atom [])]
    (dotimes [i (count gamut)]
      (let [f (nth gamut i)
            x (cond (< f 1.0) 2
                    (< f 4.0) 4
                    (< f 8.0) 1
                    :default 0)]
        (swap! acc* (fn [q](conj q (repeat x i))))))
    (rand-nth (flatten @acc*))))

(defn- select-noise-and-buzz [alist]
  (let [acc* (atom [])
        nse (nth alist 8)
        bzz (if (> (count alist) 9) (nth alist 9)(rand))]
    (dotimes [i 8](swap! acc* (fn [q](conj q (nth alist i)))))
    (swap! acc* (fn [q](conj q (coin 0.8 nse (+ 0.5 (rand 0.5))))))
    (swap! acc* (fn [q](conj q (coin 0.5 0 (coin 0.8 bzz (+ 0.5 (rand 0.5)))))))
    @acc*))


;; Returns partial "registration" IE list of amplitudes.
;; One partial is randomly selected to have the greatest amplitude.
;; Partials with frequencies cloest to the peak partial tend to 
;; have greater amplitudes, partialus further away tend to have lesser 
;; amplitudes.
;; 
(defn create-registration [gamut]
  (let [p-exp (coin 0.75 0.1 0.9)
        ppi (pick-peak-partial-index gamut)
        ppf (nth gamut ppi)
        acc* (atom [])]
    (doseq [f gamut]
      (let [r* (atom (float (/ (max ppf f)(min ppf f))))]
        (if (> @r*)(swap! r* (fn [q](/ 1.0 q))))
        (swap! acc* (fn [q](conj q (clamp (+ (coin 0.30 (+ 0.25 (srand)) 0)
                                             (coin p-exp (* @r* @r*) @r*))
                                          0 1))))))
    (if test-mode
      (map float [1 1 1 1 1 1 1 1 1 1])
      (select-noise-and-buzz @acc*))))
 


;; Create list of op velocity sensitivities
;; Partials with greater frequency tend to have greater velocity
;; sensitivity.
;;

(def ^:private p-use-velocity (if test-mode 0 0.5))
(def ^:private p-low-op-velocity 0.40)
(def ^:private p-mid-op-velocity 0.60)
(def ^:private p-high-op-velocity 0.80)

(defn select-op-velocity-sensitivities [gamut]
  (if (coin p-use-velocity)
    (let [acc* (atom [])]
      (dotimes [i 10]
        (let [f (nth gamut i)
              v (cond (<= f 2)(coin p-low-op-velocity (rand) 0)
                      (<= f 5)(coin p-mid-op-velocity (rand) 0)
                      :default p-high-op-velocity)]
          (swap! acc* (fn [q](conj q (float v))))))
      @acc*)
    (into [](repeat 10 0.0))))
        
  
(def ^:private p-use-pressure (if test-mode 0 0.3))

(defn select-op-pressure-sensitivities []
  (if (coin p-use-pressure)
    (let [acc* (atom [])]
      (dotimes [i 10]
        (swap! acc* (fn [q](conj q (coin 0.50 (rand) 0.0)))))
      @acc*)
    (into [](repeat 10 0.0))))
  
(def ^:private p-use-op-lfo (if test-mode 0 0.3))

(defn select-op-lfo-depths []
  (if (coin p-use-op-lfo)
    (let [acc* (atom [])]
      (dotimes [i 10]
        (swap! acc* (fn [q](conj q (coin 0.50 (rand) 0.0)))))
      @acc*)
    (into [](repeat 10 0.0))))
