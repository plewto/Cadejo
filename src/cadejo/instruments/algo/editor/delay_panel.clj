(ns cadejo.instruments.algo.editor.delay-panel
  (:use [cadejo.instruments.algo.algo-constants])
  ;(:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing :as drw])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.slider :as slider]))

(defn delay-panel [drw p0 ied]
  (let [param-delay-1 :echo-delay-1
        param-delay-2 :echo-delay-2
        param-damp :echo-hf-damp
        param-fb :echo-fb
        param-mix :echo-mix
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-delay-1 (+ x0 20)
        x-delay-2 x-delay-1
        x-edit-1 (+ x-delay-1 150)
        x-edit-2 x-edit-1
        x-fb (+ x-edit-1 90)
        x-damp (+ x-fb (* 1.5 slider-spacing))
        x-mix (+ x-damp slider-spacing)
        y-delay-1 (- y0 158)
        y-delay-2 (+ y-delay-1 75)
        y-edit-1 (+ y-delay-1 2)
        y-edit-2 (+ y-delay-2 2)
        y-sliders (- y0 30)
        y-labels (- y0 10)
        dbar-1 (sfactory/displaybar drw [x-delay-1 y-delay-1] 5)
        dbar-2 (sfactory/displaybar drw [x-delay-2 y-delay-2] 5)
        edit-1-action (fn [b _]
                        (dbar/displaybar-dialog dbar-1
                                                "Delay 1 Time"
                                                :validator (fn [q]
                                                             (let [b (math/str->float q)]
                                                               (and b (pos? b)(<= b max-echo-delay))))
                                                :callback (fn [_]
                                                            (let [s (.current-display dbar-1)
                                                                  dly (math/str->float s)]
                                                              (.set-param! ied param-delay-1 dly)))))
        edit-2-action (fn [b _]
                        (dbar/displaybar-dialog dbar-2
                                                "Delay 2 Time"
                                                :validator (fn [q]
                                                             (let [b (math/str->float q)]
                                                               (and b (pos? b)(<= b max-echo-delay))))
                                                :callback (fn [_]
                                                            (let [s (.current-display dbar-2)
                                                                  dly (math/str->float s)]
                                                              (.set-param! ied param-delay-2 dly)))))
        edit-1 (sfactory/mini-edit-button drw [x-edit-1 y-edit-1] 1 edit-1-action)
        edit-2 (sfactory/mini-edit-button drw [x-edit-2 y-edit-2] 2 edit-2-action)
        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        ;; s-feedback (factory/slider tools [x-fb y-sliders] param-fb
        ;;                            min-echo-fb max-echo-fb slider-action :signed)
        ;; s-damp (factory/slider tools [x-damp y-sliders] param-damp
        ;;                        0.0 1.0 slider-action false)
        ;; s-mix (factory/slider tools [x-mix y-sliders] param-mix
        ;;                       0.0 1.0 slider-action false)

        s-feedback (sfactory/vslider drw ied param-fb [x-fb y-sliders] 
                                   min-echo-fb max-echo-fb slider-action)
        s-damp (sfactory/vslider drw ied param-damp [x-damp y-sliders] 
                               0.0 1.0 slider-action)
        s-mix (sfactory/vslider drw ied param-mix [x-mix y-sliders] 
                              0.0 1.0 slider-action)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        dly1 (float (param-delay-1 dmap))
                        dly2 (float (param-delay-2 dmap))
                        fb (float (param-fb dmap))
                        damp (float (param-damp dmap))
                        mix (float (param-mix dmap))]
                    (.display! dbar-1 (format "%5.3f" dly1) false)
                    (.display! dbar-2 (format "%5.3f" dly2) false)
                    (slider/set-slider-value! s-feedback fb false)
                    (slider/set-slider-value! s-damp damp false)
                    (slider/set-slider-value! s-mix mix false)))]
    (sfactory/label drw [(+ x-delay-1 10)(+ y-delay-1 50)]  "Delay 1")
    (sfactory/label drw [(+ x-delay-2 10)(+ y-delay-2 50)]  "Delay 2")
    (sfactory/label drw [(- x-fb 28) y-labels]  "Feedback")
    (sfactory/label drw [(- x-damp 14) y-labels]  "Damp")
    (sfactory/label drw [(- x-mix 14) y-labels]  "Mix")
    ;; rules
    (let [x1 (- x-fb 10)
          x2 (+ x-fb 10)
          xtx (- x1 35)
          vn1 (- y-sliders 0)
          vp1 (- vn1 slider-length)
          v0 (math/mean vn1 vp1)
          vline (fn [y c] 
                  (line/line root [x1 y][x2 y] :id :delay
                             :style :solid
                             :color c))
          minor (fn [y n]
                  (vline y (lnf/minor-tick))
                  (text/text root [xtx (+ y 5)] (format "%+4.1f" n)
                             :style :mono
                             :size 6
                             :color (lnf/minor-tick)))]
      (minor vn1 -1.0)
      (minor vp1 1.0)
      (minor v0 0.0)
      (minor (math/mean v0 vp1) 0.5)
      (minor (math/mean v0 vn1) -0.5))
    (let [x1 (- x-damp 10)
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
                  (vline y (lnf/minor-tick))
                  (text/text root [xtx (+ y 5)] (format "%4.2f" n)
                             :style :mono
                             :size 6
                             :color (lnf/minor-tick)))]
      (minor vn1 1.0)
      (minor vp1 1.0)
      (minor v0 0.50)
      (minor (math/mean v0 vp1) 0.75)
      (minor (math/mean v0 vn1) 0.25))
    (sfactory/minor-border drw p0 [(+ x-mix 30)(- y0 250)])
    (sfactory/title drw [(+ x0 17)(- y0 222)] "Delay")
    {:sync-fn sync-fn}))
