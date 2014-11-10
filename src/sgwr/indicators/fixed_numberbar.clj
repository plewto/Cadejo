(ns sgwr.indicators.fixed-numberbar
  "Defines GUI component for fixed-point numeric display
where each digit has integral increment and decrement buttons."
  (:require [sgwr.drawing])
  (:require [sgwr.indicators.complex-display])
  (:require [sgwr.indicators.displaybar])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [seesaw.core :as ss])
  (import java.awt.event.MouseMotionListener
          java.awt.event.MouseListener))

(def segment-length 18)
(def segment-height 18)
(def pad 4)
(def display-width (+ segment-length (* 2 pad)))
(def button-width 8)
(def button-height 12)
(def button-gap 4)
(def display-height (* 2 (+ segment-height button-height 
                            pad button-gap)))
(def horizontal-digit-offset (+ (* 2 pad) segment-length))

;; Returns true iff point [x y] is in proximity to circle e
;;
(defn- in-bounds? [e x y]
  (let [[pc pr](.construction-points e)
        d (math/distance pc [x y])
        radius 8]
    (<= d radius)))

(defn digit-character [drw value x-offset y-offset]

  "Place 7-segment style numeric display with integrated increment and 
   decrement buttons on drawing. 

   drw - the drawing
   value - int, weight of this digit, value should be power of 10
   x-offset, y-offset - digit position relative to upper-left corner of 
   drawing

   Returns map with following keys

   :button-increment - sgwr element used to increment value
   :button-decrement - sgwr element to decrement value
   :offfn - function (fn) to set all display element to 'off'
   :setfn - function (fn n) sets display to digit n
   :valuefn - function (fn) which returns current value.
   :colors - function (inactive active button) used to set display 
             active and inactive colors and butt colors."
  (.style! drw 0)
  (.width! drw 1)
  (.fill! drw false)
  (let [x0 (+ x-offset pad)
        x5 (+ x0 segment-length)
        x3 (* 1/2 (+ x0 x5))            ; center
        x1 x0 ; (- x3 (* 1/2 button-width))
        x4 x5 ; (+ x1 button-width)
        y0 (+ y-offset pad)
        y1 (+ y0 segment-height)
        y2 (+ y1 segment-height)
        y3 (+ y2 button-gap)
        y4 (+ y3 button-height)
        y5 (+ y4 button-gap)
        y6 (+ y5 button-height)
        inactive* (atom (uc/color [64 32 64]))
        active* (atom (uc/color :red))
        value* (atom 0)
        a (.line! drw [x0 y0][x5 y0])
        b (.line! drw [x5 y1])
        c (.line! drw [x5 y2])
        d (.line! drw [x0 y2])
        e (.line! drw [x0 y1])
        f (.line! drw [x0 y0])
        g (.line! drw [x0 y1][x5 y1])
        segments [a b c d e f g]
        char-map {\space []
                  0 [a b c d e f]
                  1 [b c]
                  2 [a b g e d]
                  3 [a b c d g]
                  4 [f g b c]
                  5 [a f g c d]
                  6 [a f g e d c]
                  7 [a b c]
                  8 [a b c d e f g]
                  9 [a b g f c d]
                  \0 [a b c d e f]
                  \1 [b c]
                  \2 [a b g e d]
                  \3 [a b c d g]
                  \4 [f g b c]
                  \5 [a f g c d]
                  \6 [a f g e d c]
                  \7 [a b c]
                  \8 [a b c d e f g]
                  \9 [a b g f c d]}
        all-off (fn []
                  (doseq [s segments]
                    (.color! (.attributes s) @inactive*)))
        setfn (fn [n]
                (all-off)
                (doseq [e (get char-map n [])]
                  (.color! (.attributes e) @active*))
                (reset! value* n))

        increment (fn []
                    (let [v (rem (inc @value*) 10)]
                      (setfn v)))
        decrement (fn []
                    (let [v (if (zero? @value*) 9 (dec @value*))]
                      (setfn v)))
        ;; buttons
        b-inc (let [b  (.two-point-circle! drw [x3 (* 1/2 (+ y3 y4))][x3 y3])]
                (.put-property! (.attributes b) :type :button)
                (.put-property! (.attributes b) :value value)
                (.put-property! (.attributes b) :action increment)
                b)
        b-dec (let [b (.two-point-circle! drw [x3 (* 1/2 (+ y5 y6))][x3 y5])]
                (.put-property! (.attributes b) :type :button)
                (.put-property! (.attributes b) :value (* -1 value))
                (.put-property! (.attributes b) :action decrement)
                b)
        plus-sign (do (.style! drw 7)
                      (.point! drw [x3 (* 1/2 (+ y3 y4))]))
        minus-sign (do (.style! drw 2)
                       (.point! drw [x3 (* 1/2 (+ y5 y6))]))
        valuefn (fn []
                  (* value @value*))]
    {:button-increment b-inc
     :button-decrement b-dec
     :offfn all-off
     :setfn setfn
     :valuefn valuefn
     :colors (fn [inactive active button-color]
               (reset! inactive* (uc/color inactive))
               (reset! active* (uc/color active))
               (doseq [b [b-inc b-dec plus-sign minus-sign]]
                 (.color! (.attributes b) (uc/color button-color)))
               (setfn @value*)
               (.render drw))}))

