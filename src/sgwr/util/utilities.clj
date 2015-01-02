;; General utilities
;;
(ns sgwr.util.utilities
  (:require [sgwr.constants :as constants])
  (:import java.awt.geom.Line2D
           java.awt.geom.Path2D))


(defn warning [msg]
  (println (format "sgwr WARNING: %s" msg))
  nil)

(defn map-style [st]
  (let [q (get constants/style-map st)]
    (int (or q st))))

(defn member? [obj col]
  "Predicate true if obj is = to some element of collection."
  (some (fn [n](= n obj)) col))

(defn not-member? [obj col]
  (not (member? obj col)))

(defn tab 
  ([n]
     (if (> n 0)
       (let [frmt (format "%%%ds" (* n 4))]
         (format frmt ""))
       ""))
  ([](tab 1)))

;; jaba.awt.shape utilities

(defn combine-shapes 
  "Combine two instances of java.awt.Shape"
  [s1 s2]
  (let [p1 (java.awt.geom.Path2D$Double. s1)]
    (.append p1 s2 false)
    p1))
 
(defn fuse [& args]
  (reduce combine-shapes args))


;; Predicates

(defn is-group? [obj]
  (try
    (= (.element-type obj) :group)
    (catch IllegalArgumentException ex
      false)))

(defn is-point? [obj]
  (try
    (= (.element-type obj) :point)
    (catch IllegalArgumentException ex
      false)))

(defn is-line? [obj]
  (try
    (= (.element-type obj) :line)
    (catch IllegalArgumentException ex
      false)))

(defn is-rectangle? [obj]
  (try
    (= (.element-type obj) :rectangle)
    (catch IllegalArgumentException ex
      false)))

(defn is-ellipse? [obj]
  (try
    (= (.element-type obj) :ellipse)
    (catch IllegalArgumentException ex
      false)))

(defn is-circle? [obj]
  (try
    (= (.element-type obj) :circle)
    (catch IllegalArgumentException ex
      false)))

(defn is-text? [obj]
  (try
    (= (.element-type obj) :text)
    (catch IllegalArgumentException ex
      false)))

(defn is-image? [obj]
  (try
    (= (.element-type obj) :image)
    (catch IllegalArgumentException ex
      false)))
