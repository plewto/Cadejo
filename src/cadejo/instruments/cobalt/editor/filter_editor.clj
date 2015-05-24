(ns cadejo.instruments.cobalt.editor.filter-editor
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
(def ^:private height 600)
(def ^:private bottom-margin 30)
(def ^:private left-margin 30)
(def ^:private scale-states [[:1 "  1" :green]
                             [:4 "  4" :green]
                             [:16 " 16" :green]
                             [:64  " 64" :green]
                             [:256 "256" :green]])
(declare draw-background)
(declare filter-pos)

(defn filter-editor [ied]
  (let [param-freq :filter-freq
        param-track :filter-track
        param-env :filter<-env
        param-prss :filter<-pressure
        param-cca :filter<-cca
        param-ccb :filter<-ccb
        param-res :filter-res
        param-res-cca :filter-res<-cca
        param-res-ccb :filter-res<-ccb
        param-mode :filter-mode
        param-freq2 :filter2-detune
        param-lag :filter2-lag
        param-attack :filter-attack
        param-decay :filter-decay
        param-sustain :filter-sustain
        param-release :filter-release
        param-dist-mix :dist-mix
        param-dist-gain :dist-pregain
        param-dist-cca :dist<-cca
        param-dist-ccb :dist<-ccb
        drw (let [d (sf/sgwr-drawing width height)]
              (draw-background d)
              d)
        dbar-freq (sf/displaybar drw (filter-pos :freq) 5)
        edit-freq-action (fn [& _] 
                           (let [prompt "Filter Freqeuncy (int 1 < f < 12000)"]
                             (dbar/displaybar-dialog 
                              dbar-freq prompt
                              :validator (fn [q]
                                           (let [f (math/str->int q)]
                                             (and f (>= f con/min-filter-cutoff)
                                                  (<= f con/max-filter-cutoff))))
                              :callback (fn [_]
                                          (let [f (math/str->int (.current-display dbar-freq))]
                                            (.set-param! ied param-freq f))))))
        b-edit-freq (sf/mini-edit-button drw (filter-pos :freq-edit) param-freq edit-freq-action)

        slider-action (fn [s _]
                        (let [param (.get-property s :id)
                              val (slider/get-slider-value s)]
                          (.set-param! ied param val)))
        s-track (sf/vslider drw ied param-track (filter-pos :track) 
                            con/min-filter-track 
                            con/max-filter-track slider-action
                            :value-hook int)
        s-env (sf/vslider drw ied param-env (filter-pos :env) -1.0 1.0 slider-action)
        s-prss (sf/vslider drw ied param-prss (filter-pos :prss) -1.0 1.0 slider-action)
        s-cca (sf/vslider drw ied param-cca (filter-pos :cca) -1.0 1.0 slider-action)
        s-ccb (sf/vslider drw ied param-ccb (filter-pos :ccb) -1.0 1.0 slider-action)
        s-res (sf/vslider drw ied param-res (filter-pos :res) 0.0 1.0 slider-action)
        s-res-cca (sf/vslider drw ied param-res-cca (filter-pos :res-cca) -1.0 1.0 slider-action)
        s-res-ccb (sf/vslider drw ied param-res-ccb (filter-pos :res-ccb) -1.0 1.0 slider-action)
        s-freq2 (sf/vslider drw ied param-freq2 (filter-pos :freq2) 1.0 8.0 slider-action :value-hook int)
        s-lag (sf/vslider drw ied param-lag (filter-pos :lag) 0.0 1.0 slider-action)
        s-mode (sf/vslider drw ied param-mode (filter-pos :mode) -1.0 1.0 slider-action)
        scale* (atom 1.0)
        env-time-action (fn [s _]  
                     (let [param (.get-property s :id)
                           pos (slider/get-slider-value s)
                           scale @scale*
                           val (* scale pos)]
                       (.set-param! ied param val)))
        s-att (sf/vslider drw ied param-attack (filter-pos :att) 0.0 1.0 env-time-action)
        s-dcy (sf/vslider drw ied param-decay (filter-pos :dcy) 0.0 1.0 env-time-action)
        s-rel (sf/vslider drw ied param-release (filter-pos :rel) 0.0 1.0 env-time-action)
        s-sus (sf/vslider drw ied param-sustain (filter-pos :sus) 0.0 1.0 slider-action)
        scale-action (fn [b _]
                       (let [state (math/str->int (name (second (msb/current-multistate-button-state b))))]
                         (reset! scale* state))
                       (doseq [s [s-att s-dcy s-rel]]
                         (let [p (.get-property s :id)
                               v (slider/get-slider-value s)]
                           (.set-param! ied p (* @scale* v))))
                       (.status! ied (format "Filter Env time scale = %s" @scale*)))
        msb-scale (msb/text-multistate-button (.tool-root drw)
                                              (filter-pos :env-scale)
                                              scale-states
                                              :id :env-scale-factor
                                              :click-action scale-action
                                              :rim-color (lnf/button-border)
                                              :gap 6)
        s-dist-gain (sf/vslider drw ied param-dist-gain (filter-pos :dist-gain) 1 8 slider-action :value-hook int)
        s-dist-cca (sf/vslider drw ied param-dist-cca (filter-pos :dist-cca) 0 8 slider-action)
        s-dist-ccb (sf/vslider drw ied param-dist-ccb (filter-pos :dist-ccb) 0 8 slider-action)
        s-dist-mix (sf/vslider drw ied param-dist-mix (filter-pos :dist-mix) -1.0 1.0 slider-action)
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
               (let [dmap (.current-data (.bank (.parent-performance ied)))
                     freq (int (param-freq dmap))
                     track (int (param-track dmap))
                     ssi (fn [param s]
                           (let [v (int (param dmap))]
                             (slider/set-slider-value! s v false)))
                     ssf (fn [param s]
                           (let [v (float (param dmap))]
                             (slider/set-slider-value! s v false)))
                     att (float (param-attack dmap))
                     dcy (float (param-decay dmap))
                     rel (float (param-release dmap))
                     max-time (max att dcy rel)
                     [scale scale-index] (cond (<= max-time 1)[1.0 0]
                                               (<= max-time 4)[4.0 1]
                                               (<= max-time 16)[16.0 2]
                                               (<= max-time 64)[64.0 3]
                                               :default [256 4])]
                (.display! dbar-freq (format "%5d" (int freq)) false)
                (ssi param-track s-track)
                (ssf param-env s-env)
                (ssf param-prss s-prss)
                (ssf param-cca s-cca)
                (ssf param-ccb s-ccb)
                (ssf param-res s-res)
                (ssf param-res-cca s-res-cca)
                (ssf param-res-ccb s-res-ccb)
                (ssi param-freq2 s-freq2)
                (ssf param-lag s-lag)
                (ssf param-mode s-mode)
                (ssi param-dist-gain s-dist-gain)
                (ssi param-dist-cca s-dist-cca)
                (ssi param-dist-ccb s-dist-ccb)
                (ssf param-dist-mix s-dist-mix)
                (ssf param-sustain s-sus)
                (slider/set-slider-value! s-att (/ att scale) false)
                (slider/set-slider-value! s-dcy (/ dcy scale) false)
                (slider/set-slider-value! s-rel (/ rel scale) false)
                (msb/set-multistate-button-state! msb-scale scale-index false)
                (reset! scale* scale)
                (.render drw))))]
    ed))

