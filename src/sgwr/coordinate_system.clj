(ns sgwr.coordinate-system
  "Defines various mapping schemes between 'real' points and pixel coordinates"
  (:require [sgwr.utilities :as util]))


(defprotocol CoordinateSystem

  (canvas-bounds
    [this]
    "Returns size of 'physical' canvas as pair [width height]")

  (view
    [this]
    "Returns the current 'view' as a vector.
     The exact format of the result is implementation dependent.")

  (view!
    [this v]
    "Set the current view to v 
     The exact format of v is implementation dependent.")

  (map-point
    [this p]
    "Convert 'real' point p [x y] to pixel coordinates [column row]")


  (inv-map
    [this q]
    "Inversion of map-point, converts pixel coordinates [column row]
     to 'real' point [x y]")

  (clip 
    [this q]
    "Clip 'physical' point q to canvas bounds
     For point q [u v]  0 <= u < width  and 0 <= v < height")

  (zoom!
    [this ratio]
    "Zoom into view by fixed ratio")

  (zoom-ratio 
    [this]
    "Zoom out of view by fixed ratio")

  (to-string
    [this])

)


(defn native [w h]
  "Native is the most basic coordinate system with no distinction between 
   'real' points and pixel coordinates. The native coordinate system is in a 
    sense an identity mapping. Increasing x values map directly to pixel 
    columns left-to-right. Increasing y values map to pixel rows 
    top-to-bottom"

  (let [bounds [w h]]

    (reify CoordinateSystem

      (canvas-bounds [this] 
        bounds)

      (view [this]
        bounds)

      (view! [this _]  ; ignore, view is constant
        bounds)

      (map-point [this p]
        (.clip this [(int (first p))(int (second p))]))

      (inv-map [this q]
        [(float (first q))(float (second q))])

      (clip [this q]
        (let [[u v] q]
          [(util/clamp u 0 w)
           (util/clamp v 0 h)]))

      (zoom! [this _] nil)  ;; ignore

      (zoom-ratio [this] 1.0)

      (to-string [this]
        (format "native bounds [%d %d]" (int w)(int h))))))


(defn cartesian [w h p0 p1]
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
        cs (reify CoordinateSystem

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
                 [(util/clamp u 0 w)
                  (util/clamp v 0 h)]))
             
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



(defn polar [size r]
 
  "The polar coordinate system maps points based on their distance 
   from the origin and their angle of rotation from the polar-axis. 
   The origin is always placed at the center of the canvas with the 
   polar-axis extending horizontally to the right. Angles are measured
   counter-clockwise from the polar-axis in radians.

   size - 'physical' size of canvas in pixels, canvas is assumed to 
           be square.
 
   r - maximum radius of view as a real number."
 
  (let [params* (atom {:width (int size)
                       :height (int size)
                       :offset (* 1/2 (int size))
                       :radius (float r)
                       :scale (float 0.0)})
        zoom-ratio* (atom 1.0)
        cs (reify CoordinateSystem

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

             ;; point p [distance phi]
             (map-point [this p]
               (let [[r phi] p
                     offset (:offset @params*)
                     scale (:scale @params*)
                     u (+ offset (* scale r (Math/cos phi)))
                     v (+ offset (* scale r (Math/sin phi)))]
                 (.clip this [u v])))

             ;; 'physical' point q, pixel [column, row]
             (inv-map [this q]
               (let [[u v] q
                     offset (:offset @params*)
                     scale (:scale @params*)
                     x (- u offset)
                     y (- offset v)
                     r (/ (Math/sqrt (+ (Math/pow x 2)(Math/pow y 2))) scale)
                     phi (Math/atan2 y x)]
                 [r phi]))

             (clip [this q]
               (let [[u v] q]
                 [(util/clamp u 0 (:width @params*))
                  (util/clamp v 0 (:height @params*))]))

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
