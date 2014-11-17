(ns cadejo.instruments.alias.editor.divider-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.color-utilities :as cutil])
  (:require [seesaw.core :as ss])
  (:require [sgwr.drawing])
  (:import javax.swing.Box
           javax.swing.event.ChangeListener))

(def ^:private slider-size [64 :by 175])

(defn- divider-graph []
  (let [drawing-width 300
        drawing-height 253
        drawing-domain 300
        drawing-range 45
        [bg fg](config/envelope-colors)
        color-background (cutil/color (or bg (lnf/get-color (config/current-skin) :text-fg)))
        color-foreground (cutil/color (or fg (lnf/get-color (config/current-skin) :text-bg)))
        color-axis (cutil/inversion color-background)
        [drw1 drw2](let [d1 (sgwr.drawing/cartesian-drawing
                             drawing-width drawing-height
                             [-1.0 (* -1 drawing-range)]
                             [(inc drawing-domain) drawing-range])
                         cs1 (.coordinate-system d1)
                         d2 (sgwr.drawing/cartesian-drawing
                             drawing-width drawing-height
                             [-1.0 (* -1 drawing-range)]
                             [(inc drawing-domain) drawing-range])
                         cs2 (.coordinate-system d2)]
                     (.paper! d1 color-background)
                     (.paper! d2 color-background)
                     (.color! d1 color-axis)
                     (.color! d2 color-axis)
                     (.view! cs1 [[-1.0 (* -1 drawing-range)][150 drawing-range]])
                     (.view! cs2 [[-1.0 (* -1 drawing-range)][150 drawing-range]])
                     (.line! d1 [0 0][drawing-domain 0])
                     (.line! d2 [0 0][drawing-domain 0])
                     (.line! d1 [0 (* -1 drawing-range)][0 drawing-range])
                     (.line! d2 [0 (* -1 drawing-range)][0 drawing-range])
                     (.freeze! d1)
                     (.freeze! d2)
                     [d1 d2])
        syncfn (fn [data]
                  (let [a1 (:divider1-p1 data)
                        a2 (:divider2-p2 data)
                        a3 (:divider1-p3 data)
                        a4 (:divider2-p4 data)
                        a5 (:divider1-p5 data)
                        a6 (:divider2-p6 data)
                        a7 (:divider1-p7 data)
                        a8 (:divider2-p8 data)
                        pulse (fn [x ratio amp]
                                (* amp (if (odd? (int (* ratio x))) -1 1)))
                        acc* (atom [])
                        bcc* (atom [])]
                    (.clear! drw1 false)
                    (.clear! drw2 false)
                    (.move-to! drw1 [0 0])
                    (.move-to! drw2 [0 0])
                    (.color! drw1 color-foreground)
                    (.color! drw2 color-foreground)
                    (doseq [x (range drawing-domain)]
                      (let [p1 (pulse x 1 a1)
                            p2 (pulse x 1/2 a2)
                            p3 (pulse x 1/3 a3)
                            p4 (pulse x 1/4 a4)
                            p5 (pulse x 1/5 a5)
                            p6 (pulse x 1/6 a6)
                            p7 (pulse x 1/7 a7)
                            p8 (pulse x 1/8 a8)
                            odd (+ p1 p3 p5 p7)
                            even (+ p2 p4 p6 p8)]
                        (swap! acc* (fn [q](conj q [x odd])))
                        (swap! bcc* (fn [q](conj q [x even])))))
                    (.plot-to! drw1 @acc*)
                    (.plot-to! drw2 @bcc*)))
        pan-main (ss/vertical-panel 
                  :items [(.drawing-canvas drw1)
                          (.drawing-canvas drw2)]
                  :border (factory/bevel))]
    (ss/config! (.drawing-canvas drw1) :size [drawing-width :by drawing-height])
    (ss/config! (.drawing-canvas drw2) :size [drawing-width :by drawing-height])
    {:pan-main pan-main
     :syncfn syncfn}))

