(println "--> xolotl.xobj")
(ns xolotl.xobj
  (:require [cadejo.midi.node])
  (:require [xolotl.program-bank])
  (:require [xolotl.xseq])
  (:require [xolotl.timebase])
  (:require [xolotl.util :as util])
  )


;;; Start TEST Start TEST Start TEST Start TEST Start TEST
(defprotocol DummyEditor
  (sync-ui! [this])
  (warning! [this msg])
  (update-path-text [this msg]))

(def dummy-editor
  (reify DummyEditor
    (sync-ui! [this] (println "DUMMY Editor sync-ui! executed"))
    (warning! [this msg](println (format "DUMMY Editor WARNING: %s" msg)))
    (update-path-text [this msg])
    ))
  



(def msg00 "Child nodes may not be added or removed from XolotlObject")
(def msg01 "No such program slot %s")

(defprotocol XolotlObject

  (dump [this])
  
  (xseq-count [this])
  
  (get-xseq [this n])
  
  (clock-select! [this src])

  (tempo! [this bpm])

  (stop [this])

  (start [this])

  (midi-reset [this])

  (program-bank [this])

  (store-program! [this slot data])

  (use-program [this slot])
  )


(defn xolotl [children*]
  (let [xseqs [(xolotl.xseq/xolotl-seq children*)
               (xolotl.xseq/xolotl-seq children*)]
        parent* (atom nil)
        bank (xolotl.program-bank/program-bank)
        editor* (atom dummy-editor)  ;; ISSUE Remove dummy assignment after testing
        properties* (atom {})
        xobj (reify
               XolotlObject

               (dump [this]
                 (let [sb (StringBuilder.)]
                   (.append sb "XolotlObject\n")
                   (doseq [xs xseqs]
                     (.append sb (.dump-state xs)))
                   (println (.toString sb))))
               
               (xseq-count [this] (count xseqs))

               (get-xseq [this n]
                 (nth xseqs n))

               (clock-select! [this src]
                 (doseq [xs xseqs]
                   (.clock-select! xs src)))

               (tempo! [this bpm]
                 (xolotl.timebase/set-tempo bpm))

               (stop [this]
                 (xolotl.timebase/stop))

               (start [this]
                 (xolotl.timebase/start))

               (midi-reset [this]
                 (doseq [xs xseqs]
                   (.midi-reset xs)))
        
               (program-bank [this] bank)

               (store-program! [this slot data]
                 (.store-program! bank slot data))

               (use-program [this slot]
                 (let [xprog (.use-program bank slot)]
                   (if xprog
                     (do
                       (xolotl.timebase/set-tempo (.tempo xprog))
                       (doseq [pair [[(first xseqs)(.seq-params xprog :a)]
                                     [(second xseqs)(.seq-params xprog :b)]]]
                         (let [xs (first pair)
                               pmap (second pair)]
                           (.clock-select! xs (get xprog :clock-source :internal))
                           (.input-channel! xs (get pmap :input-channel 0))
                           (.output-channel! xs (get pmap :output-channel 0))
                           (.enable-reset-on-first-key! xs (get pmap :key-reset false))
                           (.enable-key-track! xs (get pmap :key-track true))
                           (.enable-key-gate! xs (get pmap :key-gate false))
                           (.transpose! xs (get pmap :transpose 0))
                           (.rhythm-pattern! xs (get pmap :rhythm-pattern [24]))
                           (.hold-pattern! xs (get pmap :hold-pattern [1.0]))
                           (.controller-number! xs 0 (get pmap :controller-1-number -1))
                           (.controller-number! xs 1 (get pmap :controller-2-number -1))
                           (.controller-pattern! xs 0 (get pmap :controller-1-pattern [0]))
                           (.controller-pattern! xs 1 (get pmap :controller-2-pattern [0]))
                           (.velocity-mode! xs (get pmap :velocity-mode :seq))
                           (.velocity-pattern! xs (get pmap :velocity-pattern [100]))
                           (.pitch-mode! xs (get pmap :pitch-mode :seq))
                           (.pitch-pattern! xs (get pmap :pitch-pattern [0 12]))
                           (.taps! xs (get pmap :sr-taps 2r10000000)(if (:sr-inject pmap) 1 0))
                           (.seed! xs (get pmap :sr-seed 2r00000001))
                           (.strum-mode! xs (get pmap :strum-mode :forward))
                           (.strum! xs (get pmap :strum-delay 0))
                           ))
                       (.sync-ui! @editor*))
                     (.warning! @editor* (format msg01 slot)))))
                   
               cadejo.midi.node/Node

               (node-type [this] :xolotl)

               (is-root? [this]
                 (not @parent*))
                 
               (find-root [this]
                 (if (.is-root? this)
                   this
                   (.find-root @parent*)))

               (parent [this] @parent*)

               (children [this] @children*)

               (is-child? [this obj]
                 (util/member? obj (.children this)))

               (add-child! [this _]
                 (throw (UnsupportedOperationException. msg00)))

               (remove-child! [this _]
                 (throw (UnsupportedOperationException. msg00)))

               (_set-parent! [this p]
                 (reset! parent* p))

               (_orphan! [this]
                 (._set-parent! this nil))

               (put-property! [this key val]
                 (swap! properties* (fn [q](assoc q key val))))

               (remove-property! [this key]
                 (swap! properties* (fn [q](dissoc q key))))

               (get-property [this key default]
                 (let [value (or (get @properties* key)
                                 (and (.parent this)
                                      (.get-property (.parent this) key default))
                                 default)]
                   (if (= value :fail)
                     (util/warning (format "Xolotl does not have property %s" key))
                     value)))
               
               (get-property [this key]
                 (.get-property this key :fail))
               
               (local-property [this key]
                 (get @properties* key))
               
               (properties [this local-only]
                 (set (concat (keys @properties*)
                              (if (and (.parent this) (not local-only))
                                (.properties (.parent this))
                                nil))))
               
               (properties [this]
                 (.properties this false))
               
               ;; (event-dispatcher [this]
               ;;   (fn [evn]
               ;;     (doseq [xs xseqs]
               ;;       ((.event-dispatcher xs) evn))))

               (event-dispatcher [this]
                 (fn [evn]
                   (doseq [xs xseqs]
                     ((.event-dispatcher (.get-clock xs)) evn))))
                 
               (get-editor [this] @editor*)

               (set-editor! [this ed]
                 (reset! editor* ed)) )]
    ;; ISSUE set editor

    ;; set program-change callback first xseq only. 
    ;; -- do not want to execute it more then one time --
    (.program-function! (.get-clock (first xseqs))
                        (fn [slot]
                          (.use-program xobj slot)))
    xobj))
               



;;;; TEST TEST TEST TEST TES
;;;; TEST TEST TEST TEST TEST
;;;; TEST TEST TEST TEST TEST

(def x (xolotl nil))

