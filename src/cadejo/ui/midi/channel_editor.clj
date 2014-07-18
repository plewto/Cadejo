(println "--> channel-editor")

(ns cadejo.ui.midi.channel-editor
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
          ))

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
    
  (sync-ui
    [this]))


(defn channel-editor [chanobj]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :channel chanobj)
        pan-center (.widget basic-ed :pan-center)
        pan-performance (ss/grid-panel :columns 1)
        ]
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

                (sync-ui [this]
                  
                  ;; update performances
                  (.removeAll pan-performance)
                  (doseq [p (.children chanobj)]
                    (let [pid (.get-property p :id)
                          jb (ss/button :text (name pid))]
                      (.putClientProperty jb :performance-id pid)
                      (.add pan-performance jb)
                      (println "DEBUG pid = " pid)
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
                      (.sync-ui (.get-editor p))))
                  ;; end update performances
                  (.revalidate (.widget basic-ed :frame))
                  ) ; end sync-ui
                )]
      (ss/listen (.widget ced :jb-parent)
                 :action (fn [_]
                           (let [scene (.parent chanobj)
                                 sed (.get-editor scene)
                                 sframe (.frame sed)]
                             (ss/show! sframe)
                             (.toFront sframe))))
                                                    
      
      (.add pan-center pan-performance BorderLayout/SOUTH)
      ced)))
                          
    
                      
            
