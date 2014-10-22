(ns sgwr.indicators.displaybar
  "Provides aggregate sets of complex display elements"
  (:require [sgwr.indicators.complex-display :as display])
  (:require [sgwr.drawing])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.indicators.shift-register]))

(defprotocol Displaybar

  (colors!
    [this background inactive active]
    "Sets display colors
     See sgwr.util.color/color
     Returns vector of java.awt.Color [backgound inactive active]")

  (colors
    [this]
    "Returns vector of java.awt.Color
     [background inactive active]")

  (cell 
    [this n]
    "Returns instance of ComplexDisplay, the nth display cell.")

  (cell-count 
    [this]
    "Returns maximum number of charters which may be displayed.")

  (shift-register 
    [this]
    "Returns the ShiftRegister instance holding contents of this.
     Returns nil if display not backed by shif register.")

  (overflow?
    [this]
    "Boolean returns true if maximum number of elements are displayed.")

  (filter! 
    [this ffn]
    "The filter is a predicate which takes the proposed value for the
    display and returns true iff the value is valid. If the filter returns
    false the display update is blocked. The default filter is constantly
    true")

  (clear!
    [this]
    "Clear this display to all spaces
    Returns display value")

  (insert!
    [this c render]
    [this c]
    "Insert character c into display 
     If it is not possible to display c, display a space.
     If render flag is true immedialty update the sgwr drawing.
     Display updates are blocked if the filter function returns false
     Returns displayed value")

  (backspace!
    [this]
    "Delete the last character.
     Display updates are blocked if the filter returns false
     Returns displayed value")

  (display! 
    [this text]
    "Display contents of text string.
     Display updates are blocked if the filter returns false
     See also load!
     Returns displayed value")

  (load! 
    [this data delay]
    [this data]
    "Similar to display except data takes form of an integer vector.
     Using load! graphics characters may be included which are not possible 
     with display!
     The delay argument causes the display to scroll with delay interval 
     (in milliseconds) between each character insertion.
     Display updates are blocked if the filter returns false.
     Returns displayed value")

  (pad! 
    [this c delay]
    [this c]
    [this]
    "Fill display with pad character c (default space) until it is full.
     delay argument (in milliseconds) causes the display to scroll.
     Display updates are blocked if the filter returns false.
     Returns displayed value")

  (to-string
    [this]
    "Returns the string equivalent of the current display.
     Note that to-string is not defined for non-standard 'graphical'
     characters.")

  (value 
    [this]
    "Returns the current 'value' of the display
     The value type is implementatin depenent.
     For a general DisplayBar the value is identical to to-string.
     For numeric bars the value should be an apprpriate numeric type.")

  (blink
    [this n period]
    [this period]
    [this]
    "Cause display to blink
     n - integer, number of blinks, default 1
     period - blink duration in milliseconds, default 100")
  
  (off! 
    [this flag c]
    [this flag]
    "If flag true set display to character c (default 292)")

  (drawing 
    [this]
    "Returns the instance of drawing which contains this.")

  (drawing-canvas
    [this]
    "Convenience method returns the drawing-canvas containing this."))

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
           filter* (atom (constantly true))
           off* (atom nil)
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
           
           sync-display (fn [text render]
                          (all-off)
                          (let [pos* (atom 0)]
                            (doseq [c (reverse text)]
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
                  
                  (filter! [this ffn]
                    (reset! filter* ffn))

                  (clear! [this]
                    (.clear! register)
                    (sync-display (.to-string register) true)
                    (.value this))
                  
                  (off! [this flag c]
                    (if flag
                      (do 
                        (reset! off* true)
                        (doseq [q cells]
                          (.set-character! q c))
                        (.render drw))
                      (let [text (.to-string register)]
                        (reset! off* false)
                        (sync-display text true))))

                  (off! [this flag]
                    (.off! this flag 292))

                  (insert! [this c render]
                    (if (and (not @off*)(not (.overflow? register)))
                      (let [temp (.cells register)]
                        (.shift! register c)
                        (let [val (.to-string register)]
                          (if (@filter* val)
                            (sync-display val render)
                            (.load! register temp)))))
                    (.value this))
                  
                  (insert! [this c]
                    (.insert! this c true))

                  (backspace! [this]
                    (if (not @off*)
                        (let [temp (.cells register)]
                          (.backspace! register)
                          (let [val (.to-string register)]
                            (if (@filter* val)
                              (sync-display val true)
                              (.load! register temp)))))
                    (.value this))
                  
                  (display! [this text]
                    (if (not @off*)
                      (let [temp (.cells register)]
                        (.parse! register text)
                        (let [val (.to-string register)]
                          (if (@filter* val)
                            (do 
                              (sync-display val true)
                              (.overflow? register))
                            (.load! register temp)))))
                    (.value this))
                  
                  (load! [this data delay]
                    (if (not @off*)
                      (let [temp (.cells register)
                            flag* (atom false)]
                      (doseq [d data]
                        (.shift! register d)
                        (let [val (.to-string register)]
                          (if (@filter* val)
                            (do 
                              (sync-display val true)
                              (Thread/sleep delay))
                            (reset! flag* true))))
                      (if @flag*
                        (do (.load! register temp)
                            (sync-display (.to-string register) true)))))
                    (.value this))
                  
                   (load! [this data]
                    (if (not @off*)
                      (let [temp (.cells register)]
                        (doseq [d data]
                          (.shift! register d))
                        (let [val (.to-string register)]
                          (if (@filter* val)
                            (sync-display val true)
                            (.load! register temp)))))
                     (.value this))

                   (pad! [this c delay]
                    (if (not @off*)
                      (let [temp (.cells register)
                            flag* (atom false)]
                        (while (not (.overflow? register))
                          (.shift! register c)
                          (let [val (.to-string register)]
                            (if (@filter* val)
                              (do 
                                (sync-display val true)
                                (Thread/sleep delay))
                              (reset! flag* true))))
                        (if @flag*
                          (do
                            (.load! register temp)
                            (sync-display (.to-string register) true)))))
                     (.value this))
                  
                   (pad! [this c]
                     (if (not @off*)
                       (let [temp (.cells register)]
                         (while (not (.overflow? register))
                           (.shift! register c))
                         (let [val (.to-string register)]
                           (if (@filter* val)
                             (sync-display val true)
                             (.load! register temp)))))
                     (.value this))
                  
                  (pad! [this]
                    (.pad! this \space))
                  
                  (to-string [this]
                    (.to-string register))
                  
                  (value [this]
                    (.to-string this))

                  (blink [this n period]
                    (dotimes [i n]
                      (.blink this period)
                      (Thread/sleep 100)))
                  
                  (blink [this period]
                    (if (not @off*)
                      (let [temp (.to-string this)]
                        (doseq [c cells]
                          (.set-character! c \space))
                        (.render drw)
                        (Thread/sleep period)
                        (.display! this temp))))
                  
                  (blink [this]
                    (.blink this 500))
                  
                  (drawing [this] drw)
                  
                  (drawing-canvas [this] (.drawing-canvas drw)) )]
       dbar)))
