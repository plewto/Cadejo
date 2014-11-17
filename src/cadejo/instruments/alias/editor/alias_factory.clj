(ns cadejo.instruments.alias.editor.alias-factory
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [seesaw.border :as ssb])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.ui.util.factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:import java.awt.Dimension
           java.util.Hashtable
           javax.swing.Box
           javax.swing.event.ChangeListener
           javax.swing.event.ListSelectionListener))

(def title cadejo.ui.util.factory/title)
(def padding  cadejo.ui.util.factory/padding)
(def line  cadejo.ui.util.factory/line)
(def bevel cadejo.ui.util.factory/bevel)
(def button cadejo.ui.util.factory/button)
(def toggle cadejo.ui.util.factory/toggle)
(def radio cadejo.ui.util.factory/radio)

(def default-font (ssf/font :size 9 :name :serif))

; ---------------------------------------------------------------------- 
;                                  Sliders

;; Returns Hashtable for use as slider label map
;; with 5 marked positions.
;; The positions are evenly spaced at 0%, 25%,
;; 50%, 75% and 100%.
;;
(defn slider-label-map [p0 p25 p50 p75 p100]
  (let [ht (Hashtable. 5)]
    (.put ht (int 0)(ss/label :text (str p0) :font default-font))
    (.put ht (int 25)(ss/label :text (str p25) :font default-font))
    (.put ht (int 50)(ss/label :text (str p50) :font default-font))
    (.put ht (int 75)(ss/label :text (str p75) :font default-font))
    (.put ht (int 100)(ss/label :text (str p100) :font default-font))
    ht))

(defn signed-unit-label-map []
  (slider-label-map "-1.0" "" " 0.0" "" "+1.0"))

(defn unsigned-unit-label-map []
  (slider-label-map "0.00" "" "0.50" "" "1.00"))

(defn pan-label-map []
  (slider-label-map "F1" "" "" "" "F2"))

(def slider-size [56 :by 148])  ;; 175
(def half-slider-size [56 :by 87])

(defn blank-slider []
  (Box/createRigidArea (Dimension. (first slider-size)
                                    (last slider-size))))

;; Returns vertical JSlider with 100 positions.
;; param - keyword parameter thias slider controls
;; minval - float, The minimum value 
;; maxval - float, The maximum value
;; labmap - boolean or Hashtable. 
;;          
;; Returns JSlider with following client properties
;; :param - 
;; :scale and :bias - used to map slider psotion to data value 
;; :rvs-scale and :rvs-bias - uses to map data value to slider position
;;
(defn slider [param minval maxval labmap]
  (let [steps 100
        isteps 1/100
        delta (float (- maxval minval))
        scale (* isteps delta)
        bias minval
        rvs-scale (/ steps delta)
        rvs-bias (/ (* -1 steps minval) delta)
        s (ss/slider :orientation :vertical
                     :value 0 :min 0 :max steps
                     :snap-to-ticks? false
                     :paint-labels? labmap
                     :minor-tick-spacing 5
                     :major-tick-spacing 25)]
    (if (= (type labmap) Hashtable)
      (.setLabelTable s labmap))
    (.putClientProperty s :param param)
    (.putClientProperty s :scale scale)
    (.putClientProperty s :bias bias)
    (.putClientProperty s :rvs-scale rvs-scale)
    (.putClientProperty s :rvs-bias rvs-bias)
    s))

;; Creates amplitude slider with DB units.
;;
(defn mix-slider [param]
  (let [s (ss/slider :orientation :vertical
                     :value 0 :min -48 :max 0 
                     :snap-to-ticks? false
                     :paint-labels? true
                     :font default-font
                     :minor-tick-spacing 6
                     :major-tick-spacing 24)]
    (.putClientProperty s :param param)
    (.putClientProperty s :scale 1.0)
    (.putClientProperty s :bias 0.0)
    (.putClientProperty s :rvs-scale 1.0)
    (.putClientProperty s :rvs-bias 0.0)
    s))

(defn unit-slider 
  ([param signed]
     (if signed
       (slider param -1.0 1.0 (signed-unit-label-map))
       (slider param 0.0 1.0 (unsigned-unit-label-map))))
  ([param]
     (unit-slider param false)))

