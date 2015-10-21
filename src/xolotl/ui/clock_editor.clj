(ns xolotl.ui.clock-editor
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener
  ))

(defn clock-editor [parent-editor]
  (let [cb-external (factory/checkbox "External Clock")
        jb-reset (factory/button "Reset")
        rbpan-transport (factory/radio '[["Stop" :stop]["Start" :start]] 1 2 :btype :toggle)
        rb-stop (:stop (:buttons rbpan-transport))
        rb-start (:start (:buttons rbpan-transport))
        span-tempo (factory/spinner-panel "Tempo" 20 300 1)
        spin-tempo (:spinner span-tempo)
        pan-center (factory/grid-panel 2 2 cb-external jb-reset rb-stop rb-start)
        pan-main (factory/border-panel :center pan-center
                                       :south (:pan-main span-tempo))
        action-clock-select (proxy [ActionListener][]
                                (actionPerformed [_]
                                  (println "ISSUE: clock-editor.action-clock-select NOT implemented")
                                  ))
        action-reset (proxy [ActionListener][]
                       (actionPerformed [_]
                         (println "ISSUE: clock-editor.action-reset NOT implemented")
                         ))
        action-stop (proxy [ActionListener][]
                       (actionPerformed [_]
                         (println "ISSUE: clock-editor.stop NOT implemented")
                         ))
        action-start (proxy [ActionListener][]
                       (actionPerformed [_]
                         (println "ISSUE: clock-editor.start NOT implemented")
                         ))
        tempo-listener (proxy [ChangeListener][]
                         (stateChanged [_]
                           (println "ISSUE: clock-editor.tempo-listener NOT implemented")
                           ))
        ]
    (.addActionListener cb-external action-clock-select)
    (.addActionListener jb-reset action-reset)
    (.addActionListener rb-stop action-stop)
    (.addActionListener rb-start action-start)
    (.addChangeListener spin-tempo tempo-listener)
    {:pan-main pan-main
     }))
