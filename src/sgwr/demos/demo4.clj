(println "--> demos4")
(ns sgwr.demos.demo4
  (:require [sgwr.elements.drawing :reload false])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text :reload false])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.widgets.button :as button :reload true])
  (:require [sgwr.widgets.multistate-button :as msb :reload true])
  (:require [sgwr.widgets.radio :as radio :reload true])
  (:require [sgwr.widgets.slider :as slider :reload true])
  (:require [sgwr.widgets.dual-slider :as dual-slider :reload true])
  (:require [seesaw.core :as ss])
)
(def drw (sgwr.elements.drawing/native-drawing 600 600))
(def root (.root drw))
(def widgets (.widget-root drw))
(text/text root [195 20] "Sgwr Demo 4 ~ Widgets" :size 8)
(line/line root [10 30][590 30]) 
(def group-text (group/group root :color [64 196 16] :size 7 :style 1))

(defn text-obj [pos txt]
  (text/text group-text pos txt :color nil :style nil :size nil))

(text-obj [50 70] "Buttons")
(def b1 (button/icon-button widgets [ 30  80] :white :general :add :rim-color [0 0 0 0]))
(def b2 (button/icon-button widgets [ 30 130] :white :general :bank  :rim-color [0 0 0 0]))
(def b3 (button/icon-button widgets [ 80  80] :white :general :exchange :rim-color [0 0 0 0]))
(def b4 (button/icon-button widgets [ 80 130] :white :general :matrix :rim-color [0 0 0 0]))

(def b5 (button/mini-icon-button widgets [ 30 183] :white :reset :rim-color :gray))
(def b6 (button/mini-icon-button widgets [ 60 183] :white :up1   :rim-color :gray))
(def b7 (button/mini-icon-button widgets [ 90 183] :white :up2   :rim-color :gray))
(def b8 (button/mini-icon-button widgets [120 183] :white :help  :rim-color :gray))
(def b9 (button/text-button widgets [ 30 225] "Alpha"))
(def b10 (button/text-button widgets [95  225] "Beta"))

(text-obj [160 70] "Radio Buttons")
(def rbl* (atom []))
(def rb1 (radio/radio-button widgets [160  90] "Ape" rbl*))
(def rb2 (radio/radio-button widgets [160 115] "Bat" rbl*))
(def rb3 (radio/radio-button widgets [160 140] "Cat" rbl*))
(radio/select-radio-button! rb3)


(text-obj [280 70] "Checkboxes")
(def cb1 (msb/checkbox widgets [280  90] "Alpha"))
(def cb2 (msb/checkbox widgets [280 115] "Beta"))
(def cb3 (msb/checkbox widgets [280 140] "Gamma"))
(msb/select-checkbox! cb2 true)


(text-obj [280 180] "Toggle Buttons")
(def tb1 (msb/text-toggle-button widgets [280 200] "Dog"))
(def tb2 (msb/text-toggle-button widgets [280 230] "Eel"))
(def tb3 (msb/icon-toggle-button widgets [330 200] :white :wave :am :rim-color :green))

(text-obj [400 70] "Multistate Buttons")
(def ms1 (msb/text-multistate-button widgets 
                                     [400 80]
                                     [[:red "Red" [255 0 0]]
                                      [:yellow "Yellow" [255 255 0]]
                                      [:green "Green" [0 255 0]]
                                      [:cyan "Cyan" [0 255 255]]]))

(def ms2 (msb/icon-multistate-button widgets
                                     [400 120]
                                     [[:sine :wave :sine]
                                      [:tri :wave :triangle]
                                      [:pulse :wave :pulse]
                                      [:saw :wave :sawneg]
                                      [:am :wave :am]
                                      [:fm :wave :fm]
                                      [:step :wave :step]
                                      [:noise :wave :noise]]))


(text-obj [90 280] "Single & Dual Sliders")     
(def sl1 (slider/slider widgets [60 300] 200 0 100 
                        :orientation :horizontal))

(def sl2 (dual-slider/dual-slider widgets [60 330] 200 0 100 
                                  :orientation :horizontal))


(.background! drw [0 0 0])
(.render drw)
(def pan-main (ss/border-panel
               :center (.canvas drw)
               ))
(def f (ss/frame :title "Sgwr Demo 4"
                 :content pan-main
                 :on-close :dispose
                 :size [650 :by 650]))
(ss/show! f)

(defn rl [](use 'sgwr.demos.demo4 :reload))
(defn rla [](use 'sgwr.demos.demo4 :reload-all))
(defn exit [](System/exit 0))

