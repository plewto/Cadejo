(ns cadejo.instruments.masa.editor.scanner-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.ui.instruments.subedit])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.util.color :as uc]))

;; map rate positions
;; 0 <= pos <= 1
;; min-scanner-rate <= (fn pos) <= max-scanner-rate
;; Usees quadratic mapping.
;;
(def ^:private lin-rate (math/linear-function 0 min-scanner-rate 1 max-scanner-rate))
(def ^:private inv-lin-rate (math/linear-function min-scanner-rate 0 max-scanner-rate 1))

(defn- pos->rate [pos]
  (lin-rate (* pos pos)))

(defn- rate->pos [rate]
  (let [q (inv-lin-rate rate)
        pos (math/sqrt q)]
    pos))

;; (defn- label [drw p0 txt]
;;   (text/text (.root drw) p0 txt
;;              :color (lnf/text-color)
;;              :style :mono 
;;              :size 6))

(defn- vert-label [drw p0 txt]
  (let [root (.root drw)
        delta 12
        x (first p0)
        y* (atom (second p0))
        color (lnf/label)]
    (doseq [c txt]
      (text/text root [x @y*] c 
                 :color color
                 :style :mono
                 :size 6)
      (swap! y* (fn [q](+ q delta))))))
    
(defn- tick [drw pc color]
  (let [len 12
        [x yc] pc]
    (line/line (.root drw)[x (- yc len)][x (+ yc len)]
               :id :tick
               :color color)))

