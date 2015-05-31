(ns cadejo.instruments.cobalt.editor.efx-editor
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.instruments.cobalt.editor.op-env-panel :as envpan])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.col :as col])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 1605)
(def ^:private height 718)
(def ^:private bottom-margin 30)
(def ^:private top-margin bottom-margin)
(def ^:private left-margin 30)
(def ^:private time-scale-states [[:1 "  1" :green]
                                  [:4 "  4" :green]
                                  [:16 " 16" :green]
                                  [:64  " 64" :green]
                                  [:256 "256" :green]])

(declare lfo-panel)
(declare delay-panel)
(declare output-panel)
(declare draw-background)
(declare draw-lfo-panel)
(declare draw-delay-panel)
(declare draw-output-panel)

;; Vertical positions
;; section either :upper or :lower, defaults to :upper
;;
(defn- vpos 
  ([item section]
   (let [top (section {:upper top-margin :lower (+ top-margin (* 0.5 height))})
         bottom (int (+ top (* 0.5 height)))
         slider-bottom (- bottom 50)
         slider-top (- slider-bottom sf/slider-length)
         title (+ top 30)
         dbar (+ top 50)]
     (get {:top top
           :bottom bottom
           :title title
           :slider-top slider-top
           :slider-bottom slider-bottom
           :dbar dbar} item)))
  ([item](vpos item :upper)))

(def env-position [left-margin 390])
(def lfo2-position [(+ (first env-position) 450) (vpos :bottom :upper)])
(def lfo3-position [(first lfo2-position)(vpos :bottom :lower)])
(def delay1-position [(+ (first lfo2-position) 310) (vpos :bottom :upper)])
(def delay2-position [(first delay1-position) (vpos :bottom :lower)])
(def outpan-position [left-margin (vpos :bottom :lower)])

(defn efx-editor [ied]
 (let [drw (let [d (sf/sgwr-drawing width (+ height (* 2 bottom-margin)))]
             (draw-background d)
             d)
       envpan (envpan/env-panel "xenv" drw ied env-position)
       lfo2 (lfo-panel 2 drw ied)
       lfo3 (lfo-panel 3 drw ied)
       delay1 (delay-panel 1 drw ied)
       delay2 (delay-panel 2 drw ied)
       outpan (output-panel drw ied)
       pan-main (ss/scrollable (ss/vertical-panel :items [(.canvas drw)]))
       widget-map {:pan-main pan-main
                   :drawing drw}
       ed (reify subedit/InstrumentSubEditor
            (widgets [this] widget-map)
            (widget [this key](key widget-map))
            (parent [this] ied)
            (parent! [this _] ied) ;; ignore
            (status! [this msg](.status! ied msg))
            (warning! [this msg](.warning! ied msg))
            (set-param! [this param value](.set-param! ied param value))
            (init! [this]  ) ;; not implemented
            (sync-ui! [this]
              (let [dmap (.current-data (.bank (.parent-performance ied)))]
                (envpan dmap)
                (lfo2 dmap)
                (lfo3 dmap)
                (delay1 dmap)
                (delay2 dmap)
                (outpan dmap)
                (.render drw))))]
   ed))

(defn- draw-background [ddrw]
  (let [bg (sf/sgwr-drawing width (+ height (* 2 bottom-margin)))]
    (envpan/draw-env-panel "xenv" bg env-position)
    (draw-lfo-panel 2 bg)
    (draw-lfo-panel 3 bg)
    (draw-delay-panel 1 bg)
    (draw-delay-panel 2 bg)
    (draw-output-panel bg)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-omage)]
      (.put-property! iobj :image (.image bg))
      iobj)))

; ---------------------------------------------------------------------- 
;                                    LFO

