(ns cadejo.instruments.alias.editor.efx-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 2000)
(def ^:private height 550)
(def ^:private x-matrix-overview 1580)
(def ^:private y-matrix-overview 360)
(def ^:private pshifter-xoffset 32)
(def ^:private flanger-xoffset 400)
(def ^:private delay1-xoffset 800)
(def ^:private delay2-xoffset (+ delay1-xoffset 384))
(def ^:private min-db constants/min-amp-db)
(def ^:private max-db constants/max-amp-db)

(declare draw-background)
(declare pitch-shifter-panel)
(declare flanger-panel)
(declare delay-panel)
(declare out-panel)
(declare draw-pitch-shifter)
(declare draw-flanger)
(declare draw-delay)
(declare draw-out-panel)

(defn efx-editor [ied]
  (let [p0 [0 height]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        pshifter (pitch-shifter-panel drw ied p0)
        flanger (flanger-panel drw ied p0)
        delay1 (delay-panel 1 drw ied p0)
        delay2 (delay-panel 2 drw ied p0)
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
                 (doseq [se [pshifter flanger delay1 delay2 matrix-overview]]
                   (se dmap))
                 (.render drw))))]
    ed))

(defn- vertical-position [item p0]
  (let [y0 (- (second p0) 32)
        border (- y0 340)
        y-main-title (- y0 350) 
        y-logo (- y-main-title 150)
        y-sub-title (- y0 310)
        y-slider1 (- y0 90)
        y-slider2 (- y-slider1 sfactory/slider-length)
        source-button-position (+ y-slider1 35)
        map {:slider1 y-slider1 
             :slider2 y-slider2
             :logo y-logo
             :sub-title y-sub-title 
             :main-title y-main-title
             :source source-button-position
             :y0 y0
             :border border}]
  (get map item y-slider1)))

(defn- draw-background [ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)
        [x0 y0] p0
        x-logo (+ x0 60)
        y-logo (vertical-position :logo p0)]
    (draw-pitch-shifter bg p0)
    (draw-flanger bg p0)
    (draw-delay 1 bg p0)
    (draw-delay 2 bg p0)
    (sfactory/label bg [(+ (first p0) x-matrix-overview)(- (second p0) y-matrix-overview)] "Bus Assignments:")
    (image/read-image (.root bg) [x-logo y-logo]
                      (format "resources/alias/effects_logo.png"))
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))

(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- msb-state! [b n]
  (msb/set-multistate-button-state! b n false))

; ---------------------------------------------------------------------- 
;                               Pitch Shifter

