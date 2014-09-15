(ns cadejo.ui.midi.properties-editor
  "Provides GUI for common node properties: bend, pressure, velocity, 
   dbscale, transpose, key-range and scale-id"
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.midi.curve-panel])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defprotocol PropertyEditor 

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
        vpan (reify PropertyEditor
               
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
        spin-scale (ss/spinner :model (ss/spinner-model 1.0 :from 0.1 :to 4.0 :by 0.1))
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
        ppan (reify PropertyEditor
                   
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
                       (.setValue spin-scale (Double. pscale))
                       (.setValue spin-bias (Double. pbias))
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
        bpan (reify PropertyEditor
               
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
                                (.put-property! (.node bpan)
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
                                       (.put-property! (.node bpan) :bend-curve crv)
                                       (.put-property! (.node bpan) :bend-range (.getValue spin-range)))
                                     (do
                                       (.setEnabled jb-dec false)
                                       (.setEnabled jb-inc false)
                                       (.setEnabled spin-range false)
                                       (.setEnabled pan-spin false)
                                       (.enable! curve-panel false)
                                       (.remove-property! (.node bpan) :bend-curve)
                                       (.remove-property! (.node bpan) :bend-range)))))
    (ss/listen jb-dec :action (fn [_]
                                (let [r (int (max 0 (- (.getValue spin-range) 100)))]
                                  (.setValue spin-range r))))

    (ss/listen jb-inc :action (fn [_]
                                (let [r (int (min 2400 (+ (.getValue spin-range) 100)))]
                                  (.setValue spin-range r))))

    (.addChangeListener spin-range
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                          (if @enable-change-listeners*
                            (let [r (.getValue spin-range)]
                              (.put-property! (.node bpan) :bend-range r))))))
    bpan))


