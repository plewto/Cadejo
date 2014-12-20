(println "--> cadejo.ui.midi.bank-editor")

(ns cadejo.ui.midi.bank-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.config :as config])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [cadejo.ui.util.undo-stack])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser])
  (:import javax.swing.event.ListSelectionListener
           javax.swing.event.CaretListener
           javax.swing.SwingUtilities
           java.io.File))

(def program-count 128) 

(defprotocol BankEditor

  (widgets 
    [this])

  (widget
    [this key])

  (bank 
    [this])
  
  (performance
    [this])

  (push-undo-state!
    [this meg])

  (push-redo-state!
    [this msg])

  (undo! 
    [this])

  (redo! 
    [this])

  (set-parent-editor! 
    [this parent])

  (working
    [this flag])

  (status!
    [this msg])

  (warning!
    [this msg])

  (sync-ui!
    [this])

  (instrument-editor!
    [this cfn]
    "Set instrument editor")

  (instrument-editor
    [this]
    "Return instrument editor or nil")
)

(defn- format-program-cell [pnum programs]
  (let [prog (get programs pnum)
        name (if prog (.program-name prog) "")]
    (format "[%03d] %s" pnum name)))

(defn- create-program-list [bank]
  (let [acc* (atom [])
        programs (.programs bank)]
    (dotimes [p program-count]
      (swap! acc* (fn [n](conj n (format-program-cell p programs)))))
    @acc*))

