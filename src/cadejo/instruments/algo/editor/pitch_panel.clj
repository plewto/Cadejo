(ns cadejo.instruments.algo.editor.pitch-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))


(defn pitch-panel [drw p0 ied]
  (let [param-port :port-time
        param-env :env1->pitch
        param-lfo1 :lfo1->pitch
        param-lfo2 :lfo2->pitch
        id :pitch
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-port (+ x0 70)
        x-env (+ x-port slider-spacing 20)
        x-lfo1 (+ x-env slider-spacing)
        x-lfo2 (+ x-lfo1 slider-spacing)
        y-sliders (- y0 32)
        y-labels (- y0 30)
        action (fn [s _]
                 (let [p (.get-property s :id)
                       v (slider/get-slider-value s)]
                   (.set-param! ied p v)))
        s-port (factory/slider tools [x-port y-sliders] param-port 0.0 max-port-time action false)
        s-env (factory/slider tools [x-env y-sliders] param-env min-pitch-modulation max-pitch-modulation action :signed)
        s-lfo1 (factory/slider tools [x-lfo1 y-sliders] param-lfo1 min-pitch-modulation max-pitch-modulation action :signed)
        s-lfo2 (factory/slider tools [x-lfo2 y-sliders] param-lfo2 min-pitch-modulation max-pitch-modulation action :signed)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        ptime (float (param-port dmap))
                        env (float (param-env dmap))
                        lfo1 (float (param-lfo1 dmap))
                        lfo2 (float (param-lfo2 dmap))]
                    (slider/set-slider-value! s-port ptime false)
                    (slider/set-slider-value! s-env env false)
                    (slider/set-slider-value! s-lfo1 lfo1 false)
                    (slider/set-slider-value! s-lfo2 lfo2 false)))]
    ;; rules signed
    (let [x1 (- x-env 10)
          x2 (+ x-lfo2 10)
          xtx (- x1 35)
          vn1 (- y-sliders 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [y c] 
                  (line/line root [x1 y][x2 y] :id id
                             :style :solid
                             :color c))
          minor (fn [y n]
                  (vline y (lnf/minor-tick-color))
                  (text/text root [xtx (+ y 5)] (format "%+4.1f" n)
                             :style :mono
                             :size 6
                             :color (lnf/minor-tick-color)))]
      (minor vn1 -1.0)
      (minor vp1 1.0)
      (minor v0 0.0)
      (minor (math/mean v0 vp1) 0.5)
      (minor (math/mean v0 vn1) -0.5))

    ;; rules port
    (let [x1 (- x-port 10)
          x2 (+ x-port 10)
          xtx (- x1 35)
          vn1 (- y-sliders 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [y c] 
                  (line/line root [x1 y][x2 y] :id id
                             :style :solid
                             :color c))
          minor (fn [y n]
                  (vline y (lnf/minor-tick-color))
                  (text/text root [xtx (+ y 5)] (format "%4.2f" n)
                             :style :mono
                             :size 6
                             :color (lnf/minor-tick-color)))]
      (minor vn1 0.00)
      (minor vp1 1.00)
      (minor v0 0.50)
      (minor (math/mean v0 vp1) 0.75)
      (minor (math/mean v0 vn1) 0.25))
    (factory/slider-label root [(- x-port 0) y-labels] id "Port")
    (factory/slider-label root [(- x-env 0) y-labels] id "Env")
    (factory/slider-label root [(- x-lfo1 0) y-labels] id "LFO1")
    (factory/slider-label root [(- x-lfo2 0) y-labels] id "LFO2")
    (factory/major-label root [(+ x0 90)(- y0 220)] "Pitch")
    (factory/inner-border root [x0 y0][(+ x0 280)(- y0 260)])
    {:sync-fn sync-fn}))
        
        
