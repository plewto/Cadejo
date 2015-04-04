(ns cadejo.instruments.alias.editor.matrix-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss]))



(def ^:private c1 (lnf/text))
(def ^:private c2 (lnf/text))
(def ^:private c3 (uc/color :red))

(def ^:private general-bus-states  
  [[:con "CON" c1]
   [:a " A " c2][:b " B " c2][:c " C " c2][:d " D " c2]
   [:e " E " c2][:f " F " c2][:g " G " c2][:h " H " c2]
   [:off "OFF" c1]])

(def ^:private control-bus-states 
  [[:con     " ONE  " c1]
   [:env1    "ENV 1 " c2][:env2    "ENV 2 " c2][:env3    "ENV 3 " c2]
   [:lfo1    "LFO 1 " c2][:lfo2    "LFO 2 " c2][:lfo3    "LFO 3 " c2]
   [:stp1    "STEP 1" c2][:stp2    "STEP 2" c2]
   [:div1    "DIV 1 " c2][:div2    "DIV 2 " c2][:div     "DIV   " c2]
   [:lfnse   "LFNSE " c2][:sh      "  SH  " c2]
   [:freq    " FREQ " c2][:period  "Period" c2][:note    " Note " c2]
   [:prss    " Prss " c2][:vel     " Vel  " c2]
   [:cca     " CCA  " c2][:ccb     " CCB  " c2][:ccc     " CCC  " c2][:ccd     " CCD  " c2]
   [:a       "  A   " c3][:b       "  B   " c3][:c       "  C   " c3][:d       "  D   " c3]
   [:e       "  E   " c3][:f       "  F   " c3][:g       "  G   " c3][:h       "  H   " c3]
   [:gate    " Gate " c2][:off     " OFF  " c1]])

;; Returns sgwr multi-state button for selection of general matrix bus
;;
(defn source-button [drw ieditor id p0]
  (let [w 40
        h 40
        ;; c1 (lnf/text)
        ;; c2 (lnf/selected-text)
        action (fn [b _] 
                 (let [state (msb/current-multistate-button-state b)]
                   (.set-param! ieditor id (first state))
                   (.status! ieditor (format "[%s] -> %s" id (second state)))))
        msb (msb/text-multistate-button (.tool-root drw) p0 general-bus-states
                                        :click-action action
                                        :w w :h h
                                        :gap 4)]
    msb))
  

;; Returns sgwr mulit-state button for bus selection within control block
;;
(defn control-source-button [drw ieditor id p0]
  (let [w 50
        h 40
        action (fn [b _]
                 (let [state (msb/current-multistate-button-state b)]
                   (.set-param! ieditor id (first state))
                   (.status! ieditor (format "[%s] -> %s" id (second state)))))
        msb (msb/text-multistate-button (.tool-root drw) p0 control-bus-states
                                        :click-action action
                                        :text-size 6.0
                                        :rim-color (lnf/button-border)
                                        :w w :h h :gap 4)]
    msb))
                                        

;; Places matrix routing overview onto sgwr drawing
;; Returns function to update overview 
;;  
(defn matrix-overview [drw p0]
  (let[[x0 y0] p0
       delta-y 20
       c (lnf/selected-text)
       txobj (fn [pos bus] 
               (let [obj (text/text (.root drw) 
                                    [x0 (+ y0 (* pos delta-y))]
                                    ""
                                    :id :matrix-overview 
                                    :style :mono
                                    :size 6
                                    :color (lnf/text))]
                 (.put-property! obj :bus-name (.toUpperCase bus))
                 (.put-property! obj :bus bus)
                 obj))
       txa (txobj 0 "a")
       txb (txobj 1 "b")
       txc (txobj 2 "c")
       txd (txobj 3 "d")
       txe (txobj 4 "e")
       txf (txobj 5 "f")
       txg (txobj 6 "g")
       txh (txobj 7 "h")
       sync-txtobj (fn [obj dmap]
                     (let [bus (.get-property obj :bus)
                           param-s1 (keyword (format "%s-source1" bus))
                           param-s2 (keyword (format "%s-source2" bus))]
                       (.put-property! obj :text 
                                       (format "%s <-- %-8s   %-8s"
                                               (.get-property obj :bus-name)
                                               (constants/reverse-control-bus-map (param-s1 dmap))
                                               (constants/reverse-control-bus-map (param-s2 dmap))))))
       sync-fn (fn [dmap]
                 (doseq [tx [txa txb txc txd txe txf txg txh]]
                   (sync-txtobj tx dmap)))]
    sync-fn))
                                                              


