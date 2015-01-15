(ns sgwr.widgets.checkbox
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.util.color :as uc]))



(defn get-pressed-action 
  ([](get-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj env]
     (let [states (.get-property obj :states)
           csi (rem (inc (.get-property obj :current-state-index))
                    (count states))
           new-state (nth states csi)
           drw (.get-property obj :drawing)]
       (.put-property! obj :current-state-index csi)
       (.put-property! obj :current-state (nth states csi))
       (.use-attributes! obj new-state)
       (if drw (.render drw))
       (pfn obj env)))))
           
(defn get-exited-action 
  ([](get-exited-action (fn [& _])))
  ([xfn]
   (fn [obj ev]
     (let [states (.get-property obj :states)
           csi (.get-property obj :current-state-index)]
       (.use-attributes! obj (nth states csi))
       (xfn obj ev)))))
     
        
   

(defn empty-checkbox [parent id states]
  (let [grp (group/group parent :etype :checkbox :id id)]
    (.put-property! grp :states states)
    (.put-property! grp :current-state-index 0)
    (.put-property! grp :current-state (first states))
    (.put-property! grp :action-mouse-dragged  (fn [obj ev] ))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (get-exited-action))
    (.put-property! grp :action-mouse-pressed  (get-pressed-action))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    grp))


(defn test-checkbox [parent pc id]
  (let [states [:alpha :beta :gamma]
        grp (empty-checkbox parent id states)
        p1 (point/point grp pc :style 0 :size 2)
        c1 (circle/circle-r grp pc 2)]
    (set-attributes! p1 :alpha :size 2 :style :dot)
    (set-attributes! p1 :beta  :size 2 :style :box)
    (set-attributes! p1 :gamma :size 2 :style :triangle)
    grp))
    
