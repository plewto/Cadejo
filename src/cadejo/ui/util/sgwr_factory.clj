(ns cadejo.ui.util.sgwr-factory
  (:require [cadejo.config :as config])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.color :as ssc]))

; ---------------------------------------------------------------------- 
;                            Borders and Regions


(defn sgwr-drawing [width height]
  (let [drw (sgwr.components.drawing/native-drawing width height)]
    (.background! drw (lnf/background))
    drw))

(defn minor-border [drw p0 p1 & {:keys [radius]
                                 :or {radius 12}}]
  (rect/rectangle (.root drw) p0 p1 :id :minor-border
                  :color (lnf/minor-border)
                  :radius radius))

(defn major-border [drw p0 p1 & {:keys [radius]
                                 :or {radius 12}}]
  (rect/rectangle (.root drw) p0 p1 :id :minor-border
                  :color (lnf/major-border)
                  :radius radius))


(defn occluder [drw p0 p1 & {:keys [color radius]
                             :or {color nil
                                  radius 12}}]
  (let [r (rect/rectangle (.occluder-root drw) p0 p1
                          :id :occluder)]
    (.color! r :disabled (lnf/occluder))
    (.color! r :enabled [0 0 0 0])
    (.fill! r :disabled true)
    (.fill! r :enabled :no)
    (.use-attributes! r :enabled)
    r))

; ---------------------------------------------------------------------- 
;                              Text Components

(defn text [drw p0 txt & {:keys [size style color]
                          :or {size 8.0
                               style :sans
                               color nil}}]
  (let [root (.root drw)]
    (text/text root p0 (str txt)
               :color (or color (lnf/text))
               :style style
               :size size)))

(defn title [drw p0 txt & {:keys [size style color]
                           :or {size 9.0
                                style :serif-bold
                                color nil}}]
  (let [root (.root drw)]
    (text/text root p0 (str txt)
               :color (or color (lnf/title))
               :style style
               :size size)))

(defn sub-title [drw p0 txt & {:keys [size style color offset]
                               :or  {size 8.0
                                     style :sans-bold
                                     color nil
                                     offset [0 0]}}]
  (let [x (+ (first p0)(first offset))
        y (+ (second p0)(second offset))]
    (text/text (.root drw) [x y] (str txt)
               :size size
               :style style
               :color (or color (lnf/title)))))

(defn label [drw p0 txt & {:keys [size style color offset]
                           :or {size 6.0
                                style :mono
                                color nil
                                offset [0 0]}}]
  (text/text (.root drw) [(+ (first p0)(first offset))
                          (+ (second p0)(second offset))]
                          (str txt)
             :color (or color (lnf/label))
             :style style
             :size size))


(defn vtext [drw p0 txt & {:keys [size style color delta]
                           :or {size 6.0
                                style :mono
                                color nil
                                delta 12}}]
  (let [root (.root drw)
        x (first p0)
        y* (atom (second p0))
        c (or color (lnf/text))]
    (doseq [s txt]
      (text/text root [x @y*] (str s) :size size :style style :color c)
      (swap! y* (fn [q](+ q delta))))))


; ---------------------------------------------------------------------- 
;                                  Sliders

(def slider-length 150)
(def minor-tick-length 4)
(def major-tick-length 8)
(def tick-label-offsets [-60 5])

(defn vslider [drw ieditor id p0 v0 v1 drag-action & {:keys [passive-track active-track
                                                             passive-width active-width
                                                             length
                                                             handle-color handle-size handle-style
                                                             value-hook
                                                             orientation]
                                                      :or {passive-track nil
                                                           passive-width 1
                                                           active-track nil
                                                           active-width 1
                                                           length slider-length
                                                           handle-color nil
                                                           handle-size nil
                                                           handle-style nil
                                                           value-hook identity
                                                           orientation :vertical}}]
  (let [s (slider/slider (.tool-root drw) p0 length v0 v1
                         :id id
                         :orientation orientation
                         :drag-action drag-action
                         :track1-color (or passive-track (lnf/passive-track))
                         :track1-width passive-width
                         :track2-color (or active-track (lnf/active-track))
                         :track2-width active-width
                         :rim-color (uc/color [0 0 0 0])
                         :handle-color (or handle-color (lnf/handle))
                         :handle-size (or handle-size (lnf/handle-size))
                         :handle-style (or handle-style (lnf/handle-style))
                         :value-hook value-hook)]
    (slider/set-slider-value! s v0)
    (.put-property! s :editor ieditor)
    s))

