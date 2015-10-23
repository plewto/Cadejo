(ns xolotl.ui.controller-editor
  (:require [xolotl.ui.factory :as factory])
  )


(defn- validator [text]
  false)



;; cindex either 0 or 1
;;
(defn controller-editor [parent-editor seq-id cindex]
  (let [controller-action (fn [txt] )
        ted (factory/text-editor (format "Controller %s Pattern" (inc cindex))
                                 validator controller-action
                                 factory/int-clipboard*
                                 parent-editor)
        spin-ctrl (factory/spinner -1 127 1)
        ;; pan-ctrl (factory/border-panel :center spin-ctrl
        ;;                                :south (factory/label "CTRL Num"))
        ;; pan-ctrl (factory/vertical-panel (factory/vertical-strut 130)
        ;;                                  spin-ctrl
        ;;                                  (factory/label "CTRL Num"))
        pan-ctrl (factory/horizontal-panel spin-ctrl
                                           (factory/label "  CTRL "))
        
        pan-main (factory/border-panel :center (:pan-main ted)
                                       :south (factory/vertical-panel pan-ctrl
                                                                      (factory/vertical-strut)))
                                       
        sync-fn (fn []
                  (println "controller-editor.sync-fn NOT ijmplemented")
                  )]
    {:pan-main pan-main
     :sync-fn sync-fn}))          
        


