(ns cadejo.instruments.algo.editor.algo-editor
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.instruments.algo.editor.overview :as overview])
  (:require [cadejo.instruments.algo.editor.freqamp-editor :as freqamp])
  (:require [seesaw.core :as ss])
)  


;; (defn algo-editor [performance]
;;   (let [ied (ied/instrument-editor performance)
;;         op123 (oped/op123 performance ied)
;;         op456 (oped/op456 performance ied)
;;         op78  (oped/op78 performance ied)
;;         opmap {1 (.widget op123 :op1)
;;                2 (.widget op123 :op2)
;;                3 (.widget op123 :op3)
;;                4 (.widget op123 :op4)
;;                5 (.widget op123 :op5)
;;                6 (.widget op123 :op6)
;;                7 (.widget op78 :op7)
;;                8 (.widget op78 :op8)}
;;         fbed (cadejo.instruments.algo.feedback-editor/feedback-efx-editor performance ied)]
;;     ied))


(defn algo-editor [performance]
  (let [ied (ied/instrument-editor performance)
        ;oed (overview/overview-editor performance ied)
        faed (freqamp/freqamp-editor performance ied)
        ]
    ;(.add-sub-editor! ied "Test" oed)
    (.add-sub-editor! ied "Main" faed)
    ied))



