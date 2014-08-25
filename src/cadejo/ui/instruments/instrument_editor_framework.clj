(ns cadejo.ui.instruments.instrument-editor-framework
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [cadejo.ui.util.undo-stack])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser])
  (:require [overtone.core :as ot])
  (:import java.io.FileNotFoundException
           java.io.File
           javax.swing.JFileChooser
           javax.swing.Box
           java.awt.event.FocusListener))

(defn- third [col] (nth col 2))

(def all-file-filter (seesaw.chooser/file-filter
                      "All Files"
                      (fn [_] true)))


(defprotocol InstrumentEditorFramework

  (widgets
    [this])

  (widget
    [this key])

  (status! 
    [this msg])

  (warning!
    [this msg])

  (push-undo-state! 
    [this msg])

  (push-redo-state!
    [this msg])

  (add-tab! 
    [this description content])
  
  (client-editor!
    [this ced])
  
  (client-editor
    [this])

  (sync-ui!
    [this]) )

(defn- save-program [ied jfc dform ext program data]
  (let [file (.getSelectedFile jfc)
        filename (path/replace-extension 
                  (.getAbsolutePath file) ext)
        pan-main (.widget ied :pan-main)
        pout (assoc program 
               :args (ucol/map->alist data)
               :file-type :cadejo-program
               :data-format dform)]
    (if (cadejo.ui.util.overwrite-warning/overwrite-warning pan-main "Program" filename)
      (try
        (spit filename (pr-str pout))
        (.status! ied (format "Saved '%s'" filename))
        filename
        (catch java.io.FileNotFoundException e
          (umsg/warning "FileNotFoundException"
                        "instrument-editor/save-program"
                        (str filename))
          (.warning! ied "Can not write file '%s'" filename)
          nil))
      (do 
        (.status! ied "Save Canceled")
        nil))))

(defn- open-program [ied jfc dform ext]
  (let [file (.getSelectedFile jfc)
        filename (path/replace-extension 
                  (.getAbsolutePath file) ext)
        rec (try
              (read-string (slurp filename))
              (catch FileNotFoundException e
                nil))]
    (if (and rec 
             (= (:file-type rec) :cadejo-program)
             (= (:data-format rec) dform))
      (dissoc rec :file-type :data-format)
      nil)))


