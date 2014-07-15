(println "--> cadejo.midi.channel")
(ns cadejo.midi.channel
  "Provides management of a single MIDI channel of a scene."
  (:require [cadejo.midi.node])
;  (:require [cadejo.midi.cc])
;  (:require [cadejo.midi.pitch-bend])
;  (:require [cadejo.midi.pressure])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(defprotocol ChannelProtocol

  (channel-number 
    [this]
    "Return s the MIDI channel number between 0 and 15 inclusive")
  
  ;; (set-velocity-map! 
  ;;   [this mapfn]
  ;;   [this y0 y1]
  ;;   [this]
  ;;   "Sets channel velocity map. The velocity map is a function which
  ;;    converts incoming velocity values to a 'normalized' form.
  ;;    The map function should take a single integer argument between
  ;;    0 and 127 inclusive and return a float between 0.0 and 1.0 
  ;;    inclusive. If the y0 Y1 values are used the function is linear 
  ;;    between the points (0,y0) and (127,y1). If no arguments are
  ;;    provided the defaults are y0=0.0, y1=1.0")

  ;; (set-bend-range! 
  ;;   [this range]
  ;;   "Sets pitch bend range in cents.")

  ;; (bend-range
  ;;   [this]
  ;;   "Returns pitch-bend range in cents.")

  ;; (enable-bend!
  ;;   [this flag]
  ;;   "Enable bend events.")

  ;; (trace-bend!
  ;;   [this flag]
  ;;   "Enable diagnostic tracing of bend events.")

  ;; (set-pressure-map!
  ;;   [this mapfn]
  ;;   [this y0 y1]
  ;;   "Sets mapping function for MIDI channel-pressure events.
  ;;    The function should take a single integer value between 0
  ;;    and 127 inclusive and return a float. If y0 and y1 are specified
  ;;    the mapping function is linear between points (0,y0) and (127,y1)")

  ;; (enable-pressure!
  ;;   [this flag]
  ;;   "Enable channel-pressure events")

  ;; (trace-pressure!
  ;;   [this flag])

  ;; (add-controller! 
  ;;   [this ctrl mapfn initial-value]
  ;;   [this ctrl y0 y1 initial-value]
  ;;   [this ctrl]
  ;;   "Add controller assignment. 
  ;;    See cadejo.midi.cc.ControllerDistributor")

  ;; (enable-controller! 
  ;;   [this ctrl flag]
  ;;   "Enable specific controller events.
  ;;    ctrl - Either MIDI controller number or symbolic local controller 
  ;;    keyword.")

  ;; (trace-controller!
  ;;   [this ctrl flag]
  ;;   "Enable diagnostic tracing for specific MIDI controller events.")

  ;; (bus 
  ;;   [this id]
  ;;   "Return sc control bus.
  ;;    id may be :bend, :pressure, in integer MIDI controller number or a
  ;;    symbolic controller id. If no such bus exists return nil.")

  ;; (bus-ids 
  ;;   [this]
  ;;   "Return a list of assigned sc control-bus ids.
  ;;    The list will contain :bend :pressure and zero or more MIDI 
  ;;    controller numbers. Symbolic MIDI controller ids are not included.")

  (add-performance! 
    [this id pobj]
    "Add a performance to this channel.
     Each channel may have any number of performances.
     id   - A unique keyword identifying the performance 
     pobj - An instance of Performance.")
  
  (remove-performance! 
    [this id])
  
  (performance-ids 
    [this]
    "return list of all performance ids for this channel.")
 
  (handle-event 
    [this event]
    "Handle MIDI channel event.
     event should represent a MIDI channel event for this channel.")

  (reset 
    [this]
    "reset all control buses to their initial value.")

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(deftype Channel [parent children* properties*]
                  ;bend-properties 
                  ;pressure-properties
;                  controller-properties
;                  ]

  cadejo.midi.node/Node

  (node-type [this] :channel)

  (is-root? [this] (not parent))

  (children [this]
    (map second (seq @children*)))

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
                    (and parent
                         (.get-property parent key default))
                    default)]
      (if (= value :fail)
        (umsg/warning (format "Channel %s does not have property %s"
                              (.channel-number this) key))
        value)))
  
  (get-property [this key]
    (.get-property this key :fail))

  (properties [this local-only]
    (set (concat (keys @properties*)
                 (if (and parent (not local-only))
                   (.properties parent)
                   nil))))
  
  (properties [this]
    (.properties this false))

  (get-editor [this]
    (umsg/warning "Channel.get-editor not implemented"))

  (get-editor-frame [this]
    (umsg/warning "Channel.get-editor-frame not implemented"))

  ChannelProtocol

  (channel-number [this]
    (.get-property this :channel))
  
  ;; (set-velocity-map! [this mapfn]
  ;;   (.put-property! this :velocity-map mapfn))
  
  ;; (set-velocity-map! [this y0 y1]
  ;;   (.set-velocity-map! this (cadejo.util.math/linear-function 0.0 y0 1.0 y1)))

  ;; (set-velocity-map! [this]
  ;;   (.set-velocity-map! this identity))

  ;; (set-bend-range! [this range]
  ;;   (.set-bend-range! bend-properties range))

  ;; (bend-range [this]
  ;;   (.bend-range bend-properties))

  ;; (enable-bend! [this flag]
  ;;   (.enable! bend-properties flag))

  ;; (trace-bend! [this flag]
  ;;   (.trace! bend-properties flag))

  ;; (set-pressure-map! [this mapfn]
  ;;   (.set-mapfn! pressure-properties mapfn))

  ;; (set-pressure-map! [this y0 y1]
  ;;   (.set-mapfn! pressure-properties y0 y1))

  ;; (enable-pressure! [this flag]
  ;;   (.enable! pressure-properties flag))

  ;; (trace-pressure! [this flag]
  ;;   (.trace! pressure-properties flag))

  ;; (add-controller! [this ctrl mapfn initial-value]
  ;;   (.add-controller! controller-properties ctrl mapfn initial-value))

  ;; (add-controller! [this ctrl y0 y1 initial-value]
  ;;   (.add-controller! controller-properties ctrl y0 y1 initial-value))

  ;; (add-controller! [this ctrl]
  ;;   (.add-controller! controller-properties ctrl))
 
  ;; (enable-controller! [this ctrl flag]
  ;;   (.enable! controller-properties ctrl flag))

  ;; (trace-controller! [this ctrl flag]
  ;;   (.trace! controller-properties ctrl flag))

  ;; (bus [this id]
  ;;   (let [rs (cond ;(= id :bend)(.bus bend-properties)
  ;;                  ;(= id :pressure)(.bus pressure-properties)
  ;;                  :default (.bus controller-properties id))]
  ;;     ;; DO not display warning
  ;;     ;; (if (not rs)
  ;;     ;;   (umsg/warning (format "Channel %s does not have bus id %s"
  ;;     ;;                         (.channel-number this) id))
  ;;       rs))

  ;; (bus-ids [this]
  ;;   (concat (list :bend :pressure) 
  ;;           (.assigned-controllers controller-properties)))

  (add-performance! [this id pobj]
    (swap! children* (fn [n](assoc n id pobj))))
  
  (remove-performance! [this id]
    (swap! children* (fn [n](dissoc n id))))
  
  (performance-ids [this]
    (sort (keys @children*)))
 
  ;; (handle-event [this event]
  ;;   (let [cmd (:command event)]
  ;;     (cond (= cmd :pitch-bend)
  ;;           (.handle-event bend-properties event)
  ;;           (= cmd :channel-pressure)
  ;;           (.handle-event pressure-properties event)
  ;;           (= cmd :control-change)
  ;;           (.handle-event controller-properties event)
  ;;           :default
  ;;           (doseq [p (.children this)]
  ;;             (.handle-event p event)))))

  (handle-event [this event]
    (doseq [p (.children this)]
      (.handle-event p event)))

  (reset [this]
    ;(.reset bend-properties)
    ;(.reset pressure-properties)
    ;(.reset controller-properties)
    (doseq [p (.children this)]
      (.reset p)))

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)]
      (printf "%sChannel %s\n" pad (.channel-number this))
      (if verbose
        (do
          (doseq [k (sort (.properties this :local-only))]
            (printf "%s[%-12s] --> %s\n" pad2 k (.get-property this k "?")))
          ;; (.dump bend-properties verbose depth2)
          ;; (.dump pressure-properties verbose depth2)
          ;; (.dump controller-properties verbose depth2))
        ))
      (doseq [pid (.performance-ids this)]
        (let [pobj (get @children* pid)]
          (.dump pobj verbose depth2)))))
  
  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this true 0))) 


(defn channel [parent ci]
  "Create new Channel. instance 
   parent - An instance of Scene.
   ci - MIDI channel number ci {0,1,2,...15}
   Channels are not typically created directly. Instead the parent Scene
   calls the function to create it's channels."
  (let [children (atom {})
        properties (atom {:channel (int ci)})
        cobj (Channel. parent
                       children
                       properties)]
    cobj))

(println "<<- cadejo.midi.channel")
