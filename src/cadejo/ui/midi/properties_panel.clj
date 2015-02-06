(ns cadejo.ui.midi.properties-panel
  (:use [cadejo.util.trace])
  (:require [cadejo.ui.node-observer])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.indicators.displaybar :as dbar :reload true])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.tools.dual-slider :as dslider])
  (:require [sgwr.tools.multistate-button :as msb])  
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))


(defn- background-color [] :black)
(defn- icon-prefix [] :gray)
(defn- text-color [] :gray)
(defn- inherited-text-color [] :green)
(defn- title-color [] (text-color))
(defn- dbar-inactive-color [] [77 58 83])
(defn- dbar-active-color [] [245 244 207])
(defn- border-color [] (text-color))
(defn- major-tick-color [] (text-color))
(defn- minor-tick-color [] (text-color))
(defn- passive-track-color [] (text-color))
(defn- active-track-color [] :yellow)

(def drawing-width 970)
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
  (let [wr (.tool-root drawing)
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
(defn- text [drawing p txt & {:keys [style size]
                             :or {style :serif
                                  size 8}}]
  (text/text (.root drawing) p txt :style style :size size :color (text-color)))

(defn- inherited-text [drawing p txt]
  (text/text (.root drawing) p txt
             :style :mono
             :size 5
             :color (inherited-text-color)))


;; Create edit button
;;
(defn edit-button [parent p0 dbar msg validator callback]
  (let [click-action (fn [& _]
                       (dbar/displaybar-dialog dbar msg
                                               :validator validator
                                               :callback callback))]
    (button/mini-icon-button parent p0 (icon-prefix) :edit
                             :click-action click-action)))


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
        tool (.tool-root drw)
        w 175
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
        dbar (dbar/displaybar root (+ x0 17)(+ y0 92) 5 :matrix 
                              :cell-height dbar-cell-height
                              :cell-width dbar-cell-width)
        b-edit (edit-button tool [(+ x0 130)(+ y0 92)] dbar 
                            "Bend Range in Cents +/- 2400"
                            range-validator
                            range-callback)
        tx2 (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
        tx3 (text drw [(+ x0 17)(+ y0 135)] "Range" :style :sans :size 6)
        tx-inherited (inherited-text drw [(+ x0 8)(+ y0 200)] "") ; displays inherited value
                           
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :bend-curve nil)
                    (.put-property! node :bend-range nil)
                    (.put-property! tx-inherited :text 
                                    (format "Curve %s Range %s"
                                            (.get-property node :bend-curve)
                                            (.get-property node :bend-range)))
                    (doseq [w [b-curve dbar b-edit tx2 tx3]]
                      (.disable! w false))
                    (.render drw)))

        enable (fn []
                 (let [node (get-node)
                       curve (second (msb/current-multistate-button-state b-curve))
                       range (int (math/str->float (.current-display dbar)))]
                   (.put-property! tx-inherited :text " ")
                   (.put-property! node :bend-curve curve)
                   (.put-property! node :bend-range range)
                   (doseq [w [b-curve dbar b-edit tx2 tx3]]
                     (.enable! w false))
                   (.render drw)))

        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (msb/checkbox-selected? b)]
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

      (widgets [this] {})

      (widget [this key] nil)
      
     (sync-ui! [this]

       )
     )))


; ---------------------------------------------------------------------- 
;                              Pressure Panel

