
;; standard properties:
;; node-type - keyword
;; id - keyword or integer or string
;; remarks 

(ns cadejo.midi.node
  "Node defines the basic component for building trees."
  (:require [cadejo.util.string])
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

  (parent 
    [this]
    "Returns the parent of this node. If this is a root node return nil.")

  (children 
    [this]
    "Returns a list of children node of this.")

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

  (properties 
    [this local-only]
    [this]
    "Return set off all assigned property keys for this. If local-only
     is true only include assignments made directly in this node, otherwise
     include all assignments for all parents of this node.")

  (update-properties
    [this key]
    "Method called whenever a property value is added are removed.
     A call to update-properties propagates down the tree to all children 
     nodes. Currently update-property is not being used.")
     )


