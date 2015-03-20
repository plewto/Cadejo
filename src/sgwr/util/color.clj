(ns sgwr.util.color
  "Provides functions for color manipulation"
  (:require [sgwr.util.math :as math])
  (:require [seesaw.color :as ssc])
  (:import java.awt.Color
           java.awt.GradientPaint
           java.awt.geom.Point2D))


(defn is-color? [obj]
  (= (type obj) java.awt.Color))

;; (defn str-rep-color [c]
;;   "Returns string representation of color as vector of int RGB values"
;;   (println (type c)) ;; DEBUG
;;   (if c
;;     (let [r (.getRed c)
;;           g (.getGreen c)
;;           b (.getBlue c)
;;           a (.getAlpha c)]
;;       (format "[%3d %3d %3d %3d]" r g b a))
;;     "nil"))

(defn str-rep-color [c]
  (let [ct (type c)]
    (cond (= c java.awt.GradientPaint)
          "Gradient"

          (= ct java.awt.Color)
          (let [r (.getRed c)
                g (.getGreen c)
                b (.getBlue c)
                a (.getAlpha c)]
            (format "[%3d %3d %3d %3d]" r g b a))

          :default
          "?")))

;; As with seesaw.color/color except that if the first argument 
;; is an instance of java.awt.Color it becomes the return color.
;; The first argument may also be an instance of java.awt.GradientPaint
;; A nil argument returns nil
;;
(defn color [& args]
  (let [ct (type (first args))]
    (cond 
      (= ct Color) (first args)

      (= ct GradientPaint) (first args)

      (nil? (first args)) nil
      
      (keyword? (first args))(ssc/color (first args))

      (or (list? (first args))(vector? (first args)))
      (apply ssc/color (first args))

      :default (apply ssc/color args)

      )))


(defn crossmix [a b mix]
  "Return color mixture
   a - Color
   b - Color
   mix - float, 0.0 <= mix <= 1.0, relative amounts of a and b in result
         mix = 0 --> return b
         mix = 1 --> return b"
  (let [w (float (math/clamp mix 0 1))
        ar (.getRed a)
        ag (.getGreen a)
        ab (.getBlue a)
        aa (.getAlpha a)
        br (.getRed b)
        bg (.getGreen b)
        bb (.getBlue b)
        ba (.getAlpha b)
        cr (int (math/interpolate ar br w))
        cg (int (math/interpolate ag bg w))
        cb (int (math/interpolate ab bb w))
        ca (int (math/interpolate aa ba w))]
    (ssc/color cr cg cb ca)))

(defn inversion [c]
  "Return color inversion"
  (let [r (- 255 (.getRed c))
        g (- 255 (.getGreen c))
        b (- 255 (.getBlue c))]
    (ssc/color r g b)))

(defn hsb [c]
  "Returns color hue, saturation and brightness as float vector [h s b]
   See java.awt.Color"
  (let [r (.getRed c)
        g (.getGreen c)
        b (.getBlue c)]
    (Color/RGBtoHSB r g b nil)))

(defn rgb [c]
  "Returns red, green, blue color values as int vector [r g b]"
  [(.getRed c)(.getGreen c)(.getBlue c)])


(defn hue [c]
  "Return color hue as float 0.0 <= hue < 1.0"
  (first (hsb c)))

(defn saturation [c]
  "Return color saturation as float, 0.0 <= s <= 1.0"
  (second (hsb c)))

(defn brightness [c]
  "Return color brightness as float, 0.0 <= b <= 1.0"
  (nth (hsb c) 2))

(defn modify [c & {:keys [h s b]
                   :or {h 0.0
                        s 1.0
                        b 1.0}}]
  "Return modified version of color c
   h - float, hue shift, default 0.0
   s - float, saturation scale, default 1.0
   b - float, brightness scale, default 1.0"
  (let [[h1 s1 b1](hsb c)
        h2 (+ h h1)
        s2 (math/clamp (* s s1) 0 1)
        b2 (math/clamp (* b b1) 0 1)]
    (Color/getHSBColor h2 s2 b2)))

(defn complement [c]
  "Return complement of color c"
  (modify c :h 0.3))

(defn darker 
  ([c n]
     (modify c :b n))
  ([c]
     (darker c 0.60)))
 
(defn brighter
  ([c n]
     (modify c :b n))
  ([c]
     (brighter c 1.667)))

;; Set color's alpha channel
;;
(defn transparent
  ([c] (transparent c 128))
  ([c alpha]
   (let [c2 (color c)
         r (.getRed c2)
         g (.getGreen c2)
         b (.getBlue c2)
         rs (Color. r g b alpha)]
     rs)))


;; convert vector point [x y] to java.awt.geom.Point2D
;;
(defn- p2d 
  ([p]
   (p2d (first p)(second p)))
  ([x y]
   (java.awt.geom.Point2D$Double. x y)))

;; DEPRECIATED
;; Produce linear gradient 
;; p0     - Point vector [x0 y0]
;; c0     - Color at p0, see color function
;; p1     - Point 
;; c1     - Color at p1
;; cs     - Coordinate system
;; cyclic - Boolean if true
;;
;; Returns instance of java.awt.GradientPaint.
;; Note gradients are not modified by object transformations
;; (translation, scale, etc.)
;;
;; (defn gradient 
;;   ([p0 c0 p1 c1 cs cyclic]
;;    (let [[u0 v0](.map-point cs p0)
;;          [u1 v1](.map-point cs p1)
;;          q0 (p2d u0 v0)
;;          q1 (p2d u1 v1)] 
;;      (GradientPaint. q0 (color c0) q1 (color c1) cyclic))))




  
;; Create gradient realtive to object's position and size
;; obj - SgwrElement
;; c0  - First color
;; c1  - Second color
;; :strech - gradient scale factor 
;;           stretch < 1 shortens transition
;;           stretch > 1 extends transition
(defn obj-gradient [obj c0 c1 & {:keys [stretch direction cyclic]
                                 :or {stretch 1.0
                                      swap false
                                      cyclic false}}]
  (let [[p0 p1] (.physical-bounds obj)
        [x2 y2 x3 y3] (cond 
                        (= direction :horizontal)
                        [(first p1)(second p0)(first p0)(second p0)]

                        (= direction :vertical)
                        [(first p1)(second p1)(first p1)(second p0)]

                        (= direction :diagonal)
                        [(first p1)(second p0)(first p0)(second p1)]

                        :default
                        [(first p0)(second p0)(first p1)(second p1)])
        p2 (p2d (math/scale-point [x2 y2] [stretch stretch]))
        p3 (p2d (math/scale-point [x3 y3] [stretch stretch]))]
    (GradientPaint. p2 (color c0) p3 (color c1) cyclic)))

(defn set-gradient! [obj c0 c1 & {:keys [attribute stretch direction cyclic]
                                  :or {attribtue nil
                                       stretch 1.0
                                       direction :vertical
                                       cyclic false}}]
  (let [grad (obj-gradient obj c0 c1 
                           :stretch stretch 
                           :direction direction
                           :stretch stretch
                           :cyclic cyclic)]
    (if attribute
      (.color! obj attribute grad)
      (.color! obj grad))
    grad))
                                         
                                  
