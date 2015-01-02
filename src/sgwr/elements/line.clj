;; TODO
;;    1. Add orthogonal constructors
;;    2. Add simulated infinit constructors
;;    3. Add point-slope constructors

(println "--> sgwr.elements.line")
(ns sgwr.elements.line
  (:require [sgwr.util.math :as math])
  (:require [sgwr.elements.element])
  (:import java.awt.geom.Line2D))

(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        [u0 v0] q0
        [u1 v1] q1]
    (java.awt.geom.Line2D$Double. u0 v0 u1 v1)))

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
                                  :bounds-fn bounds-fn})

(defn line
  ([](line nil [0 0][1 1]))
  ([parent x0 y0 x1 y1](line parent [x0 y0][x1 y1]))
  ([parent p0 p1]
   (let [obj (sgwr.elements.element/create-element :line parent line-function-map)]
     (.set-points! obj [p0 p1]) 
     (if parent (.set-parent! obj parent))
     obj)))
