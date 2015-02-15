(ns sgwr.demos.demo5
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.components.circle :as circle])
  (:require [sgwr.components.component :as component])
  (:require [sgwr.components.drawing :as drawing])
  (:require [sgwr.components.group :as group])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.mesh :as mesh])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.text :as text])
  (:require [seesaw.core :as ss]))



(def drw (drawing/native-drawing 900 700))
(def root (.root drw))

(def c1 (circle/circle root [50 50][100 100]))
(def a1 (line/line root [50 500][100 200] :id :segment-1))
(def p1 (point/point root [200 100] :size 6 :style [:dot]))
(def r1 (rect/rectangle root [200 500][400 200]))
(def tx (text/text root [300 300] "Why" :size 8))


(def jb (ss/button :text "Change"))

(ss/listen jb :action (fn [_]
                        (.set-points! c1  [[450 450][550 550]])
                        (.set-points! a1  [[450 500][650 200]])
                        (.set-points! p1  [[400 100]])
                        (.set-points! r1  [[600 500][800 200]])
                        (.set-points! tx  [[700 500]])
                        (.render drw)))

(def pan-main (ss/border-panel :center (.canvas drw)
                               :south jb
                               ))

(.background! drw :black)
(.render drw)
(def f (ss/frame :content pan-main
                  :on-close :dispose
                  :size [910 :by 710]))

(ss/show! f)


(defn rl [](use 'sgwr.demos.demo5 :reload))
(defn rla [](use 'sgwr.demos.demo5 :reload-all))
(defn exit [](System/exit 0))
