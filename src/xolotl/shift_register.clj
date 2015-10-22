(ns xolotl.shift-register
  (:require [xolotl.util]))


(def default-length 12)

(def expt xolotl.util/expt)

(defprotocol ShiftRegister

  (length [this]
    "(.length ShiftRegister)
     RETURNS: int, number of register stages.")

  (value [this]
    "(.value ShiftRegister)
     RETURNS: int, the current register value.")

  (midi-reset [this]
    "(.midi-reset ShiftRegister)
     Set register value to initial seed value.
     RETURNS: int, the seed value.")

  (taps! [this n]
    "(.taps! ShiftRegister fbt)
     Sets register feedback taps
     ARGS:
       fbt - int, the taps value.  Stages enabled for feedback if the 
             corresponding bit in fbt is set.
     RETURNS: int, the tap value.")

  (taps [this]
    "(.taps ShiftRegister)
     RETURNS: int, the feedback taps value.")

  (seed! [this s]
    "(.seed ShiftRegister s)
     Sets initial register seed. On midi-reset the register is set to
     the seed value.
     ARGS:
       s - int, the initial seed.
     RETURNS: int, the seed value.")

  (seed [this]
    "(.seed ShiftRegister)
     RETURNS: int, the seed value.")

  (mask! [this m]
    "(.mask! ShiftRegister n)
     Sets output masking value.  The output value of the register is bitwise 
     and with the mask. One application is to simulate a register which is
     shorter then the actual length. Note the mask is not applied to feedback.
     ARGS:
       n - int, the mask value
     RETURNS: int n")
  
  (mask [this]
    "(.mask ShiftRegister)
     RETURNS: int, the output masking value")
  
  (stage [this n]
    "(.stage ShiftRegister n)
     Retrieves the state of a register stage.
     ARGS: 
       n - int, the stage number.
     RETURNS: Two element list (s t) where s and t are binary 0 or 1.
              s is 1 if stage n is set, 0 if stage n is clear.
              t is 1 if stage n feedback is enabled.")

  (stages [this]
    "(.stages ShiftRegister)
     RETURNS: list, the current state of the register. The result has the 
              form (s t m) where s, t and m are vectors with lengths equal
              to the register's length and contain binary values 0 or 1. 
              For s 0 indicates the corresponding stage is clear and 1
              indicates the stage is set. The t vector indicates which 
              stages have feedback enabled. The m vector is for the 
              output-mask")

  (feedback [this inject]
    "(.feedback ShiftRegister inject)
     Calculates feedback value.
     ARGS:
       inject - int, either 0 or 1, indicates what value is to be added
                to the feedback value.
     RETURNS: int, either 0 or 1, the value to be shifted into the register.
              Feedback is calculated by taking the sum s of all stages 
              where feedback is enabled, adding the inject value, and then
              taking the parity of s. If s is odd feedback is 1, if s is
              even feedback is 0.")

  (shift [this inject]
    "(.shift ShiftRegister inject)
     Shift register stages right. 
     ARGS:
       inject - int, either 0 or 1, the value to be added to feedback
                count. 
     RETURNS: - int, the value of the register after shifting contents
                right and adding in feedback.")

  (dump [this]
    "(.dump ShiftRegister)
     Print diagnostics of current register state."))


(defn shift-register
  "(shift-register)
   (shift-register n seed)
   
   Creates new shift register.
   ARGS:
     n    - optional int, register length, default 12.
     seed - optional int, initial seed,default 1.
   RETURNS: an instance of ShiftRegister"  
  ([]
   (shift-register default-length (fn [_]) 1))
  ([n seed]
   (let [limit-mask (- (expt 2 n) 1)
         output-mask* (atom limit-mask)
         seed* (atom (bit-and limit-mask seed))
         value* (atom @seed*)
         taps* (atom (expt 2 (dec n)))]

     (reify ShiftRegister
       
       (length [this] n)
       
       (value [this]
         (bit-and @output-mask* @value*))
       
       (midi-reset [this]
         (reset! value* @seed*))
       
       (taps! [this n]
         (reset! taps* (bit-and limit-mask n)))
       
       (taps [this] @taps*)
       
       (seed! [this n]
         (reset! seed* (bit-and limit-mask n)))

       (seed [this] @seed*)
       
       (mask! [this n]
         (reset! output-mask* (bit-and limit-mask n)))

       (mask [this] @output-mask*)
       
       (stage [this i]
         (let [j (expt 2 i)
               v (if (pos? (bit-and @value* j)) 1 0)
               t (if (pos? (bit-and @taps* j)) 1 0)]
           (list v t)))
       
       (stages [this]
         (let [s* (atom [])
               t* (atom [])
               m* (atom [])]
           (dotimes [i n]
             (let [j (expt 2 i)
                   v (if (pos? (bit-and @value* j)) 1 0)
                   t (if (pos? (bit-and @taps* j)) 1 0)
                   m (if (pos? (bit-and @output-mask* j)) 1 0)]
               (swap! s* (fn [q](conj q v)))
               (swap! m* (fn [q](conj q m)))
               (swap! t* (fn [q](conj q t)))))
          (list @s* @t* @m*)))
       
       (feedback [this inject]
         (let [fb* (atom (bit-and inject 0x1))]
           (dotimes [i n]
             (let [j (expt 2 i)
                   s (bit-and @value* j)
                   t (bit-and @taps* j)]
               (if (and (pos? s)(pos? t))
                 (swap! fb* inc))))
           (rem @fb* 2)))
       
       (shift [this inject]
         (let [fb (.feedback this inject)]
           (swap! value* (fn [q](bit-and limit-mask (+ (* 2 q) fb))))
           @value*))
       
       (dump [this]
         (let [state (.stages this)
               a (StringBuilder. "")
               b (StringBuilder. "")
               c (StringBuilder. "")
               cells (first state)
               taps (second state)
               mask (nth state 2)]
           (dotimes [i n]
             (let [s (nth cells i)
                   t (nth taps i)
                   m (nth mask i)]
               (.append a (format "%01d" s))
               (.append b (format "%01d" t))
               (.append c (format "%01d" m))))
           (println "ShiftRegister")
           (printf "\tStages: %s\n" (.toString a))
           (printf "\tTaps  : %s\n" (.toString b))
           (printf "\tMask  : %s\n" (.toString c))
           (println (format "\tValue : %d" (.value this))))) ))))


       
    
