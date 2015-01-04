;; TODO 
;;    1. Add rounded-corner constructor


(ns sgwr.elements.rectangle
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.elements.element])
  (:require [seesaw.graphics :as ssg]))

(defn- shape-function [obj]
  (let [cs (.coordinate-system obj)
        [p0 p1] (.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        [u0 v0] q0
        [u1 v1] q1
        w (- u1 u0)
        h (- v1 v0)
        radius (.get-property obj :corner-radius 0)]
    (if (pos? radius)
      (ssg/rounded-rect u0 v0 w h radius radius)
      (ssg/rect u0 v0 w h))))

(defn- distance-helper [obj q]
  (let [cs (.coordinate-system obj)
        [p0 p1] (.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        u0 (min (first q0)(first q1))
        u1 (max (first q0)(first q1))
        v0 (min (second q0)(second q1))
        v1 (max (second q0)(second q1))
        [u v] (.map-point cs q)]
    [[u0 v0][u1 v1][u v]]))


(defn rectangle-contains? [obj q]
  (let [[p0 p1 q2](distance-helper obj q)
        [u0 v0] p0
        [u1 v1] p1
        [u2 v2] q2]
    (and (<= u0 u2)(<= u2 u1)
         (<= v0 v2)(<= v2 v1))))

(defn rectangle-distance [obj q]
  (let [[p0 p1 q2](distance-helper obj q)
        [u0 v0] p0
        [u1 v1] p1
        [u2 v2] q2]
    (if  (and (<= u0 u2)(<= u2 u1)
              (<= v0 v2)(<= v2 v1)) 
      0
      (let [a [u0 v0]
            b [u0 v1]
            c [u1 v1]
            d [u1 v0]
            da (math/point-line-distance q2 a d)
            db (math/point-line-distance q2 a b)
            dc (math/point-line-distance q2 b c)
            dd (math/point-line-distance q2 c d)]
        (min da db dc dd)))))

(defn- update-fn [obj points]
  (let [[p0 p1] points
        [x0 y0] p0
        [x1 y1] p1
        pc [(math/mean x0 x1)(math/mean y0 y1)]
        width (math/abs (- x0 x1))
        height (math/abs (- y0 y1))]
    (.put-property! obj :center pc)
    (.put-property! obj :width width)
    (.put-property! obj :height height)
    points))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]]))

(def ^:private rectangle-function-map {:shape-fn shape-function
                                       :contains-fn rectangle-contains?
                                       :distance-fn rectangle-distance
                                       :update-fn update-fn
                                       :bounds-fn bounds-fn})

(def locked-properties [:center :width :height :corner-radius])

(defn rectangle [parent p0 p1  & {:keys [id color style width fill]
                                  :or {id :new-rectangle
                                       color (ucolor/color :white)
                                       style 0
                                       width 1.0
                                       fill nil}}]
  (let [obj (sgwr.elements.element/create-element :rectangle parent rectangle-function-map locked-properties)]
    (if parent (.set-parent! obj parent))
    (.set-points! obj [p0 p1])
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.use-attributes! obj :default)
    obj))
