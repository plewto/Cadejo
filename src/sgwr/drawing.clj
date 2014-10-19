(ns sgwr.drawing
  "Drawing is the primary class for sgwr
   
   A drawing consist of a coordinate-system and a set of geometric elements."
  (:require [sgwr.attributes])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.circle])
  (:require [sgwr.ellipse])
  (:require [sgwr.line])
  (:require [sgwr.point])
  (:require [sgwr.rectangle])
  (:require [sgwr.text-element])
  (:require [sgwr.util.math :as math])
  (:import java.awt.Dimension
           java.awt.geom.AffineTransform
           java.awt.image.BufferedImage
           java.awt.image.AffineTransformOp
           java.awt.Rectangle
           java.awt.event.MouseMotionListener
           java.awt.event.MouseListener
           java.io.File
           javax.imageio.ImageIO
           javax.swing.JPanel))

(defn read-image [fqn]
  "Read image file into BufferedImage
   fqn - The fully qualified filename
   If fqn does not exists return nil"
  (let [f (File. fqn)]
    (if (.exists f)
      (ImageIO/read f)
      nil)))

(def selected-color* (atom (sgwr.attributes/create-color :yellow)))
(def zoom-ratio* (atom 2/3))

(def ^:private null-transform (AffineTransform.))
(def ^:private null-transform-op 
  (AffineTransformOp.
   null-transform AffineTransformOp/TYPE_NEAREST_NEIGHBOR))

