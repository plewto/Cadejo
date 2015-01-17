(ns sgwr.cs.polar
  (:require [sgwr.cs.coordinate-system :as cs])
  (:require [sgwr.util.math :as umath])
  (:require [sgwr.util.utilities :as utilities]))

(defn polar-coordinate-system [w h r & {:keys [origin unit]
                                         :or {origin [nil nil]
                                              unit :rad}}]
  "The polar coordinate systems maps defines points by their distance from
   and angle relative to the origin. 

   w      - int, drawing width in pixels
   h      - int, drawing height in pixels
   r      - float, maximum amplitude
   origin - pair [x y], location of polar origin in pixels
   unit   - keyword, angle unit, either :rad, :deg or :turn, default :rad"                                       
  (let [params* (atom {:width (int w)
                       :height (int h)
                       :x-offset (or (first origin)(* 1/2 (int w)))
                       :y-offset (or (second origin)(* 1/2 (int h)))
                       :radius (float r)
                       :scale (float 0.0)})
        unit-hook (cond (= unit :deg) umath/deg->rad
                        (= unit :turn) umath/turn->rad
                        :default identity)
        inv-unit-hook (cond (= unit :deg) umath/rad->deg
                            (= unit :turn) umath/rad->turn
                            :default identity)
        zoom-ratio* (atom 1.0) 
        cs (reify cs/CoordinateSystem

             (cs-type [this] :polar)

             (canvas-bounds [this]
               [(:width @params*)(:height @params*)])

             (view [this]
               (:radius @params*))

             (restore-view! [this]
               (.view! this r)
               (reset! zoom-ratio* 1.0)
               (.view this))

             (set-view! [this r]
               (.view! this r)
               (reset! zoom-ratio* 1.0)
               (.view this))

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
                     x-offset (:x-offset @params*)
                     y-offset (:y-offset @params*)
                     scale (:scale @params*)
                     u (+ x-offset (* scale r (Math/cos phi)))
                     v (+ y-offset (* scale r (Math/sin (* -1 phi))))]
                 (.clip this [u v])))

             ;; 'physical' point q, pixel [column row] --> [radius phi]
             (inv-map [this q]
               (let [[u v] q
                     x-offset (:x-offset @params*)
                     y-offset (:y-offset @params*)
                     scale (:scale @params*)
                     u2 (- u x-offset)
                     v2 (- y-offset v)
                     r (/ (Math/sqrt (+ (Math/pow u2 2)(Math/pow v2 2))) scale)
                     phi (let [p (Math/atan2 v2 u2)]
                           (if (neg? p)(+ p (* 2 Math/PI)) p))]
                 [r (inv-unit-hook phi)]))

             (map-x [this x]
               (utilities/warning "polar-coordinate-system map-x is not defined")
               x)

             (inv-map-x [this u]
               (utilities/warning "polar-coordinate-system inv-map-x is not defined")
               u)
             
             (map-y [this y]
               (utilities/warning "polar-coordinate-system map-y is not defined")
               y)
             
             (inv-map-y [this v]
               (utilities/warning "polar-coordinate-system inv-map-y is not defined")
               v)

             (x-scale [this]
               (:scale @params*))

             (y-scale [this]
               (:scale @params*))

             (clip [this q] q)

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
