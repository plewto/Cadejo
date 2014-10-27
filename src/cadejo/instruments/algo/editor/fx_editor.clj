;; LFO1, LFO2, feedback, Delay 1 Delay 2, Reverb, overall amp
;; 
(ns cadejo.instruments.algo.editor.fx-editor
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:import java.util.Hashtable
           javax.swing.event.ChangeListener))

(def ^:private slider-width 50)
(def ^:private slider-height 100)

(defn- slider 
  ([param labtab]
     (slider param 0.0 1.0 labtab))
  ([param minval maxval labtab]
     (let [steps 100 
           isteps 1/100
           
           delta (float (- maxval minval))
           scale (* isteps delta)
           bias minval
           rvs-scale (/ steps delta)
           rvs-bias (/ (* -1 steps minval) delta)
           s (ss/slider :orientation :vertical
                        :value 0 :min 0 :max steps
                        :snap-to-ticks? false
                        :paint-labels? labtab
                        :minor-tick-spacing 5
                        :major-tick-spacing 25
                        :size [slider-width :by slider-height])]
       (if (= (type labtab) Hashtable) 
         (.setLabelTable s labtab))
       (.putClientProperty s :scale scale)
       (.putClientProperty s :bias bias)
       (.putClientProperty s :rvs-scale rvs-scale)
       (.putClientProperty s :rvs-bias rvs-bias)
       (.putClientProperty s :param param)
       s)))

(defn- label [text]
  (ss/label :text (str text) 
            :halign :center))

(defn- set-slider [slider data]
  (let [param (.getClientProperty slider :param)
        rscale (.getClientProperty slider :rvs-scale)
        rbias (.getClientProperty slider :rvs-bias)
        val (param data)
        pos (int (+ rbias (* rscale val)))]
    (.setValue slider pos)))

; ---------------------------------------------------------------------- 
;                                 Feedback
;
(defn- fb-label-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int 0) (ss/label :text "0.0"))
    (.put ht (int 25) (ss/label :text "1.5"))
    (.put ht (int 50) (ss/label :text "3.0"))
    (.put ht (int 75) (ss/label :text "4.5"))
    (.put ht (int 100) (ss/label :text "6.0"))
    ht))

(defn- fbmod-label-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int 0) (ss/label :text "-2"))
    (.put ht (int 25) (ss/label :text "-1"))
    (.put ht (int 50) (ss/label :text " 0"))
    (.put ht (int 75) (ss/label :text "+1"))
    (.put ht (int 100) (ss/label :text "+2"))
    ht))

(defn- highpass-label-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int 0) (ss/label :text "  1"))
    (.put ht (int 25) (ss/label :text " 50"))
    (.put ht (int 50) (ss/label :text "100"))
    (.put ht (int 75) (ss/label :text "150"))
    (.put ht (int 100) (ss/label :text "200"))
    ht))

