;; Replacement for cadejo.midi.pitch-bend
(ns cadejo.midi.bend-handler
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.string])
  (:require [cadejo.midi.curves :as curves])
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

(defprotocol BendHandler

  (client
    [this])

  (enable!    
    [this flag]
    "Sets or removes local bend properties")

  ;; (is-enabled? 
  ;;   [this])

  (bend-range!                          ; :bend-range
    [this range])

  (bend-range
    [this])

  (bend-curve!                          ; :bend-curve
    [this curve])

  (bend-curve
    [this])
  
  (bend-scale
    [this])

  (mapfn 
    [this])

  (trace!  
    [this flag])

  (bus
    [this])

  (?bus
    [this])

  (reset
    [this])

  (handle-event
    [this event])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(defn bend-handler [client-node]
  (let [trace* (atom false)
        bbus (ot/control-bus)
        previous* (atom {:range 200 :curve :linear})
        bh (reify BendHandler
             
             (client [this] client-node)
             
             ;; (enable! [this flag]
             ;;   (.put-property! client-node :bend-enabled flag))

             (enable! [this flag]
               (if flag
                 (do 
                   (.put-property! client-node :bend-range (:range @previous*))
                   (.put-property! client-node :bend-curve (:curve @previous*)))
                 (do
                   (reset! previous* {:range (.get-property client-node :bend-range)
                                      :curve (.get-property client-node :bend-curve)})
                   (.remove-property! client-node :bend-range)
                   (.remove-property! client-node :bend-curve))))
             
             ;; (is-enabled? [this]
             ;;   (.get-property client-node :bend-enabled) true)
             
             (bend-range! [this cents]
               (.put-property! client-node :bend-range (int cents)))
             
             (bend-range [this]
               (.get-property client-node :bend-range 200))
             
             (bend-scale [this]
               (math/expt cent (.bend-range this)))
             
             (bend-curve! [this curve]
               (.put-property! client-node :bend-curve (keyword curve)))
             
             (bend-curve [this]
               (.get-property client-node :bend-curve :linear))
             
             (mapfn [this]
               (curves/get-bipolar-curve (.bend-curve this)))
             
             (trace! [this flag]
               (reset! trace* flag))
             
             (bus [this] bbus)
             
             (?bus [this]
               (ot/control-bus-get bbus))
             
             (reset [this]
               (ot/control-bus-set! bbus 1.0))
             
             (handle-event [this event]
               (let [norm (normalize-bend event)
                     mfn (.mapfn this)
                     scale (.bend-scale this)
                     value (float (math/expt scale (mfn norm)))]
                 (if @trace*
                   (println (format "Bend chan [%02d] %s(%f) --> %f"
                                    (:channel event)
                                    (.bend-curve this)
                                    (float norm)
                                    (float value))))
                 (ot/control-bus-set! bbus value)))
             
             (dump [this verbose depth]
               (let [pad (cadejo.util.string/tab depth)
                     pad2 (str pad pad)]
                 (printf "%sBendHandler range %s curve %s"
                         pad
                         (.bend-range this)
                         (.bend-curve this))
                 (if verbose
                   (do 
                     (printf "\n%smapfn %s\n" pad2 (.mapfn this))
                     (printf "\n%sbus   %s\n" pad2 (.bus this))
                     (printf "\n%sbus value %s" (ot/control-bus-get bbus))))
                 (println)))
             
             (dump [this verbose]
               (.dump this verbose 0))
             
             (dump [this]
               (.dump this true 0)))]
    (ot/control-bus-set! bbus 1.0)
    bh))
