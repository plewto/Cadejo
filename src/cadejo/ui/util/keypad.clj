(ns cadejo.ui.util.keypad
  "Provides numeric keypad component with integrated display"
  (:use [seesaw.core])
  (:require [seesaw.font]))

(def display-font (seesaw.font/font :name :monospaced
                                    :style #{:italic}
                                    :size 24))
(def max-digits 16)

(defn default-validator [tx]
  (if (zero? (count tx))
    true
    (try
      (Double/parseDouble (str tx))
      (< (count tx) max-digits)
      (catch NumberFormatException ex nil))))

(defprotocol KeypadProtocol

  (set-validator! 
    [this vfn]
    "Sets validator function.
     The function should take a single string argument and return 
     true if it is valid. The default validator checks that it's
     argument may be parsed as a Java double and that it does not
     exceed max-digits in length. Sign and decimal point characters
     are included in the length test. An empty string is a valid
     representation for 0.")

  (get-validator 
    [this]
    "Returns the validator function.")

  (set-value!
    [this tx]
    "Set display value to tx.
     The display is updated only if the validator function 
     returns true for tx.")

  (get-value
    [this]
    "Returns the current value of the display as a Java double")

  (append
    [this c]
    "Add character c to end of display. The display is updated only
     if the validator returns true for the appended text.")

  (backspace
    [this]
    "Delete the right-most display character.
     If as a result the display text becomes invalid then 
     the nothing is deleted.")

  (clear 
    [this]
    "Clear all display text.
     The display is cleared only if the validator allows 
     an empty string.")

  (widgets
    [this]
    "Returns a map of GUI widgets.")

  (widget
    [this key]
    "Returns a specific GUI widget allowing direct access to keypad 
     components. Of particular note is :pan-main which returns the 
     the primary JPanel. 
     
     :jb-back    - backspace button
     :jb-clear   - clear display button
     :jb-point   - decimal point button
     :jb-user-1  - unused button on lower left corner
     :jp-user-2  - unused button on lower left corner
     :jb-x       - (where x is a digit 0-9) the number buttons
     :pan-center - panel holding keypad
     :pan-north  - panel holding display
     :pan-main   - main outer panel
     :tx-display - JTextField used for display"))

(deftype Keypad [validator* widgets*]
  KeypadProtocol
  
  (set-validator! [this vfn]
    (swap! validator* (fn [n] vfn)))

  (get-validator [this]
    @validator*)

  (set-value! [this tx]
    (let [vfn (.get-validator this)
          stx (str tx)]
      (if (vfn stx)
        (config! (.widget this :tx-display) :text stx)
        nil)))

  (get-value [this]
    (let [tx (config (.widget this :tx-display) :text)]
      (cond (zero? (count tx)) 0
            :default (Double/parseDouble tx))))

  (append [this c]
    (let [vfn (.get-validator this)
          tx1 (config (.widget this :tx-display) :text)
          tx2 (str tx1 c)]
      (if (vfn tx2)
        (config! (.widget this :tx-display) :text tx2)
        nil)))

  (backspace [this]
    (let [vfn (.get-validator this)
          tx1 (config (.widget this :tx-display) :text)
          k (count tx1)
          tx2 (if (pos? k)
                (subs tx1 0 (dec k)))]
      (if (vfn tx2)
        (config! (.widget this :tx-display) :text tx2)
        nil)))
  
  (clear [this]
    (let [vfn (.get-validator this)
          tx ""]
      (if (vfn tx)
        (config! (.widget this :tx-display) :text tx)
        nil)))
  
  (widgets [this]
    @widgets*)

  (widget [this key]
    (get @widgets* key)))
      
(defn keypad 
  "(keypad [validator])
   (keypad)
   Create Keypad object with validator function.
   If validator not specified use default-validator"
  ([validator]
     (let [widgets* (atom {})
           validator* (atom validator)
           kp (Keypad. validator* widgets*)
           tx-display (text :text ""
                            :multi-line? false
                            :editable? false
                            :halign :right
                            :font display-font)
           number-buttons (let [acc* (atom [])]
                            (dotimes [i 10]
                              (let [jb (button :text (str i))]
                                (.putClientProperty jb :value i)
                                (swap! acc* (fn [n](conj n jb)))
                                (listen jb :action (fn [ev]
                                                     (let [src (.getSource ev)
                                                           val (.getClientProperty src :value)]
                                                       (.append kp val))))
                                (swap! widgets* (fn [n](assoc n (keyword (format "jp-%d" i)) jb)))))
                            @acc*)
           jb-sign (button :text "-/+")
           jb-point (button :text ".")
           jb-back (button :text "<--")
           jb-clear (button :text "CLR")
           jb-user-1 (button :text "")
           jb-user-2 (button :text "")
           buttons [(nth number-buttons 7)
                    (nth number-buttons 8)
                    (nth number-buttons 9)
                    jb-back
                    (nth number-buttons 4)
                    (nth number-buttons 5)
                    (nth number-buttons 6)
                    jb-clear
                    (nth number-buttons 1)
                    (nth number-buttons 2)
                    (nth number-buttons 3)
                    jb-user-1
                    jb-sign
                    (first number-buttons)
                    jb-point
                    jb-user-2]
           pan-center (grid-panel :rows 4 :columns 4 :items buttons)
           pan-north (horizontal-panel :items [tx-display])
           pan-main (border-panel :north pan-north
                                  :center pan-center)]
       (swap! widgets* (fn [n](assoc n 
                                     :tx-display tx-display
                                     :jb-sign jb-sign
                                     :jb-point jb-point
                                     :jb-back jb-back
                                     :jb-clear jb-clear
                                     :jb-user-1 jb-user-1
                                     :jb-user-2 jb-user-2
                                     :pan-north pan-north
                                     :pan-center pan-center
                                     :pan-main pan-main)))
       (listen jb-sign :action (fn [ev]
                                 (let [val (* -1 (.get-value kp))
                                       vfn (.get-validator kp)]
                                   (if (vfn (str val))
                                     (config! (.widget kp :tx-display) :text val)))))
       (listen jb-point :action (fn [ev](.append kp ".")))
       (listen jb-back :action (fn [ev](.backspace kp)))
       (listen jb-clear :action (fn [ev](.clear kp))) 
       kp))
  ([](keypad default-validator)))
                                     
           

;; Example usage
;; (def kp (keypad))
;; (def f (frame :title "Test Keypad"
;;               :content (.widget kp :pan-main)
;;               :on-close :dispose
;;               :size [300 :by 300]))
;; (show! f)
           
          
          
        
    

