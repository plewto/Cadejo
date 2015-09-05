(println "--> Loading Cobalt")

(ns cadejo.instruments.cobalt.cobalt-engine
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu])
  (:require [cadejo.modules.env :as cenv])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.poly-rotate-mode])
  (:require [cadejo.midi.performance])
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.instruments.cobalt.vibrato])
  (:require [cadejo.instruments.cobalt.efx])
  (:require [cadejo.instruments.cobalt.program])
  (:require [cadejo.instruments.cobalt.data])
  (:require [cadejo.instruments.cobalt.pp :as pp])
  (:require [cadejo.instruments.cobalt.randprog.random-program :as randprog])
  (:require [cadejo.instruments.cobalt.editor.cobalt-editor :as cobalt-ed]))

(def ^:private clipboard* (atom nil))

(def cobalt-descriptor
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :cobalt "Cobalt" clipboard*)]
    (.add-controller! d :cc1 "Vibrato" 1)
    (.add-controller! d :cc5 "PortTime" 5) 
    (.add-controller! d :cc7 "Volume" 7)
    (.add-controller! d :cc9 "Pitch Env" 9)
    (.add-controller! d :cca "CCA" 16)
    (.add-controller! d :ccb "CCB" 17)
    (.set-editor-constructor! d cobalt-ed/cobalt-editor)
    (.initial-program! d con/initial-cobalt-program)
    (.program-generator! d randprog/random-cobalt-program)
    (.help-topic! d :cobalt)
    d))

(defcgen op-amp-modulators [amp lf1 p-lf1]
  (:kr (* amp 
          (qu/amp-modulator-depth lf1 p-lf1))))

(defcgen op-amp-midi-modulators [cca ccb vel prss p-cca p-ccb p-vel p-prss]
  (:kr (* (qu/amp-modulator-depth cca p-cca)
          (qu/amp-modulator-depth ccb p-ccb)
          (qu/amp-modulator-depth vel p-vel)
          (qu/amp-modulator-depth prss p-prss))))
         
(defcgen op-amp-keytrack [note op-key left right]
  (:ir (dbamp (qu/keytrack:ir note op-key right op-key left))))

(defcgen op-freq [f0 penv detune penv-depth]
  (:kr (let [f1 (* f0 detune)]
         (+ f1 
            (* f1 penv penv-depth)))))

(defcgen op-env [att dcy1 dcy2 rel pk bp sus gate]
  (:kr 
   (let [e (cenv/addsr2 att dcy1 dcy2 rel pk bp sus 0 gate)]
     (* e e))))

(defcgen pitch-env [t1 t2 t3 a0 a1 a2 a3 gate]
  (:kr 
   (env-gen:kr (envelope [0 a0 a1 a2 a3]
                         [t1 t2 t3 t3]
                         :linear 2 2)
               :gate gate
               :action NO-ACTION)))

