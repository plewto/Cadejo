(ns cadejo.ui.util.color-utilities
  "Functions for color manipulation"
  (:require [cadejo.util.math :as math])
  (:require [seesaw.color :as ssc])
  (:import java.awt.Color))

(defn- third [col]
  (nth col 2))

;; As with seesaw.color/color except that if the first argument 
;; is an instance of java.awt.Color it becomes the return color.
;;
(defn color [& args]
  (cond (= (type (first args)) Color) (first args)
        (keyword? (first args))(ssc/color (first args))
        (or (list? (first args))(vector? (first args)))
        (apply ssc/color (first args))
        :default (apply ssc/color args)))

(defn hsb 
  ([h s b]
     (let [rgb (Color/HSBtoRGB (float h)(float s)(float b))]
       (Color. rgb)))
  ([h s]
     (hsb h s 1.0))
  ([h]
     (hsb h 1.0 1.0)))        

(defn crossmix [a b w]
  "Return crossmix of trwo colors
   a - Color
   b - Clor
   w - float, the mix ratio 0.0 <= w <= 1.0"
  (let [ar (.getRed a)
        ag (.getGreen a)
        ab (.getBlue a)
        br (.getRed b)
        bg (.getGreen b)
        bb (.getBlue b)
        cr (int (math/interpolate ar br w))
        cg (int (math/interpolate ag bg w))
        cb (int (math/interpolate ab bb w))]
    (Color. cr cg cb)))

(defn inversion [c]
  "Return the color inversion"
  (let [r (.getRed c)
        g (.getGreen c)
        b (.getBlue c)]
    (Color. (- 255 r)(- 255 g)(- 255 b))))

(defn getHSB [c]
  "Return the HSB representatin of the color
   The result is a float array [hue, saturation, value]
   where each element  e 0.0 <= e <= 1.0"
  (let [r (.getRed c)
        g (.getGreen c)
        b (.getBlue c)]
    (Color/RGBtoHSB r g b nil)))

(defn hue [c]
  "Return color's hue as float 0.0 <= hue <= 1.0"
  (first (getHSB c)))

(defn saturation [c]
  "Return color's saturation. 0.0 <= saturation <= 1.0"
  (second (getHSB c)))

(defn value [c]
  "Return color's value 0.0 <= value <= 1.0"
  (third (getHSB c)))

(defn shift [c shift]
  "Shift color's hue"
  (let [hsb (getHSB c)
        h (+ (first hsb) shift)
        s (second hsb)
        v (third hsb)]
    (Color/getHSBColor h s v)))

(defn complementry [c]
  "Returns complementry color"
  (shift c 0.3))

(defn darker
  "Returns darker version of color
   Optional n argument iterates the process n times"
  ([c n]
     (if (zero? n) 
       c
       (recur (.darker c)(dec n))))
  ([c]
     (darker c 1)))

(defn brighter
  "Returns brighter version of color
   Optional n argument iterates the processs n times"
  ([c n]
     (if (zero? n)
       c
       (recur (.brighter c)(dec n))))
  ([c]
     (brighter c 1)))

(defn saturate [c n]
  "Change color's saturation by n
   -1.0 <= n <= +1.0"
  (let [hsb (getHSB c)
        h (first hsb)
        s (math/clamp (+ (second hsb) n) 0.0 1.0)
        v (third hsb)]
    (Color/getHSBColor h s v)))

(defn value [c n]
    (let [hsb (getHSB c)
          h (first hsb)
          s (second hsb)
          v (math/clamp (+ (third hsb) n) 0.0 1.0)]
    (Color/getHSBColor h s v)))

;; (defn channel-color-cue [c]
;;   (let [h (float (/ (rem c 8) 8))
;;         s (/ c 16.0)
;;         b (if (< c 8) 1.000 0.250)
;;         bg (hsb h s b)
;;         fg (if (< c 8) (Color/BLACK)(Color/WHITE))]
;;     [bg fg]))
