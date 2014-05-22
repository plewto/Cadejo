(println "cadejo.demo")

(ns cadejo.demo
  (:require [cadejo.midi.scene])
  (:require [cadejo.scale.just :as just])
  (:require [cadejo.scale.intonation :as intonation])
  (:require [cadejo.instruments.algo.engine])
  (:require [cadejo.instruments.masa.engine])
  (:require [cadejo.instruments.combo.engine])
  (:require [cadejo.instruments.alias.engine])
  (:require [cadejo.util.math :as math]))

            
;; midi-input-port must be changed to match a device on your machine.
;;
(def midi-input-port "UM1SX") 

;; Create a "scene" object connected to midi-input-port. 
;; A scene is the top-level Cadejo object.
;;
(defonce s (cadejo.midi.scene/scene midi-input-port))


;; Generate MIDI program-change event
;;
(defn prog 
  ([pnum c]
     (let [event {:command :program-change
                  :channel c
                  :data1 pnum
                  :data2 0}
           chanobj (.channel s c)]
       (.handle-event chanobj event)
       event))
  ([pnum]
     (prog pnum 0)))


;; ------------------------------------- DEMO-1  Basic setup
;; 
;; 4 instruments: 
;; Polyphonic Algo on MIDI channel 0
;; Monophonic Alias on MIDI channel 1
;; Polyphonic MASA organ channel 2
;; Polyphonic combo organ channel 3
;;
(defn demo-1 [scene]
  (let [algo (cadejo.instruments.algo.engine/algo-poly scene 0)
        alias (cadejo.instruments.alias.engine/alias-mono scene 1)
        masa (cadejo.instruments.masa.engine/masa-poly scene 2)
        combo (cadejo.instruments.combo.engine/combo-poly scene 3)]
    (.reset scene)
    (doseq [c '[0 1 2 3]]
      (prog 0 c))
    (.dump scene [0 1 2 3])
    (println "Ready ....")
    (list algo alias masa combo)))

;; ------------------------------------- DEMO-2  Key layers and splits
;; 
;; Monophonic Algo and Alias layered on channel 0
;; Polyphonic Algo and MASA with key split on channel 1
;;
(defn demo-2 [scene]
  (let [algo-0 (cadejo.instruments.algo.engine/algo-mono scene 0)
        alias-0 (cadejo.instruments.alias.engine/alias-mono scene 0)
        algo-1 (cadejo.instruments.algo.engine/algo-poly scene 1)
        masa-1 (cadejo.instruments.masa.engine/masa-poly scene 1)]
    (.set-key-range! algo-1 0 59)
    (.set-key-range! masa-1 60 127)
    (.set-transpose! masa-1 -12)
    (.set-db-scale! masa-1 -6)
    (.reset scene)
    (doseq [c '[0 1]]
      (prog 0 c))
    (.dump scene [0 1])
    (println "Ready ....")
    (list algo-0 alias-0 algo-1 masa-1)))

;; ------------------------------------- DEMO-3  Alternate tuning
;;
;; channel 0 - polyphonic algo with just intonation
;; channel 1 - layer mono algo and MASA with quarter-tone scale

(defn demo-3 [scene]
  (let [algo-0 (cadejo.instruments.algo.engine/algo-poly scene 0)
        algo-1 (cadejo.instruments.algo.engine/algo-mono scene 1)
        masa-1 (cadejo.instruments.masa.engine/masa-mono scene 1)
        chanobj-1 (.channel scene 1)]
    ;; tuning table may be set at performance or channel level.
    (.put-property! algo-0 :tuning-table (just/just-scale :just-c1))
    (.put-property! chanobj-1 :tuning-table (intonation/eqtemp-scale 24))
    (.set-db-scale! masa-1 -6)
    (.reset scene)
    (doseq [c '[0 1]]
      (prog 0 c))
    (.dump scene [0 1])
    (println "Ready ....")
    (list algo-0 algo-1 masa-1)))

;; ------------------------------------- DEMO-4 Mapping Functions, pitch bend
;; 
;; Velocity mapping is typically set at the channel level. The mapping
;; functions should take a single integer argument between 0 and 127
;; inclusive and return a float between 0.0 and 1.0 inclusive. On channel 0
;; are being made more sensitive using the sqrt function. For comparison an
;; identical instrument is defined without the map on channel 1. MIDI
;; pressure and continuous controller maps are also possible.
;;
;; Pitch bend is specified in cents and defaults to a whole-step (200
;; cents). demo-4 sets channel 0 pitch-bend to an octave (1200 cents).
;;
(defn demo-4 [scene]
  (let [vmap (fn [v](math/sqrt (* 1/127 v)))
        algo-0 (cadejo.instruments.algo.engine/algo-poly scene 0)
        algo-1 (cadejo.instruments.algo.engine/algo-poly scene 1)
        chanobj-0 (.channel scene 0)]
    (.set-velocity-map! chanobj-0 vmap)
    (.set-bend-range! chanobj-0 1200)
    (.reset scene)
    (prog 0 0)
    (.dump s [0])
    (println "Ready ....")
    (list algo-0 algo-1)))

(demo-1 s)
