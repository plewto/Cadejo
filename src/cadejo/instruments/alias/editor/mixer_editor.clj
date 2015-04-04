(ns cadejo.instruments.alias.editor.mixer-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss]))

(def ^:private width 1840)
(def ^:private height 500)
(def ^:private source-xoffset 50)
(def ^:private filter-xoffset 460)
(def ^:private efx-xoffset 700)
(def ^:private out-xoffset 1000)
(def ^:private min-db constants/min-amp-db)
(def ^:private max-db constants/max-amp-db)

(declare draw-background)
(declare draw-source-panel)
(declare draw-filter-panel)
(declare draw-efx-panel)
(declare draw-out-panel)
(declare source-panel)
(declare filter-panel)
(declare efx-panel)
(declare out-panel)

(defn mixer-editor [ied]
  (let [p0 [0 height]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        sources (source-panel drw ied p0)
        filters (filter-panel drw ied p0)
        efx (efx-panel drw ied p0)
        out (out-panel drw ied p0)
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
                 (doseq [se [sources filters efx out]]
                   (se dmap))
                 (.render drw))))]
    ed))

(defn- draw-background [ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)]
    (draw-source-panel bg p0)
    (draw-filter-panel bg p0)
    (draw-efx-panel bg p0)
    (draw-out-panel bg p0)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))

(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- vertical-position [item p0]
  (let [y0 (- (second p0) 32)
        y-slider1 (- y0 40)             ; pan sliders
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-slider3 (- y-slider2 50)      ; mix sliders
        y-slider4 (- y-slider2 sfactory/slider-length)
        y-sub-title (- y-slider4 40)
        y-main-title (- y-sub-title 40)
        y-border (- y-sub-title 30)
        y-map {:p0 y0 :border y-border 
               :y1 y-slider1 :y2 y-slider2
               :y3 y-slider3 :y4 y-slider4
               :sub-title y-sub-title :title y-main-title}]
    (get y-map item)))

; ---------------------------------------------------------------------- 
;                               Source panel
               
