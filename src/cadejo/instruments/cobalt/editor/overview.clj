(ns cadejo.instruments.cobalt.editor.overview
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.color-utilities :as cu])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.components.line :as line]))

(def ^:private c-op (lnf/text))
(def ^:private c-fm (cu/shift c-op 0.4))
(def ^:private mark-length 6)
(def ^:private min-fm-amp 0.0)
(def ^:private max-fm-amp 8.0)
(def ^:private width 150)
(def ^:private height 120)

;; op | {1 2 3 ... 6}
(defn op-observer [op drw p0 ied]
  (let [[x0 y0] p0
        x-name (+ x0 10)
        x-values (+ x0 50)
        x-mute (+ x-name 60)
        x-border (+ x0 width)
        x-amp (+ x0 110)
        x-amp-mark1 (- x-amp (* 1/2 mark-length))
        x-amp-mark2 (+ x-amp-mark1 mark-length)
        x-fm-amp (+ x-amp 25)
        x-fm-mark1 (- x-fm-amp (* 1/2 mark-length))
        x-fm-mark2 (+ x-fm-mark1 mark-length)
        y-border (- y0 height)
        y-name (+ y-border 20)
        y-mute (+ y-border 10)
        y-detune (+ y-name 20)
        y-fm (+ y-detune 20)
        y-bias (+ y-fm 20)
        y-amp1 (- y0 20)
        y-amp2 (- y-detune 10)
        param-enable (keyword (format "op%d-enable" op))
        param-detune (keyword (format "op%d-detune" op))
        param-amp (keyword (format "op%d-amp" op))
        param-fm-detune (keyword (format "fm%d-detune" op))
        param-fm-bias (keyword (format "fm%d-bias" op))
        param-fm-amp (keyword (format "fm%d-amp" op))
        amp-mark-fn (math/linear-function 0.0 y-amp1 1.0 y-amp2)
        fm-mark-fn (math/linear-function min-fm-amp y-amp1 max-fm-amp y-amp2)
        mute-action (fn [b _]
                      (let [selected? (msb/checkbox-selected? b)]
                        (.set-param! ied param-enable (if selected? 0 1))
                        (.sync-ui! ied)
                        (.status! ied (format "%s op%d" (if selected? "Mute" "Enable") op))))
        cb-mute (sf/checkbox drw [x-mute y-mute]
                               (keyword (format "op%d-enable" op))
                               "Mute"
                               mute-action)
        txt-detune (sf/text drw [x-values y-detune] "X.XXXX" :size 6.0)
        txt-fm-detune (sf/text drw [x-values y-fm] "X.XXXX" :size 6.0 :color c-fm)
        txt-bias (sf/text drw [x-values y-bias] "+XXXX" :size 6.0 :color c-fm)
        marker-amp (line/line (.root drw) [x-amp-mark1 y-amp1][x-amp-mark2 y-amp1] :color c-op)
        marker-fm (line/line (.root drw) [x-fm-mark1 y-amp1][x-fm-mark2 y-amp1] :color c-fm)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        mute? (zero? (param-enable dmap))
                        detune (param-detune dmap)
                        fm-detune (param-fm-detune dmap)
                        fm-bias (param-fm-bias dmap)
                        amp (math/clamp (param-amp dmap) 0 1)
                        fm-amp (math/clamp (param-fm-amp dmap) min-fm-amp max-fm-amp)
                        y-amp-mark (amp-mark-fn amp)
                        y-fm-mark (fm-mark-fn fm-amp)]
                    (msb/select-checkbox! cb-mute mute? false)
                    (.put-property! txt-detune :text (format "%7.4f" detune))
                    (.put-property! txt-fm-detune :text (format "%7.4f" fm-detune))
                    (.put-property! txt-bias :text (format "%+7.3f" fm-bias))
                    (.set-points! marker-amp [[x-amp-mark1 y-amp-mark][x-amp-mark2 y-amp-mark]])
                    (.set-points! marker-fm [[x-fm-mark1 y-fm-mark][x-fm-mark2 y-fm-mark]]) ))]
    (sf/text drw [x-name y-name] (format "OP%d" op))
    (sf/text drw [x-name y-detune] "Freq" :size 6.0)
    (sf/text drw [x-name y-fm] "FM" :size 6.0 :color c-fm)
    (sf/text drw [x-name y-bias] "Bias" :size 6.0 :color c-fm)
    (sf/text drw [(- x-amp 10)(+ y-amp1 10)] "Amp" :size 5)
    (sf/text drw [(- x-fm-amp 5)(+ y-amp1 10)] "FM" :size 5 :color c-fm)
    (line/line (.root drw) [x-amp y-amp1][x-amp y-amp2] :color c-op) 
    (line/line (.root drw) [x-fm-amp y-amp1][x-fm-amp y-amp2] :color c-fm)
    (sf/minor-border drw [x0 y0][x-border y-border])
    {:sync-fn sync-fn }))


