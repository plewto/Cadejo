(ns xolotl.ui.clock-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; Constructs clock-editor sub-panel
;; Includes * external clock selection checkbox
;;          * tempo spinner
;; ARGS:
;;   parent-editor - an instance of NodeEditor for Xolotl
;;   seq-id - keyword, either :A or :B
;;
;; RETURNS: map with keys :pan-main -> JPanel
;;                        :sync-fn -> GUI update function
;;
(defn clock-editor [parent-editor]
  (let [node (.node parent-editor)
        bank (.program-bank node)
        cb-external (factory/checkbox "External Clock")
        span-tempo (factory/spinner-panel "Tempo" 20 300 1)
        pan-main (factory/horizontal-panel
                  cb-external
                  (:pan-main span-tempo))
        sync-fn (fn [prog]
                  (let [tempo (.tempo prog)]
                    (.setValue (:spinner span-tempo) (int tempo))))]
    (.addActionListener cb-external
                        (proxy [ActionListener][]
                          (actionPerformed [_]
                            (let [val (if (.isSelected cb-external)
                                        :external
                                        :internal)]
                              (.global-param! node :clock val)))))
    (.addChangeListener (:spinner span-tempo)
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (let [val (.getValue (:spinner span-tempo))]
                              (.global-param! node :tempo val)))))
    {:pan-main pan-main
     :sync-fn sync-fn}))