(defn- lfo-pos [n item]
  (let [section (if (= n 2) :upper :lower)
        [x0 y0](if (= n 2) lfo2-position lfo3-position)
        pan-width 300
        pan-height (int (* 0.5 height))
        x-border (+ x0 pan-width)
        x-dbar (+ x0 70)
        x-edit-freq (+ x-dbar 160)
        x-env (+ x0 100)
        x-cca (+ x-env 50)
        x-ccb (+ x-cca 50)
        y-border (- y0 pan-height)
        y-dbar (vpos :dbar section)
        y-edit-freq y-dbar
        y-slider1 (vpos :slider-bottom section)
        rs (get {:p0 [x0 y0]
                 :border [x-border y-border]
                 :title [(+ x0 30)(+ y-border 30)]
                 :dbar [x-dbar y-dbar]
                 :edit-freq [x-edit-freq y-edit-freq]
                 :env [x-env y-slider1]
                 :cca [x-cca y-slider1]
                 :ccb [x-ccb y-slider1]} item)]
    rs))

(defn- draw-lfo-panel [n bg]
  (let [slider-positions [(lfo-pos n :env)(lfo-pos n :cca)(lfo-pos n :ccb)]
        y1 (second (first slider-positions))
        y2 (- y1 sf/slider-length)
        xenv (first (first slider-positions))]
    (sf/minor-border bg (lfo-pos n :p0)(lfo-pos n :border))
    (sf/label bg (lfo-pos n :env) "Env" :offset [-10 20])
    (sf/label bg (lfo-pos n :cca) "CCA" :offset [-10 20])
    (sf/label bg (lfo-pos n :ccb) "CCB" :offset [-10 20])
    (sf/label bg (lfo-pos n :dbar) "Freq" :offset [-40 20])
    (sf/sub-title bg (lfo-pos n :title) (format "LFO %d" n))
    (doseq [x (map first slider-positions)]
      (sf/minor-ticks bg x y1 y2 10)
      (sf/major-tick-marks bg x y1 y2 
                           :v0 0.0 :v1 1.0 :step 0.5
                           :frmt (if (= x xenv) "%4.1f" "")
                           :font-size 5.0))))

(defn- lfo-panel [n drw ied]
  (let [param-freq (keyword (format "lfo%d-freq" n))
        param-env (keyword (format "lfo%d-amp<-xenv" n))
        param-cca (keyword (format "lfo%d-amp<-cca" n))
        param-ccb (keyword (format "lfo%d-amp<-ccb" n))
        dbar-freq (sf/displaybar drw (lfo-pos n :dbar) 5)
        dbar-edit-action (fn [& _]
                           (let [mn con/min-lfo-frequency
                                 mx con/max-lfo-frequency
                                 prompt (format "LFO %s Frequency (%s <= f <= %s)" n mn mx)]
                             (dbar/displaybar-dialog dbar-freq prompt
                                                     :validator (fn [q]
                                                                  (let [f (math/str->float q)]
                                                                    (and f (<= mn f)(<= f mx))))
                                                     :callback (fn [_]
                                                                 (let [f (math/str->float (.current-display dbar-freq))]
                                                                   (.set-param! ied param-freq f))))))
                                                                       
                      
        b-dbar (sf/mini-edit-button drw (lfo-pos n :edit-freq) param-freq dbar-edit-action)
        slider-action (fn [s _] 
                         (let [param (.get-property s :id)
                               val (slider/get-slider-value s)]
                           (.set-param! ied param val)))
        s-env (sf/vslider drw ied param-env (lfo-pos n :env) 0.0 1.0 slider-action)
        s-cca (sf/vslider drw ied param-cca (lfo-pos n :cca) 0.0 1.0 slider-action)
        s-ccb (sf/vslider drw ied param-ccb (lfo-pos n :ccb) 0.0 1.0 slider-action)]
    (fn [dmap]
      (let [f (param-freq dmap)
            e (param-env dmap)
            a (param-cca dmap)
            b (param-ccb dmap)]
        (.display! dbar-freq (format "%5.3f" (float f)) false)
        (slider/set-slider-value! s-env (float e) false)
        (slider/set-slider-value! s-cca (float a) false)
        (slider/set-slider-value! s-ccb (float b) false)))))


; ---------------------------------------------------------------------- 
;                                   Delay

