;; Mono Exclusive Keymode
;; In mono-exclusive mode notes are articulated -only- on the first keypress.
;; No new notes are generated until all subsequent keys have been released. 
;; In isolation mono-exclusive mode is not particularly useful. It is intended
;; to be use while layering two or more instruments to simulate the initial 
;; "key-click" effect found in older organs. It is not limited to this "click"
;; effect since any synth/program may be used. 
;;

(ns cadejo.midi.mono-exclusive
   (:require [cadejo.midi.keymode])
   (:require [cadejo.util.stack :as stack])
   (:require [cadejo.util.string])
   (:require [overtone.core :as ot]))

(def enable-trace false)

(deftype MonoExclusive [id parent* keystack* keycount* trace*]
  cadejo.midi.keymode/Keymode

  (set-parent! [this performance]
    (swap! parent* (fn [n] performance)))

  (reset [this]
    (reset! keycount* 0)
    (doseq [v (.synths @parent*)]
      (ot/ctl v :gate 0))
    (doseq [v (.voices @parent*)]
      (ot/ctl v :gate 0))
    (stack/clear-stack keystack*))
  
  (key-down [this event]
    (let [keynum (:note event)]
      (if (.key-in-range? @parent* keynum)
        (let [xkeynum (min (max (+ keynum (.transpose @parent*)) 0) 127)
              dbscale (.db-scale @parent*)
              freq (.keynum-frequency @parent* xkeynum)
              vel (.map-velocity @parent* (:data2 event))
              args (cons (concat (.synths @parent*)(.voices @parent*))
                         (list :note keynum :freq freq :velocity vel :gate 1 :dbscale dbscale))]
          (swap! keycount* inc)
          (if (= 1 @keycount*)
            (do 
              (apply ot/ctl args)
              (stack/stack-push keystack* keynum)
              (if @trace*
                (println (format "Key Down chan %02d key %03d vel %5.3f  freq %f"
                                 (:channel event) keynum vel freq)))))))))

  (key-up [this event]
    (let [keynum (:note event)]
      (if (.key-in-range? @parent* keynum)
        (let [synths (concat (.synths @parent*)(.voices @parent*))]
           (stack/stack-pop keystack* keynum)
           (swap! keycount* (fn [q](max 0 (dec q))))
           (if (stack/stack-empty? keystack*)
             (do 
               (ot/ctl synths :gate 0))
             (let [previous-key (stack/stack-peek keystack*)
                   xkeynum (min (max (+ previous-key (.transpose @parent*)) 0) 127)
                   previous-freq (.keynum-frequency @parent* xkeynum)]
               (ot/ctl synths :note previous-key :freq previous-freq)))
           (if @trace*
             (println (printf "Key Up   chan %02d key %03d" 
                              (:channel event) keynum)))))))

  (trace! [this flag]
    (swap! trace* (fn [n] flag)))

  (dump [this depth]
    (let [pad (cadejo.util.string/tab depth)]
      (println (format "%smono-exclusive keymode %s" pad id))))

  (dump [this]
    (.dump this 0))) 

(defn mono-exclusive-keymode [id]
  (let [keycount* (atom 0)]
    (MonoExclusive. id 
                    (atom nil)
                    (stack/create-stack)
                    keycount*
                    (atom enable-trace))))
