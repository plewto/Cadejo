(ns xolotl.ui.channel-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

(def ^:private msg00 "WARNING: Input = Output")
(def ^:private msg01 (let [sb (StringBuilder.)]
                       (dotimes [i (count msg00)]
                         (.append sb " "))
                       (.toString sb)))

;; Construct channel-editor sub-panel
;; Includes * MIDI input and output channel spinners
;;          * key reset, gate and track check-boxes
;;          * transpose spinner
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
        cb-reset (factory/checkbox "Key Reset")
        cb-gate (factory/checkbox "Key Gate")
        cb-track (factory/checkbox "Key Track")
        spin-transpose (factory/spinner -96 96 1)
        
        pan-key-mode (factory/grid-panel 3 1 cb-reset cb-gate cb-track)

        pan-transpose (factory/border-panel :center spin-transpose
                                            :east (factory/label "Transpose"))
        pan-main (factory/border-panel :north pan-channels
                                       :center pan-key-mode
                                       :south pan-transpose)
        reset-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (.enable-reset-on-first-key! xseq (.isSelected cb-reset))))
        gate-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (.enable-key-gate! xseq (.isSelected cb-gate))))
        track-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (.enable-key-track! xseq (.isSelected cb-track))))
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
                               (.transpose! xseq (int (.getValue spin-transpose)))))
        sync-fn (fn [prog]
                  (let [true? (fn [obj]
                                (if (or (not obj)(= obj 0)) false true))
                        trans (.transpose prog seq-id)]
                    (.setValue spin-transpose (int trans))
                    (.setSelected cb-reset (true? (.key-reset prog seq-id)))
                    (.setSelected cb-track (true? (.key-track prog seq-id)))
                    (.setSelected cb-gate (true? (.key-gate prog seq-id)))))]
    (.setBorder pan-channels (factory/border "MIDI Channels"))
    (.addActionListener cb-reset reset-action)
    (.addActionListener cb-gate gate-action)
    (.addActionListener cb-track track-action)
    (.addChangeListener spin-input input-chan-listener)
    (.addChangeListener spin-output output-chan-listener)
    (.addChangeListener spin-input warning-listener)
    (.addChangeListener spin-output warning-listener)
    (.addChangeListener spin-transpose transpose-listener)
    {:pan-main pan-main
     :sync-fn sync-fn}))
