(ns sgwr.elements.point
  "Sgwr points are the most basic drawing element. They are rendered
  in any number of shapes depending on the attributes style.  Unlike
  most other elements a point's size remains the same remains the same
  with different degrees of drawing zoom."

  (:require [sgwr.elements.element])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.util.math :as math])
  (:import java.awt.geom.Line2D
           java.awt.geom.Rectangle2D
           java.awt.geom.Ellipse2D
           java.awt.BasicStroke))

(def ^:private size-quant 3)
(def ^:private half-quant (* 1/2 size-quant))

(defn- line 
  ([x0 y0 x1 y1]
     (java.awt.geom.Line2D$Double. x0 y0 x1 y1))
  ([p0 p1]
     (let [[x0 y0] p0
           [x1 y1] p1]
       (line x0 y0 x1 y1))))

(defn- pixel [x y _]   ;; pixel style ignores size
  (line x y (inc x) y))

(defn- dash [x y size]
  (let [half (* half-quant size)]
    (line (- x half) y (+ x half) y)))

(defn- bar [x y size]
  (let [half (* half-quant size)]
    (line x (- y half) x (+ y half))))

(defn- diag [x y size]
  (let [half (* half-quant size)]
    (line (- x half)(- y half)(+ x half)(+ y half))))

(defn- diag2 [x y size]
  (let [half (* half-quant size)]
    (line (- x half)(+ y half)(+ x half)(- y half))))

(defn- box [x y size]
  (let [half (* half-quant size)
        width (* size-quant size)]
    (java.awt.geom.Rectangle2D$Double.
     (- x half)(- y half) width width)))

(defn- dot [x y size]
  (let [half (* half-quant size)
        width (* size-quant size)]
    (java.awt.geom.Ellipse2D$Double.
     (- x half)(- y half) width width)))

(defn- cross [x y size]
  (let [s1 (dash x y size)
        s2 (bar x y size)]
    (utilities/combine-shapes s1 s2)))

(defn- x-point [x y size]
  (let [s1 (diag x y size)
        s2 (diag2 x y size)]
    (utilities/combine-shapes s1 s2)))

(defn- triangle [x y size]
  (let [half (* half-quant size)
        y-base (+ y half)
        base (line (- x half) y-base (+ x half) y-base)
        s1 (line (- x half) y-base x (- y half))
        s2 (line (+ x half) y-base x (- y half))]
    (utilities/fuse base s1 s2)))

(defn- right-chevron [x y size]
  (let [half (* size half-quant)
        whole (* size size-quant)
        x1 (+ x whole)
        y0 (+ y half)
        y2 (- y half)
        s1 (line x y0 x1 y)
        s2 (line x y2 x1 y)]
    (utilities/combine-shapes s1 s2)))

(defn- right-arrow [x y size]
  (let [s1 (right-chevron x y size)
        s2 (dash x y size)]
    (utilities/combine-shapes s1 s2)))

(defn- left-chevron [x y size]
  (let [half (* size half-quant)
        whole (* size size-quant)
        x1 (+ x whole)
        y0 (+ y half)
        y2 (- y half)
        s1 (line x y x1 y0)
        s2 (line x y x1 y2)]
    (utilities/combine-shapes s1 s2)))

(defn- left-arrow [x y size]
  (let [s1 (left-chevron x y size)
        s2 (dash (+ x (* size size-quant)) y size)]
    (utilities/combine-shapes s1 s2)))
    
(defn- up-chevron [x y size]
  (let [x1 (+ x (* size size-quant))
        xc (math/mean x x1)
        y1 (- y (* size size-quant))
        s1 (line x y xc y1)
        s2 (line x1 y xc y1)]
    (utilities/combine-shapes s1 s2)))

(defn- up-arrow [x y size]
  (let [s1 (up-chevron x y size)
        s2 (bar (+ x (* size half-quant)) y size)]
    (utilities/combine-shapes s1 s2)))

(defn- down-chevron [x y size]
  (let [x1 (+ x (* size size-quant))
        xc (math/mean x x1)
        y1 (+ y (* size size-quant))
        s1 (line x y xc y1)
        s2 (line x1 y xc y1)]
    (utilities/combine-shapes s1 s2)))

(defn- down-arrow [x y size]
  (let [s1 (down-chevron x y size)
        s2 (bar (+ x (* size half-quant)) y size)]
    (utilities/combine-shapes s1 s2)))

(defn- dot-cross [x y size]
  (let [s1 (dot x y size)
        s2 (bar x y (dec size))
        s3 (dash x y (dec size))]
    (utilities/fuse s1 s2 s3)))

(defn- dot-x [x y size]
  (let [s1 (dot x y size)
        s2 (diag x y (dec size))
        s3 (diag2 x y (dec size))]
    (utilities/fuse s1 s2 s3)))



(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        p (first (.points obj))
        q (.map-point cs p)
        [u v] q
        sfn (get {0 dot, 1 pixel, 2 dash, 3 bar,
                  4 diag, 5 diag2, 6 box, 7 cross, 
                  8 x-point, 9 triangle,
                  10 right-chevron, 11 right-arrow,
                  12 left-chevron, 13 left-arrow,
                  14 up-chevron, 15 up-arrow, 
                  16 down-chevron, 17 down-arrow,
                  18 dot-cross, 19 dot-x
                  -1 dot, -2 box
                  }

                 (.style obj) dot)]
    (sfn u v (.size obj))))

(defn- distance-fn [obj q]
  (math/distance (first (.points obj)) q))

(defn- update-fn [_ pnts] pnts)
  
(defn- bounds-fn [obj points]
  (let [p (first points)]
    [p p]))

(def ^:private point-function-map {:shape-fn shape-fn
                                   :contains-fn (constantly false)
                                   :distance-fn distance-fn
                                   :update-fn update-fn
                                   :bounds-fn bounds-fn})

(def locked-properties [])

(defn point 
  ([parent p & {:keys [id color style size]
                :or {id :new-point
                     color (uc/color :white)
                     style 0
                     size 1.0}}]
   (let [obj (sgwr.elements.element/create-element :point 
                                                   parent 
                                                   point-function-map 
                                                   locked-properties)]
     (if parent (.set-parent! obj parent))
     (.set-points! obj [p])
     (.put-property! obj :id id)
     (.color! obj :default color)
     (.style! obj :default style)
     (.size! obj :default size)
     (.use-attributes! obj :default)
     obj)))
     
  
