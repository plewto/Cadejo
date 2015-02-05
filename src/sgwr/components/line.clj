(ns sgwr.components.line
  "Defines line components
   Attributes:
       color - 
       style - dash pattern
       width - line width
       size  - ignored
       fill  - ignored
       hide  -"
  (:require [sgwr.constants :as constants])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.components.component])
  (:import java.awt.geom.Line2D))

(defn style-fn [& args]
  (get constants/line-styles (first args) 0))

(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        [u0 v0] q0
        [u1 v1] q1]
    (java.awt.geom.Line2D$Double. u0 v0 u1 v1)))

(defn render-line [obj g2d]
  (.setStroke g2d (ustroke/stroke obj))
  (.draw g2d (shape-fn obj)))

(defn- distance-fn [obj q]
  (let [[p0 p1](.points obj)]
    (math/point-line-distance q p0 p1)))
        
(defn- update-fn [obj points]
  (let [[p0 p1] points
        [x0 y0] p0
        [x1 y1] p1
        xc (math/mean x0 x1)
        yc (math/mean y0 y1)
        pc [xc yc]]
    (.put-property! obj :midpoint pc)
    points))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]]))
    

(def ^:private line-function-map {:shape-fn shape-fn
                                  :contains-fn (constantly false)
                                  :distance-fn distance-fn
                                  :update-fn update-fn
                                  :bounds-fn bounds-fn
                                  :style-fn style-fn})

(def locked-properties [:midpoint])

(defn line [parent p0 p1 & {:keys [id color style width]
                            :or {id :new-line
                                 color (uc/color :white)
                                 style 0
                                 width 1.0}}]
  "(line parent p0 p1 :id :color :style :width)
   Create segmen twith end-points p0 and p1." 
  (let [obj (sgwr.components.component/create-component :line parent line-function-map locked-properties)]
    (if parent (.set-parent! obj parent))
    (.set-points! obj [p0 p1])
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.width! obj :default width)
    (.use-attributes! obj :default)
    obj))

;; (defn vline [parent p0 length & args]
;;   (let [[x0 y0] p0
;;         x1 x0
;;         y1 (+ y0 length)
;;         arglst* (atom [parent p0 [x1 y1]])]
;;      (doseq [a args]
;;       (swap! arglst* (fn [q](conj q a))))
;;     (apply line @arglst*)))

;; (defn hline [parent p0 length & args]
;;   (let [[x0 y0] p0
;;         x1 (+ x0 length)
;;         y1 y0
;;         arglst* (atom [parent p0 [x1 y1]])]
;;      (doseq [a args]
;;       (swap! arglst* (fn [q](conj q a))))
;;     (apply line @arglst*)))

;; ;; Define line given point, length and slope
;; ;;
;; (defn line-ps [parent p0 length slope & args]
;;   (let [[x0 y0] p0
;;         theta (math/slope->angle slope)
;;         x1 (+ x0 (* length (Math/cos theta)))
;;         y1 (+ y0 (* length (Math/sin theta)))
;;         arglst* (atom [parent p0 [x1 y1]])]
;;     (doseq [a args]
;;       (swap! arglst* (fn [q](conj q a))))
;;     (apply line @arglst*)))

