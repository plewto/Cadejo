(ns cadejo.ui.util.keypad
  "Provides numeric keypad component with integrated display"
  (:require [seesaw.core :as ss])
  (:require [cadejo.ui.indicator.complex-display])
  )



(defprotocol KeypadProtocol

  ;; (validator! 
  ;;  [this vfn]
  ;;  "Set validation function
  ;;  vfn should take a single string argument and return boolean.")

  (validator 
   [this]
   "Return validation function")

  (value! 
   [this v]
   "Set display value to v if validator agrees.
    Return true if v is valid")

  (value
   [this]
   "Return current display value")

  (widgets
   [this]
   "Return map of components")

  (widget 
   [this key]
   "Return specific component")

  (digits
   [this]
   "Returns number of display digits")
)


(deftype Keypad [validator* value* widgetmap display]
    KeypadProtocol
    
    ;; (validator! [this vfn]
    ;;   (swap! validator* (fn [n] vfn)))

    (validator [this]
      @validator*)

    (value! [this v]
      (let [vfn (.validator this)]
        (if vfn
          (do 
            (swap! value* (fn [n] v))
            (.set-display! display (str v))
            true)
        false)))

    (value [this]
      (Double/parseDouble @value*))

    (widgets [this]
      widgetmap)

    (widget [this key]
      (get widgetmap key))

    (digits [this]
      (.char-count display)))

(defn keypad [& {:keys [digits validator]
                 :or {digits 8
                      validator (fn [n] true)
                      }}]
  (let [widgets* (atom {})
        value* (atom "")
        numeric-buttons 
        (let [acc* (atom [])]
          (dotimes [n 10]
            (let [id (keyword (format "jb-keypad-%d" n))
                  jb (ss/button :text (str n)
                                :id id)]
              (.putClientProperty jb :value n)
              (swap! acc* (fn [a](conj a jb)))
              (swap! widgets* (fn [a](assoc a id jb)))
              ))
          @acc*)
        jb-sign (ss/button :text "-/+"
                           :id (keyword "jb-keypad-sign"))
        jb-point (ss/button :text "."
                            :id (keyword "jb-keypad-point"))
        jb-clear (ss/button :text "C"
                            :id (keyword "jb-keypad-clear"))
        jb-user-1 (ss/button :text ""
                             :id :jb-keypad-user-1)
        jb-user-2 (ss/button :text ""
                             :id :jb-keypad-user-2)
        jb-user-3 (ss/button :text ""
                             :id :jb-keypad-user-3)
        center-items [(nth numeric-buttons 7)(nth numeric-buttons 8)
                      (nth numeric-buttons 9) jb-sign 
                      (nth numeric-buttons 4)(nth numeric-buttons 5)
                      (nth numeric-buttons 6) jb-user-1
                      (nth numeric-buttons 1)(nth numeric-buttons 2)
                      (nth numeric-buttons 3) jb-user-2
                      (nth numeric-buttons 0) jb-point jb-clear jb-user-3]
        pan-center (ss/grid-panel :rows 4 :columns 4 :items center-items)
        display (cadejo.ui.indicator.complex-display/matrix-display-bar :count digits)
        pan-north (ss/horizontal-panel :items [(.lamp-canvas display)])
        pan-main (ss/border-panel :north pan-north
                                  :center pan-center)]
    (swap! widgets* (fn [n](merge n {:jb-keypad-sign jb-sign
                                     :jb-keypad-point jb-point
                                     :jb-keypad-clear jb-clear
                                     :jb-keypad-user-1 jb-user-1
                                     :jb-keypad-user-2 jb-user-2
                                     :jb-keypad-user-3 jb-user-3
                                     :pan-north pan-north
                                     :pan-center pan-center
                                     :pan-main pan-main
                                     :display display})))
    (doseq [jb numeric-buttons]
      (ss/listen jb :action (fn [ev]
                              (let [src (.getSource ev)
                                    digit (.getClientProperty src :value)
                                    old-value @value*
                                    new-value (str old-value digit)]
                                (if (validator new-value)
                                  (do 
                                    (.set-display! display (str new-value))
                                    (swap! value* (fn [n] new-value))))))))
    (ss/listen jb-sign :action (fn [n]
                                 (let [old-value 
                                       (try 
                                         (Double/parseDouble @value*)
                                         (catch NumberFormatException ex 0))
                                       new-value (str (* -1 old-value))]
                                   (if (validator new-value)
                                     (do 
                                       (.set-display! display new-value)
                                       (swap! value* (fn [n] new-value)))))))
    (ss/listen jb-point :action (fn [n]
                                  (let [old-value
                                        (try
                                          (str (Double/parseDouble @value*))
                                          (catch NumberFormatException ex "0.0"))
                                        dp-pos (.indexOf old-value ".")
                                        int-part (subs old-value 0 dp-pos)
                                        new-value (str int-part ".")]
                                    (if (validator new-value)
                                      (do
                                        (.set-display! display new-value)
                                        (swap! value* (fn [n] new-value)))))))
    (ss/listen jb-clear :action (fn [n]
                                  (swap! value* (fn [n] ""))
                                  (.clear-display! display)))
                                        
    
    (.on! display)
    (Keypad. (atom validator)
             value*
             @widgets*
             display))) 

