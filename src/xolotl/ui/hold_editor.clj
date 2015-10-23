(ns xolotl.ui.hold-editor
  (:require [xolotl.ui.factory :as factory])
  )


(defn- validator [text]
                          false)



(defn hold-editor [parent-editor seq-id]
  (let [hold-action (fn [txt] )
        ted (factory/text-editor "Hold Pattern"
                                 validator hold-action
                                 factory/hold-clipboard*
                                 parent-editor)
        sync-fn (fn [prog]
                  (println "hold-editor.sync-fn NOT ijmplemented")
                  )]
    {:pan-main (:pan-main ted)
     :sync-fn sync-fn}))          
        


