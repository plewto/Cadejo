(ns cadejo.instruments.alias.editor.osc-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def width 1160)
(def height 500)

(declare osc-panel)
(declare freq-panel)
(declare port-panel)
(declare fm-panel)
(declare wave-panel)
(declare amp-panel)

(defn osc-editor [n ied]
  (let [drw (sfactory/sgwr-drawing width height)
        x0 32
        y0 (- height 32)
        x1 (- width 32)
        y1 32
        port-pan (port-panel n drw [x0 y0] ied)
        freq-pan (freq-panel n drw [(+ x0 130)(- y0 344)] ied)
        fm-pan (fm-panel n drw [(+ x0 130)(- y0 26)] ied)
        wave-pan (wave-panel n drw [(+ x0 388)(- y0 26)] ied)
        amp-pan (amp-panel n drw [(+ x0 636)(- y0 26)] ied)
        pan-main (ss/scrollable (ss/vertical-panel :items [(.canvas drw)] ))
        widget-map {:drawing drw
                    :pan-main pan-main}
        osced (reify subedit/InstrumentSubEditor
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
                    (port-pan dmap)
                    (freq-pan dmap)
                    (fm-pan dmap)
                    (wave-pan dmap)
                    (amp-pan dmap)
                    (.render drw))))]
    (sfactory/major-border drw [x0 y0][x1 y1])
    (sfactory/title drw [(+ x0 30)(- y0 398)] (format "OSC %d" n) :size 12)
    (image/read-image (.root drw) [(+ x0 40)(- y0 390)]
                      (format "resources/alias/osc%d_wave.png" n))
    osced))


(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- msb-state! [b n]
  (msb/set-multistate-button-state! b n false))


(defn- port-panel [n drw p0 ied]
  (if (= n 1)
    (let [[x0 y0] p0
          x1 (+ x0 26)
          x2 (+ x1 97)
          y1 (- y0 25)
          y2 (- y1 311)
          x-slider (+ x1 50)
          y-slider (- y1 111)
          x-title (+ x1 10)
          y-title (+ y2 20)
          param :port-time
          action (fn [s _]
                   (let [v (slider-value s)]
                     (.set-param! ied param v)))
          s-port (sfactory/vslider drw ied param [x-slider y-slider] 0.0 constants/max-port-time action)
          sync-fn (fn [dmap] 
                    (let [v (param dmap)]
                      (slider-value! s-port v)))]
      (sfactory/minor-border drw [x1 y1][x2 y2])
      (sfactory/text drw [x-title y-title] "Port")
      (sfactory/minor-ticks drw x-slider y-slider (- y-slider sfactory/slider-length) 10)
      sync-fn)
    (let [dummy-sync-fn (fn [_])]
      dummy-sync-fn)))

(defn freq-panel [n drw p1 ied]
  (sfactory/fpo drw p1 :style [:dot :bar :dash] :color :green)
  (let [[x1 y1] p1
        x-freq (+ x1 145)
        x-freq-label (- x-freq 50)
        x-freq-edit (+ x-freq 180)
        x-bias (+ x-freq 300)
        x-bias-label (- x-bias 50)
        x-bias-edit (+ x-bias 180)
        y-freq (- y1 48)
        y-bias y-freq
        y-labels (- y1 (if (= (lnf/dbar-style) :matrix) 30 26))
        y-edit (- y-labels 20)
        param-freq (keyword (format "osc%d-detune" n))
        param-bias (keyword (format "osc%d-bias" n))
        dbar-freq (sfactory/displaybar drw [x-freq y-freq] 7)
        dbar-bias (sfactory/displaybar drw [x-bias y-bias] 7)
        edit-freq-action (fn [& _]
                           (dbar/displaybar-dialog dbar-freq
                                                   (format "Osc %d Frequency" n)
                                                   :validator (fn [q]
                                                                (let [f (math/str->float q)]
                                                                  (and f (>= f 0))))
                                                   :callback (fn [_]
                                                               (let [s (.current-display dbar-freq)
                                                                     f (math/str->float s)]
                                                                 (.set-param! ied param-freq f)))))
        edit-bias-action (fn [& _]
                           (dbar/displaybar-dialog dbar-bias
                                                   (format "Osc %d Bias" n)
                                                   :validator (fn [q]
                                                                (let [f (math/str->float q)]
                                                                  f))
                                                   :callback (fn [_]
                                                               (let [s (.current-display dbar-bias)
                                                                     b (math/str->float s)]
                                                                 (.set-param! ied param-bias b)))))
        b-edit-freq (sfactory/mini-edit-button drw [x-freq-edit y-edit] :freq edit-freq-action)
        b-edit-bias (sfactory/mini-edit-button drw [x-bias-edit y-edit] :bias edit-bias-action)
        sync-fn (fn [dmap]
                  (let [b (param-bias dmap)
                        f (param-freq dmap)
                        sb (format "%+1.4f" b)
                        sf (format "%2.4f" f)]
                    (.display! dbar-bias sb false)
                    (.display! dbar-freq sf false))) ]
    (sfactory/text drw [x-freq-label y-labels] "Freq")
    (sfactory/text drw [x-bias-label y-labels] "Bias")
    sync-fn))

