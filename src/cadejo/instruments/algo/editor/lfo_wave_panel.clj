;; parameters
;; :lfo1-skew              :lfo2-skew                                           
;; :env1->lfo1-skew        :lfo1->lfo2-skew

(ns cadejo.instruments.algo.editor.lfo-wave-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory])  
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn wave-panel [n drw p0 ied]
  (let [param-skew (keyword (format "lfo%d-skew" n))
        param-mod (if (= n 1) :env1->lfo1-skew :lfo1->lfo2-skew)
        id (keyword (format "lfo%d" n))
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-skew (+ x0 50)
        x-mod (+ x-skew (* 1.5 slider-spacing))
        x-skew-label (+ x-skew 0)
        x-mod-label (+ x-mod 0)
        y-slider (- y0 32)
        y-labels (- y0 30)
        width 160
        height 230
        action (fn [s _]
                 (let [p (.get-property s :id)
                       v (slider/get-slider-value s)]
                   (.set-param! ied p v)))
        s-skew (factory/slider tools [x-skew y-slider] 
                               param-skew
                               0.0 1.0 action false)
        s-mod (factory/slider tools [x-mod y-slider]
                              param-mod
                              -1.0 1.0 action :signed)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        skew (float (param-skew dmap))
                        mod (float (param-mod dmap))]
                    (slider/set-slider-value! s-skew skew false)
                    (slider/set-slider-value! s-mod mod false)))]
    (factory/slider-label root [x-skew-label y-labels] id "Skew")
    (factory/slider-label root [x-mod-label y-labels] id (if (= n 1) "ENV1" "LFO1"))
    ;; rules
    (let [vn1 (- y-slider 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [x-slider y]
                  (let [x1 (- x-slider 10)
                        x2 (+ x-slider 10)]
                    (line/line root [x1 y][x2 y] :id id
                               :color (lnf/major-tick-color)
                               :style :solid)))
          major (fn [x-slider y n]
                  (vline x-slider y)
                  (text/text root [(- x-slider 45)(+ y 5)](format "%+4.1f" n)
                             :id id 
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))]
      (major x-mod vn1 -1.0)
      (major x-mod vp1 1.0)
      (major x-mod v0 0.0)
      (major x-mod (math/mean v0 vp1) 0.5)
      (major x-mod (math/mean v0 vn1) -0.5)
      (vline x-skew vn1)
      (vline x-skew v0)
      (vline x-skew vp1))
    ;; draw wave shapes
    (let [w-len 15
          w-height 8      ;; half height
          wx4 (- x-skew 15)
          wx1 (- wx4 w-len)
          wxc (math/mean wx1 wx4)
          wx2 (math/mean wxc wx1)
          wx3 (math/mean wxc wx4)

          w2yc (- y-slider 0)
          w2y1 (+ w2yc w-height)
          w2y2 (- w2yc w-height)

          w1yc (- (- y-slider slider-length) 0)
          w1y1 (+ w1yc w-height)
          w1y2 (- w1yc w-height)



          w3yc (math/mean w1yc w2yc)
          w3y1 (- w3yc w-height)
          w3y2 (+ w3yc w-height)
          c (lnf/major-tick-color)]
      ;; saw (skew = 0)
      (line/line root [wx1 w1y1][wx1 w1y2] :color c)
      (line/line root [wx1 w1y2][wx4 w1y1] :color c)
      ;; saw (skew = 1)
      (line/line root [wx1 w2y1][wx4 w2y2] :color c)
      (line/line root [wx4 w2y2][wx4 w2y1] :color c)
      ;; triangle (skew = 0.5)
      (line/line root [wx1 w3yc][wx2 w3y1] :color c)
      (line/line root [wx2 w3y1][wx3 w3y2] :color c)
      (line/line root [wx3 w3y2][wx4 w3yc] :color c))
    (factory/inner-border root [x0 y0][(+ x0 width)(- y0 height)])
    (factory/section-label root [(+ x0 60)(- y0 200)] :lfo "Wave")
    {:sync-fn sync-fn}))

