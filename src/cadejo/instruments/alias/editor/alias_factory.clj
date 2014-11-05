(ns cadejo.instruments.alias.editor.alias-factory
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [seesaw.core :as ss])
  (:require [cadejo.ui.util.factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:import java.util.Hashtable
           javax.swing.event.ChangeListener
           javax.swing.event.ListSelectionListener
           ))

(def title cadejo.ui.util.factory/title)
(def padding  cadejo.ui.util.factory/padding)
(def line  cadejo.ui.util.factory/line)
(def bevel cadejo.ui.util.factory/bevel)
(def button cadejo.ui.util.factory/button)
(def toggle cadejo.ui.util.factory/toggle)
(def radio cadejo.ui.util.factory/radio)


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

;; Creates limited matrix output bus selection panel
;; Selection limited to buses A...G 
;; Returns map
;;  :panel - JPanel 
;;  :syncfn - (fn [data]) called on data to syn buttons to data
;;  :valuefn (fn), returns currently selected bus number or nil
;;
(defn matrix-outbus-panel [ied param]
  (let [grp (ss/button-group)
        selected* (atom nil)
        action (fn [ev]
                 (let [src (.getSource ev)
                       busnum (.getClientProperty src :bus-number)]
                   (.set-param! ied param busnum)
                   (reset! selected* src)))
        buttons (let [acc* (atom (sorted-map))]
                  (doseq [[sym val][[:A  1][:B  2][:C  3] 
                                    [:D  4][:E  5][:F  6] 
                                    [:G  7][:H  8][:CON 0]]]
                    (let [b (ss/toggle :text (name sym) :group grp)]
                      (.putClientProperty b :param param)
                      (.putClientProperty b :bus-number val)
                      (.putClientProperty b :bus-name sym)
                      (ss/listen b :action action)
                      (swap! acc* (fn [q](assoc q val b)))))
                  @acc*)
        panel (ss/grid-panel :rows 3 :columns 3 :items (vals buttons))
        syncfn (fn [data]
                 (let [busnum (get data param)
                       b (get buttons busnum)]
                   (.clearSelection grp)
                   (reset! selected* nil)
                   (if b 
                     (do 
                       (.setSelected b true)
                       (reset! selected* b)))))
        resetfn (fn []
                  (.doClick (get buttons 23)))
        valuefn (fn []
                  (let [b @selected*]
                    (if b 
                      (.getClientProperty b :bus-number)
                      nil)))]
    {:panel panel
     :syncfn syncfn
     :resetfn resetfn
     :valuefn valuefn}))

; ---------------------------------------------------------------------- 
;                                 Spinners

(defn spinner [param ival minval maxval step]
  (let [m (ss/spinner-model ival :from minval :to maxval :step step)
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


; ---------------------------------------------------------------------- 
;                                  Sliders

;; Returns Hashtable for use as slider label map
;; with 5 marked positions.
;; The positions are evenly spaced at 0%, 25%,
;; 50%, 75% and 100%.
;;
(defn slider-label-map [p0 p25 p50 p75 p100]
  (let [ht (Hashtable. 5)]
    (.put ht (int 0)(ss/label :text (str p0)))
    (.put ht (int 25)(ss/label :text (str p25)))
    (.put ht (int 50)(ss/label :text (str p50)))
    (.put ht (int 75)(ss/label :text (str p75)))
    (.put ht (int 100)(ss/label :text (str p100)))
    ht))

(defn signed-unit-label-map []
  (slider-label-map "-1.0" "-0.5" " 0.0" "+0.5" "+1.0"))

(defn unsigned-unit-label-map []
  (slider-label-map "0.00" "0.25" "0.50" "0.75" "1.00"))

(defn pan-label-map []
  (slider-label-map "F2" "" "" "" "F1"))

(def ^:private slider-width 50)
(def ^:private slider-height 100)

;; Returns vertical JSlider with 100 positions.
;; param - keyword parameter thias slider controls
;; minval - float, The minimum value 
;; maxval - float, The maximum value
;; labmap - boolean or Hashtable. 
;;          
;; Returns JSlider wit following client properties
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
                     :major-tick-spacing 25
                     :size [slider-width :by slider-height])]
    (if (= (type labmap) Hashtable)
      (.setLabelTable s labmap))
    (.putClientProperty s :param param)
    (.putClientProperty s :scale scale)
    (.putClientProperty s :bias bias)
    (.putClientProperty s :rvs-scale rvs-scale)
    (.putClientProperty s :rvs-bias rvs-bias)
    s))

(defn mix-slider [param]
  (let [s (ss/slider :orientation :vertical
                     :value 0 :min -99 :max 0 
                     :snap-to-ticks? false
                     :paint-labels? true
                     :minor-tick-spacing 6
                     :major-tick-spacing 24
                     :size [slider-width :by slider-height])]
    (.putClientProperty s :param param)
    (.putClientProperty s :scale 1.0)
    (.putClientProperty s :bias 0.0)
    (.putClientProperty s :rvs-scale 1.0)
    (.putClientProperty s :rvs-bias 0.0)
    s))

;; Returns border panel holding slider and label
;;
(defn slider-panel 
  ([slider text]
     (ss/border-panel :center slider
                      :south (ss/label :text (str text)
                                       :halign :center)))
  ([slider text size]
     (let [p (slider-panel slider text)]
       (ss/config! p :size size)
       p)))
                                       
  
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
