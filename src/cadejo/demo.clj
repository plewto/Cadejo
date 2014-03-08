(println "cadejo.demo")

(ns cadejo.demo
  (:require [cadejo.midi.scene])
  (:require [cadejo.scale.just :as just])
  (:require [cadejo.scale.intonation :as intonation])
  (:require [cadejo.instruments.algo.engine])
  (:require [cadejo.instruments.masa.engine])
  (:require [cadejo.instruments.combo.engine])
  (:require [cadejo.instruments.alias.engine])
  )

            
(def midi-input-port "UM1SX")

(defonce s (cadejo.midi.scene/scene midi-input-port))

;; Execute MIDI program change from repl
;;
(defn prog 
  ([pnum chan]
     (let [event {:command :program-change
                  :channel chan
                  :data1 pnum
                  :data2 0}
           chanobj (.channel s chan)]
       (.handle-event chanobj event)))
  ([pnum](prog pnum 0))
  ([](prog 0)))
     

;; ------------------------------------- DEMO 1  Basic setup
;; 
;; 3 instruments 
;; polyphonic Algo on MIDI channel 0
;; monophonic Alias on MIDI channel 1
;; polyphonic Combo organ channel 2

(defonce algo-1 (cadejo.instruments.algo.engine/algo-poly s 0 ))
(defonce alias-1 (cadejo.instruments.alias.engine/alias-mono s 1))
(defonce combo-1 (cadejo.instruments.combo.engine/combo-poly s 2))

(prog 0 0)
(prog 1 1)
(prog 1 2)



(.dump s [0 1 2])
