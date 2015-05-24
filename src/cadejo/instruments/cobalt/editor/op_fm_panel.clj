(ns cadejo.instruments.cobalt.editor.op-fm-panel
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider]))


(def ^:private left-margin 30)
(def ^:private keyscale-states [[:n12 "-12" :red]
                                [:n9  "-9"  :red]
                                [:n6  "-6"  :red]
                                [:n3  "-3"  :red]
                                [:0   " 0"  :green]
                                [:3   "+3"  :green]
                                [:6   "+6"  :green]
                                [:9   "+9"  :green]
                                [:12  "+12" :green]])

(defn- fm-pos [item p0]
  (let [pan-width 350
        pan-height 360
        x-offset (+ left-margin 410)
        y-offset 190
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        x-label (+ x0 20)
        x-freq (+ x0 60)
        x-freq-edit (+ x-freq 210)
        x-depth (+ x0 110)
        x-depth-edit x-freq-edit
        slider-space 50
        x-env (+ x0 75)
        x-lag (+ x-env slider-space)
        x-left (+ x0 260)
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-freq (+ y-border 20)
        y-bias (+ y-freq 50)
        y-depth (+ y-bias 70)
        y-slider1 (+ y-depth 45)
        y-slider2 (+ y-slider1 sf/slider-length)
        y-left (+ y-border 240)
        y-right (+ y-left 40)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :freq [x-freq y-freq]
          :freq-label [x-label (+ y-freq 20)]
          :freq-edit [x-freq-edit y-freq]
          :bias [x-freq y-bias]
          :bias-label [x-label (+ y-bias 20)]
          :bias-edit [x-freq-edit y-bias]
          :depth [x-depth y-depth]
          :depth-edit [x-depth-edit y-depth]
          :depth-label [x-label (+ y-depth 20)]
          :env [x-env y-slider2]
          :lag [x-lag y-slider2]
          :left [x-left y-left]
          :right [x-left y-right]}
         item)))


(defn- draw-fm-panel [op bg p0]
  (sf/label bg (fm-pos :freq-label p0) "FM" :offset [0 -15] :size 8)
  (sf/label bg (fm-pos :freq-label p0) "Freq")
  (sf/label bg (fm-pos :bias-label p0) "Bias")
  (sf/label bg (fm-pos :depth-label p0) "Depth")
  (sf/label bg (fm-pos :env p0) "Env" :offset [-10 20])
  (sf/label bg (fm-pos :lag p0) "Lag" :offset [-10 20])
  (sf/label bg (fm-pos :left p0) "Left" :offset [-45 15])
  (sf/label bg (fm-pos :right p0) "Right" :offset [-45 15])
  (sf/label bg (fm-pos :left p0) "Keyscale" :offset [-15 -15])
  (let [positions [(fm-pos :env p0)(fm-pos :lag p0)]
        y1 (second (first positions))
        y2 (- y1 sf/slider-length)]
    (doseq [x (map first positions)]
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 
                           :frmt (if (= x (first (first positions))) "%4.2f" "")
                           :x-offset -42
                           :step 0.25)
      (sf/minor-ticks bg x y1 y2 20)))
  (sf/minor-border bg (fm-pos :p0 p0)(fm-pos :border p0)))

