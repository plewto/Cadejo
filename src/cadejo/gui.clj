(ns cadejo.gui
  (:require [cadejo.core])
  (:require [cadejo.config :as config])
  (:require [seesaw.core :as ss])
  (:require [overtone.core :as ot])
  (:require [cadejo.ui.util.icon])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf])
  (:require [cadejo.util.midi]))

(config/load-gui! true)

(defn cadejo-splash []
  (let [lab-header (ss/label :icon cadejo.ui.util.icon/splash-image)
        jb-skin (ss/button :text "Skin")
        jb-about (ss/button :text "About")
        jb-help (ss/button :text "Help")
        jb-exit (ss/button :text "Exit")
        txt-status (ss/text :multi-line? false
                           :editable? false
                           :border (factory/bevel))
        pan-south (ss/vertical-panel 
                   :items [(ss/grid-panel :rows 1 :items [jb-skin jb-about jb-help jb-exit])
                           txt-status]
                   :border (factory/padding))
        ;; server selection panel
        server-group (ss/button-group)
        server-selection* (atom nil)
        rb-internal (ss/radio :text "Internal" :group server-group)
        rb-external (ss/radio :text "External" :group server-group)
        rb-existing (ss/radio :text "Existing" :group server-group :enabled? false)
        jb-server (ss/button :text "Start Server" :enabled? false)
        pan-server-options (ss/grid-panel :columns 1
                                          :items [rb-internal rb-external rb-existing (ss/vertical-panel) jb-server]
                                          :border (factory/title "Select SuperCollider Server"))
        
        ;; Scene creation panel 
        selected-device-button* (atom nil)
        jb-create-scene (ss/button :text "Create Scene" :enabled? false)
        device-buttons (let [acc* (atom [])
                             grp (ss/button-group)]
                         (doseq [t (cadejo.util.midi/transmitters)]
                           (let [[flag dev] t
                                 info (.getDeviceInfo dev)
                                 [name dev](cadejo.util.midi/parse-device-name (.getName info))
                                 tb (ss/radio :text (format "%s %s" name dev)
                                              :group grp
                                              :enabled? flag)]
                             (.putClientProperty tb :name name)
                             (.putClientProperty tb :dev dev)
                             (ss/listen tb :action (fn [ev]
                                                     (reset! selected-device-button* (.getSource ev))
                                                     (ss/config! jb-create-scene :enabled? true)))
                             (if name (swap! acc* (fn [n](conj n tb))))
                         (swap! acc* (fn [n](conj n (ss/vertical-panel))))
                         (swap! acc* (fn [n](conj n jb-create-scene)))))
                         @acc*)
        pan-scene (ss/grid-panel :columns 1
                                  :items device-buttons
                                  :border (factory/title "Select Scene MIDI Device"))

        pan-center (ss/card-panel
                    :items [[pan-server-options :server]
                            [pan-scene :scene]
                            ]
                   :border (factory/padding))

        pan-main (ss/border-panel :north lab-header
                                  :center pan-center
                                  :south pan-south)
        f (ss/frame :title (format "Cadejo %s" (config/cadejo-version))
                    :content pan-main
                    :on-close :nothing
                    :size [783 :by 551])]
    (if (ot/server-connected?) 
      (do 
        (ss/show-card! pan-center :scene)
        (ss/config! txt-status :text "Using existing server")))
    
    (ss/listen jb-help :action (fn [_](println (.size f)))) ;; DEBUG
    
    (ss/listen rb-internal :action (fn [_]
                                     (reset! server-selection* :internal)
                                     (.setEnabled jb-server true)))
    (ss/listen rb-external :action (fn [_]
                                     (reset! server-selection* :external)
                                     (.setEnabled jb-server true)))
    (ss/listen jb-server :action 
               (fn [_]
                 (let [s @server-selection*]
                   (println (format "Booting %s server" s))
                   (ss/config! txt-status :text (format "Using %s server" s))
                   (cond (= s :internal)
                         (do 
                           (ot/boot-internal-server)
                           (ss/show-card! pan-center :scene))
                         (= s :external)
                         (do 
                           (ot/boot-external-server)
                           (ss/show-card! pan-center :scene))
                         :default
                         nil))))
                         
    (ss/listen jb-create-scene :action 
               (fn [_]
                 (let [jb @selected-device-button*
                       name (.getClientProperty jb :name)
                       dev (.getClientProperty jb :dev)
                       s (cadejo.midi.scene/scene dev)
                       sed (.get-editor s)
                       sframe (.frame sed)]
                   (.put-property! s :midi-device-name name)
                   (.setEnabled jb false)
                   (.setEnabled jb-create-scene false)
                   (.sync-ui! sed)
                   (ss/show! sframe)
                   (ss/config! txt-status :text (format "Scene %s %s created" name dev)))))
      
    (ss/listen jb-skin :action (fn [_](cadejo.ui.util.lnf/skin-dialog)))
             
    
    (ss/show! f)))



(cadejo-splash)
