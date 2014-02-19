(ns cadejo.midi.pitch-bend
  "Defines MIDI pitch-bend response"
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(def cent math/cent)

(defn- normalize-bend 
  "Scales 14-bit MIDI bend value to interval (-1,+1)
   [d1 d2] - the two MIDI data bytes.
   [event] - The MIDI pitch-bend event"
  ([d1 d2]
     (let [data (bit-or (bit-shift-left d2 7) d1)
           magnitude (bit-and data 0x1FFF)
           sign (bit-and data 0x2000)]
       (if (zero? sign)
         (/ magnitude 8191.0)
         (/ (+ (bit-xor magnitude 0x1FFF) 1)
            -8192.0))))
  ([event]
     (normalize-bend (get event :data1)
                     (get event :data2))))

(defprotocol BendProtocol 

  (set-bend-range! 
    [this range]
    "Sets bend range in cents.")

  (bend-range
    [this]
    "returns bend range in cents.")
  
  (enable! 
    [this flag]
    "Enable/disable bend events.")

  (trace!
    [this flag]
    "Enable/disable diagnostic tracing.")

  (bus
    [this]
    "Returns sc control bus used for bend output.")

  (reset 
    [this]
    "Sets the bend-bus to 1.0")

  (handle-event 
    [this event]
    "Process MIDI pitch-bend event.")

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(deftype BendProperties [range* scale* enabled* trace* bbus]
    BendProtocol

    (set-bend-range! [this range]
      (swap! range* (fn [n] range))
      (swap! scale* (fn [n] (float (math/expt cent range)))))

    (bend-range [this] @range*)

    (enable! [this flag]
      (swap! enabled* (fn [n] flag)))

    (trace! [this flag]
      (swap! trace* (fn [n] flag)))

    (bus [this] bbus)

    (reset [this]
      (ot/control-bus-set! bbus 1.0))

    (handle-event [this event]
      (if @enabled*
        (let [norm (normalize-bend event)
              value (math/expt @scale* norm)]
          (if @trace*
            (printf "Bend chan [%02d] %5.3f\n"
                    (:channel event)
                    value))
          (ot/control-bus-set! bbus value))))

    (dump [this verbose depth]
      (let [pad (cadejo.util.string/tab depth)]
        (printf "%sBend range %s  enabled %s" pad @range* @enabled*)
        (if verbose
          (printf "  bus %s" bbus))
        (println)))

    (dump [this verbose]
      (.dump this verbose 0))

    (dump [this]
      (.dump this false 0)))
            

(defn bend-properties []
  "Creates new instance of BendProperties"
  (let [bobj (BendProperties. (atom 0)             ; range
                              (atom 1.0)           ; scale
                              (atom true)          ; enabled
                              (atom false)         ; trace
                              (ot/control-bus))]
    (.set-bend-range! bobj 200)
    (.reset bobj)
    bobj))
