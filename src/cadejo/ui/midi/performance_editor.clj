(ns cadejo.ui.midi.performance-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.bank-editor])
  (:require [cadejo.ui.midi.cceditor-tab])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.WindowListener))

(def frame-size [1280 :by 587])

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
        bank-ed (let [bank (.bank performance)
                      bed (cadejo.ui.midi.bank-editor/bank-editor bank)]
                  (.editor! bank bed)
                  bed)
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)
        pan-tabs (ss/tabbed-panel :tabs [{:title (if (config/enable-button-text) "Bank" "")
                                          :icon (if (config/enable-button-icons) (lnf/read-icon :general :bank) nil)
                                          :content (.widget bank-ed :pan-main)}
                                         {:title (if (config/enable-button-text) "MIDI" "")
                                          :icon (if (config/enable-button-icons) (lnf/read-icon :midi :plug) nil)
                                          :content (.widget properties-editor :pan-main)}])
        pan-center (.widget basic-ed :pan-center)
        descriptor (.get-property performance :descriptor)
        available-controllers (.controllers descriptor)
        cc-panels* (atom [])]
    (doseq [i (range 0 (count available-controllers) 4)]
      (let [cced (cadejo.ui.midi.cceditor-tab/cceditor-tab descriptor i)]
        (.addTab pan-tabs 
                 (if (config/enable-button-text) (format "CC\\%d" i) "")
                 (if (config/enable-button-icons)(lnf/read-icon :midi :ctrl) nil)
                 (.widget cced :pan-main))
        (swap! cc-panels* (fn [n](conj n cced)))))
    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ped (reify PerformanceEditor
                 (widgets [this] (.widgets basic-ed))

                 (widget [this key]
                   (or (.widget basic-ed key)
                      (umsg/warning (format "PerformanceEditor does not have %s widget" key))))

                 (node [this] (.node basic-ed))
                
                 (status! [this msg]
                   (.status! basic-ed msg))
                 
                 (warning! [this msg]
                   (.warning! basic-ed msg))
                 
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
                   (doseq [cced @cc-panels*]
                     (.sync-ui! cced))))]
      (.set-parent-editor! properties-editor ped)
      (.set-parent-editor! bank-ed ped)
      (.put-property! performance :bank-editor bank-ed)
      (doseq [cced @cc-panels*]
        (.set-parent-editor! cced ped))
      (.add pan-center pan-tabs BorderLayout/CENTER)
      (ss/config! (.frame ped) :size frame-size)
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
      (.putClientProperty (.widget basic-ed :jb-help) :topic :performance)
      ped))) 