(defn- delay-pos [n item]
  (let [section (if (= n 1) :upper :lower)
        [x0 y0] (if (= n 1) delay1-position delay2-position)
        pan-height (int (* 0.5 height))
        x-dbar (+ x0 70)
        x-edit-time (+ x-dbar 220)
        x-init (+ x-edit-time 100)
        x-time-lfo2 (+ x0 70)
        x-time-lfo3 (+ x-time-lfo2 50)
        x-time-env (+ x-time-lfo3 50)
        x-fb (+ x-time-env 75)
        x-xfb (+ x-fb 50)
        x-amp (+ x-xfb 75)
        x-amp-lfo2 (+ x-amp 60)
        x-amp-lfo3 (+ x-amp-lfo2 50)
        x-amp-env (+ x-amp-lfo3 50)
        x-pan (+ x-amp-env 75)
        x-pan-lfo2 (+ x-pan 50)
        x-pan-lfo3 (+ x-pan-lfo2 50)
        x-pan-env (+ x-pan-lfo3 50)
        x-border (+ x-pan-env 50)
        y-border (- y0 pan-height)
        y-dbar (vpos :dbar section)
        y-edit-time y-dbar
        y-init (- y-edit-time 5)
        y-slider1 (vpos :slider-bottom section)
        rs (get {:p0 [x0 y0]
                 :border [x-border y-border]
                 :title [(+ x0 30)(+ y-border 30)]
                 :dbar [x-dbar y-dbar]
                 :edit-time [x-edit-time y-edit-time]
                 :init [x-init y-init]
                 :time-env [x-time-env y-slider1]
                 :time-lfo2 [x-time-lfo2 y-slider1]
                 :time-lfo3 [x-time-lfo3 y-slider1]
                 :fb [x-fb y-slider1]
                 :xfb [x-xfb y-slider1]
                 :amp [x-amp y-slider1]
                 :amp-env [x-amp-env y-slider1]
                 :amp-lfo2 [x-amp-lfo2 y-slider1]
                 :amp-lfo3 [x-amp-lfo3 y-slider1]
                 :pan [x-pan y-slider1]
                 :pan-env [x-pan-env y-slider1]
                 :pan-lfo2 [x-pan-lfo2 y-slider1]
                 :pan-lfo3 [x-pan-lfo3 y-slider1]} item)]
    rs))

