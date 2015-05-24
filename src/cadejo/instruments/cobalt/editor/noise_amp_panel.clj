(ns cadejo.instruments.cobalt.editor.noise-amp-panel
  (:require [cadejo.instruments.cobalt.constants :as con :reload true])
  (:require [cadejo.instruments.cobalt.editor.op-amp-panel :as oap])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider]))

(def ^:private left-margin oap/left-margin)
(def ^:private keyscale-states oap/keyscale-states)
(def ^:private keyscale-key-states oap/keyscale-key-states)
(def ^:private -amp-pos oap/amp-pos)

(defn- amp-pos [item p0]
  (let [pan-width 760
        pan-height 360
        slider-space 50
        x-offset left-margin
        y-offset 190
        y-slider2 (second (-amp-pos :lfo p0))
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        x-amp (+ x0 485)
        x-bw1 (+ x0 515)
        x-bw2 (+ x-bw1 slider-space)
        x-lag (+ x-bw2 slider-space)
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-amp1 (+ y-border 20)
        y-amp2 (+ y-amp1 50)]
    (or (get {:p0 [x0 y0]
              :border [x-border y-border]
              :amp1 [x-amp y-amp1]
              :amp1-edit [(+ x-amp 150) y-amp1]
              :amp1-label [(- x-amp 50)(+ y-amp1 20)]
              :amp2 [x-amp y-amp2]
              :amp2-edit [(+ x-amp 150) y-amp2]
              :amp2-label [(- x-amp 50)(+ y-amp2 20)]
              :bw1 [x-bw1 y-slider2]
              :bw2 [x-bw2 y-slider2]
              :lag [x-lag y-slider2]
              } item)
        (-amp-pos item p0))))
        

(defn draw-amp-panel [bg p0]
  (let [positions [(amp-pos :lfo p0)(amp-pos :vel p0)(amp-pos :prss p0)
                   (amp-pos :cca p0)(amp-pos :lag p0)]
        y1 (second (first positions))
        y2 (- y1 sf/slider-length)]
    (sf/minor-border bg (amp-pos :p0 p0)(amp-pos :border p0))
    (sf/label bg (amp-pos :amp1-label p0) "Amp 1")
    (sf/label bg (amp-pos :amp2-label p0) "Amp 2")
    (sf/label bg (amp-pos :lfo p0) "LFO1" :offset [-10 20])
    (sf/label bg (amp-pos :vel p0) "Vel" :offset [-10 20])
    (sf/label bg (amp-pos :prss p0) "Prss" :offset [-10 20])
    (sf/label bg (amp-pos :cca p0) "CCA" :offset [-10 20])
    (sf/label bg (amp-pos :bw1 p0) "BW 1" :offset [-10 20])
    (sf/label bg (amp-pos :bw2 p0) "BW 2" :offset [-10 20])
    (sf/label bg (amp-pos :lag p0) "Lag 2" :offset [-10 20])
    (sf/label bg (amp-pos :key p0) "Keyscale" :offset [-15 -15])
    (sf/label bg (amp-pos :key p0) "Key" :offset [-45 15])
    (sf/label bg (amp-pos :left p0) "Left" :offset [-45 15])
    (sf/label bg (amp-pos :right p0) "Right" :offset [-45 15])
    (doseq [x (map first positions)]
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 
                           :frmt (if (= x (first (first positions))) "%4.2f" "")
                           :x-offset -42
                           :step 0.25)
      (sf/minor-ticks bg x y1 y2 20))
    (doseq [x (map first [(amp-pos :bw1 p0)(amp-pos :bw2 p0)])]
      (sf/major-tick-marks bg x y1 y2 :v0 con/min-noise-filter-bw :v1 con/max-noise-filter-bw
                           :frmt (if (= x (first (amp-pos :bw1 p0))) "%3d" "")
                           :x-offset -42
                           :step 10))))