(defn panner-slider
  ([param lab-map vertical]
     (let [s (slider param -1.0 1.0 lab-map)]
       (ss/config! s :orientation (if vertical :vertical :horizontal))
       s))
  ([param lab-map]
     (panner-slider param lab-map false))
  ([param]
     (panner-slider param (pan-label-map))))

;; Returns border panel holding slider and label
;;
(defn slider-panel 
  ([slider text]
     (ss/border-panel :center slider
                      :south (ss/label :text (str text)
                                       :font default-font
                                       :halign :center)
                      :size slider-size))
  ([slider text size]
     (let [p (slider-panel slider text)]
       (ss/config! p :size size)
       p)))
  
(defn half-slider-panel [slider text]
  (slider-panel slider text half-slider-size))
                                     
  
;; Returns slider value. 
;; Note this is mapped 'real' value as required by program data
;; and not simply the slider's position.
;;
(defn slider-value [slider]
  (let [s (.getClientProperty slider :scale)
        b (.getClientProperty slider :bias)
        pos (.getValue slider)]
    (+ b (* s pos))))

;; Set slider position according to corresponding value in data
;;
(defn sync-slider [slider data]
  (let [param (.getClientProperty slider :param)
        value (get data param)
        rscale (.getClientProperty slider :rvs-scale)
        rbias (.getClientProperty slider :rvs-bias)]
    (if value
      (let [pos (int (+ rbias (* rscale value)))]
        (.setValue slider pos)))
    value))


; ---------------------------------------------------------------------- 
;                              'Micro' buttons

(def micro-button-size [18 :by 18])

(defn micro-button [icon-subgroup tooltip-text]
  (let [i (lnf/read-icon :mini icon-subgroup)
        b (ss/button :icon i
                     :size micro-button-size)]
    (.setToolTipText b (str (or tooltip-text (name icon-subgroup))))
    b))

(defn micro-button-panel [help-topic]
  (let [jb-init (micro-button :reset "Initialize")
        jb-dice (micro-button :dice "Randomize")
        jb-help (micro-button :help "Help")
        vgap 8
        pan (ss/vertical-panel :items [jb-init
                                       (Box/createVerticalStrut vgap)
                                       jb-dice
                                       (Box/createVerticalStrut vgap)
                                       jb-help
                                       (Box/createVerticalStrut vgap)]
                               :border (padding))]
    (.putClientProperty jb-help :topic help-topic)
    (ss/listen jb-help :action help/help-listener)
    {:panel pan
     :jb-init jb-init
     :jb-dice jb-dice
     :jb-help jb-help}))


; ---------------------------------------------------------------------- 
;                                  Matrix

(def ^:private control-bus-names ["CONSTANT" "ENV 1" "ENV 2" "ENV 3" "LFO 1"
                                  "LFO 2" "LFO 3" "STEPPER 1" "STEPPER 2"
                                  "DIVIDER ODD" "DIVIDER EVEN"
                                  "DIVIDER COMBINED" "NOISE" "SAMPLE-HOLD"
                                  "KEY-FREQUENCY" "KEY-PERIOD" "KEY-NUMBER"
                                  "MIDI PRESSURE" "MIDI VELOCITY"
                                  "MIDI CC A" "MIDI CC B" "MIDI CC C"
                                  "MIDI CC D" "BUS A" "BUS B" "BUS C" "BUS D"
                                  "BUS E" "BUS F" "BUS G" "BUS H" "KEY GATE"
                                  "OFF"])

(def ^:private name->busnumber {"CONSTANT" 0,
                                 "ENV 1" 1, "ENV 2" 2, "ENV 3" 3, 
                                 "LFO 1" 4, "LFO 2" 5, "LFO 3" 6,
                                 "STEPPER 1" 7, "STEPPER 2" 8,
                                 "DIVIDER ODD" 9, "DIVIDER EVEN" 10, "DIVIDER COMBINED" 11,
                                 "NOISE" 12, "SAMPLE-HOLD" 13,
                                 "KEY-FREQUENCY" 14, "KEY-PERIOD" 15, "KEY-NUMBER" 16,
                                 "MIDI PRESSURE" 17, "MIDI VELOCITY" 18,
                                 "MIDI CC A" 19, "MIDI CC B" 20, "MIDI CC C" 21, "MIDI CC D" 22,
                                 "BUS A" 23, "BUS B" 24, "BUS C" 25, "BUS D" 26, 
                                 "BUS E" 27, "BUS F" 28, "BUS G" 29, "BUS H" 30,
                                 "KEY GATE" 31, "OFF" 32})