(defn- draw-delay-panel [n bg]
  (let [time-positions [(delay-pos n :time-lfo2)(delay-pos n :time-lfo3)(delay-pos n :time-env)]
        fb-positions [(delay-pos n :fb)(delay-pos n :xfb)]
        amp-positions [(delay-pos n :amp-lfo2)(delay-pos n :amp-lfo3)(delay-pos n :amp-env)]
        pan-positions [(delay-pos n :pan)(delay-pos n :pan-lfo2)(delay-pos n :pan-lfo3)(delay-pos n :pan-env)]
        y1 (second (first time-positions))
        y2 (- y1 sf/slider-length)
        ;; Vertical section divider lines
        y3 (+ y1 20)
        y4 (- y2 20)
        vline (fn [x](line/line (.root bg) [x y3][x y4] 
                                :color (lnf/minor-border)
                                :style 1))]
    (sf/label bg (delay-pos n :dbar) "Time" :offset [-40 20])
    (sf/label bg (delay-pos n :time-lfo2) "LFO2" :offset [-10 20])
    (sf/label bg (delay-pos n :time-lfo3) "LFO3" :offset [-10 20])
    (sf/label bg (delay-pos n :time-env) "Env" :offset [-8 20])
    (sf/label bg (delay-pos n :fb) "FB" :offset [-6 20])
    (sf/label bg (delay-pos n :xfb) "xFB" :offset [-8 20])
    (sf/label bg (delay-pos n :amp) "Amp" :offset [-10 20])
    (sf/label bg (delay-pos n :amp-lfo2) "LFO2" :offset [-10 20])
    (sf/label bg (delay-pos n :amp-lfo3) "LFO3" :offset [-10 20])
    (sf/label bg (delay-pos n :amp-env) "Env" :offset [-8 20])
    (sf/label bg (delay-pos n :pan) "Pan" :offset [-10 20])
    (sf/label bg (delay-pos n :pan-lfo2) "LFO2" :offset [-10 20])
    (sf/label bg (delay-pos n :pan-lfo3) "LFO3" :offset [-10 20])
    (sf/label bg (delay-pos n :pan-env) "Env" :offset [-8 20])
    ;; Time slider ticks
    (doseq [x (map first time-positions)]
      (sf/minor-ticks bg x y1 y2 16)
      (sf/major-tick-marks bg x y1 y2 
                           :v0 -1.0 :v1 1.0 :step 0.5
                           :frmt (if (= x (first (first time-positions))) "%+4.1f" "")
                           :font-size 5.0))
    ;; Feedback slider ticks
    (doseq [x (map first fb-positions)]
      (sf/minor-ticks bg x y1 y2 16)
      (sf/major-tick-marks bg x y1 y2 
                           :v0 -1.0 :v1 1.0 :step 0.5
                           :frmt (if (= x (first (first fb-positions))) "%+4.1f" "")
                           :font-size 5.0))
    ;; Amd (db) ticks
    (sf/major-tick-marks bg (first (delay-pos n :amp)) y1 y2 
                         :v0 con/min-db
                         :v1 con/max-db
                         :step 6
                         :frmt "%+3d"
                         :font-size 5.0)
    ;; Amp slider ticks
    (doseq [x (map first amp-positions)]
      (sf/minor-ticks bg x y1 y2 16)
      (sf/major-tick-marks bg x y1 y2 
                           :v0 0.0 :v1 1.0 :step 0.5
                           :frmt (if (= x (first (first amp-positions))) "%+4.1f" "")
                           :font-size 5.0))
    ;; Pan slider ticks
    (doseq [x (map first pan-positions)]
      (sf/minor-ticks bg x y1 y2 16)
      (sf/major-tick-marks bg x y1 y2 
                           :v0 -1.0 :v1 1.0 :step 0.5
                           :frmt (if (= x (first (first pan-positions))) "%+4.1f" "")
                           :font-size 5.0))
    (vline (- (first (delay-pos n :time-lfo2)) 50))
    (vline (+ (first (delay-pos n :time-env)) 25))
    (vline (+ (first (delay-pos n :xfb)) 25))
    (vline (+ (first (delay-pos n :amp-env)) 25))
    (vline (+ (first (delay-pos n :pan-env)) 25))
    (sf/label bg (delay-pos n :time-lfo2) "Time" :size 8.0 :offset [32 -170])
    (sf/label bg (delay-pos n :fb) "Feedback" :size 8.0 :offset [-25 -170])
    (sf/label bg (delay-pos n :amp) "Amplitude" :size 8.0 :offset [40 -170])
    (sf/label bg (delay-pos n :pan) "Pan" :size 8.0 :offset [60 -170])
    (sf/sub-title bg (delay-pos n :title) (format "Delay %d" n))
    (sf/minor-border bg (delay-pos n :p0)(delay-pos n :border))))

