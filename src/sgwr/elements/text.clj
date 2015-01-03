;; TODO: Add text rotation

(ns sgwr.elements.text 
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:import java.awt.Font
           java.awt.BasicStroke
           java.awt.geom.GeneralPath))

(def font-stroke (BasicStroke.))

(def ^:private SERIF Font/SERIF)
(def ^:private SANS-SERIF Font/SANS_SERIF)
(def ^:private MONOSPACED Font/MONOSPACED)
(def ^:private DIALOG Font/DIALOG)
(def ^:private BOLD Font/BOLD)
(def ^:private ITALIC Font/ITALIC)
(def ^:private PLAIN Font/PLAIN)
(def ^:private font-styles { 0 [MONOSPACED PLAIN]
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

(def ^:private size-quant 2)

(defn- get-baseline-font [style size]
  (let [sty (get font-styles style [SANS-SERIF PLAIN])]
    (Font. (first sty)(int (second sty))(int (* size-quant size)))))

(defn- scale-font [f ratio]
  (let [fname (.getName f)
        style (.getStyle f)
        size (.getSize2D f)]
    (Font. fname style (int (* size ratio)))))
  

(defn- update-fn [obj points]
  points)

(defn- bounds-fn [obj points]
  (let [[x0 y0](first points)]
    [[x0 y0][x0 y0]]))

(defn render-text [txt-element g2d]
  (let [cs (.coordinate-system txt-element)
        pos (first (.points txt-element))
        [u v] (.map-point cs pos)
        sty (.style txt-element)
        siz (.width txt-element)
        fnt (scale-font (get-baseline-font sty siz)(/ 1.0 (.zoom-ratio cs)))
        text (str (.get-property txt-element :text))]
    (.setFont g2d fnt)
    (.setStroke g2d font-stroke)
    (.drawString g2d text (int u)(int v))))

(def ^:private text-function-map {:shape-fn (constantly constants/null-shape)
                                  :contains-fn (constantly false)
                                  :distance-fn (constantly constants/infinity)
                                  :update-fn update-fn
                                  :bounds-fn bounds-fn})

(def locked-properties [:text])

(defn text 
  "(text txt])
   (text parent p txt)
  
   Create text object with left-hand bas-point p [x y].
  
   Text objects never 'contain' points and the the distance between a
   text object and a point q is defined as the distance between q and p. 

   The bounds of a text object is a degenerate rectangle [[x y][x y]]

   The font style and size are determined by the attributes style and
   width values. Text objects ignore the attributes fill value."

  ([txt](text nil [0 0] txt))
  ([parent position txt]
   (let [obj (sgwr.elements.element/create-element :text parent text-function-map locked-properties)]
     (.set-points! obj [position])
     (.put-property! obj :id :text)
     (if parent (.set-parent! obj parent))
     (.put-property! obj :text (str txt))
     obj)))
                                  
