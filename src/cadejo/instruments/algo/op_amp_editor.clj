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
;; lfo1 and lfo2. 
;;
;; op - int operator number, 1,2,3,... 8
;; performance - parent Performance object
;; ied - parent InstrumentEditor object
;;
;; returns map with following keys:
;; :pan-main - the primary swing panel
;; :syncfn - function used  to synchronize editor controls to current 
;;           program state
;; :mutefn - function (fn flag) if flag is true disable all components
;;
(defn op-amp-editor [op performance ied]
  (let [param-amp (keyword (format "op%d-amp" op))
        param-velocity (keyword (format "op%d-velocity" op))
        param-pressure (keyword (format "op%d-pressure" op))
        param-cca (keyword (format "op%d-cca" op))
        param-ccb (keyword (format "op%d-ccb" op))
        param-lfo1 (keyword (format "op%d-lfo1" op))
        param-lfo2 (keyword (format "op%d-lfo2" op))
        param-left-key (keyword (format "op%d-left-key" op))
        param-right-key (keyword (format "op%d-right-key" op))
        param-left-scale (keyword (format "op%d-left-scale" op))
        param-right-scale (keyword (format "op%d-right-scale" op))
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
        lab-amp (ss/label :text "Amp" :halign :center)
        pan-amp (ss/border-panel :center (ss/border-panel :center slide-amp
                                                          :west (ss/grid-panel :columns 1
                                                                               :items ampscale-buttons
                                                                               :border (factory/padding)))
                                 :south lab-amp)
        ;; Velocity 
        lab-velocity (ss/label :text "Vel" :halign :center)
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
                                      :south lab-velocity)

        ;; Pressure 
        lab-pressure (ss/label :text "Press" :halign :center)
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
                                      :south lab-pressure)

        ;; CCA
        lab-cca (ss/label :text "CCA" :halign :center)
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
                                 :south lab-cca)

        ;; CCB
        lab-ccb (ss/label :text "CCB" :halign :center)
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
                                 :south lab-ccb)

        ;; LFO1
        lab-lfo1 (ss/label :text "LFO1" :halign :center)
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
                                  :south lab-lfo1)
        
        ;; LFO2
        lab-lfo2 (ss/label :text "LFO2" :halign :center)
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
                                  :south lab-lfo2)

        ;; keyscale
        lab-lkn (ss/label :text "Keynum" :halign :center)
        lab-lks (ss/label :text "Scale" :halign :center)

        lab-rkn (ss/label :text "Keynum" :halign :center)
        lab-rks (ss/label :text "Scale" :halign :center)

        keyscale-listener (proxy [ChangeListener] []
                            (stateChanged [ev]
                              (if @enable-change-listener*
                                (let [src (.getSource ev)
                                      param (.getClientProperty src :param)
                                      val (.getValue src)]
                                  (update-synths param val)))))
        spin-left-keynum (let [s (ss/spinner :model (ss/spinner-model 60 :min 0 :mx 127 :by 1))]
                           (.putClientProperty s :param param-left-key)
                           (.addChangeListener s keyscale-listener)
                           s)
        spin-left-keyscale (let [s (ss/spinner :model (ss/spinner-model 0 :min -99 :max 99 :by 3))]
                             (.putClientProperty s :param param-left-scale)
                             (.addChangeListener s keyscale-listener)
                             s)
        spin-right-keynum (let [s (ss/spinner :model (ss/spinner-model 60 :min 0 :mx 127 :by 1))]
                            (.putClientProperty s :param param-right-key)
                            (.addChangeListener s keyscale-listener)
                            s)
        spin-right-keyscale (let [s (ss/spinner :model (ss/spinner-model 0 :min -99 :max 99 :by 3))]
                              (.putClientProperty s :param param-right-scale)
                              (.addChangeListener s keyscale-listener)
                              s)
        pan-left-keyscale (ss/vertical-panel :items [spin-left-keynum 
                                                     lab-lkn
                                                     (Box/createVerticalStrut 12)
                                                     spin-left-keyscale
                                                     lab-lks]
                                             :border (factory/title "Left"))
        pan-right-keyscale (ss/vertical-panel :items [spin-right-keynum 
                                                     lab-rkn
                                                     (Box/createVerticalStrut 12)
                                                     spin-right-keyscale
                                                     lab-rks]
                                             :border (factory/title "Right"))

        pan-keyscale (ss/vertical-panel :items [pan-left-keyscale pan-right-keyscale])
                     pan-main (ss/horizontal-panel :items [pan-amp
                                              (ss/grid-panel :rows 1 :items [pan-velocity pan-pressure 
                                                                             pan-cca pan-ccb 
                                                                             pan-lfo1 pan-lfo2
                                                                             pan-keyscale])])                          
        sync-ui (fn []
                  (reset! enable-change-listener* false)
                  (let [data (.current-data (.bank performance))
                        velocity (get data param-velocity 0.0)
                        pressure (get data param-pressure 0.0)
                        cca (get data param-cca 0.0)
                        ccb (get data param-ccb 0.0)
                        lfo1 (get data param-lfo1 0.0)
                        lfo2 (get data param-lfo2 0.0)
                        lks (int (get data param-left-scale 60))
                        lkey (int (get data param-left-scale 0))
                        rks (int (get data param-right-scale 60))
                        rkey (int (get data param-right-scale 0))
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
                    (.setValue slide-lfo2 (int (* 100 lfo2)))
                    (.setValue spin-left-keynum lkey)
                    (.setValue spin-left-keyscale lks)
                    (.setValue spin-right-keynum rkey)
                    (.setValue spin-right-keyscale lks))
                  (reset! enable-change-listener* true))
        mute (fn [flag]
               (let [f (not flag)]
                 (doseq [obj [slide-amp slide-velocity slide-pressure slide-cca 
                              slide-ccb slide-lfo1 slide-lfo2 spin-left-keynum 
                              spin-left-keyscale spin-right-keynum spin-right-keyscale
                              lab-amp lab-velocity lab-pressure lab-cca lab-ccb
                              lab-lfo1 lab-lfo2 lab-lkn lab-lks lab-rkn lab-rks]]
                   (.setEnabled obj f))
                 (doseq [obj ampscale-buttons]
                   (.setEnabled obj f))))]
    {:pan-main pan-main
     :syncfn sync-ui
     :mutefn mute}))
