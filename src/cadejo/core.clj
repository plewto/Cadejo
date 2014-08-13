(println "--> Loading Cadejo")
 (ns cadejo.core
   (:use [cadejo.util.trace])
   (:require [cadejo.config :as config])  
   (:require [seesaw.core :as ss])
   (:require [overtone.core :as ot])
   (:require [cadejo.ui.util.icon])
   (:require [sgwr.drawing])
   (:require [cadejo.midi.scene])
   (:require [cadejo.midi.channel])
   (:require [cadejo.midi.performance])
   (:require [cadejo.midi.program-bank])
   (:require [cadejo.midi.mono-mode])
   (:require [cadejo.midi.poly-mode])
   (:require [cadejo.instruments.descriptor])
   (:require [cadejo.util.midi])
   (import java.awt.Color))


(require '[cadejo.instruments.algo.algo-engine :as algo])
(def algo-descriptor algo/algo-descriptor) 
(cadejo.config/add-instrument! algo-descriptor)

(require '[cadejo.instruments.alias.alias-engine :as alias])
(def alias-descriptor alias/alias-descriptor)
(cadejo.config/add-instrument! alias-descriptor)

(require '[cadejo.instruments.combo.combo-engine :as combo])
(def combo-descriptor combo/combo-descriptor)
(cadejo.config/add-instrument! combo-descriptor)

(require '[cadejo.instruments.masa.masa-engine :as masa])
(def masa-descriptor masa/masa-descriptor)
(cadejo.config/add-instrument! masa-descriptor)


(defn scene-creation-dialog []
  (let [selected* (atom nil)
        buttons (let [acc* (atom [])
                      grp (ss/button-group)]
                  (doseq [t (cadejo.util.midi/transmitters)]
                    (let [[flag dev] t
                          info (.getDeviceInfo dev)
                          name (.getName info)
                          ;descript (.getDecription info)
                          jb (ss/radio :text name :group grp)]
                      (.putClientProperty jb :name name)
                      (.putClientProperty jb :dev (second (cadejo.util.midi/parse-device-name name)))
                      (ss/listen jb :action (fn [ev]
                                              (let [src (.getSource ev)
                                                    n (.getClientProperty src :name)
                                                    dev (.getClientProperty src :dev)]
                                                (reset! selected* dev))))
                      (swap! acc* (fn [n](conj n jb)))))
                  @acc*)
        pan-device (ss/grid-panel :columns 1 :items buttons)
        jb-create (ss/button :text "Create Scene")
        dia (ss/dialog :title "Create Scene"
                       :content pan-device
                       :type :plain
                       :options [jb-create]
                       :size [400 :by 200])]
    (ss/listen jb-create :action (fn [_]
                                   (println (format "DEBUG Selected device %s" @selected*))
                                   (let [midi-input-device @selected*
                                         s (cadejo.midi.scene/scene midi-input-device)
                                         sed (.get-editor s)]
                                     (.sync-ui! sed)
                                     (ss/show! (.frame sed))
                                     (ss/return-from-dialog dia true))))
    (ss/show! dia)
))



(defn server-boot-dialog []
  (if (ot/server-connected?)
    (println ";; Using existing server")
    (let [grp (ss/button-group)
          rb-internal (ss/radio :text "Internal" :group grp)
          rb-external (ss/radio :text "External" :group grp)
          jb-boot (ss/button :text "Start Server")
          pan-options (ss/vertical-panel :items [rb-internal rb-external])
          dia (ss/dialog :title "Select SuperCollider Server"
                         :content pan-options
                         :type :plain
                         :options [jb-boot]
                         :on-close :nothing
                         :size [400 :by 200])]
      (.setSelected rb-external true)
      (ss/listen jb-boot :action 
                 (fn [_]
                   (if (.isSelected rb-external)
                     (ot/boot-external-server)
                     (ot/boot-internal-server))
                   (ss/return-from-dialog dia true)))
      (ss/show! dia))))


(defn cadejo-splash []
  (let [fg (Color. 150 150 100)
        jb-scene (ss/button :text "Create Scene"
                            :background :black
                            :foreground fg)
        jb-about (ss/button :text "About"
                            :background :black
                            :foreground fg)
        jb-help (ss/button :text "Help"
                           :background :black
                           :foreground fg)
        lab-version (ss/label :text (format "Cadejo Version %s" (config/cadejo-version))
                              :foreground fg)
        pan-south (let [tbar (ss/toolbar :floatable? false
                                         :background :black)]
                    (.addSeparator tbar)
                    (.add tbar lab-version)
                    (.addSeparator tbar)
                    (.add tbar jb-scene)
                    (.addSeparator tbar)
                    (.add tbar jb-about)
                    (.addSeparator tbar)
                    (.add tbar jb-help)
                    tbar)
        splash (let [drw (sgwr.drawing/native-drawing 800 585)]
                  (.paper! drw :black)
                  (.read-background! drw cadejo.ui.util.icon/splash-filename)
                  (.drawing-canvas drw))
        pan-main (ss/border-panel :center splash
                                  :south pan-south
                                  :background :black)
        f (ss/frame :title "Cadejo"
                    :content pan-main
                    :size [800 :by 637]
                    :on-close :nothing ;; ISSUE
                    )]

    (ss/listen jb-help :action 
               (fn [_]
                 (println (.getSize f))))

    (ss/listen jb-about :action
               (fn [_]
                 (println (format ";; Cadejo version %s" (config/cadejo-version)))))

    (ss/listen jb-scene :action (fn [_] (scene-creation-dialog)))

    (ss/show! f) 
    (server-boot-dialog)))
    



(cadejo-splash)
