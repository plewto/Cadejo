(ns cadejo.ui.util.validated-text-field
  "Provides JTextField with validation.
   The background color changes to indicate valid content."
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
  (:require [cadejo.ui.util.factory :as factory])
  (:import javax.swing.event.CaretListener
           javax.swing.JTextField))

(def normal-background nil)
(def warning-background (ssc/color :pink))

(defprotocol ValidatedTextFieldProtocol

  (widget-map
    [this]
    "Returns map of widgets")

  (widget 
    [this key]
    "Returns specific widget, only two keys are supported
     :text-field an instance of JTextField 
     :pan-main an instance of JPanel containing the text field")

  (suspend-listeners
    [this flag]
    "Sets flag indicating that listeners on the text field should 
     be suspended.")
  

  (listeners-suspended? 
    [this]
    "Returns true if text filed listeners are currently suspended")

  (is-valid?
    [this value]
    [this]
    "Predicate returns true if validator function returns true for value.
     If value not provided the current content of the text filed is used.")

  (set-value 
    [this value suspend]
    [this value]
    "Sets value of the text filed 
     If the validator returns true for value the text field contents are 
     updated and the string version of value returned.
     If the validator returns false for value the text field is not 
     updated and nil is returned.

     The optional suspend argument temporarily sets the listener-suspend
     flag. The previous state of the flag is restored prior to set-value
     returning.")

  (get-value
    [this]
    "Returns the current value.
     Prior to returning the current value is processed by the post function.
     Returns nil if the current value is invalid.")
)


(deftype ValidatedTextField [widgets
                             validator
                             postfn
                             suspend*]

  ValidatedTextFieldProtocol

  (widget-map [this]
    widgets)

  (widget [this key]
    (or (get widgets key)
        (umsg/warning (format "ValidatedTextField does not have %s widget" key))))
  
  (suspend-listeners [this flag]
    (swap! suspend* (fn [n] flag)))

  (listeners-suspended? [this]
    @suspend*)

  (is-valid? [this value]
    (validator value))

  (is-valid? [this]
    (.is-valid? this (ss/config (.widget this :text-field) :text)))

  (set-value [this value suspend]
    (let [temp @suspend*]
      (if (.is-valid? this value)
        (let [sval (str value)] 
          (suspend-listeners this suspend)
          (ss/config! (.widget this :text-field) :text sval)
          (suspend-listeners this temp)
          sval)
        false)))
  
  (set-value [this value]
    (.set-value this value true))


  (get-value [this]
    (postfn (ss/config (.widget this :text-field) :text))))
    
          
(defn validated-text-field [& {:keys [validator post value border]
                               :or {validator (constantly true)
                                    post identity
                                    value ""
                                    border nil}}]
  "Creates ValidatedTextField object
   :validator - Function, the validation function should take a single
                string argument and return true if it is valid and
                false otherwise. Defaults to constantly true
   :post      - Function, the post function is called by the get-value 
                method and is applied to the text-field value prior
                to returning. The function should take a single string 
                argument. If the current JTextField contents are invalid
                the post function should return nil. Defaults to identity 
   :value     - The initial value
   :border    - Sets the border of the panel holding the JTextField
                If border is a string a compound title and empty border is
                used. If border is an integer an empty border of 
                thickness is used. If border is nil a default empty border
                is used."
  (let [text-field (ss/text :text (str value) :multi-line? false)
        pan (ss/vertical-panel :items [text-field]
                               :border (cond (string? border)
                                             (factory/title border)
                                             (number? border)
                                             (factory/padding (int border))
                                             :default
                                             (factory/padding)))
        widgets {:text-field text-field
                 :pan-main pan}
        vtf (ValidatedTextField. widgets
                                 validator
                                 post
                                 (atom false))]
    (.addCaretListener text-field
                       (proxy [CaretListener][]
                         (caretUpdate [ev]
                           (let [tx (ss/config text-field :text)]
                             (.setBackground text-field
                                             (if (validator tx)
                                               normal-background
                                               warning-background))))))
    (.setBackground text-field 
                    (if (validator value)
                      normal-background
                      warning-background))

    vtf))
                  

(defn numeric-text-field [& {:keys [min max value post border]
                            :or {min Double/NEGATIVE_INFINITY
                                 max Double/POSITIVE_INFINITY
                                 value 0.0
                                 post nil
                                 border nil}}]
  "Creates a ValidatedTextField for real numbers.
   See validated-text-field
   :min    - float, the minimum valid value, defaults -infinity
   :max    - float, the maximum valid value, defaults +infinity
   :value  - float, the initial value, defaults 0
   :post   - Function the post function. The default is to use
             Double/parseDouble
   :border - "
  (let [vfn (fn [n] 
              (try
                (let [v (Double/parseDouble (str n))]
                  (and (>= v min)(<= v max)))
                (catch NumberFormatException ex false)))
        postfn (or post (fn [s]
                          (try
                            (Double/parseDouble s)
                            (catch NumberFormatException ex nil))))
        vtf (validated-text-field :validator vfn
                                  :post postfn
                                  :value value
                                  :border border)]
    (.setHorizontalAlignment (.widget vtf :text-field) JTextField/RIGHT)
    vtf))
        

(defn integer-text-field [& {:keys [min max value post border]
                             :or {min Integer/MIN_VALUE
                                  max Integer/MAX_VALUE
                                  value 0
                                  post nil
                                  border nil}}]
  "Creates ValidatedTextField for integer values
  See validated-text-field
   :min    - int, the minimum valid value, defaults Integer/MIN_VALUE
   :max    - int, the maximum valid value, defaults Integer/MAX_VALUE
   :value  - int, the initial value, defaults 0
   :post   - Function the post function. The default is to use
             Integer/parseInt
   :border - "
  (let [vfn (fn [n]
              (try
                (let [v (Integer/parseInt (str n))]
                      (and (>= v min)(<= v max)))
                (catch NumberFormatException ex false)))
        postfn (or post (fn [s]
                          (try 
                            (Integer/parseInt s)
                            (catch NumberFormatException ex nil))))
        vtf (validated-text-field :validator vfn
                                  :post postfn
                                  :value value
                                  :border border)]
    (.setHorizontalAlignment (.widget vtf :text-field) JTextField/RIGHT)
    vtf))
