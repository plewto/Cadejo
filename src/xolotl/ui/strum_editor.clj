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
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        spin-delay (factory/spinner 0 5000 10)
        pan-delay (factory/border-panel :center spin-delay
                                        :east (factory/label "Delay (ms)"))
        mode-action (proxy [ActionListener][]
                      (actionPerformed [evn]
                        (let [src (.getSource evn)
                              mode (.getClientProperty src :id)
                              prog (.current-program bank)]
                          (.strum-mode! prog seq-id mode)
                          (.strum-mode! xseq mode))))
                          
        rpan-mode (factory/radio [["--->" :forward]["<---" :reverse]
                                  ["<-->" :alternate]["RND" :random]]
                                 2 2 :font :bold-mono
                                 :listener mode-action)
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
    (.addChangeListener spin-delay (proxy [ChangeListener][]
                                     (stateChanged[evn]
                                       (let [prog (.current-program bank)
                                             dly (int (.getValue spin-delay))]
                                         (.strum! xseq dly)
                                         (.strum-delay! prog seq-id dly)))))
    {:pan-main pan-main
     :sync-fn sync-fn
     }))
                                  
