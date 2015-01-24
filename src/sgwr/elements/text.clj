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


(def ^:private size-quant 2)
(def ^:private font-stroke (BasicStroke.))
(def ^:private SERIF Font/SERIF)
(def ^:private SANS-SERIF Font/SANS_SERIF)
(def ^:private MONOSPACED Font/MONOSPACED)
(def ^:private DIALOG Font/DIALOG)
(def ^:private BOLD Font/BOLD)
(def ^:private ITALIC Font/ITALIC)
(def ^:private PLAIN Font/PLAIN)


(def ^:private style->font-map {0 [MONOSPACED PLAIN]
                                1 [SANS-SERIF PLAIN]
                                2 [SERIF PLAIN]
                                3 [DIALOG PLAIN]
                                4 [MONOSPACED BOLD]
                                5 [SANS-SERIF BOLD]
                                6 [SERIF BOLD]
                                7 [DIALOG BOLD]
                                8 [MONOSPACED ITALIC]
                                9 [SANS-SERIF ITALIC]
                                10 [SERIF ITALIC]
                                11 [DIALOG ITALIC]
                                12 [MONOSPACED (+ BOLD ITALIC)]
                                13 [SANS-SERIF (+ BOLD ITALIC)]
                                14 [SERIF (+ BOLD ITALIC)]
                                15 [DIALOG (+ BOLD ITALIC)]})

(def ^:private style-index-map {:mono 0
                                :sans 1
                                :serif 2
                                :dialog 3
                                :mono-bold 4 
                                :sans-bold 5
                                :serif-bold 6
                                :dialog-bold 7
                                :mono-italic 8
                                :sans-italic 9
                                :serif-italic 10
                                :dialog-italic 11
                                :mono-bold-italic 12 
                                :sans-bold-italic 13
                                :serif-bold-italic 14
                                :dialog-bold-italic 15
                                0 0
                                1 1
                                2 2
                                3 3
                                4 4
                                5 5
                                6 6
                                7 7 
                                8 8
                                9 9
                                10 10
                                11 11
                                12 12
                                13 13
                                14 14
                                15 15})

(defn style-fn [& args]
  (let [s (first args)]
    (get style-index-map s 0)))

(defn- update-fn [obj points]
  points)

(defn- bounds-fn [obj points]
  (let [[x0 y0](first points)]
    [[x0 y0][x0 y0]]))

(defn- get-baseline-font [sty size]
  (let [[family style](get style->font-map sty [MONOSPACED PLAIN])]
    (Font. family (int style)(int (* size-quant size)))))

(defn- scale-font [f ratio]
  (let [fname (.getName f)
        style (.getStyle f)
        size (.getSize2D f)]
    (Font. fname style (int (* size ratio)))))
    

(defn render-text [txt-element g2d]
  (let [cs (.coordinate-system txt-element)
        pos (first (.points txt-element))
        [u v] (.map-point cs pos)
        sty (.style txt-element)
        siz (.size txt-element)
        scale-ratio (if (.get-property txt-element :lock-size) 
                      1 
                      (/ 1.0 (.zoom-ratio cs)))
        fnt (scale-font (get-baseline-font sty siz)scale-ratio)
        text (str (.get-property txt-element :text))]
    (.setFont g2d fnt)
    (.setStroke g2d font-stroke)
    (.drawString g2d text (int u)(int v))))

(def ^:private text-function-map {:shape-fn (constantly constants/null-shape)
                                  :contains-fn (constantly false)
                                  :distance-fn (constantly constants/infinity)
                                  :update-fn update-fn
                                  :bounds-fn bounds-fn
                                  :style-fn style-fn})

(def locked-properties [:text :lock-size])

(defn text [parent p txt & {:keys [id color style size lock-size]
                            :or {id :new-text
                                 color (uc/color :white)
                                 style 0
                                 size 6.0
                                 lock-size false}}]
  (let [obj (sgwr.elements.element/create-element :text parent text-function-map locked-properties)]
    (.set-points! obj [p])
    (if parent (.set-parent! obj parent))
    (.put-property! obj :id :text)
    (.put-property! obj :text (str txt))
    (.put-property! obj :lock-size lock-size)
    (.color! obj :default color)
    (.style! obj :default style)
    (.size! obj :default size)
    (.use-attributes! obj :default)
    obj))
