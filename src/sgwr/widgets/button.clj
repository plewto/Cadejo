(ns sgwr.widgets.button
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:import javax.swing.SwingUtilities))


;; Time in ms that button uses 'pressed' attributes 
;; in responce to mouse click before returning to 
;; original apperenace.
;;
(def hold-time 200) 

(defn get-pressed-action 
  ([](get-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj ev]
     (let [drw (.get-property obj :drawing)]
       (if drw
         (do 
           (.use-temp-attributes! obj :pressed)
           (.render drw)
           (SwingUtilities/invokeLater
            (proxy [Runnable][]
              (run []
                (Thread/sleep hold-time)
                (.restore-attributes! obj)
                (.render drw))))))
       (pfn obj ev)))))

(defn get-released-action 
  ([](get-released-action (fn [& _])))
  ([rfn]
   (fn [obj ev]
     (let [drw (.get-property obj :drawing)]
       (if drw
         (let [cs (.coordinate-system obj)
               p [(.getX ev)(.getY ev)]
               q (.inv-map cs p)]
           (.use-attributes! obj (if (.contains? obj q) :rollover :default))
           (.render drw))))
     (rfn obj ev))))

(defn empty-button [parent id]
  (let [grp (group/group parent :etype :button :id id)]
    (.put-property! grp :action-mouse-dragged  (fn [obj ev] ))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (get-pressed-action))
    (.put-property! grp :action-mouse-released (get-released-action))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    grp))

(defn test-button [parent p0 p1 id]
  (let [grp (empty-button parent id)
        r1 (rect/rectangle grp p0 p1 :id (keyword (format "%s-r1" (name id))))]
    (set-attributes! r1 :default :color :white :style 0 :width 0 :fill false)
    (set-attributes! r1 :pressed :color constants/default-pressed-color :width 0 :fill false)
    grp))
    
