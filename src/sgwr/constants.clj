(ns sgwr.constants
  (:import 
   java.awt.geom.AffineTransform
   java.awt.image.AffineTransformOp
   java.awt.geom.Line2D
))


(def +VERSION+ "0.2.0-SNAPSHOT")

(def min-style 0)
(def max-style 9)

(def style-map {:default 0
                :dot 0                  ; Point styles
                :pixel 1
                :dash 2                 ; used for point and medium line dash
                :bar 3
                :diag 4
                :diag2 5
                :box 6
                :cross 7
                :x 8
                :triangle 9

                :solid 0                ; line styles
                :dotted 1
                :short-dash 3
                :long-dash 4
                :center 5               ; dot-dash
                :dot-dash 6
                })

(def dot-styles {:default 0
                 :dot 0
                 :pixel 1
                 :dash 2
                 :bar 3
                 :diag 4
                 :diag2 5
                 :box 6
                 :cross 7
                 :x 8
                 :triangle 9})

(def line-styles {:solid 0
                  :dotted 1
                  :dash 2
                  :short-dash 3
                  :long-dash 4
                  :center 5
                  :dot-dash 6
                  })

(def dash-patterns {0 [1.0]
                    1 [1.0 4.0]
                    2 [6.0 6.0]
                    3 [4.0 4.0]
                    4 [12.0 12.0]
                    5 [12.0 6.0 2.0 6.0]
                    6 [2.0 4.0 6.0 4.0]})


(def null-shape (java.awt.geom.Line2D$Double.)) 

(def null-transform (AffineTransform.))
(def null-transform-op  (AffineTransformOp.
                         null-transform AffineTransformOp/TYPE_NEAREST_NEIGHBOR))
