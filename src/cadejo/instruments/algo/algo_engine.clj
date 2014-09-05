(println "--> ALGO")

;; [3]-->[2]-->(hp)-->[1]   feedback on 6 and 8 
;;                          feedback from 7 to 8 if possible
;; [5]---------+--->[4]
;; [6]-->(hp)--+
;;
;; [8]-->(hp)-->[7]

(ns cadejo.instruments.algo.algo-engine
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.instruments.algo.efx])
  (:require [cadejo.instruments.algo.program])
  (:require [cadejo.instruments.algo.pp])
  (:require [cadejo.instruments.algo.data])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.performance])
  (:require [cadejo.modules.env :as cenv])
  (:require [cadejo.modules.qugen :as qu])
  (:use [overtone.core]))

(def clipboard* (atom nil))

(def algo-descriptor 
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :algo "FM Synth" clipboard*)]
    (.add-controller! d :cc1 "Vibrato" 1)
    (.add-controller! d :cc7 "Volume" 7)
    (.add-controller! d :cca "A" 16)
    (.add-controller! d :ccb "B" 17)
    (.add-controller! d :ccc "Echo mix" 91)
    (.add-controller! d :ccc "Reverb mix" 92)
    d))

(defcgen op-freq [f0 detune bias]
  (:kr 
   (+ bias (* detune f0))))

(defcgen op-amp [amp
                 note 
                 left-key left-scale
                 right-key right-scale
                 velocity velocity-depth
                 pressure pressure-depth
                 cca cca-depth
                 ccb ccb-depth
                 lfo1 lfo1-depth
                 lfo2 lfo2-depth]
  (:kr
   (* amp
      (qu/amp-modulator-depth velocity velocity-depth)
      (qu/amp-modulator-depth pressure pressure-depth)
      (qu/amp-modulator-depth cca cca-depth)
      (qu/amp-modulator-depth ccb ccb-depth)
      (qu/amp-modulator-depth lfo1 lfo1-depth)
      (qu/amp-modulator-depth lfo2 lfo2-depth)
      (dbamp (qu/keytrack note right-key right-scale left-key left-scale)))))

(defcgen carrier-env [a d1 d2 r bp sus gate]
  (:kr
   (cenv/addsr a d1 d2 r bp sus gate)))

(defcgen modulator-env [a d1 d2 r bp sus bias scale gate]
  (:kr
   (+ bias (* scale 
              (carrier-env a d1 d2 r bp sus gate)))))

