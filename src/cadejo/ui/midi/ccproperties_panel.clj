(ns cadejo.ui.midi.ccproperties-panel
  "Provides GUI properties panel for single MIDI continuous controller"
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.midi.curve-panel])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defn ccproperties-panel [descriptor cc-key]
  (let [parent* (atom nil)
        ccobj (fn []
                (let [n (.node @parent*)
                      cs (.controllers n)
                      cc (.get-controller cs cc-key)]
                  cc))
        curve-panel (cadejo.ui.midi.curve-panel/curve-panel)
        cb-enable (ss/checkbox :text "Enable")
        spin-scale (ss/spinner :model (ss/spinner-model 1.0 :from 0.20 :to 4.0 :by 0.1))
        spin-bias  (ss/spinner :model (ss/spinner-model 0.0 :from -1.0 :to 1.0 :by 0.1))
        pan-scale (ss/vertical-panel :items [spin-scale] :border (factory/title "Scale"))
        pan-bias (ss/vertical-panel :items [spin-bias] :border (factory/title "Bias"))
        pan-south (ss/grid-panel :rows 1 :items [cb-enable pan-scale pan-bias])
        pan-main (ss/border-panel :center (.widget curve-panel :pan-main)
                                  :south pan-south
                                  :border (factory/title (name cc-key)))
        widget-map {:curve-panel curve-panel
                    :cd-enable cb-enable
                    :spin-scale spin-scale
                    :spin-bias spin-bias
                    :pan-main pan-main}
        ccpan (reify cadejo.ui.midi.properties-editor/PropertyEditor
                
                (widgets [this] widget-map)

                (widget [this key]
                  (or (get widget-map key)
                      (umsg/warning (format "ccproperties-panel does not have %s widget" key))))

                (set-parent-editor! [this ed]
                  (reset! parent* ed)
                  (let [ccsuite (.controllers (.node ed))
                        cc (.get-controller ccsuite cc-key)
                        ctrl (.controller-number cc)]
                    (ss/config! pan-main :border (factory/title (format "%s %3d" cc-key (int ctrl)))))
                  )

                (node [this]
                  (.node @parent*))

                (sync-ui! [this]
                  (let [node (.node this)
                        cc (ccobj)
                        scale (.scale cc)
                        bias (.bias cc)
                        enable (.is-enabled? cc)
                        curve (.get-curve cc)]
                    (.setSelected cb-enable enable)
                    (.enable! curve-panel enable)
                    (.setEnabled spin-scale enable)
                    (.setEnabled pan-scale enable)
                    (.setEnabled spin-bias enable)
                    (.setEnabled pan-bias enable)
                    (.set-curve! curve-panel curve)
                    (.setValue spin-scale scale)
                    (.setValue spin-bias bias))) )]

    (ss/listen cb-enable :action (fn [_]
                                   (let [flag (.isSelected cb-enable)]
                                   (.enable! (ccobj) flag)
                                   (.enable! curve-panel flag)
                                   (.setEnabled spin-scale flag)
                                   (.setEnabled spin-bias flag)
                                   (.setEnabled pan-scale flag)
                                   (.setEnabled pan-bias flag))))

    (.addChangeListener spin-scale
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (.set-scale! (ccobj)(float (.getValue spin-scale))))))

    (.addChangeListener spin-bias
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (.set-bias! (ccobj)(float (.getValue spin-bias))))))
    (doseq [jb (.widget curve-panel :buttons)]
      (ss/listen jb :action
                 (fn [ev]
                   (let [src (.getSource ev)
                         crv (.getClientProperty src :curve)]
                     (.set-curve! (ccobj) crv)))))
    ccpan))
