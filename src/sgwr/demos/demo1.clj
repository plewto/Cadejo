(ns sgwr.demos.demo1
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.drawing :as drw])
  (:require [sgwr.elements.group :as grp])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rectangle])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.elements.image :as image])
  (:require [seesaw.core :as ss]))


(def drw1 (drw/native-drawing 600 200))

(def root (.root drw1))
(def grp1 (grp/group root :grp1))
(def p1 (point/point grp1 20 20))
(def p2 (point/point grp1 40 20))
(def p3 (point/point grp1 60 20))



(.width! grp1 2)
(.color! grp1 :green)

(.use-attributes! grp1 :default)
(.render drw1)
(def pan-main (ss/border-panel :north (.canvas drw1)
                               ))

(def f (ss/frame :title "SGWR Demo 1"
                 :content pan-main
                 :on-close :dispose
                 :size [700 :by 500]))

(ss/show! f)

(comment ----------------------------------------------------
---------------------------------------------------------------- END COMMENT)
                 
(defn rl [] (use 'sgwr.demos.demo1 :reload))
(defn rla [] (use 'sgwr.demos.demo1 :reload-all))
(defn exit [] (System/exit 0))                    
                
