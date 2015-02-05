(ns cadejo.ui.midi.properties-panel
  (:use [cadejo.util.trace])
  (:require [cadejo.ui.node-observer])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.elements.drawing])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.elements.rule :as rule])
  (:require [sgwr.indicators.displaybar :as dbar :reload true])
  (:require [sgwr.widgets.button :as button])
  (:require [sgwr.widgets.dual-slider :as dslider])
  (:require [sgwr.widgets.multistate-button :as msb])  
  (:require [sgwr.widgets.slider :as slider])
  (:require [seesaw.core :as ss]))


(defn- background-color [] :black)
(defn- icon-prefix [] :gray)
(defn- text-color [] :gray)
(defn- title-color [] (text-color))
(defn- dbar-inactive-color [] [77 58 83])
(defn- dbar-active-color [] [245 244 207])
(defn- border-color [] (text-color))
(defn- major-tick-color [] (text-color))
(defn- minor-tick-color [] (text-color))
(defn- passive-track-color [] (text-color))
(defn- active-track-color [] :yellow)

(def drawing-width 900)
(def drawing-height 400)
(def dbar-cell-width 18)
(def dbar-cell-height 28)
(def slider-length 150)

(def ^:private curve-button-states [[:zero :curve :zero  0]
                                    [:half :curve :half  1]
                                    [:one :curve :one  2]
                                    [:linear :curve :linear  3]
                                    [:quadratic :curve :quadratic  4]
                                    [:cubic :curve :cubic  5]
                                    [:quartic :curve :quartic  6]
                                    [:convex2 :curve :convex2  7]
                                    [:convex4 :curve :convex4  8]
                                    [:convex6 :curve :convex6  9]
                                    [:logistic :curve :logistic 10]
                                    [:logistic2 :curve :logistic2 11]
                                    [:ilinear :curve :ilinear 12]
                                    [:iquadratic :curve :iquadratic 13]
                                    [:icubic :curve :icubic 14]
                                    [:iconvex2 :curve :iconvex2 15]
                                    [:iconvex4 :curve :iconvex4 16]
                                    [:iconvex6 :curve :iconvex6 17]
                                    [:ilogistic :curve :ilogistic 18]
                                    [:ilogistic2 :curve :ilogistic2 19]])

;; create curve multistate button
;;
(defn- curve-button [drawing id p0 & {:keys [click-action]
                                      :or {click-action nil}}]
  (let [wr (.widget-root drawing)
        b (msb/icon-multistate-button wr p0 curve-button-states :id id
                                      :icon-prefix (icon-prefix)
                                      :click-action click-action)]
    b))

;; create title text
;;
(defn- title [drawing p txt]
  (let [obj (text/text (.root drawing) p txt
                       :style :serif
                       :size 8
                       :color (title-color))]
    obj))
                       
;; create general text
;;
(defn text [drawing p txt & {:keys [style size]
                             :or {style :serif
                                  size 8}}]
  (text/text (.root drawing) p txt :style style :size size :color (text-color)))

;; Create edit button
;;
(defn- edit-button [parent p0 dbar msg validator callback]
  (let [caction (fn [& _]
                  (dbar/displaybar-dialog dbar msg
                                          :validator validator
                                          :callback callback))]
    (button/text-button parent p0 "Edit"
                        :rim-width 1.0
                        :text-color (text-color)
                        :click-action caction)))

;; Create inherit checkbox
;;
(defn- inherit-checkbox [parent p0 click-action]
  (let [cb (msb/checkbox parent p0 "Inherit"
                         :text-color (text-color)
                         :text-size 6
                         :click-action click-action)]
    cb))

;; Create sub-panel border
;;
(defn- border [parent p0 p1]
  (let [r (rect/rectangle parent p0 p1
                          :width 1.0
                          :color (border-color))]
    (.put-property! r :corner-radius 18)
    r))


; ---------------------------------------------------------------------- 
;                             Pitch Bend Panel