(defn- filter-pos [item]
  (let [slider-space 50
        slider-space-wide 75
        x0 left-margin
        x-border (- width left-margin)
        x-title (+ x0 20)
        x-freq (+ x0 80)
        x-track (+ x0 70)
        x-env (+ x-track slider-space-wide)
        x-prss (+ x-env slider-space)
        x-cca (+ x-prss slider-space)
        x-ccb (+ x-cca slider-space)
        x-res (+ x-ccb slider-space-wide)
        x-res-cca (+ x-res slider-space-wide)
        x-res-ccb (+ x-res-cca slider-space)
        x-freq2 (+ x-res-ccb slider-space-wide)
        x-lag (+ x-freq2 slider-space-wide)
        x-mode (+ x-lag slider-space-wide)
        x-att (+ x-mode (* 2 slider-space))
        x-dcy (+ x-att slider-space)
        x-rel (+ x-dcy slider-space)
        x-sus (+ x-rel slider-space)
        x-scale (+ x-rel 30)
        x-gain (+ x-sus (* 2 slider-space))
        x-dist-cca (+ x-gain slider-space-wide)
        x-dist-ccb (+ x-dist-cca slider-space)
        x-dist-mix (+ x-dist-ccb slider-space-wide)
        y0 (- height bottom-margin)
        y-border bottom-margin
        y-title (+ y-border 20)
        y-freq (+ y-border 50)
        y-slider1 (+ y-freq 110)
        y-slider2 (+ y-slider1 sf/slider-length)
        y-scale (- y-slider1 40)
        y-sub-title (- y-slider1 50)
        ]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :title [x-title y-title]
          :freq [x-freq y-freq]
          :freq-edit [(+ x-freq 150) y-freq]
          :track [x-track y-slider2]
          :env [x-env y-slider2]
          :prss [x-prss y-slider2]
          :cca [x-cca y-slider2]
          :ccb [x-ccb y-slider2]
          :res [x-res y-slider2]
          :res-cca [x-res-cca y-slider2]
          :res-ccb [x-res-ccb y-slider2]
          :freq2 [x-freq2 y-slider2]
          :lag [x-lag y-slider2]
          :mode [x-mode y-slider2]
          :att [x-att y-slider2]
          :dcy [x-dcy y-slider2]
          :rel [x-rel y-slider2]
          :sus [x-sus y-slider2]
          :env-scale [x-scale y-scale]
          :dist-gain [x-gain y-slider2]
          :dist-cca [x-dist-cca y-slider2]
          :dist-ccb [x-dist-ccb y-slider2]
          :dist-mix [x-dist-mix y-slider2]
          :res-title [(- x-res 30) y-sub-title]
          :env-title [(- x-att 30) y-sub-title]
          :bp-title [(- x-freq2 30) y-sub-title]
          :dist-title [(- x-gain 30) y-sub-title]} item)))

