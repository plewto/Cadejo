(ns sgwr.elements.attributes
  "Each drawing element has access to an attribute set which defines
  how the element is to rendered.  Attributes define color, style,
  width (or size), fill flag and hide flag. Each attribute has a
  keyword id and it is possible to switch attributes based on it's
  id. For example an element may have 'selected' and 'unselected'
  attributes which define the appearance under these two conditions.

  Attribute sets may either be contained within an element or they may
  be inherited from a parent element.

  Each element type is free to interpret attribute values in a unique
  manner. This is particularly true for the style, width and filled values."

  (:require [sgwr.util.color :as ucolor])
  (:require [sgwr.util.utilities :as utilities]))


(defn- bol [n](if n true false))

(defn create-attribute-map [id]
  "(create-attribute-map [id])
   Returns a map with the following keys
   :id - keyword
   :color - instance of java.awt.Color
   :style - int, 0 <= style
   :width - float 0 < width
   :filled - boolean
   :hidden - boolean"
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
    [this id]
    "(add! this id amap) (add! this id)
     Adds a new named set of attributes to this.
     id - keyword
     amap - an attributes map as returned by create-attribute-map
     If amap is not supplied an new map is created.
     Returns the added map")
 
  (current-id
    [this]
    "(current-id this)
    Returns the keyword id for the 'current' attributes map")

  (keys [this]
    (clojure.core/keys @maps*)
    "(keys this)
     Returns sorted list of attribute id keywords defined in this.
     Two id keywords are always defined :current and :default")

  (get 
    [this id]
    [this]
    "(get this id) (get this)
     Returns the attribute map with matching id. If the map does not
     exists return nil.  If id not specified return current map")

  (use!
    [this id]
    "(get this id)
     Make map with matching id the 'current' map and return it.
     If id does not exist return nil")

  (remove!
    [this id]
    "(remove! this id)
     Remove the map with indicated id. It is not possible to remove
     maps with the id :default or :current, or to remove the current 
     map. Returns the removed map or nil if no changes were made.")

  (color! 
    [this c]
    "(color! c)
     Sets color value of the current map. The argument c may be either an 
     instance of java.awt.Color or any valid argument to seesaw.color/color
     Returns the color object.")

  (color 
    [this]
    "(color this)
     Returns java.awt.Color of the current map")

  (style!
    [this st]
    "(style! this st)
     Sets the style value for the current map.
     st may be either a non-negative integer or keyword. 
     see sgwr.constants/style-map for valid style keywords.
     Returns the int style value.")

  (style
    [this]
    "(style this)
     Returns the style value for the current map")

  (width!
    [this w]
    "(width! this w)
     Sets the width value for the current map
     Returns the the float width value.")

  (width
    [this]
    "(width this)
     Returns float width value of current map.")

  (fill! 
    [this flag]
    "(fill! this flag)
     Sets fill value of current map
     Returns boolean.")

  (filled?
    [this]
    "(filled? this)
     Returns Boolean status of fill flag.")

  (hide!
    [this flag]
    "(hide! this flag)
     Sets hide flag of current map
     Returns Boolean.")

  (hidden?
    [this]
    "(hidden? this)
     Returns Boolean status of hide flag.")

  (to-string 
    [this verbosity depth]
    "(to-string this verbosity depth)
     Returns a string representation of this
     verbosity is a integer with higher values producing greater text
     depth is a non-negative integer which sets the indentation.
     Returns String.")

  (dump 
    [this verbosity depth]
    [this verbosity]
    [this]) )

(defn attributes []
  "(attributes)
   Create new instance of Attributes"
  (let [current-id* (atom :default)
        default (create-attribute-map :default)
        maps* (atom {:default default
                     :current default})]
    (reify Attributes

      (add! [this id att]
        (swap! maps* (fn [q](assoc q id (assoc att :id id))))
        att)

      (add! [this id]
        (swap! maps* (fn [q](assoc q id (create-attribute-map id)))))

      (keys [this]
        (sort (clojure.core/keys @maps*)))

      (current-id [this]
        @current-id*)

      (get [this id]
        (clojure.core/get @maps* id))

      (get [this]
        (:current @maps*))
   
      (use! [this id]
        (let [att (.get this id)]
          (if att 
            (do 
              (reset! current-id* (:id att))
              (swap! maps* (fn [q](assoc q :current att)))
              att)
            nil)))

      (remove! [this id]
        (if (or (= id :current)(= id :default)(= id @current-id*))
          (do
            (utilities/warning (format "Can not remove current or default attributes %s " id))
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
          cobj))

      (style! [this st]
        (let [n (utilities/map-style st)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :style n)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          n))

      (width! [this w]
        (let [n (float w)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :width n)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          n))

      (fill! [this flag]
        (let [f (bol flag)
              att (assoc (clojure.core/get @maps* @current-id*)
                         :filled f)]
          (swap! maps* (fn [q](assoc q @current-id* att)))
          (swap! maps* (fn [q](assoc q :current att)))
          f))

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
           (.dump this 2)) )))
         
      
(def default-attribute-set (attributes))
  
