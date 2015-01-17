(ns sgwr.indicators.char)

;; (def char-width 25)
;; (def char-height 35)


(defprotocol CharDisplay

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


 
    

