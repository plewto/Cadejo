(println "-->    alias controllers")

;; Provides several synth control elements:
;; Envelopes, addsr2 style
;;   env1  
;;   env2
;;   env3 (sans inversion)
;; LFOS
;;   lfo1
;;   lfo2
;;   lfo3
;; Steppers
;;   stepper1 
;;   stepper2 
;; Dividers
;;   divider1 <--lfo3 odd divisions only
;;   divider2 <--lfo3 even divisions only
;;   divider sum of divider1 and divider2
;; Noise source
;; Sample & Hold
;; freq - note frequency
;; fperiod - 1/freq
;; keynum - MIDI key-number
;; pressure - MIDI channel pressure
;; velocity 
;; cca - MIDI continuous controller 'a'
;; ccb - MIDI continuous controller 'b'
;; ccc - MIDI continuous controller 'c'
;; ccd - MIDI continuous controller 'd'
;; bus a   General buses a..h have two inputs each.
;; bus b   Each input has a source selection and a depth.
;; bus c   Inputs may include other general buses.
;; bus d   The bus value is the product of the two  
;; bus e   input buses.
;; bus f
;; bus g
;; bus h
;; constant 1
;; constant 0
;; gate


(ns cadejo.instruments.alias.control
  (:use [overtone.core])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.modules.env :as cenv])
  (:require [cadejo.modules.qugen :as qu]))


;; Matrix Sources
;;
(def s-one  (:con constants/control-bus-map))
(def s-zero (:off constants/control-bus-map))
(def s-lfo1 (:lfo1 constants/control-bus-map))
(def s-lfo2 (:lfo2 constants/control-bus-map))
(def s-noise (:lfnse constants/control-bus-map))

;; Local bus numbers
;;
(def lb-env1         0)
(def lb-env2         1)
(def lb-env3         2)
(def lb-lfo1         3)
(def lb-lfo2         4)
(def lb-lfo3         5)
(def lb-stepper1     6)
(def lb-stepper2     7)
(def lb-divider1     8)
(def lb-divider2     9)
(def lb-divider     10)
(def lb-noise       11)
(def lb-sample-hold 12)
(def lb-a           13)
(def lb-b           14)
(def lb-c           15)
(def lb-d           16)
(def lb-e           17)
(def lb-f           18)
(def lb-g           19)
(def lb-h           20)

(defcgen sample-hold [src rate]
  (:kr
   (gate:kr src (lf-pulse:kr rate 0 0.01))))

