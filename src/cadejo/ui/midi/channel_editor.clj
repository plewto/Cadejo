(println "--> channel-editor")

(ns cadejo.ui.midi.channel-editor
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.color-utilities])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.validated-text-field :as vtf])
  (:require [clojure.string ])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.WindowListener))

(def frame-size [1281 :by 661])

;; Generate unique performance name 
;;
(defn- gen-performance-name [chanobj iname]
  (let [current (.performance-ids chanobj)
        counter* (atom 0)
        name* (atom "")
        found* (atom false)
        chan (.channel-number chanobj)
        frmt "%s-%d-%d"]
    (while (not @found*)
      (reset! name* (format frmt iname chan @counter*))
      (reset! found* (not (some (fn [q](= q (keyword @name*))) current)))
      (swap! counter* inc))
    @name*))

;; Display modal dialog for adding performance/instrument to channel
;; 
(defn- performance-options-dialog [chanobj descriptor]
  (let [logo (.logo descriptor :small)
        instrument-type (.instrument-name descriptor)
        iname (clojure.string/capitalize (name instrument-type))
        about (.about descriptor)
        lab-logo (ss/label :icon logo)
        lab-name (ss/label :text (format "  %s   %s" iname about))
        jb-help (factory/button "Help" :general :help "Help") ; (ss/button)
        pan-head (ss/border-panel :west lab-logo
                                  :center lab-name
                                  :east jb-help
                                  :border (factory/padding))
        spin-mainbus (ss/spinner 
                      :model (ss/spinner-model 0 :from 0 :to 8 :by 1))
        pan-mainbus (ss/vertical-panel :items [spin-mainbus]
                                       :border (factory/title "Output bus"))
        spin-voice-count (ss/spinner
                          :model (ss/spinner-model 8 :from 1 :to 32 :by 1))
        pan-voice-count (ss/vertical-panel
                         :items [spin-voice-count]
                         :border (factory/title "Voice Count"))
        bgrp (ss/button-group)
        selected-mode* (atom nil)
        mode-buttons (let [acc* (atom [])]
                       (doseq [km (.modes descriptor)]
                         (let [rb (ss/radio :text (name km) :group bgrp)]
                           (.putClientProperty rb :keymode km)
                           (swap! acc* (fn [n](conj n rb)))
                           (ss/listen rb :action 
                                      (fn [ev]
                                        (let [src (.getSource ev)
                                              keymode (.getClientProperty src :keymode)
                                              is-poly (not (= keymode :mono))]
                                          (.setEnabled spin-voice-count is-poly)
                                          (.setEnabled pan-voice-count is-poly)
                                          (reset! selected-mode* keymode))))))
                       (.doClick (last @acc*))
                       @acc*)
        pan-modes (ss/grid-panel :rows 1
                                 :items mode-buttons
                                 :border (factory/title "Key mode"))
        pan-mode-options (ss/grid-panel :rows 1
                                        :items [pan-mainbus pan-voice-count])
        pan-north (ss/vertical-panel
                   :items [pan-head pan-modes pan-mode-options])

        ;; Controllers
        cc-spinners* (atom {})
        cc-panels (let [acc* (atom [])]
                    (doseq [cc (.controllers descriptor)]
                      (let [ccd (.controller descriptor cc)
                            usage (:usage ccd)
                            default (:default ccd)
                            spin (ss/spinner
                                  :model (ss/spinner-model default :from 0
                                                           :to 127 :by 1))
                            lab (ss/label :text (format "%-3s %-16s " (name cc) usage))
                            pan (ss/border-panel :center lab :east spin)]
                        (swap! cc-spinners* (fn [n](assoc n (keyword cc) spin)))
                        (swap! acc* (fn [n](conj n pan)))))
                    @acc*)
        pan-controllers (ss/grid-panel :columns 1
                                       :items cc-panels
                                       :border (factory/title "Controllers"))

        ;; Performance name
        pname-test (fn [n]
                     (let [current-children (.performance-ids chanobj)
                           is-continuous (= -1 (.indexOf n " "))
                           no-colon (= -1 (.indexOf n ":"))]
                       (and 
                        (pos? (count n))
                        is-continuous
                        no-colon
                        (not (some (fn [q](= (keyword n) q))
                                   current-children)))))
        vtf-instrument-id (vtf/validated-text-field :validator pname-test
                                                    :value (gen-performance-name chanobj iname)
                                                    :border "Instrument ID")
        ;; jb-add (factory/button "Add Instrument")
        ;; jb-cancel (factory/button "Cancel")
        jb-add (ss/button :text "Add Instrument")
        jb-cancel (ss/button :text "Cancel")
        pan-main (ss/border-panel :north pan-north
                                  :center pan-controllers
                                  :south (.widget vtf-instrument-id :pan-main))
        dia (ss/dialog :title (format "Add %s to channel %d"
                                      iname (.channel-number chanobj))
                       :content pan-main  
                       :type :plain
                       :options [jb-add jb-cancel]
                       :default-option jb-add
                       :modal? true
                       :on-close :dispose
                       :size [400 :by 500])]
    ;; (if (cadejo.config/enable-button-text)
    ;;   (do
    ;;     (ss/config! jb-help :text "Help")))
    ;; (if (cadejo.config/enable-button-icons)
    ;;   (do
    ;;     (.setIcon jb-help (lnf/read-icon :general :help))
    ;;     (.setSelectedIcon jb-help (lnf/read-selected-icon :general :help))))
    ;; (if (cadejo.config/enable-tooltips)
    ;;   (do
    ;;     (.setToolTipText jb-help "Add Instrument Help")))

    (.putClientProperty jb-help :topic :add-instrument)
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)

    (ss/listen jb-cancel :action 
               (fn [_]
                 (.status! (.get-editor chanobj)
                           "Add instrument canceled")
                 (ss/return-from-dialog dia nil)))
    
    (ss/listen jb-add :action 
               (fn [_]
                 (let [s (.parent chanobj)
                       ci (.channel-number chanobj)
                       id (.get-value vtf-instrument-id)
                       vc (int (.getValue spin-voice-count))
                       mbus (int (.getValue spin-mainbus))
                       args (let [acc* (atom [s ci (keyword id)
                                              :voice-count vc
                                              :main-out mbus])]
                              (doseq [cc (keys @cc-spinners*)]
                                (let [spin (get @cc-spinners* (keyword cc))
                                      val (int (.getValue spin))]
                                  (swap! acc* (fn [n](conj n cc)))
                                  (swap! acc* (fn [n](conj n val)))))
                              @acc*)]
                   (if (.is-valid? vtf-instrument-id)
                     (let [kmode @selected-mode*]
                       (.create descriptor kmode args)
                       (.sync-ui! (.get-editor s))
                       (.status! (.get-editor chanobj)
                                 (format "Added %s %s id = %s" kmode iname id))
                       (ss/return-from-dialog dia true))
                     (do
                       (Thread/sleep 750))) )))
    (ss/show! dia)))
                       