; ---------------------------------------------------------------------- 
;                                    FM

(defn- fm-panel [n drw p1 ied]
  (let [[x1 y1] p1
        x2 (+ x1 251)
        x-title (+ x1 10)
        x-depth1 (+ x1 80)
        x-lag1 (+ x-depth1 30)
        x-depth2 (+ x-lag1 80)
        x-lag2 (+ x-depth2 30)
        x-label-offset -16
        x-src1 (+ x1 73)
        x-src2 (+ x1 183)
        y2 (- y1 311)
        y-title (+ y2 20)
        y-sliders (- y1 111)
        y-sliders2 (- y-sliders sfactory/slider-length)
        y-labels (+ y-sliders 22)
        param-source1 (keyword (format "osc%d-fm1-source" n))
        param-depth1  (keyword (format "osc%d-fm1-depth" n))
        param-lag1    (keyword (format "osc%d-fm1-lag" n))
        param-source2 (keyword (format "osc%d-fm2-source" n))
        param-depth2  (keyword (format "osc%d-fm2-depth" n))
        param-lag2    (keyword (format "osc%d-fm2-lag" n))
        slider-action (fn [s _] 
                        (let [param (.get-property s :id)
                              val (slider-value s)]
                          (.set-param! ied param val)))
        mlt constants/max-lag-time
        s-depth1 (sfactory/vslider drw ied param-depth1 [x-depth1 y-sliders] -1.0 1.0 slider-action)
        s-lag1   (sfactory/vslider drw ied param-lag1 [x-lag1 y-sliders] 0.0 mlt slider-action)
        s-depth2 (sfactory/vslider drw ied param-depth2 [x-depth2 y-sliders] -1.0 1.0 slider-action)
        s-lag2   (sfactory/vslider drw ied param-lag2 [x-lag2 y-sliders] 0.0 mlt slider-action)
        b-source1 (matrix/source-button drw ied param-source1 [x-src1 (- y1 75)])
        b-source2 (matrix/source-button drw ied param-source2 [x-src2 (- y1 75)])
        sync-fn (fn [dmap] 
                  (let [s1 (param-source1 dmap)
                        d1 (param-depth1 dmap)
                        l1 (param-lag1 dmap)
                        s2 (param-source2 dmap)
                        d2 (param-depth2 dmap)
                        l2 (param-lag2 dmap)]
                    (slider-value! s-depth1 d1)
                    (slider-value! s-lag1 l1)
                    (slider-value! s-depth2 d2)
                    (slider-value! s-lag2 l2)
                    (msb-state! b-source1 (int s1))
                    (msb-state! b-source2 (int s2))))]
    (sfactory/fpo drw p1 :color :red)
    (sfactory/minor-border drw [x1 y1][x2 y2])
    (sfactory/text drw [x-title y-title] "FM")
    (sfactory/minor-ticks drw x-lag1 y-sliders y-sliders2 10)
    (sfactory/minor-ticks drw x-lag2 y-sliders y-sliders2 10)
    (doseq [x [x-depth1 x-depth2]]
      (sfactory/minor-ticks drw x y-sliders y-sliders2 16)
      (let [val* (atom -1.0)
            delta-v 0.50
            y* (atom y-sliders)
            delta-y (/ sfactory/slider-length 4.0)]
        (while (<= @val* 1.0)
          (sfactory/major-tick drw x @y* (format "%+5.2f" @val*))
          (swap! val* (fn [q](+ q delta-v)))
          (swap! y* (fn [q](- q delta-y))))))
    (sfactory/label drw [(+ x-depth1 x-label-offset) y-labels] "DPTH")
    (sfactory/label drw [(+ x-lag1 x-label-offset 8) y-labels] "LAG")
    (sfactory/label drw [(+ x-depth2 x-label-offset) y-labels] "DPTH")
    (sfactory/label drw [(+ x-lag2 x-label-offset 8) y-labels] "LAG")
    (sfactory/label drw [(+ x-src1 5)(- y1 15)] "BUS 1")
    (sfactory/label drw [(+ x-src2 5)(- y1 15)] "BUS 2")
    sync-fn))


