;; vibrato
;; LFO1
;; pitch env
;; port

(ns cadejo.instruments.cobalt.editor.vibrato-editor
 (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.col :as col])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 1400)
(def ^:private height 380)
(def ^:private bottom-margin 30)
(def ^:private left-margin 30)
(def ^:private x-port left-margin)
(def ^:private x-vibrato (+ x-port 160))
(def ^:private x-lfo1 (+ x-vibrato 260))
(def ^:private x-penv (+ x-lfo1 260))
(def ^:private time-scale-states [[:1 "  1" :green]
                                  [:4 "  4" :green]
                                  [:16 " 16" :green]
                                  [:64  " 64" :green]
                                  [:256 "256" :green]])
(def ^:private level-scale-states [[:1 "1" :green]
                                   [:10 "1/10" :green]
                                   [:100 "1/100" :green]])

(declare port-panel)
(declare vibrato-panel)
(declare lfo-panel)
(declare penv-panel)
(declare draw-background)
(declare draw-port-panel)
(declare draw-vibrato-panel)
(declare draw-lfo-panel)
(declare draw-penv-panel)

(defn vibrato-editor [ied]
  (let [drw (let [d (sf/sgwr-drawing width height)]
              (draw-background d)
              d)
        portamento (port-panel drw ied)
        vibrato (vibrato-panel drw ied)
        lfo1 (lfo-panel drw ied)
        pitch-env (penv-panel drw ied)
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
                 (portamento dmap)
                 (vibrato dmap)
                 (lfo1 dmap)
                 (pitch-env dmap))
               (.render drw)))]
    ed))

(defn- draw-background [ddrw]
  (let [bg (sf/sgwr-drawing width height)]
    (draw-port-panel bg)
    (draw-vibrato-panel bg)
    (draw-lfo-panel bg)
    (draw-penv-panel bg)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-omage)]
      (.put-property! iobj :image (.image bg))
      iobj)))

(defn- vpos [item]
  (let [top 0
        bottom height
        top-border (+ top bottom-margin)
        bottom-border (- bottom bottom-margin)
        slider-bottom (- bottom-border 50)
        slider-top (- slider-bottom sf/slider-length)
        title (+ top-border 30)
        dbar (+ top-border 50)]
    (get {:top-border top-border
          :title title
          :slider-top slider-top
          :slider-bottom slider-bottom
          :bottom-border bottom-border
          :dbar dbar
          } item)))

; ---------------------------------------------------------------------- 
;                                Portamento

(defn- port-pos [item]
  (let [x-time (+ x-port 50)
        x-cc5 (+ x-time 50)
        x-border (+ x-cc5 50)
        y0 (vpos :bottom-border)
        y-slider (vpos :slider-bottom)
        y-border (vpos :top-border)]
    (get {:p0 [x-port y0]
          :border [x-border y-border]
          :title [(+ x-port 20) (vpos :title)]
          :time [x-time y-slider]
          :cc5 [x-cc5 y-slider]} item)))

(defn- draw-port-panel [bg]
  (let [pos-time (port-pos :time)
        pos-cc5 (port-pos :cc5)
        [xt x5](map first [pos-time pos-cc5])
        y1 (second pos-time)
        y2 (- y1 sf/slider-length)]
  (sf/label bg pos-time "Time" :offset [-12 20])
  (sf/label bg pos-cc5 "CC5" :offset [-10 20])
  (sf/sub-title bg (port-pos :title) "Portamento")
  (sf/minor-border bg (port-pos :p0)(port-pos :border))
  (doseq [x [xt x5]]
    (sf/minor-ticks bg x y1 y2 10)
    (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 :step 0.5
                         :frmt (if (= x xt) "%3.1f" "")
                         :size 5.0))))

(defn- port-panel [drw ied]
  (let [param-time :port-time
        param-cc5 :port-time<-cc5
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (float (slider/get-slider-value s))]
                   (.set-param! ied param val)))
        s-time (sf/vslider drw ied param-time (port-pos :time) 0.0 con/max-port-time action)
        s-cc5 (sf/vslider drw ied param-cc5 (port-pos :cc5) 0.0 1.0 action)]
    (fn [dmap]
      (let [time (float (param-time dmap))
            cc5 (float (param-cc5 dmap))]
        (slider/set-slider-value! s-time time false)
        (slider/set-slider-value! s-cc5 cc5 false)))))
        
        
