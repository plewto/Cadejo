(ns cadejo.midi.pressure-handler
  (:require [cadejo.util.string])
  (:require [cadejo.midi.curves :as curves])
  (:require [overtone.core :as ot]))

(defprotocol PressureHandler

  (client
    [this])

  (enable!   
    [this flag])

  (pressure-curve!                      ; :pressure-curve
    [this curve])
            
  (pressure-curve
    [this])

  (pressure-scale!                      ; :pressure-scale
    [this scale])

  (pressure-scale
    [this])

  (pressure-bias!                       ; :pressure-bias
    [this b])

  (pressure-bias
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


(defn pressure-handler [client-node]
  (let [trace* (atom false)
        pbus (ot/control-bus)
        previous* (atom {:curve :linear :scale 1.0 :bias 0.0})
        ph (reify PressureHandler

             (client [this] client-node)

             (enable! [this flag]
               (if flag
                 (do
                   (.put-property! client-node :pressure-curve (:curve @previous*))
                   (.put-property! client-node :pressure-scale (:scale @previous*))
                   (.put-property! client-node :pressure-bias (:bias @previous*)))
                 (do
                   (reset! previous* {:curve (.get-property client-node :pressure-curve)
                                      :scale (.get-property client-node :pressure-scale)
                                      :bias (.get-property client-node :pressure-bias)})
                   (.remove-property! client-node :pressure-curve)
                   (.remove-property! client-node :pressure-scale)
                   (.remove-property! client-node :pressure-bias))))

             (pressure-curve! [this curve]
               (.put-property! client-node :pressure-curve (keyword curve)))

             (pressure-curve [this]
               (.get-property client-node :pressure-curve))

             (pressure-scale! [this scale]
               (.put-property! client-node :pressure-scale (keyword scale)))

             (pressure-scale [this]
               (.get-property client-node :pressure-scale))

             (pressure-bias! [this bias]
               (.put-property! client-node :pressure-bias (keyword bias)))

             (pressure-bias [this]
               (.get-property client-node :pressure-bias))
               
             (mapfn [this]
               (curves/get-curve (.pressure-curve this)))

             (trace! [this flag]
               (reset! trace* flag))

             (bus [this] pbus)

             (?bus [this]
               (ot/control-bus-get pbus))

             (reset [this]
               (ot/control-bus-set! pbus 0.0))

             (handle-event [this event]
               (let [norm (/ (:data1 event) 127.0)
                     mfn (.mapfn this)
                     scale (.pressure-scale this)
                     bias (.pressure-bias this)
                     value (+ bias (* scale (mfn norm)))]
                 (if @trace*
                   (println (format "Pressure [%02d] scale %f  bias %f %s(%f) --> %f"
                                    (:channel event)
                                    (.pressure-scale this)
                                    (.pressure-bias this)
                                    (.pressure-curve this)
                                    norm value)))
                 (ot/control-bus-set! pbus value)))

             (dump [this verbose depth]
               (let [pad (cadejo.util.string/tab depth)
                     pad2 (str pad pad)]
                 (printf "%sPressureHandler range %s curve %s"
                         pad
                         (.bend-range this)
                         (.bend-curve this))
                 (if verbose
                   (do 
                     (printf "\n%smapfn %s\n" pad2 (.mapfn this))
                     (printf "\n%sbus   %s\n" pad2 (.bus this))
                     (printf "\n%sbus value %s" (ot/control-bus-get pbus))))
                 (println)))
             
             (dump [this verbose]
               (.dump this verbose 0))
             
             (dump [this]
               (.dump this true 0)))]
    (ot/control-bus-set! pbus 0.0)
    ph))
             
