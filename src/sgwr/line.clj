(ns sgwr.line
  "Defines line element in terms of end points"
  (:require [sgwr.attributes])
  (:require [sgwr.element])
  (:require [seesaw.graphics :as ssg])
  (:import java.awt.geom.Line2D))


(defn line
  ([]
     (line 0.0 0.0 1.0 1.0))
  ([x0 x1 y0 y1]
     (line [x0 y0][x1 y1]))
  ([p0 p1]
    (let [[x0 y0] p0
          [x1 y1] p1
          position* (atom [[(float x0)(float y0)]
                           [(float x1)(float y1)]])
          attributes* (atom (sgwr.attributes/attributes))
          obj (reify sgwr.element.Element
                
                (is-text? [this] false)

                (attributes [this] 
                  @attributes*)

                (attributes! [this att]
                  (reset! attributes* att))

                (construction-points [this]
                  @position*)

                ;; position [[x0 y0][x1 y1]]
                (position! [this points]
                  (reset! position* points))

                (shape [this cs]
                  (let [[p0 p1] @position*
                        q0 (.map-point cs p0)
                        q1 (.map-point cs p1)
                        [u0 v0] q0
                        [u1 v1] q1]
                    (java.awt.geom.Line2D$Double. u0 v0 u1 v1)))
                    
                (color [this]
                    (.color @attributes*))

                (stroke [this]
                  (let [w (.width @attributes*)
                        sty (.style @attributes*)
                        dashpat (sgwr.attributes/line-style sty)]
                    (ssg/stroke :width w :dashes dashpat)))

                (hidden? [this]
                  (.hidden? @attributes*))
                
                (filled? [this] false)
                
                (selected? [this]
                  (.selected? @attributes*))
                
                (clone [this]
                  (let [[p0 p1] @position* 
                        other (line p0 p1)]
                    (.attributes! other (.clone @attributes*))
                    other))
                
                (to-string [this]
                  (let [[p0 p1] @position*
                        [x0 y0] p0
                        [x1 y1] p1]
                    (format "Line [%f %f] [%f %f]" x0 y0 x1 y1))) )]
      obj)))