(defn noise-observer [drw p0 ied]
  (let [[x0 y0] p0
        x-name (+ x0 10)
        x-values (+ x0 50)
        x-mute (+ x-name 60)
        x-border (+ x0 width)
        x-amp1 (+ x0 110)
        x-amp1-mark1 (- x-amp1 (* 1/2 mark-length))
        x-amp1-mark2 (+ x-amp1-mark1 mark-length)
        x-amp2 (+ x-amp1 25)
        x-amp2-mark1 (- x-amp2 (* 1/2 mark-length))
        x-amp2-mark2 (+ x-amp2-mark1 mark-length)
        y-border (- y0 height)
        y-name (+ y-border 20)
        y-mute (+ y-border 10)
        y-detune1 (+ y-name 20)
        y-bw1 (+ y-detune1 20)
        y-detune2 (+ y-bw1 20)
        y-bw2 (+ y-detune2 20)
        y-amp1 (- y0 20)
        y-amp2 (- y-detune1 10)
        param-enable :nse-enable
        param-detune1 :nse-detune
        param-bw1 :nse-bw
        param-amp1 :nse-amp
        param-detune2 :nse2-detune
        param-bw2 :nse2-bw
        param-amp2 :nse2-amp
        mute-action (fn [b _]
                      (let [selected? (msb/checkbox-selected? b)]
                        (.set-param! ied param-enable (if selected? 0 1))
                        (.sync-ui! ied)
                        (.status! ied (format "Noise %s" (if selected? "Mute" "Enabled")))))
        cb-mute (sf/checkbox drw [x-mute y-mute]
                             :noise-enable
                             "Mute"
                             mute-action)
        txt-detune1 (sf/text drw [x-values y-detune1] "X.XXXX" :size 6.0)
        txt-bw1 (sf/text drw [x-values y-bw1] "XXX" :size 6.0)
        txt-detune2 (sf/text drw [x-values y-detune2] "X.XXXX" :size 6.0 :color c-fm)
        txt-bw2 (sf/text drw [x-values y-bw2] "XXX" :size 6.0 :color c-fm)
        marker-amp1 (line/line (.root drw) [x-amp1-mark1 y-amp1][x-amp1-mark2 y-amp1] :color c-op)
        marker-amp2 (line/line (.root drw) [x-amp2-mark1 y-amp1][x-amp2-mark2 y-amp1] :color c-fm)
        amp-mark-fn (math/linear-function 0.0 y-amp1 1.0 y-amp2)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        mute? (zero? (param-enable dmap))
                        detune1 (param-detune1 dmap)
                        amp1 (math/clamp (param-amp1 dmap) 0.0 1.0)
                        bw1 (param-bw1 dmap)
                        detune2 (param-detune2 dmap)
                        amp2 (math/clamp (param-amp2 dmap) 0.0 1.0)
                        bw2 (param-bw2 dmap)
                        y-amp1 (amp-mark-fn amp1)
                        y-amp2 (amp-mark-fn amp2)]
                     (msb/select-checkbox! cb-mute mute? false)
                     (.put-property! txt-detune1 :text (format "%7.4f" detune1))
                     (.put-property! txt-bw1 :text (format "%4d" (int bw1)))
                     (.put-property! txt-detune2 :text (format "%7.4f" detune2))
                     (.put-property! txt-bw2 :text (format "%4d" (int bw2)))
                     (.set-points! marker-amp1 [[x-amp1-mark1 y-amp1][x-amp1-mark2 y-amp1]]) 
                     (.set-points! marker-amp2 [[x-amp2-mark1 y-amp2][x-amp2-mark2 y-amp2]])))] 
    (sf/text drw [x-name y-name] "Noise")
    (sf/text drw [x-name y-detune1] "Freq1" :size 6.0)
    (sf/text drw [x-name y-bw1] "BW1" :size 6.0)
    (sf/text drw [x-name y-detune2] "Freq2" :size 6.0 :color c-fm)
    (sf/text drw [x-name y-bw2] "BW2" :size 6.0 :color c-fm)
    (sf/text drw [(- x-amp1 3)(+ y-amp1 10)] "1" :color c-op :size 5)
    (sf/text drw [(- x-amp2 3)(+ y-amp1 10)] "2" :color c-fm :size 5)
    (line/line (.root drw) [x-amp1 y-amp1][x-amp1 y-amp2] :color c-op)
    (line/line (.root drw) [x-amp2 y-amp1][x-amp2 y-amp2] :color c-fm)
    (sf/minor-border drw [x0 y0][x-border y-border])
    {:sync-fn sync-fn}))

