(ns cadejo.instruments.combo.combo-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.instrument-editor-framework :as framework])
  (:require [cadejo.ui.instruments.instrument-editor])
  (:require [seesaw.core :as ss])
  )

;; (defn combo-editor [performance]
;;   (trace-enter "combo-editor")
;;   (let [ied (cadejo.ui.instruments.instrument-editor-framework/instrument-editor-framework performance)]
;;     (trace-mark "type ied" (type ied))
;;     (trace-exit "combo-editor")
;;     ied))


(defn combo-editor [performance]
  (let [parent* (atom nil)
        widget-map {
                    }
                 
        ied (reify cadejo.ui.instruments.instrument-editor/InstrumentEditor
              
              (set-parent! [this iefw]
                (reset! parent* iefw))

              (widgets [this]
                (merge (.widgets @parent*)
                       widget-map))

              (widget [this key]
                (or (get (.widgets this) key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))
              
              (status! [this msg]
                (.status! @parent* msg))

              (warning! [this msg]
                (.warning! @parent* msg))

              (push-undo-state! [this msg]
                (.push-undo-state! @parent* msg))

              (push-redo-state! [this msg]
                (.push-redo-state! @parent* msg))

              (sync-ui! [this]
                (println "(Combo) InstrumentEditor.sync-ui not implemented")
                ))
        ieframework (let [ifw (framework/instrument-editor-framework performance)]
                      (.client-editor! ifw ied)
                      ifw)
        ]
    ied))
              
              
                    