; ---------------------------------------------------------------------- 
;                                  Vibrato

(defn- vib-pos [item]
  (let [x0 x-vibrato
        x-freq (+ x0 60)
        x-freq-edit (+ x-freq 150)
        x-sens (+ x0 50)
        x-prss (+ x-sens 50)
        x-bleed (+ x-prss 50)
        x-delay (+ x-bleed 50)
        x-border (+ x-delay 50)
        y0 (vpos :bottom-border)
        y-slider (vpos :slider-bottom)
        y-border (vpos :top-border)
        y-freq (vpos :dbar)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :freq [x-freq y-freq]
          :freq-edit [x-freq-edit y-freq]
          :sens [x-sens y-slider]
          :prss [x-prss y-slider]
          :bleed [x-bleed y-slider]
          :delay [x-delay y-slider]
          :title [(+ x0 20) (vpos :title)]}
         item)))

(defn- draw-vibrato-panel [bg]
  (let [pos-freq (vib-pos :freq)
        pos-sens (vib-pos :sens)
        pos-prss (vib-pos :prss)
        pos-bleed (vib-pos :bleed)
        pos-delay (vib-pos :delay)
        xs (first pos-sens)
        y1 (second pos-sens)
        y2 (- y1 sf/slider-length)]
    (sf/label bg pos-freq "Freq" :offset [-40 15])
    (sf/label bg pos-sens "Sens" :offset [-12 20])
    (sf/label bg pos-prss "Prss" :offset [-12 20])
    (sf/label bg pos-bleed "Bleed" :offset [-16 20])
    (sf/label bg pos-delay "Delay" :offset [-16 20])
    (sf/sub-title bg (vib-pos :title) "Vibrato")
    (sf/minor-border bg (vib-pos :p0)(vib-pos :border))
    (doseq [x (map first [pos-sens pos-prss pos-bleed pos-delay])]
      (sf/minor-ticks bg x y1 y2 10)
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 :step 0.5
                           :frmt (if (= x xs) "%3.1f" "")
                           :size 5.0))))

(defn- vibrato-panel [drw ied]
  (let [param-freq :vibrato-frequency
        param-sens :vibrato-sensitivity
        param-prss :vibrato<-pressure
        param-bleed :vibrato-depth
        param-delay :vibrato-delay
        dbar-freq (sf/displaybar drw (vib-pos :freq) 5)
        dbar-edit-action (fn [& _]
                           (dbar/displaybar-dialog dbar-freq 
                                                   "Vibrato Frequency (0 < f <= 10)"
                                                   :validator (fn [q]
                                                                (let [f (math/str->float q)]
                                                                  (and f (pos? f)(<= f 10))))
                                                   :callback (fn [_]
                                                               (let [f (math/str->float (.current-display dbar-freq))]
                                                                 (.set-param! ied param-freq f)))))
        b-dbar-edit (sf/mini-edit-button drw (vib-pos :freq-edit) param-freq dbar-edit-action)
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (float (slider/get-slider-value s))]
                   (.set-param! ied param val)))
        s-sens (sf/vslider drw ied param-sens (vib-pos :sens) 0.0 con/max-vibrato-sensitivity action)
        s-prss (sf/vslider drw ied param-prss (vib-pos :prss) 0.0 1.0 action)
        s-bleed (sf/vslider drw ied param-bleed (vib-pos :bleed) 0.0 1.0 action)
        s-delay (sf/vslider drw ied param-delay (vib-pos :delay) 0.0 1.0 action)]
    (fn [dmap]
      (let [freq (param-freq dmap)
            sens (param-sens dmap)
            prss (param-prss dmap)
            bleed (param-bleed dmap)
            delay (param-delay dmap)]
        (.display! dbar-freq (format "%5.3f" (float freq)) false)
        (slider/set-slider-value! s-sens (float sens) false)
        (slider/set-slider-value! s-prss (float prss) false)
        (slider/set-slider-value! s-bleed (float bleed) false)
        (slider/set-slider-value! s-delay (float delay) false))))) 
            

