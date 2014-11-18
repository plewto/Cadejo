(ns cadejo.instruments.alias.editor.matrix-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.util.math :as umath])
  (:require [sgwr.coordinate-system])
  (:require [sgwr.drawing])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf])
  (:import java.awt.event.MouseListener
           java.awt.event.MouseMotionListener
           javax.swing.SwingUtilities
           javax.swing.Box
           javax.swing.event.ChangeListener))

;; Colors
(def ^:private c1 (uc/color [160  96 131])) ; envelopes
(def ^:private c2 (uc/color [128  96  96])) ; noise
(def ^:private c3 (uc/color [144 160  96])) ; general bus A-H
(def ^:private c4 (uc/color [ 76 140 124])) ; LFO
(def ^:private c5 (uc/color [ 96  96 200])) ; key
(def ^:private c6 (uc/color [120  32  64])) ; ctrl 
(def ^:private background-color (uc/color :black))
(def ^:private pin-color (uc/color :cyan))

(def ^:private off 32)
(def ^:private con 0)

(def ^:private param->pin-style {:lfo1-freq1-source 0
                       :lfo1-freq2-source 7
                       :lfo2-freq1-source 0
                       :lfo2-freq2-source 7
                       :lfo3-freq1-source 0
                       :lfo3-freq2-source 7
                       :lfo1-wave1-source 0
                       :lfo1-wave2-source 7
                       :lfo2-wave1-source 0
                       :lfo2-wave2-source 7
                       :lfo3-wave1-source 0
                       :lfo3-wave2-source 7
                       :stepper1-trigger 0
                       :stepper1-reset 0
                       :stepper2-trigger 0
                       :stepper2-reset 0
                       :divider1-scale-source 0
                       :divider2-scale-source 0
                       :lfnoise-freq-source 0
                       :sh-source 0 
                       :a-source1 0
                       :a-source2 8
                       :b-source1 0
                       :b-source2 8
                       :c-source1 0
                       :c-source2 8
                       :d-source1 0
                       :d-source2 8
                       :e-source1 0
                       :e-source2 8
                       :f-source1 0
                       :f-source2 8
                       :g-source1 0
                       :g-source2 8
                       :h-source1 0
                       :h-source2 8
                       :osc1-fm1-source 0
                       :osc1-fm2-source 7
                       :osc1-wave1-source 0
                       :osc1-wave2-source 7
                       :osc1-amp1-src 0
                       :osc1-amp2-src 8
                       :osc2-fm1-source 0
                       :osc2-fm2-source 7
                       :osc2-wave1-source 0
                       :osc2-wave2-source 7
                       :osc2-amp1-src 0
                       :osc2-amp2-src 8
                       :osc3-fm1-source 0
                       :osc3-fm2-source 7
                       :osc3-wave1-source 0
                       :osc3-wave2-source 8
                       :osc3-amp1-src 0
                       :osc3-amp2-src 9
                       :noise-amp1-src 0
                       :noise-amp2-src 8
                       :ringmod-amp1-src 0
                       :ringmod-amp2-src 8
                       :distortion1-param-source 0
                       :filter1-freq1-source 0
                       :filter1-freq2-source 7
                       :filter1-res-source 0
                       :filter1-pan-source 0
                       :distortion2-param-source 0
                       :filter2-freq1-source 0
                       :filter2-freq2-source 7
                       :filter2-res-source 0
                       :filter2-pan-source 0
                       :pitchshift-ratio-source 0
                       :flanger-mod-source 0 
                       :echo1-delay-source 7
                       :echo1-amp-source 8
                       :echo2-delay-source 7
                       :echo2-amp-source 8})
  
(defn- pin-style [param]
  (let [rs (get param->pin-style (keyword param))]
    rs))

(def ^:private row-color-map 
  {"A" c3 "B" c3 "C" c3 "D" c3 "E" c3 "F" c3 "G" c3 "H" c3
   "ENV-1" c1 "ENV-2" c1 "ENV-3" c1
   "LFO-1" c4 "LFO-2" c4 "LFO-3" c4 "STEP-1" c4 "STEP-2" c4 "DIV-1" c4 "DIV-2" c4 "DIV-3" c4
   "LF-NOISE" c2 "SAM-HOLD" c2
   "KEY FREQ" c5 "KEY PERIOD" c5 "KEY NUMBER" c5 "PRESSURE" c5 "VELOCITY" c5 "GATE" c5
   "ON" c6 "OFF" c6})

