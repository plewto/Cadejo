(ns cadejo.instruments.masa.masa-editor
  (:require [cadejo.instruments.masa.gamut-editor])
  (:require [cadejo.instruments.masa.efx-editor])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.ActionListener
           javax.swing.event.ChangeListener))

(defn- third [col]
  (nth col 2))

(defn- fourth [col]
  (nth col 3))

(defn- fifth [col]
  (nth col 4))


;; drawbar number n
;; --> slider 
;;         range [0,1,2, 8], snap on tick on
;;         properties :param   :ampn
;; --> radio
;;        properties :param :percn 
;; 
;; --> slider
;;       range [0.0 1.0], 
;;       properties :param [edeln
;; --> label
;;
;; --> panel
;;

(defn- create-drawbar-panel [n]
  (let [slider-drawbar (ss/slider :orientation :vertical
                                  :value 0 :min 0 :max 8
                                  :minor-tick-spacing 1
                                  :major-tick-spacing 4
                                  :snap-to-ticks? true
                                  :paint-labels? true
                                  :paint-ticks? true)
        slider-pedal (ss/slider :orientation :vertical
                                :value 0 :min -8 :max 8
                                :minor-tick-spacing 1
                                :major-tick-spacing 4
                                :paint-labels? true
                                :paint-track? true
                                :snap-to-ticks? true)
        jb-perc (ss/radio)
        lab-freq (ss/label :text "x")
        pan (ss/border-panel
             ;; :north (ss/label :text (str n)
             ;;                  :halign :center)
             :north lab-freq
             :center (ss/grid-panel :columns 1
                                    :items [(ss/border-panel
                                             :center slider-drawbar
                                             :south (ss/label :text "Amp"
                                                              :halign :center))
                                            (ss/border-panel
                                             :center slider-pedal
                                             :south (ss/label :text "Pedal"
                                                              :halign :center))])
             :south (ss/border-panel :center (ss/horizontal-panel :items [jb-perc])
                                     :south (ss/label :text "Perc"
                                                      :halign :center))
             :border (factory/title (str n)))]
    (.putClientProperty slider-drawbar :param (keyword (format "a%d" n)))
    (.putClientProperty slider-drawbar :scale 1)
    (.putClientProperty slider-drawbar :bias 0)
    (.putClientProperty slider-drawbar :rvs-scale 1)
    (.putClientProperty slider-drawbar :rvs-bias 0)
    (.putClientProperty slider-pedal :param (keyword (format "p%d" n)))
    (.putClientProperty slider-pedal :scale 1/8)
    (.putClientProperty slider-pedal :bias 0)
    (.putClientProperty slider-pedal :rvs-scale 8)
    (.putClientProperty slider-pedal :rvs-bias 0)
    (.putClientProperty jb-perc :param (keyword (format "perc%d" n)))
    (.setIcon jb-perc (lnf/read-icon :led1 :off))
    (.setSelectedIcon jb-perc (lnf/read-selected-icon :led1 :on))
    [slider-drawbar slider-pedal jb-perc pan lab-freq]))
                                
                                  
(defn registration-tab [performance ied]
  (let [drawbars (let [acc* (atom [])]
                   (dotimes [n 9]
                     (let [q (create-drawbar-panel (inc n))]
                       (swap! acc* (fn [n](conj n q)))))
                   @acc*)
        enable-change-listener* (atom true)
        slider-decay (ss/slider :orientation :vertical
                                :value 0 :min 0 :max 100
                                :minor-tick-spacing 5
                                :major-tick-spacing 25
                                :snap-to-ticks? true
                                :paint-labels? true
                                :paint-ticks? true)
        slider-sustain (ss/slider :orientation :vertical
                                  :value 0 :min 0 :max 100
                                  :minor-tick-spacing 5
                                  :major-tick-spacing 25
                                  :snap-to-ticks? true
                                  :paint-labels? true
                                  :paint-ticks? true)
        pan-env (ss/grid-panel :rows 1
                               :items [(ss/border-panel :center slider-decay
                                                        :south (ss/label :text "Decay"
                                                                         :halign :center))
                                       (ss/border-panel :center slider-sustain
                                                        :south (ss/label :text "Sustain"
                                                                         :halign :center))]
                               :border (factory/title "Percussion Envelope"))
        slider-vrate (ss/slider :orientation :vertical
                                :value 0 :min 0 :max 100
                                :minor-tick-spacing 5
                                  :major-tick-spacing 25
                                  :snap-to-ticks? false
                                  :paint-labels? true
                                  :paint-ticks? true)
        slider-vsens (ss/slider :orientation :vertical
                                :value 0 :min 0 :max 100
                                :minor-tick-spacing 5
                                :major-tick-spacing 25
                                :snap-to-ticks? false
                                :paint-labels? true
                                :paint-ticks? true)
        slider-vdepth (ss/slider :orientation :vertical
                                 :value 0 :min 0 :max 100
                                 :minor-tick-spacing 5
                                 :major-tick-spacing 25
                                 :snap-to-ticks? false
                                 :paint-labels? true
                                 :paint-ticks? true)
        slider-vdelay (ss/slider :orientation :vertical
                                 :value 0 :min 0 :max 100
                                 :minor-tick-spacing 5
                                 :major-tick-spacing 25
                                 :snap-to-ticks? false
                                 :paint-labels? true
                                 :paint-ticks? true)
        pan-vibrato (ss/grid-panel
                     :rows 1
                     :items [(ss/border-panel
                              :center slider-vrate
                              :south (ss/label :text "Rate"
                                               :halign :center))
                             (ss/border-panel
                              :center slider-vsens
                              :south (ss/label :text "Sensitivity"
                                               :halign :center))
                             (ss/border-panel
                              :center slider-vdepth
                              :south (ss/label :text "Depth"
                                               :halign :center))
                             (ss/border-panel
                              :center slider-vdelay
                              :south (ss/label :text "Delay"
                                               :halign :center))]
                     :border (factory/title "Vibrato"))
        pan-east (ss/grid-panel :columns 1 :items [pan-env pan-vibrato])
        pan-drawbars (ss/grid-panel :rows 1
                                    :items (map fourth drawbars)
                                    :border (factory/title "Registration"))

        pan-main (ss/horizontal-panel :items [pan-drawbars pan-east])
        widget-map {:pan-main pan-main}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
             
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "masa amp-tab does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this ignore] ied)
        
             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param (float val)))

             (init! [this]
               (.set-param! this :a1 0)
               (.set-param! this :a2 0)
               (.set-param! this :a3 8)
               (.set-param! this :a4 6)
               (.set-param! this :a5 0)
               (.set-param! this :a6 4)
               (.set-param! this :a7 0)
               (.set-param! this :a8 0)
               (.set-param! this :a9 2)
               (.set-param! this :p1 0)
               (.set-param! this :p2 0)
               (.set-param! this :p3 0)
               (.set-param! this :p4 0)
               (.set-param! this :p5 0)
               (.set-param! this :p6 0)
               (.set-param! this :p7 0)
               (.set-param! this :p8 0)
               (.set-param! this :p9 0)
               (.set-param! this :perc1 0)
               (.set-param! this :perc2 0)
               (.set-param! this :perc3 0)
               (.set-param! this :perc4 0)
               (.set-param! this :perc5 0)
               (.set-param! this :perc6 0)
               (.set-param! this :perc7 0)
               (.set-param! this :perc8 0)
               (.set-param! this :perc9 0)
               (.set-param! this :decay 0.2)
               (.set-param! this :sustain 0.7)
               (.set-param! this :vrate 7.0)
               (.set-param! this :vsens 0.05)
               (.set-param! this :vdepth 0.0)
               (.set-param! this :vdelay 0.0)
               (.sync-ui! this))

             (sync-ui! [this]
               (reset! enable-change-listener* false)
               (let [data (.current-data (.bank performance))
                     a1 (int (get data :a1 0))
                     a2 (int (get data :a2 0))
                     a3 (int (get data :a3 0))
                     a4 (int (get data :a4 0))
                     a5 (int (get data :a5 0))
                     a6 (int (get data :a6 0))
                     a7 (int (get data :a7 0))
                     a8 (int (get data :a8 0))
                     a9 (int (get data :a9 0))

                     p1 (int (* 8 (get data :p1 0)))
                     p2 (int (* 8 (get data :p2 0)))
                     p3 (int (* 8 (get data :p3 0)))
                     p4 (int (* 8 (get data :p4 0)))
                     p5 (int (* 8 (get data :p5 0)))
                     p6 (int (* 8 (get data :p6 0)))
                     p7 (int (* 8 (get data :p7 0)))
                     p8 (int (* 8 (get data :p8 0)))
                     p9 (int (* 8 (get data :p9 0)))

                     pr1 (pos? (get data :perc1 0))
                     pr2 (pos? (get data :perc2 0))
                     pr3 (pos? (get data :perc3 0))
                     pr4 (pos? (get data :perc4 0))
                     pr5 (pos? (get data :perc5 0))
                     pr6 (pos? (get data :perc6 0))
                     pr7 (pos? (get data :perc7 0))
                     pr8 (pos? (get data :perc8 0))
                     pr9 (pos? (get data :perc9 0))
                     decay (int (* 100 (get data :decay 0)))
                     sustain (int (* 100 (get data :sustain 0)))
                     vrate (min 100 (int (- (* 100/9 (get data :vrate 7)) 100/9)))
                     vsens (min 100 (int (* 30000 (get data :vsens 0.001))))
                     vdepth (int (* 100 (get data :vdepth 0)))
                     vdelay (int (* 25/2 (get data :vdelay 0)))

                     gamut [(float (get data :r1 1))
                            (float (get data :r2 1))
                            (float (get data :r3 1))
                            (float (get data :r4 1))
                            (float (get data :r5 1))
                            (float (get data :r6 1))
                            (float (get data :r7 1))
                            (float (get data :r8 1))
                            (float (get data :r9 1))]
                     freq-labels (map fifth drawbars)
                     [sa1 sa2 sa3 sa4 sa5 sa6 sa7 sa8 sa9](map first drawbars)
                     [sp1 sp2 sp3 sp4 sp5 sp6 sp7 sp8 sp9](map second drawbars)
                     [jb1 jb2 jb3 jb4 jb5 jb6 jb7 jb8 jb9](map third drawbars)]
                 (dotimes [n (count gamut)]
                   (ss/config! (nth freq-labels n)
                               :text (format "%6.4f" (nth gamut n))))
                 (.setValue sa1 a1)
                 (.setValue sa2 a2)
                 (.setValue sa3 a3)
                 (.setValue sa4 a4)
                 (.setValue sa5 a5)
                 (.setValue sa6 a6)
                 (.setValue sa7 a7)
                 (.setValue sa8 a8)
                 (.setValue sa9 a9)
                 (.setValue sp1 p1)
                 (.setValue sp2 p2)
                 (.setValue sp3 p3)
                 (.setValue sp4 p4)
                 (.setValue sp5 p5)
                 (.setValue sp6 p6)
                 (.setValue sp7 p7)
                 (.setValue sp8 p8)
                 (.setValue sp9 p9)
                 (.setValue slider-decay decay)
                 (.setValue slider-sustain sustain)
                 (.setValue slider-vrate vrate)
                 (.setValue slider-vsens vsens)
                 (.setValue slider-vdepth vdepth)
                 (.setValue slider-vdelay vdelay)
                 (.setSelected jb1 pr1)
                 (.setSelected jb2 pr2)
                 (.setSelected jb3 pr3)
                 (.setSelected jb4 pr4)
                 (.setSelected jb5 pr5)
                 (.setSelected jb6 pr6)
                 (.setSelected jb7 pr7)
                 (.setSelected jb8 pr8)
                 (.setSelected jb9 pr9)
                 (reset! enable-change-listener* true) )))

        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [slider (.getSource ev)
                                    param (.getClientProperty slider :param)
                                    scale (.getClientProperty slider :scale)
                                    bias (.getClientProperty slider :bias)
                                    pos (int (ss/config slider :value))
                                    val (float (+ bias (* scale pos)))]
                                (.set-param! ied param val)
                                (.status! ied (format "[%-5s %3d] --> val %6.3f" param pos (float val)))))))

        perc-action (proxy [ActionListener][]
                      (actionPerformed [ev]
                        (let [b (.getSource ev)
                              param (.getClientProperty b :param)
                              val (if (.isSelected b) 1.0 0.0)]
                          (.status! ied (format "[%s] --> %s" param (if (zero? val) "off" "on ")))
                          (.set-param! ied param val))))]
             
    (.putClientProperty slider-decay :param :decay) ;; [0.0 ... 1.0]
    (.putClientProperty slider-decay :scale 1/100)
    (.putClientProperty slider-decay :bias 0)
    (.putClientProperty slider-decay :rvs-scale 100)
    (.putClientProperty slider-decay :rvs-bias 0)

    (.putClientProperty slider-sustain :param :sustain) ;; [0.0 ... 1.0]
    (.putClientProperty slider-sustain :scale 1/100)
    (.putClientProperty slider-sustain :bias 0)
    (.putClientProperty slider-sustain :rvs-scale 100/9)
    (.putClientProperty slider-sustain :rvs-bias -100/9)
    
    (.putClientProperty slider-vrate :param :vrate) ;; [1.0 ... 10.0]
    (.putClientProperty slider-vrate :scale 9/100)
    (.putClientProperty slider-vrate :bias 1)
    (.putClientProperty slider-vrate :rvs-scale 100/9)
    (.putClientProperty slider-vrate :rvs-bias -100/9)

    (.putClientProperty slider-vsens :param :vsens) ;; [0.00 ... 0.03]
    (.putClientProperty slider-vsens :scale 3/10000)
    (.putClientProperty slider-vsens :bias 0)
    (.putClientProperty slider-vsens :rvs-scale 30000)
    (.putClientProperty slider-vsens :rvs-bias 0)

    (.putClientProperty slider-vdepth :param :vdepth) ;; [0.0 ... 1.0]
    (.putClientProperty slider-vdepth :scale 1/100)
    (.putClientProperty slider-vdepth :bias 0)
    (.putClientProperty slider-vdepth :rvs-scale 100)
    (.putClientProperty slider-vdepth :rvs-bias 0)

    (.putClientProperty slider-vdelay :param :vdelay) ;; [0.0 ... 8.0]
    (.putClientProperty slider-vdelay :scale 2/25)
    (.putClientProperty slider-vdelay :bias 0)
    (.putClientProperty slider-vdelay :rvs-scale 25/2)
    (.putClientProperty slider-vdelay :rvs-bias 0)

    (doseq [s (map first drawbars)]
      (.addChangeListener s change-listener))
    (doseq [s (map second drawbars)]
      (.addChangeListener s change-listener))
    (doseq [s [slider-decay slider-sustain slider-vrate
               slider-vsens slider-vdepth slider-vdelay]]
      (.addChangeListener s change-listener))
    (doseq [s (map third drawbars)]
      (.addActionListener s perc-action))

    (.add-sub-editor! ied "Registration" ed)
    ed))


(defn masa-editor [performance]
  (let [ied (ied/instrument-editor performance)
        rtab (registration-tab performance ied)
        gtab (cadejo.instruments.masa.gamut-editor/gamut-tab performance ied)
        fxed (cadejo.instruments.masa.efx-editor/efx-tab performance ied)]
    ied))
