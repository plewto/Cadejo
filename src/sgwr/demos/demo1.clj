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


(def drw1 (let [drw (drw/native-drawing 600 200)
                root (.root drw)]
            (.background! drw :black)
            (let [y 10 
                  x* (atom 10)
                  x-delta 32
                  grp1 (grp/group root :grp1)]
              (.color! grp1 :green)
              (.width! grp1 2)
              (doseq [st (keys constants/point-styles)]
                (let [p (point/point grp1 @x* y)]
                  (swap! x* (fn [x](+ x x-delta)))
                  (.style! p st)
                  (.color! p :green)
                  (.width! p 2)))
              )
            (.render drw)
            ;(ss/config! (.canvas drw) :size [600 :by 200])
            drw))

(def pan-main (ss/border-panel :north (.canvas drw1)
                               ))

(def f (ss/frame :title "SGWR Demo 1"
                 :content pan-main
                 :on-close :dispose
                 :size [700 :by 500]))

(ss/show! f)
                 
                    
                
