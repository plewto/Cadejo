(ns cadejo.ui.indicator.eight-segment-lamp
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.indicator.multisegment-lamp])
  (:require [seesaw.graphics :as ssg])
  (:require [seesaw.color :as ssc]))

(def segment-length 12)
(def segment-height segment-length)
(def segment-gap 0)
(def segment-padding 4)
(def line-width 2)
(def dot-radius 2)

(def default-colors [(ssc/color 192 192 192)
                     (ssc/color :blue)])

;;    --       1       
;;   |  |    2   3    
;;    --       4
;;   |  |    5   6
;;    -- *     7 0
;;

(defn- hline [x y length]
  (ssg/rect x y length line-width))

(defn- vline [x y height]
  (ssg/rect x y line-width height))


(def charmap {0 [1 2 3 5 6 7]
              1 [3 6]
              2 [1 3 4 5 7]
              3 [1 3 4 6 7]
              4 [2 3 4 6]
              5 [1 2 4 6 7]
              6 [1 2 4 5 6 7]
              7 [1 3 6]
              8 [1 2 3 4 5 6 7]
              9 [1 2 3 4 6 7]
              \. [0]})



(defn eight-segment-display []
  (let [pad segment-padding
        gap segment-gap
        pan-width (+ (* 2 pad)
                     (* 2 gap)
                     segment-length)
        pan-height (+ (* 2 pad)
                      (* 1 gap)
                      (* 2 segment-height))
        x0 pad
        x1 (+ x0 (* 1/2 gap))
        x2 (+ x1 segment-length)
        x3 (+ x2 (* 1/2 gap))
        y0 pad
        y1 (+ y0 (* 1/2 gap))
        y2 (+ y1 segment-height)
        y3 (+ y2 (* 1/2 gap))
        y4 (+ y2 gap)
        y5 (+ y4 segment-height)
        y6 (+ y5 (* 1/2 gap))
        segments [(ssg/circle x3 y6 dot-radius)        
                  (hline x1 y0 segment-length)
                  (vline x0 y1 segment-height)
                  (vline x3 y1 segment-height)
                  (hline x1 y3 segment-length)
                  (vline x0 y4 segment-height)
                  (vline x3 y4 segment-height)
                  (hline x1 y6 segment-length)]
        on-segments* (atom nil)
        colors* (atom default-colors)
        current-color* (atom (lastault-colors))
        state* (atom false)
        canvas* (atom nil)
        lamp (reify cadejo.ui.indicator.multisegment-lamp/MultiSegmentLamp

               (colors! [this colors]
                 (reset! colors* colors))
               
               (colors [this]
                 @colors*)
               
               (use-color! [this i]
                 (let [j (min (max i 1)
                              (dec (count @colors*)))]
                   (reset! current-color* (nth @colors* j))
                   (.repaint @canvas*)))
               
               (current-color [this]
                 @current-color*)

               (on! [this]
                 (reset! state* true)
                 (.repaint @canvas*))
               
               (off! [this]
                 (reset! state* false)
                 (.repaint @canvas*))
               
               (on? [this]
                 @state*)
               
               (flip! [this]
                 (if (.on? this)
                   (.off! this)
                   (.on! this)))
               
               (blink! [this ms]
                 (.flip! this)
                 (Thread/sleep ms)
                 (.flip! this))
               
               (blink! [this]
                 (.blink! this 1000))
               
               (char-list [this]
                 (keys charmap))
               
               (set-char! [this c]
                 (let [pat (get charmap c)]
                   (if pat 
                     (do 
                       (reset! on-segments* [])
                       (doseq [s pat]
                         (swap! on-segments* (fn [n](conj n (nth segments s)))))
                       (.repaint @canvas*))
                     (umsg/warning "eight-segment-display can not display %s" c))))
               
               (elements [this]
                 segments)
               
               (on-elements [this]
                 @on-segments*)

               (lamp-canvas! [this jc]
                 (reset! canvas* jc))

               (lamp-canvas [this]
                 @canvas*))]
    (.lamp-canvas! lamp (cadejo.ui.indicator.multisegment-lamp/multisegment-canvas lamp))
    lamp)) 
