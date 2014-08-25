;; Defines InstrumentEditor protocol 
;; An instance of InstrumentEditor is always a 'tab'
;; within an InstrumentEditorFramework

(ns cadejo.ui.instruments.instrument-editor)

(defprotocol InstrumentEditor

  (set-parent!
    [this iefw])

  (widgets 
    [this])

  (widget
    [this key])

  (status!
    [this msg])

  (warning! 
    [this msg])

  (push-undo-state! 
    [this msg])
  
  (push-redo-state!
    [this msg])
  
  (sync-ui!
    [this])
)
