(println "--> cadejo/ui.indicator.led")
(ns cadejo.ui.indicator.led
  (:require [cadejo.ui.indicator.lamp])
  (:require [seesaw.core :as ss])
  (:require [seesaw.graphics :as ssg])
  (:require [seesaw.color :as ssc])
  (:import 
   java.awt.Dimension))

(def led-default-colors
  [(ssc/color :black)
   (ssc/color  64   0   0)
   (ssc/color 128   8  16)
   (ssc/color 192  16  32)
   (ssc/color 255  32  64)])

(deftype LED [colors*
              current-color*
              state*
              elements
              canvas*]
  cadejo.ui.indicator.lamp/Lamp

  (colors! [this clist]
    (reset! colors* clist))

  (colors [this]
    @colors*)

  (use-color! [this i]
    (let [k (min (max i 1)
                 (dec (count @colors*)))]
      (reset! current-color* (nth @colors* k))
      (.repaint @canvas*)))

  (current-color [this]
    @current-color*)

  (on! [this]
    (reset! state* true)
    (.repaint @canvas*))

  (on? [this]
    @state*)

  (off! [this]
    (reset! state* false)
    (.repaint @canvas*))

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
  
  (lamp-elements [this]
    elements)

  (lamp-canvas! [this jc]
    (reset! canvas* jc))

  (lamp-canvas [this]
    @canvas*)
)
        
;;; Round LED

(def led-radius 4)
(def led-pad 4)
(def led-width (+ led-radius (* led-pad)))
(def led-height led-width)

(defn led [& {:keys [colors]
              :or {colors led-default-colors}}]
  (let [dot (ssg/circle (* 1/2 led-width)
                        (* 1/2 led-height)
                        led-radius)
        lamp  (LED. (atom colors)
                    (atom (last colors))
                    (atom false)
                    [dot]
                    (atom nil))
        jc (cadejo.ui.indicator.lamp/lamp-canvas lamp)]
    (.lamp-canvas! lamp jc)
    (ss/config! jc :size (Dimension. led-width led-height))
    lamp))
        

(def rled-width 16)
(def rled-height (* 1/2 rled-width))
(def rled-pad 1)

(defn rled [& {:keys [colors]
                  :or {colors led-default-colors}}]
  (let [pan-width (+ rled-width (* 2 rled-pad))
        pan-height (+ rled-height (* 2 rled-pad))
        d-width (- pan-width rled-width)
        d-height (- pan-height rled-height)
        bar (ssg/rect (* 1/2 d-width)
                      (* 1/2 d-height)
                      rled-width
                      rled-height)
        lamp (LED. (atom colors)
                   (atom (last colors))
                   (atom false)
                   [bar]
                   (atom nil))
        jc (cadejo.ui.indicator.lamp/lamp-canvas lamp)]
    (.lamp-canvas! lamp jc)
    (ss/config! jc :size (Dimension. rled-width rled-height))
    lamp))
        
        

;; ;;; --------------- TEST
;; ;;; --------------- TEST
;; ;;; --------------- TEST

;; (def a1 (rled))
;; (def a2 (rled))
;; (def a3 (rled))
;; (def a4 (rled))

;; (def pan (ss/grid-panel :rows 1
;;                         :items [
;;                                 (.lamp-canvas a1)
;;                                 (.lamp-canvas a2)
;;                                 (.lamp-canvas a3)
;;                                 (.lamp-canvas a4)
;;                                 ]))

;; (def f (ss/frame 
;;         :content pan
;;         :on-close :dispose
;;         :size [300 :by 300]))

;; (ss/show! f)

;; (println "<<-- cadejo/ui.indicator.led")