(defn instrument-editor-framework [performance]
  (let [client-editor* (atom nil)
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo")
        current-directory* (atom (cadejo.config/config-path))
        bank (.bank performance)
        pnum (or (.current-program-number bank) 0)
        original-program (.get-program bank pnum)
        program* (atom original-program)
        original-data (ucol/alist->map (:args @program*))
        data* (atom original-data)
        itype (.get-property performance :instrument-type)
        id (.get-property performance :id)
        descriptor (cadejo.config/instrument-descriptor itype)
        file-extension (format "%s_program" (.toLowerCase (name (.data-format bank))))
        program-file-filter (seesaw.chooser/file-filter
                     (format "%s Program File" (name itype))
                     (fn [f] 
                       (path/has-extension? (.getAbsolutePath f) file-extension)))
        ;; North toolbar
        jb-open (ss/button :text "Open")
        jb-save (ss/button :text "Save")
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-transmit (ss/button :text "Transmit")
        jb-help (ss/button :text "Help")

        tbar1 (ss/grid-panel :rows 1
                             :items [jb-open jb-save jb-undo jb-redo jb-transmit jb-help]
                             :border (factory/padding))
        ;; Common tab (init revert store name remarks)
        jb-revert (ss/button :text "Revert")
        jb-init (ss/button :text "Init")
        jb-store (ss/button :text "Store")
        jb-cancel (ss/button :text "Cancel")
        spin-program (ss/spinner :model (ss/spinner-model pnum :from 0 :to 119 :by 1))
        txt-name (ss/text :multi-line? false
                          :text (name (:name @program*)))
        txt-remarks (ss/text :multi-line? true
                             :text (str (:remarks @program*)))
        pan-common (let [pan-name (ss/vertical-panel :items [txt-name]
                                                     :border (factory/title "Name"))
                         pan-remarks (ss/vertical-panel :items [(ss/scrollable txt-remarks)]
                                                        :border (factory/title "Remarks"))
                         pan-south (ss/grid-panel :rows 1
                                                  :items [jb-revert jb-init 
                                                          (Box/createHorizontalStrut 1)
                                                          spin-program jb-store jb-cancel]
                                                  :border (factory/padding))]
                     (ss/border-panel :north pan-name
                                      :center pan-remarks
                                      :south pan-south))
        pan-tabs (ss/tabbed-panel
                  :tabs [{:title "Common" :content pan-common}]
                  :border (factory/padding))

        lab-status (ss/label :text ""
                             :border (factory/bevel))
        lab-id (ss/label :text (format "%s program [%3d]" (name id) pnum)
                         :border (factory/bevel))
        lab-name (ss/label :text (format "name '%s'" (name (:name @program*)))
                           :border (factory/bevel))
        pan-south (ss/grid-panel :rows 1
                                 :items [lab-status lab-name lab-id]
                                 :border (factory/padding))
        pan-main (ss/border-panel
                  :north tbar1
                  :center pan-tabs
                  :south pan-south)
        frame (ss/frame :title (format "%s" (name id))
                        :content pan-main
                        :on-close :nothing
                        :size [600 :by 300]
                        :icon (.logo descriptor :tiny))
        widget-map {:jb-help jb-help
                    :lab-name lab-name
                    :pan-main pan-main
                    :frame frame}
        ied (reify InstrumentEditorFramework
              
              (widgets [this] widget-map)

              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))
              
              (status! [this msg]
                (ss/config! lab-status :text (str msg)))

              (warning! [this msg]
                (ss/config! lab-status :text (format "WARNING: %s" msg)))

              (push-undo-state! [this msg]
                (.push-state! undo-stack [msg (dissoc @program* :args) @data*]))

              (push-redo-state! [this msg]
                (.push-state! redo-stack [msg (dissoc @program* :args) @data*]))

              (add-tab! [this title content]
                (.addTab pan-tabs title content))

              (client-editor! [this ced]
                (reset! client-editor* ced)
                (.set-parent! ced this))

              (client-editor [this]
                @client-editor*)

              (sync-ui! [this]
                (ss/config! txt-name :text (str (:name @program*)))
                (ss/config! txt-remarks :text (str (:remarks @program*)))
                (ss/config! lab-name :text (format "Name %s" (:name @program*)))
                (if @client-editor*
                  (.sync-ui! @client-editor*)))
               )]

        (.addFocusListener txt-name (proxy [FocusListener][]
                                      (focusGained [_])
                                      (focusLost [_]
                                        (let [progname (ss/config txt-name :text)]
                                          (.push-undo-state! ied "Name Change")
                                          (swap! program* (fn [n](assoc n :name progname)))
                                          (.status! ied "Name Change")
                                          (ss/config! lab-name :text (format "name '%s'" progname))))))

        (.addFocusListener txt-remarks (proxy [FocusListener][]
                                         (focusGained [_])
                                         (focusLost [_]
                                           (let [remtext (ss/config txt-remarks :text)]
                                             (.push-undo-state! ied "Remarks Change")
                                             (swap! program* (fn [n](assoc n :remarks remtext)))
                                             (.status! ied "Remarks Change")))))

        (ss/listen jb-save :action
                   (fn [_]
                     (let [pname (ss/config txt-name :text)
                           default-file (path/append-extension pname file-extension)
                           jfc (JFileChooser. @current-directory*)]
                       (.setDialogTitle jfc (format "Save %s Program" (name itype)))
                       (.addChoosableFileFilter jfc all-file-filter)
                       (.addChoosableFileFilter jfc program-file-filter)
                       (.setFileFilter jfc all-file-filter)
                       (.setSelectedFile jfc (File. default-file))
                       (let [rs (.showSaveDialog jfc (.widget ied :pan-main))]
                         (cond (= rs JFileChooser/CANCEL_OPTION)
                               (.status! ied "Save Canceled")
                               
                               (= rs JFileChooser/APPROVE_OPTION)
                               (let [rs (save-program ied jfc itype file-extension @program* @data*)]
                                 (if rs
                                   (reset! current-directory*
                                           (apply path/join (butlast (path/split rs))))))
                               
                               :default ;; should only see on error
                               (do
                                 (umsg/warning "InstrumentEditor jb-save action"
                                               "default cond executed")
                                 (.warning! ied "Unknown error")))) )))
        
        (ss/listen jb-open :action
                   (fn [_]
                     (let [jfc (JFileChooser. @current-directory*)]
                       (.setDialogTitle jfc (format "Open %s Program" (name itype)))
                       (.addChoosableFileFilter jfc all-file-filter)
                       (.addChoosableFileFilter jfc program-file-filter)
                       (.setFileFilter jfc all-file-filter)
                       (let [rs (.showOpenDialog jfc (.widget ied :pan-main))]
                         (cond (= rs JFileChooser/CANCEL_OPTION)
                               (.status! ied "Open Canceled")
                               
                               (= rs JFileChooser/APPROVE_OPTION)
                               (let [prog (open-program ied jfc itype file-extension)]
                                 (if prog
                                   (do 
                                     (.push-undo-state! ied "Open")
                                     (reset! program* prog)
                                     (reset! data* (ucol/alist->map (:args prog)))
                                     (.sync-ui! ied)
                                     (.status! ied (format "Opened %s" (name (:name prog)))))
                                   (do
                                     (umsg/warning "Can not read '%s' as %s program file"
                                                   (.getSelectedFile jfc) itype)
                                     (.warning! ied "Can not read program file"))))
                               
                               :default ;; should never see this
                               (do
                                 (umsg/warning "InstrumentEditor jb-save action"
                                               "default cond executed")
                                 (.warning! ied "Unknown error")))) )))

        (ss/listen jb-undo :action
                   (fn [_]
                     (let [udframe (.pop-state! undo-stack)]
                       (.push-redo-state! ied (first udframe))
                       (reset! program* (second udframe))
                       (reset! data* (third udframe))
                       (.sync-ui! ied)
                       (.status! ied (format "Undo %s" (first udframe))))))

        (ss/listen jb-redo :action
                   (fn [_]
                     (let [udframe (.pop-state! redo-stack)]
                       (.push-redo-state! ied (first udframe))
                       (reset! program* (second udframe))
                       (reset! data* (third udframe))
                       (.sync-ui! ied)
                       (.status! ied (format "Redo %s" (first udframe))))))

        (ss/listen jb-transmit :action
                   (fn [_]
                     (let [s (.synths performance)
                           d (ucol/map->alist @data*)
                           pp (.pp-hook bank)]
                       (apply ot/ctl (cons s d))
                       (.sync-ui! ied)
                       (.status! ied "Transmit")
                       (if pp
                         (let [pname (str (:name @program*))
                               rem (str (:remarks @program*))
                               data (ucol/map->alist @data*)]
                           (println (pp pnum pname data rem)))))))

        (ss/listen jb-revert :action
                   (fn [_]
                     (.push-undo-state! ied "Revert")
                     (reset! program* original-program)
                     (reset! data* original-data)
                     (.sync-ui! ied)
                     (.status! ied "Revert to original")))

        (ss/listen jb-store :action
                   (fn [_]
                     (let [banked (.get-property performance :bank-editor)
                           pnum (int (.getValue spin-program))
                           prog (assoc @program*
                                  :args (ucol/map->alist @data*))]
                       (.push-undo-state! banked (format "Store program %s" pnum))
                       (.set-program! bank pnum prog)
                       (ss/hide! frame)
                       (.enable! banked true)
                       (.sync-ui! banked)
                       (.status! banked (format "Stored program %s" pnum)) )))

        (ss/listen jb-cancel :action
                   (fn [_]
                     (let [banked (.get-property performance :bank-editor)
                           c-name (:name @program*)
                           c-remarks (:remarks @program*)
                           c-data @data*
                           o-name (:name original-program)
                           o-remarks (:remarks original-program)
                           o-data original-data
                           close (fn []
                                   (ss/hide! frame)
                                   (.enable! banked true)
                                   (.status! banked "Store Program Canceled"))]
                       (if (and (= c-name o-name)
                                (= c-remarks o-remarks)
                                (= c-data o-data))
                         (close)
                         (if (cadejo.config/warn-on-unsaved-data)
                           (let [msg "Unsaved program data will be lost. Continue?"
                                 yes-fn (fn [d]
                                          (close)
                                          (ss/return-from-dialog d true))
                                 no-fn (fn [d]
                                         (ss/return-from-dialog d false))
                                 dia (ss/dialog
                                      :content msg
                                      :option-type :yes-no
                                      :type :warning
                                      :default-option :no
                                      :modal? true
                                      :parent (.widget ied :pan-main)
                                      :success-fn yes-fn
                                      :no-fn no-fn)]
                             (ss/pack! dia)
                             (ss/show! dia)))) )))
                                      
        ied))
