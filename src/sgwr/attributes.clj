(ns sgwr.attributes
  "Defines drawing element attributes.

Each drawing element has a set of attributes including id, color, style,
width, hidden, selected, and filled.  Not all attributes are applicable
to every element and two different elements may interpret the same attribute
differently."
  (:require [seesaw.color :as ssc])
  (:require [seesaw.graphics :as ssg]))

(def ^:private color-type (type (ssc/color :black)))

(defn create-color [c]
  "Create instance of java.awt.Color

create-color is similar to seesaw.color/color with the exception
that the argument c may be an instance of Color.

If c is already an instance of Color it is simply returned
For all other options see http://daveray.github.io/seesaw/seesaw.color-api.html

If for some reason c can not be converted to a color a neutral gray is
returned."

  (cond (= (type c) color-type)
        c
        
        (keyword? c)
        (ssc/color c)

        (vector? c)
        (apply ssc/color c)

        :default
        (ssc/color :gray)))
       
(def ^:private point-styles { 0 :dot
                              1 :pixel
                              2 :dash
                              3 :bar
                              4 :diag
                              5 :diag2
                              6 :box
                              7 :cross
                              8 :x
                              9 :triangle})

(defn point-style [n]
  (get point-styles n :dot))

(def ^:private line-styles { 0 [1.0]                      ; solid 
                             1 [2.0 2.0]                  ; dotted
                             2 [4.0 4.0]                  ; small dash
                             3 [6.0 6.0]                  ; med dash
                             4 [12.0 12.0]                ; large dash
                             5 [12.0 6.0 2.0 6.0]         ; center dash dot
                             6 [24.0 6.0 6.0 6.0]
                             7 [2.0 4.0 6.0 4.0]
                             8 [4.0 8.0 12.0 8.0]
                             9 [24.0 24.0]})

(defn line-style [n]
  (let [rs (get line-styles n [1.0])]
    rs))

;; Return instance of BasicStroke for given style and width
;; where style is an integer indicating dash-pattern 
;; and width is a float.
(defn- stroke [style width]
  (ssg/stroke :width width :dashes (line-styles style)))


(defprotocol Attributes

  ;; (id 
  ;;   [this]
  ;;   "Return the id (a keyword) of this element. 
  ;;    The id attribute is currently not used by sgwr.
  ;;    Client applications are free to use id in anyway
  ;;    they want.")

  ;; (id!
  ;;   [this key]
  ;;   "Sets id of this element")

  (put-property! 
    [this key value]
    "Set arbitrary property value")

  (properties
    [this]
    "Return list of property keys")

  (property
    [this key default]
    [this key]
    "Return property value associated with key
     If key has no value return default or nil")

  (color 
    [this]
    "Returns instance of java.awt.Color")

  (color-rgb
    [this]
    "Returns int RGB value of the color")

  (color!
    [this c]
    "Sets the color attribute to c, 
     see create-color")

  (style
    [this]
    "Returns the style attribute as an integer.
     Different drawing elements interpret style in unique ways:
     point - the point shape
     line, circle, rectangle - the line dash style
     text - font")
     
  (style!
    [this s]
    "Set the style attribute where s is an integer.")

  (width 
    [this]
    "Returns the stroke width as a float. Different drawing elements 
     interpret width in unique ways:
     point - width attribute is ignored.
     line, rectangle, circle - line width
     text - font size")

  (width!
    [this w]
    "Sets width attribute")

  (filled?
    [this]
    "Predicate indicates if element is to be filled.
     The filled attribute is ignored by point, line and text")

  (fill!
    [this flag]
    "Sets the filled attribute flag")

  (hidden? 
    [this]
    "Predicate indicates if the element is hidden.
     Hidden elements continue to exists in the drawing 
     but are not rendered.")

  (hide!
    [this flag]
    "Sets hidden attribute")

  (selected?
    [this]
    "Predicate indicating if element is selected.
     All selected elements are rendered in the same color
     regardless of their color attribute.")

  (select!
    [this flag]
    "Set element as selected.")

  (clone
    [this]
    "Return a new instance of Attributes with identical 
     values to this.")

  (to-string
    [this]))  


(defn attributes
  "Creates new instance of attributes
   c - color
   s - style
   w - width
   f - filled"
  ([]
     (attributes :blue))
  ([c]
     (attributes c 0))
  ([c s]
     (attributes c s 1.0 false))
  ([c s w f]
     (let [;id* (atom nil)
           properties* (atom {})
           color* (atom nil)
           style* (atom nil)
           width* (atom nil)
           filled* (atom nil)
           hidden* (atom nil)
           selected* (atom false)
           
           att (reify Attributes

                 ;; (id [this] @id*)
                   
                 ;; (id! [this key]
                 ;;   (reset! id* (keyword key)))

                 (put-property! [this key value]
                   (swap! properties* (fn [n](assoc n key value))))

                 (properties [this]
                   (keys @properties*))

                 (property [this key default]
                   (get @properties* key default))

                 (property [this key]
                   (.property this key nil))

                 (color [this] @color*)
                 
                 (color-rgb [this]
                   (.getRGB @color*))

                 (color! [this c]
                   (reset! color* (create-color c)))
                 
                 (style [this] @style*)

                 (style! [this sty]
                   (reset! style* (int sty)))

                 (width [this] @width*)

                 (width! [this w]
                   (reset! width* (float w)))

                 (filled? [this] @filled*)

                 (fill! [this f]
                   (reset! filled* f))

                 (hidden? [this] @hidden*)

                 (hide! [this f]
                   (reset! hidden* f))

                 (selected? [this] @selected*)

                 (select! [this f]
                   (reset! selected* f))

                 (to-string [this]
                   (let [c (.color this)
                         r (.getRed c)
                         g (.getGreen c)
                         b (.getBlue c)]
                     (str
                      (format "style %d  width %3.1f  filled %s  hidden %s  "
                              (.style this)(.width this)
                              (.filled? this)(.hidden? this))
                      (format "selected %s" (.selected? this)))))

                 (clone [this]
                   (let [other (attributes @color* @style* @width* @filled*)]
                     (.hide! other (.hidden? this))
                     (.select! other (.selected? this))
                     other)))]
       (.color! att c)
       (.style! att s)
       (.width! att w)
       (.fill! att f)
       (.hide! att false)
       att))) 
