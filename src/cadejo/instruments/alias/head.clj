(println "\t--> head")

(ns cadejo.instruments.alias.head
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu]))

(def ^:private one  0)

(defcgen fm-select [array src1 depth1 lag1 src2 depth2 lag2]
  (:kr
   (+ (lag2:kr (* depth1 (select:kr src1 array)) lag1)
      (lag2:kr (* depth2 (select:kr src2 array)) lag2))))

;; pre-osc frequency signal processing
;; f0 - reference frequency
;; detune - osc detune ratio
;; bias - osc linear freq shift
;; fm - fm signal - amplitude of fm scaled by other arguments 
;;      
(defcgen fmproc [f0 detune bias fm]
  (:kr 
   (abs (* (+ 1 fm)
           (+ bias (* detune f0)))))
  (:ar
   (abs (* (+ 1 fm)
           (+ bias (* detune f0)))))
  (:default :kr))

(defcgen amproc [array db  src1 depth1 lag1 src2 depth2 lag2]
  (:kr (* (dbamp db)
          (lag2:kr
           (qu/amp-modulator-depth (select:kr src1 array) depth1)
           lag1)
          (lag2:kr
           (qu/amp-modulator-depth (select:kr src2 array) depth2)
           lag2))))

(defsynth AliasHead [freq 440
                     port-time 0.00
                     osc1-detune 1.00  ;; osc1 sync-saw
                     osc1-bias 0.0
                     osc1-fm1-source one
                     osc1-fm1-depth 0
                     osc1-fm1-lag 0
                     osc1-fm2-source one
                     osc1-fm2-depth 0
                     osc1-fm2-lag 0
                     osc1-wave 0.00
                     osc1-wave1-source one
                     osc1-wave1-depth 0
                     osc1-wave2-source one
                     osc1-wave2-depth 0
                     osc1-amp 0         ; db
                     osc1-amp1-src one
                     osc1-amp1-depth 0
                     osc1-amp1-lag 0
                     osc1-amp2-src one
                     osc1-amp2-depth 0
                     osc1-amp2-lag 0
                     osc1-pan -1.0
                     osc2-detune 1.00 ;; osc2 pulse
                     osc2-bias 0.0
                     osc2-fm1-source one
                     osc2-fm1-depth 0
                     osc2-fm1-lag 0
                     osc2-fm2-source one
                     osc2-fm2-depth 0
                     osc2-fm2-lag 0
                     osc2-wave 0.50
                     osc2-wave1-source one
                     osc2-wave1-depth 0
                     osc2-wave2-source one
                     osc2-wave2-depth 0
                     osc2-amp 0         ; db
                     osc2-amp1-src one
                     osc2-amp1-depth 0
                     osc2-amp1-lag 0
                     osc2-amp2-src one
                     osc2-amp2-depth 0
                     osc2-amp2-lag 0
                     osc2-pan -1.0
                     osc3-detune 1.00 ;; osc3 pulse
                     osc3-bias 0.0
                     osc3-fm1-source one
                     osc3-fm1-depth 0
                     osc3-fm1-lag 0
                     osc3-fm2-source one
                     osc3-fm2-depth 0
                     osc3-fm2-lag 0
                     osc3-wave 0.00
                     osc3-wave1-source one
                     osc3-wave1-depth 0
                     osc3-wave2-source one
                     osc3-wave2-depth 0
                     osc3-amp 0         ; db
                     osc3-amp1-src one
                     osc3-amp1-depth 0
                     osc3-amp1-lag 0
                     osc3-amp2-src one
                     osc3-amp2-depth 0
                     osc3-amp2-lag 0
                     osc3-pan -1.0
                     noise-param 0.50 ;; noise
                     noise-lp 10000
                     noise-hp 10
                     noise-amp 0         ; db
                     noise-amp1-src one
                     noise-amp1-depth 0
                     noise-amp1-lag 0
                     noise-amp2-src one
                     noise-amp2-depth 0
                     noise-amp2-lag 0
                     noise-pan +1.0
                     ringmod-carrier -1.0    ; ringmod -1.0 = osc1 +1 = osc2
                     ringmod-modulator -1.0  ; -1.0 = osc3 +1 = noise
                     ringmod-amp 0           ; db
                     ringmod-amp1-src one
                     ringmod-amp1-depth 0
                     ringmod-amp1-lag 0
                     ringmod-amp2-src one
                     ringmod-amp2-depth 0
                     ringmod-amp2-lag 0
                     ringmod-pan +1.0
                     mute-amp 0         ; mutes output bus until instrument ready
                     bend-bus 0         ; buses
                     a-bus 0
                     b-bus 0
                     c-bus 0
                     d-bus 0
                     e-bus 0
                     f-bus 0
                     g-bus 0
                     h-bus 0
                     out-bus 0]         ; 2-channel bus
  (let [bend (in:kr bend-bus)
        a (in:kr a-bus)
        b (in:kr b-bus)
        c (in:kr c-bus)
        d (in:kr d-bus)
        e (in:kr e-bus)
        f (in:kr f-bus)
        g (in:kr g-bus)
        h (in:kr h-bus)
        sources [1 a b c d e f g h 0]
        f0 (* (lag2:kr freq port-time)
              bend)
        ;; OSC1 sync-saw
        freq-1 (fmproc f0 osc1-detune osc1-bias
                   (fm-select sources 
                              osc1-fm1-source osc1-fm1-depth osc1-fm1-lag
                              osc1-fm2-source osc1-fm2-depth osc1-fm2-lag))
        wave-1 (* freq-1 
                  (max 1  
                       (abs 
                        (+ osc1-wave 
                           (* osc1-wave1-depth
                              (select:kr osc1-wave1-source sources))
                           (* osc1-wave2-depth 
                              (select:kr osc1-wave2-source sources))))))
        amp-1 (amproc sources osc1-amp
                      osc1-amp1-src osc1-amp1-depth osc1-amp1-lag
                      osc1-amp2-src osc1-amp2-depth osc1-amp2-lag)
        osc1 (sync-saw:ar freq-1 wave-1)
        ;; OSC2 pulse
        freq-2 (fmproc f0 osc2-detune osc2-bias
                       (fm-select sources 
                                  osc2-fm1-source osc2-fm1-depth osc2-fm1-lag
                                  osc2-fm2-source osc2-fm2-depth osc2-fm2-lag))
        wave-2 (qu/clamp 
                (+ osc2-wave
                   (* osc2-wave1-depth
                      (select:kr osc2-wave1-source sources))
                   (* osc2-wave2-depth
                      (select:kr osc2-wave2-source sources)))
                0.05 0.95)
        amp-2 (amproc sources osc2-amp
                      osc2-amp1-src osc2-amp1-depth osc2-amp1-lag
                      osc2-amp2-src osc2-amp2-depth osc2-amp2-lag)
        osc2 (pulse freq-2 wave-2)
        ;; OSC3 fm-feedback
        freq-3 (fmproc f0 osc3-detune osc3-bias
                       (fm-select sources 
                                  osc3-fm1-source osc3-fm1-depth osc3-fm1-lag
                                  osc3-fm2-source osc3-fm2-depth osc3-fm2-lag))
        wave-3 (abs (+ osc3-wave
                       (* osc3-wave1-depth
                          (select:kr osc3-wave1-source sources))
                       (* osc3-wave2-depth
                          (select:kr osc3-wave2-source sources))))
        amp-3 (amproc sources osc3-amp
                      osc3-amp1-src osc3-amp1-depth osc3-amp1-lag
                      osc3-amp2-src osc3-amp2-depth osc3-amp2-lag)
        osc3 (sin-osc-fb freq-3 wave-3)
        ;; NOISE 
        noise-parameter (qu/clamp (+ 1.3 (* 0.7 noise-param)) 1 2)
        noise-gain (dbamp 12)
        amp-noise (* noise-gain
                     (amproc sources noise-amp
                             noise-amp1-src noise-amp1-depth noise-amp1-lag
                             noise-amp2-src noise-amp2-depth noise-amp2-lag))
        noise (lpf (hpf (crackle:ar noise-parameter)
                        noise-hp)
                   noise-lp)
        ;; RINGMODULATOR
        rm-carrier (x-fade2:ar osc1 osc2 ringmod-carrier)
        rm-modulator (x-fade2:ar osc3 (* (dbamp 15) noise) ringmod-modulator)
        amp-rm (amproc sources ringmod-amp
                     ringmod-amp1-src ringmod-amp1-depth ringmod-amp1-lag
                     ringmod-amp2-src ringmod-amp2-depth ringmod-amp2-lag)
        ringmodulator (* rm-carrier rm-modulator)
        ;; MIXER
        mixer (+ (pan2:ar (* amp-1 osc1) osc1-pan)
                 (pan2:ar (* amp-2 osc2) osc2-pan)
                 (pan2:ar (* amp-3 osc3) osc3-pan)
                 (pan2:ar (* amp-noise noise) noise-pan)
                 (pan2:ar (* amp-rm ringmodulator) ringmod-pan))]
    (out:ar out-bus (* mute-amp mixer))))
