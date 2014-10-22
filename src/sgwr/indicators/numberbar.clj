(ns sgwr.indicators.numberbar
  "Provides numeric display bar"
  (:require [sgwr.indicators.complex-display :as display])
  (:require [sgwr.indicators.displaybar])
  (:require [sgwr.drawing])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.indicators.shift-register])
  (:import java.lang.Long
           java.lang.Float))



; ---------------------------------------------------------------------- 
;                            Numeric Display Bar
;
; Similar to DisplayBar but specifically for numbers.
; 

(def ^:private cell-width 25)
(def ^:private cell-height 35)
(def ^:private border 6)
(def ^:private gap 4)

(def ^:private digits [0 1 2 3 4 5 6 7 8 9
                       \0 \1 \2 \3 \4 \5 \6 \7 \8 \9
                       "0" "1" "2" "3" "4" "5" "6" "7" "8" "9"
                       '0 '1 '2 '3 '4 '5 '6 '7 '8 '9
                       :0 :1 :2 :3 :4 :5 :6 :7 :8 :9])

(defn- digit? [obj] 
  (some (fn [n](= n obj)) digits))


(defn numberbar 

  ([digit-count]
     (let [mx (dec (int (Math/pow 10 digit-count)))
           mn (* -1 mx)]
       (numberbar digit-count mn mx)))

  ([digit-count min-val max-val] 
     (let [drawing-width (+ (* 2 border)(* (+ 1 digit-count) (+ gap cell-width)))
           drawing-height (+ (* 2 border) cell-height)
           value* (atom "") ;; string representation of absolute value
           negative* (atom false) ;; flag true if value is negative
           has-point* (atom false) ;; flag true if decimal point is present
           drw (let [d (sgwr.drawing/native-drawing drawing-width drawing-height)]
                 (.suspend-render! d true)
                 d)
           background* (atom (uc/color :black))
           inactive* (atom (uc/color [38 10 38]))
           active* (atom (uc/color :red))
           filter* (atom (fn [n](and (>= n min-val)(<= n max-val))))
           off* (atom nil)
           sign-cell (display/sign-display drw border border cell-width cell-height)
           cells (let [acc* (atom [])
                       y border]
                   (.paper! drw @background*)
                   (.color! drw @inactive*)
                   (.style! drw 0)
                   (.width! drw 1)
                   (doseq [i (range digit-count)]
                     (let [x (+ border (* (inc i) (+ gap cell-width)))
                           e (display/dot-matrix-display drw x y)]
                       (.colors! e @inactive* @active*)
                       (swap! acc* (fn [q](conj q e)))))
                   (.suspend-render! drw false)
                   (into [] (reverse @acc*)))

           all-off (fn []
                     (doseq [c cells]
                       (.set-character! c \space))
                     (.set-character! sign-cell \space))
          
           sync-display (fn []
                          (all-off)
                          (if @negative*
                            (.set-character! sign-cell \-)
                            (.set-character! sign-cell \+))
                          (if (not @off*)
                            (let [pos* (atom 0)]
                              (doseq [c (reverse @value*)]
                                (let [d (nth cells @pos*)]
                                  (.set-character! d c)
                                  (swap! pos* inc)))
                              (.render drw))))
                            


           dbar (reify sgwr.indicators.displaybar/Displaybar
                  
                  
                  (colors! [this background inactive active]
                    (reset! background* (uc/color background))
                    (reset! inactive* (uc/color inactive))
                    (reset! active* (uc/color active))
                    (doseq [c cells]
                      (.colors! c @inactive* @active*))
                    [@background* @inactive* @active*])
                  
                  (colors [this]
                    [@background* @inactive* @active*])

                  (cell [this n]
                    (nth cells n))
                  
                  (cell-count [this]
                    digit-count) 
                  
                  (shift-register [this] nil)

                  (overflow? [this] 
                    (>= (+ (if @has-point* 1 0)
                           (count @value*))
                        digit-count))

                  (filter! [this ffn]
                    (reset! filter* ffn))

                  (clear! [this]
                    (reset! value* "")
                    (reset! negative* false)
                    (reset! has-point* false)
                    (sync-display)
                    (.value this))

                  (insert! [this c render]
                    (if (not @off*)
                      (do 
                        (cond 
                         ; swap sign 
                         (= c :sign)
                         (let [s (if @negative* 1 -1)
                               v (Math/abs (.value this))]
                           (if (@filter* (* s v))
                             (reset! negative* (neg? s))))
                             
                         ; set to negative
                         (or (= c \-)(= c "-"))    ; set to negative
                         (let [v (* -1 (.value this))] ; (Float/valueOf @value*))]
                           (if (@filter* v)
                             (reset! negative* true)))
                           
                         ; set to positive
                         (or (= c \+)(= c "+"))
                         (let [v (Float/valueOf @value*)]
                           (if (@filter* v)
                             (reset! negative* false)))

                         ; insert point
                         (and (or (= c :point)(= c \.)(= c "."))
                              (not (.overflow? this)))
                         (if (not @has-point*)
                           (do (reset! value* (str @value* \.))
                               (reset! has-point* true)))
                         
                         ; backspace
                         (= c :back)
                         (if (pos? (count @value*))
                           (let [vcount (count @value*)
                                 restore @value*]
                             (reset! value* (subs @value* 0 (dec vcount)))
                             (let [v (* (if @negative* -1 1)
                                        (if (pos? (count @value*))
                                          (Float/valueOf @value*)
                                          0))]
                               (if (not (@filter* v))
                                 (reset! value* restore))
                               (reset! has-point* (not (neg? (.indexOf @value* ".")))))))
                         
                         ; clear
                         (= c :clear)
                         (.clear! this)
                              
                         ; insert digit
                         (digit? c)
                         (if (not (.overflow? this))
                           (let [v (str @value* c)
                                 s (if @negative* -1 1)]
                             (if (@filter* (* s (Float/valueOf v)))
                               (reset! value* v))))
                         
                         :default
                         (println (format "numberpad.insert! '%s' ???" c)) )
                        (if render (sync-display))))
                    (.value this))
                          
                  (insert! [this c]
                    (.insert! this c true))
    
                  (backspace! [this]
                    (.insert this :back))
                  
                  (display! [this n]
                      (.clear! this)
                      (doseq [c (str n)]
                        (.insert! this c true))
                      (.value this))

                    (load! [this data delay]
                      (.clear! this)
                      (doseq [c data]
                        (.insert! this c true)
                        (Thread/sleep delay))
                      (.value this))
                    
                    (load! [this data]
                      (.clear! this)
                      (doseq [c data]
                        (.insert! this c false))
                      (sync-display)
                      (.value this))

                    (pad! [this c delay] ;; ignore
                      (.value this))
                    
                    (pad! [this c]
                      (.value this))

                    (pad! [this]
                      (.value this))

                    (to-string [this]
                      (str (if @negative* "-" "")
                           @value*))

                    (value [this]
                      (if (pos? (count @value*))
                        (* (if @negative* -1 1)
                           (Float/valueOf @value*))
                        0.0))
                    
                    (blink [this n period]
                      (dotimes [i n]
                        (.blink this period)
                        (Thread/sleep period)))
                    
                    (blink [this period]
                      (if (not @off*)
                        (doseq [c (conj cells sign-cell)]
                          (.set-character! c \space)))
                      (.render drw)
                      (Thread/sleep period)
                      (sync-display))
                    
                    (off! [this flag c]
                      (if flag
                        (do
                          (reset! off* true)
                          (doseq [q cells]
                            (.set-character! q c))
                          (.render drw))
                        (do
                          (reset! off* false)
                          (sync-display))))
                    
                    (off! [this flag]
                      (.off! this flag 292)) 

                    (drawing [this] drw)

                    (drawing-canvas [this]
                      (.drawing-canvas drw)))]
       dbar)))
