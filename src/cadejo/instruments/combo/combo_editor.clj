(ns cadejo.instruments.combo.combo-editor
  (:use [cadejo.util.trace])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.instrument-editor-framework :as framework])
  (:require [cadejo.ui.instruments.instrument-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.slider-panel :as slidepan])
  (:require [seesaw.core :as ss])
 )

(defn- amp-slider [lab]
  (let [ticks {:major 1/2 :minor 1/10 :paint true :snap true}]
    (slidepan/slider-panel lab :value 0.00 :min 0.00 :max 1.00
                           :formatter (fn [n] (format "%4.2f" n))
                           :ticks ticks)))

(defn combo-editor [performance]
  (let [parent* (atom nil)
        ;; Tone Panel 
        span-a1 (amp-slider "A1")
        span-a2 (amp-slider "A2")
        span-a3 (amp-slider "A3")
        span-a4 (amp-slider "A4")
        span-w1 (amp-slider "W1")
        span-w2 (amp-slider "W2")
        span-w3 (amp-slider "W3")
        span-w4 (amp-slider "W4")
        pan-amp (ss/grid-panel :rows 1
                                :items [(.widget span-a1 :pan-main)
                                        (.widget span-a2 :pan-main)
                                        (.widget span-a3 :pan-main)
                                        (.widget span-a4 :pan-main)]
                                :border (factory/title "Mix"))
        pan-wave (ss/grid-panel :rows 1
                                 :items [(.widget span-w1 :pan-main)
                                         (.widget span-w2 :pan-main)
                                         (.widget span-w3 :pan-main)
                                         (.widget span-w4 :pan-main)]
                                 :border (factory/title "Wave"))
        pan-mix (ss/grid-panel :columns 1 :items [pan-amp pan-wave])
        ;; Filter
        filter-group (ss/button-group)
        tb-filter-bypass (ss/toggle :text "Off" :group filter-group)
        tb-filter-lp (ss/toggle :text "Low" :group filter-group)
        tb-filter-hp (ss/toggle :text "High" :group filter-group)
        tb-filter-bp (ss/toggle :text "Band" :group filter-group)
        tb-filter-br (ss/toggle :text "Notch" :group filter-group)
        harmonic-group (ss/button-group)
        tb-ffreq-1 (ss/toggle :text "1" :group harmonic-group)
        tb-ffreq-2 (ss/toggle :text "2" :group harmonic-group)
        tb-ffreq-4 (ss/toggle :text "4" :group harmonic-group)
        tb-ffreq-8 (ss/toggle :text "8" :group harmonic-group)
        tb-ffreq-16 (ss/toggle :text "16" :group harmonic-group)
        tb-ffreq-32 (ss/toggle :text "32" :group harmonic-group)
        pan-filter (ss/grid-panel :columns 1
                                  :items [tb-filter-bypass tb-filter-lp 
                                          tb-filter-hp tb-filter-bp 
                                          tb-filter-br tb-ffreq-1 tb-ffreq-2 
                                          tb-ffreq-4 tb-ffreq-8 tb-ffreq-16 
                                          tb-ffreq-32]
                                  :border (factory/title "Filter"))
        span-detune (slidepan/slider-panel ""
                                           :min 0.0 :max 1.0 :value 0.0 
                                           :formatter (fn [n](format "%5.3f" n))
                                           :orientation :horizontal
                                           :title-position :west
                                           :value-position :east
                                           :ticks {:major 1/2 :minor 1/10 :paint true :snap false}
                                           :border (factory/title "Detune"))
        pan-tone (ss/border-panel :center pan-mix
                                   :east pan-filter
                                   :south (.widget span-detune :pan-main)
                                   :border (factory/title "Tone"))
        ;; Flanger Panel 
        span-frate (slidepan/slider-panel "Rate" :min 0.5 :max 10 :value 1.0
                                          :ticks {:major 1/2 :minor 1/10 :paint true :snap false}
                                          :formatter (fn [n](format "%4.2f" n)))
        span-fdepth (slidepan/slider-panel "Depth" :min 0.0 :max 1.0 :value 0.0
                                           :ticks {:major 1/2 :minor 1/10 :paint true :snap true}
                                           :formatter (fn [n](format "%4.2f" n)))
        span-ffb (slidepan/slider-panel "FB" :min -1.0 :max +1.0 :value 0.5
                                        :ticks {:major 1/2 :minor 1/10 :paint true :snap true}
                                        :formatter (fn [n](format "%+5.2f" n)))
        span-fmix (slidepan/slider-panel "Mix" :min 0.0 :max 1.0 :value 0.0
                                         :ticks {:major 1/2 :minor 1/10 :paint true :snap false}
                                         :formatter (fn [n](format "%4.2f" n)))
        pan-flanger (ss/grid-panel :rows 1
                                   :items [(.widget span-frate :pan-main)
                                           (.widget span-fdepth :pan-main)
                                           (.widget span-ffb :pan-main)
                                           (.widget span-fmix :pan-main)]
                                   :border (factory/title "Flanger"))
        ;; Vibrato Panel
        span-vrate (slidepan/slider-panel "Rate" :min 1.0 :max 10.0 :value 6.0
                                          :ticks {:major 1/2 :minor 1/10 :paint true :snap false}
                                          :formatter (fn [n](format "%5.3f" n)))
                                          
        span-vsens (slidepan/slider-panel "Sens" :min 0.0 :max 0.2 :value 0.01
                                          :ticks {:major 1/2 :minor 1/10 :paint true :snap false}
                                          :formatter (fn [n](format "%5.3f" n)))
        pan-vibrato (ss/grid-panel :rows 1
                                   :items [(.widget span-vrate :pan-main)
                                           (.widget span-vsens :pan-main)]
                                   :border (factory/title "Vibrato"))
        ;; Reverb panel 
        span-reverb (slidepan/slider-panel "" :min 0.0 :max 1.0 :value 0.0
                                           :ticks {:major 1/2 :minor 1/10 :paint true :snap true}
                                           :formatter (fn [n](format "%5.3f" n))
                                           :border (factory/title "Reverb"))
        ;; Amp Panel
        span-amp (slidepan/slider-panel "" :max 0.0 :min -18.0 :value 0.0
                                        :ticks {:major 1/18 :minor 1/18 :paint true :snap true}
                                        :formatter (fn [n](format "%-2d" (int n)))
                                        :border (factory/title "Amp (db)"))
        pan-mod-north (ss/grid-panel :rows 1
                                     :items [pan-vibrato 
                                             (.widget span-reverb :pan-main)
                                             (.widget span-amp :pan-main)])
        pan-mod (ss/grid-panel :columns 1
                               :items [pan-mod-north pan-flanger])
        pan-combo-main (ss/border-panel
                        :center (ss/grid-panel :rows 1
                                               :items [pan-tone pan-mod]))
        widget-map {
                    }
        ied (reify cadejo.ui.instruments.instrument-editor/InstrumentEditor
              
              (set-parent! [this iefw]
                (reset! parent* iefw))

              (widgets [this]
                (merge (.widgets @parent*)
                       widget-map))

              (widget [this key]
                (or (get (.widgets this) key)
                    (umsg/warning (format "InstrumentEditor does not have %s widget" key))))
              
              (status! [this msg]
                (.status! @parent* msg))

              (warning! [this msg]
                (.warning! @parent* msg))

              (push-undo-state! [this msg]
                (.push-undo-state! @parent* msg))

              (push-redo-state! [this msg]
                (.push-redo-state! @parent* msg))

              (sync-ui! [this]
                (println "(Combo) InstrumentEditor.sync-ui not implemented")
                ))
        ieframework (let [ifw (framework/instrument-editor-framework performance)]
                      (.client-editor! ifw ied)
                      ifw)
        ]
    (ss/config! (.widget ied :frame) :size [930 :by 520])
    (.add-tab! ieframework "Combo" pan-combo-main)
    ied))
              
              
                    
