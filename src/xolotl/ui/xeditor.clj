(println "--> xolotl.ui.xeditor")
(ns xolotl.ui.xeditor
  (:require [cadejo.ui.cadejo-frame :as cframe])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.icon])
  (:require [xolotl.ui.factory :as factory])
  (:require [xolotl.ui.bank-editor])
  (:require [xolotl.ui.channel-editor])
  (:require [xolotl.ui.clock-editor])
  (:require [xolotl.ui.controller-editor])
  (:require [xolotl.ui.hold-editor])
  (:require [xolotl.ui.pitch-editor])
  (:require [xolotl.ui.rhythm-editor])
  (:require [xolotl.ui.velocity-editor])
  (:require [xolotl.ui.strum-editor])

  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           javax.swing.JLabel
           )
  )


(def logo (cadejo.ui.util.icon/logo "xolotl" :small))

(defn xseq-editor [xobj parent-editor seq-id]
  (let [lab-id (JLabel. (if (= seq-id :A) "Seq A" "Seq B"))
        
        chan-editor (xolotl.ui.channel-editor/channel-editor parent-editor seq-id)
        strum-editor (xolotl.ui.strum-editor/strum-editor parent-editor seq-id)
        rhythm-editor (xolotl.ui.rhythm-editor/rhythm-editor parent-editor seq-id)
        hold-editor (xolotl.ui.hold-editor/hold-editor parent-editor seq-id)
        ctrl-1-editor (xolotl.ui.controller-editor/controller-editor parent-editor seq-id 0)
        ctrl-2-editor (xolotl.ui.controller-editor/controller-editor parent-editor seq-id 1)
        pitch-editor (xolotl.ui.pitch-editor/pitch-editor parent-editor seq-id)
        velocity-editor (xolotl.ui.velocity-editor/velocity-editor parent-editor seq-id)
        pan-west (factory/vertical-panel (:pan-main chan-editor)
                                         (factory/vertical-strut 16)
                                         (:pan-main strum-editor)
                                         )
        pan-center (factory/grid-panel 2 3
                                       (:pan-main rhythm-editor)
                                       (:pan-main hold-editor)
                                       (:pan-main ctrl-1-editor)
                                       (:pan-main pitch-editor)
                                       (:pan-main velocity-editor)
                                       (:pan-main ctrl-2-editor)
                                       )
        ;; rhythed (xolotl.ui.rhythm-editor/rhythm-editor parent-editor seq-id)

        ;; pan-north (factory/border-panel :north lab-id
        ;;                                 :center (:pan-main chaned))
        ;; pan-center (factory/vertical-panel (:pan-main rhythed))
                                           
        pan-main (factory/border-panel :west pan-west
                                       :center pan-center
                                       )
                                           
        sync-fn (fn []
                  (println (format "ISSUE: xseq-editor.sync-fn NOT implemented"))
                  )
        ]
    {:pan-main pan-main
     :sync-fn sync-fn}))




(defn xolotl-editor [xobj]
  (let [cf (cframe/cadejo-frame "Xolotl" :xolotl 
                                [:exit :about :skin :progress-bar :path])
        bed (cadejo.ui.midi.node-editor/basic-node-editor :xolotl xobj false)
        xseq-a-editor (xseq-editor xobj bed :a)
        xseq-b-editor (xseq-editor xobj bed :b)
        pan-main (.widget cf :pan-center)
        bank (.program-bank xobj)
        clock-editor (xolotl.ui.clock-editor/clock-editor bed)
        bank-editor (xolotl.ui.bank-editor/bank-editor bed bank)
        pan-west (factory/border-panel :north (:pan-main clock-editor)
                                       :center (:pan-main bank-editor)
                                       :border (factory/padding 16)
                                       )
        pan-center (factory/horizontal-panel (:pan-main xseq-a-editor)
                                              (:pan-main xseq-b-editor))
        pan-center (ss/card-panel :items [[(:pan-main xseq-a-editor) :A]
                                          [(:pan-main xseq-b-editor) :B]])
        toolbar (.widget cf :toolbar)
        jb-open (factory/button "Open")
        jb-save (factory/button "Save")
        jb-init (factory/button "Init")
        jb-reset (factory/button "Reset")
        tpan-transport (factory/radio '[["Stop" :stop]["Start" :start]] 1 2 :btype :toggle)
        tb-stop (:stop (:buttons tpan-transport))
        tb-start (:start (:buttons tpan-transport))
        
        ]
    (.add toolbar jb-init)
    (.add toolbar jb-open)
    (.add toolbar jb-save)
    (.add toolbar (factory/horizontal-strut))
    (.add toolbar jb-reset)
    (.add toolbar tb-stop)
    (.add toolbar tb-start)
    (.add toolbar (factory/horizontal-strut))
    
    
    (let [grp (ss/button-group)
          tb-a (ss/toggle :text "Seq A" :group grp)
          tb-b (ss/toggle :text "Seq B" :group grp)
          action (proxy [ActionListener][]
                   (actionPerformed [evn]
                     (let [src (.getSource evn)
                           id (.getClientProperty src :id)]
                       (ss/show-card! pan-center id))))]
      (.putClientProperty tb-a :id :A)
      (.putClientProperty tb-b :id :B)
      (.add toolbar tb-a)
      (.add toolbar tb-b)
      (.addActionListener tb-a action)
      (.addActionListener tb-b action))
    
    (.cframe! bed cf)
    (.set-icon! bed logo)
    (ss/config! pan-main :west pan-west)
    (ss/config! pan-main :center pan-center)
                
    (reify cadejo.ui.midi.node-editor/NodeEditor

      (cframe! [this cframe embed]
        (.cframe! bed this cframe embed))

      (cframe! [this cframe]
        (.cframe! bed cframe))

      (jframe [this]
        (.jframe bed))

      (set-icon! [this ico]
        (.set-icon! bed ico))

      (show! [this]
        (.show! bed))

      (hide! [this]
        (.hide! bed))

      (widgets [this]
        (.widgets bed))

      (widget [this key]
        (.widget bed key))

      (add-widget! [this key obj]
        (.add-widget! bed key obj))

      (node [this] xobj)

      (set-node! [this _]) ;; not implemented

      (set-path-text! [this msg]
        (.set-path-text! bed msg))

      (working [this flag]
        (.working bed flag))

      (status! [this msg]
        (.status! bed msg))

      (warning! [this msg]
        (.warning! bed msg))

      (update-path-text [this]
        (.update-path-text bed))

      (sync-ui! [this]
        ;; ISSUE Not implemented
        ))))