(defsynth AlgoVoice [freq 440
                     note 69
                     gate 0
                     amp 0.20
                     velocity 1
                     port-time 0.0      ; pitch
                     env1->pitch 0.00
                     lfo1->pitch 0.00
                     lfo2->pitch 0.00

                     env1-attack 0.0    ; ENV1
                     env1-decay1 0.0
                     env1-decay2 0.0
                     env1-breakpoint 1.0
                     env1-sustain 1.0
                     env1-release 0.0
                     env1-bias 0.0
                     env1-scale 1.0

                     vfreq 7.00         ; dedicated vibrato osc
                     vsens 0.03
                     vdepth 0.00
                     vdelay 0.00

                     lfo1-freq 1.00     ; LFO1
                     lfo1-skew 0.50
                     env1->lfo1-skew 0.00
                     env1->lfo1-amp 0.00
                     pressure->lfo1-amp 0.0
                     cca->lfo1-freq 0
                     cca->lfo1-amp  0
                     ccb->lfo1-freq 0
                     ccb->lfo1-amp  0

                     lfo2-freq 2.50     ; LFO2
                     lfo2-skew 0.50
                     lfo1->lfo2-skew 0.00
                     lfo1->lfo2-amp 0.00
                     pressure->lfo2-amp 0.0
                     cca->lfo2-freq 0
                     cca->lfo2-amp  0
                     ccb->lfo2-freq 0
                     ccb->lfo2-amp  0
                      
                     op1-detune   1.000 ; op1 carrier
                     op1-bias     0.000
                     op1-amp      1.000
                     op1-left-key    60
                     op1-left-scale   0
                     op1-right-key   60
                     op1-right-scale  0
                     op1-attack   0.001
                     op1-decay1   0.100
                     op1-decay2   0.000
                     op1-breakpoint 1.00
                     op1-sustain  1.000
                     op1-release  0.001
                     op1-velocity 0.00 
                     op1-pressure 0.00
                     op1-cca      0.00
                     op1-ccb      0.00
                     op1-lfo1     0.00
                     op1-lfo2     0.00
                     op2-detune    1.000 ; op2 --> op1
                     op2-bias      0.000
                     op2-amp       0.000
                     op2-left-key    60
                     op2-left-scale   0
                     op2-right-key   60
                     op2-right-scale  0
                     op2-attack    0.001
                     op2-decay1    0.100
                     op2-decay2   0.000
                     op2-breakpoint 1.00
                     op2-sustain   1.000
                     op2-release   0.001
                     op2-env-bias  0.001
                     op2-env-scale 1.000
                     op2-velocity  0.00
                     op2-pressure  0.00
                     op2-cca       0.00
                     op2-ccb       0.00
                     op2-lfo1      0.00
                     op2-lfo2      0.00
                     op2-hp        50.0

                     op3-detune    1.000 ; op3 --> op2
                     op3-bias      0.000
                     op3-amp       0.000
                     op3-left-key    60
                     op3-left-scale   0
                     op3-right-key   60
                     op3-right-scale  0
                     op3-attack    0.001
                     op3-decay1    0.100
                     op3-decay2   0.000
                     op3-breakpoint 1.00
                     op3-sustain   1.000
                     op3-release   0.001
                     op3-env-bias  0.001
                     op3-env-scale 1.000
                     op3-velocity  0.00
                     op3-pressure  0.00
                     op3-cca       0.00
                     op3-ccb       0.00
                     op3-lfo1      0.00
                     op3-lfo2      0.00
                     
                     op4-detune   1.000 ; op4 carrier
                     op4-bias     0.000
                     op4-amp      1.000
                     op4-left-key    60
                     op4-left-scale   0
                     op4-right-key   60
                     op4-right-scale  0
                     op4-attack   0.001
                     op4-decay1   0.100
                     op4-decay2   0.000
                     op4-breakpoint 1.00
                     op4-sustain  1.000
                     op4-release  0.001
                     op4-velocity 0.00 
                     op4-pressure 0.00
                     op4-cca      0.00
                     op4-ccb      0.00
                     op4-lfo1     0.00
                     op4-lfo2     0.00

                     op5-detune    1.000 ; op5 --> op4
                     op5-bias      0.000
                     op5-amp       0.000
                     op5-left-key    60
                     op5-left-scale   0
                     op5-right-key   60
                     op5-right-scale  0
                     op5-attack    0.001
                     op5-decay1    0.100
                     op5-decay2   0.000
                     op5-breakpoint 1.00
                     op5-sustain   1.000
                     op5-release   0.001
                     op5-env-bias  0.001
                     op5-env-scale 1.000
                     op5-velocity  0.00
                     op5-pressure  0.00
                     op5-cca       0.00
                     op5-ccb       0.00
                     op5-lfo1      0.00
                     op5-lfo2      0.00

                     op6-detune    1.000 ; op6 --> op4
                     op6-bias      0.000
                     op6-amp       0.000
                     op6-left-key    60
                     op6-left-scale   0
                     op6-right-key   60
                     op6-right-scale  0
                     op6-attack    0.001
                     op6-decay1    0.100
                     op6-decay2   0.000
                     op6-breakpoint 1.00
                     op6-sustain   1.000
                     op6-release   0.001
                     op6-env-bias  0.001
                     op6-env-scale 1.000
                     op6-velocity  0.00
                     op6-pressure  0.00
                     op6-cca       0.00
                     op6-ccb       0.00
                     op6-lfo1      0.00
                     op6-lfo2      0.00
                     op6-hp                 50
                     op6-feedback           0.00
                     op6-env->feedback      0.00
                     op6-lfo1->feedback     0.00
                     op6-pressure->feedback 0.00
                     op6-cca->feedback      0.00
                     op6-ccb->feedback      0.00

                     op7-detune   1.000 ; op7 carrier
                     op7-bias     0.000
                     op7-amp      1.000
                     op7-left-key    60
                     op7-left-scale   0
                     op7-right-key   60
                     op7-right-scale  0
                     op7-attack   0.001
                     op7-decay1   0.100
                     op7-decay2   0.000
                     op7-breakpoint 1.00
                     op7-sustain  1.000
                     op7-release  0.001
                     op7-velocity 0.00 
                     op7-pressure 0.00
                     op7-cca      0.00
                     op7-ccb      0.00
                     op7-lfo1     0.00
                     op7-lfo2     0.00

                     op8-detune    1.000 ; op8 --> op7
                     op8-bias      0.000
                     op8-amp       0.000
                     op8-left-key    60
                     op8-left-scale   0
                     op8-right-key   60
                     op8-right-scale  0
                     op8-attack    0.001
                     op8-decay1    0.100
                     op8-decay2   0.000
                     op8-breakpoint 1.00
                     op8-sustain   1.000
                     op8-release   0.001
                     op8-env-bias  0.001
                     op8-env-scale 1.000
                     op8-velocity  0.00
                     op8-pressure  0.00
                     op8-cca       0.00
                     op8-ccb       0.00
                     op8-lfo1      0.00
                     op8-lfo2      0.00
                     op8-hp                 50
                     op8-feedback           0.00
                     op8-env->feedback      0.00
                     op8-lfo2->feedback     0.00
                     op8-pressure->feedback 0.00
                     op8-cca->feedback      0.00
                     op8-ccb->feedback      0.00

                     bend-bus 0         ; control buses
                     pressure-bus 0
                     vibrato-depth-bus 0
                     cca-bus 0
                     ccb-bus 0
                     out-bus 0]
  (let [bend (in:kr bend-bus)
        pressure (in:kr pressure-bus)
        cca (in:kr cca-bus)
        ccb (in:kr ccb-bus)
        env1 (modulator-env env1-attack env1-decay1 env1-decay2 env1-release
                            env1-breakpoint env1-sustain 
                            env1-bias env1-scale gate)
        vibrato-env (cenv/delay-env vdelay 16 gate)
        vibrato-amp (* vsens (+ (in:kr vibrato-depth-bus)
                                (* vdepth vibrato-env)))
        vibrato (* vibrato-amp (sin-osc:kr vfreq)) ; vibrato signal
        lfo1-skew (qu/clamp (+ lfo1-skew 
                               (* env1->lfo1-skew env1))
                            0.0 1.0)
        lfo1-amp (* (qu/amp-modulator-depth env1 env1->lfo1-amp)
                    (qu/amp-modulator-depth pressure pressure->lfo1-amp)
                    (qu/amp-modulator-depth cca cca->lfo1-amp)
                    (qu/amp-modulator-depth ccb ccb->lfo1-amp))
        lfo1-freq (+ lfo1-freq
                     (* cca->lfo1-freq cca)
                     (* ccb->lfo1-freq ccb))
        lfo1 (* lfo1-amp (var-saw:kr lfo1-freq 0 lfo1-skew))

        lfo2-skew (qu/clamp (+ lfo2-skew
                               (* lfo1->lfo2-skew lfo1))
                            0.0 1.0)
        lfo2-amp (* (qu/amp-modulator-depth lfo1 lfo1->lfo2-amp)
                    (qu/amp-modulator-depth pressure pressure->lfo2-amp)
                    (qu/amp-modulator-depth cca cca->lfo2-amp)
                    (qu/amp-modulator-depth ccb ccb->lfo2-amp))
        lfo2-freq (+ lfo2-freq
                     (* cca->lfo2-freq cca)
                     (* ccb->lfo2-freq ccb))
        lfo2 (* lfo2-amp (var-saw:kr lfo2-freq 0 lfo2-skew))

        fkey (* (lag2 freq port-time) bend)
        f0 (* fkey ; common freq
              (+ 1 vibrato
                 (* env1->pitch env1 env1)
                 (* lfo1->pitch lfo1)
                 (* lfo2->pitch lfo2)))
        op1-freq (+ op1-bias (* op1-detune f0))
        op2-freq (+ op2-bias (* op2-detune f0))
        op3-freq (+ op3-bias (* op3-detune f0))
        op4-freq (+ op4-bias (* op4-detune f0))
        op5-freq (+ op5-bias (* op5-detune f0))
        op6-freq (+ op6-bias (* op6-detune f0))
        op7-freq (+ op7-bias (* op7-detune f0))
        op8-freq (+ op8-bias (* op8-detune f0))
        op1-amp (op-amp op1-amp
                        note
                        op1-left-key op1-left-scale
                        op1-right-key op1-right-scale
                        velocity op1-velocity
                        pressure op1-pressure
                        cca op1-cca
                        ccb op1-ccb
                        lfo1 op1-lfo1
                        lfo2 op1-lfo2)
        op2-amp (op-amp op2-amp
                        note
                        op2-left-key op2-left-scale
                        op2-right-key op2-right-scale
                        velocity op2-velocity
                        pressure op2-pressure
                        cca op2-cca
                        ccb op2-ccb
                        lfo1 op2-lfo1
                        lfo2 op2-lfo2)
        op3-amp (op-amp op3-amp
                        note
                        op3-left-key op3-left-scale
                        op3-right-key op3-right-scale
                        velocity op3-velocity
                        pressure op3-pressure
                        cca op3-cca
                        ccb op3-ccb
                        lfo1 op3-lfo1
                        lfo2 op3-lfo2)
        op4-amp (op-amp op4-amp
                        note
                        op4-left-key op4-left-scale
                        op4-right-key op4-right-scale
                        velocity op4-velocity
                        pressure op4-pressure
                        cca op4-cca
                        ccb op4-ccb
                        lfo1 op4-lfo1
                        lfo2 op4-lfo2)
        op5-amp (op-amp op5-amp
                        note
                        op5-left-key op5-left-scale
                        op5-right-key op5-right-scale
                        velocity op5-velocity
                        pressure op5-pressure
                        cca op5-cca
                        ccb op5-ccb
                        lfo1 op5-lfo1
                        lfo2 op5-lfo2)
        op6-amp (op-amp op6-amp
                        note
                        op6-left-key op6-left-scale
                        op6-right-key op6-right-scale
                        velocity op6-velocity
                        pressure op6-pressure
                        cca op6-cca
                        ccb op6-ccb
                        lfo1 op6-lfo1
                        lfo2 op6-lfo2)
        op7-amp (op-amp op7-amp
                        note
                        op7-left-key op7-left-scale
                        op7-right-key op7-right-scale
                        velocity op7-velocity
                        pressure op7-pressure
                        cca op7-cca
                        ccb op7-ccb
                        lfo1 op7-lfo1
                        lfo2 op7-lfo2)
        op8-amp (op-amp op8-amp
                        note
                        op8-left-key op8-left-scale
                        op8-right-key op8-right-scale
                        velocity op8-velocity
                        pressure op8-pressure
                        cca op8-cca
                        ccb op8-ccb
                        lfo1 op8-lfo1
                        lfo2 op8-lfo2)
        op1-env (carrier-env op1-attack op1-decay1 op1-decay2 op1-release
                             op1-breakpoint op1-sustain gate)

        op2-env (modulator-env op2-attack op2-decay1 op2-decay2 op2-release
                               op2-breakpoint op2-sustain
                               op2-env-bias op2-env-scale gate)
        op3-env (modulator-env op3-attack op3-decay1 op3-decay2 op3-release
                               op3-breakpoint op3-sustain
                               op3-env-bias op3-env-scale gate)
        op4-env (carrier-env op4-attack op4-decay1 op4-decay2 op4-release
                             op4-breakpoint op4-sustain gate)
        op5-env (modulator-env op5-attack op5-decay1 op5-decay2 op5-release
                               op5-breakpoint op5-sustain
                               op5-env-bias op5-env-scale gate)
        op6-env (modulator-env op6-attack op6-decay1 op6-decay2 op6-release
                               op6-breakpoint op6-sustain
                               op6-env-bias op6-env-scale gate)
        op7-env (carrier-env op7-attack op7-decay1 op7-decay2 op7-release
                             op7-breakpoint op7-sustain gate)
        op8-env (modulator-env op8-attack op8-decay1 op8-decay2 op8-release
                               op8-breakpoint op8-sustain
                               op8-env-bias op8-env-scale gate)
        
        ;; [3]-->[2]-->[hp]-->[1]-->
        ;;
        op3 (* op3-amp op3-env op3-env (sin-osc:ar op3-freq))
        op2 (hpf (* op2-amp op2-env op2-env (sin-osc:ar (* op2-freq (+ 1 op3)))) op2-hp)
        op1 (* op1-amp op1-env op1-env (sin-osc:ar (* op1-freq (+ 1 op2))))
        
        ;; fb-->[6]-->[hp]--+-->[4]--+-->[env]--> out
        ;;            [5]---+        +--> fb
        ;;
        op6-fb-scale 0.10 
        op6-fb-depth  (+ op6-feedback
                         (* op6-env->feedback op6-env)
                         (* op6-lfo1->feedback lfo1)
                         (* op6-pressure->feedback pressure)
                         (* op6-cca->feedback cca)
                         (* op6-ccb->feedback ccb))
        op6-fb-sig (* op6-fb-depth op6-fb-scale (local-in:ar 1))
        op6 (hpf (* op6-amp op6-env op6-env 
                    (sin-osc:ar (* op6-freq (+ 1 op6-fb-sig))))
                 op6-hp)
        op5 (* op5-amp op5-env op5-env (sin-osc:ar op5-freq))
        op4-fb-send (sin-osc:ar (* op4-freq (+ 1 op5 op6)))
        op4 (* op4-amp op4-env op4-env op4-fb-send)

        ;; fb-->[8]--[hp]-->[7]-->
        ;;
        fb-8 (+ op8-feedback
                (* op8-env->feedback op8-env)
                (* op8-lfo2->feedback lfo2)
                (* op8-pressure->feedback pressure)
                (* op8-cca->feedback cca)
                (* op8-ccb->feedback ccb))
        op8 (hpf (* op8-amp op8-env op8-env (sin-osc-fb op8-freq fb-8)) op8-hp)
        op7 (* op7-amp op7-env op7-env (sin-osc:ar (* op7-freq (+ 1 op8))))
        mixed-sig (+ op1 op4 op7)]
    (local-out:ar op4-fb-send)
    (out:ar out-bus mixed-sig)))

