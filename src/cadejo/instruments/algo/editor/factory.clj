(ns cadejo.instruments.algo.editor.factory
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.tools.slider :as slider])
  (:require [sgwr.util.color :as uc]))

(defn- icon-prefix []
  (let [cs (config/current-skin)]
    (cond (= cs "Twilight") :gray
          :default (lnf/icon-prefix))))

(defn slider 
  ([grp p0 id v0 v1 action is-signed?]
   (slider/slider grp p0 slider-length v0 v1
                  :id id 
                  :drag-action action
                  :rim-color [0 0 0 0]
                  :track1-color (lnf/passive-track-color)
                  :track2-color (lnf/active-track-color)
                  ;; :track3-color (if is-signed? 
                  ;;                 (lnf/alternate-track-color)
                  ;;                 (lnf/passive-track-color))
                  :handle-color (lnf/slider-handle-color)))
  ([grp p0 id v0 v1 action]
   (slider grp p0 id v0 v1 action false)))

(defn hp-slider [grp p0 id action]
  (slider/slider grp p0 slider-length min-hp-freq max-hp-freq
                 :id id
                 :drag-action action
                 :rim-color [0 0 0 0]
                 :track1-color (lnf/passive-track-color)
                 :track2-color (lnf/active-track-color)
                 :handle-color (lnf/slider-handle-color)
                 :value-hook (fn [n]
                               (int (if (<= n 10)
                                      n
                                      (* 10 (int (/ n 10)))))) ))

(defn lp-slider [grp p0 id action]
  (slider/slider grp p0 slider-length min-lp-freq max-lp-freq
                 :id id
                 :drag-action action
                 :rim-color [0 0 0 0]
                 :track1-color (lnf/passive-track-color)
                 :track2-color (lnf/active-track-color)
                 :handle-color (lnf/slider-handle-color)
                 :value-hook (fn [n]
                               (let [f (* 1000 (int (/ n 1000)))]
                                 f))))

(defn volume-slider [grp p0 id action]
  (slider/slider grp p0 slider-length min-amp-db max-amp-db
                 :id id
                 :drag-action action
                 :rim-color [0 0 0 0]
                 :track1-color (lnf/passive-track-color)
                 :track2-color (lnf/active-track-color)
                 :handle-color (lnf/slider-handle-color)
                 :value-hook (fn [n]
                               (let [db (* 3 (int (/ n 3)))]
                                 db))))

;; (defn db-slider [grp p0 id action]
;;   (slider/slider grp p0 slider-length min-amp-db max-amp-db
;;                  :id id
;;                  :drag-action action
;;                  :rim-color [0 0 0 0]
;;                  :track1-color (lnf/passive-track-color)
;;                  :track2-color (lnf/active-track-color)
;;                  :handle-color (lnf/slider-handle-color)
;;                  :value-hook (fn [n](int n))))


(defn slider-label [grp pc id txt]
  (let [[xc yc] pc
        x (- xc 12)
        y (+ yc 20)]
    (text/text grp [x y] txt :id id
               :style :sans
               :size 6
               :color (lnf/text-color))))

(defn section-label [grp p0 id txt]
  (text/text grp p0 txt :id id 
             :style :sans
             :size 8
             :color (lnf/text-color)))

(defn major-label [grp p0 txt]
  (text/text grp p0 txt
             :style :sans-bold
             :size 18
             :color (lnf/text-color)))

(defn op-label [grp p0 n]
  (major-label grp p0 (format "OP %d" n)))


(defn inner-border [grp p0 p1]
  (rect/rectangle grp p0 p1 
                  :id :inner-border
                  :style :solid
                  :color (lnf/minor-border-color)
                  :radius 12))
  
(defn outer-border [grp p0 p1]
  (rect/rectangle grp p0 p1
                  :id :outer-border
                  :style :solid
                  :color (lnf/major-border-color)
                  :radius 12))
  


(defn mini-edit-button [grp p0 id action]
  (button/mini-icon-button grp p0 (icon-prefix) :edit
                           :id id
                           :click-action action))

;; (defn mute-button [grp p0 id action]
;;   (button/icon-button grp p0 (icon-prefix) :general :delete
;;                       :id id
;;                       :click-action action
;;                       :rim-color [0 0 0 0]))


(defn init-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :general :reset
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn dice-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :general :dice
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))


(defn copy-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :general :copy
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn paste-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :general :paste
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))


(defn help-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :general :help
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))
                      
(defn zoom-in-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :view :in
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn zoom-out-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :view :out
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn zoom-restore-button [grp p0 id action]
  (button/icon-button grp p0 (icon-prefix) :view :reset
                      :id id
                      :click-action action
                      :rim-color [0 0 0 0]))

(defn env-button [grp p0 curve action]
  (button/icon-button grp p0 (icon-prefix) :env curve
                      :id curve
                      :click-action action
                      :rim-color [0 0 0 0]))
                                              


(defn blank-button [grp p0 p1 id action]
  (let [[x0 y0] p0
        [x1 y1] p1
        width (math/abs (- x1 x0))
        height (math/abs (- y1 y0))
        b (button/icon-button grp [(min x0 x1)(max y0 y1)] :black :general :blank 
                              :id id 
                              :click-action action
                              :w width
                              :h height
                              :rim-color :red ;; FPO
                              :rim-width 1.0
                              :rim-radius 0)]
    (println b)
    b))


(defn- dbar-cell-height [] 
  (let [sty (lnf/dbar-style)]
    (get {:matrix 35 :sixteen 30 :basic 30} sty 35)))

(defn- dbar-cell-width []
  (let [sty (lnf/dbar-style)]
    (get {:matrix 25 :sixteen 20 :basic 20} sty 25)))
  

(defn displaybar [grp p0 count]
  (let [db (dbar/displaybar grp (first p0)(second p0) count (lnf/dbar-style)
                            :cell-width (dbar-cell-width)
                            :cell-height (dbar-cell-height))]
    (.colors! db (lnf/dbar-inactive-color)(lnf/dbar-active-color))
    db))


(defn electronic-eye [grp p0 n]
  (image/read-image grp p0 (format "resources/algo/electronic_eye_%d.png" n)))


;; testing function 'fpo' -> "For Position Only"
;;
(defn fpo 
 
  ([grp p](point/point grp p
               :color :gray
               :style [:dot :bar :dash]
               :size 6))
  ([grp p c]
   (point/point grp p :id :FPO :color c
                :style [:diag :diag2]
                :size 5)))
