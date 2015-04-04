;; LFO
;; LF-NOISE
;; Sample and Hold
;;
(ns cadejo.instruments.alias.editor.lf-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 1600)
(def ^:private height 500)
(def ^:private bottom-margin 100)
(def ^:private lfo-panel-width 360)
(def ^:private lfo1-xoffset bottom-margin)
(def ^:private lfnoise-xoffset (+ lfo1-xoffset (* 3 lfo-panel-width)))
(def ^:private snh-xoffset (+ lfnoise-xoffset 105))
(def ^:private max-snh-freq constants/max-snh-freq)
(def ^:private max-snh-bias constants/max-snh-bias)
(def ^:private max-snh-scale constants/max-snh-scale)
(def ^:private lfo-slider-length 210)
(def ^:private waveshape-length 12)
(def ^:private waveshape-height 5)

(declare draw-background)
(declare draw-lfo-panel)
(declare draw-lfnoise-panel)
(declare draw-snh-panel)
(declare lfo-panel)
(declare lfnoise-panel)
(declare snh-panel)

(defn lf-editor [ied]
  (let [p0 [0 (- height bottom-margin)]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        lfo1 (lfo-panel 1 drw ied p0)
        lfo2 (lfo-panel 2 drw ied p0)
        lfo3 (lfo-panel 3 drw ied p0)
        lfnoise (lfnoise-panel drw ied p0)
        snh (snh-panel drw ied p0)
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
                 (doseq [sp [lfo1 lfo2 lfo3 lfnoise snh]]
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
  (let [y0 (- (second p0) bottom-margin)
        y-slider1 (- y0 90)
        y-slider2 (- y-slider1 lfo-slider-length)
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
    (draw-lfo-panel 1 bg p0)
    (draw-lfo-panel 2 bg p0)
    (draw-lfo-panel 3 bg p0)
    (draw-lfnoise-panel bg p0)
    (draw-snh-panel bg p0)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))

; ---------------------------------------------------------------------- 
;                                    LFO

(defn- lfo-pos [n item p0]
  (let [x0 (+ (first p0) lfo1-xoffset (* (dec n) lfo-panel-width))
        slider-length sfactory/slider-length
        x-wave (+ x0 60)                              ; slider left
        x2 (+ x-wave slider-length)                   ; slider right
        x-display (+ x0 60)
        x-map {:p0 x0 :title (+ x0 10) :border (+ x0 (- lfo-panel-width 5))
               :freq1 x-display :freq2 x-display
               :wave1 x-wave :wave2 x-wave}
        y0 (second p0)
        y-wave2 (- y0 50)
        y-wave1 (- y-wave2 60)
        y-freq2 (- y-wave1 80)
        y-freq1 (- y-freq2 60)
        y-title (- y-freq1 30)
        y-border (- y-title 30)
        y-map {:p0 y0 :title y-title :border y-border
               :freq1 y-freq1 :freq2 y-freq2
               :wave1 y-wave1 :wave2 y-wave2}
        x (item x-map)
        y (item y-map)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate lfo-pos item = %s" item)))
    [x y]))


(defn- draw-triangle [bg x0 yc]
  (let [y1 (- yc waveshape-height)
        y2 (+ yc waveshape-height)
        x1 (+ x0 (* 1/4 waveshape-length))
        x2 (+ x0 (* 3/4 waveshape-length))
        x3 (+ x0 waveshape-length)
        c (lnf/major-tick)
        root (.root bg)]
    (line/line root [x0 yc][x1 y1] :color c :id :tri-wave)
    (line/line root [x1 y1][x2 y2] :color c :id :tri-wave)
    (line/line root [x2 y2][x3 yc] :color c :id :tri-wave)))
    
(defn- draw-possaw [bg x0 yc]
  (let [y1 (- yc waveshape-height)
        y2 (+ yc waveshape-height)
        x1 (+ x0 waveshape-length)
        c (lnf/major-tick)
        root (.root bg)]
    (line/line root [x0 y2][x1 y1] :color c :id :saw-wave)
    (line/line root [x1 y1][x1 y2] :color c :id :saw-wave)))


(defn- draw-negsaw [bg x0 yc]
  (let [y1 (- yc waveshape-height)
        y2 (+ yc waveshape-height)
        x1 (+ x0 waveshape-length)
        c (lnf/major-tick)
        root (.root bg)]
    (line/line root [x0 y2][x0 y1] :color c :id :saw-wave)
    (line/line root [x0 y1][x1 y2] :color c :id :saw-wave)))

(defn- draw-lfo-panel [n bg p0]
  (let [[x-freq1 y-freq1](lfo-pos n :freq1 p0)
        [x-freq2 y-freq2](lfo-pos n :freq2 p0)
        [x-wave1 y-wave1](lfo-pos n :wave1 p0)
        [x-wave2 y-wave2](lfo-pos n :wave2 p0)
        xw-1 x-wave1
        xw+1 (+ x-wave1 lfo-slider-length)
        xw-0 (math/mean xw-1 xw+1)
        xw-2 (math/mean xw-0 xw-1)
        xw+2 (math/mean xw-0 xw+1)
        val* (atom -1.0)] ;; major-tick counter
    ;; Minor ticks
    (doseq [yc [y-wave1 y-wave2]]
      (doseq [x (range x-wave1 (+ x-wave1 lfo-slider-length) 13.125)]
        (line/line (.root bg) [x (- yc 1)][x (+ yc 1)] :color (lnf/minor-tick))))
    ;; Major ticks
    (doseq [x [xw-1 xw-2 xw-0 xw+2 xw+1]]
      (doseq [yc [y-wave1 y-wave2]]
        (line/line (.root bg) [x (- yc 4)][x (+ yc 4)] :color (lnf/major-tick))
        (sfactory/label bg [x yc] (format "%+4.1f" @val*) :size 5 :offset [-12 20] :color (lnf/major-tick)))
      (swap! val* (fn [q](+ q 0.5))))
    (sfactory/label bg [x-freq1 y-freq1] "Freq 1" :offset [-50 20])
    (sfactory/label bg [x-freq2 y-freq2] "Freq 2" :offset [-50 20])
    (sfactory/label bg [x-wave1 y-wave1] "Wave 1" :offset [-50 5])
    (sfactory/label bg [x-wave2 y-wave2] "Wave 2" :offset [-50 5])
    (doseq [y [y-wave1 y-wave2]]
      (doseq [x [xw-2 xw+2]](draw-triangle bg (- x 6)(+ y 30)))
      (doseq [x [xw-1 xw+1]](draw-possaw bg (- x 6)(+ y 30)))
      (draw-negsaw bg (+ xw-0 6)(+ y 30)))
    (sfactory/title bg (lfo-pos n :title p0) (format "LFO %d" n))
    (sfactory/minor-border bg (lfo-pos n :p0 p0)(lfo-pos n :border p0)) ))

(defn- lfo-panel [n drw ied p0]
  (let [param-d1 (keyword (format "lfo%d-freq1-depth" n))
        param-d2 (keyword (format "lfo%d-freq2-depth" n))
        param-d3 (keyword (format "lfo%d-wave1-depth" n))
        param-d4 (keyword (format "lfo%d-wave2-depth" n))
        param-s1 (keyword (format "lfo%d-freq1-source" n))
        param-s2 (keyword (format "lfo%d-freq2-source" n))
        param-s3 (keyword (format "lfo%d-wave1-source" n))
        param-s4 (keyword (format "lfo%d-wave2-source" n))
        [x-freq1 y-freq1](lfo-pos n :freq1 p0)
        [x-freq2 y-freq2](lfo-pos n :freq2 p0)
        [x-wave1 y-wave1](lfo-pos n :wave1 p0)
        [x-wave2 y-wave2](lfo-pos n :wave2 p0)
        x-edit (+ x-freq1 180)
        x-source (+ x-freq1 235)
        dbar1 (sfactory/displaybar drw [x-freq1 y-freq1] 7)
        dbar2 (sfactory/displaybar drw [x-freq2 y-freq2] 7)
        edit-action (fn [b _]
                      (let [id (.get-property b :id)
                            dbar (if (= id :f1) dbar1 dbar2)
                            msg (format "LFO %d freq %s depth" n (name id))]
                        (dbar/displaybar-dialog dbar msg
                                                :validator (fn [q]
                                                             (let [f (math/str->float q)]
                                                               f))
                                                :callback (fn [_]
                                                            (let [s (.current-display dbar)
                                                                  f (math/str->float s)
                                                                  p (if (= id :f1) param-d1 param-d2)]
                                                              (.set-param! ied p f))))))
                        
        b-edit1 (sfactory/mini-edit-button drw [x-edit (+ y-freq1 5)] :f1 edit-action)
        b-edit2 (sfactory/mini-edit-button drw [x-edit (+ y-freq2 5)] :f2 edit-action)
        b-s1 (matrix/control-source-button drw ied param-s1 [x-source (- y-freq1 3)])
        b-s2 (matrix/control-source-button drw ied param-s2 [x-source (- y-freq2 3)])
        b-s3 (matrix/control-source-button drw ied param-s3 [x-source (- y-wave1 20)])
        b-s4 (matrix/control-source-button drw ied param-s4 [x-source (- y-wave2 20)])
        wave-action (fn [s _]
                      (let [param (.get-property s :id)
                            val (slider-value s)]
                        (.set-param! ied param val)))
        s-wave1 (sfactory/vslider drw ied param-d3 [x-wave1 y-wave1] -1.0 1.0 wave-action
                                  :orientation :horizontal
                                  :length lfo-slider-length
                                  :handle-style [:chevron-s :bar]
                                  :passive-track (lnf/passive-track)
                                  :active-track (lnf/passive-track))
        s-wave2 (sfactory/vslider drw ied param-d4 [x-wave1 y-wave2] -1.0 1.0 wave-action
                                  :orientation :horizontal
                                  :length lfo-slider-length
                                  :handle-style [:chevron-s :bar]
                                  :passive-track (lnf/passive-track)
                                  :active-track (lnf/passive-track))
        sync-fn (fn [dmap]
                  (slider-value! s-wave1 (param-d3 dmap))
                  (slider-value! s-wave2 (param-d4 dmap))
                  (.display! dbar1 (format "%+7.3f" (float (param-d1 dmap))) false)
                  (.display! dbar2 (format "%+7.3f" (float (param-d2 dmap))) false)
                  (msb-state! b-s1 (int (param-s1 dmap)))
                  (msb-state! b-s2 (int (param-s2 dmap)))
                  (msb-state! b-s3 (int (param-s3 dmap)))
                  (msb-state! b-s4 (int (param-s4 dmap))))]
    sync-fn))


; ---------------------------------------------------------------------- 
;                                 LF Noise

(defn- lfnoise-pos [item p0]
  (let [x0 (+ (first p0) lfnoise-xoffset)
        x1 (+ x0 50)
        x-map {:p0 x0 :title (+ x0 10) :border (+ x1 50)
               :slider x1}
        y0 (second p0)
        y1 (- y0 100)
        y-source (+ y1 40)
        y-border (- y0 310)
        y-title (+ y-border 10)
        y-map {:p0 (second p0) :title y-title :border y-border
               :slider y1}
        x (item x-map)
        y (item y-map)]
    [x y]))

(defn- draw-lfnoise-panel [bg p0]
  (let [[x0 y0](lfnoise-pos :p0 p0)
        [xb yb](lfnoise-pos :border p0)
        [x1 y1](lfnoise-pos :slider p0)]
    (sfactory/minor-border bg [x0 y0][xb yb])
    (sfactory/title bg [(+ x0 5)(+ yb 30)] "LF Noise")
    (sfactory/label bg [x1 y1] "Freq Mod" :offset [-27 20])
    (sfactory/major-tick-marks bg x1 y1 (- y1 sfactory/slider-length)
                               :v0 1 :v1 max-snh-freq :step 1 :frmt "%2d"
                               :x-offset -30)))

(defn- lfnoise-panel [drw ied p0]
  (let [param-d :lfnoise-freq-depth
        param-s :lfnoise-freq-depth
        action (fn [s _]
                 (let [val (slider-value s)]
                   (.set-param! ied param-d val)))
        [x1 y1](lfnoise-pos :slider p0)
        s-depth (sfactory/vslider drw ied param-d [x1 y1] 1.0 max-snh-freq action)
        b-src (matrix/control-source-button drw ied param-s [(- x1 25)(+ y1 30)])
        sync-fn (fn [dmap]
                  (slider-value! s-depth (param-d dmap))
                  (msb-state! b-src (int (param-s dmap))))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                              Sample And Hold

(defn- snh-pos [item p0]
  (let [x0 (+ (first p0) snh-xoffset)
        x-rate (+ x0 60)
        x-bias (+ x-rate 70)
        x-scale (+ x-bias 70)
        x-border (+ x-scale 50)
        x-source (- x-scale 25)
        x-title (+ x0 10)
        x-map {:p0 x0 :title x-title :border x-border
               :rate x-rate :bias x-bias :scale x-scale
               :source x-source}

        y0 (second p0)
        y1 (- y0 100)
        y-source (+ y1 40)
        y-slider y1
        y-border (- y0 310)
        y-title (+ y-border 30)
        y-map {:p0 y0 :title y-title :border y-border
               :rate y-slider :bias y-slider :scale y-slider
               :source y-source}
        x (item x-map)
        y (item y-map)]
    [x y]))

(defn- draw-snh-panel [bg p0]
  (let [[x0 y0](snh-pos :p0 p0)
        [x-rate y-slider](snh-pos :rate p0)
        x-bias (first (snh-pos :bias p0))
        x-scale (first (snh-pos :scale p0))
        y1 y-slider
        y2 (- y1 sfactory/slider-length)]
    (sfactory/label bg [x-rate y-slider] "Rate" :offset [-14 20])
    (sfactory/label bg [x-bias y-slider] "Bias" :offset [-14 20])
    (sfactory/label bg [x-scale y-slider] "Scale" :offset [-16 20])
    (sfactory/label bg [x-rate y-slider] "Input Source" :offset [20 65])

    (sfactory/major-tick-marks bg x-rate y1 y2 
                               :v0 1 :v1 max-snh-freq :step 1 :frmt "%2d"
                               :x-offset -30)
    (sfactory/major-tick-marks bg x-bias y1 y2
                               :v0 (float (- max-snh-bias)) :v1 (float max-snh-bias)
                               :step 2.0 :frmt "%+4.1f")
    (sfactory/major-tick-marks bg x-scale y1 y2
                               :v0 1.0 :v1 max-snh-scale
                               :step 1.0 :frmt "%2.1f")
    (sfactory/title bg (snh-pos :title p0) "Sample & Hold")
    (sfactory/minor-border bg (snh-pos :p0 p0)(snh-pos :border p0))))

(defn- snh-panel [drw ied p0]
  (let [param-src :sh-source
        param-rate :sh-rate
        param-bias :sh-bias
        param-scale :sh-scale
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-rate (sfactory/vslider drw ied param-rate (snh-pos :rate p0) 0.0001 max-snh-freq action)
        s-bias (sfactory/vslider drw ied param-bias (snh-pos :bias p0) (- max-snh-bias) max-snh-bias action)
        s-scale (sfactory/vslider drw ied param-bias (snh-pos :scale p0) 1 max-snh-scale action)
        b-src (matrix/control-source-button drw ied param-src (snh-pos :source p0))
        sync-fn (fn [dmap]
                  (slider-value! s-rate (param-rate dmap))
                  (slider-value! s-bias (param-bias dmap))
                  (slider-value! s-scale (param-scale dmap))
                  (msb-state! b-src (int (param-src dmap))))]
    sync-fn))
