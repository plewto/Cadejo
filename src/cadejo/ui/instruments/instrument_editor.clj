(ns cadejo.ui.instruments.instrument-editor
  (:require [cadejo.config])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.undo-stack])
  (:require [cadejo.util.user-message :as umsg])


  (:use [cadejo.ui.util.overwrite-warning :only [overwrite-warning]])
  
  (:require [overtone.core :as ot])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser :as ssc])
  (:import 
   java.awt.event.FocusListener
   java.io.File
   java.io.FileNotFoundException
   javax.swing.Box
   javax.swing.JFileChooser))

(defn- third [col](nth col 2))

(def all-file-filter (ssc/file-filter
                      "All Files" (constantly true)))

(defn- save-program [ied jfc dform ext program data]
  (let [file (.getSelectedFile jfc)
        filename (path/replace-extension 
                  (.getAbsolutePath file) ext) 
        pan-main (.widget ied :pan-main)
        pout (assoc program 
               :args (ucol/map->alist data)
               :file-type :cadejo-program
               :data-format dform)]
    (if (overwrite-warning pan-main "Program" filename)
      (try
        (spit filename (pr-str pout))
        (.status! ied (format "Saved '%s'" filename))
        filename
        (catch FileNotFoundException e
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



(defprotocol InstrumentEditor

  (parent-performance 
    [this])

  (parent-bank 
    [this])

  (add-sub-editor!
    [this label subed])

  (data 
    [this]
    "Return current data as map")

  (program! 
    [this pnum prog])

  (data!
    [this pnum dmap save-undo]
    [this pnum dmap]
    "Set current data to dmap
    - call sync-ui!
    - do not update synths")

  (program->clipboard!
    [this])

  (clipboard->program!
    [this])
  
  (set-value!
    [this param value save-undo]
    "Set data parameter param to value
     - do not call sync-ui
     - *do* update active-synths")

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

  (pp
    [this]
    "Apply bank's pp-hook to current data")

  (sync-synths
    [this]
    "Synchronize active synth parameters to match current state of editor")

  (sync-ui!
    [this])
  ) ;; end InstrumentEditor


(defn instrument-editor [performance]
  (let [itype (.get-property performance :instrument-type)
        id (.get-property performance :id)
        descriptor (cadejo.config/instrument-descriptor itype)
        clipboard* (.clipboard descriptor)
        current-directory* (atom (cadejo.config/config-path))
        bank (.bank performance)
        file-extension (format "%s_program" 
                               (.toLowerCase (name (.data-format bank))))
        program-file-filter (ssc/file-filter
                             (format "%s Program Files" (name itype))
                             (fn [f]
                               (path/has-extension? (.getAbsolutePath f)
                                                    file-extension)))
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo")
        current-program-number (fn []
                                 (or (.current-program-number bank) 0))
        restore-program (.get-program bank (current-program-number))
        restore-data (ucol/alist->map (:args restore-program))
        program* (atom restore-program)
        data* (atom restore-data)
        sub-editors* (atom [])

        ;; North toolbar
        ;;   lab-id  show-parent open save copy paste undo redo help
        ;;
        lab-id (ss/label :text (name id)
                         :border (factory/bevel))
        jb-show-parent (ss/button :text "Parent")
        jb-open (ss/button :text "Open")
        jb-save (ss/button :text "Save")
        jb-copy (ss/button :text "Copy")
        jb-paste (ss/button :text "Paste")
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-transmit (ss/button :text "Transmit")
        jb-help (ss/button :text "Help")
        pan-north (ss/grid-panel :rows 1 ; :columns 5
                                 :items [lab-id jb-show-parent jb-open jb-save
                                         jb-copy jb-paste jb-undo jb-redo
                                         jb-help]
                                 :border (factory/padding))
        ;; South 1 toolbar
        ;;   transmit init revert spacer spin-store store 
        jb-init (ss/button :text "Init")
        jb-revert (ss/button :text "Revert")
        spin-prognum (ss/spinner :model (ss/spinner-model 0 :min 0 :max 119 :by 1))
        jb-store (ss/button :text "Store")
        pan-south1 (ss/grid-panel :rows 1
                                  :items [jb-transmit jb-init jb-revert
                                          (Box/createHorizontalStrut 1)
                                          (ss/vertical-panel :items [spin-prognum]
                                                             :border (factory/padding))
                                          jb-store])
        ;; South 2 info
        ;;    lab-status lab-name
        lab-status (ss/label :text "<status>")
        lab-name (ss/label :text "<name>")
        pan-south2 (ss/grid-panel :rows 1
                                  :items [(ss/vertical-panel :items [lab-status]
                                                             :border (factory/bevel))
                                          (ss/vertical-panel :items [lab-name]
                                                             :border (factory/bevel))])

        pan-south (ss/vertical-panel :items [pan-south1 pan-south2]
                                     :border (factory/padding))

        ;; Common tab
        ;;    program name
        ;;    program remarks
        ;;    data update when component looses focus
        txt-name (ss/text :multi-line? :false
                          :text "<name>")
        txt-remarks (ss/text :multi-line? true
                             :text "<remarks>")
        pan-common (let [pan-name (ss/vertical-panel :items [txt-name]
                                                     :border (factory/title "Name"))
                         pan-remarks (ss/vertical-panel :items [(ss/scrollable txt-remarks)]
                                                        :border (factory/title "Remarks"))]
                     (ss/border-panel :north pan-name
                                      :center pan-remarks
                                      :border (factory/padding)))

        pan-tabs (ss/tabbed-panel 
                  :tabs [{:title "Common" :content pan-common}]
                  :border (factory/padding))

        pan-main (ss/border-panel
                  :north pan-north
                  :center pan-tabs
                  :south pan-south
                  )

        frame (ss/frame :title (format "%s Editor" (name id))
                        :content pan-main
                        :on-close :hide
                        :size [1050 :by 440]
                        :icon (.logo descriptor :tiny))

        widget-map {
                    :lab-name lab-name
                    :frame frame
                    :pan-main pan-main
                    }

        ied (reify InstrumentEditor
                         
              (parent-performance [this] performance)

              (parent-bank [this]
                (.bank performance))

              (add-sub-editor! [this label subed]
                (.addTab pan-tabs label (.widget subed :pan-main))
                (swap! sub-editors* (fn [n](conj n subed)))
                (.parent! subed this))

              (program! [this pnum prog]
                (.push-undo-state! this "Program change")
                (.setValue spin-prognum pnum)
                (reset! program* (dissoc prog :args))
                (reset! data* (ucol/alist->map (:args prog)))
                (.sync-ui! this))

              (data [this] @data*)

              (data! [this pnum dmap save-undo]
                (if save-undo (.push-undo-state! this "Set Data"))
                (.setValue spin-prognum (int pnum))
                (reset! data* dmap)
                (.sync-synths this)
                (.status! this "Data Changed"))
                
              (data! [this pnum dmap]
                (.data! this pnum dmap true))

              (program->clipboard! [this]
                (reset! clipboard* 
                        (swap! program* 
                               (fn [n](assoc n :args (ucol/map->alist @data*)))))
                (.setEnabled jb-paste true)
                (.status! this (format "Program '%s' copied to clipboard" (ss/config txt-name :text))))

              (clipboard->program! [this]
                (let [prog @clipboard*]
                  (if prog 
                    (let [data-alist (:args prog)
                          data-map (ucol/alist->map data-alist)
                          pname (:name prog)
                          premarks (:remarks prog)]
                      (.push-undo-state! this "Paste Program")
                      (reset! program* prog)
                      (reset! data* data-map)
                      (.sync-synths this)
                      (.sync-ui! this)
                      (.pp this)
                      (.status! this (format "Program '%s' pasted from clipboard" pname)))
                    (.warning! this "Clipboard empty"))))

              (set-value! [this param value save-undo]
                (if save-undo (.push-undo-state! this))
                (swap! data* (fn [n](assoc n param value)))
                (.ctl performance param value))

              (widgets [this] widget-map)
              
              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))

              (status! [this msg]
                (ss/config! lab-status :text msg))

              (warning! [this msg]
                (ss/config! lab-status :text (format "WARNING: %s" msg)))

              (push-undo-state! [this msg]
                (.push-state! undo-stack [msg (dissoc @program* :args) @data*]))

              (push-redo-state! [this msg]
                (.push-state! redo-stack [msg (dissoc @program* :args) @data*]))

              (pp [this]
                (let [pname (:name @program*)
                      rem (:remarks @program*)
                      pnum (int (.getValue spin-prognum))
                      d (ucol/map->alist @data*)
                      ppf (.pp-hook bank)]
                  (if ppf
                    (println (ppf pnum pname d rem)))))

              (sync-synths [this]
                (let [d (ucol/map->alist @data*)
                      sv (concat (.synths performance)(.voices performance))]
                  (apply ot/ctl (cons sv d))))

              (sync-ui! [this]
                (let [pnum (min 119 (or (.current-program-number bank) 0))
                      pname (str (:name @program*))
                      prem (str (:remarks @program*))]
                  (reset! data* (ucol/alist->map (.current-program-data bank)))
                  (.setValue spin-prognum pnum)
                  (ss/config! txt-name :text pname)
                  (ss/config! lab-name :text pname)
                  (ss/config! txt-remarks :text prem)
                  (doseq [t @sub-editors*]
                    (.sync-ui! t))))) ]
    (.addFocusListener txt-name (proxy [FocusListener][]
                                  (focusGained [_])
                                  (focusLost [_]
                                    (let [pname (ss/config txt-name :text)]
                                      (.push-undo-state! ied "Name Change")
                                      (ss/config! txt-name :text pname)
                                      (swap! program* (fn [n](assoc n :name pname)))))))

    (.addFocusListener txt-remarks (proxy [FocusListener][]
                                     (focusGained [_])
                                     (focusLost [_]
                                       (let [prem (ss/config txt-remarks :text)]
                                         (.push-undo-state! ied "Remarks Change")
                                         (swap! program* (fn [n](assoc n :remarks prem)))))))

    (ss/listen jb-show-parent :action 
               (fn [_]
                 (let [ped (.get-editor performance)
                       f (.widget ped :frame)]
                   (.setVisible f true)
                   (.toFront f))))

    (ss/listen jb-open :action
               (fn [_]
                 (let [jfc (JFileChooser. @current-directory*)]
                   (.setDialogTitle jfc (format "Open %s Program" (name itype)))
                   (.addChoosableFileFilter jfc all-file-filter)
                   (.addChoosableFileFilter jfc program-file-filter)
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
                                 (.sync-synths ied)
                                 (.sync-ui! ied)
                                 (.pp ied)
                                 (.status! ied (format "Opened %s" (name (:name prog)))))
                               (do
                                 (umsg/warning "Can not read '%s' as %s program file"
                                               (.getSelectedFile jfc) itype)
                                 (.warning! ied "Can not read program file"))))
                           
                           :default ;; should never see this
                           (do
                             (umsg/warning "InstrumentEditor jb-save action"
                                           "default cond executed")
                             (.warning! ied "Unknown open error")))) )))
    
    (ss/listen jb-save :action
               (fn [_]
                 (let [pname (ss/config txt-name :text)
                       default-file (path/append-extension pname file-extension)
                       jfc (JFileChooser. @current-directory*)]
                   (.setDialogTitle jfc (format "Save %s Program" (name itype)))
                   (.addChoosableFileFilter jfc all-file-filter)
                   (.addChoosableFileFilter jfc program-file-filter)
                   (.setSelectedFile jfc (File. default-file))
                   (let [rs (.showSaveDialog jfc (.widget ied :pan-main))]
                     (cond (= rs JFileChooser/CANCEL_OPTION)
                           (.status! ied "Save Canceled")

                           (= rs JFileChooser/APPROVE_OPTION)
                           (let [rs (save-program ied jfc itype file-extension @program* @data*)]
                             (if rs
                               (reset! current-directory*
                                       (apply path/join (butlast (path/split rs))))))
                           
                           :default ;; Should only see this on error
                           (do
                             (umsg/warning "InstrumentEditor jb-save action"
                                           "default cond executed")
                             (.warning! ied "Unknown save error")))))))

    (ss/listen jb-copy :action
               (fn [_]
                 (program->clipboard! ied)))
                 
    (ss/listen jb-paste :action
               (fn [_]
                 (clipboard->program! ied)))

    (ss/listen jb-undo :action
               (fn [_]
                 (let [udframe (.pop-state! undo-stack)]
                   (.push-redo-state! ied (first udframe))
                   (reset! program* (second udframe))
                   (reset! data* (third udframe))
                   (.sync-synths ied)
                   (.sync-ui! ied)
                   (.pp ied)
                   (.status! ied (format "Undo %s" (first udframe))))))
                
    (ss/listen jb-redo :action
               (fn [_]
                 (let [udframe (.pop-state! redo-stack)]
                   (.push-undo-state! ied (first udframe))
                   (reset! program* (second udframe))
                   (reset! data* (third udframe))
                   (.sync-synths ied)
                   (.sync-ui! ied)
                   (.pp ied)
                   (.status! ied (format "Redo %s" (first udframe))))))

    (ss/listen jb-transmit :action
               (fn [_]
                 (let [d (ucol/map->alist @data*)
                       pp (.pp-hook bank)]
                   (.sync-synths ied)
                   (.pp ied)
                   (.status! ied "Transmit"))))
    
    ;; (ss/listen jb-transmit :action
    ;;            (fn [_]
    ;;              (let [pnum (.current-program-number bank)]
    ;;                (.program-change bank pnum)
    ;;                (.sync-ui! ied)
    ;;                (.status! ied (format "Transmit %03d" pnum)))))

    (ss/listen jb-init :action
               (fn [_]
                 (.push-undo-state! ied "Init Program")
                 (reset! program* (.initial-program descriptor))
                 (reset! data* (ucol/alist->map (:args @program*)))
                 (.sync-synths ied)
                 (.sync-ui! ied)
                 (.pp ied)
                 (.status! ied "Init Program")))

    (ss/listen jb-revert :action
               (fn [_]
                 (.push-undo-state! ied "Revert")
                 (reset! program* restore-program)
                 (reset! data* restore-data)
                 (.sync-synths ied)
                 (.sync-ui! ied)
                 (.pp ied)
                 (.status! ied "Revert")))
                 
    (ss/listen jb-store :action
               (fn [_]
                 (let [bank-ed (.editor bank)
                       pnum (int (.getValue spin-prognum))
                       msg (format "Store Program %s" pnum)
                       prog (assoc @program* :args (ucol/map->alist @data*))]
                   (.push-undo-state! bank-ed msg)
                   (.set-program! bank pnum prog)
                   (.program-change bank pnum)
                   (.sync-ui! bank-ed)
                   (.status! bank-ed msg)
                   (.status! ied msg))))
                 
                       
                       

    ;; START DEBUG
    (ss/listen jb-help :action 
               (fn [_]
                 (println (format "Frame size      %s" (.getSize frame)))
                 (println (format "pan-common size %s" (.getSize pan-common)))
                 ))
    ;; END DEBUG


    (.clear-stack! undo-stack)

    ied)
) 
