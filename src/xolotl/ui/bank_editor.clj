(println "    --> xolotl.ui.bank-editor")
(ns xolotl.ui.bank-editor
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [xolotl.program-bank])
  (:require [xolotl.ui.factory :as factory])
  (:require [xolotl.util :as util])
  (:require [seesaw.core :as ss])
  (:require [seesaw.chooser])
  (:import java.awt.event.ActionListener
           java.util.Vector
           javax.swing.event.ListSelectionListener))
           

(def bank-length xolotl.program-bank/bank-length)
(def bank-extension xolotl.program-bank/bank-extension)

(def file-filter (seesaw.chooser/file-filter
                  (format "%s Bank" bank-extension)
                  (fn [f] 
                    (path/has-extension? (.getAbsolutePath f) bank-extension))))

;; Holds Program bank editor in JPanel
;; widgets:
;;    list in scroll-pane
;;    spinner - program store location
;;    button  - store program
;;    button  - open file 
;;    button  - save file
;;    button  - init bank
;;    text-field - program name


(defn- format-program-cell [slot pname]
  (format "[%03d] %s" slot pname))

(defn- create-program-list [bank]
  (let [vcc (Vector. bank-length)
        slot* (atom 0)
        plst (.program-names bank)]
    (doseq [pn plst]
      (.add vcc (format-program-cell @slot* pn))
      (swap! slot* inc))
    vcc))

(defn bank-init-warning [parent]
  (let [selection* (atom false)
        msg "Initialize Xolotl bank? All unsaved programs will be lost"
        yes-fn (fn [d] 
                 (swap! selection* (fn [n] true))
                 (ss/return-from-dialog d true))
        no-fn (fn [d] 
                (swap! selection* (fn [n] false))
                (ss/return-from-dialog d false))
        dia (ss/dialog 
             :content msg
             :option-type :yes-no
             :type :warning
             :default-option :no
             :modal? true
             :parent parent
             :success-fn yes-fn
             :no-fn no-fn)]
    (ss/pack! dia)
    (ss/show! dia)
    @selection*))

(defn- save-dialog [editor bank]
  (let [cancel (fn [& _](.status! editor "Bank Save Canceled"))
        success (fn [_ f]
                  (let [abs (path/append-extension (.getAbsolutePath f)
                                                   bank-extension)]
                    (if (cadejo.ui.util.overwrite-warning/overwrite-warning
                         (:pan-main editor) "Xolotl Bank" abs)
                      (if (.write-bank bank abs)
                        (.status! editor (format "Bank saved to '%s'" abs))
                        (.warning! editor (format "Can not save bank to '%s'" abs)))
                      (cancel))))
        dia (seesaw.chooser/choose-file
             :type "Save Xolotl Bank"
             :multi? false
             :selection-mode :files-only
             :filters [file-filter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]
    dia))

(defn- open-dialog [editor bank]
  (let [cancel (fn [& _](.status! editor "bank Read Canceled"))
        success (fn [_ f]
                  (let [abs (.getAbsolutePath f)]
                    (if (.read-bank! bank abs)
                      (do
                        (.sync-ui! editor)
                        (.status! editor (format "Read Xolotl Bank '%s'" abs)))
                      (do
                        (.warning! bank (format "Could not read '%s' as Xolotl bank"))))))
        dia (seesaw.chooser/choose-file
             :type "Open Xolotl bank"
             :multi? false
             :selection-mode :files-only
             :filters [file-filter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]
    dia))
                        
                        


;; Creates bank-editor panel
;; args:
;;   parent-editor - an instance of NodeEditor for xolotl
;;   bank - an instance of ProgramBank
;;
;; RETURNS: map  keys  :pan-main -> JPanel
;;                     :sync-fn  -> (fn [])
;;
(defn bank-editor [bank jb-open jb-save jb-init]
  (let [parent-editor* (atom nil)
        enable-selection-listener* (atom true)
        lst-programs (ss/listbox :model (create-program-list bank)
                                 :size [180 :by 320])
        rebuild-list (fn []
                       (let [model (create-program-list bank)
                             slot (.getSelectedIndex lst-programs)]
                         (ss/config! lst-programs :model model)
                         (.setSelectedIndex lst-programs slot)))
        spin-slot (factory/spinner 0 (dec xolotl.program-bank/bank-length) 1)
        jb-store (factory/button "Store" :font :small)
        tf (factory/text-field "Name")
        pan-south (factory/border-panel :center spin-slot
                                        :east jb-store
                                        :south (:pan-main tf))
        pan-center (ss/horizontal-panel :items [(ss/scrollable lst-programs)]
                                        :border (factory/padding))
        pan-main (factory/border-panel :center pan-center
                                       :south pan-south
                                       )
        store-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (let [prog (.clone (.current-program bank))
                               slot (int (.getValue spin-slot))
                               name (.getText (:text-field tf))]
                           (.program-name! prog name)
                           (.store-program! bank slot prog)
                           (rebuild-list)
                           (.status! @parent-editor* (format "Xolotl program '%s' saved" name)))))
        selection-listener (proxy [ListSelectionListener][]
                             (valueChanged [_]
                               (cond
                                 (.getValueIsAdjusting lst-programs) ; do nothing
                                 nil

                                 @enable-selection-listener*
                                 (let [slot (.getSelectedIndex lst-programs)
                                       xobj (.node @parent-editor*)]
                                   (reset! enable-selection-listener* false)
                                   (.use-program xobj slot)
                                   (reset! enable-selection-listener* true))

                                 :else  ; do nothing
                                 nil)))
        
        sync-fn (fn [prog]
                  (reset! enable-selection-listener* false)
                  (let [slot (.current-slot bank)]
                    (.setSelectedIndex lst-programs slot)
                    (.setValue spin-slot (int slot))
                    (.setText (:text-field tf) (.program-name prog))
                    (reset! enable-selection-listener* true)))]

    (.addActionListener jb-init (proxy [ActionListener][]
                                  (actionPerformed [_]
                                    (if (bank-init-warning @parent-editor*)
                                      (do
                                        (.init! bank)
                                        (.setListData lst-programs (create-program-list bank))
                                        (.use-program bank 0)
                                        (.sync-ui! @parent-editor*)
                                        (.status! @parent-editor* "Xolotl Bank Initialized"))
                                      (.status! @parent-editor* "Bank initialization canceled")))))
    
    (.addActionListener jb-store store-action)
    (.addActionListener jb-save (proxy [ActionListener][]
                                  (actionPerformed [_]
                                    (save-dialog @parent-editor* bank))))
    (.addActionListener jb-open (proxy [ActionListener][]
                                  (actionPerformed [_]
                                    (try
                                      (open-dialog @parent-editor* bank)
                                      (rebuild-list)
                                      (catch Exception ex
                                        (.warning! @parent-editor* (.getMessage ex)))))))
                                                   
    (.addListSelectionListener lst-programs selection-listener)
    {:pan-main pan-main
     :set-parent-editor (fn [ed](reset! parent-editor* ed))
     :sync-fn sync-fn}))
