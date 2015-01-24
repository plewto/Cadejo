(ns sgwr.elements.rectangle
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.line])
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

(defn render-rectangle [obj g2d]
  (.setStroke g2d (ustroke/stroke obj))
  (let [shp (shape-function obj)
        f (.filled? obj)]
    (if (and f (not (= f :no)))
      (.fill g2d shp)
      (.draw g2d shp))))

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
                                       :bounds-fn bounds-fn
                                       :style-fn sgwr.elements.line/style-fn})

(def locked-properties [:center :corner-radius])

(defn rectangle [parent p0 p1 & {:keys [id color style width fill radius]
                                  :or {id :new-rectangle
                                       color (uc/color :white)
                                       style 0
                                       width 1.0
                                       fill nil
                                       radius 0}}]
  (let [obj (sgwr.elements.element/create-element :rectangle parent rectangle-function-map locked-properties)]
    (if parent (.set-parent! obj parent))
    (.set-points! obj [p0 p1])
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.put-property! obj :corner-radius radius)
    (.use-attributes! obj :default)
    obj))


;; Rctangle defined by point, width and height
;;
(defn rectangle-wh [parent p0 w h & args]
  (let [[x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        arglst*  (atom [parent p0 [x1 y1]])]
    (doseq [a args]
      (swap! arglst* (fn [q](conj q a))))
    (apply rectangle @arglst*)))


;; Rectangle defined by center point width and height
;; If height h is nil, height is same as width.
;;        
(defn rectangle-c [parent pc w h & args]
  (let [[xc yc] pc
        w2 (* 1/2 w)
        h2 (* 1/2 (or h w))
        x1 (int (- xc w2))
        x2 (int (+ xc w2))
        y1 (int (- yc h2))
        y2 (int (+ yc h2))
        arglst* (atom [parent [x1 y1][x2 y2]])]
    (doseq [a args]
      (swap! arglst* (fn [q](conj q a))))
    (apply rectangle @arglst*)))
