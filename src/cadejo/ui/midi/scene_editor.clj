(println "--> cadejo.ui.midi.scene-editor")

(ns cadejo.ui.midi.scene-editor
  (:require [cadejo.config])
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.scale.registry-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.icon :as icon])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.ActionListener))

(def width 840)
(def height 660)
(def channel-count (cadejo.config/channel-count))

(defprotocol SceneEditorProtocol

  (registered-tables
    [this]
    "Convenience method returns sorted list of registered scales")

  (show-vkbd  ;; if defined make virtual keyboard visible
    [this])
  
  (show-hide-channel 
    [this id])
    
  ;; (sync-ui!
  ;;   [this])
  )

(deftype SceneEditor [scene chan-buttons basic-ed sregistry-editor   ]
  
  cadejo.ui.midi.node-editor/NodeEditor

  (cframe! [this cf]
    (.cframe! basic-ed cf))

  (cframe [this]
    (.cframe basic-ed))

  (jframe [this]
    (.jframe basic-ed))

  (show! [this]
    (.show! basic-ed))

  (hide! [this]
    (.hide! basic-ed))

  (widgets [this]
    (.widgets basic-ed))

  (widget [this key]
    (.widget basic-ed key))

  (add-widget! [this key obj]
    (.add-widget basic-ed key obj))

  (node [this]
    (.node basic-ed))
  
  (set-node! [this node]
    (.set-node! basic-ed node))

  (set-path-text! [this msg]
    (.set-path-text! basic-ed msg))

  (working [this flag]
    (.working basic-ed flag))

  (status! [this msg]
    (.status! basic-ed msg))

  (warning! [this msg]
    (.warning! basic-ed msg))

  (update-path-text [this]
    (.update-path-text basic-ed))

  ;; (sync-ui! [this]
  ;;   (.update-path-text this)
  ;;   (.sync-ui! sregistry-editor))

  (sync-ui! [this]
    (.update-path-text this)
    (dotimes [ci channel-count]
      (let [chan (inc ci)
            jb (nth chan-buttons ci)
            cobj (.channel this)
            child-count (count (.children cobj))]
        (if (pos? child-count)
          (ss/config! jb :text (format "%02d*" chan))
          (ss/config! jb :text (format "%02d" chan)))
        (.sync-ui! sregistry-editor))))
  
  SceneEditorProtocol

  (registered-tables [this]             ; ISSUE NOT IMPLEMENTEDD
    )

  (show-vkbd [this]
    (let [vkb (.get-property scene :virtual-keyboard)]
      (and vkb (.show! vkb))))

  (show-hide-channel [this id]
    (let [cobj (.channel scene id)
          ced (.get-editor cobj)
          jframe (and ced (.jframe ced))]
      (if jframe
        (if (.isVisible jframe)
          (ss/hide! jframe)
          (do
            (ss/show! jframe)
            (.toFront jframe))))))

  ;; (sync-ui! [this]
  ;;   (dotimes [ci channel-count]
  ;;     (let [chan (inc ci)
  ;;           jb (nth chan-buttons ci)
  ;;           cobj (.channel this)
  ;;           child-count (count (.children cobj))]
  ;;       (if (pos? child-count)
  ;;         (ss/config! jb :text (format "%02d*" chan))
  ;;         (ss/config! jb :text (format "%02d" chan)))
  ;;       (.sync-ui! sregistry-editor))))
  )



(defn- create-midi-panel [scene]
  (let [lab-temp (ss/label :text "FPO MIDI Panel")
        pan-main (ss/border-panel :center lab-temp)
        ]
    pan-main))

(def ^:private local-help-text
  (str "\n\n\n"
       "        Use MIDI button above to connect to a MIDI input port.\n\n"
       "        Select MIDI channel button to add instruments.\n"))

(defn- create-help-panel []
  (let [txt-area (ss/text :text local-help-text
                     :multi-line? true
                     :editable? false)
        pan-main (ss/border-panel :center txt-area)]
    pan-main))


(defn scene-editor [scene]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :scene scene true)
        toolbar (.widget basic-ed :toolbar)
        jb-vkbd (factory/icon-button :keyboard :virtual "Show Virtual Keyboard")
        jb-scale-registry (factory/icon-button :general :staff "Show Scale Registry")
        jb-midi (factory/icon-button :midi :plug "Show MIDI configuration")
        jb-help (.widget basic-ed :jb-help)
        jb-channels (let [acc* (atom [])]
                      (dotimes [i channel-count]
                        (let [jb (ss/button :text (format "%02d" (inc i)))]
                          (.putClientProperty jb :channel i)
                          (swap! acc* (fn [q](conj q jb)))))
                      @acc*)
        pan-channels (ss/toolbar :orientation :horizontal
                                 :floatable? false
                                 :items [jb-vkbd
                                         (ss/grid-panel :rows 2 :items jb-channels
                                                        :border (factory/title "Channels"))])
        sregistry-editor (cadejo.ui.scale.registry-editor/registry-editor scene)
        pan-midi (create-midi-panel scene)
        pan-help (create-help-panel)
        pan-cards (ss/card-panel :items [[pan-help :help]
                                         [pan-midi :midi]
                                         [(.widget sregistry-editor :pan-main) :scale-registry]])
        pan-center (.widget basic-ed :pan-center)
        sed (SceneEditor. scene jb-channels basic-ed sregistry-editor)
        ]
    (.set-icon! basic-ed (lnf/read-icon :general :scene))
    (.help-topic! (.cframe basic-ed) :scene)
    (ss/listen jb-midi :action (fn [_]
                                 (ss/show-card! pan-cards :midi)))
    (ss/listen jb-scale-registry :action (fn [_]
                                           (ss/show-card! pan-cards :scale-registry)))
    (ss/listen jb-help :action (fn [_]
                                 (ss/show-card! pan-cards :help)))
    (ss/config! toolbar :items [jb-midi jb-scale-registry])
    (ss/config! pan-center :center pan-cards)
    (ss/config! pan-center :south pan-channels)

    (ss/listen jb-vkbd :action (fn [_]
                                 (let [vkb (.get-property scene :virtual-keyboard)]
                                   (and vkb (.show! vkb)))))
    (.size! (.cframe basic-ed) width height)
    sed))
    

                              
  
