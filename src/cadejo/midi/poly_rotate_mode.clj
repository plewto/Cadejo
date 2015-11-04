

(ns cadejo.midi.poly-rotate-mode
  "Implements polyphonic mode using voice rotation

Poly Rotate mode allocates synth voices in rotation.
The effect is subtly different from 'normal' poly mode which uses a 
voice stealing scheme.

The differences are most noticeable when 'progressive program mode' 
is active. In progressive program mode each voice is set to a separate
program. In the normal poly mode the first key press after all keys 
are up always uses voice 1, the 2nd keypress voice 2 etc. In rotation 
mode the first keypress may use any voice, whichever the next one
in rotation is up." 

  (:require [cadejo.midi.keymode])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(def enable-trace false)

;; Return the next voice-number, if possible skipping active voices.
;;
(defn ^:private next-voice [voice-count counter voice-assignments*]
  (if (not (nth @voice-assignments* counter))
    counter
    (let [i* (atom voice-count)
          flag* (atom false)
          pos* (atom counter)]
      (while (and (pos? @i*)(not @flag*))
        (let [v (nth @voice-assignments* @pos*)]
          (if (not v)
            (reset! flag* true)
            (do
              (swap! i* dec)
              (swap! pos* (fn [q](rem (inc q) voice-count)))))))
      @pos*)))

(deftype RotateMode [id voice-count parent* counter* keymap* voice-assignments* trace*]
  cadejo.midi.keymode/Keymode

  (set-parent! [this performance]
    (reset! parent* performance))

  (reset [this]
    (doseq [v (concat (.synths @parent*)(.voices @parent*))]
      (ot/ctl v :gate 0))
    (dotimes [i 127]
      (swap! keymap* (fn [q](assoc q i nil))))
    (dotimes [i voice-count]
      (swap! voice-assignments* (fn [q](assoc q i nil))))
    (reset! counter* 0))

  (key-down [this event]
    (let [voices (.voices @parent*)
          keynum (:note event)
          xkeynum (min (max (+ keynum (.transpose @parent*)) 0) 127)
          freq (.keynum-frequency @parent* xkeynum)
          vel (.map-velocity @parent* (:data2 event))
          dbscale (.db-scale @parent*)
          previous-voice-number (get @keymap* keynum)]
      (if previous-voice-number
        (let [previous-voice (nth voices previous-voice-number)]
          (ot/ctl previous-voice :gate 0)
          (swap! voice-assignments* (fn [q](assoc q previous-voice-number nil)))))
      (reset! counter* (next-voice voice-count @counter* voice-assignments*))
      (let [current-voice (nth voices @counter*)]

        (ot/ctl (cons current-voice (.synths @parent*))
                :gate 1 :note keynum :freq freq :velocity vel :dbscale dbscale)

        (swap! keymap* (fn [q](assoc q keynum @counter*)))
        (swap! voice-assignments* (fn [q](assoc q @counter* keynum))))
      (if @trace*
        (println (format "Key Down chan %02d key %03d vel %5.3f   freq %5.3f  voice-number %2d"
                         (:channel event) keynum vel freq @counter*)))
      (swap! counter* (fn [q](rem (inc q) voice-count)))))

  (key-up [this event]
    (let [voices (.voices @parent*)
          keynum (:note event)
          previous-voice-number (nth @keymap* keynum)]
      (if previous-voice-number
        (let [previous (get voices previous-voice-number)]
          (ot/ctl previous :gate 0)
          (swap! keymap* (fn [q](assoc q keynum nil)))
          (swap! voice-assignments* (fn [q](assoc q previous-voice-number nil)))))
      (if @trace*
        (println (format "Key Up  chan %02d key %03d  voice-number %2d"
                         (:channel event) keynum previous-voice-number)))))

  (dump [this depth]
    (let [pad (cadejo.util.string/tab depth)]
      (println (format "%spoly-rotate keymode %s  %d voices" pad id voice-count))))

  (dump [this]
    (.dump this 0)))


(defn poly-rotate-mode [id voice-count]
  (let [keymap* (atom (into [] (repeat 127 nil)))
        voicemap* (atom (into [] (repeat voice-count nil)))]
    (RotateMode. id 
                 voice-count 
                 (atom nil)             ; parent performance
                 (atom 0)               ; voice counter
                 keymap*
                 voicemap*
                 (atom enable-trace))))
