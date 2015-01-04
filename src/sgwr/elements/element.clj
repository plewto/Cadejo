(println "--> sgwr.elements.element")

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

  (get-attributes
    [this])

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
  
  (fill!
    [this id flag]
    [this flag])
  
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
 
  (select! 
    [this f]
    "(select! this flag)
     Sets the selected flag for this. Note that selection status is not
     part of the attributes mechanism.")
 
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


(def reserved-property-keys '[:id :color :style :width :size :filled 
                              :hidden :selected])

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
         attributes (att/attributes)
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
                     (utilities/warning 
                      (format "Can not remove locked property %s from %s element"
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
           
               (get-attributes [this] attributes)
               
               (current-attribute-id [this]
                 (.current-id (.get-attributes this)))
               
               (attribute-keys [this]
                 (.keys (.get-attributes this)))

               (use-attributes! [this id]
                 (.use! attributes id)
                 (let [att (.get-attributes attributes id)
                       akeys [:color :style :size :width :filled :hidden]]
                   (doseq [k akeys]
                     (.put-property! this k (k att)))
                   (doseq [c (.children this)]
                     (.use-attributes! c id))))
               
               (remove-attributes! [this id]
                 (.remove! attributes id))
         
               (color! [this id c]
                 (.color! attributes id c))
               
               (color! [this c]
                 (.color! attributes c))
               
               (style! [this id st]
                 (.style! attributes id st))
               
               (style! [this st]
                 (.style! attributes st))
               
               (width! [this id w]
                 (.width! attributes id w))
               
               (width! [this w]
                 (.width! attributes w))
               
               (size! [this id sz]
                 (.size! attributes id sz))
               
               (size! [this sz]
                 (.size! attributes sz))
               
               (fill! [this id flag]
                 (.fill! attributes id flag))
               
               (fill! [this flag]
                 (.fill! attributes flag))
               
               (hide! [this id flag]
                 (.hide! attributes id flag))
               
               (hide! [this flag]
                 (.hide! attributes flag))
               
               (color [this]
                 (.get-property this :color att/default-color))
               
               (style [this]
                 (.get-property this :style att/default-style))
               
               (width [this]
                 (.get-property this :width att/default-width))
               
               (size [this]
                 (.get-property this :size att/default-size))
               
               (filled? [this]
                 (.get-property this :filled nil))
               
               (hidden? [this]
                 (.get-property this :hidden nil))
               
               (select! [this f]
                 (.put-property! this :selected f)
                 f)
               
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
                 (let [pad1 (utilities/tab depth)
                       pad2 (utilities/tab (inc depth))]
                   (cond (<= verbosity 0)
                         (let [et (.element-type this)
                               id (.get-property this :id)]
                           (format "-- %s%s element id = %s  %s\n"
                                   pad1 et id (if (not (= et :group))
                                                (str "points = " (.points this))
                                                "")))

                         (= verbosity 1)
                         (let [head (.to-string this 0 depth)
                               sb (StringBuilder.)]
                           (.append sb head)
                           (.append sb (.to-string attributes 1 (inc depth)))
                           (.append sb (format "%sProperties %s\n" pad2 (.property-keys this)))
                           (doseq [c @children*]
                             (.append sb (.to-string c 1 (inc depth))))
                           (.toString sb))

                         :default
                         (let [pad3 (utilities/tab (+ depth 2))
                               head (.to-string this 0 depth)
                               sb (StringBuilder.)]
                           (.append sb head)
                           (.append sb (format "%scs %s\n" pad2 (.to-string (.coordinate-system this))))
                           (.append sb (.to-string attributes 2 (inc depth)))
                           (.append sb (format "%sProperties\n" pad2))
                           (doseq [k (.property-keys this :local)]
                             (.append sb (format "%s[%-12s] -> %s\n" pad3 k (.get-property this k))))
                           (doseq [c @children*]
                             (.append sb (.to-string c 2 (inc depth) )))
                           (.toString sb)) ))) )]
     (.put-property! elem :id etype)
     (.put-property! elem :color nil)
     (.put-property! elem :style nil)
     (.put-property! elem :width nil)
     (.put-property! elem :size nil)
     (.put-property! elem :filled nil)
     (.put-property! elem :hidden nil)
     (.put-property! elem :selected false)
     elem ))) 
