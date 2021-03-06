(println "    --> xolotl.xseq")
(ns xolotl.xseq
  (:require [xolotl.util :as util])
  (:require [xolotl.clock])
  (:require [xolotl.timebase])
  (:require [xolotl.controllers])
  (:require [xolotl.eventgen])
  (:require [xolotl.pitch])
  (:require [xolotl.ui.monitor]))

(defprotocol XolotlSeq

  (-set-parent-xobj! [this parent])

  ;; Clock parameters
  ;;

  (step [this])

  (get-monitor [this])
  
  (get-clock [this])  ;; return clock module

  (get-shift-register [this])

  (get-pitch-cycle [this])
  
  (get-transmitter [this])
  
  (clock-select! [this mode])
  
  (input-channel! [this c0])

  (output-channel! [this c0])
  
  (enable-reset-on-first-key! [this flag])

  (enable-key-track! [this flag])

  (enable-key-gate! [this flag])
  
  (transpose! [this x])

  (repeat! [this x])

  (jump! [this x])
  
  (rhythm-pattern! [this rpat])

  (rhythm-pattern [this])
  
  (hold-pattern! [this hpat])

  ;; Controller parameters
  ;;
  (controller-number! [this n ctrl])

  (controller-pattern! [this n cpat])

  ;; Pitch parameters
  ;;
  (velocity-mode! [this vmode])
  
  (velocity-pattern! [this vpat])

  (pitch-mode! [this pmode])

  (pitch-pattern! [this ppat])

  (taps! [this tval inject])

  (seed! [this sval])

  (sr-mask! [this n])
  
  (strum! [this delay])

  (strum-mode! [this mode])

  (midi-program-number! [this pnum])
  
  ;; General 

  (dump-state [this])

  ;; Return period of shift-register
  ;; For lone registers this may taks some time
  (sr-period [this])

  (enable! [this flag])

  (kill-all-notes [this])
  
  (midi-reset [this]))


(def ^:private instance-counter* (atom 0))

;; (defn- generate-id []
;;   (let [id (keyword (format "XolotlSeq-%d" @instance-counter*))]
;;     (swap! instance-counter* inc)
;;     id))

(defn xolotl-seq [children* id]
  (let [parent-obj* (atom nil)  ; should be instance of XolotlObject
        transmitter (xolotl.eventgen/transmitter children*)
        ctrl-block (xolotl.controllers/controller-block transmitter)
        pitch-block (xolotl.pitch/pitch-block transmitter)
        midi-program-number* (atom -1)
        reset-fn (fn []
                   (.midi-reset ctrl-block)
                   (.midi-reset pitch-block))
        ctrl-function (.callback ctrl-block)
        pitch-function (.callback pitch-block)
        clock (xolotl.clock/clock id reset-fn ctrl-function pitch-function (fn [_]))
        monitor (xolotl.ui.monitor/monitor id clock pitch-block ctrl-block transmitter)
        xobj (reify XolotlSeq

               (get-monitor [this] monitor)
               
               (dump-state [this]
                 (let [sb (StringBuilder.)]
                   (.append sb "XolotlSeq\n")
                   (.append sb (.dump-state clock))
                   (.append sb (.dump-state ctrl-block))
                   (.append sb (.dump-state pitch-block))
                   (.append sb (.dump-state transmitter))
                   (.toString sb)))
                   
               (-set-parent-xobj! [this parent]
                 (reset! parent-obj* parent))

               (step [this]
                 (.step clock)
                 ((:fn-sample monitor)))
               
               (get-clock [this] clock)

               (get-shift-register [this]
                 (.get-shift-register pitch-block))

               (get-pitch-cycle [this]
                 (.get-pitch-cycle pitch-block))
               
               (get-transmitter [this] transmitter)
               
               (clock-select! [this mode]
                 (.clock-select! clock mode))

               (input-channel! [this c0]
                 (.input-channel! clock c0))

               (output-channel! [this c0]
                 (.channel! transmitter c0))
               
               (enable-reset-on-first-key! [this flag]
                 (.enable-reset-on-first-key! clock flag))

               (enable-key-track! [this flag]
                 (.enable-key-track! clock flag))

               (enable-key-gate! [this flag]
                 (.enable-key-gate! clock flag))
               
               (transpose! [this xpose]
                 (.transpose! clock xpose))

               (repeat! [this n]
                 (.repeat! clock n))

               (jump! [this n]
                 (.jump! clock n))
               
               (rhythm-pattern! [this rpat]
                 (.rhythm-pattern! clock rpat))

               (rhythm-pattern [this]
                 (.rhythm-pattern clock))
               
               (hold-pattern! [this hpat]
                 (.hold-pattern! clock hpat))
               
               (controller-number! [this n ctrl]
                 (.controller-number! ctrl-block n ctrl))

               (controller-pattern! [this n cpat]
                 (.controller-pattern! ctrl-block n cpat))

               (velocity-mode! [this vmode]
                 (.velocity-mode! pitch-block vmode))

               (velocity-pattern! [this vpat]
                 (.velocity-pattern! pitch-block vpat))

               (pitch-mode! [this pmode]
                 (.pitch-mode! pitch-block pmode))

               (pitch-pattern! [this ppat]
                 (.pitch-pattern! pitch-block ppat))

               (taps! [this tval inject]
                 (.taps! pitch-block tval inject))

               (seed! [this sval]
                 (.seed! pitch-block sval))

               (sr-mask! [this mval]
                 (.sr-mask! pitch-block mval))
               
               (strum! [this delay]
                 (.strum! transmitter delay))

               (strum-mode! [this mode]
                 (.strum-mode! transmitter mode))

               (midi-program-number! [this pnum]
                 (reset! midi-program-number* pnum))

               (kill-all-notes [this]
                 (.kill-all-notes transmitter))
               
               (midi-reset [this]
                 (.midi-reset clock)
                 (.generate-program-change transmitter @midi-program-number*)
                 ((:fn-reset monitor)))
                       
               (enable! [this flag]
                 (.enable! clock flag)) )]

    (.clock-select! clock :internal)
    xobj))


