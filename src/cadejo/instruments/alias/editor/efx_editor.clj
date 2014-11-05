(ns cadejo.instruments.alias.editor.efx-editor
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.MouseListener
           javax.swing.event.ChangeListener))

(def bus-con (:con constants/general-bus-map))
(def bus-a (:a constants/general-bus-map))
(def spinner-size [100 :by 24])

(defn- pitch-shifter [ied]
  (let [enable-change-listener* (atom true)
        param-ratio :pitchshift-ratio            ; 0 <= pr <= 4
        param-ratio-source :ratio-source         ; 
        param-ratio-depth :ratio-depth           ; -1.0 <= d <= 1.0
        param-pitch-dispersion :pitch-dispersion ; 0.0 <= pd <= 1.0
        param-time-dispersion :time-dispersion   ; 0.0 <= td <= 1.0
        param-mix :mix                           ; -99 <= mix <= 0 (db)
        spin-ratio (let [model (ss/spinner-model 0.0 :from 0.0 :to 4.0 :by 0.1)
                         s (ss/spinner :model model
                                       :size spinner-size)]
                     (.putClientProperty s :param param-ratio)
                     (.putClientProperty s :scale 1.0)
                     (.putClientProperty s :bias 0.0)
                     (.putClientProperty s :rvs-scale 1.0)
                     (.putClientProperty s :rvs-bias 0.0)
                     s)
        slide-depth (factory/slider param-ratio-depth -1.0 1.0
                                    (factory/signed-unit-label-map))
        slide-pd (factory/slider param-pitch-dispersion 0.0 1.0 true)
        slide-td (factory/slider param-time-dispersion 0.0 1.0 true)
        slide-mix (factory/mix-slider param-mix)
        buspan (factory/matrix-outbus-panel ied param-ratio-source)
        pan-ratio (ss/border-panel :north spin-ratio
                                   :center (ss/horizontal-panel 
                                            :items [(:panel buspan)
                                                    (factory/slider-panel slide-depth "Depth")])
                                   :border (factory/title "Ratio"))
        pan-dispersion (ss/horizontal-panel 
                        :items [(factory/slider-panel slide-pd "Pitch")
                                (factory/slider-panel slide-td "Time")]
                        :border (factory/title "Dispersion"))
        pan-mix (let [pan (factory/slider-panel slide-mix "Mix")]
                  (ss/config! pan :border (factory/title "Out"))
                  pan)
        pan-main (ss/horizontal-panel :items [pan-ratio 
                                              pan-dispersion 
                                              pan-mix]
                                      :border (factory/title "Pitch Shifter"))
        sliders [slide-depth slide-pd slide-td slide-mix]

        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan) data)
                 (.setValue spin-ratio (double (get data param-ratio 0.0)))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true)) 

        resetfn (fn []
                  (let [data {param-ratio 0.0
                              param-ratio-source bus-con
                              param-ratio-depth 0.0
                              param-pitch-dispersion 0.0
                              param-time-dispersion 0.0
                              param-mix -99.0}]
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
    (doseq [s (conj sliders spin-ratio)]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))


(defn- flanger [ied]
  (let [enable-change-listener* (atom true)
        param-mod-source :flanger-mod-source     ; 
        param-mod-depth :flanger-mod-depth       ; 0.1 <= d <= 1.0
        param-lfo-depth :flanger-lfo-depth       ; 0.0 <= d <= 1.0
        param-lfo-rate :flanger-lfo-rate         ; 0.01 <= r <= 15 
        param-feedback :flanger-feedback         ; -1.0 <= fb <= 1.0
        param-crossmix :flanger-crossmix         ; 0.0 <= m <= 1.0
        param-mix :flanger-mix                   ; -99 <= m <= 0 (db)
        buspan (factory/matrix-outbus-panel ied param-mod-source)
        spin-lfo-rate (let [m (ss/spinner-model 0.1 :from 0.001 :to 15.0 :by 0.1)
                            s (ss/spinner :model m 
                                          :size spinner-size)]
                        (.putClientProperty s :param param-lfo-rate)
                        (.putClientProperty s :scale 1.0)
                        (.putClientProperty s :bias 0.0)
                        (.putClientProperty s :rvs-scale 1.0)
                        (.putClientProperty s :rvs-bias 0.0)
                        s)
        slide-mod-depth (factory/slider param-mod-depth -1.0 1.0 
                                        (factory/signed-unit-label-map))
        slide-lfo-depth (factory/slider param-lfo-depth 0.0 1.0 true)
        slide-feedback (factory/slider param-feedback -1.0 1.0 
                                       (factory/signed-unit-label-map))
        slide-crossmix (factory/slider param-crossmix 0.0 1.0 true)
        slide-mix (factory/mix-slider param-mix)
        pan-1 (ss/border-panel 
               :north (ss/horizontal-panel 
                       :items [(ss/label :text "Rate ")
                               spin-lfo-rate])
               :center (ss/horizontal-panel 
                        :items [(:panel buspan)
                                (factory/slider-panel slide-mod-depth "Depth")
                                (factory/slider-panel slide-lfo-depth "LFO")
                                (factory/slider-panel slide-feedback "Feedback")
                                ])
               :border (factory/title "Modulation"))
        pan-2 (ss/horizontal-panel 
               :items [(factory/slider-panel slide-crossmix "Crossmix")
                       (factory/slider-panel slide-mix "Mix")]
               :border (factory/title "Out"))
        pan-main (ss/horizontal-panel :items [pan-1 pan-2]
                                      :border (factory/title "Flanger"))
        sliders [slide-mod-depth slide-lfo-depth slide-feedback slide-crossmix slide-mix]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan) data)
                 (.setValue spin-lfo-rate (double (get data param-lfo-rate 1.0)))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true)) 
        resetfn (fn []
                  (let [data {param-mod-source bus-con
                              param-mod-depth 0.0
                              param-lfo-depth 0.1
                              param-lfo-rate 0.1
                              param-feedback 0.5
                              param-crossmix 0.0
                              param-mix -99}]
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
    (doseq [s (conj sliders spin-lfo-rate)]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))

(defn- delayed [prefix ied]
  (let [enable-change-listener* (atom true)
        param (fn [suffix](keyword (format "echo%d-%s" prefix suffix)))
        param-delay-time (param "delay")          ; 0.0 <= time <= 2.0
        param-delay-source (param "delay-source") ;
        param-delay-depth (param "delay-depth")   ; -1.0 <= d <= 1.0
        param-feedback (param "feedback")         ; -1.0 <= fb <= 1.0
        param-damp (param "damp")                 ; 0.0 <= d <= 1.0
        param-pan (param "pan")                   ; -1.0 <= p 1.0
        param-amp-source (param "amp-source")     ;
        param-amp-depth (param "amp-depth")       ; -1.0 <= a <= 1.0
        param-mix (param "mix")                   ; -99 <= m <= 0 (db)
        spin-delay (let [m (ss/spinner-model 0.25 :from 0.0 :to 2.0 :by 0.05)
                         s (ss/spinner :model m
                                       :size spinner-size)]
                     (.putClientProperty s :param param-delay-time)
                     (.putClientProperty s :scale 1.0)
                     (.putClientProperty s :bias 0.0)
                     (.putClientProperty s :rvs-scale 1.0)
                     (.putClientProperty s :rvs-bias 0.0)
                     s)
        slide-delay-depth (factory/slider param-delay-depth -1.0 1.0
                                          (factory/signed-unit-label-map))
        slide-feedback (factory/slider param-feedback -1.0 1.0
                                       (factory/signed-unit-label-map))
        slide-damp (factory/slider param-damp 0.0 1.0
                                   (factory/unsigned-unit-label-map))
        slide-pan (factory/slider param-pan -1.0 1.0
                                  (factory/signed-unit-label-map))
        slide-amp-depth (factory/slider param-amp-depth -1.0 1.0
                                        (factory/signed-unit-label-map))
        slide-mix (factory/mix-slider param-mix)
        buspan-delay (factory/matrix-outbus-panel ied param-delay-source)
        buspan-amp (factory/matrix-outbus-panel ied param-amp-source)
        pan-1 (ss/border-panel 
               :north (ss/horizontal-panel :items [(ss/label "Time ")
                                                   spin-delay])
               :center (ss/horizontal-panel 
                        :items [(:panel buspan-delay)
                                (factory/slider-panel slide-delay-depth "Depth")
                                (factory/slider-panel slide-feedback "Feedback")
                                (factory/slider-panel slide-damp "Damp")])
               :border (factory/title "Delay Time"))
        pan-2 (ss/horizontal-panel 
               :items [(:panel buspan-amp)
                       (factory/slider-panel slide-amp-depth "Depth")
                       (factory/slider-panel slide-pan "Pan")
                       (factory/slider-panel slide-mix "Mix")]
               :border (factory/title "Out"))
        pan-main (ss/horizontal-panel
                  :items [pan-1 pan-2]
                  :border (factory/title (format "Delay %s" prefix)))
        sliders [slide-delay-depth slide-feedback slide-damp slide-pan
                 slide-amp-depth slide-mix]

        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 ((:syncfn buspan-delay) data)
                 ((:syncfn buspan-amp) data)
                 (.setValue spin-delay (double (get data param-delay-time 1.0)))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (reset! enable-change-listener* true)) 

        resetfn (fn []
                  (let [data {param-delay-time 1.0
                              param-delay-source bus-con
                              param-delay-depth 0.0
                              param-feedback 0.0
                              param-damp 0.0
                              param-pan 0.0
                              param-amp-source bus-con
                              param-amp-depth 0.0
                              param-mix -99.0}]
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
    (doseq [s (conj sliders spin-delay)]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn
     :resetfn resetfn}))
               

(defn efx-editor [performance ied]
  (let [shifter (pitch-shifter ied)
        flang (flanger ied)
        dly1 (delayed 1 ied)
        dly2 (delayed 2 ied)
        pan-1 (ss/vertical-panel :items [(:pan-main shifter)
                                         (:pan-main flang)])
        pan-2 (ss/vertical-panel :items [(:pan-main dly1)
                                         (:pan-main dly2)])
        pan-main (ss/scrollable 
                  (ss/horizontal-panel :items [pan-1 pan-2]))
        widget-map {:pan-main pan-main}]
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
        (doseq [e [shifter flang dly1 dly2]]
          ((:resetfn e))))
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [e [shifter flang dly1 dly2]]
            ((:syncfn e) data)))))))
