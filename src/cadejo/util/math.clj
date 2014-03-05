(ns cadejo.util.math
  (:require [clojure.math.numeric-tower]))
 
(def expt 
  "(expt x e) --> x^e  Returns x raised to the power e"
  clojure.math.numeric-tower/expt)

(def abs
  clojure.math.numeric-tower/abs)

(def step 
  "Ratio for 12-tone half-step"
  (expt 2.0 1/12))

(def cent 
  "Ratio of single cent."
  (expt 2.0 1/1200))

(defn linco 
  "Calculates coefficients for linear function through points
  (x0,y0)(x1,y1) where x0 != x1. If only two values provided first point is
  (0,0). The result is a two element vector [a,b]
  where f(x) = a*x+b." 
  ([x0 y0 x1 y1]
     (let [dy (float (- y1 y0))
           dx (float (- x1 x0))
           a (/ dy dx)
           b (- y0 (* a x0))]
       [a b]))
  ([x1 y1]
     (linco 0 0 x1 y1)))

(defn linear-function
  "Returns linear function through points (x0,y0)(x1,y1) where x0 != x1." 
  ([x0 y0 x1 y1]
     (let [co (linco x0 y0 x1 y1)
           a (first co)
           b (second co)]
       (fn [x](+ (* a x) b)))))

(defn expt-function
  "Returns exponential function over interval (x0,x1)"
  ([base x0 x1]
     (let [lin (linear-function x0 -1.0 x1 +1.0)]
       (fn [x](expt base (lin x)))))
  ([base]
     (expt-function base -1 +1))
  ([]
     (expt-function 2)))
           
     



(defn bool [obj]
  "Returns canonical boolean true or false"
  (if obj true false))


(defn posnum? [obj]
  "Predicate true if obj is a positive number, obj may be of any type"
  (and (number? obj)(> obj 0)))

(defn negnum? [obj]
  "Predicate true if obj is a negative number, obj may be of any type"
  (and (number? obj)(< obj 0)))

(defn coin 
  "Returns one of two random values.
   [p a b & args] [p] []
   p    - Probability of 'a' result, default 0.5
   a    - Value for a result, default true
   b    - Value for b result, default false
   args - Optional args passed to a and b if they are functions.
   Arguments a and b may be functions, If so upon selection they are 
   evaluated with the optional args and the result returned."   
  ([p a b & args]
     (if (> p (rand))
       (if (fn? a)(apply a args) a)
       (if (fn? b)(apply b args) b)))
  ([p]
     (coin p true false))
  ([]
     (coin 0.5)))

(defn rand-range [low high]
  "Returns random number in interval [low, high)"
  (+ low (rand (- high low))))

(defn dice 
  "Returns random integer 
   [low high] Returns random integer between low and high-1, inclusive
   [n] Return random integer between 0 and n-1 inclusive."
  ([low high](int (rand-range low high)))
  ([n](dice 0 n)))

(defn pick [col & args]
  "Pick object at random from collection. If the selected object is a function
   call it with args and return the result."
  (let [obj (rand-nth col)]
    (if (fn? obj)
      (apply obj args)
      obj)))

(defn approx 
  "Returns a float which is approximately equal to n.
   [n range] [n]
   n     - The mean result.
   range - Set maximum deviation from mean as a ratio, default 1% = 0.01"
    ([n range] (+ n (* (coin 0.5 -1 +1)(* n range)(rand))))
    ([n](approx n 0.01)))
