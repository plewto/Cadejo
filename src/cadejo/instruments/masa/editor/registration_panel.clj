(ns cadejo.instruments.masa.editor.registration-panel
  (:use [cadejo.instruments.masa.masa-constants ])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.util.col :as ucol])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.util.color :as uc]))

(defn- drawbar [drw ied p0 param track-color action]
  (let [x-shift 32
        y-shift 100
        x (+ (first p0) x-shift)
        y (- (second p0) y-shift)
        s (sfactory/vslider drw ied param [x y] 0 8 action
                            :passive-track (lnf/passive-track)
                            :active-track track-color
                            :active-width 8
                            :handle-color (uc/inversion track-color)
                            :handle-style [:fill :box]
                            :handle-size 4
                            :value-hook int)]
    s))

;; Create 'pedal' multistate-button
;;
(defn- pedal-button [drw ied p0 param action]
  (let [c-zero (uc/color (lnf/text))
        [r0 g0 b0] (uc/rgb c-zero)
        c-pos [(int (* 0.75 r0)) (min 255 (int (* 1.5 g0))) b0] 
        c-neg [(min 255 (int (* 1.5 r0))) (int (* 0.75 g0)) b0] 
        w 32
        x-shift (* 0.5 w)
        y-shift 80
        x (+ (first p0) x-shift)
        y (- (second p0) y-shift)
        states [[:neg8 "-8" c-neg][:neg7 "-7" c-neg][:neg6 "-6" c-neg]
                [:neg5 "-5" c-neg][:neg4 "-4" c-neg][:neg3 "-3" c-neg]
                [:neg2 "-2" c-neg][:neg1 "-1" c-neg][:zero " 0" c-zero]
                [:pos1 "+1" c-pos][:pos2 "+2" c-pos][:pos3 "+3" c-pos] 
                [:pos4 "+4" c-pos][:pos5 "+5" c-pos][:pos6 "+6" c-pos] 
                [:pos7 "+7" c-pos][:pos8 "+8" c-pos]]
        b (msb/text-multistate-button (.tool-root drw) [x y] states 
                                      :id param
                                      :rim-color (lnf/button-border)
                                      :click-action action
                                      :gap 4
                                      :w w
                                      :h 24)]
    b))

;; Create envelope selection button
;;        
(defn- env-button [drw ied p0 param action]
  (let [x-shift 8
        y-shift 48
        x (+ (first p0) x-shift)
        y (- (second p0) y-shift)
        cs (config/current-skin)
        states [[:gate :env :gate][:perc :env :percussion]]
        b (msb/icon-multistate-button (.tool-root drw) [x y] states
                                      :id param
                                      :icon-prefix (cond (= cs "Twilight") :gray
                                                         :default (lnf/icon-prefix))
                                      :click-action action)]
    b))


;; Draw rule lines
;;
(defn- rulers [drw p0]
  (let [[x0 y0] p0
        rlength 425
        x1 (+ x0 20)
        x2 (+ x1 rlength)
        y* (atom (- y0 100))
        delta-y (/ slider-length 8.0)
        txt-x-offset -12
        txt-y-offset 4
        txt-x (+ x1 txt-x-offset)
        txt-size 6]
    (dotimes [i 9]
      (line/line (.root drw)[x1 @y*][x2 @y*]  
                 :color (lnf/major-tick)
                 :style :dotted)
      (text/text (.root drw)[txt-x (+ @y* txt-y-offset)](str i)
                 :color (lnf/major-tick)
                 :style :mono
                 :size txt-size)
      (swap! y* (fn [q](- q delta-y))))))


(defn registration-panel [drw ied p0]
  (let [data (fn [param] ;; Returns current program value of parameter
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        [x0 y0] p0
        pan-w 458
        pan-h 268
        p1 [(+ x0 pan-w)(- y0 pan-h)]
        drawbars (let [acc* (atom [])
                       params [:a1 :a2 :a3 :a4 :a5 :a6 :a7 :a8 :a9]
                       x* (atom (+ x0 0))
                       index* (atom 0)
                       y (+ y0 0)
                       action (fn [s _] 
                                (let [id (.get-property s :id)
                                      val (slider/get-slider-value s)]
                                  (.set-param! ied id val)))]
                   (doseq [p params]
                     (let [c (nth drawbar-color-seq @index*)
                           s (drawbar drw ied [@x* y] p c action)]
                       (swap! index* inc)
                       (swap! acc* (fn [q](conj q s)))
                       (swap! x* (fn [q](+ q slider-spacing)))))
                   @acc*)
        pedal-buttons (let [acc* (atom [])
                            params [:p1 :p2 :p3 :p4 :p5 :p6 :p7 :p8 :p9]
                            x* (atom (+ x0 0))
                            y (+ y0 0)
                            action (fn [b _] 
                                     (let [id (.get-property b :id)
                                           state (first (msb/current-multistate-button-state b))
                                           val (/ (- state 8) 8.0)]
                                       (.set-param! ied id val)))]
                        (doseq [p params]
                          (let [b (pedal-button drw ied [@x* y] p action)]
                            (swap! acc* (fn [q](conj q b)))
                            (swap! x* (fn [q](+ q slider-spacing)))))
                        @acc*)
        env-buttons (let [acc* (atom [])
                          params [:perc1 :perc2 :perc3
                                  :perc4 :perc5 :perc6
                                  :perc7 :perc8 :perc9]
                          x* (atom (+ x0 0))
                          y (+ y0 0)
                          action (fn [b _]
                                   (let [id (.get-property b :id)
                                         state (first (msb/current-multistate-button-state b))]
                                     (.set-param! ied id state)))]
                      (doseq [p params]
                        (let [b (env-button drw ied [@x* y] p action)]
                          (swap! acc* (fn [q](conj q b)))
                          (swap! x* (fn [q](+ q slider-spacing)))))
                      @acc*)
        widget-map {}]
    (rulers drw p0)
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      
      (widget [this key]
        (get widget-map key))

      (parent [this] ied)

      (parent! [this _] ied) 

      (status! [this msg]
        (.status! ied msg))

      (warning! [this msg]
        (.warning! ied msg))

      (set-param! [this param value]
        (.status! this (format "[%s] --> %s" param val))
        (.set-param! ied param val))

      (init! [this]
        (let [params [:a1 :a2 :a3 :a4 :a5 :a6 :a7 :a8 :a9
                      :p1 :p2 :p3 :p4 :p5 :p6 :p7 :p8 :p9
                      :perc1 :perc2 :perc3
                      :perc4 :perc5 :perc6
                      :perc7 :perc8 :perc9]
              data {:a3 8 :a4 8}]
          (doseq [p params]
            (let [val (get data p 0)]
              (.set-param! ied p val)))))

      (sync-ui! [this]
        (let [amp-params [:a1 :a2 :a3 :a4 :a5 :a6 :a7 :a8 :a9]
              ped-params [:p1 :p2 :p3 :p4 :p5 :p6 :p7 :p8 :p9]
              env-params [:perc1 :perc2 :perc3
                          :perc4 :perc5 :perc6
                          :perc7 :perc8 :perc9]]
          (doseq [[s p](ucol/zip drawbars amp-params)]
            (let [v (data p)]
              (slider/set-slider-value! s v false)))
          (doseq [[b p](ucol/zip pedal-buttons ped-params)]
            (let [v (int (* 8 (data p)))
                  i (+ 8 v)]
              (msb/set-multistate-button-state! b i false)))
          (doseq [[b p](ucol/zip env-buttons env-params)]
            (let [v (int (data p))]
              (msb/set-multistate-button-state! b v false))) ))))) 

