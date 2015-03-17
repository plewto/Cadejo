(ns sgwr.components.point
  "Defines point drawing components.
   Despite being called 'points', point objects may take any number of shapes.
   The key concept for point objects is that they have no
   dimension. Their shape and size are not effected by the
   coordinate-system view. 
   attributes:
       color -
       style - additive list of components
       width - ignored
       size  - fixed size as some multiple of pixels
       fill  - ignored 
       hide  -

       Point shapes are specified by adding together fundamental shapes

           :pixel :dot :bar :dash :diag :diag2 :triangle 
           :chevron-n :chevron-e :chevron-s :chevron-w 
           :edge-n :edge-e :edge-s :edge-w :box 

       in a vector, to define a 'cross' use [:bar :dash]
       
       The dot and box shapes may be filled by including :fill
       Note that if :fill is used with any shape besides :dot 
       and :box the point will not be rendered."
  (:require [sgwr.components.component])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.stroke :as ustroke])
  (:import java.awt.geom.Line2D
           java.awt.geom.Rectangle2D
           java.awt.geom.Ellipse2D
           java.awt.BasicStroke))

(def ^:private size-quant 2)
(def ^:private half-quant (* 1/2 size-quant))

(def ^:private point-styles {:default   0x020
                             :fill      0x00001
                             :pixel     0x00010
                             :dot       0x00020
                             :bar       0x00040
                             :dash      0x00080
                             :diag      0x00100
                             :diag2     0x00200
                             :triangle  0x00400
                             :chevron-n 0x00800
                             :chevron-e 0x01000
                             :chevron-s 0x02000
                             :chevron-w 0x04000
                             :edge-n    0x08000
                             :edge-e    0x10000
                             :edge-s    0x20000
                             :edge-w    0x40000
                             :box       0x80000
                             :cross     (bit-or 0x08 0x10)
                             :x         (bit-or 0x20 0x40)
                             :arrow-n   (bit-or 0x08 0x100)
                             :arrow-s   (bit-or 0x08 0x400)
                             :arrow-e   (bit-or 0x10 0x200)
                             :arrow-w   (bit-or 0x10 0x800)})
(defn- line 
  ([x0 y0 x1 y1]
     (java.awt.geom.Line2D$Double. x0 y0 x1 y1))
  ([p0 p1]
     (let [[x0 y0] p0
           [x1 y1] p1]
       (line x0 y0 x1 y1))))

(defn- pixel [x y _]   ;; pixel style ignores size
  (line x y (inc x) y))

(defn- dash [x y size]
  (let [half (* half-quant size)]
    (line (- x half) y (+ x half) y)))

(defn- bar [x y size]
  (let [half (* half-quant size)]
    (line x (- y half) x (+ y half))))

(defn- diag [x y size]
  (let [half (* half-quant size)]
    (line (- x half)(- y half)(+ x half)(+ y half))))

(defn- diag2 [x y size]
  (let [half (* half-quant size)]
    (line (- x half)(+ y half)(+ x half)(- y half))))

(defn- box [x y size]
  (let [half (* half-quant size)
        width (* size-quant size)]
    (java.awt.geom.Rectangle2D$Double.
     (- x half)(- y half) width width)))

(defn- dot [x y size]
  (let [half (* half-quant size)
        width (* size-quant size)]
    (java.awt.geom.Ellipse2D$Double.
     (- x half)(- y half) width width)))

(defn- triangle [x y size]
  (let [half (* half-quant size)
        y-base (+ y half)
        base (line (- x half) y-base (+ x half) y-base)
        s1 (line (- x half) y-base x (- y half))
        s2 (line (+ x half) y-base x (- y half))]
    (utilities/fuse base s1 s2)))

(defn- chevron-e [x y size]
  (let [half (* size half-quant)
        whole (* size size-quant)
        x1 (+ x whole)
        y0 (+ y half)
        y2 (- y half)
        s1 (line x y0 x1 y)
        s2 (line x y2 x1 y)]
    (utilities/combine-shapes s1 s2)))

(defn- chevron-w [x y size]
  (let [half (* size half-quant)
        whole (* size size-quant)
        x1 (- x half)
        x2 (+ x half)
        y0 (+ y half)
        y2 (- y half)
        s1 (line x1 y x2 y0)
        s2 (line x1 y x2 y2)]
    (utilities/combine-shapes s1 s2)))

