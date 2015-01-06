(println "--> sgwr.elements.drawing")
(ns sgwr.elements.drawing
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group])
  (:require [sgwr.elements.text])
  (:require [sgwr.elements.image])
  (:require [sgwr.cs.native :as native-cs])
  (:require [sgwr.cs.cartesian :as cartesian-cs])
  (:require [sgwr.cs.polar :as polar-cs])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.util.utilities :as utilities])
  (:require [seesaw.core :as ss])
  (:import 
           java.awt.image.BufferedImage
           java.awt.event.MouseMotionListener
           java.awt.event.MouseListener
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
    [this as-sgwr-element]
    [this])

  (zoom-in
    [this])

  (zoom-out
    [this])

  (set-view
    [this v])

  (restore-view
    [this])

  (add-mouse-motion-listener
    [this mml])

  (remove-mouse-motion-listener
    [this mml])

  (add-mouse-listener
    [this ml])

  (remove-mouse-listener
    [this ml])

  (mouse-where
    [this raw]
    [this])
  
  (mouse-pressed-where
    [this raw]
    [this])

  (mouse-released-where
    [this raw]
    [this])

  )
    

(defn drawing [cs]
  (let [root-group (sgwr.elements.group/group nil :id :root)
        background-color* (atom (uc/color :black))
        enable-render* (atom true)
        [width height](.canvas-bounds cs)
        image* (atom (BufferedImage. width height BufferedImage/TYPE_INT_ARGB))
        cpan* (atom nil)
        mouse-dragged* (atom false)            ; true if mouse position results from drag
        mouse-position* (atom [0 0])           ; as pixel [row col]
        mouse-pressed-position* (atom [0 0])   ; as pixel [row col]
        mouse-released-position* (atom [0 0])  ; as pixel [row col]
        drw (reify SgwrDrawing

              (root [this] root-group)

              (canvas-bounds [this] (.canvas-bounds cs))

              (canvas [this] @cpan*)

              (background! [this bg]
                (reset! background-color* (uc/color bg)))

              (render [this]
                (if @enable-render*
                  (let [image @image*
                        g2d (.createGraphics image)]
                    (.setColor g2d @background-color*)
                    (.fillRect g2d 0 0 width height)
                    (doseq [e (.children root-group)]
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
                            (sgwr.elements.image/render-image element g2d)
                            )
                          :default
                          (do (.setStroke g2d (ustroke/stroke element))
                              (if (.filled? element)
                                (.fill g2d shape)
                                (.draw g2d shape))))))))
              (image [this]
                (.render this)
                @image*)
              
              (image [this as-sgwr-element]
                (if (not as-sgwr-element)
                  (.image this)
                  (let [[w h](.canvas-bounds this)
                        si (sgwr.elements.image/image nil [0 0] w h)]
                    (.put-property! si :image (.image this))
                    si)))

              (zoom-in [this]
                (.zoom! cs @zoom-ratio*)
                (.render this))

              (zoom-out [this]
                (.zoom! cs (/ 1.0 @zoom-ratio*))
                (.render this))

              (set-view [this v]
                (.set-view! cs v)
                (.render this))
              
              (restore-view [this]
                (.restore-view! cs)
                (.render this))

              (add-mouse-motion-listener [this mml]
                (.addMouseMotionListener @cpan* mml))

              (remove-mouse-motion-listener [this mml]
                (.removeMoseMotionListener @cpan* mml))

              (add-mouse-listener [this ml]
                (.addMouseListener @cpan* ml))

              (remove-mouse-listener [this ml]
                (.removeMouseListener @cpan* ml))

              (mouse-where [this raw]
                (if raw @mouse-position* (.inv-map cs @mouse-position*)))

              (mouse-where [this]
                (.mouse-where this false))

              (mouse-pressed-where [this raw]
                (if raw @mouse-pressed-position* (.inv-map cs @mouse-pressed-position*)))

              (mouse-pressed-where [this]
                (.mouse-pressed-position this false))

              
              (mouse-released-where [this raw]
                (if raw @mouse-released-position* (.inv-map cs @mouse-released-position*)))

              (mouse-released-where [this]
                (.mouse-released-where this false))
              )]

    (.put-property! root-group :drawing drw)
    (reset! cpan* (proxy [JPanel][true]
                    (paint [g]
                      (.drawImage g @image* constants/null-transform-op 0 0))))
    (ss/config! @cpan* :size [width :by height])
    (.set-coordinate-system! root-group cs)
    (.addMouseMotionListener @cpan* (proxy [MouseMotionListener][]
                                      (mouseDragged [ev]
                                        (reset! mouse-dragged* true)
                                        (reset! mouse-position* [(.getX ev)(.getY ev)]))
                                      
                                      (mouseMoved [ev]
                                        (reset! mouse-dragged* false)
                                        (reset! mouse-position* [(.getX ev)(.getY ev)]))))
    (.addMouseListener @cpan* (proxy [MouseListener][]
                                (mouseClicked [_])
                                (mouseEntered [_])
                                (mouseExited [_])
                                (mousePressed [ev]
                                  (reset! mouse-pressed-position* [(.getX ev)(.getY ev)]))
                                (mouseReleased [ev]
                                  (reset! mouse-released-position* [(.getX ev)(.getY ev)]))))

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
 
