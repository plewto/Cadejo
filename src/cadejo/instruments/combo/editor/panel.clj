(ns cadejo.instruments.combo.editor.panel
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.util.color :as uc]))

(def ^:private ed-width 620)
(def ^:private ed-height 450)

(def ^:private vibrato-pos->freq 
  (math/linear-function 0 min-vibrato-frequency
                        100 max-vibrato-frequency))

(def ^:private vibrato-pos->sens 
  (math/linear-function 0 min-vibrato-sensitivity
                        100 max-vibrato-sensitivity))

(defn- default-drag-action [slider _]
  (let [param (.get-property slider :id)
        pos (slider/get-slider-value slider)
        val (/ pos 100.0)
        ied (.get-property slider :editor)]
    (.set-param! ied param val)))

(defn- detune-coarse-action [slider _]
  (let [param :chorus
        coarse (/ (slider/get-slider-value slider) 100.0)
        fine @(.get-property slider :fine*)
        ied (.get-property slider :editor)]
    (reset! (.get-property slider :coarse*) coarse)
    (.set-param! ied param (+ coarse fine))))

(defn- detune-fine-action [slider _]
  (let [param :chorus
        fine (/ (slider/get-slider-value slider) 1000.0)
        coarse @(.get-property slider :coarse*)
        ied (.get-property slider :editor)]
    (reset! (.get-property slider :fine*) fine)
    (.set-param! ied param (+ coarse fine))))

(defn- vibrato-freq-action [slider _]
  (let [param :vibrato-freq
        pos (slider/get-slider-value slider)
        val (vibrato-pos->freq pos)
        ied (.get-property slider :editor)]
    (.set-param! ied param val)))

 (defn- vibrato-sens-action [slider _]
   (let [param :vibrato-sens
         pos (slider/get-slider-value slider)
         val (vibrato-pos->sens pos)
         ied (.get-property slider :editor)]
     (.set-param! ied param val)))

(defn- db-amp-action [slider _]
  (let [param :amp
        pos (slider/get-slider-value slider)
        pos->db (math/linear-function 0 min-amp-db 100 max-amp-db)
        ied (.get-property slider :editor)]
    (.set-param! ied param (math/db->amp (pos->db pos)))))

(defn- flanger-drag-action [field _]
  (let [param-rate :flanger-rate
        param-fb :flanger-fb
        b (:b1 @(.get-property field :balls*))
        pos (.get-property b :value)
        pos->fb (math/linear-function -1 min-flanger-fb 1 max-flanger-fb)
        pos->rate (math/linear-function 0 min-flanger-rate 1 max-flanger-rate)
        rate (pos->rate (second pos))
        fb (pos->fb (first pos))
        ied (.get-property field :editor)]
    (.set-param! ied param-rate rate)
    (.set-param! ied param-fb fb)
    (.status! ied (format "flanger fb %+6.2f  rate %5.3f" fb rate))))

(defn- filter-curve-click-action [b _]
  (let [param :filter-type
        val (first (msb/current-multistate-button-state b))
        ied (.get-property b :editor)]
    (.set-param! ied param val)))

(defn- filter-freq-click-action [b _]
  (let [param :filter
        imap {0 1, 1 2, 2 3, 3 4, 4 6, 5 8}
        key (first (msb/current-multistate-button-state b))
        val (get imap key 1)
        ied (.get-property b :editor)]
    (.set-param! ied param val)))

