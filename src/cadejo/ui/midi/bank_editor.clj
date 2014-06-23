(println "--> Loading cadejo.ui.midi.bank-editor")
;; NOTES:
;; 1) Editing bank name and remarks are not saved to undo stack

(ns cadejo.ui.midi.bank-editor
  (:require [cadejo.ui.util.undo-stack])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.overwrite-warning])
  (:use [seesaw.core])
  (:require [seesaw.border :as ssb])
  (:require [seesaw.chooser])
  (:import javax.swing.BorderFactory
           javax.swing.event.ListSelectionListener
           javax.swing.event.CaretListener))

(declare open-dialog)
(declare save-dialog)
(declare function-edit-dialog)

(def program-count 128)

(defn- make-border [title]
  (ssb/compound-border
   (ssb/to-border title)
   (ssb/empty-border :thickness 4)))

(defn- create-file-filter [ed]
  (let [ext (.file-extension ed)
        description (format "%s Bank file" ext)]
    (seesaw.chooser/file-filter
     description
     (fn [f]
       (path/has-extension? (.getAbsolutePath f) ext)))))

(defn- format-program-cell [pnum bank]
  (let [prog (.get-program bank pnum)
        fid (:function-id prog)
        name (:name prog)]
    (format "[%03d] %s %s"
            pnum (if fid (format "(%s)" fid) "") name)))

(defn- create-program-list [bank]
  (let [acc* (atom [])]
    (dotimes [p program-count]
      (swap! acc* (fn [a](conj a (format-program-cell p bank)))))
    @acc*))


(defprotocol BankEditorProtocol

  (widget-map 
    [this])
  
  (widget
    [this key])

  (new-bank 
    [this])

  (open-bank
    [this])

  (save-bank 
    [this])

  (undo 
    [this])

  (redo
    [this])
 
  (edit-program-function
    [this])

  (edit-program-data
    [this])

  (show-help 
    [this])

  ;; (select-program 
  ;;   [this pnum])

  (enable-store-program-mode 
    [this flag])

  (store-program-mode? 
    [this])

  (new-program 
    [this])
  
  (delete-program
    [this])

  (push-undo-state
    [this])

  (push-redo-state
    [this])

  (enable-list-selection
    [this flag])

  (list-selection-enabled? 
    [this])

  (sync-ui
    [this])
    
  (status 
    [this msg])

  (warning
    [this msg])

  (file-extension 
    [this])

  (filename 
    [this]
    [this update])


)

(deftype BankEditor [bank 
                     widgets
                     selection-enable-flag*
                     store-mode-flag*
                     undo-stack
                     redo-stack
                     filename*]
  BankEditorProtocol
  
  (widget-map [this]
    widgets)

  (widget [this key]
    (let [w (get widgets key)]
      (if (not w)
        (umsg/warning (format "BankEditor does not have %s widget" key))
        w)))

  (new-bank [this]
    (.push-undo-state this)
    (.bank-name! bank "New Bank")
    (.bank-remarks! bank "")
    (.clear-all-programs! bank)
    (.sync-ui this)
    (.status this "New bank"))

  (open-bank [this]
    (open-dialog this bank))

  (save-bank [this]
    (save-dialog this bank))

  (undo [this]
    (let [src (.pop-state undo-stack)]
      (if src
        (do 
          (.push-redo-state this)
          (.copy-state! bank src)
          (.sync-ui this)
          (.status this "Undo"))
        (.warning this "Nothing to undo"))))

  (redo [this]
    (let [src (.pop-state redo-stack)]
      (if src
        (do 
          (.push-undo-state this)
          (.copy-state! bank src)
          (.sync-ui this)
          (.status this "Redo"))
        (.warning this "Nothing to redo"))))

  (edit-program-function [this]
    (let [pnum (or (.current-program-number bank) 0)]
      (function-edit-dialog this bank pnum)))

  (edit-program-data [this]
    (.warning this "ISSUE edit-program-data not implemented")
    )

  (show-help [this]
    (.warning this "ISSUE show-help not implemented")
    )

  ;; (select-program [this pnum]
  ;;   (.warning this "ISSUE select-program not implemented")
  ;;   )
     
  (enable-store-program-mode [this flag]
    (swap! store-mode-flag* (fn [n] flag))
    (config! (.widget this :jb-store) :selected? flag))

   
  (store-program-mode? [this]
    @store-mode-flag*)
  
  (new-program [this]
    (let [pnum (.current-program-number bank)]
      (.push-undo-state this)
      (.set-program! bank pnum "New" [])
      (.sync-ui this)
      (.status this (format "New program saved to %s" pnum))))

  (delete-program [this]
    (let [pnum (.current-program-number bank)]
      (.push-undo-state this)
      (.clear-program! bank pnum)
      (.sync-ui this)
      (.status this (format "program %s deleted" pnum))))
          
  (enable-list-selection [this flag]
    (swap! selection-enable-flag* (fn [n] flag)))

  (list-selection-enabled? [this]
    @selection-enable-flag*)

  (sync-ui [this]
    (let [plst (.widget this :lst-programs)
          pnum (or (.current-program-number bank) 0)]
      (swap! selection-enable-flag* (fn [n] false))
      (config! (.widget this :tx-name) :text (.bank-name bank))
      (config! (.widget this :tx-remarks) :text (.bank-remarks bank))
      (config! plst :model (create-program-list bank))
      (.setSelectedIndex plst pnum)
      (.ensureIndexIsVisible plst pnum)
      (swap! selection-enable-flag* (fn [n] true))))
    
  (status [this msg]
    (let [lab (.widget this :lab-status)]
      (config! lab :text msg)))

  (warning [this msg]
    (.status this (format "WARNING: %s" msg)))

  (push-undo-state [this]
    (.push-state undo-stack (.clone bank)))
  
  (push-redo-state [this]
    (.push-state redo-stack (.clone bank)))
  
  (file-extension [this]
    (name (.data-format bank)))

  (filename [this update]
    (swap! filename* (fn [n] update))
    (config! (.widget this :lab-filename)
             :text (format "  Filename '%s'" update))
    update)

  (filename [this]
    @filename*)

)

