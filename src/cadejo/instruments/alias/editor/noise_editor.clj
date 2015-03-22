(ns cadejo.instruments.alias.editor.noise-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix :reload true])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory :reload true])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.tools.dual-slider :as dslider])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss]))


(def width 1600)                        ; TEMP
(def height 380)
(def min-noise-filter 10)
(def max-noise-filter 16000)
(def min-db constants/min-amp-db)
(def max-db constants/max-amp-db)

(declare draw-background)
(declare create-editor)



(defn noise-editor [ied]
 (let [p0 [0 height]
       drw (let [d (sfactory/sgwr-drawing width height)]
             (draw-background d p0)
             d)
       editor (create-editor drw ied p0)
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
                (editor dmap)
                (.render drw))))]
   ed))


;; Returns component coordinates
;; main - keyword either :nse or :rm for noise and ring-modulator panels 
;;        respectivly
;; sub  - keyword - specific component id
;; p0   - vector, all coordinates are r3elative to point p0 [x0 y0]
;; Returns vector [x y]
;;
(defn- coordinates [main sub p0]
  (let [[x0 y0] p0
        x-nse (+ x0 32)
        x-nse-crackle (+ x-nse 40)
        x-nse-filter (+ x-nse-crackle 60)
        x-nse-d1 (+ x-nse-filter 80)
        x-nse-l1 (+ x-nse-d1 40)
        x-nse-s1 (- x-nse-d1 0)
        x-nse-d2 (+ x-nse-l1 80)
        x-nse-l2 (+ x-nse-d2 40)
        x-nse-s2 (- x-nse-d2 0)
        x-nse-mix (+ x-nse-l2 80)
        x-nse-pan (+ x-nse-mix 80)
        x-nse-title (+ x-nse 30)
        x-nse-border0 x-nse
        x-nse-border1 (+ x-nse-border0 575)
        x-nse-help (- x-nse-pan 20)
        x-rm (+ x-nse-border1 10)
        x-rm-car (+ x-rm 60)
        x-rm-mod (+ x-rm-car 100)
        x-rm-d1 (+ x-rm-mod 90)
        x-rm-l1 (+ x-rm-d1 40)
        x-rm-s1 (+ x-rm-d1 0)
        x-rm-d2 (+ x-rm-l1 80)
        x-rm-l2 (+ x-rm-d2 40)
        x-rm-s2 (+ x-rm-d2 0)
        x-rm-mix (+ x-rm-l2 80)
        x-rm-pan (+ x-rm-mix 80)
        x-rm-title (+ x-rm 20)
        x-rm-help (- x-rm-pan 20)
        x-rm-border0 x-rm
        x-rm-border1 (+ x-rm-border0 650)
        y-title (- y0 310)
        y-slider1 (- y0 111)
        y-slider2 (- y0 sfactory/slider-length)
        y-source (+ y-slider1 33)
        y-border0 (- y0 20)
        y-border1 (- y-border0 330)
        y-help (- y-title 25)
        nse-x-map {:crackle x-nse-crackle,
                   :filter x-nse-filter,
                   :depth1 x-nse-d1, :lag1 x-nse-l1, :source1 x-nse-s1,
                   :depth2 x-nse-d2, :lag2 x-nse-l2, :source2 x-nse-s2,
                   :mix    x-nse-mix,:pan  x-nse-pan,
                   :title  x-nse-title, :help x-nse-help
                   :border0 x-nse-border0, :border1 x-nse-border1}
        rm-x-map {:carrier x-rm-car,  :modulator x-rm-mod,
                  :depth1 x-rm-d1, :lag1 x-rm-l1, :source1 x-rm-s1,
                  :depth2 x-rm-d2, :lag2 x-rm-l2, :source2 x-rm-s2,
                  :mix    x-rm-mix,:pan  x-rm-pan,
                  :title  x-rm-title, :help x-rm-help
                  :border0 x-rm-border0, :border1 x-rm-border1}
        y-map {:title y-title, :help y-help :source1 y-source, :source2  y-source,
               :slider1 y-slider1, :slider2 y-slider2
               :border0 y-border0, :border1 y-border1}
        x-map (main {:nse nse-x-map :rm rm-x-map})
        x (get x-map sub)
        y (get y-map sub y-slider1)]
    [x y]))


