(ns cadejo.midi.pressure
  "Defines MIDI channel-pressure response."
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(defprotocol PressureProtocol 
  
  (bus 
    [this]
    "returns sc control bus used for pressure output.")

  (enable!
    [this flag]
    "Enable/disable pressure events.")

  (trace!
    [this flag]
    "Enable/disable diagnostic tracing.")

  (set-mapfn!
    [this mapfn]
    [this y0 y1]
    "Sets pressure mapping function.
     mapfn should take a single integer argument, the MIDI pressure 
     0 <= pressure < 128 and return a float.
     If y0 and Y1 are specified the function is linear between the points 
     (0,Y0) and (127,y1), by default Y0 = 0.0 and Y1 = 1.0")

  (reset
    [this]
    "Sets pressure bus to 0.0")

  (handle-event 
    [this event])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(deftype PressureProperties [mapfn* enabled* trace* pbus]

  PressureProtocol

  (bus [this] pbus)

  (enable! [this flag]
    (swap! enabled* (fn [n] flag)))

  (trace! [this flag]
    (swap! trace* (fn [n] flag)))

  (set-mapfn! [this mapfn]
    (swap! mapfn* (fn [n] mapfn)))

  (set-mapfn! [this y0 y1]
    (.set-mapfn! this (math/linear-function 0 y0 127.0 y1)))

  (reset [this]
    (let [y0 (@mapfn* 0)]
      (ot/control-bus-set! pbus y0)))

  (handle-event [this event]
    (if @enabled*
      (let [p (:data1 event)
            v (@mapfn* p)]
        (if @trace*
          (printf "Pres chan [%02d] %5.3f\n" 
                  (:channel event) v))
        (ot/control-bus-set! pbus v))))

  (dump [this verbose depth]
    (let [pad (cadejo.util.string/tab depth)]
      (printf "%sPressure enabled %s" 
              pad
              @enabled*)
      (if verbose
        (printf "  map %s  bus %s"
                @mapfn* pbus))
      (println)))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false 0)))
      
      
(defn pressure-properties []
  "Creates new instance of PressureProperties"
  (let [pobj (PressureProperties. 
              (atom (fn [n](/ n 127.0))) ; mapfn 
              (atom true)                ; enable
              (atom false)               ; trace
              (ot/control-bus))]
    (.reset pobj)
    pobj))