(defn- draw-background [ddrw]
  (let [bg (sf/sgwr-drawing width height)]
    (sf/title bg (filter-pos :title) "Filter")
    (sf/label bg (filter-pos :freq) "Freq" :offset [-50 20])
    (sf/label bg (filter-pos :track) "Track" :offset [-15 20])
    (sf/label bg (filter-pos :env) "Env" :offset [-10 20])
    (sf/label bg (filter-pos :prss) "Prss" :offset [-10 20])
    (sf/label bg (filter-pos :cca) "CCA" :offset [-8 20])
    (sf/label bg (filter-pos :ccb) "CCB" :offset [-8 20])
    (sf/label bg (filter-pos :res) "Res" :offset [-8 20])
    (sf/label bg (filter-pos :res-cca) "CCA" :offset [-8 20])
    (sf/label bg (filter-pos :res-ccb) "CCB" :offset [-8 20])
    (sf/label bg (filter-pos :freq2) "Offset" :offset [-20 20])
    (sf/label bg (filter-pos :lag) "Lag" :offset [-8 20])
    (sf/label bg (filter-pos :mode) "Mix" :offset [-8 20])
    (sf/label bg (filter-pos :dist-gain) "Gain" :offset [-12 20])
    (sf/label bg (filter-pos :dist-cca) "CCA" :offset [-8 20])
    (sf/label bg (filter-pos :dist-ccb) "CCB" :offset [-8 20])
    (sf/label bg (filter-pos :dist-mix) "Mix" :offset [-8 20])
    (sf/label bg (filter-pos :att) "Att" :offset [-8 20])
    (sf/label bg (filter-pos :dcy) "Dcy" :offset [-8 20])
    (sf/label bg (filter-pos :rel) "Rel" :offset [-8 20])
    (sf/label bg (filter-pos :sus) "Sus" :offset [-8 20])
    (sf/label bg (filter-pos :env-scale) "Scale" :offset [-50 15])
    (sf/label bg (filter-pos :mode) "LP" :offset [15 5] :size 5.0)
    (sf/label bg (filter-pos :mode) "BP" :offset [15 -145] :size 5.0)
    
    ;; Draw section borders and labels
    (let [y0 (+ (second (filter-pos :track)) 35)
          y1 (- y0 310)
          xt (- (first (filter-pos :track)) 65)
          xr (+ xt 315)
          xb (+ xr 200)
          xe (+ xb 250)
          xd (+ xe 250)
          xx (+ xd 285)
          label (fn [x text]
                  (let [xlab (+ x 15)
                        ylab (+ y1 25)]
                    (sf/sub-title bg [xlab ylab] text)))]
      (sf/minor-border bg [xt y0][xr y1])
      (sf/minor-border bg [xr y0][xb y1])
      (sf/minor-border bg [xb y0][xe y1])
      (sf/minor-border bg [xe y0][xd y1])
      (sf/minor-border bg [xd y0][xx y1])
      (label xr "Resoance")
      (label xb "Band Pass")
      (label xe "Envelope")
      (label xd "Distortion"))
    ;; Signed ticks 
    (let [positions (map first [(filter-pos :env)(filter-pos :prss)(filter-pos :cca)(filter-pos :ccb)
                                (filter-pos :res-cca)(filter-pos :res-ccb)(filter-pos :mode)])
                                
          labels (map first [(filter-pos :env)(filter-pos :res-cca)(filter-pos :mode)
                             (filter-pos :dist-cca)])
          y1 (second (filter-pos :env))
          y2 (- y1 sf/slider-length)]
      (doseq [xc positions]
        (sf/minor-ticks bg xc y1 y2 10)
        (sf/major-tick-marks bg xc y1 y2 :v0 -1.0 :v1 1.0 :step 1.0
                             :frmt (if (col/member? xc labels) "%+4.1f" "")
                             :x-offset -35
                             :font-size 5.0)))
    ;; Unit ticks
    (let [positions (map first [(filter-pos :res)(filter-pos :lag)(filter-pos :att)
                                (filter-pos :dcy)(filter-pos :rel)
                                (filter-pos :sus)
                                (filter-pos :dist-mix)
                                (filter-pos :dist-cca)(filter-pos :dist-ccb)])
          labels (map first [(filter-pos :res)(filter-pos :lag)(filter-pos :att)(filter-pos :dist-mix)])
          y1 (second (filter-pos :env))
          y2 (- y1 sf/slider-length)]
      (doseq [xc positions]
        (sf/minor-ticks bg xc y1 y2 10)
        (sf/major-tick-marks bg xc y1 y2 :v0 0.0 :v1 1.0 :step 0.5
                                   :frmt (if (col/member? xc labels) "%3.1f" "")
                                   :x-offset -35
                                   :font-size 5.0)))
    ;; Track ticks
    (let [[xc y1](filter-pos :track)
          y2 (- y1 sf/slider-length)]
      (sf/major-tick-marks bg xc y1 y2 :v0 0 :v1 8 :step 1 :frmt "%d" :font-size 5.0 :x-offset -30))
    ;; Offset/gain ticks
    (let [[x1 y1](filter-pos :freq2)
          x2 (first (filter-pos :dist-gain))
          y2 (- y1 sf/slider-length)]
      (doseq [xc [x1 x2]]
        (sf/major-tick-marks bg xc y1 y2 :v0 1 :v1 8 :step 1 :frmt "%d" :font-size 5.0 :x-offset -30)))
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-omage)]
      (.put-property! iobj :image (.image bg))
      iobj)))