(def ^:private src->button-state {0 0, 1 1, 2 2, 3 3, 4 4, 5 5, 6 6,
                                  7 7, 8 8, 9 9, 10 10, 11 11,
                                  12 12, 13 13, 14 14, 15 15, 16 16, 17 17,
                                  18 18, 19 19, 20 20, 21 21, 22 22,
                                  31 23, 32 24})
(def ^:private width 2200)
(def ^:private height 550)

(declare draw-background)

(defn matrix-editor [ied]
  (let [p0 [0 height]
        drw (sfactory/sgwr-drawing width height)
        x0 100
        x-space 100
        y-space 50
        y0 100
        y-text (+ y0 120)
        states (let [acc* (atom [])]
                 (doseq [n (filter (fn [q](or (< q 23)(>= q 31)))(range 0 33 1))]
                   (swap! acc* (fn [q](conj q [(keyword (str n)) (name (constants/reverse-control-bus-map n)) (lnf/text)]))))
                 @acc*)
        action (fn [b _] 
                 (let [param (.get-property b :param)
                       state (math/str->int (name (second (msb/current-multistate-button-state b))))]
                   (.set-param! ied param state)))
        assignments (let [acc* (atom [])
                          x* (atom x0)]
                      (doseq [bus '[a b c d e f g h]]
                        (sfactory/label drw [@x* y-text] (format "%s" (.toUpperCase (str bus))) :size 8.0 :offset [35 0])
                                        
                        (doseq [n [0 1]]
                          (let [param (keyword (format "%s-source%s" bus (inc n)))
                                b (msb/text-multistate-button (.tool-root drw)
                                                              [@x* (+ y0 (* n y-space))]
                                                              states
                                                              :click-action action
                                                              :text-color (lnf/text)
                                                              :rim-color (lnf/button-border)
                                                              :rim-radius 4
                                                              :w 80 :h 40 :gap 8)]
                            (.put-property! b :param param)
                            (swap! acc* (fn [q](conj q b)))))
                        (swap! x* (fn [q](+ q x-space))))
                      @acc*)
        pan-main (ss/scrollable (ss/vertical-panel :items [(.canvas drw)]))
        widget-map {:pan-main pan-main
                    :drawing drw}
        ed (reify subedit/InstrumentSubEditor
             (widgets [this] widget-map)
             (widget [this key](key widget-map))
             (parent [this] ied)
             (parent! [this _] ied) ;;' ignore
             (status! [this msg](.status! ied msg))
             (warning! [this msg](.warning! ied msg))
             (set-param! [this param value](.set-param! ied param value))
             (init! [this]  ) ;; not implemented
             (sync-ui! [this]
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (doseq [b assignments]
                   (let [param (.get-property b :param)
                         srcnum (param dmap)
                         state-index (get src->button-state srcnum)]
                     (msb/set-multistate-button-state! b state-index false)))
                 (.render drw))))]
    (sfactory/label drw [x0 y-text] "Bus" :offset [-50 0])
    (let [x 900
          y* (atom y0)
          y-delta 15]
      (sfactory/label drw [x @y*] "Legend:")
      (doseq [s ["con    - Constant 1"
                 "env1   - Envelope 1"
                 "env2   - Envelope 2"
                 "env3   - Envelope 3 (main amp envelope)"
                 "lfo1   - LFO 1 (default vibrato)"
                 "lfo2   - LFO 2"
                 "lfo3   - LFO 3"
                 "step1  - Step counter 1"
                 "step2  - Step counter 2"
                 "div1   - Frequency Divider (odd)"
                 "div2   - Frequency Divider (even)"
                 "div    - Sum  div1 + div2"
                 "lfnse  - Low Frequency Noise"
                 "sh     - Sample & Hold"
                 "freq   - Key frequency (hz)"
                 "period - 1/freq"
                 "keynum - MIDI key number"
                 "press  - MIDI Pressure"
                 "vel    - MIDI Velocity"
                 "cca    - MIDI cc a (default 1)"
                 "ccb    - MIDI cc b"
                 "ccc    - MIDI cc c"
                 "ccd    - MIDI cc d"
                 "gate   - Key gate signal"
                 "off   -  Disable bus"]]
        (sfactory/label drw [x @y*] s :offset [10 y-delta])
        (swap! y* (fn [q](+ q y-delta)))))
    (sfactory/title drw [(- x0 0)(- y0 40)] "Matrix Bus Assignments")  
    ed)) 
        
                        


        
  
