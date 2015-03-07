(ns cadejo.ui.midi.properties-panel
  "Provides editor for common MIDI parameters:
   bend, pressure, velocity, transpose, dbscale, tuning-table selection
   and key-range.  Also defines several functions and values used by 
   cc-properties-panel."
  (:require [cadejo.ui.node-observer])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.tools.dual-slider :as dslider])
  (:require [sgwr.tools.multistate-button :as msb])  
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def drawing-width 970)
(def drawing-height 400)
(def dbar-cell-width 18)
(def dbar-cell-height 28)
;(def dbar-style :basic)
(def slider-length 150)
(def curve-button-states [[:zero :curve :zero  0]
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

(def inv-curve-map {:zero 0, :half 1, :one 2,
                    :linear 3, :quadratic 4, :cubic 5, :quartic 6,
                    :convex2 7, :convex4 8, :convex6 9,
                    :logitstic 10, :logistic2 11,
                    :ilinear 12, :iquadratic 13, :icubic 14, 
                    :iconvex2 15, :iconvex4 16, :iconvex6 17,
                    :ilogistic 18, :ilogistic2 19})

;; create curve multistate button
;;
(defn curve-button [drawing id p0 & {:keys [click-action]
                                      :or {click-action nil}}]
  (let [wr (.tool-root drawing)
        cs (config/current-skin)
        prefix (cond (= cs "Twilight") :gray :default (lnf/icon-prefix))
        b (msb/icon-multistate-button wr p0 curve-button-states :id id
                                      :icon-prefix prefix
                                      :occluder-color (lnf/occluder-color)
                                      :click-action click-action)]
    b))

;; create title text
;;
(defn title [drawing p txt]
  (let [obj (text/text (.root drawing) p txt
                       :style :serif
                       :size 8
                       :color (lnf/title-color))]
    obj))
                       
;; create general text
;;
(defn text [drawing p txt & {:keys [style size]
                             :or {style :serif
                                  size 8}}]
  (text/text (.root drawing) p txt :style style :size size :color (lnf/text-color)))

(defn inherited-text [drawing p txt]
  (text/text (.root drawing) p txt
             :style :mono
             :size 5
             :color (lnf/text-color)))

;; Create edit button
;;
(defn edit-button [parent p0 dbar msg validator callback]
  (let [click-action (fn [& _]
                       (dbar/displaybar-dialog dbar msg
                                               :validator validator
                                               :callback callback))]
    (button/mini-icon-button parent p0 (lnf/icon-prefix) :edit
                             :occluder-color (lnf/occluder-color)
                             :click-action click-action)))

;; Create inherit checkbox
;;
(defn inherit-checkbox [parent p0 click-action]
  (let [stylemap (lnf/checkbox)
        cb (msb/checkbox parent p0 "Inherit"
                         :text-color (lnf/text-color)
                         :text-size 6
                         :rim-radius (:rim-radius stylemap)
                         :rim-color (:rim-color stylemap)
                         :selected-check [(:check-color stylemap)(:check-style stylemap)(:check-size stylemap)]
                         :click-action click-action)]
    cb))

;; Create sub-panel border
;;
(defn border [parent p0 p1]
  (let [r (rect/rectangle parent p0 p1
                          :width 1.0
                          :color (lnf/major-border-color))]
    (.put-property! r :corner-radius 18)
    r))


(defn displaybar [parent x0 y0 ccount]
  (let [b (dbar/displaybar parent x0 y0 ccount (lnf/dbar-style)
                           :occluder-color (lnf/occluder-color)
                           :cell-height dbar-cell-height
                           :cell-width dbar-cell-width)]
    (.colors! b (lnf/dbar-inactive-color)(lnf/dbar-active-color))
    b))

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
        dbar (displaybar root (+ x0 17)(+ y0 92) 5)
        b-edit (edit-button tool [(+ x0 130)(+ y0 92)] dbar 
                            "Bend Range in Cents +/- 2400"
                            range-validator
                            range-callback)
        tx2 (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
        tx3 (text drw [(+ x0 17)(+ y0 135)] "Range" :style :sans :size 6)
        tx-inherited (inherited-text drw [(+ x0 8)(+ y0 200)] "") ; displays inherited value
        components [b-curve dbar b-edit tx2 tx3]
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :bend-curve nil)
                    (.put-property! node :bend-range nil)
                    (.put-property! tx-inherited :text 
                                    (format "Curve %s Range %s"
                                            (.get-property node :bend-curve)
                                            (.get-property node :bend-range)))
                    (doseq [w components]
                      (.disable! w false))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       curve (second (msb/current-multistate-button-state b-curve))
                       range (int (math/str->float (.current-display dbar)))]
                   (.put-property! tx-inherited :text "")
                   (.put-property! node :bend-curve curve)
                   (.put-property! node :bend-range range)
                   (doseq [w components]
                     (.enable! w false))
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (msb/checkbox-selected? b)]
                                         (if inherit
                                           (disable)
                                           (enable))))) ]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 67)(+ y0 25)] "Bend")
    (.hide! b-curve :disabled true)
    (.hide! b-edit :disabled true)
    (.hide! tx2 :disabled true)
    (.hide! tx3 :disabled true)
    (.display! dbar "+200" false)
    (reify cadejo.ui.node-observer/NodeObserver

      (set-parent-editor! [this ed]
        (reset! ed* ed))

      (widgets [this] {})

      (widget [this key] nil)
      
     (sync-ui! [this]
       (let [node (get-node)
             crv (.local-property node :bend-curve)
             rng (.local-property node :bend-range)]
         (if (not crv)
           (do 
             (doseq [w components](.disable! w false))
             (msb/select-checkbox! cb-inherit true false)
             (.put-property! tx-inherited :text (format "%s range %s"
                                                        (.get-property node :bend-curve)
                                                        (.get-property node :bend-range))))
           (let [crv (.get-property node :bend-curve)]
             (doseq [w components](.enable! w false))
             (msb/select-checkbox! cb-inherit false false)
             (.put-property! tx-inherited :text "")
             (msb/set-multistate-button-state! b-curve (get inv-curve-map crv 0) false)
             (.display! dbar (str rng) false))) )))))


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
        dbar-bias (displaybar root (+ x0 32)(+ y0 92) 4)
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
        dbar-scale (displaybar root (+ x0 32)(+ y0 142) 4)
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
        components [b-curve dbar-bias b-edit-bias dbar-scale b-edit-scale]
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
                    (doseq [w components](.disable! w false))
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
                   (doseq [w components](.enable! w false))
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable))))) ]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 50)(+ y0 25)] "Pressure")
    (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
    (text drw [(+ x0 28)(+ y0 132)] "Bias" :style :sans :size 6)
    (text drw [(+ x0 24)(+ y0 188)] "Scale" :style :sans :size 6)
    (.display! dbar-bias "+0.0" false)
    (.display! dbar-scale "1.00" false)
    (reify cadejo.ui.node-observer/NodeObserver

      (set-parent-editor! [this ed]
        (reset! ed* ed))

      (widgets [this] {})

      (widget [this key] nil)

      (sync-ui! [this]
        (let [node (get-node)]
          (if (not (.local-property node :pressure-curve))
            (do
              (doseq [w components](.disable! w false))
              (msb/select-checkbox! cb-inherit true false)
              (.put-property! tx-inherit-curve :text (str (.get-property node :pressure-curve)))
              (.put-property! tx-inherit-values :text (format "Bias %+4.1f Scale 4.2f" 
                                                         (float (.get-property node :pressure-bias))
                                                         (float (.get-property node :pressure-scale)))))
            (let [crv (.get-property node :pressure-curve)]
              (doseq [w components](.enable! w false))
              (msb/select-checkbox! cb-inherit false false)
              (.put-property! tx-inherit-curve :text "")
              (.put-property! tx-inherit-values :text "")
              (msb/set-multistate-button-state! b-curve (get inv-curve-map crv 0) false)
              (.display! dbar-bias (str (.get-property node :pressure-bias)) false)
              (.display! dbar-scale (str (.get-property node :pressure-scale)) false))))))))


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
                                           (enable))))) ]
    (border root p0 [x1 y1])
    (title drw [(+ x0 50)(+ y0 25)] "Velocity")
    (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        (let [node (get-node)]
          (if (not (.local-property node :velocity-map))
            (do
              (.disable! b-curve false)
              (msb/select-checkbox! cb-inherit true false)
              (.put-property! tx-inherit :text (str (.get-property node :velocity-map))))
            (let [crv (.get-property node :velocity-map)]
              (.enable! b-curve false)
              (msb/set-multistate-button-state! b-curve (get inv-curve-map crv 0) false)
              (msb/select-checkbox! cb-inherit false false))))) )))

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
        dbar (displaybar root (+ x0 40)(+ y0 42) 3)
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
        components [dbar b-edit]
        tx-inherit (inherited-text drw [(+ x0 100)(+ y0 105)] "")
        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :transpose nil)
                    (doseq [w components](.disable! w false))
                    (.put-property! tx-inherit :text 
                                    (str (.get-property node :transpose)))
                    (.render drw)))
        enable (fn []
                 (let [node (get-node)
                       xpose (int (math/str->float (.current-display dbar)))]
                   (.put-property! node :transpose xpose)
                   (doseq [w components](.enable! w false))
                   (.put-property! tx-inherit :text "")
                   (.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 98)]
                                     (fn [b _]
                                       (let [state (msb/current-multistate-button-state b)
                                             inherit (= (first state) 1)]
                                         (if inherit
                                           (disable)
                                           (enable))))) ]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 50)(+ y0 25)] "Transpose")
    (.display! dbar "  0" false)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        (let [node (get-node)
              xpose (.local-property node :transpose)]
          (if (not xpose)
            (do 
              (doseq [w components](.disable! w false))
              (msb/select-checkbox! cb-inherit true false)
              (.put-property! tx-inherit :text (str (.get-property node :transpose))))
            (do 
              (doseq [w components](.enable! w false))
              (msb/select-checkbox! cb-inherit false false)
              (.display! dbar (str xpose) false))))) )))

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
                               :track1-color (lnf/passive-track-color)
                               :track2-color (lnf/active-track-color)
                               :rim-color [0 0 0 0]
                               :occluder-color (lnf/occluder-color)
                               :handle-color (lnf/slider-handle-color)
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
                    (.put-property! tx-value :text "")))
                    ;(.render drw)))
        enable (fn []
                 (let [node (get-node)
                       db (slider/get-slider-value s-scale)]
                   (.put-property! node :dbscale db)
                   (.enable! s-scale false)
                   (.put-property! tx-inherit :text "")
                   (.put-property! tx-value :text (format "%+3d db" (.get-property node :dbscale)))))
                   ;(.render drw)))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable))
                                         (.render drw))))]
    (border root p0 [(+ x0 w)(+ y0 h)])
    (title drw [(+ x0 6)(+ y0 25)] "DB Scale")
    (line/line root [tick-x0 y-db12][tick-x1 y-db12] :color (lnf/major-tick-color))
    (line/line root [tick-x0 y-db0][tick-x1 y-db0] :color (lnf/major-tick-color))
    (line/line root [tick-x0 y-db60][tick-x1 y-db60] :color (lnf/major-tick-color))
    (text drw [(+ tick-x0 27)(+ y-db12 4)] "+12" :style :mono :size 5)
    (text drw [(+ tick-x0 27)(+ y-db0 4)] " 0" :style :mono :size 5)
    (text drw [(+ tick-x0 27)(+ y-db60 4)] "-60" :style :mono :size 5)

    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        (let [node (get-node)
              value (.local-property node :dbscale)]
          (if (not value)
            (do 
              (disable)
              (msb/select-checkbox! cb-inherit true false))
            (do 
              (enable)
              (msb/select-checkbox! cb-inherit false false))))) )))


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
        dbar (displaybar root (+ x0 12)(+ y0 42) 12)
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
                                    (.get-property node :scale-id))))
        enable (fn []
                 (let [node (get-node)
                       sid (keyword (.current-display dbar))]
                   (.put-property! node :scale-id sid)
                   (doseq [w [dbar b-edit]]
                     (.enable! w false))
                   (.put-property! tx-inherit :text "")))
        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 225)]
                                     (fn [b _]
                                       (let [inherit (= (first (msb/current-multistate-button-state b)) 1)]
                                         (if inherit
                                           (disable)
                                           (enable))
                                         (.render drw))))]
    (title drw [(+ x0 78)(+ y0 25)] "Tuning Table")
    (border root p0 p1)
    (.display! dbar "EQ-12" false) 
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! ed* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        (let [node (get-node)
              tt (.local-property node :scale-id)]
          (if (not tt)
            (do 
              (disable)
              (msb/select-checkbox! cb-inherit true false))
            (do
              (enable)
              (msb/select-checkbox! cb-inherit false false)))))) ))

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
                        :color (lnf/text-color))
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
                                    :track1-color (lnf/passive-track-color)
                                    :track4-color (lnf/active-track-color)
                                    :rim-color [0 0 0 0]
                                    :occluder-color (lnf/occluder-color)
                                    :handle1-color (lnf/slider-handle-color)
                                    :handle2-color (lnf/slider-handle-color)
                                    :value-hook (fn [n](int n))
                                    :drag-action drag-action)

        disable (fn []
                  (let [node (get-node)]
                    (.put-property! node :key-range nil)
                    (doseq [w [slider]]
                      (.disable! w false))
                    (.put-property! tx-range :text "")
                    (.put-property! tx-inherit :text
                                    (str (.get-property node :key-range)))))
        enable (fn []
                 (doseq [w [slider]]
                   (.enable! w false))
                 (let [node (get-node)
                       kr (dslider/get-dual-slider-values slider)]
                   (.put-property! node :key-range kr)
                   (.put-property! tx-range :text (str (.get-property node :key-range)))
                   (.put-property! tx-inherit :text "")
                   (.status! @ed* (format "[:key-range] -> %s" kr))))

        cb-inherit (inherit-checkbox tool [(+ x0 8)(+ y0 98)]
                                     (fn [b _]
                                       (let [state (msb/current-multistate-button-state b)
                                             inherit (= (first state) 1)]
                                         (if inherit
                                           (disable)
                                           (enable))
                                         (.render drw))))]
    ;; Draw major ticks
    (let [kn* (atom 0)
          x* (atom x2)]
      (while (< @kn* 128)
        (let [kn @kn*
              x @x*
              text-offset (cond (< kn 99) -14
                                :default -10)]
          (line/line root [x yc][x (+ yc major-length)] :color (lnf/major-tick-color))
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
            (line/line root [x yc][x (- yc minor-length)] :color (lnf/minor-tick-color))
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
        (let [node (get-node)
              kr (.local-property node :key-range)]
          (if (not kr)
            (do
              (disable)
              (msb/select-checkbox! cb-inherit true false))
            (do
              (enable)
              (msb/select-checkbox! cb-inherit false false))))) )))

; ********************************************************************** 
;                          General MIDI properties
; ********************************************************************** 

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
                                     (lnf/icon-prefix) :general :help
                                     :rim-color [0 0 0 0]
                                     :rim-width 1.0))
        sub-panels [bend-subpan pressure-subpan velocity-subpan transpose-subpan
                    dbscale-subpan ttab-subpan krange-subpan] 
        pan-main (ss/horizontal-panel :items [(.canvas drw)])]
    (.background! drw (lnf/background))
    (.render drw)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! editor* ed)
        (doseq [sp sub-panels]
          (.set-parent-editor! sp ed)))

      (widgets [this] {:drawing drw
                       :canvas (.canvas drw)
                       :pan-main pan-main})

      (widget [this key]
        (get (.widgets this) key))

      (sync-ui! [this]
        (doseq [s [bend-subpan pressure-subpan velocity-subpan 
                   transpose-subpan dbscale-subpan ttab-subpan 
                   krange-subpan]]
          (.sync-ui! s))
        (.render drw)))))
