(println "    --> xolotl.eventgen")
(ns xolotl.eventgen
  (:require [xolotl.util :as util])
  (:import java.util.Timer
           java.text.SimpleDateFormat
           java.util.Date              
           java.util.TimerTask))


(def date_formatter (SimpleDateFormat. "mm:ss"))  
  
(def trace-all false)
(def trace-keys false)
(def trace-controllers false)


(defn- dispatch [event nodes*]
  (if trace-all (println (format "Xolotl trace event at %s -> %s" 
                                 (.format date_formatter (Date.))  event)))
  (doseq [c @nodes*]
    ((.event-dispatcher c) event)))

(defn- note-off [c0 keynum hold-time nodes*]
  "(note-off c0 keynum hold-time nodes*)
   Generates note-off event
   ARGS:
     c0         - int, MIDI channel, zero-indexed.
     keynum     - int, MIDI key-number
     hold-time  - float, delay time in seconds
     nodes*  - list of Cadejo nodes
   RETURNS: nil"
  (let [event {:command :note-off :channel c0
               :note keynum :data1 keynum :data2 0}
        timer (Timer. false)
        task (proxy [TimerTask][]
               (cancel [& this] false)
                 (scheduledExecutionTime [& this] 0)
                 (run [& this]
                   (dispatch event nodes*)
                   (.cancel timer)))]
    (.schedule timer task (long (* 1000 hold-time)))
    nil))

(def ^:private strum-counter* (atom false))

(defn- strum-notes [c0 klst velocity strum strum-mode hold-time nodes*]
  "(strum-notes c0 klst velocity strum hold-time nodes*)
   Generate note-on events with successive delay
   ARGS:
     c0         - int, MIDI channel, zero-indexed
     klst       - list of MIDI key-numbers. 
     velocity   - int, MIDI velocity.
     strum      - long, delay time in milliseconds between successive events.
     strum-mode - keyword
     hold-time  - float, note duration in seconds.
     nodes*     - list of Cadejo nodes
   RETURNS: nil"
  (let [counter* (atom 0)
        smode (cond (= strum-mode :alternate)
                    (get {false :forward, true :reverse}
                         (swap! strum-counter* not))
                    :else
                    strum-mode)
        keys (cond (= smode :forward) (reverse klst)
                   (= smode :reverse) klst
                   (= smode :random) (shuffle klst)
                   (= smode :off) '()
                   :else (reverse klst))]
    (doseq [kn keys]
      (if (and (>= kn 0)(< kn 128))
        (let [delay (long (* @counter* strum))
              event {:command :note-on :channel c0
                     :note kn :data1 kn :data2 velocity}
              timer (Timer. true)
              task (proxy [TimerTask][]
                     (cancel [& _] true)
                     (run [& _]
                       (dispatch event nodes*)
                       (note-off c0 kn hold-time nodes*)
                       (.cancel timer))
                     (scheduledExecutionTime [& _] 0))]
          (swap! counter* inc)
          (.schedule timer task delay))))
    nil))

(defn- note-on [c0 keylist velocity strum strum-mode hold-time nodes*]
  "(note-on c0 keylist velocity strum hold-time nodes*)
   Transmits note-on events to nodes*
   ARGS:
     c0         - int, MIDI channel, zero-indexed. 0 <= c0 < 16.
     keylist    - list, list of MIDI key-numbers. Alternatively a single int
                  is promoted to a list. Any value in keylist outside of 
                  valid MIDI range (0 <= keynum < 128) is ignored.
     velocity   - int, MIDI velocity, 0 <= velocity < 128
     strum      - long, sets delay in milliseconds of consecutive note events 
     strum-mode - keyword
     hold-time  - float, note duration in seconds.
     nodes*  - list of Cadejo nodes
   RETURNS: nil"
  (if trace-keys
    (println 
     (format "Xolotl trace-keys c0:%s  klst: %s  vel: %s  strum: %s  hold: %s" 
                     c0 (doall keylist) velocity strum hold-time)))
  (let [klst (util/->list keylist)]
    (if (and (pos? strum)(> (count klst) 1))
      (strum-notes c0 klst velocity strum strum-mode hold-time nodes*)
      (doseq [kn klst]
        (if (and (>= kn 0)(< kn 128))
          (let [event {:command :note-on :channel c0
                       :note kn :data1 kn :data2 velocity}]
            (dispatch event nodes*)
            (note-off c0 kn hold-time nodes*))))))
  nil)

