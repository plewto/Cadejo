;; General amplitude
;; - lowpass filter
;; - cc7 depth
;; - amp

(ns cadejo.instruments.algo.editor.amp-panel
  (:use [cadejo.instruments.algo.algo-constants])
  ;(:require [cadejo.instruments.algo.editor.factory :as factory :reload true])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing :as drw])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))

(defn amp-panel [drw p0 ied]
  (let [param-lp :lp
        param-amp :amp
        param-cc7 :cc-volume-depth
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        x-filter (+ x0 50)
        x-cc7 (+ x-filter (* 1.75 slider-spacing))
        x-vol (+ x-cc7 (* 1.75 slider-spacing))
        y-sliders (- y0 30)
        y-labels (- y0 10)
        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        ;; s-filter (factory/lp-slider tools [x-filter y-sliders] param-lp slider-action)
        ;; s-cc7 (factory/slider tools [x-cc7 y-sliders] param-cc7
        ;;                       0.0 1.0 slider-action false)

        s-filter (sfactory/vslider drw ied param-lp [x-filter y-sliders] min-lp-freq max-lp-freq slider-action
                                   :value-hook (fn [n](* 1000 (int (/ n 1000)))))
        s-cc7 (sfactory/vslider drw ied param-cc7 [x-cc7 y-sliders] 0.0 1.0 slider-action)
                                                        

        volume-action (fn [s _]
                        (let [db (int (slider/get-slider-value s))
                              lin (math/db->amp db)]
                          (.set-param! ied param-amp lin)
                          (.status! ied (format "[:amp] -> %d db  -> %f" db lin))))
        ;;s-vol (factory/volume-slider tools [x-vol y-sliders] param-amp volume-action)

        s-vol (sfactory/vslider drw ied param-amp [x-vol y-sliders] min-amp-db max-amp-db volume-action
                                :value-hook (fn [n](* 3 (int (/ n 3)))))
                                                     
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        filter-freq (float (param-lp dmap))
                        lin-amp (float (param-amp dmap))
                        db (int (math/amp->db lin-amp))
                        cc7 (float (param-cc7 dmap))]
                    (slider/set-slider-value! s-filter filter-freq)
                    (slider/set-slider-value! s-cc7 cc7)
                    (slider/set-slider-value! s-vol db)))]
    (sfactory/label drw [(- x-filter 8) y-labels] "LP")
    (sfactory/label drw [(- x-cc7 12) y-labels] "CC7")
    (sfactory/label drw [(- x-vol 20) y-labels] "Amp db")
    ;; Draw filter ticks
    (let [x1 (- x-filter 10)
          x2 (+ x-filter 10)
          y1 y-sliders
          y2 (- y1 slider-length)
          y* (atom y-sliders)
          n 10
          delta-y (/ slider-length (dec n))
          f* (atom min-lp-freq)
          delta-f 1000]
      (while (>= @y* y2)
        (line/line root [x1 @y*][x2 @y*] :id :amp 
                   :style :solid
                   :color (lnf/minor-tick))
        (text/text root [(- x1 30)(+ @y* 4)] 
                   (format "%2dk" (int (/ @f* 1000)))
                   :style :mono
                   :size 6
                   :color (lnf/minor-tick))
        (swap! y* (fn [q](- q delta-y)))
        (swap! f* (fn [q](+ q delta-f)))))
    ;; Draw cc7 ticks
    (let [x1 (- x-cc7 10)
          x2 (+ x-cc7 10)
          y1 y-sliders
          y2 (- y1 slider-length)
          y* (atom y-sliders)
          n 4
          v1 1.0
          v0 0.0
          v* (atom v0)
          delta-v (/ (- v1 v0) n)
          delta-y (/ slider-length n)]
      (while (>= @y* y2)
        (line/line root [x1 @y*][x2 @y*] :id :amp
                   :style :solid
                   :color (lnf/minor-tick))
        (text/text root [(- x1 35)(+ @y* 4)] 
                   (format "%4.2f" @v*)
                   :id :amp
                   :style :mono
                   :size 6
                   :color (lnf/minor-tick))
        (swap! y* (fn [q](- q delta-y)))
        (swap! v* (fn [q](+ q delta-v)))))
    ;; Draw volume ticks
    (let [x1 (- x-vol 10)
          x2 (+ x-vol 10)
          y1 y-sliders
          y2 (- y1 slider-length)
          y* (atom y-sliders)
          n 16.0
          v* (atom min-amp-db)
          delta-y (/ slider-length n)
          delta-v 3
          counter* (atom 0)]
      (while (>= @y* y2)
        (line/line root [x1 @y*][x2 @y*] :id :amp
                   :style :solid
                   :color (lnf/minor-tick))
        (if (even? @counter*)
          (text/text root [(- x1 35)(+ @y* 4)] 
                     (format "%+3d" @v*)
                     :style :mono
                     :id :amp
                     :size 6
                     :color (lnf/minor-tick)))
        (swap! y* (fn [q](- q delta-y)))
        (swap! v* (fn [q](+ q delta-v)))
        (swap! counter* inc)))
    (sfactory/title drw [(+ x0 20)(- y0 222)] "Out")
    (sfactory/minor-border drw [x0 y0][(+ x0 260)(- y0 250)])
    {:sync-fn sync-fn}))
