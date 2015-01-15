;; demos1 
;; Uses native coordinate system, displays basic elements.
;;

(ns sgwr.demos.demo1
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.drawing :as drw])
  (:require [sgwr.elements.group :as grp])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss]))


(def set-attributes! sgwr.elements.element/set-attributes!)

(def drw (drw/native-drawing 600 600))
(def root (.root drw))
(def cs (.coordinate-system root))
(text/text root [60 20] "Sgwr Demo 1 ~ 600 x 600 Native Coordinate System" :size 8)
(line/line root [10 30][590 30])

(def group-text (grp/group root :color [64 196 16] :size 7 :style 1))

(defn text-obj [pos, txt]
  (text/text group-text pos txt :color nil :style nil :size nil))

(text-obj [10 55] "Basic Objects: Point,  Line,  Rectangle,  Circle, Text & Image")
(point/point root [120 70] :style :x :size 2)
(line/line root [165 80][195 60])
(rect/rectangle root [205 80][270 60])
(circle/circle root [290  90][320 60])

(text-obj [10 110] "Point styles")
(doseq [st (range 10)]
  (point/point root [(+ 120 (* st 15)) 105] :style st :size 2))

(text-obj [10 150] "Font Style & Size")
(doseq [st (range 14)]
  (let [x (+ 140 (* st 32))
        y 150
        sz (+ 2 (* 2 st))]
    (text/text root [x y] "A" :style st :size sz)))
    

(text-obj [10 180] "Line styles")
(doseq [st (range 7)]
  (let [x0 120 
        x1 260
        y (+ 180 (* st 12))]
    (line/line root [x0 y][x1 y] :style st)))

(text-obj [280 180] "Line Widths")
(doseq [p (range 6)]
  (let [y0 170
        y1 260
        x (+ 380 (* p 15))
        w (+ 1 (* p 2))]
    (line/line root [x y0][x y1] :width w)))

(text-obj [10 280] "Color Transparency & Gradients")
(let [y0 290
      y1 (+ y0 64 )
      y2 (+ y0 15)
      y3 (+ y2 64 )]
  (rect/rectangle root [ 20 y1][125 y0] :fill true :color :red)
  (rect/rectangle root [145 y1][270 y0] :fill true :color (uc/gradient [145 y1] :green [160 (+ y1 10)] :yellow cs true))
  (rect/rectangle root [290 y1][415 y0] :fill true :color :blue)
  (rect/rectangle root [435 y1][560 y0] :fill true :color (uc/gradient [435 y1] :red [560 y0] :blue cs false)) 
  (rect/rectangle root [ 83 y2][187 y3] :fill true :color [255 255   0 128])
  (rect/rectangle root [228 y2][332 y3] :fill true :color [255   0 255 128])
  (rect/rectangle root [373 y2][477 y3] :fill true :color [  0 255 255 128]))

;; Nested groups
(text-obj [10 390] "Objects may be combined into nested groups")
(rect/rectangle root [ 40 400][560 560] :style :dash :color :gray)
(rect/rectangle root [300 420][540 540] :style :dash :color :gray)
(text/text root [ 50 550] "Group 1" :color :gray :size 6)
(text/text root [310 530] "Group 2" :color :gray :size 6)
;;    group 1
(def grp1 (grp/group root :id :group-1))
(def c1 (circle/circle   grp1 [160 500][200 460]  ))
(def r1 (rect/rectangle  grp1 [140 520][220 440]  ))

;;    group 2
(def grp2 (grp/group grp1 :id :group-2))
(def c2 (circle/circle   grp2 [340 500][400 440]))
(def r2 (rect/rectangle  grp2 [440 480][520 440]))
(def l2 (line/line       grp2 [400 530][520 490]))

(doseq [e [c2 r2 l2 c1 r1]]
  (set-attributes! e :alpha :hide :no))

(doseq [e [c2 r2 l2] ]
  (set-attributes! e :beta :hide true))

(doseq [e [c2 r2 l2 c1 r1]]
  (set-attributes! e :gamma :hide true))


(.background! drw :black)
(.render drw)

(def jb-show-all (ss/button :text "Show All" :id :alpha))
(def jb-hide-1   (ss/button :text "Hide Group 1" :id :gamma))
(def jb-hide-2   (ss/button :text "Hide Group 2" :id :beta))

(doseq [jb [jb-show-all jb-hide-1 jb-hide-2]]
  (ss/listen jb :action (fn [ev]
                          (let [src (.getSource ev)
                                id (ss/config src :id)]
                            (.use-attributes! grp1 id)
                            (.render drw)))))

(def pan-south (ss/grid-panel :rows 1
                              :items [jb-show-all jb-hide-1 jb-hide-2]))
(def pan-main (ss/border-panel :center (.canvas drw)
                               :south pan-south))


(def f (ss/frame :title "Sgwr Demo 1"
                 :content pan-main
                 :on-close :dispose
                 :size [610 :by 630]))

(ss/show! f)

;; (defn rl [](use 'sgwr.demos.demo1 :reload))
;; (defn rla [](use 'sgwr.demos.demo1 :reload-all))
;; (defn exit [](System/exit 0))