(defn fm-panel [op drw ied p0]
  (let [param-freq (keyword (format "fm%d-detune" op))
        param-bias (keyword (format "fm%d-bias" op))
        param-amp (keyword (format "fm%d-amp" op))
        param-env (keyword (format "fm%d<-env" op))
        param-lag (keyword (format "fm%d-lag" op))
        param-left (keyword (format "fm%d-keyscale-left" op))
        param-right (keyword (format "fm%d-keyscale-right" op))
        dbar-freq (sf/displaybar drw (fm-pos :freq p0) 7)
        freq-edit-action (fn [& _]
                           (let [prompt (format "FM%d Frequency (0 < f < 32)" op)]
                             (dbar/displaybar-dialog
                              dbar-freq prompt
                              :validator (fn [q](let [f (math/str->float q)]
                                                  (and f (< 0 f)(< f 32))))
                              :callback (fn [_](let [s (.current-display dbar-freq)
                                                     f (math/str->float s)]
                                                 (.set-param! ied param-freq f))))))
        b-freq (sf/mini-edit-button drw (fm-pos :freq-edit p0) :op-fm-edit freq-edit-action)

        dbar-bias (sf/displaybar drw (fm-pos :bias p0) 7)
        bias-edit-action (fn [& _]
                           (let [prompt (format "FM%d Bias (-9999 <= b <= 9999)" op)]
                             (dbar/displaybar-dialog
                              dbar-bias prompt
                              :validator (fn [q](let [f (math/str->float q)]
                                                  (and f (< -9999 f)(< f 9999))))
                              :callback (fn [_](let [s (.current-display dbar-bias)
                                                     f (math/str->float s)]
                                                 (.set-param! ied param-bias f))))))
                                                
        b-bias (sf/mini-edit-button drw (fm-pos :bias-edit p0) :op-bias-edit bias-edit-action)
        
        dbar-depth (sf/displaybar drw (fm-pos :depth p0) 5)


        depth-edit-action (fn [& _]
                             (let [prompt (format "FM%d Depth (0 <= d)" op)]
                               (dbar/displaybar-dialog
                                dbar-depth prompt
                                :validator (fn [q](let [f (math/str->float q)]
                                                    (and f (<= 0 f))))
                                :callback (fn [_](let [s (.current-display dbar-depth)
                                                       f (math/str->float s)]
                                                   (.set-param! ied param-amp f))))))


        b-depth (sf/mini-edit-button drw (fm-pos :depth-edit p0) :op-fm-depth-edit depth-edit-action)

        slider-action (fn [s _]
                        (let [p (.get-property s :id)
                              v (slider/get-slider-value s)]
                          (.set-param! ied p v)))
        s-env (sf/vslider drw ied param-env (fm-pos :env p0) 0.0 1.0 slider-action)
        s-lag (sf/vslider drw ied param-lag (fm-pos :lag p0) 0.0 con/max-fm-lag slider-action)
        
        keyscale-action (fn [b _] 
                          (let [index (first (msb/current-multistate-button-state b))
                                db (- (* 3 index) 12)
                                param (.get-property b :id)]
                            (.set-param! ied param db)))
        msb-left (msb/text-multistate-button (.tool-root drw) 
                                             (fm-pos :left p0) 
                                             keyscale-states
                                             :id param-left
                                             :click-action keyscale-action
                                             :rim-color (lnf/button-border)
                                             :gap 6)

        msb-right (msb/text-multistate-button (.tool-root drw) 
                                             (fm-pos :right p0) 
                                             keyscale-states
                                             :id param-right
                                             :click-action keyscale-action
                                             :rim-color (lnf/button-border)
                                             :gap 6)]
    (fn [dmap]
      (let [freq (param-freq dmap)
            bias (param-bias dmap)
            amp (param-amp dmap)
            env (param-env dmap)
            lag (param-lag dmap)
            left (let [v (param-left dmap)]
                   (int (math/clamp (+ (* 1/3 v) 4) 0 8)))
            right (let [v (param-right dmap)]
                    (int (math/clamp (+ (* 1/3 v) 4) 0 8)))]
        (.display! dbar-freq (format "%7.4f" (float freq)) false)
        (.display! dbar-bias (format "%+7.3f" (float bias)) false)
        (.display! dbar-depth (format "%4.3f" (float amp)) false)
        (slider/set-slider-value! s-env (float env) false)
        (slider/set-slider-value! s-lag (float lag) false)
        (msb/set-multistate-button-state! msb-left left false)
        (msb/set-multistate-button-state! msb-right right false))) ))
