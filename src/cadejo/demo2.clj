(println "--> cadejo.demo2")
(ns cadejo.demo2
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
 
  (:require [cadejo.midi.scene])
  (:require [cadejo.instruments.algo.engine]) 
  (:require [cadejo.instruments.alias.engine]) 
  (:require [cadejo.instruments.masa.engine]) 
  (:require [cadejo.instruments.combo.engine]) 
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


(def algo0 (cadejo.instruments.algo.engine/algo-poly     s 0 :algo-0))
(def alias1 (cadejo.instruments.alias.engine/alias-mono  s 1 :alias-1))
(def masa2 (cadejo.instruments.masa.engine/masa-poly     s 2 :masa-2))
(def combo3 (cadejo.instruments.combo.engine/combo-poly  s 3 :combo-3))


(.sync-ui! sed)
(def f (.frame sed))
(ss/show! f)