;; Draws static drawing elements and then "flattens" them into a sing image.
;; This reduces overhead of updating sgwr drawing.
;;
(defn- draw-background [ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)
        label (fn [main sub text xoffset]
                (let [[xt yt](coordinates main sub p0)]
                  (sfactory/label bg [(- xt xoffset)(+ yt 23)] text)))
        [x-crack y-slider1](coordinates :nse :crackle p0)
        x-filter (first (coordinates :nse :filter p0))
        x-carrier (first (coordinates :rm :carrier p0))
        x-modulator (first (coordinates :rm :modulator p0))
        x-d1 (first (coordinates :nse :depth1 p0))
        x-d2 (first (coordinates :nse :depth2 p0))
        x-d3 (first (coordinates :rm :depth1 p0))
        x-d4 (first (coordinates :rm :depth2 p0))
        x-l1 (first (coordinates :nse :lag1 p0))
        x-l2 (first (coordinates :nse :lag2 p0))
        x-l3 (first (coordinates :rm :lag1 p0))
        x-l4 (first (coordinates :rm :lag2 p0))
        x-mix1 (first (coordinates :nse :mix p0))
        x-mix2 (first (coordinates :rm :mix p0))
        x-pan1 (first (coordinates :nse :pan p0))
        x-pan2 (first (coordinates :rm :pan p0))
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-bus (+ y-slider1 80)]
    (label :nse :crackle "Crackle" 25)
    (label :nse :filter "Filter" 20)
    (label :nse :depth1 "Dpth" 15)
    (label :nse :lag1 "Lag" 10)
    (label :nse :depth2 "Dpth" 15)
    (label :nse :lag2 "Lag" 10)
    (label :nse :mix "Mix(db)" 19)
    (label :nse :pan "Pan" 10)
    (sfactory/label bg [(+ x-d1 50)(+ y-slider1 60)] "-- Bus --")
    (sfactory/label bg [(+ x-d3 50)(+ y-slider1 60)] "-- Bus --")
    (label :rm :carrier "Carrier" 20)
    (label :rm :modulator "Modulator" 32)
    (label :rm :depth1 "Dpth" 15)
    (label :rm :lag1 "Lag" 10)
    (label :rm :depth2 "Dpth" 15)
    (label :rm :lag2 "Lag" 10)
    (label :rm :mix "Mix(db)" 19)
    (label :rm :pan "Pan" 10)
    ;; unsigned minor ticks
    (doseq [x [x-crack x-l1 x-l2 x-l3 x-l4]]
      (sfactory/minor-ticks bg x y-slider1 y-slider2 10))
    ;; signed ticks
    (doseq [x [x-d1 x-d2 x-d3 x-d4 x-carrier x-modulator x-pan1 x-pan2]]
            (sfactory/minor-ticks bg x y-slider1 y-slider2 8))
    (let [delta-v 0.50
          delta-y (/ sfactory/slider-length 4)
          val* (atom -1.0)
          y* (atom y-slider1)]
      (while (<= @val* 1.0)
        (doseq [x [x-d1 x-d2 x-d3 x-d4 x-carrier x-modulator x-pan1 x-pan2]]
          (sfactory/major-tick bg x @y* (format "%+4.1f" @val*) [-50 5]))
        (swap! val* (fn [q](+ q delta-v)))
        (swap! y* (fn [q](- q delta-y)))))
    ;; mixer ticks
    (let [diff (- max-db min-db)
          delta-v 6
          count (/ diff delta-v)
          delta-y (/ sfactory/slider-length count)
          val* (atom nil)
          y* (atom nil)]
      (doseq [x [x-mix1 x-mix2]]
        (reset! val* min-db)
        (reset! y* y-slider1)
        (while (<= @val* max-db)
          (sfactory/major-tick bg x @y* (format "%+3d" (int @val*)) [-50 5])
          (swap! val* (fn [q](+ q delta-v)))
          (swap! y* (fn [q](- q delta-y))))))
    ;; pan labels
    (doseq [x [x-pan1 x-pan2]]
      (sfactory/label bg [(+ x 15)(+ y-slider1 5)] "Filter 1" :size 5.0)
      (sfactory/label bg [(+ x 15)(+ y-slider2 5)] "Filter 2" :size 5.0))
    ;; ring mod inputs
    (sfactory/label bg [(+ x-carrier 12)(+ y-slider2 4)] "OSC 2" :size 5)
    (sfactory/label bg [(+ x-carrier 12)(+ y-slider1 4)] "OSC 1" :size 5)
    (sfactory/label bg [(+ x-modulator 12)(+ y-slider2 4)] "NSE" :size 5)
    (sfactory/label bg [(+ x-modulator 12)(+ y-slider1 4)] "OSC 3" :size 5)
    ;; Filter ticks
    (sfactory/minor-ticks bg x-filter y-slider1 y-slider2 16)
    (let [freq* (atom max-noise-filter)
          height* (atom sfactory/slider-length)]
      (while (>= @freq* 2000)
        (sfactory/major-tick bg x-filter (- y-slider1 @height*) (format "%2dk" (int (/ @freq* 1000))) [-40 5])
        (swap! freq* (fn [q](* 1/2 q)))
        (swap! height* (fn [q](* 1/2 q)))))
    ;; Panel Labels and borders
    (sfactory/title bg (coordinates :nse :title p0) "Noise")
    (sfactory/title bg (coordinates :rm :title p0) "Ring Modulator")
    (sfactory/minor-border bg 
                           (coordinates :nse :border0 p0)
                           (coordinates :nse :border1 p0))
    (sfactory/minor-border bg
                           (coordinates :rm :border0 p0)
                           (coordinates :rm :border1 p0))
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