; ---------------------------------------------------------------------- 
;                                   LFO1

(defn- lfo-pos [item]
  (let [x0 x-lfo1
        x-freq (+ x0 60)
        x-freq-edit (+ x-freq 150)
        x-prss (+ x0 50)
        x-cca (+ x-prss 50)
        x-bleed (+ x-cca 50)
        x-delay (+ x-bleed 50)
        x-border (+ x-delay 50)
        y0 (vpos :bottom-border)
        y-slider (vpos :slider-bottom)
        y-border (vpos :top-border)
        y-freq (vpos :dbar)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :freq [x-freq y-freq]
          :freq-edit [x-freq-edit y-freq]
          :prss [x-prss y-slider]
          :cca [x-cca y-slider]
          :bleed [x-bleed y-slider]
          :delay [x-delay y-slider]
          :title [(+ x0 20) (vpos :title)]}
         item)))

(defn- draw-lfo-panel [bg]
  (let [pos-freq (lfo-pos :freq)
        pos-prss (lfo-pos :prss)
        pos-cca (lfo-pos :cca)
        pos-bleed (lfo-pos :bleed)
        pos-delay (lfo-pos :delay)
        xs (first pos-prss)
        y1 (second pos-prss)
        y2 (- y1 sf/slider-length)]
    (sf/label bg pos-freq "Freq" :offset [-40 15])
    (sf/label bg pos-prss "Prss" :offset [-12 20])
    (sf/label bg pos-cca "CCA" :offset [-10 20])
    (sf/label bg pos-bleed "Bleed" :offset [-16 20])
    (sf/label bg pos-delay "Delay" :offset [-16 20])
    (sf/sub-title bg (lfo-pos :title) "LFO 1")
    (sf/minor-border bg (lfo-pos :p0)(lfo-pos :border))
    (doseq [x (map first [pos-prss pos-cca pos-bleed pos-delay])]
      (sf/minor-ticks bg x y1 y2 10)
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0 :step 0.5
                           :frmt (if (= x xs) "%3.1f" "")
                           :size 5.0))))

(defn- lfo-panel [drw ied]
  (let [param-freq :lfo1-freq
        param-cca :lfo1<-cca
        param-prss :lfo1<-pressure
        param-bleed :lfo1-bleed
        param-delay :lfo1-delay
        dbar-freq (sf/displaybar drw (lfo-pos :freq) 5)
        dbar-edit-action (fn [& _]
                           (dbar/displaybar-dialog dbar-freq 
                                                   "LFO 1 Frequency (0 < f <= 10)"
                                                   :validator (fn [q]
                                                                (let [f (math/str->float q)]
                                                                  (and f (pos? f)(<= f 10))))
                                                   :callback (fn [_]
                                                               (let [f (math/str->float (.current-display dbar-freq))]
                                                                 (.set-param! ied param-freq f)))))
        b-dbar-edit (sf/mini-edit-button drw (lfo-pos :freq-edit) param-freq dbar-edit-action)
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (float (slider/get-slider-value s))]
                   (.set-param! ied param val)))
        s-prss (sf/vslider drw ied param-prss (lfo-pos :prss) 0.0 1.0 action)
        s-cca (sf/vslider drw ied param-cca (lfo-pos :cca) 0.0 1.0 action)
        s-bleed (sf/vslider drw ied param-bleed (lfo-pos :bleed) 0.0 1.0 action)
        s-delay (sf/vslider drw ied param-delay (lfo-pos :delay) 0.0 1.0 action)]
    (fn [dmap]
      (let [freq (param-freq dmap)
            cca (param-cca dmap)
            prss (param-prss dmap)
            bleed (param-bleed dmap)
            delay (param-delay dmap)]
        (.display! dbar-freq (format "%5.3f" (float freq)) false)
        (slider/set-slider-value! s-cca (float cca) false)
        (slider/set-slider-value! s-prss (float prss) false)
        (slider/set-slider-value! s-bleed (float bleed) false)
        (slider/set-slider-value! s-delay (float delay) false)))))