(defn- create-pressure-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        w 175
        h 253
        [x0 y0] p0
        b-curve (curve-button drw :pressure [(+ x0 93)(+ y0 40)]
                              :click-action (fn [b _]
                                              (let [c (second (msb/current-multistate-button-state b))]
                                                (.status! @ed* (format "[:pressure-curve] -> %s" c))
                                                (.put-property! (get-node) :pressure-curve c))))
        dbar-bias (dbar/displaybar root (+ x0 32)(+ y0 92) 4 :matrix
                                   :cell-height dbar-cell-height
                                   :cell-width dbar-cell-width)

        bias-validator (fn [s](let [n (math/str->float s)]
                                (if n
                                  (and (<= -1.0 n)(<= n 1.0))
                                  false)))
        bias-callback (fn [dbar]
                        (let [r (math/str->float (.current-display dbar))]
                          (.status! @ed* (format "[:pressure-bias] -> %s" r))
                          (.put-property! (get-node) :pressure-bias r)))
        b-edit-bias (edit-button tool [(+ x0 125)(+ y0 90)] dbar-bias
                                 "Pressure Bias +/- 1.0"
                                 bias-validator
                                 bias-callback)
        dbar-scale (dbar/displaybar root (+ x0 32)(+ y0 142) 4 :matrix
                                    :cell-height dbar-cell-height
                                    :cell-width dbar-cell-width)
       
        scale-validator (fn [s](let [n (math/str->float s)]
                                 (if n
                                   (and (<= 0 n)(<= n 4))
                                   false)))
        scale-callback (fn [dbar]
                         (let [r (math/str->float (.current-display dbar))]
                           (.status! @ed* (format "[:pressure-scale] -> %s" r))
                           (.put-property! (get-node) :pressure-scale r)))
        b-edit-scale (edit-button tool [(+ x0 125)(+ y0 142)] dbar-scale
                                  "Pressure Scale (0.0 4.0)"
                                  scale-validator
                                  scale-callback)
        tx-inherit-curve (inherited-text drw [(+ x0 8)(+ y0 200)] "")
        tx-inherit-values (inherited-text drw [(+ x0 8)(+ y0 212)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :pressure-curve nil)
                    (.put-property! node :pressure-bias nil)
                    (.put-property! node :pressure-scale nil)
                    (.put-property! tx-inherit-curve :text
                                   (format "Curve %s" (.get-property node :pressure-curve)))
                    (.put-property! tx-inherit-values :text
                                   (format "Bias %+4.1f Scale %4.2f"
                                           (float (.get-property node :pressure-bias))
                                           (float (.get-property node :pressure-scale))))
                    (doseq [w [b-curve dbar-bias b-edit-bias dbar-scale b-edit-scale]]
                      (.disable! w false))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       curve (second (msb/current-multistate-button-state b-curve))
                       bias (math/str->float (.current-display dbar-bias))
                       scale (math/str->float (.current-display dbar-scale))]
                   (.put-property! node :pressure-curve curve)
                   (.put-property! node :pressure-bias bias)
                   (.put-property! node :pressure-scale scale)
                   (.put-property! tx-inherit-curve :text "")
                   (.put-property! tx-inherit-values :text "")
                   (doseq [w [b-curve dbar-bias b-edit-bias dbar-scale b-edit-scale]]
                     (.enable! w false))
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))
      
        ]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 50)(+ y0 25)] "Pressure")
    (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
    (text drw [(+ x0 28)(+ y0 132)] "Bias" :style :sans :size 6)
    (text drw [(+ x0 24)(+ y0 188)] "Scale" :style :sans :size 6)
    (.colors! dbar-bias (dbar-inactive-color)(dbar-active-color))
    (.colors! dbar-scale (dbar-inactive-color)(dbar-active-color))
    (.display! dbar-bias "+0.0" false)
    (.display! dbar-scale "1.00" false)
    (reify cadejo.ui.node-observer/NodeObserver

      (set-parent-editor! [this ed]
        (reset! ed* ed))

      (widgets [this] {})

      (widget [this key] nil)

      (sync-ui! [this]
        )
      )))


; ---------------------------------------------------------------------- 
;                              Velocity Panel

(defn- create-velocity-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        [x0 y0] p0
        w 175
        h 126 
        x1 (+ x0 w)
        y1 (+ y0 h)
        b-curve (curve-button drw :velocity [(+ x0 93)(+ y0 40)]
                              :click-action (fn [b _]
                                              (let [c (second (msb/current-multistate-button-state b))]
                                                (.status! @ed* (format "[:velocity-map] -> %s" c))
                                                (.put-property! (get-node) :velocity-map c))))
        tx-inherit (inherited-text drw [(+ x0 100)(+ y0 105)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :velocity-map nil)
                    (.disable! b-curve false)
                    (.put-property! tx-inherit :text
                                    (format "%s" (.get-property node :velocity-map)))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)]
                   (.put-property! node :velocity-map nil)
                   (.enable! b-curve false)
                   (.put-property! tx-inherit :text "")
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 98)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))]
    (border root p0 [x1 y1])
    (title drw [(+ x0 50)(+ y0 25)] "Velocity")
    (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        
        )
      )))



; ---------------------------------------------------------------------- 
;                              Transpose Panel

