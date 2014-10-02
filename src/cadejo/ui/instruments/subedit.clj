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

  (set-param! 
    [this param value])

  (init!
    [this])

  (sync-ui!
    [this]))
