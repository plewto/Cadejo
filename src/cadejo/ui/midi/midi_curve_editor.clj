(ns cadejo.ui.midi.midi-curve-editor
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.midi.curve-panel])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defprotocol MidiCurveEditor 

  (widgets
    [this])

  (widget 
    [this key])

  (set-parent-editor!
    [this ed])

  (node
    [this])
  
  (sync-ui!
    [this]))

(defn- velocity-panel []
  (let [parent* (atom nil)
        curve-panel (cadejo.ui.midi.curve-panel/curve-panel)
        cb-enable (ss/checkbox :text "Enable")
        pan-main (ss/border-panel :center (.widget curve-panel :pan-main)
                                  :south cb-enable
                                  :border (factory/title "Velocity"))
        widget-map {:curve-panel curve-panel
                    :cb-enable cb-enable
                    :pan-main pan-main}
        vpan (reify MidiCurveEditor
               
               (widgets [this] widget-map)
               
               (widget [this key]
                 (or (get widget-map key)
                     (umsg/warning (format "VelocityPanel does not have %s widget" key))))

               (set-parent-editor! [this ed]
                 (reset! parent* ed))

               (node [this]
                 (.node @parent*))

               (sync-ui! [this]
                 (let [node (.node this)
                       vcurve (.local-property node :velocity-map)]
                   (if vcurve
                     (do
                       (.setSelected cb-enable true)
                       (.set-curve! curve-panel vcurve)
                       (.enable! curve-panel true))
                     (do
                       (.setSelected cb-enable false)
                       (.enable! curve-panel false))))))]
    (doseq [jb (.widget curve-panel :buttons)]
      (ss/listen jb :action (fn [ev]
                              (let [src (.getSource ev)
                                    crv (.getClientProperty src :curve)]
                                (.put-property! (.node vpan)
                                                :velocity-map crv)))))
    
    (ss/listen cb-enable :action (fn [_]
                                   (if (.isSelected cb-enable)
                                     (let [crv (or (.get-curve curve-panel)
                                                   :linear)]
                                       (.put-property! (.node vpan)
                                                       :velocity-map crv)
                                       (.enable! curve-panel true))
                                     (do
                                       (.remove-property! (.node vpan) 
                                                          :velocity-map)
                                       (.enable! curve-panel false)))))
    vpan))




(defn- pressure-panel []
  (let [parent* (atom nil)
        curve-panel (cadejo.ui.midi.curve-panel/curve-panel)
        cb-enable (ss/checkbox :text "Enable")
        spin-scale (ss/spinner :model (ss/spinner-model 1.0 :from 0.25 :to 4.0 :by 0.1))
        spin-bias (ss/spinner :model (ss/spinner-model 0.0 :from -1.0 :to 1.0 :by 0.1))
        pan-scale (ss/vertical-panel :items [spin-scale]
                                     :border (factory/title "Scale"))
        pan-bias (ss/vertical-panel :items [spin-bias]
                                    :border (factory/title "Bias"))
        pan-south (ss/grid-panel :rows 1 :items [cb-enable pan-scale pan-bias])
        pan-main (ss/border-panel :center (.widget curve-panel :pan-main)
                                  :south pan-south
                                  :border (factory/title "Pressure"))
        widget-map {:curve-panel curve-panel
                    :cb-enable cb-enable
                    :spin-scale spin-scale
                    :spin-bias spin-bias
                    :pan-main pan-main}
        ppan (reify MidiCurveEditor
                   
               (widgets [this]
                 widget-map)

               (widget [this key]
                 (or (get widget-map key)
                     (umsg/warning (format "VelocityPanel does not have %s widget" key))))
               
               (set-parent-editor! [this ed]
                 (reset! parent* ed))
               
               (node [this]
                 (.node @parent*))
               
               (sync-ui! [this]        
                 (let [pcurve (.local-property (.node this) :pressure-curve)
                       pscale (float (.get-property (.node this) :pressure-scale))
                       pbias (float (.get-property (.node this) :pressure-bias))]
                   (if pcurve
                     (do
                       (.setSelected cb-enable true)
                       (.enable! curve-panel true)
                       (.setEnabled spin-scale true)
                       (.setEnabled spin-bias true)
                       (.setEnabled pan-scale true)
                       (.setEnabled pan-bias true)
                       (.set-curve! curve-panel pcurve)
                       (.setValue spin-scale pscale)
                       (.setValue spin-bias pbias)
                       )
                     (do
                       (.setSelected cb-enable false)
                       (.enable! curve-panel false)
                       (.setEnabled spin-scale false)
                       (.setEnabled spin-bias false)
                       (.setEnabled pan-scale false)
                       (.setEnabled pan-bias false))))))]
    (doseq [jb (.widget curve-panel :buttons)]
      (ss/listen jb :action 
                 (fn [ev]
                   (let [src (.getSource ev)
                         crv (.getClientProperty src :curve)]
                     (.put-property! (.node ppan) :pressure-curve crv)))))
      (ss/listen cb-enable :action
                 (fn [_]
                   (if (.isSelected cb-enable)
                     (let [pcurve (or (.get-curve curve-panel)
                                      :linear)
                           pscale (float (.getValue spin-scale))
                           pbias (float (.getValue spin-bias))]
                       (.enable! curve-panel true)
                       (.setEnabled spin-scale true)
                       (.setEnabled spin-bias true)
                       (.setEnabled pan-scale true)
                       (.setEnabled pan-bias true)
                       (.put-property! (.node ppan) :pressure-curve pcurve)
                       (.put-property! (.node ppan) :pressure-scale pscale)
                       (.put-property! (.node ppan) :pressure-bias pbias))
                     (do 
                       (.enable! curve-panel false)
                       (.setEnabled spin-scale false)
                       (.setEnabled spin-bias false)
                       (.setEnabled pan-scale false)
                       (.setEnabled pan-bias false)
                       (.remove-property! (.node ppan) :pressure-curve)
                       (.remove-property! (.node ppan) :pressure-scale)
                       (.remove-property! (.node ppan) :pressure-bias)))))
      (.addChangeListener spin-scale
                          (proxy [ChangeListener][]
                            (stateChanged [_]
                              (.put-property! (.node ppan)
                                              :pressure-scale
                                              (float (.getValue spin-scale))
                                              ))))
      (.addChangeListener spin-bias
                          (proxy [ChangeListener][]
                            (stateChanged [_]
                              (.put-property! (.node ppan)
                                              :pressure-bias
                                              (float (.getValue spin-bias))
                                              ))))
      ppan))

