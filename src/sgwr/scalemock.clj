;; Mock-up of component to display/set MIDI key number ranges
;; left click on ruler to set lower limit, right click for upper.
;;


(ns sgwr.scalemock
  (:require [sgwr.drawing])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.MouseListener
           java.awt.event.MouseMotionListener))
           
            
(def rule-color :gray)
(def point1-color :cyan)
(def point2-color :yellow)
(def active-color :blue)

(defn keyscale []
  (let [w 900
        h 150
        drw (sgwr.drawing/cartesian-drawing w h [-12.0 -1.0][140.0 1.0])
        p1* (atom nil)
        p2* (atom nil)
        active-line* (atom nil)
        current-positions (fn []
                        (let [x1 (ffirst (.construction-points @p1*))
                              x2 (ffirst (.construction-points @p2*))]
                          [(min x1 x2)(max x1 x2)]))]

    (.color! drw rule-color)
    (.line! drw [0 0][128 0])
    (doseq [x (range 128)]
      (let [h (cond (zero? (rem x 12)) 0.20
                    (zero? (rem x  6)) 0.10
                    :default 0.05)]
        (.line! drw [x (- h)][x h])))
    (.width! drw 1.5)
    (doseq [x (range 0 128 12)]
      (.text! drw [(- x 1) -0.50](str x)))
    (.freeze! drw)


    (.width! drw 6.0)
    (.color! drw active-color)
    (reset! active-line* (.line! drw [0 0][128 0]))
    
    (.width! drw 1.0)
    (.style! drw 0.0)
    (.color! drw point1-color)
    (reset! p1* (.point! drw [0 0]))
    (.color! drw point2-color)
    (reset! p2* (.point! drw [128 0]))

    (.add-mouse-listener! 
     drw (proxy [MouseListener][]
              (mouseClicked [ev])
              (mouseEntered [ev])
              (mouseExited [ev])
              (mousePressed [ev]) 
              (mouseReleased [ev]
                (let [u (.getX ev)
                      cs (.coordinate-system drw)
                      pos (.inv-map cs [u 0])
                      keynum (int (first pos))
                      button (.getButton ev)   ; 1 = left   3 = right
                      positions (current-positions)]
                  (println (format "DEBUG keynumber %3d  button %d  positions %s " keynum button  positions))
                  (cond (and (= button 1) (< keynum (second positions)));; left button ~ lower limit
                        (let [mx (second positions)
                              mn (max 0 (min keynum (dec mx)))]
                          (.position! @p1* [[mn 0]])
                          (.position! @active-line* [[mn 0][mx 0]])
                          (.render drw))

                        (and (= button 3)(>= keynum (first positions))) ;; right button ~ upper limit
                        (let [mn (first positions)
                              mx (max (inc mn) (min keynum 1128))]
                          (.position! @p2* [[mx 0]])
                          (.position! @active-line* [[mn 0][mx 0]])
                          (.render drw)))
                  ))))
  drw))
  

    
(def d (keyscale))

(def f (ss/frame :title "Key Range Scale Test"
                 :content (.drawing-canvas d)
                 :on-close :dispose
                 :size [950 :by 200]))
(ss/show! f)