(defn minor-ticks [drw xc y0 y1 count]
  (let [root (.root drw)
        c (lnf/minor-tick)
        x1 (- xc minor-tick-length)
        x2 (+ xc minor-tick-length)
        diff (math/abs (- y0 y1))
        delta (/ (float diff) count)
        y* (atom (max y0 y1))]
    (while (>= @y* (min y0 y1))
      (line/line root [x1 @y*][x2 @y*] :id :minor-tick 
                 :style :solid
                 :size 1.0
                 :color c)
      (swap! y* (fn [q](- q delta))))))
    
(defn major-tick 
  ([drw xc y]
   (let [x1 (- xc major-tick-length)
         x2 (+ xc major-tick-length)]
     (line/line (.root drw) [x1 y][x2 y] 
                :id :major-tick
                :color (lnf/major-tick))))

  ([drw xc y txt offsets]
   (major-tick drw xc y)
   (let [[xoff yoff] offsets
         xt (+ xc major-tick-length (first offsets))
         yt (+ y (second offsets))]
     (label drw [xt yt] (str txt) :size 6.0 :color (lnf/major-tick))))

  ([drw xc y txt]
   (major-tick drw xc y txt tick-label-offsets)))

;; NOTE: y2 must be less then y1!!!! 
;;
(defn major-tick-marks [drw xc y1 y2 & {:keys [v0 v1 step frmt x-offset y-offset font-size color]
                                          :or {v0 -1.0
                                               v1 1.0
                                               step 0.5
                                               frmt "%+4.1f"
                                               x-offset -40
                                               y-offset 5
                                               font-size 6.0
                                               color nil}}]
  (let [root (.root drw)
        vmin (min v0 v1)
        vmax (max v0 v1)
        vrange (- vmax vmin)
        count (/ vrange step)
        ydiff (- y2 y1)
        ydelta (/ ydiff count)
        v* (atom v0)
        y* (atom y1)]
    (while (>= @y* y2)
      (major-tick drw xc @y*)
      (label drw [(+ xc x-offset)(+ @y* y-offset)]
             (format frmt @v*)
             :color (or color (lnf/major-tick))
             :size font-size)
      (swap! y* (fn [q](+ q ydelta)))
      (swap! v* (fn [q](+ q step))))))
   

(defn db-ticks [drw xc y0 & {:keys [min max length step
                                    tick-length 
                                    color
                                    label-offset
                                    label-size
                                    label-format]
                             :or {min -48
                                  max 0
                                  length slider-length
                                  step 6
                                  tick-length major-tick-length
                                  color nil
                                  label-offset [-30  6]
                                  label-size 6.0
                                  label-format "%+3d"}}]
  (let [x1 (- xc tick-length)
        x2 (+ xc tick-length)
        diff-val (math/abs (- max min))
        count (/ diff-val step)
        delta-y (/ length count)
        val* (atom min)
        y* (atom y0)
        c (or color (lnf/major-tick))
        root (.root drw)]
    (while (and (<= @val* max)(< min max))
      (line/line root [x1 @y*][x2 @y*] :color c)
      (label drw [x1 @y*] (format label-format @val*)
             :color c :size label-size :offset label-offset)
      (swap! val* (fn [q](+ q step)))
      (swap! y* (fn [q](- q delta-y))))))
        
        
    
        


(defn hrule [drw x0 x1 y & {:keys [color style width]
                                :or {color (lnf/major-tick)
                                     style :solid
                                     width 1}}]
  (let [root (.root drw)]
    (line/line root [x0 y][x1 y] :id :hrule 
               :style style :color color :width width)))
                                            
