(ns xolotl.ui.clock-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; checkbox - external
;; spinner - tempo
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
                  (let [;clk (.clock-source prog)
                        tempo (.tempo prog)]
                    ;(.setSelected cb-external (= clk :external))
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
