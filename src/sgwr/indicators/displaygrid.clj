(ns sgwr.indicators.displaygrid
  (:require [sgwr.indicators.complex-display :as display])
  (:require [sgwr.indicators.displaybar])
  (:require [sgwr.drawing])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.indicators.shift-register]))


; ---------------------------------------------------------------------- 
;                          Rectangular Display Area

(defn displaygrid 
  "Creates rectangular display area using dot-matrix elements.
   (display-grid rows columns)
   (display-grid drw rows columns x-offset y-offset)

   rows     - int, character row count
   columns  - int, character column count    
   drw      - sgwr Drawing containing display, if not specified a drawing 
              object is created. Note that the drawing must have a 
              'native' coordinate-system. 
   x-offset - int, horizontal position of displaybar in drawing, default 0
   y-offset - int, vertical position of displaybar in drawing, default 0
   NOTES displaygrid does not suport filter
"

  ([rows columns]
     (let [border 6
           gap 0
           cell-width (+ 35 gap)
           cell-height (+ 45 gap)
           dwidth (+ (* 2 border)(* columns cell-width))
           dheight (+ (* 2 border)(* rows cell-height))
           drw (sgwr.drawing/native-drawing dwidth dheight)]
       (displaygrid drw rows columns 0 0)))

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
          
          dgrid (reify sgwr.indicators.displaybar/Displaybar
                  
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
                  
                  (filter! [this _] nil) ;; filter not suported

                  (clear! [this]
                    (.clear! register)
                    (sync-display true)
                    "")
                  
                  (insert! [this c render]
                    (if (not (.overflow? register))
                      (do 
                        (.shift! register c)
                        (sync-display render)))
                    (.to-string this))
                  
                  (insert! [this c]
                    (.insert! this c true))
                  
                  (backspace! [this]
                    (.backspace! register)
                    (sync-display true)
                    (.to-string this))
                  
                  (display! [this text]
                    (.parse! register text)
                    (sync-display true)
                    (.overflow? register)
                    (.to-string this))
                  
                  (load! [this data delay]
                    (doseq [d data]
                      (.shift! register d)
                      (sync-display true)
                      (Thread/sleep delay))
                    (.to-string this))
                  
                  (load! [this data]
                    (doseq [d data]
                      (.shift! register d))
                    (sync-display true)
                    (.to-string this))
                  
                  (pad! [this c delay]
                    (while (not (.overflow? register))
                      (.shift! register c)
                      (sync-display true)
                      (Thread/sleep delay))
                    (.to-string this))
                  
                  (pad! [this c]
                    (while (not (.overflow? register))
                      (.shift! register c))
                    (sync-display true)
                    (.to-string this))
                  
                  (pad! [this]
                    (.pad! this \space)
                    (.to-string this))
                  
                  (off! [this flag c] ;; not suported
                    nil)

                  (off! [this flag] ;; not suported
                    nil)

                  (to-string [this]
                    (.to-string register))
                  
                  (value [this]
                    (.to-string this))

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
