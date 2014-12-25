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

(defn alias-editor [performance]
  (let [ied (ied/instrument-editor performance)
        osc (let [oed (osced/osc-editor performance ied)
                  ned (noiseed/noise-editor performance ied)
                  pan (ss/scrollable
                       (ss/horizontal-panel :items [(.widget oed :pan-main)
                                                    (.widget ned :pan-main)]))]
              (subedit/subeditor-wrapper [oed ned] pan))
        mix (mixer/mixer performance ied)
        filter (let [fed (filter/filter-editor performance ied)
                     efx (efxed/efx-editor performance ied)
                     pan (ss/scrollable
                          (ss/horizontal-panel :items [(.widget fed :pan-main)
                                                       (.widget efx :pan-main)]))]
                 (subedit/subeditor-wrapper [fed efx] pan))
        env (enved/envelope-editor performance ied)
        lfo (let [led (lfoed/lfo-editor performance ied)
                  sed (snh/sample-and-hold performance ied)
                  pan (ss/scrollable
                       (ss/horizontal-panel :items [(.widget led :pan-main)
                                                    (.widget sed :pan-main)]))]
              (subedit/subeditor-wrapper [led sed] pan))
        stepper (let [sed (steped/step-counter-editor performance ied)
                      ded (dived/divider-editor performance ied)
                      pan (ss/scrollable
                           (ss/horizontal-panel :items [(.widget sed :pan-main)
                                                        (.widget ded :pan-main)]))]
                  (subedit/subeditor-wrapper [sed ded] pan))
        matrix (matrix/matrix-editor performance ied)]
    (.add-sub-editor! ied "Osc" :wave :sine2 "Edit Oscillators" osc)
    (.add-sub-editor! ied "Mixer" :general :mixer "Mixer" mix)
    (.add-sub-editor! ied "Filter" :filter :low "Filter and Efects editor" filter)
    (.add-sub-editor! ied "Env" :env :adsr "Envelope editor" env)
    (.add-sub-editor! ied "LFO" :wave :triangle "LFO and Sample-HOLD editor" lfo)
    (.add-sub-editor! ied "Stepper" :wave :step "Stepper and Divider editor" stepper)
    (.add-sub-editor! ied "Matrix" :general :matrix "Control Matrix Editor" matrix)
    (.show-card-number! ied 1)
    ied))