(defn- bend-panel []
  (let [parent* (atom nil)
        enable-change-listeners* (atom true)
        curve-panel (cadejo.ui.midi.curve-panel/curve-panel)
        cb-enable (ss/checkbox :text "Enable")
        spin-range (ss/spinner :model (ss/spinner-model 200 :from 0 :to 2400 :by 1))
        jb-inc (ss/button :text "++")
        jb-dec (ss/button :text "--")
        pan-spin (ss/border-panel :west jb-dec 
                                  :center spin-range
                                  :east jb-inc
                                  :border (factory/title "Range (cents)"))
        pan-south (ss/border-panel :west cb-enable
                                   :center pan-spin)
        pan-main (ss/border-panel :center (.widget curve-panel :pan-main)
                                  :south pan-south
                                  :border (factory/title "Bend"))
        widget-map {:curve-panel curve-panel
                    :cb-enable cb-enable
                    :jb-inc jb-inc
                    :jb-dec jb-dec
                    :spin-range spin-range
                    :pan-main pan-main}
        vpan (reify MidiCurveEditor
               
               (widgets [this] widget-map)
               
               (widget [this key]
                 (or (get widget-map key)
                     (umsg/warning (format "BendPanel does not have %s widget" key))))

               (set-parent-editor! [this ed]
                 (reset! parent* ed))

               (node [this]
                 (.node @parent*))

               (sync-ui! [this]
                 (reset! enable-change-listeners* false)
                 (let [node (.node this)
                       vcurve (.local-property node :bend-curve)]
                   (if vcurve
                     (do
                       (.setSelected cb-enable true)
                       (.setEnabled jb-dec true)
                       (.setEnabled jb-inc true)
                       (.setEnabled spin-range true)
                       (.setEnabled pan-spin true)
                       (.setValue spin-range (or (.local-property node :bend-range) 0))
                       (.set-curve! curve-panel vcurve)
                       (.enable! curve-panel true))
                     (do
                       (.setSelected cb-enable false)
                       (.setEnabled jb-dec false)
                       (.setEnabled jb-inc false)
                       (.setEnabled spin-range false)
                       (.setEnabled pan-spin false)
                       (.enable! curve-panel false))))
                 (reset! enable-change-listeners* true)))]
    (doseq [jb (.widget curve-panel :buttons)]
      (ss/listen jb :action (fn [ev]
                              (let [src (.getSource ev)
                                    crv (.getClientProperty src :curve)]
                                (.put-property! (.node vpan)
                                                :bend-curve crv)))))
    (ss/listen cb-enable :action (fn [_]
                                   (if (.isSelected cb-enable)
                                     (let [crv (or (.get-curve curve-panel)
                                                   :linear)]
                                       (.setEnabled jb-dec true)
                                       (.setEnabled jb-inc true)
                                       (.setEnabled spin-range true)
                                       (.setEnabled pan-spin true)
                                       (.enable! curve-panel true)
                                       (.put-property! (.node vpan) :bend-curve crv)
                                       (.put-property! (.node vpan) :bend-range (.getValue spin-range)))
                                     (do
                                       (.setEnabled jb-dec false)
                                       (.setEnabled jb-inc false)
                                       (.setEnabled spin-range false)
                                       (.setEnabled pan-spin false)
                                       (.enable! curve-panel false)
                                       (.remove-property! (.node vpan) :bend-curve)
                                       (.remove-property! (.node vpan) :bend-range)))))
    (ss/listen jb-dec :action (fn [_]
                                (let [r (max 0 (- (.getValue spin-range) 100))]
                                  (.setValue spin-range r))))

    (ss/listen jb-inc :action (fn [_]
                                (let [r (min 2400 (+ (.getValue spin-range) 100))]
                                  (.setValue spin-range r))))

    (.addChangeListener spin-range
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                          (if @enable-change-listeners*
                            (let [r (.getValue spin-range)]
                              (.put-property! (.node vpan) :bend-range r))))))
    vpan))


(defn midi-curve-editor []
  (let [vep (velocity-panel)
        pep (pressure-panel)
        bep (bend-panel)
        pan-main (ss/grid-panel :rows 1
                                :items [(.widget bep :pan-main)
                                        (.widget pep :pan-main)
                                        (.widget vep :pan-main)])
        wid {:velocity-panel vep
             :pressure-panel pep
             :bend-panel bep
             :pan-main pan-main}
        ed (reify MidiCurveEditor

             (widgets [this] wid)

             (widget [this key]
               (get wid key))

             (set-parent-editor! [this p]
               (.set-parent-editor! vep p)
               (.set-parent-editor! pep p)
               (.set-parent-editor! bep p))

             (node [this]
               (.node vep))

             (sync-ui! [this]
               (.sync-ui! vep)
               (.sync-ui! pep)
               (.sync-ui! bep)))]
    ed))
