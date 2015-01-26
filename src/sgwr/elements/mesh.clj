(ns sgwr.elements.mesh
  "Defines composite meshes
   A mesh is a pseudo element, it is not single element but 
   collection of related elements."
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))

(let [counter* (atom 0)]
  (defn- get-mesh-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "mesh-%d" n))))))

(let [counter* (atom 0)]
  (defn- get-point-field-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "point-field-%d" n))))))


(defn mesh [parent p0 p1 spacing & {:keys [id color style width force-final]
                                    :or {id nil
                                         color :gray
                                         style 0
                                         width 1.0
                                         force-final true}}]
  "(mesh parent p0 p1 spacing :id :color :style :width :force-final)
    Create mesh of orthogonal lines. 
    parent  - SgwrElement, the parent group
    p0      - vector, bounding rectangle vertex p0 -> [x0 y0]
    p1      - vector, opposing bounding rectangle vertex p1 -> [x1 y1]
    spacing - vector, line spacing deltas [dx dy]
    :id     - keyword
    :color  -
    :style  -
    :width  -
    :force-final - Boolean, if true make sure final mesh lines are drawn. 
                   Depending on the relative values between the bounding 
                   rectangle size and the spacing deltas, the spacing of 
                   the final lines may be inconsistent with the other lines.
    Returns list holding all mesh lines."
  (let [[x0 y0] p0
        [x1 y1] p1
        [dx dy] spacing
        id (get-mesh-id id)
        acc* (atom ())]
    (doseq [y (range (min y0 y1)(+ (max y0 y1)(if force-final dy 0)) dy)]
      (let [obj (line/line parent [x0 y][x1 y] :id id :color color :style style :width width)]
        (swap! acc* (fn [q](cons obj q)))))
    (doseq [x (range (min x0 x1)(+ (max x0 x1)(if force-final dx 0)) dx)]
      (let [obj (line/line parent [x y0][x y1] :id id :color color :style style :width width)]
        (swap! acc* (fn [q](cons obj q)))))
    @acc*))

(defn point-field [parent p0 p1 spacing & {:keys [id color style size force-final]
                                           :or {id nil
                                                color :gray
                                                style [:dot]
                                                size 2
                                                force-finale true}}]
  "(point-field parent p0 p1 spacing :id :style :size :force-final)
   As with mesh except instead of drawing lines, place points 
   Returns list of all point objects."
  (let [[x0 y0] p0
        [x1 y1] p1
        [dx dy] spacing
        id (get-point-field-id id)
        acc* (atom ())]
    (doseq [x (range (min x0 x1)(+ (max x0 x1)(if force-final dx 0)) dx)]
      (doseq [y (range (min y0 y1)(+ (max y0 y1)(if force-final dy 0)) dy)]
        (let [p (point/point parent [x y] :id id :color color :style style :size size)]
          (swap! acc* (fn [q](cons p q))))))
    @acc*))

