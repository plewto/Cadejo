(println "cadejo.demo2")

;; Illustrates bank editor GUI 
;;

(ns cadejo.demo2
  (:require [cadejo.config :as config])
  (:require [overtone.core :as ot])
  (:require [cadejo.midi.scene])
  (:require [cadejo.instruments.algo.engine])
  (:require [cadejo.instruments.masa.engine])
  (:require [cadejo.instruments.combo.engine])
  (:require [cadejo.instruments.alias.engine])
  (:require [seesaw.core :as ss])
  (:require [cadejo.ui.util.laf :as laf]))

(ot/boot-external-server)

(def midi-input-port "UM1SX")
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


(def algo (cadejo.instruments.algo.engine/algo-poly s 0))
(def alias (cadejo.instruments.alias.engine/alias-mono s 1))
(def masa (cadejo.instruments.masa.engine/masa-poly s 2))
(def combo (cadejo.instruments.combo.engine/combo-poly s 3))

(if (config/load-gui)
  (let [algo-bank (.bank algo)
        algo-banked (.get-editor algo-bank)
        algo-frame (ss/frame :title "Algo Channel 0"
                             :content (.widget algo-banked :pan-main)
                             :on-close :nothing)
        alias-bank (.bank alias)
        alias-banked (.get-editor alias-bank)
        alias-frame (ss/frame :title "Alias Channel 1"
                              :content (.widget alias-banked :pan-main)
                              :on-close :nothing)
        masa-bank (.bank masa)
        masa-banked (.get-editor masa-bank)
        masa-frame (ss/frame :title "Masa Channel 2"
                              :content (.widget masa-banked :pan-main)
                              :on-close :nothing)
        combo-bank (.bank combo)
        combo-banked (.get-editor combo-bank)
        combo-frame (ss/frame :title "Combo Channel 3"
                              :content (.widget combo-banked :pan-main)
                              :on-close :nothing)]
    (.program-change algo-bank 0)
    (.program-change alias-bank 0)
    (.program-change masa-bank 0)
    (.program-change combo-bank 0)
    (.sync-ui algo-banked)
    (.sync-ui alias-banked)
    (.sync-ui masa-banked)
    (.sync-ui combo-banked)
    (ss/pack! algo-frame)
    (ss/pack! alias-frame)
    (ss/pack! masa-frame)
    (ss/pack! combo-frame)
    (ss/show! algo-frame)
    (ss/show! alias-frame)
    (ss/show! masa-frame)
    (ss/show! combo-frame)
    (laf/show-laf-frame)))
                             
                         