;; Creates active components.
;; Returns sync function use to update component positions.
;;
(defn- create-editor [drw ied p0]
  (let [param-nse-crackle :noise-param
        param-nse-lp :noise-lp
        param-nse-hp :noise-hp
        param-nse-mix :noise-amp
        param-nse-s1 :noise-amp1-src
        param-nse-d1 :noise-amp1-depth
        param-nse-l1 :noise-amp1-lag
        param-nse-s2 :noise-amp2-src
        param-nse-d2 :noise-amp2-depth
        param-nse-l2 :noise-amp2-lag
        param-nse-pan :noise-pan
        param-rm-car :ringmod-carrier
        param-rm-mod :ringmod-modulator
        param-rm-mix :ringmod-amp
        param-rm-s1 :ringmod-amp1-src
        param-rm-d1 :ringmod-amp1-depth
        param-rm-l1 :ringmod-amp1-lag
        param-rm-s2 :ringmod-amp2-src
        param-rm-d2 :ringmod-amp2-depth
        param-rm-l2 :ringmod-amp2-lag
        param-rm-pan :ringmod-pan
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider/get-slider-value s)]
                   (.set-param! ied param val)))
        s-nse-crackle (sfactory/vslider drw ied param-nse-crackle
                                        (coordinates :nse :crackle p0)
                                        0.0 1.0 action) 
        s-nse-mix (sfactory/vslider drw ied param-nse-mix
                                    (coordinates :nse :mix p0)
                                    min-db max-db action
                                    :value-hook int)
        s-nse-d1 (sfactory/vslider drw ied param-nse-d1
                                   (coordinates :nse :depth1 p0)
                                   -1.0 1.0 action)
        s-nse-l1 (sfactory/vslider drw ied param-nse-l1
                                   (coordinates :nse :lag1 p0)
                                   0.0 1.0 action)
        s-nse-d2 (sfactory/vslider drw ied param-nse-d2
                                   (coordinates :nse :depth2 p0)
                                   -1.0 1.0 action)
        s-nse-l2 (sfactory/vslider drw ied param-nse-l2
                                   (coordinates :nse :lag2 p0)
                                   0.0 1.0 action)
        s-nse-pan (sfactory/vslider drw ied param-nse-pan
                                    (coordinates :nse :pan p0)
                                    -1.0 1.0 action)
        s-rm-car (sfactory/vslider drw ied param-rm-car
                                   (coordinates :rm :carrier p0)
                                   -1.0 1.0 action)
        s-rm-mod (sfactory/vslider drw ied param-rm-mod
                                   (coordinates :rm :modulator p0)
                                   -1.0 1.0 action)
        s-rm-mix (sfactory/vslider drw ied param-rm-mix
                                   (coordinates :rm :mix p0)
                                   min-db max-db action
                                   :value-hook int)
        s-rm-d1 (sfactory/vslider drw ied param-rm-d1
                                  (coordinates :rm :depth1 p0)
                                  -1.0 1.0 action)
        s-rm-l1 (sfactory/vslider drw ied param-rm-l1
                                  (coordinates :rm :lag1 p0)
                                  0.0 1.0 action)
        s-rm-d2 (sfactory/vslider drw ied param-rm-d2
                                  (coordinates :rm :depth2 p0)
                                  -1.0 1.0 action)
        s-rm-l2 (sfactory/vslider drw ied param-rm-l2
                                  (coordinates :rm :lag2 p0)
                                  0.0 1.0 action)
        s-rm-pan (sfactory/vslider drw ied param-rm-pan
                                   (coordinates :rm :pan p0)
                                   -1.0 1.0 action)
        filter-action (fn [s _]
                        (let [[hp lp](dslider/get-dual-slider-values s)]
                          (.set-param! ied param-nse-lp lp)
                          (.set-param! ied param-nse-hp hp)
                          (.status! ied (format "[noise filter] -> hp %4d  lp %4d" hp lp)))) 
        ds-filter (dslider/dual-slider (.tool-root drw) (coordinates :nse :filter p0)
                                       sfactory/slider-length
                                       min-noise-filter max-noise-filter
                                       :id :filter-slider
                                       :orientation :vertical
                                       :value-hook int
                                       :drag-action filter-action
                                       :rim-color (uc/color 0 0 0 0)
                                       :track1-color (lnf/passive-track)
                                       :track2-color (lnf/passive-track)
                                       :track3-color (lnf/passive-track)
                                       :track4-color (lnf/active-track)
                                       :track4-width 2
                                       :handle1-color (lnf/active-track)
                                       :handle2-color (lnf/active-track)
                                       :handle1-style [:edge-e :edge-s :edge-w]
                                       :handle2-style [:edge-e :edge-n :edge-w])
        b-nse-s1 (matrix/source-button drw ied param-nse-s1 (coordinates :nse :source1 p0))
        b-nse-s2 (matrix/source-button drw ied param-nse-s2 (coordinates :nse :source2 p0))
        b-rm-s1  (matrix/source-button drw ied param-rm-s1 (coordinates :rm :source1 p0))
        b-rm-s2  (matrix/source-button drw ied param-rm-s2 (coordinates :rm :source2 p0))
        noise-help-action (fn [& _]
                           (println "ISSUE: Alias Noise Help action not implemented")
                           )

        rm-help-action (fn [& _]
                         (println "ISSUE: Alias Ringmodulator Help action not implemented")
                         )
        b-nse-help (sfactory/help-button drw (coordinates :nse :help p0) :alias-noise noise-help-action)
        b-rm-help (sfactory/help-button drw (coordinates :rm :help p0) :alias-ringmod rm-help-action)
        sync-fn (fn [dmap]
                  )]
    sync-fn))
