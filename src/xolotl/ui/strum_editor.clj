(ns xolotl.ui.strum-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; includes
;;   spinner - delay ms
;;   radio <-- --> <--> Rnd
;;

(defn strum-editor [parent-editor seq-id]
  (let [spin-delay (factory/spinner 0 5000 10)
        pan-delay (factory/border-panel :center spin-delay
                                        :east (factory/label "Delay (ms)"))
        rpan-mode (factory/radio [["--->" :forward]["<---" :backward]
                                  ["<-->" :alternate]["RND" :random]]
                                 2 2 :font :bold-mono)
        pan-main (factory/border-panel :north pan-delay
                                       :center (:pan-main rpan-mode)
                                       :border (factory/border "Strum"))
        ]
    {:pan-main pan-main
     }))
                                  