(defn feedback-editor [op ied]
  (let [enable-change-listener* (atom true)
        param-fb (keyword (format "op%d-feedback" op))
        param-env (keyword (format "op%d-env->feedback" op))
        param-pressure (keyword (format "op%d-pressure->feedback" op))
        param-cca (keyword (format "op%d-cca->feedback" op))
        param-ccb (keyword (format "op%d-ccb->feedback" op))
        param-lfo (keyword (format (if (= op 6) "op%d-lfo1->feedback" "op%d-lfo2->feedback") op))
        param-hp (keyword (format "op%d-hp" op))

        slide-fb (slider param-fb 0 6 (fb-label-table))
        slide-env (slider param-env -2 2 (fbmod-label-table))
        slide-pressure (slider param-pressure -2 2 (fbmod-label-table))
        slide-cca (slider param-cca -2 2 (fbmod-label-table))
        slide-ccb (slider param-ccb -2 2 (fbmod-label-table))
        slide-lfo (slider param-lfo -2 2 (fbmod-label-table))
        slide-hp (slider param-hp 1 200 (highpass-label-table))

        lab-fb (label "FB")
        lab-env (label "Env")
        lab-pressure (label "Press")
        lab-cca (label "CCA")
        lab-ccb (label "CCB")
        lab-lfo (if (= op 6)(label "LFO1")(label "LFO2"))
        lab-hp (label "HP")
        elements [slide-fb slide-env slide-pressure slide-cca slide-ccb
                  slide-lfo slide-hp lab-fb lab-env lab-pressure lab-cca
                  lab-ccb lab-lfo lab-hp]
        pan-fb (ss/border-panel :center slide-fb :south lab-fb)
        pan-env (ss/border-panel :center slide-env :south lab-env)
        pan-pressure (ss/border-panel :center slide-pressure :south lab-pressure)
        pan-cca (ss/border-panel :center slide-cca :south lab-cca)
        pan-ccb (ss/border-panel :center slide-ccb :south lab-ccb)
        pan-lfo (ss/border-panel :center slide-lfo :south lab-lfo)
        pan-hp (ss/border-panel :center slide-hp :south lab-hp)
        pan-main (ss/horizontal-panel :items [pan-fb pan-env pan-lfo
                                              pan-pressure pan-cca pan-ccb
                                              pan-hp]
                                      :border (factory/title (format "OP%d Feedback" op)))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (+ bias (* scale pos))]
                                (.set-param! ied param val)))))
                     
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (let [enable (not (zero? ((keyword (format "op%d-enable" op)) data)))]
                   (set-slider slide-fb data)
                   (set-slider slide-env data)
                   (set-slider slide-pressure data)
                   (set-slider slide-cca data)
                   (set-slider slide-ccb data)
                   (set-slider slide-lfo data)
                   (set-slider slide-hp data)
                   (doseq [e elements]
                     (.setEnabled e enable)))
                 (reset! enable-change-listener* true))]
                   
    (.addChangeListener slide-fb slider-listener)
    (.addChangeListener slide-env slider-listener)
    (.addChangeListener slide-pressure slider-listener)
    (.addChangeListener slide-cca slider-listener)
    (.addChangeListener slide-ccb slider-listener)
    (.addChangeListener slide-lfo slider-listener)
    (.addChangeListener slide-hp slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))

; ---------------------------------------------------------------------- 
;                                  Vibrato
;

(defn-  vibrato-delay-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0) (ss/label :text "0"))
    (.put ht (int  25) (ss/label :text "2"))
    (.put ht (int  50) (ss/label :text "4"))
    (.put ht (int  75) (ss/label :text "6"))
    (.put ht (int 100) (ss/label :text "8"))
    ht))

(defn vibrato-editor [ied]
  (let [enable-change-listener* (atom true)
        spin-freq (ss/spinner :model (ss/spinner-model 1.0 :from 1.0 :to 15.0 :by 1.0))
        slide-sens (slider :vsens 0.0 0.1 true)
        slide-depth (slider :vdepth 0.0 1.0 true)
        slide-delay (slider :vdelay 0.0 8.0 (vibrato-delay-table))
        lab-freq (ss/label :text "Freq")
        lab-sens (label "Sens")
        lab-depth (label "Depth")
        lab-delay (label "Delay")
        pan-freq (ss/border-panel :west lab-freq :center spin-freq)
        pan-sens (ss/border-panel :center slide-sens :south lab-sens)
        pan-depth (ss/border-panel :center slide-depth :south lab-depth)
        pan-delay (ss/border-panel :center slide-delay :south lab-delay)
        pan-center (ss/horizontal-panel :items [pan-sens pan-depth pan-delay])
        pan-main (ss/border-panel :center pan-center 
                                  :south pan-freq
                                  :border (factory/title "Vibrato"))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (float (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))

        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-sens data)
                 (set-slider slide-depth data)
                 (set-slider slide-delay data)
                 (.setValue spin-freq (double (:vfreq data)))
                 (reset! enable-change-listener* true))]
    (.putClientProperty spin-freq :param :vfreq)
    (.putClientProperty spin-freq :scale 1.0)
    (.putClientProperty spin-freq :bias 0.0)
    (.putClientProperty spin-freq :rvs-scale 1.0)
    (.putClientProperty spin-freq :rvs-bias 0.0)
    (.addChangeListener spin-freq slider-listener)
    (.addChangeListener slide-sens slider-listener)
    (.addChangeListener slide-depth slider-listener)
    (.addChangeListener slide-delay slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))
                 

; ---------------------------------------------------------------------- 
;                                    LFO


