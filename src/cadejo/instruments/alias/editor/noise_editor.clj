(ns cadejo.instruments.alias.editor.noise-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.util.math :as math])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener
           javax.swing.SwingUtilities))

(def bus-con (:con constants/general-bus-map))
(def bus-a (:a constants/general-bus-map))

(defn- noise-dice [ied]
  (let [crack (math/coin 0.75 (rand 0.25)(rand))
        lowpass (math/coin 0.75 10000 (+ 100 (rand 10000)))
        highpass (math/coin 0.75 10 (+ 10 (rand 10000)))
        amp (rand-nth [0 0 0 0 0 -3 -3 -3 -6 -6 -9 -12])
        s1 (rand-nth (range 9))
        d1 (math/coin 0.75 0 (* (math/coin 0.5 -1 1)(rand)))
        l1 (math/coin 0.75 0 (rand))
        s2 (rand-nth (range 9))
        d2 (math/coin 0.75 0 (* (math/coin 0.5 -1 1)(rand)))
        l2 (math/coin 0.75 0 (rand))]
    (.set-param! ied :noise-param crack)
    (.set-param! ied :noise-lp lowpass)
    (.set-param! ied :hp highpass)
    (.set-param! ied :noise-amp amp)
    (.set-param! ied :noise-amp1-src s1)
    (.set-param! ied :noise-amp1-depth d1)
    (.set-param! ied :noise-amp1-lag l1)
    (.set-param! ied :noise-amp2-src s2)
    (.set-param! ied :noise-amp2-depth d2)
    (.set-param! ied :noise-amp2-lag l2)
    (.sync-ui! ied)
    (.status! ied (format "Set random noise parameters"))))

(defn- ringmod-dice [ied]
  (let [carrier (* (math/coin 0.5 -1 1)(rand))
        modulator -1
        amp (rand-nth [0 0 0 0 0 -3 -3 -3 -6 -6 -9 -12])
        s1 (rand-nth (range 9))
        d1 (math/coin 0.75 0 (* (math/coin 0.5 -1 1)(rand)))
        l1 (math/coin 0.75 0 (rand))
        s2 (rand-nth (range 9))
        d2 (math/coin 0.75 0 (* (math/coin 0.5 -1 1)(rand)))
        l2 (math/coin 0.75 0 (rand))]
    (.set-param! ied :ringmod-carrier carrier)
    (.set-param! ied :ringmod-modulator modulator)
    (.set-param! ied :ringmod-amp amp)
    (.set-param! ied :ringmod-amp1-src s1)
    (.set-param! ied :ringmod-amp1-depth d1)
    (.set-param! ied :ringmod-amp1-lag l1)
    (.set-param! ied :ringmod-amp2-src s2)
    (.set-param! ied :ringmod-amp2-depth d2)
    (.set-param! ied :ringmod-amp2-lag l2)
    (.sync-ui! ied)
    (.status! ied (format "Set random noise parameters"))))