; ---------------------------------------------------------------------- 
;                                   Wave

(defn- wave-panel [n drw p1 ied]
  (sfactory/fpo drw p1 :color :green)
  (let [[x1 y1] p1
        w 240
        h 311
        x2 (+ x1 w)
        x-title (+ x1 10)
        x-wave (+ x1 60)
        x-depth1 (+ x-wave 80)
        x-depth2 (+ x-depth1 60)
        x-src1 (- x-depth1 20)
        x-src2 (- x-depth2 20)
        y2 (- y1 h)
        y-sliders (- y1 111)
        y-sliders2 (- y-sliders sfactory/slider-length)
        y-title (+ y2 20)
        y-labels (+ y-sliders 22)
        param-wave (keyword (format "osc%d-wave" n))
        param-source1 (keyword (format "osc%d-wave1-source" n))
        param-depth1 (keyword (format "osc%d-wave1-depth" n))
        param-source2 (keyword (format "osc%d-wave2-source" n))
        param-depth2 (keyword (format "osc%d-wave2-depth" n))
        min-val (if (= n 1) 1.0 0.0)
        max-val (get {1 4.0, 2 1.0, 3 4.0} n)
        action (fn [s _] 
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-wave (sfactory/vslider drw ied param-wave [x-wave y-sliders] min-val max-val action)
        s-depth1 (sfactory/vslider drw ied param-depth1 [x-depth1 y-sliders] (- max-val) max-val action)
        s-depth2 (sfactory/vslider drw ied param-depth2 [x-depth2 y-sliders] (- max-val) max-val action)
        b-source1 (matrix/source-button drw ied param-source1 [x-src1 (- y1 75)])
        b-source2 (matrix/source-button drw ied param-source1 [x-src2 (- y1 75)])
        sync-fn (fn [dmap]
                  (let [w (param-wave dmap)
                        d1 (param-depth1 dmap)
                        d2 (param-depth2 dmap)
                        s1 (param-source1 dmap)
                        s2 (param-source2 dmap)]
                    (slider-value! s-wave w)
                    (slider-value! s-depth1 d1)
                    (slider-value! s-depth2 d2)
                    (msb-state! b-source1 s1)
                    (msb-state! b-source2 s2)))]
    (sfactory/minor-border drw p1 [x2 y2])
    (sfactory/text drw [x-title y-title] "Wave")
    (sfactory/label drw [(- x-wave 12) y-labels] "WAVE")
    (sfactory/label drw [(- x-depth1 14) y-labels] "DPTH")
    (sfactory/label drw [(- x-depth2 14) y-labels] "DPTH")
    (sfactory/label drw [(+ x-src1 5)(- y1 15)] "BUS 1")
    (sfactory/label drw [(+ x-src2 5)(- y1 15)] "BUS 2")
    (sfactory/minor-ticks drw x-wave y-sliders (- y-sliders sfactory/slider-length) 10)
    (sfactory/major-tick drw x-wave y-sliders (str min-val) [-45 5])
    (sfactory/major-tick drw x-wave y-sliders2 (str max-val) [-45 5])
    (sfactory/major-tick drw x-wave (math/mean y-sliders y-sliders2) "")
    (let [val* (atom -1.0)
          delta-v 0.50
          y* (atom y-sliders)
          delta-y (/ sfactory/slider-length 4)
          xt1 (- x-depth1 8)
          xt2 (+ x-depth2 8)]
      (while (<= @val* 1.0)
        (sfactory/hrule drw xt1 xt2 @y* :style :dotted)
        (sfactory/label drw [(- xt1 45)(+ @y* 5)] (format "%+5.2f" @val*))
        (swap! val* (fn [q](+ q delta-v)))
        (swap! y* (fn [q](- q delta-y)))))
    sync-fn))

; ---------------------------------------------------------------------- 
;                                    Amp

(defn- amp-panel [n drw p1 ied]
  (sfactory/fpo drw p1 :color :yellow)
  (let [[x1 y1] p1
        w 440 ; FPO
        h 311
        x2 (+ x1 w)
        x-title (+ x1 10)
        x-amp (+ x1 60)
        x-depth1 (+ x-amp 80)
        x-lag1 (+ x-depth1 30)
        x-depth2 (+ x-lag1 80)
        x-lag2 (+ x-depth2 30)
        x-pan (+ x-lag2 80)
        x-src1 (+ x-depth1 0)
        x-src2 (+ x-depth2 0)
        y2 (- y1 h)
        y-sliders (- y1 111)
        y-sliders2 (- y-sliders sfactory/slider-length)
        y-labels (+ y-sliders 22)
        y-title (+ y2 20)
        y-src (- y1 75)
        param-amp (keyword (format "osc%d-amp" n))
        param-src1 (keyword (format "osc%d-amp1-src" n))
        param-depth1 (keyword (format "osc%d-amp1-depth" n))
        param-lag1 (keyword (format "osc%d-amp1-lag" n))
        param-src2 (keyword (format "osc%d-amp2-src" n))
        param-depth2 (keyword (format "osc%d-amp2-depth" n))
        param-lag2 (keyword (format "osc%d-amp2-lag" n))
        param-pan (keyword (format "osc%d-pan" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        s-amp (sfactory/vslider drw ied param-amp [x-amp y-sliders ] 
                                constants/min-amp-db constants/max-amp-db action
                                :value-hook int)
        s-depth1 (sfactory/vslider drw ied param-depth1 [x-depth1 y-sliders] -1.0 1.0 action)
        s-lag1 (sfactory/vslider drw ied param-lag1 [x-lag1 y-sliders] 0.0 constants/max-lag-time action)
        s-depth2 (sfactory/vslider drw ied param-depth2 [x-depth2 y-sliders] -1.0 1.0 action)
        s-lag2 (sfactory/vslider drw ied param-lag2 [x-lag2 y-sliders] 0.0 constants/max-lag-time action)
        s-pan  (sfactory/vslider drw ied param-pan [x-pan y-sliders] -1.0 1.0 action)
        b-src1 (matrix/source-button drw ied param-src1 [x-src1 y-src])
        b-src2 (matrix/source-button drw ied param-src2 [x-src2 y-src])
        sync-fn (fn [dmap] 
                  (let [a (param-amp dmap)
                        s1 (param-src1 dmap)
                        d1 (param-depth1 dmap)
                        l1 (param-lag1 dmap)
                        s2 (param-src2 dmap)
                        d2 (param-depth2 dmap)
                        l2 (param-lag2 dmap)
                        pan (param-pan dmap)]
                    (slider-value! s-amp a)
                    (slider-value! s-depth1 d1)
                    (slider-value! s-lag1 l1)
                    (slider-value! s-depth2 d2)
                    (slider-value! s-lag2 l2)
                    (slider-value! s-pan pan)
                    (msb-state! b-src1 s1)
                    (msb-state! b-src2 s2)))]
    (sfactory/minor-border drw p1 [x2 y2])
    (sfactory/text drw [x-title y-title] "Amp")
    (sfactory/label drw [(- x-amp 25) y-labels] "AMP(db)")
    (sfactory/label drw [(- x-depth1 12) y-labels] "DPTH")
    (sfactory/label drw [(- x-depth2 12) y-labels] "DPTH")
    (sfactory/label drw [(- x-lag1 8) y-labels] "LAG")
    (sfactory/label drw [(- x-lag2 8) y-labels] "LAG")
    (sfactory/label drw [(- x-pan 10) y-labels] "PAN")
    (sfactory/fpo drw [x-src1 y-src] :color :yellow)
    (sfactory/label drw [(+ x-src1 5)(+ y-src 60)] "BUS 1")
    (sfactory/label drw [(+ x-src2 5)(+ y-src 60)] "BUS 2")
    ;; amp ticks
    (let [delta-v 6
          count (/ (- constants/max-amp-db constants/min-amp-db) delta-v)
          delta-y (/ sfactory/slider-length count)
          val* (atom constants/min-amp-db)
          y* (atom y-sliders)]
      (while (<= @val* constants/max-amp-db)
        (sfactory/major-tick drw x-amp @y* (format "%+3d" @val*) [-50 5])
        (swap! val* (fn [q](+ q delta-v)))
        (swap! y* (fn [q](- q delta-y)))))
    ;; lag ticks
    (sfactory/minor-ticks drw x-lag1 y-sliders y-sliders2 10)
    (sfactory/minor-ticks drw x-lag2 y-sliders y-sliders2 10)
    ;; Signed ticks
    (doseq [x [x-depth1 x-depth2 x-pan]]
      (sfactory/minor-ticks drw x y-sliders y-sliders2 16)
      (let [delta-v 0.50
            delta-y (/ sfactory/slider-length 4.0)
            val* (atom -1.0)
            y* (atom y-sliders)]
        (while (<= @val* 1.0)
          (sfactory/major-tick drw x @y* (format "%+5.2f" @val*))
          (swap! val* (fn [q](+ q delta-v)))
          (swap! y* (fn [q](- q delta-y))))))
    (sfactory/label drw [(+ x-pan 15)(+ y-sliders2 5)] "Filter 1")
    (sfactory/label drw [(+ x-pan 15)(+ y-sliders  5)] "Filter 2")
    sync-fn))
