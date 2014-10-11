(ns cadejo.instruments.algo.feedback-editor
  (:require [cadejo.instruments.algo.op-freq-editor])
  (:require [cadejo.instruments.algo.op-amp-editor])
  (:require [cadejo.instruments.algo.env-editor])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

;; Feedback
;;
(defn- op-fb-editor [op performance ied]
  (let [enable-change-listener* (atom true)
        param-fb (keyword (format "op%d-feedback" op))
        param-env (keyword (format "op%d-env->feedback" op))
        param-pressure (keyword (format "op%d-pressure->feedback" op))
        param-cca (keyword (format "op%d-cca->feedback" op)) 
        param-ccb (keyword (format "op%d-ccb->feedback" op))
        param-lfo (keyword (format "op%d-lfo%d->feedback" op (if (= op 6) 1 2)))
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    pos (.getValue src)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    value (float (+ bias (* scale pos)))]
                                (.set-param! ied param value)))))
        slide-fb (let [s (ss/slider :orientation :vertical
                                    :value 0 :min 0 :max 100 
                                    :major-tick-spacing 20
                                    :paint-labels? true)]
                   
                   (.putClientProperty s :param param-fb)
                   (.putClientProperty s :scale 3/10)
                   (.putClientProperty s :bias 0)
                   (.putClientProperty s :rvs-scale 10/3)
                   (.putClientProperty s :rvs-bias 0)
                   (.addChangeListener s change-listener)
                   s)
        slide-env (let [s (ss/slider :orientation :vertical
                                     :value 0 :min -100 :max 100
                                     :major-tick-spacing 20
                                     :paint-labels? true)]
                    (.putClientProperty s :param param-env)
                    (.putClientProperty s :scale 3/10)
                    (.putClientProperty s :bias 0)
                    (.putClientProperty s :rvs-scale 10/3)
                    (.putClientProperty s :rvs-bias 0)
                    (.addChangeListener s change-listener)
                    s)
        slide-lfo (let [s (ss/slider :orientation :vertical
                                     :value 0 :min -100 :max 100
                                     :major-tick-spacing 20
                                     :paint-labels? true)]
                    (.putClientProperty s :param param-lfo)
                    (.putClientProperty s :scale 3/10)
                    (.putClientProperty s :bias 0)
                    (.putClientProperty s :rvs-scale 10/3)
                    (.putClientProperty s :rvs-bias 0)
                    (.addChangeListener s change-listener)
                    s)
        slide-pressure (let [s (ss/slider :orientation :vertical
                                     :value 0 :min -100 :max 100
                                     :major-tick-spacing 20
                                     :paint-labels? true)]
                    (.putClientProperty s :param param-pressure)
                    (.putClientProperty s :scale 3/10)
                    (.putClientProperty s :bias 0)
                    (.putClientProperty s :rvs-scale 10/3)
                    (.putClientProperty s :rvs-bias 0)
                    (.addChangeListener s change-listener)
                    s)
        slide-cca (let [s (ss/slider :orientation :vertical
                                          :value 0 :min -100 :max 100
                                          :major-tick-spacing 20
                                          :paint-labels? true)]
                         (.putClientProperty s :param param-cca)
                         (.putClientProperty s :scale 3/10)
                         (.putClientProperty s :bias 0)
                         (.putClientProperty s :rvs-scale 10/3)
                         (.putClientProperty s :rvs-bias 0)
                         (.addChangeListener s change-listener)
                         s)
        slide-ccb (let [s (ss/slider :orientation :vertical
                                          :value 0 :min -100 :max 100
                                          :major-tick-spacing 20
                                          :paint-labels? true)]
                         (.putClientProperty s :param param-ccb)
                         (.putClientProperty s :scale 3/10)
                         (.putClientProperty s :bias 0)
                         (.putClientProperty s :rvs-scale 10/3)
                         (.putClientProperty s :rvs-bias 0)
                         (.addChangeListener s change-listener)
                         s)
        lab-fb (ss/label :text "FB" :halign :center)
        lab-env (ss/label :text "Env" :halign :center)
        lab-lfo (ss/label :text (format "LFO%d" (if (= op 6) 1 2)) :halign :center)
        lab-pressure (ss/label :text "Press" :halign :center)
        lab-cca (ss/label :text "CCA" :halign :center)
        lab-ccb (ss/label :text "CCB" :halign :center)
        pan-fb (ss/border-panel :center slide-fb :south lab-fb)
        pan-env (ss/border-panel :center slide-env :south lab-env)
        pan-lfo (ss/border-panel :center slide-lfo :south lab-lfo)
        pan-pressure (ss/border-panel :center slide-pressure :south lab-pressure)
        pan-cca (ss/border-panel :center slide-cca :south lab-cca)
        pan-ccb (ss/border-panel :center slide-ccb :south lab-ccb)
        pan-main (ss/horizontal-panel :items [pan-fb pan-env pan-lfo
                                              pan-pressure pan-cca pan-ccb]
                                      :border (factory/title (format "Op %d Feedback" op)))
        widget-map {:pan-main pan-main}

        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo feedback-editor does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo feedback-editor init method not implemented"))
               )
            
             (sync-ui! [this]
               (reset! enable-change-listener* false)
               (let [data (.current-data (.bank performance))
                     fb (float (get data param-fb 0.0))
                     env (float (get data param-env 0.0))
                     pressure (float (get data param-pressure 0.0))
                     cca (float (get data param-cca 0.0))
                     ccb (float (get data param-ccb 0.0))
                     lfo (float (get data param-lfo 0.0))
                     values [fb env lfo pressure cca ccb]
                     sliders [slide-fb slide-env slide-lfo slide-pressure slide-cca slide-ccb]]
                 (dotimes [i (count values)]
                   (let [s (nth sliders i)
                         v (nth values i)
                         scale (.getClientProperty s :rvs-scale)
                         bias (.getClientProperty s :rvs-bias)
                         pos (int (+ bias (* scale v)))]
                     (.setValue s pos))))
               (reset! enable-change-listener* true)))]
    ed))

