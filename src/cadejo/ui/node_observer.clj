(ns cadejo.ui.node-observer)

(defprotocol NodeObserver
  
  (set-parent-editor! 
    [this ed])

  (widgets
    [this])

  (widget 
    [this key])

  (sync-ui! 
    [this])
)
