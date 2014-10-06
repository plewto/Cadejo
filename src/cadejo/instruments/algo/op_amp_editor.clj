(ns cadejo.instruments.algo.op-amp-editor
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.util.Hashtable
           javax.swing.Box
           javax.swing.event.ChangeListener))

(defn- third [col](nth col 2))
(defn- fourth [col](nth col 3))

;; Provides editor controls related to operator amplitude
;; including overall amplitude, velocity, pressure, cca, ccb
;; lfo1 and lfo2. This editor does not provide controls for
;; envelopes are key-scale.
;;
;; op - int operator number, 1,2,3,... 8
;; performance - parent Performance object
;; ied - parent InstrumentEditor object
;;
;; returns map with following keys:
;; :pan-main - the primary swing panel
;; :sync-ui - a function used  to synchronize editor controls to current 
;; program state
;;
(defn op-amp-editor [op performance ied]
  (let [param-amp (keyword (format "op%d-amp" op))
        param-velocity (keyword (format "op%d-velocity" op))
        param-pressure (keyword (format "op%d-pressure" op))
        param-cca (keyword (format "op%d-cca" op))
        param-ccb (keyword (format "op%d-ccb" op))
        param-lfo1 (keyword (format "op%d-lfo1" op))
        param-lfo2 (keyword (format "op%d-lfo2" op))
        enable-change-listener* (atom true)
        amp* (atom 0.0)
        amp-scale* (atom 4.0)
        update-synths (fn [param val] 
                        (.set-param! ied param val))
        ;; Primary amplitude slider with 4-scale buttons
        slide-amp (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :snap-to-ticks? false
                                     :paint-labels? true
                                     :minor-tick-spacing 5
                                     :major-tick-spacing 25)]
                    (.addChangeListener s (proxy [ChangeListener][]
                                            (stateChanged [ev]
                                              (if @enable-change-listener*
                                                (let [pos (.getValue s)
                                                      val (* 1/100 pos)]
                                                  (reset! amp* val)
                                                  (update-synths param-amp (* @amp-scale* val)))))))
                    s)
        ampscale-buttons (let [acc* (atom [])
                               grp (ss/button-group)]
                           (doseq [s [4 8 16 100]]
                             (let [tb (ss/toggle :text (format "x%d" s)
                                                 :group grp
                                                 :selected? (= s 4))]
                               (swap! acc* (fn [n](conj n tb)))
                               (.putClientProperty tb :value s)
                               (ss/listen tb :action (fn [ev]
                                                       (let [src (.getSource ev)
                                                             val (float (.getClientProperty src :value))]
                                                         (reset! amp-scale* val)
                                                         (update-synths param-amp (* val @amp*)))))))
                           @acc*)
        pan-amp (ss/border-panel :center (ss/border-panel :center slide-amp
                                                          :west (ss/grid-panel :columns 1
                                                                               :items ampscale-buttons
                                                                               :border (factory/padding)))
                                 :south (ss/label :text "Amp" :halign :center))
        ;; Velocity 
        slide-velocity (let [s (ss/slider :orientation :vertical
                                          :value 0 :min 0 :max 100
                                          :snap-to-ticks? false
                                          :paint-labels? true
                                          :minor-tick-spacing 5
                                          :major-tick-spacing 25)]
                         (.addChangeListener s (proxy [ChangeListener][]
                                                 (stateChanged [ev]
                                                   (if @enable-change-listener*
                                                     (let [pos (.getValue s)
                                                           val (float (* 1/100 pos))]
                                                       (update-synths param-velocity val))))))
                         s)
        pan-velocity (ss/border-panel :center slide-velocity
                                      :south (ss/label :text "Vel" :halign :center))

        ;; Pressure 
        slide-pressure (let [s (ss/slider :orientation :vertical
                                          :value 0 :min 0 :max 100
                                          :snap-to-ticks? false
                                          :paint-labels? true
                                          :minor-tick-spacing 5
                                          :major-tick-spacing 25)]
                         (.addChangeListener s (proxy [ChangeListener][]
                                                 (stateChanged [ev]
                                                   (if @enable-change-listener*
                                                     (let [pos (.getValue s)
                                                           val (float (* 1/100 pos))]
                                                       (update-synths param-pressure val))))))
                         s)
        pan-pressure (ss/border-panel :center slide-pressure
                                      :south (ss/label :text "Press" :halign :center))

        ;; CCA
        slide-cca (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :snap-to-ticks? false
                                     :paint-labels? true
                                     :minor-tick-spacing 5
                                     :major-tick-spacing 25)]
                    (.addChangeListener s (proxy [ChangeListener][]
                                            (stateChanged [ev]
                                              (if @enable-change-listener*
                                                (let [pos (.getValue s)
                                                      val (float (* 1/100 pos))]
                                                  (update-synths param-cca val))))))
                    s)
        pan-cca (ss/border-panel :center slide-cca
                                 :south (ss/label :text "CCA" :halign :center))

        ;; CCB
        slide-ccb (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :snap-to-ticks? false
                                     :paint-labels? true
                                     :minor-tick-spacing 5
                                     :major-tick-spacing 25)]
                    (.addChangeListener s (proxy [ChangeListener][]
                                            (stateChanged [ev]
                                              (if @enable-change-listener*
                                                (let [pos (.getValue s)
                                                      val (float (* 1/100 pos))]
                                                  (update-synths param-ccb val))))))
                    s)
        pan-ccb (ss/border-panel :center slide-ccb
                                 :south (ss/label :text "CCB" :halign :center))

        ;; LFO1
        slide-lfo1 (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :snap-to-ticks? false
                                     :paint-labels? true
                                     :minor-tick-spacing 5
                                     :major-tick-spacing 25)]
                    (.addChangeListener s (proxy [ChangeListener][]
                                            (stateChanged [ev]
                                              (if @enable-change-listener*
                                                (let [pos (.getValue s)
                                                      val (float (* 1/100 pos))]
                                                  (update-synths param-lfo1 val))))))
                    s)
        pan-lfo1 (ss/border-panel :center slide-lfo1
                                 :south (ss/label :text "LFO1" :halign :center))

        ;; LFO2
        slide-lfo2 (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :snap-to-ticks? false
                                     :paint-labels? true
                                     :minor-tick-spacing 5
                                     :major-tick-spacing 25)]
                    (.addChangeListener s (proxy [ChangeListener][]
                                            (stateChanged [ev]
                                              (if @enable-change-listener*
                                                (let [pos (.getValue s)
                                                      val (float (* 1/100 pos))]
                                                  (update-synths param-lfo2 val))))))
                    s)
        pan-lfo2 (ss/border-panel :center slide-lfo2
                                 :south (ss/label :text "LFO2" :halign :center))

        pan-main (ss/horizontal-panel :items [pan-amp
                                              (ss/grid-panel :rows 1 :items [pan-velocity pan-pressure 
                                                                             pan-cca pan-ccb 
                                                                             pan-lfo1 pan-lfo2])])
        sync-ui! (fn []
                   (reset! enable-change-listener* false)
                   (let [data (.current-data (.bank performance))
                         velocity (get data param-velocity 0.0)
                         pressure (get data param-pressure 0.0)
                         cca (get data param-cca 0.0)
                         ccb (get data param-ccb 0.0)
                         lfo1 (get data param-lfo1 0.0)
                         lfo2 (get data param-lfo2 0.0)
                         amp (get data param-amp 0.0)
                         ascale (cond (<= amp 4)
                                      (do (.setSelected (first ampscale-buttons) true)
                                          4.0)
                                      (<= amp 8)
                                      (do (.setSelected (second ampscale-buttons) true)
                                          8.0)
                                      (<= amp 16)
                                      (do (.setSelected (third ampscale-buttons) true)
                                          16.0)
                                      :default
                                      (do (.setSelected (fourth ampscale-buttons) true)
                                          100.0))]
                     (reset! amp-scale* ascale)
                     (.setValue slide-amp (int (/ (* 100 amp) ascale)))
                     (.setValue slide-velocity (int (* 100 velocity)))
                     (.setValue slide-pressure (int (* 100 pressure)))
                     (.setValue slide-cca (int (* 100 cca)))
                     (.setValue slide-ccb (int (* 100 ccb)))
                     (.setValue slide-lfo1 (int (* 100 lfo1)))
                     (.setValue slide-lfo2 (int (* 100 lfo2))))
                   (reset! enable-change-listener* true))]
    {:pan-main pan-main
     :sync-ui sync-ui!}))
