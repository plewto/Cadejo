(println "--> performance-editor")

(ns cadejo.ui.midi.performance-editor
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.bank-editor])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.WindowListener))
          

(defprotocol PerformanceEditor

   (widgets 
    [this])

  (widget
    [this key])
  
  (node 
    [this])

  (color-id! 
    [this n]
    "Sets the background and foreground colors of
     :lab-id widget")

  (color-id
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
        color-id* (atom nil)
        bank-ed (cadejo.ui.midi.bank-editor/bank-editor (.bank performance))
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)

        pan-tabs (ss/tabbed-panel :tabs [{:title "Bank" :content (.widget bank-ed :pan-main)}
                                         {:title "MIDI" :content (.widget properties-editor :pan-main)}
                                         ])
        pan-center (.widget basic-ed :pan-center)
        

        ]
    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ped (reify PerformanceEditor
                 (widgets [this] (.widgets basic-ed))

                 (widget [this key]
                   (or (.widget basic-ed key)
                      (umsg/warning (format "PerformanceEditor does not have %s widget" key))))

                 (node [this] (.node basic-ed))
                 
                 (color-id! [this n]
                   (let [bg (cadejo.config/performance-id-background n)
                         fg (cadejo.config/performance-id-foreground n)
                         lab-id (.widget this :lab-id)]
                     (.setOpaque lab-id true)
                     (.setBackground lab-id bg)
                     (.setForeground lab-id fg)
                     (.revalidate lab-id)
                     (reset! color-id* n)))

                 (color-id [this] @color-id*)

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
                   (.sync-ui! bank-ed)
                   (.sync-ui! properties-editor)
                   )
                 )]
      (.set-parent-editor! properties-editor ped)
      (.add pan-center pan-tabs BorderLayout/CENTER)
      (ss/config! (.frame ped) :size [1082 :by 540])
      (ss/listen (.widget ped :jb-parent)
                 :action (fn [_]
                           (let [chanobj (.parent performance)
                                 ced (.get-editor chanobj)
                                 cframe (.frame ced)]
                             (ss/show! cframe)
                             (.toFront cframe))))

      (.addWindowListener (.widget ped :frame)
                          (proxy [WindowListener][]
                            (windowClosed [_] nil)
                            (windowClosing [_] nil)
                            (windowDeactivated [_] nil)
                            (windowIconified [_] nil)
                            (windowDeiconified [_] (.sync-ui! ped))
                            (windowActivated [_] 
                              (.sync-ui! ped))
                            (windowOpened [_] nil)))
      (.info-text! basic-ed (let [scene (.get-scene performance)
                                  chanobj (.parent performance)
                                  sid (.get-property scene :id)
                                  cid (.get-property chanobj :id)
                                  pid (.get-property performance :id)]
                              (format "Scene %s   Channel %s   Performance %s"
                                      sid cid pid)))
      ped)))
