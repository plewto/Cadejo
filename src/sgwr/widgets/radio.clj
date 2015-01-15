(ns sgwr.widgets.radio
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.group :as group]))

(defn clear-radio-button-list! [rbl*]
  (doseq [b @rbl*]
    (.select! b false)
    (.use-attributes! b :default))
  nil)

(defn select-radio-button! [b rbl*]
  (clear-radio-button-list! rbl*)
  (.select! b true)
  (.use-attributes! b :selected)
  b)

(defn get-pressed-action 
  ([](get-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj ev]
     (let [rbl* (.get-property obj :radio-button-list* (atom []))
           drw (.get-property obj :drawing)]
       (clear-radio-button-list! rbl*)
       (.select! obj true)
       (.use-attributes! obj :selected)
       (if drw (.render drw))
       (pfn obj ev)))))

(defn get-exited-action 
  ([](get-exited-action (fn [& _])))
  ([xfn]
   (fn [obj ev]
     (let [flag (.selected? obj)
           drw (.get-property obj :drawing)]
       (if flag
         (.use-attributes! obj :selected))
       (xfn obj ev)))))


(defn empty-radio-button [parent id rbl*]
  (let [grp (group/group parent :etype :radio-button :id id)]
    (.put-property! grp :radio-button-list* rbl*)
    (.put-property! grp :action-mouse-dragged  (fn [obj ev] ))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (get-exited-action))
    (.put-property! grp :action-mouse-pressed  (get-pressed-action))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (swap! rbl* (fn [q](conj q grp)))
    grp))

(defn test-radio-button [parent pc id rbl*]
  (let [grp (empty-radio-button parent id rbl*)
        c1 (circle/circle-r grp pc 2 :id (keyword (format "%s-c1" (name id))))]
    (set-attributes! c1 :default  :color :white :style 0 :width 1 :fill :no)
    (set-attributes! c1 :selected :fill true)
    grp))
