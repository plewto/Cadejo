(ns sgwr.circle
  "Defines circle drawing element.
   Circles are defined by their center point and a point defining the radius.
   This is in contrast to ellipses which are defined in terms of a bounding
   rectangle"
  (:require [sgwr.attributes])
  (:require [sgwr.element])
  (:require [sgwr.utilities :as util])
  (:require [seesaw.graphics :as ssg]))

(defn circle 
  "Create circle with center point pc and point pr defining the radius"
  ([pc pr]
     (let [[xc yc] pc
           [xr yr] pr
           position* (atom [pc pr])
           attributes* (atom (sgwr.attributes/attributes))
           obj (reify sgwr.element.Element
                 
                 (element-type [this] :circle)

                 (attributes [this] 
                   @attributes*)
                 
                 (attributes! [this att]
                   (reset! attributes* att))
                 
                 (construction-points [this]
                   @position*)
                 
                 ;; position center point, point on locus [pc pr]
                 ;;
                 (position! [this points]
                   (reset! position* points))
                 
                 (shape [this cs]
                   (let [[pc pr] @position*
                         qc (.map-point cs pc)
                         qr (.map-point cs pr)
                         radius (util/distance qc qr)]
                     (ssg/circle (first qc)(second qc) radius)))
                 
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
                   (let [[pc pr] @position*
                         other (circle pc pr)]
                     (.attributes! other (.clone (.attributes other)))
                     other))
                 
                 (to-string [this]
                   (let [[pc pr] @position*
                         radius (util/distance pc pr)]
                     (format "Circle center %s radius %s" pc radius)))) ]
       obj)))
