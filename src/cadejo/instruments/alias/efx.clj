(println "\t--> efx")

(ns cadejo.instruments.alias.efx
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu]))

(def pitch-shift-window 0.2)
(def flanger-max-delay 0.01)
(def echo-max-delay 2.0)

;; 0 --> 10k
;; 1 --> 2k
(defcgen echo-damp-factor [damp]
  (:kr 
   (+ (* -8000 (qu/clamp damp 0 1)) 10000)))

(defsynth EfxBlock [pitchshift-ratio 1.00         ; 0 <= pr <= 4
                    pitchshift-ratio-source 0
                    pitchshift-ratio-depth 0
                    pitchshift-pitch-dispersion 0 ; 0.0 <= pd <= 1.0
                    pitchshift-time-dispersion 0  ; normalized 0.0 <= td <= 1.0
                    pitchshift-mix -99            ; in db
                    flanger-mod-source 0
                    flanger-mod-depth 0
                    flanger-lfo-amp 0.1
                    flanger-lfo-rate 1.0
                    flanger-feedback 0.5
                    flanger-mix -99       
                    flanger-crossmix 0.0  ; 0 <= xmix <= 1
                    echo1-delay  0.25
                    echo1-delay-source 0
                    echo1-delay-depth 0
                    echo1-feedback 0
                    echo1-damp 0.0
                    echo1-pan -0.25
                    echo1-amp-source 0
                    echo1-amp-depth 0
                    echo1-mix -99
                    echo2-delay  0.125
                    echo2-delay-source 0
                    echo2-delay-depth 0
                    echo2-feedback 0.5
                    echo2-damp 0.0
                    echo2-mix -99
                    echo2-amp-source 0
                    echo2-amp-depth 0
                    echo2-pan -0.25
                    dry-mix 0           ; in db
                    amp 0.20
                    dbscale 0
                    cc7->volume 0
                    a-bus 0
                    b-bus 0
                    c-bus 0
                    d-bus 0
                    e-bus 0
                    f-bus 0
                    g-bus 0
                    h-bus 0
                    cc-volume-bus 0
                    in-bus 0                 ; 2 channel
                    out-bus 0]
  (let [a (in:kr a-bus)
        b (in:kr b-bus)
        c (in:kr c-bus)
        d (in:kr d-bus)
        e (in:kr e-bus)
        f (in:kr f-bus)
        g (in:kr g-bus)
        h (in:kr h-bus)
        sources [1 a b c d e f g h 0]
        drysig (in:ar in-bus 2)
        fbsig (local-in:ar 4) ; 0|1 flanger, 2|3 echo
        ;; Pitchshifter
        ps-ratio (qu/clamp (+ pitchshift-ratio
                              (* pitchshift-ratio-depth
                                 (select:kr pitchshift-ratio-source sources)))
                           0 4)
        ps-pdisp (qu/clamp pitchshift-pitch-dispersion 0 1)
        ps-tdisp (* pitch-shift-window (qu/clamp pitchshift-time-dispersion 0 0.99))
        ps-wetsig (pitch-shift drysig pitch-shift-window ps-ratio ps-pdisp ps-tdisp)
        ps-out (* (dbamp pitchshift-mix) ps-wetsig)
        ;; Flanger
        fl-feedback (qu/clamp flanger-feedback -1 +1)
        fl-lfo1 (lf-par:kr flanger-lfo-rate 0.0)
        fl-lfo2 (lf-par:kr flanger-lfo-rate 0.5)
        fl-lfo (* 0.5 flanger-max-delay flanger-lfo-amp [fl-lfo1 fl-lfo2])
        fl-mod1 (* flanger-mod-depth (select:kr flanger-mod-source sources))
        fl-mod2 (lag2:kr fl-mod1 0.01)
        fl-mod (* 0.5 flanger-max-delay [fl-mod1 fl-mod2])
        fl-fixed-delay (* 0.5 flanger-max-delay)
        fl-delay (qu/clamp (+ fl-fixed-delay fl-lfo fl-mod) 0 flanger-max-delay)
        flanger-input [(+ (nth drysig 0)(nth fbsig 0))
                       (+ (nth drysig 1)(nth fbsig 1))]
        fl-wetsig (delay-c flanger-input flanger-max-delay fl-delay)
        fl-xmix (- (qu/clamp flanger-crossmix 0 1) 1)
        fl-wetsig-0 (pan2:ar (nth fl-wetsig 0) fl-xmix)
        fl-wetsig-1 (pan2:ar (nth fl-wetsig 1) (* -1 fl-xmix))
        fl-out (* (dbamp flanger-mix)(+ fl-wetsig-0 fl-wetsig-1))
        ;; Echo1
        ec1-input (+ (nth drysig 0)(nth fbsig 2))
        ec1-delay (qu/clamp (+ echo1-delay
                               (* echo1-delay-depth
                                  (select:kr echo1-delay-source sources)))
                            0 echo-max-delay)
        ec1-wetsig (delay-c ec1-input echo-max-delay ec1-delay)
        ec1-damp (echo-damp-factor echo1-damp)
        ec1-feedback-sig (lpf (* echo1-feedback ec1-wetsig)
                              ec1-damp)
        ec1-amp (* (dbamp echo1-mix) 
                   (qu/amp-modulator-depth (select:kr echo1-amp-source sources)
                                           echo1-amp-depth))
        ec1-out (* ec1-amp (pan2 ec1-wetsig echo1-pan))
        ;; Echo2
        ec2-input (+ (nth drysig 1)(nth fbsig 3))
        ec2-delay (qu/clamp (+ echo2-delay
                               (* echo2-delay-depth
                                  (select:kr echo2-delay-source sources)))
                            0 echo-max-delay)
        ec2-wetsig (delay-c ec2-input echo-max-delay ec2-delay)
        ec2-feedback-sig (lpf (* echo2-feedback ec2-wetsig)
                              (echo-damp-factor echo2-damp))
        ec2-amp (* (dbamp echo2-mix)
                   (qu/amp-modulator-depth (select:kr echo2-amp-source sources)
                                           echo2-amp-depth))
        ec2-out (* ec2-amp (pan2 ec2-wetsig echo2-pan))
        ;; Gain 
        cc7 (qu/amp-modulator-depth (in:kr cc-volume-bus) cc7->volume)
        out-gain (* amp (dbamp dbscale) cc7)
        out-sig (* out-gain 
                   (+ ps-out
                      fl-out
                      ec1-out
                      ec2-out
                      (* (dbamp dry-mix) drysig)))]
    (local-out:ar [(* fl-feedback (nth fl-wetsig 0))
                   (* fl-feedback (nth fl-wetsig 1))
                   (* echo1-feedback ec1-feedback-sig)
                   (* echo2-feedback ec2-feedback-sig)])
    (out:ar out-bus out-sig)))
