;; Provides simple integer shift-register
;;
(ns sgwr.util.shift-register
  (:require [clojure.math.numeric-tower :as nt]))


(defprotocol ShiftRegister

  (clear! 
    [this]
    "Set register value to 0, clear overflow flag")

  (minimum-value 
    [this]
    "Returns minimum possible value")

  (maximum-value 
    [this]
    "Returns maximum possible value")

  (overflow?
    [this]
    "Predicate returns true if register has overflowed")

  (value
    [this]
    "Return current value")

  (value!
    [this n]
    "Set current value to n
     If n exceeds capacity the overflown flag is set and the register
     value remains unchanged. Returns register value after update")

  (shift-left! 
    [this n]
    ;[this]
    "Shift register value to left (multiply by 10) and add n (default 0).
     If the register overflows the overflow flag is set and the register
     value remains unchanged. Returns register value after shift operation.")

  (shift-right!
    [this]
    "Shift register value right (divide by 10) and clear overflow flag
     Vacated digits are filled with 0, the right most digit
     drops off the register.")

  (cells 
    [this]
    "Returns register current value as vector of digits.
     Least significant digit first.")

  (dump 
    [this]) )


;; Create decimal n-digit integer shift register
;;
;; (defn shift-register-10 [digits]
;;   (let [value* (atom 0)
;;         overflow* (atom false)
;;         mask (int (Math/pow 10 digits))]
;;     (reify ShiftRegister
      
;;       (clear! [this]
;;         (reset! overflow* false)
;;         (reset! value* 0))

;;       (minimum-value [this] 
;;         0)

;;       (maximum-value [this] 
;;         (dec mask))

;;       (value [this]
;;         @value*)
    
;;       (value! [this n]
;;         (if (<= n (.maximum-value this))
;;           (reset! value* n)
;;           (reset! overflow* true))
;;         @value*)

;;       (overflow? [this]
;;         @overflow*)

;;       (shift-left! [this n]
;;         (let [v (int (+ (* @value* 10) n))]
;;           (if (<= v (.maximum-value this))
;;             (reset! value* v)
;;             (reset! overflow* true))
;;           (.value this)))

;;       ;; (shift-left! [this]
;;       ;;   (.shift-left! this 0))

;;       (shift-right! [this]
;;         (let [v2 (int (* @value* 1/10))]
;;           (reset! overflow* false)
;;           (reset! value* v2)))

;;       (cells [this]
;;         (let [acc* (atom [])
;;               v* (atom (.value this))]
;;           (dotimes [n digits]
;;             (let [d (rem @v* 10)]
;;               (swap! acc* (fn [q](conj q (int d))))
;;               (swap! v* (fn [q](int (* 1/10 q))))))
;;           @acc*)) )))

(defn shift-register-256 [digits]
  (let [value* (atom 0)
        overflow* (atom false)
        position* (atom 0)
        point-position* (atom -1)
        limit 0 ] ; (int (dec (nt/expt 256N digits)))]
        
    (reify ShiftRegister
      
      (clear! [this]
        (reset! overflow* false)
        (reset! position* 0)
        (reset! point-position* -1)
        (reset! value* 0))

      (minimum-value [this] 0)

      (maximum-value [this] limit)

      (value [this] @value*)

      (value! [this n]
        (if (<= n (.maximum-value this))
          (reset! value* n)
          (reset! overflow* true))
        @value*)

      (overflow? [this] @overflow*)

      (shift-left! [this n]
        (cond (= n (int \.))
              (do (reset! point-position* @position*))
              :default
              (let [v (int (+ (* @value* 256) (Math/abs n)))]
                (swap! position* inc)))
        (.value! this))

      (shift-right! [this]
        (let [v (int (* @value* 1/256))]
          (swap! position* dec)
          (if (< @position* @point-position*)
            (reset! point-position* -1))
          (reset! overflow* false)
          (reset! value* v)))

      (cells [this]
        (let [acc* (atom [])
              v* (atom (.value this))]
          (dotimes [n digits]
            (let [d (rem @v* 256)]
              (swap! acc* (fn [q](conj q (int d))))
              (swap! v* (fn [q](int (* q 1/256))))))
          @acc*))
      
      (dump [this]
        (doseq [p (range digits 0 -1)]
          (print (format "%4d " p)))
        (println)
          
        )
      ))
      ) 

;;; TEST TEST TEST TEST TEST 

(def sr (shift-register-256 4))

(.dump sr)                  
            
            
          