(defn- create-bend-controls [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        widgets (.widget-root drw)
        w 171
        h 253
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        b-curve (curve-button drw :bend [(+ x0 93)(+ y0 40)]
                              :click-action (fn [b _]
                                              (let [c (second (msb/current-multistate-button-state b))]
                                                (.status! @ed* (format "[:bend-curve] -> %s" c))
                                                (.put-property! (get-node) :bend-curve c))))
        range-validator (fn [s](let [n (math/str->float s)]
                                 (if n 
                                   (and (<= -2400 n)(<= n 2400))
                                   false)))
        range-callback (fn [dbar]
                         (let [r (int (math/str->float (.current-display dbar)))]
                           (.status! @ed* (format "[:bend-range] -> %s" r))
                           (.put-property! (get-node) :bend-range r)))
        dbar (dbar/displaybar root (+ x0 32)(+ y0 92) 5 :matrix 
                              :cell-height dbar-cell-height
                              :cell-width dbar-cell-width)
        b-edit (edit-button widgets [(+ x0 90)(+ y0 125)] dbar 
                            "Bend Range in Cents +/- 2400"
                            range-validator
                            range-callback)
        tx2 (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
        tx3 (text drw [(+ x0 24)(+ y0 140)] "Range" :style :sans :size 6)
        tx-inherited (text drw [(+ x0 8)(+ y0 200)] ; displays inherited value
                           (format "(%s range %s)" "--" "--")
                           :style :mono 
                           :size 5)
        disable (fn []
                  (trace-enter "bend-panel disable")
                  (let [node (get-node)
                        icurve (.get-property node :bend-curve)
                        irange (.get-property node :bend-range)]
                    (.put-property! tx-inherited :text (format "(%s range %s)" icurve irange))
                    (.put-property! node :bend-curve nil)
                    (.put-property! node :bend-range nil)
                    (doseq [w [b-curve dbar b-edit tx2 tx3]]
                      (.disable! w false))
                    (.render drw))
                  (trace-exit)
                  )

        enable (fn []
                 (trace-enter "bend-panel enable")
                 (let [node (get-node)
                       curve (second (msb/current-multistate-button-state b-curve))
                       range (int (math/str->float (.current-display dbar)))]
                   (.put-property! tx-inherited :text " ")
                   (.put-property! node :bend-curve curve)
                   (.put-property! node :bend-range range)
                   (doseq [w [b-curve dbar b-edit tx2 tx3]]
                     (.enable! w false))
                   (.render drw))
                 (trace-exit)
                 )
        cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (println) ;; DEBUG
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         ;(println (format "DEBUG inherit flag -> %s" inherit))
                                         (if inherit
                                           (disable)
                                           (enable)))))]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 67)(+ y0 25)] "Bend")
    (.hide! b-curve :disabled true)
    (.hide! b-edit :disabled true)
    (.hide! tx2 :disabled true)
    (.hide! tx3 :disabled true)
    (.colors! dbar (dbar-inactive-color)(dbar-active-color))
    (.display! dbar "+200" false)
    (reify cadejo.ui.node-observer/NodeObserver

      (set-parent-editor! [this ed]
        (reset! ed* ed))

      (components [this] {})

      (component [this key] nil)
      
     (sync-ui! [this]

       )
     )))


; ---------------------------------------------------------------------- 
;                              Pressure Panel

