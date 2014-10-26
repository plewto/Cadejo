(ns cadejo.instruments.algo.editor.overview
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [seesaw.core :as ss]))


(defn overview-editor [performance ied]
  (let [op-count 8
        title-labels [(ss/label :text "Freq")
                      (ss/label :text "Bias")
                      (ss/label :text "Amp")
                      (ss/label :text "Enable")]
       
        freq-labels (let [acc* (atom [])]
                      (doseq [i (range op-count)]
                        (let [lab (ss/label :text "fxx.xxx")]
                          (swap! acc* (fn [q](conj q lab)))))
                      @acc*)
        bias-labels (let [acc* (atom [])]
                      (doseq [i (range op-count)]
                        (let [lab (ss/label :text "bxxx")]
                          (swap! acc* (fn [q](conj q lab)))))
                      @acc*)
        amp-labels (let [acc* (atom [])]
                      (doseq [i (range op-count)]
                        (let [lab (ss/label :text "axx.xxx")]
                          (swap! acc* (fn [q](conj q lab)))))
                      @acc*)
        enable-action (fn [ev]
                      (let [src (.getSource ev)
                            param (.getClientProperty src :param)
                            val (if (.isSelected src) 1.0 0.0)]
                        (.set-param! ied param val)
                        (.sync-ui! ied)))
                     
        enable-buttons (let [acc* (atom [])]
                       (doseq [op (range 1 (inc op-count))]
                         (let [b (ss/toggle :text (format "op %d" op) :selected? true)]
                           (.putClientProperty b :param (keyword (format "op%d-enable" op)))
                           (swap! acc* (fn [q](conj q b)))
                           (ss/listen b :action enable-action)))
                       @acc*)
        pan-freq (ss/grid-panel :rows 1 :items (flatten [(nth title-labels 0) freq-labels]))
        pan-bias (ss/grid-panel :rows 1 :items (flatten [(nth title-labels 1) bias-labels]))
        pan-amp (ss/grid-panel :rows 1 :items (flatten [(nth title-labels 2) amp-labels]))
        pan-enable (ss/grid-panel :rows 1 :items (flatten [(nth title-labels 3) enable-buttons]))
        pan-main (ss/vertical-panel :items [pan-freq pan-bias pan-amp pan-enable])
        
        widget-map {:pan-main pan-main}
        oed (reify subedit/InstrumentSubEditor

              (widgets [this] widget-map)

              (widget [this key](get widget-map key))
 
              (parent [this] ied)

              (parent! [this _] ) ;; ignore

              (status! [this msg]
                (.status! ied msg))

              (warning! [this msg]
                (.warning! ied msg))

              (init! [this]
                (doseq [op (range 1 (inc op-count))]
                  (let [param (keyword (format "op%d-enable" op))]
                    (.set-param! ied param 1.0))))

              (sync-ui! [this]
                  (let [data (.current-data (.bank performance))
                        freqlst [(:op1-detune data)(:op2-detune data)
                                 (:op3-detune data)(:op4-detune data)
                                 (:op5-detune data)(:op6-detune data)
                                 (:op7-detune data)(:op8-detune data)]
                        biaslst [(:op1-bias data)(:op2-bias data)
                                 (:op3-bias data)(:op4-bias data)
                                 (:op5-bias data)(:op6-bias data)
                                 (:op7-bias data)(:op8-bias data)]
                        amplst [(:op1-amp data)(:op2-amp data)
                                 (:op3-amp data)(:op4-amp data)
                                 (:op5-amp data)(:op6-amp data)
                                 (:op7-amp data)(:op8-amp data)]
                        enablelst [(:op1-enable data)(:op2-enable data)
                                 (:op3-enable data)(:op4-enable data)
                                 (:op5-enable data)(:op6-enable data)
                                 (:op7-enable data)(:op8-enable data)]]
                    (dotimes [op op-count]
                      (let [f (nth freqlst op)
                            flab (nth freq-labels op)
                            b (nth biaslst op)
                            blab (nth bias-labels op)
                            a (nth amplst op)
                            alab (nth amp-labels op)
                            e (nth enablelst op)
                            jb (nth enable-buttons op)]
                        (ss/config! flab :text (format "%7.4f" (or (float f) 0.0)))
                        (ss/config! blab :text (format "%4.2f" (or (float b) 0.0)))
                        (ss/config! alab :text (format "%6.3f" (or (float a) 0.0)))
                        (ss/config! jb :selected? (not (zero? e))))))))]
    oed))
                
