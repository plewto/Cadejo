(ns cadejo.instruments.alias.editor.filter-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.config :as config])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [seesaw.core :as ss])
  (:require [sgwr.indicators.fixed-numberbar :as fixedbar])
  (:import javax.swing.Box
           javax.swing.SwingUtilities
           javax.swing.event.ChangeListener))

(defn- flip 
  ([n p]
     (* (math/coin p -1 1) n))
  ([n]
     (flip n 0.5)))
  

(defn- filter1-dice [ied]
  (let [data {:distortion1-pregain (math/coin 0.75 1 (rand 16))
              :distortion1-param (rand)
              :distortion1-param-source (math/coin 0.75 0 (int (rand 9)))
              :distortion1-param-depth (flip (rand))
              :distortion1-mix (flip (rand))
              :filter1-res (rand)
              :filter1-res-source (int (rand 9))
              :filter1-res-depth (flip (math/coin 0.75 0 1))
              :filter1-freq (rand 10000)
              :filter1-freq1-source (int (rand 9))
              :filter1-freq1-depth (flip 0.10 (rand 5000))
              :fitler1-freq2-source (int (rand 9))
              :filter1-freq2-depth (flip 0.10 (rand 5000))
              :filter1-pan (math/coin 0.75 -0.75 (flip (rand)))
              :filter1-pan-source (int (rand 9))
              :filter1-pan-depth (math/coin 0.75 0 (flip (rand)))
              :filter1-mode (math/coin 0.75 0 (rand))
              :filter1-postgain 1.0}]
    (doseq [[p v] data]
      (.set-param! ied p v))))
    ;; (.sync-ui! ied)
    ;; (.status! ied "Randomized filter 1")))

(defn- filter2-dice [ied]
  (let [data {:distortion2-pregain (math/coin 0.75 1 (rand 16))
              :distortion2-param (rand)
              :distortion2-param-source (math/coin 0.75 0 (int (rand 9)))
              :distortion2-param-depth (flip (rand))
              :distortion2-mix (flip (rand))
              :filter2-res (rand)
              :filter2-res-source (int (rand 9))
              :filter2-res-depth (flip (math/coin 0.75 0 1))
              :filter2-freq (rand 10000)
              :filter2-freq1-source (int (rand 9))
              :filter2-freq1-depth (flip 0.10 (rand 5000))
              :fitler2-freq2-source (int (rand 9))
              :filter2-freq2-depth (flip 0.10 (rand 5000))
              :filter2-pan (math/coin 0.75 -0.75 (flip (rand)))
              :filter2-pan-source (int (rand 9))
              :filter2-pan-depth (math/coin 0.75 0 (flip (rand)))
              :filter2-postgain 1.0}]
    (doseq [[p v] data]
      (.set-param! ied p v))))
    ;; (.sync-ui! ied)
    ;; (.status! ied "Randomized filter 2")))

(defn- vertical-strut []
  (Box/createVerticalStrut 1))

