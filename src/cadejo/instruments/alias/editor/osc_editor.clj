(ns cadejo.instruments.alias.editor.osc-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))

(def ^:private west-pan-size [130 :by 160])
(def ^:private icon-inc1 (lnf/read-icon :mini :up1))
(def ^:private icon-inc2 (lnf/read-icon :mini :up2))
(def ^:private icon-dec1 (lnf/read-icon :mini :down1))
(def ^:private icon-dec2 (lnf/read-icon :mini :down2))
(def ^:private aux-panel-size [450 :by 465]) 

(def bus-con (:con constants/general-bus-map))
(def bus-a (:a constants/general-bus-map))

(defn- detune-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-detune (keyword (format "osc%d-detune" prefix))
        param-bias (keyword (format "osc%d-bias" prefix))
        spinner-size [100 :by 36]
        spin-detune (let [model (ss/spinner-model 1.0 
                                                  :from 0.0
                                                  :to 32.000
                                                  :by 0.001)
                          s (ss/spinner :model model
                                        :size spinner-size)]
                      (.putClientProperty s :param param-detune)
                      s)
        spin-bias (let [model (ss/spinner-model 0.0
                                                 :from -4000.0
                                                 :to 4000.0
                                                 :by 1.0)
                         s (ss/spinner :model model
                                       :size spinner-size)]
                     (.putClientProperty s :param param-bias)
                     s)
        jb-inc1 (let [b (ss/button :icon icon-inc1 :size [18 :by 18])]
                  (ss/listen b :action
                             (fn [_]
                               (let [v (.getValue spin-detune)]
                                 (.setValue spin-detune (min 32 (inc v))))))
                  b)
        jb-inc2 (let [b (ss/button :icon icon-inc2 :size [18 :by 18])]
                  (ss/listen b :action (fn [_]
                                         (let [v1 (.getValue spin-detune)
                                               v2 (min 32 (* 2 v1))]
                                           (if (= v2 (* 2 v1))
                                             (.setValue spin-detune v2)))))
                  b)
        jb-dec1 (let [b (ss/button :icon icon-dec1 :size [18 :by 18])]
                  (ss/listen b :action
                             (fn [_]
                               (let [v (.getValue spin-detune)]
                                 (.setValue spin-detune (max 0 (dec v))))))
                  b)
        jb-dec2 (let [b (ss/button :icon icon-dec2 :size [18 :by 18])]
                  (ss/listen b :action
                             (fn [_]
                               (let [v1 (.getValue spin-detune)
                                     v2 (/ v1 2.0)]
                                 (.setValue spin-detune v2))))
                  b)
        pan-inc1 (ss/vertical-panel :items [jb-inc1 jb-dec1])   ;; linear +-1
        pan-inc2 (ss/vertical-panel :items [jb-inc2 jb-dec2])   ;; By octaves
        pan-detune (ss/horizontal-panel :items [spin-detune
                                                pan-inc1 pan-inc2]
                                        :border (factory/title "Detune"))
        pan-bias (ss/horizontal-panel :items [spin-bias]
                                      :border (factory/title "Bias"))
        pan-main (ss/horizontal-panel :items [pan-detune pan-bias])
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (let [dt (double (get data param-detune 1.0))
                       bias (double (get data param-bias 0.0))]
                 (.setValue spin-detune dt)
                 (.setValue spin-bias bias)
                 (reset! enable-change-listener* true)))
        resetfn (fn []
                  (.setValue spin-detune 1.0)
                  (.setValue spin-bias 0.0))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    val (float (.getValue src))
                                    param (.getClientProperty src :param)]
                                (.set-param! ied param val)))))]
    (.addChangeListener spin-detune change-listener)
    (.addChangeListener spin-bias change-listener)
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn
     :id :detune-editor}))


