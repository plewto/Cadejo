(ns cadejo.ui.indicator.dot-matrix
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.indicator.multisegment-lamp])
  (:require [seesaw.graphics :as ssg])
  (:require [seesaw.color :as ssc]))

;;        01234
;;     0  .....
;;     1  .....
;;     2  .....
;;     3  .....
;;     4  .....
;;     5  .....
;;     6  .....

(def default-colors [(ssc/color 192 192 192)
                     (ssc/color :blue)])

(def rows 7)
(def columns 5)
(def padding 4)                  ; padding around outside
(def gap 2)                      ; gap between points
(def dot-radius 2)


(def charmap* (atom {}))

(defn defrow [r template]
  (let [offset (* r columns)
        acc* (atom [])]
    (dotimes [i (count template)]
      (let [p (nth template i)]
        (if (not (zero? p))
          (swap! acc* (fn [n](conj n (+ offset i)))))))
    @acc*))

(defn defchar [c r0 r1 r2 r3 r4 r5 r6]
  (let [acc (flatten (list (defrow 0 r0)
                           (defrow 1 r1)
                           (defrow 2 r2)
                           (defrow 3 r3)
                           (defrow 4 r4)
                           (defrow 5 r5)
                           (defrow 6 r6)))]
    (swap! charmap* (fn [n](assoc n c acc)))))

(defchar \space
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0])

(defchar 0
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 1 1]
  '[1 0 1 0 1]
  '[1 1 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])
  
(defchar 1
  '[0 0 1 0 0]
  '[0 1 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 1 1 1 0])                           

(defchar 2
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 1 1 0]
  '[0 1 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 1])
    
(defchar 3
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 1 1 0]
  '[0 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar 4
  '[0 0 0 1 0]
  '[0 0 1 1 0]
  '[0 1 0 1 0]
  '[1 0 0 1 0]
  '[1 1 1 1 1]
  '[0 0 0 1 0]
  '[0 0 0 1 0])

(defchar 5
  '[1 1 1 1 1]
  '[1 0 0 0 0]
  '[1 1 1 1 0]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar 6
  '[0 0 1 1 0]
  '[0 1 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar 7
  '[1 1 1 1 1]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 0 1 0 0]
  '[0 1 0 0 0]
  '[0 1 0 0 0]
  '[0 1 0 0 0])

(defchar 8
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar 9
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 1]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 1 1 0 0])

(defchar \A
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1])


