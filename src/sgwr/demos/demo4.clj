(ns sgwr.demos.demo4
  (:require [sgwr.components.drawing :reload false])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.components.group :as group])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text :reload false])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.mesh :as mesh])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.button :as button :reload false])
  (:require [sgwr.tools.multistate-button :as msb :reload false])
  (:require [sgwr.tools.radio :as radio :reload false])
  (:require [sgwr.tools.slider :as slider :reload true])
  (:require [sgwr.tools.dual-slider :as dual-slider :reload true])
  (:require [sgwr.tools.field :as field :reload false])
  (:require [seesaw.core :as ss]))

(declare display)

(def drw (sgwr.components.drawing/native-drawing 600 600))
(def root (.root drw))
(def tools (.tool-root drw))
(text/text root [195 20] "Sgwr Demo 4 ~ Tools" :size 8)
(line/line root [10 30][590 30]) 
(def group-text (group/group root :color [64 196 16] :size 7 :style 1))


(defn- text-obj [pos txt]
  (text/text group-text pos txt :color nil :style nil :size nil))

(text-obj [50 70] "Buttons")
(text-obj [160 70] "Radio Buttons")
(text-obj [280 70] "Checkboxes")
(text-obj [280 180] "Toggle Buttons")
(text-obj [400 70] "Multistate Buttons")
(text-obj [90 280] "Single & Dual Sliders")
(text-obj [300 280] "Control 'Field'")
(text-obj [20 420] "Display bars")
(text-obj [420 180] "Rulers and Meshes")

(def dbar1 (dbar/displaybar root 20 440 7 :basic))
(def dbar2 (dbar/displaybar root 20 490 12 :sixteen :cell-width 15 :cell-height 20))
(def dbar3 (dbar/displaybar root 20 530 12 :matrix :cell-height 20 :cell-height 30))
(.display! dbar2 "16-SEGMENT")
(.display! dbar3 "5X7 MATRIX")
(.colors! dbar2 [30 30 30] [255 192 192])

(def ru1 (rule/ruler root [440 280] 80 :orientation :vertical
                     :pad-color [128 0 0 64]))
(rule/ticks ru1 20 :color :white :offset 4)
(rule/ticks ru1 5 :color :green :length 4 :offset -4)
(mesh/mesh root [470 290][560 190] [10 10] :color [64 64 64])
(mesh/point-field root [480 290][560 200] [10 10] :color [128 0 255 128] :size 3)
(mesh/radial-mesh root [500 380](range 20 80 10) (range 0 360 15) :ray-gap 5)

(def b1 (button/icon-button tools [ 30  80] :white :general :add :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "add"))))
(def b2 (button/icon-button tools [ 30 130] :white :general :bank  :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "bank"))))
(def b3 (button/icon-button tools [ 80  80] :white :general :exchange :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "exchange"))))
(def b4 (button/icon-button tools [ 80 130] :white :general :matrix :rim-color [0 0 0 0]
                            :click-action (fn [& _](display "matrix"))))
(def b5 (button/mini-icon-button tools [ 30 183] :white :reset :rim-color :gray
                                       :click-action (fn [& _](display "reset"))))
(def b6 (button/mini-icon-button tools [ 60 183] :white :up1   :rim-color :gray
                                 :click-action (fn [& _](display "UP 1"))))
(def b7 (button/mini-icon-button tools [ 90 183] :white :up2   :rim-color :gray
                                 :click-action (fn [& _](display "UP 2"))))
(def b8 (button/mini-icon-button tools [120 183] :white :help  :rim-color :gray
                                 :click-action (fn [& _](display "help"))))
(def b9 (button/text-button tools [ 30 225] "Alpha"
                            :click-action (fn [& _](display "alpha"))))
(def b10 (button/text-button tools [95  225] "Beta"
                             :click-action (fn [& _](display "beta"))))

(def rbl* (atom []))
(def rb1 (radio/radio-button tools [160  90] "Ape" rbl* :click-action (fn [& _](display "Ape"))))
(def rb2 (radio/radio-button tools [160 115] "Bat" rbl* :click-action (fn [& _](display "Bat"))))
(def rb3 (radio/radio-button tools [160 140] "Cat" rbl* :click-action (fn [& _](display "Cat"))))
(radio/select-radio-button! rb3)


(def cb1 (msb/checkbox tools [280  90] "Alpha" :click-action (fn [& _](display "Alpha"))))
(def cb2 (msb/checkbox tools [280 115] "Beta" :click-action (fn [& _](display "Beta"))))
(def cb3 (msb/checkbox tools [280 140] "Gamma" :click-action (fn [& _](display "Gamma"))))
(msb/select-checkbox! cb2 true)

(def tb1 (msb/text-toggle-button tools [280 200] "Dog"))
(def tb2 (msb/text-toggle-button tools [280 230] "Eel"))
(def tb3 (msb/icon-toggle-button tools [330 200] :white :wave :am :rim-color :green))

(def ms1 (msb/text-multistate-button tools 
                                     [400 80]
                                     [[:red "Red" [255 0 0]]
                                      [:yellow "Yellow" [255 255 0]]
                                      [:green "Green" [0 255 0]]
                                      [:cyan "Cyan" [0 255 255]]]))

(def ms2 (msb/icon-multistate-button tools
                                     [400 120]
                                     [[:sine :wave :sine]
                                      [:tri :wave :triangle]
                                      [:pulse :wave :pulse]
                                      [:saw :wave :sawneg]
                                      [:am :wave :am]
                                      [:fm :wave :fm]
                                      [:step :wave :step]
                                      [:noise :wave :noise]]))


(def sl1 (slider/slider tools [60 300] 200 0 100 
                        :orientation :horizontal
                        :drag-action (fn [obj ev]
                                       (let [v (.get-property obj :value)]
                                         (display (int v))))))

(def sl2 (dual-slider/dual-slider tools [60 330] 200 0 100 
                                  :orientation :horizontal
                                  :drag-action (fn [obj ev]
                                                 (let [v (.get-property obj :values)]
                                                   (display (format "%d %d" (int (first v))(int (second v))))))))

(line/line root [300 340][400 340])
(line/line root [350 290][350 390])
(def f1 (field/field tools [300 290][400 390] [-1 1][-1 1]
                     :pad-color [64 32 64 200]
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


(def tool-list [b1 b2 b3 b4 b5 b6 b7 b8 b9 b10
                  rb1 rb2 rb3 cb1 cb2 cb3
                  tb1 tb2 tb3 ms1 ms2
                  sl1 sl2 f1
                  dbar1 dbar2 dbar3])



(def cb-enable (msb/checkbox tools [400 550] "Enable Tools"
                             :click-action (fn [cb _]
                                             (let [flag (msb/checkbox-selected? cb)]
                                               (if flag
                                                 (doseq [w tool-list]
                                                   (.enable! w false))
                                                 (doseq [w tool-list]
                                                   (.disable! w false)))
                                               (.render drw)))))
(msb/select-checkbox! cb-enable true)

(defn display [text]
  (let [txt (.toUpperCase (str text))]
    (.display! dbar1 txt false)
    (.display! dbar2 txt false) 
    (.display! dbar3 txt false)
    (.render drw)))
    

(.display! dbar1  "Basic")
(.display! dbar2  "16-element")
(.display! dbar3  "DOT-MATRIX")

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

