(println "--> xolotl.xseq")
(ns xolotl.xseq
  (:require [xolotl.util :as util])
  (:require [xolotl.clock])
  (:require [xolotl.timebase])
  (:require [xolotl.controllers])
  (:require [xolotl.eventgen])
  (:require [xolotl.pitch]))

(defprotocol XolotlSeq

  (-set-parent-xobj! [this parent])

  ;; Clock parameters
  ;;

  (get-clock [this])  ;; return clock module

  (clock-select! [this mode])
  
  (input-channel! [this c0])

  (output-channel! [this c0])
  
  (enable-reset-on-first-key! [this flag])

  (enable-key-track! [this flag])

  (enable-key-gate! [this flag])
  
  (transpose! [this x])

  (rhythm-pattern! [this rpat])

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

  (strum! [this delay])

  (strum-mode! [this mode])

  ;; General 

  (dump-state [this])
  
  (load-program [this pmap])

  (enable! [this flag])
  
  (midi-reset [this]))


(def ^:private instance-counter* (atom 0))

(defn- generate-id []
  (let [id (keyword (format "XolotlSeq-%d" @instance-counter*))]
    (swap! instance-counter* inc)
    id))

(defn xolotl-seq [children*]
  (let [parent-obj* (atom nil)  ; should be instance of XolotlObject
        transmitter (xolotl.eventgen/transmitter children*)
        ctrl-block (xolotl.controllers/controller-block transmitter)
        pitch-block (xolotl.pitch/pitch-block transmitter)
        reset-fn (fn []
                   (.midi-reset ctrl-block)
                   (.midi-reset pitch-block))
        ctrl-function (.callback ctrl-block)
        pitch-function (.callback pitch-block)
        clock (xolotl.clock/clock reset-fn ctrl-function pitch-function (fn [_]))
      
        xobj (reify XolotlSeq

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

               (get-clock [this] clock)
               
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

               (rhythm-pattern! [this rpat]
                 (.rhythm-pattern! clock rpat))

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

               (strum! [this delay]
                 (.strum! transmitter delay))

               (strum-mode! [this mode]
                 (.strum-mode! transmitter mode))
               
               (midi-reset [this]
                 (.midi-reset clock))

               ;; (use-program! [this slot]
               ;;   (let [pmap (.recall-program bank slot)]
               ;;     (if pmap
               ;;       (let [params (:xseq pmap)] 
               ;;         (.clock-select! this (get params :clock :internal))
               ;;         (.input-channel! this (get params :input-channel 0))
               ;;         (.output-channel! this (get params :output-channel 0))
               ;;         (.enable-reset-on-first-key! this (get params :reset-on-key))
               ;;         (.enable-key-track! this (get params :enable-key-track))
               ;;         (.enable-key-gate! this (get params :enable-key-gate))
               ;;         (.transpose! this (get params :transpose 0))
               ;;         (.rhythm-pattern! this (get params :rhythm-pattern [24]))
               ;;         (.hold-pattern! this (get params :hold-pattern [1.0]))
               ;;         (.controller-number! this 0 (get params :controller-1 -1))
               ;;         (.controller-number! this 1 (get params :controller-2 -1))
               ;;         (.controller-pattern! this 0 (get params :controller-1-pattern [0]))
               ;;         (.controller-pattern! this 1 (get params :controller-2-pattern [0]))
               ;;         (.velocity-mode! this (get params :velocity-mode :seq))
               ;;         (.velocity-pattern! this (get params :velocity-pattern [127]))
               ;;         (.pitch-mode! this (get params :pitch-mode :seq))
               ;;         (.pitch-pattern! this (get params :pitch-pattern [-1000]))
               ;;         (.taps! this
               ;;                 (get params :shift-register-taps 0x80)
               ;;                 (get params :shift-register-inject 0))
               ;;         (.seed! this (get params :shift-register-seed 1))
               ;;         (.strum-mode! this (get params :strum-mode :forward))
               ;;         (.strum! this (get params :strum-delay 0))
               ;;         (.midi-reset this)
               ;;         (reset! current-program-slot* slot)
               ;;         (reset! current-program-map* pmap)
               ;;         (xolotl.prog.pp/pp pmap)
               ;;         pmap)
               ;;       false)))
                       
               (load-program [this pmap]
                 (println "ISSUE: xseq load-program method not implemented")
                 )

               (enable! [this flag]
                 (.enable! transmitter flag))
               
               )]
                          
    (.clock-select! clock :internal)
    xobj))


