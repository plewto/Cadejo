(ns sgwr.cs.polar
  (:require [sgwr.cs.coordinate-system :as cs])
  (:require [sgwr.util.math :as umath]))

(defn polar-coordinate-system [size r & {:keys [units]
                                         :or {units :rad}}]
  "The polar coordinate system maps points based on their distance 
   from the origin and their angle of rotation from the polar-axis. 
   The origin is always placed at the center of the canvas with the 
   polar-axis extending horizontally to the right. Angles are measured
   counter-clockwise from the polar-axis in radians (by default) or degrees.

   size - 'physical' size of canvas in pixels, canvas is assumed to 
           be square.
 
   r - maximum radius of view as a real number.

   :units - angular units options, possible values
            :deg - degrees
            :rad - radians (default)"
  (let [params* (atom {:width (int size)
                       :height (int size)
                       :offset (* 1/2 (int size))
                       :radius (float r)
                       :scale (float 0.0)})
        unit-hook (cond (= units :deg)
                        umath/deg->rad
                        :default
                        identity)
        inv-unit-hook (cond (= units :deg)
                            umath/rad->deg
                            :default
                            identity)
        zoom-ratio* (atom 1.0)
        cs (reify cs/CoordinateSystem

             (canvas-bounds [this]
               [(:width @params*)(:height @params*)])

             (view [this]
               (:radius @params*))

             ;; view float r = radius
             (view! [this r]
               (let [scale (/ (* 0.5 (:width @params*)) r)]
                 (swap! params* (fn [n](assoc n
                                         :radius (float r)
                                         :scale (float scale))))
                 (.view this)))

             ;; point p [r phi]  --> pixel [column row]
             (map-point [this p]
               (let [r (first p)
                     phi (unit-hook (second p))
                     offset (:offset @params*)
                     scale (:scale @params*)
                     u (+ offset (* scale r (Math/cos phi)))
                     v (+ offset (* scale r (Math/sin (* -1 phi))))]
                 (.clip this [u v])))

             ;; 'physical' point q, pixel [column row] --> [radius phi]
             (inv-map [this q]
               (let [[u v] q
                     offset (:offset @params*)
                     scale (:scale @params*)
                     u2 (- u offset)
                     v2 (- offset v)
                     r (/ (Math/sqrt (+ (Math/pow u2 2)(Math/pow v2 2))) scale)
                     phi (let [p (Math/atan2 v2 u2)]
                           (if (neg? p)(+ p (* 2 Math/PI)) p))]
                 [r (inv-unit-hook phi)]))

             (clip [this q]
               (let [[u v] q]
                 [(umath/clamp u 0 (:width @params*))
                  (umath/clamp v 0 (:height @params*))]))

             (distance [this p1 p2]
               (let [[r1 phi1] p1
                     [r2 phi2] p2]
                 (Math/sqrt (- (+ (* r1 r1)(* r2 r2))
                               (* 2 r1 r2 (Math/cos (- (unit-hook phi1) (unit-hook phi2))))))))
             
             (zoom! [this ratio]
               (let [r2 (/ (:radius @params*) ratio)
                     s2 (/ (:scale @params*) ratio)]
                 (swap! params* (fn [n](assoc n :ratio r2 :scale s2)))
                 (swap! zoom-ratio* (fn [n](* n ratio)))
                 (.view this)))

             (zoom-ratio [this]
               @zoom-ratio*)

             (to-string [this]
               (format "polar bounds [%d %d] radius %f"
                       (:width @params*)
                       (:height @params*)
                       (:radius @params*))) )]
   (.view! cs r)
    cs))
