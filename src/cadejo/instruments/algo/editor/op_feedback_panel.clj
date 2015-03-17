(ns cadejo.instruments.algo.editor.op-feedback-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.components.image :as image])
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
        [x1 x2 x3 x4 x5 x6 x7 x8](range (+ x0 (* 1.0 slider-spacing))
                                        (+ x0 (* slider-spacing 10.0))
                                        slider-spacing)
        x-label-offset -12
        y1 (- y0 30)
        y8 (- y0 height)
        y-label-offset 22
        action (fn [s _]
                 (let [p (.get-property s :id)
                       v (slider/get-slider-value s)]
                   (.set-param! ied p v)))

        s-fb   (sfactory/vslider drw ied param-fb [x1 y1]
                                 min-feedback max-feedback action)
        s-env  (sfactory/vslider drw ied param-env [x2 y1]
                                 min-feedback-mod max-feedback-mod action)
        s-lfo  (sfactory/vslider drw ied param-lfo [x3 y1]
                                 min-feedback-mod max-feedback-mod action)
        s-prss (sfactory/vslider drw ied param-pressure [x4 y1]
                                 min-feedback-mod max-feedback-mod action)
        s-cca  (sfactory/vslider drw ied param-cca [x5 y1]
                                 min-feedback-mod max-feedback-mod action)
        s-ccb  (sfactory/vslider drw ied param-ccb [x6 y1]
                                 min-feedback-mod max-feedback-mod action)
        s-hp   (sfactory/vslider drw ied param-hp [x7 y1] 
                                 min-hp-freq max-hp-freq action 
                                 :value-hook (fn [n]
                                               (int (if (<= n 10)
                                                      n
                                                      (* 10 (int (/ n 10)))))))
        txt-id (keyword (format "op%d" n))
        txt-fb (sfactory/label drw [(+ x1 x-label-offset) (+ y1 y-label-offset)] " FB")
        txt-env (sfactory/label drw [(+ x2 x-label-offset) (+ y1 y-label-offset)] "Env")
        txt-lfo (sfactory/label drw [(+ x3 x-label-offset) (+ y1 y-label-offset)] (if (= n 6) "LFO1" "LFO2"))
        txt-prss (sfactory/label drw [(+ x4 x-label-offset) (+ y1 y-label-offset)] "Prss")
        txt-cca (sfactory/label drw [(+ x5 x-label-offset) (+ y1 y-label-offset)] "CCA")
        txt-ccb (sfactory/label drw [(+ x6 x-label-offset) (+ y1 y-label-offset)] "CCB")
        txt-hp (sfactory/label drw [(+ x7 x-label-offset) (+ y1 y-label-offset)] " HP")
        tx-title (sfactory/text drw [(+ x0 10)(+ y8 25)] (format "Feedback op %d" n))
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
                      (.enable! q false)))]
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
                          :color (lnf/major-tick)))]
      (line/line root [xa ya][xb ya] :id op-id :style 0 :color (lnf/major-tick))
      (line/line root [xa yb][xb yb] :id op-id :style 0 :color (lnf/major-tick))
      (line/line root [xa yc][xb yc] :id op-id :style 0 :color (lnf/major-tick))
      (line/line root [xa yd][xb yd] :id op-id :style 0 :color (lnf/major-tick))
      (line/line root [xa ye][xb ye] :id op-id :style 0 :color (lnf/major-tick))
      (line/line root [(+ xa 4) yd][(+ xa 4) ye] :id op-id :style :dotted :width 2 :color (lnf/major-tick))
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
                            :color (lnf/major-tick))
                 (text/text root [(- xa 20)(+ y 5)] (format "%+d" (int n))
                            :id op-id
                            :style :mono
                            :size 6
                            :color (lnf/major-tick)))
          minor (fn [y n]
                  (line/line root [xa y][xb y] :id op-id
                             :style :dotted
                             :color (lnf/minor-tick))
                  (text/text root [(- xa 20)(+ y 5)] (format "%+d" (int n))
                             :id op-id
                             :style :mono
                             :size 6
                             :color (lnf/major-tick)))
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
                             :color (lnf/major-tick))
                  (text/text root [(- xa 20)(+ y 5)] (format "%2d" (int (max 1 n)))
                             :id op-id 
                             :style :mono
                             :size 6
                             :color (lnf/major-tick)))
          minor (fn [y]
                  (line/line root [x7 y][xc y] :id op-id
                             :style :solid
                             :color (lnf/minor-tick)))]
      (major (- ya (* 0 delta10)) 0)
      (major (- ya (* 1 delta10)) 10)
      (major (- ya (* 2 delta10)) 20)
      (major (- ya (* 3 delta10)) 30)
      (major (- ya (* 4 delta10)) 40)
      (major (- ya (* 5 delta10)) 50)
      (doseq [i (range 1 10)](minor (- ya (* i delta1)))))
    (sfactory/minor-border drw p0 [x8 y8])
    {:sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))

(defn- electronic-eye [grp p0 n]
  (image/read-image grp p0 (format "resources/algo/electronic_eye_%d.png" n)))
 
(defn op-feedback-panel [op drw p0 ied]
  (if (or (= op 6)(= op 8))
    (feedback-panel op drw p0 ied)
    (let [[x0 y0] p0
          x (+ x0 130)
          y (- y0 190)
          nullfn (fn [])]
      (sfactory/minor-border drw p0 [(+ x0 width)(- y0 height)])
      (electronic-eye (.root drw) [x y] op)
      {:sync-fn nullfn
       :disable-fn nullfn
       :enable-fn nullfn})))
