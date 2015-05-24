(ns cadejo.instruments.cobalt.editor.noise-freq-panel
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.indicators.displaybar :as dbar]))

(def ^:private left-margin 30)

(defn- freq-pos [item p0]
  (let [pan-width 410
        pan-height 120
        x-offset left-margin
        y-offset 430 
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        x-label (+ x0 10)
        x-freq (+ x0 60)
        x-freq-edit (+ x-freq 210)
        x-freq2 x-freq
        x-freq2-edit x-freq-edit
        x-penv x-freq
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-freq1 (+ y-border 20)
        y-freq2 (+ y-freq1 50)
        y-penv (+ y-freq2 50)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :freq1 [x-freq y-freq1]
          :freq1-label [x-label (+ y-freq1 20)]
          :freq1-edit [x-freq-edit y-freq1]
          :freq2 [x-freq y-freq2]
          :freq2-edit [x-freq-edit y-freq2]
          :freq2-label [x-label (+ y-freq2 20)]
          :penv [x-penv y-penv]
          :penv-edit [x-freq-edit (+ y-penv 0)]
          :penv-label [x-label (+ y-penv 20)]}
         item)))

(defn draw-freq-panel [bg p0]
  (sf/label bg (freq-pos :freq1-label p0) "Freq 1")
  (sf/label bg (freq-pos :freq2-label p0) "Freq 2")
  (sf/label bg (freq-pos :penv-label p0) "Penv"))

(defn freq-panel [drw ied p0]
  (let [param-detune1 :nse-detune
        param-detune2 :nse2-detune
        param-penv :nse<-penv
        dbar-freq1 (sf/displaybar drw (freq-pos :freq1 p0) 7)
        dbar-freq2 (sf/displaybar drw (freq-pos :freq2 p0) 7)
        dbar-penv (sf/displaybar drw (freq-pos :penv p0) 7)
        freq-edit-action (fn [b _] 
                           (let [id (.get-property b :id)
                                 [dbar param](id {:noise-freq1-edit [dbar-freq1 param-detune1]
                                                  :noise-freq2-edit [dbar-freq2 param-detune2]})
                                 prompt (format "Noise %d Frequency (0 < f < 32)"
                                                (if (= id :noise-freq1-edit) 1 2))]
                             (dbar/displaybar-dialog dbar prompt
                                                     :validator (fn [q](let [f (math/str->float q)]
                                                                         (and f (pos? f)(< f 32))))
                                                     :callback (fn [_](let [f (math/str->float (.current-display dbar))]
                                                                        (.set-param! ied param f))))))
        penv-edit-action (fn [& _]
                           (dbar/displaybar-dialog dbar-penv 
                                                   "Noise Pitch Env Depth (-1.0 <= n <= +1.0)"
                                                   :validator (fn [q](let [f (math/str->float q)]
                                                                       (and f (<= -1 f)(<= f 1))))
                                                   :callback (fn [_](let [f (math/str->float (.current-display dbar-penv))]
                                                                      (.set-param! ied param-penv f)))))
        b-freq1 (sf/mini-edit-button drw (freq-pos :freq1-edit p0) :noise-freq1-edit freq-edit-action)
        b-freq2 (sf/mini-edit-button drw (freq-pos :freq2-edit p0) :noise-freq2-edit freq-edit-action)
        b-penv (sf/mini-edit-button drw (freq-pos :penv-edit p0) :penv-freq2-edit penv-edit-action)]
    (fn [dmap]
      (let [f1 (param-detune1 dmap)
            f2 (param-detune2 dmap)
            pe (param-penv dmap)]
        (.display! dbar-freq1 (format "%6.4f" (float f1) false))
        (.display! dbar-freq2 (format "%6.4f" (float f2) false))
        (.display! dbar-penv (format "%6.3f" (float pe)) false)))))
