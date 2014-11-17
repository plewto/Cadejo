(ns cadejo.instruments.alias.editor.lfo-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defn- lfoed [prefix ied]
  (let [enable-change-listener* (atom true)
        param (fn [suffix](keyword (format "lfo%d-%s" prefix suffix)))
        param-freq1-source (param "freq1-source")
        param-freq1-depth (param "freq1-depth")
        param-freq2-source (param "freq2-source")
        param-freq2-depth (param "freq2-depth")
        param-wave1-source (param "wave1-source")
        param-wave1-depth (param "wave1-depth")
        param-wave2-source (param "wave2-source")
        param-wave2-depth (param "wave2-depth")
        spin-freq1 (let [s (ss/spinner :model (ss/spinner-model 7.0 :from -20.0 :to 20.0 :by 0.05))]
                     (.putClientProperty s :param param-freq1-depth)
                     (.putClientProperty s :scale 1.0)
                     (.putClientProperty s :bias 0.0)
                     (.putClientProperty s :rvs-scale 1.0)
                     (.putClientProperty s :rvs-bias 0.0)
                     s)
        spin-freq2 (let [s (ss/spinner :model (ss/spinner-model 7.0 :from -20.0 :to 20.0 :by 0.05))]
                     (.putClientProperty s :param param-freq2-depth)
                     (.putClientProperty s :scale 1.0)
                     (.putClientProperty s :bias 0.0)
                     (.putClientProperty s :rvs-scale 1.0)
                     (.putClientProperty s :rvs-bias 0.0)
                     s)
        slide-wave1 (factory/slider param-wave1-depth -1.0 1.0 (factory/signed-unit-label-map))
        slide-wave2 (factory/slider param-wave2-depth -1.0 1.0 (factory/signed-unit-label-map))
        bus-freq1 (factory/matrix-listbox ied param-freq1-source)
        bus-freq2 (factory/matrix-listbox ied param-freq2-source)
        bus-wave1 (factory/matrix-listbox ied param-wave1-source)
        bus-wave2 (factory/matrix-listbox ied param-wave2-source)
        pan-f1 (ss/border-panel :center (:panel bus-freq1)
                                :north spin-freq1
                                :border (factory/title "Freq 1"))
        pan-f2 (ss/border-panel :center (:panel bus-freq2)
                                :north spin-freq2
                                :border (factory/title "Freq 2"))
        wavepan-size [64 :by 175]
        pan-w1 (ss/border-panel :center (:panel bus-wave1)
                                :east (factory/slider-panel slide-wave1 "Depth" wavepan-size)
                                :border (factory/title "Wave 1"))
        pan-w2 (ss/border-panel :center (:panel bus-wave2)
                                :east (factory/slider-panel slide-wave2 "Depth" wavepan-size)
                                :border (factory/title "Wave 2"))
        pan-main (ss/grid-panel :rows 1
                                :items [pan-f1 pan-f2 pan-w1 pan-w2]
                                :border (factory/title (format "LFO %d" prefix)))
        spinners [spin-freq1 spin-freq2]
        sliders [slide-wave1 slide-wave2]
        buspans [bus-freq1 bus-freq2 bus-wave1 bus-wave2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s spinners]
                   (let [p (.getClientProperty s :param)
                         v (double (get data p 1.0))]
                     (.setValue s v)))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (doseq [b buspans]
                   ((:syncfn b) data))
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
    (doseq [s (flatten (conj sliders spinners))]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn}))

(defn lfo-editor [performance ied]
  (let [
        lfo1 (lfoed 1 ied)
        lfo2 (lfoed 2 ied)
        lfo3 (lfoed 3 ied)
        pan-main (ss/grid-panel :columns 1
                                :items [(:pan-main lfo1)
                                        (:pan-main lfo2)
                                        (:pan-main lfo3)])
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
      (init! [this])
      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          (doseq [e [lfo1 lfo2 lfo3]]
            ((:syncfn e) data)))))))
