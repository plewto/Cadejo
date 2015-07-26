(ns cadejo.midi.input-port
  (:require [cadejo.midi.node])
  (:require [cadejo.util.string])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.midi :as midi]))


;; (defprotocol MidiInputPortProtocol

;;   (event-dispatcher [this]
;;     (fn [event]
;;       (doseq [c (.children this)]
;;         ((.event-dispatcher c) event))))

;;   (dump
;;     [this verbose depth]
;;     [this verbose]
;;     [this])
;;   )

(deftype MidiInputPort [parent-node children* properties*]
  cadejo.midi.node/Node

  (node-type [this] :midi-input-port)

  (is-root? [this] (= (.parent this) nil))

  (parent [this] parent-node)

  (children [this] @children*)

  (is-child? [this obj]
    (ucol/member? @children* obj))
  
  (remove-child! [this child])
  
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
                    (and parent-node
                         (.get-property parent-node key default))
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
                 (if (and parent-node (not local-only))
                   (.properties parent-node)
                   nil))))
  
  (properties [this]
    (.properties this false))

  (get-editor [this] nil)

  (rep-tree [this depth]
    (let [pad (cadejo.util.string/tab depth)
          sb (StringBuilder.)]
      (.append sb (format "%sMidiInputPort +%s\n" pad (.get-property this :id)))
      (doseq [p (.children this)]
        (.append sb (.rep-tree p (inc depth))))
      (.toString sb)))

  MidiInputPortProtocol

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)]
      (printf "%sMidiInputPort %s\n" pad (.get-property this :id))
      (if verbose
        (doseq [k (sort (.properties this :local-only))]
          (printf "%s[%-12s] --> %s\n" pad2 k (.get-property this k "?"))))
      (doseq [c (.children this)]
        (.dump c verbose depth2))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this nil))
  ) 
  


(defn midi-input-port
  ([parent device-name]
   (let [children* (atom [])
         properties* (atom {})
         input-device (midi/midi-in device-name)
         port-node (MidiInputPort. parent children* properties*)]
     (.put-property! port-node :id device-name)
     (.put-property! port-node :device input-device)
     (midi/midi-handle-events input-device (.event-dispatcher port-node))
     port-node)
   ))
