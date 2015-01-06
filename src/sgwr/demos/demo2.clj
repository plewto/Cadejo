;; demos2 
;; Uses native coordinate system, displays basic elements.
;;

(ns sgwr.demos.demo2
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.drawing :as drw])
  (:require [sgwr.elements.group :as grp])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss])
  (:import  java.awt.event.MouseMotionListener
            java.awt.event.MouseListener))


(def set-attributes! sgwr.elements.element/set-attributes!)

(def drw (drw/cartesian-drawing 600 600 [-100 -100][100 110]))
(def root (.root drw))
(text/text root [-85 105] "Sgwr Demo 2 ~ Cartesian Drawing [-100 -100][100 100]" :size 8)
(line/line root [-90 101][90 101])

(def group-text (grp/group root :color [64 196 16] :size 7 :style 1))
(defn text-obj [pos, txt]
  (text/text group-text pos txt :color nil :style nil :size nil))

;; Draw grid lines
(let [c1 (uc/color [64 16 196])
      c2 (uc/darker c1 0.35)
      c3 (uc/modify (uc/darker c2) :h 0.05)
      grp (grp/group root :id :axis-group :color c1)]
  (doseq [p (range 10 100 10)]
    (line/line grp [p -95][p 95] :color c2)
    (line/line grp [(- p) -95][(- p) 95] :color c2)
    (line/line grp [-95 p][95 p] :color c2)
    (line/line grp [-95 (- p)][95 (- p)] :color c2))
  (line/line grp [-95 0][95 0] :color c1)
  (line/line grp [0 -95][0 95] :color c1)
  (line/line grp [-100 -100][100  100] :color c3)
  (line/line grp [-100  100][100 -100] :color c3)
  (line/line grp [0  100][100 0] :color c3)
  (line/line grp [0 -100][100 0] :color c3)
  (line/line grp [0  100][-100 0] :color c3)
  (line/line grp [0 -100][-100 0] :color c3))

; Label quadrants
(let [c1 (uc/color [196 100 116 128])
      st 2
      sz 12
      grp (grp/group root :id :Quadrants)]
  (text/text grp [ 90  85] "I"   :size sz :style st :color c1) 
  (text/text grp [-90  85] "II"  :size sz :style st :color c1)
  (text/text grp [-92 -90] "III" :size sz :style st :color c1) 
  (text/text grp [ 85 -90] "IV"  :size sz :style st :color c1))

;; Quadrant I
;; View scale
;;
(def grp1 (grp/group root :id :quad1))
(let [a 2
      b -50
      linfn (fn [x](+ b (* a x)))]
  (line/line grp1 [30 (linfn 30)][70 (linfn 70)] :color :red)
  (doseq [x (range 30 80 10)]
    (point/point grp1 [x (linfn x)] :color :yellow :size 2.0))
  (circle/circle grp1 [50 35][80 65] :color :blue)
  (rect/rectangle grp1 [50 50][80 80] :color :green)
  (text/text grp1 [43 52] "Point objects are not effected by view changes" :size 2))

;; Quadrant II
;; Mouse Listener 
;;
(def grp2 (grp/group root :id :quad2))
(let [gray (uc/color :gray)
      yellow (uc/color :yellow)
      yellow2 (uc/color [255 255 0 32])
      c1 (circle/circle grp2 [-70 30][-50 50] :color :gray)
      r1 (rect/rectangle grp2 [-50 50][-20 80] :color :gray)
      r2 (rect/rectangle grp2 [-90 10][-10 90] :style :dash :color :gray)
      tx (text/text grp2 [-80 15] "Mouse Boundry Detection") ]

  (.add-mouse-motion-listener drw (proxy [MouseMotionListener][]
                                    (mouseDragged [_])
                                    (mouseMoved [ev]
                                      (let [pos (.mouse-where drw)
                                            dr1 (.distance r1 pos)
                                            dr2 (.distance r2 pos)
                                            dc (.distance c1 pos)]
                                        (.put-property! r1 :filled (if (zero? dr1) true false))
                                        (.put-property! r1 :color (if (zero? dr1) yellow2 gray))
                                        (.put-property! r2 :color (if (zero? dr2) yellow gray))
                                        (.put-property! c1 :hidden (if (zero? dc) true false))
                                        (.render drw))))))
;; Quadrant III
;; Mouse Listener 
;;
(def grp3 (grp/group root :id :quad3))
(let [colors (let [acc* (atom [])
                   dr 2
                   dg 2
                   db 16
                   da 16]
               (doseq [p (range 16)]
                 (let [r (min 255 (* p dr))
                       g (min 255 (* p dg))
                       b (min 255 (* p db))
                       a (min 255 (* p da))
                       c (uc/color [r g b a])]
                   (swap! acc* (fn [q](conj q c)))))
               @acc*)
      c1 (circle/circle grp3 [-80 -80][-20 -20] :color (nth colors 0) :fill true)
      c2 (circle/circle grp3 [-80 -80][-20 -20] :color [255 255 0 64] :width 2)
      p1 (point/point grp3 [-50 -50] :color :yellow :style :x :size 2)
      tx (text/text grp3 [-80 -90] "Mouse Proximity Detection" :color :white :size 6)]
  (.add-mouse-motion-listener drw (proxy [MouseMotionListener][]
                                    (mouseDragged [_])
                                    (mouseMoved [ev]
                                      (let [d (int (* 1/2 (.distance p1 (.mouse-where drw))))]
                                        (if (< d 16)
                                          (do 
                                            (.put-property! c1 :color (nth (reverse colors) d))
                                            (.render drw))))))))

