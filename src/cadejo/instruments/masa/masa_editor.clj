(println "Loading masa-editor")
(ns cadejo.instruments.masa.masa-editor
  (:use [cadejo.util.trace])
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
                                :value 0 :min 0 :max 8
                                :minor-tick-spacing 1
                                :major-tick-spacing 4
                                :paint-labels? true
                                :snap-to-ticks? true
                                :paint-ticks? true)
        jb-perc (ss/radio)
        pan (ss/border-panel :north (ss/label :text (str n)
                                              :halign :center)
                             :center (ss/grid-panel :columns 1
                                                    :items [(ss/border-panel :center slider-drawbar
                                                                             :south (ss/label :text "Amp"
                                                                                              :halign :center))
                                                            (ss/border-panel :center slider-pedal
                                                                             :south (ss/label :text "Pedal"
                                                                                              :halign :center))])
                             :south (ss/border-panel :center (ss/horizontal-panel :items [jb-perc])
                                                     :south (ss/label :text "Perc"
                                                                      :halign :center)))]
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
    [slider-drawbar slider-pedal jb-perc pan]))
                                
                                  
(defn registration-tab [performance ied]
  (let [drawbars (let [acc* (atom [])]
                   (dotimes [n 9]
                     (let [q (create-drawbar-panel (inc n))]
                       (swap! acc* (fn [n](conj n q)))))
                   @acc*)
        enable-sliders* (atom true)
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
        pan-vibrato (ss/grid-panel :rows 1
                                   :items [(ss/border-panel :center slider-vrate
                                                            :south (ss/label :text "Rate"
                                                                             :halign :center))
                                           (ss/border-panel :center slider-vsens
                                                            :south (ss/label :text "Sensitivity"
                                                                             :halign :center))
                                           (ss/border-panel :center slider-vdepth
                                                            :south (ss/label :text "Depth"
                                                                             :halign :center))
                                           (ss/border-panel :center slider-vdelay
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
               (.set-param! ied param val))

             (sync-ui! [this]
               (reset! enable-sliders* false)

               (reset! enable-sliders* true)
               ))

        change-listener (proxy [ChangeListener][]
                          (stateChanged [ev]
                            (if @enable-sliders*
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
                          (.set-param! ied param val)))) ]
             
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
    (.putClientProperty slider-vrate :rvs-scale 1)
    (.putClientProperty slider-vrate :rvs-bias 0)

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
        rtab (registration-tab performance ied)]
    
    ied))




;;; TEST ********************************************


;; (let [acc* (atom [])]
;;   (dotimes [n 9]
;;     (let [q (create-drawbar-panel (inc n))]
;;       (swap! acc* (fn [n](conj n (nth q 3))))))
;;   (let [pan-main (ss/grid-panel :rows 1 :items @acc*)
;;         f (ss/frame :title "Test"
;;                     :content pan-main
;;                     :on-close :dispose)]
;;     (ss/pack! f)
;;     (ss/show! f)))
                    
    
    