(defn- pshifter-pos [item p0]
  (let [x0 (+ (first p0) pshifter-xoffset)
        x-title (+ x0 10)
        x-display (+ x0 60)
        x-edit (+ x-display 190)
        x-depth (+ x0 60)
        x-source (- x-depth 20)
        x-f1 (+ x-depth 40)
        x-f2 (+ x-f1 140)
        x-mix (+ x-f2 80)
        x-border (+ x-mix 40)
        x-map {:p0 x0 :title x-title :border x-border
               :display x-display :edit x-edit 
               :depth x-depth :source x-source
               :f1 x-f1 :f2 x-f2 :mix x-mix}
        y0 (vertical-position :y0 p0)
        y-slider1 (vertical-position :slider1 p0)
        y-title (vertical-position :sub-title p0)
        y-display (- y0 290)
        y-edit (+ y-display 2)
        y-map {:p0 y0 :title y-title 
               :border (vertical-position :border p0)
               :display y-display :edit y-edit
               :source (+ y-slider1 40)
               :depth y-slider1
               :f1 y-slider1 :f2 (- y-slider1 sfactory/slider-length)
               :mix y-slider1}
        x (get x-map item)
        y (get y-map item)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate efx pshifter item = %s" item)))
    [x y]))

(defn- draw-pitch-shifter [bg p0]
  (let [[x-depth y-slider1](pshifter-pos :depth p0)
        x-mix (first (pshifter-pos :mix p0))
        x-f1 (first (pshifter-pos :f1 p0))
        x-f2 (first (pshifter-pos :f2 p0))
        y-slider2 (- y-slider1 sfactory/slider-length)]
    (sfactory/label bg [x-depth y-slider1] "Dpth" :offset [-12 20])
    (sfactory/label bg [x-mix y-slider1] "Mix" :offset [-10 20])
    (sfactory/label bg [x-f1 y-slider1] "Time" :offset [57 20])
    (sfactory/vtext bg [(+ x-f2 10)(+ y-slider2 60)] "Pitch" :color (lnf/label))
    (sfactory/label bg [x-f1 y-slider1] "Dispersion" :offset [30 40] :size 6.5)
    ;; ticks
    (sfactory/major-tick-marks bg x-depth y-slider1 y-slider2)
    (sfactory/minor-ticks bg x-depth y-slider1 y-slider2 16)
    (doseq [x (range x-f1 x-f2 30)]
      (line/line (.root bg) [x y-slider1][x y-slider2] :color (lnf/minor-tick) :style :dotted))
    (doseq [y (range y-slider2 y-slider1 30)]
      (line/line (.root bg) [x-f1 y][x-f2 y] :color (lnf/minor-tick) :style :dotted))
    (sfactory/sub-title bg (pshifter-pos :title p0) "Pitch Shifter")
    (sfactory/db-ticks bg x-mix y-slider1)
    (sfactory/minor-border bg (pshifter-pos :p0 p0)(pshifter-pos :border p0))))

(defn- pitch-shifter-panel [drw ied p0]
  (let [param-ratio :pitchshift-ratio
        param-source :pitchshift-ratio-source
        param-depth :pitchshift-ratio-depth
        param-p-dispersion :pitchshift-pitch-dispersion
        param-t-dispersion :pitchshift-time-dispersion
        param-mix :pitchshift-mix
        dbar-ratio (sfactory/displaybar drw (pshifter-pos :display p0) 6)
        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider-value s)]
                          (.set-param! ied param val)))

        dispersion-action (fn [fld _] 
                            (let [b (:ball1 @(.get-property fld :balls*))
                                  [td pd](.get-property b :value)]
                              (.set-param! ied param-t-dispersion (* td td))
                              (.set-param! ied param-p-dispersion (* pd pd pd pd))
                              (.status! ied (format "pitchshift dispersion time %1.4f  pitch %1.4f" td pd))))
        edit-action (fn [& _]
                      (dbar/displaybar-dialog dbar-ratio
                                              "Pitch Shifter Ratio [0 < ratio <= 16]"
                                              :validator (fn [q]
                                                           (let [f (math/str->float q)]
                                                             (and f (> f 0)(<= f 16))))
                                              :callback (fn [_]
                                                          (let [s (.current-display dbar-ratio)
                                                                f (math/str->float s)]
                                                            (.set-param! ied param-ratio f)))))
                      
        s-depth (sfactory/vslider drw ied param-depth (pshifter-pos :depth p0) -1.0 1.0 slider-action)
        s-mix (sfactory/vslider drw ied param-mix (pshifter-pos :mix p0) min-db max-db slider-action
                                :value-hook int)
        [f-dispersion dispersion-ball] (sfactory/field drw (pshifter-pos :f1 p0)(pshifter-pos :f2 p0)
                                                       param-t-dispersion [0.0 1.0]
                                                       param-p-dispersion [1.0 0.0]
                                                       dispersion-action)
       
        b-source (matrix/source-button drw ied param-source (pshifter-pos :source p0))
        b-edit (sfactory/mini-edit-button drw (pshifter-pos :edit p0) param-ratio edit-action)
        sync-fn (fn [dmap]
                  (slider-value! s-depth (param-depth dmap))
                  (slider-value! s-mix (param-mix dmap))
                  (msb-state! b-source (int (param-source dmap)))
                  (.display! dbar-ratio (format "%1.4f" (float (param-ratio dmap))) false)
                  (let [td (math/sqrt (param-t-dispersion dmap))
                        pd (math/sqrt (math/sqrt (param-p-dispersion dmap)))]
                    (field/set-ball-value! f-dispersion :ball1 [td pd] false))
                  )]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                  Flanger

(defn- flanger-pos [item p0]
  (let [x0 (+ (first p0) flanger-xoffset)
        x-title (+ x0 10)
        x-depth (+ x0 60)
        x-source (- x-depth 20)
        x-lfo-freq (+ x-depth 60)
        x-lfo-amp (+ x-lfo-freq 40)
        x-feedback (+ x-lfo-amp 65)
        x-crossmix (+ x-feedback 68)
        x-mix (+ x-crossmix 60)
        x-border (+ x-mix 40)
        x-map {:p0 x0 :title x-title :border x-border
               :depth x-depth :source x-source  :feedback x-feedback
               :lfo-freq x-lfo-freq :lfo-amp x-lfo-amp
               :crossmix x-crossmix :mix x-mix}
        y0 (vertical-position :y0 p0)
        y-slider1 (vertical-position :slider1 p0)
        y-title (vertical-position :sub-title p0)
        y-border (vertical-position :border p0)
        y-map {:p0 y0 :title y-title :border y-border
               :source (+ y-slider1 40)}
        x (item x-map)
        y (get y-map item y-slider1)]
     (if (or (not x)(not y))
      (println (format "ERROR nil coordinate efx lnager item = %s" item)))
    [x y]))

