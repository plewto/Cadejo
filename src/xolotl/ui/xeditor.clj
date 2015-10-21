(println "--> xolotl.ui.xeditor")
(ns xolotl.ui.xeditor
  (:require [cadejo.ui.cadejo-frame :as cframe])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.icon])
  (:require [xolotl.ui.bank-editor])
  (:require [xolotl.ui.clock-editor])
  (:require [xolotl.ui.factory :as factory])
  (:require [seesaw.core :as ss])
  )


(def logo (cadejo.ui.util.icon/logo "xolotl" :small))

(defn xolotl-editor [xobj]
  (let [cf (cframe/cadejo-frame "Xolotl" :xolotl 
                                [:exit :about :skin :progress-bar :path])
        bed (cadejo.ui.midi.node-editor/basic-node-editor :xolotl xobj false)
        pan-main (.widget cf :pan-center)
        bank (.program-bank xobj)
        bank-editor (xolotl.ui.bank-editor/bank-editor bed bank)
        clock-editor (xolotl.ui.clock-editor/clock-editor bed)
        pan-west (factory/border-panel :center (:pan-main bank-editor)
                                       :north (:pan-main clock-editor)
                                       )
        ]

    (.cframe! bed cf)
    (.set-icon! bed logo)
    (ss/config! pan-main :west pan-west)
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
