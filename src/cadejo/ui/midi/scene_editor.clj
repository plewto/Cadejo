(ns cadejo.ui.midi.scene-editor
  (:require [cadejo.ui.scale.registry-editor])
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.color-utilities])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout))

(def channel-count (cadejo.config/channel-count))
(def frame-size [1000 :by 610])


(defprotocol SceneEditor
  
  (widgets 
    [this])

  (widget
    [this key])

  (node 
    [this])

  (registered-tables
    [this]
    "Convenience method returns sorted list of registered scales")

  (status!
    [this msg])

  (warning!
    [this msg])

  (frame 
    [this])

  (show-hide-channel 
    [this id])
    
  (sync-ui!
    [this]))

(defn scene-editor [scene]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :scene scene)
        pan-center (.widget basic-ed :pan-center)
        jb-channels (let [acc* (atom [])]
                      (dotimes [i channel-count]
                        (let [jb (ss/button :text (format "%02d" i)
                                            :id (format "jb-channel-%02d" i))
                              ;[bg fg](cadejo.ui.util.color-utilities/channel-color-cue i)
                              ]
                          ;; (.setBackground jb bg)
                          ;; (.setForeground jb fg)
                          (.putClientProperty jb :channel i)
                          (swap! acc* (fn [n](conj n jb)))))
                      @acc*)
        pan-channels (ss/grid-panel :rows 2 :items jb-channels
                                    :border (factory/title "Channels"))
        reged (cadejo.ui.scale.registry-editor/registry-editor scene)

        txt-tree (ss/text :text " "
                          :multi-line? true
                          :editable? false)
        pan-tree (ss/border-panel :north (ss/label "Cadejo Tree")
                                  :center txt-tree)
        pan-tab (ss/tabbed-panel 
                 :tabs [{:title "Tree" :content pan-tree}
                        {:title "Scale Registry" :content (.widget reged :pan-main)}]
                 :border (factory/padding))]
    (ss/config! (.widget basic-ed :frame) :on-close :nothing)
    (ss/config! (.widget basic-ed :frame) :size frame-size)
    (.add pan-center pan-channels BorderLayout/SOUTH)
    (.add pan-center pan-tab BorderLayout/CENTER)

    (.setEnabled (.widget basic-ed :jb-parent) false)
    (.info-text! basic-ed (format "MIDI device %s" (.get-property scene :id)))
    (let [sed (reify SceneEditor 
                
                (widgets [this]
                  (.widgets basic-ed))

                (widget [this key]
                  (or (.widget basic-ed key)
                      (umsg/warning (format "SceneEditor does not have %s widget" key))))

                (node [this]
                  (.node basic-ed))

                (registered-tables [this]
                  (.registered-tables (.node this)))

                (status! [this msg]
                  (.status! basic-ed msg))

                (warning! [this msg]
                  (.warning! basic-ed msg))

                (frame [this]
                  (.widget basic-ed :frame))

                (show-hide-channel [this id]
                  (let [cobj (.channel scene id)
                        ced (.get-editor cobj)
                        cframe (.widget ced :frame)]
                    (if (.isVisible cframe)
                      (ss/hide! cframe)
                      (do 
                        (ss/show! cframe)
                        (.toFront cframe)))))

                (sync-ui! [this]
                  ;; update channel buttons
                  (dotimes [ci channel-count]
                    (let [jb (nth jb-channels ci)
                          cobj (.channel scene ci)
                          child-count (count (.children cobj))]
                      (if (pos? child-count)
                        (ss/config! jb :text (format "%02d*" ci))
                        (ss/config! jb :text (format "%02d" ci)))
                      (.sync-ui! (.get-editor cobj))))
                  (.sync-ui! reged)
                  (ss/config! txt-tree :text (.rep-tree scene 0))
                  (.revalidate (.widget basic-ed :frame))
                  ) ;; end sync-ui!
                )]
      (doseq [jb jb-channels]
        (ss/listen jb :action (fn [ev]
                                (let [src (.getSource ev)
                                      cid (.getClientProperty src :channel)]
                                  (.show-hide-channel sed cid)))))
      sed)))
