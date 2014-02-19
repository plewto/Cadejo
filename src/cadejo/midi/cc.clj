(ns cadejo.midi.cc
  "Provides handlers for MIDI continuous controller events. 
   Protocols are defined for single controllers and aggregates of all 
   controllers on a specific channel."
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot]))


;; Local controller assignments
;; The local controller assignments are a global mapping between 
;; symbolic controller names and actual MIDI controller numbers. 
;; These assignments should be established during configuration time to 
;; match the user's studio environment.
;;
(def local-assignments* (atom {}))
(def reverse-local-assignments* (atom {}))

(defn assign-controller! [id ctrl mnemonic]
  "Define a local controller
   id       - keyword, symbolic controller id. By convention ids have the 
              form :cc-xx where xx is an integer between 00 and 99.
   ctrl     - integer, actual MIDI controller number 0 <= ctrl < 128.
   mnemonic - String, Descriptive name for controller. "
  (swap! local-assignments* 
         (fn [n](assoc n id (list ctrl id mnemonic))))
  (swap! reverse-local-assignments* 
         (fn [n](assoc n ctrl id))))

;; Establish bare-bones controller assignments.
(assign-controller! :cc01  1 :modwheel)
(assign-controller! :cc02  2 :breath)
(assign-controller! :cc04  4 :foot)
(assign-controller! :cc07  7 :volume)

(defn list-controllers 
  "Returns a sorted list of assigned controllers.
   By default the list contains the id keywords used with assign-controller!
   If optional reverse argument is true the result is a list of actual 
   MIDI controller numbers." 
  ([reverse]
     (println "Assigned Controllers:")
     (if reverse
       (doseq [k (sort (keys @reverse-local-assignments*))]
         (printf "\t[%03d] --> %s\n" 
                 k (get @reverse-local-assignments* k)))
       (doseq [k (sort (keys @local-assignments*))]
         (let [ccobj (get @local-assignments* k)
               ctrl (first ccobj)
               mnemonic (nth ccobj 2)]
           (printf "\t%-6s --> [%03d] %s\n" k ctrl mnemonic)))))
  ([]
     (list-controllers false)))

(defn local-controller-number [ctrl]
  "Return MIDI controller number assigned to ctrl.
   If ctrl is an integer between 0 and 127, return ctrl.
   Otherwise look up value using ctrl as id into assigned controllers."
  (or (and (integer? ctrl)(>= ctrl 0)(< ctrl 128) ctrl)
      (first (get @local-assignments* ctrl nil))))
    

;; ControllerProtocol defines properties for a single MIDI controller.
;; cc properties include
;;    controller-number - The MIDI controller number.
;;    bus               - sc control-bus updated to reflect control changes
;;    enabled-flag      - boolean, update bus only if flag is true.
;;    trace-flag        - boolean, if true and cc is enabled, print 
;;                        diagnostic information on control change.
;;    mapfn             - mapping function maps MIDI controller value 
;;                        (an integer between 0 and 127 inclusive) to 
;;                        eventual bus value. 
;;    initial-value     - Value of bus after reset.
;;    
(defprotocol ControllerProtocol 
  
  (controller-number 
    [this]
    "Returns MIDI controller number.")

  (bus 
    [this]
    "Returns sc control-bus")

  (enable! 
    [this flag]
    "If flag true enable this controller")

  (trace!
    [this flag]
    "If flag true display diagnostics info on controller events.")

  (set-mapfn!
    [this mapfn initial-value]
    [this y0 y1 initial-value]
    "Sets mapping function between MIDI controller values and bus value.
     mapfn should take a single integer argument between 0 and 127 inclusive
     and return a float. 
     For the y0 y1 form a linear function between the points 
     (0,y0)(127,y1) is used.")

  (reset
    [this])

  (handle-cc-event 
    [this event])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(deftype Controller [controller-number enabled* 
                     trace* cbus mapfn* initial-value*]
  ControllerProtocol 

  (bus [this] cbus)

  (enable! [this flag]
    (swap! enabled* (fn [n] flag)))

  (trace! [this flag]
    (swap! trace* (fn [n] flag)))

  (set-mapfn! [this mapfn initial-value]
    (swap! initial-value* (fn [n](float initial-value)))
    (swap! mapfn* (fn [n] mapfn)))

  (set-mapfn! [this y0 y1 initial-value]
    (.set-mapfn! this (math/linear-function 0 y0 127 y1) initial-value)) 

  (reset [this]
    (ot/control-bus-set! cbus @initial-value*))

  (handle-cc-event [this event]
    (if @enabled*
      (let [d2 (:data2 event)
            mfn @mapfn*
            value (mfn d2)]
        (if @trace*
          (println (printf ";; CTRL:%03d CHAN:%02d DATA2:%03d VALUE:%f" 
                         controller-number (:channel event) d2 value)))
        (ot/control-bus-set! cbus value))))

  (dump [this verbose depth]
    (let [pad (cadejo.util.string/tab depth)]
      (printf "%sController %3d  enabled: %s "
              pad 
              controller-number 
              (if @enabled* "YES" "NO "))
      (if verbose
        (printf "map: %s bus: %s" @mapfn* cbus))
      (println)))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false 0)))


