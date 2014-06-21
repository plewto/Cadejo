(ns cadejo.ui.indicator.led
  "Defines two simple 'LED' lamps with circle and rectangular shapes."
  (:require [cadejo.ui.indicator.lamp])
  (:require [seesaw.core :as ss])
  (:require [seesaw.graphics :as ssg])
  (:require [seesaw.color :as ssc])
  (:import 
   java.awt.Dimension
   javax.swing.JPanel))

(defn- led-canvas [lamp]
  (proxy [JPanel][]
    (paint [g]
      (.setPaint g (if (.is-on? lamp)
                     (second (.colors lamp))
                     (first (.colors lamp))))
      (.fill g (.shape lamp)))))

;; Default round LED parameters
;;
(def led-default-off-color (ssc/color :black))
(def led-default-on-color (ssc/color :red))
(def led-radius 4)
(def led-pad 4)

;; Default rect LED parameters
;;
(def rled-default-width 16)
(def rled-default-height 8)
(def rled-pad 1)


(deftype LED [colors*
              state*
              shape
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
    shape)

  (lamp-canvas! [this c]
    (swap! canvas* (fn [n] c)))

  (lamp-canvas [this]
    @canvas*))
  
(defn led [& {:keys [off on radius pad width height]
              :or {off led-default-off-color
                   on led-default-on-color
                   radius led-radius
                   pad led-pad
                   width nil
                   height nil}}]
  "Returns a round 'LED' indicator
   off    - The 'off' color 
   on     - The 'on' color
   radius - Radius of LED in pixels
   pad    - Amount of padding (in pixels) around LED.
            pad value is ignored if width or height are specified.
   width  - Width of led panel (defaults to radius + 2*pad)
   height - Height of led panel (defaults to radius + 2*pad)"
  (let [pan-width (or width (+ radius (* 2 pad)))
        pan-height (or height pan-width)
        dot (ssg/circle (* 1/2 pan-width)
                        (* 1/2 pan-height)
                        radius)
        l (LED. (atom [(ssc/to-color off)(ssc/to-color on)])
                (atom false)
                dot
                (atom nil))
        pan (led-canvas l)]
    (ss/config! pan 
                :size (Dimension. pan-width pan-height))
    (.lamp-canvas! l pan)
    l))
              
(defn rled [& {:keys [off on bar-width bar-height pad width height]
               :or {off led-default-off-color
                    on led-default-on-color
                    bar-width rled-default-width
                    bar-height rled-default-height
                    pad rled-pad
                    width nil
                    height nil}}]
  "Returns rectangular LED
  off        - The off color
  on         - The on color
  bar-width  - Width of LED
  bar-height - Height of LED
  pad        - Pad amount of padding around led element.
               if width or height are specified pad is ignored.
  width      - Width of LED panel (default bar-width + 2*pad)
  height     - Height of LED panel (default bar-height + 2*pad)"
  (let [pan-width (or width (+ bar-width (* 2 pad)))
        pan-height (or height (+ bar-height (* 2 pad)))
        d-width (- pan-width bar-width)
        d-height (- pan-height bar-height)
        bar (ssg/rect (* 1/2 d-width)
                      (* 1/2 d-height)
                      bar-width
                      bar-height)
        l (LED. (atom [(ssc/to-color off)(ssc/to-color on)])
                (atom false)
                bar
                (atom nil))
        pan (led-canvas l)]
    (ss/config! pan
                :size (Dimension. pan-width pan-height))
    (.lamp-canvas! l pan)
    l))
