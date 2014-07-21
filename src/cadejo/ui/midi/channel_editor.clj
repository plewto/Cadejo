(println "--> channel-editor")

(ns cadejo.ui.midi.channel-editor
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.color-utilities])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout))

(defprotocol ChannelEditor

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

  (show-scene
   [this])

  (show-hide-performance
    [this id])
    
  (sync-ui!
    [this]))


(defn channel-editor [chanobj]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :channel chanobj)
        pan-center (.widget basic-ed :pan-center)
        pan-performance (ss/grid-panel :rows 2 :columns 3)
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)
        pan-tabs (ss/tabbed-panel :tabs [{:title "MIDI" 
                                          :content (.widget properties-editor :pan-main)}
                                         ])]

    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ced (reify ChannelEditor
                
                (widgets [this] (.widgets basic-ed))

                (widget [this key]
                  (or (.widget basic-ed key)
                      (umsg/warning (format "ChannelEditor does not have %s widget" key))))

                (node [this] (.node basic-ed))

                (status! [this msg]
                  (.status basic-ed msg))

                (warning! [this msg]
                  (.warning basic-ed msg))

                (frame [this]
                  (.widget this :frame))

                (show-scene [this]
                  (let [scene (.parent chanobj)
                        sed (.get-editor scene)
                        sframe (.frame sed)]
                    (ss/show! sframe)
                    (.toFront sframe)))

                (show-hide-performance [this id]
                  (println "ISSUE show-hide-performance not implemented")
                  )

                (sync-ui! [this]
                  (.removeAll pan-performance)
                  (let [counter* (atom 0)]
                    (doseq [p (.children chanobj)]
                      (let [pid (.get-property p :id)
                            jb (ss/button :text (name pid))
                            bg (cadejo.config/performance-id-background @counter*)
                            fg (cadejo.config/performance-id-foreground @counter*)]
                        (.putClientProperty jb :performance-id pid)
                        (.putClientProperty jb :color-id @counter*)
                        (.setBackground jb bg)
                        (.setForeground jb fg)
                        (.color-id! (.get-editor p) @counter*)
                        (swap! counter* inc)
                        (.add pan-performance jb)
                        (ss/listen jb 
                                   :action 
                                   (fn [ev]
                                     (let [src (.getSource ev)
                                           pid (.getClientProperty src :performance-id)
                                           pobj (.performance chanobj pid)
                                           ped (.get-editor pobj)
                                           pframe (.frame ped)]
                                       (if (.isVisible pframe)
                                         (.setVisible pframe false)
                                         (do
                                           (.setVisible pframe true)
                                           (.toFront pframe))))))
                        (.sync-ui! (.get-editor p)))))
                  (.sync-ui! properties-editor)
                  (.revalidate (.widget basic-ed :frame))))]
      (ss/listen (.widget ced :jb-parent)
                 :action (fn [_]
                           (let [scene (.parent chanobj)
                                 sed (.get-editor scene)
                                 sframe (.frame sed)]
                             (ss/show! sframe)
                             (.toFront sframe))))
      (.set-parent-editor! properties-editor ced)
      (.add pan-center pan-tabs BorderLayout/CENTER)
      (.add pan-center pan-performance BorderLayout/SOUTH)
      (ss/config! (.frame ced) :size [1082 :by 540])
      (ss/listen (.widget ced :jb-help) :action (fn [_](.sync-ui! ced)))  ;;;; DEBUG
      ced)))