(def ^:private column-color-map 
  {"A" c3 "B" c3 "C" c3 "D" c3 "E" c3 "F" c3 "G" c3 "H" c3
   "LFO1f" c4 "LFO1w" c4 "LFO2f" c4 "LFO2w" c4 "LFO3f" c4 "LFO3w" c4 
   "STP1r" c4 "STP1t" c4 "STP2r" c4 "STP2t" c4 "DIV1" c4 "DIV2" c4
   "NSE" c2 "SH" c2})

(defn- column-color [c-name]
  (get column-color-map c-name c6))

(defn- row-color [r-name]
  (get row-color-map r-name c6))

(defn- pin-color [c-name]
  (uc/color :cyan))


;; Dimensions
;;
(def ^:private left-margin  24)
(def ^:private text-gap 16)
(def ^:private text-width 64)
(def ^:private right-margin left-margin)
(def ^:private header-height 24)
(def ^:private row-height 15)
(def ^:private footer-height header-height)
(def ^:private font-size 1.5)


;; row-list & column-list
;; ordered list of form 
;;    [name0 name1 name3 ...]
;; 
(defn- create-drawing [row-list column-list & {:keys [column-width]
                                              :or {column-width 40}}]
  (let [row-count (count row-list)
        column-count (count column-list)
        x0 0
        x1 (+ x0 left-margin)
        x2 (+ x1 (* (dec column-count) column-width))
        x3 (+ x2 text-gap)
        x4 (+ x3 text-width)
        x5 (+ x4 right-margin)
        y0 0
        y1 (+ y0 header-height)
        y2 (+ y1 (* (dec row-count) row-height))
        y3 (+ y2 footer-height)
        drawing-width x5
        drawing-height y3
        column->x (fn [c](int (+ x1 (* c column-width))))
        x->column (fn [x](int (/ (- x x1) column-width)))
        row->y (fn [r](int (+ y1 (* r row-height))))
        y->row (fn [y](int (/ (- y y1) row-height)))
        csys (reify sgwr.coordinate-system/CoordinateSystem
               (canvas-bounds [this][drawing-width drawing-height])
               (view [this](.canvas-bounds this))
               (view! [this _](.view this)) ; ignore
               ;; map matrix position p [row column] to pixel coordinates [x y]
               (map-point [this p]
                 (let [[r c] p]
                   [(column->x c)(row->y r)]))
               ;; map pixel coordinates q [x y] to matrix position [row column]
               (inv-map [this q]
                 (let [[x y] q]
                   [(y->row y)(x->column x)]))
               (clip [this q]
                 (let [[x y] q]
                   [(math/clamp x 0 drawing-width)
                    (math/clamp y 0 drawing-height)]))
               (distance [this _ __] nil) ; not implemented
               (zoom! [this _] nil)       ; not implemented
               (zoom-ratio [this] nil)    ; not implemented
               (to-string [this] "sgwr.CoordinateSystem  (Alias matrix)"))
        drw (let [d (sgwr.drawing/native-drawing drawing-width drawing-height)]
              (.suspend-render! d true)
              (.paper! d background-color)
              ;; draw columns
              (let [col* (atom 0)]
                (doseq [cname column-list]
                  (let [x (column->x @col*)
                        xt (- x 4)
                        yt (- y1 8)]
                    (.color! d (column-color cname))
                    (.style! d 0)
                    (.width! d 1)
                    (.line! d [x y1][x y2])
                    (.width! d font-size)
                    (.text! d [xt yt](str cname))
                    (swap! col* inc))))
              ;; draw rows
              (let [row* (atom 0)]
                (doseq [rname row-list]
                  (let [y (row->y @row*)
                        xt x3
                        yt (+ y 6) ]
                    (.color! d (row-color rname))
                    (.style! d 0)
                    (.width! d 1)
                    (.line! d [x1 y][x2 y])
                    (.width! d font-size)
                    (.text! d [xt yt](.toUpperCase (str rname)))
                    (swap! row* inc))))
              (.render d)
              (.coordinate-system! d csys :freeze)
              (ss/config! (.drawing-canvas d) :size [drawing-width :by drawing-height])
              d)]
    drw))


