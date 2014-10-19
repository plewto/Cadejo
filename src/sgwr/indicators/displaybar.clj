(ns sgwr.indicators.displaybar
  "Provides aggregate sets of complex display elements"
  (:require [sgwr.indicators.complex-display :as display])
  (:require [sgwr.drawing])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.indicators.shift-register])
)

(defprotocol Displaybar

  (colors!
    [this background inactive active]
    "Sets display colors
     See sgwr.util.color/color")

  (colors
    [this]
    "Returns vector of display colors
     [background inactive active]")

  (cell 
    [this n]
    "Returns instance of ComplexDisplay, the nth display cell.")

  (cell-count 
    [this]
    "Returns maximum number of charters which may be displayed.")

  (shift-register 
    [this]
    "Returns the ShiftRegister instance holding contents of this.")

  (overflow?
    [this]
    "Boolean returns true if maximum number of elements are displayed.")

  (clear!
    [this]
    "Clear this display to all spaces")

  (insert!
    [this c render]
    [this c]
    "Insert character c into display 
     If it is not possible to display c, display a space.
     If render flag is true immedialty update the sgwr drawing.")

  (backspace!
    [this]
    "Delete the last character.")

  (display! 
    [this text]
    "Display contents of text string.
     See also load!")

  (load! 
    [this data delay]
    [this data]
    "Similar to display but except data takes the form of an integer vector.
     Using load! graphics characters may be included which are not possible 
     with display!
     The delay argument causes the display to scroll with delay interval 
     (in milliseconds) between each character insertion.")

  (pad! 
    [this c delay]
    [this c]
    [this]
    "Fill display with pad character c (default space) until it is full.
     delay argument (in milliseconds) causes the display to scroll.")

  (to-string
    [this]
    "Returns the string equivalent of the current display.
     Note that to-string is not defined for non-standard 'graphical'
     characters.")

  (blink
    [this n period]
    [this period]
    [this]
    "Cause display to blink
     n - integer, number of blinks, default 1
     period - blink duration in milliseconds, default 100")

  (drawing 
    [this]
    "Returns the instance of drawing which contains this.")

  (drawing-canvas
    [this]
    "Convenience method returns the drawing-canvas containing this.")
  )



; ---------------------------------------------------------------------- 
;                            Linear Display Bar
    
(defn displaybar 
  
  "Creates linear display bar
   (displaybar char-count)
   (displaybar char-count etype)
   (displaybar drw char-count etype x-offset y-offset)
   drw        - Drawing. if not specified a drawing object is created.
                Note that the drawing must have a 'native' coordinate-system
                See sgwr.drawing and sgwr.coordinate-system
   char-count - int, number of characters
   etype      - display type may be one of the following
                :matrix - a 7 x 5 dot matrix
                16 - a 16-segment display
                 7 - a 7-segment display, default :matrix
   x-offset   - int, horizontal position of displaybar in drawing, default 0
   y-offset   - int, vertical position of displaybar in drawing, default 0"

  ([char-count]
     (displaybar char-count :matrix))

  ([char-count etype]
     (let [[w h border gap](cond (= etype :matrix)
                                 [24 38 6 5]
                                 (= etype 16)
                                 [25 38 6 8]
                                 :default
                                 [25 38 6 8])
           bar-width (+ (* 2 border)(* char-count (+ w gap)))
           bar-height (+ (* 2 border) h)
           drw (sgwr.drawing/native-drawing bar-width bar-height)]
       (displaybar drw char-count etype 0 0)))          

  ([drw char-count etype x-offset y-offset]
     (let [[element-constructor w h border gap]
           (cond (= etype :matrix)
                 [display/dot-matrix-display 25 35 6 5]
                 (= etype 16)
                 [display/sixteen-segment-display 25 35 6 5]
                 :default
                 [display/seven-segment-display 24 38 6 8])
           bar-width (+ (* 2 border)(* char-count (+ w gap)))
           bar-height (+ (* 2 border) h)
           background* (atom (uc/color :black))
           inactive* (atom (uc/color [38 10 38]))
           active* (atom (uc/color :red))
           cells (let [acc* (atom [])]
                   (.suspend-render! drw true)
                   (.paper! drw @background*)
                   (.color! drw @inactive*)
                   (.style! drw 0)
                   (.width! drw 1)
                   (doseq [i (range char-count)]
                     (let [x (+ border x-offset (* i (+ w gap)))
                           y (+ border y-offset)
                           e (element-constructor drw x y w h)]
                       (.colors! e @inactive* @active*)
                       (swap! acc* (fn [q](conj q e)))))
                   (.suspend-render! drw false)
                   (into [] (reverse @acc*)))
           register (sgwr.indicators.shift-register/shift-register char-count)
           
           all-off (fn []
                     (doseq [c cells]
                       (.set-character! c \space)))
           
           sync-display (fn [render]
                          (all-off)
                          (let [pos* (atom 0)
                                text (reverse (.to-string register))]
                            (doseq [c text]
                              (let [d (nth cells @pos*)]
                                (.set-character! d c)
                                (swap! pos* inc))))
                          (if render (.render drw)))
           
           dbar (reify Displaybar
                  
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
                  
                  (cell-count [this] char-count)
                  
                  (shift-register [this] register)
                  
                  (overflow? [this]
                    (.overflow? register))
                  
                  (clear! [this]
                    (.clear! register)
                    (sync-display true))
                  
                  (insert! [this c render]
                    (if (not (.overflow? register))
                      (do 
                        (.shift! register c)
                        (sync-display render))))
                  
                  (insert! [this c]
                    (.insert! this c true))
                  
                  (backspace! [this]
                    (.backspace! register)
                    (sync-display true))
                  
                  (display! [this text]
                    (.parse! register text)
                    (sync-display true)
                    (.overflow? register))
                  
                  (load! [this data delay]
                    (doseq [d data]
                      (.shift! register d)
                      (sync-display true)
                      (Thread/sleep delay)))
                  
                  (load! [this data]
                    (doseq [d data]
                      (.shift! register d))
                    (sync-display true))
                  
                  (pad! [this c delay]
                    (while (not (.overflow? register))
                      (.shift! register c)
                      (sync-display true)
                      (Thread/sleep delay)))
                  
                  (pad! [this c]
                    (while (not (.overflow? register))
                      (.shift! register c))
                    (sync-display true))
                  
                  (pad! [this]
                    (.pad! this \space))
                  
                  (to-string [this]
                    (.to-string register))
                  
                  (blink [this n period]
                    (dotimes [i n]
                      (.blink this period)
                      (Thread/sleep 100)))
                  
                  (blink [this period]
                    (let [temp (.to-string this)]
                      (doseq [c cells]
                        (.set-character! c \space))
                      (.render drw)
                      (Thread/sleep period)
                      (.display! this temp)))
                  
                  (blink [this]
                    (.blink this 500))
                  
                  (drawing [this] drw)
                  
                  (drawing-canvas [this] (.drawing-canvas drw)) )]
       dbar)))



; ---------------------------------------------------------------------- 
;                            Numeric Display Bar
;
; Similar to DisplayBar but specifically for numbers.
; 

(def ^:private digits [0 1 2 3 4 5 6 7 8 9
                       \0 \1 \2 \3 \4 \5 \6 \7 \8 \9
                       "0" "1" "2" "3" "4" "5" "6" "7" "8" "9"
                       '0 '1 '2 '3 '4 '5 '6 '7 '8 '9
                       :0 :1 :2 :3 :4 :5 :6 :7 :8 :9])

(defn- digit? [obj] 
  (some (fn [n](= n obj)) digits))

(defn numeric-bar [digit-count]
  "Creates numeric DisplayBar
   Returns map with following keys
       :displaybar :drawing :drawing-canvas
       :valuefn :setfn :insertfn and :modified?

   where
   
   :displaybar, :drawing and :drawing-canvas map to instances of
   DisplayBar Drawing and JPanel respectively

   :valuefn 
     maps to a function which returns the currently displayed value
     (valuefn) --> float

   :setfn 
     maps to a function used to set the displayed value
     (setfn n) 

   :insertfn 
     maps to a function used to modify the current value
     (insertfn c) 
     where c = :sign --> toggle value sign
           c = :point --> insert decimal point
           c = :back --> delete left-most digit
           c = :clear --> clear display, set value to 0
           c = :enter --> clear the value modified symbol
           c is digit --> insert c

    :modified?
     maps to a function which indicates if the value has been modified.
     A small triangle in the upper left hand corner indicates the
     displayed value has been modified. Inserting either :clear or
     :enter turns the modified symbol off. Inserting any other character
     turns the modified symbol on."

  (let [border 6
        cell-width 25
        cell-height 38
        drawing-width (+ (* 2 border)(* (+ 4 digit-count) cell-width))
        drawing-height (+ (* 2 border) cell-height)
        value* (atom "")
        has-point* (atom false)
        pos-sign* (atom true)
        modified* (atom false)
        drw (sgwr.drawing/native-drawing drawing-width drawing-height )
        dbar (sgwr.indicators.displaybar/displaybar drw digit-count 7 cell-width 0)
        [pos-sign neg-sign modified](let [[bg inactive active] (.colors dbar)
                                          x0 (* 1/4 cell-width)
                                          x1 (* 1/2 cell-width)
                                          x2 (* 3/4 cell-width)
                                          yc (+ 4 (* 1/2 cell-height))
                                          y0 (- yc 6)
                                          y1 (+ yc 6) ]
                                      (.color! drw inactive)
                                      (.style! drw 0)
                                      (.width! drw 1)
                                      [(.line! drw [x1 y0][x1 y1])
                                       (.line! drw [x0 yc][x2 yc])
                                       (do (.style! drw 9)
                                           (.point! drw [x1 8]))])
        sync-display (fn []
                       (let [[bg inactive active](.colors dbar)]
                         (.color! (.attributes neg-sign) active)
                         (.color! (.attributes pos-sign)(if @pos-sign* active inactive))
                         (.color! (.attributes modified)(if @modified* active inactive))
                         (.display! dbar @value*)))
        valuefn (fn []
                  (if (pos? (count @value*))
                    (let [s (if @pos-sign* 1 -1)
                          mag (Float/parseFloat (cond (.startsWith @value* ".")
                                                      (str "0" @value*)
                                                      (.endsWith @value* ".")
                                                      (str @value* "0")
                                                      :default 
                                                      @value*))]
                      (* s mag))
                    0.0))

        set-valuefn (fn [v]
                    (let [mag (Math/abs v)]
                      (reset! value* (str mag))
                      (reset! pos-sign* (pos? v))
                      (reset! has-point* (pos? (.indexOf @value* ".")))
                      (reset! modified* false)
                      (sync-display)))

        insert (fn [c]
                 (reset! modified* true)
                 (cond (= c :sign)
                       (swap! pos-sign* not)
        
                       (or (= c :point)(= c ".")(= c \.))
                       (if (not @has-point*)
                         (do (reset! value* (str @value* \.))
                             (reset! has-point* true)))
        
                       (= c :enter)
                       (reset! modified* false)
        
                       (= c :back)
                       (let [vcount (count @value*)]
                         (if (pos? vcount)
                           (do
                             (reset! value* (subs @value* 0 (dec vcount)))
                             (reset! has-point* (pos? (.indexOf @value* ".")))
                             (reset! modified* true))))

                       (= c :clear)
                       (do 
                         (reset! value* "")
                         (reset! has-point* false)
                         (reset! pos-sign* true)
                         (reset! modified* false))
                         
                       :default
                       (if (and (not (.overflow? (.shift-register dbar)))(digit? c))
                         (do
                           (reset! value* (str @value* c))
                           (reset! modified* true))))
                 (sync-display)
                 (valuefn))]
    (.block-on-overflow! (.shift-register dbar) true)
    {:displaybar dbar
     :drawing drw
     :drawing-canvas (.drawing-canvas drw)
     :valuefn valuefn
     :setfn set-valuefn
     :insertfn insert 
     :modified? (fn []@modified*) }))

; ---------------------------------------------------------------------- 
;                          Rectangular Display Area

(defn display-grid 
  "Creates rectangular display area using dot-matrix elements.
   (display-grid rows columns)
   (display-grid drw rows columns x-offset y-offset)

   rows     - int, character row count
   columns  - int, character column count    
   drw      - sgwr Drawing containing display, if not specified a drawing 
              object is created. Note that the drawing must have a 
              'native' coordinate-system. 
   x-offset - int, horizontal position of displaybar in drawing, default 0
   y-offset - int, vertical position of displaybar in drawing, default 0"

  ([rows columns]
     (let [border 6
           gap 0
           cell-width (+ 35 gap)
           cell-height (+ 45 gap)
           dwidth (+ (* 2 border)(* columns cell-width))
           dheight (+ (* 2 border)(* rows cell-height))
           drw (sgwr.drawing/native-drawing dwidth dheight)]
       (display-grid drw rows columns 0 0)))

  ([drw rows columns x-offset y-offset]
     (.style! drw 0)
     (.width! drw 1)
     (.suspend-render! drw true)
    (let [border 6
          gap 0
          cell-width (+ 35 gap)
          cell-height (+ 45 gap)
          [drawing-width drawing-height] (let [dim (.canvas-bounds 
                                                    (.coordinate-system drw))]
                                           dim)
          background* (atom (uc/color :black))
          inactive* (atom (uc/color [38 10 38]))
          active* (atom (uc/color :red))
          char-count (* rows columns)
          cells (let [acc* (atom [])
                      w (+ x-offset (* cell-width (dec columns)))]
                  (doseq [r (range rows)]
                    (let [y (+ border y-offset (* r cell-height))]
                      (doseq [c (range (dec columns) -1 -1)]
                        (let [x (* c cell-width)
                              e (display/dot-matrix-display drw (+ (* 3/2 border) (- w x)) y cell-width cell-height)]
                          (.colors! e @inactive* @active*)
                          (swap! acc* (fn [q](conj q e)))))))
                  (.suspend-render! drw false)
                  (into [] (reverse @acc*)))
          
          register (let [r (sgwr.indicators.shift-register/shift-register char-count)]
                     (.block-on-overflow! r false)
                     r)
          all-off (fn []
                    (doseq [c cells]
                      (.set-character! c \space)))
          
          sync-display (fn [render]
                         (all-off)
                         (let [pos* (atom 0)
                               text (reverse (.to-string register))]
                           (doseq [c text]
                             (let [d (nth cells @pos*)]
                               (.set-character! d c)
                               (swap! pos* inc))))
                         (if render (.render drw)))
          
          dgrid (reify Displaybar
                  
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
                  
                  (cell-count [this] char-count)
                  
                  (shift-register [this] register)
                  
                  (overflow? [this]
                    (.overflow? register))
                  
                  (clear! [this]
                 (.clear! register)
                    (sync-display true))
                  
                  (insert! [this c render]
                    (if (not (.overflow? register))
                      (do 
                        (.shift! register c)
                        (sync-display render))))
                  
                  (insert! [this c]
                    (.insert! this c true))
                  
                  (backspace! [this]
                    (.backspace! register)
                    (sync-display true))
                  
                  (display! [this text]
                    (.parse! register text)
                    (sync-display true)
                    (.overflow? register))
                  
                  (load! [this data delay]
                    (doseq [d data]
                      (.shift! register d)
                      (sync-display true)
                      (Thread/sleep delay)))
                  
                  (load! [this data]
                    (doseq [d data]
                      (.shift! register d))
                    (sync-display true))
                  
                  (pad! [this c delay]
                    (while (not (.overflow? register))
                      (.shift! register c)
                      (sync-display true)
                      (Thread/sleep delay)))
                  
                  (pad! [this c]
                    (while (not (.overflow? register))
                      (.shift! register c))
                    (sync-display true))
                  
                  (pad! [this]
                    (.pad! this \space))
                  
                  (to-string [this]
                    (.to-string register))
                  
                  (blink [this n period]
                    (dotimes [i n]
                      (.blink this period)
                      (Thread/sleep 100)))
                  
                  (blink [this period]
                    (let [temp (.to-string this)]
                      (doseq [c cells]
                        (.set-character! c \space))
                      (.render drw)
                      (Thread/sleep period)
                      (.display! this temp)))
                  
                  (blink [this]
                    (.blink this 500))
                  
                  (drawing [this] drw)
                  
                  (drawing-canvas [this] (.drawing-canvas drw)) )]
      dgrid)))
