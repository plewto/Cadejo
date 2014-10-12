;; Provides simple integer shift-register
;;
(ns sgwr.util.shift-register)


(defprotocol ShiftRegister

  (clear! 
    [this]
    "Set register value to 0")

  (minimum-value 
    [this]
    "Returns minimum possible value")

  (maximum-value 
    [this]
    "Returns maximum possible value")

  (value
    [this]
    "Return current value")

  (value!
    [this n]
    "Set current value to n
     If n exceeds register capacity it is wrapped to maximum value.")

  (shift-left! 
    [this n]
    [this]
    "Shift register value to left (multiply by 10) and add n (default 0).
     The left most digit drops off the register.")

  (shift-right!
    [this]
    "Shift register value right (divide by 10)
     Vacated digits are filled with 0, the right most digit
     drops off the register.")

;; Create n-digit integer shift register
;;
(defn shift-register [digits]
  (let [value* (atom 0)
        mask (int (Math/pow 10 digits))]
    (reify ShiftRegister
      
      (clear! [this]
        (reset! value* 0))

      (minimum-value [this] 
        0)

      (maximum-value [this] 
        (dec mask))

      (value [this]
        @value*)

      (value! [this n]
        (let [v2 (rem n (dec mask))]
          (reset! value* v2)))

      (shift-left! [this n]
        (let [v2 (int (+ (rem (* @value* 10) mask) n))]
          (reset! value* v2)))

      (shift-left! [this]
        (.shift-left! this 0))

      (shift-right! [this]
        (let [v2 (int (* @value* 1/10))]
          (reset! value* v2))))))

