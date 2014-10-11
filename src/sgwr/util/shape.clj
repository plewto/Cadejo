(ns sgwr.util.shape
  (:import java.awt.geom.Path2D))


(defn combine-shapes 
  "Combine two instances of java.awt.Shape"
  ([s1 s2 connect]
     (let [p1 (java.awt.geom.Path2D$Double. s1)]
       (.append p1 s2 connect)
       p1))
  ([s1 s2]
     (combine-shapes s1 s2 false)))
