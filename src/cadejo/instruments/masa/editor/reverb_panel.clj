(ns cadejo.instruments.masa.editor.reverb-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.util.lnf :as lnf ])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.instruments.subedit])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.tools.slider :as slider]))

(defn reverb-panel [drw ied p0]
  (let [data (fn [param] ;; Returns current program value of parameter
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        w 200
        h 290
        [x0 y0] p0
        x1 (+ x0 w)
        x-size (+ x0 slider-spacing 10)
        x-damp (+ x-size slider-spacing)
        x-mix (+ x-damp slider-spacing)
        y1 (- y0 h)
        y2 (- y0 70)
        y3 (+ y2 24)
        p1 [x1 y1]
        size-action (fn [s _](.set-param! ied :reverb-size (slider/get-slider-value s)))
        damp-action (fn [s _](.set-param! ied :reverb-damp (slider/get-slider-value s)))
        mix-action (fn [s _](.set-param! ied :reverb-mix (slider/get-slider-value s)))
        s-size (sfactory/vslider drw ied :room-size [x-size y2] 0.0 1.0 size-action)
        s-damp (sfactory/vslider drw ied :reverb-damp [x-damp y2] 0.0 1.0 damp-action)
        s-mix (sfactory/vslider drw ied :reverb-mix [x-mix y2] 0.0 1.0 mix-action)
        widget-map {}]
  (rect/rectangle (.root drw) p0 p1 :color (lnf/minor-border) :radius 12)
  (sfactory/text drw [(+ x0 70)(- y0 h -20)] "Reverb")
  (sfactory/label drw [(+ x-size -12) y3] "Room")
  (sfactory/label drw [(+ x-size -12) (+ y3 12)] "Size")
  (sfactory/label drw [(+ x-damp -12) y3] "Damp")
  (sfactory/label drw [(+ x-mix -10) y3] "Mix")
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
            c (cond (or (= i 0.0)(= i 0.5)(= i 1.0)) (lnf/major-tick)
                    :default (lnf/minor-tick))]
        (line/line (.root drw)[xa y][xb y]
                   :style :dotted
                   :color c)
        (sfactory/label drw [(- xa 34)(+ y 6)](format "%4.2f" i)))))
    
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
