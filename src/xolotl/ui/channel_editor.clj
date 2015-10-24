(ns xolotl.ui.channel-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; includes
;;   spinner  - input channel   
;;   label    - warning if input and output channels are the same
;;   spinner  - output channel
;;   checkbox - key reset
;;   checkbox - key gate
;;   checkbox - key track
;;   spinner  - transpose


(def msg00 "WARNING: Input = Output")
(def msg01 (let [sb (StringBuilder.)]
             (dotimes [i (count msg00)]
               (.append sb " "))
             (.toString sb)))

(defn channel-editor [parent-editor seq-id]
  (let [spin-input (factory/spinner 1 16 1)
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
                         (println "ISSUE: channel-editor.reset-action NOT implemented")
                         ))
        gate-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (println "ISSUE: channel-editor.gate-action NOT implemented")
                         ))
        track-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (println "ISSUE: channel-editor.track-action NOT implemented")
                         ))
        input-chan-listener (proxy [ChangeListener][]
                              (stateChanged [_]
                                (println "ISSUE: channel-editor.input-chan-listener NOT implemented")
                                ))
        output-chan-listener (proxy [ChangeListener][]
                              (stateChanged [_]
                                (println "ISSUE: channel-editor.output-chan-listener NOT implemented")
                                ))
        warning-listener (proxy [ChangeListener][]
                           (stateChanged [_]
                             (let [a (.getValue spin-output)
                                   b (.getValue spin-input)]
                               (if (= a b)
                                 (.setText lab-warning msg00)
                                 (.setText lab-warning msg01)))))
        transpose-listener (proxy [ChangeListener][]
                             (stateChanged [_]
                               (println "ISSUE: channel-editor.transpose-listener NOT implemented")))
        
        sync-fn (fn [prog]
                  (let [true? (fn [obj]
                                (if (or (not obj)(= obj 0)) false true))
                        ;; in-chan (.input-channel prog seq-id)
                        ;; out-chan (.output-channel prog seq-id)
                        trans (.transpose prog seq-id)]
                    ;; (.setValue spin-input (int in-chan))
                    ;; (.setValue spin-output (int out-chan))
                    (.setValue spin-transpose (int trans))
                    (.setSelected cb-reset (true? (.key-reset prog seq-id)))
                    (.setSelected cb-track (true? (.key-track prog seq-id)))
                    (.setSelected cb-gate (true? (.key-gate prog seq-id)))
                    ;(.setText lab-warning (if (= in-chan out-chan) msg00 msg01))
                    ))
        ]
    (.setBorder pan-channels (factory/border "MIDI Channels"))
    (.addActionListener cb-reset reset-action)
    (.addActionListener cb-gate gate-action)
    (.addActionListener cb-track track-action)
    (.addChangeListener spin-input input-chan-listener)
    (.addChangeListener spin-output output-chan-listener)
    (.addChangeListener spin-input warning-listener)
    (.addChangeListener spin-output warning-listener)
    {:pan-main pan-main
     :sync-fn sync-fn
     }))
  
