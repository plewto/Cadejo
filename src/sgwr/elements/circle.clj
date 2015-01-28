;; TODO:
;;     1. define three-point constructor

(ns sgwr.elements.circle
  "Defines circle element
   attributes:
     color
     style - dash patten
     width - line width
     size  - ignored
     fill  - 
     hide  -"
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.util.stroke :as ustroke])
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.line])
  (:require [seesaw.graphics :as ssg]))

(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        [p0 p1](.points obj)
        q0 (.map-point cs p0)
        q1 (.map-point cs p1)
        [u0 v0] q0
        [u1 v1] q1
        w (- u1 u0)
        h (- v1 v0)]
    (ssg/ellipse u0 v0 w h)))

(defn render-circle [obj g2d]
  (.setStroke g2d (ustroke/stroke obj))
  (let [shp (shape-fn obj)
        f (.filled? obj)]
    (if (and f (not (= f :no)))
      (.fill g2d shp)
      (.draw g2d shp))))

(defn- update-fn [obj points]
  (let [[p0 p1] points
        x0 (apply min (map first [p0 p1]))
        y0 (apply min (map second [p0 p1]))
        x1 (apply max (map first [p0 p1]))
        y1 (apply max (map second [p0 p1]))
        xc (math/mean x0 x1)
        yc (math/mean y0 y1)
        radius (math/distance [xc yc][x0 yc])]
    (.put-property! obj :center [xc yc])
    (.put-property! obj :radius radius)
    [[x0 y0][x1 y1]]))

(defn- distance-helper [obj q]
  (let [pc (.get-property obj :center)
        radius (.get-property obj :radius)
        dc (math/distance pc q)
        distance (- dc radius)]
    [(<= dc radius)
     (max 0 distance)]))

(defn contains-fn [obj q]
  (first (distance-helper obj q)))

(defn distance-fn [obj q]
  (second (distance-helper obj q)))

(defn- bounds-fn [obj points]
  (let [x (map first points)
        y (map second points)
        x0 (apply min x)
        x1 (apply max x)
        y0 (apply min y)
        y1 (apply max y)]
    [[x0 y0][x1 y1]])) 

(def ^:private circle-function-map {:shape-fn shape-fn
                                    :contains-fn contains-fn
                                    :distance-fn distance-fn
                                    :update-fn update-fn
                                    :bounds-fn bounds-fn
                                    :style-fn sgwr.elements.line/style-fn})

(def ^:private locked-properties [:center :radius])


(defn circle [parent p0 p1  & {:keys [id color style width fill]
                               :or {id :new-circle
                                    color (uc/color :white)
                                    style 0
                                    width 1.0
                                    fill nil}}]
  "(circle parent p0 p1 [:id :color :style :fill])
   Create circle element defined by opposing points of bounding rectangle
   parent - SgwrElement, most often parent will be a group element
   p0     - pair [x0 y0] first vertex of bounding rectangle
   p1     - pair [x1 y1] second (opposing) vertex of bounding rectangle
   :id    - keyword, if not specified a unique id is created
   :color - See sgwr.util.color/color, color for default attributes
   :style - Integer or keyword, Sets dash pattern for default attributes
            see constants/line-style
   :width - float, line width for default attributes width > 0.
   :fill  - flag, fill state for default attributes, flag may be false, true 
            or :no"
  (let [obj (sgwr.elements.element/create-element :circle
                                                  parent
                                                  circle-function-map
                                                  locked-properties)]
    (if parent (.set-parent! obj parent))
    (.set-points! obj [p0 p1])
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.use-attributes! obj :default)
    obj))


;; Helper function called by circle-r
;; Define circle by center point and radius 
;; for cartesain like coordinate systems
(defn- rectangle-circle-r [parent pc radius args]
  (let [[xc yc] pc
        x0 (- xc radius)
        y0 (- yc radius)
        x1 (+ xc radius)
        y1 (+ yc radius)
        arglst* (atom [parent [x0 y0][x1 y1]])]
    (doseq [a args]
      (swap! arglst* (fn [q](conj q a))))
    (apply circle @arglst*)))


;; Helper function called by curcle-r
;; Define circle by center point and radius
;; for polar coordinate systems
(defn- polar-circle-r [parent radius args]
  (let [[xc yc] [0 0]
        [a45 a225](let [cs (.coordinate-system parent)
                        au (second (.units cs))
                        amap (cond (= au :rad) math/deg->rad
                                   (= au :turn) math/deg->turn
                                   (= au :deg) identity
                                   :default
                                   (do 
                                     (utilities/warning (str "Unknown angular unit " au))
                                     identity))]
                    [(amap 45)(amap 225)])
        rsqr (math/sqr radius)
        hyp (math/sqrt (+ rsqr rsqr))
        p0 [hyp a225]
        p1 [hyp a45]
        arglst* (atom [parent p0 p1])]
    (doseq [a args]
      (swap! arglst* (fn [q](conj q a))))
    (apply circle @arglst*)))


(defn circle-r [parent pc radius & args]
 "(circle-r parent pc radius [args....])
   Defines circle by center point pc and radius
   All optional arguments identical to circle."
  (let [cs (.coordinate-system parent)
        cs-type (.cs-type cs)]
    (cond (= cs-type :polar)
          (polar-circle-r parent radius args)
          :default
          (rectangle-circle-r parent pc radius args))))
    


;; (defn circle-2p [parent p0 p1 & args]
;;   "(circle-2p parent p0 p1 [args....])
;;    Defines circle by opposing points on locus p0 and p1
;;    Optional arguments identical to circle"
;;   (let [pc (math/midpoint p0 p1)
;;         r (math/distance p0 pc)
;;         arglst* (atom [parent pc r])]
;;     (doseq [a args]
;;       (swap! arglst* (fn [q](conj q a))))
;;     (let [obj (apply circle-r @arglst*)]
;;       (.put-property! obj :defined-by :oposing-points)
;;       (.put-property! obj :construction-points [p0 p1])
;;       obj)))
