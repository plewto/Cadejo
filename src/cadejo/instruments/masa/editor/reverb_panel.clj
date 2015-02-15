(ns cadejo.instruments.masa.editor.reverb-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.util.lnf :as lnf ])
  (:require [cadejo.ui.instruments.subedit])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn- slider [drw p id action]
  (let [s (slider/slider (.tool-root drw) p slider-length 0.0 1.0
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

(defn reverb-panel [drw ied p0]
  (let [data (fn [param] ;; Returns current program value of parameter
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        w 200
        h 290
        [x0 y0] p0
        x1 (+ x0 w)
        x-size (+ x0 slider-spacing)
        x-damp (+ x-size slider-spacing)
        x-mix (+ x-damp slider-spacing)
        y1 (- y0 h)
        y2 (- y0 70)
        y3 (+ y2 24)
        p1 [x1 y1]
        size-action (fn [s _](.set-param! ied :reverb-size (slider/get-slider-value s)))
        damp-action (fn [s _](.set-param! ied :reverb-damp (slider/get-slider-value s)))
        mix-action (fn [s _](.set-param! ied :reverb-mix (slider/get-slider-value s)))
        s-size (slider drw [x-size y2] :room-size size-action)
        s-damp (slider drw [x-damp y2] :reverb-damp damp-action)
        s-mix (slider drw [x-mix y2] :reverb-mix mix-action)
        widget-map {}]
  (rect/rectangle (.root drw) p0 p1 :color (lnf/border-color))
  (text/text (.root drw) [(+ x0 70)(- y0 h -20)] "Reverb" 
             :style :sans
             :size 8
             :color (lnf/text-color))
  (label drw [(+ x-size -12) y3] "Room")
  (label drw [(+ x-size -12) (+ y3 12)] "Size")
  (label drw [(+ x-damp -12) y3] "Damp")
  (label drw [(+ x-mix -10) y3] "Mix")
  ;; Rules
  (let [xa (+ x0 40)
        xb (+ x0 160)
        ya y2
        yb (- ya slider-length)
        y-diff slider-length
        n 4.0
        y-delta (/ y-diff n)]
    (doseq [i (range 0.0 1.25 0.25)]
      (let [y (- ya (* i 4 y-delta))
            c (cond (or (= i 0.0)(= i 0.5)(= i 1.0)) (lnf/major-tick-color)
                    :default (lnf/minor-tick-color))]
        (line/line (.root drw)[xa y][xb y]
                   :style :dotted
                   :color c)
        (label drw [(- xa 34)(+ y 6)](format "%4.2f" i)))))
    
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
      (.set-param! ied :reverb-size 0.7)
      (.set-param! ied :reverb-damp 0.5)
      (.set-param! ied :reverb-mix 0.0))
    
    (sync-ui! [this]
      (let [size (data :reverb-size)
            damp (data :reverb-damp)
            mix (data :reverb-mix)]
        (slider/set-slider-value! s-size size false)
        (slider/set-slider-value! s-damp damp false)
        (slider/set-slider-value! s-mix mix false))) )))
