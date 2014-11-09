(ns sgwr.util.math
  (:require [clojure.math.numeric-tower]))

(def ^:private k-rad (/ Math/PI 180))
(def ^:private k-deg (/ 1 k-rad))

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

(defn clamp [n mn mx]
  "Restrict value
   Returns n such that  mn <= n <= mx"
  (max (min n mx) mn))


(defn sqr [x](* x x))

(defn distance [p0 p1]
  (let [[x0 y0] p0
        [x1 y1] p1
        dx (- x1 x0)
        dy (- y1 y0)]
    (sqrt (+ (sqr dx)(sqr dy)))))

(defn interpolate [a b w]
  (+ (* a w)
     (* b (- 1.0 w))))
