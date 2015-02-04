(ns sgwr.demos.demo5
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.elements.drawing :reload true])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.rule :as rule])
  (:require [sgwr.elements.mesh :as mesh])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.circle :as circle])

  (:require [sgwr.widgets.multistate-button :as msb :reload false])
  (:require [sgwr.widgets.radio :as radio :reload false])
  (:require [sgwr.widgets.slider :as slider :reload true])
  (:require [sgwr.widgets.dual-slider :as dslider :reload true])
  (:require [sgwr.widgets.field :as field :reload false])
  (:require [seesaw.core :as ss]))

; ---------------------------------------------------------------------- 
;                               Drawing drw1

(def drw1 (sgwr.elements.drawing/native-drawing 400 400))
(def root1 (.root drw1))
(def widgets1 (.widget-root drw1))

(rect/rectangle root1 [100 100][300 300])
(circle/circle root1 [100 150][200 250] :color :green)
(.background! drw1 :black)


; ---------------------------------------------------------------------- 
;                               Drawing drw2

(def drw2 (sgwr.elements.drawing/native-drawing 400 400))
(def root2 (.root drw2))
(def widgets2 (.widget-root drw2))


(.background! drw2 [32 32 32])

(.render drw1)
(.render drw2)

(def pan-main (ss/horizontal-panel :items [(.canvas drw1)(.canvas drw2)]))

(def f (ss/frame :title "Sgwr Demo 5"
                 :content pan-main
                 :on-close :dispose
                 :size [850 :by 450]))

(ss/show! f)

(defn rl [](use 'sgwr.demos.demo5 :reload))
(defn rla [](use 'sgwr.demos.demo5 :reload-all))
(defn exit [](System/exit 0))