(defn editor-panel [ied]
  (let [[x1 x2 x3 x4](range 64 350 50)
        [x5 x6] (range 380 470 50)
        [x7 x8] (range 500 600 50)
        y1 200
        y2 400
        drw (let [d (sgwr.components.drawing/native-drawing ed-width ed-height)
                  root (.root d)
                  y3 (+ y2 23)
                  border (lnf/minor-border)
                  r1 (rect/rectangle root [40   12][240 438] :color border) 
                  r2 (rect/rectangle root [240  12][340 155] :color border)
                  r3 (rect/rectangle root [240 155][340 438] :color border)
                  r4 (rect/rectangle root [340  12][460 234] :color border)
                  r5 (rect/rectangle root [460  12][580 234] :color border)
                  r6 (rect/rectangle root [340 234][580 438] :color border)]
              (.background! d (lnf/background))
              (sfactory/title d [120 34] "Mix")
              (sfactory/title d [114 234] "Wave")
              (sfactory/title d [260 34] "Filter")
              (sfactory/title d [256 175] "Detune")
              (sfactory/title d [364 34] "Vibrato")
              (sfactory/title d [501 34] "Out")
              (sfactory/title d [420 260] "Flanger")
              (sfactory/label d [(- x1 3) y3] "1")
              (sfactory/label d [(- x2 3) y3] "2")
              (sfactory/label d [(- x3 3) y3] "3")
              (sfactory/label d [(- x4 3) y3] "4")
              (sfactory/label d [250 430] "Coarse")
              (sfactory/label d [305 430] "Fine")
              (sfactory/label d [(- x5 13)(+ y1 20)] "Rate")
              (sfactory/label d [(- x6 13)(+ y1 20)] "Sens")
              (sfactory/label d [(- x7 18)(+ y1 20)] "Reverb") 
              (sfactory/label d [(- x8 10)(+ y1 20)] "Amp")
              (sfactory/label d [370 400] "- Feedback +")
              (sfactory/vtext d [470 315] "Rate" :color (lnf/label))
              (line/line root [410 280][410 380] :style :dotted :color (lnf/minor-border))
              (sfactory/label d [(+ x7 -15) y3] "Depth")
              (sfactory/label d [(+ x8 -10) y3] "Mix")
              d)
        detune-coarse* (atom 0.0)
        detune-fine* (atom 0.0)
        troot (.tool-root drw)
        s-amp1 (sfactory/vslider drw ied :amp1 [x1 y1] 1 100 default-drag-action) 
        s-amp2 (sfactory/vslider drw ied :amp2 [x2 y1] 1 100 default-drag-action) 
        s-amp3 (sfactory/vslider drw ied :amp3 [x3 y1] 1 100 default-drag-action) 
        s-amp4 (sfactory/vslider drw ied :amp4 [x4 y1] 1 100 default-drag-action) 
        s-wave1 (sfactory/vslider drw ied :wave1 [x1 y2] 1 100 default-drag-action) 
        s-wave2 (sfactory/vslider drw ied :wave2 [x2 y2] 1 100 default-drag-action) 
        s-wave3 (sfactory/vslider drw ied :wave3 [x3 y2] 1 100 default-drag-action) 
        s-wave4 (sfactory/vslider drw ied :wave4 [x4 y2] 1 100 default-drag-action) 
        s-detune-coarse (let [s (sfactory/vslider drw ied :detune-coarse [270 y2] 0 100 
                                                 detune-coarse-action :length 200)]
                          (.put-property! s :coarse* detune-coarse*)
                          (.put-property! s :fine* detune-fine*)
                          s)
        s-detune-fine   (let [s (sfactory/vslider drw ied :detune-fine [315 y2] 0 100 
                                                 detune-fine-action :length 200)]
                          (.put-property! s :coarse* detune-coarse*)
                          (.put-property! s :fine* detune-fine*)
                          s)
        msb-filter-curve (let [states [[:bypass :filter :none]
                                       [:lp :filter :low]
                                       [:hp :filter :high]
                                       [:bp :filter :band]
                                       [:br :filter :notch]]
                               b (msb/icon-multistate-button 
                                  troot [270 50] states 
                                  :icon-prefix (let [cs (config/current-skin)]
                                                 (cond (= cs "Twilight") :gray
                                                       :default (lnf/icon-prefix)))
                                  :click-action filter-curve-click-action)]
                           (.put-property! b :editor ied)
                           b)
        msb-filter-freq (let [states [[:1 " 1 "], [:2 " 2 "], [:3 " 3 "],
                                      [:4 " 4 "], [:6 " 6 "], [:8 " 8 "]]
                              b (msb/text-multistate-button 
                                 troot [272 100] states
                                 :text-color (lnf/text)
                                 :w 36 :h 36 :rim-radius 0
                                 :click-action filter-freq-click-action)]
                          (.put-property! b :editor ied)
                          b)
        s-vrate (sfactory/vslider drw ied :vibrato-freq [x5 y1] 1 100 vibrato-freq-action)
        s-vsens (sfactory/vslider drw ied :vibrato-sens [x6 y1] 1 100 vibrato-sens-action)
        s-reverb (sfactory/vslider drw ied :reverb-mix [x7 y1] 1 100 default-drag-action)
        s-amp (sfactory/vslider drw ied :amp [x8 y1] 0 100 db-amp-action)
        s-flanger-depth (sfactory/vslider drw ied :flanger-depth [x7 y2] 0 100 default-drag-action :length 120)
        s-flanger-mix (sfactory/vslider drw ied :flanger-mix [x8 y2] 0 100 default-drag-action :length 120)
        field-flanger (let [f (field/field troot [360 280][460 380][-1 1][0 1]
                                           :drag-action flanger-drag-action)]
                        (.put-property! f :editor ied)
                        f)
        ball-flanger (field/ball field-flanger :b1 [0.5 0.5] :style [:diag :diag2]
                                 :selected-color (lnf/handle)
                                 :color (lnf/handle))]
    (.use-attributes! (.root drw) :default)
    (msb/set-multistate-button-state! msb-filter-curve 0)
    (msb/set-multistate-button-state! msb-filter-freq 0)
    (sfactory/minor-ticks drw x1 y1 (- y1 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x2 y1 (- y1 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x3 y1 (- y1 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x4 y1 (- y1 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x1 y2 (- y2 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x2 y2 (- y2 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x3 y2 (- y2 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw x4 y2 (- y2 sfactory/slider-length) 10)
    (sfactory/minor-ticks drw 270 y2 (- y2 200) 10) ; detune coarse
    (sfactory/minor-ticks drw 315 y2 (- y2 200) 10) ; detune fine
    (sfactory/minor-ticks drw x5 y1 (- y1 sfactory/slider-length) 10) ; vibrato rate
    (sfactory/minor-ticks drw x6 y1 (- y1 sfactory/slider-length) 10) ; vibrato sens
    (sfactory/minor-ticks drw x7 y1 (- y1 sfactory/slider-length) 10) ; reverb
    (sfactory/minor-ticks drw x8 y1 (- y1 sfactory/slider-length) 8) ; amp
    (sfactory/minor-ticks drw x7 y2 (- y2 120) 10) ; flanger depth
    (sfactory/minor-ticks drw x8 y2 (- y2 120) 10) ; flanger mix
    
    (.render drw)
    {:drawing drw
     :s-amp1 s-amp1
     :s-amp2 s-amp2
     :s-amp3 s-amp3
     :s-amp4 s-amp4
     :s-wave1 s-wave1
     :s-wave2 s-wave2
     :s-wave3 s-wave3
     :s-wave4 s-wave4
     :s-detune-coarse s-detune-coarse
     :s-detune-fine s-detune-fine
     :s-vrate s-vrate
     :s-vsens s-vsens
     :s-reverb s-reverb
     :s-amp s-amp
     :s-flanger-depth s-flanger-depth
     :s-flanger-mix s-flanger-mix
     :msb-filter-curve msb-filter-curve
     :msb-filter-freq msb-filter-freq
     :field-flanger field-flanger
     :ball-flanger ball-flanger}))
  
