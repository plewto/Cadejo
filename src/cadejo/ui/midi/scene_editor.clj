(println "--> cadejo.ui.midi.scene-editor")

(ns cadejo.ui.midi.scene-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.input-port])
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.scale.registry-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.icon :as icon])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.midi :as midi])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf])
  (:import java.awt.BorderLayout
           java.awt.event.ActionListener))

(def width 840)
(def height 660)
(def channel-count (config/channel-count))

(defprotocol SceneEditorProtocol

  (registered-tables
    [this]
    "Convenience method returns sorted list of registered scales")

  (show-vkbd  ;; if defined make virtual keyboard visible
    [this])
  
  (show-hide-channel 
    [this id]) )

(deftype SceneEditor [scene chan-buttons basic-ed sregistry-editor]
  
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
 
  (sync-ui! [this]
    (.update-path-text this)
    (doseq [chanobj (.children (.node this))]
      (let [id (.get-property chanobj :id)
            jb (nth chan-buttons id)
            pcount (count(.children chanobj))]
        (if (pos? pcount)
          (ss/config! jb :text (format "%02d*" (inc id)))
          (ss/config! jb :text (format "%02d" (inc id))))
        (.sync-ui! sregistry-editor))))
  
  SceneEditorProtocol

  (registered-tables [this]             ; ISSUE NOT IMPLEMENTED
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
            (.toFront jframe)))))) )



(defn- create-midi-panel [scene]
  (let [lab-help (ss/text :text "Select MIDI input device and click 'Connect'"
                          :font (ssf/font :size 16 :style :bold)
                          :multi-line? true
                          :editable? false
                          :visible? true
                          :border (factory/padding 32))
        
        device-buttons* (atom [])
        selected-device* (atom nil)
        grp (ss/button-group)
        pan-device (ss/grid-panel :border (factory/padding 16))
        pan-main (ss/border-panel :center lab-help
                                  :west pan-device)
        jb-connect (ss/button :text "Connect"
                              :enabled? false)]
    (doseq [t (midi/transmitters)]
      (let [[flag device] t
            info (.getDeviceInfo device)
            [hw-name sys-device](midi/parse-device-name (.getName info))
            tb (ss/radio :text (format "%s %s " hw-name sys-device)
                         :group grp
                         :enabled? flag)]
        (.putClientProperty tb :name hw-name)
        (.putClientProperty tb :device sys-device)
        (if hw-name
          (do
            (swap! device-buttons* (fn [n](conj n tb)))
            (ss/listen tb :action
                       (fn [_]
                         (ss/config! jb-connect :enabled? true)
                         (reset! selected-device* hw-name)
                         (.status! (.get-editor scene) (format "%s input selected" sys-device))))))))
    (swap! device-buttons* (fn [q](conj q jb-connect)))
    (while (< (count @device-buttons*) 8)
      (swap! device-buttons* (fn [q](conj q (ss/vertical-panel)))))
    (ss/config! pan-device
                :items @device-buttons*
                :rows (inc (count @device-buttons*))
                :columns 1)
    (ss/listen jb-connect :action (fn [_]
                                    (doseq [obj @device-buttons*]
                                      (ss/config! obj :enabled? false))
                                    (let [dev @selected-device*
                                          ip (cadejo.midi.input-port/midi-input-port dev)
                                          vkb (.get-property scene :virtual-keyboard)]
                                      (.status! (.get-editor scene)(format "Connecting to %s" dev))
                                      (.add-child! ip vkb)
                                      (ss/config! lab-help :text "Select channel button to add instruments..."))))
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
        jb-xolotl (factory/icon-button :xolotl :logo "Show Xolotl Seqeuncer")
        jb-scale-registry (factory/icon-button :general :staff "Show Scale Registry")
        jb-midi (factory/icon-button :midi :plug "Show MIDI configuration")
        jb-help (.widget basic-ed :jb-help)
        jb-channels (let [acc* (atom [])]
                      (dotimes [i channel-count]
                        (let [jb (ss/button :text (format "%02d" (inc i)))]
                          (.putClientProperty jb :channel i)
                          (if (config/enable-tooltips)
                            (.setToolTipText jb (format "Editor Channel %s" (inc i))))
                          (ss/listen jb :action (fn [_]
                                                  (let [chanobj (.channel scene i)
                                                        ced (.get-editor chanobj)]
                                                    (.show! ced))))
                          (swap! acc* (fn [q](conj q jb)))))
                      @acc*)
        pan-channels (ss/toolbar :orientation :horizontal
                                 :floatable? false
                                 :items [jb-vkbd
                                         jb-xolotl
                                         (ss/grid-panel :rows 2 :items jb-channels
                                                        :border (factory/title "Channels"))])
        sregistry-editor (cadejo.ui.scale.registry-editor/registry-editor scene)
        pan-midi (create-midi-panel scene)
        pan-help (create-help-panel)
        pan-cards (ss/card-panel :items [[pan-help :help]
                                         [pan-midi :midi]
                                         [(.widget sregistry-editor :pan-main) :scale-registry]])
        pan-center (.widget basic-ed :pan-center)
        sed (SceneEditor. scene jb-channels basic-ed sregistry-editor)]
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
    (ss/listen jb-xolotl :action (fn [_]
                                   (let [vkb (.get-property scene :virtual-keyboard)]
                                     (and vkb (.show-xolotl! vkb)))))
    (ss/config! (.jframe sed) :on-close :nothing)
    (ss/show-card! pan-cards :midi)
    (.size! (.cframe basic-ed) width height)
    sed))

