(ns cadejo.instruments.alias.editor.filter-editor
   (:require [cadejo.config :as config])
   (:require [cadejo.instruments.alias.constants :as constants])
   (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
   (:require [cadejo.ui.instruments.subedit :as subedit])
   (:require [cadejo.ui.util.lnf :as lnf])
   (:require [seesaw.core :as ss])
   (:import javax.swing.Box
            javax.swing.event.ChangeListener))

(def ^:private icon-inc1 (lnf/read-icon :mini :up1))
(def ^:private icon-inc2 (lnf/read-icon :mini :up2))
(def ^:private icon-dec1 (lnf/read-icon :mini :down1))
(def ^:private icon-dec2 (lnf/read-icon :mini :down2))


(defn- distortion-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-pregain (keyword (format "distortion%d-pregain" prefix))
        param-clip (keyword (format "distortion%d-param" prefix))
        param-source (keyword (format "distortion%d-source" prefix))
        param-depth (keyword (format "distortion%d-depth" prefix))
        param-mix (keyword (format "distortion%d-mix" prefix))
        buspan (factory/matrix-outbus-panel ied param-source)
        slide-pregain (factory/slider param-pregain 1.0 16.0 true)
        slide-clip (factory/slider param-clip 0.0 1.0 true)
        slide-depth (factory/slider param-depth -1.0 1.0 
                                    (factory/signed-unit-label-map))
        slide-mix (factory/slider param-mix -1.0 1.0
                                  (factory/slider-label-map "Dry" "" "1/2" "" "Wet"))
        sliders [slide-pregain slide-clip slide-depth slide-mix]
        pan-west (ss/horizontal-panel 
                  :items [(factory/slider-panel slide-pregain "Pregain")
                          (factory/slider-panel slide-clip "Clip")]
                  :size [130 :by 200])
        pan-center (ss/horizontal-panel 
                    :items [(:panel buspan)
                            (factory/slider-panel slide-depth "Depth")
                            (factory/slider-panel slide-mix "Mix")]
                    :size [130 :by 200])
        pan-main (ss/border-panel :west pan-west
                                  :center pan-center
                                  :border (factory/title "Distortion"))
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

;; (defn- mode-editor [ied]
;;   (let [enable-change-listener* (atom true)
;;         param-mode :filter1-mode
;;         slide-mode (factory/slider param-mode 0.0 1.0
;;                                    (factory/slider-label-map 
;;                                     "Low" "Low*High" "High" "Band" "Bypass"))
;;         pan-main (factory/slider-panel slide-mode "Mode")
;;         syncfn (fn [data]
;;                  (reset! enable-change-listener* false)
;;                  (factory/sync-slider slide-mode data)
;;                  (reset! enable-change-listener* true))
;;         resetfn (fn []
;;                   (doseq [[p v]{param-mode 0.0}]
;;                     (.set-param! ied p v)))
;;         change-listener (proxy [ChangeListener][]
;;                           (stateChanged [ev]
;;                             (if @enable-change-listener*
;;                               (let [src (.getSource ev)
;;                                     param (.getClientProperty src :param)
;;                                     scale (.getClientProperty src :scale)
;;                                     bias (.getClientProperty src :bias)
;;                                     pos (.getValue src)
;;                                     val (float (+ bias (* scale pos)))]
;;                                 (.set-param! ied param val)))))]
;;     (.addChangeListener slide-mode change-listener)
;;     {:pan-main pan-main
;;      :syncfn syncfn
;;      :resetfn resetfn}))

(defn- mode-editor [ied]
  (let [param-mode :filter1-mode
        grp (ss/button-group)
        jb-low (factory/toggle "Lowpass" :filter :low "Lowpass" grp)
        jb-notch (factory/toggle "Low/High" :filter :notch "Ringmod low and high pass filters" grp)
        jb-high (factory/toggle "Highpass" :filter :high "Highpass" grp)
        jb-band (factory/toggle "Bandpass" :filter :band "Bandpass"grp)
        jb-bypass (factory/toggle "Bypass" :filter :none "Bypass" grp)
        pan-main (ss/grid-panel 
                  :columns 1
                  :items [jb-bypass jb-band jb-high jb-notch jb-low])

        syncfn (fn [data]
                 (println "ISSUE: filter mode-editor.syncfn not implemented")
                 )
        resetfn (fn []
                  )
        ]
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
        spinner-size [100 :by 36]
        spin-freq (let [model (ss/spinner-model 1000.0 :from 10.0 :to 10000.0 :by 100.0)
                        s (ss/spinner :model model
                                      :size spinner-size)]
                    (.putClientProperty s :param param-freq)
                    (.putClientProperty s :scale 1.0)
                    (.putClientProperty s :rvs-scale 1.0)
                    (.putClientProperty s :bias 0.0)
                    (.putClientProperty s :rvs-bias 0.0)
                    s)
        slide-depth1 (factory/slider param-depth1 -1.0 1.0 
                                     (factory/signed-unit-label-map))
        slide-depth2 (factory/slider param-depth2 -1.0 1.0 
                                     (factory/signed-unit-label-map))
        buspan1 (factory/matrix-outbus-panel ied param-source1)
        buspan2 (factory/matrix-outbus-panel ied param-source2)
        pan-1 (ss/horizontal-panel :items [(:panel buspan1)
                                           (factory/slider-panel slide-depth1 "Depth")]
                                   :border (factory/title "FM 1"))
        pan-2 (ss/horizontal-panel :items [(:panel buspan2)
                                           (factory/slider-panel slide-depth2 "Depth")]
                                   :border (factory/title "FM 2"))
        pan-main (ss/border-panel :north spin-freq
                                  :center (ss/horizontal-panel 
                                           :items [pan-1 pan-2])
                                  :border (factory/title "Freq"))
        sliders [slide-depth1 slide-depth2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan1) data)
                 ((:syncfn buspan2) data)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (.setValue spin-freq (double (get data param-freq 10000)))
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
    (.addChangeListener spin-freq change-listener)
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- res-editor [prefix ied]
  (let [enable-change-listener* (atom true)
        param-res (keyword (format "filter%d-res" prefix))
        param-source (keyword (format "filter%d-res-source" prefix))
        param-depth (keyword (format "filter%d-res-depth" prefix))
        buspan (factory/matrix-outbus-panel ied param-source)
        slide-res (factory/slider param-res 0.0 1.0 
                                  (factory/unsigned-unit-label-map))
        slide-depth (factory/slider param-depth -1.0 1.0 
                                    (factory/signed-unit-label-map))
        pan-main (ss/horizontal-panel
                  :items [(factory/slider-panel slide-res "Res")
                          (:panel buspan)
                          (factory/slider-panel slide-depth "Depth")]
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
        buspan (factory/matrix-outbus-panel ied param-source)
        slide-pan (factory/slider param-pan -1.0 1.0 
                                  (factory/signed-unit-label-map))
        slide-depth (factory/slider param-depth -1.0 1.0 
                                    (factory/signed-unit-label-map))
        slide-gain (factory/slider param-gain 0.0 1.0
                                   (factory/unsigned-unit-label-map))
        pan-main (ss/horizontal-panel
                  :items [(factory/slider-panel slide-pan "Pan")
                          (:panel buspan)
                          (factory/slider-panel slide-depth "Depth")
                          (factory/slider-panel slide-gain "Post gain")]
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
        
(defn filter-editor [prefix performance ied]
  (let [dst (distortion-editor prefix ied)
        mode (if (= prefix 1)
               (mode-editor ied)
               nil)
        freq (freq-editor prefix ied)
        res (res-editor prefix ied)
        out (pan-editor prefix ied)
        pan-north (ss/horizontal-panel 
                   :items [(:pan-main dst)
                           (if mode (:pan-main mode)(Box/createHorizontalStrut 8))
                           (:pan-main freq)])
        pan-south (ss/horizontal-panel
                   :items [(:pan-main res)
                           (:pan-main out)])
        pan-main (ss/vertical-panel
                  :items [pan-north pan-south]
                  :border (factory/title (format "Filter %d" prefix)))
        widget-map {:pan-main pan-main}]
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

        
        

        
