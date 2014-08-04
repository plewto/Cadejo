(println "--> channel-editor")

(ns cadejo.ui.midi.channel-editor
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.color-utilities])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.WindowListener))

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
 
  (sync-ui!
    [this]))


(defn channel-editor [chanobj]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :channel chanobj)
        pan-center (.widget basic-ed :pan-center)
        pan-performance (ss/toolbar :floatable? false)
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)
        pan-tabs (ss/tabbed-panel :tabs [{:title "MIDI" 
                                          :content (.widget properties-editor :pan-main)}
                                         ])]

    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [[bg fg] (cadejo.ui.util.color-utilities/channel-color-cue (.channel-number chanobj))
          ced (reify ChannelEditor
                
                (widgets [this] (.widgets basic-ed))

                (widget [this key]
                  (or (.widget basic-ed key)
                      (umsg/warning (format "ChannelEditor does not have %s widget" key))))

                (node [this] (.node basic-ed))

                (status! [this msg]
                  (.status! basic-ed msg))

                (warning! [this msg]
                  (.warning! basic-ed msg))

                (frame [this]
                  (.widget this :frame))

                (show-scene [this]
                  (let [scene (.parent chanobj)
                        sed (.get-editor scene)
                        sframe (.frame sed)]
                    (ss/show! sframe)
                    (.toFront sframe)))

                ;; (sync-ui! [this]
                ;;   (.removeAll pan-performance)
                ;;     (doseq [p (.children chanobj)]
                ;;       (let [pid (.get-property p :id)
                ;;             jb (ss/button :text (name pid))]
                ;;         (.putClientProperty jb :performance-id pid)
                ;;         (.add pan-performance jb)
                ;;         (ss/listen jb 
                ;;                    :action 
                ;;                    (fn [ev]
                ;;                      (let [src (.getSource ev)
                ;;                            pid (.getClientProperty src :performance-id)
                ;;                            pobj (.performance chanobj pid)
                ;;                            ped (.get-editor pobj)
                ;;                            pframe (.frame ped)]
                ;;                        (if (.isVisible pframe)
                ;;                          (.setVisible pframe false)
                ;;                          (do
                ;;                            (.setVisible pframe true)
                ;;                            (.toFront pframe))))) )
                ;;         (.sync-ui! (.get-editor p)) ))
                ;;   (.sync-ui! properties-editor)
                ;;   (.revalidate (.widget basic-ed :frame))
                ;;   ) ;; end sync-ui!

                (sync-ui! [this]
                  (.removeAll pan-performance)
                  (doseq [p (.children chanobj)]
                    (let [itype (.get-property p :instrument-type)
                          id (.get-property p :id)
                          logo (.logo p :small)
                          jb (ss/button :icon logo)]
                      (ss/listen jb :action (fn [ev]
                                              (let [src (.getSource ev)
                                                    mods (.getModifiers ev)
                                                    performance (.getClientProperty jb :performance)
                                                    ped (.get-editor performance)
                                                    pframe (.widget ped :frame)
                                                    chaned (.get-editor chanobj)
                                                    sed (.get-editor (.get-scene performance))
                                                    id (.getClientProperty src :id)]
                                                (println "DEBUG channel-editor click mods = " mods)
                                                (cond (= mods 17) ; shift+click remove performance
                                                      (let [ped (.get-editor performance)] 
                                                        (.remove-performance! chanobj id)
                                                        (.setVisible pframe false)
                                                        (.dispose pframe)
                                                        (.sync-ui! sed)
                                                        (.status! chaned (format "Performance %s removed" id)))

                                                      :default ; hide/show performance editor
                                                      (if (.isVisible pframe)
                                                        (.setVisible pframe false)
                                                        (do 
                                                          (.setVisible pframe true)
                                                          (.toFront pframe)))))))
                      (.putClientProperty jb :instrument-type itype)
                      (.putClientProperty jb :id id)
                      (.putClientProperty jb :performance p)
                      (.setToolTipText jb (format "%s id = %s" (name itype)(name id)))
                      (.add pan-performance jb)
                      (.sync-ui! (.get-editor p))
                      ))

                  (.sync-ui! properties-editor)
                  (.revalidate (.widget basic-ed :frame)))
                )]
      (.setOpaque (.widget ced :lab-id) true)
      (.setBackground (.widget ced :lab-id) bg)
      (.setForeground (.widget ced :lab-id) fg)
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
      (ss/config! (.frame ced) :size [1092 :by 568])
      (.addWindowListener (.widget ced :frame)
                          (proxy [WindowListener][]
                            (windowClosed [_] nil)
                            (windowClosing [_] nil)
                            (windowDeactivated [_] nil)
                            (windowIconified [_] nil)
                            (windowDeiconified [_] (.sync-ui! ced))
                            (windowActivated [_] 
                              (.sync-ui! ced))
                            (windowOpened [_] nil)))
      (.info-text! basic-ed (let [scene (.parent chanobj)
                                  sid (.get-property scene :id)
                                  cid (.get-property chanobj :id)]
                              (format "Scene %s Channel %s" sid cid)))
      ced)))
