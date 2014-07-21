(println "--> cadejo.ui.midi.curve-panel")
(ns cadejo.ui.midi.curve-panel
  (:require [cadejo.config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.icon :as icon])
  (:use [seesaw.core :only [border-panel button button-group
                            grid-panel label listen radio]])
  (:import java.io.File
           javax.swing.JLabel))

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
  (if (cadejo.config/enable-tooltips)
    (doseq [b buttons]
      (let [curve (.getClientProperty b :curve)]
        (.setToolTipText b (get tooltips curve ""))))))

(def icon-extension "png")

(def curve-order [:zero :half :one 
                  :linear :quadratic :cubic
                  :convex2 :convex4 :convex6
                  :logistic :logistic2 nil
                  ;; inverted
                  :ilinear :iquadratic :icubic
                  :iconvex2 :iconvex4 :iconvex6
                  :ilogistic :ilogistic2 nil])
                  
(def blank-icon (let [pathname (path/append-extension
                                (path/join "resources" "icons" 
                                           "curves" "blank")
                       icon-extension)
                      ico (icon/icon (File. pathname))]
                  ico))

(defn- get-icon-filename [base selected inverted]
  (let [file (str (if selected "s" "u")
                  (if inverted "" "n")
                  base)
        pathname (path/append-extension
                  (path/join "resources" "icons" "curves" (name file))
                  icon-extension)]
    (File. pathname)))

(defn- create-buttons [grp]
  (let [acc* (atom [])
        rvsmap* (atom {})]
    (doseq [curve curve-order]
      (if curve
        (let [curve-name (name curve)
              inverted (= (first curve-name) \i)
              s-icon (get-icon-filename curve-name true inverted)
              u-icon (get-icon-filename curve-name false inverted)
              jrb (radio :group grp)]
          (.putClientProperty jrb :curve curve)
          (.setIcon jrb (icon/icon u-icon))
          (.setSelectedIcon jrb (icon/icon s-icon))
          (.setSelected jrb (= curve :linear))
          (swap! acc* (fn [n](conj n jrb)))
          (swap! rvsmap* (fn [n](assoc n curve jrb))))
        (let [jrb (radio)]
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
     (let [bgrp (button-group)
           [buttons rvsmap] (create-buttons bgrp)
           pan-center (grid-panel :rows 7 :columns 3 :items buttons)
           pan-main (border-panel :center pan-center
                                  :border (if title 
                                            (factory/title title)
                                            (factory/padding)))
           
           current-curve* (atom :linear)
           widgets {:button-group bgrp
                    :buttons buttons
                    :pan-main pan-main}]
       (doseq [b buttons]
         (let [c (.getClientProperty b :curve)]
           (if c 
             (listen b :action 
                     (fn [ev] 
                       (let [src (.getSource ev)]
                         (reset! current-curve*
                                 (.getClientProperty src :curve))))))))
       (set-tooltips buttons)
       (reify CurvePanel
         
         (widget-map [this] widgets)
         
         (widget [this key]
           (or (get widgets key)
               (umsg/warning (format "CurvePanel does not have %s widget" key))))
         
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
               (listen b :action afn))))))))
