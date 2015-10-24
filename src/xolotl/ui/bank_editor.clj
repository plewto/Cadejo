(ns xolotl.ui.bank-editor
  (:require [xolotl.program-bank])
  (:require [xolotl.ui.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           javax.swing.event.ListSelectionListener
           ))


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

(defn- create-program-list [bank]
  (let [acc* (atom [])
        programs (.program-names bank)]
    (dotimes [slot xolotl.program-bank/bank-length]
      (swap! acc* (fn [q](conj q (format-program-cell slot programs)))))
    @acc*))



;; Creates bank-editor panel
;; args:
;;   parent-editor - an instance of NodeEditor for xolotl
;;   bank - an instance of ProgramBank
;;
;; RETURNS: map  keys  :pan-main -> JPanel
;;                     :sync-fn  -> (fn [])
;;
(defn bank-editor [parent-editor bank jb-open jb-save jb-init]
  (let [enable-selection-listener* (atom true)
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
        init-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (println "ISSUE: bank-editor.init-action NOT implemented")
                        ))
        selection-listener (proxy [ListSelectionListener][]
                             (valueChanged [_]
                               (if @enable-selection-listener*
                                 (do 
                                   (println "ISSUE: bank-editor.selection-listener NOT implemented")
                                   ))))
        sync-fn (fn [prog] 
                  (reset! enable-selection-listener* false)
                  (let [slot (.current-slot bank)]
                    (.setSelectedIndex lst-programs slot)
                    (.setValue spin-slot slot)
                    (.setText (:text-field tf) (.program-name prog))
                    (reset! enable-selection-listener* true)))
        ]
    (.addActionListener jb-store store-action)
    (.addActionListener jb-open open-action)
    (.addActionListener jb-save save-action)
    (.addActionListener jb-init init-action)
    (.addListSelectionListener lst-programs selection-listener)
    
    {:pan-main pan-main
     :sync-fn sync-fn}))
