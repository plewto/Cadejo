(println "--> cadejo.midi.node")

;; standard properties:
;; node-type - keyword
;; id - keyword or integer or string
;; remarks 

(ns cadejo.midi.node
  "Node defines the basic component for building trees."
  (:require [cadejo.util.string :as strutil])
  (:require [cadejo.util.user-message :as umsg]))

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

  (rep-tree
    [this depth]))