;; (defn- create-pressure-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         w 171
;;         h 253
;;         [x0 y0] p0
;;         b-curve (curve-button drw :pressure [(+ x0 93)(+ y0 40)]
;;                               :click-action (fn [b _]
;;                                               (let [c (second (msb/current-multistate-button-state b))]
;;                                                 (.status! ed (format "[:pressure-curve] -> %s" c))
;;                                                 (.put-property! (.node ed) :pressure-curve c))))
;;         dbar-bias (dbar/displaybar root (+ x0 32)(+ y0 92) 4 :matrix
;;                                    :cell-height dbar-cell-height
;;                                    :cell-width dbar-cell-width)

;;         bias-validator (fn [s](let [n (math/str->float s)]
;;                                 (if n
;;                                   (and (<= -1.0 n)(<= n 1.0))
;;                                   false)))
;;         bias-callback (fn [dbar]
;;                         (let [r (int (math/str->float (.current-display dbar)))]
;;                           (.status! ed (format "[:pressure-bias] -> %s" r))
;;                           (.put-property! (.node ed) :pressure-bias r)))
;;         b-edit-bias (edit-button widgets [(+ x0 90)(+ y0 125)] dbar-bias
;;                                  "Pressure Bias +/- 1.0"
;;                                  bias-validator
;;                                  bias-callback)
;;         dbar-scale (dbar/displaybar root (+ x0 32)(+ y0 160) 4 :matrix
;;                                     :cell-height dbar-cell-height
;;                                     :cell-width dbar-cell-width)
       
;;         scale-validator (fn [s](let [n (math/str->float s)]
;;                                  (if n
;;                                    (and (<= 0 n)(<= n 4))
;;                                    false)))
;;         scale-callback (fn [dbar]
;;                          (let [r (int (math/str->float (.current-display dbar)))]
;;                            (.status! ed (format "[:pressure-scale] -> %s" r))
;;                            (.put-property! (.node ed) :pressure-scale r)))
;;         b-edit-scale (edit-button widgets [(+ x0 90)(+ y0 195)] dbar-scale
;;                                   "Pressure Scale (0.0 4.0)"
;;                                   scale-validator
;;                                   scale-callback)
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :pressure-curve nil)
;;                     (.put-property! node :pressure-bias nil)
;;                     (.put-property! node :pressure-scale nil)
;;                     (doseq [w [b-curve dbar-bias b-edit-bias dbar-scale b-edit-scale]]
;;                       (.disable! w false))
;;                     (.render drw)))
;;         enable (fn []
;;                  (let [node (.node ed)
;;                        curve (second (msb/current-multistate-button-state b-curve))
;;                        bias (math/str->float (.current-display dbar-bias))
;;                        scale (math/str->float (.current-display dbar-scale))]
;;                    (.put-property! node :pressure-curve curve)
;;                    (.put-property! node :pressure-bias bias)
;;                    (.put-property! node :pressure-scale scale)
;;                    (doseq [w [b-curve dbar-bias b-edit-bias dbar-scale b-edit-scale]]
;;                      (.enable! w false))
;;                    (.render drw)))
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 225)]
;;                                      (fn [b _]
;;                                        (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))]
;;     (border root p0 [(+ x0 w)(+ y0 h)])
;;     (title drw [(+ x0 50)(+ y0 25)] "Pressure")
;;     (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
;;     (text drw [(+ x0 24)(+ y0 140)] "Bias" :style :sans :size 6)
;;     (text drw [(+ x0 24)(+ y0 210)] "Scale" :style :sans :size 6)
;;     (.colors! dbar-bias (dbar-inactive-color)(dbar-active-color))
;;     (.colors! dbar-scale (dbar-inactive-color)(dbar-active-color))
;;     (.display! dbar-bias "+0.0" false)
;;     (.display! dbar-scale "1.00" false)))


; ---------------------------------------------------------------------- 
;                              Velocity Panel

;; (defn- create-velocity-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         [x0 y0] p0
;;         w 171
;;         h 126 
;;         x1 (+ x0 w)
;;         y1 (+ y0 h)
;;         b-curve (curve-button drw :bend [(+ x0 93)(+ y0 40)]
;;                               :click-action (fn [b _]
;;                                               (let [c (second (msb/current-multistate-button-state b))]
;;                                                 (.status! ed (format "[:velocity-curve] -> %s" c))
;;                                                 (.put-property! (.node ed) :velocity-curve c))))
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :velocity-curve nil)
;;                     (.disable! b-curve false)
;;                     (.render drw)))
;;         enable (fn []
;;                  (let [node (.node ed)]
;;                    (.put-property! node :velocity-curve nil)
;;                    (.enable! b-curve false)
;;                    (.render drw)))
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 98)]
;;                                      (fn [b _]
;;                                        (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))]
;;     (border root p0 [x1 y1])
;;     (title drw [(+ x0 50)(+ y0 25)] "Velocity")
;;     (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)))


; ---------------------------------------------------------------------- 
;                              Transpose Panel

