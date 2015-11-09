(println "    --> xolotl.ui.channel-editor")
(ns xolotl.ui.channel-editor
  (:require [xolotl.ui.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

(def ^:private msg00 "WARNING: Input = Output")
(def ^:private msg01 (let [sb (StringBuilder.)]
                       (dotimes [i (count msg00)]
                         (.append sb " "))
                       (.toString sb)))

;; Construct channel-editor sub-panel
;; Includes * MIDI input and output channel spinners
;;          * seq enable checkbox
;;          * key reset, gate and track check-boxes
;;          * transpose spinner
;;          * MIDI program change spinner
;; ARGS:
;;   parent-editor - an instance of NodeEditor for Xolotl
;;   seq-id - keyword, either :A or :B
;;
;; RETURNS: map with keys :pan-main -> JPanel
;;                        :sync-fn -> GUI update function
;;
(defn channel-editor [parent-editor seq-id]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        transmitter (.get-transmitter xseq)
        bank (.program-bank xobj)
        spin-input (factory/spinner 1 16 1)
        pan-input (factory/border-panel :center spin-input :east (factory/label "IN "))
        spin-output (factory/spinner 1 16 1)
        pan-output (factory/border-panel :center spin-output :east (factory/label "OUT"))
        lab-warning (factory/label  msg00 :font :small-mono)

        pan-channels (factory/vertical-panel
                      (factory/horizontal-panel pan-input
                                                (factory/horizontal-strut 8 ))
                      pan-output
                      lab-warning)
        cb-enable (ss/toggle :text "Enable")
        cb-monitor (factory/checkbox "MON")
        pan-enable (factory/border-panel :center cb-enable ) ;:east cb-monitor)
        cb-reset (factory/checkbox "Key Reset")
        cb-gate (factory/checkbox "Key Gate")
        cb-track (factory/checkbox "Key Track")
        spin-transpose (factory/spinner -96 96 1)
        spin-program (factory/spinner -1 127 1)
        pan-key-mode (factory/grid-panel 4 1 pan-enable cb-reset cb-gate cb-track)

        pan-transpose (factory/border-panel :center spin-transpose
                                            :south (factory/label "Transpose"))
        pan-program (factory/border-panel :center spin-program
                                          :south   (factory/label "Program"))
        pan-south (factory/grid-panel 1 2 pan-transpose pan-program)
        pan-main (factory/border-panel :north pan-channels
                                       :center pan-key-mode
                                       :south pan-south)
        enable-action (proxy [ActionListener][]
                        (actionPerformed [_]
                          (let [flag (.isSelected cb-enable)
                                prog (.current-program bank)]
                            (.enable! xseq flag)
                            (.enable! prog seq-id flag))))
        reset-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (let [flag (.isSelected cb-reset)
                               prog (.current-program bank)]
                           (.enable-reset-on-first-key! xseq flag)
                           (.key-reset! prog seq-id flag))))
        gate-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (let [flag (.isSelected cb-gate)
                              prog (.current-program bank)]
                          (.enable-key-gate! xseq flag) 
                          (.key-gate! prog seq-id flag))))
        track-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (let [flag (.isSelected cb-track)
                               prog (.current-program bank)]
                           (.enable-key-track! xseq flag)
                           (.key-track! prog seq-id flag))))
        input-chan-listener (proxy [ChangeListener][]
                              (stateChanged [_]
                                (.input-channel! xseq (dec (int (.getValue spin-input))))))
        output-chan-listener (proxy [ChangeListener][]
                              (stateChanged [_]
                                (.output-channel! xseq (dec (int (.getValue spin-output))))))
        warning-listener (proxy [ChangeListener][]
                           (stateChanged [_]
                             (let [a (.getValue spin-output)
                                   b (.getValue spin-input)]
                               (if (= a b)
                                 (.setText lab-warning msg00)
                                 (.setText lab-warning msg01)))))
        transpose-listener (proxy [ChangeListener][]
                             (stateChanged [_]
                               (let [n (int (.getValue spin-transpose))
                                     prog (.current-program bank)]
                                 (.transpose! xseq n)
                                 (.transpose! prog seq-id n))))
        midi-program-listener (proxy [ChangeListener][]
                                (stateChanged [_]
                                  (let [n (.getValue spin-program)
                                        prog (.current-program bank)]
                                    (.generate-program-change transmitter n)
                                    (.midi-program-number! xseq n)
                                    (.midi-program! prog seq-id n)))) 
        sync-fn (fn [prog]
                  (let [true? (fn [obj]
                                (if (or (not obj)(= obj 0)) false true))
                        trans (.transpose prog seq-id)
                        midi-prognum (.midi-program prog seq-id)]
                    (.setValue spin-transpose (int trans))
                    (.setValue spin-program (int midi-prognum))
                    (.setSelected cb-enable (.enabled? prog seq-id))
                    (.setSelected cb-reset (true? (.key-reset prog seq-id)))
                    (.setSelected cb-track (true? (.key-track prog seq-id)))
                    (.setSelected cb-gate (true? (.key-gate prog seq-id)))))]
    (.setBorder pan-channels (factory/border "MIDI Channels"))
    (.setValue spin-input (int 16))
    (.addActionListener cb-reset reset-action)
    (.addActionListener cb-enable enable-action)
    (.addActionListener cb-gate gate-action)
    (.addActionListener cb-track track-action)
    (.addChangeListener spin-input input-chan-listener)
    (.addChangeListener spin-output output-chan-listener)
    (.addChangeListener spin-input warning-listener)
    (.addChangeListener spin-output warning-listener)
    (.addChangeListener spin-transpose transpose-listener)
    (.addChangeListener spin-program midi-program-listener)
    (.addActionListener cb-monitor (proxy [ActionListener][]
                                     (actionPerformed [_]
                                       (let [mon (.get-monitor xseq)]
                                         ((:fn-enable mon)(.isSelected cb-monitor))))))
    {:pan-main pan-main
     :sync-fn sync-fn})) 
