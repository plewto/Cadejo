(println "--> cadejo.midi.curves")

(ns cadejo.midi.curves
  "Provides standard mapping functions.
   Mapping functions come in two flavours: monopolar and bipolar
   Monopolr functions have range and domain of (0.0,+1.0)
   Bipolar functions have range and domain of (-1.0,+1.0)"
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math :as math]))

(def e math/e)
(def expt math/expt)

; ---------------------------------------------------------------------- 
;                         Positive monopolar curves
;
; fn(x) --> y   0.0 <= x <= 1.0 
;               0.0 <= y <= 1.0
;

(def zero (constantly 0.0))
(def half (constantly 0.5))
(def one (constantly 1.0))
(def linear (fn [x] x))
(def quadratic (fn [x](expt x 2)))
(def cubic (fn [x](expt x 3)))
(def quartic (fn [x](expt x 4)))
(def convex2 (fn [x](- 1 (expt (- x 1) 2))))
(def convex4 (fn [x](- 1 (expt (- x 1) 4))))
(def convex6 (fn [x](- 1 (expt (- x 1) 6))))
(def logistic (fn [x]
                (math/clamp 
                 (+ (/ 1.18 (+ 1 (expt e (* -5.0 (- x 0.5))))) -0.09 )
                 0 1)))
(def logistic2 (fn [x]
                (math/clamp
                 (+ (/ 1.024 (+ 1 (expt e (* -9 (- x 0.5))))) -0.012)
                 0 1)))
(def inverted-linear (math/linear-function 0.0 1.0 1.0 0.0))
(def inverted-quadratic (fn [x](- 1 (expt x 2))))
(def inverted-cubic (fn [x](- 1 (expt x 3))))
(def inverted-convex2 (fn [x](expt (- x 1) 2)))
(def inverted-convex4 (fn [x](expt (- x 1) 4)))
(def inverted-convex6 (fn [x](expt (- x 1) 6)))
(def inverted-logistic (fn [x](- 1 (logistic x))))
(def inverted-logistic2 (fn [x](- 1 (logistic2 x))))

(def curves-map {:zero zero
                 :half half
                 :one   one
                 :linear linear
                 :quadratic quadratic
                 :cubic cubic
                 :quartic quartic
                 :convex2 convex2
                 :convex4 convex4
                 :convex6 convex6
                 :logistic logistic
                 :logistic2 logistic2
                 :ilinear inverted-linear
                 :iquadratic inverted-quadratic
                 :icubic inverted-cubic
                 :iconvex2 inverted-convex2
                 :iconvex4 inverted-convex4
                 :iconvex6 inverted-convex6
                 :ilogistic inverted-logistic
                 :ilogistic2 inverted-logistic2})

(def curve-names {:zero "zero"
                  :half "half"
                  :one "one"
                  :linear "linear"
                  :quadratic "quadratic"
                  :cubic "cubic"
                  :quartic "quartic"
                  :convex2 "convex2"
                  :convex4 "convex4"
                  :convex6 "convex6"
                  :logistic "logistic"
                  :logistic2 "logistic2"
                  :ilinear "inv-linear"
                  :iquadratic "inv-quadratic"
                  :icubic "inv-cubic"
                  :iconvex2 "inv-convex2"
                  :iconvex4 "inv-convex4"
                  :iconvex6 "inv-convex6"
                  :ilogistic "inv-logistic"
                  :ilogistic2 "inv-logistic2"})

(def available-curves
  '[:zero :half :one 
    :linear :quadratic :cubic :quartic
    :convex2 :convex4 :convex6 :logistic :logistic2
    :ilinear :iquadratic :icubic 
    :iconvex2 :iconvex4 :iconvex6 
    :ilogistic :ilogistic2])

(defn get-curve 
  ([key default]
     (or (get curves-map key)
         (umsg/warning (format "Curve %s does not exists, using default" key))
         default))
  ([key]
     (get-curve key linear)))

        
; ---------------------------------------------------------------------- 
;                              Bipolar curves
;
; f(x) --> y   -1.0 <= x <= +1.0
;              -1.0 <= y <= +1.0   
;
; f(-1) = -1 or +1
; f(0)  = 0
; f(+1) = +1 or -1
;

(defn uni->bi [ufn]
  (fn [x] (cond (pos? x)(ufn x)
                (neg? x)(* -1 (ufn (math/abs x)))
                :default 0)))

(def bi-inverted-linear (math/linear-function -1.0 +1.0 +1.0 -1.0))
(def bi-quadratic (uni->bi quadratic))
(def bi-cubic (uni->bi cubic))
(def bi-quartic (uni->bi quartic))
(def bi-convex2 (uni->bi convex2))
(def bi-convex4 (uni->bi convex4))
(def bi-convex6 (uni->bi convex6))
(def bi-logistic (uni->bi logistic))
(def bi-logistic2 (uni->bi logistic2))
(def bi-inverted-quadratic (uni->bi inverted-quadratic))
(def bi-inverted-cubic (uni->bi inverted-cubic))
(def bi-inverted-convex2 (uni->bi inverted-convex2))
(def bi-inverted-convex4 (uni->bi inverted-convex4))
(def bi-inverted-convex6 (uni->bi inverted-convex6))
(def bi-inverted-logistic (uni->bi inverted-logistic))
(def bi-inverted-logistic2 (uni->bi inverted-logistic2))

(def bi-curve-map {:zero zero
                   :half half
                   :one one
                   :linear linear
                   :quadratic bi-quadratic
                   :cubic bi-cubic
                   :quartic bi-quartic
                   :convex2 bi-convex2
                   :convex4 bi-convex4
                   :convex6 bi-convex6
                   :logistic bi-logistic
                   :logistic2 bi-logistic2
                   :ilinear bi-inverted-linear
                   :iquadratic bi-inverted-quadratic
                   :icubic bi-inverted-cubic
                   :iconvex2 bi-inverted-convex2
                   :iconvex4 bi-inverted-convex4
                   :iconvex6 bi-inverted-convex6
                   :ilogistic bi-inverted-logistic
                   :ilogistic2 bi-inverted-logistic2})

(defn get-bipolar-curve 
  ([key default]
     (or (get bi-curve-map key)
         (umsg/warning (format "Bipolar curve %s does not exists, using default" key))
         default))
  ([key]
     (get-bipolar-curve key linear)))
