(ns cadejo.ui.util.sgwr-factory
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.util.color :as uc])
  )


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
                                                             length
                                                             handle-color handle-size handle-style]
                                                      :or {passive-track nil
                                                           active-track nil
                                                           length slider-length
                                                           handle-color nil
                                                           handle-size nil
                                                           handle-style nil}}]
  (let [s (slider/slider (.tool-root drw) p0 length v0 v1
                         :id id
                         :orientation :vertical
                         :drag-action drag-action
                         :track1-color (or passive-track (lnf/passive-track))
                         :track2-color (or active-track (lnf/active-track))
                         :rim-color (uc/color [0 0 0 0])
                         :handle-color (or handle-color (lnf/handle))
                         :handle-size (or handle-size (lnf/handle-size))
                         :handle-style (or handle-style (lnf/handle-style)))]
    (.put-property! s :editor ieditor)
    s))
    
;; (defn minor-tick [drw pc]
;;   (let [[xc yc] pc
;;         x0 (- xc minor-tick-length)
;;         x1 (+ xc minor-tick-length)]
;;     (line/line (.root drw) [x0 yc][x1 yc] :id :minor-tick
;;                :style :solid
;;                :color (lnf/minor-tick))))


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
    
        
                 
                                            