(defn- create-transpose-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        [x0 y0] p0
        w 175
        h 127
        dbar (dbar/displaybar root (+ x0 40)(+ y0 42) 3 :matrix  
                              :cell-height dbar-cell-height
                              :cell-width dbar-cell-width)
        validator (fn [s]
                    (let [n (math/str->float s)]
                      (if n
                        (and (<= -36 n)(<= n 36))
                        nil)))
        callback (fn [dbar]
                   (let [node (get-node)
                         xpose (int (math/str->float (.current-display dbar)))]
                     (.status! @ed* (format "[:transpose] -> %s" xpose))
                     (.put-property! node :transpose xpose)))
        click-action (fn [b _]
                       (dbar/displaybar-dialog dbar "Transpose -/+ 36"
                                               :validator validator
                                               :callback callback))
        b-edit (edit-button tool [(+ x0 110)(+ y0 42)] dbar
                            "Transpose -/+ 36"
                            validator
                            callback)
        tx-inherit (inherited-text drw [(+ x0 100)(+ y0 105)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :transpose nil)
                    (doseq [w [dbar b-edit]]
                      (.disable! w false))
                    (.put-property! tx-inherit :text 
                                    (str (.get-property node :transpose)))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       xpose (int (math/str->float (.current-display dbar)))]
                   (.put-property! node :transpose xpose)
                   (doseq [w [dbar b-edit]]
                     (.enable! w false))
                   (.put-property! tx-inherit :text "")
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 98)]
                                     (fn [b _]
                                       (let [state (msb/current-multistate-button-state b)
                                             inherit (= (first state) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 50)(+ y0 25)] "Transpose")
    (.colors! dbar (dbar-inactive-color)(dbar-active-color))
    (.display! dbar "  0" false)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        
        )
      )))

; ---------------------------------------------------------------------- 
;                              Db Scale Panel

(defn- create-db-scale-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        [x0 y0] p0
        w 85
        h 253
        xc (+ x0 (* 0.5 w))
        tick-length 16
        tick-x0 (- xc (* 0.5 tick-length))
        tick-x1 (+ xc (* 0.5 tick-length))
        y-db0 (+ y0 75)
        y-db60 (+ y0 200)
        y-db12 (- y-db60 slider-length)
        tx-value (text drw [(+ x0 24)(+ y0 40)] (format "%+3d db" 0)
                       :style :mono 
                       :size 5)
        s-scale (slider/slider tool [xc (+ y0 200)] slider-length -60 12
                               :gap 8
                               :track1-color (passive-track-color)
                               :track2-color (active-track-color)
                               :rim-color [0 0 0 0]
                               :value-hook (fn [n]
                                             (let [q (int (/ n 3))
                                                   db (* 3 q)]
                                               db))
                               :drag-action (fn [b _]
                                                 (let [node (get-node)
                                                       db (slider/get-slider-value b)]
                                                   (.status! @ed* (format "[:dbscale] -> %s" db))
                                                   (.put-property! tx-value :text (format "%+3d db" db))
                                                   (.put-property! node :dbscale db))))
        tx-inherit (inherited-text drw [(+ x0 30)(+ y0 218)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :dbscale nil)
                    (.disable! s-scale false)
                    (.put-property! tx-inherit :text 
                                    (format "%+3d db" (.get-property node :dbscale)))
                    (.put-property! tx-value :text "")
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       db (slider/get-slider-value s-scale)]
                   (.put-property! node :dbscale db)
                   (.enable! s-scale false)
                   (.put-property! tx-inherit :text "")
                   (.put-property! tx-value :text (format "%+3d db" (.get-property node :dbscale)))
                   (.render drw)
                   ))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 6)(+ y0 25)] "DB Scale")
    (line/line root [tick-x0 y-db12][tick-x1 y-db12] :color (major-tick-color))
    (line/line root [tick-x0 y-db0][tick-x1 y-db0] :color (major-tick-color))
    (line/line root [tick-x0 y-db60][tick-x1 y-db60] :color (major-tick-color))
    (text drw [(+ tick-x0 27)(+ y-db12 4)] "+12" :style :mono :size 5)
    (text drw [(+ tick-x0 27)(+ y-db0 4)] " 0" :style :mono :size 5)
    (text drw [(+ tick-x0 27)(+ y-db60 4)] "-60" :style :mono :size 5)

    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        
        )
      )))


; ---------------------------------------------------------------------- 
;                            Tuning Tabel Panel

