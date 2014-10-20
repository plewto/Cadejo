(ns cadejo.instruments.masa.gamut-editor
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.config :as config])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))
           

(def ^:private freq-format "%7.4f")

(defn- third [col]
  (nth col 2))

(defn- fourth [col]
  (nth col 3))

(def harmonic-list (range 0.5 16 0.5)) 

(defn- harmonic-model []
  (ss/spinner-model 1.0 :from 0.5 :to 16.0 :by 0.5))

;; in cents
;;
(defn- detune-model []
  (ss/spinner-model 0 :from -1200 :to 1200 :by 1))
              
(defn- partial-sub-panel [n]
  (let [spin-harmonic (ss/spinner :model (harmonic-model))
        spin-detune (ss/spinner :model (detune-model))
        jb-inc (ss/button :text "+")
        jb-dec (ss/button :text "-")
        lab-ratio (ss/label :text "x")
        pan-sub (ss/grid-panel :columns 1
                               :items [(ss/label :text "Harmonic")
                                       spin-harmonic
                                       (ss/vertical-panel)
                                       (ss/label :text "Detune")
                                       spin-detune
                                       jb-inc
                                       jb-dec])
        pan-main (ss/border-panel :center (ss/vertical-panel
                                           :items [pan-sub 
                                                   (Box/createVerticalStrut 224)])
                                  :south lab-ratio
                                  :border (factory/title (str n)))
        param (keyword (format "r%d" n))]
    (ss/listen jb-inc :action (fn [_]
                                (let [v1 (long (.getValue spin-detune))
                                      v2 (min 1200 (+ (.longValue v1) 100))]
                                  (.setValue spin-detune (.intValue v2)))))
    (ss/listen jb-dec :action (fn [_]
                                (let [v1 (long (.getValue spin-detune))
                                      v2 (max -1200 (- (.longValue v1) 100))]
                                  (.setValue spin-detune (.intValue v2)))))
    (if (config/enable-tooltips)
      (do 
        (.setToolTipText spin-harmonic "Set drawbar partial harmonic")
        (.setToolTipText spin-detune "Detune partial (cents)")
        (.setToolTipText jb-inc "Increases detune by 100 cents")
        (.setToolTipText jb-dec "Decrease detune by 100 cents")))
    (.putClientProperty spin-harmonic :drawbar (dec n))
    (.putClientProperty spin-harmonic :param param)
    (.putClientProperty spin-detune :drawbar (dec n))
    (.putClientProperty spin-detune :param param)
    (.putClientProperty lab-ratio :drawbar (dec n))
    [spin-harmonic spin-detune lab-ratio pan-main]))