(defn buzz-observer [drw p0 ied]
  (let [[x0 y0] p0
        x-border (+ x0 width)
        x-name (+ x0 10)
        x-values (+ x0 50)
        x-mute (+ x-name 60)
        x-amp (+ x0 110)
        x-amp-mark1 (- x-amp (* 1/2 mark-length))
        x-amp-mark2 (+ x-amp-mark1 mark-length)
        y-border (- y0 height)
        y-name (+ y-border 20)
        y-mute (+ y-border 10)
        y-detune (+ y-name 20)
        y-harmonics (+ y-detune 20)
        y-hp (+ y-harmonics 20)
        y-amp1 (- y0 20)
        y-amp2 (- y-detune 10)
        param-enable :bzz-enable
        param-detune :bzz-detune
        param-harmonics :bzz-harmonics
        param-hp :bzz-hp-track
        param-amp :bzz-amp
        mute-action (fn [b _]
                      (let [selected? (msb/checkbox-selected? b)]
                        (.set-param! ied param-enable (if selected? 0 1))
                        (.sync-ui! ied)
                        (.status! ied (format "Buzz %s" (if selected? "Mute" "Enabled")))))
        cb-mute (sf/checkbox drw [x-mute y-mute] :buzz-enable "Mute" mute-action)
        txt-detune (sf/text drw [x-values y-detune] "X.XXXX" :size 6.0)
        txt-harmonics (sf/text drw [x-values y-harmonics] "XXX" :size 6.0)
        txt-hp (sf/text drw [x-values y-hp] "XXX" :size 6.0)
        marker-amp (line/line (.root drw) [x-amp-mark1 y-amp1][x-amp-mark2 y-amp1] :color c-op)
        amp-mark-fn (math/linear-function 0.0 y-amp1 1.0 y-amp2)
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        mute? (zero? (param-enable dmap))
                        detune (param-detune dmap)
                        harmonics (int (param-harmonics dmap))
                        hp (int (param-hp dmap))
                        amp (param-amp dmap)
                        y-amp (amp-mark-fn amp)]
                    (msb/select-checkbox! cb-mute mute? false)
                    (.put-property! txt-detune :text (format "%7.4f" detune))
                    (.put-property! txt-harmonics :text (format "%3d" harmonics))
                    (.put-property! txt-hp :text (format "%3d" hp))
                    (.set-points! marker-amp [[x-amp-mark1 y-amp][x-amp-mark2 y-amp]])))]
    (sf/text drw [x-name y-name] "Buzz")
    (sf/text drw [x-name y-detune] "Freq" :size 6.0)
    (sf/text drw [x-name y-harmonics] "N" :size 6.0)
    (sf/text drw [x-name y-hp] "HP" :size 6.0)
    (line/line (.root drw) [x-amp y-amp1][x-amp y-amp2] :color c-op) 
    (sf/text drw [(- x-amp 10)(+ y-amp1 10)] "Amp" :size 5.0)
    (sf/minor-border drw [x0 y0][x-border y-border])
    {:sync-fn sync-fn}))

(defn observer [drw p0 ied]
  (let [[x0 y0] p0
        sub-panels* (atom [])
        offset* (atom 0)]
    (doseq [op [1 2 3 4 5 6 :noise :buzz]]
      (let [x (+ x0 @offset*)
            obs (cond (= op :noise)
                      (noise-observer drw [x y0] ied)
                      (= op :buzz)
                      (buzz-observer drw [x y0] ied)
                      :default
                      (op-observer op drw [x y0] ied))]
        (swap! sub-panels* (fn [q](conj q obs)))
        (swap! offset* (fn [q](+ q width)))))
    (reify subedit/InstrumentSubEditor
      (widgets [this] {})
      (widget [this _] nil)
      (parent [this] ied)
      (parent! [this _] nil) ; ignore
      (status! [this msg](.status! ied msg))
      (warning! [this msg](.warning! ied msg))
      (set-param! [this param value](.set-param! ied param value))
      (init! [this] nil)
      (sync-ui! [this]
        (doseq [q @sub-panels*]
          (let [syncfn (:sync-fn q)]
            (syncfn)))))))
        
      
    