(def ^:private source-list-1  [{:name "ON" :bus-number 0 :row  0}
                               {:name "ENV-1" :bus-number 1 :row  1}
                               {:name "ENV-2" :bus-number 2 :row  2}
                               {:name "ENV-3" :bus-number 3 :row  3}
                               {:name "LFO-1" :bus-number 4 :row  4}
                               {:name "LFO-2" :bus-number 5 :row  5}
                               {:name "LFO-3" :bus-number 6 :row  6}
                               {:name "STEP-1" :bus-number 7 :row  7}
                               {:name "STEP-2" :bus-number 8 :row  8}
                               {:name "DIV-1" :bus-number 9 :row  9}
                               {:name "DIV-2" :bus-number 10 :row 10}
                               {:name "DIV-3" :bus-number 11 :row 11}
                               {:name "LF-NOISE" :bus-number 12 :row 12}
                               {:name "SAM-HOLD" :bus-number 13 :row 13}
                               {:name "KEY FREQ" :bus-number 14 :row 14}
                               {:name "KEY PERIOD" :bus-number 15 :row 15}
                               {:name "KEY NUMBER" :bus-number 16 :row 16}
                               {:name "PRESSURE" :bus-number 17 :row 17}
                               {:name "VELOCITY" :bus-number 18 :row 18}
                               {:name "CTRL-A" :bus-number 19 :row 19}
                               {:name "CTRL-B" :bus-number 20 :row 20}
                               {:name "CTRL-C" :bus-number 21 :row 21}
                               {:name "CTRL-D" :bus-number 22 :row 22}
                               ;; {:name "A" :bus-number 23 :row 23}
                               ;; {:name "B" :bus-number 24 :row 24}
                               ;; {:name "C" :bus-number 25 :row 25}
                               ;; {:name "D" :bus-number 26 :row 27}
                               ;; {:name "E" :bus-number 27 :row 28}
                               ;; {:name "F" :bus-number 28 :row 29}
                               ;; {:name "G" :bus-number 29 :row 20}
                               ;; {:name "H" :bus-number 30 :row 30}
                               {:name "GATE" :bus-number 31 :row 23}
                               {:name "OFF" :bus-number 32 :row 24}])


