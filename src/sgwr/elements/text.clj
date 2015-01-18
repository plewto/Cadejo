;; TODO: Add text rotation

(ns sgwr.elements.text 
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.element])
  (:require [sgwr.util.color :as uc])
  (:import java.awt.Font
           java.awt.BasicStroke
           java.awt.geom.GeneralPath))

;; Estimate functions only valid for plain monospaced font
;; 
(defn estimate-monospaced-height [size] (* 1.50 size))
(defn estimate-monospaced-width [size] (* 1.25 size))
(defn estimate-monospaced-font-size [width] (/ width 1.5))


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
        siz (.size txt-element)
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

(defn text [parent p txt & {:keys [id color style size]
                            :or {id :new-text
                                 color (uc/color :white)
                                 style 0
                                 size 6.0}}]
  (let [obj (sgwr.elements.element/create-element :text parent text-function-map locked-properties)]
    (.set-points! obj [p])
    (if parent (.set-parent! obj parent))
    (.put-property! obj :id :text)
    (.put-property! obj :text (str txt))
    (.color! obj :default color)
    (.style! obj :default style)
    (.size! obj :default size)
    (.use-attributes! obj :default)
    obj))
                            
                                  
