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
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [seesaw.core :as ss]))



(def drw (drawing/native-drawing 900 700))
(def root (.root drw))
(def tools (.tool-root drw))


(def headline-1 (text/text root [50 70] "Headline-1 size 12"
                         :style :sans
                         :size 12))


(def headline-2 (text/text root [50 120] "Section head size 8"
                           :style :sans
                           :size 8))

(def label-1 (text/text root [85 420] "Label" 
                        :style :mono
                        :size 6))

(def label-2 (text/text root [132 420] "Size 6"
                        :style :mono
                        :size 6))

(def slider-length 75)

(def v-slider1 (slider/slider tools [100 400] slider-length 0.0 1.0
                              :orientation :vertical
                              :rim-color [0 0 0 0]))


(def v-slider2 (slider/slider tools [150 400] slider-length 0.0 1.0
                              :orientation :vertical
                              :rim-color [0 0 0 0]))


(def b1 (button/icon-button tools [50 500] :gray :general :help :rim-width 1))


(def b2 (button/mini-icon-button tools [150 500] :gray :help ))

(def display-type :sixteen)

;; (def dbar1 (dbar/displaybar root 300 100 5 display-type
;;                             :cell-width 25
;;                             :cell-height 35))
;; (.display! dbar1 "25.35" false)

;; (def dbar2 (dbar/displaybar root 300 150 5 display-type
;;                             :cell-width 20
;;                             :cell-height 28))
;; (.display! dbar2 "20.28")


;; (def dbar3 (dbar/displaybar root 300 200 5 display-type
;;                             :cell-width 18
;;                             :cell-height 25))
;; (.display! dbar3 "18.25")

;; (def dbar4 (dbar/displaybar root 300 250 5 display-type
;;                             :cell-width 16
;;                             :cell-height 22))
;; (.display! dbar4 "16.22")


;; (def dbar5 (dbar/displaybar root 300 300 5 display-type
;;                             :cell-width 14
;;                             :cell-height 19))
;; (.display! dbar5 "14.19")


(def dbar6 (dbar/displaybar root 100 150 7 display-type
                            :cell-width 12
                            :cell-height 16))
(.display! dbar6 "1.234567")


(def dbar7 (dbar/displaybar root 100 200 5 display-type
                            :cell-width 12
                            :cell-height 16))
(.display! dbar7 "AMP")








(def pan-main (ss/border-panel :center (.canvas drw)
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
