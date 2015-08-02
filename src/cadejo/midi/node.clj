(println "--> cadejo.midi.node")

;; standard properties:
;; node-type - keyword
;; id - keyword or integer or string
;; remarks 

(ns cadejo.midi.node
  "Node defines the basic component for building trees."
  (:require [cadejo.util.string])
  (:require [cadejo.util.col :as ucol]))

(defprotocol Node
  
  "Each node has zero or one parent node and zero or more child nodes.
   Nodes maintain a set of local properties. If a property is not
   defined for a specific node the parent of that node is checked.
   This continues until either anode is found which defines the property
   or the root node is reached."

  (node-type 
    [this]
    "Returns a unique keyword identifying the 'class' of this node.")

  (is-root? 
    [this]
    "Returns true if this is a root node.
     A root node is a node whose parent is nil.")

  (find-root
    [this]
    "Returns the root node for tree to which this node belongs")
  
  (parent
    [this]
    "Returns the parent of this node. If this is a root node return nil.")

  (children 
    [this]
    "Returns a list of children node of this.")

  (is-child? [this obj]
    "Predicate returns true if obj is a child node of this")

  (add-child! [this obj]
    "Adds obj as a child node of this, but only if it is not already a child.
     Returns Boolean true iff child was actually added to list.
     Note some node types may restrict the type of node which may be added as a child.")

  (remove-child! [this obj]
    "Removes obj as a child node of this.
     Returns Boolean true if obj actually removed.
     Not all node types allow child nodes to be removed.")

  (_orphan! [this]
    "Set parent node to nil.
     _orphan! is called by remove-child!, do not call directly.")

  (_set-parent! [this parent]
    "Set parent nod for this.
     _set-parent! is called by add-child! and should not be called directly.")
  
  
  (put-property!
    [this key value]
    "Creates an assignment between key and value. 
    If a parent node has an assignment to the same key the local 
    assignment takes priority.")

  (remove-property!
    [this key]
    "Removes the local assignment to key.
     Parent nodes are not effected.")

  (get-property 
    [this key default]
    [this key]
    "Returns the property value of key. If key has no value locally in this
     check the parent node. Continue checking parent nodes until either an 
     assignment to key is found or the root node is reached. It the root
     node is reached return default. If default is returned and it's value
     id :fail display a warning message and return nil.")

  (local-property 
    [this key]
    "Return local value of property key
     Return nil if key has no local value")

  (properties 
    [this local-only]
    [this]
    "Return set off all assigned property keys for this. If local-only
     is true only include assignments made directly in this node, otherwise
     include all assignments for all parents of this node.")

  (event-dispatcher [this]
    "Returns function to handle MIDI events. 
     As of version 0.3.1.SNAPSHOT
     (fn [event] ....)")
  
  (get-editor
    [this]
    "Returns gut editor, if any, for this node.
     Returns nil if no editor is present")
 
  )


(defn rep-tree [node depth]
  (let [pad (cadejo.util.string/tab depth)
        sb (StringBuilder. 300)
        ntype (.node-type node)
        id (.get-property node :id)]
    (.append sb (format "%s%s %s\n" pad ntype id))
    (doseq [c (.children node)]
      (.append sb (rep-tree c (inc depth))))
    (.toString sb)))

(defn trace-path
  "Returns list of all nodes on the path between the root and node.
   The root node appears at the head of the list"
  [node]
  (let [acc* (atom (list node))
        parent* (atom (.parent node))]
    (while @parent*
      (swap! acc* (fn [q](cons @parent* q)))
      (reset! parent* (.parent @parent*)))
    @acc*))


(defn rep-path
  "Returns a string representation of path between root and node.
   Each node is separated by forward slash /.
   If type is true  include node-type information (default false)
   if id is true include each node's id (default true)"
   
  ([node & {:keys [type id]
            :or {type nil
                 id true}}]
   (let [path (trace-path node)
         sb (StringBuilder. 300)]
     (doseq [n path]
       (if type (.append sb (format "%s " (.node-type n))))
       (if id (.append sb (format "%s" (.get-property n :id))))
       (.append sb "/"))
     (.toString sb))))


(comment <<<<<<<<<<<<<<<<<<<<< node test code
;; Return a node object useful for testing.
;;
(defn dummy-node [id]
  (let [parent* (atom nil)
        properties* (atom {:id id})
        children* (atom [])
        node (reify Node

               (node-type [this] :dummy)

               (is-root? [this](not @parent*))

               (find-root [this]
                 (if (.is-root? this)
                   this
                   (.find-root @parent*)))

               (parent [this] @parent*)

               (children [this] @children*)

               (is-child? [this other]
                 (ucol/member? other @children*))

               (add-child! [this other]
                 (if (not (.is-child? this other))
                   (do
                     (swap! children* (fn [q](conj q other)))
                     (._set-parent! other this)
                     true)
                   false))

               (remove-child! [this other]
                 (if (.is-child? this other)
                   (let [predicate (fn [r](not (= r other)))]
                     (reset! children* (filter predicate @children*))
                     true)
                   false))

               (_set-parent! [this p]
                 (reset! parent* p))

               (_orphan! [this]
                 (._set-parent! this nil))

               (put-property! [this key value]
                 (swap! properties* (fn [q](assoc q key value))))

               (get-property [this key default]
                 (let [lp (get @properties* key)]
                   (if lp
                     lp
                     (if (.is-root? this)
                       default
                       (.get-property @parent* key default)))))

               (get-property [this key]
                 (.get-property this key nil))

               (local-property [this key]
                 (get @properties* key))
             
               (properties [this local-only]
                 (set (concat (keys @properties*)
                              (if (and @parent* (not local-only))
                                (.properties @parent*)
                                nil))))

               (event-dispatcher [this]
                 (fn [ev]
                   (println (format ":dummy-node event-dispatcher %s" ev))
                   (doseq [c (.children this)]
                     ((.event-dispatcher c) ev))))

               (get-editor [this]
                 nil))]
    node))
                 

(def u (dummy-node :living))
(def p1 (dummy-node :plants))
(def a1 (dummy-node :animlas))
(def cats (dummy-node :cats))
(def lion (dummy-node :lion))
(def tiger (dummy-node :tiger))
(def birds (dummy-node :birds))
(def crow (dummy-node :crow))
(def dove (dummy-node :dove))

(.add-child! u p1)
(.add-child! u a1)
(.add-child! a1 cats)
(.add-child! a1 birds)
(.add-child! cats lion)
(.add-child! cats tiger)
(.add-child! birds crow)
(.add-child! birds dove)

(println (rep-tree u 0))
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> end node test code)
