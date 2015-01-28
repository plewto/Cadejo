(ns sgwr.util.math
  (:require [clojure.math.numeric-tower]))

(def ^:private k-rad (/ Math/PI 180))
(def ^:private k-deg (/ 1 k-rad))
(def ^:private pi2 (* 2 Math/PI))

(defn clamp 
  "Restrict value
   Returns n such that  mn <= n <= mx"
  ([n mn mx]
   (max (min n mx) mn))
  ([n minmax]
   (clamp n (first minmax)(second minmax))))

(defn in-range? [n a b]
  "Predicate true iff a <= n <= b or b <= n <= a"
  (let [mn (min a b)
        mx (max a b)]
    (and (<= mn n)(<= n mx))))

(defn linear-function [x0 y0 x1 y1]
  "Returns linear function through points [x0 y0][x1 y1]"
  (let [dx (- x0 x1)
        dy (- y0 y1)
        a (/ (float dy) dx)
        b (- y0 (* a x0))]
    (fn [x](+ (* a x) b))))

(defn clipped-linear-function [x0 y0 x1 y1]
  "Returns linear function through points [x0 y0][x1 y1]
   where y is restricted to y0 <= y <= y1   or  y1 <= y <= y0"
  (let [dx (- x0 x1)
        dy (- y0 y1)
        a (/ (float dy) dx)
        b (- y0 (* a x0))]
    (fn [x](clamp (+ (* a x) b)(min y0 y1)(max y0 y1)))))

(defn mean [& args]
  "Return arithmetic average of arguments"
  (let [sum (float (apply + args))]
    (/ sum (.count args))))

(defn interpolate [a b w]
  "Returns weighted average of two values
   a and b, the values
   w - weight  0 <= w <= 1"
  (+ (* a w)
     (* b (- 1.0 w))))

(defn deg->rad [d]
  "Convert degrees to radians"
  (* d k-rad))

(defn rad->deg [r]
  "Convert radians to degrees"
  (* r k-deg))

(defn rad->turn [r]
  "Convert radians to turns"
  (/ r pi2))

(defn turn->rad [tr]
  "Convert turns to radians"
  (* tr pi2))

(defn deg->turn [d]
  "Convert degrees to turns"
  (/ d 360.0))

(def expt 
  "(expt x e) --> x^e  Returns x raised to the power e"
  clojure.math.numeric-tower/expt)

(defn abs [n]
  (Math/abs n))

(defn sqrt [n]
  (Math/sqrt n))

(defn sin [n]
  (Math/sin n))

(defn cos [n]
  (Math/cos n))

(defn tan [n]
  (Math/tan n))

(defn sqr [x](* x x))

(defn vertical? [p0 p1]
  "Predicate returns true if segment [p0,p1] is vertical"
  (= (first p0)(first p1)))

(defn slope [p0 p1]
  "Returns slope of line through point p0 and p1
   Returns nil for vertical lines"
  (if (vertical?)
    nil
    (let [dx (- (first p0)(first p1))
          dy (- (second p0)(second p1))]
      (/ dy dx))))

(defn slope->angle [slope]
  "Returns angle of slope in radians"
  (Math/atan slope))

(defn colinear? [q p0 p1]
  "Predicate returns true if point q is colinear to segment [p0,p1]"
  (if (vertical? p0 p1)
    (= (first q)(first p0))
    (let [ma (slope p0 p1)
          mb (slope q p0)]
      (= ma mb))))

(defn midpoint [p0 p1]
  "Returns midpoint of line segment [p0 p1]"
  (let [[x0 y0] p0
        [x1 y1] p1
        xc (mean x0 x1)
        yc (mean y0 y1)]
    [xc yc]))

(defn distance [p0 p1]
  "Returns distance between points."
  (let [[x0 y0] p0
        [x1 y1] p1
        dx (- x1 x0)
        dy (- y1 y0)]
    (sqrt (+ (sqr dx)(sqr dy)))))

;; Returns distance between point q and finit line segment [p0,p1]
;;
(defn point-line-distance [q p0 p1]
  (let [[x y] q
        [x0 y0] p0
        [x1 y1] p1
        px (- x1 x0)
        py (- y1 y0)
        sumsqr (float (+ (sqr px)(sqr py)))
        u (clamp (/ (+ (* px (- x x0))
                       (* py (- y y0)))
                    sumsqr)
                 0.0 1.0)
        x4 (+ x0 (* u px))
        y4 (+ y0 (* u py))
        dx (sqr (- x4 x))
        dy (sqr (- y4 y))]
    (sqrt (+ dx dy))))
        
(defn point-rectangle-distance [q p0 p1]
  (let [[x0 y0] p0
        [x1 y1] p1
        a (point-line-distance q [x0 y0][x0 y1])
        b (point-line-distance q [x0 y1][x1 y1])
        c (point-line-distance q [x1 y1][x1 y0])
        d (point-line-distance q [x0 y0][x1 y0])]
    (min a b c d)))

(defn translate-point [p offsets]
  [(+ (first p)(first offsets))
   (+ (second p)(second offsets))])

(defn scale-point 
  ([p factors ref-point]
   (let [[sx sy] factors
         [x0 y0] ref-point
         [x1 y1] p
         kx (* x0 (- 1 sx))
         ky (* y0 (- 1 sy))]
     [(+ (* x1 (first factors)) kx)
      (+ (* y1 (second factors)) ky)]))
  ([p factors]
   (scale-point p factors [0 0])))
