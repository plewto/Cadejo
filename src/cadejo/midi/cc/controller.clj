(ns cadejo.midi.cc.controller
  "Defines single MIDI continuous controller.
   Each controller is associated with a MIDI controller number and a
   SuperCollider control bus.
   
   Bus values are updated in response to incoming MIDI cc events.
   Each controller has a curve (see cadejo.misi.curves) a scale factor
   and a bias value. The incoming MIDI data is first 'normalized' to 
   the float interval [0.0,1.0] which then serves as the argument to 
   the mapping function. 

        mapfn(x) * s + b --> bus

   Where x is the normalized MIDI value
         s is the scale factor
         b is the bias"

  (:require [cadejo.midi.curves])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(defprotocol Controller

  (controller-number 
    [this]
    "Returns MIDI controller number")

  (bus 
    [this]
    "Returns SuperCollider control bus")

  (enable!
    [this flag]
    "Enables/disables MIDI event handling")

  (trace! 
    [this flag]
    "Enables/disables event tracing")

  (set-curve! 
    [this curve-id]
    "Sets mapping function curve.
     curve-id is keyword, see cadejo.midi.curves")

  (get-curve
    [this]
    "Returns mapping function name as keyword")

  (set-scale!
    [this s]
    "Sets controller scale factor")

  (scale
    [this]
    "Returns controller scale factor")

  (set-bias!
    [this b]
    "Sets constant value added to controller")

  (bias 
    [this]
    "Returns bias value")

  (enable!
    [this flag]
    "Enables/disables MIDI event handling")

  (is-enabled? 
    [this]
    "Predicate true if controller is enabled")

  (trace! 
    [this flag]
    "Enables/disables event tracing")

  (reset 
    [this]
    "Sets control bus to initial value")

  (handle-cc-event 
    [this event])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(defn controller [ctrl cbus curve-id ivalue]
  "Construct Controller object
   ctrl     - int MIDI controller number,  0 <= ctrl < 128
   cbus     - SuperCollider control bus
   curve-id - keyword indicating the mapping function
   ivalue   - float, initial bus value."
    (let [enable* (atom true)
          trace* (atom false)
          curve* (atom curve-id)
          scale* (atom 1.0)
          bias* (atom 0.0)
          mapfn* (atom (cadejo.midi.curves/get-curve curve-id))]
      (reify Controller

        (controller-number [this] ctrl)

        (bus [this] cbus)

        (set-curve! [this curve-id]
          (reset! curve* curve-id)
          (reset! mapfn* (cadejo.midi.curves/get-curve curve-id)))
        
        (get-curve [this] @curve*)

        (set-scale! [this s]
          (reset! scale* (float s)))

        (scale [this] @scale*)

        (set-bias! [this b]
          (reset! bias* (float b)))

        (bias [this] @bias*)

        (enable! [this flag] (reset! enable* flag))

        (is-enabled? [this] @enable*)

        (trace! [this flag] (reset! trace* flag)) 

        (reset [this]
          (ot/control-bus-set! cbus ivalue))

        (handle-cc-event [this event]
          (if @enable*
            (let [data2 (:data2 event)
                  x (float (* 1/127 data2))
                  s @scale*
                  b @bias*
                  y (+ b (* s (float (@mapfn* x))))]
              (ot/control-bus-set! cbus y)
              (if @trace*
                (println 
                 (format 
                  "cc %3d chan [%02d] %s(%f)  scale = %f  bias = %f --> %f"
                  ctrl (:channel event) @curve* x s b y))))))

        (dump [this verbose depth]
          (let [pad1 (cadejo.util.string/tab depth)
                pad2 (str pad1 pad1)
                sb (StringBuilder.)]
            (.append sb (format "%s%scc curve %s   scale %s   bias %s\n" 
                                pad1 ctrl @curve* @scale* @bias*))
            (if verbose
              (do 
                (.append sb (format "%senbale    %s  trace %s\n" pad2 @enable* @trace*))
                (.append sb (format "%smapfn     %s\n" pad2 @mapfn*))
                (.append sb (format "%sbus       %s\n" pad2 cbus))
                (.append sb (format "%sbus value %s\n" pad2 (ot/control-bus-get cbus)))))
            (print (.toString sb))))

        (dump [this verbose]
          (.dump this verbose 0))

        (dump [this]
          (.dump this true 0)))))
