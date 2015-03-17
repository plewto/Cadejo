(ns cadejo.ui.util.sgwr-factory
  (:require [cadejo.config :as config])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
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
  )


; ---------------------------------------------------------------------- 
;                            Borders and Regions

(defn minor-border [drw p0 p1 & {:keys [radius]
                                 :or {radius 12}}]
  (rect/rectangle (.root drw) p0 p1 :id :minor-border
                  :color (lnf/minor-border)
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
               :color (or color (lnf/label))
               :style style
               :size size)))

(defn label [drw p0 txt & {:keys [size style color]
                           :or {size 6.0
                                style :mono
                                color nil}}]
  (text/text (.root drw) p0 (str txt)
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

(defn vslider [drw ieditor id p0 v0 v1 drag-action & {:keys [passive-track active-track
                                                             passive-width active-width
                                                             length
                                                             handle-color handle-size handle-style
                                                             value-hook]
                                                      :or {passive-track nil
                                                           passive-width 1
                                                           active-track nil
                                                           active-width 1
                                                           length slider-length
                                                           handle-color nil
                                                           handle-size nil
                                                           handle-style nil
                                                           value-hook identity}}]
  (let [s (slider/slider (.tool-root drw) p0 length v0 v1
                         :id id
                         :orientation :vertical
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
    
        
                 
                                            
; ---------------------------------------------------------------------- 
;                               Display Bars

(defn- dbar-cell-height [] 
  (let [sty (lnf/dbar-style)]
    (get {:matrix 35 :sixteen 30 :basic 30} sty 35)))

(defn- dbar-cell-width []
  (let [sty (lnf/dbar-style)]
    (get {:matrix 25 :sixteen 20 :basic 20} sty 25)))
  

(defn displaybar [drw p0 count]
  (let [db (dbar/displaybar (.root drw)(first p0)(second p0) count (lnf/dbar-style)
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

(defn init-button [drw p0 id action]
  (button/icon-button (.tool-root drw) p0 (icon-prefix) :general :reset
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
;                              Test Positions


(defn fpo [drw p & {:keys [color style size print]
                    :or {color (lnf/selected-text)
                         style [:diag :diag2]
                         size 4
                         print nil}}]
  (if print (println (format "FPO %s" print)))
  (point/point (.root drw) p :id :FPO :color (uc/color color) :style style :size size))