; ---------------------------------------------------------------------- 
;                               Display Bars

(defn- dbar-cell-height [] 
  (let [sty (lnf/dbar-style)]
    (get {:matrix 35 :sixteen 30 :basic 30} sty 35)))

(defn- dbar-cell-width []
  (let [sty (lnf/dbar-style)]
    (get {:matrix 25 :sixteen 20 :basic 20} sty 25)))
  

(defn displaybar [drw p0 count & {:keys [style]
                                  :or {style nil}}]
  (let [db (dbar/displaybar (.root drw)(first p0)(second p0) count (or style (lnf/dbar-style))
                            :cell-width (dbar-cell-width)
                            :cell-height (dbar-cell-height))]
    (.colors! db (lnf/dbar-inactive)(lnf/dbar-active))
    db))

; ---------------------------------------------------------------------- 
;                                  Buttons

(defn- icon-prefix []
  (let [cs (config/current-skin)]
    (cond (= cs "Twilight") :gray
          :default (lnf/icon-prefix))))

(defn mini-edit-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :edit
                           :id id
                           :click-action action))

(defn mini-chevron-up-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :up1
                           :id id
                           :click-action action))

(defn mini-chevron-up2-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :up2
                           :id id
                           :click-action action))

(defn mini-chevron-down-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :down1
                           :id id
                           :click-action action))

(defn mini-chevron-down2-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :down2
                           :id id
                           :click-action action))

(defn mini-delete-button [drw p0 id action]
  (button/mini-icon-button (.tool-root drw) p0 (icon-prefix) :delete
                           :id id
                           :click-action action
                           :rim-color (ssc/color 0 0 0 0)))

(defn init-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :reset
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))



(defn name-edit-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :edit :text
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn open-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :open
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn save-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :save
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn undo-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :undo
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn redo-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :redo
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn dice-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :dice
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn copy-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :copy
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn paste-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :paste
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn help-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :help
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))
                      
(defn zoom-in-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :view :in
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn zoom-out-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :view :out
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn zoom-restore-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :view :reset
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn env-button [drw p0 curve action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :env curve
                      :id curve
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn program-store-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :bankstore
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))


(defn blank-button [drw p0 p1 id action]
  (let [[x0 y0] p0
        [x1 y1] p1
        width (math/abs (- x1 x0))
        height (math/abs (- y1 y0))
        b (button/icon-button (.tool-root drw) [(min x0 x1)(max y0 y1)] :black :general :blank 
                              :id id 
                              :click-action action
                              :w width
                              :h height
                              :rim-color :red ;; FPO
                              :rim-width 1.0
                              :rim-radius 0)]
    (println b)
    b))

(defn checkbox [drw p0 id txt action] 
  (let [checkmark [(lnf/checkbox)(lnf/checkbox-style)(lnf/checkbox-size)]
        cb (msb/checkbox (.tool-root drw) p0 txt
                         :id id
                         :rim-radius (lnf/checkbox-rim-radius)
                         :rim-color (lnf/checkbox-rim)
                         :selected-check checkmark
                         :text-color (lnf/text)
                         :click-action action)]
    cb))


; ---------------------------------------------------------------------- 
;                                  Fields


(defn field [drw p0 p1 param-x range-x param-y range-y action & {:keys []
                                                 :or {}}]
  (let [f (field/field (.tool-root drw) p0 p1 range-x range-y 
                       :id :dispersion-field
                       :drag-action action
                       :rim-color (lnf/minor-border)
                       )
        b (field/ball f :ball1 [(first range-x)(first range-y)]
                      :color (lnf/handle)
                      :style [:dot :fill]
                      :size 3)]
    [f b]))

; ---------------------------------------------------------------------- 
;                              Test Positions


(defn fpo [drw p & {:keys [color style size print]
                    :or {color (lnf/selected-text)
                         style [:diag :diag2]
                         size 4
                         print nil}}]
  (if print (println (format "FPO %s" print)))
  (point/point (.root drw) p :id :FPO :color (uc/color color) :style style :size size))
