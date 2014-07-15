(ns cadejo.ui.indicator.multisegment-lamp
  (:import javax.swing.JPanel))

(defprotocol MultiSegmentLamp

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

 (char-list
    [this]
    "Returns list of all possible chars")

  (set-char!
    [this pkey]
    "Set the current display patterns
     where pkey is a char
     See pattern-keys")

  (elements 
    [this]
    "Returns list of all shapes which define the display geometry")

  (on-elements
    [this]
    "Returns list of Java shapes which define the current pattern.")

  (lamp-canvas!
    [this jc])

  (lamp-canvas 
    [this])
)

(defn multisegment-canvas [mslamp]
  (proxy [JPanel][]
    (paint [g]
      (.setPaint g (first (.colors mslamp)))
      (doseq [e (.elements mslamp)](.fill g e))
      (if (.on? mslamp)
        (do 
          (.setPaint g (.current-color mslamp))
          (doseq [e (.on-elements mslamp)](.fill g e)))))))
