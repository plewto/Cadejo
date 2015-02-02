(ns cadejo.ui.node-observer)

(defprotocol NodeObserver
  
  (components
    [this])

  (component 
    [this key])

  (sync-ui! 
    [this])
)