(defn- distortion-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-pregain (keyword (format "distortion%d-pregain" prefix))
        param-clip (keyword (format "distortion%d-param" prefix))
        param-source (keyword (format "distortion%d-param-source" prefix))
        param-depth (keyword (format "distortion%d-param-depth" prefix))
        param-mix (keyword (format "distortion%d-mix" prefix))
        buspan (factory/matrix-toolbar param-source ied)
        slide-pregain (factory/slider param-pregain 1.0 16.0
                                      (factory/slider-label-map "1" "4" "8" "12" "16"))
        slide-clip (factory/unit-slider param-clip false)
        slide-depth (factory/unit-slider param-depth true)
        slide-mix (factory/slider param-mix -1.0 1.0
                                  (factory/slider-label-map "Dry" "" "" "" "Wet"))
        sliders [slide-pregain slide-clip slide-depth slide-mix]
        pan-pregain (ss/vertical-panel 
                  :items [(factory/half-slider-panel slide-pregain "Pregain")
                          (factory/half-slider-panel slide-clip "Clip")])
        pan-center (ss/horizontal-panel 
                    :items [(:panel buspan)
                            (ss/vertical-panel 
                             :items [(factory/half-slider-panel slide-depth "Depth")
                                     (factory/half-slider-panel slide-mix "Mix")])])
        pan-main (ss/vertical-panel 
                  :items [(vertical-strut)
                          (ss/horizontal-panel :items [pan-pregain pan-center])]
                  :border (factory/title "Distortion"))
        ;; pan-main (ss/horizontal-panel 
        ;;           :items [(factory/slider-panel slide-pregain "Pregain")
        ;;                   (factory/slider-panel slide-clip "Clip")
        ;;                   (:panel buspan)
        ;;                   (factory/slider-panel slide-depth "Depth")
        ;;                   (factory/slider-panel slide-mix "Mix")]
        ;;           :border (factory/title "Distortion"))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-pregain 1.0
                              param-clip 0
                              param-source 0
                              param-depth 0.0
                              param-mix -1.0}]
                    (doseq [[p v] data]
                      (.set-param! ied p v))))
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
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- mode-editor [ied]
  (let [enable-change-listener* (atom true)
        param-mode :filter1-mode
        slide-mode (factory/slider param-mode 0.0 1.0 
                                   (factory/slider-label-map 
                                    "Low" "L*H" "High" "Band" "Off"))
        pan-main (factory/slider-panel slide-mode "Mode")
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (factory/sync-slider slide-mode data)
                 (reset! enable-change-listener* true))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [_]
                            (if @enable-change-listener*
                              (let [scale (.getClientProperty slide-mode :scale)
                                    bias (.getClientProperty slide-mode :bias)
                                    pos (.getValue slide-mode)
                                    value (float (+ bias (* scale pos)))]
                                (.set-param! ied param-mode value)))))
        resetfn (fn []
                   (.setValue slide-mode 0))]
    (.addChangeListener slide-mode change-listener)
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- freq-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-freq (keyword (format "filter%d-freq" prefix))
        param-source1 (keyword (format "filter%d-freq1-source" prefix))
        param-source2 (keyword (format "filter%d-freq2-source" prefix))
        param-depth1 (keyword (format "filter%d-freq1-depth" prefix))
        param-depth2 (keyword (format "filter%d-freq2-depth" prefix))
        buspan1 (factory/matrix-toolbar param-source1 ied "1")
        buspan2 (factory/matrix-toolbar param-source2 ied "2")
        slide-depth1 (factory/slider param-depth1 -8000 8000 (factory/slider-label-map "-8k" "" "0" "" "+8k"))
        slide-depth2 (factory/slider param-depth2 -8000 8000 (factory/slider-label-map "-8k" "" "0" "" "+8k"))
        nbar-freq (let [[bg inactive active button](config/displaybar-colors)
                        nbar (fixedbar/fixedpoint-numberbar 5)]
                    (ss/config! (:drawing-canvas nbar) :size [165 :by 90])
                    ((:colors nbar) bg inactive active button)
                    ((:set-hook nbar)(fn [value]
                                       (.set-param! ied param-freq value)))
                    nbar)
        pan-north (ss/flow-panel :items [(:drawing-canvas nbar-freq)])
        pan-fm1 (ss/horizontal-panel :items [(:panel buspan1)
                                           (factory/slider-panel slide-depth1 "Depth 1")
                                           (:panel buspan2)
                                           (factory/slider-panel slide-depth2 "Depth 2")])
        pan-main (ss/vertical-panel :items [pan-north pan-fm1]
                                    :border (factory/title "Frequency"))
        sliders [slide-depth1 slide-depth2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan1) data)
                 ((:syncfn buspan2) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 ((:setfn nbar-freq)(float (param-freq data)))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-freq 10000.0
                              param-source1 0
                              param-source2 0
                              param-depth1 0.0
                              param-depth2 0.0}]
                    (doseq [[p v] data]
                      (.set-param! ied p v))))
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
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- res-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-res (keyword (format "filter%d-res" prefix))
        param-source (keyword (format "filter%d-res-source" prefix))
        param-depth (keyword (format "filter%d-res-depth" prefix))
        buspan (factory/matrix-toolbar param-source ied)
        slide-res (factory/unit-slider param-res false)
        slide-depth (factory/unit-slider param-depth true)
        pan-main (ss/vertical-panel 
                  :items [(vertical-strut)
                          (ss/horizontal-panel 
                           :items [(:panel buspan)
                                   (ss/vertical-panel 
                                    :items [(factory/half-slider-panel slide-depth "Depth")
                                            (factory/half-slider-panel slide-res "Res")])])]
                  :border (factory/title "Resonance"))
        sliders [slide-res slide-depth]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-res 0.0
                              param-source 0
                              param-depth 0.0}]
                    (doseq [[p v] data]
                      (.set-param! ied p v))))
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
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- pan-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-pan (keyword (format "filter%d-pan" prefix))
        param-source (keyword (format "filter%d-pan-source" prefix))
        param-depth (keyword (format "filter%d-pan-depth" prefix))
        param-gain (keyword (format "filter%d-postgain" prefix))
        buspan (factory/matrix-toolbar param-source ied)
        slide-pan (factory/unit-slider param-pan true)
        slide-depth (factory/unit-slider param-depth true)
        slide-gain (factory/unit-slider param-gain false)
        pan-main (ss/vertical-panel 
                  :items [(vertical-strut)
                          (ss/horizontal-panel
                           :items [(:panel buspan)
                                   (ss/vertical-panel 
                                    :items [(factory/half-slider-panel slide-depth "Depth")
                                            (factory/half-slider-panel slide-pan "Pan") ])
                                   (factory/slider-panel slide-gain "Post gain")])]
                  :border (factory/title "Out"))
        sliders [slide-pan slide-depth slide-gain]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-pan 0.0
                              param-source 0
                              param-depth 0.0
                              param-gain 1.0}]
                    (doseq [[p v] data]
                      (.set-param! ied p v))))
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
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))
        