(defn amp-panel [drw ied p0]
  (let [param-amp1 :nse-amp
        param-amp2 :nse2-amp
        param-lfo :nse-amp<-lfo1
        param-vel :nse-amp<-velocity
        param-prss :nse-amp<-pressure
        param-cca :nse-amp<-cca
        param-bw1 :nse-bw
        param-bw2 :nse2-bw
        param-lag :nse2-lag
        param-key :nse-keyscale-key
        param-left :nse-keyscale-left
        param-right :nse-keyscale-right
        dbar-amp1 (sf/displaybar drw (amp-pos :amp1 p0) 5)
        dbar-amp2 (sf/displaybar drw (amp-pos :amp2 p0) 5)
        amp-edit-action (fn [b _] 
                          (let [param (.get-property b :id)
                                dbar (param {param-amp1 dbar-amp1 param-amp2 dbar-amp2})
                                prompt (format "Noise %d Amp (0 <= a <= 1)"
                                               (if (= param param-amp1) 1 2))]
                            (dbar/displaybar-dialog dbar prompt
                                                    :validator (fn [q](let [f (math/str->float q)]
                                                                        (and f (<= 0 f)(<= f 1))))
                                                    :callback (fn [_]
                                                                (let [f (math/str->float (.current-display dbar))]
                                                                  (.set-param! ied param f))))))
        b-edit-amp1 (sf/mini-edit-button drw (amp-pos :amp1-edit p0) param-amp1 amp-edit-action)
        b-edit-amp2 (sf/mini-edit-button drw (amp-pos :amp2-edit p0) param-amp2 amp-edit-action)
        slider-action (fn [s _] 
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        s-lfo (sf/vslider drw ied param-lfo (amp-pos :lfo p0) 0.0 1.0 slider-action)
        s-vel (sf/vslider drw ied param-vel (amp-pos :vel p0) 0.0 1.0 slider-action)
        s-prss (sf/vslider drw ied param-prss (amp-pos :prss p0) 0.0 1.0 slider-action)
        s-cca (sf/vslider drw ied param-cca (amp-pos :cca p0) 0.0 1.0 slider-action)
        s-bw1 (sf/vslider drw ied param-bw1 (amp-pos :bw1 p0) con/min-noise-filter-bw con/max-noise-filter-bw slider-action)
        s-bw2 (sf/vslider drw ied param-bw2 (amp-pos :bw2 p0) con/min-noise-filter-bw con/max-noise-filter-bw slider-action)
        s-lag (sf/vslider drw ied param-lag (amp-pos :lag p0) 0 con/max-noise-lag slider-action)
         keynum-action (fn [b _]
                          (let [n (first (msb/current-multistate-button-state b))
                                keynum (+ 36 (* n 12))]
                            (.set-param! ied param-key keynum)))
        msb-key (msb/text-multistate-button (.tool-root drw)
                                            (amp-pos :key p0)
                                            keyscale-key-states
                                            :id param-key
                                            :click-action keynum-action
                                            :rim-color (lnf/button-border)
                                            :gap 6)
        keyscale-action (fn [b _]
                          (let [index (first (msb/current-multistate-button-state b))
                                db (- (* 3 index) 12)
                                param (.get-property b :id)]
                            (.set-param! ied param db)))

        msb-left (msb/text-multistate-button (.tool-root drw)
                                             (amp-pos :left p0)
                                             keyscale-states
                                             :id param-left
                                             :click-action keyscale-action
                                             :rim-color (lnf/button-border)
                                             :gap 6)
        msb-right (msb/text-multistate-button (.tool-root drw)
                                              (amp-pos :right p0)
                                              keyscale-states
                                              :id param-right
                                              :click-action keyscale-action
                                              :rim-color (lnf/button-border)
                                              :gap 6)]
    (fn [dmap]
      (let [a1 (param-amp1 dmap)
            a2 (param-amp2 dmap)
            lfo (param-lfo dmap)
            vel (param-vel dmap)
            prss (param-prss dmap)
            cca (param-cca dmap)
            bw1 (param-bw1 dmap)
            bw2 (param-bw2 dmap)
            lag (param-lag dmap)
            key (let [v (param-key dmap)]
                  (math/clamp (- (int (/ v 12)) 3) 0 5))
            left (let [v (param-left dmap)]
                   (int (math/clamp (+ (* 1/3 v) 4) 0 8)))
            right (let [v (param-right dmap)]
                    (int (math/clamp (+ (* 1/3 v) 4) 0 8)))]
        
        (.display! dbar-amp1 (format "%6.4f" (float a1)) false)
        (.display! dbar-amp2 (format "%6.4f" (float a2)) false)
        (slider/set-slider-value! s-lfo (float lfo) false)
        (slider/set-slider-value! s-vel (float vel) false)
        (slider/set-slider-value! s-prss (float prss) false)
        (slider/set-slider-value! s-cca (float cca) false)
        (slider/set-slider-value! s-lag (float lag) false)
        (slider/set-slider-value! s-bw1 (int bw1) false)
        (slider/set-slider-value! s-bw2 (int bw2) false)
        (msb/set-multistate-button-state! msb-key key false)
        (msb/set-multistate-button-state! msb-left left false)
        (msb/set-multistate-button-state! msb-right right false)))))
