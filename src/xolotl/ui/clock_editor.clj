(ns xolotl.ui.clock-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; checkbox - external
;; spinner - tempo
;;
(defn clock-editor [parent-editor]
  (let [cb-external (factory/checkbox "External Clock")
        span-tempo (factory/spinner-panel "Tempo" 20 300 1)
        pan-main (factory/horizontal-panel
                  cb-external
                  (:pan-main span-tempo))
        ]
    {:pan-main pan-main
     }))
  