; ---------------------------------------------------------------------- 
;                              Pitch Envelope

(defn- penv-pos [item]
  (let [x0 x-penv
        x-a0 (+ x0 50)
        x-a1 (+ x-a0 50)
        x-a2 (+ x-a1 50)
        x-a3 (+ x-a2 50)
        x-t1 (+ x-a3 75)
        x-t2 (+ x-t1 50)
        x-t3 (+ x-t2 50)
        x-cc9 (+ x-t3 75)
        x-time-scale (+ x-t2 60)
        x-init x-a0
        x-level-scale x-a3
        x-border (+ x-cc9 50)
        y0 (vpos :bottom-border)
        y-slider (vpos :slider-bottom)
        y-border (vpos :top-border)
        y-scale (+ (vpos :dbar) 0)
        y-init (- y-scale 10)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :a0 [x-a0 y-slider]
          :a1 [x-a1 y-slider]
          :a2 [x-a2 y-slider]
          :a3 [x-a3 y-slider]
          :t1 [x-t1 y-slider]
          :t2 [x-t2 y-slider]
          :t3 [x-t3 y-slider]
          :cc9 [x-cc9 y-slider]
          :init [x-init y-init]
          :time-scale [x-time-scale y-scale]
          :level-scale [x-level-scale y-scale]
          :title [(+ x0 20)(vpos :title)]}
         item)))

(defn- draw-penv-panel [bg]
  (let [pos-a0 (penv-pos :a0)
        pos-a1 (penv-pos :a1)
        pos-a2 (penv-pos :a2)
        pos-a3 (penv-pos :a3)
        pos-t1 (penv-pos :t1)
        pos-t2 (penv-pos :t2)
        pos-t3 (penv-pos :t3)
        pos-cc9 (penv-pos :cc9)
        ]
    (sf/label bg pos-a0 "L0" :offset [-6 20])
    (sf/label bg pos-a1 "L1" :offset [-6 20])
    (sf/label bg pos-a2 "L2" :offset [-6 20])
    (sf/label bg pos-a3 "L3" :offset [-6 20])
    (sf/label bg pos-t1 "T1" :offset [-6 20])
    (sf/label bg pos-t2 "T2" :offset [-6 20])
    (sf/label bg pos-t3 "T3" :offset [-6 20])
    (sf/label bg pos-cc9 "CC9" :offset [-8 20])
    (sf/label bg (penv-pos :level-scale) "Level Scale" :offset [-90 20])
    (sf/label bg (penv-pos :time-scale) "Time Scale"   :offset [-90 20])
    (sf/sub-title bg (penv-pos :title) "Pitch Envelope")
    (sf/minor-border bg (penv-pos :p0)(penv-pos :border))))


