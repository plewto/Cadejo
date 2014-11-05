(ns cadejo.instruments.alias.editor.matrix-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))


(defn matrix-bus [prefix ied]
  (let [enable-change-listener* (atom true)
        param-source1 (keyword (format "%s-source1" prefix))
        param-source2 (keyword (format "%s-source2" prefix))
        param-depth1 (keyword (format "%s-depth1" prefix))
        param-depth2 (keyword (format "%s-depth2" prefix))
        buslist1 (factory/matrix-listbox ied param-source1)
        slide1 (factory/slider param-depth1 -1.0 1.0 (factory/signed-unit-label-map))
        pan1 (ss/horizontal-panel :items [(:panel buslist1)
                                          (factory/slider-panel slide1 "Depth" [65 :by 175])]
                                  :border (factory/title "Source 1"))
                                          

        buslist2 (factory/matrix-listbox ied param-source2)
        slide2 (factory/slider param-depth2 -1.0 1.0 (factory/signed-unit-label-map))
        pan2 (ss/horizontal-panel :items [(:panel buslist2)
                                          (factory/slider-panel slide2 "Depth" [65 :by 175])]
                                  :border (factory/title "Source 2"))
        pan-main (ss/horizontal-panel :items [pan1 pan2]
                                      :border (factory/title (format "Bus %s" (.toUpperCase prefix))))
        sliders [slide1 slide2]
        buslist [buslist1 buslist2]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 (doseq [b buslist]
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
    (doseq [s sliders]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn}))


(defn matrix-editor [performance ied]
  (let [a (matrix-bus "a" ied)
        b (matrix-bus "b" ied)
        c (matrix-bus "c" ied)
        d (matrix-bus "d" ied)
        e (matrix-bus "e" ied)
        f (matrix-bus "f" ied)
        g (matrix-bus "g" ied)
        h (matrix-bus "h" ied)
        pan-center (ss/grid-panel :rows 4 :columns 2
                                  :items [(:pan-main a)(:pan-main b)
                                          (:pan-main c)(:pan-main d)
                                          (:pan-main e)(:pan-main f)
                                          (:pan-main g)(:pan-main h)])
        pan-main (ss/scrollable pan-center)
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
          (doseq [q [a b c d e f g h]]
            ((:syncfn q) data)))))))
        