;; for non-polar coordinate systems
(defn radial-mesh [parent center radii rays & {:keys [id 
                                                      color style width
                                                      aunit 
                                                      ray-gap ray-length ray-color ray-style ray-width]
                                                :or {id nil
                                                     color :gray
                                                     style 0
                                                     width 1.0
                                                     aunit :deg
                                                     ray-gap 0
                                                     ray-color nil
                                                     ray-style nil
                                                     ray-width nil
                                                     ray-length nil}}]
  "(radial-mesh parent center radii rays 
      :id :color :style :width :aunit 
      :ray-gap :ray-length :ray-color :ray-style :ray-width)

   Draw concentric circles with optional rays converging at the center
   parent      - SgwrElement, parent group
   center      - vector [x0 y0] location of center
   radii       - vector [r1 r2 r3...] list of circle radii
   rays        - vector [a1 a2 a3 ...] list of ray angles (see aunit)
   :id         - keyword,
   :color      - circle color
   :style      - circle dash pattern
   :width      - circle line width
   :aunit      - keyword, angle unit, either :rad, :deg or :turn, default :deg
   :ray-gap    - float, distance between end of ray and center point
   :ray-length - float, length of rays, if not specified set to size of final circle
   :ray-color  - ray color, defaults to circle color
   :ray-style  - ray line dash pattern, defaults to circle style
   :ray-width  - ray line width, defaults to circle line width
   Returns list of all circle and line objects

   NOTE radial-mesh is specifically for non-polar coordinate systems.
   See also polar-mesh"
  (let [acc* (atom ())
        id (get-mesh-id id)
        ray-color (or ray-color color)
        ray-style (or ray-style style)
        ray-width (or ray-width width)]
    (if (seq radii)
      (doseq [r radii]
        (let [c (circle/circle-r parent center r 
                                 :id id
                                 :color color
                                 :style style
                                 :width width)]
          (swap! acc* (fn [q](cons c q))))))
    (if (seq rays)
      (let [length (or ray-length (last radii) 8)
            umap (cond (= aunit :rad) identity
                       (= aunit :deg) math/deg->rad
                       (= aunit :turn) math/turn->rad
                       :default
                       (do 
                         (utilities/warning (format "Invalid angle unit %s argument to radial-mesh" aunit))
                         identity))]
        (doseq [a rays]
          (let [theta (umap a)
                cs (math/cos theta)
                sn (math/sin theta)
                x0 (+ (first center)(* ray-gap cs))
                y0 (+ (second center)(* ray-gap sn))
                x1 (+ (first center)(* length cs))
                y1 (+ (second center)(* length sn))
                
                n (line/line parent [x0 y0] [x1 y1]
                             :id id
                             :color ray-color
                             :style ray-style
                             :width ray-width)]
            (swap! acc* (fn [q](cons n q)))))))
    @acc*))


(defn polar-mesh [parent radii rays & {:keys [id
                                              color style width
                                              ray-gap ray-length ray-color ray-style ray-width]
                                       :or {id nil
                                            color :gray
                                            style 0
                                            width 1.0
                                            ray-gap 0
                                            ray-color nil
                                            ray-style nil
                                            ray-width nil
                                            ray-length nil}}]
  "(polar-mesh parent radii rays 
        :id :color :style :width
        :ray-gap :ray-length :ray-color :ray-style :ray-width)

   Draw concentric circles with optional rays converging at the center
   parent      - SgwrElement, parent group
   radii       - vector [r1 r2 r3...] list of circle radii
   rays        - vector [a1 a2 a3 ...] list of ray angles 
                 (unit determined by coordinate-system)
   :id         - keyword,
   :color      - circle color
   :style      - circle dash pattern
   :width      - circle line width
   :ray-gap    - float, distance between end of ray and center point
   :ray-length - float, length of rays, if not specified set to size of final circle
   :ray-color  - ray color, defaults to circle color
   :ray-style  - ray line dash pattern, defaults to circle style
   :ray-width  - ray line width, defaults to circle line width
   Returns list of all circle and line objects

   NOTE polar-mesh is specifically for polar coordinate systems.
   See also radial-mesh"        
  (let [acc* (atom ())
        cs (.coordinate-system parent)
        aunit (second (.units cs))
        a45 (cond (= aunit :rad) (math/deg->rad 45)
                  (= aunit :deg) 45
                  (= aunit :turn) (math/deg->turn 45)
                  :default 
                  (do
                    (utilities/warning (format "Invalid angle unit %s or non-polar coordinate system %s arguments to polar-mesh" 
                                               aunit (.cs-type cs)))
                    identity))
        
        id (get-mesh-id id)
        ray-color (or ray-color color)
        ray-style (or ray-style style)
        ray-width (or ray-width width)]
    (if (seq radii)
      (doseq [r radii]
        (let [c (circle/circle parent [(- r) a45][r a45] :id id
                               :color color
                               :style style
                               :width width)]
          (swap! acc* (fn [q](cons c q))))))
    (if (seq rays)
      (let [length (or ray-length (last radii) 8)]
        (doseq [a rays]
          (let [obj (line/line parent [ray-gap a][length a] :id id
                               :color ray-color
                               :style ray-style
                               :width ray-width)]
            (swap! acc* (fn [q](cons obj q)))))))
    @acc*))
