(println "cadejo.demo")

(ns cadejo.demo
  (:require [cadejo.midi.scene])
  ;(:require [cadejo.scale.just :as just])
  ;(:require [cadejo.scale.tuning-table :as tuning-table])
  (:require [cadejo.scale.intonation :as intonation])
  (:require [cadejo.instruments.algo.engine])
  (:require [cadejo.instruments.masa.engine])
  (:require [cadejo.instruments.combo.engine]))
  
            
(def midi-input-port "UM1SX")

(defonce s (cadejo.midi.scene/scene midi-input-port))
(defonce c0 (.channel s 0))


       

;; ------------------------------------- DEMO 1  Basic setup
;; 
;; 3 poly phonic instruments 
;; Algo on MIDI channel 0
;; MASA channel 1
;; Combo channel 2

;(comment ------
(defonce algo-1 (cadejo.instruments.algo.engine/algo-poly s 0 ))
(.set-tuning-table! algo-1 intonation/quarter-tone-table)
(defonce masa-1 (cadejo.instruments.masa.engine/masa-poly s 1))
(defonce combo-1 (cadejo.instruments.combo.engine/combo-poly s 2))
;----- end-comment)

;; ------------------------------------- DEMO 2 mono/poly keysplit & layering
;;
;; Configure keysplit and layering by setting two instruments to the same
;; channel. Here a mono version of mono is used above key 60 and then
;; transpose it down an octave. The output of MASA is hot relative to ALGO
;; so amplitude of ALGO is boosted +9db.
;;
;; The pitch bend range is set to an octave at the channel level. Bend
;; range is specified in cents 1200 = octave.

(comment ------
(defonce algo-2 (cadejo.instruments.algo.engine/algo-mono s 0))
(.set-key-range! algo-2 60 127)
(.set-db-scale! algo-2 +9)
(.set-transpose! algo-2 -12)

(defonce masa-2 (cadejo.instruments.masa.engine/masa-poly s 0))
(.set-key-range! masa-2 0 60)

(.set-bend-range! c0 1200)
----- end-comment)



;; ------------------------------------- DEMO 3 Alternate tuning

;(defonce algo-3 (cadejo.instruments.algo.engine/algo-poly s 0))
;(.set-tuning-table! algo-3 (just/get-tuning-table 'just-c1))
;(def quarter-tone (tuning-table/get-tuning-table))tun
;(tuning-table/set-eqtemp-scale! quarter-tone 24)
;(defonce masa-3 (cadejo.instruments.masa.engine/masa-poly s 1))
;(.set-tuning-table! masa-3 quarter-tone)



(.dump s [0])