(defn- controller [c0 ctrl val nodes*]
  "(controller c0 ctrl val nodes*)
   Transmits MIDI controller event.
   ARGS:
      c0     - int, MIDI channel, zero-indexed.
      ctrl   - int, MIDI controller number, 0 <= ctrl < 128
      val    - int, MIDI controller value, 0 <= val < 128
      nodes* - list of Cadejo nodes
   RETURNS: nil"
  (let [event {:command :control-change :channel c0
               :data1 ctrl :data2 val}]
    (if trace-controllers 
      (println (format "Xolotl trace-controller %s" event)))
    (dispatch event nodes*)
    nil))

(defn- program-change [c0 prognum nodes*]
  (if (>= prognum 0)
    (let [event {:command :program-change :channel c0 :data1 prognum}]
      (dispatch event nodes*)))
  nil)


(defprotocol Transmitter

  (enable! [this flag])  ;;DEPRECIATE

  (current-keylist [this])
  
  (dump-state [this])
  
  (kill-all-notes [this]
    "(.kill-all-notes Transmitter)
     Transmit note-off messages 0 through 127 on all channels")

  (strum! [this delay]
    "(.strum! Transmitter delay)
     Sets strum delay time in milliseconds")

  (strum-mode! [this mode]
    "(.strum-mode! Transmitter mode)
     Determines how chords are strummed.
     ARGS:
       mode - keyword. Possible values are
              :off       - Turn strum off
              :forward   - strum chord forward
              :reverse   - strum chords backwards
              :alternate - alternate between forward and backwards
              :random   - use random permutation of chord.
     RETURNS: mode")  
  
  (channel! [this c0]
    "(.channel! Transmitter c0)
     Sets MIDI output channel, zero-indexed 0 <= c0 < 16.")
  
  (generate-key-events [this keylist velocity hold-time]
    "(.generate-key-events Transmitter keylist velocity hold-time)
     ARGS:
        keylist   - list of MIDI key numbers. Alternatively may be a single int.
                    If list length is greater then 1 a chord is produced.
                    Values outside of valid MIDI range [0,127] are ignored.
        velocity  - int, MIDI velocity.
        hold-time - float, note duration in seconds.")

  (generate-controller-event [this ctrl val]
    "(.generate-controller-event Transmitter ctrl val)
     ARGS:
       ctrl - int, MIDI controller number 0 <= ctrl < 128.
       val  - int, Controller value, 0 <= val < 128.")

  (generate-program-change [this prognum])
  )




(defn transmitter [nodes*]
  "(transmitter node)
   Creates Transmitter object
   ARGS:
     node - Cadejo Node to receive MIDI events.
   RETURNS: Transmitter"
  (let [enabled* (atom true)  ;; DEPRECIATE
        previous-program* (atom -1)
        channel* (atom 0)
        current-keylist* (atom [])
        strum* (atom 0)
        strum-mode* (atom :forward)]
    (reify Transmitter

      (dump-state [this]
        (let [sb (StringBuilder.)
              pad "  "
              pad2 (str pad pad)]
          (.append sb (format "%sTransmitter\n" pad))
          (.append sb
                   (format "%senabled (depreciated) -> %s\n" pad2 @enabled*))
          (.append sb (format "%soutput-channel -> %s\n" pad2 @channel*))
          (.append sb (format "%sstrum-mode     -> %s\n" pad2 @strum-mode*))
          (.append sb (format "%sstrum-delay    -> %s\n" pad2 @strum*))
          (.toString sb)))
      
      (enable! [this flag]
        (util/warning "xolotl Transmitter enable flag is depreciated")
        (reset! enabled* (util/->bool true)))

      (current-keylist [this]
        @current-keylist*)
      
      (kill-all-notes [this]
        (dotimes [c0 15]
          (dotimes [kn 128]
            (dispatch {:command :note-off :channel c0
                       :note kn :data1 kn :data2 0}
                      nodes*)
            (Thread/sleep 1))))
      
      (channel! [this c0]
        (reset! channel* c0))
      
      (strum! [this s]
        (reset! strum* s))

      (strum-mode! [this mode]
        (reset! strum-mode* mode))
      
      (generate-key-events [this keylist velocity hold-time]
        (if @enabled*
          (do
            (reset! current-keylist* keylist)
            (note-on @channel* keylist velocity @strum* @strum-mode* hold-time nodes*))))

      (generate-program-change [this prognum]
        (if (not (= prognum @previous-program*))
          (do
            (program-change @channel* prognum nodes*)
            (reset! previous-program* prognum))))
      
      (generate-controller-event [this ctrl val]
        (if @enabled*
          (controller @channel* ctrl val nodes*))))))


