;; Step counter 1 & 2
;; Frequency dividers 1 & 2

(ns cadejo.instruments.alias.editor.step-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 2000)
(def ^:private height 500)
(def ^:private bottom-margin 100)
(def ^:private max-counter-value constants/max-counter-value)
(def ^:private max-counter-step-size constants/max-counter-step-size)
(def ^:private max-counter-bias constants/max-counter-bias)
(def ^:private min-counter-scale constants/min-counter-scale)
(def ^:private max-counter-scale constants/max-counter-scale)
(def ^:private counter1-xoffset 10)
(def ^:private counter2-xoffset 435)
(def ^:private divider1-xoffset 860)
(def ^:private divider2-xoffset 1370)

(declare draw-background)
(declare draw-counter-panel)
(declare draw-divider-panel)
(declare counter-panel)
(declare divider-panel)

(defn step-editor [ied]
  (let [p0 [0 (- height bottom-margin)]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        counter1 (counter-panel 1 drw ied p0)
        counter2 (counter-panel 2 drw ied p0)
        divider1 (divider-panel 1 drw ied p0)
        divider2 (divider-panel 2 drw ied p0)
        pan-main (ss/scrollable (ss/vertical-panel :items [(.canvas drw)]))
        widget-map {:pan-main pan-main
                    :drawing drw}
        ed (reify subedit/InstrumentSubEditor
             (widgets [this] widget-map)
             (widget [this key](key widget-map))
             (parent [this] ied)
             (parent! [this _] ied) ;;' ignore
             (status! [this msg](.status! ied msg))
             (warning! [this msg](.warning! ied msg))
             (set-param! [this param value](.set-param! ied param value))
             (init! [this]  ) ;; not implemented
             (sync-ui! [this]
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (doseq [sp [counter1 counter2 divider1 divider2]]
                   (sp dmap)))
               (.render drw)))]
    ed))

(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- msb-state! [b n]
  (msb/set-multistate-button-state! b n false))

(defn- vertical-position [item p0]
  (let [y0 (second p0) 
        y-slider1 (- y0 90)
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-title (- y-slider2 20)
        source-button-position (+ y-slider1 35)
        map {:p0 y0
             :title y-title
             :slider1 y-slider1 
             :slider2 y-slider2
             :button (+ y-slider1 20)
             :border (- y-title 25)}]
    (get map item y-slider1)))

(defn- draw-background [ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)]
    (draw-counter-panel 1 bg p0)
    (draw-counter-panel 2 bg p0)
    (draw-divider-panel 1 bg p0)
    (draw-divider-panel 2 bg p0)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))

; ---------------------------------------------------------------------- 
;                                  Counter

(defn- counter-pos [n item p0]
  (let [x0 (+ (first p0) (if (= n 1) counter1-xoffset counter2-xoffset))
        x-min (+ x0 60)
        x-max (+ x-min 60)
        x-step (+ x-max 60)
        x-reset (+ x-step 60)
        x-bias (+ x-reset 80)
        x-scale (+ x-bias 60)
        x-trigger-src (+ x0 120)
        x-reset-src (+ x-trigger-src 220)
        x-title (+ x0 10)
        x-border (+ x-scale 40)
        x-map {:p0 x0 :title x-title :border x-border
               :min x-min :max x-max :step x-step :reset x-reset :bias x-bias :scale x-scale
               :trig-src x-trigger-src :reset-src x-reset-src}
        y0 (second p0)
        y-slider1 (- y0 100)
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-src (+ y-slider1 30)
        y-title (- y-slider2 20)
        y-border (- y-title 30)
        y-map {:p0 y0 :title y-title :border y-border
               :trig-src y-src :reset-src y-src}
        x (item x-map)
        y (get y-map item y-slider1)]
    [x y]))

