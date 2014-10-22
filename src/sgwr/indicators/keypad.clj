(ns sgwr.indicators.keypad
  "Defines keypad GUI component for entering numeric values
  The numeric display associated with a keypad is switchable.
  This allows a single keypad object to service multible displaybars."
  (:require [seesaw.core :as ss])
  (:require [sgwr.indicators.numberbar]))

(defprotocol NumericKeypad

  (value
    [this]
    "Returns float, the value current displaybar.
     If no displaybar is associated with thsi return 0.0")

  (value! 
    [this n]
    "Sets the value of the current displaybar.
     ignore if no displaybar is associated with this.")

  (enter-action! 
    [this fn]
    "Action function executed with enter button is pressed.")

  (displaybar! 
    [this dbar add-to-panel]
    [this dbar]
    "Associate displaybar with this
     If add-to-panel is true then dbar is added to the north panel of the 
     keypad. add-to-panel is true by default")

  (displaybar
    [this]
    "Returns the displaybar associated with this or nil")

  (widgets
    [this]
    "Returns a map of keypad components")

  (widget 
    [this key]
    "Returns a specific keypad component, the defined keys are
     :buttons - a vector of keypad buttons
     :pan-main - the main JPanel with BorderLayout
     :pan-north - The north JPanel
     :pan-south - The south JPanel")) 

(defn numeric-keypad 
  "Creates NumericKeypad
   [digit-count allow-point allow-sign]
   [allow-point allow-sign]

   digit-count - Int, optional number of digits. 
                 The keypad is not automatically associated with a displaybar.
                 This allows the same keypad to operate on any number of 
                 numeric display components. If the digit-count argument is 
                 specified then a default numeric-display is added to the 
                 keypad. See displaybar! method
   allow-point - Boolean, if true enable decimal point button
   allow-sign - Boolean, if true enable sign button"

  ([digit-count allow-point allow-sign]
     (let [numbar (sgwr.indicators.numberbar/numberbar digit-count)
           kpad (numeric-keypad allow-point allow-sign)]
       (.displaybar! kpad numbar true)
       kpad))
  ([allow-point allow-sign]
     (let [numberbar* (atom nil)
           button (fn [text value enable]
                    (let [b (ss/button :text (str text) :enabled? enable)]
                      (.putClientProperty b :value value)
                      b))
           jb-enter (button "[Ent]" :enter true)
           buttons [(button "7" 7 true)(button "8" 8 true)(button "9" 9 true)
                    (button "4" 4 true)(button "5" 5 true)(button "6" 6 true)
                    (button "1" 1 true)(button "2" 2 true)(button "3" 3 true)
                    (button "0" 0 true)(button "." :point allow-point)(button "-+" :sign allow-sign)
                    (button "<<" :back true)(button "X" :clear true) jb-enter]
           pan-center (ss/grid-panel :rows 5 :columns 3 :items buttons)
           pan-north (ss/horizontal-panel)
           pan-south (ss/horizontal-panel)
           pan-main (ss/border-panel :north pan-north
                                     :center pan-center
                                     :south pan-south)
           action (fn [ev]
                    (let [src (.getSource ev)
                          val (.getClientProperty src :value)]
                      (if @numberbar*
                        (.insert! @numberbar* val))))
           
           enter-action* (atom nil)
           
           widget-map {:buttons buttons
                       :pan-main pan-main
                       :pan-north pan-north
                       :pan-south pan-south}
           kp (reify NumericKeypad
                
                (value [this]
                  (or (and @numberbar* ((:valuefn @numberbar*))) 0.0))
                
                (value! [this n]
                  (and @numberbar* ((:setfn @numberbar*)(float n))))
                
                (enter-action! [this afn]
                  (try
                    (.removeAtcionListener @enter-action*)
                    (catch NullPointerException ex
                      nil))
                  (reset! enter-action* afn)
                  (ss/listen jb-enter :action afn))
                
                (widgets [this]
                  (assoc widget-map :displaybar @numberbar*))
                
                (widget [this key]
                  (get (.widgets this) key nil))
                
                (displaybar! [this dbar add-to-panel]
                  (reset! numberbar* dbar)
                  (if add-to-panel
                    (let [drw (.drawing dbar)
                          canvas (.drawing-canvas drw)
                          [w h](let [dim (.bounds drw)]
                                 [(.getWidth dim)(.getHeight dim)])]
                      (ss/config! pan-north :items [canvas] :size [w :by h])
                      (.validate pan-main)))
                  dbar)

                (displaybar! [this dbar]
                  (.displaybar! this dbar true))
                
                (displaybar [this]
                  @numberbar*))]
       (doseq [b buttons]
         (ss/listen b :action action))
       kp)))



(comment -------------------------------------------- DEMO
(def kp (numeric-keypad 10 true false))
(def bar1 (let [nbar (sgwr.indicators.numberbar/numberbar 5)
                dbar (:displaybar nbar)]
            (.colors! dbar :black [67 32 32] :yellow)
            nbar))

(def bar2 (let [nbar (sgwr.indicators.numberbar/numberbar 6)]
            nbar))

(def grp (ss/button-group))
(def rb1 (ss/radio :text "1" :group grp :id 1))
(def rb2 (ss/radio :text "2" :group grp :id 2))

(ss/listen rb1 :action (fn [_]
                         (.displaybar! kp bar1 false)))
(ss/listen rb2 :action (fn [_]
                         (.displaybar! kp bar2 false)))

(def pan-bars (ss/vertical-panel :items [(:drawing-canvas bar1)
                                         (:drawing-canvas bar2)
                                         rb1 rb2]))

(def f1 (ss/frame
        :content (.widget kp :pan-main)
        :on-close :dispose
        :size [400 :by 400]))

(def f2 (ss/frame
        :content pan-bars
        :on-close :dispose
        :size [400 :by 400]))

(ss/show! f1)
(ss/show! f2)
------------------------------------------------ END DEMO)
