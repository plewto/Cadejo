(ns xolotl.ui.pitch-editor
  (:require [xolotl.ui.factory :as factory])
  )


(defn- validator [text]
                          false)



(defn pitch-editor [parent-editor seq-id]
  (let [pitch-action (fn [txt] )
        ted (factory/text-editor "Pitch Pattern"
                                 validator pitch-action
                                 factory/pitch-clipboard*
                                 parent-editor)
        rpan-mode (factory/radio '[["SEQ" :seq]["RND" :rnd]["SR" :sr]] 3 1)
        pan-main (factory/border-panel :center (:pan-main ted)
                                       :east (factory/grid-panel 2 1
                                              (:pan-main rpan-mode)))
        sync-fn (fn []
                  (println "pitch-editor.sync-fn NOT ijmplemented")
                  )]
    {:pan-main pan-main
     :sync-fn sync-fn}))          
        


 