(defn- lfo-freq-label-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "-16"))
    (.put ht (int  25)(ss/label :text " -8"))
    (.put ht (int  50)(ss/label :text "  0"))
    (.put ht (int  75)(ss/label :text " +8"))
    (.put ht (int 100)(ss/label :text "+16"))
    ht))

(defn- lfo-skew-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "0.00 \\"))
    (.put ht (int  25)(ss/label :text "0.25"))
    (.put ht (int  50)(ss/label :text "0.50 /\\"))
    (.put ht (int  75)(ss/label :text "0.75"))
    (.put ht (int 100)(ss/label :text "1.00 /"))
    ht))

(defn- lfo-skew-mod-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "-1.0"))
    (.put ht (int  25)(ss/label :text "-0.5"))
    (.put ht (int  50)(ss/label :text " 0.0"))
    (.put ht (int  75)(ss/label :text "+0.5"))
    (.put ht (int 100)(ss/label :text "+1.0"))
    ht))

(defn lfo-editor [id ied]
  (let [enable-change-listener* (atom false)
        param-freq (keyword (format "lfo%d-freq" id))           ; 0 - 16
        param-freq-cca (keyword (format "cca->lfo%d-freq" id))  ; +/- 16
        param-freq-ccb (keyword (format "ccb->lfo%d-freq" id))  ; +/- 16
 
        param-skew (keyword (format "lfo%d-skew" id))           ; 0.0 - 1.0
        param-skew-mod (keyword (if (= id 1)                    ; +/- 1.0
                                  "env1->lfo1-skew"
                                  "lfo1->lfo2-skew"))
        param-amp-mod (keyword (if (= id 1)                     ; 0.0 - 1.0
                                 "env1->lfo1-amp"
                                 "lfo1->lfo2-amp"))
        param-amp-pressure (keyword (format "pressure->lfo%d-amp" id))
        param-amp-cca (keyword (format "cca->lfo%d-amp" id))
        param-amp-ccb (keyword (format "ccb->lfo%d-amp" id))

        spin-freq (ss/spinner :model (ss/spinner-model 5.0 :from 0.1 :to 16.0 :by 1.0))
        slide-freq-cca (slider param-freq-cca -16 16 (lfo-freq-label-table))
        slide-freq-ccb (slider param-freq-ccb -16 16 (lfo-freq-label-table))

        slide-skew (slider param-skew 0.0 1.0 (lfo-skew-table))
        slide-skew-mod (slider param-skew-mod -1.0 1.0 (lfo-skew-mod-table))

        slide-amp-mod (slider param-amp-mod 0.0 1.0 true)
        slide-amp-pressure (slider param-amp-pressure 0.0 1.0 true)
        slide-amp-cca (slider param-amp-cca 0.0 1.0 true)
        slide-amp-ccb (slider param-amp-ccb 0.0 1.0 true)

        lab-freq (ss/label :text "Freq")
        lab-freq-cca (label "CCA")
        lab-freq-ccb (label "CCB")
        pan-freq (ss/border-panel :center (ss/horizontal-panel
                                           :items [(ss/border-panel :center slide-freq-cca
                                                                    :south lab-freq-cca)
                                                   (ss/border-panel :center slide-freq-ccb
                                                                    :south lab-freq-ccb)])
                                  :south spin-freq
                                  :border (factory/title "Freq"))
        lab-skew (label "Skew")
        lab-skew-mod (label (if (= id 1) "Env1" "LFO1"))
        pan-skew (ss/horizontal-panel :items [(ss/border-panel :center slide-skew
                                                               :south lab-skew)
                                              (ss/border-panel :center slide-skew-mod
                                                               :south lab-skew-mod)]
                                      :border (factory/title "Skew"))
        
        lab-amp-mod (label (if (= id 1) "Env1" "LFO1"))
        lab-amp-pressure (label "Prss")
        lab-amp-cca (label "CCA")
        lab-amp-ccb (label "CCB")
        pan-amp (ss/horizontal-panel :items [(ss/border-panel :center slide-amp-mod
                                                              :south lab-amp-mod)
                                             (ss/border-panel :center slide-amp-pressure
                                                              :south lab-amp-pressure)
                                             (ss/border-panel :center slide-amp-cca
                                                              :south lab-amp-cca)
                                             (ss/border-panel :center slide-amp-ccb
                                                              :south lab-amp-ccb)]
                                     :border (factory/title "Amp"))
        pan-main (ss/horizontal-panel :items [pan-freq pan-skew pan-amp]
                                      :border (factory/title (if (= id 1) "LFO1" "LFO2")))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (float (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))

        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-freq-cca data)
                 (set-slider slide-freq-ccb data)
                 (set-slider slide-skew data)
                 (set-slider slide-skew-mod data)
                 (set-slider slide-amp-mod data)
                 (set-slider slide-amp-pressure data)
                 (set-slider slide-amp-cca data)
                 (set-slider slide-amp-ccb data)
                 (.setValue spin-freq (double (param-freq data)))
                 (reset! enable-change-listener* true))]
    (.putClientProperty spin-freq :param param-freq)
    (.putClientProperty spin-freq :scale 1.0)
    (.putClientProperty spin-freq :bias 0.0)
    (.putClientProperty spin-freq :rvs-scale 1.0)
    (.putClientProperty spin-freq :rvs-bias 0.0)
    (.addChangeListener spin-freq slider-listener)
    (.addChangeListener slide-freq-cca slider-listener)
    (.addChangeListener slide-freq-ccb slider-listener)
    (.addChangeListener slide-skew slider-listener)
    (.addChangeListener slide-skew-mod slider-listener)
    (.addChangeListener slide-amp-mod slider-listener)
    (.addChangeListener slide-amp-pressure slider-listener)
    (.addChangeListener slide-amp-cca slider-listener)
    (.addChangeListener slide-amp-ccb slider-listener)
    {:pan-main pan-main
     :syncfn syncfn} ))


