(ns cadejo.instruments.algo.editor.lfo-amp-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory])  
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn amp-panel [n drw p0 ied]
  (let [param-1 (if (= n 1) :env1->lfo1-amp :lfo1->lfo2-amp)
        param-pressure (keyword (format "pressure->lfo%d-amp" n))
        param-cca (keyword (format "cca->lfo%d-amp" n))
        param-ccb (keyword (format "ccb->lfo%d-amp" n))
        id (keyword (format "lfo%d" n))
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-env (+ x0 50)
        x-prss (+ x-env slider-spacing)
        x-cca (+ x-prss slider-spacing)
        x-ccb (+ x-cca slider-spacing)
        x-label-offset 0
        x-amp-label (+ x0 75)
        y-sliders (- y0 32)
        y-labels (- y0 30)
        y-amp-label (- y0 200)
        width 230
        height 230
        action (fn [s _]
                 (let [p (.get-property s :id)
                       v (slider/get-slider-value s)]
                   (.set-param! ied p v)))
        s-mod (factory/slider tools [x-env y-sliders]
                              (if (= 1 n) :env1->lfo1-amp :lfo1->lfo2-amp)
                              -1.0 1.0 action :signed)
        s-prss (factory/slider tools [x-prss y-sliders]
                               (keyword (format "pressure->lfo%d-amp" n))
                               -1.0 1.0 action :signed)
        s-cca (factory/slider tools [x-cca y-sliders]
                               (keyword (format "cca->lfo%d-amp" n))
                               -1.0 1.0 action :signed)
        s-ccb (factory/slider tools [x-ccb y-sliders]
                               (keyword (format "ccb->lfo%d-amp" n))
                               -1.0 1.0 action :signed)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        mod-1 (float (param-1 dmap))
                        pressure (float (param-pressure dmap))
                        cca (float (param-pressure dmap))
                        ccb (float (param-pressure dmap))]
                    (slider/set-slider-value! s-mod mod-1 false)
                    (slider/set-slider-value! s-prss pressure false)
                    (slider/set-slider-value! s-cca cca false)
                    (slider/set-slider-value! s-ccb ccb false)))]
    (factory/slider-label root [(+ x-env x-label-offset) y-labels] id (if (= n 1) "ENV1" "LFO1"))
    (factory/slider-label root [(+ x-prss x-label-offset) y-labels] id "Prss")
    (factory/slider-label root [(+ x-cca x-label-offset) y-labels] id "CCA")
    (factory/slider-label root [(+ x-ccb x-label-offset) y-labels] id "CCB")
    (factory/section-label root [x-amp-label y-amp-label] id "Amplitude")
    (factory/inner-border root [x0 y0][(+ x0 width)(- y0 height)])
    ;; rules
    (let [x1 (- x-env 10)
          x2 (+ x-ccb 10)
          xtx (- x1 35)
          vn1 (- y-sliders 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [y c] 
                  (line/line root [x1 y][x2 y] :id id
                             :style :solid
                             :color c))
          major (fn [y n]
                  (vline y (lnf/major-tick-color))
                  (text/text root [xtx (+ y 5)] (format "%+4.1f" n)
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))]
      (major vn1 -1.0)
      (major vp1 1.0)
      (major v0 0.0)
      (major (math/mean v0 vp1) 0.5)
      (major (math/mean v0 vn1) -0.5))
    {:sync-fn sync-fn}))
