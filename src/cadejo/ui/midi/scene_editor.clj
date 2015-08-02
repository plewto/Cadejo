(println "--> cadejo.ui.midi.scene-editor")

(ns cadejo.ui.midi.scene-editor
  (:require [cadejo.ui.scale.registry-editor])
  (:require [cadejo.config])
  (:require [cadejo.midi.node])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.icon :as icon])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.ActionListener))

(def channel-count (cadejo.config/channel-count))

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

  (cframe!
    [this f])

  (cframe 
    [this])

  (show-vkbd  ;; if defined make virtual keyboard visible
    [this])
  
  (show-hide-channel 
    [this id])
    
  (sync-ui!
    [this]))

(defn scene-editor [scene]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :scene scene true)
        cframe (.cframe basic-ed)
        jb-vkbd (factory/icon-button :keyboard :virtual "Show Virtual Keyboard")
        pan-main (ss/border-panel)
        jb-channels (let [acc* (atom [])]
                      (dotimes [i channel-count]
                        (let [jb (ss/button :text (format "%02d" (inc i))
                                            :id (format "jb-channel-%02d" (inc i)))]
                          (.putClientProperty jb :channel i)
                          (swap! acc* (fn [n](conj n jb)))))
                      @acc*)
        pan-channels  (ss/toolbar :orientation :horizontal
                                  :floatable? false
                                  :items [jb-vkbd
                                          (ss/grid-panel :rows 2 :items jb-channels
                                                         :border (factory/title "Channels"))])
        reged (cadejo.ui.scale.registry-editor/registry-editor scene)

        txt-tree (ss/text :text " "
                          :multi-line? true
                          :editable? false)
        pan-tree (ss/border-panel :north (ss/label "Cadejo Tree")
                                  :center txt-tree)
        pan-tab (ss/tabbed-panel 
                 :tabs [{:title (if (cadejo.config/enable-button-text) "Tree" "")
                         :icon (if (cadejo.config/enable-button-icons)(lnf/read-icon :tree :info) nil)
                         :content pan-tree}
                        {:title (if (cadejo.config/enable-button-text) "Scale Registry" "")
                         :icon (if (cadejo.config/enable-button-icons)(lnf/read-icon :general :staff) nil)
                         :content (.widget reged :pan-main)}]
                 :border (factory/padding))]
    (.add pan-main pan-channels BorderLayout/SOUTH)
    (.add pan-main pan-tab BorderLayout/CENTER)
    (.set-path-text! basic-ed (format "MIDI device %s" (.get-property scene :id)))
    (.set-icon! basic-ed (icon/logo "scene" :tiny))
    (ss/config! (.widget cframe :pan-center) :center pan-main)
    (let [sed (reify SceneEditor 
                
                (widgets [this]
                  (assoc (.widgets basic-ed)
                         :pan-main pan-main))

                (widget [this key]
                  (or (get (.widgets this) key)
                      (umsg/warning (format "SceneEditor does not have %s widget" key))))

                (node [this]
                  (.node basic-ed))

                (registered-tables [this]
                  (.registered-tables (.node this)))

                (status! [this msg]
                  (.status! basic-ed msg))

                (warning! [this msg]
                  (.warning! basic-ed msg))

                (cframe [this]
                  (.widget basic-ed :cframe))

                (cframe! [this f]
                  (.cframe! basic-ed f))

                (show-vkbd [this]
                  (let [vkbd (.get-property scene :vkbd)]
                    (if vkbd
                      ((:show (.get-editor vkbd)))
                      (.status this "Virtual keyboard not defined"))))
                
                (show-hide-channel [this id]
                  (let [cobj (.channel scene id)
                        ced (.get-editor cobj)
                        jframe (.widget ced :jframe)]
                    (if (.isVisible jframe)
                      (ss/hide! jframe)
                      (do 
                        (ss/show! jframe)
                        (.toFront jframe)))))

                (sync-ui! [this]
                  ;; update channel buttons
                  (dotimes [ci channel-count]
                    (let [chan (inc ci)
                          jb (nth jb-channels ci)
                          cobj (.channel scene ci)
                          child-count (count (.children cobj))]
                      (if (pos? child-count)
                        (ss/config! jb :text (format "%02d*" chan))
                        (ss/config! jb :text (format "%02d" chan)))
                      (.sync-ui! (.get-editor cobj))))
                  (.sync-ui! reged)
                  ;;(ss/config! txt-tree :text (.rep-tree (.find-root scene) 0))
                  (ss/config! txt-tree :text
                              (cadejo.midi.node/rep-tree (.find-root scene) 0))
                  (.revalidate (.widget basic-ed :jframe))) )]
      (doseq [jb jb-channels]
        (ss/listen jb :action (fn [ev]
                                (let [src (.getSource ev)
                                      cid (.getClientProperty src :channel)]
                                  (.show-hide-channel sed cid)))))
      (.addActionListener jb-vkbd (proxy [ActionListener][]
                                    (actionPerformed [_] (.show-vkbd sed))))
      sed)))
