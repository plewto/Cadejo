(ns cadejo.ui.instruments.instrument-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.config :as config])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.color-utilities :as cutil])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.user-message :as umsg])
  (:use [cadejo.ui.util.overwrite-warning :only [overwrite-warning]])
  (:require [overtone.core :as ot])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser :as ssc])
  (:require [seesaw.font :as ssfont])
  (:require [sgwr.indicators.numberbar :as numberbar])
  (:import 
   java.awt.event.FocusListener
   java.io.File
   java.io.FileNotFoundException
   javax.swing.Box
   javax.swing.JFileChooser))


(def ^:private max-program-number 128)

(defn- third [col](nth col 2))

(def all-file-filter (ssc/file-filter
                      "All Files" (constantly true)))

(defn- save-program [ied jfc dform ext program data]
  (let [file (.getSelectedFile jfc)
        filename (path/replace-extension 
                  (.getAbsolutePath file) ext) 
        pan-main (.widget ied :pan-main)
        pout (assoc (.to-map program)
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

;; (defn- open-program [ied jfc dform ext]
;;   (let [file (.getSelectedFile jfc)
;;         filename (path/replace-extension 
;;                   (.getAbsolutePath file) ext)
;;         rec (try
;;               (read-string (slurp filename))
;;               (catch FileNotFoundException e
;;                 nil))]
;;     (if (and rec 
;;              (= (:file-type rec) :cadejo-program)
;;              (= (:data-format rec) dform))
;;       (dissoc rec :file-type :data-format)
;;       nil)))

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
              
              (init! [this] )

              (sync-ui! [this]
                (let [bank (.parent-bank parent-editor)
                      prog (.current-program bank)
                      name (.program-name prog)
                      remarks (.program-remarks prog)]
                  (ss/config! txt-name :text name)
                  (ss/config! txt-remarks :text remarks))))]
    
    (.addFocusListener txt-name 
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 pname (ss/config txt-name :text)]
                             (.program-name! (.current-program bank) (str pname))
                             (ss/config! (.widget parent-editor :lab-name)
                                         :text pname)
                             (.status! parent-editor 
                                       "Program name changed")))))
    (.addFocusListener txt-remarks
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 remarks (ss/config txt-remarks :text)]
                             (.program-remarks! (.current-program bank)(str remarks))
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
    [this label icon subed])

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

  ;; If flag true place progress bar into 'indeterminate' mode
  ;; if flag false restore progress bar to default state
  (working
    [this flag])

  (pp
    [this]
    "Apply bank pp-hook to current data, display results")

  (set-store-location!
    [this pnum]
    "Sets the value of the store-program spinner")
  
  (init!
    [this]
    "Initialize current program")

  (random-program!
    [this]
    "Generate random program")

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
        ;;
        lab-id (ss/label :text (name id)
                         :font (ssfont/font :size id-font-size)
                         :border (factory/bevel))
        jb-show-parent (factory/button "Parent" :tree :up "Show parent program-bank")
        jb-copy (factory/button "Copy" :general :copy "Copy program data to clipboard")
        jb-paste (factory/button "Paste" :general :paste "Paste clipboard data to program")
        jb-open (factory/button "Open" :general :open "Open program file")
        jb-save (factory/button "Save" :general :save "Save program file")
        jb-help (factory/button "Help" :general :help "Program editor help")
        jb-sync (ss/button :text "[Sync]") ;; DEBUG
        pan-north (ss/toolbar :floatable? false
                              :items [lab-id 
                                      :separator jb-show-parent
                                      :separator jb-open jb-save
                                      :separator jb-copy jb-paste
                                      :separator jb-help jb-sync]
                              :border (factory/padding))

        ;; South toolbar
        ;;
        nbar-store (let [[cbg cna cav](config/displaybar-colors)
                         skin(config/current-skin)
                         bg (cutil/color (or cbg (lnf/get-color skin :text-fg)))
                         na (cutil/color (or cna (lnf/get-color skin :text-bg)))
                         av (cutil/color (or cav (lnf/get-color skin :text-bg-selected)))
                         nbar (numberbar/numberbar 3 0 127)]
                     (ss/config! (.drawing-canvas nbar) :size [128 :by 48])
                     (.colors! nbar bg na av)
                     (.display! nbar "0")
                     nbar)
        
        jb-init (factory/button "Init" :general :reset "Initialize program data")
        jb-dice (factory/button "Random" :general :dice "Generate random patch")
        jb-store (factory/button "Store" :general :bankstore "Store program data to selected bank slot")
        jb-store-inc1 (ss/button :icon (lnf/read-icon :mini :up1) :size [18 :by 18])
        jb-store-inc8 (ss/button :icon (lnf/read-icon :mini :up2) :size [18 :by 18])
        jb-store-dec1 (ss/button :icon (lnf/read-icon :mini :down1) :size [18 :by 18])
        jb-store-dec8 (ss/button :icon (lnf/read-icon :mini :down2) :size [18 :by 18])
        pan-store-inc1 (ss/grid-panel :columns 1 :items [jb-store-inc1 jb-store-dec1])
        pan-store-inc8 (ss/grid-panel :columns 1 :items [jb-store-inc8 jb-store-dec8])
       
        pan-store (ss/horizontal-panel :items [(.drawing-canvas nbar-store)
                                                pan-store-inc1
                                                pan-store-inc8
                                                jb-store])
        
        progress-bar (ss/progress-bar :indeterminate? false)
        pan-south1 (ss/border-panel 
                    :center (ss/horizontal-panel :items [jb-init jb-dice])
                    :east pan-store
                    :border (factory/padding))

        lab-status (ss/label :text " ")
        lab-name (ss/label :text " ")
        pan-south2 (ss/grid-panel 
                    :rows 1
                    :items [(ss/vertical-panel :items [lab-status]
                                               :border (factory/bevel))
                            (ss/vertical-panel :items [progress-bar]
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
        widget-map {:jb-help jb-help
                    :lab-name lab-name
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
                (let [pan-main (.widget subed :pan-main)]
                  (cond (and (config/enable-button-text)
                             (config/enable-button-icons))
                        (.addTab pan-tabs label icon pan-main)

                        (config/enable-button-icons)
                        (.addTab pan-tabs "" icon pan-main)

                        :default
                        (.addTab pan-tabs label))
                  (.parent! subed this)))

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
                (.status! this (format "[%-16s] --> %s" param value))
                (.set-param! bank param value))

              (widgets [this] widget-map)

              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))

              (status! [this msg]
                (ss/config! lab-status :text (str msg)))

              (warning! [this msg]
                (ss/config! lab-status :text (format "WARNING: %s" msg)))

              (working [this flag]
                (ss/config! progress-bar :indeterminate? flag))

              (pp [this]
                (let [prog (.current-program this)]
                  (if (and prog (config/enable-pp))
                    (let [pname (:name prog)
                          rem (:remarks prog)
                          pnum (.value nbar-store)
                          d (.current-data this)
                          ppf (.pp-hook bank)]
                      (if ppf
                        (println (ppf pnum pname d rem)))))))
           
              (set-store-location! [this slot]
                (if (and slot (>= slot 0)(<= slot max-program-number))
                  (.display! nbar-store (str (int slot)))))

              (init! [this]
                (let [prog (.clone (.initial-program descriptor))
                      bank (.parent-bank this)]
                  (.current-program! bank prog)
                  (.sync-ui! this)
                  (.status! this "Initialized program")))

              (random-program! [this]
                (let [prog (.random-program descriptor)
                      bank (.parent-bank this)
                      pp (.pp-hook bank)]
                  (if prog
                    (do
                      (.current-program! bank prog)
                      (if (and pp (config/enable-pp))
                        (println (pp -1 "Random" (.data prog) "")))
                      (.sync-ui! this)
                      (.status! this "Random Program")))))

              (sync-ui! [this]
                (let [prog (.current-program bank)
                      data (and prog (.data prog))]
                  (if data
                    (do
                      (ss/config! lab-name :text (.program-name prog))
                      (doseq [s @sub-editors*]
                        (.sync-ui! s))))))
              ) ;; end ied

        name-editor (program-name-editor ied)]

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
                                 (.current-program! bank (:name prog)(:remarks prog)(:data prog))
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
    
    (ss/listen jb-store :action
               (fn [_]
                 (let [bank-ed (.editor bank)
                       prog (.current-program bank)
                       slot (int (max 0 (min max-program-number (.value nbar-store))))]
                   (.push-undo-state! bank-ed 
                                      (format "Store program %s" slot))
                   (.store! bank slot (.clone prog))
                   (.sync-ui! bank-ed)
                   (.status! ied (format "Stored program %s" slot)))))
                       
    (ss/listen jb-store-inc1 :action 
               (fn [_]
                 (let [v (int (min 127 (inc (.value nbar-store))))]
                   (.display! nbar-store (str v)))))

    (ss/listen jb-store-inc8 :action 
               (fn [_]
                 (let [v (int (min 127 (+ 8 (.value nbar-store))))]
                   (.display! nbar-store (str v)))))
    
    (ss/listen jb-store-dec1 :action 
               (fn [_]
                 (let [v (int (max 0 (dec (.value nbar-store))))]
                   (.display! nbar-store (str v)))))

    (ss/listen jb-store-dec8 :action 
               (fn [_]
                 (let [v (int (max 0 (- (.value nbar-store) 8)))]
                   (.display! nbar-store (str v)))))

    (ss/listen jb-dice :action
               (fn [_]
                 (.random-program! ied)))

    (ss/listen jb-init :action
               (fn [_]
                 (.init! ied)))
    
    (ss/listen jb-sync :action
               (fn [_]
                 (let [bnk (.bank performance)
                       data (.current-data bnk)
                       pp (.pp-hook bnk)]
                   (println (pp -1 "" data "")))
                 (.sync-ui! ied)))

    (if (config/enable-tooltips)
      (do (.setToolTipText jb-store-inc1 "Increment program store location")
          (.setToolTipText jb-store-dec1 "Decrement program store location")
          (.setToolTipText jb-store-inc8 "Increase program store location by 8")
          (.setToolTipText jb-store-dec8 "Decrease program store location by 8")
          (.setToolTipText (.drawing-canvas nbar-store) "Program store location")))
    (.putClientProperty jb-help :topic (.help-topic descriptor))
    (ss/listen jb-help :action (fn [_](println (ss/config pan-main :size)))) ;; DEBUG
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)
    ied))
                
                      
