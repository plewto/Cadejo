(println "\t--> program")

(ns cadejo.instruments.alias.program
  (:require [cadejo.midi.program])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg]))

(defonce bank (cadejo.midi.program/bank :Alias "Default bank"))
  

