(println "--> cadejo.demo2")
(ns cadejo.demo2
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
  (:require [cadejo.midi.scene])
  (:require [seesaw.core :as ss])
  (:require [overtone.core :as ot]))

; ---------------------------------------------------------------------- 
;                             Load Instruments


;; ISSUE:
;; We are loading -all- instrument definitions. There should be some sort of
;; configuarable conditional load instead!

; ---------------------------------------------------------------------- 
;                                   Algo

(require '[cadejo.instruments.algo.algo-engine :as algo])
(def algo-descriptor algo/algo-descriptor) 
(cadejo.config/add-instrument! algo-descriptor)



; ---------------------------------------------------------------------- 
;                                   Alias

(require '[cadejo.instruments.alias.alias-engine :as alias])
(def alias-descriptor alias/alias-descriptor)
(cadejo.config/add-instrument! alias-descriptor)

; ---------------------------------------------------------------------- 
;                                   Combo

(require '[cadejo.instruments.combo.combo-engine :as combo])
(def combo-descriptor combo/combo-descriptor)
(cadejo.config/add-instrument! combo-descriptor)


; ---------------------------------------------------------------------- 
;                                   Masa

(require '[cadejo.instruments.masa.masa-engine :as masa])
(def masa-descriptor masa/masa-descriptor)
(cadejo.config/add-instrument! masa-descriptor)


; ---------------------------------------------------------------------- 
;                  Create scene -AFTER- instruments loaded


(try
  (ot/boot-external-server)
  (catch Exception ex
    (println "**** Assume server already up! ****")
    (println (.getMessage ex))))
(def midi-input-port "UM1SX")

(defonce s (cadejo.midi.scene/scene midi-input-port))
(def sed (.get-editor s))

(.create algo-descriptor  :poly [s 0 :algo-0-0])
(.create alias-descriptor :mono [s 1 :alias-1-0])
(.create combo-descriptor :poly [s 2 :combo-2-0])
(.create masa-descriptor  :poly [s 3 :masa-3-0])

(.sync-ui! sed)
(def f (.frame sed))
(ss/show! f)

