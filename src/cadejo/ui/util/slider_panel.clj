(ns cadejo.ui.util.slider-panel
  "Defines a composite component consisting of a JSlider, title-label,
  value-label all included in a border-panel. The value-label is
  automatically updated to reflect the slider position"
  (:use [seesaw.core])
  (:import java.awt.BorderLayout
           javax.swing.SwingConstants))

(defn slider-panel 
  "Construct JSlider composite component consisting of the slider together
   with text and value labels contained in a border-panel.
   min                - int, the minimum slider value, default -10
   max                - int, the maximum slider value, default +10
   value              - int, the initial slider value, min <= value <= max,
                        default mean of min and max.
   text               - string, the title label text
   text-position      - keyword, position of title-label, possible values
                        :north :east :south :west, default :north
   value-formatter    - function used to format value label. The function
                        should take a single int value and return a string
   value-position     - keyword, position of value-label, possible values
                        :north :east :south :west, should be the same as
                        text-position, default :south 
   orientation        - keyword, slider orientation. possible values
                        :vertical, horizontal, default :vertical
   inverted?          - boolean, indicates if slider values are inverted,
                        default false
   minor-tick-spacing - int, positions of minor tick marks, defaults to 
                        1/10 of slider range
   major-tick-spacing - int, positions of major tick marks, defaults to 
                        1/2 slider range
   snap-to-ticks?     - boolean, indicates if slider snaps to tick mark
                        upon mouse release. default false
   paint-ticks?       - boolean, indicates if tick marks should be painted,
                        default false
   paint-labels?      - boolean, indicates if tick labels should be
                        painted, default false.
   paint-track?       - boolean, indicates if slider track is painted,
                        default true.
   listener           - Function used as ChangeListener for slider.
                        The function should take a single ChangeEvent
                        argument. The return value and side effects are
                        undefined. 
   id                 - A seesaw id prefix applied to the component parts.

   slider-panel returns a map with the following keys
      :panel - the containing border-panel
      :slider - the JSlider proper
      :lab-title - the JLabel used for drawing the title text
      :lab-value - the JLabel used for drawing the value text"
  [& {:keys [min max value 
             text text-position
             value-formatter value-position
             orientation inverted?
             minor-tick-spacing major-tick-spacing
             snap-to-ticks? paint-ticks? paint-labels? paint-track?
             id listener]
      :or {min -10 
           max 10 
           value nil
           text ""
           text-position :north
           value-formatter (fn [val](format "%s" val))
           value-position :south
           orientation :vertical
           inverted? false
           minor-tick-spacing nil
           major-tick-spacing nil
           snap-to-ticks? false
           paint-ticks? false
           paint-labels? false
           paint-track? true
           listener (fn [ev] nil)
           id :sliderpan}}]
  (let [val (or value (/ (+ min max) 2.0))
        lab-title (label :text (str text)
                         :id (keyword (format "%s-lab-title" id)))
        lab-value (label :text (value-formatter val)
                         :id (keyword (format "%s-lab-value" id)))
        delta (- max min)
        slide (slider :orientation orientation
                      :value val
                      :min min
                      :max max
                      :minor-tick-spacing (or minor-tick-spacing (/ delta 10))
                      :major-tick-spacing (or major-tick-spacing (/ delta 2))
                      :snap-to-ticks? snap-to-ticks?
                      :paint-ticks? paint-ticks?
                      :paint-labels? paint-labels?
                      :paint-track? paint-track?
                      :inverted? inverted?
                      :id (keyword (format "%s-slider" id)))
        pan (border-panel :center slide :id (keyword (format "%s-panel" id)))
        position-map {:north BorderLayout/NORTH
                      :east BorderLayout/EAST
                      :south BorderLayout/SOUTH
                      :west BorderLayout/WEST}]
    (listen slide :state-changed 
            (fn [ev]
              (let [v (.getValue slide)]
                (config! lab-value :text (value-formatter v)))))
    (listen slide :state-changed listener)
    (.add pan lab-title (get position-map text-position BorderLayout/NORTH))
    (.add pan lab-value (get position-map value-position BorderLayout/SOUTH))
    (.setHorizontalAlignment lab-title SwingConstants/CENTER)
    (.setHorizontalAlignment lab-value SwingConstants/CENTER)
    {:panel pan
     :slider slide
     :lab-title lab-title
     :lab-value lab-value}))




;;; Example usage
;;;
(defn foo [ev]
  (println (.getValue (.getSource ev))))

(def sp (slider-panel :text "Alpha"
                      :text-position :north
                      :value-position :south
                      :orientation :vertical
                      :paint-labels? true
                      :min -100
                      :max 100
                      :minor-tick-spacing 10
                      :major-tick-spacing 50
                      :snap-to-ticks? true
                      :listener foo
                      :inverted? false))

(def pan-main (border-panel :center (:panel sp)))
(def f (frame :title "TEST"
              :content pan-main
              :on-close :dispose))
(pack! f)
(show! f)
              
              
  
                              
