(ns cadejo.ui.indicator.lamp
   (:import javax.swing.JPanel))

(defprotocol Lamp

  (colors!
    [this colors]
    "Sets possible lamp colors
     colors should be a vector containing at least two colors [off on].
     Additional colors may be specified to indicate variable lamp brightness
     or gradients")

  (colors
    [this]
    "Returns vector of colors representing off and on states
     At a minimum there are 2 colors [off on]
     Intermediate colors are used for variable 'brightness'")
  
  (use-color!
    [this i]
    "Sets the current 'on' color")
   
  (current-color
    [this])

  (on!
    [this]
    "Set lamp color using current brightness")
  
  (off!
    [this]
    "Set lamp to off color")
  
  (on? 
    [this]
    "Predicate returns true if lamp is not off")
  
  (flip!
    [this]
    "Swap current lamp color with off color")
  
  (blink!
    [this ms]
    [this]
    "Swap current lamp color with off color for ms milliseconds
     default dwell 1000 ms = 1 sec")
  
  (lamp-elements
    [this]
    "Returns a list of elements use to render the lamp geometry")
  
  (lamp-canvas! 
    [this jc])

  (lamp-canvas
    [this]
    "Returns swing component lamp is rendered on."))


(defn lamp-canvas [lamp]
  (proxy [JPanel][]
    (paint [g]
      (.setPaint g (if (.on? lamp)
                     (.current-color lamp)
                     (first (.colors lamp))))
      (doseq [e (.elements lamp)]
        (.fill g e)))))
