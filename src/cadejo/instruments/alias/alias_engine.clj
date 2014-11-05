(println "--> Alias")
(ns cadejo.instruments.alias.alias-engine
  (:use [overtone.core])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.performance])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.instruments.alias.genpatch])
  (:require [cadejo.instruments.alias.program])
  (:require [cadejo.instruments.alias.pp])
  (:require [cadejo.instruments.alias.data])
  (:require [cadejo.instruments.alias.control])
  (:require [cadejo.instruments.alias.head])
  (:require [cadejo.instruments.alias.tone])
  (:require [cadejo.instruments.alias.efx])
  (:require [cadejo.instruments.alias.editor.alias-editor :as aliased]))


(def clipboard* (atom nil))

(def alias-descriptor 
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :alias "Matrix Synth" clipboard*)]
    (.add-controller! d :cc7 "Volume" 7)
    (.add-controller! d :cca "A"  1)
    (.add-controller! d :ccb "B" 16)
    (.add-controller! d :ccc "C" 17)
    (.add-controller! d :ccd "d"  4)
    (.set-editor-constructor! d aliased/alias-editor)
    (.initial-program! d {:port-time 0.00 :osc1-detune 1.00 :osc1-bias 0.0
			  :osc1-fm1-source 0 :osc1-fm1-depth 0 :osc1-fm1-lag 0 :osc1-fm2-source 0
			  :osc1-fm2-depth 0 :osc1-fm2-lag 0 :osc1-wave 0.00 :osc1-wave1-source 0
			  :osc1-wave1-depth 0 :osc1-wave2-source 0 :osc1-wave2-depth 0 :osc1-amp 0
			  :osc1-amp1-src 0 :osc1-amp1-depth 0 :osc1-amp1-lag 0 :osc1-amp2-src 0
			  :osc1-amp2-depth 0 :osc1-amp2-lag 0 :osc1-pan -1.0 :osc2-detune 1.00
			  :osc2-bias 0.0 :osc2-fm1-source 0 :osc2-fm1-depth 0 :osc2-fm1-lag 0
			  :osc2-fm2-source 0 :osc2-fm2-depth 0 :osc2-fm2-lag 0 :osc2-wave 0.50
			  :osc2-wave1-source 0 :osc2-wave1-depth 0 :osc2-wave2-source 0
			  :osc2-wave2-depth 0 :osc2-amp 0 :osc2-amp1-src 0 :osc2-amp1-depth 0
			  :osc2-amp1-lag 0 :osc2-amp2-src 0 :osc2-amp2-depth 0 :osc2-amp2-lag 0
			  :osc2-pan -1.0 :osc3-detune 1.00 :osc3-bias 0.0 :osc3-fm1-source 0
			  :osc3-fm1-depth 0 :osc3-fm1-lag 0 :osc3-fm2-source 0 :osc3-fm2-depth 0
			  :osc3-fm2-lag 0 :osc3-wave 0.00 :osc3-wave1-source 0 :osc3-wave1-depth 0
			  :osc3-wave2-source 0 :osc3-wave2-depth 0 :osc3-amp 0 :osc3-amp1-src 0
			  :osc3-amp1-depth 0 :osc3-amp1-lag 0 :osc3-amp2-src 0 :osc3-amp2-depth 0
			  :osc3-amp2-lag 0 :osc3-pan -1.0 :noise-param 0.50 :noise-lp 10000
			  :noise-hp 10 :noise-amp 0 :noise-amp1-src 0 :noise-amp1-depth 0
			  :noise-amp1-lag 0 :noise-amp2-src 0 :noise-amp2-depth 0 :noise-amp2-lag 0
			  :noise-pan +1.0 :ringmod-carrier -1.0 :ringmod-modulator -1.0
			  :ringmod-amp 0 :ringmod-amp1-src 0 :ringmod-amp1-depth 0
			  :ringmod-amp1-lag 0 :ringmod-amp2-src 0 :ringmod-amp2-depth 0
			  :ringmod-amp2-lag 0 :ringmod-pan +1.0 :env1-attack 0.001
			  :env1-decay1 0.001 :env1-decay2 0.001 :env1-release 0.001
			  :env1-peak 1.000 :env1-breakpoint 1.000 :env1-sustain 1.000
			  :env1-invert 0 :env2-attack 0.001 :env2-decay1 0.001 :env2-decay2 0.001
			  :env2-release 0.001 :env2-peak 1.000 :env2-breakpoint 1.000
			  :env2-sustain 1.000 :env2-invert 0 :env3-attack 0.001 :env3-decay1 0.001
			  :env3-decay2 0.001 :env3-release 0.001 :env3-peak 1.000
			  :env3-breakpoint 1.000 :env3-sustain 1.000 :lfo1-freq1-source 0
			  :lfo1-freq1-depth 7 :lfo1-freq2-source 0 :lfo1-freq2-depth 0
			  :lfo1-wave1-source 0 :lfo1-wave1-depth 0.5 :lfo1-wave2-source 0
			  :lfo1-wave2-depth 0 :lfo2-freq1-source 0 :lfo2-freq1-depth 7
			  :lfo2-freq2-source 0 :lfo2-freq2-depth 0 :lfo2-wave1-source 0
			  :lfo2-wave1-depth 0.5 :lfo2-wave2-source 0 :lfo2-wave2-depth 0
			  :lfo3-freq1-source 0 :lfo3-freq1-depth 7 :lfo3-freq2-source 0
			  :lfo3-freq2-depth 0 :lfo3-wave1-source 0 :lfo3-wave1-depth 0.5
			  :lfo3-wave2-source 0 :lfo3-wave2-depth 0 :stepper1-trigger 4
			  :stepper1-reset 32 :stepper1-min -10 :stepper1-max 10
			  :stepper1-step 1 :stepper1-reset-value -10 :stepper1-bias 0
			  :stepper1-scale 1/10 :stepper2-trigger 5 :stepper2-reset 32
			  :stepper2-min -10 :stepper2-max 10 :stepper2-step 1
			  :stepper2-reset-value -10 :stepper2-bias 0 :stepper2-scale 1/10
			  :divider1-pw 0.5 :divider1-p1 1.000 :divider1-p3 0.333 :divider1-p5 0.200
			  :divider1-p7 0.142 :divider1-bias 0.000 :divider1-scale-source 0
			  :divider1-scale-depth 1.0 :divider2-pw 0.5 :divider2-p2 0.500
			  :divider2-p4 0.250 :divider2-p6 0.167 :divider2-p8 0.125 :divider2-bias 0
			  :divider2-scale-source 0 :divider2-scale-depth 1.0 :lfnoise-freq-source 0
			  :lfnoise-freq-depth 1.0 :sh-source 12 :sh-rate 7 :sh-bias 0
			  :sh-scale 1 :a-source1 0 :a-depth1 0 :a-source2 0 :a-depth2 1
			  :b-source1 0 :b-depth1 0 :b-source2 0 :b-depth2 1 :c-source1 0
			  :c-depth1 0 :c-source2 0 :c-depth2 1 :d-source1 0 :d-depth1 0
			  :d-source2 0 :d-depth2 1 :e-source1 0 :e-depth1 0 :e-source2 0
			  :e-depth2 1 :f-source1 0 :f-depth1 0 :f-source2 0 :f-depth2 1
			  :g-source1 0 :g-depth1 0 :g-source2 0 :g-depth2 1 :h-source1 0
			  :h-depth1 0 :h-source2 0 :h-depth2 1 :distortion1-pregain 1.00
			  :distortion1-param 0 :distortion1-param-source 0
			  :distortion1-param-depth 0 :distortion1-mix 0.00 :filter1-res 0.0
			  :filter1-res-source 0 :filter1-res-depth 0 :filter1-freq 10000
			  :filter1-freq1-source 0 :filter1-freq1-depth 0 :filter1-freq2-source 0
			  :filter1-freq2-depth 0 :filter1-pan -0.75 :filter1-pan-source 0
			  :filter1-pan-depth 0 :filter1-mode 0 :filter1-postgain 1.00
			  :pitchshift-ratio 1.00 :pitchshift-ratio-source 0
			  :pitchshift-ratio-depth 0 :pitchshift-pitch-dispersion 0
			  :pitchshift-time-dispersion 0 :pitchshift-mix -99 :flanger-mod-source 0
			  :flanger-mod-depth 0 :flanger-lfo-amp 0.1 :flanger-lfo-rate 1.0
			  :flanger-feedback 0.5 :flanger-mix -99  :flanger-crossmix 0.0
			  :echo1-delay 0.25 :echo1-delay-source 0 :echo1-delay-depth 0
			  :echo1-feedback 0 :echo1-damp 0.0 :echo1-pan -0.25 :echo1-amp-source 0
			  :echo1-amp-depth 0 :echo1-mix -99 :echo2-delay 0.125
			  :echo2-delay-source 0 :echo2-delay-depth 0 :echo2-feedback 0.5
			  :echo2-damp 0.0 :echo2-mix -99 :echo2-amp-source 0 :echo2-amp-depth 0
			  :echo2-pan -0.25 :dry-mix 0 :amp 0.20 :cc7->volume 0})
    (.program-generator! d cadejo.instruments.alias.genpatch/random-alias-program)
    (.help-topic! d :alias)
    d))