(defn- delay-panel [n drw ied]
  (let [param-time (keyword (format "delay%d-time" n))
        param-time-lfo2 (keyword (format "delay%d-time<-lfo2" n))
        param-time-lfo3 (keyword (format "delay%d-time<-lfo3" n))
        param-time-env (keyword (format "delay%d-time<-xenv" n))
        param-fb (keyword (format "delay%d-fb" n))
        param-xfb (keyword (format "delay%d-xfb" n))
        param-amp (keyword (format "delay%d-amp" n))
        param-amp-lfo2 (keyword (format "delay%d-amp<-lfo2" n))
        param-amp-lfo3 (keyword (format "delay%d-amp<-lfo3" n))
        param-amp-env (keyword (format "delay%d-amp<-xenv" n))
        param-pan (keyword (format "delay%d-pan" n))
        param-pan-lfo2 (keyword (format "delay%d-pan<-lfo2" n))
        param-pan-lfo3 (keyword (format "delay%d-pan<-lfo3" n))
        param-pan-env (keyword (format "delay%d-pan<-xenv" n))
        dbar-time (sf/displaybar drw (delay-pos n :dbar) 7)
        dbar-edit-action (fn [& _]
                           (let [mn 0.0
                                 mx (float con/max-delay-time)
                                 prompt (format "Delay %d time (%s <= t <= %s)" n mn mx)]
                             (dbar/displaybar-dialog dbar-time prompt
                                                     :validator (fn [q]
                                                                  (let [f (math/str->float q)]
                                                                    (and f (<= mn f)(<= f mx))))
                                                     :callback (fn [_]
                                                                 (let [f (math/str->float (.current-display dbar-time))]
                                                                   (.set-param! ied param-time f))))))
        b-dbar (sf/mini-edit-button drw (delay-pos n :edit-time) param-time dbar-edit-action)
        init-action (fn [& _] 
                      (.set-param! ied param-time-lfo2 0)
                      (.set-param! ied param-time-lfo3 0)
                      (.set-param! ied param-time-env 0)
                      (.set-param! ied param-fb 0)
                      (.set-param! ied param-xfb 0)
                      (.set-param! ied param-amp con/min-db)
                      (.set-param! ied param-amp-lfo2 0)
                      (.set-param! ied param-amp-lfo3 0)
                      (.set-param! ied param-amp-env 0)
                      (.set-param! ied param-pan (if (= n 1) -0.75 0.75))
                      (.set-param! ied param-pan-lfo2 0)
                      (.set-param! ied param-pan-lfo3 0)
                      (.set-param! ied param-pan-env 0)
                      (.sync-ui! ied)
                      (.status! ied (format "Delay %d reset" n)))
        b-init (sf/init-button drw (delay-pos n :init) :init-delay init-action)
        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        s-time-lfo2 (sf/vslider drw ied param-time-lfo2 (delay-pos n :time-lfo2) -1.0 1.0 slider-action)
        s-time-lfo3 (sf/vslider drw ied param-time-lfo3 (delay-pos n :time-lfo3) -1.0 1.0 slider-action)
        s-time-env  (sf/vslider drw ied param-time-env (delay-pos n :time-env) -1.0 1.0 slider-action)
        s-fb  (sf/vslider drw ied param-fb (delay-pos n :fb) -1.0 1.0 slider-action)
        s-xfb (sf/vslider drw ied param-xfb (delay-pos n :xfb) -1.0 1.0 slider-action)
        s-amp (sf/vslider drw ied param-amp (delay-pos n :amp) con/min-db con/max-db slider-action :value-hook int)
        s-amp-lfo2 (sf/vslider drw ied param-amp-lfo2 (delay-pos n :amp-lfo2) 0.0 1.0 slider-action)
        s-amp-lfo3 (sf/vslider drw ied param-amp-lfo3 (delay-pos n :amp-lfo3) 0.0 1.0 slider-action)
        s-amp-env  (sf/vslider drw ied param-amp-env (delay-pos n :amp-env) 0.0 1.0 slider-action)
        s-pan (sf/vslider drw ied param-pan (delay-pos n :pan) -1.0 1.0 slider-action)
        s-pan-lfo2 (sf/vslider drw ied param-pan-lfo2 (delay-pos n :pan-lfo2) -1.0 1.0 slider-action)
        s-pan-lfo3 (sf/vslider drw ied param-pan-lfo3 (delay-pos n :pan-lfo3) -1.0 1.0 slider-action)
        s-pan-env  (sf/vslider drw ied param-pan-env (delay-pos n :pan-env) -1.0 1.0 slider-action)]
    (fn [dmap]
      (let [ssv (fn [s v](slider/set-slider-value! s (float v) false))
            t (param-time dmap)
            t-lfo2 (param-time-lfo2 dmap)
            t-lfo3 (param-time-lfo3 dmap)
            t-env (param-time-env dmap)
            fb (param-fb dmap)
            xfb (param-xfb dmap)
            a (param-amp dmap)
            a-lfo2 (param-amp-lfo2 dmap)
            a-lfo3 (param-amp-lfo3 dmap)
            a-env (param-amp-env dmap)
            p (param-pan dmap)
            p-lfo2 (param-pan-lfo2 dmap)
            p-lfo3 (param-pan-lfo3 dmap)
            p-env (param-pan-env dmap)]
        (.display! dbar-time (format "%7.5f" (float t)) false)
        (ssv s-time-lfo2 t-lfo2)
        (ssv s-time-lfo3 t-lfo3)
        (ssv s-time-env t-env)
        (ssv s-fb fb)
        (ssv s-xfb xfb)
        (ssv s-amp a)
        (ssv s-amp-lfo2 a-lfo2)
        (ssv s-amp-lfo3 a-lfo3)
        (ssv s-amp-env a-env)
        (ssv s-pan p)
        (ssv s-pan-lfo2 p-lfo2)
        (ssv s-pan-lfo3 p-lfo3)
        (ssv s-pan-env p-env))))) 


