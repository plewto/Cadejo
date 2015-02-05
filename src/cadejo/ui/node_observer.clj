(ns cadejo.ui.node-observer)

(defprotocol NodeObserver
  
  (set-parent-editor! 
    [this ed])

  (components
    [this])

  (component 
    [this key])

  (sync-ui! 
    [this])
)
