(ns sgwr.utilities
  (:require [seesaw.color :as ssc])
  (:import java.awt.geom.Path2D
           java.awt.Color))


; ---------------------------------------------------------------------- 
;                                   Math

(def ^:private k-rad (/ Math/PI 180))
(def ^:private k-deg (/ 1 k-rad))

(defn deg->rad [d]
  "Convert degrees to radians"
  (* d k-rad))

(defn rad->deg [r]
  "Convert radians to degrees"
  (* r k-deg))

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


(defprotocol ShiftRegister

  (clear! [this]
    "Reset register to 0")

  (shift! [this n]
    "Shift register left then insert n at end
     Return register value")

  (shift-right! [this n]
    "Shift register right, insert n")

  (value [this]
    "Return current value of register")

  (dump [this])
  )


(defn shift-register [cell-count scale]
  (let [data* (atom (into [] (repeat cell-count 0)))]
    (reify ShiftRegister

      (clear! [this]
        (reset! data* (into [] (repeat cell-count 0)))
        0.0)
      
      (shift! [this n]
        (let [acc* (atom [])]
          (doseq [i (range (- cell-count 2) -1 -1)]
            (swap! acc* (fn [q](conj q (nth @data* i)))))
          (swap! acc* (fn [q](conj q n)))
          (reset! data* (reverse @acc*))
          (.value this)))
      
      (shift-right! [this n]
        (let [acc* (atom [])]
          (doseq [i (range 1 cell-count)]
            (swap! acc* (fn [q](conj q (nth @data* i)))))
          (swap! acc* (fn [q](conj q n)))
          (reset! data* @acc*)
          (.value this)))

      (dump [this]
        (println (format "Register %s  value %s" @data* (.value this))))

      (value [this]
        (let [acc* (atom 0)]
          (dotimes [i cell-count]
            (let [dec (Math/pow 10 i)]
              (swap! acc* (fn [q](+ q (* dec (nth @data* i)))))))
          (* scale @acc*))))))

; ---------------------------------------------------------------------- 
;                                  Shapes

(defn combine-shapes 
  "Combine two instances of java.awt.Shape"
  ([s1 s2 connect]
     (let [p1 (java.awt.geom.Path2D$Double. s1)]
       (.append p1 s2 connect)
       p1))
  ([s1 s2]
     (combine-shapes s1 s2 false)))


; ---------------------------------------------------------------------- 
;                                   Color

;; As with seesaw.color/color except that if the first argument 
;; is an instance of java.awt.Color it becomes the return color.
;;
(defn color [& args]
  (cond (= (type (first args)) Color) (first args)
        (keyword? (first args))(ssc/color (first args))
        :default (apply ssc/color args)))