(defsynth CobaltVoice [freq 440
                       note 69
                       gate 0
                       velocity 1.0
                       port-time 1.0
                       port-time<-cc5 1.0
                       pe-a0 0.00                ; pitch env          
                       pe-a1 0.00                ;     amp values -/+ 1.0 
                       pe-a2 0.00 
                       pe-a3 0.00
                       pe-t1 1.00                ;     time a0->a1
                       pe-t2 1.00
                       pe-t3 1.00
                       pe<-cc9 0.0
                       lfo1-freq 5.00              ; lfo1
                       lfo1<-cca 0.0
                       lfo1<-pressure 0.0
                       lfo1-bleed 0     ; = 0 -> use delay, = 1 -> ignore delay
                       lfo1-delay 2.0
                       ;; OP1
                       op1-amp 1.000 
                       op1-amp<-lfo1 0.00
                       op1-amp<-cca 0.00
                       op1-amp<-ccb 0.00
                       op1-amp<-velocity 0.00
                       op1-amp<-pressure 0.00
                       op1-keyscale-key 60 
                       op1-keyscale-left 0 
                       op1-keyscale-right 0
                       op1-detune 1.00
                       op1<-penv 1.00
                       op1-attack 0.00
                       op1-decay1 0.00
                       op1-decay2 0.00
                       op1-release 0.00
                       op1-peak 1.00
                       op1-breakpoint 1.00
                       op1-sustain 1.00
                       fm1-detune 1.00
                       fm1-bias 0.00
                       fm1-amp 0.00
                       fm1<-env 0.00
                       fm1-lag 0.000
                       fm1-keyscale-left 0
                       fm1-keyscale-right 0
                       ;; OP2
                       op2-amp 0.000 
                       op2-amp<-lfo1 0.00
                       op2-amp<-cca 0.00
                       op2-amp<-ccb 0.00
                       op2-amp<-velocity 0.00
                       op2-amp<-pressure 0.00
                       op2-keyscale-key 60 
                       op2-keyscale-left 0 
                       op2-keyscale-right 0
                       op2-detune 2.00
                       op2<-penv 1.00
                       op2-attack 0.00
                       op2-decay1 0.00
                       op2-decay2 0.00
                       op2-release 0.00
                       op2-peak 1.00
                       op2-breakpoint 1.00
                       op2-sustain 1.00
                       fm2-detune 1.00
                       fm2-bias 0.00
                       fm2-amp 0.00
                       fm2<-env 0.00
                       fm2-lag 0.00
                       fm2-keyscale-left 0
                       fm2-keyscale-right 0
                       ;; OP3
                       op3-amp 0.000 
                       op3-amp<-lfo1 0.00
                       op3-amp<-cca 0.00
                       op3-amp<-ccb 0.00
                       op3-amp<-velocity 0.00
                       op3-amp<-pressure 0.00
                       op3-keyscale-key 60 
                       op3-keyscale-left 0 
                       op3-keyscale-right 0
                       op3-detune 3.00
                       op3<-penv 1.00
                       op3-attack 0.00
                       op3-decay1 0.00
                       op3-decay2 0.00
                       op3-release 0.00
                       op3-peak 1.00
                       op3-breakpoint 1.00
                       op3-sustain 1.00
                       fm3-detune 1.00
                       fm3-bias 0.00
                       fm3-amp 0.00
                       fm3<-env 0.00
                       fm3-lag 0.00
                       fm3-keyscale-left 0
                       fm3-keyscale-right 0
                       ;; OP4
                       op4-amp 0.000 
                       op4-amp<-lfo1 0.00
                       op4-amp<-cca 0.00
                       op4-amp<-ccb 0.00
                       op4-amp<-velocity 0.00
                       op4-amp<-pressure 0.00
                       op4-keyscale-key 60 
                       op4-keyscale-left 0 
                       op4-keyscale-right 0
                       op4-detune 4.00
                       op4<-penv 1.00
                       op4-attack 0.00
                       op4-decay1 0.00
                       op4-decay2 0.00
                       op4-release 0.00
                       op4-peak 1.00
                       op4-breakpoint 1.00
                       op4-sustain 1.00
                       fm4-detune 1.00
                       fm4-bias 0.00
                       fm4-amp 0.00
                       fm4<-env 0.00
                       fm4-lag 0.00
                       fm4-keyscale-left 0
                       fm4-keyscale-right 0
                       ;; OP5
                       op5-amp 0.000 
                       op5-amp<-lfo1 0.00
                       op5-amp<-cca 0.00
                       op5-amp<-ccb 0.00
                       op5-amp<-velocity 0.00
                       op5-amp<-pressure 0.00
                       op5-keyscale-key 60 
                       op5-keyscale-left 0 
                       op5-keyscale-right 0
                       op5-detune 4.00
                       op5<-penv 1.00
                       op5-attack 0.00
                       op5-decay1 0.00
                       op5-decay2 0.00
                       op5-release 0.00
                       op5-peak 1.00
                       op5-breakpoint 1.00
                       op5-sustain 1.00
                       fm5-detune 1.00
                       fm5-bias 0.00
                       fm5-amp 0.00
                       fm5<-env 0.00
                       fm5-lag 0.00
                       fm5-keyscale-left 0
                       fm5-keyscale-right 0
                       ;; OP6
                       op6-amp 0.000 
                       op6-amp<-lfo1 0.00
                       op6-amp<-cca 0.00
                       op6-amp<-ccb 0.00
                       op6-amp<-velocity 0.00
                       op6-amp<-pressure 0.00
                       op6-keyscale-key 60 
                       op6-keyscale-left 0 
                       op6-keyscale-right 0
                       op6-detune 4.00
                       op6<-penv 1.00
                       op6-attack 0.00
                       op6-decay1 0.00
                       op6-decay2 0.00
                       op6-release 0.00
                       op6-peak 1.00
                       op6-breakpoint 1.00
                       op6-sustain 1.00
                       fm6-detune 1.00
                       fm6-bias 0.00
                       fm6-amp 0.00
                       fm6<-env 0.00
                       fm6-lag 0.00
                       fm6-keyscale-left 0
                       fm6-keyscale-right 0
                       ;; BUZZ
                       bzz-amp 1.000               ; linear
                       bzz-amp<-lfo1 0.00
                       bzz-amp<-cca 0.00
                       bzz-amp<-ccb 0.00
                       bzz-amp<-velocity 0.00
                       bzz-amp<-pressure 0.00
                       bzz-keyscale-key 60        ; MIDI key number
                       bzz-keyscale-left 0        ; db/octave
                       bzz-keyscale-right 0
                       bzz-detune 1.00
                       bzz<-penv 1.00
                       bzz-attack 0.00
                       bzz-decay1 0.00
                       bzz-decay2 0.00
                       bzz-release 0.00
                       bzz-peak 1.00
                       bzz-breakpoint 1.00
                       bzz-sustain 1.00
                       bzz-harmonics 16           ; int > 0
                       bzz-harmonics<-env 0       
                       bzz-harmonics<-cca 0
                       bzz-hp-track 1.00          ; relative to f0
                       bzz-hp-track<-env  0.00    ; env adds to tracking
                       ;; NOISE
                       nse-amp 0.000 
                       nse-amp<-lfo1 0.00
                       nse-amp<-cca 0.00
                       nse-amp<-velocity 0.00
                       nse-amp<-pressure 0.00
                       nse-keyscale-key 60
                       nse-keyscale-left 0
                       nse-keyscale-right 0
                       nse-detune 9.00
                       nse<-penv 1.00
                       nse-attack 0.00
                       nse-decay1 0.00
                       nse-decay2 0.00
                       nse-release 0.00
                       nse-peak 1.00
                       nse-breakpoint 1.00
                       nse-sustain 1.00
                       nse-bw 10
                       nse2-amp 0.000
                       nse2-detune 1.000
                       nse2-bw 10
                       nse2-lag 0.000
                       filter-freq 0
                       filter-track 1
                       filter-res 0
                       filter-attack 0
                       filter-decay 0
                       filter-sustain 1
                       filter-release 0
                       filter<-env 0
                       filter<-pressure 0
                       filter<-cca 0
                       filter<-ccb 0
                       filter-res<-cca 0
                       filter-res<-ccb 0
                       filter-mode 0     ; -/+1   -1 -> lp   +1 -> bp
                       filter2-detune 1  ; bp filter freq rel to lp filter
                       filter2-lag 0.0
                       dist-mix 1       ; 0 -> dry 1 -> wet
                       dist-pregain 1
                       dist<-cca 4
                       dist<-ccb 0
                       op1-enable 1
                       op2-enable 1
                       op3-enable 1
                       op4-enable 1
                       op5-enable 1
                       op6-enable 1
                       nse-enable 1
                       bzz-enable 1
                       bend-bus 0                  ; control buses
                       pressure-bus 0
                       cca-bus 0
                       ccb-bus 0
                       cc5-bus 0        ; port time
                       cc9-bus 0        ; penv depth
                       vibrato-bus 0
                       out-bus 0] 
  (let [bend (in:kr bend-bus)
        pressure (in:kr pressure-bus)
        cca (in:kr cca-bus)
        ccb (in:kr ccb-bus)
        cc5 (in:kr cc5-bus)
        cc9 (in:kr cc9-bus)
        ptime (* port-time (qu/amp-modulator-depth cc5 port-time<-cc5))
        vibrato (+ 1 (in:kr vibrato-bus))
        f0 (* (lag2:kr freq ptime) 
              bend vibrato)
        penv (* (pitch-env:kr pe-t1 pe-t2 pe-t3 pe-a0 pe-a1 pe-a2 pe-a3 gate)
                (qu/amp-modulator-depth cc9 pe<-cc9))
        lfo1-amp (+ (* pressure lfo1<-pressure)
                    (* cca lfo1<-cca)
                    (* lfo1-bleed
                       (cenv/delay-env lfo1-delay (* 1/2 lfo1-delay) gate)))
        lfo1 (* lfo1-amp
                (sin-osc:kr lfo1-freq))
        op1 (let [ac (* (op-amp-modulators op1-amp lfo1 op1-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op1-amp<-cca op1-amp<-ccb
                                                op1-amp<-velocity op1-amp<-pressure)
                        (op-amp-keytrack:ir note op1-keyscale-key 
                                            op1-keyscale-left op1-keyscale-right))
                  e (op-env op1-attack op1-decay1 op1-decay2 op1-release
                            op1-peak op1-breakpoint op1-sustain gate)
                  fc (op-freq f0 penv op1-detune op1<-penv)
                  fm (+ fm1-bias (* fm1-detune fc))
                  am (* fc fm1-amp 
                        (op-amp-keytrack:ir note op1-keyscale-key 
                                            fm1-keyscale-left fm1-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm1-lag) fm1<-env))]
              (* ac e op1-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        op2 (let [ac (* (op-amp-modulators op2-amp lfo1 op2-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op2-amp<-cca op2-amp<-ccb
                                                op2-amp<-velocity op2-amp<-pressure)
                        (op-amp-keytrack:ir note op2-keyscale-key 
                                            op2-keyscale-left op2-keyscale-right))
                  e (op-env op2-attack op2-decay1 op2-decay2 op2-release
                            op2-peak op2-breakpoint op2-sustain gate)
                  fc (op-freq f0 penv op2-detune op2<-penv)
                  fm (+ fm2-bias (* fm2-detune fc))
                  am (* fc fm2-amp 
                        (op-amp-keytrack:ir note op2-keyscale-key 
                                            fm2-keyscale-left fm2-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm2-lag) fm2<-env))]
              (* ac e op2-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        op3 (let [ac (* (op-amp-modulators op3-amp lfo1 op3-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op3-amp<-cca op3-amp<-ccb
                                                op3-amp<-velocity op3-amp<-pressure)
                        (op-amp-keytrack:ir note op3-keyscale-key 
                                            op3-keyscale-left op3-keyscale-right))
                  e (op-env op3-attack op3-decay1 op3-decay2 op3-release
                            op3-peak op3-breakpoint op3-sustain gate)
                  fc (op-freq f0 penv op3-detune op3<-penv)
                  fm (+ fm3-bias (* fm3-detune fc))
                  am (* fc fm3-amp 
                        (op-amp-keytrack:ir note op3-keyscale-key 
                                            fm3-keyscale-left fm3-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm3-lag) fm3<-env))]
              (* ac e op3-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        op4 (let [ac (* (op-amp-modulators op4-amp lfo1 op4-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op4-amp<-cca op4-amp<-ccb
                                                op4-amp<-velocity op4-amp<-pressure)
                        (op-amp-keytrack:ir note op4-keyscale-key 
                                            op4-keyscale-left op4-keyscale-right))
                  e (op-env op4-attack op4-decay1 op4-decay2 op4-release
                            op4-peak op4-breakpoint op4-sustain gate)
                  fc (op-freq f0 penv op4-detune op4<-penv)
                  fm (+ fm4-bias (* fm4-detune fc))
                  am (* fc fm4-amp 
                        (op-amp-keytrack:ir note op4-keyscale-key 
                                            fm4-keyscale-left fm4-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm4-lag) fm4<-env))]
              (* ac e op4-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        op5 (let [ac (* (op-amp-modulators op5-amp lfo1 op5-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op5-amp<-cca op5-amp<-ccb
                                                op5-amp<-velocity op5-amp<-pressure)
                        (op-amp-keytrack:ir note op5-keyscale-key 
                                            op5-keyscale-left op5-keyscale-right))
                  e (op-env op5-attack op5-decay1 op5-decay2 op5-release
                            op5-peak op5-breakpoint op5-sustain gate)
                  fc (op-freq f0 penv op5-detune op5<-penv)
                  fm (+ fm5-bias (* fm5-detune fc))
                  am (* fc fm5-amp 
                        (op-amp-keytrack:ir note op5-keyscale-key 
                                            fm5-keyscale-left fm5-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm5-lag) fm5<-env))]
              (* ac e op5-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        op6 (let [ac (* (op-amp-modulators op6-amp lfo1 op6-amp<-lfo1)
                        (op-amp-midi-modulators cca ccb velocity pressure
                                                op6-amp<-cca op6-amp<-ccb
                                                op6-amp<-velocity op6-amp<-pressure)
                        (op-amp-keytrack:ir note op6-keyscale-key 
                                            op6-keyscale-left op6-keyscale-right))
                  e (op-env op6-attack op6-decay1 op6-decay2 op6-release
                            op6-peak op6-breakpoint op6-sustain gate)
                  fc (op-freq f0 penv op6-detune op6<-penv)
                  fm (+ fm6-bias (* fm6-detune fc))
                  am (* fc fm6-amp 
                        (op-amp-keytrack:ir note op6-keyscale-key 
                                            fm6-keyscale-left fm6-keyscale-right)
                        (qu/amp-modulator-depth (lag2:kr e fm6-lag) fm6<-env))]
              (* ac e op6-enable (sin-osc:ar (+ fc (* am (sin-osc:ar fm))))))
        bzz (let [a (* (op-amp-modulators bzz-amp lfo1 bzz-amp<-lfo1)
                       (op-amp-midi-modulators cca ccb velocity pressure bzz-amp<-cca
                                               bzz-amp<-ccb bzz-amp<-velocity
                                               bzz-amp<-pressure)
                       (op-amp-keytrack:ir note bzz-keyscale-key bzz-keyscale-left
                                           bzz-keyscale-right))
                  f (op-freq f0 penv bzz-detune bzz<-penv)
                  e (op-env bzz-attack bzz-decay1 bzz-decay2 bzz-release bzz-peak
                            bzz-breakpoint bzz-sustain gate)
                  h (max 0 (+ bzz-harmonics 
                               (* e bzz-harmonics<-env)
                               (* cca bzz-harmonics<-cca)))
                  hp-cutoff (qu/clamp (* f0 (+ bzz-hp-track
                                               (* e bzz-hp-track<-env)))
                                      con/min-buzz-hp-freq
                                      con/max-buzz-hp-freq)]
              (* e a bzz-enable (hpf (blip:ar f h) hp-cutoff)))
        nse (let [a1 (* (dbamp con/noise-amp-boost)
                        (op-amp-modulators nse-amp lfo1 nse-amp<-lfo1 0)
                        (op-amp-midi-modulators cca 0 velocity pressure nse-amp<-cca
                                                0 nse-amp<-velocity
                                                nse-amp<-pressure)
                        (op-amp-keytrack:ir note nse-keyscale-key nse-keyscale-left
                                            nse-keyscale-right))

                  a2 (* (dbamp con/noise-amp-boost)
                        (op-amp-modulators nse2-amp lfo1 nse-amp<-lfo1 0)
                        (op-amp-midi-modulators cca 0 velocity pressure nse-amp<-cca
                                                0 nse-amp<-velocity
                                                nse-amp<-pressure)
                        (op-amp-keytrack:ir note nse-keyscale-key nse-keyscale-left
                                            nse-keyscale-right))


                  f1 (max con/min-noise-filter-cutoff  
                          (op-freq f0 penv nse-detune nse<-penv))

                  f2 (max con/min-noise-filter-cutoff
                           (op-freq f0 penv nse2-detune nse<-penv))

                  e1 (op-env nse-attack nse-decay1 nse-decay2 nse-release nse-peak
                             nse-breakpoint nse-sustain gate)
                  e2 (lag2:kr e1 nse2-lag)
                  bw1 (qu/clamp nse-bw con/min-noise-filter-bw con/max-noise-filter-bw)
                  bw2 (qu/clamp nse2-bw con/min-noise-filter-bw con/max-noise-filter-bw)
                  rq1 (/ bw1 f1)
                  rq2 (/ bw2 f2)
                  agc1 (qu/clamp (/ 48.0 bw1) 5 1)
                  agc2 (qu/clamp (/ 48.0 bw2) 5 1)]
              (* nse-enable (+ (* a1 e1 agc1 (* (sin-osc:ar f1)(bpf (white-noise:ar) f1 rq1)))
                               (* a2 e2 agc2 (* (sin-osc:ar f2)(bpf (white-noise:ar) f2 rq2))))))
                               
        filter-in (+ op1 op2 op3 op4 op5 op6 nse bzz)
        filter-out (let [fe (cenv/addsr filter-attack 0 filter-decay filter-release 1.0 filter-sustain gate)
                         fenv (* fe fe)
                         ffreq (qu/clamp (+ filter-freq
                                            (* f0 filter-track)
                                            (* con/filter-mod-scale
                                               (+ (* fenv filter<-env)
                                                  (* pressure filter<-pressure)
                                                  (* cca filter<-cca)
                                                  (* ccb filter<-ccb))))
                                         con/min-filter-cutoff con/max-filter-cutoff)
                         moog-res (qu/clamp (* con/filter-res-scale 
                                          (+ filter-res
                                             (* cca filter-res<-cca)
                                             (* ccb filter-res<-ccb)))
                                       con/min-filter-res con/max-filter-res)
                         moog-out (moog-ff filter-in ffreq moog-res)
                         bp-freq (lag:kr (qu/clamp (* ffreq filter2-detune)
                                           con/min-filter-cutoff con/max-filter-cutoff)
                                         filter2-lag)
                         bp-rq (+ (* -7/32 moog-res) 1)
                         bp-out (bpf:ar filter-in bp-freq bp-rq)]
                     (x-fade2 moog-out bp-out filter-mode))
        fold-pregain (max 1.0 (+ dist-pregain 
                                 (* cca dist<-cca)
                                 (* ccb dist<-ccb)))
        fold-agc (qu/clamp (/ 2.0 fold-pregain)
                           0.50 1.00)
        fold-in (* fold-pregain filter-out)
        fold-out (qu/efx-mixer filter-out (* fold-agc (fold2 fold-in 1.0)) dist-mix)
        ]
    (tap :pregain 5 fold-pregain)
    (tap :agc 5 fold-agc)
    (tap :cca 5 cca)
    (out:ar out-bus fold-out)))

(defn- create-performance [chanobj id keymode cc1 cc5 cc7 cc9 cca ccb]
  (let [bank (.clone cadejo.instruments.cobalt.program/bank)
        performance (cadejo.midi.performance/performance
                     chanobj id keymode bank cobalt-descriptor
                     [:cc1 cc1 :linear 0.0]
                     [:cc5 cc5 :linear 0.0]
                     [:cc7 cc7 :linear 1.0]
                     [:cc9 cc9 :linear 0.0]
                     [:cca cca :linear 0.0]
                     [:ccb ccb :linear 0.0])]
    (.put-property! performance :instrument-type :cobalt)
    (.set-bank! performance bank) ;; ISSUE Is this line necessary, if so why?
    (.pp-hook! bank pp/pp-cobalt)
    (.add-control-bus! performance :vibrato (control-bus))
    (.add-audio-bus! performance :efx-in (audio-bus))
    performance))

(defn- sleep 
  ([arg]
     (Thread/sleep 100)
     arg)
  ([]
     (sleep nil)))

(defn cobalt-mono [scene chan id & {:keys [cc1 cc5 cc7 cc9 cc1 cca ccb main-out]
                                    :or {cc1 1
                                         cc5 5 
                                         cc7 7
                                         cc9 9
                                         cca 16
                                         ccb 17
                                         main-out 0}}]
  (let [chanobj (.channel scene chan)
        keymode (cadejo.midi.mono-mode/mono-keymode :cobalt)
        performance (create-performance chanobj id keymode cc1 cc5 cc7 cc9 cca ccb)
        bend-bus (.control-bus performance :bend)
        pressure-bus (.control-bus performance :pressure)
        cc1-bus (.control-bus performance :cc1)
        cc5-bus (.control-bus performance :cc5)
        cc7-bus (.control-bus performance :cc7)
        cc9-bus (.control-bus performance :cc9)
        cca-bus (.control-bus performance :cca)
        ccb-bus (.control-bus performance :ccb)
        vibrato-bus (.control-bus performance :vibrato)
        efx-in-bus (.audio-bus performance :efx-in)
        vibrato-block (cadejo.instruments.cobalt.vibrato/Vibrato 
                       :cc1-bus cc1-bus
                       :pressure-bus pressure-bus
                       :vibrato-bus vibrato-bus)
        voice (CobaltVoice :bend-bus bend-bus :pressure-bus pressure-bus
                           :cc5-bus cc5-bus
                           :cc9-bus cc9-bus
                           :cca-bus cca-bus
                           :ccb-bus ccb-bus
                           :vibrato-bus vibrato-bus 
                           :out-bus efx-in-bus)
        efx-block (cadejo.instruments.cobalt.efx/CobaltEffects
                   :cca-bus cca-bus
                   :ccb-bus ccb-bus
                   :cc7-bus cc7-bus
                   :in-bus efx-in-bus
                   :out-bus main-out)]
    (.add-synth! performance :vibrato vibrato-block)
    (.add-synth! performance :efx efx-block)
    (.add-voice! performance voice)
    (sleep 100)
    (.reset chanobj)
    performance))


(defn- --cobalt-poly [scene chan keymode id cc1 cc5 cc7 cc9 cca ccb voice-count main-out]
  (let [chanobj (.channel scene chan)
        performance (create-performance chanobj id keymode cc1 cc5 cc7 cc9 cca ccb)
        bend-bus (.control-bus performance :bend)
        pressure-bus (.control-bus performance :pressure)
        cc1-bus (.control-bus performance :cc1)
        cc5-bus (.control-bus performance :cc5)
        cc7-bus (.control-bus performance :cc7)
        cc9-bus (.control-bus performance :cc9)
        cca-bus (.control-bus performance :cca)
        ccb-bus (.control-bus performance :ccb)
        vibrato-bus (.control-bus performance :vibrato)
        efx-in-bus (.audio-bus performance :efx-in)
        vibrato-block (cadejo.instruments.cobalt.vibrato/Vibrato 
                       :cc1-bus cc1-bus
                       :pressure-bus pressure-bus
                       :vibrato-bus vibrato-bus)
        voices (let [acc* (atom [])]
                (dotimes [i voice-count]
                  (let [v (CobaltVoice
                           :bend-bus bend-bus :pressure-bus pressure-bus
                           :cc5-bus cc5-bus
                           :cc9-bus cc9-bus
                           :cca-bus cca-bus 
                           :ccb-bus ccb-bus
                           :vibrato-bus vibrato-bus
                           :out-bus efx-in-bus)]
                    (swap! acc* (fn [q](conj q v)))
                    (sleep 100)))
                @acc*)
        efx-block (cadejo.instruments.cobalt.efx/CobaltEffects
                   :cca-bus cca-bus
                   :ccb-bus ccb-bus
                   :cc7-bus cc7-bus
                   :in-bus efx-in-bus
                   :out-bus main-out)]
    (.add-synth! performance :vibrato vibrato-block)
    (.add-synth! performance :efx efx-block)
    (doseq [v voices]
      (.add-voice! performance v))
    (.reset chanobj)
    performance))


(defn cobalt-poly [scene chan id & {:keys [cc1 cc5 cc7 cc9 cca ccb voice-count main-out]
                                  :or {cc1 1
                                       cc5 5 
                                       cc7 7
                                       cc9 9
                                       cca 16
                                       ccb 17
                                       voice-count 8
                                       main-out 0}}]
  (let [km (cadejo.midi.poly-mode/poly-keymode :Cobalt voice-count)]
    (--cobalt-poly scene chan km id cc1 cc5 cc7 cc9 cca ccb voice-count main-out)))

(defn cobalt-poly-rotate [scene chan id & {:keys [cc1 cc5 cc7 cc9 cca ccb voice-count main-out]
                                           :or {cc1 1
                                                cc5 5 
                                                cc7 7
                                                cc9 9
                                                cca 16
                                                ccb 17
                                                voice-count 8
                                                main-out 0}}]
  (let [km (cadejo.midi.poly-rotate-mode/poly-rotate-mode :Cobalt voice-count)]
    (--cobalt-poly scene chan km id cc1 cc5 cc7 cc9 cca ccb voice-count main-out)))

(.add-constructor! cobalt-descriptor :mono cobalt-mono)
(.add-constructor! cobalt-descriptor :rotate cobalt-poly-rotate)
(.add-constructor! cobalt-descriptor :poly cobalt-poly)
