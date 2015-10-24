(ns xolotl.ui.strum-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; includes
;;   spinner - delay ms
;;   radio <-- --> <--> Rnd
;;

(def msg00 "Illegal strum mode %s for Xolotl program '%s'")

(defn strum-editor [parent-editor seq-id]
  (let [spin-delay (factory/spinner 0 5000 10)
        pan-delay (factory/border-panel :center spin-delay
                                        :east (factory/label "Delay (ms)"))
        rpan-mode (factory/radio [["--->" :forward]["<---" :reverse]
                                  ["<-->" :alternate]["RND" :random]]
                                 2 2 :font :bold-mono)
        pan-main (factory/border-panel :north pan-delay
                                       :center (:pan-main rpan-mode)
                                       :border (factory/border "Strum"))
        sync-fn (fn [prog]
                  (let [mode (.strum-mode prog seq-id)
                        b (get (:buttons rpan-mode) mode)]
                    (.setValue spin-delay (int (.strum-delay prog seq-id)))
                    (if b
                      (.setSelected b true)
                      (let [msg (format msg00 mode (.program-name prog))]
                        (throw (IllegalArgumentException. msg))))))
        ]
    {:pan-main pan-main
     :sync-fn sync-fn
     }))
                                  