(defn sign-character  [drw x-offset y-offset]
  "Place sign character with integrated buttons on drawing.
  Returns map, see digit-character"
  (.width! drw 1)
  (.fill! drw false)
  (let [x0 (+ 4 x-offset pad)
        x5 (+ -4 x0 segment-length)
        x3 (* 1/2 (+ x0 x5))            ; center
        y0 (+ 4 y-offset pad)
        y1 (+ y0 segment-height)
        y2 (+ -4 y1 segment-height)
        y3 (+ y2 button-gap)
        y4 (+ y3 button-height)
        y5 (+ y4 button-gap)
        y6 (+ y5 button-height)
        inactive* (atom (uc/color [64 32 64]))
        active* (atom (uc/color :red))
        value* (atom 1)
        a (.line! drw [x3 y0][x3 y2])
        b (.line! drw [x0 y1][x5 y1])
        segments [a b]
        char-map {\space []
                  \- [b]
                  \+ []}
        all-off (fn []
                  (doseq [s segments]
                    (.color! (.attributes s) @inactive*)))
        setfn (fn [n]
                (let [[s key] (cond (neg? n) [-1 \-]
                                    (pos? n) [1 \+]
                                    :default [1 \space])]
                  (reset! value* s)
                  (all-off)
                  (doseq [e (get char-map key)]
                    (.color! (.attributes e) @active*))))
        decrement (fn [](setfn (* -1 @value*)))
        increment (fn [](setfn (* -1 @value*)))
        ;; buttons
        b-inc (let [b  (.two-point-circle! drw [x3 (* 1/2 (+ y3 y4))][x3 y3])]
                (.put-property! (.attributes b) :type :button)
                (.put-property! (.attributes b) :value 1)
                (.put-property! (.attributes b) :action increment)
                b)
        b-dec (let [b (.two-point-circle! drw [x3 (* 1/2 (+ y5 y6))][x3 y5])]
                (.put-property! (.attributes b) :type :button)
                (.put-property! (.attributes b) :value -1)
                (.put-property! (.attributes b) :action decrement)
                b)
        plus-sign (do (.style! drw 7)
                      (.point! drw [x3 (* 1/2 (+ y3 y4))]))
        minus-sign (do (.style! drw 2)
                       (.point! drw [x3 (* 1/2 (+ y5 y6))]))
       
        valuefn (fn[] @value*)]
    {:button-increment b-inc
     :button-decrement b-dec
     :offfn all-off
     :setfn setfn
     :valuefn valuefn
     :colors (fn [inactive active button-color]
               (reset! inactive* (uc/color inactive))
               (reset! active* (uc/color active))
               (doseq [b [b-inc b-dec plus-sign minus-sign]]
                 (.color! (.attributes b)(uc/color button-color)))
               (setfn @value*)
               (.render drw))}))
                     

