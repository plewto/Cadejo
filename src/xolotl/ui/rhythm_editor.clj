(ns xolotl.ui.rhythm-editor
  (:require [xolotl.ui.factory :as factory])
  )


(defn- validator [text]
                          false)



(defn rhythm-editor [parent-editor seq-id]
  (let [rhythm-action (fn [txt] )
        ted (factory/text-editor "Rhythm Pattern"
                                 validator rhythm-action
                                 factory/rhythm-clipboard*
                                 parent-editor)
        sync-fn (fn [prog]
                  (println "rhythm-editor.sync-fn NOT ijmplemented")
                  )]
    {:pan-main (:pan-main ted)
     :sync-fn sync-fn}))          
        

