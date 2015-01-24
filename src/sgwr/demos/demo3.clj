;; demos 3
;; Polar coordinate system
;;

(ns sgwr.demos.demo3
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.drawing :as drw])
  (:require [sgwr.elements.group :as grp])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.cs.native])
  (:require [seesaw.core :as ss])
  (:import  java.awt.event.MouseMotionListener
            java.awt.event.MouseListener))

(declare status)
(def set-attributes! sgwr.elements.element/set-attributes!)

(def drw (drw/polar-drawing 600 600 1.5 :unit :deg))
(def root (.root drw))

;; NOTE: 
;; Coordinate system may be set at element level, here text is displayed 
;; using native coordinates on what is otherwise a polar drawing.
;;
(def grp1 (grp/group root :id :grp1))
(.set-coordinate-system! grp1 (sgwr.cs.native/native-coordinate-system 600 600))
(text/text grp1 [170 15] "Sgwr Demo 3 ~ Polar Coordinates")
(line/line grp1 [10 20][590 20])


;; Axis
;; 
(def grp2 (grp/group root :id :grp2))
(doseq [d (range 0 180 30)]
  (line/line grp2 [-1.1 d][1.1 d] :color [16 0 128])
  (text/text grp2 [1.2 d] (str d) :size 4)
  (text/text grp2 [-1.2 d] (str (+ 180 d)) :size 4.5))
(doseq [r (range 0.1 1.5 0.2)]
  (circle/circle grp2 [(- r) 45][r 45] :color [16 0 128]))


;; Plots
;; 
(def grp3 (grp/group root :id :grp3))
(def point-count 500)
(def points (let [acc* (atom [])]
              (dotimes [i point-count]
                (let [p (point/point grp3 [0 0] :style :pixel)]
                  (swap! acc* (fn [q](conj q p)))))
              @acc*))

(defn plot [amp1 freq1 amp2 freq2]
  (let [k (/ (* 2 Math/PI) point-count)
        k1 (* k freq1)
        k2 (* k freq2)]
  (dotimes [i point-count]
    (let [a (* amp1 (Math/sin (* i k1)))
          b (* amp2 (Math/cos (* i k2)))
          c (+ a b)
          deg (math/rad->deg (* i k))]
      (.set-points! (nth points i) [[c deg]])))
  (.render drw)))

(.add-mouse-motion-listener drw (proxy [MouseMotionListener][]
                                  (mouseDragged [_])
                                  (mouseMoved [ev]
                                    (let [[r a](.mouse-where drw)
                                          rs (format "Radius %5.3f  Angle %3d"
                                                     r (int a))]
                                      (status (str rs))))))

(.background! drw :black)
(.render drw)
(plot 1 3 0 1)

; -------------------------------------------------------------

(def bgrp-sin-amp (ss/button-group))
(def bgrp-cos-amp (ss/button-group))
(def bgrp-sin-frq (ss/button-group))
(def bgrp-cos-frq (ss/button-group))

(def tb-sin-a0 (ss/radio :text "0.00" :group bgrp-sin-amp :id :a0))
(def tb-sin-a1 (ss/radio :text "0.25" :group bgrp-sin-amp :id :a1))
(def tb-sin-a2 (ss/radio :text "0.50" :group bgrp-sin-amp :id :a2))
(def tb-sin-a3 (ss/radio :text "0.75" :group bgrp-sin-amp :id :a3))
(def tb-sin-a4 (ss/radio :text "1.00" :group bgrp-sin-amp :id :a4 :selected? true))
(def sin-amp-buttons [tb-sin-a0 tb-sin-a1 tb-sin-a2 tb-sin-a3 tb-sin-a4])
(def pan-sin-amp (ss/border-panel 
                  :west (ss/label :text "Amp ")
                  :center (ss/grid-panel :rows 1
                                         :items sin-amp-buttons)))

(def tb-cos-b0 (ss/radio :text "0.00" :group bgrp-cos-amp :id :b0 :selected? true))
(def tb-cos-b1 (ss/radio :text "0.25" :group bgrp-cos-amp :id :b1))
(def tb-cos-b2 (ss/radio :text "0.50" :group bgrp-cos-amp :id :b2))
(def tb-cos-b3 (ss/radio :text "0.75" :group bgrp-cos-amp :id :b3))
(def tb-cos-b4 (ss/radio :text "1.00" :group bgrp-cos-amp :id :b4))
(def cos-amp-buttons [tb-cos-b0 tb-cos-b1 tb-cos-b2 tb-cos-b3 tb-cos-b4])
(def pan-cos-amp (ss/border-panel 
                  :west (ss/label :text "Amp ")
                  :center (ss/grid-panel :rows 1
                                         :items cos-amp-buttons)))