; ---------------------------------------------------------------------- 
;                               Output Panel

(defn- out-pos [item]
  (let [section :lower
        [x0 y0] outpan-position
        pan-width 300  ; FPO
        pan-height 300 ; FPO
        x-border (+ x0 pan-width)
        x-amp (+ x0 110)
        x-dry-amp (+ x-amp 70)
        x-vel (+ x-dry-amp 140)
        x-cc7 (+ x-vel 50)

        y-border (- y0 pan-height)
        y-slider1 (vpos :slider-bottom section)
        rs (get {:p0 [x0 y0]
                 :border [x-border y-border]
                 :title [(+ x0 30)(+ y-border 30)]
                 :amp [x-amp y-slider1]
                 :dry [x-dry-amp y-slider1]
                 :vel [x-vel y-slider1]
                 :cc7 [x-cc7 y-slider1]} item)]
    rs))

(defn- draw-output-panel [bg]
  (let [pos-amp (out-pos :amp)
        pos-dry (out-pos :dry)
        pos-vel (out-pos :vel)
        pos-cc7 (out-pos :cc7)
        y1 (second pos-amp)
        y2 (- y1 sf/slider-length)]
    (sf/label bg pos-amp "Amp" :offset [-10 20])
    (sf/label bg pos-dry "Dry" :offset [-10 20])
    (sf/label bg pos-vel "Vel" :offset [-10 20])
    (sf/label bg pos-cc7 "CC7" :offset [-10 20])
    (sf/sub-title bg pos-amp "Main Out" :offset [80 -200])
    (doseq [x (map first [pos-amp pos-dry])]
      (sf/major-tick-marks bg x y1 y2 
                           :v0 con/min-db 
                           :v1 con/max-db
                           :step 6
                           :frmt "%+3d"
                           :font-size 5.0))
    (doseq [x (map first [pos-vel pos-cc7])]
      (sf/minor-ticks bg x y1 y2 10)
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 :step 0.5
                           :frmt (if (= x (first pos-vel)) "%3.1f" "")
                           :font-size 5.0))))

(defn- output-panel [drw ied]
  (let [action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider/get-slider-value s)]
                   (.set-param! ied param val)))
        s-amp (sf/vslider drw ied :amp (out-pos :amp) con/min-db con/max-db action :value-hook int)
        s-dry (sf/vslider drw ied :dry-amp (out-pos :dry) con/min-db con/max-db action :value-hook int)
        s-vel (sf/vslider drw ied :amp<-velocity (out-pos :vel) 0.0 1.0 action)
        s-cc7 (sf/vslider drw ied :amp<-cc7 (out-pos :cc7) 0.0 1.0 action)]
    (fn [dmap]
      (let [amp (:amp dmap)
            dry (:dry-amp dmap)
            vel (:amp<-velocity dmap)
            cc7 (:amp<-cc7 dmap)]
        (slider/set-slider-value! s-amp amp false)
        (slider/set-slider-value! s-dry dry false)
        (slider/set-slider-value! s-vel vel false)
        (slider/set-slider-value! s-cc7 cc7 false)))))
