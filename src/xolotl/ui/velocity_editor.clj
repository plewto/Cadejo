(ns xolotl.ui.velocity-editor
  (:require [xolotl.ui.factory :as factory])
  )


(defn- validator [text]
                          false)



(defn velocity-editor [parent-editor seq-id]
  (let [velocity-action (fn [txt] )
        ted (factory/text-editor "Velocity Pattern"
                                 validator velocity-action
                                 factory/int-clipboard*
                                 parent-editor)
        rpan-mode (factory/radio '[["SEQ" :seq]["RND" :rnd]["SR" :sr]] 3 1)
        pan-main (factory/border-panel :center (:pan-main ted)
                                       :east (:pan-main rpan-mode))
        sync-fn (fn []
                  (println "velocity-editor.sync-fn NOT ijmplemented")
                  )]
    {:pan-main pan-main
     :sync-fn sync-fn}))          
        


 
