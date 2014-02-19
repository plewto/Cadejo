(println "\t--> ALGO efx")

(ns cadejo.instruments.algo.efx
  (:require [cadejo.modules.qugen :as qu])
  (:use [overtone.core]))

(def echo-max-delay 1)

(defsynth EfxBlock [lp 10000
                    echo-delay-1 0.25
                    echo-delay-2 0.50
                    echo-fb 0.5
                    echo-hf-damp 0
                    echo-mix 0.0
                    room-size 0.5
                    reverb-mix 0.5
                    amp 0.20
                    dbscale 0
                    cc-volume-depth 0.00 ;; how much effect ctrl 7 has.
                    cc-volume-bus 0
                    echo-mix-bus 0
                    reverb-mix-bus 0
                    in-bus 0
                    out-bus 0]
  (let [delay-fb-cutoff (+ (* -8000 echo-hf-damp) 10000)
        drysig (lpf (in:ar in-bus 1) lp)
        fbsig (+ (local-in:ar 2) drysig)
        wetsig (lpf:ar (delay-c fbsig echo-max-delay 
                                [echo-delay-1 echo-delay-2]) 
                       delay-fb-cutoff)
        wetsig1 (pan2 (nth wetsig 0) 0.5)
        wetsig2 (pan2 (nth wetsig 1) -0.5)
        echo-mix (qu/clamp (+ echo-mix
                              (in:kr echo-mix-bus))
                           0 1)
        reverb-sig (free-verb 
                    (qu/efx-mixer drysig (+ wetsig1 wetsig2) echo-mix)
                    (* 0.5 (+ reverb-mix (in:kr reverb-mix-bus)))
                    room-size 0.5)
        gain (* 
              (dbamp dbscale)
              amp (qu/amp-modulator-depth 
                   (in:kr cc-volume-bus) 
                   cc-volume-depth))]
    (local-out:ar (* echo-fb wetsig))
    (out:ar out-bus (* gain reverb-sig))))

