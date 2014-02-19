(ns cadejo.efx.echo
  (:require [cadejo.modules.qugen :as qu])
  (:use [overtone.core]))


(def echo-max-delay 2.0)

(defsynth DualEcho [in-bus 10
                    out-bus 0
                    decho-delay-1 0.25
                    decho-delay-2 0.50
                    decho-fb 0.5
                    decho-hf-damp 0
                    decho-mix 0.5
                    decho-reverb-room-size 0.5
                    decho-reverb-mix 0.5]
  (let [cutoff (+ (* -8000 decho-hf-damp) 10000)
        drysig (in:ar in-bus 2)
        fbsig (+ (local-in:ar 2) drysig)
        wetsig (lpf:ar (delay-c fbsig echo-max-delay 
                                [decho-delay-1 decho-delay-2]) 
                       cutoff)
        wetsig1 (pan2 (nth wetsig 0) 0.5)
        wetsig2 (pan2 (nth wetsig 1) -0.5)
        reverb-sig (free-verb 
                    (qu/efx-mixer drysig (+ wetsig1 wetsig2) decho-mix)
                              decho-reverb-mix 
                              decho-reverb-room-size 0.5)]
    (local-out:ar (* decho-fb wetsig))
    (out:ar out-bus reverb-sig)))