(defn controller [ctrl cbus mapfn ivalue]
  "Creates a new instance of cadejo.midi.cc.Controller
   ctrl   - MIDI controller number or symbolic controller id.
            see local-controller-number
   cbus   - sc control bus
   mapfn  - value mapping function. 
            see set-mapfn! method for Controller.
   ivalue - Initial bus value"
  (Controller. (local-controller-number ctrl)
               (atom true)              ; enable flag
               (atom false)             ; trace flag
               cbus
               (atom (or mapfn (fn [n](/ n 127.0))))
               (atom (float ivalue))))
  


;; The ControllerDistributorProtocol defines management for all controllers
;; on a single channel. It is effectively a map between MIDI controller
;; numbers and Controller. objects.
;;
(defprotocol ControllerDistributorProtocol

  ;; return nil if cc unassigned
  (get-controller 
    [this ctrl]
    "Returns Controller object assigned to ctrl. 
    If no such controller exists return nil.
    ctrl - MIDI controller number or symbolic controller id.
    Result is either an instance of Controller or nil.")

  (bus 
    [this ctrl]
    "Returns sc control bus associated with controller.
     ctrl - MIDI controller number or symbolic controller id.
     Result is eith a sc control-bus or nil.")

  (add-controller! 
    [this ctrl mapfn ivalue]
    [this ctrl y0 y1 ivalue]
    [this ctrl]
    "Adds new controller assignment. 
     See controller function.
     If a controller is already assigned to ctrl it is replaced with the 
     new assignment. However any bus allocated to a previous assignment is
     reused for the new assignment.")

  (assigned-controllers 
    [this]
    "Returns a list of assigned controllers.")

  (enable! 
    [this ctrl flag]
    "Set enable flag for specific controller.")

  (trace!
    [this ctrl flag]
    "Set trace flag for specific controller.")

  (handle-event 
    [this event]
    "Process MIDI event to update control bus.
     event should represent a MIDI cc event on an appropriate channel.")

  (reset
    [this]
    "Sets all managed control buses to their initial value.")

  (?buses
    [this]
    "Diagnostic displays current values for all managed buses.")
    
  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(defn- normalize-mapfn [n](/ n 127.0))

(deftype ControllerDistributor [controllers*]
  ControllerDistributorProtocol

  (get-controller [this ctrl]
    (let [cnum (local-controller-number ctrl)]
      (get @controllers* cnum nil)))

  (bus [this ctrl]
    (let [cc (.get-controller this ctrl)]
      (and cc (.bus cc))))

  (assigned-controllers [this]
    (keys @controllers*))

  (add-controller! [this ctrl mapfn ivalue]
    (let [cnum (local-controller-number ctrl)
          old-cc (.get-controller this ctrl)
          bus (or (and cnum old-cc (.bus old-cc))
                  (ot/control-bus))]
      (if cnum 
        (let [cc (controller cnum bus mapfn ivalue)]
          (swap! controllers* (fn [n](assoc n cnum cc)))
          cc)
        (umsg/warning "cc/ControllerDistributer.add-controller!"
                      (format "Unknown controller %s" ctrl)))))

  (add-controller! [this ctrl y0 y1 ivalue]
    (.add-controller! this ctrl (math/linear-function 0 y0 127.0 y1) ivalue))

  (add-controller! [this ctrl]
    (.add-controller! this ctrl normalize-mapfn 0))

  (enable! [this ctrl flag]
    (let [ccobj (.get-controller this ctrl)]
      (if ccobj 
        (.enable! ccobj flag))))

  (trace! [this ctrl flag]
    (let [ccobj (.get-controller this ctrl)]
      (if ccobj
        (.trace! ccobj flag))))

  (handle-event [this event]
    (let [ccnum (:data1 event)
          cc (.get-controller this ccnum)]
      ;; (println (format "CC DEBUG :chan %02d :ctrl %03d :data %03d"
      ;;                  (:channel event)
      ;;                  (:data1 event)
      ;;                  (:data2 event)))
      (if cc (.handle-cc-event cc event))))

  (reset [this]
    (doseq [p @controllers*]
      (.reset (second p))))

  (?buses [this]
    (doseq [k (sort (keys @controllers*))]
      (let [b (.bus this k)]
        (printf "cc [%03d] bus value = %f\n"
                k (float (ot/control-bus-get b))))))

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)]
      (printf "%sControlelrs:\n" pad)
      (doseq [k (sort (keys @controllers*))]
        (let [cc (get @controllers* k)]
          (.dump cc verbose depth2)))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false 0)))

(defn controller-distributor []
  "Creates new instance of ControllerDistributor."
  (ControllerDistributor. (atom {})))