;; (defn- create-transpose-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         [x0 y0] p0
;;         w 171
;;         h 127
;;         dbar (dbar/displaybar root (+ x0 60)(+ y0 42) 3 :matrix  
;;                               :cell-height dbar-cell-height
;;                               :cell-width dbar-cell-width)
;;         validator (fn [s]
;;                     (let [n (math/str->float s)]
;;                       (if n
;;                         (and (<= -36 n)(<= n 36))
;;                         nil)))
;;         callback (fn [dbar]
;;                    (let [node (.node ed)
;;                          xpose (int (math/str->float (.current-display dbar)))]
;;                      (.status! ed (format "[:transpose] -> %s" xpose))
;;                      (.put-property! node :transpose xpose)))
;;         click-action (fn [b _]
;;                        (dbar/displaybar-dialog dbar "Transpose -/+ 36"
;;                                                :validator validator
;;                                                :callback callback))
;;         b-edit (edit-button widgets [(+ x0 80)(+ y0 80)] dbar
;;                             "Transpose -/+ 36"
;;                             validator
;;                             callback)
                            
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :transpose nil)
;;                     (doseq [w [dbar b-edit]]
;;                       (.disable! w false))
;;                     (.render drw)))
;;         enable (fn []
;;                  (let [node (.node ed)
;;                        xpose (int (math/str->float (.current-display dbar)))]
;;                    (.put-property! node :transpose xpose)
;;                    (doseq [w [dbar b-edit]]
;;                      (.enable! w false))
;;                    (.render drw)))
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 98)]
;;                                      (fn [b _]
;;                                        (let [state (msb/current-multistate-button-state b)
;;                                              inherit (= (first state) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))]
;;     (border root p0 [(+ x0 w)(+ y0 h)])
;;     (title drw [(+ x0 50)(+ y0 25)] "Transpose")
;;     (.colors! dbar (dbar-inactive-color)(dbar-active-color))
;;     (.display! dbar "  0" false)))


; ---------------------------------------------------------------------- 
;                              Db Scale Panel

;; (defn- create-db-scale-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         [x0 y0] p0
;;         w 85
;;         h 253
;;         xc (+ x0 (* 0.5 w))
;;         tick-length 22
;;         tick-x0 (- xc (* 0.5 tick-length))
;;         tick-x1 (+ xc (* 0.5 tick-length))
;;         y-db0 (+ y0 75)
;;         y-db60 (+ y0 200)
;;         y-db12 (- y-db60 slider-length)
;;         tx-value (text drw [(+ x0 24)(+ y0 40)] (format "%+3d db" 0)
;;                        :style :mono 
;;                        :size 5)
;;         s-scale (slider/slider widgets [xc (+ y0 200)] slider-length -60 12
;;                                :gap 8
;;                                :track1-color (passive-track-color)
;;                                :track2-color (active-track-color)
;;                                :rim-color [0 0 0 0]
;;                                :value-hook (fn [n]
;;                                              (let [q (int (/ n 3))
;;                                                    db (* 3 q)]
;;                                                db))
;;                                :drag-action (fn [b _]
;;                                                  (let [node (.node ed)
;;                                                        db (slider/get-slider-value b)]
;;                                                    (.status! ed (format "[:dbscale] -> %s" db))
;;                                                    (.put-property! tx-value :text (format "%+3d db" db))
;;                                                    (.put-property! node :dbscale db))))
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :dbscale nil)
;;                     (.disbale! s-scale true false)
;;                     (.render drw)))
;;         enable (fn []
;;                  (let [node (.node ed)
;;                        db (slider/get-slider-value s-scale)]
;;                    (.put-property! node :dbscale db)))
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 225)]
;;                                      (fn [b _]
;;                                        (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))]
;;     (border root p0 [(+ x0 w)(+ y0 h)])
;;     (title drw [(+ x0 6)(+ y0 25)] "DB Scale")
;;     (line/line root [tick-x0 y-db12][tick-x1 y-db12] :color (major-tick-color))
;;     (line/line root [tick-x0 y-db0][tick-x1 y-db0] :color (major-tick-color))
;;     (line/line root [tick-x0 y-db60][tick-x1 y-db60] :color (major-tick-color))
;;     (text drw [(+ tick-x0 27)(+ y-db12 4)] "+12" :style :mono :size 5)
;;     (text drw [(+ tick-x0 27)(+ y-db0 4)] " 0" :style :mono :size 5)
;;     (text drw [(+ tick-x0 27)(+ y-db60 4)] "-60" :style :mono :size 5)))


; ---------------------------------------------------------------------- 
;                            Tuning Tabel Panel