(def ^:private busnumber->name (let [acc* (atom {})]
                                 (doseq [[k v] name->busnumber]
                                   (swap! acc* (fn [q](assoc q v k))))
                                 @acc*))

;; Creates matrix bus selection listbox
;; Returns map with following keys
;;   :panel - JPanel holding listbox
;;   :syncfn - Function called to update selected list index
;;
(defn matrix-listbox [ied param]
  (let [enable-selection-listener* (atom true)
        lstbx (ss/listbox :model control-bus-names)
        selection-listener (proxy [ListSelectionListener][]
                             (valueChanged [ev]
                               (if @enable-selection-listener*
                                 (let [i (max 0 (.getSelectedIndex lstbx))
                                       name (nth control-bus-names i)
                                       busnumber (name->busnumber name)]
                                   (.set-param! ied param busnumber)))))
        syncfn (fn [data]
                 (reset! enable-selection-listener* false)
                 (let [busnumber (get data param 32)]
                   (.setSelectedIndex lstbx busnumber)
                   (.ensureIndexIsVisible lstbx busnumber))
                 (reset! enable-selection-listener* true))
        pan (ss/horizontal-panel :items [(ss/scrollable lstbx)])]
    (.addListSelectionListener lstbx selection-listener)
    {:panel pan
     :syncfn syncfn}))

(defn matrix-toolbar 
  ([param ied] (matrix-toolbar param ied ""))
  ([param ied busnum]
     (let [enable-change-listener* (atom true)
           labmap (let [ht (Hashtable. 10)]
                    (.put ht (int 0)(ss/label :text "1" :font default-font))
                    (.put ht (int 1)(ss/label :text "A" :font default-font))
                    (.put ht (int 2)(ss/label :text "B" :font default-font))
                    (.put ht (int 3)(ss/label :text "C" :font default-font))
                    (.put ht (int 4)(ss/label :text "D" :font default-font))
                    (.put ht (int 5)(ss/label :text "E" :font default-font))
                    (.put ht (int 6)(ss/label :text "F" :font default-font))
                    (.put ht (int 7)(ss/label :text "G" :font default-font))
                    (.put ht (int 8)(ss/label :text "H" :font default-font))
                    (.put ht (int 9)(ss/label :text "0" :font default-font))
                    ht)
           s (ss/slider :orientation :vertical
                        :inverted? true
                        :value 0 :min 0 :max 9
                        :size slider-size
                        :snap-to-ticks? true
                        :paint-labels? true
                        :major-tick-spacing 1)
           syncfn (fn [data]
                    (reset! enable-change-listener* false)
                    (let [v (get data param)]
                      (.setValue s (int v)))
                    (reset! enable-change-listener* true))

           valuefn (fn [v]
                     (let [pos (.getValue s)]
                       (int pos))) ]
    (.setLabelTable s labmap)
    (.addChangeListener s (proxy [ChangeListener][]
                            (stateChanged [_]
                              (if @enable-change-listener*
                                (let [bus (.getValue s)]
                                  ;(println (format "DEBUG slider positon %s" pos))
                                  (.set-param! ied param (float bus)))))))
    {:panel (ss/border-panel :center s
                             :south (ss/label :text (format "Bus %s" busnum)
                                              :halign :center
                                              :font default-font)
                             :size slider-size)
     :syncfn syncfn
     :valuefn valuefn})))

; ---------------------------------------------------------------------- 
;                                 Spinners

(defn spinner [param ival minval maxval step]
  (let [m (ss/spinner-model ival :from minval :to maxval :by step)
        s (ss/spinner :model m)]
    (.putClientProperty s :param param)
    (.putClientProperty s :scale 1.0)
    (.putClientProperty s :bias 0.0)
    (.putClientProperty s :rvs-scale 1.0)
    (.putClientProperty s :rvs-bias 0.0)
    s))

(defn spinner-panel 
  ([spinner text]
     (ss/border-panel :center spinner
                      :south (ss/label :text (str text)
                                       :halign :center)))
  ([spinner text size]
     (let [p (spinner-panel spinner text)]
       (ss/config! p :size size)
       p)))

(defn sync-spinner [s data]
  (let [param (.getClientProperty s :param)
        value (double (get data param 0.0))]
    (.setValue s value)))


