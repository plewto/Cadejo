(ns cadejo.instruments.masa.efx-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defn- slider [param min max]
  (let [s (ss/slider :orientation :vertical
                     :value min
                     :min 0
                     :max 100
                     :minor-tick-spacing 5
                     :major-tick-spacing 25
                     :snap-to-ticks? false
                     :paint-ticks? true
                     :paint-labels? true
                     :paint-track? true)]
    (.putClientProperty s :param param)
    (.putClientProperty s :scale (/ (- max min) 100.0))
    (.putClientProperty s :bias min)
    (.putClientProperty s :rvs-scale (/ 100.0 (- max min)))
    (.putClientProperty s :rvs-bias (/ (* -100 min)(- max min)))
    s))

(defn efx-tab [performance ied]
  (let [enable-change-listener* (atom true)
        slide-scanner-delay (slider :scanner-delay 0.001 0.010)
        slide-scanner-delay-mod (slider :scanner-delay-mod 0.0 1.0)
        slide-scanner-mod-rate (slider :scanner-mod-rate 0.1 10.0)
        slide-scanner-mod-spread (slider :scanner-mod-spread 0.0 1.0)
        slide-scanner-scan-rate (slider :scanner-scan-rate 0.1 10.0)
        slide-scanner-crossmix (slider :scanner-crossmix 0.0 1.0)
        slide-scanner-mix (slider :scanner-mix 0.0 1.0)
        slide-reverb-room-size (slider :room-size 0.0 1.0)
        slide-reverb-damp (slider :reverb-damp 0.0 1.0)
        slide-reverb-mix (slider :reverb-mix 0.0 1.0)
        slide-amp (slider :amp 0.0 1.0)
        pan-scanner (ss/grid-panel 
                     :rows 2 :columns 4
                     :items [(ss/vertical-panel :items [slide-scanner-delay] :border (factory/title "Delay"))
                             (ss/vertical-panel :items [slide-scanner-delay-mod] :border (factory/title "Delay Mod"))
                             (ss/vertical-panel :items [slide-scanner-mod-rate] :border (factory/title "Mod Rate"))
                             (ss/vertical-panel :items [slide-scanner-mod-spread] :border (factory/title "Spread"))
                             (ss/vertical-panel :items [slide-scanner-scan-rate] :border (factory/title "Scan Rate"))
                             (ss/vertical-panel :items [slide-scanner-crossmix] :border (factory/title "Crossmix"))
                             (ss/vertical-panel :items [slide-scanner-mix] :border (factory/title "Mix"))]
                     :border (factory/title "Scanner"))
        pan-reverb (ss/grid-panel 
                    :rows 1
                    :items [(ss/vertical-panel :items [slide-reverb-room-size] :border (factory/title "Room Size"))
                            (ss/vertical-panel :items [slide-reverb-damp] :border (factory/title "Damp"))
                            (ss/vertical-panel :items [slide-reverb-mix] :border (factory/title "Mix"))]
                    :border (factory/title "Reverb"))
        pan-amp (ss/vertical-panel 
                 :items [slide-amp]
                 :border (factory/title "Amp"))
        pan-main (ss/horizontal-panel :items [pan-scanner pan-reverb pan-amp])
        widget-map {:pan-main pan-main}
        
        sliders [slide-scanner-delay slide-scanner-delay-mod
                 slide-scanner-mod-rate slide-scanner-mod-spread
                 slide-scanner-scan-rate slide-scanner-crossmix
                 slide-scanner-mix slide-reverb-room-size
                 slide-reverb-damp slide-reverb-mix slide-amp]

        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor

             (widgets [this] widget-map)
             
             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "masa efx-tab does not have %s widget" key))))
             
             (parent [this] ied)

             (parent! [this ignore] ied)
        
             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (sync-ui! [this]
               (reset! enable-change-listener* false)
               (let [data (.current-data (.bank performance))]
                 (doseq [s sliders]
                   (let [param (.getClientProperty s :param)
                         scale (.getClientProperty s :rvs-scale)
                         bias (.getClientProperty s :rvs-bias)
                         value (float (get data param 1.0))
                         pos (int (+ bias (* scale value)))]
                     (.setValue s pos))))
               (reset! enable-change-listener* true)) )

        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias) 
                                    pos (.getValue src)
                                    value (float (+ bias (* scale pos)))]
                                (.status! ed (format "[%-12s] --> val %s" param value))
                                (.set-param! ied param value))))) ]

    (doseq [s sliders]
      (.addChangeListener s change-listener))
    (.add-sub-editor! ied "EFX" ed)
    ed))