;; (defn- create-scale-selection-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         w 266
;;         h 253
;;         [x0 y0] p0
;;         x1 (+ x0 w)
;;         y1 (+ y0 h)
;;         p1 [x1 y1]
;;         dbar (dbar/displaybar root (+ x0 12)(+ y0 92) 11 :matrix
;;                               :cell-height dbar-cell-height
;;                               :cell-width dbar-cell-width)
;;         validator (fn [s] true)  ;; ISSUE NOT IMPLEMENTED
;;         callback (fn [dbar]   ;; ISSUE NOT IMPLEMENTED
;;                    (umsg/warning "properties-panel create-scale-selection-panel callback is NOT IMPLEMENTED")
;;                    )
;;         b-edit (edit-button widgets [(+ x0 110)(+ y0 128)] dbar
;;                                      "Enter Tuning Tabel Name"
;;                                      validator
;;                                      callback)
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :scale-id nil)
;;                     (doseq [w [dbar b-edit]]
;;                       (.disable! w false))
;;                     (.render drw)))
;;         enable (fn []
;;                  (let [node (.node ed)
;;                        sid (keyword (.current-display dbar))]
;;                    (.put-property! node :scale-id sid)
;;                    (doseq [w [dbar b-edit]]
;;                      (.enable! w false))
;;                     (.render drw)))
;; 
;; 
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 225)]
;;                                      (fn [b _]
;;                                        (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))]
;;     (title drw [(+ x0 78)(+ y0 25)] "Tuning Table")
;;     (border root p0 p1)
;;     (.colors! dbar (dbar-inactive-color)(dbar-active-color))
;;     (.display! dbar "EQ-12" false)))

; ---------------------------------------------------------------------- 
;                              Keyrange Panel

;; (defn- create-keyrange-panel [drw p0 ed]
;;   (let [root (.root drw)
;;         widgets (.widget-root drw)
;;         w 864
;;         h 127
;;         [x0 y0] p0
;;         x1 (+ x0 w)
;;         y1 (+ y0 h)
;;         x2 (+ x0 15)
;;         p1 [x1 y1]
;;         yc (int (math/mean y0 y1))
;;         slider-length (- w 30)
;;         minor-length 8
;;         minor-delta ( / slider-length 128.0)
;;         major-length 16
;;         major-delta (* 12 minor-delta)
;;         txt-range (text drw [(+ x0 8)(+ y0 25)] (format "Range [%3d %3d]" 0 127)
;;                         :style :mono
;;                         :size 5)
;;         drag-action (fn [s _]
;;                       (let [node (.node ed)
;;                             r (dslider/get-dual-slider-values s)]
;;                         (.put-property! txt-range :text (format "Range [%3d %3d]" (first r)(second r)))
;;                         (.put-property! node :key-range r)
;;                         (.status! ed (format "[:key-range] -> %s" r))))
;; 
;; 
;;         slider (dslider/dual-slider widgets [x2 yc] slider-length 0 128 
;;                                     :orientation :horizontal
;;                                     :gap 12
;;                                     :track1-color (passive-track-color)
;;                                     :track4-color (active-track-color)
;;                                     :rim-color [0 0 0 0]
;;                                     :value-hook (fn [n](int n))
;;                                     :drag-action drag-action
;;                                     )
;; 
;;         disable (fn []
;;                   (let [node (.node ed)]
;;                     (.put-property! node :key-range nil)
;;                     (doseq [w [slider]]
;;                       (.disable! w false))
;;                     (.render drw)))
;;         enable (fn []
;;                  (doseq [w [slider]]
;;                    (.enable! w false))
;;                  (let [node (.node ed)
;;                        kr (dslider/get-dual-slider-values slider)]
;;                    (.put-property! node :key-range kr)
;;                    (.status! ed (format "[:key-range] -> %s" kr))))
;; 
;;         cb-inherit (inherit-checkbox widgets [(+ x0 8)(+ y0 98)]
;;                                      (fn [b _]
;;                                        (let [state (msb/current-multistate-button-state b)
;;                                              inherit (= (first state) 1)]
;;                                          (if inherit
;;                                            (disable)
;;                                            (enable)))))
;;         ]
;; 
;;     ;; Draw major ticks
;;     (let [kn* (atom 0)
;;           x* (atom x2)]
;;       (while (< @kn* 128)
;;         (let [kn @kn*
;;               x @x*
;;               text-offset (cond (< kn 99) -14
;;                                 :default -10)]
;;           (line/line root [x yc][x (+ yc major-length)] :color (major-tick-color))
;;           (text drw [(+ x text-offset)(+ yc major-length 12)](format "%3d" kn)
;;                 :style :mono :size 6)
;;           (swap! kn* (fn [q](+ q 12)))
;;           (swap! x* (fn [q](+ q major-delta))))))
;;     ;; Draw minor ticks
;;     (let [kn* (atom 0)
;;           x* (atom x2)]
;;       (while (< @kn* 128)
;;         (let [kn @kn*
;;               x @x*]
;;           (if (not (zero? (rem kn 12)))
;;             (line/line root [x yc][x (- yc minor-length)] :color (minor-tick-color))
;;             )
;;           (swap! kn* inc)
;;           (swap! x* (fn [q](+ q minor-delta))))))
;; 
;;     (title drw [(+ x0 387)(+ y0 25)] "Key Range")
;;     (border root p0 p1)
;;     ))