(defn- create-scale-selection-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        w 330
        h 253
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        p1 [x1 y1]
        dbar (dbar/displaybar root (+ x0 12)(+ y0 42) 12 :matrix
                              :cell-height dbar-cell-height
                              :cell-width dbar-cell-width)
        validator (fn [s] true)  ;; ISSUE NOT IMPLEMENTED
        callback (fn [dbar]   ;; ISSUE NOT IMPLEMENTED
                   (umsg/warning "properties-panel create-scale-selection-panel callback is NOT IMPLEMENTED")
                   )
        b-edit (edit-button tool [(+ x0 290)(+ y0 40)] dbar
                                     "Enter Tuning Tabel Name"
                                     validator
                                     callback)
        tx-inherit (inherited-text drw [(+ x0 90)(+ y0 235)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :scale-id nil)
                    (doseq [w [dbar b-edit]]
                      (.disable! w false))
                    (.put-property! tx-inherit :text
                                    (.get-property node :scale-id))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       sid (keyword (.current-display dbar))]
                   (.put-property! node :scale-id sid)
                   (doseq [w [dbar b-edit]]
                     (.enable! w false))
                   (.put-property! tx-inherit :text "")
                    (.render drw)))


        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))]
    (title drw [(+ x0 78)(+ y0 25)] "Tuning Table")
    (border root p0 p1)
    (.colors! dbar (dbar-inactive-color)(dbar-active-color))
    (.display! dbar "EQ-12" false) 
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        
        )
      )))

; ---------------------------------------------------------------------- 
;                              Keyrange Panel

(defn- create-keyrange-panel [drw p0]
  (let [ed* (atom nil)
        get-node (fn [](.node @ed*))
        root (.root drw)
        tool (.tool-root drw)
        w 864
        h 127
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        x2 (+ x0 15)
        p1 [x1 y1]
        yc (int (math/mean y0 y1))
        slider-length (- w 30)
        minor-length 8
        minor-delta ( / slider-length 128.0)
        major-length 16
        major-delta (* 12 minor-delta)
        tx-range (text drw [(+ x0 8)(+ y0 25)] (format "Range [%3d %3d]" 0 127)
                        :style :mono
                        :size 5
                        :color (text-color))
        tx-inherit (inherited-text drw [(+ x0 90)(+ y0 109)] "")
        drag-action (fn [s _]
                      (let [node (get-node)
                            r (dslider/get-dual-slider-values s)]
                        (.put-property! tx-range :text (format "Range [%3d %3d]" (first r)(second r)))
                        (.put-property! node :key-range r)
                        (.status! @ed* (format "[:key-range] -> %s" r))))


        slider (dslider/dual-slider tool [x2 yc] slider-length 0 127 
                                    :orientation :horizontal
                                    :gap 12
                                    :track1-color (passive-track-color)
                                    :track4-color (active-track-color)
                                    :rim-color [0 0 0 0]
                                    :value-hook (fn [n](int n))
                                    :drag-action drag-action)

        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :key-range nil)
                    (doseq [w [slider]]
                      (.disable! w false))
                    (.put-property! tx-range :text "")
                    (.put-property! tx-inherit :text
                                    (str (.get-property node :key-range)))
                    (.render drw)))
        enable (fn []
                 (doseq [w [slider]]
                   (.enable! w false))
                 (let [node (get-node)
                       kr (dslider/get-dual-slider-values slider)]
                   (.put-property! node :key-range kr)
                   (.put-property! tx-range :text (str (.get-property node :key-range)))
                   (.put-property! tx-inherit :text "")
                   (.status! @ed* (format "[:key-range] -> %s" kr))
                   (.render drw)))

        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 98)]
                                     (fn [b _]
                                       (let [state (msb/current-multistate-button-state b)
                                             inherit (= (first state) 1)]
                                         (if inherit
                                           (disable)
                                           (enable)))))
        ]

    ;; Draw major ticks
    (let [kn* (atom 0)
          x* (atom x2)]
      (while (< @kn* 128)
        (let [kn @kn*
              x @x*
              text-offset (cond (< kn 99) -14
                                :default -10)]
          (line/line root [x yc][x (+ yc major-length)] :color (major-tick-color))
          (text drw [(+ x text-offset)(+ yc major-length 12)](format "%3d" kn)
                :style :mono :size 6)
          (swap! kn* (fn [q](+ q 12)))
          (swap! x* (fn [q](+ q major-delta))))))
    ;; Draw minor ticks
    (let [kn* (atom 0)
          x* (atom x2)]
      (while (< @kn* 128)
        (let [kn @kn*
              x @x*]
          (if (not (zero? (rem kn 12)))
            (line/line root [x yc][x (- yc minor-length)] :color (minor-tick-color))
            )
          (swap! kn* inc)
          (swap! x* (fn [q](+ q minor-delta))))))

    (title drw [(+ x0 387)(+ y0 25)] "Key Range")
    (border root p0 p1)
    (reify cadejo.ui.node-observer/NodeObserver

      (set-parent-editor! [this ed]
        (reset! ed* ed))

      (widgets [this] {})

      (widget [this key] nil)
      
     (sync-ui! [this]

       )
     )))


; ---------------------------------------------------------------------- 
;                           MIDI Properties Panel

