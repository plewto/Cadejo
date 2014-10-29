;; Operator MIDI velocity pressure coontroller, LFO and keyscale
;;
(ns cadejo.instruments.algo.editor.amp-editor
  (:require [cadejo.instruments.algo.editor.overview :as overview])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(def ^:private slider-width 50)
(def ^:private slider-height 100)

(defn- slider 
  ([param paint-labels]
     (slider param 0.0 1.0 paint-labels))
  ([param minval maxval paint-labels]
     (let [steps 100 
           isteps 1/100
           s (ss/slider :orientation :vertical
                        :value 0 :min 0 :max steps
                        :snap-to-ticks? false
                        :paint-labels? paint-labels
                        :minor-tick-spacing 5
                        :major-tick-spacing 25
                        :size [slider-width :by slider-height])
           delta (float (- maxval minval))
           scale (* isteps delta)
           bias minval
           rvs-scale (/ steps delta)
           rvs-bias (/ (* -1 steps minval) delta)]
       (.putClientProperty s :scale scale)
       (.putClientProperty s :bias bias)
       (.putClientProperty s :rvs-scale rvs-scale)
       (.putClientProperty s :rvs-bias rvs-bias)
       (.putClientProperty s :param param)
       s)))

(defn- label [text]
  (ss/label :text (str text) 
            :halign :center))

(defn spinner [param ival minval maxval step]
  (let [s (ss/spinner :model (ss/spinner-model ival
                                               :from minval
                                               :to maxval
                                               :by step))]
    (.putClientProperty s :param param)
    s))


(defn- op-amp-editor [op performance ied]
  (let [pkeyword (fn [p](keyword (format "op%d-%s" op p)))
        param-velocity (pkeyword "velocity")
        param-pressure (pkeyword "pressure")
        param-cca (pkeyword "cca")
        param-ccb (pkeyword "ccb")
        param-lfo1 (pkeyword "lfo1")
        param-lfo2 (pkeyword "lfo2")
        param-left-key (pkeyword "left-key")
        param-left-scale (pkeyword "left-scale")
        param-right-key (pkeyword "right-key")
        param-right-scale (pkeyword "right-scale")
        param-enable (pkeyword "enable")
        enable-change-listener* (atom true)
        slide-velocity (slider param-velocity  true)
        slide-pressure (slider param-pressure  false)
        slide-lfo1 (slider param-lfo1  false)
        slide-lfo2 (slider param-lfo2  false)
        slide-cca (slider param-cca  false)
        slide-ccb (slider param-ccb  true)
        lab-velocity (label "Vel")
        lab-pressure (label "Press")
        lab-lfo1 (label "LFO1")
        lab-lfo2 (label "LFO2")
        lab-cca (label "CCA")
        lab-ccb (label "CCB")
        pan-velocity (ss/border-panel :center slide-velocity
                                      :south lab-velocity)
        pan-pressure (ss/border-panel :center slide-pressure
                                      :south lab-pressure)
        pan-lfo1 (ss/border-panel :center slide-lfo1
                                  :south lab-lfo1)
        pan-lfo2 (ss/border-panel :center slide-lfo2
                                  :south lab-lfo2)
        pan-cca (ss/border-panel :center slide-cca
                                 :south lab-cca)
        pan-ccb (ss/border-panel :center slide-ccb
                                 :south lab-ccb)
        ;; keyscale
        ;;
        spin-left-key (spinner (pkeyword "left-key") 60 0 108 1)
        spin-right-key (spinner (pkeyword "right-key") 60 12 127 1)
        spin-left-scale (spinner (pkeyword "left-scale") 0 -99 99 3)
        spin-right-scale (spinner (pkeyword "right-scale") 0 -99 99 3)
        lab-left-key (label "Key")
        lab-left-scale (label "Scale")
        lab-right-key (label "Key")
        lab-right-scale (label "Scale")
        pan-left (ss/vertical-panel :items [(ss/vertical-panel :items [spin-left-key lab-left-key])
                                            (ss/vertical-panel :items [spin-left-scale lab-left-scale])]
                                    :border (factory/title "Left"))
        pan-right (ss/vertical-panel :items [(ss/vertical-panel :items [spin-right-key lab-right-key])
                                            (ss/vertical-panel :items [spin-right-scale lab-right-scale])]
                                    :border (factory/title "Right"))
        pan-keyscale (ss/horizontal-panel :items [pan-left pan-right]
                                          :border (factory/title "Keyscale"))
                                            
        elements [slide-velocity lab-velocity 
                  slide-pressure lab-pressure 
                  slide-lfo1 lab-lfo1 
                  slide-lfo2 lab-lfo2 
                  slide-cca lab-cca 
                  slide-ccb lab-ccb 
                  spin-left-key lab-left-key
                  spin-right-key lab-right-key
                  spin-left-scale lab-left-scale
                  spin-right-scale lab-right-scale]
        pan-main (ss/horizontal-panel :items [pan-velocity pan-pressure 
                                              pan-lfo1 pan-lfo2 
                                              pan-cca pan-ccb pan-keyscale]
                                      :border (factory/title (format "OP%d" op)))
        set-slider (fn [slider val]
                     (let [rvs-scale (.getClientProperty slider :rvs-scale)
                           rvs-bias (.getClientProperty slider :rvs-bias)
                           pos (int (+ rvs-bias (* rvs-scale val)))]
                       (.setValue slider pos)))
        
        syncfn (fn [data]
                 (reset! enable-change-listener* false)
                 (let [enable (not (zero? (param-enable data)))
                       velocity (float (param-velocity data))
                       pressure (float (param-pressure data))
                       lfo1 (float (param-lfo1 data))
                       lfo2 (float (param-lfo2 data))
                       cca (float (param-cca data))
                       ccb (float (param-ccb data))
                       left-scale (int (param-left-scale data))
                       left-key (int (param-left-key data))
                       right-scale (int (param-right-scale data))
                       right-key (int (param-right-key data))]
                       (set-slider slide-velocity velocity)
                       (set-slider slide-pressure pressure)
                       (set-slider slide-lfo1 lfo1)
                       (set-slider slide-lfo2 lfo2)
                       (set-slider slide-cca cca)
                       (set-slider slide-ccb ccb)
                       (.setValue spin-left-key left-key)
                       (.setValue spin-left-scale left-scale)
                       (.setValue spin-right-key right-key)
                       (.setValue spin-right-scale right-scale)
                       (doseq [e elements]
                         (.setEnabled e enable)))
                 (reset! enable-change-listener* true))
        
        slider-listener (proxy [ChangeListener] []
                          (stateChanged [ev]
                            (if @enable-change-listener*
                              (let [src (.getSource ev)
                                    param (.getClientProperty src :param)
                                    pos (.getValue src)
                                    scale (.getClientProperty src :scale)
                                    bias (.getClientProperty src :bias)
                                    val (+ bias (* scale pos))]
                                (.set-param! ied param val)))))
                                

        spinner-listener (proxy [ChangeListener] []
                           (stateChanged [ev]
                             (if @enable-change-listener*
                               (let [src (.getSource ev)
                                     param (.getClientProperty src :param)
                                     val (.getValue src)]
                                 (.set-param! ied param val)))))]
 
    (.addChangeListener slide-velocity slider-listener)
    (.addChangeListener slide-pressure slider-listener)
    (.addChangeListener slide-lfo1 slider-listener)
    (.addChangeListener slide-lfo2 slider-listener)
    (.addChangeListener slide-cca slider-listener)
    (.addChangeListener slide-ccb slider-listener)
    (.addChangeListener spin-left-key spinner-listener)
    (.addChangeListener spin-left-scale spinner-listener)
    (.addChangeListener spin-right-key spinner-listener)
    (.addChangeListener spin-right-scale spinner-listener)
    {:pan-main pan-main
     :syncfn syncfn}))

(defn amp-editor [performance ied]
  (let [oed (overview/overview-editor performance ied)
        ed1 (op-amp-editor 1 performance ied)
        ed2 (op-amp-editor 2 performance ied)
        ed3 (op-amp-editor 3 performance ied)
        ed4 (op-amp-editor 4 performance ied)
        ed5 (op-amp-editor 5 performance ied)
        ed6 (op-amp-editor 6 performance ied)
        ed7 (op-amp-editor 7 performance ied)
        ed8 (op-amp-editor 8 performance ied)
        stack-a (ss/grid-panel :columns 1 :items [(:pan-main ed3)(:pan-main ed2)(:pan-main ed1)])
        stack-b (ss/grid-panel :columns 1 :items [(:pan-main ed6)(:pan-main ed5)(:pan-main ed4)])
        stack-c (ss/grid-panel :columns 1 :items [(ss/vertical-panel)(:pan-main ed8)(:pan-main ed7)])
        pan-center (ss/grid-panel :rows 1 :items [stack-a stack-b stack-c])
        pan-main (ss/scrollable (ss/border-panel :center pan-center
                                                 :south (:pan-main oed)))
        widget-map {:pan-main pan-main}
        ed (reify subedit/InstrumentSubEditor

             (widgets [this] widget-map)
             
             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "Algo ampmod editor does not have %s widget" key))))
             
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
                 ((:syncfn oed) data)
                 ((:syncfn ed1) data)
                 ((:syncfn ed2) data)
                 ((:syncfn ed3) data)
                 ((:syncfn ed4) data)
                 ((:syncfn ed5) data)
                 ((:syncfn ed6) data)
                 ((:syncfn ed7) data)
                 ((:syncfn ed8) data)))) ]
    ed)) 
