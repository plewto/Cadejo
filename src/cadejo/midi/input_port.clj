(ns cadejo.midi.input-port
  "Defines MIDI input port"
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.child-dialog])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.midi :as midi-util])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.midi :as ot-midi])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf]))

(def ^:private font (ssf/font :name :monospaced :style :bold :size 16))
(def ^:private width 580)
(def ^:private height 470)

(deftype MidiInputPort [parent* children* properties* editor*]
  cadejo.midi.node/Node

  (node-type [this] :midi-input-port)

  (is-root? [this] (= (.parent this) nil))

  (find-root [this]
    (if (.is-root? this)
      this
      (.find-root (.parent this))))
  
  (parent [this] @parent*)

  (children [this] @children*)

  (is-child? [this obj]
    (ucol/member? obj (.children this)))

  (add-child! [this child]
    (if (not (.is-child? this child))
      (do
        (swap! children* (fn [q](conj q child)))
        (._set-parent! child this)
        true)
      false))
        
  (remove-child! [this child]
    (if (.is-child? this child)
      (let [predicate (fn [q](not (= q child)))]
        (swap! children* (fn [q](into [] (filter predicate q))))
        (._orphan! child)
        true)
      false))

  (_set-parent! [this obj]
    (reset! parent* obj))

  (_orphan! [this]
    (._set-parent! this nil))
  
  (put-property! [this key value]
    (let [k (keyword key)]
      (swap! properties* (fn [n](assoc n k value)))
      k))

  (remove-property! [this key]
    (let [k (keyword key)]
      (swap! properties* (fn [n](dissoc n k)))
      k))
 
  (get-property [this key default]
    (let [value (or (get @properties* key)
                    (and (.parent this)
                         (.get-property (.parent this) key default))
                    default)]
      (if (= value :fail)
        (umsg/warning (format "MidiInputPort does not have property %s" key)))
        value))
  
  (get-property [this key]
    (.get-property this key :fail))

  (local-property [this key]
    (get @properties* key))

  (properties [this local-only]
    (set (concat (keys @properties*)
                 (if (and (.parent this) (not local-only))
                   (.properties (.parent this))
                   nil))))
  
  (properties [this]
    (.properties this false))

  (event-dispatcher [this]
    (fn [event]
      (doseq [c (.children this)]
        ((.event-dispatcher c) event))
      ))
      
      
  
  (get-editor [this] @editor*)

  (set-editor! [this ed]
    (reset! editor* ed)) )


(deftype InputPortEditor [cframe mip]
  cadejo.ui.midi.node-editor/NodeEditor

  (cframe! [this cframe embed] nil) ;; not implemented

  (cframe [this] cframe)

  (jframe [this] (.jframe cframe))

  (set-icon! [this ico] (.set-icon! cframe ico))

  (show! [this] (.show! cframe))

  (hide! [this] (.hide cframe))

  (widgets [this] {})

  (widget [this key]
    (let [rs (get (.widgets this) key)]
      (if (not rs)
        (umsg/warning (format "MIDI input does not have %s widget" key)))
      rs))

  (add-widget! [this key obj] nil) ;; not implemented

  (node [this] mip)

  (set-node! [this ignore] nil) ;; not implemented

  (working [this flag] nil) ;; not implemented

  (status! [this msg]
    (.set-path-text! cframe msg))

  (warning! [this msg]
    (.set-path-text! cframe (format "WARNING: %s" msg)))

  (update-path-text [this]
    (let [pt (cadejo.midi.node/rep-path mip)]
      (.set-path-text! cframe pt)
      (doseq [c (.children mip)]
        (let [ced (.get-editor c)]
          (if ced (.update-path-text ced))))))

  (sync-ui! [this]
    (.update-path-text this)) )
    
(defn- mip-editor [mip]
  (let [cframe (cadejo.ui.cadejo-frame/cadejo-frame "MIDI Input" ""
                                                    [:progress-bar :status])
        jb-parent (.widget cframe :jb-parent)
        jb-child (.widget cframe :jb-child)
        device (.get-property mip :midi-device)
        device-info (fn []
                      (let [info (.getDeviceInfo device)
                            dname (.getName info)
                            description (.getDescription info)
                            vendor (.getVendor info)
                            version (.getVersion info)
                            flag (.isOpen device)]
                        (str (format "MIDI Input\n\n" flag)
                             (format "Open        : %s\n" flag)
                             (format "Name        : %s\n" dname)
                             (format "Description : %s\n" description)
                             (format "Vendor      : %s\n" vendor)
                             (format "Version     : %s\n" version))))
        tree-info (fn []
                    (cadejo.midi.node/rep-tree mip 0))
        txt (ss/text :text (device-info)
                     :multi-line? true :editable? false :font font)
        jb-tree (factory/icon-button :tree :info "Update tree info")
        ed (InputPortEditor. cframe mip)]
    (.size! cframe width height)
    (.help-topic! cframe :midi-input-port)
    (ss/listen jb-parent :action (fn [& _]
                                   (let [p (.parent mip)
                                         ped (and p (.get-editor p))]
                                     (and ped (.show! ped)))))
    (ss/listen jb-child :action (fn [& _]
                                  (cadejo.ui.util.child-dialog/child-dialog mip)))
                                     
    (ss/listen jb-tree :action (fn [& _]
                                 (let [s (str (device-info)
                                              "\n\nCadejo Process Tree:\n"
                                              (tree-info))]
                                   (ss/config! txt :text s)
                                   (.update-path-text ed))))
    
    (ss/config! (.widget cframe :toolbar) :items [jb-tree])
    (ss/config! (.widget cframe :pan-center) :center txt)
    (.set-editor! mip ed)
    ed))

(defn midi-input-port [device]
  (let [parent* (atom nil)
        children* (atom [])
        properties* (atom {})
        editor* (atom nil)
        transmitter (ot-midi/midi-in device)
        port-node (MidiInputPort. parent* children* properties* editor*)]
    (.put-property! port-node :id "")
    (.put-property! port-node :midi-device (:device transmitter))
    (ot-midi/midi-handle-events transmitter (.event-dispatcher port-node))
    (if (config/load-gui)
      (.set-editor! port-node (mip-editor port-node)))
    port-node))

