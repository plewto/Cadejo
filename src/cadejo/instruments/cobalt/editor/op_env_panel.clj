(ns cadejo.instruments.cobalt.editor.op-env-panel
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.components.line :as line]))

(def ^:private left-margin 30)
(def ^:private min-time 0.0)
(def ^:private max-time 1.0)
(def ^:private slider-length 250)

(def ^:private scale-states [[:1 "  1" :green]
                             [:4 "  4" :green]
                             [:16 " 16" :green]
                             [:64  " 64" :green]
                             [:256 "256" :green]])


(def ^:private clipboard* (atom {:att 0.00
                                 :dcy1 0.00
                                 :dcy2 0.00
                                 :rel 0.00
                                 :pk 1.00
                                 :bp 1.00
                                 :sus 1.00}))

;; Copy op envelope to clipboard
;;
(defn- copy [op ied]
  (let [dmap (.current-data (.bank (.parent-performance ied)))
        prefix (cond (= op :xenv) "xenv"
                     :default (format "op%d" op))
        p-attack (keyword (format "%s-attack" prefix))
        p-decay1 (keyword (format "%s-decay1" prefix))
        p-decay2 (keyword (format "%s-decay2" prefix))
        p-release (keyword (format "%s-release" prefix))
        p-peak (keyword (format "%s-peak" prefix))
        p-breakpoint (keyword (format "%s-breakpoint" prefix))
        p-sustain (keyword (format "%s-sustain" prefix))]
    (reset! clipboard* {:att (p-attack dmap)
                        :dcy1 (p-decay1 dmap)
                        :dcy2 (p-decay2 dmap)
                        :rel (p-release dmap)
                        :pk (p-peak dmap)
                        :bp (p-breakpoint dmap)
                        :sus (p-sustain dmap)})
    (.status! ied (if (number? op)
                    (format "OP %d Envelope Copied to Clipboard" op)
                    "Effects Envelope copied to clipboard"))))

;; Paste clipboard to op envelope
;;
(defn- paste [op ied]
  (let [prefix (cond (= op :xenv) "xenv"
                     :default (format "op%d" op))
        p-attack (keyword (format "%s-attack" prefix))
        p-decay1 (keyword (format "%s-decay1" prefix))
        p-decay2 (keyword (format "%s-decay2" prefix))
        p-release (keyword (format "%s-release" prefix))
        p-peak (keyword (format "%s-peak" prefix))
        p-breakpoint (keyword (format "%s-breakpoint" prefix))
        p-sustain (keyword (format "%s-sustain" prefix))]
    (.set-param! ied p-attack (:att @clipboard*))
    (.set-param! ied p-decay1 (:dcy1 @clipboard*))
    (.set-param! ied p-decay2 (:dcy2 @clipboard*))
    (.set-param! ied p-release (:rel @clipboard*))
    (.set-param! ied p-peak (:pk @clipboard*))
    (.set-param! ied p-breakpoint (:bp @clipboard*))
    (.set-param! ied p-sustain (:sus @clipboard*))
    (.sync-ui! ied)
    (.status! ied (format "Clipboard copied to envelope %s" op))))
    
        
(defn- env-pos [op item p0]
  (let [pan-width 440
        pan-height 360
        [x-offset y-offset](cond (or (number? op)(= op :bzz)(= op :nse)) [(+ left-margin 760) 190]
                                 :default [0 0])
        x0 (+ (first p0) x-offset)
        x-border (+ x0 pan-width)
        slider-space 50
        x-attack (+ x0 70)
        x-decay1 (+ x-attack slider-space)
        x-decay2 (+ x-decay1 slider-space)
        x-release (+ x-decay2 slider-space)
        x-peak (+ x-release 70)
        x-breakpoint (+ x-peak slider-space)
        x-sustain (+ x-breakpoint slider-space)
        x-title (+ x0 20)
        x-scale (- x-release 25)
        x-copy x-peak
        x-paste x-breakpoint
        y0 (- (second p0) y-offset)
        y-border (- y0 pan-height)
        y-slider1 (+ y-border 80)
        y-slider2 (+ y-slider1 slider-length)
        y-title (+ y-border 30)
        y-scale (+ y-border 30)
        y-copy (- y-scale 10)
        y-paste y-copy]
    (get {:p0 [x0 y0]
          :border [x-border y-border]
          :attack [x-attack y-slider2]
          :decay1 [x-decay1 y-slider2]
          :decay2 [x-decay2 y-slider2]
          :release [x-release y-slider2]
          :peak [x-peak y-slider2]
          :breakpoint [x-breakpoint y-slider2]
          :sustain [x-sustain y-slider2]
          :title [x-title y-title]
          :scale [x-scale y-scale] 
          :copy [x-copy y-copy]
          :paste [x-paste y-paste]}
         item)))

