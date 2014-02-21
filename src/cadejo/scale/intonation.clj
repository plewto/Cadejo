(ns cadejo.scale.intonation
  (:require [cadejo.util.string])
  (:require [cadejo.util.math :as math]))

(def step math/step)
(def cent math/cent)

(defn a69->c0 
  "Returns frequency of MIDI key 0 relative to that of key 69"
  ([a440 notes-per-octave octave-size]
     (let [ratio (math/expt octave-size (/ 1.0 notes-per-octave))]
       (* a440 (math/expt ratio -69))))
  ([a440 notes-per-octave]
     (a69->c0 a440 notes-per-octave 2.0))
  ([a440]
     (a69->c0 a440 12))
  ([]
     (a69->c0 440.0)))


(defprotocol Intonation

  (get-name 
    [this])

  (notes-per-octave 
    [this])

  (octave-size
    [this])
 
  (get-frequency 
    [this keynum])

  (reference-key 
    [this])

  (reference-frequency
    [this])

  (template
    [this])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(deftype EqualTempScale [properties ftab*]
  Intonation
  
  (get-name [this]
    (:name properties))

  (notes-per-octave [this]
    (:notes-per-octave properties))

  (octave-size [this]
    (:octave-size properties))

  (get-frequency [this keynum]
    (nth @ftab* keynum))

  (reference-frequency [this]
    (:reference-frequency properties))

  (reference-key [this]
    (:reference-key properties))

  (template [this]
    (:template properties))

  (dump [this verbose depth]
    (let [pad1 (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab (inc depth))]
      (printf "%sEqualTempScale %s\n" pad1 (.get-name this))
      (if verbose
        (do 
          (printf "%snotes per octave : %s\n" pad2 (.notes-per-octave this))
          (printf "%soctave-size      : %s\n" pad2 (.octave-size this))
          (printf "%sreference-key    : %s\n" pad2 (.reference-key this))
          (printf "%sreference-freq   : %s\n" pad2 (.reference-frequency this))))))
  
  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this true)))

;; Return tuning-table with equal-temperment intonation
;; [notes-per-octave a440 octave-size]
;; [notes-per-octave a440]
;; [notes-per-octave]
;; []
;;
;; notes-per-octave - Number of notes per octave, default 12
;; a440 - The frequency of 'a440' MIDI key 69, default 440.0
;; octave-size - The ratio considerd as an octave, default 2.0
;; 
(defn eqtemp-scale 
  ([notes-per-octave a440 octave-size]
     (let [c0 (a69->c0 a440 notes-per-octave octave-size)
           properties* (atom {name (format "eqtemp-%s/%s" notes-per-octave octave-size)
                              :notes-per-octave (float notes-per-octave)
                              :octave-size (float octave-size)
                              :reference-key 69
                              :reference-frequency (float a440)})
           template* (atom [])
           common-ratio (math/expt octave-size (/ 1.0 notes-per-octave))
           ftab* (atom [])]
       (let [ratio* (atom 1.0)]
         (dotimes [i notes-per-octave]
           (swap! template* (fn [n](conj n @ratio*)))
           (swap! ratio* (fn [n](* n common-ratio)))))
       (swap! properties* (fn [n](assoc n :template @template*)))
       (let [freq* (atom c0)]
         (dotimes [keynum 128]
           (swap! ftab* (fn [n](conj n @freq*)))
           (swap! freq* (fn [n](* n common-ratio)))))
       (EqualTempScale. @properties* ftab*)))
  ([notes-per-octave a440]
     (eqtemp-scale notes-per-octave a440 2.0))
  ([notes-per-octave]
     (eqtemp-scale notes-per-octave 440.0))
  ([]
     (eqtemp-scale 12)))
                             
(def default-tuning-table (eqtemp-scale))                             
(def quarter-tone-table (eqtemp-scale 24))                              
