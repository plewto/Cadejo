(println "--> sgwr.elements.drawing")
(ns sgwr.elements.drawing
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group])
  (:require [sgwr.cs.native :as native-cs])
  (:require [sgwr.cs.cartesian :as cartesian-cs])
  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.util.utilities :as utilities])
  (:import 
           java.awt.image.BufferedImage
           javax.swing.JPanel
           )
  )

(def zoom-ratio* (atom 2/3))


(defprotocol SgwrDrawing

  (root 
    [this])

  (canvas-bounds
    [this])

  (canvas 
    [this])

  (background!
    [this bg])

  (render
    [this])

  (render-node 
    [this g2d element])

  (image
    [this])

  (zoom-in
    [this])

  (zoom-out
    [this])

  )
    

(defn drawing [cs]
  (let [root-group (sgwr.elements.group/group :root)
        background-color* (atom (ucolor/color :black))
        enable-render* (atom true)
        [width height](.canvas-bounds cs)
        image* (atom (BufferedImage. width height BufferedImage/TYPE_INT_ARGB))
        cpan* (atom nil)
        drw (reify SgwrDrawing

              (root [this] root-group)

              (canvas-bounds [this] (.canvas-bounds cs))

              (canvas [this] @cpan*)

              (background! [this bg]
                (reset! background-color* (ucolor/color bg)))

              (render [this]
                (if @enable-render*
                  (let [image @image*
                        g2d (.createGraphics image)]
                    (.setColor g2d @background-color*)
                    (.fillRect g2d 0 0 width height)
                    (doseq [e (.children root)]
                      (.render-node this g2d e))
                    (.repaint @cpan*))))

              (render-node [this g2d element]
                (if (not (.is-leaf? element))
                  
                  (doseq [c (.children element)]
                    (.render-node this g2d c))
                  
                  (if (not (.hidden? element))
                    (let [etype (.element-type element)
                          shape (.shape element)]
                    (.setColor g2d (.color element))
                    (cond (= etype :point)
                          (do (.setStroke g2d ustroke/default-stroke)
                              (.draw g2d shape))
                          (= etype :text)
                          (do
                            ;; ISSUE: text render not implemented
                            )
                          (= etype :image)
                          (do
                            ;; ISSUE: image render not implemented
                            )
                          :default
                          (do (.setStroke g2d (ustroke/stroke element))
                              (if (.filled? element)
                                (.fill g2d shape)
                                (.draw g2d shape))))))))
              (image [this]
                @image*)
              
              (zoom-in [this]
                (.zoom! cs @zoom-ratio*)
                (.render this))

              (zoom-out [this]
                (.zoom! cs (/ 1.0 @zoom-ratio*))
                (.render this))

              )]
    (.set-local-attributes! root-group)
    (reset! cpan* (proxy [JPanel][true]
                    (paint [g]
                      (.drawImage g @image* constants/null-transform-op 0 0))))

    (.set-coordinate-system! root-group cs)
    drw))


(defn native-drawing [w h]
  (let [cs (native-cs/native-coordinate-system w h)]
    (drawing cs)))


(defn cartesian-drawing [w h p0 p1]
  (let [cs (cartesian-cs/cartesian-coordinate-system w h p0 p1)]
    (drawing cs)))

;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST 


(require '[seesaw.core :as ss])
(require '[sgwr.elements.group :as grp])
(require '[sgwr.elements.line :as line])
(require '[sgwr.elements.rectangle :as rect])

(def drw (cartesian-drawing 500 500 [-10 -10] [10 10]))
(def root (.root drw))

(.color! root :red)
(.width! root 1)
(.style! root 0)

(def a1 (line/line root [0 -10][0 10]))
(def a2 (line/line root [-10 0][10 0]))
(def r1 (rect/rectangle root [1 1][5 5]))
(.put-property! r1 :corner-radius 32)

(.put-property! a1 :id :a1)
(.put-property! a2 :id :a2)
(.put-property! r1 :id :r1)


(def g3 (grp/group root :g3))
(def a3 (line/line g3 [-2 -2][-4 -4]))
(def r3 (rect/rectangle g3 [-1 -1][-5 -5]))



(.put-property! a3 :id :a3)
(.put-property! r3 :id :r3)

(.set-local-attributes! g3)
(.set-local-attributes! a3)
(.set-local-attributes! r3)

(.add-attributes! g3 :alpha)
(.add-attributes! a3 :alpha)
(.add-attributes! r3 :alpha)
(.use-attributes! g3 :alpha)
(.color! a3 :blue)
(.color! r3 :green)

(.add-attributes! g3 :beta)
(.add-attributes! a3 :beta)
(.add-attributes! r3 :beta)
(.use-attributes! g3 :beta)
(.color! a3 :gray)
(.color! r3 :purple)



(def jb-render (ss/button :text "Render"))

(ss/listen jb-render :action (fn [_]
                               (.render drw)
                               (.dump root)
                               (println)))

(def pan-main (ss/border-panel :center (.canvas drw)
                               :south jb-render))

(def f (ss/frame :title "SGWR Test"
                 :on-close :dispose
                 :content pan-main
                 :size [500 :by 700]))

(ss/show! f)
