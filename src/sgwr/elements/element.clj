(println "--> sgwr.elements.element")

;; NOTE:
;;    By default elements inherit attributes from their parent.
;;    The set-local-attributes! method is used to establish a local set of
;;    attributes instead. The following attribute modification methods
;;    implicitly creates a local attribute set: color!, style!, width!,
;;    fill! and hide!
;;

(ns sgwr.elements.element
  "SgwrElement defines the basic interface for drawing elements.
  Each element is a node which automatically inherits properties and
  attributes from it's ancestors. The terms 'node' and 'element' are
  used interchangeably here. Nodes may broadly be divided into
  2 classes, leaf nodes and internal nodes.

  Each element has a set of properties in the form of key/value
  pairs. If a specific property is not defined for an element the
  property value is inherited from the parent node, or a default value
  is used.  Each element type also defines a set of 'locked'
  properties which may not be removed. The value of locked properties
  may be changed but the element is guaranteed to always have such locked 
  properties defined. 

  Attributes are a separate set of values used to define how an
  element is to be rendered, I.E. color, line-style, width, etc. Like
  properties, attributes are inherited from parent to child. Changing
  the current attribute for a parent node causes it to send a message
  to all of it's children nodes. If the child nodes defines an
  attribute by the same name the it switches to that attribute."
 
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.elements.attributes :as att])
  (:require [sgwr.cs.coordinate-system]))