; ---------------------------------------------------------------------- 
;                                  Dialogs

(defn- open-dialog [ed bank]
  (let [ext (.file-extension ed)
        success (fn [jfc f]
                  (let [abs (path/replace-extension (.getAbsolutePath f) ext)]
                    (.push-undo-state ed)
                    (if (.read-bank! bank abs)
                      (do 
                        (.filename ed abs)
                        (.program-change bank 0)
                        (.sync-ui ed)
                        (.status ed "Bank read"))
                      (.warning ed (format "Could not read bank file '%s'" abs)))))
        cancel (fn [jfc]
                 (.status ed "Bank read canceled"))
        ffilter (create-file-filter ed)
        default-file (.filename ed)
        dia (seesaw.chooser/choose-file
             :type :open
             :dir default-file
             :multi? false
             :selection-mode :files-only
             :filters [ffilter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]))

;; (defn- save-dialog [ed bank]
;;   (let [ext (.file-extension ed)
;;         success (fn [jfc f]
;;                   (let [abs (path/append-extension (.getAbsolutePath f) ext)]
;;                     (if (.write-bank bank abs)
;;                       (do
;;                         (.filename ed abs)
;;                         (.status ed "Saved bank file"))
;;                       (.warning ed (format "Could not save bank to '%s'" abs)))))
;;         cancel (fn [jfc]
;;                  (.status ed "Bank save canceled"))
;;         ffilter (create-file-filter ed)
;;         default-file (.filename ed)
;;         dia (seesaw.chooser/choose-file
;;              :type :save
;;              :dir default-file
;;              :multi? false
;;              :selection-mode :files-only
;;              :filters [ffilter]
;;              :remember-directory? true
;;              :success-fn success
;;              :cancel-fn cancel)]))

(defn- save-dialog [ed bank]
  (let [ext (.file-extension ed)
        success (fn [jfc f]
                  (let [abs (path/append-extension (.getAbsolutePath f) ext)]
                    (if (cadejo.ui.util.overwrite-warning/overwrite-warning
                         (.widget ed :pan-main) "Bank" abs)
                      (if (.write-bank bank abs)
                        (do
                          (.filename ed abs)
                          (.status ed "Saved bank file"))
                        (.warning ed (format "Could not save bank to '%s'" abs)))
                      (.status ed "Save canceled"))))
        cancel (fn [jfc]
                 (.status ed "Bank save canceled"))
        ffilter (create-file-filter ed)
        default-file (.filename ed)
        dia (seesaw.chooser/choose-file
             :type :save
             :dir default-file
             :multi? false
             :selection-mode :files-only
             :filters [ffilter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]))



(defn- function-edit-dialog [ed bank pnum]
  (let [prog (.get-program bank pnum)
        fid (:function-id prog)
        dst-fid* (atom nil)
        name (:name prog)
        args (:args prog)
        remarks (:remarks prog)
        grp (button-group)
        function-buttons (let [acc* (atom [])]
                           (doseq [f (.function-keys bank)]
                             (let [rb (radio :text (str f)
                                             :group grp
                                             :selected? (= f fid)
                                             :id f)]
                               (swap! acc* (fn [n](conj n rb)))
                               (listen rb :action (fn [ev]
                                                    (let [src (.getSource ev)
                                                          id (config src :id)]
                                                    (swap! dst-fid* (fn [n] id)))))))
                           
                               @acc*)
        pan-functions (vertical-panel :items function-buttons
                                      :border (make-border "Available Functions"))

        tx-name (text :text (str name) 
                      :multi-line? false)
        jb-clear-name (button :text "X")
        pan-name (border-panel :center tx-name
                               :east jb-clear-name
                               :border (make-border "Name"))

        tx-remarks (text :text (str remarks)
                         :multi-line? true
                         :size [300 :by 200])
        jb-clear-remarks (button :text "X")
        pan-remarks (border-panel :center (scrollable tx-remarks)
                                  :east jb-clear-remarks
                                  :border (make-border "Remarks"))

        tx-args (text :text (str args)
                           :multi-line? true
                           :size [300 :by 200])
        jb-clear-args (button :text "X")
        pan-args (border-panel :center (scrollable tx-args)
                               :east jb-clear-args
                               :border (make-border "Arguments"))

        spn-model (spinner-model pnum :from 0 :to program-count)
        spn-pnum (spinner :model spn-model
                          :size [200 :by 32])
        pan-south (horizontal-panel :items [spn-pnum]
                                    :border (make-border "Program Number"))
        pan-north (border-panel :north pan-name
                                :center pan-functions)
        pan-center (vertical-panel :items [pan-args pan-remarks])
        pan-main (border-panel :north pan-north
                               :center pan-center
                               :south pan-south)
        jb-cancel (button :text "Cancel")
        jb-save (button :text "Save")
        dia (dialog :content pan-main
                    :type :plain
                    :options [jb-cancel jb-save]
                    :default-option jb-save
                    :title "Edit Program Function (Use With Care!)"
                    :modal? true
                    :on-close :nothing
                    :parent (.widget ed :pan-main))]
    (listen jb-clear-name :action (fn [ev](config! tx-name :text "")))
    (listen jb-clear-args :action (fn [ev](config! tx-args :text "")))
    (listen jb-clear-remarks :action (fn [ev](config! tx-remarks :text "")))
    (listen jb-cancel :action 
            (fn [ev]
              (.dispose dia)
              (.status ed "Edit function canceled")))
    (listen jb-save :action 
            (fn [ev]
              (let [dst-pnum (.getValue spn-pnum)
                    dst-name (config tx-name :text)
                    dst-remarks (config tx-remarks :text)
                    dst-args (config tx-args :text)]
                (.push-undo-state ed)
                (.set-program! bank dst-pnum @dst-fid* dst-name dst-args dst-remarks)
                (.dispose dia)
                (.sync-ui ed)
                (.status ed (format "%s program edit stored to %s"
                                    dst-name dst-pnum)))))
    (pack! dia)
    (show! dia)))
                         
             
(defn- store-program-dialog [ed bank pnum]
  (let [pnum-src (.current-program-number bank)
        prog-src (.get-program bank pnum-src)
        parent (.widget ed :pan-main)
        dest-pnum pnum
        tx-name (text :text (:name prog-src)
                      :multi-line? false)
        jb-clear-name (button :text "X")
        pan-north (border-panel :center tx-name
                                :east jb-clear-name
                                :border (make-border "Name"))
                      
        tx-remarks (text :text (str (:remarks prog-src))
                         :multi-line? true
                         :size [300 :by 100])
        sp-remarks (scrollable tx-remarks)
        jb-clear-remarks (button :text "X")
        pan-center (border-panel :center sp-remarks
                                 :east jb-clear-remarks
                                 :border (make-border "Remarks"))
                               
        spn-model (spinner-model pnum :from 0 :to program-count)
        spn-pnum (spinner :model spn-model
                          :size [300 :by 32])
        pan-south (horizontal-panel :items [spn-pnum]
                                    :border (make-border "Program Number"))
        jb-cancel (button :text "Cancel")
        jb-save (button :text "Store")
        pan-main (border-panel :north pan-north
                               :center pan-center
                               :south pan-south)
        dia (dialog :content pan-main
                    :type :plain
                    :options [jb-cancel jb-save]
                    :default-option jb-save
                    :title "Store Program"
                    :modal? true
                    :on-close :nothing
                    :parent parent)]
    (listen jb-clear-name :action (fn [ev](config! tx-name :text "")))
    (listen jb-clear-remarks :action (fn [ev](config! tx-remarks :text "")))
    (listen jb-cancel :action (fn [ev]
                                (.dispose dia)
                                (.enable-store-program-mode ed false)
                                (.status ed "Store canceled")))
    (listen jb-save :action (fn [ev]
                             (let [dst-pnum (.getValue spn-pnum)
                                   dst-name (config tx-name :text)
                                   dst-remarks (config tx-remarks :text)
                                   dst-data (.current-program-data bank)]
                               (.push-undo-state ed)
                               (.set-program! bank dst-pnum nil dst-name dst-data dst-remarks)
                               (.enable-store-program-mode ed false)
                               (.dispose dia)
                               (.sync-ui ed)
                               (.status ed (format "%s stored to %s" dst-name dst-pnum)))))
    (pack! dia)
    (show! dia)))

; ---------------------------------------------------------------------- 
;                          Bank Editor Constructor

(defn bank-editor [bank]
  (let [max-undo (cadejo.config/maximum-undo-count)
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo" :max-depth max-undo)
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo" :max-depth max-undo)
        jb-init (button :text "Init" :id :jb-bank-init)
        jb-open (button :text "Open" :id :jb-bank-open)
        jb-save (button :text "Save" :id :jb-bank-save)
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-edit-fn (button :text "(Fn)" :id :jb-bank-edit-function)
        jb-edit-data (button :text "Editor" :id :jb-bank-editor)
        jb-store (toggle :text "Store" :id :jb-bank-store)
        jb-transmit (button :text "Transmit" :id :jb-bank-transmit)
        jb-new-program (button :text "+" :id :jb-bank-new-program)
        jb-delete-program (button :text "-" :id :jb-bank-delete-program)
        jb-help (button :text "Help" :id :jb-bank-help)
        pan-north (grid-panel :rows 1
                              :items [jb-init jb-open jb-save
                                      jb-undo jb-redo jb-help]
                              :border (ssb/empty-border :top 4 :right 4
                                                        :bottom 4 :left 4))
        ;; Status panel (south)
        lab-format (label :text (format "Format %s" (.data-format bank))
                          :border (BorderFactory/createLoweredBevelBorder))
        lab-status (label :text "<status>"
                          :border (BorderFactory/createLoweredBevelBorder))
        lab-filename (label :text "<filename>"
                          :border (BorderFactory/createLoweredBevelBorder))
        pan-status (border-panel :west lab-format
                                 :center lab-filename
                                 :south lab-status
                                 :border (ssb/empty-border
                                          :top 4 :right 4 :bottom 4 :left 4))
        ;; Info panel (west)
        tx-name (text :text (.bank-name bank)
                      :multi-line? false)
        tx-remarks (text :text (.bank-remarks bank)
                         :multi-line? true
                         :size [200 :by 200])
        sp-remarks (scrollable tx-remarks
                               :border (make-border "Remarks"))
        tx-functions (text :text (let [sb (StringBuilder.)]
                                   (doseq [fid (.function-keys bank)]
                                     (.append sb (format "%s\n" fid)))
                                   (.toString sb))
                           :multi-line? true
                           :editable? false)
        pan-info (border-panel 
                  :north (horizontal-panel :items [tx-name]
                                           :border (make-border "Name"))
                  :center sp-remarks
                  :south (horizontal-panel :items [tx-functions]
                                           :border (make-border "Functions")))
        ;; Program panel (center)
        lst-programs (listbox :model (create-program-list bank))
        pan-programs-south (grid-panel :rows 1 
                                       :items [jb-delete-program
                                               jb-new-program
                                               jb-transmit
                                               jb-edit-fn
                                               jb-edit-data
                                               jb-store])
        pan-programs (border-panel :center (scrollable lst-programs)
                                   :south pan-programs-south
                                   :border (ssb/empty-border 
                                            :top 4 :right 2 :bottom 0 :left 2))
        pan-main (border-panel :north pan-north
                               :west pan-info
                               :center pan-programs
                               :south pan-status
                               :id :pan-bank-main)
        widgets {:jb-init jb-init
                 :jb-open jb-open
                 :jb-save jb-save
                 :jb-undo jb-undo
                 :jb-redo jb-redo
                 :jb-edit-fn jb-edit-fn
                 :jb-edit-data jb-edit-data
                 :jb-store jb-store
                 :jb-transmit jb-transmit
                 :jb-new-program jb-new-program
                 :jb-delete-program jb-delete-program
                 :jb-help jb-help
                 :lab-format lab-format
                 :lab-status lab-status
                 :lab-filename lab-filename
                 :tx-name tx-name
                 :tx-remarks tx-remarks
                 :lst-programs lst-programs
                 :pan-main pan-main}
         ed (BankEditor. bank
                         widgets
                         (atom true)    ; selection-enable flag
                         (atom false)   ; store mode flag
                         undo-stack
                         redo-stack
                         (atom "")
                         )]
    (listen jb-init :action (fn [ev](.new-bank ed)))
    (listen jb-open :action (fn [ev](.open-bank ed)))
    (listen jb-save :action (fn [ev](.save-bank ed)))
    (listen jb-undo :action (fn [ev](.undo ed)))
    (listen jb-redo :action (fn [ev](.redo ed)))
    (listen jb-edit-fn :action (fn [ev](.edit-program-function ed)))
    (listen jb-edit-data :action (fn [ev](.edit-program-data ed)))
    (listen jb-transmit :action 
            (fn [n]
              (let [pnum (.getSelectedIndex lst-programs)]
                (.program-change bank pnum)
                (.status ed (format "[%3d] Transmitted" pnum)))))
    (listen jb-store :action 
            (fn [ev]
              (let [pnum (.current-program-number bank)]
                (if (.isSelected jb-store)
                  (do 
                    (.enable-list-selection ed false)
                    (.clearSelection lst-programs)
                    (.enable-list-selection ed true)
                    (.enable-store-program-mode ed true)
                    (.status ed (format "Current [%03d], Select destination."
                                        pnum)))
                (do
                  (.enable-list-selection ed false)
                  (.enable-store-program-mode ed false)
                  (.setSelectedIndex lst-programs pnum)
                  (.enable-list-selection ed true)
                  (.status ed "Store program canceled"))))))
    (listen jb-new-program :action (fn [ev](.new-program ed)))
    (listen jb-delete-program :action (fn [ev](.delete-program ed)))
    (listen jb-help :action (fn [ev](.show-help ed)))
    (.addListSelectionListener
     lst-programs
     (proxy [ListSelectionListener][]
       (valueChanged [ev]
         (cond 
          ;; do nothing 
          (.getValueIsAdjusting lst-programs) 
          nil 
          ;; pop up store dialog
          (.store-program-mode? ed)
          (let [pnum (.getSelectedIndex lst-programs)]
            (store-program-dialog ed bank pnum)
            (config! jb-store :selected? false))
          ;; program-change
          (.list-selection-enabled? ed)
          (let [pnum (.getSelectedIndex lst-programs)]
            (.program-change bank pnum))
          ;; do nothing by default
          :default
          nil))))
    (listen tx-name :caret-update (fn [ev]
                                    (let [txt (config tx-name :text)]
                                      (.bank-name! bank txt))))
    (listen tx-remarks :caret-update (fn [ev]
                                       (let [txt (config tx-remarks :text)]
                                         (.bank-remarks! bank txt))))

    (.filename ed (cadejo.config/config-path))
    ed))
                                      


;;; -------------------------- TEST 
;;; -------------------------- TEST 
;;; -------------------------- TEST 

(require '[cadejo.midi.dummy-bank :as dummy])

(def bnk1 dummy/bnk1)
(.program-change bnk1 0)
(def ed (bank-editor bnk1))
(def pan-main (.widget ed :pan-main))

(def f (frame :title "TEST BANK EDITOR"
              :content pan-main
              :on-close :dispose
              ))


(.write-bank bnk1 "/home/sj/.cadejo/apple.dummy")
(pack! f)
(show! f)
