(ns cadejo.ui.instruments.subedit
  )

(defprotocol InstrumentSubEditor

  (widgets
    [this])

  (widget 
    [this key])

  (parent
    [this])

  (parent!
    [this p])

  (status! 
    [this msg])

  (warning!
    [this msg])

  (set-value! 
    [this param value])

  (push-undo-state!
    [this msg])

  (sync-ui!
    [this])
)