(defprotocol SgwrElement

  (element-type 
    [this]
    "(element-type this)
     Returns keyword identification for element type")

  (parent
    [this]
    "(parent this)
     Returns the parent node of this or nil")

  (set-parent!
    [this parent]
    "(set-parent! this parent)
     Sets the parent node for this. The parent's add-child method is
     automatically called.
     Returns vector of parent's children")

  (add-child! 
    [this other]
    "(add-child! this parent)
     Adds this as a child node to parent.
     Do not call add-child! directly, use add-parent! instead.
     Returns vector of parent's child node.")

  (children 
    [this]
    "(children this)
     Returns vector of child nodes")

  (child-count 
    [this]
    "(child-count this)
     Returns number of child nodes")

  (has-children?
    [this]
    "(has-children? this)
      Convenience method returns true if child count is greater then 0")

  (is-root? 
    [this]
    "(is-root? this)
     Convenience method returns true if parent node is nil")
  
  (is-leaf?
    [this]
    "(is-leaf? this)
     Convenience method returns true if child-count is 0")

  (locked-properties 
    [this]
    "(locked-properties this)
     Returns list of locked property keys")

  (put-property!
    [this key value]
    "(put-property! this key value)
     Assign local value to property key
     Returns map of local properties.")

  (get-property
    [this key default]
    [this key]
    "(get-property this key default)
     (get-property this key) Returns value for property key. If the
     property is not defined by this node then return the value from the
     parent node. If the property is not defined by any of the ancestor
    nodes return default. Unless otherwise specified the default return
    value is nil")

  (remove-property!
    [this key]
    "(remove-property! this key)
     Remove the definition of the property key from this.
     If key is a 'locked' property display a warning message and return
     nil, otherwise return a map of the local properties after key has been
    removed.") 

  (property-keys
    [this local-only]
    [this]
    "(property-keys this local-only)
     (property-keys this)
     Returns a list of all defined property keys. If local-only is true
     return only those properties defined by this, otherwise return keys
     for the defined properties for this and all of the ancestor nodes of
     this. local-only is false by default.")

  (has-property? 
    [this key local-only]
    [this key]
    "(has-property? this key local-only)
     (has-property? this key)
     Convenience method returns true if key is a defined property of this.
     If local-only is true only consider properties defined by this,
     otherwise consider all properties defined by this and all ancestor
     nodes of this. local-only is false by default.")

  ;; Attributes

  (attributes-inherited? 
    [this]
    "(attributes-inherited? this)
     Returns true if this node defines it's own attribute set.")

  (set-local-attributes! 
    [this att]
    [this]
    "(set-local-attributes! this att)
     (set-local-attributes! this)
     Set attributes att as the local attributes for this. If att is not
     specified a new attributes object is created and used.
     Returns instance of sgwr.elements.attributes.Attributes")

  (remove-local-attributes!
    [this]
    "(remove-local-attributes! this)
     Remove local attributes if any, returns nil")

  (get-attributes
    [this]
    "(get-attributes this)
     Returns the attribute object used by this. 
     The result will either be locally defined or inherited. Care should
     be used when manipulating inherited attributes.")
    
  (add-attributes!
    [this id]
    "(add-attributes! this id)
     Add a named attribute set to this. 
     A local attributes object is implicitly created.
     Returns a map")

  (local-attributes-exist? 
    [this id]
    "(local-attributes-exist? this id)
     Returns true if an attribute map with given id is defined for this.")

  (current-attribute-id
    [this]
    "(current-attribute-id this)
     Return the keyword id for the currently selected attribute map.")

  (attribute-keys 
    [this]
    "(attribute-keys this)
     Return list of keywords for all defined attribute maps")

  (use-attributes!
    [this id]
    "(use-attributes! this id)
     Make attributes with id the 'current' attributes if no matching
     attribute map is defined then ignore the change. In either case
     broadcast the change to all child nodes. Returns nil.")

  (remove-attributes!
    [this id]
    "(remove-attributes! this id)
     Remove the matching attribute map. It is not possible to remove an
    attribute map if the attributes are inherited.")

  (color!
    [this c]
    [this id c]
    "(color! this id c)
     (color! this c)
     Set the color for the current attributes. A local attributes object is
     implicitly created if needed. If id is specified create or use an
     attribute map with matching id. The color argument c may be either an
     instance of java.awt.Color or any valid argument to seesaw.color/color 
     Alpha values are supported. Returns the color object.")

  (style!
    [this st]
    [this id st]
    "(style! this id st)
     (style! this st)
     Set the style value for the current attribute. A local attributes
     object is implicitly created if needed. If id is specified create or use
     an attribute map with matching id. The style argument may be either
     numeric or symbolic. See sgwr.constants/style-map for possible
     symbolic values. Each element type interprets the style value
     differently. For group elements style is ignored except to serve as an
     inherited value. Point objects use style to define the point
     shape. Line, rectangle and circle objects use style as a
     dash-pattern. For text objects style sets the font.")

  (width!
    [this w]
    [this id w]
    "(width! this id w)
     (width! this w)
     Sets width (size) value for current attributes.  A local attributes
     object is implicitly created if needed. If id is specified create or use
     an attribute map with matching id. The width argument should be a
     float value greater then 0.0")

  (fill!
    [this f]
    [this id f]
    "(fill! this id flag)
     (fill! this flag)
     Sets the fill flag for the current attributes.  A local attributes
     object is implicitly created if needed. If id is specified create or use
     an attribute map with matching id.") 

  (hide!
    [this f]
    [this id f]
    "(hide! this id flag)
     (hide! this flag)
     Sets the hide flag for the current attributes.  A local attributes
     object is implicitly created if needed. If id is specified create or use
     an attribute map with matching id. Hidden objects are not rendered")
  
  (select! 
    [this f]
    "(select! this flag)
     Sets the selected flag for this. Note that selection status is not
     part of the attributes mechanism.")
  
  (color
    [this]
    "(color this)
     Returns the current attribute color")

  (style
    [this]
    "(style this)
     Returns the current attributes style")

  (width
    [this]
    "(width this)
     Returns the current attributes width")

  (filled? 
    [this]
    "(filled? this)
     Returns the current attributes fill flag")

  (hidden?
    [this]
    "(hidden? this)
     Returns the current attributes hide flag")
  
  (selected?
    [this]
    "(selected? this)
     Returns the selection state of this. Note that object selection is not
     part of the attributes mechanism but it is inherited. If any
     ancestor node to this is selected then this is also
     selected. Conversely this may be selected  while the ancestors are not
     selected.")

  (set-coordinate-system!
    [this cs]
    "(set-coordinate-system! this cs)
     Sets the coordinate system for this. If not established the coordinate
     system is automatically inherited from the parent node.")

  (coordinate-system
    [this]
    "(coordinate-system this)
     Returns the coordinate-systems in use for this. The coordinate system
     will either be defined locally or be inherited.")

  (set-update-hook!
    [this hfn]
    "(set-update-hook! this hfn)
     Sets a hook function to be executed whenever the set-points! method is
     called. The function should takes two arguments (hfn this points)
     the first is this and the second is the vector of construction points
     -after- they have been updated.")

  (points
    [this]
    "(points this)
     Returns a vector of construction points which define the position/shape
     of this. The result is always a nested vector of form 
     [[x0 y0][x1 y1]...[xn yn]]") 

  (set-points!
    [this pnts]
    "(set-points! this pnts)
     Set the construction points which define the position and size/shape of
     this. Each element type will have it's own interpretation for these
     points and in some cases (groups) may ignore them. 
     The points argument is always a nested vector of form 
     [[x0 y0][x1 y0]...[xn yn]] Where pairs [xi yi] make 'sence' to the
     coordinate-system in use.")

  (shape 
    [this]
    "(shape this)
     Returns an instance of java.awt.Shape")

  (bounds 
    [this]
    "(bounds this)
     Returns vector [[x0 y0][x1 y1]] defining the rectangular bounds for
     this object. Point and text objects return a single point 
     [[x y][x y]] regardless for their on screen image.")

  (contains?
    [this q]
    "(contains? this q)
     Returns true if this object contains the point q. For group objects
     contains? is true if it is true for any of the group's child
     objects. For some objects, (points, lines and text) contains? always
     returns false.")

  (distance 
    [this q]
    "Returns the distance between this object and point q.
     If an object contains the point the the distance is 0.")

  (to-string 
    [this verbosity depth])

  (dump 
    [this verbosity depth]
    [this verbosity]
    [this])
)


;; Add local attributes set to element 
;; If one does not already exist.
;;
;; If id specified create attribute map with id
;;
(defn- add-implicit-local-attributes! 
  ([elem]
   (if (.attributes-inherited? elem)
     (.set-local-attributes! elem)))

  ([elem id]
   (add-implicit-local-attributes! elem)
   (.add-attributes! elem id)
   (.use-attributes! elem id)))


(def reserved-property-keys '(:id :color :style :width :size :filled :hidden :current-attributes :selected))

(defn create-element
  "(create-element etype fnmap)
   (create-element etype parent fnmap locked)
   Create a new SgwrElement object 
   
   etype - keyword 
   parent - nil or instance of SgwrElement
   fnmap - map which defines certain aspects of the element. Each
           element type will define it's own function-map 
   locked - list of locked property keywords.
 
   See other files in sgwr.elements for usage examples."

  ([etype fnmap]
   (create-element etype nil fnmap {}))

  ([etype parent fnmap locked]
   (let [parent* (atom parent)
         coordinate-system* (atom nil)
         children* (atom [])
         locked-properties* (distinct (flatten (merge reserved-property-keys locked)))
         properties* (atom {})
         attributes* (atom nil)
         ;selected* (atom false)
         update-hook* (atom (fn [& _] nil))
         points* (atom [])
         elem (reify SgwrElement
       
               (element-type [this] etype)
               
               (parent [this] @parent*)

               (set-parent! [this p]
                 (reset! parent* p)
                 (.add-child! p this))
               
               (add-child! [this other]
                 (if (utilities/not-member? other @children*)
                   (do
                     (swap! children* (fn [q](conj q other))))))
               
               (children [this]
                 @children*)

               (child-count [this] (count @children*))

               (has-children? [this](pos? (.child-count this)))

               (is-root? [this] (nil? parent))

               (is-leaf? [this] (not (.has-children? this)))

               (put-property! [this key value]
                 (swap! properties* (fn [q](assoc q key value))))
               
               (get-property [this key default]
                 (or (get @properties* key)
                     (and parent (.get-property parent key))
                     default))
               
               (get-property [this key]
                 (.get-property this key nil))
               
               (locked-properties [this](.keys @locked-properties*))

               (remove-property! [this key]
                 (if (not (get @locked-properties* key))
                   (swap! properties* (fn [q](dissoc q key)))
                   (do
                     (utilities/warning (format "Can not remove locked property %s from %s element"
                                                key (.element-type this)))
                     nil)))
               
               (property-keys [this local-only]
                 (if local-only
                   (sort (keys @properties*))
                   (let [acc* (atom (keys @properties*))]
                     (if parent
                       (swap! acc* (fn [q](conj q (.property-keys parent nil)))))
                     (sort (distinct (flatten @acc*))))))
               
               (property-keys [this]
                 (.property-keys this false))
               
               
               (has-property? [this key local-only]
                 (let [klst (.property-keys this local-only)]
                   (utilities/member? key klst)))
               
               (has-property? [this key]
                 (.has-property? this key false))
               
               ;; Attributes
               
               (attributes-inherited? [this]
                 (not @attributes*))
               
               (set-local-attributes! [this att]
                 (reset! attributes* att))
               
               (set-local-attributes! [this]
                 (.set-local-attributes! this (att/attributes)))
               
               (remove-local-attributes! [this]
                 (reset! attributes* nil))
               
               (get-attributes [this]
                 (or @attributes*
                     (and parent (.get-attributes parent))
                     (do 
                       ;(utilities/warning "Using global default attributes")
                       att/default-attribute-set)))
               
               (add-attributes! [this id]
                 (if (.attributes-inherited? this)
                   (.set-local-attributes! this))
                 (.add! (.get-attributes this) id))

               (local-attributes-exist? [this id]
                 (and (.attributes-inherited? this)
                      (.exist? @attributes* id)))

               (current-attribute-id [this]
                 (.current-id (.get-attributes this)))
               
               (attribute-keys [this]
                 (.keys (.get-attributes this)))
               
               ;; NOTE only update if local attributes defined.
               (use-attributes! [this id]
                 (if @attributes*
                   (.use! @attributes* id))
                 (doseq [c @children*]
                   (.use-attributes! c id))
                 nil)
               
               (remove-attributes! [this id]
                 (if (.attributes-inherited? this)
                   (utilities/warning (format "can not remove id %s from inherited attributes" id))
                   (.remove! @attributes* id)))

               (color! [this c]
                 (add-implicit-local-attributes! this)
                 (.color! (.get-attributes this) c))

               (color! [this id c]
                 (add-implicit-local-attributes! this id)
                 (.color! (.get-attributes this) c))

               (style! [this st]
                 (add-implicit-local-attributes! this)
                 (.style! (.get-attributes this) st))

               (style! [this id st]
                 (add-implicit-local-attributes! this id)
                 (.style! (.get-attributes this) st))
               
               (width! [this w]
                 (add-implicit-local-attributes! this)
                 (.width! (.get-attributes this) w))
               
               (width! [this id w]
                 (add-implicit-local-attributes! this id)
                 (.width! (.get-attributes this) w))

               (fill! [this f]
                 (add-implicit-local-attributes! this)
                 (.fill! (.get-attributes this) f))
               
               (fill! [this id f]
                 (add-implicit-local-attributes! this id)
                 (.fill! (.get-attributes this) f))

               (hide! [this f]
                 (add-implicit-local-attributes! this)
                 (.hide! (.get-attributes this) f))
               
               (hide! [this id f]
                 (add-implicit-local-attributes! this id)
                 (.hide! (.get-attributes this) f))
               
               (select! [this f]
                 (.put-property! this :selected f)
                 f)
               
               (color [this]
                 (.color (.get-attributes this)))
               
               (style [this]
                 (.style (.get-attributes this)))
               
               (width [this]
                 (.width (.get-attributes this)))
               
               (filled? [this]
                 (.filled? (.get-attributes this)))
               
               (hidden? [this]
                 (.hidden? (.get-attributes this)))
               
               (selected? [this]
                 (.get-property this :selected))
               
               (set-coordinate-system! [this cs]
                 (reset! coordinate-system* cs))

               (coordinate-system [this]
                 (or @coordinate-system*
                     (and @parent* (.coordinate-system @parent*))
                     sgwr.cs.coordinate-system/default-coordinate-system))

               (set-update-hook! [this hfn]
                 (reset! update-hook* hfn))

               (points [this]
                 (let [pfn (get fnmap :points-fn (fn [_ pnts] pnts))]
                   (pfn this @points*)))

               (set-points! [this pnts]
                 (let [update-fn (get fnmap :update-fn (fn [& _] nil))]
                   (reset! points* (update-fn this pnts))
                   (@update-hook* this @points*)
                   @points*))
                 
               (shape [this]
                 (let [sfn (:shape-fn fnmap)]
                   (sfn this)))

               (bounds [this]
                 (let [bf (:bounds-fn fnmap)]
                   (bf this (.points this))))

               (contains? [this q]
                 (let [cfn (:contains-fn fnmap)]
                   (cfn this q)))

               (distance [this q]
                 (let [dfn (:distance-fn fnmap)]
                   (dfn this q)))

               (dump [this verbosity depth]
                 (println (.to-string this verbosity depth)))

               (dump [this verbosity]
                 (.dump this verbosity 0))

               (dump [this]
                 (.dump this 10))

               (to-string [this verbosity depth]
                 (let [sb (StringBuilder. )
                       pad1 (utilities/tab depth)
                       pad2 (utilities/tab (inc depth))]
                   (.append sb (format "%s%s id = %s  points = %s   \n"
                                       pad1
                                       (.element-type this)
                                       (get @properties* :id "")
                                       @points*))
                   (if (> verbosity 0)
                     (do 
                       (if (.attributes-inherited? this)
                         (.append sb (format "%sinherited-attributes\n" pad2))
                         (.append sb (.to-string @attributes* (dec verbosity)(inc depth))))
                       (doseq [k (.property-keys this :local)]
                         (.append sb (format "%s[%-12s] --> %s\n" pad2 k (.get-property this k))))
                       (doseq [c (.children this)]
                         (.append sb (.to-string c (dec verbosity)(inc depth))))
                       ))
                   (.toString sb)))
               )]
     (.put-property! elem :id etype)
     (.put-property! elem :color nil)
     (.put-property! elem :style nil)
     (.put-property! elem :width nil)
     (.put-property! elem :size nil)
     (.put-property! elem :filled nil)
     (.put-property! elem :hidden nil)
     (.put-property! elem :current-attributes nil)
     (.put-property! elem :selected false)
     elem ))) 
