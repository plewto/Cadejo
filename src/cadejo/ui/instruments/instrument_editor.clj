(ns cadejo.ui.instruments.instrument-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.user-message :as umsg])
  (:use [cadejo.ui.util.overwrite-warning :only [overwrite-warning]])
  (:require [overtone.core :as ot])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser :as ssc])
  (:require [seesaw.font :as ssfont])
  (:import 
   java.awt.event.FocusListener
   java.io.File
   java.io.FileNotFoundException
   javax.swing.Box
   javax.swing.JFileChooser))


(def ^:private max-program-number
  (dec cadejo.midi.program-bank/start-reserved))

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



(defn- program-name-editor [parent-editor]
  (let [txt-name (ss/text 
                  :text "<name>"
                  :multi-line? :false)
        txt-remarks (ss/text
                     :text "<remarks"
                     :multi-line? :true)
        pan-main (ss/border-panel 
                  :north (ss/vertical-panel 
                          :items [txt-name]
                          :border (factory/title "Program Name"))
                  :center (ss/vertical-panel 
                           :items [(ss/scrollable txt-remarks)]
                           :border (factory/title "Program Remarks")))
        widget-map {:txt-name txt-name
                    :txt-remarks txt-remarks
                    :pan-main pan-main}
        pne (reify cadejo.ui.instruments.subedit/InstrumentSubEditor

              (widgets [this] widget-map)

              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "program-name-editor does not have %s widget" key))))

              (parent [this] parent-editor)

              (parent! [this other]  nil) ;; ignore

              (status! [this msg]
                (.status! parent-editor msg))

              (warning! [this msg]
                (.warning! parent-editor msg))

              (set-param! [this param value] nil) ;; ignore

              (sync-ui! [this]
                (let [bank (.parent-bank parent-editor)
                      prog (.current-program bank)
                      name (str (:name prog))
                      remarks (str (:remarks prog))]
                  (ss/config! txt-name :text name)
                  (ss/config! txt-remarks :text remarks))))]
    
    (.addFocusListener txt-name 
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 pname (ss/config txt-name :text)
                                 prog (assoc (.current-program bank)
                                        :name pname)]
                             (.current-program! bank prog)
                             (ss/config! (.widget parent-editor :lab-name)
                                         :text pname)
                             (.status! parent-editor 
                                       "Program name changed")))))
    (.addFocusListener txt-remarks
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 remarks (ss/config txt-remarks :text)
                                 prog (assoc (.current-program bank)
                                        :remarks remarks)]
                             (.current-program! bank prog)
                             (.status! parent-editor
                                       "Program remarks changed")))))
    pne))
                

(defprotocol InstrumentEditor

  (parent-performance
    [this])

  (parent-bank
    [this])

  (add-sub-editor!
    [this label subed]
    [this lable icon subed])

  (current-program 
    [this]
    "Returns current bank program")

  (current-data
    [this]
    "Returns current bank data")

  (program->clipboard
    [this]
    "Stores current program into clipboard")

  (clipboard->program!
    [this]
    "Sets clipboard contents as bank current program")

  (set-param!
    [this param value])

  (widgets
    [this])

  (widget
    [this key])

  (status!
    [this msg])

  (warning! 
    [this msg])

  (pp
    [this]
    "Apply bank pp-hook to current data, display results")

  (set-store-location!
    [this pnum]
    "Sets the value of the store-program spinner")
  
  (sync-ui!
    [this]))

(def id-font-size 24)

