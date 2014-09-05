(println "--> cadejo.midi.performance")
(ns cadejo.midi.performance
  "A Performance is a node with a single Channel parent and a set of
   sc synths, properties and keymodes. "
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
  (:require [cadejo.midi.bend-handler])
  (:require [cadejo.midi.pressure-handler])
  (:require [cadejo.midi.cc.controller-suite])
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.midi.node])
  (:require [cadejo.util.col])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.string])
  (:require [cadejo.ui.midi.performance-editor])
  (:require [overtone.core :as ot]))

(defprotocol PerformanceProtocol

  (get-scene 
    [this]
    "Return grand-parent node of this")

  (logo 
    [this size]
    [this]
    "Return instrument logo icon
     size may be :small :medium or :large")

  (set-bank! 
    [this bnk]
    "Sets program bank for this.
     See cadejo.midi.program-bank/Bank")

  (bank 
    [this]
    "Returns the program bank.")
 
  (bend-handler
    [this])
    
  (pressure-handler
    [this])

  (controllers
    [this]
    "Return instance of ControllerSuite")

  (add-controller!
    [this id ctrl curve ivalue]
    "Convenience method add new controller to controller-suite")

  (add-control-bus! 
    [this id bus]
    "Adds a control-bus to this performance.
     id   - Unique keyword id
     bus  - sc control-bus
     See control-bus method.")
 
  (control-bus
    [this bus-id]
    "Returns the sc control-bus assigned to id. If no such bus exist
     return nil. Control buses established by the parent channel are
     automatically included. At a minimum :bend and :pressure will 
     be defined as well as numeric ids for any MIDI controllers 
     assigned for the channel. Any number of additional buses may be
     be assigned at the performance level. Such buses are not currently
     directly used by cadejo, instead they are provided for possible
     communication between performances in the future.")

  (control-bus-ids 
    [this]
    "Returns a list of all assigned control-buses. The list includes all
     buses defined by the parent Channel as well as the buses assigned
     by add-control-bus!")

  (add-audio-bus!
    [this id bus]
    "Adds assignment to sc to audio-bus. Currently theses assignments are 
     are not directly used bu cadejo, they are provided for possible 
     inter-performance communication in the future.")

  (audio-bus 
    [this id]
    "Returns the sc audio bus assigned to id. Return nil if no such
     bus exists.")

  (audio-bus-ids
    [this]
    "Returns a list of all assigned audio bus ids.")
  
  (add-synth! 
    [this id s]
    "Adds a sc synth to this performance.  Note there is a distinction
     between a 'synth' and a 'voice'. A synth in this context is a sc synth
     object which provides some global feature to the instrument. Typical
     examples would include vibrato oscillators or effects processors. A
     'voice' by contrast is a sc synth object which produces the primary
     instrument signal.")
  
  (synth 
    [this id]
    "Returns the synth object assigned to id.")

  (synth-ids
    [this]
    "Returns a list of synth ids.")

  (synths 
    [this]
    "Returns a list of all synths.")

  (add-voice!
    [this v]
    "Adds 'voice' synth to this performance. A voice is a sc synth which 
     directly generates the instruments signal. This is in contrast to 
     a performance 'synth' which is used for some global signal processing.
     The keymode will allocate voices in response to key up/down events.")
  
  (voices
    [this]
    "Returns a list of all voices for this performance.")

  (ctl
    [this param value]
    "Apply overtone.core/ctl to all synths and voices")
    
  (keymode 
    [this]
    "Returns the keymode object for this performance.
     See cadejo.midi.keymode cadejo.midi.mono-mode and
     cadejo.midi,poly-mode")

  (keynum-frequency 
    [this keynum]
    "Returns the note frequency for MIDI key number. The frequency is
     obtained by looking up keynum in the tuning-table.  By default a
     performance does not directly contain a tuning-table, instead it 
     inherits the default tuning-table from the root scene. The default
     table may be overridden by using 
     (.put-property this :tuning-table ttab) where ttab is an instance
     of cadejo.scale.tuning-table/TuningTable.

     The tuning-table may also be set at the channel level.")

  (map-velocity 
    [this v128]
    "Maps the integer MIDI velocity to a float between 0.0 and 1.0
     By default Performance does not directly contain a velocity map, 
     instead it inherits the map defined by the root scene. The default map
     may be overridden by assigning a mapping function to the key
    :velocity-map at either the performance of channel levels.")

  (set-tuning-table!
    [this ttab])

  (set-key-range! 
    [this low high]
    "Sets the operational key range for this performance. MIDI key events
     outside of this range are ignored.
     low - lower key lint
     high - upper key limit
     0 <= low < high < 128.")

  (key-range 
    [this]
    "Returns the key range as a vector [low high]")

  (key-in-range? 
    [this keynum]
    "Predicate true if keynum is within the key range.")

  (set-transpose! 
    [this n]
    "Transposition amount in steps.
    Transposes note does not effect key-range")

  (transpose 
    [this]
    "Return transposition in steps")

  (set-db-scale! 
    [this db]
    "Scale overall amplitude of synths by db")

  (db-scale 
    [this]
    "Return instrument amplitude scale factor in db")

  ;; all-notes-off (via keymode)
  ;;
  (reset
    [this])

  (handle-event 
    [this event])

  (buses? 
    [this]
    "Diagnostic, display current control bus values")
   
  (synth-tap? 
    [this id key]
    "Diagnostic, display synth tab")

  (voice-tap? 
    [this key]
    "Diagnostic, display tap value for all voices")

  ;; (programs? 
  ;;   [this]
  ;;   "Display program bank contents")

  ;; (program 
  ;;   [this pnum]
  ;;   "Simulate MIDI program-change")

  ;; (dump-program 
  ;;   [this pnum]
  ;;   "List program pnum parameters")

  (dump 
    [this verbose depth]
    [this verbose]
    [this])
)


(deftype Performance [parent-channel properties* bank* 
                      control-buses* audio-buses* 
                      synths* voices* keymode
                      bend-handler*
                      pressure-handler*
                      controller-suite
                      editor*]

    cadejo.midi.node/Node

    (node-type [this] :performance)

    (is-root? [this] (not parent-channel))

    (children [this] [])

    (parent [this] parent-channel)

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
                      (and parent-channel
                           (.get-property parent-channel key default))
                      default)]
        (if (= value :fail)
          (umsg/warning (format "Performance %s does not have property %s"
                                (get @properties* :id nil) key))
          value)))
    
    (get-property [this key]
      (.get-property this key :fail))
    
    (local-property [this key]
      (get @properties* key))

    (properties [this local-only]
      (set (concat (keys @properties*)
                   (if (and parent-channel (not local-only))
                     (.properties parent-channel)
                     nil))))
    
    (properties [this]
      (.properties this false))
    
    (get-editor [this]
      @editor*)
    
    (rep-tree [this depth]
      (let [pad (cadejo.util.string/tab depth)]
        (format "%sPerformance %s\n" pad (.get-property this :id))))

    PerformanceProtocol

    (get-scene [this]
      (.parent (.parent this)))

    (logo [this]
      (.logo this :small))

    (logo [this size]
      (let [des (cadejo.config/instrument-descriptor 
                 (.get-property this :instrument-type))]
        (if des 
          (.logo des size)
          nil)))

    (set-bank! [this bnk]
      (.parent! bnk this)
      (swap! bank* (fn [n] bnk)))

    (bank [this] @bank*)

    (bend-handler [this]
      @bend-handler*)

    (pressure-handler [this]
      @pressure-handler*)

    (controllers [this]
      controller-suite)

    (add-controller! [this id ctrl curve ivalue]
      (.add-controller! controller-suite id ctrl curve ivalue))

    (add-control-bus! [this id bus]
      (swap! control-buses* (fn [n](assoc n id bus))))
   
    (control-bus [this bus-id]
      (cond (= bus-id :bend)
            (.bus @bend-handler*)

            (= bus-id :pressure)
            (.bus @pressure-handler*)

            :default
            (or (get @control-buses* bus-id
                     (.bus controller-suite bus-id))
                (umsg/warning (format "Control bus %s does not exists in performance %s"
                                      bus-id (.get-property this :id))))))

     (control-bus-ids [this]
       (concat
        (.assigned-controllers controller-suite)
        '(:bend :pressure)
        (keys @control-buses*)))

    (add-audio-bus! [this id bus]
      (swap! audio-buses* (fn [n](assoc n id bus))))

    (audio-bus [this id]
      (or (get @audio-buses* id)
          (umsg/warning (format "No such audio-bus %s" id))))

    (audio-bus-ids [this]
      (keys @audio-buses*))

    (add-synth! [this id s]
      (swap! synths* (fn [n](assoc n id s))))

    (synth [this id]
      (or (get @synths* id)
          (umsg/warning (format "No such synth %s" id))))

    (synth-ids [this]
      (keys @synths*))

    (synths [this]
      (map second (seq @synths*)))

    (add-voice! [this s]
      (swap! voices* (fn [n](conj n s))))

    (voices [this] @voices*)
      
    (ctl [this param value]
      (let [s (.synths this)
            v (.voices this)
            a (flatten (merge s v))]
        (ot/ctl a param value)))

    (keymode [this]
      (umsg/warning "Performance.keymode not implemented")
      nil)

    (keynum-frequency [this keynum]
      (let [sregistry (.scale-registry (.get-scene this))
            scale-id (.get-property this :scale-id)
            tt (.table sregistry scale-id)]
        (.get-key-frequency tt keynum)))

    (map-velocity [this v128]
      (let [vmap-id (.get-property this :velocity-map)
            mapfn (cadejo.midi.curves/get-curve vmap-id)
            x (float (* 1/128 v128))
            vel (mapfn x)]
        vel))

    (set-tuning-table! [this ttab]
      (.put-property! this :tuning-table ttab))

    (set-key-range! [this low high]
      (.put-property! this :key-range [low high]))

    (key-range [this] (.get-property this :key-range))

    (key-in-range? [this keynum]
      (let [kr (.key-range this)]
        (and (>= keynum (first kr))
             (<= keynum (second kr)))))

    (set-transpose! [this n]
      (.put-property! this :transpose n))

    (transpose [this]
      (.get-property this :transpose 0))

    (set-db-scale! [this db]
      (.put-property! this :db-scale db)
      (ot/ctl (concat (.synths this)(.voices this)) :dbscale db))

    (db-scale [this]
      (.get-property this :dbscale))

    (reset [this]
      (.reset keymode)
      (.reset controller-suite)
      (.reset @bend-handler*)
      (.reset @pressure-handler*)
      (.program-change @bank* 0))

    (handle-event [this event]
      (let [cmd (:command event)]
        ;; :note-on
        ;; :note-off
        ;; :pitch-bend
        ;; :channel-pressure
        ;; :control-change
        ;; :program-change
        (cond (= cmd :note-on)
              (.key-down keymode event)

              (= cmd :note-off)
              (.key-up keymode event)

              (= cmd :pitch-bend)
              (.handle-event @bend-handler* event)

              (= cmd :channel-pressure)
              (.handle-event @pressure-handler* event)
                 
              (= cmd :control-change)
              (.handle-event controller-suite event)

              (= cmd :program-change)
              (.handle-event @bank* event)

              :default
              ;; Should never see this!
              (umsg/error "Performance.handle-event cond default"
                          (format "channel = %s  command = %s"
                                  (:channel event) cmd)))) )
      
     
    (buses? [this]
      (println "Performance control bus state")
      (doseq [id (.control-bus-ids this)]
        (let [bs (.control-bus this id)]
          (println (format "\tbus %-12s --> value %s" 
                           id (ot/control-bus-get bs))))))
        
    (synth-tap? [this id key]
      (let [s (.synth this id)]
        (if s
          (println (format "%s %s tap --> %s"
                           id key @(get-in s [:taps key] :fail)))
          (println "No such synth " id))))

    (voice-tap? [this key]
      (println "Voice tap")
      (let [vlist (.voices this)]
        (dotimes [n (count vlist)]
          (let [v (nth vlist n)
                value @(get-in v [:taps key] :fail)]
            (println (format "\tvoice %2d tap %s --> %s"
                             n key value))))))
      
    ;; (programs? [this]
    ;;   (.dump (.bank this)))

    ;; (program [this pnum]
    ;;   (let [slist (concat (.synths this)
    ;;                       (.voices this))
    ;;         bnk (.bank this)
    ;;         chan (.channel-number (.parent this))
    ;;         qevent {:channel chan :command :program-change :data1 pnum :data2 0}]
    ;;     (.handle-event bnk qevent slist)))

    ;; (program [this pnum]
    ;;   (let [bnk (.bank this)
    ;;         chan (.channel-number (.parent this))
    ;;         ev {:channel chan :command :program-change :data1 pnum :data2 0}]
    ;;     (.handle-event bnk ev)))

    ;; (dump-program [this pnum]
    ;;   (let [data (.data (.bank this))
    ;;         dmap (cadejo.util.col/alist->map data)]
    ;;     (println "Program " pnum)
    ;;     (doseq [k (sort (keys dmap))]
    ;;       (let [v (get dmap k)]
    ;;         (printf "\t[%-16f] --> %s\n" k v)))
    ;;     (println)))
               
    (dump [this verbose depth]
      (let [depth2 (inc depth)
            pad (cadejo.util.string/tab depth)
            pad2 (cadejo.util.string/tab depth2)]
        (printf "%sPerformance %s\n" pad (.get-property this :id))
        (if verbose
          (do 
            (doseq [k (.properties this :local-only)]
              (printf "%s[%-12s] --> %s\n"
                      pad2 k (.get-property this k)))
            (.dump controller-suite verbose (inc depth))))))

    (dump [this verbose]
      (.dump this verbose 0))

    (dump [this]
      (.dump this true 0)))

(defn- load-editor [pobj]
  (if (cadejo.config/load-gui)
    (let [rs (cadejo.ui.midi.performance-editor/performance-editor pobj)]
      rs)
    nil))

(defn performance [parent-channel id keymode bank descriptor & args]
  "Creates new Performance instance. 
   parent-channel - An instance of cadejo.midi.cahnnel
   id - Unique keyword id.
   keymode - An object implementing cadejo.midi.keymode.
   args - optional MIDI cc specifications
          each cc spec should be vector of form [id ctrl-num :curve init-value]
          where  id - unique keyword mnemonic
                 ctrl-num - MIDI controller number 0 <= ctrl < 128
                 :curve  - default mapping curve (typically :linear)
                 init-value - float initial bus value  0.0 <= ivalue <= 1.0"
  (let [bend-handler* (atom nil)
        pressure-handler* (atom nil)
        editor* (atom nil)
        csuite (cadejo.midi.cc.controller-suite/controller-suite)
        pobj (Performance. parent-channel 
                           (atom {})    ; local properties
                           (atom bank)
                           (atom {})    ; control buses
                           (atom {})    ; audio buses
                           (atom {})    ; synths
                           (atom [])    ; voices
                           keymode
                           bend-handler*
                           pressure-handler*
                           csuite
                           editor*)]
    (doseq [ccspec args]
      (let [[id cnum curve ival] ccspec]
        (.add-controller! csuite (keyword id)(int cnum)(keyword curve)(float ival))))
    (.add-performance! parent-channel id pobj)
    (.put-property! pobj :id id)
    (.put-property! pobj :descriptor descriptor)
    (.set-parent! keymode pobj)
    (reset! editor* (load-editor pobj))
    (reset! bend-handler* (cadejo.midi.bend-handler/bend-handler pobj))
    (reset! pressure-handler* (cadejo.midi.pressure-handler/pressure-handler pobj))
    pobj))