(defn- misc-properties-panel []
  (let [parent* (atom nil)
        enable-change-listeners* (atom true)
        ;; dbscale property
        spin-dbscale (ss/spinner :model (ss/spinner-model 0 :from -99 :to 12 :by 3))
        cb-dbscale (ss/checkbox :text "Enable")
        pan-dbscale (ss/border-panel :north spin-dbscale
                                     :center cb-dbscale
                                     :border (factory/title "DB Scale"))
        ;; transpose property
        spin-transpose (ss/spinner :model (ss/spinner-model 0 :from -36 :to 36 :by 1))
        cb-transpose (ss/checkbox :text "Enable")
        pan-transpose (ss/border-panel :north spin-transpose
                                       :center cb-transpose
                                       :border (factory/title "Transpose"))
        ;; key-range property
        spin-range-low (ss/spinner :model (ss/spinner-model 0 :from 0 :to 127 :by 1))
        spin-range-high (ss/spinner :model (ss/spinner-model 127 :from 0 :to 127 :by 1))
        cb-range (ss/checkbox :text "Enable")
        pan-range-low (ss/vertical-panel :items [spin-range-low]
                                         :border (factory/title "Low"))
        pan-range-high (ss/vertical-panel :items [spin-range-high]
                                          :border (factory/title "High"))
        pan-range (ss/border-panel :north (ss/grid-panel :columns 1
                                                         :items [pan-range-low pan-range-high])
                                   :center cb-range
                                   :border (factory/title "Key Range"))
        ;; scale-id property
        combo-scale (ss/combobox :model [:eq-12]);(.registered-tables (.get-scene (.node @parent*))))
        cb-scale (ss/checkbox :text "Enable")
        pan-scale (ss/border-panel :north combo-scale
                                   :center cb-scale
                                   :border (factory/title "Scale"))

        pan-main (ss/vertical-panel :items [pan-dbscale pan-transpose pan-range pan-scale])
        widget-map {:spin-dbscale spin-dbscale
                    :cb-dbscale cb-dbscale
                    :pan-dbscale pan-dbscale
                    :spin-transpose spin-transpose
                    :cb-transpose cb-transpose
                    :pan-transpose pan-transpose
                    :spin-range-low spin-range-low
                    :spin-range-high spin-range-high
                    :pan-range-low pan-range-low
                    :pan-range-high pan-range-high
                    :cb-range cb-range
                    :combo-scale combo-scale
                    :cb-scale cb-scale
                    :pan-main pan-main}
        med (reify PropertyEditor
              
              (widgets [this] widget-map)
              
              (widget [this key]
                (get widget-map key))

              (set-parent-editor! [this ed]
                (reset! parent* ed))

              (node [this]
                (.node @parent*))

              (sync-ui! [this]
                (reset! enable-change-listeners* false)
                (let [node (.node this)
                      db (.local-property node :dbscale)
                      transpose (.local-property node :transpose)
                      krange (.local-property node :key-range)
                      scale-id (.local-property node :scale-id)]
                  (if db
                    (do 
                      (.setSelected cb-dbscale true)
                      (.setEnabled spin-dbscale true)
                      (.setValue spin-dbscale db))
                    (do 
                      (.setSelected cb-dbscale false)
                      (.setEnabled spin-dbscale false)))
                  (if transpose
                    (do
                      (.setSelected cb-transpose true)
                      (.setEnabled spin-transpose true)
                      (.setValue spin-transpose transpose))
                    (do 
                      (.setSelected cb-transpose false)
                      (.setEnabled spin-transpose false)))
                  (if krange
                    (let [low (apply min krange)
                          high (apply max krange)]
                      (.setSelected cb-range true)
                      (.setEnabled spin-range-low true)
                      (.setEnabled spin-range-high true)
                      (.setEnabled pan-range-low true)
                      (.setEnabled pan-range-high true)
                      (.setValue spin-range-low low)
                      (.setValue spin-range-high high))
                    (do
                      (.setSelected cb-range false)
                      (.setEnabled spin-range-low false)
                      (.setEnabled spin-range-high false)
                      (.setEnabled pan-range-low false)
                      (.setEnabled pan-range-high false)))
                  (let [previous-scale-id (.getSelectedItem combo-scale)]
                    (ss/config! combo-scale :model (.registered-tables (.get-scene (.node @parent*))))
                    (.setSelectedItem combo-scale previous-scale-id)
                    (if scale-id
                      (do 
                        (.setSelected cb-scale true)
                        (.setEnabled combo-scale true)
                        (.setSelectedItem combo-scale scale-id))
                      (do
                        (.setSelected cb-scale false)
                        (.setEnabled combo-scale false))))) 

                  (reset! enable-change-listeners* true)))]
    
    (ss/listen cb-dbscale :action 
               (fn [_]
                 (if (.isSelected cb-dbscale)
                   (do
                     (.setEnabled spin-dbscale true)
                     (.put-property! (.node @parent*) 
                                     :dbscale 
                                     (int (or (.getValue spin-dbscale) 0))))
                   (do
                     (.setEnabled spin-dbscale false)
                     (.remove-property! (.node @parent*) :dbscale)))))

    (.addChangeListener spin-dbscale
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (if @enable-change-listeners*
                              (.put-property! (.node @parent*)
                                              :dbscale
                                              (.getValue spin-dbscale))))))
    (ss/listen cb-transpose :action
               (fn [_]
                 (if (.isSelected cb-transpose)
                   (do 
                     (.setEnabled spin-transpose true)
                     (.put-property! (.node @parent*)
                                     :transpose
                                     (int (or (.getValue spin-transpose) 0))))
                   (do 
                     (.setEnabled spin-transpose false)
                     (.remove-property! (.node @parent*) :transpose)))))
    
    (.addChangeListener spin-transpose
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (if @enable-change-listeners*
                              (.put-property! (.node @parent*)
                                              :transpose
                                              (.getValue spin-transpose))))))
    (ss/listen cb-range :action
               (fn [_]
                 (if (.isSelected cb-range)
                   (let [a (.getValue spin-range-low)
                         b (.getValue spin-range-high)]
                     (.setEnabled spin-range-low true)
                     (.setEnabled spin-range-high true)
                     (.setEnabled pan-range-low true)
                     (.setEnabled pan-range-high true)
                     (.put-property! (.node @parent*)
                                     :key-range
                                     [(min a b)(max a b)]))
                   (do
                     (.setEnabled spin-range-low false)
                     (.setEnabled spin-range-high false)
                     (.setEnabled pan-range-low false)
                     (.setEnabled pan-range-high false)
                     (.remove-property! (.node @parent*) :key-range)))))
    
    (let [krange-listener (proxy [ChangeListener][]
                           (stateChanged [_]
                             (if @enable-change-listeners*
                               (let [a (.getValue spin-range-low)
                                     b (.getValue spin-range-high)]
                                 (.put-property! (.node @parent*)
                                                 :key-range
                                                 [(min a b)(max a b)])))))]
      (.addChangeListener spin-range-low krange-listener)
      (.addChangeListener spin-range-high krange-listener))

    (ss/listen cb-scale :action
               (fn [_]
                 (if (.isSelected cb-scale)
                   (let [sid (or (.getSelectedItem combo-scale) :eq-12)]
                     (.setEnabled combo-scale true)
                     (.put-property! (.node @parent*) :scale-id sid))
                   (do 
                     (.setEnabled combo-scale false)
                     (.remove-property! (.node @parent*) :scale-id)))))

    (ss/listen combo-scale :action 
               (fn [_]
                 (if @enable-change-listeners*
                   (.put-property! (.node @parent*)
                                  :scale-id
                                  (or (.getSelectedItem combo-scale) :eq-12)))))
    med))
                    


(defn properties-editor []
  "Returns general node PropertyEditor 
   includes: bend, pressure, velocity, dbscale, transpose & key-range"
  (let [vep (velocity-panel)
        pep (pressure-panel)
        bep (bend-panel)
        msp (misc-properties-panel)
        pan-main (ss/grid-panel :rows 1
                                :items [(.widget bep :pan-main)
                                        (.widget pep :pan-main)
                                        (.widget vep :pan-main)
                                        (.widget msp :pan-main)])
        wid {:velocity-panel vep
             :pressure-panel pep
             :bend-panel bep
             :misc-properties-panel msp
             :pan-main pan-main}
        ed (reify PropertyEditor

             (widgets [this] wid)

             (widget [this key]
               (get wid key))

             (set-parent-editor! [this p]
               (.set-parent-editor! vep p)
               (.set-parent-editor! pep p)
               (.set-parent-editor! msp p)
               (.set-parent-editor! bep p))

             (node [this]
               (.node vep))

             (sync-ui! [this]
               (.sync-ui! vep)
               (.sync-ui! pep)
               (.sync-ui! bep)
               (.sync-ui! msp)))]
    ed))
