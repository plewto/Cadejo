(ns cadejo.instruments.alias.editor.osc-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.config :as config])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory :reload true])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.util.math :as math])
  (:require [seesaw.core :as ss])
  (:require [sgwr.drawing :as drawing])
  (:require [sgwr.indicators.fixed-numberbar :as fixedbar])
  (:import java.awt.BorderLayout
           javax.swing.Box
           javax.swing.event.ChangeListener
           javax.swing.SwingUtilities))

(def bus-con (:con constants/general-bus-map))
(def bus-a (:a constants/general-bus-map))

(defn- osc-dice [ied prefix]
  (let [param (fn [suffix](keyword (format "osc%s-%s" prefix suffix)))
        param-detune (param "detune")
        param-bias (param "bias")
        param-fm1-source (param "fm1-source")
        param-fm1-depth (param "fm1-depth")
        param-fm1-lag (param "fm1-lag")
        param-fm2-source (param "fm2-source")
        param-fm2-depth (param "fm2-depth")
        param-fm2-lag (param "fm2-lag")
        param-wave (param "wave")
        param-wave1-source (param "wave1-source")
        param-wave1-depth (param "wave1-depth")
        param-wave2-source (param "wave2-source")
        param-wave2-depth (param "wave2-depth")
        param-amp (param "amp")
        param-amp1-source (param "amp1-source")
        param-amp1-depth (param "amp1-depth")
        param-amp1-lag (param "amp1-lag")
        param-amp2-source (param "amp2-source")
        param-amp2-depth (param "amp2-depth")
        param-amp2-lag (param "amp2-lag")
        param-pan (param "pan")
        harmonic (rand-nth [0.25 0.5 0.5 0.25 0.5 0.5 
                            1 1 1 1 1 1 1 1 1 1 1 1
                            2 2 2 2 2 2 3 3 3 4 4 5
                            6 7 8 0])
        detune (* harmonic (math/coin 0.75 1 (rand)))
        bias (cond (zero? detune)
                   (+ 100 (rand 1000))
                   
                   :default
                   (math/coin 0.75 0 (math/coin 0.75 (rand 2)(rand 1000))))
        fm1 (if (math/coin 0.75)
              [1 0.05 0.0]
              [(rand-nth (range 9))
               (* (math/coin 0.5 -1 1)(rand))
               (math/coin 0.5 0 (rand))])
        fm2 (if (math/coin 0.75)
              [0 0 0]
              [(rand-nth (range 9))
               (* (math/coin 0.5 -1 1)(rand))
               (math/coin 0.5 0 (rand))])
        wv (rand)
        wv1 (if (math/coin 0.75)
              [0 0]
              [(rand-nth (range 9))
               (* (math/coin 0.5 -1 1)(rand))])
        wv2 (if (math/coin 0.75)
              [0 0]
              [(rand-nth (range 9))
               (* (math/coin 0.5 -1 1)(rand))])
        amp (rand-nth '[0 0 0 -3 -3 -6 -6 -9 -48])
        pan (* (math/coin 0.5 -1 1)(rand))
        amp1 (if (math/coin 0.75)
               [0 1 0]
               [(rand-nth (range 9))
                (* (math/coin 0.5 -1 1)(rand))
                (math/coin 0.75 0 (rand))])
        amp2 (if (math/coin 0.75)
               [0 1 0]
               [(rand-nth (range 9))
                (* (math/coin 0.5 -1 1)(rand))
                (math/coin 0.75 0 (rand))])]
    (.set-param! ied param-detune detune)
    (.set-param! ied param-bias bias)
    (.set-param! ied param-fm1-source (first fm1))
    (.set-param! ied param-fm1-depth (second fm1))
    (.set-param! ied param-fm1-lag (last fm1))
    (.set-param! ied param-fm2-source (first fm2))
    (.set-param! ied param-fm2-depth (second fm2))
    (.set-param! ied param-fm2-lag (last fm2))
    (.set-param! ied param-wave wv)
    (.set-param! ied param-wave1-source (first wv1))
    (.set-param! ied param-wave1-depth (second wv1))
    (.set-param! ied param-wave2-source (first wv2))
    (.set-param! ied param-wave2-depth (second wv2))
    (.set-param! ied param-amp amp)
    (.set-param! ied param-pan pan)
    (.set-param! ied param-amp1-source (first amp1))
    (.set-param! ied param-amp1-depth (second amp1))
    (.set-param! ied param-amp1-lag (last amp1))
    (.set-param! ied param-amp2-source (first amp2))
    (.set-param! ied param-amp2-depth (second amp2))
    (.set-param! ied param-amp2-lag (last amp2))
    (.sync-ui! ied)
    (.status! ied (format "Set osc %s to random values" prefix))))

(defn osced [prefix ied]
  (let [enable-change-listener* (atom true)
        [bg inactive active button](config/displaybar-colors)
        drw (let [d (drawing/native-drawing 360 102)]   ; 400
              (.paper! d bg)
              (.color! d button)
              (.width! d 2)
              (.text! d [8 96] "Detune")
              (.text! d [200 96] "Bias")   ; [240 96]
              d)
        param (fn [suffix](keyword (format "osc%s-%s" prefix suffix)))
        param-detune (param "detune")
        param-bias (param "bias")
        param-fm1-source (param "fm1-source")
        param-fm1-depth (param "fm1-depth")
        param-fm1-lag (param "fm1-lag")
        param-fm2-source (param "fm2-source")
        param-fm2-depth (param "fm2-depth")
        param-fm2-lag (param "fm2-lag")
        param-wave (param "wave")
        param-wave1-source (param "wave1-source")
        param-wave1-depth (param "wave1-depth")
        param-wave2-source (param "wave2-source")
        param-wave2-depth (param "wave2-depth")
        param-amp (param "amp")
        param-amp1-source (param "amp1-src")
        param-amp1-depth (param "amp1-depth")
        param-amp1-lag (param "amp1-lag")
        param-amp2-source (param "amp2-src")
        param-amp2-depth (param "amp2-depth")
        param-amp2-lag (param "amp2-lag")
        param-pan (param "pan")
        micro-buttons (factory/micro-button-panel :alias-osc)
        pan-north-west (:panel micro-buttons)
        nbar-detune (let [d (fixedbar/fixedpoint-numberbar 6
                                                           :drawing drw
                                                           :x-offset 0
                                                           :y-offset 0
                                                           :decimal-point 4)]
                      (.color! drw :blue)
                      (.style! drw 0)
                      ((:colors d) bg inactive active button)
                      d)
        nbar-bias (let [d (fixedbar/fixedpoint-numberbar 5
                                                         :drawing drw
                                                         :x-offset 191
                                                         :y-offset 0
                                                         :decimal-point 1)]
                      (.color! drw :blue)
                      (.style! drw 0)
                      ((:colors d) bg inactive active button)
                      d)
        pan-north (ss/border-panel :west pan-north-west
                                   :center (ss/horizontal-panel
                                            :items [(.drawing-canvas drw)])
                                   :size [408 :by 102])
        ;; FM Panel
        bus-fm1 (factory/matrix-toolbar param-fm1-source ied 1)
        slide-fm1-depth (factory/unit-slider param-fm1-depth :signed)
        slide-fm1-lag (factory/unit-slider param-fm1-lag)
        pan-fm1 (ss/border-panel
                 :west (:panel bus-fm1)
                 :center (ss/horizontal-panel 
                          :items [(factory/slider-panel slide-fm1-depth "Depth 1")
                                  (factory/slider-panel slide-fm1-lag "Lag 1")]))
        bus-fm2 (factory/matrix-toolbar param-fm2-source ied 2)
        slide-fm2-depth (factory/unit-slider param-fm2-depth :signed)
        slide-fm2-lag (factory/unit-slider param-fm2-lag)
        pan-fm2 (ss/border-panel
                 :west (:panel bus-fm2)
                 :center (ss/horizontal-panel 
                          :items [(factory/slider-panel slide-fm2-depth "Depth 2")
                                  (factory/slider-panel slide-fm2-lag "Lag 2")]))
        pan-fm (ss/horizontal-panel :items [(factory/blank-slider)
                                            pan-fm1 pan-fm2]
                                    :size [408 :by 164]
                                    :border (factory/title "FM"))
        ;; Wave panel
        bus-wave1 (factory/matrix-toolbar param-wave1-source ied 1)
        slide-wave1-depth (factory/unit-slider param-wave1-depth :signed)
        pan-wave1 (ss/border-panel
                   :west (:panel bus-wave1)
                   :center  (factory/slider-panel slide-wave1-depth "Depth 1"))
        bus-wave2 (factory/matrix-toolbar param-wave2-source ied 2)
        slide-wave2-depth (factory/unit-slider param-wave2-depth :signed)
        pan-wave2 (ss/border-panel
                   :west (:panel bus-wave2)
                   :center (factory/slider-panel slide-wave2-depth "Depth 2"))
        slide-wave (factory/unit-slider param-wave)
        pan-wave (ss/horizontal-panel :items [(factory/slider-panel slide-wave "Wave")
                                              pan-wave1
                                              pan-wave2]
                                      :size [408 :by 164]
                                      :border (factory/title "Wave"))
        ;; Amp panel
        bus-amp1 (factory/matrix-toolbar param-amp1-source ied 1)
        slide-amp1-depth (factory/unit-slider param-amp1-depth :signed)
        slide-amp1-lag (factory/unit-slider param-amp1-lag)
        pan-amp1 (ss/border-panel
                  :west (:panel bus-amp1)
                  :center (ss/horizontal-panel
                           :items [(factory/slider-panel slide-amp1-depth "Depth 1")
                                   (factory/slider-panel slide-amp1-lag "lag 1")]))
        bus-amp2 (factory/matrix-toolbar param-amp2-source ied 2)
        slide-amp2-depth (factory/unit-slider param-amp2-depth :signed)
        slide-amp2-lag (factory/unit-slider param-amp2-lag)
        pan-amp2 (ss/border-panel
                  :west (:panel bus-amp2)
                  :center (ss/horizontal-panel
                           :items [(factory/slider-panel slide-amp2-depth "Depth 2")
                                   (factory/slider-panel slide-amp2-lag "lag 2")]))
        slide-amp (factory/mix-slider param-amp)
        slide-pan (factory/panner-slider param-pan (factory/pan-label-map) false)
        pan-am (ss/border-panel 
                 :center (ss/horizontal-panel 
                          :items [(factory/slider-panel slide-amp "Amp")
                                  pan-amp1
                                  pan-amp2])
                 :south (ss/border-panel :west (ss/label :text "Pan" 
                                                         :font factory/default-font)
                                         :center slide-pan)
                 :size [408 :by 220]
                 :border (factory/title "AM"))
        pan-main (ss/vertical-panel
                  :items [pan-north
                          pan-fm
                          pan-wave
                          pan-am]
                  :border (factory/title (format "OSC %s" prefix)))
        sliders [slide-fm1-depth slide-fm1-lag slide-fm2-depth slide-fm2-lag
                 slide-wave slide-wave1-depth slide-wave2-depth
                 slide-amp slide-pan 
                 slide-amp1-depth slide-amp1-lag slide-amp2-depth slide-amp2-lag]
        bus-switches [bus-fm1 bus-fm2 bus-wave1 bus-wave2 bus-amp1 bus-amp2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (let [detune (get data param-detune)
                       bias (get data param-bias)]
                   ((:setfn nbar-detune) detune)
                   ((:setfn nbar-bias) bias))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (doseq [s bus-switches]
                   ((:syncfn s) data))
                 (reset! enable-change-listener* true))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (float (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))]
    (doseq [s sliders]
      (.addChangeListener s change-listener))
    ((:set-hook nbar-detune)(fn [value]
                              (.set-param! ied param-detune (float value))))
    ((:set-hook nbar-bias)(fn [value]
                            (.set-param! ied param-bias (float value))))
    (ss/listen (:jb-init micro-buttons) 
               :action (fn [_]
                         (.working ied true)
                         (SwingUtilities/invokeLater
                          (proxy [Runnable][]
                            (run []
                              (let [data (nth [constants/initial-program-osc1
                                               constants/initial-program-osc2
                                               constants/initial-program-osc3]
                                              (dec prefix))]
                                (doseq [[p v] data]
                                  (.set-param! ied p v))
                                (.sync-ui! ied)
                                (.working ied false)
                                (.status! ied (format "OSC %s initialized" prefix))))))))

    (ss/listen (:jb-dice micro-buttons) 
               :action (fn [_]
                         (.working ied true)
                         (SwingUtilities/invokeLater
                          (proxy [Runnable][]
                            (run []
                              (osc-dice ied prefix)
                              (.working ied false))))))
                                                  
    (.render drw)
    {:pan-main pan-main
     :syncfn syncfn }))


(defn osc-editor [performance ied]
  (let [osc1 (osced 1 ied)
        osc2 (osced 2 ied)
        osc3 (osced 3 ied)
        pan-main (ss/grid-panel :rows 1
                                :items [(:pan-main osc1)
                                        (:pan-main osc2)
                                        (:pan-main osc3)])]
        (reify subedit/InstrumentSubEditor
          (widgets [this] {:pan-main pan-main})
          (widget [this key](get (.widgets this) key))
          (parent [this] ied)
          (parent! [this _] ied) ;; ignore
          (status! [this txt] (.status! ied txt))
          (warning! [this txt](.warning! ied txt))
          (set-param! [this p v]
            (.set-param! ied p v))
          (init! [this]
            (doseq [e [osc1 osc2 osc3]]
              ((:resetfn e))))
          (sync-ui! [this]
            (let [data (.current-data (.bank performance))]
              (doseq [e [osc1 osc2 osc3]]
                ((:syncfn e) data)))))))