(defn bank-editor [bnk]
  (let [parent* (atom nil)
        enabled* (atom true)
        instrument-editor* (atom nil)
        enable-list-selection-listener* (atom true)
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo")
        jb-init (factory/button "Init" :general :reset "Initialize bank")
        jb-name (factory/button "Name" :edit :text "Edit bank name & remarks")
        jb-open (factory/button "Open" :general :open "Open bank file")
        jb-save (factory/button "Save" :general :save "Save bank file")
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-transmit (factory/button "Transmit" :midi :transmit "Transmit selected program data")
        jb-edit (factory/button "Edit" :edit nil "Edit program")
        jb-help (factory/button "Help" :general :help "Program bank help")
        tbar1 (ss/toolbar :floatable? false
                          :items [jb-init jb-name 
                                  jb-open jb-save
                                  :separator jb-undo jb-redo 
                                  :separator jb-edit jb-transmit 
                                  :separator jb-help])
        lab-name (ss/label :text " "
                           :border (factory/bevel))
        lab-filename (ss/label :text (config/config-path)
                               :border (factory/bevel))
        pan-info (ss/grid-panel :rows 1
                                :items [lab-name lab-filename])
        pan-south (ss/grid-panel :columns 1
                                 :items [pan-info])
        lst-programs (ss/listbox :model (create-program-list bnk))
        pan-center (ss/horizontal-panel :items [(ss/scrollable lst-programs)]
                                        :border (factory/padding))
        pan-main (ss/border-panel :north tbar1
                                  :center pan-center
                                  :south pan-south)
        file-extension (.toLowerCase (name (.data-format bnk)))

        file-filter (seesaw.chooser/file-filter
                     (format "%s Bank" file-extension)
                     (fn [f] 
                       (path/has-extension? (.getAbsolutePath f) file-extension)))
        widget-map {:jb-init jb-init
                    :jb-name jb-name
                    :jb-open jb-open
                    :jb-save jb-save
                    :jb-transmit jb-transmit
                    :jb-edit jb-edit
                    :jb-help jb-help
                    :lab-name lab-name
                    :lab-filename lab-filename
                    :list-programs lst-programs
                    :pan-main pan-main}
        bank-ed (reify BankEditor
                  
                  (widgets [this] widget-map)

                  (widget [this key]
                    (or (get widget-map key)
                        (umsg/warning (format "BankEditor does not have %s widget" key))))

                  (set-parent-editor! [this ed]
                    (reset! parent* ed))
                  
                  (bank [this] bnk)

                  (performance [this]
                    (.get-parent-performance bnk))

                  (push-undo-state! [this action]
                    (.push-state! undo-stack [action (.clone bnk)]))

                  (push-redo-state! [this action]
                    (.push-state! redo-stack [action (.clone bnk)]))

                  (undo! [this]
                    (let [src (.pop-state! undo-stack)]
                      (if src
                        (do (.push-redo-state! this  (first src))
                            (.copy-state! bnk (second src))
                            (.sync-ui! this)
                            (.status! this (format "Undo %s" (first src))))
                        (.warning! this "Nothing to Undo"))))

                  (redo! [this]
                    (let [src (.pop-state! redo-stack)]
                      (if src
                        (do (.push-undo-state! this (first src))
                            (.copy-state! bnk (second src))
                            (.sync-ui! this)
                            (.status! this (format "Redo %s" (first src))))
                        (.warning! this "Nothing to Redo"))))
                  
                  (working [this flag]
                    (.working @parent* flag))

                  (status! [this msg]
                    (.status! @parent* msg))

                  (warning! [this msg]
                    (.warning! @parent* msg))

                  (sync-ui! [this]
                    (if @enabled*
                      (let [plst (.widget this :list-programs)
                            pnum (or (.current-slot bnk) 0)]
                        (reset! enable-list-selection-listener* false)
                        (ss/config! (.widget this :lab-name) :text (.bank-name bnk))
                        (ss/config! plst :model (create-program-list bnk))
                        (.setSelectedIndex plst pnum)
                        ;; ISSUE: automatic scrolling causes substance 
                        ;; UiThreadingViolationException
                        ;; (try
                        ;;   (.ensureIndexIsVisible plst pnum)
                        ;;   (catch Exception ex
                        ;;     (umsg/warning "Caught exception BankEditor.sync-ui!"
                        ;;                   "ensureIndexIsVisible")))
                        (if @instrument-editor*
                          (.sync-ui! @instrument-editor*))
                        (reset! enable-list-selection-listener* true))))

                  (instrument-editor! [this ied]
                    (reset! instrument-editor* ied))

                  (instrument-editor [this]
                    (or @instrument-editor*
                        (do (.warning! this "Instrument editor not defined")
                            nil))) 
                  ) ;; end bank-ed
        
        create-instrument-editor (fn []
                                   (println ";; Creating instrument editor ...")
                                   (.working bank-ed true)
                                   (SwingUtilities/invokeLater
                                    (proxy [Runnable][]
                                      (run []
                                        (let [performance (.node @parent*)
                                              itype (.get-property performance :instrument-type)
                                              descriptor (.get-property performance :descriptor)
                                              ied (.create-editor descriptor performance)]
                                          (reset! instrument-editor* ied)
                                          (try
                                            (ss/show! (.widget ied :frame))
                                            (catch NullPointerException ex
                                              (.warning! bank-ed "Instrument Editor not defined")))
                                          (.working bank-ed false))))))]
    (.addListSelectionListener 
     lst-programs
     (proxy [ListSelectionListener][]
       (valueChanged [_]
         (cond
          (.getValueIsAdjusting lst-programs) ; do nothing
          nil

          @enable-list-selection-listener* ;; program-change
          (let [slot (.getSelectedIndex lst-programs)]
            (.recall bnk slot)
            (if @instrument-editor*
              (let [ied @instrument-editor*
                    prog (.current-program bnk)]
                (if prog
                  (do 
                    (.set-store-location! ied slot)
                    (.sync-ui! ied)
                    )) )))

          :default                      ; do nothing
          nil)))) 
                               
    (ss/listen jb-init :action (fn [_]
                                 (.working bank-ed true)
                                 (SwingUtilities/invokeLater
                                  (proxy [Runnable][]
                                    (run []
                                      (let [ied (.instrument-editor bank-ed)]
                                        (.push-undo-state! bank-ed "Initialize Bank")
                                        (.init! bnk)
                                        (if ied (.init! ied))
                                        (.sync-ui! bank-ed)
                                        (.status! bank-ed "Initialized Bank")
                                        (.working bank-ed false)))))))


    (ss/listen jb-name :action (fn [_]
                                 (let [ref-name (.bank-name bnk)
                                       ref-rem (.bank-remarks bnk)
                                       tx-name (ss/text :multi-line? false
                                                        :editable? true
                                                        :text ref-name)
                                       pan-name (ss/vertical-panel :items [tx-name]
                                                                   :border (factory/title "Name"))
                                       tx-remarks (ss/text :multi-line? true
                                                           :editable? true
                                                           :text ref-rem)
                                       pan-remarks (ss/vertical-panel
                                                    :items [(ss/scrollable tx-remarks)]
                                                    :border (factory/title "Remarks"))
                                       jb-cancel (ss/button :text "Cancel")
                                       jb-save (ss/button :text "Save")
                                       pan-main (ss/border-panel :north pan-name
                                                                 :center pan-remarks)
                                       dia (ss/dialog 
                                            :title "Bank Name"
                                            :content pan-main
                                            :type :plain
                                            :options [jb-save jb-cancel]
                                            :default-option jb-save
                                            :size [300 :by 300])]
                                   (ss/listen jb-cancel :action (fn [_]
                                                                  (ss/return-from-dialog dia false)))
                                   (ss/listen jb-save :action (fn [_]
                                                                (let [n (ss/config tx-name :text)
                                                                      r (ss/config tx-remarks :text)]
                                                                  (if (or (not (= n ref-name))(not (= r ref-rem)))
                                                                    (do 
                                                                      (.push-undo-state! bank-ed "Name/Remarks")
                                                                      (.bank-name! bnk n)
                                                                      (.bank-remarks! bnk r))))
                                                                (ss/return-from-dialog dia true)))
                                   (ss/show! dia))))
    (ss/listen jb-save :action
               (fn [_] 
                 (let [ext file-extension
                       success (fn [jfc f]
                                 (let [abs (path/append-extension (.getAbsolutePath f) ext)]
                                   (if (cadejo.ui.util.overwrite-warning/overwrite-warning
                                        pan-main "Bank" abs)
                                     (if (.write-bank bnk abs)
                                       (do 
                                         (ss/config! lab-filename :text abs)
                                         (.status! bank-ed "Saved Bank"))
                                       (.warning! bank-ed (format "Can not save bank to \"%s\"" abs)))
                                     (.status! bank-ed "Bank Save Canceled"))))
                       cancel (fn [jfc]
                                (.status! bank-ed "Bank Save Canceled"))
                       default-file (ss/config lab-filename :text)
                       dia (seesaw.chooser/choose-file
                            :type (format "Save %s Bank" (name (.data-format bnk)))
                            :dir default-file
                            :multi? false
                            :selection-mode :files-only
                            :filters [file-filter]
                            :remember-directory? true
                            :success-fn success
                            :cancel-fn cancel)] )))
    
    (ss/listen jb-open :action
               (fn [_]
                 (let [ext file-extension
                       success (fn [jfc f]
                                 (let [abs (.getAbsolutePath f)]
                                   (.push-undo-state! bank-ed "Open bank")
                                   (if (.read-bank! bnk abs)
                                     (do 
                                       (ss/config! lab-filename :text abs)
                                       (ss/config! lab-name :text (.bank-name bnk))
                                       (.sync-ui! bank-ed)
                                       (.status! bank-ed "Bank read"))
                                     (.warning! bank-ed (format "Can not open \"%s\" as %s bank"
                                                       abs (.data-format bnk))))))
                       cancel (fn [jfc]
                                (.status! bank-ed "Bank read canceled"))
                       default-file (ss/config lab-filename :text)
                       dia (seesaw.chooser/choose-file
                            :type (format "Open %s Bank" ext)
                            :dir default-file
                            :multi? false
                            :selection-mode :files-only
                            :filters [file-filter]
                            :remember-directory? true
                            :success-fn success
                            :cancel-fn cancel)] )))

    (ss/listen jb-undo :action (fn [_](.undo! bank-ed)))

    (ss/listen jb-redo :action (fn [_](.redo! bank-ed)))
   
    (ss/listen jb-transmit :action
               (fn [_]
                 (let [slot (.current-slot bnk)]
                   (if slot
                     (do 
                       (reset! enable-list-selection-listener* false)
                       (.program-change bnk {:data1 slot})
                       (reset! enable-list-selection-listener* true))))))
    
    (ss/listen jb-edit :action
               (fn [_]
                 (let [ied @instrument-editor*]
                   (if (not ied)
                     (create-instrument-editor)))
                 (let [ied @instrument-editor*]
                   (if ied
                     (do 
                       (let [f (.widget ied :frame)]
                         (.set-store-location! ied (.current-slot bnk))
                         (.sync-ui! ied)
                         (ss/show! f)
                         (.toFront f)))))))

    (.putClientProperty jb-help :topic :bank-editor)
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)
    (.sync-ui! bank-ed)
    bank-ed))
