;; Generate frequency gamuts
;;
(println "-->    cobalt genpatch gamut")
(ns cadejo.instruments.cobalt.genpatch.gamut
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.constants :as con]))


(def ^:private test-mode false) ;; in test-mode return gamut [1 2 3 4 5 6 7 8 9 1]
(def ^:private coin math/coin)
(def ^:private approx math/approx)

;; Generates harmonic gamut
;; skip      - int, harmonics to be skipped
;;             skip=2 -> odd only
;;             skip=3 -> multiples of 3 missing
;;             SKIP MUST BE EITHER nil OR int GREATER THEN 1!
;;             default nil 
;; p-cluster - float, probability of duplicate harmonic
;;             A random detune is applied to duplicate harmonics.
;;             default 0.0
;; chorus    - float, maximum random detune applied to duplicate
;;             harmonics, default 0.001  
;;
;; Returns list of con/op-count partial frequencies
;; The last partial (buzz) is randomly selected from the first 
;; 3 partials.
;;
(defn- harmonics [& {:keys [skip p-cluster chorus]
                   :or {skip nil
                        p-cluster 0
                        chorus 0.001}}]
  (let [acc* (atom [])
        f* (atom 1)
        previous* (atom nil)
        advance (fn [q](+ q (coin p-cluster 0 1)))]
    (while (< (count @acc*) (dec con/op-count))
      (if (not (and skip (zero? (rem @f* skip))))
        (if (= @f* @previous*)
          (swap! acc* (fn [q](conj q (approx @f* chorus))))
          (swap! acc* (fn [q](conj q @f*)))))
      (reset! previous* @f*)
      (swap! f* advance))
    (conj @acc* (nth @acc* (rand-nth [1 1 1 2 2 3])))))


;; Generates gamut with enharmonic partials
;; skip         - As in harmonics
;; p-cluster    - As in harmonics
;; p-enharmonic - float, probability partial will be enharmonic
;;                default 0.1
;;
;; Returns list of con/op-count partial frequencies
;; The last partial (buzz) is randomly selected from the first 
;; 3 partials.
;;
(defn- semi-harmonic [& {:keys [skip p-cluster p-enharmonic]
                        :or {skip nil
                             p-cluster 0
                             p-enharmonic 0.1}}]
  (let [hg (butlast (harmonics :skip skip :p-cluster p-cluster))
        acc* (atom [])]
    (doseq [h hg]
      (swap! acc* (fn [q](conj q (coin p-enharmonic (approx h 0.5) h)))))
    (swap! acc* (fn [q](sort < q)))
    (conj @acc* (nth @acc* (rand-nth [1 1 1 2 2 3])))))

;; Randomly exchange noise partial with another position.
;; g - list of partials
;; p-swap, probability of exchange, default 0.25
;; Returns list of partials.
;;
(defn- swap-noise [g & {:keys [p-swap]
                       :or {p-swap 0.75}}]
  (if (coin p-swap true false)
    (let [pos (int (rand 4))
          val (nth g pos)
          nse (nth g 8)
          acc* (atom [])]
      (doseq [f g]
        (cond (= f val)(swap! acc* (fn [q](conj q nse)))
              (= f nse)(swap! acc* (fn [q](conj q val)))
              :default (swap! acc* (fn [q](conj q f)))))
      @acc*)
    g))


(def ^:private harmonic-fm-ratios (flatten [(repeat  8 1/4)
                                            (repeat 15 1/2)
                                            (repeat 20 1/1)
                                            (repeat 20 2/1)
                                            (repeat 15 3/1)
                                            (repeat  8 4/1)
                                            (repeat  6 5/1)]))
(defn- fm-ratios [& {:keys [p-harmonic]
                     :or {p-harmonic 0.80}}]
  (let [use-harmonic (coin p-harmonic true false)
        acc* (atom [])]
    (if use-harmonic
      (dotimes [i 4]
        (swap! acc* (fn [q](conj q (rand-nth harmonic-fm-ratios)))))
      (dotimes [i 4]
        (swap! acc* (fn [q](conj q (coin 0.5 (rand-nth harmonic-fm-ratios)(rand 5)))))))
    (map float @acc*)))
  
(defn- fm-biases [& {:keys [p-harmonic p-use-bias]
                    :or {p-harmonic 0.80
                         p-use-bias 1.0}}]
  (let [acc* (atom [])]
    (dotimes [i 4]
      (swap! acc* (fn [q](conj q (coin p-use-bias (coin p-harmonic (rand 3)(rand 400)) 0)))))
    @acc*))

;; Returns map with keys
;; :gamut    -> list of 10 op/noise/buzz frequencies
;; :fm      -> list of 4 fm modulator frequencies
;; :fm-bias -> list of 4 fm modulator bias offsets.
;;
(defn create-gamut []
  (let [gtype (coin 0.60 :harmonic (coin 0.75 :semi-harmonic :enharmonic))
        skip (rand-nth [nil nil 2 2 3 4 5 7])
        cluster (coin 0.70 0.0 0.20)
        gamut*  (atom (cond (= gtype :harmonic)(harmonics :skip skip :p-cluster cluster)
                            (= gtype :semi-harmonic)(semi-harmonic :skip skip 
                                                                   :p-cluster cluster
                                                                   :p-enharmonic 0.1)
                            :default (semi-harmonic :skip skip
                                                    :p-cluster cluster
                                                    :p-enharmonic 0.9)))
        fm (fm-ratios :p-harmonic (cond (= gtype :harmonic) 1.0
                                        (= gtype :semi-harmonic) 0.8
                                        :default 0.7))]
    (if (> (nth @gamut* 7) 12)
      (reset! gamut* (map (fn [q](* 0.5 q)) @gamut*)))
    (reset! gamut* (swap-noise @gamut*))
    (if test-mode 
      {:gamut [1 2 3 4 5 6 7 8 9 1]
       :fm [1 1 1 1]
       :fm-bias [0 0 0 0]}
      {:gamut @gamut*
       :fm fm
       :fm-bias (fm-biases :p-harmonic 0.80 :p-use-bias 0.05)})))