(def tb-sin-c0 (ss/radio :text "1" :group bgrp-sin-frq :id :c0))
(def tb-sin-c1 (ss/radio :text "2" :group bgrp-sin-frq :id :c1))
(def tb-sin-c2 (ss/radio :text "3" :group bgrp-sin-frq :id :c2 :selected? true))
(def tb-sin-c3 (ss/radio :text "4" :group bgrp-sin-frq :id :c3))
(def tb-sin-c4 (ss/radio :text "5" :group bgrp-sin-frq :id :c4))
(def sin-frq-buttons [tb-sin-c0 tb-sin-c1 tb-sin-c2 tb-sin-c3 tb-sin-c4])
(def pan-sin-frq (ss/border-panel 
                  :west (ss/label :text "Frq  ")
                  :center (ss/grid-panel :rows 1
                                         :items sin-frq-buttons)))

(def tb-cos-d0 (ss/radio :text "1" :group bgrp-cos-frq :id :d0 :selected? true))
(def tb-cos-d1 (ss/radio :text "2" :group bgrp-cos-frq :id :d1))
(def tb-cos-d2 (ss/radio :text "3" :group bgrp-cos-frq :id :d2))
(def tb-cos-d3 (ss/radio :text "4" :group bgrp-cos-frq :id :d3))
(def tb-cos-d4 (ss/radio :text "5" :group bgrp-cos-frq :id :d4))
(def cos-frq-buttons [tb-cos-d0 tb-cos-d1 tb-cos-d2 tb-cos-d3 tb-cos-d4])
(def pan-cos-frq (ss/border-panel 
                  :west (ss/label :text "Frq  ")
                  :center (ss/grid-panel :rows 1
                                         :items cos-frq-buttons)))


(let [button-value-map {:a0  0.00  
                        :a1  0.25  
                        :a2  0.50  
                        :a3  0.75  
                        :a4  1.00  
                        :b0  0.00  
                        :b1  0.25  
                        :b2  0.50  
                        :b3  0.75  
                        :b4  1.00  
                        :c0  1  
                        :c1  2  
                        :c2  3  
                        :c3  4  
                        :c4  5  
                        :d0  1  
                        :d1  2  
                        :d2  3  
                        :d3  4  
                        :d4  5}
      sin-amp* (atom 1.0)
      sin-frq* (atom 3)
      cos-amp* (atom 0.0)
      cos-frq* (atom 1.0)
      update (fn [] 
               (plot @sin-amp* @sin-frq* @cos-amp* @cos-frq*))]
  (doseq [b sin-amp-buttons]
    (ss/listen b :action (fn [ev]
                           (let [id (ss/config (.getSource ev) :id)
                                 val (id button-value-map)]
                             (reset! sin-amp* val)
                             (update)))))
  (doseq [b cos-amp-buttons]
    (ss/listen b :action (fn [ev]
                           (let [id (ss/config (.getSource ev) :id)
                                 val (id button-value-map)]
                             (reset! cos-amp* val)
                             (update)))))
  (doseq [b sin-frq-buttons]
    (ss/listen b :action (fn [ev]
                           (let [id (ss/config (.getSource ev) :id)
                                 val (id button-value-map)]
                             (reset! sin-frq* val)
                             (update)))))
  (doseq [b cos-frq-buttons]
    (ss/listen b :action (fn [ev]
                           (let [id (ss/config (.getSource ev) :id)
                                 val (id button-value-map)]
                             (reset! cos-frq* val)
                             (update))))) )

(def pan-sin (ss/vertical-panel 
              :items [(ss/label :text "Sine" :halign :center)
                      pan-sin-amp
                      pan-sin-frq]))

(def pan-cos (ss/vertical-panel 
              :items [(ss/label :text "Cosine" :halign :center)
                      pan-cos-amp
                      pan-cos-frq]))


(def lab-status (ss/label :text " "))

(def pan-south (ss/border-panel :south lab-status
                                :west pan-sin
                                :east pan-cos))

(def pan-main (ss/border-panel 
               :center (.canvas drw)
               :south pan-south
               ))

(defn status [msg]
  (ss/config! lab-status :text (str " " msg)))

(def f (ss/frame :title "Sgwr Demo 3"
                 :content pan-main
                 :on-close :dispose
                 :size [650 :by 650]))

(ss/show! f)

(defn rl [](use 'sgwr.demos.demo3 :reload))
(defn rla [](use 'sgwr.demos.demo3 :reload-all))
(defn exit [](System/exit 0))