(defn- draw-flanger [bg p0]
  (let [[x-depth y1](flanger-pos :depth p0)
        x-lfo-freq (first (flanger-pos :lfo-freq p0))
        x-lfo-amp (first (flanger-pos :lfo-amp p0))
        x-fb (first (flanger-pos :feedback p0))
        x-xmix (first (flanger-pos :crossmix p0))
        x-mix (first (flanger-pos :mix p0))
        y2 (- y1 sfactory/slider-length)]
    (sfactory/label bg [x-depth y1] "Dpth" :offset [-12 20])
    (sfactory/label bg [x-lfo-freq y1] "Freq" :offset [-12 20])
    (sfactory/label bg [x-lfo-amp y1] "Amp" :offset [-10 20])
    (sfactory/label bg [x-lfo-freq y1] "LFO" :offset [10 40])
    (sfactory/label bg [x-fb y1] "Feedback" :size 5 :offset [-24 20])
    (sfactory/label bg [x-xmix y1] "CrossMix" :size 5 :offset [-24 20])
    (sfactory/label bg [x-mix y1] "Mix" :offset [-10 20])
    (doseq [x [x-depth x-fb]]
      (sfactory/major-tick-marks bg x y1 y2)
      (sfactory/minor-ticks bg x y1 y2 16))
    (doseq [x [x-lfo-freq x-lfo-amp x-xmix]]
      (sfactory/minor-ticks bg x y1 y2 10))
    (sfactory/db-ticks bg x-mix y1)
    (sfactory/sub-title bg (flanger-pos :title p0) "Flanger")
    (sfactory/minor-border bg (flanger-pos :p0 p0)(flanger-pos :border p0))))

(defn- flanger-panel [drw ied p0]
  (let [min-lfo-freq 0.0
        max-lfo-freq 5.0
        param-source :flanger-mod-source
        param-depth  :flanger-mod-depth
        param-lfo-amp :flanger-lfo-amp
        param-lfo-freq :flanger-lfo-rate
        param-fb :flanger-feedback
        param-crossmix :flanger-crossmix
        param-mix :flanger-mix
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        lfo-freq-action (fn [s _]
                          (let [param param-lfo-freq
                                val (slider-value s)]
                            (.set-param! ied param (* val val))))
        s-depth (sfactory/vslider drw ied param-depth (flanger-pos :depth p0) -1.0 1.0 action)
        s-lfo-freq (sfactory/vslider drw ied param-lfo-freq (flanger-pos :lfo-freq p0) 
                                     min-lfo-freq (math/sqrt max-lfo-freq)
                                     lfo-freq-action)
        s-lfo-amp (sfactory/vslider drw ied param-lfo-amp (flanger-pos :lfo-amp p0) 0.0 1.0 action)
        
        s-fb (sfactory/vslider drw ied param-fb (flanger-pos :feedback p0) -1.0 1.0 action)

        s-crossmix (sfactory/vslider drw ied param-crossmix (flanger-pos :crossmix p0) 0.0 1.0 action)
        s-mix (sfactory/vslider drw ied param-mix (flanger-pos :mix p0) min-db max-db action
                                :value-hook int)
        b-source (matrix/source-button drw ied param-source (flanger-pos :source p0))
        sync-fn (fn [dmap]
                  (let [lfo-freq (param-lfo-freq dmap)]
                    (slider-value! s-depth (param-depth dmap))
                    (slider-value! s-lfo-amp (param-lfo-amp dmap))
                    (slider-value! s-fb (param-fb dmap))
                    (slider-value! s-crossmix (param-crossmix dmap))
                    (slider-value! s-mix (param-mix dmap))
                    (slider-value! s-lfo-freq (* lfo-freq lfo-freq))
                    (msb-state! b-source (int (param-source dmap)))))]
    sync-fn))
   

; ---------------------------------------------------------------------- 
;                                   Delay