; ---------------------------------------------------------------------- 
;                                   Pitch

(defn- pitch-env-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "-1.0"))
    (.put ht (int  25)(ss/label :text "-0.5"))
    (.put ht (int  50)(ss/label :text " 0.0"))
    (.put ht (int  75)(ss/label :text "+0.5"))
    (.put ht (int 100)(ss/label :text "+1.0"))
    ht))

(defn- pitch-lfo-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "0.00"))
    (.put ht (int  25)(ss/label :text "0.25"))
    (.put ht (int  50)(ss/label :text "0.50"))
    (.put ht (int  75)(ss/label :text "0.75"))
    (.put ht (int 100)(ss/label :text "1.00"))
    ht))

(defn- pitch-port-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "0.00"))
    (.put ht (int  25)(ss/label :text "0.25"))
    (.put ht (int  50)(ss/label :text "0.50"))
    (.put ht (int  75)(ss/label :text "0.75"))
    (.put ht (int 100)(ss/label :text "1.00"))
    ht))

(defn- pitch-editor [ied]
  (let [enable-change-listener* (atom true)
        param-env :env1->pitch
        param-lfo1 :lfo1->pitch
        param-lfo2 :lfo2->pitch
        param-port :port-time
        slide-env (slider param-env -1.0 1.0 (pitch-env-table))
        slide-lfo1 (slider param-lfo1 0.0 1.0 (pitch-lfo-table))
        slide-lfo2 (slider param-lfo2 0.0 1.0 (pitch-lfo-table))
        slide-port (slider param-port 0.0 1.0 (pitch-port-table))
        lab-env (label "Env1")
        lab-lfo1 (label "LFO1")
        lab-lfo2 (label "LFO2")
        lab-port (label "Port")
        pan-main (ss/horizontal-panel :items [(ss/border-panel :center slide-env 
                                                               :south lab-env)
                                              (ss/border-panel :center slide-lfo1
                                                               :south lab-lfo1)
                                              (ss/border-panel :center slide-lfo2
                                                               :south lab-lfo2)
                                              (ss/border-panel :center slide-port
                                                               :south lab-port)]
                                      :border (factory/title "Pitch"))

        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-env data)
                 (set-slider slide-lfo1 data)
                 (set-slider slide-lfo2 data)
                 (set-slider slide-port data)
                 (reset! enable-change-listener* true))

        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (float (+ bias (* scale pos)))]
                                (.set-param! ied param val)))))]
    (.addChangeListener slide-env slider-listener)
    (.addChangeListener slide-lfo1 slider-listener)
    (.addChangeListener slide-lfo2 slider-listener)
    (.addChangeListener slide-port slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))


; ---------------------------------------------------------------------- 
;                                   Delay

