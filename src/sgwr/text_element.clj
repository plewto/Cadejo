(ns sgwr.text-element
  "Defines text element
   The style attribute selects one of 16 fonts 
   The width attribute sets the font size.
   Text elements are always horizontal (may change later?) 
   and are defined in terms of the left-most base-line point"
  (:require [sgwr.attributes])
  (:require [sgwr.element])
  (:import java.awt.Font
           java.awt.BasicStroke
           java.awt.geom.GeneralPath))

(declare text-element)

(def font-stroke (BasicStroke.))

(def SERIF Font/SERIF)
(def SANS-SERIF Font/SANS_SERIF)
(def MONOSPACED Font/MONOSPACED)
(def DIALOG Font/DIALOG)
(def BOLD Font/BOLD)
(def ITALIC Font/ITALIC)
(def PLAIN Font/PLAIN)

(def font-styles { 0 [MONOSPACED PLAIN]
                   1 [SANS-SERIF PLAIN]
                   2 [SERIF      PLAIN]
                   3 [DIALOG     PLAIN]
                   4 [MONOSPACED BOLD]
                   5 [SANS-SERIF BOLD]
                   6 [SERIF      BOLD]
                   7 [DIALOG     BOLD]
                   8 [MONOSPACED ITALIC]
                   9 [SANS-SERIF ITALIC]
                  10 [SERIF      ITALIC]
                  11 [DIALOG     ITALIC]
                  12 [MONOSPACED (+ BOLD ITALIC)]
                  13 [SANS-SERIF (+ BOLD ITALIC)]
                  14 [SERIF      (+ BOLD ITALIC)]
                  15 [DIALOG     (+ BOLD ITALIC)]})
                      

(defn- get-baseline-font [style-index size]
  (let [att (get font-styles style-index [SERIF PLAIN])]
    (Font. (first att)(int (second att))(int size))))

(defn- scale-font [f ratio]
  (let [fname (.getName f)
        style (.getStyle f)
        size (.getSize2D f)]
    (Font. fname style (int (* size ratio)))))

(defprotocol TextProtocol
  
  (text 
    [this])

  (text!
    [this s])

  (get-font 
    [this cs]))

(deftype TextElement [attributes* position* text* ]

  sgwr.element.Element

  (element-type [this] :text)

  (attributes [this]
    @attributes*)

  (attributes! [this att]
    (reset! attributes* att))

  (construction-points [this]
    @position*)

  ;; position p always nested vector [[x0 y0]]
  (position! [this p]
    (reset! @position* (first p)))

  (shape [this cs] ;; ignored, returns a 'null' shape
    (GeneralPath.))

  (color [this]
    (.color @attributes*))

  (stroke [this]
    font-stroke)

  (hidden? [this]
    (.hidden? @attributes*))

  (filled? [this]
    false)

  (selected? [this]
    (.selected? @attributes*))
  
  (clone [this]
    (let [other (text-element @position* @text*)]
      (.attributes! other (.clone @attributes*))
      other))

  (to-string [this]
    (format "Text %s \"%s\"" @position* @text*))

  TextProtocol

  (text [this] @text*)

  (text! [this s]
    (reset! text* (str s)))

  (get-font [this cs]
    (let [att (.attributes this)
          sty (.style att)
          size (* 8 (.width att))
          ratio (/ 1.0 (.zoom-ratio cs))
          blf (get-baseline-font sty size)]
      (scale-font blf ratio))))

(defn text-element 
  ([position txt]
     (let [attributes* (atom (sgwr.attributes/attributes))
           position* (atom [position])
           text* (atom (str txt))
           tobj (TextElement. attributes* position* text*)]
       tobj)))
