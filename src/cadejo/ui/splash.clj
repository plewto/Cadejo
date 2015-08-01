(ns cadejo.ui.splash
  (:require [cadejo.about])
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.scene])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.util.exit-dialog :as exit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.icon])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.midi])
  (:require [overtone.core :as ot])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           javax.swing.SwingUtilities
           javax.swing.Box))

(def global-scenes* (atom {}))

;;; START DEBUG
(defn get-scene []
  (let [skey (first (keys @global-scenes*))]
    (get @global-scenes* skey)))
(defn chan0 [](.channel (get-scene) 0))
(defn performance [](first (.children (chan0))))
(defn ctl [param val]
  (println (format ";; ctl  [%s] -> %s" param val))
  (doseq [s (.synths (performance))]
    (Thread/sleep 10)
    (ot/ctl s param val))
  (doseq [v (.voices (performance))]
    (Thread/sleep 10)
    (ot/ctl v param val)))
(defn ?tap [key]
  (.voice-tap? (performance) key))
(defn ?efx [key](.synth-tap? (performance) :efx key))
;;; END DEBUG

(config/load-gui! true)

(defn- splash-label []
  (ss/label :icon cadejo.ui.util.icon/splash-image))

(def ^:private big-button-size [637 :by 233])
(def ^:private splash-frame-size [1027 :by 720])

(defn- vertical-strut 
  ([n](Box/createVerticalStrut n))
  ([](vertical-strut 16)))
  
(defn- create-server-panel [tb-server tb-midi cframe]
  (let [lab-title (ss/label :text "Select Server")
        selected* (atom :default)
        grp (ss/button-group)
        rb-default (ss/radio :text "Default" :group grp :selected? true :user-data :default)
        rb-internal (ss/radio :text "Internal" :group grp :user-data :internal)
        rb-external (ss/radio :text "External" :group grp :user-data :external)
        rb-existing (ss/radio :text "Existing" :group grp :enabled? false :user-data :existing) ;; ISSUE currently do not support existing server
        buttons [rb-default rb-internal rb-external rb-existing]
        pan-options (ss/vertical-panel :items (flatten [(vertical-strut)
                                                        lab-title
                                                        (vertical-strut)
                                                        buttons]))
        jb-start (ss/button :text "Start Server"
                            :enabled? true
                            :size big-button-size)
        pan-main (ss/border-panel
                  :north (splash-label)
                  :west pan-options
                  :center (ss/vertical-panel
                           :items [jb-start]
                           :border (factory/padding 16)))]
    (doseq [b buttons]
      (ss/listen b :action 
                 (fn [ev]
                   (let [src (.getSource ev)
                         server (ss/config src :user-data)]
                     (reset! selected* server)
                     (.status! cframe 
                               (format "%s server selected" (name server)))))))
    (ss/listen jb-start :action 
               (fn [_]
                 (let [server @selected*]
                   (.status! cframe "Starting server")
                   (.working cframe true)
                   (SwingUtilities/invokeLater
                    (proxy [Runnable][]
                            (run []
                              (cond (= server :default)
                                    (ot/boot-server)
                                    (= server :internal)
                                    (ot/boot-internal-server)
                                    (= server :external)
                                    (ot/boot-external-server)
                                    (= server :existing)
                                    nil ;; ISSUE existing server not implemented
                                    :default ;; should never see this
                                    nil)
                              (doseq [b (conj buttons jb-start)](ss/config! b :enabled? false))
                              (ss/config! tb-midi :enabled? true :selected? true)
                              (.doClick tb-midi)
                              (.status! cframe (format "%s server started" (name server)))
                              (.working cframe false)))))))
    pan-main))


(defn- create-midi-panel [cframe card-panel toolbar bgroup]
  (let [lab-title (ss/label :text "Select Scene MIDI device")
        device-buttons* (atom [])
        selected-button* (atom nil)
        grp (ss/button-group)
        jb-create (ss/button :text "Create Scene"
                             :enabled? false
                             :size big-button-size)]
    (doseq [t (cadejo.util.midi/transmitters)]
      (let [[flag device] t
            info (.getDeviceInfo device)
            [hw-name sys-device](cadejo.util.midi/parse-device-name (.getName info))
            tb (ss/radio :text (format "%s %s" hw-name sys-device)
                         :group grp
                         :enabled? flag)]
        (.putClientProperty tb :name hw-name)
        (.putClientProperty tb :device sys-device)
        (if hw-name 
          (do 
            (swap! device-buttons* (fn [n](conj n tb)))
            (ss/listen tb :action 
                       (fn [ev]
                         (let [src (.getSource ev)]
                           (ss/config! jb-create :enabled? true)
                           (reset! selected-button* src)
                           (.status! cframe (format "%s selected" (.getClientProperty src :device))))))))))
    (let [pan-west (ss/vertical-panel :items (flatten (merge [(vertical-strut)
                                                              lab-title
                                                              (vertical-strut)
                                                              @device-buttons*])))
          pan-main (ss/border-panel
                    :north (splash-label)
                    :west pan-west
                    :center (ss/vertical-panel
                             :items [jb-create]
                             :border (factory/padding 16)))]
      (ss/listen jb-create :action
                 (fn [_]
                   (.working cframe true)
                   (.status! cframe "Creating scene")
                   (SwingUtilities/invokeLater
                    (proxy [Runnable][]
                      (run []
                        (let [button @selected-button*
                              name (.getClientProperty button :name)
                              device (.getClientProperty button :device)
                              s (cadejo.midi.scene/scene device)
                              sed (.get-editor s)
                              tb (factory/toggle (format "(%s %s)" name device) :general :scene "Show scene" bgroup)]
                          (ss/config! tb :user-data device)
                          (.cframe! sed cframe)
                          (ss/config! button :enabled? false :selected? false)
                          (ss/config! jb-create :enabled? false)
                          (.add toolbar tb)
                          (.add card-panel (.widget sed :pan-main) device)
                          (ss/listen tb :action 
                                     (fn [ev]
                                       (let [src (.getSource ev)
                                             id (ss/config src :user-data)]
                                         (.set-path-text! cframe (format "Root / %s" id))
                                         (ss/show-card! card-panel id))))
                          (.working cframe false)
                          (.doClick tb)
                          (swap! global-scenes* (fn [n](assoc n device s)))
                          (.status! cframe (format "Scene %s created" device))))))))
      pan-main)))

    
