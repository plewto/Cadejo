(ns sgwr.elements.attributes
  (:require [sgwr.constants :as constants])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.utilities :as utilities]))

(def default-color (uc/color constants/default-color))
(def default-style 0)
(def default-width 1.0)
(def default-size 1.0)

(def rollover-color (uc/color constants/default-rollover-color))


(defprotocol SgwrAttributes

  (add! 
    [this id])

  (remove!
    [this id])

  (remove-all!
    [this])

  (attribute-keys 
    [this])

  (get-attributes
    [this id]
    [this])

  (current-id
    [this])

  (use!
    [this id])

  (color! 
    [this id c]
    [this c])

  (style!
    [this id st]
    [this st])

  (width!
    [this id w]
    [this w])

  (size!
    [this id sz]
    [this sz])

  ;; fill value may be
  ;; false - inherit fill attribute from parent
  ;; true  - element is filled
  ;; :no   - element is not to be filled 
  ;;
  (fill!
    [this id flag]
    [this flag])
  
  ;; hide value may be
  ;; false - inhert hidden flag from parent
  ;; true  - object is hidden
  ;; :no   - object is NOT hidden
  ;;
  (hide!
    [this id flag]
    [this flag])

  (color
    [this])

  (style 
    [this])

  (width
    [this])

  (size
    [this])

  (filled? 
    [this])

  (hidden? 
    [this])

  (copy! 
    [this other id]
    [this other])

  (to-string
    [this verbosity depth])

  (dump 
    [this verbosity depth]
    [this verbosity]
    [this]))

(defn- create-attribute-map [id & {:keys [color style width size]
                                    :or {color nil
                                         style nil
                                         width nil
                                         size nil}}]
  {:id id
   :color (uc/color color)
   :style style
   :width width
   :size size
   :filled nil
   :hidden nil})

(def default-attribute-map (create-attribute-map :default
                                                 :color default-color
                                                 :style 0
                                                 :size 1.0
                                                 :width 1.0))

(def rollover-attribute-map (create-attribute-map :rollover
                                                  :color rollover-color))
  

(defn- str-rep-attribute-map [att]
  (format "{:id %-8s :color %-17s :style %4s :width %4s :size %4s :filled %-5s :hidden %-5s}"
          (:id att)
          (uc/str-rep-color (:color att))
          (:style att)
          (:width att)
          (:size att)
          (:filled att)
          (:hidden att)))

(defn- get-implicit-attributes [sgwratt id]
  (let [att (.get-attributes sgwratt id)]
    (if (not att)
      (do 
        (.add! sgwratt id)
        (.get-attributes sgwratt id))
      att)))

(defn attributes 

  ([](attributes nil))

  ([client]
   (let [maps* (atom {:default default-attribute-map
                      :rollover rollover-attribute-map})
         client* (atom nil)
         current-id* (atom :default)
         sgwratt (reify SgwrAttributes
                   
                   (add! [this id]
                     (let [att (create-attribute-map id)]
                       (swap! maps* (fn [m](assoc m id att)))
                       (.attribute-keys this)))
                   
                   (remove! [this id]
                     (if (= id @current-id*)
                       (utilities/warning (format "Can not remove current attributes %s" id))
                       (swap! maps* (fn [m](dissoc m id))))
                     (.attribute-keys this))
                   
                   (remove-all! [this]
                     (reset! maps* {:default default-attribute-map
                                    :rollover rollover-attribute-map})
                     (reset! current-id* :defaut))

                   (attribute-keys [this]
                     (sort (keys @maps*)))
                   
                   (get-attributes [this id]
                     (get @maps* id))
                   
                   (get-attributes [this]
                     (.get-attributes this @current-id*))

                   (current-id [this]
                     @current-id*)

                   (use! [this id]
                     (reset! current-id* id))
                   
                   (color! [this id c]
                     (let [c2 (uc/color c)
                           att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :color c2)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       c2))
                   
                   (color! [this c]
                     (let [id (or @current-id* :default)]
                       (.color! this id c)))
                   
                   (style! [this id st]
                     (let [st2 st ; (utilities/map-style st)
                           att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :style st2)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       st2))
                   
                   (style! [this st]
                     (let [id (or @current-id* :default)]
                       (.style! this id st)))

                   (width! [this id w]
                     (let [w2 (cond (number? w)(float (max w 0.1))
                                     (nil? w) nil
                                     :default 
                                     (do 
                                       (utilities/warning (format "Invalid width attribute: %s" w))
                                       nil))
                           att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :width w2)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       w2))

                   (width! [this w]
                     (let [id (or @current-id* :default)]
                       (.width! this id w)))

                   (size! [this id sz]
                     (let [sz2 (cond (number? sz)(float (max sz 0.1))
                                     (nil? sz) nil
                                     :default 
                                     (do 
                                       (utilities/warning (format "Invalid size attribute: %s" sz))
                                       nil))
                           att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :size sz2)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       sz2))

                   (size! [this sz]
                     (let [id (or @current-id* :default)]
                       (.size! this id sz)))

                   (fill! [this id flag]
                     (let [att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :filled flag)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       flag))

                   (fill! [this flag]
                     (let [id (or @current-id* :default)]
                       (.fill! this id flag)))

                   (hide! [this id flag]
                     (let [att1 (get-implicit-attributes this id)
                           att2 (assoc att1 :hidden flag)]
                       (swap! maps* (fn [q](assoc q id att2)))
                       flag))

                   (hide! [this flag]
                     (let [id (or @current-id* :default)]
                       (.hide! this id flag)))
                   
                   (color [this]
                     (:color (.get-attributes this)))
                       
                   (style [this]
                     (:style (.get-attributes this)))

                   (width [this]
                     (:width (.get-attributes this)))

                   (size [this]
                     (:size (.get-attributes this)))

                   (filled? [this]
                     (:filled (.get-attributes this)))

                   (hidden? [this]
                     (:hidden (.get-attributes this)))

                   (copy! [this other id]
                     (let [src (.get-attributes other id)
                           acc* (atom {})]
                       (doseq [[k v](seq src)]
                         (swap! acc* (fn [q](assoc q k v))))
                       (swap! maps* (fn [q](assoc q (:id src) @acc*)))
                       (id @maps*)))

                   (copy! [this other]
                     (.remove-all! this)
                     (doseq [k (.attribute-keys other)]
                       (.copy! this other k)))

                   (to-string [this verbosity depth]
                     (let [pad1 (utilities/tab depth)
                           pad2 (utilities/tab (inc depth))]
                       (cond (<= verbosity 0) 
                             ""
                           
                             (= verbosity 1)
                             (format "%sAttributes current %s  keys %s\n"
                                     pad1 @current-id* (.attribute-keys this))

                             :default
                             (let [sb (StringBuilder. 300)]
                               (.append sb (format "%sAttributes current %s\n"
                                                   pad1 @current-id*))
                               (doseq [k (.attribute-keys this)]
                                 (.append sb (format "%s%s\n"
                                                     pad2 
                                                     (str-rep-attribute-map (k @maps*)))))
                               (.toString sb)) ))) 

                     (dump [this verbosity depth]
                           (println (.to-string this verbosity depth)))

                     (dump [this verbosity]
                           (.dump this verbosity 0))

                     (dump [this]
                       (.dump this 2)) )]
     sgwratt))) 
