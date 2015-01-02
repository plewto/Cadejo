(println "--> sgwr.elements.drawing")
(ns sgwr.elements.drawing
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group])
  (:require [sgwr.cs.native :as native-cs])
  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.util.utilities :as utilities])
  (:import 
           java.awt.image.BufferedImage
           javax.swing.JPanel
           )
  )


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

;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST 


(require '[seesaw.core :as ss])
(require '[sgwr.elements.line :as line])

(def drw (native-drawing 400 600))2
(def root (.root drw))

(.color! root :red)
(.width! root 1)
(.style! root :dash)
(def a1 (line/line root [100 100][300 500]))


(def a2 (line/line root [100 300][300 100]))
(.color! a2 :blue)


(def jb-render (ss/button :text "Render"))
(ss/listen jb-render :action (fn [_](.render drw)))
(def pan-main (ss/border-panel :center (.canvas drw)
                               :south jb-render))

(def f (ss/frame :title "SGWR Test"
                 :on-close :dispose
                 :content pan-main
                 :size [500 :by 700]))

(ss/show! f)