(defn- draw-counter-panel [n bg p0]
  (let [[x0 y0](counter-pos n :p0 p0)
        [x-min y1](counter-pos n :min p0)
        x-max (first (counter-pos n :max p0))
        x-step (first (counter-pos n :step p0))
        x-reset (first (counter-pos n :reset p0))
        x-bias (first (counter-pos n :bias p0))
        x-scale (first (counter-pos n :scale p0))
        [x-trig-src y-src](counter-pos n :trig-src p0)
        x-reset-src (first (counter-pos n :reset-src p0))
        y2 (- y1 sfactory/slider-length)]
    (sfactory/label bg [x-min y1] "Min" :offset [-10 20])
    (sfactory/label bg [x-max y1] "Max" :offset [-10 20])
    (sfactory/label bg [x-step y1] "Step" :offset [-12 20])
    (sfactory/label bg [x-reset y1] "Reset" :offset [-16 20])
    (sfactory/label bg [x-bias y1] "Bias" :offset [-12 20])
    (sfactory/label bg [x-scale y1] "Scale" :offset [-14 20])
    (sfactory/label bg [x-trig-src y-src] "Trig Input" :offset [-80 25])
    (sfactory/label bg [x-reset-src y-src] "Reset Input" :offset [-80 25])
    (doseq [x [x-min x-max x-step x-reset]]
      (sfactory/minor-ticks bg x y1 y2 20)
      (sfactory/major-tick-marks bg x y1 y2 
                                 :v0 (- max-counter-value) :v1 max-counter-value
                                 :step 50
                                 :frmt "%+4d"))
    (sfactory/minor-ticks bg x-bias y1 y2 20)
    (sfactory/major-tick-marks bg x-bias y1 y2
                          :v0 (- max-counter-bias) :v1 max-counter-bias
                          :step 2
                          :frmt "%3d")
    (sfactory/major-tick-marks bg x-scale y1 y2
                               :v0 0.00 :v1 1.0
                               :step 0.1
                               :frmt "%3.2f")
    (sfactory/title bg (counter-pos n :title p0) (format "Step Counter %d" n))
    (sfactory/minor-border bg (counter-pos n :p0 p0)(counter-pos n :border p0))))

