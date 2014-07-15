(ns cadejo.ui.midi.node-editor)

(defprotocol NodeEditor

  (widgets 
    [this]
    "Returns map of gui widgets")

  (widget
    [this key]
    "Returns specific widget.
     If no such widget exists display warning and return nil")

  (get-node 
    [this]
    "Return the node object this editor is operating with")

  (set-node!
    [this n]
    "Set the nod object this editor is to operate with.")

  (sync-ui
    [this]
    "Update all gui components to match current state of 
     the node")

  (status 
    [this msg]
    "Display status message")

  (warning 
    [this msg]
    "Display warning message")

  (show-parent
    [this]
    "Move focus to editor for parent node of this.
     If this is a root node do nothing")

  (show-child 
    [this id]
    "Display editor for indicated child node")
  )
