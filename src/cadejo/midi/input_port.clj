(ns cadejo.midi.input-port
  (:require [cadejo.midi.node])
  (:require [cadejo.util.string])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.midi :as midi]))


(deftype MidiInputPort [parent* children* properties*]
  cadejo.midi.node/Node

  (node-type [this] :midi-input-port)

  (is-root? [this] (= (.parent this) nil))

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
        (umsg/warning (format "Channel %s does not have property %s"
                              (.channel-number this) key))
        value)))
  
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
        ((.event-dispatcher c) event))))
  
  (get-editor [this] nil)

  (rep-tree [this depth]
    (let [pad (cadejo.util.string/tab depth)
          sb (StringBuilder.)]
      (.append sb (format "%sMidiInputPort +%s\n" pad (.get-property this :id)))
      (doseq [p (.children this)]
        (.append sb (.rep-tree p (inc depth))))
      (.toString sb)))
)


(defn midi-input-port
  ([parent device-name]
   (let [children* (atom [])
         properties* (atom {})
         midi-device (midi/midi-in device-name)
         port-node (MidiInputPort. parent children* properties*)]
     (.put-property! port-node :id device-name)
     (.put-property! port-node :midi-device midi-device)
     (midi/midi-handle-events midi-device (.event-dispatcher port-node))
     port-node))
  ([device-name]
   (midi-input-port nil device-name)))
