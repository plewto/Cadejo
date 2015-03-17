;; Vibrato, port, pitch env

(ns cadejo.instruments.algo.editor.pitch-editor
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.op-selection-panel :as osp])
  (:require [cadejo.instruments.algo.editor.pitch-panel :as pp])
  (:require [cadejo.instruments.algo.editor.envelope-panel :as ep])
  (:require [cadejo.instruments.algo.editor.vibrato-panel :as vp])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing :as drw])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box))

(defn pitch-editor [ied]
  (println "-->     Pitch")
  (let [drw (let [d (drw/native-drawing 800 520)]
              (.background! d (lnf/background))
              d)
        x-vib 60
        x-pitch (+ x-vib 420)
        x-algo (+ x-vib 180)
        y-vib 500
        y-pitch y-vib
        y-algo (- y-vib 280)
        vib (vp/vibrato-panel drw [x-vib y-vib] ied)
        pitch (pp/pitch-panel drw [x-pitch y-pitch] ied)
        selection-action (fn [b _]
                           (let [card (.get-property b :card-number)]
                             (.show-card-number! ied card)))
        osp (osp/op-selection-panel drw [x-algo y-algo] selection-action)
        envpan (ep/envelope-panel :pitch ied)
        pan-main (ss/scrollable
                  (ss/horizontal-panel :items [(.canvas drw)
                                               (Box/createHorizontalStrut 16)
                                               (:pan-main envpan)]
                                       :background (lnf/background)))
        widget-map {:pan-main pan-main}]
    (osp/highlight! :pitch osp)
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
        (doseq [sp (list vib pitch envpan)]
          ((:sync-fn sp)))
        (.render drw)))))
