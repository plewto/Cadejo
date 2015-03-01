(ns cadejo.instruments.masa.editor.amp-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn- slider [drw p id action]
  (let [s (slider/slider (.tool-root drw) p slider-length
                         min-amp-db max-amp-db
                         :id id
                         :orientation :vertical
                         :drag-action action
                         :rim-color [0 0 0 0]
                         :track1-color (lnf/passive-track-color)
                         :track2-color (lnf/active-track-color))]
    s))

(defn- label [drw p0 txt]
  (text/text (.root drw) p0 txt
             :color (lnf/text-color)
             :style :mono
             :size 6))

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
                           
        s-amp (slider drw [xc y2] :amp amp-action)
        widget-map {}]
    (rect/rectangle (.root drw) p0 p1 :color (lnf/major-border-color))
    (text/text (.root drw) [(+ x0 26)(- y0 h -20)] "Amp"
               :style :sans
               :size 8
               :color (lnf/text-color))
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
              c (cond (or (= i 0.0)(= i 0.5)(= i 1.0)) (lnf/major-tick-color)
                      :default (lnf/minor-tick-color))]
          (line/line (.root drw)[xa y][xb y]
                     :style :solid
                     :color c)
          (label drw [(- xc 6)(+ y2 20)] "db"))))
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
