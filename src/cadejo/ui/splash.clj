(ns cadejo.ui.splash
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.scene])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.util.about-dialog :as about])
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

(config/load-gui! true)

(defn- splash-label []
  (ss/label :icon (cadejo.ui.util.icon/splash-image)))

(def ^:private big-button-size [637 :by 233])
(def ^:private splash-frame-size [1027 :by 720])

(defn- vertical-strut 
  ([n](Box/createVerticalStrut n))
  ([](vertical-strut 16)))
  
(defn- create-server-panel [cframe]
  (let [lab-title (ss/label :text "Select Server")
        selected* (atom :default)
        grp (ss/button-group)
        server-up? (ot/server-connected?)
       
        rb-internal (ss/radio :text "Internal" :group grp 
                              :selected? (not server-up?)
                              :enabled? (not server-up?)
                              :user-data :internal)
        rb-external (ss/radio :text "External" :group grp 
                              :selected? (not server-up?)
                              :enabled? (not server-up?)
                              :user-data :external)
        rb-default (ss/radio :text "Default" :group grp 
                             :selected? (not server-up?)
                             :enabled? (not server-up?)
                             :user-data :default)
        rb-existing (ss/radio :text "Existing" :group grp 
                              :selected? server-up?
                              :enabled? server-up?
                              :user-data :existing) 
        buttons [rb-default rb-internal rb-external rb-existing]
        pan-options (ss/vertical-panel :items (flatten [(vertical-strut)
                                                        lab-title
                                                        (vertical-strut)
                                                        buttons]))
        jb-start (ss/button :text (if server-up? "USe Existing Server" "Start Server")
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
                              (cond (ot/server-connected?)
                                    nil ;; use existing server
                                    (= server :default)
                                    (ot/boot-server)
                                    (= server :internal)
                                    (ot/boot-internal-server)
                                    (= server :external)
                                    (ot/boot-external-server)
                                    :default ;; should never see this
                                    nil)
                              (doseq [b (conj buttons jb-start)](ss/config! b :enabled? false))
                              (.status! cframe (format "%s server started" (name server)))
                              (.working cframe false)))))))
    pan-main))

(defn- splash-screen []
  (let [cframe (cadejo.ui.cadejo-frame/cadejo-frame (config/cadejo-version) "")
        bgroup (ss/button-group)
        jb-about (factory/button "About" :general :info "Display About Text")
        jb-skin (factory/button "Skin" :general :skin "Open skin selector")
        jb-exit (factory/button "Exit" :general :exit "Exit Cadejo/Overtone")
        jb-help (.widget cframe :jb-help)
        toolbar (let [tbar (.widget cframe :toolbar-east)
                           jb-parent (.widget cframe :jb-parent)]
                       (.remove tbar jb-parent)
                       (.add tbar jb-skin)
                       (.add tbar jb-about)
                       (.add tbar jb-exit)
                       tbar)
        pan-server (create-server-panel cframe)]
    (ss/listen jb-exit :action exit/exit-cadejo)
    (ss/listen jb-skin :action lnf/skin-dialog)
    (ss/listen jb-about :action about/about-dialog)
    (ss/config! (.widget cframe :jframe) :on-close :nothing)
    (ss/config! (.widget cframe :jframe) :icon (cadejo.ui.util.icon/logo "cadejo" :tiny))
    (ss/config! (.widget cframe :jframe) :size splash-frame-size)
    (.add (.widget cframe :pan-center) pan-server BorderLayout/CENTER)
    cframe))

(def global-splash-frame (splash-screen))
(lnf/set-initial-skin)
(.show! global-splash-frame)

