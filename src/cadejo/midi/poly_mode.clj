(ns cadejo.midi.poly-mode
  "Implements keymode using voice stealing scheme."
  (:require [cadejo.midi.keymode])
  (:require [cadejo.util.string])
  (:require [cadejo.util.queue :as q])
  (:require [overtone.core :as ot]))

(def enable-trace false)

(def default-voice-count 8)

;; Operational notes:
;; Polymode maintains three objects to keep track of voice allocation.
;; First each voice is indicated by a pointer 0 <= vptr < voice-count
;; keymap*  maps MIDI key numbers to a voice pointer currently playing 
;;          that specific note (if any).  
;; free*    Is a queue holding pointers to available voices.
;; in-use*  is a queue holding pointers to currently occupied voices.
;;
;; Upon a key press keymap is consulted to see if any voice is currently
;; playing the note. If so the event is ignored.
;; If there is no voice playing the note pointer is fetched from free*
;; if free is empty (all voices playing) the most stale voice from in-use* 
;; is used. Some effort is made to prevent held notes from becoming 'stale' 
;; but there is no guarantee. 
;; Once a voice is selected it's pointer is moved from either the free or 
;; in-use queue and placed at the end of the in-use queue. Simultaneity 
;; keymap is updated to reflect the change.
;;


(deftype Polymode [id voice-count parent* keymap* free* in-use* trace*]
  cadejo.midi.keymode/Keymode

  (set-parent! [this performance]
    (swap! parent* (fn [n] performance)))

  (reset [this]
    (doseq [v (.synths @parent*)]
      (ot/ctl v :gate 0))
    (doseq [v (.voices @parent*)]
      (ot/ctl v :gate 0))
    (swap! keymap* (fn [n] {}))
    (q/clear-queue free*)
    (q/clear-queue in-use*)
    (dotimes [i voice-count]
      (q/enqueue free* i)))

  (key-down [this event]
    (let [keynum (:note event)]
      (if (.key-in-range? @parent* keynum)
        (if (not (get @keymap* keynum))
          (let [vptr (or (q/dequeue free*)
                         (q/dequeue in-use*))
                xkeynum (min (max (+ keynum (.transpose @parent*)) 0) 127)
                dbscale (.db-scale @parent*)
                freq (.keynum-frequency @parent* xkeynum)
                vel (.map-velocity @parent* (:data2 event))]
            (swap! keymap* (fn [n](assoc n keynum vptr)))
            (q/enqueue in-use* vptr)
            (ot/ctl (cons (nth (.voices @parent*) vptr)
                          (.synths @parent*))
                    :note keynum :freq freq :velocity vel :gate 1 :dbscale dbscale)
            (if @trace*
              (println (format "Key Down chan %02d key %03d vel %5.3f    freq %5.3f" 
                               (:channel event) keynum vel freq ))))))))

  (key-up [this event]
    (let [keynum (:note event)
          vptr (get @keymap* keynum)]
      (if (and (.key-in-range? @parent* keynum) vptr)
        (do 
          (swap! keymap* (fn [n](dissoc n keynum)))
          (q/dequeue in-use* vptr)
          (q/enqueue free* vptr)
          (ot/ctl (nth (.voices @parent*) vptr) :gate 0)
          (ot/ctl (.synths @parent*)
                  :gate (if (= (q/queue-depth free*) voice-count)
                          0 1))
          (if @trace*
            (println (format "Key Up   chan %02d key %03d"
                             (:channel event) keynum)))))))
                               
                               
  (dump [this depth]
    (let [pad (cadejo.util.string/tab depth)]
      (println (format "%spoly keymode %s   %d voices" pad id voice-count))))

  (dump [this]
    (.dump this 0)))

(defn poly-keymode [id voice-count]
  "Creates new instance of Polymode.
   id - unique keyword identification
   voice-count - number of voices."
  (let [keymap* (atom {})
        free* (q/create-queue)
        in-use* (q/create-queue)]
    (dotimes [i voice-count]
      (q/enqueue free* i))
    (Polymode. id 
               voice-count
               (atom nil)      ; parent performance
               keymap*
               free*
               in-use*
               (atom enable-trace))))