(defn- noise [ied]
  (let [enable-change-listener* (atom true)
        param-crackle :noise-param
        param-lp :noise-lp
        param-hp :noise-hp
        param-amp :noise-amp
        param-pan :noise-pan
        param-src1 :noise-amp1-src
        param-src2 :noise-amp2-src
        param-depth1 :noise-amp1-depth
        param-depth2 :noise-amp2-depth
        param-lag1 :noise-amp1-lag
        param-lag2 :noise-amp2-lag
        jb-init (factory/micro-button :reset "Reset noise parameters")
        jb-dice (factory/micro-button :dice "Set random noise parameters")
        jb-help (let [b (factory/micro-button :help "Alias noise help")]
                  (.putClientProperty b :topic :alias-noise)
                  (ss/listen b :action help/help-listener)
                  b)
        slide-crackle (factory/unit-slider param-crackle false)
        slide-lp (factory/slider param-lp 10.0 10000.0 
                                 (factory/slider-label-map "" "2.5k" "5k" "7.5k" "10.k"))
        slide-hp (factory/slider param-hp 10.0 10000.0 
                                 (factory/slider-label-map "" "2.5k" "5k" "7.5k" "10.k"))
        slide-amp (factory/mix-slider param-amp)
        slide-pan (factory/panner-slider param-pan)
        slide-depth1 (factory/unit-slider param-depth1 true)
        slide-depth2 (factory/unit-slider param-depth2 true)
        slide-lag1 (factory/unit-slider param-lag1 false)
        slide-lag2 (factory/unit-slider param-lag2 false)
        buspan1 (factory/matrix-toolbar param-src1 ied)
        buspan2 (factory/matrix-toolbar param-src2 ied)
        sliders [slide-crackle slide-lp slide-hp slide-amp slide-pan
                 slide-depth1 slide-depth2 slide-lag1 slide-lag2]
        pan-west (ss/vertical-panel :items [jb-init jb-dice jb-help]
                                    :border (factory/padding))
        pan-1 (ss/horizontal-panel 
               :items [(factory/slider-panel slide-crackle "Crackle")
                       (factory/slider-panel slide-lp "LP")
                       (factory/slider-panel slide-hp "HP")
                       (factory/slider-panel slide-amp "Amp")
                       (factory/slider-panel slide-pan "Pan")]
               :border (factory/padding))
        pan-2 (ss/horizontal-panel 
               :items [(:panel buspan1)
                       (factory/slider-panel slide-depth1 "Depth")
                       (factory/slider-panel slide-lag1 "Lag")]
               :border (factory/title "AM 1"))
        pan-3 (ss/horizontal-panel 
               :items [(:panel buspan2)
                       (factory/slider-panel slide-depth2 "Depth")
                       (factory/slider-panel slide-lag2 "Lag")]
               :border (factory/title "AM 2"))
        pan-main (ss/border-panel :west pan-west
                                  :center (ss/vertical-panel 
                                           :items [pan-1 pan-2 pan-3])
                  :border (factory/title "Noise"))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s [buspan1 buspan2]]
                   ((:syncfn s) data))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-crackle 0.0, 
                              param-lp 10000.0
                              param-hp 1.0
                              param-amp 0.0
                              param-pan 0.0
                              param-src1 bus-a, param-depth1 0.0 param-lag1 0.0
                              param-src2 bus-a, param-depth2 0.0 param-lag2 0.0}]
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
    (ss/listen jb-init :action (fn [_]
                                 (.working ied true)
                                  (SwingUtilities/invokeLater
                                   (proxy [Runnable][]
                                     (run []
                                      (let [data constants/initial-program-noise]
                                        (doseq [[p v] data]
                                          (.set-param! ied p v))
                                        (.sync-ui! ied)
                                        (.working ied false)
                                        (.status! ied "Noise source initialized")))))))
    (ss/listen jb-dice :action (fn [_]
                                 (.working ied true)
                                 (SwingUtilities/invokeLater
                                  (proxy [Runnable][]
                                    (run []
                                      (noise-dice ied)
                                      (.working ied false))))))
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- ringmod [ied]                       ; ringmodulator
  (let [enable-change-listener* (atom true)
        param-carrier :ringmod-carrier
        param-modulator :ringmod-modulator
        param-amp :ringmod-amp
        param-pan :ringmod-pan
        param-src1 :ringmod-amp1-src
        param-src2 :ringmod-amp2-src
        param-depth1 :ringmod-amp1-depth
        param-depth2 :ringmod-amp2-depth
        param-lag1 :ringmod-amp1-lag
        param-lag2 :ringmod-amp2-lag
        jb-init (factory/micro-button :reset "Reset ringmod parameters")
        jb-dice (factory/micro-button :dice "Set random ringmod parameters")
        jb-help (let [b (factory/micro-button :help "Alias ringmod help")]
                  (.putClientProperty b :topic :alias-ringmod)
                  (ss/listen b :action help/help-listener)
                  b)
        slide-carrier (factory/slider param-carrier -1.0 1.0
                                      (factory/slider-label-map "Osc1" "" "" "" "Osc2"))
        slide-modulator (factory/slider param-modulator -1.0 1.0
                                        (factory/slider-label-map "Osc3" "" "" "" "Noise"))
        slide-amp (factory/mix-slider param-amp)
        slide-pan (factory/panner-slider param-pan)
        slide-depth1 (factory/unit-slider param-depth1 true)
        slide-depth2 (factory/unit-slider param-depth2 true)
        slide-lag1 (factory/unit-slider param-lag1 false)
        slide-lag2 (factory/unit-slider param-lag2 false)
        sliders [slide-carrier slide-modulator slide-amp slide-pan 
                 slide-depth1 slide-depth2 slide-lag1 slide-lag2]
        buspan1 (factory/matrix-toolbar param-src1 ied)
        buspan2 (factory/matrix-toolbar param-src2 ied)
        pan-west (ss/vertical-panel :items [jb-init jb-dice jb-help]
                                    :border (factory/padding))
        pan-1 (ss/horizontal-panel 
               :items [(factory/slider-panel slide-carrier "Carrier")
                       (factory/slider-panel slide-modulator "Modulator")
                       (factory/slider-panel slide-amp "Amp")
                       (factory/slider-panel slide-pan "Pan")]
               :border (factory/padding))
        pan-2 (ss/horizontal-panel 
               :items [(:panel buspan1)
                       (factory/slider-panel slide-depth1 "Depth")
                       (factory/slider-panel slide-lag1 "Lag")]
               :border (factory/title "AM 1"))
        pan-3 (ss/horizontal-panel 
               :items [(:panel buspan2)
                       (factory/slider-panel slide-depth2 "Depth")
                       (factory/slider-panel slide-lag2 "Lag")]
               :border (factory/title "AM 2"))
        pan-main (ss/border-panel :west pan-west
                                  :center (ss/vertical-panel 
                                           :items [pan-1 pan-2 pan-3])
                                  :border (factory/title "Ring Modulator"))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s [buspan1 buspan2]]
                   ((:syncfn s) data))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-carrier -1.0 param-modulator -1.0
                              param-amp 0 param-pan 1.0
                              param-src1 bus-a param-src2 bus-a
                              param-depth1 0.0 param-lag1 0.0
                              param-depth2 0.0 param-lag2 0.0}]
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
    (ss/listen jb-init :action (fn [_]
                                 (.working ied true)
                                  (SwingUtilities/invokeLater
                                   (proxy [Runnable][]
                                     (run []
                                      (let [data constants/initial-program-ringmod]
                                        (doseq [[p v] data]
                                          (.set-param! ied p v))
                                        (.sync-ui! ied)
                                        (.working ied false)
                                        (.status! ied "Ringmod initialized")))))))
    (ss/listen jb-dice :action (fn [_]
                                 (.working ied true)
                                 (SwingUtilities/invokeLater
                                  (proxy [Runnable][]
                                    (run []
                                      (ringmod-dice ied)
                                      (.working ied false))))))
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn noise-editor [performance ied]
  (let [ned (noise ied)
        red (ringmod ied)
        pan-main (ss/scrollable 
                  (ss/horizontal-panel 
                   :items [(:pan-main ned)
                           (:pan-main red)
                           (Box/createHorizontalStrut 250)]))]
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
        (doseq [e [ned red]]
          ((:resetfn e))))
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [e [ned red]]
            ((:syncfn e) data)))))))

        
