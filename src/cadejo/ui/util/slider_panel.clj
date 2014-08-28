(ns cadejo.ui.util.slider-panel
  "Provides composite JSlider, title JLabel and value JLabel embedded in a JPanel. 
   The value label is automatically updated by slider movement.
   Normally JSliders only have integer values. Mapping functions convert
   between sliders integer position and an arbitrary range of values.
   A formatting function is used to format the current value label."
 
  (:require [seesaw.core :as ss])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math :as math])
  (:import java.awt.BorderLayout
           javax.swing.SwingConstants
           java.util.Hashtable))

(def positions {:north BorderLayout/NORTH
                :east BorderLayout/EAST
                :south BorderLayout/SOUTH
                :west BorderLayout/WEST})


(defn create-default-label-table [steps major mapfn formatter]
  "Creates a default slider label table 
   see http://docs.oracle.com/javase/7/docs/api/javax/swing/JSlider.html#setLabelTable(java.util.Dictionary)
   
   steps - number of slider positions
   major - position of major tick marks (and label) as fraction of steps
           i.e.  major = 1/4 will place labels at 0, 1/4, 1/2, 3/4 and 1
           positions along track.

   mapfn - function used to map slider position to value.
           this should be the same value passed to slider-panel 

   formatter - function to format label text.
   
   Returns instance of java.util.Hashtable"
  (let [d (Hashtable.)]
    (doseq [n (range 0 steps (int (* steps major)))]
      (let [v (mapfn n)
            s (formatter v)]
        (.put d (Integer. n)(ss/label :text s))))
    (if (math/divides? (/ major) steps)
      (.put d (Integer. steps) (ss/label :text (formatter (mapfn steps)))))
    d))


(defn default-formatter [x]
  (str x))

