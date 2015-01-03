(println "--> sgwr.elements.drawing")
(ns sgwr.elements.drawing
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group])
  (:require [sgwr.elements.text])
  (:require [sgwr.cs.native :as native-cs])
  (:require [sgwr.cs.cartesian :as cartesian-cs])
  (:require [sgwr.cs.polar :as polar-cs])
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
                            (sgwr.elements.text/render-text element g2d)
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


(defn polar-drawing 
  ([w r units]
   (let [cs (polar-cs/polar-coordinate-system w r :units units)]
     (drawing cs)))
  ([w r]
   (polar-drawing w r :rad)))
 

;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST 


(defn rl [] (use 'sgwr.elements.drawing :reload))
(defn rla [] (use 'sgwr.elements.drawing :reload-all))
(defn exit [] (System/exit 0))

(require '[seesaw.core :as ss])
(require '[sgwr.elements.group :as grp])
(require '[sgwr.elements.point :as point])
(require '[sgwr.elements.line :as line])
(require '[sgwr.elements.rectangle :as rect])
(require '[sgwr.elements.circle :as circle])

(def drw (cartesian-drawing 600 600 [-10 -10][10 10]))
(def root (.root drw))
(.color! root [64 64 96])
(.width! root 1)
(.style! root 0)
(.fill! root false)
(.hide! root false)
(line/line root [-10 0][10 0])
(line/line root [0 -10][0 10])
(rect/rectangle root [-1 -1][1 1])

(def g1 (grp/group root :g1))
(.set-local-attributes! g1)
(.color! g1 :green )
(.style! g1 :solid)
(.width! g1 1)
(.hide! g1 true)
(point/point g1 [-1 -1])
(point/point g1 [1 1])
(circle/circle g1 [-1 -1][1 0.5])


(def txt "The Quick Brown Fox")

(def g2 (grp/group root :g2))
(def tx2 (sgwr.elements.text/text g2 [0 0] txt))
(.style! g2 :sans)
(.width! g2 8)

(def g3 (grp/group root :g3))
(def tx3 (sgwr.elements.text/text g3 [0 1] txt))
(.style! g3 :sans-bold-italic)
(.width! g3 8)


;; (def c1 (circle/circle g1 [-1 -1] [1 1]))


(def jb-render (ss/button :text "Render"))
(def jb-alpha (ss/button :text "Alpha"))
(def jb-beta (ss/button :text "Beta"))
(def jb-gamma (ss/button :text "Gamma"))
(def jb-zoom-in (ss/button :text "Zoom in"))
(def jb-zoom-out (ss/button :text "Zoom out"))

(ss/listen jb-render :action (fn [_]
                               (.render drw)
                               ;(.dump root)
                               ))

(ss/listen jb-alpha :action (fn [_]
                              (.use-attributes! root :alpha)
                              (.render drw)))

(ss/listen jb-beta :action (fn [_]
                              (.use-attributes! root :beta)
                              (.render drw)))


(ss/listen jb-gamma :action (fn [_]
                              (.use-attributes! root :gamma)
                              (.render drw)))

(ss/listen jb-zoom-in :action (fn [_](.zoom-in drw)))
(ss/listen jb-zoom-out :action (fn [_](.zoom-out drw)))


(def pan-south (ss/horizontal-panel :items [jb-render jb-alpha jb-beta jb-gamma jb-zoom-in jb-zoom-out]))

(def pan-main (ss/border-panel :center (.canvas drw)
                               :south pan-south))

(def f (ss/frame :title "SGWR Test"
                 :on-close :dispose
                 :content pan-main
                 :size [650 :by 650]))
(.render drw)
(ss/show! f)