(def ^:private destination-list-1 [{:name "LFO1f" :params '[lfo1-freq1-source lfo1-freq2-source]}
                         {:name "LFO1w" :params '[lfo1-wave1-source lfo1-wave2-source]}
                         {:name "LFO2f" :params '[lfo2-freq1-source lfo2-freq2-source]}
                         {:name "LFO2w" :params '[lfo2-wave1-source lfo2-wave2-source]}
                         {:name "LFO3f" :params '[lfo3-freq1-source lfo3-freq2-source]}
                         {:name "LFO3w" :params '[lfo3-wave1-source lfo3-wave2-source]}
                         {:name "STP1t" :params '[stepper1-trigger]}
                         {:name "STP1r" :params '[stepper1-reset]}
                         {:name "STP2t" :params '[stepper2-trigger]}
                         {:name "STP2r" :params '[stepper2-reset]}
                         {:name "DIV1" :params '[divider1-scale-source]}
                         {:name "DIV2" :params '[divider2-scale-source]}
                         {:name "NSE" :params '[lfnoise-freq-source]}
                         {:name "SH" :params '[sh-source]}
                         {:name "A" :params '[a-source1 a-source2]}
                         {:name "B" :params '[b-source1 b-source2]} 
                         {:name "C" :params '[c-source1 c-source2]} 
                         {:name "D" :params '[d-source1 d-source2]} 
                         {:name "E" :params '[e-source1 e-source2]} 
                         {:name "F" :params '[f-source1 f-source2]} 
                         {:name "G" :params '[g-source1 g-source2]} 
                         {:name "H" :params '[h-source1 h-source2]} 
                         ])

(def ^:private source-list-2 [{:name "ON" :bus-number 0 :row 0}
                              {:name "A" :bus-number 1 :row 1}
                              {:name "B" :bus-number 2 :row 2}
                              {:name "C" :bus-number 3 :row 3}
                              {:name "D" :bus-number 4 :row 4}
                              {:name "E" :bus-number 5 :row 5}
                              {:name "F" :bus-number 6 :row 6}
                              {:name "G" :bus-number 7 :row 7}
                              {:name "H" :bus-number 8 :row 8}
                              {:name "OFF" :bus-number 9 :row 9}])

(def ^:private destination-list-2 
  [{:name "FM1" :params '[osc1-fm1-source osc1-fm2-source]}
   {:name "WV1" :params '[osc1-wave1-source osc1-wave2-source]}
   {:name "AM1" :params '[osc1-amp1-src osc1-amp2-src]}
   {:name "FM2" :params '[osc2-fm1-source osc2-fm2-source]}
   {:name "WV2" :params '[osc2-wave1-source osc2-wave2-source]}
   {:name "AM2" :params '[osc2-amp1-src osc2-amp2-src]}
   {:name "FM2" :params '[osc3-fm1-source osc3-fm2-source]}
   {:name "WV3" :params '[osc3-wave1-source osc3-wave2-source]}
   {:name "AM3" :params '[osc3-amp1-src osc3-amp2-src]}
   {:name "NSE" :params '[noise-amp1-src noise-amp2-src]}
   {:name "RM" :params '[ringmod-amp1-src ringmod-amp2-src]}
   {:name "DST1" :params '[distortion1-param-source]}
   {:name "F1FM" :params '[filter1-freq1-source filter1-freq2-source]}
   {:name "F1RS" :params '[filter1-res-source]}
   {:name "F1PN" :params '[filter1-pan-source]}
   {:name "DST2" :params '[distortion2-param-source]}
   {:name "F2FM" :params '[filter2-freq1-source filter2-freq2-source]}
   {:name "F2RS" :params '[filter2-res-source]}
   {:name "F2PN" :params '[filter2-pan-source]}
   {:name "SHFT" :params '[pitchshift-ratio-source]}
   {:name "FLNG" :params '[flanger-mod-source]}
   {:name "DL1T" :params '[echo1-delay-source]}
   {:name "DL1A" :params '[echo1-amp-source]}
   {:name "DL2T" :params '[echo2-delay-source]}
   {:name "DL2A" :params '[echo2-amp-source]}])

(defn- mtrxed [performance ied source-list destination-list]
  (let [row-labels (mapv (fn [q](:name q)) source-list)
        row-count (count row-labels)
        col-labels (mapv (fn [q](:name q)) destination-list)
        drw (create-drawing row-labels col-labels)
        csys (.coordinate-system drw)
        pins (let [acc* (atom {})       ; map param --> pin
                   col* (atom 0)]
               (doseq [spec destination-list]
                 (let [col-name (:name spec)]
                   (.color! drw (pin-color col-name))
                   (doseq [param (:params spec)]
                     (.style! drw (pin-style param))
                     (let [pin (.point! drw [(rand-int 4) @col*])
                           att (.attributes pin)]
                       (.put-property! att :param (keyword param))
                       (.put-property! att :column @col*)
                       (swap! acc* (fn [q](assoc q param pin)))))
                   (swap! col* inc)))
               @acc*)
        bus->src (let [acc* (atom {})]
                   (doseq [s source-list]
                     (let [busnum (:bus-number s)]
                       (swap! acc* (fn [q](assoc q busnum s)))))
                   @acc*)
        current-pin* (atom nil)
        widget-map {:pan-main (.drawing-canvas drw)}]
    
    (.add-mouse-motion-listener! drw 
     (proxy [MouseMotionListener][]
       
       (mouseDragged [ev]
         (let [x (.getX ev)
               y (.getY ev)
               pos (.inv-map csys [x y])
               pin @current-pin*
               row (math/clamp (first pos) 0 (dec row-count))]
           (if pin 
             (let [[_ col](first (.construction-points pin))]
               (.position! pin [[row col]])
               (.render drw)))))

       (mouseMoved [_])))
       
    (.add-mouse-listener! drw
     (proxy [MouseListener][]
       (mouseEntered [_])
       (mouseExited [_])
       (mouseClicked [_])
       
       (mousePressed [ev] 
         (let [x (.getX ev)
               y (.getY ev)
               pos (.inv-map csys [x y])
               pin (.closest drw pos)
               att (.attributes pin)
               dist (math/distance pos (first (.construction-points pin)))]
           (if (< dist 2)
             (do 
               (reset! current-pin* pin)
               (.select! att true)
               (.render drw)))))
       
       (mouseReleased [ev] 
         (let [pin @current-pin*]
           (if pin
             (let [x (.getX ev)
                   y (.getY ev)
                   pos (.inv-map csys [x y])
                   row (math/clamp (first pos) 0 (dec row-count))
                   spec (nth source-list row)
                   busnum (:bus-number spec)
                   att (.attributes pin)
                   param (.property att :param)]
               (.set-param! ied param busnum)
               (.select! att false)
               (reset! current-pin* false)
               (.render drw))))) )) 
    (.render drw)
    (reify subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      (widget [this key](get widget-map key))
      (parent [this] ied)
      (parent! [this _] ied)            ; not implemented
      (status! [this msg](.status! ied msg))
      (warning! [this msg](.warning! ied msg))
      (set-param! [this p v](.set-param! this p v))
      (init! [this] )                   ; not implemented
     
      
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [p (vals pins)]
            (let [param (.property (.attributes p) :param)
                  busnum (int (get data param 32))
                  spec (get bus->src busnum)
                  row (or (get spec :row) (dec row-count))
                  col (second (first (.construction-points p)))]
              (.position! p [[row col]])))
          (.render drw))) )))

(def ^:private legend-text 
" Destinations                      | Sources
 ----------------------------------+------------------------------------
 LFO1f - LFO-1 frequency           | ENV-1
 LFO1W - LFO-1 wave                | ENV-2
 LFO2f - LFO-2 frequency           | ENV-3
 LFO2W - LFO-2 wave                | LFO-1
 LFO3f - LFO-3 frequency           | LFO-2
 LFO3W - LFO-3 wave                | LFO-3
 STP1t - Stepper-1 trigger source  | STEP-1 Stepper-1
 STP1r - Stepper-1 reset source    | STEP-2 Stepper-2
 STP2t - Stepper-2 trigger source  | DIV-1 Divider-1 (odd)
 STP2r - Stepper-2 reset source    | DIV-2 Divider-2 (even
 DIV1  - Divider-1 amp             | DIV-3 (DIV-1+DIV2
 DIV2  - Divider-2 amp             | LF-NOISE
 NSE   - Low Freq noise frequency  | SAM-HOLD
 SH    - Sample & Hold input       | KEY FREQ   Key frequency (Hz)
 A     - Bus A                     | KEY PERIOD Reciprocal key frequency
 B     - Bus B                     | KEY NUMBER (midi key number)
 C     - Bus C                     | GATE
 D     - Bus D                     | PRESSURE
 E     - Bus E                     | VELOCITY
 F     - Bus F                     | CTRL-A MIDI controller
 G     - Bus G                     | CTRL-B MIDI controller
 H     - Bus H                     | CTRL-C MIDI controller
                                   | CTRL-C MIDI controller                                                                       
 FM1   - Osc 1 frequency           | A Bus A
 WV1   - OSc 1 wave                | B Bus B
 AM1   - Osc 1 amp                 | C Bus C
 FM2   - Osc 2 frequency           | D Bus D
 WV2   - Osc 2 wave                | E Bus E
 AM2   - Osc 2 amp                 | F Bus F
 FM3   - Osc 3 frequency           | G Bus G
 WV3   - Osc 3 wave                | H Bus H
 AM3   - Osc 3 amp                 | ON Constant 1.0
 NSE   - Noise amp                 | OFF Constant 0.0
 RM    - Ring modulator amp
 DST1  - Distortion 1 clip point     
 F1FM  - Filter 1 frequency              Pin styles:
 F1RS  - Filter 1 resonace       
 F1PN  - Filter 1 pan                      o - primary pin
 DST2  - Distortion2 clip point            + - additive
 F2FM  - Filter 2 frequency                x - multiplicative
 F2RS  - Filter 2 resonance
 F2PN  - Filter 2 pan
 SHFT  - Pitch shifter tune
 FLNG  - Flanger delay time
 DL1T  - Delay 1 time
 DL1A  - Delay 1 amp
 DL2T  - Delay 2 time
 DL2T  - Delay 2 amp")


(def ^:private default-matrix {:osc1-fm1-source 1
                               :osc1-fm2-source 9
                               :osc1-wave1-source 9
                               :osc1-wave2-source 9
                               :osc2-fm1-source 1
                               :osc2-fm2-source 9
                               :osc2-wave1-source 9
                               :osc2-wave2-source 9
                               :osc3-fm1-source 1
                               :osc3-fm2-source 9
                               :osc3-wave1-source 9
                               :osc3-wave2-source 9
                               :osc1-amp1-src 0
                               :osc1-amp2-src 0
                               :osc2-amp1-src 0
                               :osc2-amp2-src 0
                               :osc3-amp1-src 0
                               :osc3-amp2-src 0
                               :noise-amp1-src 0
                               :noise-amp2-src 0
                               :ringmod-amp1-src 0
                               :ringmod-amp2-src 0
                               :pitchshift-ratio-source 9
                               :flanger-mod-source 9
                               :echo1-delay-source 9
                               :echo1-amp-source 0
                               :echo1-amp-depth 1
                               :echo2-delay-source 9
                               :echo2-amp-source 0
                               :echo2-amp-depth 1
                               :lfo1-freq1-source 0
                               :lfo1-freq2-source 32
                               :lfo1-wave1-source 0
                               :lfo1-wave2-source 32
                               :lfo2-freq1-source 0
                               :lfo2-freq2-source 32
                               :lfo2-wave1-source 0
                               :lfo2-wave2-source 32
                               :lfo3-freq1-source 0
                               :lfo3-freq2-source 32
                               :lfo3-wave1-source 0
                               :lfo3-wave2-source 32
                               :divider1-scale-source 32
                               :divider2-scale-source 32
                               :lfnoise-freq-source 0
                               :sh-source 12
                               :a-source1 4
                               :b-source1 18 
                               :c-source1 1
                               :d-source1 2
                               :e-source1 7
                               :f-source1 12
                               :g-source1 13
                               :h-source1 9
                               :a-source2 19
                               :b-source2 32
                               :c-source2 32
                               :d-source2 32
                               :e-source2 32
                               :f-source2 32
                               :g-source2 32
                               :h-source2 32
                               :a-depth1 1
                               :b-depth1 1
                               :c-depth1 1
                               :d-depth1 1
                               :e-depth1 1
                               :f-depth1 1
                               :g-depth1 1
                               :h-depth1 1
                               :a-depth2 1
                               :b-depth2 1
                               :c-depth2 1
                               :d-depth2 1
                               :e-depth2 1
                               :f-depth2 1
                               :g-depth2 1
                               :h-depth2 1})


(def ^:private zero-matrix {:osc1-fm1-source con
                            :osc1-fm2-source off
                            :osc1-wave1-source con
                            :osc1-wave2-source off
                            :osc2-fm1-source con
                            :osc2-fm2-source off
                            :osc2-wave1-source con
                            :osc2-wave2-source off
                            :osc3-fm1-source con
                            :osc3-fm2-source off
                            :osc3-wave1-source con
                            :osc3-wave2-source off
                            :osc1-amp1-src con
                            :osc1-amp2-src off
                            :osc2-amp1-src con
                            :osc2-amp2-src off
                            :osc3-amp1-src con
                            :osc3-amp2-src off
                            :noise-amp1-src con
                            :noise-amp2-src off
                            :ringmod-amp1-src con
                            :ringmod-amp2-src off
                            :pitchshift-ratio-source off
                            :flanger-mod-source off
                            :echo1-delay-source off
                            :echo1-amp-source off
                            :echo2-delay-source off
                            :echo2-amp-source off
                            :lfo1-freq1-source con
                            :lfo1-freq2-source off
                            :lfo1-wave1-source con
                            :lfo1-wave2-source off
                            :lfo2-freq1-source con
                            :lfo2-freq2-source off
                            :lfo2-wave1-source con
                            :lfo2-wave2-source off
                            :lfo3-freq1-source con
                            :lfo3-freq2-source off
                            :lfo3-wave1-source con
                            :lfo3-wave2-source off
                            :divider1-scale-source off
                            :divider2-scale-source off
                            :lfnoise-freq-source con
                            :sh-source off
                            :a-source1 con
                            :a-source2 off
                            :b-source1 con
                            :b-source2 off
                            :c-source1 con
                            :c-source2 off
                            :d-source1 con
                            :d-source2 off
                            :e-source1 con
                            :e-source2 off
                            :f-source1 con
                            :f-source2 off
                            :g-source1 con
                            :g-source2 off
                            :h-source1 con
                            :h-source2 off
                            :a-depth1 1
                            :a-depth2 1
                            :b-depth1 1
                            :b-depth2 1
                            :c-depth1 1
                            :c-depth2 1
                            :d-depth1 1
                            :d-depth2 1
                            :e-depth1 1
                            :e-depth2 1
                            :f-depth1 1
                            :f-depth2 1
                            :g-depth1 1
                            :g-depth2 1
                            :h-depth1 1
                            :h-depth2 1})

(defn- dice-fn []
  (let [pick-bus (fn [](rand 32))]
    {:a-source1 (umath/coin 0.85 23 (pick-bus))
     :a-source2 (umath/coin 0.85 19 (pick-bus))
     :b-source1 (umath/coin 0.85 1 (pick-bus))
     :b-source2 (umath/coin 0.85 0 (pick-bus))
     :c-source1 (umath/coin 0.85 2 (pick-bus))
     :c-source2 (umath/coin 0.85 0 (pick-bus))
     :d-source1 (umath/coin 0.50 2 (pick-bus))
     :d-source2 (umath/coin 0.85 0 (pick-bus))
     :e-source1 (pick-bus)
     :e-source2 (umath/coin 0.85 0 (pick-bus))
     :f-source1 (pick-bus)
     :f-source2 (umath/coin 0.85 0 (pick-bus))
     :g-source1 (pick-bus)
     :g-source2 (umath/coin 0.85 0 (pick-bus))
     :h-source1 (pick-bus)
     :h-source2 (umath/coin 0.85 0 (pick-bus))}))
                      
(defn matrix-editor [performance ied]
  (let [m1 (mtrxed performance ied source-list-1 destination-list-1)
        m2 (mtrxed performance ied source-list-2 destination-list-2)
        txt-legend (ss/text :text legend-text
                            :multi-line? true
                            :editable? false
                            :font (ssf/font :name :monospaced
                                            :size 10)
                            :size [432 :by 656])
        jb-init (factory/button "Init" :general :reset "Set default matrix")
        jb-zero (factory/button "0" nil nil "Set 'zero' matrix")
        jb-dice (factory/button "Random" :general :dice "Randomize matrix")
        jb-help (let [b (factory/button  "Help" :general :help "Matrix help")]
                  (.putClientProperty b :topic :alias-matrix)
                  (ss/listen b :action help/help-listener)
                  b)
        pan-west (ss/grid-panel :columns 1 
                                :items [jb-init jb-zero jb-dice jb-help]
                                :border (factory/padding 12))
        pan-main (ss/scrollable 
                  (ss/border-panel :center (ss/vertical-panel 
                                            :items [(.widget m1 :pan-main)
                                                    (Box/createVerticalStrut 4)
                                                    (.widget m2 :pan-main)
                                                    ])
                                   :west pan-west
                                   :east (ss/border-panel :center txt-legend
                                                          :border (factory/padding))))]
    (ss/listen jb-init :action
               (fn [_]
                 (.working ied true)
                 (SwingUtilities/invokeLater
                  (proxy [Runnable][]
                    (run []
                      (doseq [[p v] default-matrix]
                          (.set-param! ied p v))
                      (.sync-ui! ied)
                      (.status! ied "Set matrix to default")
                      (.working ied false))))))
    (ss/listen jb-zero :action
               (fn [_]
                 (.working ied true)
                 (SwingUtilities/invokeLater
                  (proxy [Runnable][]
                    (run []
                      (doseq [[p v] zero-matrix]
                        (.set-param! ied p v))
                      (.sync-ui! ied)
                      (.status! ied "Reset matrix to 'zero'")
                      (.working ied false))))))
   
    (ss/listen jb-dice :action
               (fn [_]
                 (.working ied true)
                 (SwingUtilities/invokeLater
                  (proxy [Runnable][]
                    (run []
                      (doseq [[p v] (dice-fn)]
                        (.set-param! ied p v))
                      (.sync-ui! ied)
                      (.status! ied "Randomized matrix")
                      (.working ied false))))))
        
    (subedit/subeditor-wrapper [m1 m2] pan-main)))


        