(defn- delay-pos [n item p0]
  (let [x0 (+ (first p0) (if (= n 1) delay1-xoffset delay2-xoffset))
        x-title (+ x0 10)
        x-delay-depth (+ x0 60)
        x-delay-source (- x-delay-depth 20)
        x-fb (+ x-delay-depth 60)
        x-damp (+ x-fb 40)
        x-amp-depth (+ x-damp 60)
        x-amp-source (- x-amp-depth 20)
        x-pan (+ x-amp-depth 60)
        x-mix (+ x-pan 60)
        x-border (+ x-mix 40)
        x-display (+ x0 60)
        x-edit (+ x-display 160)
        x-map {:p0 x0 :title x-title :border x-border
               :delay-depth x-delay-depth :delay-source x-delay-source
               :feedback x-fb :damp x-damp
               :amp-depth x-amp-depth :amp-source x-amp-source
               :pan x-pan :mix x-mix
               :display x-display :edit x-edit}
        y0 (vertical-position :y0 p0)
        y-slider1 (vertical-position :slider1 p0)
        y-source (+ y-slider1 40)
        y-title (vertical-position :sub-title p0)
        y-border (vertical-position :border p0)
        y-display (- y0 290)
        y-edit (+ y-display 2)
        y-map {:p0 y0 :title y-title :border y-border
               :display y-display :edit y-edit
               :delay-source y-source :amp-source y-source}
        x (item x-map)
        y (get y-map item y-slider1)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate efx delay item = %s" item)))
    [x y]))

(defn- draw-delay [n bg p0]
  (let [[x-delay-depth y1](delay-pos n :delay-depth p0)
        x-fb (first (delay-pos n :feedback p0))
        x-damp (first (delay-pos n :damp p0))
        x-amp (first (delay-pos n :amp-depth p0))
        x-pan (first (delay-pos n :pan p0))
        x-mix (first (delay-pos n :mix p0))
        y2 (- y1 sfactory/slider-length)]
    (sfactory/label bg [x-delay-depth y1] "Delay" :offset [-17 20])
    (sfactory/label bg [x-fb y1] "Feedback" :size 5 :offset [-24 20])
    (sfactory/label bg [x-damp y1] "Damp" :size 5 :offset [-10 20])
    (sfactory/label bg [x-amp y1] "Amp" :offset [-10 20])
    (sfactory/label bg [x-pan y1] "Pan" :offset [-10 20])
    (sfactory/label bg [x-mix y1] "Mix" :offset [-10 20])
    (doseq [x [x-delay-depth x-fb x-amp x-pan]]
      (sfactory/major-tick-marks bg x y1 y2)
      (sfactory/minor-ticks bg x y1 y2 16))
    (sfactory/minor-ticks bg x-damp y1 y2 10)
    (sfactory/db-ticks bg x-mix y1)
    (sfactory/sub-title bg (delay-pos n :title p0) (format "Delay %d" n))
    (sfactory/minor-border bg (delay-pos n :p0 p0)(delay-pos n :border p0)) ))

(defn- delay-panel [n drw ied p0]
  (let [param-delay (keyword (format "echo%d-delay" n))
        param-delay-source (keyword (format "echo%d-delay-source" n))
        param-delay-depth (keyword (format "echo%d-delay-depth" n))
        param-feedback (keyword (format "echo%d-feedback" n))
        param-damp (keyword (format "echo%d-damp" n))
        param-pan (keyword (format "echo%d-pan" n))
        param-amp-source (keyword (format "echo%d-amp-source" n))
        param-amp-depth (keyword (format "echo%d-amp-depth" n))
        param-mix (keyword (format "echo%d-mix" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-delay-mod (sfactory/vslider drw ied param-delay-depth (delay-pos n :delay-depth p0) -1.0 1.0 action)
        s-fb (sfactory/vslider drw ied param-feedback (delay-pos n :feedback p0) -1.0 1.0 action)
        s-damp (sfactory/vslider drw ied param-damp (delay-pos n :damp p0) 0.0 1.0 action)
        s-amp-depth (sfactory/vslider drw ied param-amp-depth (delay-pos n :amp-depth p0) -1.0 1.0 action)
        s-pan (sfactory/vslider drw ied param-pan (delay-pos n :pan p0) -1.0 1.0 action)
        s-mix (sfactory/vslider drw ied param-mix (delay-pos n :mix p0) min-db max-db action :value-hook int)
        b-delay-source (matrix/source-button drw ied param-delay-source (delay-pos n :delay-source p0))
        b-amp-source (matrix/source-button drw ied param-amp-source (delay-pos n :amp-source p0))
        dbar (sfactory/displaybar drw (delay-pos n :display p0) 6)
        edit-action (fn [& _]
                      (dbar/displaybar-dialog dbar
                                              (format "Delay %d Time [0 <= t <= 2]" n)
                                              :validator (fn [q]
                                                           (let [f (math/str->float q)]
                                                             (and f (>= f 0)(<= f 2))))
                                              :callback (fn [_]
                                                          (let [s (.current-display dbar)
                                                                f (math/str->float s)]
                                                            (.set-param! ied param-delay f)))))
        b-edit (sfactory/mini-edit-button drw (delay-pos n :edit p0) :delay-edit edit-action)
        sync-fn (fn [dmap]
                  (slider-value! s-delay-mod (param-delay-depth dmap))
                  (slider-value! s-fb (param-feedback dmap))
                  (slider-value! s-damp (param-damp dmap))
                  (slider-value! s-amp-depth (param-amp-depth dmap))
                  (slider-value! s-pan (param-pan dmap))
                  (slider-value! s-mix (param-mix dmap))
                  (msb-state! b-delay-source (int (param-delay-source dmap)))
                  (msb-state! b-amp-source (int (param-amp-source dmap)))
                  (.display! dbar (format "%5.3f" (param-delay dmap)) false))]
    sync-fn))
