(ns cadejo.instruments.alias.editor.snh-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))


(defn- snh [ied]
  (let [enable-change-listener* (atom true)
        param-source :sh-source
        param-rate :sh-rate
        param-scale :sh-scale
        param-bias :sh-bias
        spin-rate (factory/spinner param-rate 7.0 0.01 10.0 0.01)
        slide-scale (factory/slider param-scale 0.0 1.0 (factory/unsigned-unit-label-map))
        slide-bias (factory/slider param-bias -1.0 1.0 (factory/signed-unit-label-map))
        buslist (factory/matrix-listbox ied param-source)
        pan-center (ss/horizontal-panel 
                    :items [(:panel buslist)
                            (factory/slider-panel slide-scale "Scale" [64 :by 175])
                            (factory/slider-panel slide-bias "Bias" [64 :by 175])])
        pan-rate (ss/horizontal-panel :items [(ss/label :text "Rate ")
                                              spin-rate])
        pan-main (ss/border-panel :north pan-rate
                                  :center pan-center
                                  :border (factory/title "Sample and Hold"))
        sliders [slide-scale slide-bias]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (.setValue spin-rate (double (get data param-rate 1.0)))
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 ((:syncfn buslist) data)
                 (reset! enable-change-listener* true))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (double (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))]
    (doseq [s (conj sliders spin-rate)]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn}))

(defn- lfnoise [ied]
   (let [enable-change-listener* (atom true)
         param-source :lfnoise-freq-source
         param-depth :lfnoise-freq-depth
         buslist (factory/matrix-listbox ied param-source)
         spin-depth (factory/spinner param-depth 1.0 0.01 10.0 0.1)
         pan-center (ss/horizontal-panel 
                    :items [(:panel buslist)])
         pan-north (ss/horizontal-panel 
                    :items [(ss/label "Rate ")
                            spin-depth]) 
         pan-main (ss/border-panel :north pan-north
                                   :center pan-center
                                   :border (factory/title "Low Frequency Noise"))
         syncfn (fn [data]
                  (reset! enable-change-listener* false)
                  (.setValue spin-depth (double (get data param-depth 1.0)))
                  ((:syncfn buslist) data)
                  (reset! enable-change-listener* true))
         change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (double (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))]
     (.addChangeListener spin-depth change-listener)
    {:pan-main pan-main
     :syncfn syncfn}))


(defn sample-and-hold [performance ied]
  (let [s (snh ied)
        nse (lfnoise ied)
        pan-main (ss/scrollable (ss/horizontal-panel :items [(:pan-main s)
                                                             (:pan-main nse)
                                                             (Box/createVerticalStrut 200)]))
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
          (doseq [e [s nse]]
            ((:syncfn e) data)))))))