(defn fixedpoint-numberbar [digit-count & {:keys [drawing x-offset y-offset decimal-point hook]
                                           :or {drawing nil
                                                x-offset pad
                                                y-offset pad
                                                hook identity
                                                decimal-point nil}}]
  "Creates fixedpoint numeric display bar. Each digit, along with the sign
   character has integrated increment and decrement buttons

   digit-count - int number of digits
   drawing - The sgwr drawing object where number bar is placed. If not 
   specified a drawing object is created.

   x-offset, y-offset - int, position of number-bar relative to top left 
   corner of drawing.
   
   decimal-point - int, the number of digits to the right of the decimal point.
   0 <= decimal-point <= digit-count
   
   hook - function (fn n) called whenever any digit or sign is changed via 
   the integrated buttons. The single argument n is the value of the display 
   after the change.

   Returns a map with the following keys
   
   :drawing - the sgwr drawing object
   :setfn - function (fn n) used to set display value
   :valuefn - function (fn) which returns current display value
   :set-hook - function (fn hook) function used to set the hook function.
   :colors - function (fn bg inactive active button) used set display colors"

  (let [w (+ (* 2 pad)(* horizontal-digit-offset (inc digit-count)))
        h (* 2 (+ pad pad segment-height button-height button-gap))
        drw (or drawing (sgwr.drawing/native-drawing w h))
        scale (or (and decimal-point (/ 1.0 (math/expt 10 decimal-point))) 1)
        sign-char (sign-character drw x-offset y-offset)
        digits (let [acc* (atom [])
                     value* (atom (math/expt 10 (dec digit-count)))]
                 (dotimes [n digit-count]
                   (let [x (+ x-offset (* (inc n) horizontal-digit-offset))
                         d (digit-character drw (* scale @value*) x y-offset)]
                     (swap! value* (fn [q](* 1/10 q)))
                     (swap! acc* (fn [q](conj q d)))))
                 @acc*)
        pd (if (and decimal-point (pos? decimal-point))
             (let [x (+ x-offset (- w (* 2 pad) (* decimal-point horizontal-digit-offset)))
                   y (+ y-offset pad (* 2 segment-height))]
               (.color! drw (uc/color :red))
               (.style! drw 1)
               [(.point! drw [x y])
                (.point! drw [x (dec y)])

                (.point! drw [(dec x) y])
                (.point! drw [(dec x) (dec y)])])
             [])
        hook* (atom hook)
        buttons (let [acc* (atom [])]
                  (doseq [b (conj digits sign-char)]
                    (swap! acc* (fn [q](conj q (:button-increment b))))
                    (swap! acc* (fn [q](conj q (:button-decrement b)))))
                  @acc*)
        setfn (fn [n]
                (let [value (int (math/abs (/ n scale)))
                      probe* (atom (math/expt 10 (dec digit-count)))]
                  (doseq [d digits]
                    (let [n (int (/ value @probe*))]
                      ((:setfn d) (rem n 10))
                      (swap! probe* (fn [q](* 1/10 q)))))
                  ((:setfn sign-char) n)
                  (.render drw)))
        valuefn (fn []
                  (let [acc* (atom 0)]
                    (doseq [d digits]
                      (swap! acc* (fn [q](+ q ((:valuefn d))))))
                    (* @acc* ((:valuefn sign-char)))))
        active-button* (atom nil)]
    (.add-mouse-listener! drw (proxy [MouseListener][]
                                (mouseClicked [_])
                                (mouseEntered [_])
                                (mouseExited [_])
                                (mouseReleased [_])
                                (mousePressed [ev]
                                  (if @active-button*
                                    (let [action (.property (.attributes  @active-button*) :action)]
                                      (action)
                                      (@hook* (valuefn))
                                      (.render drw))))))
    (.add-mouse-motion-listener! drw (proxy [MouseMotionListener][]
                                       (mouseDragged [_])
                                       (mouseMoved [ev]
                                         (let [x (.getX ev)
                                               y (.getY ev)]
                                           (reset! active-button* nil)
                                           (doseq [b buttons]
                                             (let [flag (in-bounds? b x y)]
                                               (if flag (reset! active-button* b))
                                               (.select! (.attributes b) flag)))
                                           (.render drw)))))
    {:drawing drw
     :drawing-canvas (.drawing-canvas drw)
     :setfn setfn
     :valuefn valuefn
     :set-hook (fn [hf](reset! hook* hf))
     :colors (fn [background inactive active button-color]
               (.paper! drw (uc/color background))
               (doseq [d (conj digits sign-char)]
                 ((:colors d) inactive active button-color))
               (doseq [p pd]
                 (.color! (.attributes p) (uc/color active)))
               (.render drw))}))
