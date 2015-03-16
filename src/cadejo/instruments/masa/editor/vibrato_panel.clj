(ns cadejo.instruments.masa.editor.vibrato-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.instruments.subedit])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))


;; (defn- slider [drw p0 param mn mx drag-action]
;;   (let [len 140
;;         s (slider/slider (.tool-root drw) p0 len mn mx
;;                          :id param
;;                          :drag-action drag-action
;;                          :orientation :vertical
;;                          :rim-color [0 0 0 0]
;;                          :track1-color (lnf/passive-track-color)
;;                          :track2-color (lnf/active-track-color)
;;                          :track2-width 1
;;                          :handle-style [:fill :dot]
;;                          :handle-color (lnf/slider-handle-color)
;;                          :handle-size 3)]
;;     s))

;; (defn- small-text [drw p0 txt]
;;   (text/text (.root drw) p0 txt
;;              :style :mono
;;              :size 5.5
;;              :color (lnf/text-color)))

(defn- rule-line [drw p0 txt]
  (let [length 180
        [x0 y0] p0
        x1 (+ x0 length)
        y1 y0]
    (line/line (.root drw) [x0 y0][x1 y1] :id :rule
               :color (lnf/major-tick)
               :style :dotted)
    (sfactory/label drw [(- x0 28) (+ y0 4) ] txt)))

(defn vibrato-panel [drw ied p0]
  (let [data (fn [param] ;; Returns current program value of parameter
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        w 175
        h 183
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (- y0 h)
        p1 [x1 y1]
        freq-action (fn [s _] 
                      (let [v (slider/get-slider-value s)]
                        (.set-param! ied :vrate v)))
        sens-action (fn [s _] 
                      (let [v (slider/get-slider-value s)]
                        (.set-param! ied :vsens v)))
        depth-action (fn [s _]
                       (let [v (slider/get-slider-value s)]
                         (.set-param! ied :vdepth v)))
        delay-action (fn [s _]
                       (let [v (slider/get-slider-value s)]
                         (.set-param! ied :vdelay v)))
        ;; s-rate (slider drw  [(+ x0  12)(- y0 25)] :vrate min-vibrato-frequency max-vibrato-frequency freq-action)
        ;; s-sens (slider drw  [(+ x0  62)(- y0 25)] :vsens 0.0 max-vibrato-sensitivity sens-action)
        ;; s-depth (slider drw [(+ x0 112)(- y0 25)] :vdepth 0.0 1.0 depth-action)
        ;; s-delay (slider drw [(+ x0 162)(- y0 25)] :vdelay 0.0 max-vibrato-delay delay-action)

        s-rate (sfactory/vslider drw  ied :vrate  [(+ x0  12)(- y0 25)] min-vibrato-frequency max-vibrato-frequency freq-action)
        s-sens (sfactory/vslider drw  ied :vsens  [(+ x0  62)(- y0 25)] 0.0 max-vibrato-sensitivity sens-action)
        s-depth (sfactory/vslider drw ied :vdepth [(+ x0 112)(- y0 25)] 0.0 1.0 depth-action)
        s-delay (sfactory/vslider drw ied :vdelay [(+ x0 162)(- y0 25)] 0.0 max-vibrato-delay delay-action)
        widget-map {}]
    (rect/rectangle (.root drw) [(- x0 33) y0][(+ x1 33) y1] :color (lnf/minor-border) :radius 12)
    ;; (text/text (.root drw) [(+ x0 60)(- y0 185)] "Vibrato"
    ;;            :style :sans
    ;;            :size 8
    ;;            :color (lnf/text-color))
    (sfactory/text drw [(+ x0 60)(- y0 188)] "Vibrato")
    (sfactory/label drw [(+ x0 2)(- y0 5)] "Rate")
    (sfactory/label drw [(+ x0 50)(- y0 5)] "Sens")
    (sfactory/label drw [(+ x0 98)(- y0 5)] "Depth")
    (sfactory/label drw [(+ x0 145)(- y0 5)] "Delay")
    (rule-line drw [(+ x0 0)(- y0  23)] "0.0")
    (rule-line drw [(+ x0 0)(- y0  94)] "0.5")
    (rule-line drw [(+ x0 0)(- y0 163)] "1.0")
    
     (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      
      (widget [this key]
        (get widget-map key))
      
      (parent [this] ied)
      
      (parent! [this _] ied) 
      
      (status! [this msg]
        (.status! ied msg))
      
      (warning! [this msg]
        (.warning! ied msg))
      
      (set-param! [this param value]
        (.set-param! ied param val))
      
      (init! [this]
        (let [vmap {:vrate 7 :vsens 0.05 :vdepth 0.0 :vdelay 0.0}]
          (doseq [[p v] vmap]
            (.set-param! ied p v))))
      
      (sync-ui! [this]
        (let [rate (data :vrate)
              sens (data :vsens)
              depth (data :vdepth)
              delay (data :vdelay)]
          (slider/set-slider-value! s-rate rate false)
          (slider/set-slider-value! s-sens sens false)
          (slider/set-slider-value! s-depth depth false)
          (slider/set-slider-value! s-delay delay false))) )))
