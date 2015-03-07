(ns cadejo.instruments.algo.editor.lfo-editor
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.lfo-panel :as lfp])
  (:require [cadejo.instruments.algo.editor.op-selection-panel :as osp :reload true])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing :as drw])
  (:require [seesaw.core :as ss]))


(defn lfo-editor [ied]
  (println "-->     LFO")
  (let [drw (let [d (drw/native-drawing 1300 600)]
              (.background! d (lnf/background))
              d)
        x-lfo1 20
        x-lfo2 (+ x-lfo1 400)
        x-osp (+ x-lfo2 450)
        y-lfo1 500
        y-lfo2 y-lfo1
        y-osp (- y-lfo1 160)
        lfo1 (lfp/lfo-panel 1 drw [x-lfo1 y-lfo1] ied)
        lfo2 (lfp/lfo-panel 2 drw [x-lfo2 y-lfo2] ied)
        selection-action (fn [b _]
                           (let [card (.get-property b :card-number)]
                             (.show-card-number! ied card)))
        osp (osp/op-selection-panel drw [x-osp y-osp] selection-action)
        pan-main (ss/scrollable
                  (ss/border-panel :center (.canvas drw)
                                   :background (lnf/background)
                                   :size [1665 :by 600])
                  :hscroll :as-needed
                  :vscroll :as-needed)
        widget-map {:pan-main pan-main}]
    (osp/highlight! :lfo osp)
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      (widget [this key] (get widget-map key))
      (parent [this] ied)
      (parent! [this _] ied) ;; ignore
      (status! [this msg](.status! ied msg))
      (warning! [this msg](.warning! ied msg))
      (set-param! [this param val](.set-param! ied param val))
      (init! [this]
        ;; not implemented
        )
      (sync-ui! [this]
        (doseq [sp (list lfo1 lfo2)]
          ((:sync-fn sp)))
        (.render drw)))))