;; Provides buttons for available instruments
;;
(defn- add-performance-panel [chanobj]
  (let [buttons (let [acc* (atom [])]
                  (doseq [id (cadejo.config/instruments)]
                    (let [d (cadejo.config/instrument-descriptor id)
                          logo (.logo d :medium)
                          ttt (format "Add %s - %s" (.instrument-name d)(.about d))
                          jb (ss/button :icon logo)]
                      (.putClientProperty jb :instrument-name id)
                      (.putClientProperty jb :descriptor d)
                      (.setToolTipText jb ttt)
                      (swap! acc* (fn [n](conj n jb)))
                      (ss/listen jb :action
                                 (fn [ev]
                                   (let [src (.getSource ev)
                                         descriptor (.getClientProperty src :descriptor)]
                                     (performance-options-dialog chanobj descriptor))))))
                  @acc*)
        break 6
        tbar1 (ss/toolbar :floatable? false :items (take break buttons))
        tbar2 (ss/toolbar :floatable? false :items (nthrest buttons break))
        pan-main (ss/grid-panel :rows 2 :items [tbar1 tbar2])]
    pan-main))


(defprotocol ChannelEditor

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

  (show-scene
   [this])
 
  (sync-ui!
    [this]))

(defn channel-editor [chanobj]
  (let [basic-ed (cadejo.ui.midi.node-editor/basic-node-editor :channel chanobj)
        pan-center (.widget basic-ed :pan-center)
        tbar-performance (ss/toolbar :floatable? true)
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)
        pan-add-performance (add-performance-panel chanobj)
        pan-tabs (ss/tabbed-panel :tabs [{:title (if (cadejo.config/enable-button-text) "Add Instruments" "")
                                          :icon (if (cadejo.config/enable-button-icons)(lnf/read-icon :general :instrument) nil)
                                          :content pan-add-performance}
                                         {:title (if (cadejo.config/enable-button-text) "MIDI" "")
                                          :icon (if (cadejo.config/enable-button-icons)(lnf/read-icon :midi :plug) nil)
                                          :content (.widget properties-editor :pan-main)}])]

    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ced (reify ChannelEditor
                
                (widgets [this] (.widgets basic-ed))

                (widget [this key]
                  (or (.widget basic-ed key)
                      (umsg/warning (format "ChannelEditor does not have %s widget" key))))

                (node [this] (.node basic-ed))

                (status! [this msg]
                  (.status! basic-ed msg))

                (warning! [this msg]
                  (.warning! basic-ed msg))

                (frame [this]
                  (.widget this :frame))

                (show-scene [this]
                  (let [scene (.parent chanobj)
                        sed (.get-editor scene)
                        sframe (.frame sed)]
                    (ss/show! sframe)
                    (.toFront sframe)))

                (sync-ui! [this]
                  (.removeAll tbar-performance)
                  (.add tbar-performance (let [lab (ss/label :text (format " %2d " (.channel-number chanobj))
                                                             :border (factory/line))]
                                           lab))
                  (doseq [p (.children chanobj)]
                    (let [itype (.get-property p :instrument-type)
                          id (.get-property p :id)
                          logo (.logo p :small)
                          jb (ss/button :icon logo)]
                      (ss/listen jb :action (fn [ev]
                                              (let [src (.getSource ev)
                                                    mods (.getModifiers ev)
                                                    performance (.getClientProperty jb :performance)
                                                    ped (.get-editor performance)
                                                    pframe (.widget ped :frame)
                                                    chaned (.get-editor chanobj)
                                                    sed (.get-editor (.get-scene performance))
                                                    id (.getClientProperty src :id)]
                                                (cond (= mods 17) ; shift+click remove performance
                                                      (let [ped (.get-editor performance)] 
                                                        (.remove-performance! chanobj id)
                                                        (.setVisible pframe false)
                                                        (.dispose pframe)
                                                        (.sync-ui! sed)
                                                        (.status! chaned (format "Performance %s removed" id)))

                                                      :default ; hide/show performance editor
                                                      (if (.isVisible pframe)
                                                        (.setVisible pframe false)
                                                        (do 
                                                          (.setVisible pframe true)
                                                          (.toFront pframe)))))))
                      (.putClientProperty jb :instrument-type itype)
                      (.putClientProperty jb :id id)
                      (.putClientProperty jb :performance p)
                      (.setToolTipText jb (format "%s id = %s" (name itype)(name id)))
                      (.add tbar-performance jb)
                      (.sync-ui! (.get-editor p))
                      ))

                  (.sync-ui! properties-editor)
                  (.revalidate (.widget basic-ed :frame))) )]
      (ss/listen (.widget ced :jb-parent)
                 :action (fn [_]
                           (let [scene (.parent chanobj)
                                 sed (.get-editor scene)
                                 sframe (.frame sed)]
                             (ss/show! sframe)
                             (.toFront sframe))))
      (.set-parent-editor! properties-editor ced)
      (.add pan-center pan-tabs BorderLayout/CENTER)
      (.add pan-center tbar-performance BorderLayout/SOUTH)
      (ss/config! (.frame ced) :size frame-size)
      (.addWindowListener (.widget ced :frame)
                          (proxy [WindowListener][]
                            (windowClosed [_] nil)
                            (windowClosing [_] nil)
                            (windowDeactivated [_] nil)
                            (windowIconified [_] nil)
                            (windowDeiconified [_] (.sync-ui! ced))
                            (windowActivated [_] 
                              (.sync-ui! ced))
                            (windowOpened [_] nil)))
      (.info-text! basic-ed (let [scene (.parent chanobj)
                                  sid (.get-property scene :id)
                                  cid (.get-property chanobj :id)]
                              (format "Scene %s Channel %s" sid cid)))
      (.putClientProperty (.widget basic-ed :jb-help) :topic :channel)
      ced)))
