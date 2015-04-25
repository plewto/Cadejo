(println "-->    cobalt efx")
(ns cadejo.instruments.cobalt.efx
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu])
  (:require [cadejo.modules.env :as cenv])
  (:require [cadejo.instruments.cobalt.constants :as con])
  )

;; dual delay lines
;;    lfo3 --> time, amp, pan 
;;    xenv --> time, amp, pan
;; panner
;; 3 fixed formant filters 


(defsynth CobaltEffects [gate 0
                         velocity 1
                         dbscale 0
                         amp -18        ; overall amplitude (db)
                         amp<-velocity 0.00
                         amp<-cc7 0.00
                         xenv-attack 0.00
                         xenv-decay1 0.00
                         xenv-decay2 0.00
                         xenv-release 0.00
                         xenv-peak 1.00
                         xenv-breakpoint 1.00
                         xenv-sustain 1.00

                         lfo3-freq 1.00
                         lfo3-amp<-cca 0
                         lfo3-amp<-ccb 0
                         lfo3-amp<-xenv 0
                         lfo4-freq 0.50
                         lfo4-amp<-cca 0
                         lfo4-amp<-ccb 0
                         lfo4-amp<-xenv 0

                         dry-amp 0
                         dry-pan 0

                         delay1-time 0.500
                         delay1-time<-lfo3 0
                         delay1-time<-lfo4 0
                         delay1-time<-xenv 0
                         delay1-fb 0.5
                         delay1-xfb 0
                         delay1-amp con/min-db ; db
                         delay1-amp<-lfo3 0
                         delay1-amp<-lfo4 0
                         delay1-amp<-xenv 0
                         delay1-pan -0.7 ; +/- 1
                         delay1-pan<-lfo3 0
                         delay1-pan<-lfo4 0
                         delay1-pan<-xenv 0

                         delay2-time 0.500
                         delay2-time<-lfo3 0
                         delay2-time<-lfo4 0
                         delay2-time<-xenv 0
                         delay2-fb 0.5
                         delay2-xfb 0
                         delay2-amp con/min-db ; db
                         delay2-amp<-lfo3 0
                         delay2-amp<-lfo4 0
                         delay2-amp<-xenv 0
                         delay2-pan -0.7 ; +/- 1
                         delay2-pan<-lfo3 0
                         delay2-pan<-lfo4 0
                         delay2-pan<-xenv 0
                         cca-bus 0
                         ccb-bus 0
                         cc7-bus 0
                         in-bus 0
                         out-bus 0]
  (let [cca (in:kr cca-bus)
        ccb (in:kr ccb-bus)
        cc7 (in:kr cc7-bus)
        env (cenv/addsr2 xenv-attack xenv-decay1 xenv-decay2 xenv-release
                         xenv-peak xenv-breakpoint xenv-sustain 0 gate)
        lfo3 (* (sin-osc:kr lfo3-freq)
                (qu/amp-modulator-depth cca lfo3-amp<-cca)
                (qu/amp-modulator-depth ccb lfo3-amp<-ccb)
                (qu/amp-modulator-depth env lfo3-amp<-xenv))
        lfo4 (* (sin-osc:kr lfo4-freq)
                (qu/amp-modulator-depth cca lfo4-amp<-cca)
                (qu/amp-modulator-depth ccb lfo4-amp<-ccb)
                (qu/amp-modulator-depth env lfo4-amp<-xenv))
        drysig (in:ar in-bus)
        [fb1 fb2](local-in:ar 2)
        delay1 (let [time (qu/clamp (+ delay1-time
                                       (* 1/100 (+ (* lfo3 delay1-time<-lfo3)
                                                    (* lfo4 delay1-time<-lfo4)
                                                    (* env delay1-time<-xenv))))
                                    0 con/max-delay-time)
                     insig (+ drysig (* fb1 delay1-fb)(* fb2 delay1-xfb))]
                 (delay-c insig con/max-delay-time time))
       
        delay1-amp (* (dbamp delay1-amp)
                      (qu/amp-modulator-depth lfo3 delay1-amp<-lfo3)
                      (qu/amp-modulator-depth lfo4 delay1-amp<-lfo4)
                      (qu/amp-modulator-depth env delay1-amp<-xenv))
        delay1-pos (qu/clamp (+ delay1-pan
                                (* lfo3 delay1-pan<-lfo3)
                                (* lfo4 delay1-pan<-lfo4)
                                (* env delay1-pan<-xenv))
                             -1 1)
        delay2 (let [time (qu/clamp (+ delay2-time
                                       (* 1/100 (+ (* lfo3 delay2-time<-lfo3)
                                                    (* lfo4 delay2-time<-lfo4)
                                                    (* env delay2-time<-xenv))))
                                    0 con/max-delay-time)
                     insig (+ drysig (* fb1 delay2-fb)(* fb2 delay2-xfb))]
                 (delay-c insig con/max-delay-time time))
        delay2-amp (* (dbamp delay2-amp)
                      (qu/amp-modulator-depth lfo3 delay2-amp<-lfo3)
                      (qu/amp-modulator-depth lfo4 delay2-amp<-lfo4)
                      (qu/amp-modulator-depth env delay2-amp<-xenv))
        delay2-pos (qu/clamp (+ delay2-pan
                                (* lfo3 delay2-pan<-lfo3)
                                (* lfo4 delay2-pan<-lfo4)
                                (* env delay2-pan<-xenv))
                             -1 1)
        master-amp (* (dbamp (+ dbscale amp))
                      (qu/amp-modulator-depth:ir velocity amp<-velocity)
                      (qu/amp-modulator-depth:kr cc7 amp<-cc7))]
    (local-out:ar [delay1 delay2])
    (out:ar out-bus (* master-amp (+ (pan2 (* (dbamp dry-amp) drysig) dry-pan)
                                     (pan2 (* delay1-amp delay1) delay1-pos)
                                     (pan2 (* delay2-amp delay2) delay2-pos))))))
        
        