(defn- penv-panel [drw ied]
  (let [a0 :pe-a0
        a1 :pe-a1
        a2 :pe-a2
        a3 :pe-a3
        t1 :pe-t1
        t2 :pe-t2
        t3 :pe-t3
        cc9 :pe<-cc9
        time-scale* (atom 1)
        level-action (fn [s _]
                       (let [param (.get-property s :id)
                             val (slider/get-slider-value s)]
                         (.set-param! ied param val)))
        s-a0 (sf/vslider drw ied a0 (penv-pos :a0) -1.0 1.0 level-action)
        s-a1 (sf/vslider drw ied a1 (penv-pos :a1) -1.0 1.0 level-action)
        s-a2 (sf/vslider drw ied a2 (penv-pos :a2) -1.0 1.0 level-action)
        s-a3 (sf/vslider drw ied a3 (penv-pos :a3) -1.0 1.0 level-action)
        s-cc9 (sf/vslider drw ied cc9 (penv-pos :cc9) 0.0 1.0 level-action)
        time-action (fn [s _]
                      (let [param (.get-property s :id)
                            pos (slider/get-slider-value s)
                            val (* @time-scale* pos)]
                        (.set-param! ied param val)))
        s-t1 (sf/vslider drw ied t1 (penv-pos :t1) 0.0 1.0 time-action)
        s-t2 (sf/vslider drw ied t2 (penv-pos :t2) 0.0 1.0 time-action)
        s-t3 (sf/vslider drw ied t3 (penv-pos :t3) 0.0 1.0 time-action)
        
        time-scale-action (fn [b _]
                       (let [state (math/str->int (name (second (msb/current-multistate-button-state b))))]
                         (reset! time-scale* state))
                       (doseq [s [s-t1 s-t2 s-t3]]
                         (let [p (.get-property s :id)
                               v (slider/get-slider-value s)]
                           (.set-param! ied p (* @time-scale* v))))
                       (.status! ied (format "Pitch Env Time scale = %s" @time-scale*)))
        level-scale* (atom 1.0)
        msb-time-scale (msb/text-multistate-button (.tool-root drw)
                                                   (penv-pos :time-scale)
                                                   time-scale-states
                                                   :id :penv-time-scale-factor
                                                   :click-action time-scale-action
                                                   :rim-color (lnf/button-border)
                                                   :gap 6)
        level-scale-action (fn [b _]
                             (let [state (/ 1.0 (math/str->int (name (second (msb/current-multistate-button-state b)))))]
                               (reset! level-scale* state))
                             (doseq [s [s-a0 s-a1 s-a2 s-a3]]
                               (let [p (.get-property s :id)
                                     v (slider/get-slider-value s)]
                                 (.set-param! ied p (* v @level-scale*))))
                             (.status! ied (format "Pitch Env Level scale = %s" @level-scale*)))

        msb-level-scale (msb/text-multistate-button (.tool-root drw)
                                                    (penv-pos :level-scale)
                                                    level-scale-states
                                                    :id :penv-level-scale-factor
                                                    :click-action level-scale-action
                                                    :rim-color (lnf/button-border)
                                                    :gap 6)

        b-init (sf/init-button drw (penv-pos :init) :penv-init-values
                               (fn [& _]
                                 (doseq [s [s-a0 s-a1 s-a2 s-a3]]
                                   (let [p (.get-property s :id)]
                                     (.set-param! ied p 0.0)
                                     (slider/set-slider-value! s 0 false)))
                                 (.render drw)
                                 (.status! ied "Reset Pitch envelope levels to 0")))]
    (fn [dmap]
      (let [abs math/abs
            lev0 (a0 dmap)
            lev1 (a1 dmap)
            lev2 (a2 dmap)
            lev3 (a3 dmap)
            max-level (max (abs lev0)(abs lev1)(abs lev2)(abs lev3))
            [lev-scale lev-scale-index](cond (<= max-level 1/100) [0.01 2]
                                             (<= max-level 1/10) [0.1 1]
                                             :default [1.0 0])
            tm1 (t1 dmap)
            tm2 (t2 dmap)
            tm3 (t3 dmap)
            max-time (max tm1 tm2 tm3)
            [tm-scale tm-scale-index] (cond (<= max-time 1) [1.0 0]
                                            (<= max-time 4) [4.0 1]
                                            (<= max-time 16) [16.0 2]
                                            (<= max-time 64) [64.0 3]
                                            :default [256.0 4])]
        (reset! level-scale* lev-scale)
        (slider/set-slider-value! s-a0 (/ lev0 lev-scale) false)
        (slider/set-slider-value! s-a1 (/ lev1 lev-scale) false)
        (slider/set-slider-value! s-a2 (/ lev2 lev-scale) false)
        (slider/set-slider-value! s-a3 (/ lev3 lev-scale) false)
        (msb/set-multistate-button-state! msb-level-scale lev-scale-index false)
        (reset! time-scale* tm-scale)
        (slider/set-slider-value! s-t1 (/ tm1 tm-scale) false)
        (slider/set-slider-value! s-t2 (/ tm2 tm-scale) false)
        (slider/set-slider-value! s-t3 (/ tm3 tm-scale) false)
        (msb/set-multistate-button-state! msb-time-scale tm-scale-index false)
        (slider/set-slider-value! s-cc9 (cc9 dmap) false)))))    