(defn- fm-editor [prefix ied jb-reset]
  (let [enable-change-listener* (atom true)
        param-fm1-source (keyword (format "osc%d-fm1-source" prefix))
        param-fm1-depth (keyword (format "osc%d-fm1-depth" prefix))
        param-fm1-lag (keyword (format "osc%d-fm1-lag" prefix))
        param-fm2-source (keyword (format "osc%d-fm2-source" prefix))
        param-fm2-depth (keyword (format "osc%d-fm2-depth" prefix))
        param-fm2-lag (keyword (format "osc%d-fm2-lag" prefix))
        ;; FM1 panel
        buspanel-fm1 (factory/matrix-outbus-panel ied param-fm1-source)
        slide-fm1-depth (factory/slider param-fm1-depth -1.0 1.0
                                         (factory/signed-unit-label-map))
        slide-fm1-lag (factory/slider param-fm1-lag 0.0 1.0 nil)
        pan-fm1 (ss/border-panel
                 :center (ss/horizontal-panel
                          :items [(factory/slider-panel slide-fm1-depth "Depth")
                                  (factory/slider-panel slide-fm1-lag "Lag")])
                 :west (:panel buspanel-fm1)
                 :border (factory/title "FM 1"))
        ;; FM2 panel
        buspanel-fm2 (factory/matrix-outbus-panel ied param-fm2-source)
        slide-fm2-depth (factory/slider param-fm2-depth -1.0 1.0
                                         (factory/signed-unit-label-map))
        slide-fm2-lag (factory/slider param-fm2-lag 0.0 1.0 nil)
        pan-fm2 (ss/border-panel
                 :center (ss/horizontal-panel
                          :items [(factory/slider-panel slide-fm2-depth "Depth")
                                  (factory/slider-panel slide-fm2-lag "Lag")])
                                 :west (:panel buspanel-fm2)
                                 :border (factory/title "FM 2"))
        pan-fm-west (ss/flow-panel :items [jb-reset]
                                   :size west-pan-size)
        pan-fm (ss/border-panel :west pan-fm-west
                                :center (ss/horizontal-panel
                                         :items [pan-fm1 pan-fm2]))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspanel-fm1) data)
                 ((:syncfn buspanel-fm2) data)
                 (factory/sync-slider slide-fm1-depth data)
                 (factory/sync-slider slide-fm2-depth data)
                 (factory/sync-slider slide-fm1-lag data)
                 (factory/sync-slider slide-fm2-lag data)
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-fm1-source bus-a,
                              param-fm1-depth 0.0,
                              param-fm1-lag 0.0,
                              param-fm2-source bus-a,
                              param-fm2-depth 0.0,
                              param-fm2-lag 0.0}]
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
    (.addChangeListener slide-fm1-depth change-listener)
    (.addChangeListener slide-fm2-depth change-listener)
    (.addChangeListener slide-fm1-lag change-listener)
    (.addChangeListener slide-fm2-lag change-listener)
    {:pan-main pan-fm
     :syncfn syncfn
     :resetfn resetfn
     :id :fm-editor}))

(defn- wave-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-wave (keyword (format "osc%d-wave" prefix))
        param-wave1-source (keyword (format "osc%d-wave1-source" prefix))
        param-wave1-depth (keyword (format "osc%d-wave1-depth" prefix))
        param-wave2-source (keyword (format "osc%d-wave2-source" prefix))
        param-wave2-depth (keyword (format "osc%d-wave2-depth" prefix))
        slide-wave (let [[p0 p25 p50 p75 p100]
                         (cond (= prefix 1)
                               ["Saw" "" "" "" "Sync"]
                               (= prefix 2)
                               ["Pls" "" "Sqr" "" "Pls"]
                               (= prefix 3)
                               ["Sin" "" "" "" "Saw"]
                               :default
                               ["?" "?" "?" "?" "?"])
                         s (factory/slider param-wave 0.0 1.0 
                                            (factory/slider-label-map
                                             p0 p25 p50 p75 p100))]
                     s)
        buspanel-wave1 (factory/matrix-outbus-panel ied param-wave1-source)
        slide-wave1-depth (factory/slider param-wave1-depth -1.0 1.0
                                           (factory/signed-unit-label-map))
        buspanel-wave2 (factory/matrix-outbus-panel ied param-wave2-source)
        slide-wave2-depth (factory/slider param-wave2-depth -1.0 1.0
                                           (factory/signed-unit-label-map))
        pan-w1 (ss/border-panel
                :center (factory/slider-panel slide-wave1-depth "Depth")
                :west (:panel buspanel-wave1)
                :border (factory/title "Wave 1"))
        pan-w2 (ss/border-panel 
                :center (factory/slider-panel slide-wave2-depth "Depth")
                :west (:panel buspanel-wave2)
                :border (factory/title "Wave 2"))
        pan-wave-west (ss/horizontal-panel
                       :items [(Box/createHorizontalStrut 32)
                               (factory/slider-panel slide-wave "Wave")]
                       :size west-pan-size)
        pan-wave (ss/border-panel
                  :west pan-wave-west
                  :center (ss/horizontal-panel :items [pan-w1 pan-w2]))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspanel-wave1) data)
                 ((:syncfn buspanel-wave2) data)
                 (factory/sync-slider slide-wave data)
                 (factory/sync-slider slide-wave1-depth data)
                 (factory/sync-slider slide-wave2-depth data)
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-wave (get {1 0.0, 2 0.5, 3 0.5} prefix),
                              param-wave1-source bus-a
                              param-wave1-depth 0.0
                              param-wave2-source bus-a
                              param-wave2-depth 0.0}]
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
    (.addChangeListener slide-wave change-listener)
    (.addChangeListener slide-wave1-depth change-listener)
    (.addChangeListener slide-wave2-depth change-listener)
    {:pan-main pan-wave
     :resetfn resetfn
     :syncfn syncfn}))


