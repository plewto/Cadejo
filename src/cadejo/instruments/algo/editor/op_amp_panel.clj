(ns cadejo.instruments.algo.editor.op-amp-panel
   (:use [cadejo.instruments.algo.algo-constants])
   (:require [cadejo.instruments.algo.editor.factory :as factory])  
   (:require [cadejo.util.math :as math])
   (:require [cadejo.ui.util.lnf :as lnf])
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
        y1 (- y0 30)
        y2 (- y1 slider-length)
        y3 (- y0 height)
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (float (slider/get-slider-value s))]
                   (.set-param! ied param val)))
        s-velocity (factory/slider tools [x1 y1] param-velocity -1.0 1.0 action :signed)
        s-pressure (factory/slider tools [x2 y1] param-pressure -1.0 1.0 action :signed)
        s-lfo1 (factory/slider tools [x3 y1] param-lfo1 -1.0 1.0 action :signed)
        s-lfo2 (factory/slider tools [x4 y1] param-lfo2 -1.0 1.0 action :signed)
        s-cca (factory/slider tools [x5 y1] param-cca -1.0 1.0 action :signed)
        s-ccb (factory/slider tools [x6 y1] param-ccb -1.0 1.0 action :signed)
        dbar (factory/displaybar root [(+ x1 80) (- y2 40)] 5)
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
                                              
        b-edit (factory/mini-edit-button tools [(- x6 14)(- y2 38)] op-id edit-action)
                                 
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
    (factory/slider-label root [x1 y1] op-id "Vel")
    (factory/slider-label root [x2 y1] op-id "Prss")
    (factory/slider-label root [x3 y1] op-id "LFO1")
    (factory/slider-label root [x4 y1] op-id "LFO2")
    (factory/slider-label root [x5 y1] op-id "CCA")
    (factory/slider-label root [x6 y1] op-id "CCB")
    (factory/section-label root [(+ x0 10)(+ y3 25)] op-id "Amplitude")
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
                             :style :solid
                             :color (lnf/major-tick-color))
                  (text/text root [(- xa 33)(+ y 5)] (format "%+4.1f" n)
                             :id op-id
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))
          minor (fn [y n]
                  (line/line root [xa y][xb y] :id op-id
                             :style :solid
                             :color (lnf/minor-tick-color))
                  (text/text root [(- xa 33)(+ y 5)] (format "%+4.1f" n)
                             :id op-id
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))]
      (while (<= @v* 1.0)
        (if (or (= @v* -1.0)(= @v* 1.0)(zero? @v*))
          (major @y* @v*)
          (minor @y* @v*))
        (swap! y* (fn [q](- q y-delta)))
        (swap! v* (fn [q](+ q v-delta)))))
    (factory/inner-border root [x0 y0][(+ x0 width)(- y0 height)])
    (.display! dbar " ")
    {:sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))
