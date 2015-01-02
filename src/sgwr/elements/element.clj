(println "--> sgwr.elements.element")
(ns sgwr.elements.element
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.elements.attributes :as att])
  (:require [sgwr.cs.coordinate-system])
)

(defprotocol SgwrElement

  (element-type 
    [this])

  (parent
    [this])

  (set-parent!
    [this parent])

  (add-child! 
    [this other])

  (children 
    [this])

  (child-count 
    [this])

  (has-children?
    [this])

  (is-root? 
    [this])
  
  (is-leaf?
    [this])

  (put-property!
    [this key value])

  (get-property
    [this key default]
    [this key])

  (remove-property!
    [this key])

  (property-keys
    [this local-only]
    [this])

  (has-property? 
    [this key local-only]
    [this key])

  ;; Attributes

  (attributes-inherited? 
    [this])

  (set-local-attributes! 
    [this att]
    [this])

  (remove-local-attributes!
    [this])

  (get-attributes
    [this])
    
  (add-attributes!
    [this id])

  (current-attribute-id
    [this])

  (attribute-keys 
    [this])

  (use-attributes!
    [this id])

  (remove-attributes!
    [this id])

  (color!
    [this c])

  (style!
    [this st])

  (width!
    [this w])

  (fill!
    [this f])

  (hide!
    [this f])
  
  (select! 
    [this f])
  
  (color
    [this])

  (style
    [this])

  (width
    [this])

  (filled? 
    [this])

  (hidden?
    [this])
  
  (selected?
    [this])

  (set-coordinate-system!
    [this cs])

  (coordinate-system
    [this])

  (set-update-hook!
    [this hfn])

  (points
    [this])

  (set-points!
    [this pnts])

  (shape 
    [this])

  (bounds 
    [this])

  (contains?
    [this q])

  (distance 
    [this q])

  (to-string 
    [this verbosity depth])

  (dump 
    [this verbosity depth]
    [this verbosity]
    [this])
)

(defn- modification-warning [el]
  (if (.attributes-inherited? el)
    (utilities/warning "Modifications made to inherited attributes")))



(defn create-element 
  ([etype fnmap]
   (create-element etype nil fnmap))

  ([etype parent fnmap]
   (let [parent* (atom parent)
         coordinate-system* (atom nil)
         children* (atom [])
         properties* (atom {})
         attributes* (atom nil)
         selected* (atom false)
         update-hook* (atom (fn [& _] nil))
         points* (atom [])
         obj (reify SgwrElement
       
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
               
               (remove-property! [this key]
                 (swap! properties* (fn [q](dissoc q key))))
               
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
                       (utilities/warning "Using global default attributes")
                       att/default-attribute-set)))
               
               (add-attributes! [this id]
                 (modification-warning this)
                 (.add! (.get-attributes this) id))
               
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
                 )
               
               (remove-attributes! [this id]
                 (if (.attributes-inherited? this)
                   (utilities/warning (format "can not remove id %s from inherited attributes" id))
                   (.remove! @attributes* id)))
               
               (color! [this c]
                 (modification-warning this)
                 (.color! (.get-attributes this) c))
               
               (style! [this n]
                 (modification-warning this)
                 (.style! (.get-attributes this) n))
               
               (width! [this n]
                 (modification-warning this)
                 (.width! (.get-attributes this) n))
               
               (fill! [this n]
                 (modification-warning this)
                 (.fill! (.get-attributes this) n))
               
               (hide! [this n]
                 (modification-warning this)
                 (.hide! (.get-attributes this) n))
               
               (select! [this f]
                 (reset! selected* f))
               
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
                 (or @selected*
                     (and parent (.selected? parent))))
               
               (set-coordinate-system! [this cs]
                 (reset! coordinate-system* cs))

               (coordinate-system [this]
                 (or @coordinate-system*
                     (and @parent* (.coordinate-system @parent*))
                     sgwr.cs.coordinate-system/default-coordinate-system))

               (set-update-hook! [this hfn]
                 (reset! update-hook* hfn))

               ;; (points [this]
               ;;   @points*)

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

                 ;; (bounds [this]
               ;;   (let [d (map first @points*)
               ;;         r (map second @points*)
               ;;         x0 (apply min d)
               ;;         x1 (apply max d)
               ;;         y0 (apply min r)
               ;;         y1 (apply max r)]
               ;;     [[x0 y0][x1 y1]]))

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
     obj

     ))) 

