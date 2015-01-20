(ns sgwr.constants
  (:import 
   java.awt.geom.AffineTransform
   java.awt.image.AffineTransformOp
   java.awt.geom.Line2D
))


(def +VERSION+ "0.2.0-SNAPSHOT")

(def widget-types '[:button :radio-button :multistate-button :slider ])

(def default-color :lightgray)
(def default-rollover-color :red)
(def default-pressed-color  :yellow)


(def min-style -2)
(def max-style 35)

(def point-styles {:default 0
                   :dot 0
                   :pixel 1
                   :dash 2
                   :bar 3
                   :diag 4
                   :diag2 5
                   :box 6
                   :cross 7
                   :x 8
                   :triangle 9
                   :right-chevron 10
                   :right-arrow 11
                   :left-chevron 12
                   :left-arrow 13
                   :up-chevron 14
                   :up-arrow 15
                   :down-chevron 16
                   :done-arrow 17
                   :filled-dot -1
                   :filled-box -2
                   })

(def line-styles {:solid 0
                  :dotted 1
                  :dash 2
                  :short-dash 3
                  :long-dash 4
                  :center 5
                  :dot-dash 6
                  })

(def font-styles {:mono        0           ; font styles %ibff
                  :sans        1
                  :serif       2
                  :dialog      3
                  :mono-bold   4           ; %01ff 
                  :sans-bold   5
                  :serif-bold  6
                  :dialog-bold 7
                  :mono-italic    8        ; %10ff
                  :sans-italic    9
                  :serif-italic  10
                  :dialog-italic 11
                  :mono-bold-italic   12   ; %11ff
                  :sans-bold-italic   13
                  :serif-bold-italic  14
                  :dialog-bold-italic 15})

(def style-map (merge point-styles line-styles font-styles))

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
(def infinity 1e999)