(defn- source-pos [item p0]
  (let [x0 (+ (first p0) source-xoffset)
        x-osc1 (+ x0 60)
        x-osc2 (+ x-osc1 60)
        x-osc3 (+ x-osc2 60)
        x-noise (+ x-osc3 60)
        x-ringmod (+ x-noise 60)
        x-title (+ x0 10)
        x-border (+ x-ringmod 40)
        x-map {:p0 x0 :title x-title :border x-border
               :osc1 x-osc1 :osc2 x-osc2 :osc3 x-osc3 
               :noise x-noise :ringmod x-ringmod
               :pan1 x-osc1 :pan2 x-osc2 :pan3 x-osc3
               :pannoise x-noise :panringmod x-ringmod}
        y1 (vertical-position :y1 p0)
        y2 (vertical-position :y2 p0)
        y3 (vertical-position :y3 p0)
        y4 (vertical-position :y4 p0)
        y0 (vertical-position :p0 p0)
        y-title (vertical-position :title p0)
        y-border (vertical-position :border p0)
        y-map {:p0 y0 :title y-title :border y-border
               :osc1 y3 :osc2 y3 :osc3 y3 :noise y3 :ringmod y3
               :pan1 y1 :pan2 y1 :pan3 y1 :pannoise y1 :panringmod y1}
        x (item x-map) 
        y (item y-map)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate mixer source-pos item = %s" item)))
    [x y]))

(defn- draw-source-panel [bg p0]
  (let [[x1 y1](source-pos :osc1 p0)
        [x2 y3](source-pos :pan2 p0)
        x3 (first (source-pos :osc3 p0))
        xnse (first (source-pos :noise p0))
        xrmod (first (source-pos :ringmod p0))
        y2 (- y1 sfactory/slider-length)
        y4 (- y3 sfactory/slider-length)
        x-label (- x1 85)]
    (sfactory/label bg [x1 y1] "Osc 1" :offset [-15 30])
    (sfactory/label bg [x2 y1] "Osc 2" :offset [-15 30])
    (sfactory/label bg [x3 y1] "Osc 3" :offset [-15 30])
    (sfactory/label bg [xnse y1] "Noise" :offset [-13 30])
    (sfactory/label bg [xrmod y1] "RMod" :offset [-13 30])
    (doseq [x [x1 x2 x3 xnse xrmod]]
      (sfactory/db-ticks bg x y1)
      (sfactory/major-tick-marks bg x y3 y4))
    (sfactory/label bg [x-label (math/mean y1 y2)] "Amp" :size 8 :style :serif-bold :offset [0 8])
    (sfactory/label bg [x-label (math/mean y3 y4)] "Pan" :size 8 :style :serif-bold :offset [0 8])
    (sfactory/label bg [xrmod y3] "Filter 1" :offset [15 5] :size 5 )
    (sfactory/label bg [xrmod y4] "Filter 2" :offset [15 5] :size 5 )
    (sfactory/title bg (source-pos :title p0) "Mixer")))

(defn- source-panel [drw ied p0]
  (let [param-osc1-mix :osc1-amp
        param-osc2-mix :osc2-amp
        param-osc3-mix :osc3-amp
        param-noise-mix :noise-amp
        param-ringmod-mix :ringmod-amp
        param-osc1-pan :osc1-pan
        param-osc2-pan :osc2-pan
        param-osc3-pan :osc3-pan
        param-noise-pan :noise-pan
        param-ringmod-pan :ringmod-pan
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-mix-1 (sfactory/vslider drw ied param-osc1-mix (source-pos :osc1 p0) min-db max-db action :value-hook int)
        s-mix-2 (sfactory/vslider drw ied param-osc2-mix (source-pos :osc2 p0) min-db max-db action :value-hook int)
        s-mix-3 (sfactory/vslider drw ied param-osc3-mix (source-pos :osc3 p0) min-db max-db action :value-hook int)
        s-mix-noise (sfactory/vslider drw ied param-noise-mix (source-pos :noise p0) min-db max-db action :value-hook int)
        s-mix-ringmod (sfactory/vslider drw ied param-ringmod-mix (source-pos :ringmod p0) min-db max-db action :value-hook int)
        s-pan-1 (sfactory/vslider drw ied param-osc1-pan (source-pos :pan1 p0) -1.0 1.0 action)
        s-pan-2 (sfactory/vslider drw ied param-osc2-pan (source-pos :pan2 p0) -1.0 1.0 action)
        s-pan-3 (sfactory/vslider drw ied param-osc3-pan (source-pos :pan3 p0) -1.0 1.0 action)
        s-pan-noise (sfactory/vslider drw ied param-noise-pan (source-pos :pannoise p0) -1.0 1.0 action)
        s-pan-ringmod (sfactory/vslider drw ied param-ringmod-pan (source-pos :panringmod p0) -1.0 1.0 action)
        sync-fn (fn [dmap]
                  (slider-value! s-mix-1 (param-osc1-mix dmap))
                  (slider-value! s-mix-2 (param-osc2-mix dmap))
                  (slider-value! s-mix-3 (param-osc3-mix dmap))
                  (slider-value! s-mix-noise (param-noise-mix dmap))
                  (slider-value! s-mix-ringmod (param-ringmod-mix dmap))
                  (slider-value! s-pan-1 (param-osc1-pan dmap))
                  (slider-value! s-pan-2 (param-osc1-pan dmap))
                  (slider-value! s-pan-3 (param-osc1-pan dmap))
                  (slider-value! s-pan-noise  (param-noise-pan dmap))
                  (slider-value! s-pan-ringmod  (param-ringmod-pan dmap)))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                               Filter Panel

(defn- filter-pos [item p0]
  (let [x0 (+ (first p0) filter-xoffset)
        x1 (+ x0 60)
        x2 (+ x1 80)
        x-title (+ x0 10)
        x-border (+ x2 40)
        x-map {:p0 x0 :title x-title :border x-border
               :filter1 x1 :filter2 x2
               :pan1 x1 :pan2 x2}
        y1 (vertical-position :y1 p0)
        y2 (vertical-position :y2 p0)
        y3 (vertical-position :y3 p0)
        y4 (vertical-position :y4 p0)
        y0 (vertical-position :p0 p0)
        y-title (vertical-position :sub-title p0)
        y-border (vertical-position :border p0)
        y-map {:p0 y0 :title y-title :border y-border
               :filter1 y3 :filter2 y3
               :pan1 y1 :pan2 y1}
        x (item x-map)
        y (item y-map)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate mixer filter-pos item = %s" item)))
    [x y]))
 

(defn- draw-filter-panel [bg p0]
  (let [[x1 y3](filter-pos :filter1 p0)
        [x2 y1](filter-pos :pan2 p0)
        y2 (- y1 sfactory/slider-length)
        y4 (- y3 sfactory/slider-length)]
    (doseq [x [x1 x2]]
      (sfactory/db-ticks bg x y3)
      (sfactory/major-tick-marks bg x y1 y2))
    (sfactory/label bg [x1 y3] "Filter 1" :offset [-26 30])
    (sfactory/label bg [x2 y3] "Filter 2" :offset [-26 30])
    ;; Draw connection lines between source-pan -> filter faders
    (let [root (.root bg)
          c (uc/darker (lnf/minor-border))
          vline (fn [x y1 y2](line/line root [x y1][x y2] :color c :style :dotted))
          hline (fn [x1 x2 y](line/line root [x1 y][x2 y] :color c :style :dotted))
          u1 x1
          u2 x2
          u3 (- u1 90)
          u4 (+ u3 20)
          u5 (- u3 10)
          v1 y1
          v2 y2
          v3 (- y4 30)
          v4 (- v3 20)
          v5 (- y4 10)]
      (hline u5 u4 v1)
      (hline u1 u4 v3)
      (vline u4 v1 v3)
      (vline u1 v3 v5)
      (hline u5 u3 v2)
      (hline u3 u2 v4)
      (vline u3 v2 v4)
      (vline u2 v4 v5))))

(defn- filter-panel [drw ied p0]
  (let [param-amp1 :filter1-postgain
        param-pan1 :filter1-pan
        param-amp2 :filter2-postgain
        param-pan2 :filter2-pan
        db-action (fn [s _]
                    (let [param (.get-property s :id)
                          db (slider-value s)
                          amp (math/db->amp db)]
                      (.set-param! ied param amp)
                      (.status! ied (format "filter mix %s db" db))))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-mix-1 (sfactory/vslider drw ied param-amp1 (filter-pos :filter1 p0) min-db max-db db-action :value-hook int)
        s-mix-2 (sfactory/vslider drw ied param-amp2 (filter-pos :filter2 p0) min-db max-db db-action :value-hook int)
        s-pan-1 (sfactory/vslider drw ied param-pan1 (filter-pos :pan1 p0) -1.0 1.0 action)
        s-pan-2 (sfactory/vslider drw ied param-pan2 (filter-pos :pan2 p0) -1.0 1.0 action)
        sync-fn (fn [dmap]
                  (let [db1 (math/amp->db (param-amp1 dmap))
                        db2 (math/amp->db (param-amp1 dmap))]
                    (slider-value! s-mix-1 db1)
                    (slider-value! s-mix-2 db2)
                    (slider-value! s-pan-1 (param-pan1 dmap))
                    (slider-value! s-pan-2 (param-pan2 dmap))))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                 Efx Panel

(defn- efx-pos [item p0]
  (let [x0 (+ (first p0) efx-xoffset)
        x-shifter (+ x0 0)
        x-flanger (+ x-shifter 60)
        x-delay1 (+ x-flanger 60)
        x-delay2 (+ x-delay1 60)
        x-dry (+ x-delay2 60)
        x-map {:p0 x0
               :shifter x-shifter :flanger x-flanger
               :delay1 x-delay1 :delay2 x-delay2 :dry x-dry
               :pan1 x-delay1 :pan2 x-delay2}

        y1 (vertical-position :y1 p0)
        y2 (vertical-position :y2 p0)
        y3 (vertical-position :y3 p0)
        y4 (vertical-position :y4 p0)
        y0 (vertical-position :p0 p0)
        y-map {:p0 y0
               :shifter y3 :flanger y3 :delay1 y3 :delay2 y3 :dry y3
               :pan1 y1 :pan2 y1}
        x (item x-map)
        y (item y-map)]
        
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate mixer efx-pos item = %s" item)))
    [x y]))

(defn- draw-efx-panel [bg p0]
  (let [[x-shifter y3](efx-pos :shifter p0)
        x-flanger (first (efx-pos :flanger p0))
        [x-delay1 y1](efx-pos :pan1 p0)
        x-delay2 (first (efx-pos :delay2 p0))
        x-dry (first (efx-pos :dry p0))
        y2 (- y1 sfactory/slider-length)
        y4 (- y3 sfactory/slider-length)]
    (sfactory/label bg [x-shifter y3] "PShift" :offset [-15 30])
    (sfactory/label bg [x-flanger y3] "Flanger" :offset [-15 30])
    (sfactory/label bg [x-delay1 y3] "Delay1" :offset [-15 30])
    (sfactory/label bg [x-delay2 y3] "Delay2" :offset [-15 30])
    (sfactory/label bg [x-dry y3] "Dry" :offset [-10 30])
    (doseq [x [x-shifter x-flanger x-delay1 x-delay2 x-dry]]
      (sfactory/db-ticks bg x y3))
    (doseq [x [x-delay1 x-delay2]]
      (sfactory/major-tick-marks bg x y1 y2))))

(defn- efx-panel [drw ied p0]
  (let [param-shifter :pitchshift-mix
        param-flanger :flanger-mix
        param-delay1 :echo1-mix
        param-delay2 :echo2-mix
        param-dry :dry-mix
        param-pan1 :echo1-pan
        param-pan2 :echo2-pan
        action (fn [s _]
                 (let [param (.get-property s :id)
                       value (slider-value s)]
                   (.set-param! ied param value)))
        s-shifter (sfactory/vslider drw ied param-shifter (efx-pos :shifter p0) min-db max-db action :value-hook int)
        s-flanger (sfactory/vslider drw ied param-flanger (efx-pos :flanger p0) min-db max-db action :value-hook int)
        s-delay1 (sfactory/vslider drw ied param-delay1 (efx-pos :delay1 p0) min-db max-db action :value-hook int)
        s-delay2 (sfactory/vslider drw ied param-delay2 (efx-pos :delay2 p0) min-db max-db action :value-hook int)
        s-dry (sfactory/vslider drw ied param-dry (efx-pos :dry p0) min-db max-db action :value-hook int)
        s-pan1 (sfactory/vslider drw ied param-pan1 (efx-pos :pan1 p0) -1.0 1.0 action)
        s-pan2 (sfactory/vslider drw ied param-pan2 (efx-pos :pan2 p0) -1.0 1.0 action)
        sync-fn (fn [dmap]
                  (slider-value! s-shifter (param-shifter dmap))
                  (slider-value! s-flanger (param-flanger dmap))
                  (slider-value! s-delay1 (param-delay1 dmap))
                  (slider-value! s-delay2 (param-delay2 dmap))
                  (slider-value! s-dry (param-dry dmap))
                  (slider-value! s-pan1 (param-pan1 dmap))
                  (slider-value! s-pan2 (param-pan2 dmap)))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                 Out Panel

(defn- out-pos [item p0]
  (let [x0 (+ (first p0) out-xoffset)
        x1 (+ x0 60)
        x-border (+ x1 60)
        x-title (+ x0 32)
        y0 (vertical-position :p0 p0)
        y1 (vertical-position :y1 p0)
        y3 (vertical-position :y3 p0)
        y-border (- (vertical-position :border p0) 40)
        y-title (vertical-position :title p0)
        x-map {:p0 x0 :border x-border :title x-title
               :amp x1 :cc7 x1}
        y-map {:p0 y0 :border y-border :title y-title
               :amp y3 :cc7 y1}
        x (item x-map)
        y (item y-map)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate mixer out-pos item = %s" item)))
    [x y]))


(defn- draw-out-panel [bg p0]
  (let [[x0 y0](out-pos :p0 p0)
        [xborder yborder](out-pos :border p0)
        [xtitle ytitle](out-pos :title p0)
        [x1 y3] (out-pos :amp p0)
        [x1 y1] (out-pos :cc7 p0)]
    (sfactory/label bg [x1 y3] "Amp" :offset [-10 20])
    (sfactory/label bg [x1 y1] "CC7" :offset [-10 20])
    (sfactory/title bg [xtitle ytitle] "OUT" )
    (sfactory/db-ticks bg x1 y3)
    (sfactory/minor-ticks bg x1 y1 (- y1 sfactory/slider-length) 10)
    (sfactory/major-border bg [x0 y0][xborder yborder])))

(defn- out-panel [drw ied p0]
  (let [param-out :amp
        param-cc7 :cc7->volume
        db-action (fn [s _]
                    (let [db (slider-value s)
                          amp (math/db->amp db)]
                      (.set-param! ied param-out amp)
                      (.status! ied (format "[%s] -> %s db" param-out db))))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-amp (sfactory/vslider drw ied param-out (out-pos :amp p0) min-db max-db db-action :value-hook int)
        s-cc7 (sfactory/vslider drw ied param-cc7 (out-pos :cc7 p0) 0.0 1.0 action)
        sync-fn (fn [dmap]
                  (slider-value! s-amp (math/amp->db (param-out dmap)))
                  (slider-value! s-cc7 (param-cc7 dmap)))]
    sync-fn))
