(ns cadejo.instruments.alias.editor.stepper-editor
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defn- steped [prefix ied]
  (let [enable-change-listener* (atom true)
        param (fn [suffix](keyword (format "stepper%d-%s" prefix suffix)))
        param-trigger-source (param "trigger")
        param-reset-source (param "reset")
        param-min (param "min")
        param-max (param "max")
        param-step (param "step")
        param-reset-value (param "reset-value")
        param-bias (param "bias")
        param-scale (param "scale")
        spin-min (factory/spinner param-min -10.0 -100.0 100.0 1.0)
        spin-max (factory/spinner param-max 10.0 -100.0 100.0 1.0)
        spin-step (factory/spinner param-step 1.0 -50.0 50.0 1.0)
        spin-reset (factory/spinner param-reset-value -10.0 -100.0 100.0 1.0)
        spin-bias (factory/spinner param-bias 0.0 -50.0 50.0 1.0)
        spin-scale (factory/spinner param-scale 0.1 0.0 1.0 0.01)
        bus-trigger (factory/matrix-listbox ied param-trigger-source)
        bus-reset (factory/matrix-listbox ied param-reset-source)
        pan-trigger (ss/border-panel :center (:panel bus-trigger)
                                     :south (ss/label :text "Trigger source"
                                                      :halign :center))
        pan-reset (ss/border-panel :center (:panel bus-reset)
                                   :south (ss/label :text "Reset source"
                                                    :halign :center))
        pan-east (ss/grid-panel :rows 3 :columns 2
                                :items [(factory/spinner-panel spin-min "Min")
                                        (factory/spinner-panel spin-reset "Reset")
                                        (factory/spinner-panel spin-max "Max")
                                        (factory/spinner-panel spin-bias "Bias")
                                        (factory/spinner-panel spin-step "Step")
                                        (factory/spinner-panel spin-scale "Scale")])
        pan-main (ss/border-panel 
                  :center (ss/horizontal-panel :items [pan-trigger pan-reset])
                  :east pan-east
                  :border (factory/title (format "Step counter %s" prefix)))
        spinners [spin-min spin-max spin-step 
                  spin-reset spin-bias spin-scale]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (doseq [s spinners]
                   (factory/sync-spinner s data))
                 ((:syncfn bus-trigger) data)
                 ((:syncfn bus-reset) data)
                 (reset! enable-change-listener* true))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    val (double (.getValue src))
                                    param (.getClientProperty src :param)]
                                (.set-param! ied param val)))))]
    (doseq [s spinners]
      (.addChangeListener s change-listener))
    {:pan-main pan-main
     :syncfn syncfn}))


(defn step-counter-editor [performance ied]
  (let [s1 (steped 1 ied)
        s2 (steped 2 ied)
        pan-main (ss/vertical-panel :items [(:pan-main s1)
                                            (:pan-main s2)])]
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
          (doseq [e [s1 s2]]
            ((:syncfn e) data)))))))
