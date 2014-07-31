(println "--> cadejo.demo2")
(ns cadejo.demo2
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
 
  (:require [cadejo.midi.scene])
  (:require [cadejo.instruments.algo.algo-engine :as algo]) 
  (:require [cadejo.instruments.alias.alias-engine :as alias]) 
  (:require [cadejo.instruments.masa.masa-engine :as masa]) 
  (:require [cadejo.instruments.combo.combo-engine :as combo]) 
  (:require [seesaw.core :as ss])
  (:require [overtone.core :as ot]))

(try
  (ot/boot-external-server)
  (catch Exception ex
    (println "**** Assume server already up! ****")
    (println (.getMessage ex))))
(def midi-input-port "UM1SX")

(defonce s (cadejo.midi.scene/scene midi-input-port))
(def sed (.get-editor s))


(def algo0  (algo/algo-poly    s 0 :algo-0))
(def alias1 (alias/alias-mono  s 1 :alias-1))
(def masa2  (masa/masa-poly    s 2 :masa-2))
(def combo3 (combo/combo-poly  s 3 :combo-3))


(.sync-ui! sed)
(def f (.frame sed))
(ss/show! f)

