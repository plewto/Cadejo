(ns sgwr.indicators.cell
  "Defines displaybar Cell protocol")

(defprotocol Cell

  (cell-width 
    [this]
    "(cell-width this)
     Returns width of cell in pixels")

  (cell-height 
    [this]
    "(cell-height this)
     Returns width of cell in pixels")

  (colors!
    [this inactive active]
    "(colors! this inactive active)
     Sets color scheme.")

  (character-set
    [this]
    "(character-set this)
     Returns vector of supported characters.")

  (display!
    [this c]
    "(display! this c)
     Set c as the display character.")
  )
