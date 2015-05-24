(ns cadejo.instruments.cobalt.editor.buzz-editor
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.instruments.cobalt.editor.overview :as overview])
  (:require [cadejo.instruments.cobalt.editor.op-freq-panel :as ofp])
  (:require [cadejo.instruments.cobalt.editor.op-amp-panel :as oap])
  (:require [cadejo.instruments.cobalt.editor.op-env-panel :as env])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 1300)
(def ^:private height 600)
(def ^:private bottom-margin 30)
(def ^:private left-margin 30)

(declare draw-background)
(declare draw-harmonic-panel)
(declare vertical-pos)
(declare harmonic-panel)

(defn buzz-editor [ied]
  (let [op :bzz
        p0 [0 height]
        [x0 y0] p0
        drw (let [d (sf/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        freq-panel (ofp/freq-panel op drw ied p0)
        amp-panel (oap/amp-panel op drw ied p0)
        harm-panel (harmonic-panel drw ied p0)
        env-panel (env/env-panel op drw ied p0)
        overview-panel (let [xv left-margin
                             yv (vertical-pos :overview p0)]
                         (overview/observer drw [xv yv] ied))
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
                 (freq-panel dmap)
                 (amp-panel dmap)
                 (env-panel dmap)
                 (harm-panel dmap)
                 (.sync-ui! overview-panel)
                 (.render drw))))]
    ed))

(defn- vertical-pos [item p0]
  (let [y0 (second p0)
        y-overview (- y0 bottom-margin)
        y-border (- y0 height)
        y-title (+ y-border 30)
        map {:y0 y0
             :overview y-overview
             :title y-title
             :border y-border}]
  (get map item)))

(defn- draw-background [ddrw p0]
  (let [op :bzz
        bg (sf/sgwr-drawing width height)
        [x0 y0] p0
        x-border (+ x0 width)
        y-border (vertical-pos :border p0)
        x-title (+ x0 left-margin)
        y-title (vertical-pos :title p0)]
    (draw-harmonic-panel bg p0)
    (ofp/draw-freq-panel op bg p0)
    (oap/draw-amp-panel op bg p0)
    (env/draw-env-panel op bg p0)
    (sf/title bg [x-title y-title] (format "Buzz" op) :size 12)
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-omage)]
      (.put-property! iobj :image (.image bg))
      iobj)))

(defn-harm-pos [item p0]
  (let [pan-width 350
        pan-height 360
        x-offset (+ left-margin 410)
        y-offset 190
        slider-space 50
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        x-harmonics (+ x0 75)
        x-env (+ x-harmonics (* 1.5 slider-space))
        x-cca (+ x-env slider-space)
        x-harmonics-label (+ x0 100)
        x-hp-label (+ x0 240)
        x-hp (+ x-cca (* 1.25 slider-space))
        x-hp-env (+ x-hp (* 1.25 slider-space))
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-slider2 (+ y-border 334)
        y-harmonics-label (+ y-border 160)]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :harmonics [x-harmonics y-slider2]
          :cca [x-cca y-slider2]
          :env [x-env y-slider2]
          :hp [x-hp y-slider2]
          :hp-env [x-hp-env y-slider2]
          :harmonics-label [x-harmonics-label y-harmonics-label]
          :hp-label [x-hp-label y-harmonics-label]
          } item)))

  (defn- draw-harmonic-panel [bg p0]
  (sf/minor-border bg (harm-pos :p0 p0)(harm-pos :border p0))
  (sf/label bg (harm-pos :harmonics p0) "N" :offset [-4 20])
  (sf/label bg (harm-pos :env p0) "Env" :offset [-10 20])
  (sf/label bg (harm-pos :cca p0) "CCA" :offset [-10 20])
  (sf/label bg (harm-pos :hp p0) "Track" :offset [-16 20])
  (sf/label bg (harm-pos :hp-env p0) "Env" :offset [-10 20])
  (sf/label bg (harm-pos :harmonics-label p0) "Harmonics" :size 8)
  (sf/label bg (harm-pos :hp-label p0) "HighPass" :size 8)
  (let [[xc y1](harm-pos :harmonics p0)
        x-hp (first (harm-pos :hp p0))
        y2 (- y1 sf/slider-length)]
    (sf/major-tick-marks bg xc y1 y2 
                         :v0 0
                         :v1 con/max-buzz-harmonics
                         :step 8 
                         :frmt "%3d")
    (doseq [x (map first [(harm-pos :env p0)(harm-pos :cca p0)])]
      (sf/major-tick-marks bg x y1 y2
                           :v0 (- con/max-buzz-harmonics)
                           :v1 con/max-buzz-harmonics
                           :step 32
                           :frmt (if (= x (first (harm-pos :env p0))) "%+3d" "")
                           :font-size 6.0))
    (sf/major-tick-marks bg x-hp y1 y2
                        :v0 0 :v1 32
                        :step 4
                        :frmt "%3d")
    (sf/major-tick-marks bg (first (harm-pos :hp-env p0)) y1 y2
                         :v0 -32 :v1 32
                         :step 8
                         :frmt "%+3d")))

(defn-harmonic-panel [drw ied p0]
  (let [param-harmonics :bzz-harmonics
        param-env :bzz-harmonics<-env
        param-cca :bzz-harmonics<-cca
        param-hp :bzz-hp-track
        param-hp-env :bzz-hp-track<-env
        slider-action (fn [s _]
                        (let [p (.get-property s :id)
                              v (slider/get-slider-value s)]
                          (.set-param! ied p (float v))))
        s-harmonics (sf/vslider drw ied param-harmonics
                                (harm-pos :harmonics p0)
                                con/min-buzz-harmonics
                                con/max-buzz-harmonics
                                slider-action
                                :value-hook int)
        s-env (sf/vslider drw ied param-env (harm-pos :env p0) -64 64 slider-action :value-hook int)
        s-cca (sf/vslider drw ied param-cca (harm-pos :cca p0) -64 64 slider-action :value-hook int)
        s-hp  (sf/vslider drw ied param-hp  (harm-pos :hp p0)   1 32 slider-action :value-hook int)
        s-hp-env (sf/vslider drw ied param-hp-env (harm-pos :hp-env p0) -32 32 slider-action :value-hook int)]
    (fn [dmap]
      (let [n (param-harmonics dmap)
            env (param-env dmap)
            cca (param-cca dmap)
            hp (param-hp dmap)
            hp-env (param-hp-env dmap)]
        (slider/set-slider-value! s-harmonics (int n) false)
        (slider/set-slider-value! s-env (int env) false)
        (slider/set-slider-value! s-cca (int cca) false)
        (slider/set-slider-value! s-hp (int hp) false)
        (slider/set-slider-value! s-hp-env (int hp-env) false)))))