; ---------------------------------------------------------------------- 
;                           MIDI Properties Panel

(defn midi-properties-panel []
  (let [editor* (atom nil)
        x 10
        y 10
        drw (sgwr.elements.drawing/native-drawing drawing-width drawing-height)

        bend-subpan (create-bend-controls drw [(+ x 10) (+ y 0)])
        ;; xx2 (create-pressure-panel drw [(+ x 181) (+ y 0)] ed)
        ;; xx3 (create-velocity-panel drw [(+ x 352) (+ y 0)] ed)
        ;; xx4 (create-transpose-panel drw [(+ x 352) (+ y 126)] ed)
        ;; xx5 (create-db-scale-panel drw [(+ x 523) (+ y 0)] ed)
        ;; xx6 (create-scale-selection-panel drw [(+ x 608) (+ y 0)] ed)
        ;; xx7 (create-keyrange-panel drw [(+ x 10)(+ y 253)] ed)
        b-help (button/mini-icon-button (.widget-root drw) [(+ x 840) (+ y 8)]
                                   (icon-prefix) :help
                                   :rim-color (border-color)
                                   :rim-width 1.0)
        sub-panels [bend-subpan]
        ]
    (.background! drw (background-color))
    (.render drw)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! editor* ed)
        (doseq [sp sub-panels]
          (.set-parent-editor! sp ed)))

      (components [this] {:drawing drw
                          :canvas (.canvas drw)
                          :pan-main (.canvas drw)})
        

      (component [this key]
        (get (.components this) key))

      (sync-ui! [this]
        (doseq [s [bend-subpan]]
          (.sync-ui! s))
        (.render drw)))))


;; TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST 


(defprotocol DummyNode 
  (get-property [this key])
  (put-property! [this key v])
  (dump [this]))

(defprotocol DummyEditor
  (node [this])
  (dump [this])
  (status! [this msg]))

(defn dummy-node []
  (let [properties* (atom {:bend-curve :linear
                           :bend-range 200})]
  (reify DummyNode
    (dump [this]
      (println "DUMP DummyNode")
      (doseq [p (seq @properties*)]
        (println "\t" p)))

    (put-property! [this key val]
      (swap! properties* (fn [q](assoc q key val))))

    (get-property [this key]
      (get @properties* key)))))
      

(defn dummy-editor []
  (let [n (dummy-node)]
    (reify DummyEditor
      (node [this] n)
      (dump [this]
        (.dump n))
      (status! [this msg]
        (println (format "DUMMY STATUS: %s" msg))))))

(def dumed (dummy-editor))
(def mpp (midi-properties-panel))
(.set-parent-editor! mpp dumed)

(defn ?? [](.dump dumed))

(def pan-main (ss/horizontal-panel :items [(.component mpp :canvas)]))
(def f (ss/frame :title "test"
                 :content pan-main
                 :on-close :dispose
                 :size [1000 :by 500]))
(ss/show! f)

(defn rl [](use 'cadejo.ui.midi.properties-panel :reload))
(defn rla [](use 'cadejo.ui.midi.properties-panel :reload-all))
(defn exit [](System/exit 0))

(println)  