(defn create-performance [chanobj id keymode main-out
                          cca ccb ccc ccd cc-volume]
  (let [bank (.clone cadejo.instruments.alias.program/bank)
        performance (cadejo.midi.performance/performance chanobj id keymode
                                                         bank alias-descriptor
                                                         [:cca cca :linear 0.0]
                                                         [:ccb ccb :linear 0.0]
                                                         [:ccc ccc :linear 0.0]
                                                         [:ccd ccd :linear 0.0]
                                                         [:cc7 cc-volume :linear 1.0])]
    (.put-property! performance :instrument-type :alias)
    (.add-controller! performance :cc7 cc-volume :linear 1.0)
    (.set-bank! performance bank)
    (let [a-bus (control-bus)
          b-bus (control-bus)
          c-bus (control-bus)
          d-bus (control-bus)
          e-bus (control-bus)
          f-bus (control-bus)
          g-bus (control-bus)
          h-bus (control-bus)
          env1-bus (control-bus)
          env2-bus (control-bus)
          env3-bus (control-bus)
          lfo1-bus (control-bus)
          lfo2-bus (control-bus)
          lfo3-bus (control-bus)
          filter-in-bus (audio-bus 2)
          filter-out-bus (audio-bus 2)
          efx-in-bus (audio-bus 2)]
      (.pp-hook! bank cadejo.instruments.alias.pp/pp-alias)
      (.add-control-bus! performance :a a-bus)
      (.add-control-bus! performance :b b-bus)
      (.add-control-bus! performance :c c-bus)
      (.add-control-bus! performance :d d-bus)
      (.add-control-bus! performance :e e-bus)
      (.add-control-bus! performance :f f-bus)
      (.add-control-bus! performance :g g-bus)
      (.add-control-bus! performance :h h-bus)
      (.add-control-bus! performance :env1 env1-bus)
      (.add-control-bus! performance :env2 env2-bus)
      (.add-control-bus! performance :env3 env3-bus)
      (.add-control-bus! performance :lfo1 lfo1-bus)
      (.add-control-bus! performance :lfo2 lfo2-bus)
      (.add-control-bus! performance :lfo3 lfo3-bus)
      (.add-audio-bus! performance :filter-in filter-in-bus)
      (.add-audio-bus! performance :efx-in efx-in-bus)
      (.add-audio-bus! performance :main-out main-out)
      performance)))
  
