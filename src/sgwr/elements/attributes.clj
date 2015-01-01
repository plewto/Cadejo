(ns sgwr.elements.attributes
  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.util.utilities :as utilities]))


(defn- bol [n](if n true false))

(defn create-attribute-map [id]
  {:id id
   :color (ucolor/color :white)
   :style 0
   :width 1.0
   :filled false
   :hidden false})

(defn str-rep-attribute-map [att]
  (format "Attributes id %-8s :color %s  :style %s  :width %s  :filled %s  :hidden %s"
          (:id att)
          (ucolor/str-rep-color (:color att))
          (:style att)
          (:width att)
          (:filled att)
          (:hidden att)))

(defprotocol Attributes

  (add!
    [this id amap]
    [this id])
 
  (current-id
    [this])

  (keys [this]
    (clojure.core/keys @maps*))

  (get 
    [this id]
    [this])

  (use!
    [this id])

  (remove!
    [this id])

  (color! 
    [this c])

  (color 
    [this])

  (style!
    [this st])

  (style
    [this])

  (width!
    [this w])

  (width
    [this])

  (fill! 
    [this flag])

  (filled?
    [this])

  (hide!
    [this flag])

  (hidden?
    [this])

  (to-string 
    [this verbosity depth])

  (dump 
    [this verbosity depth]
    [this verbosity]
    [this]) )

(defn attributes []
  (let [current-id* (atom :default)
        default (create-attribute-map :default)
        maps* (atom {:default default
                     :current default})]
    (reify Attributes

      (add! [this id att]
        (swap! maps* (fn [q](assoc q id (assoc att :id id)))))

      (add! [this id]
        (swap! maps* (fn [q](assoc q id (create-attribute-map id)))))

      (keys [this]
        (clojure.core/keys @maps*))

      (current-id [this]
        @current-id*)

      (get [this id]
        (let [att (clojure.core/get @maps* id)]
          (if (not att)
            (do
              (utilities/warning (format "attributes %s does not exists, using default" id))
              (:default @maps*))
            att)))

      (get [this]
        (:current @maps*))
   
      (use! [this id]
        (let [att (.get this id)]
          (reset! current-id* (:id att))
          (swap! maps* (fn [q](assoc q :current att)))
          att))

      (remove! [this id]
        (if (or (= id :current)(= id :default))
          (do
            (utilities/warning (format "Can not remove %s attributes" id))
            nil)
          (let [att (clojure.core/get @maps* id)]
            (swap! maps* (fn [q](dissoc q id)))
            att)))

      (color! [this c]
        (let [cobj (ucolor/color c)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :color cobj)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          att))

      (style! [this st]
        (let [n (utilities/map-style st)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :style n)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          att))

      (width! [this w]
        (let [n (float w)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :width n)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          att))

      (fill! [this flag]
        (let [att (assoc (clojure.core/get @maps* @current-id*)
                         :filled (bol flag))]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          att))

      (hide! [this flag]
        (let [att (assoc (clojure.core/get @maps* @current-id*)
                         :hidden (bol flag))]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          att))
      
      (color [this]
        (:color (:current @maps*)))

      (style [this]
        (:style (:current @maps*)))

      (width [this]
        (:width (:current @maps*)))

      (filled? [this]
        (:filled (:current @maps*)))

      (hidden? [this]
        (:hidden (:current @maps*)))

      (to-string [this verbosity depth]
        (let [sb (StringBuilder. 300)
              pad1 (utilities/tab depth)
              pad2 (utilities/tab (inc depth))]
          (.append sb (format "%sAttributes current-id =  %s\n"
                              pad1 @current-id*))
          (cond (= verbosity 1)
                (do 
                  (.append sb pad2)
                  (doseq [k (sort (remove (fn [q](= q :current))(.keys this)))]
                    (.append sb (format "%s " k)))
                  (.append sb "\n"))
                (> verbosity 1)
                (doseq [k (sort (remove (fn [q](= q :current))(.keys this)))]
                  (let [a (k @maps*)]
                    (.append sb (format "%s%s\n" pad2 (str-rep-attribute-map a)))))
                :default
                nil)
          (.toString sb)))

      
         (dump [this verbosity depth]
           (println (.to-string this verbosity depth)))

         (dump [this verbosity]
           (.dump this verbosity 0))

         (dump [this]
           (.dump this 2))))) 
      
(def default-attribute-set (attributes))
  
