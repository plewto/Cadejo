(ns sgwr.elements.drawing
  "Defines SgwrDrawing protocol 
  A drawing is an extension of javax.swing.JPanel onto which 
  elements are rendered. All drawings contain a 'root' and a
  'widget' group (See sgwr.elements.group) which then contain all
  other drawing elements. 

  The segregation between widget and non-widget elements is to reduce
  the overhead of rendering complex drawings. Once a drawing has been
  rendered all static elements may be flattened into a single image
  which greatly reduces future render times."
 
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.circle])
  (:require [sgwr.elements.group])
  (:require [sgwr.elements.image])
  (:require [sgwr.elements.point])
  (:require [sgwr.elements.rectangle])
  (:require [sgwr.elements.text])
  (:require [sgwr.cs.native :as native-cs])
  (:require [sgwr.cs.cartesian :as cartesian-cs])
  (:require [sgwr.cs.polar :as polar-cs])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.util.utilities :as utilities])
  (:require [seesaw.core :as ss])
  (:import java.awt.image.BufferedImage
           java.awt.event.MouseMotionListener
           java.awt.event.MouseListener
           javax.swing.JPanel))


(def zoom-ratio* (atom 2/3))

(defprotocol SgwrDrawing

  (root 
    [this]
    "(root this)
     Returns the 'root' group all elements are ultimately placed into the
    root group.")

  (widget-root
    [this]
    "(widget-root this)
     Returns the group used for active widgets.")

  (canvas-bounds
    [this]
    "(canvas-bounds this)
     Returns a vector holding the dimensions of the canvas in pixels
     [width height]")

  (canvas 
    [this]
    "(canvas this)
     Returns the JPanel onto which drawings are rendered.")

  (background!
    [this bg]
    "(background! this bg)
     Sets the drawing background
     The bg argument may be a color or gradient (see sgwr.util.color) or
     may be an instance of java.awt.image.BufferedImage. If a BufferedImage
     is used it's size must match that of the drawing.")

  (render
    [this]
    "(render this)
     Render all objects to the drawing. 
     This method is ignored is rendering is not enabled.")
     

  (render-node 
    [this g2d element]
    "(render-node this g2d element)
     g2d - an instance of java.awt.Graphics2D
     Render specific element. 
     Do not call this method directly, instead use the render method.")

  (flatten!
    [this include-widgets]
    [this]
    "(flatten! this include-widgets)
     (flatten! this)
     Replace all drawing elements with a single BufferedImage 
     By default the widgets group is not included.
     render is implicitly called.
     Returns java.awt.image.BufferedImage")

  (image
    [this]
    "(image this)
     Returns instance of java.awt.image.BufferedImage
     with a static image of the drawing.
     The render method is implicitly called.")

  (zoom-in
    [this]
    "(zoom-in this) 
     Increase the drawing zoom level.
     Note not all coordinate systems support zoom.")

  (zoom-out
    [this]
    "(zoom-out this)
     Decrease the drawing zoom level.
     Note not all coordinate systems support zoom.")

  (set-view
    [this v]
    "(set-view this v)
     Sets the current view to v [[x0 y0][x1 y1]]
     Not all coordinate systems support changeable views.")

  (restore-view
    [this]
    "(restore-view this)
     Return view to default.")

  (add-mouse-motion-listener
    [this mml]
    "(add-mouse-motion-listener this mml)
     Adds a new MouseMotionListener to the canvas.
     Note that a MouseMotionListener is automatically added to the canvas.
     See mouse-where method.")

  (remove-mouse-motion-listener
    [this mml]
    "(remove-mouse-motion-listener this mml)")

  (add-mouse-listener
    [this ml]
    "(add-mouse-listener this ml)
     Adds a new MouseListener to the canvas
     Note that a MouseListener is automatically added.
     See mouse-pressed-where and mouse-release-where methods")

  (remove-mouse-listener
    [this ml]
    "(remove-mouse-listener this ml)")

  (mouse-where
    [this raw]
    [this]
    "(mouse-where this raw)
     (mouse-where this)
     Returns the most recent coordinates of the mouse pointer on this.
     The result is a vector [a b].
     If raw is true the values a b are the pixel row and column 
     If raw is false the values a b are determined by the coordinate-system.")
  
  (mouse-pressed-where
    [this raw]
    [this]
    "(mouse-pressed-where this raw)
     (mouse-pressed-where this)
     Returns coordinates of where the mouse was button was pressed.
     If raw is true return pixel [row column]
     If raw is false the coordinates are mapped by the coordinate system.")

  (mouse-released-where
    [this raw]
    [this])
)

(defn drawing [cs]
  "(drawing cs)
   Create new drawing using coordinate-system cs"
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
                        (.setPaint g2d @background-color*)
                        (.fillRect g2d 0 0 width height)
                        ))
                    (doseq [e (.children root-group)]
                      (if (not (= e widget-root))(.render-node this g2d e)))
                    (.render-node this g2d widget-root)
                    (.repaint @cpan*))))

              (render-node [this g2d element]
                (if (not (.is-leaf? element))
                  (doseq [c (.children element)]
                    (.render-node this g2d c))
                  (let [hidden (.hidden? element)
                        etype (.element-type element)
                        sfn (get {:group (fn [& _])
                                  :point sgwr.elements.point/render-point
                                  :text sgwr.elements.text/render-text
                                  :image sgwr.elements.image/render-image
                                  :line sgwr.elements.line/render-line
                                  :rectangle sgwr.elements.rectangle/render-rectangle
                                  :circle sgwr.elements.circle/render-circle}
                                 etype
                                 (fn [obj _]
                                   (utilities/warning (format "Render function not defined for %s" etype))))]
                    (.setPaint g2d (.color element))
                    (if (or (= hidden :no)(not hidden))
                      (do 
                        (sfn element g2d))) )))

              (flatten! [this include-widgets]
                (let [bg (.image this)]
                  (.remove-children! root-group (fn [q](not (= (.get-property q :id) :widget-root))))
                  (if include-widgets (.remove-children! widget-root))
                  (.background! this bg)
                  bg))

              (flatten! [this]
                (.flatten! this false))

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

                                  (mouseExited [_]
                                    ;; HACK clears all 'active' widgets when mouse exits win
                                    ;(doseq [c (.children widget-root)]
                                    (.restore-attributes! widget-root)
                                    (.render drw))
                                
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
  "(native-drawing w h)
   Create new drawing with native coordinate-system"
  (let [cs (native-cs/native-coordinate-system w h)]
    (drawing cs)))


(defn cartesian-drawing [w h p0 p1]
  "(Cartesian-drawing w h p0 p1)
   Create new drawing with Cartesian coordinate system
   The points p0 --> [x0 y0] and p1 --> [x1 y1] define the range and domain 
   of the drawing. Typically x0 < x1 and y0 < y1 but inverted values are
   also possible."
  (let [cs (cartesian-cs/cartesian-coordinate-system w h p0 p1)]
    (drawing cs)))

(defn polar-drawing 
  "(polar-drawing w h r :origin :unit)
   Create drawing with polar coordinate system
   w, h - drawing size in pixels
   r     - maximum amplitude
   :origin - point [x0 y0] Set location of polar origin on canvas,
             default [0 0]
   :unit   - Sets angular unit, may be either
             :rad  -> radians (the default)
             :deg  -> degrees
             :turn"
  ([w h r & {:keys [origin unit]
             :or {origin [nil nil]
                  unit :rad}}]
   (let [cs (polar-cs/polar-coordinate-system w h r :origin origin :unit unit)]
     (drawing cs))))