(defn midi-properties-panel []
  (let [editor* (atom nil)
        x 10
        y 10
        drw (sgwr.components.drawing/native-drawing drawing-width drawing-height)
        bend-subpan (create-bend-controls drw [(+ x 10) (+ y 0)])
        pressure-subpan (create-pressure-panel drw [(+ x 185) (+ y 0)])
        velocity-subpan (create-velocity-panel drw [(+ x 360) (+ y 0)])
        transpose-subpan (create-transpose-panel drw [(+ x 360) (+ y 126)])
        dbscale-subpan (create-db-scale-panel drw [(+ x 535) (+ y 0)])
        ttab-subpan (create-scale-selection-panel drw [(+ x 620) (+ y 0)])
        krange-subpan (create-keyrange-panel drw [(+ x 10)(+ y 253)])
        b-help (do 
                 (border (.root drw) [(+ x 874)(+ y 253)] [(+ x 950)(+ y 380)])
                 (title drw [(+ x 894)(+ y 277)] "Help")
                 (button/icon-button (.tool-root drw) [(+ x 890) (+ y 293)]
                                     (icon-prefix) :general :help
                                     :rim-color [0 0 0 0]
                                     :rim-width 1.0))
        sub-panels [bend-subpan pressure-subpan velocity-subpan transpose-subpan
                    dbscale-subpan ttab-subpan krange-subpan]
        ]
    (.background! drw (background-color))
    (.render drw)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! editor* ed)
        (doseq [sp sub-panels]
          (.set-parent-editor! sp ed)))

      (widgets [this] {:drawing drw
                       :canvas (.canvas drw)
                       :pan-main (.canvas drw)})
      

      (widget [this key]
        (get (.widgets this) key))

      (sync-ui! [this]
        (doseq [s [bend-subpan]]
          (.sync-ui! s))
        (.render drw)))))


;; TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST 


;; (require '[cadejo.midi.node])

;; (defn node [parent]
;;   (let [properties* (atom {})
;;         parent* (atom parent)]
;;     (reify cadejo.midi.node/Node
;;       (node-type [this] :test)
;;       (is-root? [this] (= parent nil))
;;       (parent [this] parent)
;;       (put-property! [this key val](swap! properties* (fn [q](assoc q key val))))
;;       (remove-property! [this key](swap! properties* (fn [q](dissoc q key))))
;;       (local-property [this key](get @properties* key))
;;       (get-property [this key default]
;;         (let [locval (.local-property this key)]
;;           (or locval 
;;               (and parent (.get-property parent key))
;;               default)))
;;       (get-property [this key]
;;         (.get-property this key nil))
;;       (properties [this local-only] nil) ;; NOT IMPLEMENTESD
;;       (get-editor [this] nil) ;; NOT IMPLEMENTED
;;       (rep-tree [this depth]
;;         (doseq [k (sort (.keys @properties*))]
;;           (println (format "[%-18s] -> %s" k (.get-property this k)))))
;;       )))
      
        
;; (def s (node nil))
;; (.put-property! s :id :scene)
;; (.put-property! s :velocity-map :linear)
;; (.put-property! s :scale-id :eq-12)
;; (.put-property! s :dbscale 0)
;; (.put-property! s :transpose 0)
;; (.put-property! s :key-range [0 127])
;; (.put-property! s :bend-curve :linear)
;; (.put-property! s :bend-range 200)
;; (.put-property! s :pressure-curve :linear)
;; (.put-property! s :pressure-scale 1.0)
;; (.put-property! s :pressure-bias 0.0)

;; (def c (node s))
      


;; (defprotocol DummyEditor
;;   (node [this])
;;   (dump [this])
;;   (status! [this msg]))


;; (defn dummy-editor []
;;   (let []
;;     (reify DummyEditor
;;       (node [this] c)
;;       (dump [this]
;;         (.dump c))
;;       (status! [this msg]
;;         (println (format "DUMMY STATUS: %s" msg))))))

;; (def dumed (dummy-editor))
;; (def mpp (midi-properties-panel))
;; (.set-parent-editor! mpp dumed)

;; (defn ?? [](.dump dumed))

;; (def pan-main (ss/horizontal-panel :items [(.widget mpp :canvas)]))
;; (def f (ss/frame :title "test"
;;                  :content pan-main
;;                  :on-close :dispose
;;                  :size [1400 :by 500]))
;; (ss/show! f)

;; (defn rl [](use 'cadejo.ui.midi.properties-panel :reload))
;; (defn rla [](use 'cadejo.ui.midi.properties-panel :reload-all))
;; (defn exit [](System/exit 0))

;; (println)  
