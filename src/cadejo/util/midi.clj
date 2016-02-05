(ns cadejo.util.midi
  (:use [clojure.string :only [trim]])
  (:import javax.sound.midi.MidiSystem
           javax.sound.midi.MidiDevice
           javax.sound.midi.MidiUnavailableException))

(defn provides-transmitter [dev]
  (try
    (.getTransmitter dev)
    true
    (catch MidiUnavailableException ex
      false)))

(defn provides-receiver [dev]
  (try
    (.getReceiver dev)
    true
    (catch MidiUnavailableException ex
      false)))

;; Returns list of all MIDI transmitters known to the JVM
;; The format is a list of tuples [[f obj][f obj] .... ]
;; Where f is a flag indicating if the device is available and obj
;; is an instance of javax.sound.midi.MidiDevice
;;
(defn transmitters []
  (let [acc* (atom [])
        info (MidiSystem/getMidiDeviceInfo)]
    (doseq [i info]
      (let [mdev (MidiSystem/getMidiDevice i)]
        (if (provides-transmitter mdev)
          (try
            (.open mdev)
            (.close mdev)
            (swap! acc* (fn [n](conj n [true mdev])))
            (catch MidiUnavailableException ex
              (swap! acc* (fn [n](conj n [false mdev]))))))))
    @acc*))



;; Returns list of all MIDI receivers known to the JVM
;; The format is a list of tuples [[f obj][f obj] .... ]
;; Where f is a flag indicating if the device is available and obj
;; is an instance of javax.sound.midi.MidiDevice
;;    
(defn receivers []
  (let [acc* (atom [])
        info (MidiSystem/getMidiDeviceInfo)]
    (doseq [i info]
      (let [mdev (MidiSystem/getMidiDevice i)]
        (if (provides-receiver mdev)
          (try
            (.open mdev)
            (.close mdev)
            (swap! acc* (fn [n](conj n [true mdev])))
            (catch MidiUnavailableException ex
              (swap! acc* (fn [n](conj n [false mdev]))))))))
    @acc*))    
