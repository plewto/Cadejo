(ns sgwr.cs.coordinate-system
  "Defines generalized mapping scheme between 'real' points and pixel coordinates"
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))

(defprotocol CoordinateSystem

  (cs-type 
    [this]
    "(cs-type this)
     Returns keyword for coordinate system type")

  (canvas-bounds
    [this]
    "(canvas-bounds this)
    Returns size of 'physical' canvas as pair [width height]")

  (view
    [this]
    "(view this)
     Returns the current 'view' as a vector.
     The exact format of the result is implementation dependent.")

  (set-view!
    [this v]
    "(set-view! this v)
     Set current view to v
     The exact format of v is implementation dependent.
     Use this method instead of view!
     Returns the current view.")

  (view!
    [this v]
    "(view! this v) 
     Set the current view to v 
     The exact format of v is implementation dependent.
     DO NOT CALL VIEW! DIRECTLY, instead use set-view!
     Returns the current view.")

  (restore-view!
    [this]
    "(restore-view! this v)
     Return to the initial default view")

  (map-point
    [this p]
    "(map-point this p)
     Convert 'real' point p [x y] to pixel coordinates [column row]")

  (inv-map
    [this q]
    "(inv-map this q)
     Inversion of map-point, converts pixel coordinates [column row]
     to 'real' point [x y]")

  (map-x 
    [this x]
    "(map-x this x)
     Optional method 
     Maps horizontal x value to pixel column
     If not implemented should display warning message and return it's argument")

  (inv-map-x
    [this u]
    "(inv-map-x this u)
     Optional method
     Maps pixel row to x value
     If not implemented should display warning message and return it's argument")

  (map-y
    [this y]
    "(map-y this y)
     Optional method 
     Maps vertical y value to pixel row
     If not implemented should display warning message and return it's argument")

  (inv-map-y
    [this v]
    "(inv-map-y this v)
     Optional method
     Maps pixel column to y value
     If not implemented should display warning message and return it's argument")

  (x-scale 
    [this]
    "(x-scale this)
     Returns x scale factor")

  (y-scale
    [this]
    "(y-scale this)
     Returns y scale factor")

  (clip 
    [this q]
    "(clip this q)
     Clip 'physical' point q to canvas bounds
     For point q [u v]  0 <= u < width  and 0 <= v < height
     clip is an optional method, if not required it should 
     ct as an identity and return q unchanged.")

  (units 
    [this]
    "(units this)
     Returns vector of dimensional units
     nil indicates dimension less values")

  (distance 
    [this pa pa]
    "(distance pa pa)
     Return distance between points pa and pa")

  (zoom!
    [this ratio]
    "(zoom! this ratio)
     Zoom into view by fixed ratio")

  (zoom-ratio 
    [this]
    "(zoom-ratio this)
     Returns the zoom ratio")

  (to-string
    [this])
  )



(def default-coordinate-system 
  (reify CoordinateSystem

    (cs-type [this] :native)

    (canvas-bounds [this] 
      (utilities/warning "default-coordinate-system view canvas-bounds is nil")
      nil)

    (restore-view! [this](.view this))

    (set-view! [this _](.view this))

    (view [this] (.canvas-bounds this))

    (view! [this _] (.view this))

    (map-point [this p] p)

    (inv-map [this q] q)

    (map-x [this x] x)

    (inv-map-x [this u] u)

    (map-y [this y] y)

    (inv-map-y [this v] v)

    (x-scale [this] 1)

    (y-scale [this] 1)

    (clip [this q] q)

    (units [this] [nil nil])

    (distance [this pa pa]
      (math/distance pa pa))

    (zoom! [this _] (.view this))

    (zoom-ratio [this] nil)

    (to-string [this]
      "default-coordinate-system")))