(defn- amp-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-amp (keyword (format "osc%d-amp" prefix))
        param-amp1-source (keyword (format "osc%d-amp1-src" prefix))
        param-amp1-depth (keyword (format "osc%d-amp1-depth" prefix))
        param-amp1-lag (keyword (format "osc%d-amp1-lag" prefix))
        param-amp2-source (keyword (format "osc%d-amp2-src" prefix))
        param-amp2-depth (keyword (format "osc%d-amp2-depth" prefix))
        param-amp2-lag (keyword (format "osc%d-amp2-lag" prefix))
        param-pan (keyword (format "osc%d-pan" prefix))
        slide-amp (factory/mix-slider param-amp)
        slide-pan (factory/slider param-pan -1.0 1.0 (factory/pan-label-map))
        buspanel-amp1 (factory/matrix-outbus-panel ied param-amp1-source)
        slide-amp1-depth (factory/slider param-amp1-depth -1.0 1.0
                                          (factory/signed-unit-label-map))
        slide-amp1-lag (factory/slider param-amp1-lag 0.0 1.0 nil)
        buspanel-amp2 (factory/matrix-outbus-panel ied param-amp2-source)
        slide-amp2-depth (factory/slider param-amp2-depth -1.0 1.0
                                          (factory/signed-unit-label-map))
        slide-amp2-lag (factory/slider param-amp2-lag 0.0 1.0 nil)
        pan-a1 (ss/border-panel
                :center (ss/horizontal-panel
                         :items [(factory/slider-panel slide-amp1-depth "Depth")
                                 (factory/slider-panel slide-amp1-lag "Lag")])
                :west (:panel buspanel-amp1)
                :border (factory/title "Amp 1"))
        pan-a2 (ss/border-panel
                :center (ss/horizontal-panel
                         :items [(factory/slider-panel slide-amp2-depth "Depth")
                                 (factory/slider-panel slide-amp2-lag "Lag")])
                :west (:panel buspanel-amp2)
                :border (factory/title "Amp 2"))
        pan-amp-west (ss/horizontal-panel 
                      :items [(Box/createHorizontalStrut 4)
                              (factory/slider-panel slide-amp "Amp")
                              (Box/createHorizontalStrut 4)
                              (factory/slider-panel slide-pan "Pan")]
                      :size west-pan-size)
        pan-amp (ss/border-panel 
                 :west pan-amp-west
                 :center (ss/horizontal-panel :items [pan-a1 pan-a2]))
        sliders [slide-amp slide-pan 
                 slide-amp1-depth slide-amp1-lag
                 slide-amp2-depth slide-amp2-lag]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspanel-amp1) data)
                 ((:syncfn buspanel-amp2) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true))
        resetfn (fn []
                  (let [data {param-amp 0.333
                              param-pan 0.5
                              param-amp1-source bus-a
                              param-amp1-depth 0.0
                              param-amp1-lag 0.0
                              param-amp2-source bus-a
                              param-amp2-depth 0.0
                              param-amp2-lag 0.0}]
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
    {:pan-main pan-amp
     :resetfn resetfn
     :syncfn syncfn}))


(defn- osced [prefix performance ied]
  (let [jb-reset (factory/button "Reset" :general :reset
                                 "Reset oscillator parameters")
        dted (detune-editor prefix ied)
        fmed (fm-editor prefix ied jb-reset)
        waveed (wave-editor prefix ied)
        amped (amp-editor prefix ied)
        pan-main (ss/scrollable 
                  (ss/horizontal-panel 
                   :items [(ss/border-panel :north (:pan-main dted)
                                            :center (ss/vertical-panel 
                                                     :items [(:pan-main fmed)
                                                             (:pan-main waveed)
                                                             (:pan-main amped)])
                                            :border (factory/title (format "OSC %s" prefix)))
                           (Box/createHorizontalStrut 250)]))
        syncfn (fn [data]
                 (doseq [se [dted fmed waveed amped]]
                   (let [sfn (:sync-fn se)]
                     (sfn data))))
        widget-map {:pan-main pan-main}]
    (ss/listen jb-reset
               :action (fn [_]
                         (doseq [se [dted fmed waveed amped]]
                           ((:resetfn se))
                           (let [d (.current-data (.bank performance))]
                             ((:syncfn se) d)))
                         (.status! ied (format "OSC %d reset" prefix))))
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
        (doseq [e [dted fmed waveed amped]]
          ((:resetfn e)))
        (.status! this (format "OSC %s reset" prefix)))
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [e [dted fmed waveed amped]]
            ((:syncfn e) data)))))))

(defn osc1-editor [performance ied]
  (osced 1 performance ied))

(defn osc2-editor [performance ied]
  (osced 2 performance ied))

(defn osc3-editor [performance ied]
  (osced 3 performance ied))

