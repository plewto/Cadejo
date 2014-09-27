(ns cadejo.ui.midi.curve-panel
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:import java.awt.Dimension))

(def ^:private rows 7)
(def ^:private columns 3)
(def ^:private icon-size 36)
(def ^:private height (* (inc rows) icon-size))
(def ^:private width (* columns icon-size))
(def ^:private panel-size (Dimension. width height))

(def tooltips {:zero "Constant y=0.0"
               :half "Constant y=0.5"
               :one "Constant y=1.0"
               :linear "Linear y=x"
               :quadratic "Quadratic y=x^2"
               :cubic "Cubic y=x^3"
               :quartic "Quartic y=x^4"
               :convex2 "Convex2 y=1-(x-1)^2"
               :convex4 "Convex4 y=1-(x-1)^4"
               :convex6 "Convex6 y=1-(x-1)^6"
               :logistic "Sigma Curve"
               :logistic2 "Sigma Curve 2"
               :iconvex2 "Inverted Convex2 y=(x-1)^2"
               :iconvex4 "Inverted Convex4 y=(x-1)^4"
               :iconvex6 "Inverted Convex6 y=(x-1)^6"
               :ilinear "Inverted Linear y=1-x"
               :iquadratic "Inverted Quadratic y=1-x^2"
               :icubic "Inverted Cubic y=1-x^3"
               :ilogistic "Inverted Sigma Curve"
               :ilogistic2 "Inverted Sigma Curve 2"
               nil ""})

(defn- set-tooltips [buttons]
  (doseq [b buttons]
    (let [curve (.getClientProperty b :curve)]
      (.setToolTipText b (get tooltips curve "")))))

(def curve-order [:zero :half :one 
                  :linear :quadratic :cubic
                  :convex2 :convex4 :convex6
                  :logistic :logistic2 nil
                  ;; inverted
                  :ilinear :iquadratic :icubic
                  :iconvex2 :iconvex4 :iconvex6
                  :ilogistic :ilogistic2 nil])

(def blank-icon (lnf/read-icon :blank :36))

(defn- create-buttons [grp]
  (let [acc* (atom [])
        rvsmap* (atom {})]
    (doseq [curve curve-order]
      (if curve
        (let [curve-name (name curve)
              u-icon (lnf/read-icon :curve curve)
              s-icon (lnf/read-selected-icon :curve curve)
              jrb (ss/radio :group grp)]
          (.putClientProperty jrb :curve curve)
          (.setIcon jrb u-icon)
          (.setSelectedIcon jrb s-icon)
          (.setSelected jrb (= curve :linear))
          (swap! acc* (fn [n](conj n jrb)))
          (swap! rvsmap* (fn [n](assoc n curve jrb))))
        (let [jrb (ss/radio)]
          (.putClientProperty jrb :curve nil)
          (.setIcon jrb blank-icon)
          (swap! acc* (fn [n](conj n jrb))))))
    [@acc* @rvsmap*])) 

(defprotocol CurvePanel

  (widget-map
    [this])

  (widget
    [this key])

  (set-curve! 
    [this curve])

  (get-curve
    [this])

  (enable!
    [this flag])

  (add-action-listener! 
    [this afn]))


(defn curve-panel
  ([]
     (curve-panel nil))
  ([title]
     (let [bgrp (ss/button-group)
           [buttons rvsmap] (create-buttons bgrp)
           pan-center (let [pan (ss/grid-panel :rows rows 
                                          :columns columns 
                                          :items buttons
                                          :border (if title 
                                                    (factory/title title)
                                                    (factory/padding)))]
                        (.setMaximumSize pan panel-size)
                        (.setMinimumSize pan panel-size)
                        (.setPreferredSize pan panel-size)
                        pan)
           pan-main (ss/border-panel :north pan-center)
           current-curve* (atom :linear)
           widgets {:button-group bgrp
                    :buttons buttons
                    :pan-main pan-main}]
       (doseq [b buttons]
         (let [c (.getClientProperty b :curve)]
           (if c 
             (ss/listen b :action 
                     (fn [ev] 
                       (let [src (.getSource ev)]
                         (reset! current-curve*
                                 (.getClientProperty src :curve))))))))
       (set-tooltips buttons)
       (reify CurvePanel
         
         (widget-map [this] widgets)
         
         (widget [this key]
           (or (get widgets key)
               (umsg/warning 
                (format "CurvePanel does not have %s widget" key))))
         
         (set-curve! [this curve]
           (let [jrb (get rvsmap curve)]
             (if jrb
               (do 
                 (.setSelected jrb true)
                 (reset! current-curve* curve)
                 curve)
               (do
                 (umsg/warning (format "%s is not a valid curve" curve))
                 nil))))
         
         (get-curve [this]
           @current-curve*)
         
         (enable! [this flag]
           (doseq [b buttons]
             (.setEnabled b flag)))
         
         (add-action-listener! [this afn]
           (doseq [b buttons]
             (if (.getClientProperty b :curve)
               (ss/listen b :action afn))))  ))))
