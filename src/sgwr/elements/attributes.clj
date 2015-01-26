(ns sgwr.elements.attributes
  "Each drawing element has a set of named attributes which define how
  the element is to be rendered. The use! method instructs the element
  to use a specific attribute set. The attributes include color, style,
  width, size, fill and hide. Not all elements make use of all
  attribute values."
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
    [this id]
    "(add! this id)
     Create a new attributes map with given id name and add it to this.
     Returns list of attribute keys.")

  (remove!
    [this id]
    "(remove! this id)
     Removes the indicated attribute map from this.
     It is not possible to remove the 'current' attribute map.
     Returns list of attribute keys")

  (remove-all!
    [this]
    "(remove-all! this)
     Removes all attribute maps from this. 
     New attributes maps with id's :default and :rollover are automatically
     added back to this.
     Returns the current attribute map")

  (attribute-keys 
    [this]
    "(attribute-keys this)
    Returns list of all defined attribute id keys.")

  (get-attributes
    [this id]
    [this]
    "(get-attributes this id)
     (get-attributes this)
     Returns the attribute map with matching id or nil if no attributes
     matches.  If id not specified returns the current attribute map")

  (current-id
    [this]
    "(current-id this)
     Returns the id of the current attribute map")

  (use!
    [this id]
    "(use! this id)
     Indicate that attribute map with matching id is now the 'current' map.")

  (color! 
    [this id c]
    [this c]
    "(color! this id c)
     (color! this c)
     Sets color value for indicated attribute map.
     If id does not match any existing attribute map create a new map
     If id is not specified use the current map
     See sgwr.util.color/color for valid color argument types
     Returns java.awt.Color")

  (style!
    [this id st]
    [this st]
    "(style! this id st)
     (style! this st)
     Sets the style value for indicated attribute map
     If id does not match any existing attribute map create a new map.
     If id not specified use the current map.
     style argument st is interpreted differently for each element type
     Returns st")

  (width!
    [this id w]
    [this w]
    "(width! this id w)
     (width! this w)
     Sets width value for indicated attribute map
     If id does not match any existing attribute map create a new map.
     If id not specified use the current map.
     Returns w")

  (size!
    [this id sz]
    [this sz]
    "(size! this id sz)
     (size! this sz)
     Sets size value for indicated attribute map
     If id does not match any existing attribute map create a new map.
     If id not specified use the current map.
     Returns sz")

  (fill!
    [this id flag]
    [this flag]
    "(fill! this id flag)
     (fill! this flag)
     Sets fill value for indicated attribute map
     If id does not match any existing attribute map create a new map.
     If id not specified use current map.
     flag may have one of three values
        false - inherit fill value from parent object
        true  - object is to be filled
        :no   - object is not filled.
    Returns flag")

  (hide!
    [this id flag]
    [this flag]
     "(hide! this id flag)
      (hide! this flag)
      Sets hide value for indicated attribute map
      If id does not match any existing attribute map create a new map.
      If id not specified use current map.
      flag may have one of three values
         false - inherit hide value from parent object
         true  - object is to be hidden
         :no   - object is not hidden
     Returns flag")

  (color
    [this]
    "(color this)
     Returns java.awt.Color of current attribute map")

  (style 
    [this]
    "(style this)
     Returns style value of current attribute map")

  (width
    [this]
    "(width this)
     Returns width value of current attribute map")

  (size
    [this]
    "(size this)
     Returns size value of current attribute map")

  (filled? 
    [this]
    "(filled? this)
     Returns fill value of current attribute map
     Value will either be true, false or :no")

  (hidden? 
    [this]
    "(hidden? this)
     Returns hidden value of current attribute map
     Value will either be true, false or :no")

  (copy! 
    [this other id]
    [this other]
    "(copy! other id)
     (copy! other)
     Copy attribute values from this to other
     If id specified copy only values for specified map
     Returns other")

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

;; Returns the indicated attribute map 
;; If no such map exist create it.
;;
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
                     (reset! current-id* :default))

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
                     (let [st2 st 
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
                       (.copy! this other k))
                     other)

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