(defn gamut-tab [performance ied]
  (let [enable-change-listener* (atom true)
        drawbars (let [acc* (atom [])]
                   (dotimes [n 9]
                     (let [psp (partial-sub-panel (inc n))]
                       (swap! acc* (fn [n](conj n psp)))))
                   @acc*)
        jb-preset-b3 (ss/button :text "B3")
        jb-preset-harmonic (ss/button :text "Harmonic")
        jb-preset-odd (ss/button :text "Odd")
        jb-preset-prime (ss/button :text "Prime")
        jb-preset-random1 (ss/button :text "Random 1") ;; semi-harmonic
        jb-preset-random2 (ss/button :text "Random 2") ;; enharmonic
        jb-preset-cluster (ss/button :text "Clusters") 
        jb-preset-detune (ss/button :text "Detune")    ;; additive
        pan-preset (ss/grid-panel :columns 1 ;:rows 4
                                  :items [jb-preset-b3
                                          jb-preset-harmonic
                                          jb-preset-odd
                                          jb-preset-prime
                                          jb-preset-random1
                                          jb-preset-random2
                                          jb-preset-cluster
                                          jb-preset-detune]
                                  :border (factory/title "Presets"))
        pan-drawbars (ss/grid-panel :rows 1
                                    :items (map fourth drawbars)
                                    :border (factory/title "Partials"))
        pan-main (ss/border-panel :center pan-drawbars
                                  :east pan-preset)
        widget-map {:pan-main pan-main}

        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
             
             (widgets [this] widget-map)
             
             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "masa amp-tab does not have %s widget" key))))
             
             (parent [this] ied)

             (parent! [this ignore] ied)
        
             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param (float val)))
               
             (init! [this]
               (.set-param! this :r1 0.5)
               (.set-param! this :r2 1.5)
               (.set-param! this :r3 1.0)
               (.set-param! this :r4 2.0)
               (.set-param! this :r5 3.0)
               (.set-param! this :r6 4.0)
               (.set-param! this :r7 5.0)
               (.set-param! this :r8 6.0)
               (.set-param! this :r9 8.0)
               (.sync-ui! this))

             (sync-ui! [this]
               (reset! enable-change-listener* false)
               (let [data (.current-data (.bank performance))
                     params [:r1 :r2 :r3 :r4 :r5 :r6 :r7 :r8 :r9]]
                 (dotimes [n (count params)]
                   (let [param (nth params n)
                         freq (float (get data param 1.0))
                         harmonic (math/closest freq harmonic-list)
                         detune (float (/ freq harmonic))
                         cents (int (math/logn detune math/cent))
                         drawbar (nth drawbars n)]
                     (.setValue (first drawbar) harmonic)
                     (.setValue (second drawbar)(int cents))
                     (ss/config! (third drawbar)
                                 :text (format freq-format freq)))))
               (reset! enable-change-listener* true)))

        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    n (.getClientProperty src :drawbar)
                                    param (.getClientProperty src :param)
                                    drawbar (nth drawbars n)
                                    spin-harmonic (first drawbar)
                                    spin-detune (second drawbar)
                                    lab-freq (third drawbar)
                                    harmonic (.getValue spin-harmonic)
                                    detune (.getValue spin-detune)
                                    freq (math/transpose harmonic detune)
                                    sfreq (format freq-format freq)]
                                (ss/config! lab-freq :text sfreq)
                                ;(.status! ed (format "[%s] --> val %s" param sfreq))
                                (.set-param! ied param freq)))))]
    (doseq [q drawbars]
      (.addChangeListener (first q) change-listener)
      (.addChangeListener (second q) change-listener))
    (.add-sub-editor! ied "Gamut" ed)

    (ss/listen jb-preset-b3 
               :action (fn [_]
                         (dotimes [n (.count b3)]
                           (let [drawbar (nth drawbars n)]
                             (.setValue (first drawbar) (Double. (.doubleValue (nth b3 n))))
                             (.setValue (second drawbar) (Integer. 0))
                             (.status! ed "Using B3 frequencies")))))
    
    (ss/listen jb-preset-harmonic 
               :action (fn [_]
                         (dotimes [n (.count harmonic)]
                           (let [drawbar (nth drawbars n)]
                             (.setValue (first drawbar) (Double. (.doubleValue (nth harmonic n))))
                             (.setValue (second drawbar) (Integer. 0))
                             (.status! ed "Using harmonic frequencies")))))

    (ss/listen jb-preset-odd
               :action (fn [_]
                         (dotimes [n (.count harmonic)]
                           (let [drawbar (nth drawbars n)]
                             (.setValue (first drawbar) (Double. (.doubleValue (nth odd n))))
                             (.setValue (second drawbar) (Integer. 0))
                             (.status! ed "Using odd frequencies")))))

    (ss/listen jb-preset-prime
               :action (fn [_]
                         (dotimes [n (.count harmonic)]
                           (let [drawbar (nth drawbars n)]
                             (.setValue (first drawbar) (Double. (.doubleValue (nth prime n))))
                             (.setValue (second drawbar) (Integer. 0))
                             (.status! ed "Using prime frequencies")))))

    (ss/listen jb-preset-random1
               :action (fn [_]
                         (.doClick (rand-nth [jb-preset-b3 jb-preset-b3 
                                              jb-preset-harmonic jb-preset-harmonic
                                              jb-preset-odd jb-preset-prime]))
                         (dotimes [n (.count harmonic)]
                           (if (pos? n)
                             (let [drawbar (nth drawbars n)]
                               (if (math/coin 1/3)
                                 (.setValue (second drawbar)
                                            (Integer. (int (* (math/coin 0.5 -1 +1)
                                                              (rand 100)))))))))
                         (.status! ed "Using random frequencies 1")))

    (ss/listen jb-preset-random2
               :action (fn [_]
                         (.doClick (rand-nth [jb-preset-b3 jb-preset-harmonic
                                              jb-preset-odd jb-preset-prime]))
                         (dotimes [n (.count harmonic)]
                           (if (pos? n)
                             (let [drawbar (nth drawbars n)]
                               (.setValue (second drawbar)
                                          (Integer. (int (* (math/coin 0.5 -1 +1)
                                                            (rand 100))))))))
                         (.status! ed "Using random frequencies 2")))
    
    (ss/listen jb-preset-cluster
               :action (fn [_]
                         (let [f* (atom 1)]
                           (dotimes [n (count drawbars)]
                             (let [drawbar (nth drawbars n)
                                   detune (math/approx (* n 5) 0.02)]
                               (.setValue (first drawbar)(double @f*))
                               (.setValue (second drawbar)(int detune)))
                             (swap! f* (fn [q](+ q (math/coin 0.80 0 (rand-nth '[0.5 1 1.5 2])))))))
                         (.status! ed "Using clusted frequencies")))

    (ss/listen jb-preset-detune
               :action (fn [_]
                         (doseq [drawbar drawbars]
                           (let [range 10
                                 spin-detune (second drawbar)
                                 dt1 (.getValue spin-detune)
                                 dt2 (math/clamp (math/coin 1/2 dt1 (+ dt1 (* (math/coin 1/5 -1 +1)(rand range)))) -100 100)]
                             (.setValue spin-detune (int dt2))))
                         (.status! ed "Applied random detune")))
    ed))
