(ns xolotl.ui.bank-editor
  (:require [xolotl.program-bank])
  (:require [xolotl.ui.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           java.util.Vector
           javax.swing.event.ListSelectionListener))
           

(def bank-length xolotl.program-bank/bank-length)

;; Holds Program bank editor in JPanel
;; widgets:
;;    list in scroll-pane
;;    spinner - program store location
;;    button  - store program
;;    button  - open file 
;;    button  - save file
;;    button  - init bank
;;    text-field - program name

(defn- format-program-cell [slot programs]
  (let [pname (nth programs slot)]
    (format "[%03d]  %s" slot pname)))

;; (defn- create-program-list [bank]
;;   (let [acc* (atom [])
;;         programs (.program-names bank)]
;;     (dotimes [slot xolotl.program-bank/bank-length]
;;       (swap! acc* (fn [q](conj q (format-program-cell slot programs)))))
;;     @acc*))

(defn- create-program-list [bank]
  (let [vcc (Vector. bank-length)]
    (doseq [pn (.program-names bank)]
      (.add vcc pn))
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
                         (println "ISSUE: bank-editor.store-action NOT implemented")
                         ))
        open-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (println "ISSUE: bank-editor.open-action NOT implemented")
                        ))
        save-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (println "ISSUE: bank-editor.save-action NOT implemented")
                        ))

        selection-listener (proxy [ListSelectionListener][]
                             (valueChanged [_]
                               (cond
                                 (.getValueIsAdjusting lst-programs) ; do nothing
                                 nil
                                 
                                 @enable-selection-listener*
                                 (let [slot (.getSelectedIndex lst-programs)]
                                   (reset! enable-selection-listener* false)
                                   (.use-program bank slot)
                                   (if @parent-editor* (.sync-ui! @parent-editor*))
                                   (reset! enable-selection-listener* true))
                                 
                                 :else    ; do nothing
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
                                        (.status! @parent-editor* "Xolotl Bank Initilized"))
                                      (.status! @parent-editor* "Bank initilization canceld")))))
    
    (.addActionListener jb-store store-action)
    (.addActionListener jb-open open-action)
    (.addActionListener jb-save save-action)
    (.addListSelectionListener lst-programs selection-listener)
    
    {:pan-main pan-main
     :set-parent-editor (fn [ed](reset! parent-editor* ed))
     :sync-fn sync-fn}))
