;; TODO
;;    1. Add image creation from file

(ns sgwr.elements.image
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:require [sgwr.util.math :as math])
  (:import java.awt.image.BufferedImage))


(defn render-image [iobj g2d]
  (let [cs (.coordinate-system iobj)
        p0 (first (.points iobj))
        q0 (.map-point cs p0)
        [x y] q0
        img (.get-property iobj :image)]
    (.drawImage g2d img constants/null-transform-op (int x)(int y))))

(defn- update-fn [obj points]
  (let [img (.get-property obj :image)
        w (.getWidth img)
        h (.getHeight img)
        p0 (first points)
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        p1 [x1 y1]]
    [p0 p1]))

(defn- distance-helper [obj q]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        u0 (min (first q0)(first q1))
        u1 (max (first q0)(first q1))
        v0 (min (second q0)(second q1))
        v1 (max (second q0)(second q1))
        [u v] (.map-point cs q)
        contains-flag (and (<= u0 u)(<= u u1)(<= v0 v)(<= v v1))
        distance (if contains-flag 
                   0
                   (math/point-rectangle-distance [u v] q0 q1))]
    [contains-flag distance]))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]]))

(def ^:private image-function-map {:shape-fn (constantly constants/null-shape)
                                   :contains-fn (fn [obj q](first (distance-helper obj q)))
                                   :distance-fn (fn [obj q](second (distance-helper obj q)))
                                   :update-fn update-fn
                                   :bounds-fn bounds-fn})

(def locked-properties [:id :image]) 
                                  
(defn image 
  ([parent p0 w h]
   (let [obj (sgwr.elements.element/create-element :image
                                                   parent 
                                                   image-function-map
                                                   locked-properties)
         img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)]
     (.put-property! obj :image img)
     (.put-property! obj :id :image)
     (.set-points! obj [p0])
     (if parent (.set-parent! obj parent))
     obj)))