(defn create-performance [chanobj id keymode main-out
                          cc-vibrato cca ccb  
                          cc-volume cc-echo-mix cc-reverb-mix]
  (let [bank (.clone cadejo.instruments.algo.program/bank)
        performance (cadejo.midi.performance/performance chanobj id keymode 
                                                         bank algo-descriptor
                                                         [:cc1 cc-vibrato    :linear 0.0]
                                                         [:cc7 cc-volume     :linear 1.0]
                                                         [:cca cca           :linear 0.0]
                                                         [:ccb ccb           :linear 0.0]
                                                         [:ccc cc-echo-mix   :linear 0.0]
                                                         [:ccd cc-reverb-mix :linear 0.0])]
    (.put-property! performance :instrument-type :algo)
    (.parent! bank performance)
    (.set-bank! performance bank)
    (let [bend-bus (.control-bus performance :bend)
          pressure-bus (.control-bus performance :pressure)
          vibrato-depth-bus (.control-bus performance cc-vibrato)
          cca-bus (.control-bus performance cca)
          ccb-bus (.control-bus performance ccb)
          cc-volume-bus (.control-bus performance cc-volume)
          cc-echo-mix-bus (.control-bus performance cc-echo-mix)
          cc-reverb-mix-bus (.control-bus performance cc-reverb-mix)
          tone-bus (audio-bus 1)]
      (.pp-hook! bank cadejo.instruments.algo.pp/pp-algo)
      (.add-control-bus! performance :vibrato-depth vibrato-depth-bus)
      (.add-control-bus! performance :cca cca-bus)
      (.add-control-bus! performance :ccb ccb-bus)
      (.add-control-bus! performance :cc-volume cc-volume-bus)
      (.add-control-bus! performance :cc-echo-mix cc-echo-mix-bus)
      (.add-control-bus! performance :cc-reverb-mix cc-reverb-mix-bus)
      (.add-audio-bus! performance :tone tone-bus)
      (.add-audio-bus! performance :main-out main-out)
      performance)))


