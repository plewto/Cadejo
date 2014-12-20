(ns cadejo.gui
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.core])
  (:require [cadejo.config :as config])
  (:require [cadejo.about])
  (:require [cadejo.midi.scene])
  (:require [cadejo.ui.util.icon])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.util.midi])
  (:require [seesaw.core :as ss])
  (:require [overtone.core :as ot])
  (:import javax.swing.SwingUtilities
           javax.swing.Box))

(config/load-gui! true)

(def maximum-scene-count (config/maximum-scene-count))
(def scenes* (atom [])) ;; global list of scenes

(def ^:private txt-about (ss/text :multi-line? true
                                  :editable? false
                                  :text cadejo.about/about-text))

(defn- exit-warning-dialog []
  (let [flag* (atom false)]
    (if (config/warn-on-exit)
      (let [dia (ss/dialog :content (ss/label :text "Exit Overtone ?")
                           :option-type :yes-no
                           :default-option :no
                           :success-fn (fn [_]
                                         (reset! flag* true))
                           :no-fn (fn [_]
                                    (reset! flag* false)))]
        (ss/config! dia :size [200 :by 200])
        (ss/show! dia)
        @flag*)
      true)))

(defn- cadejo-exit []
  (if (exit-warning-dialog)
    (do
      (println "Exiting Cadejo ...")
      (System/exit 0))
    (println ";; Cadejo exit canceled")))

(defn- server-panel [tb-show-server tb-show-midi txt-status progbar]
  (let [lab-title (ss/label :text "Select Server")
        selected* (atom :default)
        grp (ss/button-group)
        rb-default  (ss/radio :text "Default" :group grp :selected? true)
        rb-internal (ss/radio :text "Internal" :group grp)
        rb-external (ss/radio :text "External" :group grp)
        rb-existing (ss/radio :text "Existing" :group grp :enabled? false)
        pan-options (ss/vertical-panel :items [(Box/createVerticalStrut 16)
                                               lab-title 
                                               (Box/createVerticalStrut 16)
                                               rb-default rb-internal
                                               rb-external rb-existing])
        jb-start-server (ss/button :text "Start Server"
                                   :enabled? true
                                   :size [400 :by 100])
        pan-server-main (ss/border-panel 
                         :west pan-options
                         :center (ss/vertical-panel
                                  :items [jb-start-server]
                                  :border (factory/padding 16)))]
    (ss/listen rb-default :action
               (fn [_]
                 (reset! selected* :default)
                 (ss/config! jb-start-server :enabled? true)))

    (ss/listen rb-internal :action 
               (fn [_]
                 (reset! selected* :internal)
                 (ss/config! jb-start-server :enabled? true)))

    (ss/listen rb-external :action 
               (fn [_]
                 (reset! selected* :external)
                 (ss/config! jb-start-server :enabled? true)))

    (ss/listen rb-existing :action 
               (fn [_]
                 (reset! selected* :existing)
                 (ss/config! jb-start-server :enabled? true)))

    (ss/listen jb-start-server :action 
               (fn [_]
                 (ss/config! progbar :indeterminate? true)
                 (ss/config! txt-status :text "Starting server")
                 (SwingUtilities/invokeLater
                  (proxy [Runnable][]
                    (run []
                      (let [s @selected*]
                        (cond (= s :default)
                              (ot/boot-server)
                              (= s :internal)
                              (ot/boot-internal-server)
                              (= s :external)
                              (ot/boot-external-server)
                              
                              (= s :existing)
                              nil ;; not implemented
                              
                              :default ;; should never see default
                              nil) 
                        (ss/config! tb-show-server :enabled? false)
                        (ss/config! tb-show-midi :enabled? true)
                        (.doClick tb-show-midi)
                        (ss/config! txt-status :text (format "%s server started" (name s)))
                        (ss/config! progbar :indeterminate? false)))))))
  pan-server-main))

(defn- midi-panel [txt-status scene-buttons progbar]
  (let [lab-title (ss/label :text "Select Scene MIDI device")
        scene-counter* (atom 0)
        device-buttons* (atom [])
        selected-button* (atom nil)
        grp (ss/button-group)
        jb-create-scene (ss/button :text "Create Scene"
                                   :enabled? false
                                   :size [400 :by 100])]
    (doseq [t (cadejo.util.midi/transmitters)]
      (let [[flag dev] t
            info (.getDeviceInfo dev)
            [hw-name sys-dev](cadejo.util.midi/parse-device-name 
                              (.getName info))
            tb (ss/radio :text (format "%s %s" hw-name sys-dev)
                         :group grp
                         :enabled? flag)]
        (.putClientProperty tb :name hw-name)
        (.putClientProperty tb :dev sys-dev)

        (ss/listen tb :action (fn [ev]
                                (reset! selected-button* (.getSource ev))
                                (ss/config! jb-create-scene 
                                            :enabled? (< @scene-counter* maximum-scene-count))))

        (if hw-name (swap! device-buttons* (fn [n](conj n tb)))) ))
    (let [pan-west (ss/vertical-panel :items (flatten (merge [(Box/createVerticalStrut 16)
                                                              lab-title
                                                              (Box/createVerticalStrut 16)]
                                                             @device-buttons*)))
          pan-midi-main (ss/border-panel
                         :west pan-west
                         :center (ss/vertical-panel
                                  :items [jb-create-scene]
                                  :border (factory/padding 16)))]
      (ss/listen jb-create-scene
                 :action 
                 (fn [_]
                   (ss/config! progbar :indeterminate? true)
                   (ss/config! txt-status :text "Creating Cadejo Scene")
                   (SwingUtilities/invokeLater
                    (proxy [Runnable][]
                      (run []
                        (let [rb @selected-button*
                              name (.getClientProperty rb :name)
                              dev (.getClientProperty rb :dev)
                              s (cadejo.midi.scene/scene dev)
                              sed (.get-editor s)
                              sframe (.frame sed)
                              s-count @scene-counter*
                              jb (nth scene-buttons s-count)]
                          (.putClientProperty jb :scene-editor sed)
                          (swap! scene-counter* inc)
                          (ss/config! jb :text (str name))
                          (ss/config! jb :enabled? true)
                          (swap! scenes* (fn [q](conj q s)))
                          (.put-property! s :midi-device-name name)
                          (.setEnabled rb false)
                          (.setEnabled jb-create-scene false)
                          (.sync-ui! sed)
                          (ss/show! sframe)
                          (if (>= (inc s-count) maximum-scene-count)
                            (doseq [b @device-buttons*]
                              (ss/config! b :enabled? false)))
                          (ss/config! progbar :indeterminate? false)
                          (ss/config! txt-status
                                      :text (format "Scene %s %s created" 
                                                    name dev))))))))
                    pan-midi-main)))
      

