(ns cadejo.midi.scene
  "A scene is the top-level cadejo structure. Each scene connects
   to a single MIDI in port and contains 16 MIDI channels."
  (:require [cadejo.midi.node])
  (:require [cadejo.midi.channel])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])  
  (:require [cadejo.midi.cc])
  (:require [cadejo.scale.tuning-table])
  (:require [overtone.midi :as midi]))

(defprotocol SceneProtocol 
  
  (channel 
    [this ci]
    "Returns the channel object with MIDI channel ci 
     0 <= ci < 16, The result is an instance of cadejo.midi.channel/Channel")

  (channel-dispatch 
    [this]
    "returns a function used to dispatch MIDI events to the appropriate 
     Channel objects.")

  (reset
    [this]
    "Resets all channels which in turn resets all performances to initial 
     conditions.")

  (dump 
    [this filter verbose depth]
    [this filter verbose]
    [this filter]
    [this]
    "Displays information about this scene and it's child channels.
     The form with the depth argument is used internally.
     filter - Filter selects which channels to included. If filter
              is nil then all 16 channels are dumped. Otherwise channel must 
              be a list holding the channels to display.
     verbose - flag indicating if additional information is to be included."))
 
(deftype Scene [channels* properties*]
    cadejo.midi.node/Node

    (node-type [this] :scene)

    (is-root? [this] true)

    (parent [this] nil)

    (children [this] @channels*)

    (put-property! [this key value]
      (let [k (keyword key)]
        (swap! properties* (fn [n](assoc n k value)))
        (.update-properties this k)
        k))

    (remove-property! [this key]
      (let [k (keyword key)]
        (swap! properties* (fn [n](dissoc n (keyword k))))
        (.update-properties this k)
        k))

    (get-property [this key default]
      (let [value (get @properties* key default)]
        (if (= value :fail)
          (do 
            (umsg/warning (format "Scene %s does not have property %s"
                                  (get @properties* :id "?") key))
            nil)
          value)))

    (get-property [this key]
      (.get-property this key :fail))

    ;; ignore local-only flag
    (properties [this local-only]
      (keys @properties*))

    (properties [this]
      (.properties this true))

    (update-properties [this key]
      (doseq [c (.children this)]
        (.update-properties c key)))

    SceneProtocol 

    (channel [this ci]
      (nth @channels* ci))

    (channel-dispatch [this]
      (fn [event]
        (let [ci (:channel event)]
          (if ci
            (let [chanobj (.channel this ci)]
              (.handle-event chanobj event))
            (do
              ;; FUTURE handle non-channel events here
              )))))

    (reset [this]
      (doseq [c (.children this)]
        (.reset c)))

    (dump [this chan-filter verbose depth]
      (let [depth2 (inc depth)
            pad (cadejo.util.string/tab depth)
            pad2 (cadejo.util.string/tab depth2)
            filter (if chan-filter
                     (fn [n](let [chan (.get-property n :channel)]
                              (ucol/member? chan chan-filter)))
                     (fn [n] true))]
        (printf "%sScene %s\n" pad (.get-property this :id))
        (if verbose 
          (doseq [k (sort (.properties this))]
            (printf "%s[%-12s] --> %s\n" 
                    pad2 k (.get-property this k true))))
        (doseq [chanobj @channels*]
          (if (filter chanobj)
            (.dump chanobj verbose depth2)))))

    (dump [this filter verbose]
      (.dump this filter verbose 0))

    (dump [this filter]
      (.dump this filter true))

    (dump [this]
      (.dump this nil true)))

;; ISSUE: 
;; IllegalArgumentException if midi-input-device-name does not exists
;; MidiUnavailableException if device in use
;;
(defn scene [midi-input-device-name]
  "Creates new Scene object connected to specified MIDI input port. 
   Either an IllegalArgumentException or a MidiUnavaliableException may be
   thrown if for some reason the specified port can not be connected."
  (let [chan-count 16
        input-device (midi/midi-in midi-input-device-name)
        channels* (atom [])
        properties* 
        (atom {:id (str midi-input-device-name)
               :input-device input-device
               :tuning-table cadejo.scale.tuning-table/default-tuning-table
               :velocity-map (math/linear-function 0 0.0 127 1.0)
               })
        sobj (Scene. channels* properties*)]
    (dotimes [ci chan-count]
      (let [cobj (cadejo.midi.channel/channel sobj ci)]
        (swap! channels* (fn [n](conj n cobj)))))
    (midi/midi-handle-events input-device (.channel-dispatch sobj)) 
    sobj))


