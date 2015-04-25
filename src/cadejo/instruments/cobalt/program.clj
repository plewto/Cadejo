(println "-->    cobalt program")

(ns cadejo.instruments.cobalt.program
  (:require [cadejo.midi.pbank])
  (:require [cadejo.midi.program])
  (:require [cadejo.util.col :as col])
  (:require [cadejo.util.user-message :as umsg]))

(defonce bank (cadejo.midi.pbank/pbank :cobalt))