(defn draw-env-panel [op bg p0]
  (sf/minor-border bg (env-pos op :p0 p0)(env-pos op :border p0))
  (sf/label bg (env-pos op :attack p0) "Att" :offset [-10 20])
  (sf/label bg (env-pos op :decay1 p0) "Dcy1" :offset [-12 20])
  (sf/label bg (env-pos op :decay2 p0) "Dcy2" :offset [-12 20])
  (sf/label bg (env-pos op :release p0) "Rel" :offset [-10 20])
  (sf/label bg (env-pos op :peak p0) "Peak" :offset [-13 20])
  (sf/label bg (env-pos op :breakpoint p0) "BP" :offset [-6 20])
  (sf/label bg (env-pos op :sustain p0) "Sus" :offset [-10 20])
  (sf/label bg (env-pos op :scale p0) "Scale" :offset [-45 15])
  (sf/sub-title bg (env-pos op :title p0) (if (= op "xenv") "XEnv" "Env"))
  (let [positions [(env-pos op :attack p0)(env-pos op :decay1 p0)(env-pos op :decay2 p0)(env-pos op :release p0)]
        y1 (second (first positions))
        y2 (- y1 slider-length)]
    (doseq [x (map first positions)]
      (sf/major-tick-marks bg x y1 y2 :v0 min-time :v1 max-time
                          :frmt (if (= x (first (first positions))) "%4.2f" "")
                          :step 0.25)
      (sf/minor-ticks bg x y1 y2 20)))
  (let [positions [(env-pos op :peak p0)(env-pos op :breakpoint p0)(env-pos op :sustain p0)]
        y1 (second (first positions))
        y2 (- y1 slider-length)]
    (doseq [x (map first positions)]
      (sf/major-tick-marks bg x y1 y2 :v0 0.0 :v1 1.0
                          :frmt (if (= x (first (first positions))) "%4.2f" "")
                          :step 0.25)
      (sf/minor-ticks bg x y1 y2 20))))

(defn- param [op suffix]
  (let [prefix (if (integer? op)
                 (format "op%d" op)
                 (format "%s" (name op)))]
    (keyword (format "%s-%s" prefix suffix))))

(defn env-panel [op drw ied p0]
  (let [param-attack (param op "attack")
        param-decay1 (param op "decay1")
        param-decay2 (param op "decay2")
        param-release (param op "release")
        param-peak (param op "peak")
        param-breakpoint (param op "breakpoint")
        param-sustain (param op "sustain")
        scale* (atom 1.0)
        time-action (fn [s _] 
                      (let [p (.get-property s :id)
                            v (* @scale* (slider/get-slider-value s))]
                        (.set-param! ied p v)))
        s-attack (sf/vslider drw ied param-attack (env-pos op :attack p0) min-time max-time time-action :length slider-length)
        s-decay1 (sf/vslider drw ied param-decay1 (env-pos op :decay1 p0) min-time max-time time-action :length slider-length)
        s-decay2 (sf/vslider drw ied param-decay2 (env-pos op :decay2 p0) min-time max-time time-action :length slider-length)
        s-release (sf/vslider drw ied param-release (env-pos op :release p0) min-time max-time time-action :length slider-length)

        level-action (fn [s _] 
                       (let [p (.get-property s :id)
                             v (slider/get-slider-value s)]
                         (.set-param! ied p v)))
        s-peak (sf/vslider drw ied param-peak (env-pos op :peak p0) 0.0 1.0 level-action :length slider-length)
        s-breakpoint (sf/vslider drw ied param-breakpoint (env-pos op :breakpoint p0) 0.0 1.0 level-action :length slider-length)
        s-sustain (sf/vslider drw ied param-sustain (env-pos op :sustain p0) 0.0 1.0 level-action :length slider-length)
        scale-action (fn [b _]
                       (let [state (math/str->int (name (second (msb/current-multistate-button-state b))))]
                         (reset! scale* state))
                       (doseq [s [s-attack s-decay1 s-decay2 s-release]]
                         (let [p (.get-property s :id)
                               v (slider/get-slider-value s)]
                           (.set-param! ied p (* @scale* v))))
                       (.status! ied (format "Env time scale = %s" @scale*)))
        
        msb-scale (msb/text-multistate-button (.tool-root drw)
                                              (env-pos op :scale p0)
                                              scale-states
                                              :id :env-scale-factor
                                              :click-action scale-action
                                              :rim-color (lnf/button-border)
                                              :gap 6)
        clipboard-action (fn [b _]
                           (let [id (.get-property b :id)]
                             (if (= id :copy)
                               (copy op ied)
                               (paste op ied))))
        b-copy (sf/copy-button drw (env-pos op :copy p0) :copy clipboard-action)
        b-paste (sf/paste-button drw (env-pos op :paste p0) :paste clipboard-action)]
    (fn [dmap]
      (let [att (param-attack dmap)
            dcy1 (param-decay1 dmap)
            dcy2 (param-decay2 dmap)
            rel (param-release dmap)
            peak (param-peak dmap)
            breakpoint (param-breakpoint dmap)
            sus (param-sustain dmap)
            max-time (max att dcy1 dcy2 rel)
            [scale scale-index] (cond (<= max-time 1)[1.0 0]
                                      (<= max-time 4)[4.0 1]
                                      (<= max-time 16)[16.0 2]
                                      (<= max-time 64)[64.0 3]
                                      :default [256 4])]
        (slider/set-slider-value! s-attack (/ att scale) false)
        (slider/set-slider-value! s-decay1 (/ dcy1 scale) false)
        (slider/set-slider-value! s-decay2 (/ dcy2 scale) false)
        (slider/set-slider-value! s-release (/ rel scale) false)
        (slider/set-slider-value! s-peak (float peak) false)
        (slider/set-slider-value! s-breakpoint (float breakpoint) false)
        (slider/set-slider-value! s-sustain (float sus) false)
        (msb/set-multistate-button-state! msb-scale scale-index false)
        (reset! scale* scale))))) 
