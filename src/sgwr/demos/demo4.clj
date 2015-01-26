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
  (:require [sgwr.widgets.button :as button :reload false])
  (:require [sgwr.widgets.multistate-button :as msb :reload false])
  (:require [sgwr.widgets.radio :as radio :reload false])
  (:require [sgwr.widgets.slider :as slider :reload true])
  (:require [sgwr.widgets.dual-slider :as dual-slider :reload false])
  (:require [sgwr.widgets.field :as field :reload true])
  (:require [seesaw.core :as ss]))

(declare display)

(def drw (sgwr.elements.drawing/native-drawing 600 600))
(def root (.root drw))
(def widgets (.widget-root drw))
(text/text root [195 20] "Sgwr Demo 4 ~ Widgets" :size 8)
(line/line root [10 30][590 30]) 
(def group-text (group/group root :color [64 196 16] :size 7 :style 1))


(defn- text-obj [pos txt]
  (text/text group-text pos txt :color nil :style nil :size nil))

(text-obj [50 70] "Buttons")
(def b1 (button/icon-button widgets [ 30  80] :white :general :add :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "add"))))
(def b2 (button/icon-button widgets [ 30 130] :white :general :bank  :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "bank"))))
(def b3 (button/icon-button widgets [ 80  80] :white :general :exchange :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "exchange"))))
(def b4 (button/icon-button widgets [ 80 130] :white :general :matrix :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "matrix"))))
(def b5 (button/mini-icon-button widgets [ 30 183] :white :reset :rim-color :gray
                                       :click-action (fn [& _](display "reset"))))
(def b6 (button/mini-icon-button widgets [ 60 183] :white :up1   :rim-color :gray
                                 :click-action (fn [& _](display "UP 1"))))
(def b7 (button/mini-icon-button widgets [ 90 183] :white :up2   :rim-color :gray
                                 :click-action (fn [& _](display "UP 2"))))
(def b8 (button/mini-icon-button widgets [120 183] :white :help  :rim-color :gray
                                 :click-action (fn [& _](display "help"))))
(def b9 (button/text-button widgets [ 30 225] "Alpha"
                            :click-action (fn [& _](display "alpha"))))
(def b10 (button/text-button widgets [95  225] "Beta"
                             :click-action (fn [& _](display "beta"))))

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
                        :orientation :horizontal
                        :drag-action (fn [obj ev]
                                       (let [v (.get-property obj :value)]
                                         (display (int v))))))

(def sl2 (dual-slider/dual-slider widgets [60 330] 200 0 100 
                                  :orientation :horizontal
                                  :drag-action (fn [obj ev]
                                                 (let [v (.get-property obj :values)]
                                                   (display (format "%d %d" (int (first v))(int (second v))))))))

(text-obj [300 280] "Control 'Field'")
(def f1 (field/field widgets [300 290][400 390] [-1 1][-1 1]
                     :pad-color [16 32 64]
                     :drag-action (fn [obj ev]
                                    (let [ball @(.get-property obj :current-ball*)
                                          id (.get-property ball :id)
                                          val (.get-property ball :value)
                                          x (int (* 100 (first val)))
                                          y (int (* 100 (second val)))]
                                      (display (format "%+3d %+3d" x y))))))
                                      
(def ball1 (field/ball f1 :b1 [0.5 0.5] :color :red))
(def ball2 (field/ball f1 :b2 [0.0 0.0] :color :green))
(def sbv field/set-ball-value!)

(text-obj [20 420] "Display bars")
(def dbar2 (dbar/displaybar root 20 490 8 :sixteen :cell-width 15 :cell-height 20))
(def dbar3 (dbar/displaybar root 20 530 8 :matrix :cell-height 20 :cell-height 30))
(.colors! dbar2 [30 30 30] [255 192 192])

(defn display [text]
  (let [txt (.toUpperCase (str text))]
    (.display! dbar2 txt false)
    (.display! dbar3 txt false)
    (.render drw)))

(display "alpha...")
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

