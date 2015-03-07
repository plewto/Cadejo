;; delay, reverb. output 
;;
(ns cadejo.instruments.algo.editor.efx-editor
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory ])
  (:require [cadejo.instruments.algo.editor.op-selection-panel :as osp ])
  (:require [cadejo.instruments.algo.editor.delay-panel :as dp ])
  (:require [cadejo.instruments.algo.editor.reverb-panel :as rp ])
  (:require [cadejo.instruments.algo.editor.amp-panel :as ap ])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.subedit])
  (:require [sgwr.components.drawing :as drw])
  (:require [seesaw.core :as ss]))

(def ^:private width 750)
(def ^:private height 560)

(defn efx-editor [ied]
  (println "-->     Effects")
  (let [drw (let [d (drw/native-drawing 900 700)]
              (.background! d (lnf/background))
              d)
        pan-main (ss/scrollable (ss/border-panel :center (.canvas drw)
                                                 :background (lnf/background)))
        x-delay 50
        x-reverb (+ x-delay 430)
        x-amp x-delay
        x-algo 335
        y-delay 270
        y-reverb y-delay
        y-amp (+ y-delay 260)
        y-algo (- y-amp  30)
        delay-pan (dp/delay-panel drw [x-delay y-delay] ied)
        reverb-pan (rp/reverb-panel drw [x-reverb y-reverb] ied)
        amp-pan (ap/amp-panel drw [x-amp y-amp] ied)
        selection-action (fn [b _]
                           (let [card (.get-property b :card-number)]
                             (.show-card-number! ied card)))
        osp (osp/op-selection-panel drw [x-algo y-algo] selection-action false)
        widget-map {:pan-main pan-main}]
    (osp/highlight! :fx osp)
    (.render drw)
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
        (doseq [sp (list delay-pan reverb-pan amp-pan)]
          ((:sync-fn sp)))
        (.render drw)))))
  