(defn- dived [prefix performance ied graph]
  (let [enable-change-listener* (atom true)
        param (fn [suffix](keyword (format "divider%d-%s" prefix suffix)))
        amp-param (fn [index](param (get (if (= prefix 1)
                                           {0 "p1" 1 "p3" 2 "p5" 3 "p7"}
                                           {0 "p2" 1 "p4" 2 "p6" 3 "p8"})
                                         index)))
        param-a0 (amp-param 0)
        param-a1 (amp-param 1)
        param-a2 (amp-param 2)
        param-a3 (amp-param 3)
        param-bias (param "bias")
        param-scale-source (param "scale-source")
        param-scale-depth (param "scale-depth")
        param-pw (keyword (format "divider%d-pw" prefix))
        slide-a0 (factory/slider param-a0 -10.0 10.0 (factory/signed-unit-label-map))
        slide-a1 (factory/slider param-a1 -10.0 10.0 (factory/signed-unit-label-map))
        slide-a2 (factory/slider param-a2 -10.0 10.0 (factory/signed-unit-label-map))
        slide-a3 (factory/slider param-a3 -10.0 10.0 (factory/signed-unit-label-map))
        slide-scale (factory/slider param-scale-depth -1.0 1.0 (factory/signed-unit-label-map))
        slide-bias (factory/slider param-bias -10.0 10.0 (factory/signed-unit-label-map))
        slide-pw (factory/unit-slider param-pw false)
        bus-scale (factory/matrix-listbox ied param-scale-source)
        pan-scale-depth (factory/slider-panel slide-scale "Scale" slider-size)
        pan-bias (factory/slider-panel slide-bias "Bias" slider-size)
        pan-pw (factory/slider-panel slide-pw "PW")
        pan-scale (ss/border-panel :center (:panel bus-scale)
                                   :east (ss/horizontal-panel :items [pan-scale-depth pan-bias pan-pw])
                                   :border (factory/title "Scale"))
        mix-label (fn [index]
                    (str (if (= prefix 1)
                           (inc (* 2 index))
                           (* 2 (inc index)))))
        pan-mix (ss/grid-panel :rows 1
                               :items [(factory/slider-panel slide-a0 (mix-label 0) slider-size )
                                       (factory/slider-panel slide-a1 (mix-label 1) slider-size )
                                       (factory/slider-panel slide-a2 (mix-label 2) slider-size )
                                       (factory/slider-panel slide-a3 (mix-label 3) slider-size )]
                               :border (factory/title "Mixer"))
        pan-main (ss/horizontal-panel :items [pan-scale pan-mix]
                                      :border (factory/title (format "Divider %s" (if (= prefix 1) "Odd" "Even"))))
        sliders [slide-a0 slide-a1 slide-a2 slide-a3 
                 slide-pw slide-bias slide-scale]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s sliders]
                   (factory/sync-slider s data))
                 ((:syncfn bus-scale) data)
                 (reset! enable-change-listener* true))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (float (+ bias (* scale pos)))
                                    data (.current-data (.bank performance))]
                                ((:syncfn graph) data)
                                (.set-param! ied param val)))))]
    (doseq [s sliders]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn}))

(defn divider-editor [performance ied]
  (let [graph (divider-graph)
        div1 (dived 1 performance ied graph)
        div2 (dived 2 performance ied graph)
        pan-controls (ss/vertical-panel :items [(:pan-main div1)
                                                (:pan-main div2)])
        pan-graph (ss/border-panel :center (:pan-main graph)
                                   :east (Box/createHorizontalStrut 276)
                                     :border (factory/padding 16))
        pan-main (ss/border-panel :center pan-controls
                                  :east pan-graph)
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
          ((:syncfn graph) data)
          (doseq [e [div1 div2]]
            ((:syncfn e) data)))))))
                                        

