(ns cadejo.instruments.algo.op-freq-editor
  (:require [cadejo.util.math :as math])
  (:require [seesaw.core :as ss])
  (:import java.util.Hashtable
           javax.swing.event.ChangeListener))

(defn- tune-labels []
  (Hashtable. {(int 0)(ss/label :text "1.0")
               (int 1)(ss/label :text "1.1")
               (int 2)(ss/label :text "1.2")
               (int 3)(ss/label :text "1.3")
               (int 4)(ss/label :text "1.4")
               (int 5)(ss/label :text "1.5")
               (int 6)(ss/label :text "1.6")
               (int 7)(ss/label :text "1.7")
               (int 8)(ss/label :text "1.8")
               (int 9)(ss/label :text "1.9")
               (int 10)(ss/label :text "2.0")}))

(defn- finetune-labels []
  (Hashtable. {(int 0)(ss/label :text "0.00")
               (int 100)(ss/label :text "0.10")
               (int 200)(ss/label :text "0.20")
               (int 300)(ss/label :text "0.30")
               (int 400)(ss/label :text "0.40")
               (int 500)(ss/label :text "0.50")
               (int 600)(ss/label :text "0.60")
               (int 700)(ss/label :text "0.70")
               (int 800)(ss/label :text "0.80")
               (int 900)(ss/label :text "0.90")
               (int 1000)(ss/label :text "1.00")}))

;; Provides editor controls related to operator frequency.
;; op - operator number, 1 2 3 ... 8
;; performance - an instance of Performance
;; ied - the parent editor, an instance of InstrumentEditor or Subeditor.
;; 
;; returns two element map with keys
;; :pan-main - the swing panel which contains the editor
;; :sync-ui - a function which updates the editor controls.
;;
(defn op-freq-editor [op performance ied]
  (let [param-freq (keyword (format "op%d-detune" op))
        param-bias (keyword (format "op%d-bias" op))
        lab-freq (ss/label :text "xx.xxxx")
        enable-change-listener* (atom true)
        base* (atom 1.0)
        tune* (atom 1.0)
        finetune* (atom 0.0)
        bias* (atom 0.0)
        update-synths (fn []
                        (let [freq (float (* @base* (+ @tune* @finetune*)))]
                          (ss/config! lab-freq :text (format "%7.4f" freq))
                          (.set-param! ied param-freq freq)))
        slide-tune (let [s (ss/slider :orientation :vertical
                                      :value 0 :min 0 :max 10
                                      :snap-to-ticks? true
                                      :paint-labels? true
                                      :minor-tick-spacing 1)]
                     (.setLabelTable s (tune-labels))
                     (.addChangeListener s (proxy [ChangeListener][]
                                             (stateChanged [ev]
                                               (if @enable-change-listener*
                                                 (let [src (.getSource ev)
                                                       pos (.getValue src)
                                                       val (float (+ 1 (* 1/10 pos)))]
                                                   (reset! tune* val)
                                                   (update-synths))))))
                     s)
        slide-finetune (let [s (ss/slider :orientation :vertical
                                          :value 0 :min 0 :max 1000
                                          :snap-to-ticks? false
                                          :paint-labels? true
                                          :minor-tick-spacing 100)]
                         (.setLabelTable s (finetune-labels))
                         (.addChangeListener s (proxy [ChangeListener][]
                                                 (stateChanged [ev]
                                                   (if @enable-change-listener*
                                                     (let [src (.getSource ev)
                                                           pos (.getValue src)
                                                           val (float (* 1/1000 pos))]
                                                       (reset! finetune* val)
                                                       (update-synths))))))
                         s)
        octaves (let [acc* (atom [])
                      grp (ss/button-group)]
                  (doseq [oct [0 1/2 1 2 4 8 16 32]]
                    (let [tb (ss/toggle :text (format "%3s" (if (> oct 0) oct "OFF"))
                                        :group grp)]
                      (swap! acc* (fn [n](conj n tb)))
                      (.putClientProperty tb :value (float oct))
                      (ss/listen tb :action (fn [ev]
                                              (let [src (.getSource ev)
                                                    base (.getClientProperty src :value)]
                                                (.setEnabled slide-tune (> oct 0))
                                                (.setEnabled slide-finetune (> oct 0))
                                                (reset! base* base)
                                                (update-synths))))))
                      (.setSelected (nth @acc* 3) true)
                      (reverse @acc*))
        spin-bias (let [s (ss/spinner :model (ss/spinner-model 0.0 
                                                               :from -10000.0 
                                                               :to 10000.0 
                                                               :by 0.1))]
                        (.addChangeListener s (proxy [ChangeListener][]
                                                (stateChanged [ev]
                                                  (if @enable-change-listener*
                                                    (let [src (.getSource ev)
                                                          val (float (.getValue src))]
                                                      (.set-param! ied param-bias val))))))
                        s)
        pan-octaves (ss/border-panel :center (ss/grid-panel :columns 1 :items octaves)
                                     :south (ss/label :text "Octave" :halign :center))
        pan-tune (ss/border-panel :center slide-tune
                                  :south (ss/label :text "Tune" :halign :center))
        pan-finetune (ss/border-panel :center slide-finetune
                                      :south (ss/label :text "Fine" :halign :center))
        pan-bias (ss/border-panel :west "  Bias "
                                  :center spin-bias)
        pan-south (ss/horizontal-panel :items [lab-freq pan-bias])
        pan-main (ss/border-panel :center (ss/horizontal-panel 
                                           :items [pan-octaves pan-tune pan-finetune])
                                  :south pan-south)
        sync-ui! (fn []
                  (reset! enable-change-listener* false)
                  (let [data (.current-data (.bank performance))
                        bias (get data param-bias 0.0)
                        freq (math/abs (get data param-freq 1.0))]
                    (.setValue spin-bias bias)
                    (if (not (= freq 0.0))
                      (let [oct (if (< freq 1) -1 (int (math/log2 freq)))
                            base (math/expt 2.0 oct)
                            detune (/ freq base)
                            coarse (/ (int (* detune 10)) 10.0)
                            fine (- detune coarse)]
                        (.setSelected (nth octaves (get {0.50 6, 1.0 5, 2.0 4, 8.0 2,
                                                         16.0 1, 32.0 0} base 5)) true)
                        (.setEnabled slide-tune true)
                        (.setEnabled slide-finetune true)
                        (.setValue slide-tune (int (- (* 10 coarse) 10)))
                        (.setValue slide-finetune (int (* 1000 fine)))
                        (reset! base* base)
                        (reset! tune* coarse)
                        (reset! finetune* fine)
                        (reset! bias* bias)
                        (ss/config! lab-freq :text (format "%7.4f" (float freq))))
                      (do 
                        (.setSelected (last octaves) true)
                        (.setEnabled slide-tune false)
                        (.setEnabled slide-finetune false)
                        (ss/config! lab-freq :text (format "%7.4f" (float freq))))))
                  (reset! enable-change-listener* true))]
    {:pan-main pan-main
     :sync-ui sync-ui!}))
