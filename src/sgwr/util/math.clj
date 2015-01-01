(ns sgwr.util.math
  (:require [clojure.math.numeric-tower]))

(def ^:private k-rad (/ Math/PI 180))
(def ^:private k-deg (/ 1 k-rad))

(defn clamp [n mn mx]
  "Restrict value
   Returns n such that  mn <= n <= mx"
  (max (min n mx) mn))

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

(def expt 
  "(expt x e) --> x^e  Returns x raised to the power e"
  clojure.math.numeric-tower/expt)

(defn abs [n]
  (Math/abs n))

(defn sqrt [n]
  (Math/sqrt n))

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

(defn colinear? [q p0 p1]
  "Predicate returns true if point q is colinear to segment [p0,p1]"
  (if (vertical? p0 p1)
    (= (first q)(first p0))
    (let [ma (slope p0 p1)
          mb (slope q p0)]
      (= ma mb))))

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
           
        
;; (defn line-contains? [q p0 p1]
;;   (zero? (distance q p0 p1)))


;; (defn rectangle-contains? [q p0 p1]
;;   (let [x0 (min (first p0)(first p1))
;;         x1 (max (first p0)(first p1))
;;         y0 (min (second p0)(second p1))
;;         y1 (max (second p0)(second p1))
;;         [x y] q]
;;     (and (<= x0 x)(<= x x1)(<= y0 y)(<= y y1))))
    
;; (defn rectangle-distance [q p0 p1]
;;   (if (contains? q p0 p1)
;;     0
;;     (let [[x0 y0] p0
;;           [x1 y1] p1
;;           a (line/distance q [x0 y0][x0 y1])
;;           b (line/distance q [x0 y1][x1 y1])
;;           c (line/distance q [x1 y1][x1 y0])
;;           d (line/distance q [x0 y0][x1 y0])]
;;       (min a b c d))))

;; (defn circle-contains? [q pc r]
;;   (let [d (math/distance q pc)]
;;     (<= d r)))

;; (defn circle-distance [q pc r]
;;   (let [d (math/distance q pc)]
;;     (if (<= d r) 
;;       0
;;       (- d r))))