(defn scanner-panel [drw ied p0]
  (let [data (fn [param] ;; Returns current program value of parameter
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        [x0 y0] p0
        w 500 ; 492
        h 400 ; 392
        x1 (+ x0 w)
        x2 (+ x0 32)
        x3 (+ x2 200)
        x4 (+ x0 280)
        x5 (+ x4 200)
        y1 (- y0 h)
        y2 (- y0 130)
        y3 (- y2 200)
        y4 y2
        y5 y3
        p1 [x1 y1]
        p2 [x2 y2]
        p3 [x3 y3]
        p4 [x4 y4]
        p5 [x5 y5] 
        rate-action (fn [fld _]
                      (let [param-scan :scanner-scan-rate
                            param-mod :scanner-mod-rate
                            b (:b1 @(.get-property fld :balls*))
                            pos (.get-property b :value)
                            mod-rate (pos->rate (first pos))
                            scan-rate (pos->rate (second pos))]
                        (.set-param! ied param-scan scan-rate)
                        (.set-param! ied param-mod mod-rate)
                        (.status! ied (format "[%s] -> %5.3f [%s] -> %5.3f" param-scan scan-rate param-mod mod-rate))))
        fld-rate (field/field (.tool-root drw) p2 p3 [0 1][0 1] 
                              :drag-action rate-action
                              :rim-color (lnf/minor-border))
        ball-rate (field/ball fld-rate :b1 [0.5 0.5])
        delay-action (fn [fld _]
                      (let [param-delay :scanner-delay
                            param-mod   :scanner-delya-mod
                            b (:b1 @(.get-property fld :balls*))
                            pos (.get-property b :value)
                            delay-time (first pos)
                            delay-mod (second pos)]
                        (.set-param! ied param-delay delay-time)
                        (.set-param! ied param-mod delay-mod)
                        (.status! ied (format "[%s] -> %5.3f [%s] -> %5.3f" param-delay delay-time param-mod delay-mod))))
        fld-delay (field/field (.tool-root drw) p4 p5 ;[0 1][0 1]
                               [0 max-scanner-delay][0 max-scanner-delay-mod]
                               :drag-action delay-action
                               :rim-color (lnf/minor-border)) 
        ball-delay (field/ball fld-delay :b1 [0.5 0.5]
                               :color (lnf/handle)
                               :selected-color (lnf/handle))
        s-spread (slider/slider (.tool-root drw) [(+ x2 8) (+ y2 16)] 188 0.0 4.0 
                                :id :spread
                                :orientation :horizontal
                                :drag-action (fn [s _]
                                               (let [v (slider/get-slider-value s)]
                                                 (.set-param! ied :scanner-mod-spread v)))
                                :rim-color [0 0 0 0]
                                :handle-color (lnf/handle)
                                :track1-color (lnf/passive-track)
                                :track2-color (lnf/active-track))
        s-crossmix (slider/slider (.tool-root drw) [(+ x2 50)(+ y2 65)] 400 0.0 1.0
                                  :id :crossmix
                                  :orientation :horizontal
                                  :drag-action (fn [s _]
                                                 (let [v (slider/get-slider-value s)]
                                                   (.set-param! ied :scanner-crossmix v)))
                                  :rim-color [0 0 0 0]
                                  :handle-color (lnf/handle)
                                  :track1-color (lnf/passive-track)
                                  :track2-color (lnf/active-track))
        s-mix (slider/slider (.tool-root drw) [(+ x2 50)(+ y2 115)] 400 0.0 1.0
                             :id :scanner-mix
                             :orientation :horizontal
                             :drag-action (fn [s _]
                                            (let [v (slider/get-slider-value s)]
                                              (.set-param! ied :scanner-mix v)))
                             :rim-color [0 0 0 0]
                             :handle-color (lnf/handle)
                             :track1-color (lnf/passive-track)
                             :track2-color (lnf/active-track))
        widget-map {}]
    (image/read-image (.root drw) [(first p2)(second p3)] "resources/masa/scanner_rate_pattern.png")
    (rect/rectangle (.root drw) [x0 (+ y0 20)] [x1 y1] :id :scanner-border
                    :color (lnf/minor-border)
                    :radius 12)
    (sfactory/label drw [(+ x2 70)(- y3 8)] "Mod Rate")
    (vert-label drw [(- x2 16) (+ y3 60)] "Scan Rate")
    (sfactory/label drw [(+ x4 80)(- y3 8)] "Delay")
    (vert-label drw [(- x4 16) (+ y3 60)] "Delay Mod")
    (sfactory/label drw [(+ x2 65)(+ y2 40)] "Mod Spread")
    (sfactory/label drw [(+ x0 8) (+ y2 68)] "Cross Mix")
    (sfactory/label drw [(+ x0 8) (+ y2 118)] "Efx Mix")
    (sfactory/text drw [(+ x0 215)(+ y1 20)] "Scanner")

    ;; mix and crossmix tickmarks
    (let [y1 (+ y2 65)
          y2 (+ y2 115)
          xa (+ x2 50)
          xe (+ xa 400)
          xc (math/mean xa xe)
          xb (math/mean xa xc)
          xd (math/mean xc xe)
          cmap {xa, (lnf/major-tick)
                xc, (lnf/major-tick)
                xe, (lnf/major-tick)}]
      (doseq [x [xa xb xc xd xe]]
        (let [c (get cmap x (lnf/minor-tick))]
          (tick drw [x y2] c)
          (tick drw [x y1] c))))

    (.background! drw (lnf/background))
    
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
        (doseq [[p v](seq {:scanner-delay 0.01, :scanner-delay-mod 0.5,
                           :scanner-mod-rate 5.0, :scanner-mod-spread 0.0,
                           :scanner-scan-rate 0.1, :scanner-crossmix 0.1,
                           :scanner-mix 0.0})]
          (.set-param! ied p v)))
      
      (sync-ui! [this]
        (let [delay (data :scanner-delay)
              delay-mod (data :scanner-delay-mod)
              mod-rate (data :scanner-mod-rate)
              mod-spread (data :scanner-mod-spread)
              scan-rate (data :scanner-scan-rate)
              crossmix (data :scanner-crossmix)
              mix (data :scanner-mix)
              scan-pos (rate->pos scan-rate)
              mod-pos (rate->pos mod-rate)]
          (field/set-ball-value! fld-rate :b1 [mod-pos scan-pos] false)
          (field/set-ball-value! fld-delay :b1 [delay delay-mod] false)
          (slider/set-slider-value! s-spread mod-spread false)
          (slider/set-slider-value! s-crossmix crossmix false)
          (slider/set-slider-value! s-mix mix false))) )))
