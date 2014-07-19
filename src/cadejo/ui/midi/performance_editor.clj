(println "--> performance-editor")

(ns cadejo.ui.midi.performance-editor
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
          ))

(defprotocol PerformanceEditor

   (widgets 
    [this])

  (widget
    [this key])
  
  (node 
    [this])

  (status!
    [this msg])

  (warning!
    [this msg])

  (frame 
    [this])

  (show-channel
   [this])
    
  (sync-ui!
    [this]))

(defn performance-editor [performance]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :performance performance)
        pan-center (.widget basic-ed :pan-center)
        ]
    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ped (reify PerformanceEditor
                 (widgets [this] (.widgets basic-ed))

                 (widget [this key]
                   (or (.widget basic-ed key)
                      (umsg/warning (format "PerformanceEditor does not have %s widget" key))))

                 (node [this] (.node basic-ed))
                 
                 (status! [this msg]
                   (.status basic-ed msg))
                 
                 (warning! [this msg]
                   (.warning basic-ed msg))
                 
                 (frame [this]
                   (.widget this :frame))

                 (show-channel [this]
                   (let [chanobj (.parent performance)
                         ced (.get-editor chanobj)
                         cframe (.frame ced)]
                     (ss/show! cframe)
                     (.toFront cframe)))
                 
                 (sync-ui! [this]
                   (println "Performance.sync-ui! not implemented")
                   )
                 )]
      (ss/listen (.widget ped :jb-parent)
                 :action (fn [_]
                           (let [chanobj (.parent performance)
                                 ced (.get-editor chanobj)
                                 cframe (.frame ced)]
                             (ss/show! cframe)
                             (.toFront cframe))))
      ped)))
