(ns cadejo.instruments.alias.editor.filter-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.matrix-editor :as matrix])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider :as slider])
  (:require [seesaw.core :as ss]))

(def ^:private width 2200)
(def ^:private height 550)
(def ^:private x-matrix-overview 1780)
(def ^:private y-matrix-overview 400)
(def ^:private max-filter-gain constants/max-filter-gain)
(def ^:private max-filter-mod constants/max-filter-mod)
(def ^:private min-db constants/min-amp-db)
(def ^:private max-db constants/max-amp-db)
(def ^:private filter1-xoffset 32)
(def ^:private filter2-xoffset 900)

(declare draw-background)
(declare distortion-panel)
(declare fm-panel)
(declare res-panel)
(declare mix-panel)
(declare freq-panel)
(declare draw-distortion-panel)
(declare draw-fm-panel)
(declare draw-res-panel)
(declare draw-mix-panel)
(declare draw-freq-panel)

(defn filter-editor [ied]
  (let [p0 [0 height]
        drw (let [d (sfactory/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        dist1 (distortion-panel 1 drw ied p0)
        dist2 (distortion-panel 2 drw ied p0)
        fm1 (fm-panel 1 drw ied p0)
        fm2 (fm-panel 2 drw ied p0)
        res1 (res-panel 1 drw ied p0)
        res2 (res-panel 2 drw ied p0)
        mix1 (mix-panel 1 drw ied p0)
        mix2 (mix-panel 2 drw ied p0)
        freq1 (freq-panel 1 drw ied p0)
        freq2 (freq-panel 2 drw ied p0)
        matrix-overview (matrix/matrix-overview drw [(+ (first p0) x-matrix-overview)(- (second p0) y-matrix-overview -20)])
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
                 (doseq [se [dist1 dist2 fm1 fm2 res1 res2 mix1 mix2 freq1 freq2 matrix-overview]]
                   (se dmap))
                 (.render drw))))]
    ed))





(defn- vertical-position [item p0]
  (let [y0 (- (second p0) 15)
        border (- y0 300)
        y-main-title (- y0 350) 
        y-sub-title (- y0 260)
        y-slider1 (- y0 90)
        y-slider2 (- y-slider1 sfactory/slider-length)
        y-logo (- y0 520)
        source-button-position (+ y-slider1 35)
        map {:slider1 y-slider1 
             :slider2 y-slider2
             :sub-title y-sub-title 
             :main-title y-main-title
             :source source-button-position
             :logo y-logo
             :y0 y0
             :border border}]
  (get map item y-slider1)))

(defn- draw-background [ddrw p0]
  (let [bg (sfactory/sgwr-drawing width height)
        [x0 y0] p0
        x-logo (+ x0 60)
        y-logo (vertical-position :logo p0)]
    
    (doseq [n [1 2]]
      (draw-distortion-panel n bg p0)
      (draw-fm-panel n bg p0)
      (draw-res-panel n bg p0)
      (draw-mix-panel n bg p0)
      (draw-freq-panel n bg p0))
    (sfactory/label bg [(+ x0 x-matrix-overview)(- y0 y-matrix-overview)] "Bus Assignments:")
    (image/read-image (.root bg) [x-logo y-logo] 
                      (format "resources/alias/filter_logo.png"))
    (.render bg)
    (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-image)]
      (.put-property! iobj :image (.image bg))
      iobj)))
    
(defn- slider-value! [s val]
  (slider/set-slider-value! s val false))

(defn- slider-value [s]
  (slider/get-slider-value s))

(defn- msb-state! [b n]
  (msb/set-multistate-button-state! b n false))



; ---------------------------------------------------------------------- 
;                                Distortion

