(ns cadejo.instruments.combo.combo-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.ui.instruments.instrument-editor])
  (:require [seesaw.core :as ss])
  )

(defn combo-editor [performance]
  (trace-enter "combo-editor")
  (let [ied (cadejo.ui.instruments.instrument-editor/instrument-editor performance)]
    (trace-mark "type ied" (type ied))
    (trace-exit "combo-editor")
    ied))
                    
