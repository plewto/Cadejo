(ns cadejo.instruments.algo.editor.reverb-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing :as drw])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn reverb-panel [drw p0 ied]
  (let [param-size :room-size
        param-mix :reverb-mix
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-size (+ x0 100) ; 75
        x-mix (+ x-size slider-spacing)
        y-sliders (- y0 30)
        y-labels (- y0 30)
        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        s-size (factory/slider tools [x-size y-sliders] param-size 0.0 1.0 slider-action false)
        s-mix (factory/slider tools [x-mix y-sliders] param-mix 0.0 1.0 slider-action false)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        size (float (param-size dmap))
                        mix (float (param-mix dmap))]
                    (slider/set-slider-value! s-size size false)
                    (slider/set-slider-value! s-mix mix false)))]
    (factory/slider-label root [(+ x-size 0) y-labels] :reverb "Size")
    (factory/slider-label root [(+ x-mix 2) y-labels] :reverb "Mix")
    (let [x1 (- x-size 10)
          x2 (+ x-mix 10)
          xtx (- x1 35)
          vn1 (- y-sliders 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [y c] 
                  (line/line root [x1 y][x2 y] :id :delay
                             :style :dotted
                             :color c))
          minor (fn [y n]
                  (vline y (lnf/minor-tick-color))
                  (text/text root [xtx (+ y 5)] (format "%4.2f" n)
                             :style :mono
                             :size 6
                             :color (lnf/minor-tick-color)))]
      (minor vn1 1.0)
      (minor vp1 1.0)
      (minor v0 0.50)
      (minor (math/mean v0 vp1) 0.75)
      (minor (math/mean v0 vn1) 0.25))
    (factory/major-label root [(+ x0 40)(- y0 210)] "Reverb")
    (factory/inner-border root [x0 y0][(+ x0 220)(- y0 250)])
    {:sync-fn sync-fn}))