(defchar \B
  '[1 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 0])

(defchar \C
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar \D
  '[1 1 1 0 0]
  '[1 0 0 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 1 0]
  '[1 1 1 0 0])

(defchar \E
  '[1 1 1 1 1]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 1])

(defchar \F
  '[1 1 1 1 1]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0])

(defchar \G
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 0]
  '[1 0 1 1 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar \H
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1])

(defchar \I
  '[0 1 1 1 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 1 1 1 0])

(defchar \J
  '[0 0 1 1 1]
  '[0 0 0 1 0]
  '[0 0 0 1 0]
  '[0 0 0 1 0]
  '[1 0 0 1 0]
  '[1 0 0 1 0]
  '[0 1 1 0 0])

(defchar \K
  '[1 0 0 0 1]
  '[1 0 0 1 0]
  '[1 0 1 0 0]
  '[1 1 0 0 0]
  '[1 0 1 0 0]
  '[1 0 0 1 0]
  '[1 0 0 0 1])

(defchar \L
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 1])

(defchar \M
  '[1 0 0 0 1]
  '[1 1 0 1 1]
  '[1 0 1 0 1]
  '[1 0 1 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1])

(defchar \N
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 0 0 1]
  '[1 0 1 0 1]
  '[1 0 0 1 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1])

(defchar \O
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar \P
  '[1 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0])

(defchar \Q
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 1 0 1]
  '[0 1 1 1 0]
  '[0 0 0 0 1])

(defchar \R
  '[1 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 1 1 1 0]
  '[1 0 1 0 0]
  '[1 0 0 1 0]
  '[1 0 0 0 1])

(defchar \S
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 0]
  '[0 1 1 1 0]
  '[0 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar \T
  '[1 1 1 1 1]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0])

(defchar \U
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 1 1 0])

(defchar \V
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 0 1 0]
  '[0 0 1 0 0])

(defchar \W
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 1 0 1]
  '[1 0 1 0 1]
  '[1 0 1 0 1]
  '[0 1 0 1 0])

(defchar \X
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 0 1 0]
  '[0 0 1 0 0]
  '[0 1 0 1 0]
  '[1 0 0 0 1]
  '[1 0 0 0 1])

(defchar \Y
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[1 0 0 0 1]
  '[0 1 0 1 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0])

(defchar \Z
  '[1 1 1 1 1]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 0 1 0 0]
  '[0 1 0 0 0]
  '[1 0 0 0 0]
  '[1 1 1 1 1])

(defchar \.
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 1 1 0]
  '[0 0 1 1 0])

(defchar \,
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 1 0]
  '[0 0 0 1 0]
  '[0 0 1 0 0])

(defchar \!
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 0 0 0]
  '[0 0 1 0 0])

(defchar \#
  '[0 1 0 1 0]
  '[1 1 1 1 1]
  '[0 1 0 1 0]
  '[0 1 0 1 0]
  '[0 1 0 1 0]
  '[1 1 1 1 1]
  '[0 1 0 1 0])

(defchar \?
  '[0 1 1 1 0]
  '[1 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 0 1 0 0]
  '[0 0 0 0 0]
  '[0 0 1 0 0])

(defchar \*
  '[0 0 1 0 0]
  '[1 0 1 0 1]
  '[0 1 1 1 0]
  '[0 0 1 0 0]
  '[0 1 1 1 0]
  '[1 0 1 0 1]
  '[0 0 1 0 0])

(defchar \(
  '[0 0 1 0 0]
  '[0 1 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[0 1 0 0 0]
  '[0 0 1 0 0])

(defchar \)
  '[0 0 1 0 0]
  '[0 0 0 1 0]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 0 1 0 0])

(defchar \[
  '[1 1 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 0 0 0 0]
  '[1 1 0 0 0])

(defchar \]
  '[0 0 0 1 1]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 0 1]
  '[0 0 0 1 1])

(defchar \-
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 1 1 1 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[0 0 0 0 0])

(defchar \+
  '[0 0 0 0 0]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[1 1 1 1 1]
  '[0 0 1 0 0]
  '[0 0 1 0 0]
  '[0 0 0 0 0])

(defchar \/
  '[0 0 0 0 0]
  '[0 0 0 0 1]
  '[0 0 0 1 0]
  '[0 0 1 0 0]
  '[0 1 0 0 0]
  '[1 0 0 0 0]
  '[0 0 0 0 0])

(defchar \=
  '[0 0 0 0 0]
  '[0 0 0 0 0]
  '[1 1 1 1 1]
  '[0 0 0 0 0]
  '[1 1 1 1 1]
  '[0 0 0 0 0]
  '[0 0 0 0 0])



(defn dot-matrix 
  ([c]
     (let [dm (dot-matrix)]
       (.set-char! dm c)
       (.on! dm)
       dm))
  ([]
     (let [pan-width (+ (* 2 padding)
                        (* columns dot-radius)
                        (* (dec columns) gap))
           pan-height (+ (* 2 padding)
                         (* rows dot-radius)
                         (* (dec rows) gap))
           segments (let [acc* (atom [])
                          delta (+ dot-radius gap)]
                      (dotimes [r rows]
                        (let [y (+ padding (* r delta))]
                          (dotimes [c columns]
                            (let [x (+ padding (* c delta))]
                              (swap! acc* (fn [n](conj n (ssg/circle x y dot-radius))))))))
                      @acc*)
           on-segments* (atom nil)
           colors* (atom default-colors)
           current-color* (atom (last default-colors))
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
                    (keys @charmap*))
                  
                  (set-char! [this c]
                    (let [pat (get @charmap* c)]
                      (if pat 
                        (do 
                          (reset! on-segments* [])
                          (doseq [s pat]
                            (swap! on-segments* (fn [n](conj n (nth segments s)))))
                          (.repaint @canvas*))
                        (umsg/warning "dot-matrix can not display %s" c))))
                  
                  (elements [this]
                    segments)
                  
                  (on-elements [this]
                    @on-segments*)
                  
                  (lamp-canvas! [this jc]
                    (reset! canvas* jc))
                  
                  (lamp-canvas [this]
                    @canvas*))]
       (.lamp-canvas! lamp (cadejo.ui.indicator.multisegment-lamp/multisegment-canvas lamp))
       lamp)))        
