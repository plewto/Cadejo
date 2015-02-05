(ns sgwr.constants
  (:import 
   java.awt.geom.AffineTransform
   java.awt.image.AffineTransformOp
   java.awt.geom.Line2D))


(def +VERSION+ "0.2.0-SNAPSHOT")

(def tool-types '[:button :radio-button :multistate-button :slider :dual-slider
                    :field :ball])

(def default-color :lightgray)
(def default-rollover-color :yellow)


(def dash-patterns {0 [1.0]
                    1 [1.0 4.0]
                    2 [6.0 6.0]
                    3 [4.0 4.0]
                    4 [12.0 12.0]
                    5 [12.0 6.0 2.0 6.0]
                    6 [2.0 4.0 6.0 4.0]})

(def line-styles {:solid 0
                  :dotted 1
                  :dash 2
                  :short-dash 3
                  :long-dash 4
                  :center 5
                  :dot-dash 6
                  0 0
                  1 1
                  2 2
                  3 3
                  4 4
                  5 5
                  6 6
                  })

(def null-shape (java.awt.geom.Line2D$Double.)) 
(def null-transform (AffineTransform.))
(def null-transform-op  (AffineTransformOp.
                         null-transform AffineTransformOp/TYPE_NEAREST_NEIGHBOR))
(def infinity 1e999)
