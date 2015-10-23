(ns xolotl.ui.sr
  (:require [xolotl.ui.factory :as factory])
  (:require [xolotl.util :as util])
  (:import java.awt.event.ActionListener
           )
  )


(def bits 12)

(defn button-bar [id]
  (let [value* (atom 0)
        buttons* (atom [])

        test-action (proxy [ActionListener][]
                      (actionPerformed [evn]
                        (let [src (.getSource evn)
                              id (.getClientProperty src :id)
                              n (.getClientProperty src :bit)]
                          (println (format "%5s %s" id n)))))
        ]

        
    (dotimes [i bits]
      (let [j (util/expt 2 i)
            cb (factory/checkbox "")]
        (.putClientProperty cb :bit i)
        (.putClientProperty cb :value j)
        (.putClientProperty cb :id id)
        (.addActionListener cb test-action)
        (swap! buttons* (fn [q](conj q cb)))))
    @buttons*))


(defn sr-editor [parent-editor seq-id]
  (let [cb-taps (button-bar :tap)
        cb-seed (button-bar :seed)
        cb-mask (button-bar :mask)
        cb-inject (factory/checkbox "Inject")
        labs (let [acc* (atom [])]
               (dotimes [i bits]
                 (let [lb (factory/label (format "%2d" (inc i)))]
                   (swap! acc* (fn [q](conj q lb)))))
               @acc*)
        pan-buttons (factory/grid-panel 4 bits)
        pan-west (factory/grid-panel 4 1
                                     (factory/label "Taps")
                                     (factory/label "Seed")
                                     (factory/label "Mask")
                                     (factory/label ""))
        pan-north (factory/border-panel
                   :east cb-inject)
        pan-main (factory/border-panel :north pan-north
                                       :center pan-buttons
                                       :west pan-west
                                       :border (factory/border "Shift Register"))
                                       
        
        ]
    (doseq [bar [cb-taps cb-seed cb-mask labs]]
      (doseq [cb bar]
        (.add pan-buttons cb)))
    

    {:pan-main pan-main
     }))
        
        
