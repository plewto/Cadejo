(println "-->    alias editor")

(ns cadejo.instruments.alias.editor.alias-editor
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.instruments.alias.editor.osc-editor :as osced])
  (:require [cadejo.instruments.alias.editor.noise-editor :as noiseed])
  (:require [cadejo.instruments.alias.editor.mixer-editor :as mixer])
  (:require [cadejo.instruments.alias.editor.filter-editor :as filter])
  (:require [cadejo.instruments.alias.editor.efx-editor :as efxed])
  (:require [cadejo.instruments.alias.editor.env-editor :as enved])
  (:require [cadejo.instruments.alias.editor.lfo-editor :as lfoed])
  (:require [cadejo.instruments.alias.editor.snh-editor :as snh])
  (:require [cadejo.instruments.alias.editor.stepper-editor :as steped])
  (:require [cadejo.instruments.alias.editor.divider-editor :as dived])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [seesaw.core :as ss]))

(def osc-icon (lnf/read-icon :wave :sine2))
(def noise-icon (lnf/read-icon :wave :noise))
(def mixer-icon (lnf/read-icon :general :mixer))
(def filter-icon (lnf/read-icon :filter :band))
(def env-icon (lnf/read-icon :env :adsr))
(def lfo-icon (lnf/read-icon :wave :triangle))
(def step-icon (lnf/read-icon :wave :step))
(def matrix-icon (lnf/read-icon :general :matrix))

(defn alias-editor [performance]
  (let [ied (ied/instrument-editor performance)
        osc (osced/osc-editor performance ied)
        noise (noiseed/noise-editor performance ied)
        pan-osc (ss/scrollable 
                 (ss/horizontal-panel :items [(.widget osc :pan-main)
                                            (.widget noise :pan-main)]))
        mix (mixer/mixer performance ied)
        filter (filter/filter-editor performance ied)
        efx (efxed/efx-editor performance ied)
        pan-filter (ss/scrollable
                    (ss/horizontal-panel :items [(.widget filter :pan-main)
                                                 (.widget efx :pan-main)]))
        env (enved/envelope-editor performance ied)
        lfo (lfoed/lfo-editor performance ied)
        snh (snh/sample-and-hold performance ied)
        pan-lfo (ss/scrollable
                 (ss/horizontal-panel :items [(.widget lfo :pan-main)
                                              (.widget snh :pan-main)]))
        stepper (steped/step-counter-editor performance ied)
        divider (dived/divider-editor performance ied)
        pan-stepper (ss/scrollable 
                     (ss/horizontal-panel :items [(.widget stepper :pan-main)
                                                  (.widget divider :pan-main)]))
        matrix (matrix/matrix-editor performance ied)
        ]
    (.add-sub-editor! ied "Osc" osc-icon (subedit/subeditor-wrapper [osc noise] pan-osc))
    (.add-sub-editor! ied "Mixer" mixer-icon mix)
    (.add-sub-editor! ied "Filter/Efx" filter-icon 
                      (subedit/subeditor-wrapper [filter efx] pan-filter))
    (.add-sub-editor! ied "Env" env-icon env)
    (.add-sub-editor! ied "LFO" lfo-icon
                      (subedit/subeditor-wrapper [lfo snh] pan-lfo))
    (.add-sub-editor! ied "Stepper" step-icon
                      (subedit/subeditor-wrapper [stepper divider] pan-stepper))
    (.add-sub-editor! ied "Matrix" matrix-icon matrix)
    ied))
