(ns sgwr.elements.image
  "Defines element for rendering BufferedImage.
   Unlike most drawing elements images do not scale with changes to
   the coordinate system view.

   Images may be read from a file or extracted from the resource
   directory. All source images must have an alpha layer."    
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  (:import java.awt.image.BufferedImage
           javax.imageio.ImageIO
           java.io.File))

(defn render-image [iobj g2d]
  (let [cs (.coordinate-system iobj)
        p0 (first (.points iobj))
        q0 (.map-point cs p0)
        [x y] q0
        img (.get-property iobj :image)]
    (.drawImage g2d img constants/null-transform-op (int x)(int y))))

(defn- update-fn [obj points]
  (let [img (.get-property obj :image)
        w (.getWidth img)
        h (.getHeight img)
        p0 (first points)
        [x0 y0] p0
        x1 (+ x0 w)
        y1 (+ y0 h)
        p1 [x1 y1]]
    [p0 p1]))

(defn- distance-helper [obj q]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        u0 (min (first q0)(first q1))
        u1 (max (first q0)(first q1))
        v0 (min (second q0)(second q1))
        v1 (max (second q0)(second q1))
        [u v] (.map-point cs q)
        contains-flag (and (<= u0 u)(<= u u1)(<= v0 v)(<= v v1))
        distance (if contains-flag 
                   0
                   (math/point-rectangle-distance [u v] q0 q1))]
    [contains-flag distance]))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]]))

(defn- scale-fn [& _] )

(def ^:private image-function-map {:shape-fn (constantly constants/null-shape)
                                   :contains-fn (fn [obj q](first (distance-helper obj q)))
                                   :distance-fn (fn [obj q](second (distance-helper obj q)))
                                   :update-fn update-fn
                                   :scale-fn scale-fn
                                   :bounds-fn bounds-fn
                                   :style-fn (constantly 0)})
(defn clone-buffered-image [src]
  (let [w (.getWidth src)
        h (.getHeight src)
        dst (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)]
    (.setData dst (.getData src))
    dst))

(def locked-properties [:image]) 
                                  
(defn image 
  "(image parent p w h)

    Creates an initially blank image element. Images are rectangular
    bit maps which ignore most attribute values. Images do not
    scale 

    The contains and distance concepts for an image are identical to
    rectangle objects. "

  ([parent p0 w h & {:keys [id image-src]
                     :or {id nil
                          image-src nil
                          }}]
   (let [obj (sgwr.elements.element/create-element :image
                                                   parent 
                                                   image-function-map
                                                   locked-properties)
         img (if image-src 
               (clone-buffered-image image-src)
               (BufferedImage. w h BufferedImage/TYPE_INT_ARGB))]  
     (.put-property! obj :image img)
     (.put-property! obj :id (or id :image))
     (.set-points! obj [p0])
     (if parent (.set-parent! obj parent))
     obj)))

(defn read-image [parent p0 filename & {:keys [id w h]
                                        :or {id nil
                                             w nil
                                             h nil}}]
  "(read-image parent p0 filename :id :w :h)
   Create image element from external file.
   The default width and height of the element is determined by the image
   but may be explicitly specified.

   The image file must contain an alpha layer.

   parent - SgwrElement, 
   p0     - vector, coordinate of lower left corner (coordinate system dependent) 
            [x0 y0]
   :id    - keyword, element id
   :w     - int, explicit width, defaults to image width
   :h     - int, explicit height, defaults to image height"
  (let [f (File. filename)
        i (try 
            (ImageIO/read f)
            (catch Exception ex
              (utilities/warning (format "Can not read image file '%s'" filename))
              nil))]
    (if i
      (let [iw (or w (.getWidth i))
            ih (or h (.getHeight i))
            img (image parent p0 iw ih :id (or id filename) :image-src i)]
        img)
      (image parent p0 1 1 :id (format "DEAD-%s" filename)))))
  
        
(defn read-icon [parent p0 prefix group subgroup]
  "(read-icon parent p0 prefix group subgroup)
   Create image image from resource icon.
   parent - SgwrElement
   p0     - vector [x,y] position
   prefix - string, resource icon prefix, either 'black', 'gray' or 'white'
   group    - string icon group 
   subgroup - string icon subgroup"
  (let [ifn (format "resources/icons/%s_%s%s.png" 
                    (name prefix)
                    (name group)
                    (if subgroup (format "_%s" (name subgroup)) ""))]
    (read-image parent p0 ifn)))

(defn read-logo [parent p0 logo-file]
  (read-image parent p0 (format "resources/logos/%s.png" logo-file)))
