(ns cadejo.instruments.alias.editor.noise-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))  

(def bus-con (:con constants/general-bus-map))
(def bus-a (:a constants/general-bus-map))

(defn- noise [ied]
  (let [enable-change-listener* (atom true)
        param-crackel :noise-param
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
        slide-crackle (factory/unit-slider param-crackel false)
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
        buspan1 (factory/matrix-outbus-panel ied param-src1)
        buspan2 (factory/matrix-outbus-panel ied param-src2)
        sliders [slide-crackle slide-lp slide-hp slide-amp slide-pan
                 slide-depth1 slide-depth2 slide-lag1 slide-lag2]
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
        pan-main (ss/vertical-panel 
                  :items [pan-1 pan-2 pan-3]
                  :border (factory/title "Noise"))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s [buspan1 buspan2]]
                   ((:syncfn s) data))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-crackel 0.0, 
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
        buspan1 (factory/matrix-outbus-panel ied param-src1)
        buspan2 (factory/matrix-outbus-panel ied param-src2)

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
        pan-main (ss/vertical-panel 
                  :items [pan-1 pan-2 pan-3]
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

        
