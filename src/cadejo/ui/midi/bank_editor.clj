(ns cadejo.ui.midi.bank-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.config])
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [cadejo.ui.util.undo-stack])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser])
  (:import javax.swing.event.ListSelectionListener
           javax.swing.event.CaretListener
           javax.swing.JFileChooser
           java.io.File
           )
  )

(def program-count cadejo.midi.program-bank/program-count)
(def start-reserved (- program-count cadejo.midi.program-bank/reserved-slots))

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

  (status!
    [this msg])

  (warning!
    [this msg])

  (sync-ui!
    [this])

)
  


(defn- format-program-cell [pnum bank]
  (let [prog (.get-program bank pnum)
        fid (:function-id prog)
        name (:name prog)]
    (format "[%03d] %s %s"
            pnum (if fid (format "(%s)" fid) "") name)))

(defn- create-program-list [bank]
  (let [acc* (atom [])]
    (dotimes [p program-count]
      (swap! acc* (fn [n](conj n (format-program-cell p bank)))))
    @acc*))

(defn bank-editor [bnk]
  (let [parent* (atom nil)
        enable-list-selection-listener* (atom true)
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo")
                                
        jb-init (ss/button :text "Init")
        jb-name (ss/button :text "Name")
        jb-open (ss/button :text "Open")
        jb-save (ss/button :text "Save")
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-transmit (ss/button :text "Transmit")
        jb-new (ss/button :text "New")
        jb-edit (ss/button :text "Edit")
        tb-store (ss/toggle :text "Store")
        jb-help (ss/button :text "Help")
        tbar1 (ss/grid-panel :rows 1
                             :items [jb-init jb-name jb-open jb-save
                                     jb-undo jb-redo jb-help])

        tbar2 (ss/grid-panel :rows 1
                             :items [jb-transmit jb-new jb-edit tb-store])
        lab-name (ss/label :text " "
                           :border (factory/bevel))
        lab-filename (ss/label :text (cadejo.config/config-path)
                               :border (factory/bevel))
        pan-info (ss/grid-panel :rows 1
                                :items [lab-name lab-filename])
        pan-south (ss/grid-panel :columns 1
                                 :items [tbar2 pan-info])
        lst-programs (ss/listbox :model (create-program-list bnk))
        pan-center (ss/horizontal-panel :items [(ss/scrollable lst-programs)]
                                        :border (factory/padding))
        pan-main (ss/border-panel :north tbar1
                                  :center pan-center
                                  :south pan-south)

        ;; status! (fn [txt](.status! @parent* txt))
        ;; warning! (fn [txt](.warning! @parent* txt))
        file-extension (.toLowerCase (name (.data-format bnk)))
      
        file-filter (seesaw.chooser/file-filter
                     (format "%s Bank" file-extension)
                     (fn [f] 
                       (path/has-extension? (.getAbsolutePath f) file-extension)))

        widget-map {:lab-name lab-name
                    :lab-filename lab-filename
                    :list-programs lst-programs
                    :pan-main pan-main
                    }
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

                  (status! [this msg]
                    (.status! @parent* msg))

                  (warning! [this msg]
                    (.warning! @parent* msg))

                  (sync-ui! [this]
                    (let [plst (.widget this :list-programs)
                          pnum (or (.current-program-number bnk) 0)]
                      (reset! enable-list-selection-listener* false)
                      (ss/config! (.widget this :lab-name) :text (.bank-name bnk))
                      (ss/config! plst :model (create-program-list bnk))
                      (.setSelectedIndex plst pnum)
                      (.ensureIndexIsVisible plst pnum)
                      (reset! enable-list-selection-listener* true)))
                  )]
    (.addListSelectionListener 
     lst-programs
     (proxy [ListSelectionListener][]
       (valueChanged [_]
         (cond
          (.getValueIsAdjusting lst-programs) ; do nothing
          nil
          
          @enable-list-selection-listener* ; program change
          (let [pnum (.getSelectedIndex lst-programs)
                reserved (>= pnum start-reserved)]
            (doseq [jc [jb-name jb-new jb-edit tb-store]]
                    (.setEnabled jc (not reserved)))
            (.program-change bnk pnum))

          :default                      ; do nothing
          nil))))
          
          
                               
    (ss/listen jb-init :action (fn [_]
                                 (.push-undo-state! bank-ed "Initialize Bank")
                                 (.bank-name! bnk "New Bank")
                                 (.bank-remarks! bnk "")
                                 (doseq [p (range 0 start-reserved)]
                                   (.set-program! bnk p nil "" []))
                                 (.sync-ui! bank-ed)
                                 (.status! bank-ed "New Bank")))

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

    ;; (ss/listen jb-undo :action
    ;;            (fn [_]
    ;;              (let [src (.pop-state undo-stack)]
    ;;                (if src
    ;;                  (do (.push-redo-state! this  (first src))
    ;;                      (.copy-state! bnk (second src))
    ;;                      (.sync-ui! this)
    ;;                      (.status! this (format "Undo %s" (first src))))
    ;;                  (.warning! this "Nothing to Undo")))))

    ;; (ss/listen jb-redo :action
    ;;            (fn [_]
    ;;              (let [src (.pop-state redo-stack)]
    ;;                (if src
    ;;                  (do (.push-undo-state! bank-ed (first src))
    ;;                      (.copy-state! bnk (second src))
    ;;                      (.sync-ui! bank-ed)
    ;;                      (.status! bank-ed (format "Redo %s" (first src))))
    ;;                  (.warning! bank-ed "Nothing to Redo")))))

    (ss/listen jb-undo :action (fn [_](.undo! bank-ed)))

    (ss/listen jb-redo :action (fn [_](.redo! bank-ed)))

    (ss/listen jb-transmit :action
               (fn [_]
                 (let [pnum (.getSelectedIndex lst-programs)]
                   (if pnum
                     (do 
                       (reset! enable-list-selection-listener* false)
                       (.program-change bnk pnum)
                       (reset! enable-list-selection-listener* true))))))
                     
    (ss/listen jb-new :action
               (fn [_]
                 (let [pnum (.getSelectedIndex lst-programs)]
                   (if (< pnum start-reserved)
                     (do
                       (.push-undo-state! bank-ed "New Program")
                       (.clear-program! bnk pnum)
                       (.sync-ui! bank-ed)
                       (.status! bank-ed (format "New program %d" pnum)))
                     (do
                       (.warning! bank-ed (format "Can not edit reserved slot %s" pnum)))))))

  
    (ss/listen jb-edit :action
               (fn [_]
                 ))

    bank-ed))
