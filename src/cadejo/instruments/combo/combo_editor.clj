(println "-->    combo editor")

(ns cadejo.instruments.combo.combo-editor
  (:use [cadejo.util.trace])
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

;; (def bypass-filter 0)
;; (def lp-filter 1)
;; (def hp-filter 2)
;; (def bp-filter 3)
;; (def br-filter 4)

;; Create standard slider
;;
(defn- slider [& {:keys [orientation
                         value min max
                         minor major snap
                         ticks track labels]
                  :or {orientation :vertical
                       value 0
                       min 0
                       max 100
                       minor 5
                       major 25
                       snap true
                       ticks true
                       track true
                       labels true}}]
  (ss/slider :orientation orientation
             :value value :min min :max max
             :minor-tick-spacing minor
             :major-tick-spacing major
             :snap-to-ticks? snap
             :paint-ticks? ticks
             :paint-track? track
             :paint-labels? labels))

;; Create standard slider panel
;;                         
(defn- span [slider txt]
  (let [lab (ss/label :text (str txt)
                      :halign :center)

        pan (ss/border-panel :center slider
                             :south lab)]
    pan))


(defn combo-editor [performance]
  (let [ied (ied/instrument-editor performance)
        enable-param-updates* (atom true)
        slide-a1 (slider)
        slide-a2 (slider)
        slide-a3 (slider)
        slide-a4 (slider)
        slide-w1 (slider)
        slide-w2 (slider)
        slide-w3 (slider)
        slide-w4 (slider)
        slide-detune (slider :orientation :horizontal :snap false
                             :minor 2)
        slide-flanger-rate (slider)
        slide-flanger-depth (slider)
        slide-flanger-fb (slider :value 50 :min -100 :max 100)
        slide-flanger-mix (slider)
        slide-vibrato-rate (slider)
        slide-vibrato-sens (slider)
        slide-reverb (slider)
        slide-amp (slider)
        pan-mixer (ss/grid-panel :rows 1
                                 :items [(span slide-a1 "1")
                                         (span slide-a2 "2")
                                         (span slide-a3 "3")
                                         (span slide-a4 "4")]
                                 :border (factory/title "Mixer"))
        pan-wave (ss/grid-panel :rows 1
                                :items [(span slide-w1 "1")
                                        (span slide-w2 "2")
                                        (span slide-w3 "3")
                                        (span slide-w4 "4")]
                                :border (factory/title "Wave"))
        pan-detune (ss/horizontal-panel
                    :items [slide-detune]
                    :border (factory/title "Detune"))

        pan-tone (ss/border-panel :center (ss/vertical-panel 
                                           :items [pan-mixer pan-wave])
                                  :south pan-detune)
        ;filter-button-style 10
        tb-filter-bypass (factory/filter-button :bypass)
        tb-filter-lp (factory/filter-button :lp)
        tb-filter-hp (factory/filter-button :hp)
        tb-filter-bp (factory/filter-button :bp)
        tb-filter-br (factory/filter-button :br)
     
        tb-filter-1  (ss/toggle :text "1")
        tb-filter-2  (ss/toggle :text "2")
        tb-filter-4  (ss/toggle :text "4")
        tb-filter-3  (ss/toggle :text "3")
        tb-filter-6  (ss/toggle :text "6")
        tb-filter-8  (ss/toggle :text "8")

        filter-buttons  {bypass-filter tb-filter-bypass
                         lp-filter tb-filter-lp
                         hp-filter tb-filter-hp
                         bp-filter tb-filter-bp
                         br-filter tb-filter-br}
        harmonic-buttons {1 tb-filter-1
                          2 tb-filter-2
                          3 tb-filter-3
                          4 tb-filter-4
                          6 tb-filter-6
                          8 tb-filter-8}
        pan-filter (let [p (ss/grid-panel :columns 1
                                          :items [tb-filter-bypass
                                                  tb-filter-lp
                                                  tb-filter-hp
                                                  tb-filter-bp
                                                  tb-filter-br
                                                  tb-filter-1
                                                  tb-filter-2
                                                  tb-filter-3
                                                  tb-filter-4
                                                  tb-filter-6
                                                  tb-filter-8]
                                          :border (factory/title "Filter"))
                         fgroup (ss/button-group)
                         hgroup (ss/button-group)]
                     (ss/config! tb-filter-bypass :group fgroup)
                     (ss/config! tb-filter-lp :group fgroup)
                     (ss/config! tb-filter-hp :group fgroup)
                     (ss/config! tb-filter-bp :group fgroup)
                     (ss/config! tb-filter-br :group fgroup)
                     (ss/config! tb-filter-1 :group hgroup)
                     (ss/config! tb-filter-2 :group hgroup)
                     (ss/config! tb-filter-3 :group hgroup)
                     (ss/config! tb-filter-4 :group hgroup)
                     (ss/config! tb-filter-6 :group hgroup)
                     (ss/config! tb-filter-8 :group hgroup)
                     p)
        pan-flanger (ss/grid-panel :rows 1
                                   :items [(span slide-flanger-rate "Rate")
                                           (span slide-flanger-depth "Depth")
                                           (span slide-flanger-fb "FB")
                                           (span slide-flanger-mix "Mix")]
                                   :border (factory/title "Flanger"))
        pan-vibrato (ss/grid-panel :rows 1
                                   :items [(span slide-vibrato-rate "Rate")
                                           (span slide-vibrato-sens "Sens")]
                                   :border (factory/title "Vibrato"))
        pan-out (ss/grid-panel :rows 1
                               :items [(span slide-reverb "Reverb")
                                       (span slide-amp "Amp")]
                               :border (factory/line))
        pan-main (let [pan-east1 (ss/grid-panel :rows 1
                                                :items [pan-vibrato pan-out])
                       pan-east (ss/grid-panel :columns 1
                                               :items [pan-east1 pan-flanger])
                       p (ss/horizontal-panel :items [pan-tone pan-filter pan-east])]
                   p)
        rate-scale 400/31               ; flanger & vibrato rate
        rate-bias  -100/31              ; scale coefficients
        inv-rate-scale 31/400
        inv-rate-bias 1/4
        widget-map {:pan-main pan-main}
        combo-ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor

                   (widgets [this] widget-map)

                   (widget [this key]
                     (or (get widget-map key)
                         (umsg/warning (format "Combo editor does not have %s widget" key))))

                   (parent [this] ied)

                   (parent! [this ignore] ied)

                   (status! [this msg]
                     (.status! ied msg))

                   (warning! [this msg]
                     (.warning! ied msg))

                   (set-param! [this param val]
                     (.status! this (format "[%s] --> %s" param val))
                     (.set-param! ied param val))
                  
                   (init! [this]
                     (.set-param! this :amp1 1.0)
                     (.set-param! this :amp2 0.0)
                     (.set-param! this :amp3 0.0)
                     (.set-param! this :amp4 0.0)
                     (.set-param! this :wave1 0.0)
                     (.set-param! this :wave2 0.0)
                     (.set-param! this :wave3 0.0)
                     (.set-param! this :wave4 0.0)
                     (.set-param! this :chorus 0.0)
                     (.set-param! this :filter-type 0)
                     (.set-param! this :filter 8.0)
                     (.set-param! this :vibrato-freq 5.0)
                     (.set-param! this :vibrato-sens 0.01)
                     (.set-param! this :flanger-rate 0.25)
                     (.set-param! this :flanger-depth 0.25)
                     (.set-param! this :flanger-fb -0.5)
                     (.set-param! this :flanger-mix 0.0)
                     (.set-param! this :reverb-mix 0.0)
                     (.set-param! this :amp 0.2)
                     (.sync-ui! this))

                   (sync-ui! [this]
                     (reset! enable-param-updates* false)
                     (let [data (.current-data (.bank performance))
                           a1 (int (* 100 (get data :amp1 0)))
                           a2 (int (* 100 (get data :amp2 0)))
                           a3 (int (* 100 (get data :amp3 0)))
                           a4 (int (* 100 (get data :amp4 0)))
                           w1 (int (* 100 (get data :wave1 0)))
                           w2 (int (* 100 (get data :wave2 0)))
                           w3 (int (* 100 (get data :wave3 0)))
                           w4 (int (* 100 (get data :wave4 0)))
                           detune (int (min 100 (* 100 (get data :chorus 0))))
                           frate (int (+ rate-bias 
                                         (* rate-scale (get data :flanger-rate 1.0))))
                           fdepth (int (* 100 (get data :flanger-depth 0)))
                           ffb (int (* 100 (get data :flanger-fb 0)))
                           fmix (int (* 100 (get data :flanger-mix 0)))
                           vrate (int (+ rate-bias
                                         (* rate-scale (get data :vibrato-freq 5))))
                           vsens (int (min 100 (* 1000 (get data :vibrato-sens 0))))
                           
                           rev (int (* 100 (get data :reverb-mix 0)))
                           amp (int (* 100 (get data :amp 1)))
                           f-mode (get data :filter-type bypass-filter)
                           harmonic (min 16 (max 1 (get data :filter 2)))]
                       (doseq [tb (map second (seq harmonic-buttons))]
                         (.setEnabled tb (not (= f-mode bypass-filter))))
                       (.setValue slide-a1 a1)
                       (.setValue slide-a2 a2)
                       (.setValue slide-a3 a3)
                       (.setValue slide-a4 a4)
                       (.setValue slide-w1 w1)
                       (.setValue slide-w2 w2)
                       (.setValue slide-w3 w3)
                       (.setValue slide-w4 w4)
                       (.setValue slide-detune detune)
                       (.setValue slide-flanger-rate frate)
                       (.setValue slide-flanger-depth fdepth)
                       (.setValue slide-flanger-fb ffb)
                       (.setValue slide-flanger-mix fmix)
                       (.setValue slide-vibrato-rate vrate)
                       (.setValue slide-vibrato-sens vsens)
                       (.setValue slide-reverb rev)
                       (.setValue slide-amp amp)
                       (.setSelected (get filter-buttons f-mode tb-filter-bypass) true)
                       (.setSelected (get harmonic-buttons harmonic tb-filter-8) true))
                     (reset! enable-param-updates* true)))
                   
        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-param-updates*
                              (let [slider (.getSource ev)
                                    param (.getClientProperty slider :param)
                                    scale (or (.getClientProperty slider :scale) 1/100)
                                    bias (or (.getClientProperty slider :bias) 0)
                                    pos (int (ss/config slider :value))
                                    val (float (+ bias (* scale pos)))]
                                (.status! ied (format "[%s] --> %s" param val))
                                (.set-param! ied param val)))))
        
        filter-action (proxy [ActionListener][]
                        (actionPerformed [ev]
                          (let [tb (.getSource ev)
                                param :filter-type
                                val (.getClientProperty tb :value)]
                            (doseq [hb (map second (seq harmonic-buttons))]
                              (.setEnabled hb (not (= val bypass-filter))))
                            (.status! ied (format "[%s] --> %s" param val))
                            (.set-param! ied param val))))
        
        harmonic-action (proxy [ActionListener][]
                          (actionPerformed [ev]
                            (let [tb (.getSource ev)
                                  param :filter
                                  val (.getClientProperty tb :value)]
                              (.status! ied (format "[%s] --> %s" param val))
                              (.set-param! ied param val))))]

    (doseq [b (map second (seq harmonic-buttons))]
      (ss/config! b :size [48 :by 48]))
    (.putClientProperty slide-a1 :param :amp1)
    (.putClientProperty slide-a2 :param :amp2)
    (.putClientProperty slide-a3 :param :amp3)
    (.putClientProperty slide-a4 :param :amp4)
    (.putClientProperty slide-w1 :param :wave1)
    (.putClientProperty slide-w2 :param :wave2)
    (.putClientProperty slide-w3 :param :wave3)
    (.putClientProperty slide-w4 :param :wave4)
    (.putClientProperty slide-detune :param :chorus)
    (.putClientProperty slide-flanger-rate :param :flanger-rate)
    (.putClientProperty slide-flanger-rate :bias inv-rate-bias)
    (.putClientProperty slide-flanger-rate :scale inv-rate-scale)
    (.putClientProperty slide-flanger-depth :param :flanger-depth)
    (.putClientProperty slide-flanger-fb :param :flanger-fb)
    (.putClientProperty slide-flanger-fb :bias 0)
    (.putClientProperty slide-flanger-fb :scale 0.01)
    (.putClientProperty slide-flanger-mix :param :flanger-mix)
    (.putClientProperty slide-vibrato-rate :param :vibrato-freq)
    (.putClientProperty slide-vibrato-rate :bias inv-rate-bias)
    (.putClientProperty slide-vibrato-rate :scale inv-rate-scale)
    (.putClientProperty slide-vibrato-sens :param :vibrato-sens)
    (.putClientProperty slide-vibrato-sens :scale 0.001)
    (.putClientProperty slide-reverb :param :reverb-mix)
    (.putClientProperty slide-amp :param :amp)
    (.putClientProperty tb-filter-bypass :value bypass-filter)
    (.putClientProperty tb-filter-lp :value lp-filter)
    (.putClientProperty tb-filter-hp :value hp-filter)
    (.putClientProperty tb-filter-bp :value bp-filter)
    (.putClientProperty tb-filter-br :value br-filter)
    (.putClientProperty tb-filter-1 :value 1)
    (.putClientProperty tb-filter-2 :value 2)
    (.putClientProperty tb-filter-3 :value 3)
    (.putClientProperty tb-filter-4 :value 4)
    (.putClientProperty tb-filter-6 :value 6)
    (.putClientProperty tb-filter-8 :value 8)
    (.addChangeListener slide-a1 change-listener)
    (.addChangeListener slide-a2 change-listener)
    (.addChangeListener slide-a3 change-listener)
    (.addChangeListener slide-a4 change-listener)
    (.addChangeListener slide-w1 change-listener)
    (.addChangeListener slide-w2 change-listener)
    (.addChangeListener slide-w3 change-listener)
    (.addChangeListener slide-w4 change-listener)
    (.addChangeListener slide-detune change-listener)
    (.addChangeListener slide-flanger-rate change-listener)
    (.addChangeListener slide-flanger-depth change-listener)
    (.addChangeListener slide-flanger-fb change-listener)
    (.addChangeListener slide-flanger-mix change-listener)
    (.addChangeListener slide-vibrato-rate change-listener)
    (.addChangeListener slide-vibrato-sens change-listener)
    (.addChangeListener slide-reverb change-listener)
    (.addChangeListener slide-amp change-listener)
    (.addActionListener tb-filter-bypass filter-action)
    (.addActionListener tb-filter-lp filter-action)
    (.addActionListener tb-filter-hp filter-action)
    (.addActionListener tb-filter-bp filter-action)
    (.addActionListener tb-filter-br filter-action)
    (.addActionListener tb-filter-1 harmonic-action)
    (.addActionListener tb-filter-2 harmonic-action)
    (.addActionListener tb-filter-3 harmonic-action)
    (.addActionListener tb-filter-4 harmonic-action)
    (.addActionListener tb-filter-6 harmonic-action)
    (.addActionListener tb-filter-8 harmonic-action)
    (.add-sub-editor! ied "Edit" :general :mixer "Combo editor" combo-ed)
    (.show-card-number! ied 1)
    ied))
