(ns cadejo.efx.flanger
  (:require [cadejo.modules.qugen :as qu])
  (:use [overtone.core]))


(def flanger-max-delay 0.01)


;; Mono input
;; Stereo output
(defsynth DualFlanger [in-bus 0
                       out-bus 0
                       dflanger-delay (/ flanger-max-delay 2)
                       dflanger-depth 0.1
                       dflanger-skew 0.5
                       dflanger-rate-1 0.25
                       dflanger-rate-2 0.50
                       dflanger-fb 0.5
                       dflanger-mix 0.5]
  (let [drysig (in:ar in-bus)
        crossmix 0.5
        lfo-1 (* dflanger-depth (var-saw:kr dflanger-rate-1 0 dflanger-skew))
        lfo-2 (* -1 dflanger-depth (var-saw:kr dflanger-rate-2 0 dflanger-skew))
        delay-1 (qu/clamp (* dflanger-delay (+ 1 lfo-1)) 0 flanger-max-delay)
        delay-2 (qu/clamp (* dflanger-delay (+ 1 lfo-2)) 0 flanger-max-delay)
        drysig (in:ar in-bus)
        fbsig (+ (local-in:ar 2)[drysig drysig])
        wetsig (delay-c fbsig flanger-max-delay [delay-1 delay-2])
        wetsig1 (pan2 (nth wetsig 0) crossmix)
        wetsig2 (pan2 (nth wetsig 1) (* -1 crossmix))]
    (local-out:ar (* (qu/clamp dflanger-fb -1 +1) wetsig))
    (out:ar out-bus (qu/efx-mixer [drysig drysig](+ wetsig1 wetsig2) dflanger-mix))))


