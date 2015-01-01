(ns sgwr.cs.cartesian
   (:require [sgwr.cs.coordinate-system :as cs])
   (:require [sgwr.util.math :as math]))

(defn cartesian-coordinate-system [w h p0 p1]
  "The Cartesian coordinate system maps a rectangular region defined by 
   diagonal points p0 and p1 to a 'physical' canvas of width w and height h.
   Typically increasing x values map horizontally left to right and
   increasing y values map vertically bottom to top. It is possible to 
   reverse the directional sense of either the horizontal or vertical 
   mappings."
  (let [bounds [w h]
        view* (atom [p0 p1])
        x-scale* (atom 1.0)
        x-offset* (atom 0.0)
        y-scale* (atom 1.0)
        y-offset* (atom 0.0)
        zoom-ratio* (atom 1.0)
        cs (reify cs/CoordinateSystem

             (canvas-bounds [this] 
               bounds)

             (view [this]
               @view*)

             ; view v as pair [p0 p1]
             (view! [this v]
               (let [[p0 p1] v
                     [x0 y0] p0
                     [x1 y1] p1
                     xa (/ w (- (float x1) x0))
                     xb (* -1 xa x0)
                     ya (/ h (- (float y0) y1))
                     yb (- h (* ya y0))]
                 (reset! x-scale* xa)
                 (reset! x-offset* xb)
                 (reset! y-scale* ya)
                 (reset! y-offset* yb)
                 (reset! view* v)
                 v))

             (map-point [this p]
               (let [[x y] p
                     u (int (+ @x-offset* (* @x-scale* x)))
                     v (int (+ @y-offset* (* @y-scale* y)))]
                 (.clip this [u v])))

             (inv-map [this q]
               (let [[u v] q]
                 [(/ (- u @x-offset*) @x-scale*)
                  (/ (- v @y-offset*) @y-scale*)])) 

             (clip [this q]
               (let [[u v] q]
                 [(math/clamp u 0 w)
                  (math/clamp v 0 h)]))
             
             (distance [this p1 p2]
               (math/distance p1 p2))

             (zoom! [this ratio]
               (let [[p0 p1] @view*
                     [x0 y0] p0
                     [x1 y1] p1
                     x-mean (* 1/2 (+ x0 x1))
                     x-delta (* ratio (- x1 x-mean))

                     y-mean (* 1/2 (+ y0 y1))
                     y-delta (* ratio (- y1 y-mean))]
                 (swap! zoom-ratio* (fn [n](* n ratio)))
                 (.view! this [[(- x-mean x-delta)(- y-mean y-delta)]
                               [(+ x-mean x-delta)(+ y-mean y-delta)]])))
             
             (zoom-ratio [this]
               @zoom-ratio*)

             (to-string [this]
               (let [[p0 p1] @view*
                     [x0 y0] p0
                     [x1 y1] p1]
                 (str 
                  (format "cartesian bounds [%d %d] " (int w)(int h))
                  (format "view [%f %f] [%f %f]"
                          (float x0)(float y0)
                          (float x1)(float y1))))))]
    (.view! cs [p0 p1])
    cs))



(def default-coordinate-system (cartesian-coordinate-system 1 1 [0 0][1 1])) 