(defn- counter-panel [n drw ied p0]
  (let [param-trigger-src (keyword (format "stepper%d-trigger" n))
        param-reset-src (keyword (format "stepper%d-reset" n))
        param-min (keyword (format "stepper%d-min" n))
        param-max (keyword (format "stepper%d-max" n))
        param-step (keyword (format "stepper%d-step" n))
        param-reset (keyword (format "stepper%d-reset-value" n))
        param-bias (keyword (format "stepper%d-bias" n))
        param-scale (keyword (format "stepper%d-scale" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-min (sfactory/vslider drw ied param-min (counter-pos n :min p0)
                                (- max-counter-value) max-counter-value action
                                :value-hook int)
        s-max (sfactory/vslider drw ied param-max (counter-pos n :max p0)
                                (- max-counter-value) max-counter-value action
                                :value-hook int)
        s-step (sfactory/vslider drw ied param-step (counter-pos n :step p0)
                                 (- max-counter-step-size) max-counter-step-size
                                 action :value-hook int)
        s-reset (sfactory/vslider drw ied param-reset (counter-pos n :reset p0)
                                  (- max-counter-value) max-counter-value
                                  action :value-hook int)
        s-bias (sfactory/vslider drw ied param-bias (counter-pos n :bias p0)
                                 (- max-counter-bias) max-counter-bias
                                 action :value-hook int)
        s-scale (sfactory/vslider drw ied param-scale (counter-pos n :scale p0)
                                  min-counter-scale max-counter-scale
                                  action)
        b-trigger (matrix/control-source-button drw ied param-trigger-src (counter-pos n :trig-src p0))
        b-reset (matrix/control-source-button drw ied param-reset-src (counter-pos n :reset-src p0))
        sync-fn (fn [dmap]
                  (slider-value! s-min (param-min dmap))
                  (slider-value! s-max (param-max dmap))
                  (slider-value! s-step (param-step dmap))
                  (slider-value! s-bias (param-bias dmap))
                  (slider-value! s-scale (param-scale dmap))
                  (msb-state! b-trigger (int (param-trigger-src dmap)))
                  (msb-state! b-reset (int (param-reset-src dmap))))]
    sync-fn))


; ---------------------------------------------------------------------- 
;                                  Divider

(defn- divider-pos [n item p0]
  (let [x0 (+ (first p0)(if (= n 1) divider1-xoffset divider2-xoffset))
        x-pw (+ x0 60)
        x-p1 (+ x-pw 60)
        x-p2 (+ x-p1 60)
        x-p3 (+ x-p2 60)
        x-p4 (+ x-p3 60)
        x-bias (+ x-p4 80)
        x-scale-depth (+ x-bias 60)
        x-scale-source (- x-scale-depth 20)
        x-title (+ x0 10)
        x-border (+ x-scale-depth 60)
        x-map {:p0 x0 :title x-title :border x-border
               :pw x-pw :p1 x-p1 :p2 x-p2 :p3 x-p3 :p4 x-p4
               :bias x-bias :scale x-scale-depth :scale-src x-scale-source}
        y0 (second p0)
        y-slider1 (- y0 100)
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-scale-source (+ y-slider1 40)
        y-src (+ y-slider1 30)
        y-title (- y-slider2 20)
        y-border (- y-title 30)
        y-map {:p0 y0 :title y-title :border y-border :scale-src y-scale-source}
        x (item x-map)
        y (get y-map item y-slider1)]
    [x y]))

(defn- draw-divider-panel [n bg p0]
  (let [[x0 y0](divider-pos  n :p0 p0)
        [x-pw y1](divider-pos n :pw p0)
        x-p1 (first (divider-pos n :p1 p0))
        x-p2 (first (divider-pos n :p2 p0))
        x-p3 (first (divider-pos n :p3 p0))
        x-p4 (first (divider-pos n :p4 p0))
        x-bias (first (divider-pos n :bias p0))
        x-scale (first (divider-pos n :scale p0))
        y2 (- y1 sfactory/slider-length)]
    (sfactory/label bg [x-pw y1] "PW" :offset [-5 25])
    (sfactory/label bg [x-p1 y1] (if (= n 1) "1/1" "1/2") :offset [-5 25])
    (sfactory/label bg [x-p2 y1] (if (= n 1) "1/3" "1/4") :offset [-5 25])
    (sfactory/label bg [x-p3 y1] (if (= n 1) "1/5" "1/6") :offset [-5 25])
    (sfactory/label bg [x-p4 y1] (if (= n 1) "1/7" "1/8") :offset [-5 25])
    (sfactory/label bg [x-bias y1] "Bias" :offset [-10 25])
    (sfactory/label bg [x-scale y1] "Scale" :offset [-12 25])
    (doseq [x [x-pw x-scale]]
      (sfactory/minor-ticks bg x y1 y2 16)
      (sfactory/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 :step 0.25 :frmt "%4.2f"))
    (doseq [x [x-p1 x-p2 x-p3 x-p4]]
      (sfactory/minor-ticks bg x y1 y2 16)
      (sfactory/major-tick-marks bg x y1 y2))
    (sfactory/minor-ticks bg x-bias y1 y2 20)
    (sfactory/major-tick-marks bg x-bias y1 y2 :v0 -10 :v1 10 :step 2 :frmt "%+3d")
    (sfactory/title bg (divider-pos n :title p0)(format "Divider %d" n))
    (sfactory/label bg [x0 y0] "Trigger Source: LFO 3" :size 5.0 :offset [20 -10])
    (sfactory/minor-border bg [x0 y0](divider-pos n :border p0))))

(defn- divider-panel [n drw ied p0]
  (let [param-pw (keyword (format "divider%d-pw" n))
        param-p1 (keyword (format "divider%d-p%d" n (if (= n 1) 1 2)))
        param-p2 (keyword (format "divider%d-p%d" n (if (= n 1) 3 4)))
        param-p3 (keyword (format "divider%d-p%d" n (if (= n 1) 5 6)))
        param-p4 (keyword (format "divider%d-p%d" n (if (= n 1) 7 8)))
        param-bias (keyword (format "divider%d-bias" n))
        param-scale-source (keyword (format "divider%d-scale-source" n))
        param-scale-depth (keyword (format "divider%d-scale-depth" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-pw (sfactory/vslider drw ied param-pw (divider-pos n :pw p0) 0.1 0.9 action)
        s-p1 (sfactory/vslider drw ied param-p1 (divider-pos n :p1 p0) -1.0 1.0 action)
        s-p2 (sfactory/vslider drw ied param-p2 (divider-pos n :p2 p0) -1.0 1.0 action)
        s-p3 (sfactory/vslider drw ied param-p3 (divider-pos n :p3 p0) -1.0 1.0 action)
        s-p4 (sfactory/vslider drw ied param-p4 (divider-pos n :p4 p0) -1.0 1.0 action)
        s-bias (sfactory/vslider drw ied param-bias (divider-pos n :bias p0) (- max-counter-bias) max-counter-bias action)
        s-depth (sfactory/vslider drw ied param-scale-depth (divider-pos n :scale p0) min-counter-scale max-counter-scale action)
        b-scale-src (matrix/control-source-button drw ied param-scale-source (divider-pos n :scale-src p0))
        sync-fn (fn [dmap]
                  (slider-value! s-pw (param-pw dmap))
                  (slider-value! s-p1 (param-p1 dmap))
                  (slider-value! s-p2 (param-p2 dmap))
                  (slider-value! s-p3 (param-p3 dmap))
                  (slider-value! s-p4 (param-p4 dmap))
                  (slider-value! s-bias (param-bias dmap))
                  (slider-value! s-depth (param-scale-depth dmap))
                  (msb-state! b-scale-src (int (param-scale-source dmap))))]
    sync-fn))
