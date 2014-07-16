(ns cadejo.midi.mono-mode
  "Provide basic monophonic key response.
   See cadejo.midi.keymode"
  (:require [cadejo.midi.keymode])
  (:require [cadejo.util.stack :as stack])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot]))

(def enable-trace false)

(deftype Monomode [id parent* keystack* trace*]
  cadejo.midi.keymode/Keymode

  (set-parent! [this performance]
    (swap! parent* (fn [n] performance)))

  (reset [this]
    (doseq [v (.synths @parent*)]
      (ot/ctl v :gate 0))
    (doseq [v (.voices @parent*)]
      (ot/ctl v :gate 0))
    (stack/clear-stack keystack*))
  
  (key-down [this event]
    (let [keynum (:data1 event)]
      (if (.key-in-range? @parent* keynum)
        (let [xkeynum (min (max (+ keynum (.transpose @parent*)) 0) 127)
              dbscale (.db-scale @parent*)
              freq (.keynum-frequency @parent* xkeynum)
              vel (.map-velocity @parent* (:data2 event))
              args (cons (concat (.synths @parent*)(.voices @parent*))
                         (list :note keynum :freq freq :velocity vel :gate 1 :dbscale dbscale))]
          (apply ot/ctl args)
          (stack/stack-push keystack* keynum)
          (if @trace*
            (println (format "Key Down chan %02d key %03d vel %5.3f  freq %f"
                           (:channel event) keynum vel freq)))))))

  (key-up [this event]
    (let [keynum (:data1 event)]
      (if (.key-in-range? @parent* keynum)
        (let [synths (concat (.synths @parent*)(.voices @parent*))]
           (stack/stack-pop keystack* keynum)
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
      (println (format "%smono keymode %s" pad id))))

  (dump [this]
    (.dump this 0))) 

(defn mono-keymode [id]
  (Monomode. id 
             (atom nil)
             (stack/create-stack)
             (atom enable-trace)))
