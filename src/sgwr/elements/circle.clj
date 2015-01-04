;; TODO:
;;     1. Define two-point (diameter) constructor
;;     2. Define center, radius constructor
;;     3. define three-point constructor

(ns sgwr.elements.circle
  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.elements.element])
  (:require [seesaw.graphics :as ssg]))

(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        [u0 v0] q0
        [u1 v1] q1
        w (- u1 u0)
        h (- v1 v0)]
    (ssg/ellipse u0 v0 w h)))

(defn- update-fn [obj points]
  (let [[p0 p1] points
        x0 (apply min (map first [p0 p1]))
        y0 (apply min (map second [p0 p1]))
        x1 (apply max (map first [p0 p1]))
        y1 (apply max (map second [p0 p1]))
        side (max (- x1 x0)(- y1 y0))
        x2 (+ x0 side)
        y2 (+ y0 side)
        xc (math/mean x0 x2)
        yc (math/mean y0 y2)
        dx (math/abs (- x2 x0))
        radius (* 0.5 dx)]
    (.put-property! obj :center [xc yc])
    (.put-property! obj :radius radius)
    [[x0 y0][x2 y2]]))

(defn- distance-helper [obj q]
  (let [pc (.get-property! obj :center)
        radius (.get-property! obj :radius)
        dc (math/distance pc q)
        distance (- dc radius)]
    [(<= dc radius)
     (max 0 distance)]))

(defn contains-fn [obj q]
  (first (distance-helper obj q)))

(defn distance-fn [obj q]
  (second (distance-helper q)))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]])) 

(def ^:private circle-function-map {:shape-fn shape-fn
                                    :contains-fn contains-fn
                                    :distance-fn distance-fn
                                    :update-fn update-fn
                                    :bounds-fn bounds-fn})

(def locked-properties [:center :radius])

; circle defined by bounding rectangle
; If rectangle is not square, the side with greatest length is used

(defn circle [parent p0 p1  & {:keys [id color style width fill]
                               :or {id :new-circle
                                    color (ucolor/color :white)
                                    style 0
                                    width 1.0
                                    fill nil}}]
  (let [obj (sgwr.elements.element/create-element :circle
                                                  parent
                                                  circle-function-map
                                                  locked-properties)]
    (if parent (.set-parent! obj parent))
    (.set-points! obj [p0 p1])
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.use-attributes! obj :default)
    obj))
