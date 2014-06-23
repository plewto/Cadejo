(ns cadejo.ui.indicator.complex-display
  "Defines more complex indicators including multi-segment and dot-matrix
   The multi-segment lamp can display all digits and a few letters.
   The dot-matrix display can display all alpha-numeric characters and several 
   punctuation and graphics elements, though currently (2014.06.21) only
   numeric characters are defined."
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.indicator.lamp])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
  (:require [seesaw.graphics :as ssg])
  (:import
   java.awt.Dimension
   javax.swing.JPanel))

(def default-off-color (ssc/color :white))
(def default-on-color (ssc/color :black))

(defprotocol ComplexDisplayProtocol

  (segments 
    [this]
    "Returns a seq of Java.awt.shapes used to render the display")
  
  (pattern 
    [this]
    "Returns a seq of indexes indicating which segments are to be 
     illuminated.")

  (character-set
    [this]
    "Returns a list of characters which this display recognizes."_)

  (set-char!
    [this c]
    "Set the character to display. If c is not a member of the list 
     returned by (.character-set this) set all elements to off.

     If the display is currently 'on' immediately update display")
)

(deftype ComplexDisplay [colors*
                         state*
                         segment-shapes
                         pattern*
                         charmap
                         canvas*]
  cadejo.ui.indicator.lamp/LampProtocol

  (colors! [this c]
    (swap! colors* (fn [n] c)))

  (colors [this]
    @colors*)

  (on! [this]
    (swap! state* (fn [n] true))
    (.repaint (.lamp-canvas this)))

  (off! [this]
    (swap! state* (fn [n] false))
    (.repaint (.lamp-canvas this)))

  (is-on? [this]
    @state*)

  (flip! [this]
    (if (.is-on? this)
      (.off! this)
      (.on! this)))

  (blink! [this dwell]
    (.flip! this)
    (Thread/sleep dwell)
    (.flip! this))

  (blink! [this]
    (.blink! this 1000))

  (lamp-elements [this]
    segments)

  (lamp-canvas! [this c]
    (swap! canvas* (fn [n] c)))

  (lamp-canvas [this]
    @canvas*)

  ComplexDisplayProtocol

  (segments [this]
    segment-shapes)

  (pattern [this]
    @pattern*)

  (character-set [this]
    (keys charmap))

  (set-char! [this c]
    (swap! pattern* (fn [n](get charmap c [])))
    (if (.is-on? this)
      (.repaint @canvas*)))

  ;; (char-on! [this c]
  ;;   (.set-char! this c)
  ;;   (.on! this))
  )

(defn- complex-display-canvas [lamp]
  (proxy [JPanel][]
    (paint [g]
      (let [colors (.colors lamp)
            segments (.segments lamp)]
        (.setPaint g (first colors))
        (doseq [s segments]
          (.fill g s))
        (if (.is-on? lamp)
          (do
            (.setPaint g (second colors))
            (doseq [s (.pattern lamp)]
              (.fill g (nth segments s)))))))))

;; ---------------------------------------------------------------------- 
;;                             8 Segment Display
;;
;;
;;    --
;;   |  |
;;    -- 
;;   |  |
;;    -- .
;;

(def segment-length 12)
(def segment-height segment-length)
(def segment-gap 2)
(def segment-padding 4)
(def line-width 2)
(def dot-radius 2)

(defn hline [x y length]
  (ssg/rect x y length line-width))

(defn vline [x y height]
  (ssg/rect x y line-width height))

(def seg8-charmap {nil []
                   " " []
                   ""  []
                   "0" [0 1 2 4 5 6]
                   "1" [2 5]
                   "2" [0 2 3 4 6]
                   "3" [0 2 3 5 6]
                   "4" [1 2 3 5]
                   "5" [0 1 3 5 6]
                   "6" [0 1 3 4 5 6]
                   "7" [0 2 5]
                   "8" [0 1 2 3 4 5 6]
                   "9" [0 1 2 3 5 6]
                   "a" [0 1 2 3 4 5]
                   "b" [2 3 4 5 6]
                   "c" [0 1 4 6]
                   "d" [2 3 4 5 6]
                   "e" [0 1 3 4 6]
                   "f" [0 1 3 4]
                   "g" [0 1 2 3 5 6]
                   "h" [1 2 3 4 5]
                   "i" [2 5]
                   "j" [2 4 5 6]
                   "l" [1 4 6]
                   "n" [0 1 2 4 5]
                   "o" [0 1 2 4 5 6]
                   "p" [0 1 2 3 4]
                   "q" [0 1 2 3 5]
                   "r" [0 1 4]
                   "s" [0 2 3 4 6]
                   "u" [1 2 4 5 6]
                   "-" [3]
                   "." [7]})
 
(defn segment-8 [& {:keys [off on length height gap pad]
                    :or {off default-off-color
                         on default-on-color
                         length segment-length
                         height segment-height
                         gap segment-gap
                         pad segment-padding}}]
  "Create 8-segment display which may display all numbers and 
   a few letters.
   off    - The 'off' color
   on     - The 'on' color
   length - Length of segment in pixels
   height - Height of segment in pixels
   gap    - Gap between segments
   pad    - Padding added around outside of display"
  (let [pan-width (+ (* 2 pad)
                     (* 2 gap)
                     length)
        pan-height (+ (* 2 pad)
                      (* 1 gap)
                      (* 2 height))
        x0 pad
        x1 (+ x0 gap)
        x2 (+ x1 length gap)
        y0 pad
        y1 (+ y0 height (* 1/2 gap))
        y2 (+ y0 height gap)
        y3 (+ y2 height)
        segments (let [acc* (atom [])]
                   (doseq [seg [(hline x1 y0 length)
                                (vline x0 y0 height)
                                (vline x2 y0 height)
                                (hline x1 y1 length)
                                (vline x0 y2 height)
                                (vline x2 y2 height)
                                (hline x1 y3 length)
                                (ssg/circle (+ segment-padding
                                               (* 1.5 segment-gap)
                                               segment-length)
                                            (+ segment-padding
                                               segment-gap
                                               (* 2 segment-height))
                                            dot-radius)]]
                     (swap! acc* (fn [n] (conj n seg))))
                   @acc*)
        seg8 (ComplexDisplay. (atom [off on]) ; colors
                              (atom false)    ; state
                              segments        ; shapes
                              (atom [])       ; current pattern
                              seg8-charmap    ; character map
                              (atom nil))     ; canvas
        pan (complex-display-canvas seg8)]
    (ss/config! pan 
                :size (Dimension. pan-width pan-height))
    (.lamp-canvas! seg8 pan)
    seg8))
        
;; ---------------------------------------------------------------------- 
;;                         5 x 7 dot-matrix display
;;        01234
;;     0  .....
;;     1  .....
;;     2  .....
;;     3  .....
;;     4  .....
;;     5  .....
;;     6  .....

(def matrix-rows 7)
(def matrix-columns 5)
(def matrix-padding 4)                  ; padding around outside of matrix
(def matrix-gap 2)                      ; gap between points (horiz & vert)
(def matrix-dot-radius 2)

(def matrix-charmap {nil []
                     " " []
                     ""  []
                     "A" [1 2 3
                          5 9
                          10 14
                          15 19
                          20 21 22 23 24
                          25 29
                          30 34]
                     "B" [0 1 2 3
                          5 9
                          10 14
                          15 16 17 18
                          20 24
                          25 29
                          30 31 32 33]
                     "C" [1 2 3
                          5 9
                          10
                          15
                          20
                          25 29
                          31 32 33]
                     "D" [0 1 2
                          5 8
                          10 14
                          15 19
                          20 24
                          25 28
                          30 31 32]
                     "E" [0 1 2 3 4
                          5
                          10
                          15 16 17 18
                          20
                          25
                          30 31 32 33 34]
                     "F" [0 1 2 3 4
                          5
                          10
                          15 16 17 18
                          20
                          25
                          30]
                     "G" [1 2 3
                          5 9
                          10
                          15 17 18 19
                          20 24
                          25 29
                          31 32 33 34]
                     "H" [0 4
                          5 9
                          10 14
                          15 16 17 18 19
                          20 24
                          25 29
                          30 34]
                     "I" [1 2 3
                          7 12 17 22 27 
                          31 32 33]
                     "J" [2 3 4
                          8
                          13
                          18
                          20 23
                          25 28
                          31 32]
                     "K" [0 4
                          5 8
                          10 12
                          15 16
                          20 22
                          25 28
                          30 34]
                     "L" [0 5 10 15 20 25 30 31 32 33 34]
                     "M" [0 4
                          5 6 8 9
                          10 12 14
                          15 17 19
                          20 24
                          25 29
                          30 34]
                     "N" [0 4 5 9
                          10 11 14
                          15 17 19
                          20 23 24
                          25 29
                          30 34]
                     "O" [1 2 3
                          5 9 10 14 15 19 20 24 25 29 31 32 33]
                     "P" [0 1 2 3
                          5 9
                          10 14
                          15 16 17 18
                          20 25 30]
                     "Q" [1 2 3
                          5 9 10 14 15 19 
                          20 22 24
                          26 27 28
                          34]
                     "R" [0 1 2 3
                          5 9 10 14
                          15 16 17 18
                          20 22
                          25 28
                          30 33]
                     "S" [1 2 3
                          5 9 10
                          16 17 18
                          24 25 29
                          31 32 33]
                     "T" [0 1 2 3 4
                          7 12 17 22 27 32]
                     "U" [0 4 5 9 10 14 15 19 20 24 25 29 31 32 33]
                     "V" [0 4 5 9 10 14 15 19 20 24 26 28 32]
                     "W" [0 2 4
                          5 7 9
                          10 12 14
                          15 17 19
                          20 22 24
                          25 27 29
                          31 33]
                     "X" [0 4 5 9
                          11 13
                          17
                          21 23
                          25 29
                          30 34]
                     "Y" [0 4
                          5 9
                          10 14
                          16 18
                          22 27 32]
                     "Z" [0 1 2 3 4 
                          9 13 17 21 25
                          30 31 32 33 34]
                     "0" [1 2 3
                          5 9
                          10 13 14
                          15 17 19
                          20 21 24
                          25 29
                          31 32 33]
                     "1" [2
                          6 7
                          10 12
                          17
                          22
                          27
                          30 31 32 33 34]
                     "2" [1 2 3
                          5 9
                          14
                          17 18
                          21
                          25
                          30 31 32 33 34]
                     "3" [1 2 3
                          5 9
                          14
                          17 18
                          24
                          25 29
                          31 32 33]
                     "4" [3
                          7 8
                          11 13
                          15 18
                          20 21 22 23 24
                          28
                          33]
                     "5" [0 1 2 3 4
                          5
                          10 11 12 13
                          19
                          24
                          25 29
                          31 32 33]
                     "6" [2 3
                          6
                          10
                          15 16 17 18
                          20 24
                          25 29
                          31 32 33]
                     "7" [0 1 2 3 4
                          9
                          13
                          17
                          21
                          26
                          31]
                     "8" [1 2 3
                          5 9
                          10 14
                          16 17 18
                          20 24
                          25 29
                          31 32 33]
                     "9" [1 2 3
                          5 9
                          10 14
                          16 17 18 19
                          24
                          28
                          31 32]
                     "." [26 27 31 32]
                     "," [22 27 31]
                     "-" [16 17 18]
                     "+" [12 16 17 18 22]
                     })
                          
(defn matrix-display [& {:keys [off on gap radius pad]
                         :or {off default-off-color
                              on default-on-color
                              gap matrix-gap
                              radius matrix-dot-radius
                              pad matrix-padding}}]
  "Returns a 5x7 dot-matrix display capable of rendering all alpha-numeric
   characters and most punctuation and several graphical elements.
   Currently (2014.06.21) the character-map only defines numeric characters."
  (let [pan-width (+ (* 2 pad)
                     (* matrix-columns radius)
                     (* (dec matrix-columns) gap))
        pan-height (+ (* 2 pad)
                      (* matrix-rows radius)
                      (* (dec matrix-rows) gap))
        shapes (let [acc* (atom [])
                     delta (+ radius gap)]
                 (dotimes [r matrix-rows]
                   (let [y (+ pad (* r delta))]
                     (dotimes [c matrix-columns]
                       (let [x (+ pad (* c delta))]
                         (swap! acc* (fn [n](conj n (ssg/circle x y radius))))))))
                 @acc*)
        matrix (ComplexDisplay. (atom [off on]) ; colors
                                (atom false)    ; state
                                shapes
                                (atom [])       ; current pattern
                                matrix-charmap
                                (atom nil))     ; canvas
        pan (complex-display-canvas matrix)]
    (ss/config! pan
                :size (Dimension. pan-width pan-height))
    (.lamp-canvas! matrix pan)
    matrix))


;; ---------------------------------------------------------------------- 
;;                            Matrix Display Bar
;;

(defprotocol DisplayBarProtocol

  (char-count 
    [this])

  (clear-display!
    [this])

  (push-char!
    [this c])

  (pop-char! 
    [this])

  (set-display!
    [this s])

  (update-display! 
    [this])
)

(deftype MatrixDisplayBar [stack*
                           characters
                           canvas]

  cadejo.ui.indicator.lamp/LampProtocol

  (colors! [this colors]
    (doseq [c characters]
      (.colors! c colors)))

  (colors [this]
    (.colors (first characters)))
  
  (on! [this]
    (doseq [c characters]
      (.on! c)))

  (off! [this]
      (doseq [c characters]
        (.off! c)))

  (is-on? [this]
    (.is-on? (first characters)))

  (flip! [this]
     (doseq [c characters]
        (.flip! c)))

  (blink! [this dwell]
    (.flip! this)
    (Thread/sleep dwell)
    (.flip! this))

  (blink! [this]
    (.blink! this 1000))
  
  (lamp-elements [this]
    (let [acc* (atom [])]
      (doseq [c characters]
        (swap! acc* (fn [n](conj n (.lamp-elements c)))))
      @acc*))

  (lamp-canvas! [this can]
    (umsg/warning "lamp-canvas! not defined for complex-display/MatrixDisplayBar")
    nil)

  (lamp-canvas [this]
    canvas)
    
  ComplexDisplayProtocol

  (segments [this]
    (.lamp-elements this))

  (pattern [this]
    @stack*)

  (character-set [this]
    (character-set (first characters)))

  (set-char! [this c]
    (.push-char! this c))

  DisplayBarProtocol

  (char-count [this]
    (count characters))

  (clear-display! [this]
    (swap! stack* (fn [n] []))
    (.update-display! this))

  (push-char! [this c]
    (swap! stack* (fn [n](conj n c)))
    (.update-display! this))

  (set-display! [this s]
    (swap! stack* (fn [n][]))
    (doseq [c s]
      (swap! stack* (fn [n](conj n c))))
    (.update-display! this))

  (update-display! [this]
    (doseq [c characters]
      (.set-char! c nil))
    (try
      (let [diff (- (.char-count this)(count @stack*))]
        (dotimes [i (count @stack*)]
          (.set-char! (nth characters (+ diff i))
                      (str (nth @stack* i))))
        @stack*)
      (catch IndexOutOfBoundsException ex
        (umsg/warning (format "MatrixDisplayBar character length exceeded '%s'" @stack*))
        nil)))
  )
    
    
(defn matrix-display-bar [& {:keys [count off on pad]
                             :or {count 8
                                  off default-off-color
                                  on default-on-color
                                  pad matrix-padding}}]
  "Returns lamp component with count matrix characters."
  (let [clist (let [acc* (atom [])]
                (dotimes [i count]
                  (swap! acc* (fn [n](conj n (matrix-display :off off
                                                             :on on
                                                             :pad pad)))))
                @acc*)
        sub-panels (let [acc* (atom [])]
                      (doseq [s clist]
                        (swap! acc* (fn [n](conj n (.lamp-canvas s)))))
                      @acc*)
        panel (ss/horizontal-panel :items sub-panels)
        mdb (MatrixDisplayBar. (atom [])
                               clist
                               panel)]
    mdb))

 
;;; ------ TEST  
;;; ------ TEST  
;;; ------ TEST  

;; (def q (matrix-display-bar))
;; (def f (ss/frame :title "Text Case"
;;                  :content (ss/border-panel :north (.canvas q)
;;                                            :center (ss/button :text "Why Me?"))
;;                  :on-close :dispose
;;                  :size [300 :by 30]))
;; (ss/show! f)
;; (.on! q)
;; (.set-display! q "123")
