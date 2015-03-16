(ns cadejo.instruments.masa.editor.amp-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.tools.slider :as slider]))

(defn amp-panel [drw ied p0]
  (let [w 90
        h 290
        [x0 y0] p0
        x1 (+ x0 w)
        xc (math/mean x0 x1)
        y1 (- y0 h)
        y2 (- y0 70)
        p1 [x1 y1]
        amp-action (fn [s _]
                     (let [param :amp 
                           db (slider/get-slider-value s)
                           amp (math/db->amp db)]
                       (.set-param! ied param amp)
                       (.status! ied (format "[:amp] -> %5.3f (%d db)" (float amp)(int db)))))
        s-amp (sfactory/vslider drw ied :amp [xc y2] min-amp-db max-amp-db amp-action)
        widget-map {}]
    (rect/rectangle (.root drw) p0 p1 :color (lnf/minor-border) :radius 12)
    (sfactory/text drw [(+ x0 26)(- y0 h -20)] "Amp")
    (let [xa (+ x0 35)
          xb (+ xa 20)
          ya y2
          yb (- ya slider-length)
          y-diff slider-length
          n 4.0
          y-delta (/ y-diff n)
          ]
      (doseq [i (range 0.0 1.125 0.125)]
        (let [y (- ya (* i 4 y-delta))
              c (cond (or (= i 0.0)(= i 0.5)(= i 1.0)) (lnf/major-tick)
                      :default (lnf/minor-tick))]
          (line/line (.root drw)[xa y][xb y]
                     :style :solid
                     :color c)
          (sfactory/label drw [(- xc 6)(+ y2 20)] "db"))))
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      
      (widget [this key]
        (get widget-map key))
      
      (parent [this] ied)
      
      (parent! [this _] ied) 
      
      (status! [this msg]
        (.status! ied msg))
      
      (warning! [this msg]
        (.warning! ied msg))
      
      (set-param! [this param value]
        (.set-param! ied param val))
      
      (init! [this]
        (.set-param! ied :amp 0.2))
      
      (sync-ui! [this]
        (let [dmap (.current-data (.bank (.parent-performance ied)))
              amp (:amp dmap)
              db (math/amp->db amp)]
          (slider/set-slider-value! s-amp db false))) )))
