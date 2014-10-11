(ns sgwr.rectangle
  "Defines rectangle and rounded-rectangle elements"
  (:require [sgwr.attributes])
  (:require [sgwr.element])
  ;(:require [sgwr.utilities :as util])
  (:require [seesaw.graphics :as ssg]))


(defn rectangle
  ([]
     (rectangle [-1.0 -1.0][1.0 1.0]))
  ([x0 y0 x1 y1]
     (rectangle [x0 y0][x1 y1]))
  ([p0 p1]
     (let [[x0 y0] p0
           [x1 y1] p1
           position* (atom [[(float x0)(float y0)]
                            [(float x1)(float y1)]])
           attributes* (atom (sgwr.attributes/attributes))
           obj (reify sgwr.element.Element
                 
                 (element-type [this] :rectangle)

                 (attributes [this] 
                   @attributes*)
                 
                 (attributes! [this att]
                   (reset! attributes* att))
                 
                 (construction-points [this]
                   @position*)
                 
                 ;; position as bounding rectangle 
                 ;; [[x0 y0][x1 y1]]
                 ;;
                 (position! [this points]
                   (reset! position* points))
                 
                 (shape [this cs]
                   (let [[p0 p1] @position*
                         q0 (.map-point cs p0)
                         q1 (.map-point cs p1)
                         [u0 v0] q0
                         [u1 v1] q1
                         w (- u1 u0)
                         h (- v1 v0)]
                     (ssg/rect u0 v0 w h)))
                 
                 (color [this]
                   (.color @attributes*))

                 (stroke [this]
                   (let [w (.width @attributes*)
                         sty (.style @attributes*)
                         dashpat (sgwr.attributes/line-style sty)]
                     (ssg/stroke :width w :dashes dashpat)))

                 (hidden? [this]
                   (.hidden? @attributes*))
                
                 (filled? [this] (.filled? @attributes*))
                
                 (selected? [this]
                   (.selected? @attributes*))
                
                 (clone [this]
                  (let [[p0 p1] @position* 
                        other (rectangle p0 p1)]
                    (.attributes! other (.clone @attributes*))
                    other))
                
                (to-string [this]
                  (let [[p0 p1] @position*
                        [x0 y0] p0
                        [x1 y1] p1]
                    (format "Rectangle [%f %f] [%f %f]" x0 y0 x1 y1))) )]
       obj))) 



(def rounded-rectangle-radius* (atom 16))

(defn rounded-rectangle
  ([]
     (rounded-rectangle  [-1.0 -1.0][1.0 1.0]))
  ([x0 y0 x1 y1]
     (rounded-rectangle [x0 y0][x1 y1]))
  ([p0 p1]
     (let [[x0 y0] p0
           [x1 y1] p1
           position* (atom [[(float x0)(float y0)]
                            [(float x1)(float y1)]])
           attributes* (atom (sgwr.attributes/attributes))
           obj (reify sgwr.element.Element
                 
                 (element-type [this] :rectangle)

                 (attributes [this] 
                   @attributes*)
                 
                 (attributes! [this att]
                   (reset! attributes* att))
                 
                 (construction-points [this]
                   @position*)
                 
                 ;; position as bounding rectangle 
                 ;; [[x0 y0][x1 y1]]
                 ;;
                 (position! [this points]
                   (reset! position* points))
                 
                 (shape [this cs]
                   (let [[p0 p1] @position*
                         q0 (.map-point cs p0)
                         q1 (.map-point cs p1)
                         [u0 v0] q0
                         [u1 v1] q1
                         w (- u1 u0)
                         h (- v1 v0)]
                     (ssg/rounded-rect u0 v0 w h @rounded-rectangle-radius*)))
                 
                 (color [this]
                   (.color @attributes*))

                 (stroke [this]
                   (let [w (.width @attributes*)
                         sty (.style @attributes*)
                         dashpat (sgwr.attributes/line-style sty)]
                     (ssg/stroke :width w :dashes dashpat)))

                 (hidden? [this]
                   (.hidden? @attributes*))
                
                 (filled? [this] (.filled? @attributes*))
                
                 (selected? [this]
                   (.selected? @attributes*))
                
                 (clone [this]
                  (let [[p0 p1] @position* 
                        other (rectangle p0 p1)]
                    (.attributes! other (.clone @attributes*))
                    other))
                
                (to-string [this]
                  (let [[p0 p1] @position*
                        [x0 y0] p0
                        [x1 y1] p1]
                    (format "Rectangle [%f %f] [%f %f]" x0 y0 x1 y1))) )]
       obj))) 