(defn instrument-editor [performance]
  (let [itype (.get-property performance :instrument-type)
        id (.get-property performance :id)
        descriptor (config/instrument-descriptor itype)
        clipboard* (.clipboard descriptor)
        current-directory* (atom (config/config-path))
        bank (.bank performance)
        file-extension (format "%s_program"
                               (.toLowerCase (name (.data-format bank))))
        program-file-filter (ssc/file-filter
                             (format "%s Program File" (name itype))
                             (fn [f]
                               (path/has-extension? (.getAbsolutePath f)
                                                    file-extension)))
        sub-editors* (atom [])

        ;; North toolbar
        ;;   lab-id show-parent open save copy paste help
        ;;
        lab-id (ss/label :text (name id)
                         :font (ssfont/font :size id-font-size)
                         :border (factory/bevel))
        jb-show-parent (ss/button)
        jb-copy (ss/button)
        jb-paste (ss/button :enabled? false)
        jb-open (ss/button)
        jb-save (ss/button)
        jb-help (ss/button)
        pan-north (ss/toolbar :floatable? false
                              :items [lab-id 
                                      :separator jb-show-parent
                                      :separator jb-open jb-save
                                      :separator jb-copy jb-paste
                                      :separator jb-help]
                              :border (factory/padding))

        ;; South toolbar
        ;;    init transmit revert spinner store
        ;;
        jb-init (ss/button)
        jb-store (ss/button)
        spin-program (ss/spinner 
                      :model (ss/spinner-model 0 
                                               :min 0 
                                               :max max-program-number 
                                               :by 1))
        pan-store (ss/horizontal-panel :items [jb-store spin-program]
                                       :border (factory/line))
        pan-south1 (ss/horizontal-panel :items [jb-init 
                                                (Box/createHorizontalStrut 8)
                                                pan-store]
                                        :border (factory/padding))

        lab-status (ss/label :text " ")
        lab-name (ss/label :text " ")
        pan-south2 (ss/grid-panel 
                    :rows 1
                    :items [(ss/vertical-panel :items [lab-status]
                                               :border (factory/bevel))
                            (ss/vertical-panel :items [lab-name]
                                               :border (factory/bevel))])
        pan-south (ss/vertical-panel :items [pan-south1 pan-south2]
                                     :border (factory/padding))
                                     
        pan-tabs (ss/tabbed-panel
                  :border (factory/padding))

        ;; Main panel
        pan-main (ss/border-panel :north pan-north
                                  :center pan-tabs
                                  :south pan-south)
                                 
        frame (ss/frame :title (format "%s Editor" (name id))
                        :content pan-main
                        :on-close :hide
                        :size [1050 :by 650]
                        :icon (.logo descriptor :tiny))
                                  
        widget-map {:lab-name lab-name
                    :pan-main pan-main
                    :pan-tabs pan-tabs
                    :frame frame}
        
        ied (reify InstrumentEditor
              
              (parent-performance [this] performance)
              
              (parent-bank [this]
                (.bank performance))

              (add-sub-editor! [this label subed]
                (swap! sub-editors* (fn [n](conj n subed)))
                (.addTab pan-tabs label (.widget subed :pan-main))
                (.parent! subed this))

              (add-sub-editor! [this label icon subed]
                (swap! sub-editors* (fn [n](conj n subed)))
                (.addTab pan-tabs label icon (.widget subed :pan-main))
                (.parent! subed this))

              (current-program [this]
                (.current-program bank))

              (current-data [this]
                (.current-data bank))

              (program->clipboard [this]
                (let [d (ucol/map->alist (.current-data this))
                      prog (assoc (.current-program this) :args d)]
                  (reset! clipboard* prog)
                  (.setEnabled jb-paste true)
                  (.status! this "Program copied to clipboard")))
              
              (clipboard->program! [this]
                (let [prog @clipboard*]
                  (if prog
                    (do
                      (.current-program! bank prog)
                      (.sync-ui! this)
                      (.status! this "Program pasted from clipboard"))
                    (.warning! this "Clipboard empty"))))

              (set-param! [this param value]
                (.set-param! bank param value))

              (widgets [this] widget-map)

              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))

              (status! [this msg]
                (ss/config! lab-status :text (str msg)))

              (warning! [this msg]
                (ss/config! lab-status :text (format "WARNING: %s" msg)))

              (pp [this]
                (let [prog (.current-program this)]
                  (if (and prog (config/enable-pp))
                    (let [pname (:name prog)
                          rem (:remarks prog)
                          pnum (int (max 0 (min max-program-number (.getValue spin-program))))
                          d (ucol/map->alist (.current-data this))
                          ppf (.pp-hook bank)]
                      (if ppf
                        (println (ppf pnum pname d rem)))))))

              (set-store-location! [this pnum]
                (if (and (>= pnum 0)(<= pnum max-program-number))
                  (.setValue spin-program pnum)))

              (sync-ui! [this]
                (let [prog (.current-program bank)
                      data (.current-data bank)]
                  (ss/config! lab-name :text (str (:name prog))))
                (doseq [s @sub-editors*]
                  (.sync-ui! s))) )

        name-editor (program-name-editor ied)]

    ;(.add-sub-editor! ied "Common" name-editor)
    (.add-sub-editor! ied 
                      (if (config/enable-button-text) "Common" "")
                      (if (config/enable-button-icons)(lnf/read-icon :edit :text) nil)
                      name-editor)
    
    (ss/listen jb-show-parent :action
               (fn [_](let [ped (.get-editor performance)
                            f (.frame ped)]
                        (.setVisible f true)
                        (.toFront f))))
    
    (ss/listen jb-copy :action 
               (fn [_](.program->clipboard ied)))

    (ss/listen jb-paste :action
               (fn [_](.clipboard->program! ied)))

    (ss/listen jb-open :action
               (fn [_]
                 (let [jfc (JFileChooser. @current-directory*)]
                   (.setDialogTitle jfc (format "Open %s Program"
                                                (name itype)))
                   (.addChoosableFileFilter jfc all-file-filter)
                   (.addChoosableFileFilter jfc program-file-filter)
                   (let [rs (.showOpenDialog jfc (.widget ied :pan-main))]
                     (cond (= rs JFileChooser/CANCEL_OPTION)
                           (.status! ied "Open Canceled")
                           
                           (= rs JFileChooser/APPROVE_OPTION)
                           (let [prog (open-program ied jfc itype
                                                    file-extension)]
                             (if prog
                               (do 
                                 (.current-program! bank prog)
                                 (.sync-ui! ied)
                                 (.pp ied)
                                 (.status! ied (format "Opened %s"
                                                       (name (:name prog)))))
                               (do
                                 (umsg/warning
                                  "Can not read '%s' as %s program file"
                                  (.getSelectedFile jfc) itype)
                                 (.warning! ied
                                            "Can not read program file"))))
                           
                           :default ;; should never see this
                           (do
                             (umsg/warning "InstrumentEditor jb-save action"
                                           "default cond executed")
                             (.warning! ied "Unknown open error")))) )))

    (ss/listen jb-save :action
               (fn [_]
                 (let [pname (ss/config (.widget name-editor :txt-name) :text)
                       default-file (path/append-extension pname
                                                           file-extension)
                       jfc (JFileChooser. @current-directory*)]
                   (.setDialogTitle jfc (format "Save %s Program"
                                                (name itype)))
                   (.addChoosableFileFilter jfc all-file-filter)
                   (.addChoosableFileFilter jfc program-file-filter)
                   (.setSelectedFile jfc (File. default-file))
                   (let [rs (.showSaveDialog jfc (.widget ied :pan-main))]
                     (cond (= rs JFileChooser/CANCEL_OPTION)
                           (.status! ied "Save Canceled")
    
                           (= rs JFileChooser/APPROVE_OPTION)
                           (let [rs (save-program ied jfc itype file-extension
                                                  (.current-program bank)
                                                  (.current-data bank))]
                             (if rs
                               (reset! current-directory*
                                       (apply path/join 
                                              (butlast (path/split rs))))))
                          
                           :default ;; Should only see this on error
                           (do
                             (umsg/warning "InstrumentEditor jb-save action"
                                           "default cond executed")
                             (.warning! ied "Unknown save error")))))))

    (ss/listen jb-init :action
               (fn [_]
                 (let [bank-ed (.editor bank)
                       iprog (.initial-program descriptor)]
                   (.current-program! bank iprog)
                   (.sync-ui! bank-ed)
                   (.status! ied "Initial Program"))))

    (ss/listen jb-store :action
               (fn [_]
                 (let [bank-ed (.editor bank)
                       prog (.current-program bank)
                       pnum (int (max 0 (min max-program-number (.getValue spin-program))))
                       name (str (:name prog))
                       remarks (str (:remarks prog))]
                   (.push-undo-state! bank-ed
                                      (format "Store program %s" pnum))
                   (.store-program! bank pnum
                                    (assoc prog 
                                      :name name
                                      :remarks remarks
                                      :args (ucol/map->alist
                                             (.current-data bank))))
                   (.sync-ui! bank-ed)
                   (.status! ied (format "Stored program %s" pnum)))))

    (if (config/enable-button-text)
      (do
        (ss/config! jb-show-parent :text "Parent")
        (ss/config! jb-copy :text  "Copy Program")
        (ss/config! jb-paste :text  "Paste Program")
        (ss/config! jb-open :text  "Open Program")
        (ss/config! jb-save :text  "Save Program")
        (ss/config! jb-help :text  "Help")
        (ss/config! jb-init :text  "Init")
        (ss/config! jb-store :text  "Store Program")))
    (if (config/enable-button-icons)
      (do
        (.setIcon jb-show-parent (lnf/read-icon :tree :up))
        (.setIcon jb-copy (lnf/read-icon :general :copy))
        (.setIcon jb-paste (lnf/read-icon :general :paste))
        (.setIcon jb-open (lnf/read-icon :general :open))
        (.setIcon jb-save (lnf/read-icon :general :save))
        (.setIcon jb-init (lnf/read-icon :general :reset))
        (.setIcon jb-help (lnf/read-icon :general :help))
        (.setIcon jb-store (lnf/read-icon :general :bankstore))
        (.setSelectedIcon jb-show-parent (lnf/read-selected-icon :tree :up))
        (.setSelectedIcon jb-copy (lnf/read-selected-icon :general :copy))
        (.setSelectedIcon jb-paste (lnf/read-selected-icon :general :paste))
        (.setSelectedIcon jb-open (lnf/read-selected-icon :general :open))
        (.setSelectedIcon jb-save (lnf/read-selected-icon :general :save))
        (.setSelectedIcon jb-init (lnf/read-selected-icon :general :reset))
        (.setSelectedIcon jb-help (lnf/read-selected-icon :general :help))
        (.setSelectedIcon jb-store (lnf/read-selected-icon :general :bankstore))))
    (if (config/enable-tooltips)
      (do
        (.setToolTipText jb-show-parent "Show Parent")
        (.setToolTipText jb-copy "Copy program data to clipboard")
        (.setToolTipText jb-paste "Paste clipboard to program")
        (.setToolTipText jb-open "Open program file")
        (.setToolTipText jb-save "Save program file")
        (.setToolTipText jb-init "Initialize program")
        (.setToolTipText jb-help "Program help")
        (.setToolTipText jb-store "Store program to bank")))
    (.putClientProperty jb-help :topic :program)
    ;; START DEBUG
    (ss/listen jb-help :action (fn [_]
                                 (println (ss/config frame :size))
                                 (println)))
    ;; END DEBUG

    ied))
                
                      
