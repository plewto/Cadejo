(ns cadejo.instruments.algo.op-freq-editor
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import java.util.Hashtable
           javax.swing.event.ChangeListener))



;; Provides editor controls related to operator frequency.
;; op - operator number, 1 2 3 ... 8
;; performance - an instance of Performance
;; ied - the parent editor, an instance of InstrumentEditor or Subeditor.
;; 
;; returns two element map with keys
;; :pan-main - the swing panel which contains the editor
;; :syncfn - a function which updates the editor controls.
;; :mutefn - a function (fn flag) if flag is true disable all components
;; 
(defn op-freq-editor [op performance ied]
  (let [enable-change-listener* (atom true)
        param-freq (keyword (format "op%d-detune" op))
        param-bias (keyword (format "op%d-bias" op))
        spin-tune (ss/spinner :model (ss/spinner-model 1.0 :from 0.0 :to 64.0 :by 1.0))
        spin-bias (ss/spinner :model (ss/spinner-model 0.0 :from -5000.0 :to 5000.0 :by 1.0))
        lab-tune (ss/label :text "Tune " :halign :center)
        lab-bias (ss/label :text "Bias " :halign :center)
        pan-tune (ss/border-panel :south lab-tune :center spin-tune)
        pan-bias (ss/border-panel :south lab-bias :center spin-bias)
        pan-main (ss/grid-panel :columns 1
                                :items [pan-tune 
                                        pan-bias 
                                        ;; (ss/vertical-panel)
                                        ;; (ss/vertical-panel)
                                        ;; (ss/vertical-panel)
                                        ]
                                :border (factory/title "Frequency"))
        syncfn (fn []
                 (reset! enable-change-listener* false)
                 (let [data (.current-data (.bank performance))
                       tune (float (get data param-freq 1.0))
                       bias (float (get data param-bias 0.0))]
                   (.setValue spin-tune (Double. tune))
                   (.setValue spin-bias (Double. bias)))
                 (reset! enable-change-listener* true))
        mutefn (fn [flag]
                 (let [f (not flag)]
                   (.setEnabled spin-tune f)
                   (.setEnabled spin-bias f)
                   (.setEnabled lab-tune f)
                   (.setEnabled lab-bias f)))]
    (.addChangeListener spin-tune (proxy [ChangeListener][]
                                    (stateChanged [_]
                                      (if @enable-change-listener*
                                        (let [val (float (.getValue spin-tune))]
                                          (.set-param! ied param-freq val))))))
    (.addChangeListener spin-bias (proxy [ChangeListener][]
                                    (stateChanged [_]
                                      (if @enable-change-listener* 
                                        (let [val (float (.getValue spin-bias))]
                                          (.set-param! ied param-bias val))))))
    {:pan-main pan-main
     :syncfn syncfn
     :mutefn mutefn}))
        
                                  
        
      
       