(defsynth ControlBlock [gate 0
                        freq 440
                        note 69
                        velocity 1.0
                        env1-attack     0.001          ; env1
                        env1-decay1     0.001          ; normalized to osc wave
                        env1-decay2     0.001
                        env1-release    0.001
                        env1-peak       1.000
                        env1-breakpoint 1.000
                        env1-sustain    1.000
                        env1-invert     0
                        env2-attack     0.001          ; env2
                        env2-decay1     0.001          ; normalized to filter freq
                        env2-decay2     0.001
                        env2-release    0.001
                        env2-peak       1.000
                        env2-breakpoint 1.000
                        env2-sustain    1.000
                        env2-invert     0
                        env3-attack     0.001          ; env3 (no inversion)
                        env3-decay1     0.001          ; normalized to overall amplitude
                        env3-decay2     0.001
                        env3-release    0.001
                        env3-peak       1.000
                        env3-breakpoint 1.000
                        env3-sustain    1.000
                        lfo1-freq1-source s-one        ; lfo1
                        lfo1-freq1-depth 7
                        lfo1-freq2-source s-one
                        lfo1-freq2-depth 0
                        lfo1-wave1-source s-one
                        lfo1-wave1-depth 0.5
                        lfo1-wave2-source s-one
                        lfo1-wave2-depth 0
                        lfo2-freq1-source s-one        ; lfo2
                        lfo2-freq1-depth 7
                        lfo2-freq2-source s-one
                        lfo2-freq2-depth 0
                        lfo2-wave1-source s-one
                        lfo2-wave1-depth 0.5
                        lfo2-wave2-source s-one
                        lfo2-wave2-depth 0
                        lfo3-freq1-source s-one        ; lfo3
                        lfo3-freq1-depth 7
                        lfo3-freq2-source s-one
                        lfo3-freq2-depth 0
                        lfo3-wave1-source s-one
                        lfo3-wave1-depth 0.5
                        lfo3-wave2-source s-one
                        lfo3-wave2-depth 0
                        stepper1-trigger s-lfo1        ; stepper1
                        stepper1-reset s-zero
                        stepper1-min -10               ; must be int ?
                        stepper1-max 10
                        stepper1-step 1
                        stepper1-reset-value -10
                        stepper1-bias 0
                        stepper1-scale 1/10
                        stepper2-trigger s-lfo2        ; stepper2
                        stepper2-reset s-zero
                        stepper2-min -10               ; must be int ?
                        stepper2-max 10
                        stepper2-step 1
                        stepper2-reset-value -10
                        stepper2-bias 0
                        stepper2-scale 1/10
                        divider1-pw 0.5                ; divider1 <-lfo3 (odd counts only)
                        divider1-p1 1.000
                        divider1-p3 0.333
                        divider1-p5 0.200
                        divider1-p7 0.142
                        divider1-bias 0.000
                        divider1-scale-source s-one
                        divider1-scale-depth 1.0
                        divider2-pw 0.5                 ; divider2 <-lfo3 (even counts only)
                        divider2-p2 0.500
                        divider2-p4 0.250
                        divider2-p6 0.167
                        divider2-p8 0.125
                        divider2-bias 0
                        divider2-scale-source s-one
                        divider2-scale-depth 1.0
                        lfnoise-freq-source s-one      ; noise
                        lfnoise-freq-depth 1.0
                        sh-source s-noise              ; sample & hold
                        sh-rate 7
                        sh-bias 0
                        sh-scale 1
                        a-source1 s-one                ; bus a
                        a-depth1 0
                        a-source2 s-one
                        a-depth2 1
                        b-source1 s-one                ; bus b
                        b-depth1 0
                        b-source2 s-one
                        b-depth2 1
                        c-source1 s-one                ; bus c
                        c-depth1 0
                        c-source2 s-one
                        c-depth2 1
                        d-source1 s-one                ; bus d
                        d-depth1 0
                        d-source2 s-one
                        d-depth2 1
                        e-source1 s-one                ; bus e
                        e-depth1 0
                        e-source2 s-one
                        e-depth2 1
                        f-source1 s-one                ; bus f
                        f-depth1 0
                        f-source2 s-one
                        f-depth2 1
                        g-source1 s-one                ; bus g
                        g-depth1 0
                        g-source2 s-one
                        g-depth2 1
                        h-source1 s-one                ; bus h
                        h-depth1 0
                        h-source2 s-one
                        h-depth2 1
                        pressure-bus 0                 ; source buses
                        cca-bus 0
                        ccb-bus 0
                        ccc-bus 0
                        ccd-bus 0
                        env1-bus 0                     ; output buses
                        env2-bus 0
                        env3-bus 0
                        lfo1-bus 0
                        lfo2-bus 0
                        lfo3-bus 0
                        a-bus 0
                        b-bus 0
                        c-bus 0
                        d-bus 0
                        e-bus 0
                        f-bus 0
                        g-bus 0
                        h-bus 0]
  (let [pressure (in:kr pressure-bus)
        cca (in:kr cca-bus)
        ccb (in:kr ccb-bus)
        ccc (in:kr ccc-bus)
        ccd (in:kr ccd-bus)
        lcbus (local-in:kr 21)
        sources [1 
                 (nth lcbus lb-env1)(nth lcbus lb-env2)(nth lcbus lb-env3)
                 (nth lcbus lb-lfo1)(nth lcbus lb-lfo2)(nth lcbus lb-lfo3)
                 (nth lcbus lb-stepper1)(nth lcbus lb-stepper2)
                 (nth lcbus lb-divider1)(nth lcbus lb-divider2)(nth lcbus lb-divider)
                 (nth lcbus lb-noise)(nth lcbus lb-sample-hold)
                 freq (/ 1.0 freq) note pressure velocity 
                 cca ccb ccc ccd 
                 (nth lcbus lb-a)(nth lcbus lb-b)(nth lcbus lb-c)(nth lcbus lb-d)
                 (nth lcbus lb-e)(nth lcbus lb-f)(nth lcbus lb-g)(nth lcbus lb-h) 
                 gate 0]
        env1-lin (cenv/addsr2 env1-attack env1-decay1 env1-decay2 env1-release
                              env1-peak env1-breakpoint env1-sustain env1-invert gate)
        env1 (* env1-lin env1-lin)

        env2-lin (cenv/addsr2 env2-attack env2-decay1 env2-decay2 env2-release
                              env2-peak env2-breakpoint env2-sustain env2-invert gate)
        env2 (* env2-lin env2-lin)

        env3-lin (cenv/addsr2 env3-attack env3-decay1 env3-decay2 env3-release
                              env3-peak env3-breakpoint env3-sustain 0 gate)
        env3 (* env3-lin env3-lin)
        ;; LFO1
        lfo1-freq (abs (+ (* lfo1-freq1-depth (select:kr lfo1-freq1-source sources))
                          (* lfo1-freq2-depth (select:kr lfo1-freq2-source sources))))
        lfo1-wave (abs (+ (* lfo1-wave1-depth (select:kr lfo1-wave1-source sources))
                          (* lfo1-wave2-depth (select:kr lfo1-wave2-source sources))))
        lfo1 (var-saw:kr lfo1-freq 0 lfo1-wave)
        ;; LFO2
        lfo2-freq (abs (+ (* lfo2-freq1-depth (select:kr lfo2-freq1-source sources))
                          (* lfo2-freq2-depth (select:kr lfo2-freq2-source sources))))
        lfo2-wave (abs (+ (* lfo2-wave1-depth (select:kr lfo2-wave1-source sources))
                          (* lfo2-wave2-depth (select:kr lfo2-wave2-source sources))))
        lfo2 (var-saw:kr lfo2-freq 0 lfo2-wave)
        ;; LFO3
        lfo3-freq (abs (+ (* lfo3-freq1-depth (select:kr lfo3-freq1-source sources))
                          (* lfo3-freq2-depth (select:kr lfo3-freq2-source sources))))
        lfo3-wave (abs (+ (* lfo3-wave1-depth (select:kr lfo3-wave1-source sources))
                          (* lfo3-wave2-depth (select:kr lfo3-wave2-source sources))))
        lfo3 (var-saw:kr lfo3-freq 0 lfo3-wave)
        ;; STEPPER1
        stp1-trig (select:kr stepper1-trigger sources)
        stp1-reset (select:kr stepper1-reset sources)
        stepper1 (+ stepper1-bias
                    (* stepper1-scale 
                       (stepper stp1-trig stp1-reset
                                stepper1-min stepper1-max 
                                stepper1-step stepper1-reset-value)))
        ;; STEPPER2
        stp1-trig (select:kr stepper2-trigger sources)
        stp1-reset (select:kr stepper2-reset sources)
        stepper2 (+ stepper2-bias
                    (* stepper2-scale 
                       (stepper stp1-trig stp1-reset
                                stepper2-min stepper2-max 
                                stepper2-step stepper2-reset-value)))
        ;; DIVIDER1 <-- lfo3
        lfo3-period (/ 1.0 (max lfo3-freq 0.001))
        div1-pw (* divider1-pw lfo3-period)
        div1-p1 (* divider1-p1 (trig1 lfo3 div1-pw))
        div1-p3 (* divider1-p3 (trig1 (pulse-divider lfo3 3)(* 3 div1-pw)))
        div1-p5 (* divider1-p5 (trig1 (pulse-divider lfo3 5)(* 5 div1-pw)))
        div1-p7 (* divider1-p7 (trig1 (pulse-divider lfo3 7)(* 7 div1-pw)))
        div1-scale (* divider1-scale-depth 
                      (select:kr divider1-scale-source sources))
        divider1 (+ divider1-bias (* div1-scale (+ div1-p1 div1-p3 
                                                   div1-p5 div1-p7)))
        ;; DIVIDER2 <-- lfo3
        div2-pw (* divider2-pw lfo3-period)
        div2-p2 (* divider2-p2 (trig1 (pulse-divider lfo3 2)(* 2 div2-pw)))
        div2-p4 (* divider2-p4 (trig1 (pulse-divider lfo3 4)(* 4 div2-pw)))
        div2-p6 (* divider2-p6 (trig1 (pulse-divider lfo3 6)(* 6 div2-pw)))
        div2-p8 (* divider2-p8 (trig1 (pulse-divider lfo3 8)(* 8 div2-pw)))
        div2-scale (* divider2-scale-depth
                      (select:kr divider2-scale-source sources))
        divider2 (+ divider2-bias (* div2-scale (+ div2-p2 div2-p4
                                                   div2-p6 div2-p8)))
        divider (+ divider1 divider2)
        ;; LFNOISE
        lfnoise-freq (max 0.1 (abs (* lfnoise-freq-depth (select:kr lfnoise-freq-source sources))))
        lfnoise (lf-noise2:kr lfnoise-freq)
        ;; SAMPLE & HOLD
        sh-source (select:kr sh-source sources)
        sh (+ sh-bias (* sh-scale (sample-hold sh-source sh-rate)))
        ;; General buses a...f
        a (* (* a-depth1 (select:kr a-source1 sources))
             (* a-depth2 (select:kr a-source2 sources)))
        b (* (* b-depth1 (select:kr b-source1 sources))
             (* b-depth2 (select:kr b-source2 sources)))
        c (* (* c-depth1 (select:kr c-source1 sources))
             (* c-depth2 (select:kr c-source2 sources)))
        d (* (* d-depth1 (select:kr d-source1 sources))
             (* d-depth2 (select:kr d-source2 sources)))
        e (* (* e-depth1 (select:kr e-source1 sources))
             (* e-depth2 (select:kr e-source2 sources)))
        f (* (* f-depth1 (select:kr f-source1 sources))
             (* f-depth2 (select:kr f-source2 sources)))
        g (* (* g-depth1 (select:kr g-source1 sources))
             (* g-depth2 (select:kr g-source2 sources)))
        h (* (* h-depth1 (select:kr h-source1 sources))
             (* h-depth2 (select:kr h-source2 sources)))]
    (local-out:kr [env1 env2 env3 lfo1 lfo2 lfo3
                   stepper1 stepper2 divider1 divider2 divider lfnoise sh
                   a b c d e f g h])
    (out:kr env1-bus env1)
    (out:kr env2-bus env2)
    (out:kr env3-bus env3)
    (out:kr lfo1-bus lfo1)
    (out:kr lfo2-bus lfo2)
    (out:kr lfo3-bus lfo3)
    (out:kr a-bus a)
    (out:kr b-bus b)
    (out:kr c-bus c)
    (out:kr d-bus d)
    (out:kr e-bus e)
    (out:kr f-bus f)
    (out:kr g-bus g)
    (out:kr h-bus h)))
