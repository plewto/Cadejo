(ns cadejo.instruments.cobalt.editor.op-amp-panel
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider]))

(def left-margin 30)

(def keyscale-states [[:n12 "-12" :red]
                      [:n9  "-9"  :red]
                      [:n6  "-6"  :red]
                      [:n3  "-3"  :red]
                      [:0   " 0"  :green]
                      [:3   "+3"  :green]
                      [:6   "+6"  :green]
                      [:9   "+9"  :green]
                      [:12  "+12" :green]])

(def keyscale-key-states [[:36 "C1 " :green]
                          [:48 "C2 " :green]
                          [:60 "C3 " :green]
                          [:72 "C4 " :green]
                          [:84 "C5 " :green]
                          [:96 "C6 " :green]])

(defn amp-pos [item p0]
  (let [pan-width 410
        pan-height 240
        x-offset left-margin
        y-offset 190
        slider-space 50
        x0 (+ (first p0) x-offset)
        x-amp (+ x0 110)
        x-amp-edit (+ x-amp 160)
        x-label (+ x0 20)
        x-lfo (+ x0 75)
        x-vel (+ x-lfo slider-space)
        x-prss (+ x-vel slider-space)
        x-cca (+ x-prss slider-space)
        x-ccb (+ x-cca slider-space)
        x-keyscale (+ x0 350)
        x-border (+ x0 pan-width)
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-amp (+ y-border 20)
        y-slider1 (+ y-amp 45)
        y-slider2 (+ y-slider1 sf/slider-length)
        y-keyscale (+ y-border 80)
        y-left (+ y-keyscale 40)
        y-right (+ y-left 40)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :amp [x-amp y-amp]
          :amp-edit [x-amp-edit y-amp]
          :amp-label [x-label (+ y-amp 20)]
          :vel [x-vel y-slider2]
          :prss [x-prss y-slider2]
          :cca [x-cca y-slider2]
          :ccb [x-ccb y-slider2]
          :lfo [x-lfo y-slider2]
          :key [x-keyscale y-keyscale]
          :left [x-keyscale y-left]
          :right [x-keyscale y-right]}
          item)))

(defn draw-amp-panel [op bg p0]
  (let [positions [(amp-pos :lfo p0)(amp-pos :vel p0)(amp-pos :prss p0)
                   (amp-pos :cca p0)(amp-pos :ccb p0)]
        y1 (second (first positions))
        y2 (- y1 sf/slider-length)]
    (sf/label bg (amp-pos :vel p0) "Vel" :offset [-10 20])
    (sf/label bg (amp-pos :prss p0) "Prss" :offset [-12 20])
    (sf/label bg (amp-pos :cca p0) "CCA" :offset [-10 20])
    (sf/label bg (amp-pos :ccb p0) "CCB" :offset [-10 20])
    (sf/label bg (amp-pos :lfo p0) "LFO1" :offset [-10 20])
    (doseq [x (map first positions)]
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 
                           :frmt (if (= x (first (first positions))) "%4.2f" "")
                           :x-offset -42
                           :step 0.25)
      (sf/minor-ticks bg x y1 y2 20))
    (sf/label bg (amp-pos :amp-label p0) "Amp")
    (sf/label bg (amp-pos :key p0) "Keyscale" :offset [-15 -15])
    (sf/label bg (amp-pos :key p0) "Key" :offset [-45 15])
    (sf/label bg (amp-pos :left p0) "Left" :offset [-45 15])
    (sf/label bg (amp-pos :right p0) "Right" :offset [-45 15])
    (sf/minor-border bg (amp-pos :p0 p0)(amp-pos :border p0))))
      
(defn- param [op suffix]
  (let [prefix (cond (= op :nse) "nse"
                     (= op :bzz) "bzz"
                     :default (format "op%d" op))]
    (keyword (format "%s-%s" prefix suffix))))       


(defn amp-panel [op drw ied p0]
  (let [param-amp (param op "amp") 
        param-vel (param op "amp<-velocity") 
        param-prss (param op "amp<-pressure") 
        param-cca (param op "amp<-cca") 
        param-ccb (param op "amp<-ccb") 
        param-lfo (param op "amp<-lfo1") 
        param-key (param op "keyscale-key") 
        param-left (param op "keyscale-left") 
        param-right (param op "keyscale-right") 
        dbar-amp (sf/displaybar drw (amp-pos :amp p0) 5)
        amp-edit-action (fn [& _] 
                          (let [prompt (if (= op :bzz)
                                         "Buzz Amp (0 < a < 1)"
                                         (format "OP%d Amp (0 < a < 1)" op))
                                validator (fn [q]
                                            (let [f (math/str->float q)]
                                              (and f (pos? f)(<= f 1))))
                                callback (fn [_]
                                           (let [f (math/str->float (.current-display dbar-amp))]
                                             (.set-param! ied param-amp f)))]
                            (dbar/displaybar-dialog dbar-amp prompt
                                                    :validator validator
                                                    :callback callback)))
        b-amp (sf/mini-edit-button drw (amp-pos :amp-edit p0) :op-amp-edit amp-edit-action)
        slider-action (fn [s _]
                        (let [p (.get-property s :id)
                              v (slider/get-slider-value s)]
                          (.set-param! ied p v)))
        s-lfo (sf/vslider drw ied param-lfo (amp-pos :lfo p0) 0.0 1.0 slider-action)
        s-cca (sf/vslider drw ied param-cca (amp-pos :cca p0) 0.0 1.0 slider-action)
        s-vel (sf/vslider drw ied param-vel (amp-pos :vel p0) 0.0 1.0 slider-action)
        s-prss (sf/vslider drw ied param-prss (amp-pos :prss p0) 0.0 1.0 slider-action)
        s-ccb (sf/vslider drw ied param-ccb (amp-pos :ccb p0) 0.0 1.0 slider-action)
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
    (let [amp (param-amp dmap)
          vel (param-vel dmap)
          prss (param-prss dmap)
          cca (param-cca dmap)
          ccb (param-ccb dmap)
          lfo (param-lfo dmap)
          key (let [v (param-key dmap)]
                (math/clamp (- (int (/ v 12)) 3) 0 5))
          left (let [v (param-left dmap)]
                 (int (math/clamp (+ (* 1/3 v) 4) 0 8)))
          right (let [v (param-right dmap)]
                 (int (math/clamp (+ (* 1/3 v) 4) 0 8)))]
      (slider/set-slider-value! s-vel (float vel) false)
      (slider/set-slider-value! s-prss (float prss) false)
      (slider/set-slider-value! s-cca (float cca) false)
      (slider/set-slider-value! s-ccb (float ccb) false)
      (slider/set-slider-value! s-lfo (float lfo) false)
      (.display! dbar-amp (format "%5.3f" (float amp)) false)
      (msb/set-multistate-button-state! msb-key key false)
      (msb/set-multistate-button-state! msb-left left false)
      (msb/set-multistate-button-state! msb-right right false))))) 
      
