(ns sgwr.constants
  (:import 
   java.awt.geom.AffineTransform
   java.awt.image.AffineTransformOp
   java.awt.geom.Line2D
))


(def +VERSION+ "0.2.0-SNAPSHOT")

(def widget-types '[:button :radio-button :multistate-button :slider :dual-slider
                    :field :ball])

(def default-color :lightgray)
(def default-rollover-color :yellow)
;(def default-pressed-color  :yellow)


;; (def min-style 0)
;; (def max-style 0x80000)


;; (def fill      0x00001)
;; (def pixel     0x00010)
;; (def dot       0x00020)
;; (def bar       0x00040)
;; (def dash      0x00080)
;; (def diag      0x00100)
;; (def diag2     0x00200)
;; (def triangle  0x00400)
;; (def chevron-n 0x00800)
;; (def chevron-e 0x01000)
;; (def chevron-s 0x02000)
;; (def chevron-w 0x04000)
;; (def edge-n    0x08000)
;; (def edge-e    0x10000)
;; (def edge-s    0x20000)
;; (def edge-w    0x40000)
;; (def box       0x80000)
;; (def cross     (bit-or bar dash))
;; (def x         (bit-or diag diag2))
;; (def arrow-n   (bit-or bar chevron-n))
;; (def arrow-s   (bit-or bar chevron-s))
;; (def arrow-e   (bit-or bar chevron-e))
;; (def arrow-w   (bit-or bar chevron-w))


;; (def point-styles {:default   0x08
;;                    :fill      0x00001
;;                    :pixel     0x00010
;;                    :dot       0x00020
;;                    :bar       0x00040
;;                    :dash      0x00080
;;                    :diag      0x00100
;;                    :diag2     0x00200
;;                    :triangle  0x00400
;;                    :chevron-n 0x00800
;;                    :chevron-e 0x01000
;;                    :chevron-s 0x02000
;;                    :chevron-w 0x04000
;;                    :edge-n    0x08000
;;                    :edge-e    0x10000
;;                    :edge-s    0x20000
;;                    :edge-w    0x40000
;;                    :box       0x80000
;;                    :cross     (+ 0x08 0x10)
;;                    :x         (+ 0x20 0x40)
;;                    :arrow-n   (+ 0x08 0x100)
;;                    :arrow-s   (+ 0x08 0x400)
;;                    :arrow-e   (+ 0x10 0x200)
;;                    :arrow-w   (+ 0x10 0x800)})




;; (def font-styles {:mono        0           ; font styles %ibff
;;                   :sans        1
;;                   :serif       2
;;                   :dialog      3
;;                   :mono-bold   4           ; %01ff 
;;                   :sans-bold   5
;;                   :serif-bold  6
;;                   :dialog-bold 7
;;                   :mono-italic    8        ; %10ff
;;                   :sans-italic    9
;;                   :serif-italic  10
;;                   :dialog-italic 11
;;                   :mono-bold-italic   12   ; %11ff
;;                   :sans-bold-italic   13
;;                   :serif-bold-italic  14
;;                   :dialog-bold-italic 15})

;def style-map (merge point-styles line-styles font-styles))

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
