(ns sgwr.cs.native
  (:require [sgwr.cs.coordinate-system :as cs])
  (:require [sgwr.util.math :as math]))


(defn native-coordinate-system [w h]
  "Native is the most basic coordinate system with no distinction between 
   'real' points and pixel coordinates. The native coordinate system is in a 
   sense an identity mapping. Increasing x values map directly to pixel 
   columns left-to-right. Increasing y values map to pixel rows 
   top-to-bottom"

  (let [bounds [w h]]

    (reify cs/CoordinateSystem

      (canvas-bounds [this] 
        bounds)

      (view [this]
        bounds)

      (restore-view! [this] )

      (view! [this _]  ; ignore, view is constant
        bounds)

      (map-point [this p]
        (.clip this [(int (first p))(int (second p))]))

      (inv-map [this q]
        [(float (first q))(float (second q))])

      (map-x [this x] (int x))

      (inv-map-x [this u] (float u))

      (map-y [this y] (int y))

      (inv-map-y [this v] (float v))

      (clip [this q]
        (let [[u v] q]
          [(math/clamp u 0 w)
           (math/clamp v 0 h)]))

      (distance [this p1 p2]
        (math/distance p1 p2))

      (zoom! [this _] nil)  ;; ignore

      (zoom-ratio [this] 1.0)

      (to-string [this]
        (format "native bounds [%d %d]" (int w)(int h)))

      ))) 
