(ns cadejo.instruments.algo.editor.op-amp-panel
   (:use [cadejo.instruments.algo.algo-constants])
   (:require [cadejo.util.math :as math])
   (:require [cadejo.ui.util.lnf :as lnf])
   (:require [cadejo.ui.util.sgwr-factory :as sfactory])
   (:require [sgwr.components.line :as line])
   (:require [sgwr.components.text :as text])
   (:require [sgwr.indicators.displaybar :as dbar])
   (:require [sgwr.tools.slider :as slider]))

(defn op-amp [n drw p0 ied]
  (let [param-amp (keyword (format "op%d-amp" n))
        param-velocity (keyword (format "op%d-velocity" n))
        param-pressure (keyword (format "op%d-pressure" n))
        param-lfo1 (keyword (format "op%d-lfo1" n))
        param-lfo2 (keyword (format "op%d-lfo2" n))
        param-cca (keyword (format "op%d-cca" n))
        param-ccb (keyword (format "op%d-ccb" n))
        op-id (keyword (format "op%d" n))
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        width 350
        height 230
        slider-count 6
        [x1 x2 x3 x4 x5 x6 x7 x8](range (+ x0 (* 1.0 slider-spacing)) (+ x0 (* slider-spacing 10.0)) slider-spacing)
        x-label-offset -12
        y1 (- y0 30)
        y2 (- y1 slider-length)
        y3 (- y0 height)
        y-label-offset 22
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (float (slider/get-slider-value s))]
                   (.set-param! ied param val)))
        s-velocity (sfactory/vslider drw ied param-velocity [x1 y1]  -1.0 1.0 action)
        s-pressure (sfactory/vslider drw ied param-pressure [x2 y1]  -1.0 1.0 action)
        s-lfo1 (sfactory/vslider drw ied param-lfo1 [x3 y1]  -1.0 1.0 action)
        s-lfo2 (sfactory/vslider drw ied param-lfo2 [x4 y1]  -1.0 1.0 action)
        s-cca (sfactory/vslider drw ied param-cca [x5 y1]  -1.0 1.0 action)
        s-ccb (sfactory/vslider drw ied param-ccb [x6 y1]  -1.0 1.0 action)
        dbar (sfactory/displaybar drw [(+ x1 80) (- y2 40)] 5)
        edit-action (fn [b _]
                      (dbar/displaybar-dialog dbar
                                              (format "Amplitude op %d" n)
                                              :validator (fn [q]
                                                           (let [v (math/str->float q)]
                                                             (and v (>= v 0))))
                                              :callback (fn [_]
                                                          (let [s (.current-display dbar)
                                                                v (math/str->float s)]
                                                            (.set-param! ied param-amp v)))))
                                              
        b-edit (sfactory/mini-edit-button drw [(- x6 14)(- y2 38)] op-id edit-action)
                                 
        tool-list (list s-velocity s-pressure s-lfo1 s-lfo2 s-cca s-ccb b-edit dbar)

        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        amp (float (param-amp dmap))
                        velocity (float (param-velocity dmap))
                        pressure (float (param-pressure dmap))
                        lfo1 (float (param-lfo1 dmap))
                        lfo2 (float (param-lfo2 dmap))
                        cca (float (param-cca dmap))
                        ccb (float (param-ccb dmap))]
                    (.display! dbar (format "%6.3f" amp) false)
                    (slider/set-slider-value! s-velocity velocity false)
                    (slider/set-slider-value! s-pressure pressure false)
                    (slider/set-slider-value! s-lfo1 lfo1 false)
                    (slider/set-slider-value! s-lfo2 lfo2 false)
                    (slider/set-slider-value! s-cca cca false)
                    (slider/set-slider-value! s-ccb ccb false)))

        disable-fn (fn []
                     (doseq [q tool-list]
                       (.disable! q false)))
        enable-fn (fn []
                    (doseq [q tool-list]
                      (.enable! q false)))]
    (sfactory/label drw [(+ x1 x-label-offset)(+ y1 y-label-offset)] "Vel")
    (sfactory/label drw [(+ x2 x-label-offset)(+ y1 y-label-offset)] "Prss")
    (sfactory/label drw [(+ x3 x-label-offset)(+ y1 y-label-offset)] "LFO1")
    (sfactory/label drw [(+ x4 x-label-offset)(+ y1 y-label-offset)] "LFO2")
    (sfactory/label drw [(+ x5 x-label-offset)(+ y1 y-label-offset)] "CCA")
    (sfactory/label drw [(+ x6 x-label-offset)(+ y1 y-label-offset)] "CCB")
    (sfactory/text drw [(+ x0 10)(+ y3 25)] "Amplitude")
    ;; slider tickmarks
    (let [xa (- x1 (* 0.5 minor-tick-length))
          xb (+ x6 minor-tick-length)
          y-count 4
          y-delta (/ slider-length y-count)
          y* (atom y1)
          v-delta 0.5
          v* (atom -1.0)
          major (fn [y n]
                  (line/line root [xa y][xb y] :id op-id
                             :style :dotted
                             :color (lnf/major-tick))
                  (text/text root [(- xa 33)(+ y 5)] (format "%+4.1f" n)
                             :id op-id
                             :style :mono
                             :size 6
                             :color (lnf/major-tick)))]
      (while (<= @v* 1.0)
        (major @y* @v*)
        (swap! y* (fn [q](- q y-delta)))
        (swap! v* (fn [q](+ q v-delta)))))
    (sfactory/minor-border drw [x0 y0][(+ x0 width)(- y0 height)])
    (.display! dbar "XXXXX")
    {:sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))