(defn- sleep 
  ([arg]
     (Thread/sleep 100)
     arg)
  ([]
     (sleep nil)))

;; cca - general controller - default vibrato
;; ccb - general controller
;; ccc - general controller
;; ccd - general controller
;; cc7 - volume

(defn alias-mono
  ([scene chan id & {:keys [cca ccb ccc ccd cc7 main-out]
                     :or {cca 1
                          ccb 16
                          ccc 17
                          ccd 4
                          cc7 7
                          main-out 0}}]
     (let [chanobj (.channel scene chan)
           keymode (cadejo.midi.mono-mode/mono-keymode :Alias)
           performance (create-performance chanobj id keymode main-out
                                           cca ccb ccc ccd cc7)
           bend-bus (.control-bus performance :bend)
           pressure-bus (.control-bus performance :pressure)
           cca-bus (.control-bus performance cca)
           ccb-bus (.control-bus performance ccb)
           ccc-bus (.control-bus performance ccc)
           ccd-bus (.control-bus performance ccd)
           volume-bus (.control-bus performance cc7)
           a-bus (.control-bus performance :a)
           b-bus (.control-bus performance :b)
           c-bus (.control-bus performance :c)
           d-bus (.control-bus performance :d)
           e-bus (.control-bus performance :e)
           f-bus (.control-bus performance :f)
           g-bus (.control-bus performance :g)
           h-bus (.control-bus performance :h)
           env1-bus (.control-bus performance :env1)
           env2-bus (.control-bus performance :env2)
           env3-bus (.control-bus performance :env3)
           lfo1-bus (.control-bus performance :lfo1)
           lfo2-bus (.control-bus performance :lfo2)
           lfo3-bus (.control-bus performance :lfo3)
           filter-in-bus (.audio-bus performance :filter-in)
           efx-in-bus (.audio-bus performance :efx-in)
           control-block (cadejo.instruments.alias.control/ControlBlock)
           head-block (sleep (cadejo.instruments.alias.head/AliasHead))
           filter1 (sleep (cadejo.instruments.alias.tone/ToneBlock1))
           filter2 (sleep (cadejo.instruments.alias.tone/ToneBlock2))
           efx-block (sleep (cadejo.instruments.alias.efx/EfxBlock))]
       (sleep)
       (ctl control-block  
            :pressure-bus pressure-bus
            :cca-bus cca-bus
            :ccb-bus ccb-bus
            :ccc-bus ccc-bus
            :ccd-bus ccd-bus
            :env1-bus env1-bus
            :env2-bus env2-bus
            :env3-bus env3-bus
            :lfo1-bus lfo1-bus
            :lfo2-bus lfo2-bus
            :lfo3-bus lfo3-bus
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus)
       (sleep)
       (ctl head-block 
            :bend-bus bend-bus
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :out-bus filter-in-bus)
       (sleep)
       (ctl filter1 
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :env3-bus env3-bus
            :in-bus filter-in-bus
            :out-bus efx-in-bus)
       (sleep)
       (ctl filter2
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :env3-bus env3-bus
            :in-bus filter-in-bus
            :out-bus efx-in-bus)
       (sleep)
       (ctl efx-block 
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :cc-volume-bus volume-bus
            :in-bus efx-in-bus
            :out-bus main-out)
       (sleep)
       (ctl head-block :mute-amp 1)
       (.add-synth! performance :control control-block)
       (.add-synth! performance :filter1 filter1)
       (.add-synth! performance :filter2 filter2)
       (.add-synth! performance :efx efx-block)
       (.add-voice! performance head-block)
       (.reset chanobj)
       performance)))

(.add-constructor! alias-descriptor :mono alias-mono)