(defprotocol SliderPanelProtocol 
  
  (widget-map 
    [this]
    "Returns map for swing components")


  (widget
    [this key]
    "Returns specific swing component
     possible keys include
     :slider - The JSlider
     :lab-title - The title JLabel
     :lab-value - The value JLabel
     :pan-main - The primary JPanel holding the other components.")

  (suspend-listeners 
    [this flag]
    "Sets listener suspend flag.
     If true value listeners attached to the slider
     should do nothing.")

  (listeners-suspended? 
    [this]
    "returns flag indicating if listeners should be active")

  (get-value
    [this]
    "Returns the current mapped value of the slider.")
    

  (set-value 
    [this x suspend]
    [this x]
    "Sets the current mapped value of the slider
     If suspend is true then listeners should be suspended.
     The value of suspend is restored after the slider has been  
     updated.")
  )


(deftype SliderPanel [widgets value* suspend* mapfn invmap]
    SliderPanelProtocol

    (widget-map [this]
      widgets)

    (widget [this key]
      (or (get widgets key)
          (umsg/warning (format "SliderPanel does not have %s widget" key))))

    (suspend-listeners [this flag]
      (swap! suspend* (fn [n] flag)))

    (listeners-suspended? [this]
      @suspend*)

    (get-value [this]
      @value*)

    (set-value [this x suspend]
      (let [temp @suspend*
            pos (invmap x)]
        (swap! suspend* (fn [n] suspend))
        (swap! value* (fn [n] x))
        (.setValue (.widget this :slider) pos)
        (swap! suspend* (fn [n] temp))
        pos))

    (set-value [this x]
      (.set-value this x true)))


;; ticks argument - map of form {:minor :major :paint :snap}
;;    where minor and major are relative i.e. 1/10 means paint a tick at intervals of 1/10 track length




(defn slider-panel [text & {:keys [min max value steps
                                   formatter 
                                   orientation
                                   title-position 
                                   value-position 
                                   ticks 
                                   paint-track
                                   paint-labels
                                   label-table
                                   inverted
                                   border]
                            :or {min 0.00
                                 max 1.00
                                 value nil
                                 steps 100
                                 formatter default-formatter
                                 orientation :vertical
                                 title-position :north
                                 value-position :south
                                 ticks {:minor 1/10 :major 1/2 :paint true :snap false}
                                 paint-track true
                                 paint-labels false
                                 label-table nil
                                 inverted false
                                 border nil}}]
  "Creates SliderPanel
   min            - number, the minimum value.
   max            - number, the maximum value, max must be greater then min
   value          - number, the initial value, defaults to min.
   steps          - int, the number of slider steps, default 0
   formatter      - function used to format value label text
                    The formatter should take a single float argument
                    and return a string.
   orientation    - keyword, slider orientation may be either :vertical
                    or :horizontal, default :vertical.
   title-position - keyword, position of title label relative to slider.
                    possible values, :north, :east :south & :west, default :north. 
   value-position - keyword, position of value label relative to slider.
                    possible values, :north, :east :south & :west, default :south.
   ticks          - map, sets tick mark attributes 
                    {:minor ratio, :major ratio, :paint bool, :snap bool}
                     :minor - Sets minor tick spacing, default 1/10
                     :major - Sets major tick spacing, default 1/2
                     :paint - Flag indicating if tick marks are to be painted,
                              default true
                     :snap  - Flag indicating if slider should snap to tick
                              marks, default false.
                   The :major tick value is also used to create a default 
                   label table, see create-default-label-table 
   paint-track    - bool, if true paint the slider track, default true
   paint-labels   - bool, if true display tick label, default false
   label-table    - java.util.Hashtable, holds JLables for major tick marks.
                    labels are only drawn if paint-labels is true. 
                    See create-default-label-table
   inverted       - bool, if true slider values are inverted.
   border         - Swing border, a border applied to the main panel, default nil

   Returns an instance of SliderPanel"

  (let [mapfn (math/linear-function 0 min steps max)
        invmap (comp int (math/linear-function min 0 max steps))
        value* (atom (or value min))
        slider (ss/slider :min 0 
                          :max steps 
                          :value (invmap @value*)
                          :orientation orientation
                          :minor-tick-spacing (* steps (get ticks :minor 1/10))
                          :major-tick-spacing (* steps (get ticks :major 1/2))
                          :paint-ticks? (get ticks :paint true)
                          :snap-to-ticks? (get ticks :snap false)
                          :paint-track? paint-track
                          :paint-labels? paint-labels
                          :inverted? inverted)
        lab-title (ss/label :text text)
        lab-value (ss/label :text (formatter @value*))
        pan-main (let [pan (ss/border-panel :center slider
                                            :border border)]
                   (.add pan lab-title (get positions title-position :north))
                   (.add pan lab-value (get positions value-position :south))
                   (.setLabelFor lab-title slider)
                   (.setLabelFor lab-value slider)
                   (.setHorizontalAlignment lab-title SwingConstants/CENTER)
                   (.setHorizontalAlignment lab-value SwingConstants/CENTER)
                   pan)
        widgets {:slider slider
                 :pan-main pan-main
                 :lab-title lab-title
                 :lab-value lab-value}
        labtab (or label-table
                   (create-default-label-table steps (get ticks :major 1/2) mapfn formatter))
        sp (SliderPanel. widgets value* (atom false) mapfn invmap)]
    (.setLabelTable slider labtab)
    (ss/listen slider :state-changed (fn [ev]
                                       (if (not (.listeners-suspended? sp))
                                         (let [pos (.getValue slider)
                                               x (mapfn pos)
                                               s (formatter x)]
                                           (swap! value* (fn [n] x))
                                           (ss/config! lab-value :text s)))))
    sp))
                   

;;; ----- Example use
;;; 
;; (def sp (slider-panel "Alpha"
;;                       :min -100
;;                       :max  100
;;                       :value 0
;;                       :steps 100
;;                       :orientation :vertical
;;                       :title-position :north
;;                       :value-position :south
;;                       :ticks {:minor 1/10 :major 1/2 :paint true :snap false}
;;                       :paint-labels true
;;                       :inverted false
;;                       :paint-track true
;;                       :label-table nil))
;; (def jb-reset (ss/button :text "Reset"))
;; (ss/listen jb-reset :action (fn [ev](.set-value sp 0)))
;; (def f (ss/frame :title "SliderPanel Demo"
;;                  :content (ss/border-panel 
;;                            :north jb-reset
;;                            :center (.widget sp :pan-main))
;;                  :on-close :dispose
;;                  :size [300 :by 300]))
;; (ss/listen (.widget sp :slider) 
;;            :state-changed (fn [ev]
;;                             (println (.get-value sp))))
;; (ss/show! f)
                                           