(defn- chevron-n [x y size]
  (let [half (* size half-quant)
        x1 (- x half)
        x2 (+ x half)
        y1 (- y (* size size-quant))
        s1 (line x1 y x y1)
        s2 (line x2 y x y1)]
    (utilities/combine-shapes s1 s2)))

(defn- chevron-s [x y size]
  (let [half (* size half-quant)
        x1 (- x half)
        x2 (+ x half)
        y1 (+ y (* size size-quant))
        s1 (line x1 y x y1)
        s2 (line x2 y x y1)]
    (utilities/combine-shapes s1 s2)))

(defn- edge-n [x y size]
  (let [half (* half-quant size)
        y2 (- y half)]
    (line (- x half) y2 (+ x half) y2)))

(defn edge-s [x y size]
  (let [half (* half-quant size)
        y2 (+ y half)]
    (line (- x half) y2 (+ x half) y2)))

(defn edge-e [x y size]
  (let [half (* half-quant size)
        x2 (+ x half)]
    (line x2 (- y half) x2 (+ y half))))

(defn edge-w [x y size]
  (let [half (* half-quant size)
        x2 (- x half)]
    (line x2 (- y half) x2 (+ y half))))

(def ^:private shape-functions {0x00010    pixel     
                                0x00020    dot      
                                0x00040    bar       
                                0x00080    dash      
                                0x00100    diag     
                                0x00200    diag2     
                                0x00400    triangle  
                                0x00800    chevron-n 
                                0x01000    chevron-e 
                                0x02000    chevron-s 
                                0x04000    chevron-w 
                                0x08000    edge-n    
                                0x10000    edge-e    
                                0x20000    edge-s    
                                0x40000    edge-w    
                                0x80000    box})

(defn- shape-fn [obj]
  (let [cs (.coordinate-system obj)
        p (first (.points obj))
        [u v](.map-point cs p)
        sty (.style obj)
        size (.size obj)
        acc* (atom [])
        i* (atom 0x10)]
    (while (< @i* 0x100000)
      (if (not (zero? (bit-and @i* sty)))
        (let [sfn (get shape-functions @i*)]
          (swap! acc* (fn [q](conj q (sfn u v size))))))
      (swap! i* (fn [q](* q 2))))
    (apply utilities/fuse @acc*)))
    

(defn render-point [pobj g2d]
  (let [sty (.style pobj)
        shape (.shape pobj)]
    (.setStroke g2d ustroke/default-stroke)
    (if (pos? (bit-and sty 0x1)) 
      (.fill g2d shape)
      (.draw g2d shape))))

(defn style-fn [& args]
  (let [acc* (atom 0)]
    (doseq [a args]
      (cond (number? a)
            (swap! acc* (fn [q](+ q a)))

            (keyword? a)
            (swap! acc* (fn [q](+ q (get point-styles a 0))))

            :default
            (utilities/warning (format "Unknown point-style %s" a))))
    (if (< @acc* 0x10) 
      (reset! acc* (:default point-styles)))
    @acc*))

(defn- distance-fn [obj q]
  (math/distance (first (.points obj)) q))

(defn- update-fn [_ pnts] pnts)
  
(defn- bounds-fn [obj points]
  (let [p (first points)]
    [p p]))

(def ^:private point-function-map {:shape-fn shape-fn
                                   :contains-fn (constantly false)
                                   :distance-fn distance-fn
                                   :update-fn update-fn
                                   :bounds-fn bounds-fn
                                   :style-fn style-fn})

(def locked-properties [])

(defn point 
  "(point parent p :id :color :style :size)
   Create point object
   parent - SgwrComponent, the parent group
   p      - vector, the position [x y]
   :id    - keyword
   :color - 
   :style - vector of basic shapes
   :size  - int."
  ([parent p & {:keys [id color style size]
                :or {id :new-point
                     color (uc/color :white)
                     style [:dot]
                     size 1.0}}]
   (let [obj (sgwr.components.component/create-component :point 
                                                   parent 
                                                   point-function-map 
                                                   locked-properties)]
     (if parent (.set-parent! obj parent))
     (.set-points! obj [p])
     (.put-property! obj :id id)
     (.color! obj :default color)
     (.style! obj :default style)
     (.size! obj :default size)
     (.use-attributes! obj :default)
     obj)))
