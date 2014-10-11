;;;; ISSUE BUGGY 

(ns cadejo.instruments.algo.mute-editor
  (:require [sgwr.drawing :as sgwr])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.MouseListener)
)

(def paper-color :black)
(def line-color :gray)
(def op-color :blue)
(def mute-color :gray)

(def bw 60)                             ; button width
(def bw2 (* 1/2 bw))                    ; half button width
(def bh 60)                             ; button height
(def bh2 (* 1/2 bh))                    ; half button height

(def y0 0)
(def y1 (+ y0 bh2))
(def y2 (+ y1 bh))
(def y3 (+ y2 bh2))
(def y4 (+ y3 bh2))
(def y5 (+ y4 bh))
(def y6 (+ y5 bh2))
(def y7 (+ y6 bh2))
(def y8 (+ y7 bh2))
(def y9 (+ y8 bh2))
(def y10 (+ y9 bh2))
(def y11 (+ y10 bh2))

(def x0 0)
(def x1 (+ x0 bw2))
(def x2 (+ x1 bw2))
(def x3 (+ x2 bw2))
(def x4 (+ x3 bw2))
(def x5 (+ x4 bw2))
(def x6 (+ x5 bw2))
(def x7 (+ x6 bw2))
(def x8 (+ x7 bw2))
(def x9 (+ x8 bw2))
(def x10 (+ x9 bw2))
(def x11 (+ x10 bw2))
(def x12 (+ x11 bw2))
(def x13 (+ x12 bw2))
(def x14 (+ x13 bw2))
(def x15 (+ x14 bw2))
(def x16 (+ x15 bw2))

(def w x16)
(def h y11)


(defn- op-button [drw op x y]
  (.color! drw op-color)
  (.width! drw 1)
  (let [b-main (.rectangle! drw [x y][(+ x bw)(- y bh)])
        tx-main (let [x-offset (- bw2 8)
                      y-offset (* -1  bh2)]
                  (.width! drw 3)
                  (.text! drw [(+ x x-offset)(+ y y-offset)] (str op)))
        [b-mute tx-mute] (let [width (* 1/3 bw)
                               height (* 1/3 bh)
                               x1 (+ x 4)
                               x2 (+ x1 width)
                               y1 (- y 4)
                               y2 (- y1 height)
                               txx (- (/ (+ x1 x2) 2) 5)
                               tyy (+ (/ (+ y1 y2) 2) 6)
                               tx (do (.width! drw 2)
                                      (.text! drw [txx tyy] "M"))
                               b (do (.width! drw 1)
                                     (.rectangle! drw [x1 y1][x2 y2]))]
                           [b tx])
        [b-edit tx-edit] (let [width (* 3/8 bw)
                               height (* 1/3 bh)
                               x1 (+ x (* 5/8 bw))
                               x2 (- (+ x bw) 4)
                               y1 (- y 4)
                               y2 (- y1 height)
                               txx (- (/ (+ x1 x2) 2) 4)
                               tyy (+ (/ (+ y1 y2) 2) 6)
                               tx (do (.width! drw 2)
                                      (.text! drw [txx tyy] "E"))
                               b (do (.width! drw 1)
                                     (.rectangle! drw [x1 y1][x2 y2]))]
                           [b tx])
        mute* (atom false)
        boxes [b-main tx-main b-mute tx-mute b-edit tx-edit]]
    (doseq [obj boxes]
      (let [att (.attributes obj)]
        (.put-property! att :op op)
        (.put-property! att :is-muted false)))
    (.put-property! (.attributes b-mute) :mute true)
    (.put-property! (.attributes b-edit) :edit true)
    {:op op :mute-flag mute* :boxes boxes}))
  
(defn- vline [drw x y1 y2]
  (.color! drw line-color)
  (.width! drw 1)
  (.line! drw [x y1][x y2]))

(defn- hline [drw y x1 x2]
  (.color! drw line-color)
  (.width! drw 1)
  (.line! drw [x1 y][x2 y]))

(defn- box-contains? [box point]
  (let [cp (.construction-points box)
        [x0 y0](first cp)
        [x1 y1](second cp)
        xmin (min x0 x1)
        xmax (max x0 x1)
        ymin (min y0 y1)
        ymax (max y0 y1)
        [x y] point]
    (and (>= x xmin)(<= x xmax)(>= y ymin)(<= y ymax))))

(defn- mute-box-pressed? [op point]
  (let [mb (nth (:boxes op) 2)]
    (box-contains? mb point)))

(defn- edit-box-pressed? [op point]
  (let [eb (nth (:boxes op) 4)]
    (box-contains? eb point)))

(defn overview-editor [performance ied opmap]
  (let [drw (sgwr.drawing/native-drawing w h)]
    (.paper! drw paper-color)
    (vline drw x2 y2 y4)
    (vline drw x2 y5 y7)
    (vline drw x2 y9 y10)
    (vline drw x5 y5 y6)
    (vline drw x7 y6 y7)
    (vline drw x9 y5 y6)
    (vline drw x7 y9 y10)
    (vline drw x9 y3 y4)
    (vline drw x11 y3 y8)
    (vline drw x13 y3 y4)
    (vline drw x13 y5 y7)
    (vline drw x13 y9 y10)
    (vline drw x15 y3 y6)
    (hline drw y10 x2 x13)
    (hline drw y8 x8 x11)
    (hline drw y6 x5 x9)
    (hline drw y6 x13 x15)
    (hline drw y3 x9 x11)
    (hline drw y3 x13 x15)
    (.freeze! drw)
    (let [op1 (op-button drw 1 x1 y9)
          op2 (op-button drw 2 x1 y5)
          op3 (op-button drw 3 x1 y2)
          op4 (op-button drw 4 x6 y9)
          op5 (op-button drw 5 x4 y5)
          op6 (op-button drw 6 x8 y5)
          op7 (op-button drw 7 x12 y9)
          op8 (op-button drw 8 x12 y5)
          operators [op1 op2 op3 op4 op5 op6 op7 op8]
          frame (ss/frame :title "Algo overview"
                          :content (.drawing-canvas drw)
                          :on-close :hide
                          :size [400 :by 400])]
      (.add-mouse-listener! drw (proxy [MouseListener][]
                                  (mouseClicked [_])
                                  (mouseEntered [_])
                                  (mouseExited [_])
                                  (mouseReleased [_])
                                  (mousePressed [ev]
                                    (let [x (.getX ev)
                                          y (.getY ev)
                                          p [x y]
                                          mute* (atom nil)
                                          edit* (atom nil)]
                                      (dotimes [n 8]
                                        (let [op (nth operators n)]
                                          (if (mute-box-pressed? op p)
                                            (reset! mute* (inc n)))
                                          (if (edit-box-pressed? op p)
                                            (reset! edit* (inc n)))))
                                      (if @mute*
                                        (let [opnum @mute*
                                              op (nth operators (dec opnum))
                                              flag (not @(:mute-flag op))
                                              color (if flag mute-color op-color)]
                                          (reset! (:mute-flag op) flag)
                                          (doseq [q (:boxes (nth operators op))]
                                            (.color! (.attribtues q) color))
                                          (.render drw)))
                                      (if @edit*
                                        (do 
                                          (println "edit " @edit*)
                                          ))
                                      ))))
      (ss/show! frame)
      frame)))
                                      
      
    

;;;; ttst test test test 
;;;;

(overview-editor nil nil nil)