;; cc1 - vibrato
;; cc7 - volume
;; cca - general controller
;; ccb - general controller
;; ccc - echo mix
;; ccd - reverb mix

(defn algo-mono 
  ([scene chan id & {:keys [cc1 cc7 cca ccb ccc ccd main-out]
                     :or {cc1 1
                          cc7 7
                          cca 16
                          ccb 17
                          ccc 91
                          ccd 92
                          main-out 0}}]
     (let [chanobj (.channel scene chan) 
           keymode (cadejo.midi.mono-mode/mono-keymode :ALGO)
           performance (create-performance chanobj id keymode main-out
                                           cc1 cca ccb 
                                           cc7 ccc ccd)
           voice (AlgoVoice
                  :bend-bus (.control-bus performance :bend)
                  :pressure-bus (.control-bus performance :pressure)
                  :vibrato-depth-bus (.control-bus performance :vibrato-depth)
                  :cca-bus (.control-bus performance :cca)
                  :ccb-bus (.control-bus performance :ccb)
                  :out-bus (.audio-bus performance :tone))
           efx (cadejo.instruments.algo.efx/EfxBlock
                :cc-volume-bus (.control-bus performance :cc-volume)
                :echo-mix-bus (.control-bus performance :cc-echo-mix)
                :reverb-mix-bus (.control-bus performance :cc-reverb-mix)
                :in-bus (.audio-bus performance :tone)
                :out-bus main-out)]
       (.add-synth! performance :efx efx)
       (.add-voice! performance voice)
       (.reset chanobj)
       (Thread/sleep 100)
       performance)))
 
