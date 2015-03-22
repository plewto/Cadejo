(println "-->    alias editor")

(ns cadejo.instruments.alias.editor.alias-editor
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  ;; (:require [cadejo.ui.instruments.subedit :as subedit])
  ;; (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.instruments.alias.editor.osc-editor :as osced])
  (:require [cadejo.instruments.alias.editor.noise-editor :as noiseed])
  ;; (:require [cadejo.instruments.alias.editor.mixer-editor :as mixer])
  ;; (:require [cadejo.instruments.alias.editor.filter-editor :as filter])
  ;; (:require [cadejo.instruments.alias.editor.efx-editor :as efxed])
  ;; (:require [cadejo.instruments.alias.editor.env-editor :as enved])
  ;; (:require [cadejo.instruments.alias.editor.lfo-editor :as lfoed])
  ;; (:require [cadejo.instruments.alias.editor.snh-editor :as snh])
  ;; (:require [cadejo.instruments.alias.editor.stepper-editor :as steped])
  ;; (:require [cadejo.instruments.alias.editor.divider-editor :as dived])
  ;; (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [seesaw.core :as ss]))


(defn alias-editor [performance]
  (let [ied (ied/instrument-editor performance)
        osc1 (osced/osc-editor 1 ied)
        osc2 (osced/osc-editor 2 ied)
        osc3 (osced/osc-editor 3 ied)
        nse (noiseed/noise-editor ied)
        ]
    (.add-sub-editor! ied "Osc 1" :wave :sawpos "Oscillator 1" osc1)
    (.add-sub-editor! ied "Osc 2" :wave :pulse "Oscillator 2" osc2)
    (.add-sub-editor! ied "Osc 3" :wave :sine "Oscillator 3" osc3)
    (.add-sub-editor! ied "Noise" :wave :noise "Noise/Ring Modulator" nse)

    ied))