(defn- efx-editor [performance ied]
  (let [enable-change-listener* (atom true)
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (or (.getClientProperty src :scale) 1.0)
                                    bias (or (.getClientProperty src :bias) 0.0)
                                    pos (.getValue src)
                                    value (float (+ bias (* scale pos)))]
                                (.set-param! ied param value)))))
        spin-delay1 (let [s (ss/spinner :model (ss/spinner-model 0.0 :from 0.0 :to 1.0 :by 0.01))]
                      (.putClientProperty s :param :echo-delay-1)
                      (.putClientProperty s :scale 1.0)
                      (.putClientProperty s :bias  0.0)
                      (.putClientProperty s :rvs-scale 1.0)
                      (.putClientProperty s :rvs-bias  0.0)
                      (.addChangeListener s change-listener)
                      s)
        pan-delay1 (ss/border-panel :center spin-delay1
                                    :west (ss/label :text "Delay 1 "))
        spin-delay2 (let [s (ss/spinner :model (ss/spinner-model 0.0 :from 0.0 :to 1.0 :by 0.01))]
                      (.putClientProperty s :param :echo-delay-2)
                      (.putClientProperty s :scale 1.0)
                      (.putClientProperty s :bias  0.0)
                      (.putClientProperty s :rvs-scale 1.0)
                      (.putClientProperty s :rvs-bias  0.0)
                      (.addChangeListener s change-listener)
                      s)
        pan-delay2 (ss/border-panel :center spin-delay2
                                    :west (ss/label :text "Delay 2 "))
        pan-delay-time (ss/grid-panel :rows 1 :items [pan-delay1 pan-delay2])
        slide-feedback (let [s (ss/slider :orientation :vertical
                                          :value 0 :min -100 :max 100
                                          :minor-tick-spacing 5
                                          :major-tick-spacing 25
                                          :snap-to-ticks? true
                                          :paint-labels? true)]
                         (.putClientProperty s :param :echo-fb)
                         (.putClientProperty s :scale 1/100)
                         (.putClientProperty s :bias 0.0)
                         (.putClientProperty s :rvs-scale 100)
                         (.putClientProperty s :rvs-bias 0)
                         (.addChangeListener s change-listener)
                         s)
        pan-feedback (ss/border-panel :center slide-feedback
                                      :south (ss/label :text "Feedback"
                                                       :halign :center))
        slide-hf-damp (let [s (ss/slider :orientation :vertical
                                         :value 0 :min 0 :max 100
                                         :major-tick-spacing 25
                                         :minor-tick-spacing 5
                                         :paint-labels? true
                                         :snap-to-ticks? true)]
                        (.putClientProperty s :param :echo-hf-damp)
                        (.putClientProperty s :scale 1/100)
                        (.putClientProperty s :bias 0.0)
                        (.putClientProperty s :rvs-scale 100)
                        (.putClientProperty s :rvs-bias 0)
                        (.addChangeListener s change-listener)
                        s)
        pan-hf-damp (ss/border-panel :center slide-hf-damp
                                     :south (ss/label :text "HF Damp"
                                                      :halign :center))
        slide-delay-mix (let [s (ss/slider :orientation :vertical
                                           :value 0 :min 0 :max 100
                                           :major-tick-spacing 25
                                           :minor-tick-spacing 5
                                           :snap-to-ticks? true
                                           :paint-labels? true)]
                          (.putClientProperty s :param :echo-mix)
                          (.putClientProperty s :scale 1/100)
                          (.putClientProperty s :bias 0.0)
                          (.putClientProperty s :rvs-scale 100)
                          (.putClientProperty s :rvs-bias 0)
                          (.addChangeListener s change-listener)
                          s)
        pan-delay-mix (ss/border-panel :center slide-delay-mix
                                       :south (ss/label :text "Mix" 
                                                        :halign :center))
        pan-delay (ss/border-panel :north pan-delay-time
                                   :center (ss/horizontal-panel
                                            :items [pan-feedback pan-hf-damp pan-delay-mix])
                                   :border (factory/title "Delay"))
        ;; Reverb
        slide-room-size (let [s (ss/slider :orientation :vertical
                                           :value 0 :min 0 :max 100
                                           :major-tick-spacing 25
                                           :paint-labels? true
                                           :snap-to-ticks? false)]
                          (.putClientProperty s :param :room-size)
                          (.putClientProperty s :scale 1/100)
                          (.putClientProperty s :bias 0.0)
                          (.putClientProperty s :rvs-scale 100)
                          (.putClientProperty s :rvs-bias 0)
                          (.addChangeListener s change-listener)
                          s)
        slide-reverb-mix (let [s (ss/slider :orientation :vertical
                                            :value 0 :min 0 :max 100
                                            :major-tick-spacing 25
                                            :paint-labels? true
                                            :snap-to-ticks? false)]
                           (.putClientProperty s :param :reverb-mix)
                           (.putClientProperty s :scale 1/100)
                           (.putClientProperty s :bias 0.0)
                           (.putClientProperty s :rvs-scale 100)
                           (.putClientProperty s :rvs-bias 0)
                           (.addChangeListener s change-listener)
                           s)
        pan-room-size (ss/border-panel :center slide-room-size
                                       :south (ss/label :text "Room Size"
                                                        :halign :center))
        pan-reverb-mix (ss/border-panel :center slide-reverb-mix
                                        :south (ss/label :text "Mix"
                                                         :halign :center))
        pan-reverb (ss/horizontal-panel :items [pan-room-size pan-reverb-mix]
                                        :border (factory/title "Reverb"))
        ;; Main out
        slide-lowpass (let [s (ss/slider :orientation :vertical
                                         :value 0 :min 4000 :max 10000
                                         :major-tick-spacing 1000
                                         :paint-labels? true
                                         :snap-to-ticks? false)]
                        (.putClientProperty s :param :lp)
                        (.putClientProperty s :scale 1.0)
                        (.putClientProperty s :bias 0.0)
                        (.putClientProperty s :rvs-scale 1.0)
                        (.putClientProperty s :rvs-bias 0)
                        (.addChangeListener s change-listener)
                        s)
        slide-amp (let [s (ss/slider :orientation :vertical
                                     :value 0 :min 0 :max 100
                                     :major-tick-spacing 25
                                     :paint-labels? true
                                     :snap-to-ticks? false)]
                    (.putClientProperty s :param :amp)
                    (.putClientProperty s :scale 1/100)
                    (.putClientProperty s :bias 0.0)
                    (.putClientProperty s :rvs-scale 100)
                    (.putClientProperty s :rvs-bias 0)
                    (.addChangeListener s change-listener)
                    s)
        pan-lowpass (ss/border-panel :center slide-lowpass
                                     :south (ss/label :text "Lowpass"
                                                      :halign :center))
        pan-amp (ss/border-panel :center slide-amp
                                 :south (ss/label :text "Amp"
                                                  :halign :center))
        pan-out (ss/horizontal-panel :items [pan-lowpass pan-amp]
                                     :border (factory/title "Out"))
        pan-main (ss/horizontal-panel :items [pan-delay
                                              pan-reverb
                                              pan-out])
        widget-map {:pan-main pan-main}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo efx-editor does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo efx-editor init method not implemented"))
               )
            
             (sync-ui! [this]
               (reset! enable-change-listener* false)
               (let [data (.current-data (.bank performance))
                     delay-1 (float (get data :echo-delay-1 0.0))
                     delay-2 (float (get data :echo-delay-2 0.0))
                     fb (float (get data :echo-fb 0.0))
                     hf-damp (float (get data :echo-hf-damp 0.0))
                     delay-mix (float (get data :echo-mix 0.0))
                     room-size (float (get data :room-size 0.5))
                     reverb-mix (float (get data :reverb-mix 0.0))
                     amp (float (get data :amp 0.2))
                     values [fb hf-damp delay-mix room-size reverb-mix amp]
                     sliders [slide-feedback slide-hf-damp slide-delay-mix slide-room-size slide-reverb-mix slide-amp]]
                 (dotimes [i (count values)]
                   (let [s (nth sliders i)
                         v (nth values i)
                         scale (.getClientProperty s :rvs-scale)
                         bias (.getClientProperty s :rvs-bias)
                         pos (int (+ bias (* scale v)))]
                     (.setValue s pos)))
                 (.setValue spin-delay1 (Double. delay-1))
                 (.setValue spin-delay2 (Double. delay-2))
                 (reset! enable-change-listener* true))) )]
    ed))




(defn  feedback-efx-editor [performance ied]
  (let [op6fb (op-fb-editor 6 performance ied)
        op8fb (op-fb-editor 8 performance ied)
        efx (efx-editor performance ied)
        pan-fb (ss/horizontal-panel :items [(.widget op6fb :pan-main)
                                            (.widget op8fb :pan-main)])
        pan-main (ss/horizontal-panel :items [pan-fb
                                              (.widget efx :pan-main)
                                              ])
        widget-map {:pan-main pan-main
                    :op6 op6fb
                    :op8 op8fb}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo feedback-editor does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo feedback-editor init method not implemented"))
               )
            
             (sync-ui! [this]
               (.sync-ui! op6fb)
               (.sync-ui! op8fb)
               (.sync-ui! efx)
               ))]
    (.add-sub-editor! ied "Feedback" ed)))