(defprotocol Drawing

  (drawing-canvas
    [this]
    "Returns the component on which graphics are rendered.
     The canvas is an instance of javax,swing.JPanel")

  (coordinate-system
    [this]
    "Return the coordinate-system")
  
  (coordinate-system!
    [this cs freeze]
    [this cs]
    "Change coordinate system.
     The new coordinate system should have the same 'physical' dimensions
     as the original coordinate-system

     If freeze is true drawing is frozen prior to setting the new 
     coordinate-system. freeze is true by default")

  (bounds 
    [this]
    "Convenience method returns the 'physical' drawing bounds in pixels [w h]")

  (background
    [this]
    "Return background image")

  (background!
    [this bg]
    "Set background image
     bg - Instance of Image which should have the same 'physical' dimensions 
          as the drawing.")
  
  (read-background! 
    [this fqn]
    "Set background image from file
     fqn - string, the fully qualified filename")

  (paper!
    [this c]
    "Fill background with color c")

  (freeze!
    [this]
    "Convert the current drawing to a fixed background.
     The act of freezing a drawing defines a new static background
     which is immune to the changes in the coordinate system.
     That is the background is not effected by any future changes
     in the zoom or view. Freezing also removes all elements from the
     drawing and is not reversible.") 
  
  (view 
    [this]
    "Return the current 'view' as determined by the coordinate system.")

  (view!
    [this v]
    "Set the view in accordance with the coordinate system.")

  (attributes 
    [this]
    "Return an instance of the current Attributes.")

  (attributes!
    [this att]
    "Set the current attributes. All elements added to the drawing 
     automatically inherit the current attributes.")

  (color!
    [this c]
    "Convenience method sets the current attributes color
     See attributes/create-color")

  (width!
    [this w]
    "Convenience method sets the current attributes width.
     Width is interpreted differently by different drawing entities.
     point - ignore
     line, circle, rectangle - set line width
     text - set font size")

  (fill!
    [this f]
    "Set current attributes filled flag.
     Fill is ignored by point,line and text.")

  (style!
    [this s]
    "Sets the current attributes style
     Style is interpreted differently by different drawing entities.

     points: 0 - dot   o     5 - diag2 
             1 - pixel .     6 - box
             2 - dash  -     7 - cross + 
             3 - bar   |     8 - x
             4 - diag  /     0 - triangle

     lines: style sets dash pattern
     
     text:  style sets font 
            0  4  8 12 - mono        plain bold italic bold-italic
            1  5  9 13 - sans-serif  plain bold italic bold-italic
            2  6 10 14 - serif       plain bold italic bold-italic
            3  7 11 15 - dialog      plain bold italic bold-italic")

  (closest 
    [this p filter]
    [this p]
    "Return the drawing element with construction-point closest to p
     and for which the filter function returns true
     p - point [x y]
     filter - Test function which takes a single element argument 
     and returns boolean. Default filter is always true")

  (unselect!
    [this filter]
    [this]
    "Mark all elements for which filter returns true as un-selected.
     If filter not specified un-select all elements.
     filter should be a function which takes a single Element argument and
     returns Bool.")

  (select! 
    [this filter]
    "Mark all elements for which filter true as selected
     filter should be a function which takes single Element argument
     and returns Bool.")
    
  (invert-selection! 
    [this])

  (selected
    [this]
    "Return seq of all selected elements")

  (current-position 
    [this]
    "Returns the position of the most recently added element.
     Many elements use this position as a default.")

  (move-to!
    [this p]
    "Move the current-position to point p")

  (clear!
    [this include-frozen]
    [this]
    "Clear current drawing of all elements.
     By default any frozen background remains unchanged.
     If the include-frozen flag is true the background is also removed.")

  (zoom-in!
    [this]
    "Zoom into the drawing view")

  (zoom-out!
    [this]
    "Zoom out of the drawing view")

  (render 
    [this]
    "Force drawing element to be rendered.")

  (plot! 
    [this points]
    "Plot a set of points to the drawing using the current coordinate-system.
     points should be a nested vector of point coordinates in the current
     coordinate system [[x0 y0][x1 y1][x2 y2]....]

     Each point is mapped via the coordinate system and rendered as a single
     pixel on the canvas. 

     Note that plot! is unique amongst all of the other drawing 
     methods. Pixels rendered by plot! are not permanently added to the 
     drawing. These points will be erased the next time the drawing is 
     rendered; either by adding additional elements are by a coordinate 
     system change. The output of plot! may be preserved by using the 
     freeze! method.")

  (plot-to!
    [this points]
    "Like plot! but connects points with straight lines instead of discrete 
     pixels")

  (suspend-render!
    [this flag]
    "If flag true objects are not immediatly rendered as they are added.")

  (point!
    [this p]
    [this]
    "Add stylized point to the drawing at position p. If p not specified use 
     the current position.  Point objects have a fixed size and shape which
     is not effected by the coordinate-system zoom factor.
     See sgwr.attributes/point-styles")

  (line! 
    [this p0 p1]
    [this p1]
    "Draw line between points p0 and p1. If p0 not specified begin line
    at the current position..")

  (ellipse! 
    [this p0 p1]
    [this p1]
    "Draw ellipse with bounding rectangle defined by points p0 and p1")

  (two-point-circle!
    [this pc pr]
    [this pr]
    "Draw circle with center point pc and locus point pr")

  (circle! 
    [this pc radius]
    [this radius]
    "Draw circle with center point pc and given radius")

  (rectangle! 
    [this p0 p1]
    [this p1]
    "Draw rectangle with diagonal points p0 and p1")

  (rounded-rectangle! 
    [this p0 p1]
    [this p1]
    "Draw rectangle with rounded corners and diagonal points p0 and p1")

  (text! 
    [this p0 txt]
    [this txt]
    "Add text element to drawing
     p0 - base-line point 
     txt - the text")

  (remove!
    [this e]
    "Remove specific element from drawing")

  (elements
    [this]
    "Return seq of all drawing elements")

  (add-mouse-motion-listener! 
    [this mml]
    "Add a new MouseMotionListener to drawing")

  (remove-mouse-motion-listener!
    [this mml])

  (add-mouse-listener! 
    [this ml])

  (remove-mouse-listener!
    [this ml])
 
  (where 
    [this]
    "Returns the most current position of the mouse over the drawing.
     The position coordinates are in accordance with the current coordinate 
     system.")

  (mouse-pressed-where
    [this]
    "Returns the most recent position of where mouse was pressed in terms of 
     the current coordinate system")

  (mouse-released-where
    [this]
    "Returns most recent position mouse was released in terms of 
     current coordinate system.")

  (export 
    [this filename itype]
    [this filename]
    "Export drawing as image file
     filename - String, the fully qualified filename
     itype - keyword, may be either :png or :jpeg, defaults to :png
     If image saved successfully returns image
     If an Exception occurs print stack trace and return nil")

  (to-string
    [this verbose]
    [this])
  )


;; bounds - canvas bounds as java.awt.Dimension
;; g - graphics context
;; bg back ground color
;;
(defn- clear-canvas [bounds g bg]
  (.setColor g bg)
  (.fillRect g 0 0 (.getWidth bounds)(.getHeight bounds)))

;; cs - coordinate-system
;;
;; refresh-delay - int, Sets minimum delay time in milliseconds between 
;; drawing repaints.
;;
;; Any swing components placed on top of the drawing tend to flicker as the
;; drawing's container is re-sized. Adding a few ms delay smooths this
;; flickering out. However adding delay makes any mouse-motion-listeners
;; less responsive.  To get around this the refresh delay is set to zero
;; whenever the mouse enters the drawing and reverts to it's normal value
;; on mouse exit.
;;

(defn- create-drawing [cs refresh-delay]
  (let [[width height](.canvas-bounds cs)
        cs* (atom cs)
        mouse-dragged* (atom nil)  ;; true if mouse position results from drag
        mouse-position* (atom [0 0]) ;; as pixel [col row]
        mouse-pressed-position* (atom [0 0]) ;; as pixel [col row]
        mouse-released-position* (atom [0 0]) ;; as pixel [col row]
        physical-bounds (Dimension. width height)
        position* (atom [0.0 0.0])
        attributes* (atom (sgwr.attributes/attributes))
        suspend-render* (atom false)
        elements* (atom [])
        background-image (BufferedImage. (.getWidth physical-bounds)
                                         (.getHeight physical-bounds)
                                         BufferedImage/TYPE_INT_RGB)
        image (BufferedImage. (.getWidth physical-bounds)
                              (.getHeight physical-bounds)
                              BufferedImage/TYPE_INT_RGB)
        refresh-delay* (atom refresh-delay)
        cpan* (atom nil)

        drw (reify Drawing
              (drawing-canvas [this] @cpan*)

              (coordinate-system [this] @cs*)

              (coordinate-system! [this cs freeze]
                (if freeze (.freeze! this))
                (reset! cs* cs))

              (coordinate-system! [this cs]
                (.coordinate-system! this cs true))

              (bounds [this]
                physical-bounds)

              (background [this] background-image)
                
              (background! [this image]
                (let [g2d (.createGraphics background-image)]
                  (.drawImage g2d image null-transform-op 0 0)
                  (if (not @suspend-render*)(.render this))
                  true))
              
              (read-background! [this fqn]
                (let [bi (read-image fqn)]
                  (if bi 
                    (.background! this bi)
                    false)))

              (paper! [this c]
                (let [g2d (.createGraphics background-image)]
                  (.setColor g2d (sgwr.attributes/create-color c))
                  (.fillRect g2d 0 0 width height)))

              (freeze! [this]
                (let [g2d (.createGraphics background-image)]
                  (.drawImage g2d image null-transform-op 0 0)
                  (reset! elements* [])))

              (attributes [this]
                @attributes*)

              (attributes! [this att]
                (reset! attributes* att))

              (view [this]
                (.view @cs*))

              (view! [this v]
                (.view! @cs* v)
                (.render this))

              (color! [this c]
                (.color! @attributes* c))
              
              (width! [this w]
                (.width! @attributes* w))
              
              (fill! [this f]
                (.fill! @attributes* f))

              (style! [this s]
                (.style! @attributes* s))
              
              (closest [this p]
                (.closest this p (fn [_] true)))

              (closest [this p filter]
                (let [rs* (atom nil)
                      mindist* (atom 1e64)]
                  (doseq [e @elements*]
                    (if (filter e)
                      (doseq [cp (.construction-points e)]
                        (let [d (math/distance p cp)]
                          (if (< d @mindist*)
                            (do
                              (reset! mindist* d)
                              (reset! rs* e)))))))
                  @rs*))

              (unselect! [this]
                (.unselect! this (fn [_] true)))

              (unselect! [this filter]
                (doseq [e (.elements this)]
                  (if (filter e)
                    (.select! (.attributes e) false))))
                
              (select! [this filter]
                (doseq [e (.elements this)]
                  (if (filter e)
                    (.select! (.attributes e) true))))

              (invert-selection! [this]
                (doseq [e (.elements this)]
                  (let [att (.attributes e)
                        f (.selected? att)]
                    (.select! att (not f)))))
                
              (selected [this]
                (let [acc* (atom [])]
                  (doseq [e (.elements this)]
                    (if (.selected? (.attributes e))
                      (swap! acc* (fn [n](conj n e)))))
                  @acc*))

              (current-position [this]
                @position*)
              
              (move-to! [this p]
                (reset! position* p))

              (clear! [this include-frozen]
                (reset! elements* [])
                (if include-frozen
                  (let [g2d (.createGraphics background-image)]
                    (.clearRect g2d 0 0 (.getWidth physical-bounds)(.getHeight physical-bounds))))
                (.render this))
              
              (clear! [this]
                (.clear! this false))

              (zoom-in! [this]
                (.zoom! @cs* @zoom-ratio*)
                (.render this))

              (zoom-out! [this]
                (.zoom! @cs* (/ 1.0 @zoom-ratio*))
                (.render this))

              (render [this]
                (let [g2d (.createGraphics image)
                      w (.getWidth physical-bounds)
                      h (.getHeight physical-bounds)]
                  (.fillRect g2d 0 0 w h)
                  (.drawImage g2d background-image null-transform-op 0 0)
                  (doseq [e @elements*]
                    (if (not (.hidden? e))
                      (let [c (if (.selected? e)
                                @selected-color*
                                (.color e))
                            strk (.stroke e)
                            shape (.shape e @cs*)]
                        (.setColor g2d c)
                        (.setStroke g2d strk)
                        (cond 
                         ;(.is-text? e)
                         (= (.element-type e) :text)
                         (let [fnt (.get-font e @cs*)
                               tx (.text e)
                               pos (.map-point @cs* (first (.construction-points e)))
                               [u v] pos]
                           (.setFont g2d fnt)
                           (.drawString g2d tx (int u)(int v)))
                         
                         (.filled? e)
                         (.fill g2d shape)
                         
                         :default
                         (.draw g2d shape))))))
                (.repaint @cpan*))

              ;;; plot is ephemeral  - graph will be wiped out on next repaint.
              (plot! [this points]
                (let [rgb (.color-rgb @attributes*)]
                  (doseq [p points]
                    (let [q (.map-point @cs* p)
                          [u v] q]
                      (.setRGB image (int u)(int v) rgb)))
                  (.repaint @cpan*)))
              
              (plot-to! [this points]
                (let [g2d (.createGraphics image)
                      q0* (atom (.map-point @cs* (first points)))]
                  (.setColor g2d (.color @attributes*))
                  (doseq [p (rest points)]
                    (let [q1 (.map-point @cs* p)
                          [u0 v0] @q0*
                          [u1 v1] q1]
                      (.drawLine g2d u0 v0 u1 v1)
                      (reset! q0* q1)))
                  (.repaint @cpan*)))

              (suspend-render! [this flag]
                (reset! suspend-render* flag))

              (point! [this p]
                (let [obj (sgwr.point/point p)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))

              (point! [this]
                (.point! this @position*))

              (line! [this p0 p1]
                (let [obj (sgwr.line/line p0 p1)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p1)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))

              (line! [this p1]
                (.line! this @position* p1))

              (ellipse! [this p0 p1]
                (let [obj (sgwr.ellipse/ellipse p0 p1)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p1)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))

              (ellipse! [this p1]
                (.ellipse! this @position* p1))
              
              (two-point-circle! [this pc pr]
                (let [obj (sgwr.circle/circle pc pr)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* pc)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))
            
              (two-point-circle! [this pr]
                (.two-point-circle! this @position* pr))

              (circle! [this pc radius]
                (let [pr [(+ (first pc) radius)(second pc)]]
                  (.two-point-circle! this pc pr)))

              (circle! [this radius]
                (.circle! this @position* radius))

              (rectangle! [this p0 p1]
                (let [obj (sgwr.rectangle/rectangle p0 p1)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p1)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))
              
              (rectangle! [this p1]
                (.rectangle! this @position* p1))

              (rounded-rectangle! [this p0 p1]
                (let [obj (sgwr.rectangle/rounded-rectangle p0 p1)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p1)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))

              (rounded-rectangle! [this p1]
                (.rounded-rectangle! this @position* p1))

              (text! [this p txt]
                (let [obj (sgwr.text-element/text-element p txt)]
                  (.attributes! obj (.clone @attributes*))
                  (reset! position* p)
                  (swap! elements* (fn [n](conj n obj)))
                  (if (not @suspend-render*)(.render this))
                  obj))

              (text! [this txt]
                (.text! this @position* txt))

              (elements [this]
                @elements*)
              
              (remove! [this e]
                (swap! elements* (fn [n](remove (fn [q](= q e)) n))))

              (add-mouse-motion-listener! [this mml]
                (.addMouseMotionListener @cpan* mml))
                
              (remove-mouse-motion-listener! [this mml]
                (.removeMouseMotionListener @cpan* mml))

              (add-mouse-listener! [this ml]
                (.addMouseListener @cpan* ml))

              (remove-mouse-listener! [this ml]
                (.removeMouseListener @cpan* ml))

              (where [this]
                (.inv-map @cs* @mouse-position*))

             (mouse-pressed-where [this]
               (.inv-map @cs* @mouse-pressed-position*))

             (mouse-released-where [this]
               (.inv-map @cs* @mouse-released-position*))

             (export [this filename]
               (.export this filename :png))

             (export [this filename itype]
               (let [f (File. filename)
                     iform (get {:png "PNG"
                                 :jpeg "JPEG"} 
                                itype
                                "PNG")]
                 (try
                   (ImageIO/write image iform f)
                   image
                   (catch Exception ex
                     (.printStackTrace ex)
                     nil))))

             (to-string [this verbose]
               (let [sb (StringBuilder.)]
                 (.append sb "Drawing:\n")
                 (doseq [e (.elements this)]
                   (.append sb (str (format "\t%s" (.to-string e))
                                    (if verbose
                                      (format "\n\t\t%s" (.to-string (.attributes e))))
                                    "\n")))
                 (.toString sb)))
             
             (to-string [this]
               (.to-string this false)))]
    
    (reset! cpan* (proxy [JPanel][true]
                    (paint [g]
                      (.drawImage g image null-transform-op 0 0)
                      (dotimes [i (.getComponentCount @cpan*)]
                        (let [cmp (.getComponent @cpan* i)]
                          (.repaint cmp)))
                      (Thread/sleep @refresh-delay*)
                      )))


    (.addMouseMotionListener @cpan* (proxy [MouseMotionListener][]
                                    (mouseDragged [ev]
                                      (reset! mouse-dragged* true)
                                      (reset! mouse-position* [(.getX ev)(.getY ev)]))

                                    (mouseMoved [ev]
                                      (reset! mouse-dragged* false)
                                      (reset! mouse-position* [(.getX ev)(.getY ev)]))))

    (.addMouseListener @cpan* (proxy [MouseListener][]
                              (mouseClicked [ev] )
                              (mouseEntered [ev] 
                                (reset! refresh-delay* 0))
                              (mouseExited [ev] 
                                (reset! refresh-delay* refresh-delay))
                              (mousePressed [ev] 
                                (reset! mouse-pressed-position* [(.getX ev)(.getY ev)]))
                              (mouseReleased [ev]
                                (reset! mouse-released-position* [(.getX ev)(.getY ev)]))))
    
    drw))

(defn native-drawing 
     "Create drawing with 'native' coordinate system.
   The native system uses integer coordinate positions
   with x=0 corresponding to the left edge and x=w the right hand edge
   and y=0 the top and y=h the bottom."
  ([w h & {:keys [refresh-delay]
           :or {refresh-delay 0}}]
     (let [cs (sgwr.coordinate-system/native w h)]
       (create-drawing cs refresh-delay))))

(defn cartesian-drawing 
  "Create a drawing with a rectangular Cartesian coordinate system.
   w and h - The canvas size in pixels
   p0 and p1 - The diagonal points   p0 = [x0 y0]  p1 = [x1 y1] 
   x0 corresponds to the left most edge of the canvas and x1 the right most
   edge. y0 corresponds to the bottom edge and y1 to the top.
   Note that either the horizontal or vertical sense may be inverted.
   If x0 > x1 (or y0 > y1) then x0 (y0) corresponds to the right (top)
   and x1 (y1) corresponds to the left (bottom)"
  ([w h]
     (cartesian-drawing w h [-1.0 -1.0][+1.0 +1.0]))
  ([w h p0 p1 & {:keys [refresh-delay]
                 :or {refresh-delay 0}}]
     (let [cs (sgwr.coordinate-system/cartesian w h p0 p1)]
       (create-drawing cs refresh-delay))))
  
(defn polar-drawing 
  "Create drawing with polar coordinate system. 
   The physical canvas size is square with side w pixels
   The radius r argument sets the initial maximum viewable amplitude
   
   The origin is placed at the center of the canvas with the polar axis 
   extending horizontally to the right. Angles are measured counter-clockwise
   in radians."
  ([w]
     (polar-drawing w 1.0))
  ([w r & {:keys [units refresh-delay]
           :or {units :rad
                refresh-delay 0}}]
     (let [cs (sgwr.coordinate-system/polar w r :units units)]
       (create-drawing cs refresh-delay))))