(defn cadejo-splash []
  (let [lab-header (ss/label :icon cadejo.ui.util.icon/splash-image)
        grp-card (ss/button-group)
        tb-show-server (let [b (factory/toggle "Server" :general :server "Select SuperCollider Server" grp-card)]
                         (ss/config! b :selected? true)
                         (ss/config! b :enabled? true)
                         b)
        tb-show-midi (let [b (factory/toggle "MIDI" :midi :plug "Select MIDI input device" grp-card)]
                       (ss/config! b :enabled? false))
        tb-show-about (factory/toggle "About" :general :info "Display version info" grp-card)
        jb-config (factory/button "Config" :general :config "Open Cadejo config editor")
        jb-skin (factory/button "Skin" :general :skin "Open skin selector")
        jb-help (factory/button "Help" :general :help "Display help")
        ;jb-test (factory/button "Test" :general :test "Test transperancey")
        jb-exit (factory/button "Exit" :general :exit "Exit Cadejo/Overtone")
        
        txt-status (ss/text 
                    :text (format "Cadejo %s" (config/cadejo-version))
                    :multi-line? false
                    :editable? false
                    :border (factory/bevel))
        status (fn [txt](ss/config! txt-status :text txt))
        scene-buttons (let [acc* (atom [])]
                        (dotimes [n maximum-scene-count]
                          (let [jb (ss/button :text "" :enabled? false)]
                            (ss/listen jb :action (fn [ev]
                                                     (let [src (.getSource ev)
                                                           sed (.getClientProperty src :scene-editor)
                                                           frm (.widget sed :frame)]
                                                       (ss/show! frm)
                                                       (.toFront frm))))
                            (swap! acc* (fn [q](conj q jb)))))
                        @acc*)
        pan-tbar (ss/toolbar :floatable? false
                             :items (flatten (merge [tb-show-server
                                                     tb-show-midi
                                                     jb-config
                                                     jb-skin
                                                     tb-show-about
                                                     jb-help
                                                     ;jb-test
                                                     jb-exit
                                                     :separator]
                                                    scene-buttons)))
        progress-bar (ss/progress-bar :indeterminate? false)
        pan-south (ss/vertical-panel :items [pan-tbar
                                             (ss/horizontal-panel 
                                              :items [txt-status progress-bar]
                                              :border (factory/bevel))]
                                     :border (factory/padding 4))
                                     
                  
        pan-server (server-panel tb-show-server tb-show-midi txt-status progress-bar)
        pan-midi (midi-panel txt-status scene-buttons progress-bar)
        pan-cards (ss/card-panel 
                   :items [[pan-server :server]
                           [pan-midi :midi]
                           [txt-about :about]]
                   :border (factory/padding 8))
        pan-main (ss/border-panel :north lab-header
                                  :center pan-cards
                                  :south pan-south)
        f (ss/frame :title (format "Cadejo %s" (config/cadejo-version))
                    :content pan-main
                    :on-close :nothing
                    :size [783 :by 551])]

    (ss/listen tb-show-server :action (fn [_]
                                        (ss/show-card! pan-cards :server)))

    (ss/listen tb-show-midi :action (fn [_]
                                      (ss/show-card! pan-cards :midi)))

    (ss/listen tb-show-about :action (fn [_]
                                       (ss/show-card! pan-cards :about)))
    (ss/listen jb-skin :action (fn [_]
                                 (lnf/skin-dialog)))

    (ss/listen jb-config :action (fn [_]
                                   (status "Config NOT IMPLEMENTED")))

    (ss/listen jb-exit :action (fn [_]
                                 (cadejo-exit)))

    (.putClientProperty jb-help :topic :cadejo)
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)

    (if (ot/server-connected?)
      (do
        (ss/show-card! pan-cards :midi)
        (ss/config! tb-show-server :enabled? false)
        (ss/config! tb-show-midi :enabled? true)
        (ss/config! txt-status :text "Using existing server")))
    (reset! config/splash-frame* f)
    (ss/show! f)))

(cadejo-splash)
(lnf/set-initial-skin)