(defn- delay-fb-table []
  (let [ht (Hashtable. 5)]
    (.put ht (int   0)(ss/label :text "-1.0"))
    (.put ht (int  25)(ss/label :text "-0.5"))
    (.put ht (int  50)(ss/label :text " 0.0"))
    (.put ht (int  75)(ss/label :text "+1.5"))
    (.put ht (int 100)(ss/label :text "+1.0"))
    ht))

(defn- delay-damp-table []
  (pitch-lfo-table))

(defn delay-editor [ied]
  (let [enable-change-listener* (atom true)
        param-delay-1 :echo-delay-1
        param-delay-2 :echo-delay-2
        param-fb :echo-fb
        param-damp :echo-hf-damp
        param-mix :echo-mix
        spin-delay-1 (ss/spinner :model (ss/spinner-model 0.125 :from 0.0 :to 1.0 :by 0.005))
        spin-delay-2 (ss/spinner :model (ss/spinner-model 0.125 :from 0.0 :to 1.0 :by 0.005))
        slide-fb (slider param-fb -1.0 1.0 (delay-fb-table))
        slide-damp (slider param-damp 0.0 1.0 (delay-damp-table))
        slide-mix (slider param-mix 0.0 1.0 (delay-damp-table))
        lab-d1 (ss/label :text "Time 1 ")
        lab-d2 (ss/label :text "Time 2 ")
        lab-fb (label "FB")
        lab-damp (label "Damp")
        lab-mix (label "Mix")
        pan-time (ss/vertical-panel :items [(ss/border-panel :west lab-d1 :center spin-delay-1)
                                            (ss/border-panel :west lab-d2 :center spin-delay-2)])
        pan-center (ss/horizontal-panel :items [(ss/border-panel :south lab-fb :center slide-fb)
                                                (ss/border-panel :south lab-damp :center slide-damp)
                                                (ss/border-panel :south lab-mix :center slide-mix)])
        pan-main (ss/border-panel :center pan-center
                                  :south pan-time
                                  :border (factory/title "Delay"))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (+ bias (* scale pos))]
                                (.set-param! ied param val)))))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-fb data)
                 (set-slider slide-damp data)
                 (set-slider slide-mix data)
                 (.setValue spin-delay-1 (double (param-delay-1 data)))
                 (.setValue spin-delay-2 (double (param-delay-2 data)))
                 (reset! enable-change-listener* true))]
    (.putClientProperty spin-delay-1 :param param-delay-1)
    (.putClientProperty spin-delay-1 :scale 1.0)
    (.putClientProperty spin-delay-1 :bias 0.0)
    (.putClientProperty spin-delay-1 :rvs-scale 1.0)
    (.putClientProperty spin-delay-1 :rvs-bias 0.0)
    (.putClientProperty spin-delay-2 :param param-delay-2)
    (.putClientProperty spin-delay-2 :scale 1.0)
    (.putClientProperty spin-delay-2 :bias 0.0)
    (.putClientProperty spin-delay-2 :rvs-scale 1.0)
    (.putClientProperty spin-delay-2 :rvs-bias 0.0)
    (.addChangeListener spin-delay-1 slider-listener)
    (.addChangeListener spin-delay-2 slider-listener)
    (.addChangeListener slide-fb slider-listener)
    (.addChangeListener slide-damp slider-listener)
    (.addChangeListener slide-mix slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))


; ---------------------------------------------------------------------- 
;                                  Reverb

(defn- reverb-label-table []
  (pitch-lfo-table))
                                         
(defn- reverb-editor [ied]
  (let [enable-change-listener* (atom true)
        param-room :room-size
        param-mix :reverb-mix
        slide-room (slider param-room 0.0 1.0 (reverb-label-table))
        slide-mix (slider param-mix 0.0 1.0 (reverb-label-table))
        lab-room (label "Room Size")
        lab-mix (label "Mix")
        pan-main (ss/horizontal-panel :items [(ss/border-panel :south lab-room
                                                               :center slide-room)
                                              (ss/border-panel :south lab-mix
                                                               :center slide-mix)]
                                      :border (factory/title "Reverb"))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (+ bias (* scale pos))]
                                (.set-param! ied param val)))))
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-room data)
                 (set-slider slide-mix data)
                 (reset! enable-change-listener* true))]
    (.addChangeListener slide-room slider-listener)
    (.addChangeListener slide-mix slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))


