(println "--> sgwr.elements.drawing")
(ns sgwr.elements.drawing
  (:use [cadejo.util.trace])
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
           javax.swing.JPanel))


(def zoom-ratio* (atom 2/3))

(defprotocol SgwrDrawing

  (root 
    [this])

  (widget-root
    [this])

  (canvas-bounds
    [this])

  (canvas 
    [this])

  ;; bg arg may be either color or BufferedImage
  ;;
  (background!
    [this bg])

  (render
    [this])

  (render-node 
    [this g2d element])

  ;; Forces render and replaces background with current image 
  ;; Removes all elements from root group with exception of widget-group
  ;; Removes all elements from widget group
  ;; Returns BufferedImage
  (flatten!
    [this])

  (image
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
        widget-root (sgwr.elements.group/group root-group :id :widget-root)
        active-widget* (atom nil)
        background-color* (atom (uc/color :black))
        background-image* (atom nil)
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

              (widget-root [this] widget-root)

              (canvas-bounds [this] (.canvas-bounds cs))

              (canvas [this] @cpan*)

              (background! [this bg]
                (let [bgt (type bg)]
                  (cond (= bgt java.awt.image.BufferedImage)
                        (reset! background-image* bg)
                        
                        :default
                        (reset! background-color* (uc/color bg)))))

              (render [this]
                (if @enable-render*
                  (let [image @image*
                        g2d (.createGraphics image)]
                    (if @background-image*
                      (do
                        (.drawImage g2d @background-image* constants/null-transform-op 0 0)
                        )
                      (do 
                        (.setColor g2d @background-color*)
                        (.fillRect g2d 0 0 width height)
                        ))
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
                              (if (and (.filled? element)(not (= (.filled? element) :no)))
                                (.fill g2d shape)
                                (.draw g2d shape))))))))

              (flatten! [this]
                (.render this)
                (let [bg (.image this)]
                  (.remove-children! root-group (fn [q](not (= (.get-property q :id) :widget-root))))
                  (.remove-children! widget-root)
                  (.background! this bg)
                  bg))

              (image [this]
                (.render this)
                @image*)
              
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

    (.addMouseMotionListener 
     @cpan* 
     (let [not-neg? (fn [n](>= n 0))
           motion-handler (fn [ev drag-flag]
                            (let [cs (.coordinate-system widget-root)
                                  p [(.getX ev)(.getY ev)]
                                  q (.inv-map cs p)]
                              (reset! mouse-dragged* drag-flag)
                              (reset! mouse-position* p)
                              (let [previous @active-widget*
                                    i* (atom (dec (.child-count widget-root)))]
                                (reset! active-widget* nil)
                                
                                (while (and (not @active-widget*)(not-neg? @i*))
                                  (let [c (nth (.children widget-root) @i*)]
                                    (if (.contains? c q)
                                      (reset! active-widget* c))
                                    (swap! i* dec)))
                                
                                (cond (= previous @active-widget*) ; no change
                                      nil
                                      
                                      (and previous (not @active-widget*)) ; mouse exited widget
                                      (let [exfn (.get-property previous :action-mouse-exited)]
                                        (.restore-attributes! previous)
                                        (exfn previous ev)
                                        (.render drw))
                                      
                                      (and (not previous) @active-widget*) ; mouse entered widget
                                      (let [enfn (.get-property @active-widget* :action-mouse-entered)]
                                        (.use-temp-attributes! @active-widget* :rollover)
                                        (enfn @active-widget* ev)
                                        (.render drw)
                                        
                                        :default ; no change
                                        nil)))))]
       (proxy [MouseMotionListener][]
         (mouseDragged [ev]
           (motion-handler ev true)
           (let [active @active-widget*]
             (if active
               (let [mfn (.get-property active :action-mouse-dragged (fn [& _]))]
                 (mfn active ev)))))
         
         (mouseMoved [ev]
           (motion-handler ev false)
           (let [active @active-widget*]
             (if active
               (let [mfn (.get-property active :action-mouse-moved (fn [& _]))]
                 (mfn active ev))))) )))

    (.addMouseListener @cpan* (let [default-action (fn [& _])]
                                (proxy [MouseListener][]
                                  
                                  (mouseEntered [_])
                                  (mouseExited [_])
                                
                                  (mouseClicked [ev]
                                    (let [active @active-widget*]
                                      (if active
                                        (let [cfn (.get-property active
                                                                 :action-mouse-clicked
                                                                 default-action)]
                                          (cfn active ev)))))
                                  
                                  (mousePressed [ev]
                                    (let [active @active-widget*]
                                      (if active
                                        (let [cfn (.get-property active
                                                                 :action-mouse-pressed
                                                                 default-action)]
                                          (cfn active ev)))))

                                  (mouseReleased [ev]
                                    (let [active @active-widget*]
                                      (if active
                                        (let [cfn (.get-property active
                                                                 :action-mouse-released
                                                                 default-action)]
                                          (cfn active ev))))) )))
                                  
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
