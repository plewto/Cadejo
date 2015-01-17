(ns sgwr.indicators.cell)

(defprotocol Cell

  (cell-width 
    [this])

  (cell-height 
    [this])

  (colors!
    [this inactive active])

  (character-set
    [this])

  (display!
    [this c]) )


 
    

