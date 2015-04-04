(ns cadejo.instruments.alias.editor.osc-editor
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
(def ^:private bottom-margin 30)
(def ^:private x-matrix-overview 990)
(def ^:private y-matrix-overview 300)
(def ^:private min-db constants/min-amp-db)
(def ^:private max-db constants/max-amp-db)
(def ^:private max-lag-time constants/max-lag-time)

(declare draw-background)
(declare draw-fm-panel)
(declare draw-wave-panel)
(declare draw-amp-panel)
(declare draw-freq-panel)
(declare draw-port-panel)
(declare fm-panel)
(declare wave-panel)
(declare amp-panel)
(declare freq-panel)
(declare port-panel)


(defn osc-editor [n ied]
  (let [p0 [0 (- height bottom-margin)]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background n d p0)
              d)
        fm-pan (fm-panel n drw ied p0)
        wave-pan (wave-panel n drw ied p0)
        amp-pan (amp-panel n drw ied p0)
        freq-pan (freq-panel n drw ied p0)
        port-pan (port-panel n drw ied p0)
        matrix-overview (matrix/matrix-overview drw [(+ (first p0) x-matrix-overview)(- (second p0) y-matrix-overview -20)])
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
                 (fm-pan dmap)
                 (wave-pan dmap)
                 (amp-pan dmap)
                 (freq-pan dmap)
                 (port-pan dmap)
                 (matrix-overview dmap)
                 (.render drw))))]
    ed))

(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- msb-state! [b n]
  (msb/set-multistate-button-state! b n false))


(defn- draw-background [n ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)]
    (draw-fm-panel n bg p0)
    (draw-wave-panel n bg p0)
    (draw-amp-panel n bg p0)
    (draw-freq-panel n bg p0)
    (draw-port-panel n bg p0)
    (sfactory/label bg [(+ (first p0) x-matrix-overview)(- (second p0) y-matrix-overview)] "Bus Assignments:")
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))

(defn- vertical-position [item p0]
  (let [y0 (second p0)
        y-title (- y0 290)
        y-slider1 (- y0 90)
        y-slider2 (- y-slider1 sfactory/slider-length)
        source-button-position (+ y-slider1 35)
        map {:slider1 y-slider1 
             :slider2 y-slider2
             :button source-button-position}]
    (get map item y-slider1)))
        
; ---------------------------------------------------------------------- 
;                                 FM Panel

