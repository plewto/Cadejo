(println "--> Loading Cadejo")
 (ns cadejo.core
   (:require [sgwr.util.color])
   (:require [sgwr.elements.element])
   (:require [cadejo.config :as config])  
   (:require [overtone.core :as ot])
   (:require [cadejo.midi.scene])
   (:require [cadejo.midi.channel])
   (:require [cadejo.midi.performance])
   (:require [cadejo.midi.pbank])
   (:require [cadejo.midi.mono-mode])
   (:require [cadejo.midi.poly-mode])
   (:require [cadejo.instruments.descriptor])
   
)

;; (require '[cadejo.instruments.algo.algo-engine :as algo])
;; (def algo-descriptor algo/algo-descriptor) 
;; (cadejo.config/add-instrument! algo-descriptor)

;; (require '[cadejo.instruments.alias.alias-engine :as alias])
;; (def alias-descriptor alias/alias-descriptor)
;; (cadejo.config/add-instrument! alias-descriptor)

(require '[cadejo.instruments.masa.masa-engine :as masa])
(def masa-descriptor masa/masa-descriptor)
(cadejo.config/add-instrument! masa-descriptor)

(require '[cadejo.instruments.combo.combo-engine :as combo])
(def combo-descriptor combo/combo-descriptor)
(cadejo.config/add-instrument! combo-descriptor)


(require 'cadejo.ui.splash)
