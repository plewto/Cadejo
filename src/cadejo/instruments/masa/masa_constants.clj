(ns cadejo.instruments.masa.masa-constants
    (:require [sgwr.util.color :as uc]))

(def b3 (map float [1/2 3/2 1 2 3 4 5 6 8]))
(def harmonic (map float [1 2 3 4 5 6 7 8 9]))
(def odd (map float [1/2 3/2 5/2 7/2 9/2 11/2 13/2 15/2 17/2]))
(def prime (map float [1/2 3/2 5/2 7/2 11/2 13/2 17/2 19/2 23/2]))

(def drawing-width 830)
(def drawing-height 450)
(def margins 16)
(def slider-spacing 50)
(def slider-length 150)
(def envelope-panel-width 175)
(def envelope-panel-height 150)

(def drawbar-track-color (uc/color :gray))

(def drawbar-track-color-sub (let [[r g b](uc/rgb drawbar-track-color)]
                               (uc/color [(int (min 255 (* 1.25 r)))(int (* 0.50 g)) (int (* 0.5 b))])))

(def drawbar-track-color-2 (let [[r g b](uc/rgb drawbar-track-color)]
                               (uc/color [(int (* 0.5 r)) (int (* 0.5 g))(int (min 255 (* 1.25 b)))])))

(def drawbar-color-seq [drawbar-track-color-sub
                        drawbar-track-color-sub
                        drawbar-track-color
                        drawbar-track-color
                        drawbar-track-color-2
                        drawbar-track-color
                        drawbar-track-color-2
                        drawbar-track-color-2
                        drawbar-track-color])

(def max-decay-time 0.5)
(def min-vibrato-frequency 1.0)
(def max-vibrato-frequency 8.0)
(def max-vibrato-sensitivity 0.10)
(def max-vibrato-delay 8.0)

(def max-scanner-delay 0.012)
(def max-scanner-delay-mod 1.0)
(def min-scanner-rate 0.1) ; limits both 'mod' and 'scan' signals
(def max-scanner-rate 8.0)


(def min-amp-db -48)
(def max-amp-db 0)