(defn- fm-pos [item p0]
  (let [x-offset 32
        y-offset 32
        x0 (+ (first p0) x-offset)
        y0 (- (second p0) y-offset)
        ref [x0 y0]
        x-title (+ x0 10)
        x-depth1 (+ x0 60)
        x-lag1 (+ x-depth1 40)
        x-source1 (+ x-depth1 0)
        x-depth2 (+ x-lag1 80)
        x-lag2 (+ x-depth2 40)
        x-source2 (+ x-depth2 0)
        x-border (+ x-lag2 30)
        x-map {:p0 x0
               :border x-border
               :title x-title, 
               :d1 x-depth1, :l1 x-lag1 :s1 x-source1,
               :d2 x-depth2, :l2 x-lag2 :s2 x-source2}
        y-1 (vertical-position :slider1 ref)
        y-2 (vertical-position :slider2 ref)
        y-b (vertical-position :button ref)
        y-title (- y0 260)
        y-border (- y-title 25)
        y-map {:p0 (second ref)
               :border y-border
               :title y-title
               :d1 y-1, :l1 y-1, :s1 y-b,
               :d2 y-1, :l2 y-1, :s2 y-b}
        x (get x-map item nil)
        y (get y-map item nil)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate fm-pos item = %s" item)))
    [x y]))

(defn- draw-fm-panel [n bg p0]
  (let [pos-d1 (fm-pos :d1 p0)
        pos-d2 (fm-pos :d2 p0)
        pos-l1 (fm-pos :l1 p0)
        pos-l2 (fm-pos :l2 p0)
        pos-s1 (fm-pos :s1 p0)
        pos-s2 (fm-pos :s2 p0)
        y1 (second pos-d1)
        y2 (- y1 sfactory/slider-length)
        x-bus (- (math/mean (first pos-s1)(first pos-s2)) 10)
        y-bus (+ (second pos-s1) 24)]
  (sfactory/label bg pos-d1 "Dpth" :offset [-12 24])
  (sfactory/label bg pos-d2 "Dpth" :offset [-12 24])
  (sfactory/label bg pos-l1 "Lag" :offset [-12 24])
  (sfactory/label bg pos-l2 "Lag" :offset [-12 24])
  (doseq [x (map first [pos-d1 pos-d2])]
    (sfactory/minor-ticks bg x y1 y2 16)
    (sfactory/major-tick-marks bg x y1 y2))
  (doseq [x (map first [pos-l1 pos-l2])]
    (sfactory/minor-ticks bg x y1 y2 10))
  (sfactory/label bg [x-bus y-bus] "-- Bus --")
  (sfactory/sub-title bg (fm-pos :title p0) "FM")
  (sfactory/minor-border bg (fm-pos :p0 p0) (fm-pos :border p0))))

(defn- fm-panel [n drw ied p0]
  (let [param-d1 (keyword (format "osc%d-fm1-depth" n))
        param-l1 (keyword (format "osc%d-fm1-lag" n))
        param-s1 (keyword (format "osc%d-fm1-source" n))
        param-d2 (keyword (format "osc%d-fm2-depth" n))
        param-l2 (keyword (format "osc%d-fm2-lag" n))
        param-s2 (keyword (format "osc%d-fm2-source" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-d1 (sfactory/vslider drw ied param-d1 (fm-pos :d1 p0) -1.0 1.0 action)
        s-l1 (sfactory/vslider drw ied param-l1 (fm-pos :l1 p0) 0 constants/max-lag-time action)
        s-d2 (sfactory/vslider drw ied param-d2 (fm-pos :d2 p0) -1.0 1.0 action)
        s-l2 (sfactory/vslider drw ied param-l2 (fm-pos :l2 p0) 0 constants/max-lag-time action)
        b-1  (matrix/source-button drw ied param-s1 (fm-pos :s1 p0))
        b-2  (matrix/source-button drw ied param-s2 (fm-pos :s2 p0))
        sync-fn (fn [dmap] 
                  (slider-value! s-d1 (param-d1 dmap))
                  (slider-value! s-d2 (param-d2 dmap))
                  (slider-value! s-l1 (param-l1 dmap))
                  (slider-value! s-l2 (param-l2 dmap))
                  (msb-state! b-1 (param-s1 dmap))
                  (msb-state! b-2 (param-s2 dmap)))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                Wave Panel

(defn- wave-pos [item p0]
  (let [x-offset 288
        y-offset 32
        x0 (+ (first p0) x-offset)
        y0 (- (second p0) y-offset)
        ref [x0 y0]
        x-title (+ x0 10)
        x-wave (+ x0 50)
        x-depth1 (+ x-wave 65)
        x-source1 (- x-depth1 20)
        x-depth2 (+ x-depth1 65)
        x-source2 (- x-depth2 20)
        x-border (+ x-depth2 35)
        x-map {:p0 x0 :title x-title :border x-border
               :wave x-wave
               :d1 x-depth1 :s1 x-source1
               :d2 x-depth2 :s2 x-source2}
        y1 (vertical-position :slider1 ref)
        y2 (vertical-position :slider2 ref)
        yb (vertical-position :button ref)
        y-title (- y0 260)
        y-border (- y-title 25)
        y-map {:p0 y0 :title y-title :border y-border
               :wave y1 :d1 y1 :d2 y1 :s1 yb :s2 yb}
        x (get x-map item nil)
        y (get y-map item nil)]
     (if (or (not x)(not y))
      (println (format "ERROR nil coordinate  wave-pos item = %s" item)))
    [x y]))

(defn- draw-wave-panel [n bg p0]
  (let [pos-w (wave-pos :wave p0)
        pos-d1 (wave-pos :d1 p0)
        pos-d2 (wave-pos :d2 p0)
        pos-s1 (wave-pos :s1 p0)
        pos-s2 (wave-pos :s2 p0)
        y1 (second pos-d1)
        y2 (- y1 sfactory/slider-length)
        x-bus (- (first pos-w) 0)
        y-bus (+ (second pos-s1) 24)]
    (sfactory/label bg pos-w "Wave" :offset [-12 24])
    (sfactory/label bg pos-d1 "Dpth" :offset [-12 24])
    (sfactory/label bg pos-d2 "Dpth" :offset [-12 24])
    (sfactory/label bg [x-bus y-bus] "Bus --" :offset [0 0])
    (cond (= n 1)
          (do 
            (sfactory/minor-ticks bg (first pos-w) y1 y2 12)
            (sfactory/major-tick-marks bg (first pos-w) y1 y2 :v0 1.0 :v1 4.0 :frmt "%4.2f" :step 1.00))
          (= n 2)
          (do
            (sfactory/minor-ticks bg (first pos-w) y1 y2 16)
            (sfactory/major-tick-marks bg (first pos-w) y1 y2 :v0 0.0 :v1 1.0 :frmt "%4.2f" :step 0.25))
          :default
          (do 
            (sfactory/minor-ticks bg (first pos-w) y1 y2 12)
            (sfactory/major-tick-marks bg (first pos-w) y1 y2 :v0 0.0 :v1 4.0 :frmt "%4.2f" :step 1.0)))
    (doseq [x (map first [pos-d1 pos-d2])]
      (sfactory/minor-ticks bg x y1 y2 16)
      (sfactory/major-tick-marks bg x y1 y2))
    (sfactory/sub-title bg (wave-pos :title p0) "Wave")
    (sfactory/minor-border bg (wave-pos :p0 p0)(wave-pos :border p0))))

(defn- wave-panel [n drw ied p0]
  (let [param-wave (keyword (format "osc%d-wave" n))
        param-d1 (keyword (format "osc%d-wave1-depth" n))
        param-d2 (keyword (format "osc%d-wave2-depth" n))
        param-s1 (keyword (format "osc%d-wave1-source" n))
        param-s2 (keyword (format "osc%d-wave2-source" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-wave (sfactory/vslider drw ied param-wave (wave-pos :wave p0) 
                                 (if (= n 1) 1.0 0.0)
                                 (if (= n 2) 1.0 4.0)
                                 action)
        s-d1 (sfactory/vslider drw ied param-d1 (wave-pos :d1 p0) -1.0 1.0 action)
        s-d2 (sfactory/vslider drw ied param-d2 (wave-pos :d2 p0) -1.0 1.0 action)
        b-1  (matrix/source-button drw ied param-s1 (wave-pos :s1 p0))
        b-2  (matrix/source-button drw ied param-s2 (wave-pos :s2 p0))
        sync-fn (fn [dmap] 
                  (slider-value! s-d1 (param-d1 dmap))
                  (slider-value! s-d2 (param-d2 dmap))
                  (msb-state! b-1 (param-s1 dmap))
                  (msb-state! b-2 (param-s2 dmap)))]
    sync-fn))


; ---------------------------------------------------------------------- 
;                                 Amp Panel

(defn- amp-pos [item p0]
  (let [x-offset 510
        y-offset 32
        x0 (+ (first p0) x-offset)
        y0 (- (second p0) y-offset)
        ref [x0 y0]
        x-title (+ x0 10)
        x-mix (+ x0 60)
        x-depth1 (+ x-mix 80)
        x-lag1 (+ x-depth1 40)
        x-source1 (+ x-depth1 0)
        x-depth2 (+ x-lag1 80)
        x-lag2 (+ x-depth2 40)
        x-source2 (+ x-depth2 0)
        x-pan (+ x-lag2 80)
        x-border (+ x-pan 75)
        x-map {:p0 x0
               :title x-title
               :border x-border
               :mix x-mix
               :pan x-pan
               :d1 x-depth1, :l1 x-lag1, :s1 x-source1,
               :d2 x-depth2, :l2 x-lag2, :s2 x-source2}
        y1 (vertical-position :slider1 ref)
        y2 (vertical-position :slider2 ref)
        yb (vertical-position :button ref)
        y-title (- y0 260)
        y-border (- y-title 25)
        y-map {:p0 (second ref) 
               :border y-border
               :title y-title
               :mix y1, :pan y1,
               :d1 y1, :l1 y1, :s1 yb,
               :d2 y1, :l2 y1, :s2 yb}
        [x y][(get x-map item)(get y-map item)]]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate amp-pos item = %s" item)))
    [x y]))

(defn- draw-amp-panel [n bg p0]
  (let [pos-mix (amp-pos :mix p0)
        pos-d1 (amp-pos :d1 p0)
        pos-d2 (amp-pos :d2 p0)
        pos-l1 (amp-pos :l1 p0)
        pos-l2 (amp-pos :l2 p0)
        pos-s1 (amp-pos :s1 p0)
        pos-s2 (amp-pos :s2 p0)
        pos-pan (amp-pos :pan p0)
        y1 (second pos-d1)
        y2 (- y1 sfactory/slider-length)
        x-bus (- (math/mean (first pos-s1)(first pos-s2)) 10)
        y-bus (+ (second pos-s1) 24)]
  (sfactory/label bg pos-mix "Mix" :offset [-10 24])
  (sfactory/label bg pos-d1 "Dpth" :offset [-12 24])
  (sfactory/label bg pos-d2 "Dpth" :offset [-12 24])
  (sfactory/label bg pos-l1 "Lag" :offset [-10 24])
  (sfactory/label bg pos-l2 "Lag" :offset [-10 24])
  (sfactory/label bg pos-pan "Pan" :offset [-10 24])
  (sfactory/label bg [x-bus y-bus] "-- Bus --")
  (sfactory/db-ticks bg (first pos-mix)(second pos-mix))

  ;; Draw signed slider ticks
  (doseq [x (map first [pos-d1 pos-d2 pos-pan])]
    (sfactory/minor-ticks bg x y1 y2 16)
    (sfactory/major-tick-marks bg x y1 y2))
  ;; Draw lag slider ticks
  (doseq [x (map first [pos-l1 pos-l2])]
    (sfactory/minor-ticks bg x y1 y2 10))
  (sfactory/label bg [(first pos-pan) y2] "Filter 2" :size 5.0 :offset [15 5])
  (sfactory/label bg [(first pos-pan) y1] "Filter 1" :size 5.0 :offset [15 5])
  (sfactory/sub-title bg (amp-pos :title p0) "Amp")
  (sfactory/minor-border bg (amp-pos :p0 p0)(amp-pos :border p0))))

(defn- amp-panel [n drw ied p0]
  (let [param-mix (keyword (format "osc%d-amp" n))
        param-d1 (keyword (format "osc%d-amp1-depth" n))
        param-l1 (keyword (format "osc%d-amp1-lag" n))
        param-d2 (keyword (format "osc%d-amp2-depth" n))
        param-l2 (keyword (format "osc%d-amp2-lag" n))
        param-pan (keyword (format "osc%d-pan" n))
        param-s1 (keyword (format "osc%d-amp1-src" n))
        param-s2 (keyword (format "osc%d-amp2-src" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-mix (sfactory/vslider drw ied param-mix (amp-pos :mix p0) 
                                min-db max-db action
                                :value-hook int)
        s-d1 (sfactory/vslider drw ied param-d1 (amp-pos :d1 p0) -1.0 1.0 action)
        s-l1 (sfactory/vslider drw ied param-l1 (amp-pos :l1 p0) 0.0 max-lag-time action)
        s-d2 (sfactory/vslider drw ied param-d2 (amp-pos :d2 p0) -1.0 1.0 action)
        s-l2 (sfactory/vslider drw ied param-l2 (amp-pos :l2 p0) 0.0 max-lag-time action)
        s-pan (sfactory/vslider drw ied param-pan (amp-pos :pan p0) -1.0 1.0 action)
        b-1 (matrix/source-button drw ied param-s1 (amp-pos :s1 p0))
        b-2 (matrix/source-button drw ied param-s2 (amp-pos :s2 p0))
        sync-fn (fn [dmap] 
                  (slider-value! s-d1 (param-d1 dmap))
                  (slider-value! s-d2 (param-d2 dmap))
                  (slider-value! s-l1 (param-l1 dmap))
                  (slider-value! s-l2 (param-l2 dmap))
                  (slider-value! s-mix (param-mix dmap))
                  (slider-value! s-pan (param-pan dmap))
                  (msb-state! b-1 (param-s1 dmap))
                  (msb-state! b-2 (param-s2 dmap)))]
    sync-fn))


; ---------------------------------------------------------------------- 
;                                Freq Panel

(defn- freq-pos [item p0]
  (let [x-offset 32
        y-offset 322
        x0 (+ (first p0) x-offset)
        y0 (- (second p0) y-offset)
        ref [x0 y0]
        x-title (+ x0 10)
        x-logo (+ x0 30)
        x-freq (+ x0 240)
        x-freq-edit (+ x-freq 210)
        x-bias (+ x0 600)
        x-bias-edit (+ x-bias 210)
        x-border (+ x0 600)
        x-map {:p0 x0, :title x-title, :logo x-logo, 
               :border x-border,
               :freq x-freq, :bias x-bias,
               :freq-edit x-freq-edit, :bias-edit x-bias-edit}
        y-dbar (- y0 0)
        y-title (- y0 30)
        y-logo (- y0 127)
        y-freq (- y0 50)
        y-edit (- y-freq 0)
        y-map {:p0 y0 :title y-title, :logo y-logo
               :freq y-freq :bias y-freq
               :freq-edit y-edit, :bias-edit y-edit}
        x (get x-map item)
        y (get y-map item)]
    (if (or (not x)(not y))
        (println (format "ERROR nil coordinate  freq-pos item = %s" item)))
    [x y]))

(defn- draw-freq-panel [n bg p0]
  (sfactory/label bg (freq-pos :freq p0) "Freq" :offset [-40 20])
  (sfactory/label bg (freq-pos :bias p0) "Bias" :offset [-40 20])
  (image/read-image (.root bg) (freq-pos :logo p0) 
                    (format "resources/alias/osc%d_logo.png" n)))

(defn- freq-panel [n drw ied p0]
    (let [param-freq (keyword (format "osc%d-detune" n))
          param-bias (keyword (format "osc%d-bias" n))
          dbar-freq (sfactory/displaybar drw (freq-pos :freq p0) 7)
          dbar-bias (sfactory/displaybar drw (freq-pos :bias p0) 7)
          freq-edit-action (fn [& _]
                             (dbar/displaybar-dialog dbar-freq
                                                     (format "Osc %d Frequency" n)
                                                     :validator (fn [q]
                                                                  (let [f (math/str->float q)]
                                                                    (and f (>= f 0))))
                                                     :callback (fn [_]
                                                                 (let [s (.current-display dbar-freq)
                                                                       f (math/str->float s)]
                                                                   (.set-param! ied param-freq f)))))
          bias-edit-action (fn [& _]
                             (dbar/displaybar-dialog dbar-bias
                                                     (format "Osc %d Bias" n)
                                                     :validator (fn [q]
                                                                  (let [f (math/str->float q)]
                                                                    f))
                                                     :callback (fn [_]
                                                                 (let [s (.current-display dbar-bias)
                                                                       b (math/str->float s)]
                                                                   (.set-param! ied param-bias b)))))
                                                                   
          b-freq (sfactory/mini-edit-button drw (freq-pos :freq-edit p0) :freq-edit freq-edit-action)
          b-bias (sfactory/mini-edit-button drw (freq-pos :bias-edit p0) :bias-edit bias-edit-action)
        sync-fn (fn [dmap]
                  (let [b (format "%+1.4f" (param-bias dmap))
                        f (format "%2.4f" (param-freq dmap))]
                    (.display! dbar-bias b false)
                    (.display! dbar-freq f false)))]
    sync-fn))


; ---------------------------------------------------------------------- 
;                  Portamento Panel (show with osc 1 only)

(defn- port-pos [item p0]
  (let [[x0 y0] p0
        x1 (+ x0 120)
        x2 (+ x1 (* 2 sfactory/slider-length))
        yc y0
        x-map {:p0 x0 :p1 x1 :p2 x2}
        y-map {:p0 y0 :p1 yc :p2 yc}
        x (item x-map)
        y (item y-map)]
    [x y]))

(defn draw-port-panel [n bg p0]
  (if (= n 1)
    (let [[x0 y0](port-pos :p0 p0)
          [x1 y1](port-pos :p1 p0)
          [x2 y2](port-pos :p2 p0)]
      (sfactory/label bg [x0 y0] "Portamento" :offset [30 4])
      (doseq [x (range x1 (+ x2 30) 30)]
        (line/line (.root bg) [x (- y0 4)][x (+ y0 4)] :color (lnf/minor-tick))))))


(defn port-panel [n drw ied p0]
  (if (= n 1)
    (let [param-port :port-time
          action (fn [s _]
                   (let [val (slider-value s)]
                     (.set-param! ied param-port val)))
          s-port (slider/slider (.tool-root drw) (port-pos :p1 p0) (* 2 sfactory/slider-length)
                                0.0 max-lag-time :id param-port
                                :orientation :horizontal
                                :drag-action action
                                :track1-color (lnf/passive-track)
                                :track2-color (lnf/passive-track)
                                :rim-color [0 0 0 0]
                                :handle-color (lnf/handle)
                                :handle-style [:chevron-s :bar])
          sync-fn (fn [dmap]
                    (slider-value! s-port (param-port dmap)))]
      sync-fn)
    (fn [& _])))
