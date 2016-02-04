(ns cadejo.util.midi
  ;(:require [cadejo.util.trace :as trace])
  (:use [clojure.string :only [trim]])
  (:import javax.sound.midi.MidiSystem
           javax.sound.midi.MidiDevice
           javax.sound.midi.MidiUnavailableException))

;; Split descriptive part of MIDI device name from hardware device part
;; Return tupel of strings [name hw-dev]
;; 
;; (defn parse-device-name [mdn]
;;   (trace/trace-enter (format "parse-device-name mdn = '%s'" mdn))
;;   (let [i (.indexOf mdn "[")]
;;     (if (pos? i)
;;       (let [n (trim (subs mdn 0 (dec i)))
;;             hw (trim (subs mdn i))]
;;         (trace/trace-mark (format "n = %s   hw= '%s'" n hw))
;;         (trace/trace-exit)
;;         [n hw])
;;       (do
;;         (trace/trace-mark "nil result")
;;         (trace/trace-exit)
;;         ["" mdn]))))

;; Thanks Andreas Stenius
;; DEPRECIATED
;; (defn parse-device-name [mdn]
;;   ["" mdn])

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

;; ;; Return MIDI receiver with given name
;; ;; name MUST match exactly.
;; (defn midi-in [device-name]
;;   (let [rlst (receivers)
;;         rs* (atom nil)]
;;     (doseq [r rlst]
;;       (let [mdi (.getDeviceInfo (second r))
;;             name (.getName mdi)]
;;         (if (= name device-name)
;;           (reset! rs* (second r)))))
;;     @rs*))

;; Informational only
;; Prints list of MIDI devices to terminal
;; (defn list-midi-info []
;;   (let [sb (StringBuilder.)]
;;     (.append sb "MIDI Transmitters:\n")
;;     (doseq [t (transmitters)]
;;       (let [mdi (.getDeviceInfo (second t))]
;;         (.append sb (format "  Available %-5s %s\n" (first t)(parse-device-name (.getName mdi))))))
;;     (.append sb "MIDI Receivers:\n")
;;     (doseq [r (receivers)]
;;       (let [mdi (.getDeviceInfo (second r))]
;;         (.append sb (format "  Available %-5s %s\n" (first r)(parse-device-name (.getName mdi))))))
;;     (.toString sb)))

