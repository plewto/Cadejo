(ns sgwr.demos.demo5
   (:require [sgwr.elements.drawing :reload false])
   (:require [sgwr.elements.rule :as rule :reload false])
   (:require [sgwr.widgets.slider :as slider :reload false])
   (:require [sgwr.widgets.dual-slider :as dslider :reload false])
   (:require [sgwr.elements.mesh :as mesh :reload true])
   (:require [sgwr.elements.point :as point])
   (:require [sgwr.elements.line :as line])
   (:require [sgwr.elements.circle :as circle :reload true])
   (:require [sgwr.elements.rectangle :as rect])
   (:require [sgwr.widgets.field :as field :reload true])
 (:require [seesaw.core :as ss])
)             

(def drw1 (sgwr.elements.drawing/native-drawing 300 300))
(def drw2 (sgwr.elements.drawing/cartesian-drawing 300 300 [-100 -100][100 100]))
(def drw3 (sgwr.elements.drawing/polar-drawing 300 300  100.0 :unit :deg))
(def root1 (.root drw1))
(def root2 (.root drw2))
(def root3 (.root drw3))

;; Native drawing

(def f1 (field/field-mesh drw1 [100 100][200 200] [0 1][0 1]))
(def b11 (field/ball f1 :b1 [0 0] :color :red))

;; Cartesian drawing

(def f2 (field/field-mesh drw2 [0 0][90 90] [0 1][0 1]
                          :mesh [[10 10] :red 0 true]
                    ))
(def b21 (field/ball f2 :b2 [0 0] :color :green))


;; Polar drawing
(mesh/polar-mesh root3 (range 8 80 10) (range 0 360 45) :ray-gap 8 :ray-length 50)


(.background! drw1 [16 0 0])
(.background! drw2 [0 16 0])
(.background! drw3 [0 0 32])

(.render drw1)
(.render drw2)
(.render drw3)
(def pan-main (ss/grid-panel :rows 1
                             :items [(.canvas drw1)
                                     (.canvas drw2)
                                     (.canvas drw3)]))

(def f (ss/frame :title "Sgwr Demo 5"
                 :content pan-main
                 :on-close :dispose
                 :size [930 :by 400])) 
(ss/show! f)

(defn rl [](use 'sgwr.demos.demo5 :reload))
(defn rla [](use 'sgwr.demos.demo5 :reload-all))
(defn exit [](System/exit 0))