(defn algo-poly
  ([scene chan id 
    & {:keys [cc1 cc7 cca ccb ccc ccd voice-count main-out] 
       :or {cc1 1
            cca 16
            ccb 17
            cc7 7
            ccc 91
            ccd 92
            voice-count 8
            main-out 0}}]
     (let [chanobj (.channel scene chan)
           keymode (cadejo.midi.poly-mode/poly-keymode :ALGO voice-count)
           performance (create-performance chanobj id keymode main-out
                                           cc1 cca ccb 
                                           cc7 ccc ccd)
           voices (let [acc* (atom [])] 
                    (dotimes [i voice-count]
                      (let  [v (AlgoVoice
                                :bend-bus (.control-bus performance :bend)
                                :pressure-bus (.control-bus performance :pressure)
                                :vibrato-depth-bus (.control-bus performance :vibrato-depth)
                                :cca-bus (.control-bus performance :cca)
                                :ccb-bus (.control-bus performance :ccb)
                                :out-bus (.audio-bus performance :tone))]
                        (swap! acc* (fn [n](conj n v)))
                        (Thread/sleep 100)))
                    @acc*)
           efx (cadejo.instruments.algo.efx/EfxBlock
                :cc-volume-bus (.control-bus performance :cc-volume)
                :echo-mix-bus (.control-bus performance :cc-echo-mix)
                :reverb-mix-bus (.control-bus performance :cc-reverb-mix)
                :in-bus (.audio-bus performance :tone)
                :out-bus main-out)]
       (.add-synth! performance :efx efx)
       (doseq [v voices]
         (.add-voice! performance v))
       (.reset chanobj)
       performance)))
    
(.add-constructor! algo-descriptor :mono algo-mono)
(.add-constructor! algo-descriptor :poly algo-poly)
