(ns cadejo.instruments.cobalt.editor.op-freq-panel
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.indicators.displaybar :as dbar]))

(def ^:private left-margin 30)

(defn- freq-pos [item p0]
  (let [pan-width 410; 350
        pan-height 120
        x-offset left-margin
        y-offset 430 
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        x-label (+ x0 20)
        x-freq (+ x0 60)
        x-freq-edit (+ x-freq 210)
        x-penv x-freq
        x-penv-edit x-freq-edit
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-freq (+ y-border 20)
        y-penv (+ y-freq 50)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :freq [x-freq y-freq]
          :freq-label [x-label (+ y-freq 20)]
          :freq-edit [x-freq-edit y-freq]
          :penv [x-penv y-penv]
          :penv-edit [x-penv-edit y-penv]
          :penv-label [x-label (+ y-penv 20)]}
         item)))

(defn- draw-freq-panel [op bg p0]
  (sf/label bg (freq-pos :freq-label p0) "Freq")
  (sf/label bg (freq-pos :penv-label p0) "P.Env")
  (sf/minor-border bg (freq-pos :p0 p0)(freq-pos :border p0)))

(defn- param [op suffix]
  (let [prefix (cond (= op :nse) "nse"
                     (= op :bzz) "bzz"
                     :default (format "op%d" op))]
    (keyword (format "%s%s" prefix suffix))))


(defn freq-panel [op drw ied p0]
  (let [param-freq (param op "-detune")
        param-penv (param op "<-penv")
        dbar-freq (sf/displaybar drw (freq-pos :freq p0) 7)
        dbar-penv (sf/displaybar drw (freq-pos :penv p0) 7)
        freq-edit-action (fn [& _] 
                           (let [prompt (if (= op :bzz)
                                          "Buzz Frequency (0 < f < 32)"
                                          (format "OP%d Frequency (0 < f < 32)" op))
                                 validator (fn [q]
                                             (let [f (math/str->float q)]
                                               (and f (pos? f)(< f 32))))
                                 callback (fn [_]
                                            (let [f (math/str->float (.current-display dbar-freq))]
                                              (.set-param! ied param-freq f)))]
                             (dbar/displaybar-dialog dbar-freq prompt 
                                                     :validator validator
                                                     :callback callback)))
        penv-edit-action (fn [& _] 
                           (let [prompt (if (= op :bzz)
                                          "Buzz Pitch Env Depth (-1 <= n <= +1)"
                                          (format "OP%d Pitch Env Depth (-1 <= n <= +1)" op))
                                 validator (fn [q]
                                             (let [f (math/str->float q)]
                                               (and f (<= -1 f)(<= f 1))))
                                 callback (fn [_]
                                            (let [f (math/str->float (.current-display dbar-penv))]
                                              (.set-param! ied param-penv f)))]
                             (dbar/displaybar-dialog dbar-penv prompt 
                                                     :validator validator
                                                     :callback callback)))
        b-freq (sf/mini-edit-button drw (freq-pos :freq-edit p0) :op-freq-edit freq-edit-action)
        b-penv (sf/mini-edit-button drw (freq-pos :penv-edit p0) :op-penv-edit penv-edit-action)]
  (fn [dmap]
    (let [freq (param-freq dmap)
          penv (param-penv dmap)]
      (.display! dbar-freq (format "%6.4f" (float freq)) false)
      (.display! dbar-penv (format "%6.3f" (float penv)) false))))) 