; ---------------------------------------------------------------------- 
;                                 Main Out


(defn- mainout-editor [ied]
  (let [enable-change-listener* (atom true)
        param-amp :amp
        param-cc7 :cc-volume-depth
        param-lp :lp
        lp-buttons (let [acc* (atom [])
                         grp (ss/button-group)]
                     (doseq [k [1 2 4 8 16]]
                       (let [f (* 1000 k)
                             b (ss/toggle :text (format "%dK" k)
                                          :group grp)]
                         (.putClientProperty b :value (float f))
                         (ss/listen b :action (fn [ev]
                                                (let [src (.getSource ev)
                                                      val (float (.getClientProperty src :value))]
                                                  (.set-param! ied :lp val))))
                         (swap! acc* (fn [n](conj n b)))))
                     (.setSelected (last @acc*) true)
                     (into [] (reverse @acc*)))
        pan-buttons (ss/border-panel :center (ss/grid-panel :columns 1
                                                            :items lp-buttons)
                                     :south (label "Low Pass"))
        slide-cc7 (slider param-cc7 0.0 1.0 (reverb-label-table))
        slide-amp (slider param-amp 0.0 1.0 (reverb-label-table))
        pan-main (ss/horizontal-panel :items [pan-buttons
                                              (ss/border-panel :center slide-cc7
                                                               :south (label "CC7"))
                                              (ss/border-panel :center slide-amp
                                                               :south (label "Amp"))]
                                      :border (factory/title "Out"))
        slider-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    pos (.getValue src)
                                    val (+ bias (* scale pos))]
                                (.set-param! ied param val)))))
        lp-frequencies [1000 2000 4000 8000 16000]
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (set-slider slide-cc7 data)
                 (set-slider slide-amp data)
                 (let [lp (int (:lp data))
                       f (math/closest lp lp-frequencies)
                       index (get {1000 4, 2000 3, 4000 2, 8000 1, 16000 0} f 0)]
                   (.setSelected (nth lp-buttons index) true))
                 (reset! enable-change-listener* true))]
    (.addChangeListener slide-cc7 slider-listener)
    (.addChangeListener slide-amp slider-listener)
    {:pan-main pan-main
     :syncfn syncfn}))


(defn fx-editor [performance ied]
  (let [fb6 (feedback-editor 6 ied)
        fb8 (feedback-editor 8 ied)
        vibed (vibrato-editor ied)
        lfo1 (lfo-editor 1 ied)
        lfo2 (lfo-editor 2 ied)
        pitched (pitch-editor ied)
        delayed (delay-editor ied)
        reverbed (reverb-editor ied)
        mainouted (mainout-editor ied)

        pan-fb (ss/vertical-panel :items [(:pan-main fb6)
                                          (:pan-main fb8)
                                          (:pan-main pitched)])
        pan-lfo (ss/vertical-panel :items [(:pan-main vibed)
                                           (:pan-main lfo1)
                                           (:pan-main lfo2)])
        pan-efx (ss/vertical-panel :items [(:pan-main delayed)
                                           (:pan-main reverbed)
                                           (:pan-main mainouted)])
        pan-main (ss/horizontal-panel :items [pan-fb
                                              pan-lfo
                                              pan-efx])
        widget-map {:pan-main (ss/scrollable pan-main)}
        ed (reify subedit/InstrumentSubEditor

             (widgets [this] widget-map)
             
             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "Algo mod-editor does not have %s widget" key))))
             
             (parent [this] ied)
             
             (parent! [this _]) ;; ignore
             
             (status! [this txt]
               (.status! ied txt))
             
             (warning! [this txt]
               (.warning! ied txt))
             
             (set-param! [this param value]
               (.set-param! ied param value))

             (init! [this]
               nil)

             (sync-ui! [this]
               (let [data (.current-data (.bank performance))]
                 (println (format "DEBUG fx-editor data count is %s" (count data)))
                 ((:syncfn fb6) data)
                 ((:syncfn fb8) data)
                 ((:syncfn vibed) data)
                 ((:syncfn lfo1) data)
                 ((:syncfn lfo2) data)
                 ((:syncfn pitched) data)
                 ((:syncfn delayed) data)
                 ((:syncfn reverbed) data)
                 ((:syncfn mainouted) data))))]
                 
    ed))

