(ns cadejo.ui.util.factory
  (:require [seesaw.core :as ss])
  (:require [seesaw.border :as ssb])
)


(defn title-border [& {:keys [title top right bottom left] 
                       :or {title ""
                            top 2
                            right 2
                            bottom 2
                            left 2}}]
  (ssb/compound-border
   (ssb/to-border (str title))
   (ssb/empty-border :top top
                     :right right
                     :bottom bottom
                     :left left)))
                  
(def empty-border ssb/empty-border)
(def line-border ssb/line-border)
