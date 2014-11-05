(ns cadejo.instruments.alias.editor.mixer-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))

(defn- filter-in [ied]
  (let [enable-change-listener* (atom true)
        param-a1 :osc1-amp
        param-a2 :osc2-amp
        param-a3 :osc3-amp
        param-a4 :noise-amp
        param-a5 :ringmod-amp
        param-p1 :osc1-pan
        param-p2 :osc2-pan
        param-p3 :osc3-pan
        param-p4 :noise-pan
        param-p5 :ringmod-pan
        slide-a1 (factory/mix-slider param-a1)
        slide-a2 (factory/mix-slider param-a2)
        slide-a3 (factory/mix-slider param-a3)
        slide-a4 (factory/mix-slider param-a4)
        slide-a5 (factory/mix-slider param-a5)
        panner-labels (fn [](factory/slider-label-map 
                          "F1" "" "1/2" "" "F2"))
        slide-p1 (factory/slider param-p1 -1.0 1.0 (panner-labels))
        slide-p2 (factory/slider param-p2 -1.0 1.0 (panner-labels))
        slide-p3 (factory/slider param-p3 -1.0 1.0 (panner-labels))
        slide-p4 (factory/slider param-p4 -1.0 1.0 (panner-labels))
        slide-p5 (factory/slider param-p5 -1.0 1.0 (panner-labels))
        sliders-amp [slide-a1 slide-a2 slide-a3 slide-a4 slide-a5]
        sliders-pan [slide-p1 slide-p2 slide-p3 slide-p4 slide-p5]
        sliders (flatten (merge sliders-amp sliders-pan))
       
        pan-amp (ss/grid-panel 
                 :rows 1 
                 :items [(factory/slider-panel slide-a1 "Osc 1")
                         (factory/slider-panel slide-a2 "Osc 2")
                         (factory/slider-panel slide-a3 "Osc 3")
                         (factory/slider-panel slide-a4 "Noise")
                         (factory/slider-panel slide-a5 "Ring mod")]
                 :border (factory/title "Mix"))
        pan-pan (ss/grid-panel 
                 :rows 1 
                 :items [(factory/slider-panel slide-p1 "Osc 1")
                         (factory/slider-panel slide-p2 "Osc 2")
                         (factory/slider-panel slide-p3 "Osc 3")
                         (factory/slider-panel slide-p4 "Noise")
                         (factory/slider-panel slide-p5 "Ring mod")]
                 :border (factory/title "Pan"))
        pan-main (ss/scrollable
                  (ss/grid-panel :columns 1
                                 :items [pan-amp pan-pan]
                                 :border (factory/title "Filter Input")))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
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
    {:pan-main pan-main
     :syncfn syncfn}))


(defn- filter-out [ied]
  (let [enable-change-listener* (atom true)
        param-gain1 :filter1-postgain
        param-gain2 :filter2-postgain
        param-pan1 :filter1-pan
        param-pan2 :filter2-pan
        slide-gain1 (factory/slider param-gain1 0.0 1.0 (factory/unsigned-unit-label-map))
        slide-gain2 (factory/slider param-gain2 0.0 1.0 (factory/unsigned-unit-label-map))
        slide-pan1 (factory/slider param-pan1 -1.0 1.0 
                                   (factory/slider-label-map "L" "" "1/2" "" "R"))
        slide-pan2 (factory/slider param-pan2 -1.0 1.0 
                                   (factory/slider-label-map "L" "" "1/2" "" "R"))
        pan-gain1 (factory/slider-panel slide-gain1 "1")
        pan-gain2 (factory/slider-panel slide-gain2 "2")
        pan-pan1 (factory/slider-panel slide-pan1 "1")
        pan-pan2 (factory/slider-panel slide-pan2 "2")
        pan-amp (ss/grid-panel :rows 1 
                               :items [pan-gain1 pan-gain2]
                               :border (factory/title "Mix"))
        pan-pan (ss/grid-panel :rows 1 
                               :items [pan-pan1 pan-pan2]
                               :border (factory/title "Pan"))
        pan-main (ss/vertical-panel :items [pan-amp pan-pan]
                                    :border (factory/title "Filter Out"))
        sliders [slide-gain1 slide-gain2 slide-pan1 slide-pan2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
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
    {:pan-main pan-main
     :syncfn syncfn}))        


(defn- efx-out [ied]
  (let [enable-change-listener* (atom true)
        param-pitchshifter :pitchshift-mix
        param-flanger :flanger-mix
        param-echo1 :echo1-mix
        param-echo2 :echo2-mix
        param-drysig :dry-mix
        param-echo1-pan :echo1-pan
        param-echo2-pan :echo2-pan
        slide-shift (factory/mix-slider param-pitchshifter)
        slide-flanger (factory/mix-slider param-flanger)
        slide-echo1 (factory/mix-slider param-echo1)
        slide-echo2 (factory/mix-slider param-echo2)
        slide-dry (factory/mix-slider param-drysig)
        slide-pan-echo1 (factory/slider param-echo1-pan -1.0 1.0
                                        (factory/slider-label-map "L" "" "1/2" "" "R"))
        slide-pan-echo2 (factory/slider param-echo2-pan -1.0 1.0
                                        (factory/slider-label-map "L" "" "1/2" "" "R"))
        pan-mix (ss/grid-panel :rows 1
                               :items [(factory/slider-panel slide-shift "Pitch Shifter")
                                       (factory/slider-panel slide-flanger "Flanger")
                                       (factory/slider-panel slide-echo1 "Delay 1")
                                       (factory/slider-panel slide-echo2 "Delay 2")
                                       (factory/slider-panel slide-dry "Drysig")]
                               :border (factory/title "Mix"))
        pan-pan (ss/grid-panel :rows 1
                               :items [(Box/createHorizontalStrut 1)
                                       (Box/createHorizontalStrut 1)
                                       (factory/slider-panel slide-pan-echo1 "Delay 1")
                                       (factory/slider-panel slide-pan-echo2 "Delay 2")
                                       (Box/createHorizontalStrut 1)]
                               :border (factory/title "Pan"))
        pan-main (ss/grid-panel :columns 1
                                :items [pan-mix pan-pan]
                                :border (factory/title "Efx"))
        sliders [ slide-shift slide-flanger slide-echo1 slide-echo2 
                 slide-pan-echo1 slide-pan-echo2] 
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
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
    {:pan-main pan-main
     :syncfn syncfn}))

(defn- common [ied]
  (let [enable-change-listener* (atom false)
        param-port-time :port-time
        param-amp :amp
        param-cc7 :cc7->volume
        slide-port (factory/slider param-port-time 0.0 1.0 true)
        slide-cc7 (factory/slider param-cc7 0.0 1.0 true)
        slide-amp (factory/slider param-amp 0.0 1.0 true)
        pan-port (factory/slider-panel slide-port "Portamento Time")
        pan-cc7 (factory/slider-panel slide-cc7 "MIDI Volume")
        pan-amp (factory/slider-panel slide-amp "Master Volume")
        pan-main (ss/grid-panel :rows 1
                                :items [pan-port
                                        (Box/createHorizontalStrut 1)
                                        pan-cc7
                                        pan-amp]
                                :border (factory/title "Common"))
        sliders [slide-port slide-cc7 slide-amp]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
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
    {:pan-main pan-main
     :syncfn syncfn}))

(defn mixer [performance ied]
  (let [pre (filter-in ied)
        post (filter-out ied)
        efx (efx-out ied)
        com (common ied)
        subed [pre post efx com]
        pan-north (ss/horizontal-panel :items [(:pan-main pre)
                                               (:pan-main post)
                                               (:pan-main efx)]
                                       :border (factory/title "Mixer"))
        pan-south (ss/horizontal-panel :items [(:pan-main com)]
                                       :border (factory/title "Common"))
        pan-main (ss/vertical-panel :items [pan-north pan-south])
        widge-map {:pan-main pan-main}]
    (reify subedit/InstrumentSubEditor
      (widgets [this] {:pan-main pan-main})
      (widget [this key](get (.widgets this) key))
      (parent [this] ied)
      (parent! [this _] ied) ;; ignore
      (status! [this txt] (.status! ied txt))
      (warning! [this txt](.warning! ied txt))
      (set-param! [this p v]
        (.set-param! ied p v))
      (init! [this])
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [e subed]
            ((:syncfn e) data)))))))
