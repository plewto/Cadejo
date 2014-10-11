(ns sgwr.point
  "Defines point element
   Point geometry is unchanged by coordinate system values
   See attributes/point-styles"
  (:require [sgwr.attributes])
  (:require [sgwr.element])
  ;(:require [sgwr.utilities :as util])
  (:require [sgwr.util.shape :as sutil])
  (:import java.awt.geom.Line2D
           java.awt.geom.Rectangle2D
           java.awt.geom.Ellipse2D
           java.awt.BasicStroke))

(def ^:private point-stroke (BasicStroke.))

(def ^:private point-size 6)
(def ^:private half (* 1/2 point-size))

(defn- line 
  ([x0 y0 x1 y1]
     (java.awt.geom.Line2D$Double. x0 y0 x1 y1))
  ([p0 p1]
     (let [[x0 y0] p0
           [x1 y1] p1]
       (line x0 y0 x1 y1))))

(defn- pixel [x y]
  (line x y (inc x) y))

(defn- dash [x y]
  (line (- x half) y (+ x half) y))

(defn- bar [x y]
  (line x (- y half) x (+ y half)))

(defn- diag [x y]
  (line (- x half)(- y half)(+ x half)(+ y half)))

(defn- diag2 [x y]
  (line (- x half)(+ y half)(+ x half)(- y half)))

(defn- box [x y]
  (java.awt.geom.Rectangle2D$Double.
   (- x half)(- y half)
   point-size point-size))

(defn- dot [x y]
  (java.awt.geom.Ellipse2D$Double.
   (- x half)(- y half)
   point-size point-size))

(defn- cross [x y]
  (let [s1 (dash x y)
        s2 (bar x y)]
    (sutil/combine-shapes s1 s2)))

(defn- x-point [x y]
  (let [s1 (diag x y)
        s2 (diag2 x y)]
    (sutil/combine-shapes s1 s2)))


(defn- triangle [x y]
  (let [y-base (+ y half)
        base (line (- x half) y-base (+ x half) y-base)
        s1 (line (- x half) y-base x (- y half))
        s2 (line (+ x half) y-base x (- y half))]
    (sutil/combine-shapes base (sutil/combine-shapes s1 s2))))


(def ^:private shape-functions [dot pixel dash bar diag diag2 
                                box cross x-point triangle])

(defn- gen-shape [point-style x y]
  (try
    (let [sfn (nth shape-functions point-style)]
      (sfn x y))
    (catch IndexOutOfBoundsException  ex
      (dot x y))))


(defn point
  ([]
     (point 0.0 0.0))
  ([p]
     (point (first p)(second p)))
  ([x y]
     (let [attributes* (atom (sgwr.attributes/attributes))
           position* (atom [(float x)(float y)])
           pobj (reify sgwr.element.Element
                  
                  (element-type [this] :point)

                  (attributes [this] 
                    @attributes*)

                  (attributes! [this att]
                    (reset! attributes* att))

                  (construction-points [this]
                    [@position*])

                  (position! [this points]
                    (reset! position* (first points)))

                  (shape [this cs]
                    (let [q (.map-point cs @position*)
                          [x y] q
                          sty (.style @attributes*)]
                      (gen-shape sty x y)))

                  (color [this]
                    (.color @attributes*))

                  (stroke [this]
                    point-stroke)

                  (hidden? [this]
                    (.hidden? @attributes*))

                  (filled? [this] false)

                  (selected? [this]
                    (.selected? @attributes*))

                  (clone [this]
                    (let [other (point @position*)]
                      (.attributes! other (.clone (.attributes this)))
                      other))

                  (to-string [this]
                    (let [[x y] @position*]
                      (format "Point [%f %f]" x y))))] 
       pobj)))
                 
                          
