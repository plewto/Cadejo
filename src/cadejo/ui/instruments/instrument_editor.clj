(println "--> cadejo.ui.instruments.instrument-editor")

(ns cadejo.ui.instruments.instrument-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.user-message :as umsg])
  (:use [cadejo.ui.util.overwrite-warning :only [overwrite-warning]])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser :as ssc])
  (:import java.awt.event.FocusListener
           java.io.File
           java.io.FileNotFoundException
           javax.swing.JFileChooser
           javax.swing.event.ChangeListener))

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

  (working
    [this flag])
  
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
        parent-editor (.get-editor performance)
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
        jb-copy (factory/button "Copy" :general :copy "Copy program data to clipboard")
        jb-paste (factory/button "Paste" :general :paste "Paste clipboard data to program")
        jb-open (factory/button "Open" :general :open "Open program file")
        jb-save (factory/button "Save" :general :save "Save program file")
        jb-help (factory/button "Help" :general :help "Program editor help")
        pan-north (ss/toolbar :floatable? false
                              :items [
                                      :separator jb-open jb-save
                                      :separator jb-copy jb-paste
                                      :separator jb-help]
                              :border (factory/padding))
                                     
        pan-tabs (ss/tabbed-panel
                  :border (factory/padding))

        ;; Main panel
        pan-main (ss/border-panel :north pan-north
                                  :center pan-tabs)
        frame (ss/frame :title (format "%s Editor" (name id))
                        :content pan-main
                        :on-close :hide
                        :size [1050 :by 650]
                        :icon (.logo descriptor :tiny))
        widget-map {:jb-help jb-help
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
                (let [prog (.current-program this)]
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
                (.status! parent-editor msg))

              (warning! [this msg]
                (.warning! parent-editor msg))

              (working [this flag]
                (.working parent-editor flag))
    
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
                    (let [selected-index (.getSelectedIndex pan-tabs)
                          se (nth @sub-editors* selected-index)]
                      (.sync-ui! se)))))
              ) ;; end ied

        name-editor (program-name-editor ied)]

    (.add-sub-editor! ied 
                      (if (config/enable-button-text) "Common" "")
                      (if (config/enable-button-icons)(lnf/read-icon :edit :text) nil)
                      name-editor)

    (.addChangeListener pan-tabs
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (.sync-ui! ied))))
 
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
    (.putClientProperty jb-help :topic (.help-topic descriptor))
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)
    ied))
                
                      