;; Quadrant IV Attributes
;;
(def grp4 (grp/group root :id :quad4))
(let [p1 (point/point grp4 [10 -50] :color :gray :style :dot :size 3)
      r1 (rect/rectangle grp4 [20 -70][40 -30] :color :gray :style :solid :width 1 :fill false)
      c1 (circle/circle grp4 [50 -60][70 -40] :color :gray :style :solid :width 1 :fill false)
      tx (text/text grp4 [50 -30] "SGWR" :color :gray :style 0 :size 8)]
  (set-attributes! p1 :alpha :color :blue    :style :triangle  :size 2)
  (set-attributes! p1 :beta  :color :red     :style :box       :size 2)
  (set-attributes! p1 :gamma :color :green   :style :x         :size 2)
  (set-attributes! r1 :alpha :color :blue    :style :solid     :width 1)
  (set-attributes! r1 :beta  :color :green   :style :center    :width 1)
  (set-attributes! r1 :gamma :color :red     :style :solid     :width 4 :fill false)
  (set-attributes! c1 :alpha :color :blue    :style :solid    :width 1 :fill false)
  (set-attributes! c1 :beta  :color :yellow  :style :solid    :width 1 :fill true)
  (set-attributes! c1 :gamma :color :blue    :style :long-dash :width 1 :fill false) 
  (set-attributes! tx :alpha :color :blue    :style 1          :size 6 :hide false)
  (set-attributes! tx :beta  :color :red     :style 1          :size 6 :hide true)
  (set-attributes! tx :gamma :color :green   :style 10         :size 12 :hide false)
  (.use-attributes! grp4 :alpha))
        



;; ------------------------------------------------------------

(def lab-status (ss/label :text " "))
(defn status [msg] (ss/config! lab-status :text (format " %s" msg)))


(def jb-in (ss/button :text "In" :id :in))
(def jb-out (ss/button :text "Out" :id :out))
(def jb-restore (ss/button :text "Restore" :id :restore))
(def jb-q1 (ss/button :text "I" :id :q1 ))
(def jb-q2 (ss/button :text "II" :id :q2))
(def jb-q3 (ss/button :text "III" :id :q3))
(def jb-q4 (ss/button :text "IV" :id :q4))
(def view-buttons [jb-in jb-out jb-restore jb-q1 jb-q2 jb-q3 jb-q4])
(doseq [jb view-buttons]
  (ss/listen jb :action (fn [ev]
                          (let [src (.getSource ev)
                                id (ss/config src :id)]
                            (doseq [jb view-buttons](ss/config! jb :enabled? false))
                            (cond (= id :in)(do (.zoom-in drw)(status "Zoom in"))
                                  (= id :out)(do (.zoom-out drw)(status "Zoom out"))
                                  (= id :restore)(do (.restore-view drw)(status "Restore View"))
                                  (= id :q1)(do (.set-view drw [[-1 -1][101 101]])(status "View Quadrant I"))
                                  (= id :q2)(do (.set-view drw [[-101 -1][1 101]])(status "View Quadrant II"))
                                  (= id :q3)(do (.set-view drw [[-101 -101][1 1]])(status "View Quadrant III"))
                                  (= id :q4)(do (.set-view drw [[-1 -101][101 1]])(status "View Quadrant IV")))
                            (Thread/sleep 400)
                            (doseq [jb view-buttons](ss/config! jb :enabled? true))))))
(def pan-q (ss/grid-panel :rows 2 :columns 2 :items [jb-q2 jb-q1 jb-q3 jb-q4]))
(def pan-zoom (ss/grid-panel :rows 1 :items [jb-in jb-out jb-restore]))
(def pan-view (ss/border-panel :north (ss/label :text "View" :halign :center)
                               :center pan-q
                               :south pan-zoom))


(def bgroup (ss/button-group))
(def tb-alpha (ss/radio :text "Alpha" :group bgroup :id :alpha :selected? true))
(def tb-beta  (ss/radio :text "Beta"  :group bgroup :id :beta))
(def tb-gamma (ss/radio :text "Gamma" :group bgroup :id :gamma))
(def tb-delta (ss/radio :text "Delta" :group bgroup :id :default))
(def pan-style (ss/border-panel
                :north (ss/label :text "Style Quadrant IV" :halign :center)
                :center (ss/grid-panel :rows 2 :columns 2 :items [tb-alpha tb-beta
                                                                  tb-gamma tb-delta])))
(doseq [tb [tb-alpha tb-beta tb-gamma tb-delta]]
  (ss/listen tb :action (fn [ev]
                          (let [src (.getSource ev)
                                id (ss/config src :id)]
                            (.use-attributes! grp4 id)
                            (.render drw)
                            (status (str id))))))

(def pan-south (ss/border-panel 
                :west pan-view
                :east pan-style
                :south lab-status))
                

(def pan-main (ss/border-panel :center (.canvas drw)
                               :south pan-south))

(.render drw)
(def f (ss/frame :title "Sgwr Demo 1"
                 :content pan-main
                 :on-close :dispose
                 :size [610 :by 750]))

(ss/show! f)

(.add-mouse-motion-listener drw (proxy [MouseMotionListener][]
                                  (mouseMoved [_]
                                    (let [p (.mouse-where drw)]
                                      (status (format " %s" p))))
                                  (mouseDragged [_] )))

;; (defn rl [](use 'sgwr.demos.demo2 :reload))
;; (defn rla [](use 'sgwr.demos.demo2 :reload-all))
;; (defn exit [](System/exit 0))