(defn filtered [prefix performance ied]
  (let [dst (distortion-editor prefix ied)
        mode (if (= prefix 1)
               (mode-editor ied)
               nil)
        freq (freq-editor prefix ied)
        res (res-editor prefix ied)
        out (pan-editor prefix ied)
        micro-buttons (factory/micro-button-panel :alias-filter)
        pan-west (:panel micro-buttons)
        pan-east (ss/horizontal-panel :items [(:pan-main res)
                                            (:pan-main out)])
        pan-main (ss/horizontal-panel :items [pan-west
                                              (:pan-main dst)
                                              (if mode 
                                                (:pan-main mode)
                                                (Box/createHorizontalStrut 56))
                                              (:pan-main freq)
                                              pan-east]
                                      :border (factory/title (format "Filter %s" prefix)))
        widget-map {:pan-main pan-main}]
    (ss/listen (:jb-init micro-buttons)
               :action (fn [_]
                         (.working ied true)
                         (SwingUtilities/invokeLater
                          (proxy [Runnable][]
                            (run []
                              (let [data (nth [constants/initial-program-filter1
                                               constants/initial-program-filter2]
                                              (dec prefix))]
                                (doseq [[p v] data]
                                  (.set-param! ied p v))
                                (.sync-ui! ied)
                                (.working ied false)
                                (.status! ied (format "Initialized filter %s" prefix))))))))
    (ss/listen (:jb-dice micro-buttons)
               :action (fn [_]
                         (.working ied true)
                         (SwingUtilities/invokeLater
                          (proxy [Runnable][]
                            (run []
                              (if (= prefix 1)
                                (filter1-dice ied)
                                (filter2-dice ied))
                              (.sync-ui! ied)
                              (.working ied false)
                              (.status! ied (format "Randomized filter %s" prefix)))))))
    (reify subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      (widget [this key] (get widget-map key))
      (parent [this] ied)
      (parent! [this _] ied) ;; ignore
      (status! [this txt]
        (.status ied txt))
      (warning! [this txt]
        (.warning! ied txt))
      (set-param! [this p v]
        (.set-param! ied p v))
      (init! [this] ) ;; ignore
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          ((:syncfn dst) data)
          ((:syncfn freq) data)
          ((:syncfn res) data)
          ((:syncfn out) data)
          (if mode ((:syncfn mode) data)))))))

(defn filter-editor [performance ied]
  (let [f1 (filtered 1 performance ied)
        f2 (filtered 2 performance ied)
        pan (ss/scrollable 
             (ss/vertical-panel :items [(.widget f1 :pan-main)
                                        (.widget f2 :pan-main)]))]
    (subedit/subeditor-wrapper [f1 f2] pan)))
        
