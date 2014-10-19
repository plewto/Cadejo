(ns sgwr.indicators.shift-register
  (:import java.lang.IllegalArgumentException))

(defprotocol ShiftRegister

  (length
    [this]
    "Returns register cell-count")

  (clear!
    [this]
    "Resets register to initial condition, returns 0")

  (cells
    [this]
    "Returns register content as vector")

  (load! 
    [this cells]
    "Load register with initial values.
     cells argument should be a vector with the same length as the register.
     The overflow flag is set to true.
     Returns overflow flag")

  (shift! 
    [this n]
    "Shift register to left, inset n to right-most cell.
     Returns overflow flag")

  (backspace!
    [this]
    "Shift register to right, insert 0 to left-most cell.
     Returns current-position after shift operation")

  (block-on-overflow!
    [this flag]
    "Set overflow block mode If overflow-block-mode is true additional shift
     operations are ignored once the register is filled.")

  (overflow? 
    [this]
    "Return overflow flag. The overflow flag is true once the register has
     been filled. overflow flag is reset by either clear! or backspace!
     operation.")

  (parse! 
    [this s]
    "Shift each character of string s into register")

  (to-string 
    [this]
    "Convert register contents into string")

  ;; (dump 
  ;;   [this])
  )


(defn shift-register [cell-count]
  (let [cells* (atom (into [](repeat cell-count 0)))
        current-position* (atom 0)
        block-on-overflow* (atom false)
        overflow* (atom false)

        shift-left (fn [new]
                     (let [acc* (atom [])]
                       (doseq [i (range (dec cell-count))]
                         (swap! acc* (fn [q](conj q (nth @cells* i)))))
                       (swap! acc* (fn [q](cons new q)))
                       (reset! cells* (into [] @acc*))
                       (swap! current-position* inc)))]
    (reify ShiftRegister

      (length [this] 
        cell-count)

      (clear! [this]
        (reset! cells* (into [](repeat cell-count 0)))
        (reset! current-position* 0)
        (reset! overflow* false)
        0)

      (cells [this] @cells*)

      (load! [this cells]
        (doseq [v cells]
          (.shift! this v))
        @overflow*)

      (shift! [this n]
        (if (not (and @block-on-overflow* @overflow*))
          (do 
            (shift-left n)
            (reset! overflow* (>= @current-position* cell-count)))
          @overflow*))

      (backspace! [this]
        (swap! current-position* (fn [q](max 0 (dec q))))
        (swap! cells* (fn [q](conj (into [] (rest q))0)))
        (reset! overflow* false))
     
      (block-on-overflow! [this flag]
        (reset! block-on-overflow* flag))

      (overflow? [this] @overflow*)

      (parse! [this s]
        (.clear! this)
        (doseq [c s]
          (cond (= c \backspace)
                (.backspace! this)
                :default
                (.shift! this c))))

      (to-string [this]
        (let [sb (StringBuilder. cell-count)]
          (dotimes [p cell-count]
            (let [c (int (nth @cells* p))]
              (if (pos? c)
                (.append sb (char c)))))
          (apply str (reverse sb))))

      ;; (dump [this]
      ;;   (doseq [p (range cell-count)]
      ;;     (print (format "%4d%s " p (if (= p @current-position*) "*" " "))))
      ;;   (println)
      ;;   (dotimes [p cell-count]
      ;;     (print (format "%4s  " (nth @cells* p))))
      ;;   (println (format " overflow %s" @overflow*))
      ;;   (println (format "position       %s" @current-position*))
      ;;   (println))
              
      )))

     
   
