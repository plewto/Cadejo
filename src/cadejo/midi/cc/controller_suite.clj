(ns cadejo.midi.cc.controller-suite
  "Aggregates all MIDI controllers on a given channel"
  (:require [cadejo.util.string])
  (:require [cadejo.midi.cc.controller])
  (:require [overtone.core :as ot]))

(defprotocol ControllerSuite

  (get-controller 
    [this ctrl]
    "Return cadejo.midi.cc.Controller object assigned to ctrl
     ctrl - int MIDI controller number 0 <= ctrl < 128
     If no controller has been assigned to ctrl return nil")

  (bus 
    [this ctrl]
    "Return sc control bus associated with controller ctrl
     Return nil if ctrl does not exists.")

  (add-controller!
    [this ctrl curve ivalue]
    [this ctrl]
    "Add new controller assignment
     ctrl   - int, MIDI controller number  0 <= ctrl < 128
     curve  - keyword, mapping function, see cadejo.midi.curves, 
              default :linear
     ivalue - float, initial bus value, default 0.

     A sc control bus is automatically created to receive updates
     from ctrl events. If ctrl replaces an existing controller 
     assignment the bus is reused from the previous assignment.
     Returns instance of cadejo.midi.cc.Controller")

  (remove-controller!
    [this ctrl]
    "Removes any controller assigned to ctrl")

  (assigned-controllers 
    [this]
    "Returns unsorted list of assigned controller numbers")

  (enable!
    [this ctrl flag]
    [this flag]
    "Enables/disables controller events.
     If ctrl not specified then all controllers managed by this
     are effected.")

  (trace!
    [this ctrl flag]
    [this flag]
    "Enable/disable controller event tracing.
     If ctrl is not specified then all controllers managed by this
     are effected.")

  (reset
    [this]
    "Set all managed control buses t their initial value.")

  (handle-event
    [this event]
    "Distribute MIDI cc events to appropriate Controller object.
     Ignore if the event's controller number (:data1) has not been assigned.")
  
  (set-curve! 
    [this ctrl curve]
    "Sets the mapping function for controller ctrl")

  (curve 
    [this ctrl]
    "Returns the mapping function (as keyword) for controller ctrl")

  (set-scale!
    [this ctrl s]
    "Sets scale factor for controller ctrl")

  (scale
    [this ctrl]
    "Returns scale factor for controller ctrl")

  (set-bias!
    [this ctrl b]
    "Sets bias factor for controller ctrl")

  (bias 
    [this ctrl]
    "Returns bias factor for controller ctrl")

  (?bus 
    [this ctrl]
    "Diagnostic display current value of sc control bus assigned to 
     controller ctrl")

  (?buses
    [this]
    "Diagnostic display current values of all manged sc control buses")

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(defn controller-suite []
  (let [controllers* (atom {})]
    (reify ControllerSuite

      (get-controller [this ctrl]
        (get @controllers* ctrl))

      (bus [this ctrl]
        (let [cobj (.get-controller this ctrl)]
          (and cobj (.bus cobj))))

      (add-controller! [this ctrl curve ivalue]
        (let [old-cc (.get-controller this ctrl) ; reuse old bus if posible
              bus (or (and old-cc (.bus old-cc))
                      (ot/control-bus))
              cc (cadejo.midi.cc.controller/controller ctrl bus curve ivalue)]
          (swap! controllers* (fn [n](assoc n ctrl cc)))
          cc))

      (add-controller! [this ctrl]
        (.add-controller! this ctrl :linear 0))

      (remove-controller! [this ctrl]
        (swap! controllers* (fn [n](dissoc n ctrl))))

      (assigned-controllers [this]
        (keys @controllers*))

      (enable! [this ctrl flag]
        (let [cc (.get-controller this ctrl)]
          (if cc (.enable! cc flag))))

      (enable! [this flag]
        (doseq [ctrl (.assigned-controllers this)]
          (.enable! this ctrl flag)))

      (trace! [this ctrl flag]
        (let [cc (.get-controller this ctrl)]
          (if cc (.trace! cc flag))))
      
      (trace! [this flag]
        (doseq [ctrl (.assigned-controllers this)]
          (.trace! this ctrl flag)))

      (reset [this]
        (doseq [cc (map second (seq @controllers*))]
          (.reset cc)))

      (handle-event [this event]
        (let [ctrl (:data1 event)
              cc (.get-controller this ctrl)]
          (if cc (.handle-cc-event cc event))))
      
      (set-curve! [this ctrl curve]
        (let [cc (.get-controller this ctrl)]
          (if cc (.set-curve! cc curve))
          cc))

      (curve [this ctrl]
        (let [cc (.get-controller this ctrl)]
          (and cc (.curve cc))))

      (set-scale! [this ctrl s]
        (let [cc (.get-controller this ctrl)]
          (if cc (.set-scale! cc s))
          cc))

      (scale [this ctrl]
        (let [cc (.get-controller this ctrl)]
          (and cc (.scale cc))))

      (set-bias! [this ctrl b]
        (let [cc (.get-controller this ctrl)]
          (if cc (.set-bias! cc b))
          cc))

      (bias [this ctrl]
        (let [cc (.get-controller this ctrl)]
          (and cc (.bias cc))))

      (?bus [this ctrl]
        (let [cc (.get-controller this ctrl)
              bus (if cc (.bus cc) nil)]
          (if bus
            (println (format "cc bus %3d = %s" ctrl (ot/control-bus-get bus)))
            (println (format "cc bus %3d not assigned" ctrl)))))

      (?buses [this]
        (doseq [ctrl (sort (.assigned-controllers this))]
          (.?bus this ctrl)))

      (dump [this verbose depth]
        (let [pad1 (cadejo.util.string/tab depth)]
          (println (format "%sControllerSuite" pad1))
          (doseq [ctrl (sort (.assigned-controllers this))]
            (.dump (.get-controller this ctrl) verbose (inc depth)))))

      (dump [this verbose]
        (.dump this verbose 0))

      (dump [this]
        (.dump this true 0)))))
