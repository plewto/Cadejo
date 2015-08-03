(ns cadejo.ui.util.child-dialog
  "Provides dialog for making child node visible"
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss]))

(defn child-dialog [node]
  (let [jb-children (let [acc* (atom [])]
                      (doseq [c (.children node)]
                        (let [nt (.node-type c)
                              id (.get-property c :id)
                              jb (ss/button :text (format "%-12s id %s" nt id))]
                          (ss/listen jb :action (fn [_]
                                                  (let [ed (.get-editor c)]
                                                    (and ed (.show! ed)))))
                          (swap! acc* (fn [q](conj q jb)))))
                      @acc*)
        lab-north (ss/label :icon (lnf/read-icon :tree :down))
        pan-center (ss/grid-panel :columns 1 :rows (.count jb-children)
                                  :items jb-children)
        pan-main (ss/border-panel :center pan-center
                                  :north lab-north
                                  )
        jb-exit (ss/button :text "OK")
        dia (ss/dialog :content pan-main
                       :options [jb-exit]
                       :title "Show Child Nodes"
                       :type :plain)]
    (ss/listen jb-exit :action (fn [_](.dispose dia)))
    (ss/pack! dia)
    (ss/show! dia)))
                              