(defn- distortion-pos [n item p0]
  (let [x-offset (if (= n 1) filter1-xoffset filter2-xoffset)
        x0 (+ (first p0) x-offset)
        x-title (+ x0 10)
        y0 (vertical-position :y0 p0)
        x-gain (+ x0 40)
        x-clip (+ x-gain 65)
        x-depth (+ x-clip 65)
        x-mix (+ x-depth 65)
        x-source (- x-depth 20)
        x-border (+ x-mix 25)
        x-map {:p0 x0 :title x-title
               :gain x-gain :clip x-clip :depth x-depth :mix x-mix
               :source x-source :border x-border}

        y-slider (vertical-position :slider1 p0)
        y-map {:p0 y0 :title (vertical-position :sub-title p0)
               :gain y-slider :clip y-slider :depth y-slider :mix y-slider
               :source (vertical-position :source p0) 
               :border (vertical-position :border p0)}
        x (item x-map)
        y (item y-map)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate filter distortion item = %s" item)))
    [x y]))

(defn- draw-distortion-panel [n bg p0]
  (let [x-gain (first (distortion-pos n :gain p0))
        x-clip (first (distortion-pos n :clip p0))
        x-mix (first (distortion-pos n :mix p0))
        x-depth (first (distortion-pos n :depth p0))
        y-slider1 (vertical-position :slider1 p0)
        y-slider2 (vertical-position :slider2 p0)]
    (sfactory/label bg (distortion-pos n :gain p0) "Gain" :offset [-14 24])
    (sfactory/label bg (distortion-pos n :clip p0) "Clip" :offset [-14 24])
    (sfactory/label bg (distortion-pos n :depth p0) "Dpth" :offset [-12 24])
    (sfactory/label bg (distortion-pos n :mix p0) "Mix" :offset [-10 24])
    (sfactory/label bg (distortion-pos n :source p0) "Bus --" :offset [-45 22])
    ;; tick marks
    (sfactory/minor-ticks bg x-gain y-slider1 y-slider2 (dec max-filter-gain))
    (sfactory/major-tick bg x-gain y-slider1 "1" [-30 5])
    (sfactory/major-tick bg x-gain y-slider2 "8" [-30 5])
    (doseq [x [x-clip x-mix]]
      (sfactory/minor-ticks bg x y-slider1 y-slider2 16)
      (sfactory/major-tick-marks bg x y-slider1 y-slider2 :v0 0.0 :v1 1.0 :step 0.25 :frmt "%4.2f"))
    (sfactory/major-tick-marks bg x-depth y-slider1 y-slider2)
    (sfactory/minor-ticks bg x-depth y-slider1 y-slider2 16)
    (sfactory/sub-title bg (distortion-pos n :title p0) "Distortion")
    (sfactory/label bg (distortion-pos n :title p0)
                    (if (= n 1) "f(x) = x-clip(x)" "f(x) = fold2(x)")
                    :size 5.5 :offset [110 0])
    (sfactory/minor-border bg (distortion-pos n :p0 p0)(distortion-pos n :border p0))))

(defn- distortion-panel [n drw ied p0]
  (let [param-gain (keyword (format "distortion%d-pregain" n))
        param-clip (keyword (format "distortion%d-param" n))
        param-src (keyword (format "distortion%d-param-source" n))
        param-depth (keyword (format "distortion%d-param-depth" n))
        param-mix (keyword (format "distortion%d-param-mix" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        
        s-gain (sfactory/vslider drw ied param-gain (distortion-pos n :gain p0) 
                                 1.0 max-filter-gain action
                                 :value-hook int)
        s-clip (sfactory/vslider drw ied param-clip (distortion-pos n :clip p0) 0.0 1.0 action)
        s-depth (sfactory/vslider drw ied param-depth (distortion-pos n :depth p0) -1.0 1.0 action)
        s-mix (sfactory/vslider drw ied param-depth (distortion-pos n :mix p0) 0.0 1.0 action)
        b-src (matrix/source-button drw ied param-src (distortion-pos n :source p0))]
    (fn [dmap]
      (slider-value! s-gain (param-gain dmap))
      (slider-value! s-clip (param-clip dmap))
      (slider-value! s-depth (param-depth dmap))
      (slider-value! s-mix (param-depth dmap))
      (msb-state! b-src (int (param-depth dmap))))))

; ---------------------------------------------------------------------- 
;                                    FM

(defn- fm-pos [n item p0]
  (let [x-offset (+ (if (= n 1) filter1-xoffset filter2-xoffset) 260)
        x0 (+ (first p0) x-offset)
        x-title (+ x0 10)
        x-depth1 (+ x0 40)
        x-depth2 (+ x-depth1 60)
        x-source1 (- x-depth1 20)
        x-source2 (- x-depth2 20)
        x-border (+ x-depth2 60)
        y0 (vertical-position :y0 p0)
        y-slider (vertical-position :slider1 p0)
        y-source (vertical-position :source p0) 
        x-map {:p0 x0 :title x-title :border x-border
               :depth1 x-depth1 :source1 x-source1
               :depth2 x-depth2 :source2 x-source2}
        y-map {:p0 y0 :title (vertical-position :sub-title p0)
               :border (vertical-position :border p0)
               :depth1 y-slider :depth2 y-slider
               :source1 y-source :source2 y-source}
        x (get x-map item)
        y (get y-map item)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate filter fm item = %s" item)))
    [x y]))

(defn- draw-fm-panel [n bg p0]
  (let [root (.root bg)
        cmajor (lnf/major-tick)

        x-d1 (first (fm-pos n :depth1 p0))
        x-d2 (first (fm-pos n :depth2 p0))
        y-slider1 (vertical-position :slider1 p0)
        y-slider2 (vertical-position :slider2 p0)]
    (sfactory/label bg (fm-pos n :depth1 p0) "Dpth" :offset [-12 24])
    (sfactory/label bg (fm-pos n :depth2 p0) "Dpth" :offset [-12 24])
    (let [xc (math/mean x-d1 x-d2)
          yc (math/mean y-slider1 y-slider2)
          freq* (atom max-filter-mod)
          y-offset* (atom (* 1/2 sfactory/slider-length))]
      (while (>= @freq* 2000)
        (line/line root [(- x-d1 12)(+ yc @y-offset*)][(+ x-d1 12)(+ yc @y-offset*)] :color cmajor)
        (line/line root [(- x-d2 12)(+ yc @y-offset*)][(+ x-d2 12)(+ yc @y-offset*)] :color cmajor)
        (line/line root [(- x-d1 12)(- yc @y-offset*)][(+ x-d1 12)(- yc @y-offset*)] :color cmajor)
        (line/line root [(- x-d2 12)(- yc @y-offset*)][(+ x-d2 12)(- yc @y-offset*)] :color cmajor)
        (sfactory/label bg [xc (- yc @y-offset*)] (format "%+3dk" (int (/ @freq* 1000))) :size 5 :offset [-12 +4])
        (sfactory/label bg [xc (+ yc @y-offset*)] (format "%+3dk" (int (/ @freq* -1000))) :size 5 :offset [-12 +4])
        (swap! freq* (fn [q](* 1/2 q)))
        (swap! y-offset* (fn [q](* 1/2 q))))
      (line/line root [(- x-d1 12) yc][(+ x-d1 12) yc] :color cmajor)
      (line/line root [(- x-d2 12) yc][(+ x-d2 12) yc] :color cmajor)
      (sfactory/label bg [xc yc]  "  0" :size 5 :color cmajor :offset [-12 +4])
      (sfactory/minor-ticks bg x-d1 y-slider1 y-slider2 16)
      (sfactory/minor-ticks bg x-d2 y-slider1 y-slider2 16)
      (sfactory/sub-title bg (fm-pos n :title p0) "FM")
      (sfactory/minor-border bg (fm-pos n :p0 p0)(fm-pos n :border p0)))))

(defn- fm-panel [n drw ied p0]
  (let [param-d1 (keyword (format "filter%d-freq1-depth" n))
        param-d2 (keyword (format "filter%d-freq2-depth" n))
        param-s1 (keyword (format "filter%d-freq1-source" n))
        param-s2 (keyword (format "filter%d-freq2-source" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       value (slider-value s)]
                   (.set-param! ied param value)))
        s-d1 (sfactory/vslider drw ied param-d1 (fm-pos n :depth1 p0) 
                               (- max-filter-mod) max-filter-mod action :value-hook int)
        s-d2 (sfactory/vslider drw ied param-d1 (fm-pos n :depth2 p0) 
                               (- max-filter-mod) max-filter-mod action :value-hook int)
        b-s1 (matrix/source-button drw ied param-s1 (fm-pos n :source1 p0))
        b-s2 (matrix/source-button drw ied param-s2 (fm-pos n :source2 p0))
        sync-fn (fn [dmap]
                  (slider-value! s-d1 (param-d1 dmap))
                  (slider-value! s-d2 (param-d2 dmap))
                  (msb-state! b-s1 (int (param-s1 dmap)))
                  (msb-state! b-s2 (int (param-s2 dmap))))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                 Resonance

(defn- res-pos [n item p0]
  (let [x-offset (+ (if (= n 1) filter1-xoffset filter2-xoffset) 420)
        x0 (+ (first p0) x-offset)
        x-res (+ x0 60)
        x-depth (+ x-res 60)
        x-source (- x-depth 20)
        x-title (+ x0 10)
        x-border (+ x-depth 40)
        y0 (vertical-position :y0 p0)
        y-slider (vertical-position :slider1 p0)
        y-source (vertical-position :source p0) 
        x-map {:p0 x0 :title x-title :border x-border
               :res x-res :depth x-depth :source x-source}
        y-map {:p0 y0 :title (vertical-position :sub-title p0)
               :border (vertical-position :border p0)
               :res y-slider :depth y-slider :source y-source}
        x (get x-map item)
        y (get y-map item)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate filter res item = %s" item)))
    [x y]))

(defn- draw-res-panel [n bg p0]
  (let [x-res (first (res-pos n :res p0))
        x-depth (first (res-pos n :depth p0))
        y-slider1 (vertical-position :slider1 p0)
        y-slider2 (vertical-position :slider2 p0)
        y-source (vertical-position :source p0) ]
    (sfactory/label bg (res-pos n :res p0) "Res" :offset [-10 24])
    (sfactory/label bg (res-pos n :depth p0) "Dpth" :offset [-12 24])
    (sfactory/minor-ticks bg x-res y-slider1 y-slider2 16)
    (sfactory/minor-ticks bg x-depth y-slider1 y-slider2 16)
    (sfactory/major-tick-marks bg x-res y-slider1 y-slider2 :v0 0.0 :v1 1.0 :step 0.25 :frmt "%4.2f" )
    (sfactory/major-tick-marks bg x-depth y-slider1 y-slider2)
    (sfactory/sub-title bg (res-pos n :title p0) "Resonance")
    (sfactory/minor-border bg (res-pos n :p0 p0)(res-pos n :border p0))))

(defn- res-panel [n drw ied p0]
  (let [param-res (keyword (format "filter%d-res" n))
        param-depth (keyword (format "filter%d-res-depth" n))
        param-source (keyword (format "filter%d-res-source" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       value (slider-value s)]
                   (.set-param! ied param value)))
        s-res (sfactory/vslider drw ied param-res (res-pos n :res p0) 0.0 1.0 action)
        s-depth (sfactory/vslider drw ied param-depth (res-pos n :depth p0) -1.0 1.0 action)
        b-src (matrix/source-button drw ied param-source (res-pos n :source p0))]
    (fn [dmap]
      (slider-value! s-res (param-res dmap))
      (slider-value! s-depth (param-depth dmap))
      (msb-state! b-src (int (param-source dmap))))))

; ---------------------------------------------------------------------- 
;                                   Mixer


(defn- mix-pos [n item p0]
  (let [x-offset (+ (if (= n 1) filter1-xoffset filter2-xoffset) 580)
        x0 (+ (first p0) x-offset)
        x-title (+ x0 10)
        x-pan (+ x0 60)
        x-depth (+ x-pan 80)
        x-source (- x-depth 20)
        x-mix (+ x-depth 80)
        x-border (+ x-mix 50)
        y0 (vertical-position :y0 p0)
        y-slider (vertical-position :slider1 p0)
        y-source (vertical-position :source p0) 
        x-map {:p0 x0 :title x-title :border x-border
               :pan x-pan :depth x-depth :source x-source :mix x-mix}
        y-map {:p0 y0 :title (vertical-position :sub-title p0)
               :border (vertical-position :border p0)
               :source y-source
               :pan y-slider :depth y-slider :mix y-slider}
        x (get x-map item)
        y (get y-map item)]
    (if (or (not x)(not y))
      (println (format "ERROR nil coordinate filter mix item = %s" item)))
    [x y]))  

(defn- draw-mix-panel [n bg p0]
  (let [[x-pan y1](mix-pos n :pan p0)
        x-depth (first (mix-pos n :depth p0))
        x-mix (first (mix-pos n :mix p0))
        y2 (- y1 sfactory/slider-length)]
    (doseq [x [x-pan x-depth]]
      (sfactory/major-tick-marks bg x y1 y2)
      (sfactory/minor-ticks bg x y1 y2 16))
    (sfactory/db-ticks bg x-mix y1)
    (sfactory/label bg (mix-pos n :pan p0) "Pan" :offset [-10 24])
    (sfactory/label bg (mix-pos n :depth p0) "Dpth" :offset [-12 24])
    (sfactory/label bg (mix-pos n :mix p0) "Mix" :offset [-10 24])
    (sfactory/sub-title bg (mix-pos n :title p0) "Filter Out")
    (sfactory/minor-border bg (mix-pos n :p0 p0)(mix-pos n :border p0))))

(defn- mix-panel [n drw ied p0]
  (let [param-pan (keyword (format "filter%d-pan" n))
        param-depth (keyword (format "filter%d-pan-depth" n))
        param-source (keyword (format "filter%d-pan-source" n))
        param-mix (keyword (format "filter%d-postgain" n))
        action (fn [s _]
                 (let [param (.get-property s :id)
                       val (slider-value s)]
                   (.set-param! ied param val)))
        mix-action (fn [s _]
                     (let [db (slider-value s)
                           amp (math/db->amp db)]
                       (.set-param! ied param-mix amp)
                       (.status! ied (format "[%s] -> %s db" param-mix db))))
        s-pan (sfactory/vslider drw ied param-pan (mix-pos n :pan p0) -1.0 1.0 action)
        s-depth (sfactory/vslider drw ied param-depth (mix-pos n :depth p0) -1.0 1.0 action)
        s-mix (sfactory/vslider drw ied param-mix (mix-pos n :mix p0) min-db max-db mix-action
                                :value-hook int)
        b-source (matrix/source-button drw ied param-source (mix-pos n :source p0))
        sync-fn (fn [dmap] 
                  (slider-value! s-pan (param-pan dmap))
                  (slider-value! s-depth (param-depth dmap))
                  (msb-state! b-source (int (param-source dmap)))
                  (let [amp (param-mix dmap)
                        db (int (math/amp->db amp))]
                    (slider-value! s-mix db)))]
    sync-fn))

; ---------------------------------------------------------------------- 
;                                 Frequency

(defn- freq-pos [n item p0]
  (let [x0 (+ (if (= n 1) filter1-xoffset filter2-xoffset) 0)
        x-title (+ x0 10)
        x-display (+ x0 110)
        x-edit (+ x-display 150)
        x-mode (+ x-edit 60)
        x-border (+ x0 850)
        x-map {:p0 x0 :title x-title :display x-display
               :edit x-edit :mode x-mode :border x-border}
        y-offset 210        
        y0 (- (vertical-position :p0 p0) y-offset)
        y-border (- y0 80)
        y-title (vertical-position :main-title p0)
        y-title (- y0 30)
        y-display (- y0 50)
        y-edit (- y0 46)
        y-mode (- y0 52)
        y-map {:p0 y0 :title y-title :display y-display
               :edit y-edit :mode y-mode :border y-border}
        x (get x-map item)
        y (get y-map item)]
     (if (or (not x)(not y))
      (println (format "ERROR nil coordinate filter frequency item = %s" item)))
    [x y]))  
    
    

(defn- draw-freq-panel [n bg p0]
  (sfactory/title bg (freq-pos n :title p0) (format "Filter %d" n))
  (sfactory/minor-border bg (freq-pos n :p0 p0)(freq-pos n :border p0))
  (if (= n 1)
    (let [[x1 yc](freq-pos 1 :mode p0)
          len (* 2 sfactory/slider-length)
          x2 (+ x1 len)
          x-lp (+ x1 (* 0.0 len))
          x-lphp (+ x1 (* 0.25 len))
          x-hp (+ x1 (* 0.50 len))
          x-bp (+ x1 (* 0.75 len))
          x-bypass x2
          y1 (+ yc 12)
          y2 (- yc 12)
          root (.root bg)
          major-ticks [[x-lp "Low" -8]
                       [x-lphp "Low*High" -24]
                       [x-hp "High" -12]
                       [x-bp "Band" -12]
                       [x-bypass "Off" -10]]]
      (doseq [[x txt offset] major-ticks]
        (line/line root [x y1][x y2] :color (lnf/major-tick))
        (sfactory/label bg [(+ x offset)(+ y1 15)] txt)))))

(defn- freq-panel [n drw ied p0]
  (let [param-freq (keyword (format "filter%d-freq" n))
        param-mode (keyword (format "filter%d-mode" n))
        dbar (sfactory/displaybar drw (freq-pos n :display p0) 5)
        edit-action (fn [b _]
                      (dbar/displaybar-dialog dbar 
                                              (format "Filter %d Frequency" n)
                                              :validator (fn [q]
                                                           (let [f (math/str->float q)]
                                                             (and f (>= f 0)(< f 20001))))
                                              :callback (fn [_]
                                                          (let [s (.current-display dbar)
                                                                f (math/str->float s)]
                                                            (.set-param! ied param-freq f)))))
        b-edit (sfactory/mini-edit-button drw (freq-pos n :edit p0) n edit-action)
        mode-action (fn [s _]
                      (let [val (slider-value s)]
                        (.set-param! ied param-mode val)))
        s-mode (if (= n 1)
                 (slider/slider (.tool-root drw) (freq-pos 1 :mode p0) (* 2 sfactory/slider-length) 0.0 1.0
                                :orientation :horizontal
                                :rim-color [0 0 0 0]
                                :track1-color (lnf/passive-track)
                                :track2-color (lnf/passive-track)
                                :handle-color (lnf/handle)
                                :handle-style [:chevron-s :bar]
                                :drag-action mode-action)
                 nil)
        syncfn (fn [dmap] 
                 (.display! dbar (format "%5d" (int (param-freq dmap))) false)
                 (if s-mode
                   (slider-value! s-mode (param-mode dmap))))]
    syncfn))