(defn- create-about-panel []
  (let [txt-about (ss/text :multi-line? true
                           :editable? false
                           :text cadejo.about/about-text)
        pan-main (ss/border-panel :north (splash-label)
                                  :center (ss/vertical-panel :items [txt-about]
                                                             :border (factory/padding 16)))]
    pan-main))



(defn- splash-screen []
  (let [cframe (cadejo.ui.cadejo-frame/cadejo-frame (config/cadejo-version) "")
        bgroup (ss/button-group)
        tb-server (let [b (factory/toggle "Server" :general :server "Select SuperCollider Server" bgroup)]
                    (ss/config! b :selected? true)
                    (ss/config! b :enabled? true)
                    (.setDisabledIcon b (lnf/read-icon "gray" :general :server))
                    b)
        tb-new-scene (let [b (factory/toggle "MIDI" :midi :plug "Create Scene" bgroup)]
                       (ss/config! b :enabled? false)
                       (.setDisabledIcon b (lnf/read-icon "gray" :midi :plug))
                       b)
        tb-about (factory/toggle "About" :general :info "Display about text" bgroup)
        jb-config (factory/button "Config" :general :config "Open configuration editor")
        jb-skin (factory/button "Skin" :general :skin "Open skin selector")
        jb-exit (factory/button "Exit" :general :exit "Exit Cadejo/Overtone")
        jb-help (.widget cframe :jb-help)
        toolbar-east (let [tbar (.widget cframe :toolbar-east)
                           jb-parent (.widget cframe :jb-parent)]
                       (.remove tbar jb-parent)
                       (.add tbar tb-about)
                       (.add tbar jb-exit)
                       tbar)
        toolbar-scenes (ss/toolbar :floatable? false
                                   :orientation :horizontal)
        toolbar (let [tbar (ss/toolbar :floatable? false
                                       :orientation :horizontal)]
                      (.add (.widget cframe :toolbar) tbar)
                  ;(ss/config! tbar :items [tb-server tb-new-scene toolbar-scenes :separator jb-config jb-skin :separator ])
                      (ss/config! tbar :items [tb-server tb-new-scene toolbar-scenes :separator jb-skin :separator ])
                  tbar)
       
        pan-server (create-server-panel tb-server tb-new-scene cframe)
        pan-cards (ss/card-panel)
        pan-midi (create-midi-panel cframe pan-cards toolbar-scenes bgroup)
        pan-about (create-about-panel)
        show-card (fn [button]
                    (let [id (ss/user-data button)]
                      (ss/show-card! pan-cards id)))]
    (ss/config! pan-cards :items [[pan-server "server"]
                                  [pan-midi "midi"]
                                  [pan-about "about"]])
    (ss/listen jb-exit :action exit/exit-cadejo)
    (ss/listen jb-skin :action (fn [_](lnf/skin-dialog)))
    (ss/config! tb-server :user-data "server")
    (ss/config! tb-new-scene :user-data "midi")
    (ss/config! tb-about :user-data "about")
    (doseq [b [tb-server tb-new-scene tb-about]]
      (ss/listen b :action (fn [ev]
                             (let [src (.getSource ev)]
                               (.set-path-text! cframe "Root")
                               (show-card src)))))
    (.set-path-text! cframe "Root")
    (ss/config! (.widget cframe :jframe) :on-close :nothing)
    (ss/config! (.widget cframe :jframe) :icon (cadejo.ui.util.icon/logo "cadejo" :tiny))
    (ss/config! (.widget cframe :jframe) :size splash-frame-size)
    (.add (.widget cframe :pan-center) pan-cards BorderLayout/CENTER)
    (if (ot/server-connected?)
      (do (ss/show-card! pan-cards "midi")
          (ss/config! tb-server :enabled? false)
          (ss/config! tb-new-scene :enabled? true :selected? true)
          (.status! cframe "Using existing server")))
    cframe))

(def global-splash-frame (splash-screen))
(lnf/set-initial-skin)
(.show! global-splash-frame)


