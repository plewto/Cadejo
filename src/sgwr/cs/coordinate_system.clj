(println "--> sgwr.cs.coordinate-system")
(ns sgwr.cs.coordinate-system
  "Defines genralized mapping scheme between 'real' points and pixel coordinates"
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))

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

  (distance 
    [this p1 p2]
    "Return distance between points p1 and p2")

  (zoom!
    [this ratio]
    "Zoom into view by fixed ratio")

  (zoom-ratio 
    [this]
    "Returns the zoom ratio")

  (to-string
    [this])
  )



(def default-coordinate-system 
  (reify CoordinateSystem

    (canvas-bounds [this] 
      (utilities/warning "default-coordinate-system view canvasbounds is nil")
      nil)

    (view [this] (.canvas-bounds this))

    (view! [this _] (.view this))

    (map-point [this p] p)

    (inv-map [this q] q)

    (clip [this q] [nil nil])

    (distance [this p1 p2]
      (math/distance p1 p2))

    (zoom! [this _] (.view this))

    (zoom-ratio [this] nil)

    (to-string [this]
      "default-coordinate-system")))
