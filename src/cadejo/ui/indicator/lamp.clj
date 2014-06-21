(ns cadejo.ui.indicator.lamp
  "Defines interface common to all 'lamp' indicators")

(defprotocol LampProtocol
  
  (colors! 
    [this colors]
    "Set lamp off and on colors.
     colors - list [off on]
     See seesaw.color")

  (colors
    [this]
    "Return lamp colors [off on]")

  (on!
    [this]
    "Turn this lamp on")

  (off!
    [this]
    "Turn this lamp off")

  (is-on?
    [this]
    "Predicate returns true if lamp is currently on")

  (flip!
    [this]
    "Flip on/off state of lamp")
 
  (blink! 
    [this dwell]
    [this]
    "Reverse flip on/off state for dwell milliseconds and then flip back
     dwell defaults to 100ms = 1sec")

  (lamp-elements
    [this]
    "Return java.awt.Shape(s) which define the lamp elements.
    Note for complex lamps the return value may be a sequence
    of shapes.")

  (lamp-canvas!
    [this can]
    "Set the swing component on which the lamp elements are rendered.")

  (lamp-canvas
    [this]
    "Returns the swing component on which lamp elements are rendered.
     Most often canvas is an extension of JPanel.")
     
)

