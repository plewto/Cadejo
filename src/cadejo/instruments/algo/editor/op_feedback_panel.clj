(ns cadejo.instruments.algo.editor.op-feedback-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider]))


(def ^:private width 400)
(def ^:private height 230)

;; n   - operator number, either 6 or 8
;; drw - sgwr drawing
;; p0  - reference point vector [x y] of lower left corner
;; ied - sgwr Instrumenteditor
;;
;; Returns map with keys
;;    :sync-fn     - function, updates gui tools
;;    :disable-fn  - function, disable gui tools
;;    :enable-fn   - function, enable gui tools
;;
(defn- feedback-panel [n drw p0 ied]
  (let [param-fb (keyword (format "op%d-feedback" n))
        param-env (keyword (format "op%d-env->feedback" n))
        param-lfo (if (= n 6) :op6-lfo1->feedback :op8-lfo2->feedback)
        param-pressure (keyword (format "op%d-pressure->feedback" n))
        param-cca (keyword (format "op%d-cca->feedback" n))
        param-ccb (keyword (format "op%d-ccb->feedback" n))
        param-hp (keyword (format "op%d-hp" n))
        op-id (keyword (format "op%d" n))
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        [x1 x2 x3 x4 x5 x6 x7 x8](range (+ x0 (* 1.0 slider-spacing)) (+ x0 (* slider-spacing 10.0)) slider-spacing)
        y1 (- y0 30)
        ;; width 500
        ;; height 230; (+ slider-length 65)
        y8 (- y0 height)

        ;; action (fn [s _] 
        ;;          (println (format "DEBUG slider id %s  value %s" 
        ;;                           (.get-property s :id)
        ;;                           (slider/get-slider-value s))))
        action (fn [s _]
                 (let [p (.get-property s :id)
                       v (slider/get-slider-value s)]
                   (.set-param! ied p v)))
        s-fb (factory/slider tools [x1 y1] param-fb min-feedback max-feedback action)
        s-env (factory/slider tools [x2 y1] param-env min-feedback-mod max-feedback-mod action :signed)
        s-lfo (factory/slider tools [x3 y1] param-lfo min-feedback-mod max-feedback-mod action :signed)
        s-prss (factory/slider tools [x4 y1] param-pressure min-feedback-mod max-feedback-mod action :signed)
        s-cca (factory/slider tools [x5 y1] param-cca min-feedback-mod max-feedback-mod action :signed)
        s-ccb (factory/slider tools [x6 y1] param-ccb min-feedback-mod max-feedback-mod action :signed)
        s-hp (factory/hp-slider tools [x7 y1] param-hp action)
        txt-id (keyword (format "op%d" n))
        txt-fb (factory/slider-label root [x1 y1] txt-id " FB")
        txt-env (factory/slider-label root [x2 y1] txt-id "Env")
        txt-lfo (factory/slider-label root [x3 y1] txt-id (if (= n 6) "LFO1" "LFO2"))
        txt-prss (factory/slider-label root [x4 y1] txt-id "Prss")
        txt-cca (factory/slider-label root [x5 y1] txt-id "CCA")
        txt-ccb (factory/slider-label root [x6 y1] txt-id "CCB")
        txt-hp (factory/slider-label root [x7 y1] txt-id " HP")
        tx-title (factory/section-label root [(+ x0 10)(+ y8 25)] txt-id (format "Feedback op %d" n))
        tool-list (list s-fb s-env s-lfo s-prss s-cca s-ccb s-hp)
        sync-fn (fn []  
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        fb (float (param-fb dmap))
                        env (float (param-env dmap))
                        lfo (float (param-lfo dmap))
                        pressure (float (param-pressure dmap))
                        cca (float (param-cca dmap))
                        ccb (float (param-ccb dmap))
                        hp (float (param-hp dmap))]
                    (slider/set-slider-value! s-fb fb false)
                    (slider/set-slider-value! s-env env false)
                    (slider/set-slider-value! s-lfo lfo false)
                    (slider/set-slider-value! s-prss pressure false)
                    (slider/set-slider-value! s-cca cca false)
                    (slider/set-slider-value! s-ccb ccb false)
                    (slider/set-slider-value! s-hp hp false)))
        disable-fn (fn []
                     (doseq [q tool-list]
                       (.disable! q false)))
        enable-fn (fn []
                    (doseq [q tool-list]
                      (.enable! q false)))
        ]
    ;; fb slider ticks
    (let [id (keyword (format "op%d-tick" n))
          xa (- x1 (* 0.5 major-tick-length))
          xb (+ xa major-tick-length)
          delta-y (float (/ slider-length max-feedback))
          ya y1
          yb (- ya delta-y)
          yc (- yb delta-y)
          yd (- yc delta-y)
          ye (- yd delta-y)
          tx (fn [y n]
               (text/text root [(- xa 10) (+ y 5)] (str n)
                          :id op-id 
                          :style :mono
                          :size 6
                          :color (lnf/major-tick-color)))]
      (line/line root [xa ya][xb ya] :id op-id :style 0 :color (lnf/major-tick-color))
      (line/line root [xa yb][xb yb] :id op-id :style 0 :color (lnf/major-tick-color))
      (line/line root [xa yc][xb yc] :id op-id :style 0 :color (lnf/major-tick-color))
      (line/line root [xa yd][xb yd] :id op-id :style 0 :color (lnf/major-tick-color))
      (line/line root [xa ye][xb ye] :id op-id :style 0 :color (lnf/major-tick-color))
      (line/line root [(+ xa 4) yd][(+ xa 4) ye] :id op-id :style :dotted :width 2 :color (lnf/major-tick-color))
      (tx ya 0)
      (tx yb 1)
      (tx yc 2)
      (tx yd 3)
      (tx ye 4))
    ;; mod slider tick marks 
    (let [xa (- x2 minor-tick-length)
          xb (+ x6 minor-tick-length)
          ya (- y1 0)
          yb (- ya slider-length)
          yc (math/mean ya yb)
          major (fn [y n]
                 (line/line root [xa y][xb y] :id op-id
                            :style :dotted
                            :color (lnf/major-tick-color))
                 (text/text root [(- xa 20)(+ y 5)] (format "%+d" (int n))
                            :id op-id
                            :style :mono
                            :size 6
                            :color (lnf/major-tick-color)))
          minor (fn [y n]
                  (line/line root [xa y][xb y] :id op-id
                             :style :dotted
                             :color (lnf/minor-tick-color))
                  (text/text root [(- xa 20)(+ y 5)] (format "%+d" (int n))
                             :id op-id
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))
          y* (atom (- y1 0))
          v* (atom min-feedback-mod)
          y-count (- max-feedback-mod min-feedback-mod)
          y-delta (/ slider-length y-count)]
      (dotimes [i (inc y-count)]
        (if (or (= @v* min-feedback-mod)(= @v* max-feedback-mod)(zero? @v*))
          (major @y* @v*)
          (minor @y* @v*))
        (swap! y* (fn [q](- q y-delta)))
        (swap! v* inc)))

    ;; hp tick marks
    (let [delta-value (/ (inc (- max-hp-freq min-hp-freq)) 10)
          delta10 (int (/ slider-length delta-value))
          delta1 (int (/ delta10 10))
          xa (- x7 (* 0.5 major-tick-length))
          xb (+ xa major-tick-length)
          xc (- x7 (* 0.5 minor-tick-length))
          ya (- y1 0)
          yb (- ya slider-length)
          yc (- y1 delta1)
          major (fn [y n]
                  (line/line root [xa y][xb y] :id op-id
                             :style :solid
                             :color (lnf/major-tick-color))
                  (text/text root [(- xa 20)(+ y 5)] (format "%2d" (int (max 1 n)))
                             :id op-id 
                             :style :mono
                             :size 6
                             :color (lnf/major-tick-color)))
          minor (fn [y]
                  (line/line root [x7 y][xc y] :id op-id
                             :style :solid
                             :color (lnf/minor-tick-color)))]
      (major (- ya (* 0 delta10)) 0)
      (major (- ya (* 1 delta10)) 10)
      (major (- ya (* 2 delta10)) 20)
      (major (- ya (* 3 delta10)) 30)
      (major (- ya (* 4 delta10)) 40)
      (major (- ya (* 5 delta10)) 50)
      (doseq [i (range 1 10)](minor (- ya (* i delta1)))))
    (factory/inner-border root p0 [x8 y8])
    {:sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))

 
(defn op-feedback-panel [op drw p0 ied]
  (if (or (= op 6)(= op 8))
    (feedback-panel op drw p0 ied)
    (let [[x0 y0] p0
          x (+ x0 130)
          y (- y0 190)
          nullfn (fn [])]
      (factory/inner-border (.root drw) p0 [(+ x0 width)(- y0 height)])
      (factory/electronic-eye (.root drw) [x y] op)
      {:sync-fn nullfn
       :disable-fn nullfn
       :enable-fn nullfn})))
