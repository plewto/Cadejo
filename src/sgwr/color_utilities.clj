(ns sgwr.color-utilities
  "Provides functions for color manipulation"
  (:require [sgwr.utilities :as util])
  (:require [seesaw.color :as ssc])
  (:import java.awt.Color))

(defn crossmix [a b mix]
  "Return color mixture
   a - Color
   b - Color
   mix - float, 0.0 <= mix <= 1.0, relative amounts of a and b in result
         mix = 0 --> return b
         mix = 1 --> return b"
  (let [w (float (util/clamp mix 0 1))
        ar (.getRed a)
        ag (.getGreen a)
        ab (.getBlue a)
        br (.getRed b)
        bg (.getGreen b)
        bb (.getBlue b)
        cr (int (util/interpolate ar br w))
        cg (int (util/interpolate ag bg w))
        cb (int (util/interpolate ab bb w))]
    (ssc/color cr cg cb)))

(defn inversion [c]
  "Return color inversion"
  (let [r (- 255 (.getRed c))
        g (- 255 (.getGreen c))
        b (- 255 (.getBlue c))]
    (ssc/color r g b)))

(defn hsb [c]
  "Returns color hue, saturation and brightness as float array [h s b]
   See java.awt.Color"
  (let [r (.getRed c)
        g (.getGreen c)
        b (.getBlue c)]
    (Color/RGBtoHSB r g b nil)))

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
        s2 (util/clamp (* s s1) 0 1)
        b2 (util/clamp (* b b1) 0 1)]
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

(defn gradient-list [a b count]
  "Returns list of count colors which crossfade between a and b
   a - Color, initial color
   b - Color, final color
   count - int, number of colors"
  (let [acc* (atom '())]
    (dotimes [n count]
      (let [w (/ (float n) count)]
        (swap! acc* (fn [n](conj n (crossmix a b w))))))
    (reverse @acc*)))

(defn bi-gradient-list [a1 a2 b1 b2 breakpoint count]
  "Returns list of count colors defining 2 gradients
   a1 - Color, initial color of gradient a
   a2 - Color, final color of gradient a
   b1 - Color, initial color of gradient b
   b2 - Color, final color of gradient b
   breakpoint - float, 0.0 <= breakpoint <= 1.0, relative position of 
                gradient crossover
   count - int, total number of colors"
  (let [bp (float (util/clamp breakpoint 0 1))
        b-count (int (* count bp))
        a-count (- count b-count)
        acc* (atom (gradient-list a1 a2 a-count))]
    (doseq [b (reverse (gradient-list b1 b2 b-count))]
        (swap! acc* (fn [n](conj n b))))
    @acc*))
