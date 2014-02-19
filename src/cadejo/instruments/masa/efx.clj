(println "\t--> MASA efx")

(ns cadejo.instruments.masa.efx
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu]))

(def scanner-max-delay 0.01)

(defcgen DelayTime [common-delay freq depth spread]
  (:kr
   (qu/clamp (* common-delay (+ 1 (* depth (lf-tri:kr (* freq (+ 1 spread)) 0))))
             0 scanner-max-delay)))


(defsynth EfxBlock [in-bus 0
                    out-bus 0
                    scanner-mix-bus 0
                    reverb-mix-bus 0
                    volume-bus 0
                    scanner-delay 0.01
                    scanner-delay-mod 0.5
                    scanner-mod-rate 5
                    scanner-mod-spread 0
                    scanner-scan-rate 0.1
                    scanner-crossmix 0.1
                    scanner-mix 0.0
                    room-size 0.7
                    reverb-damp 0.5
                    reverb-mix 0.0
                    dbscale 0]
  (let [dry-sig (in:ar in-bus)
        ;; Scanner 
        scanner-depth (qu/clamp
                       (+ scanner-mix (in:kr scanner-mix-bus))
                       0 1)
        cdt (+ (* 0.0099 scanner-delay) 0.0001) ;; common-delay-time
        smr scanner-mod-rate
        smd scanner-max-delay
        mod-scale scanner-delay-mod
        spread scanner-mod-spread
        z1 (delay-l dry-sig smd (DelayTime cdt smr mod-scale 0))
        z2 (delay-l z1 smd (DelayTime cdt smr mod-scale (* 1 spread)))
        z3 (delay-l z2 smd (DelayTime cdt smr mod-scale (* 2 spread)))
        z4 (delay-l z3 smd (DelayTime cdt smr mod-scale (* 3 spread)))
        z5 (delay-l z4 smd (DelayTime cdt smr mod-scale (* 4 spread)))
        z6 (delay-l z5 smd (DelayTime cdt smr mod-scale (* 5 spread)))
        z7 (delay-l z6 smd (DelayTime cdt smr mod-scale (* 6 spread)))
        z8 (delay-l z7 smd (DelayTime cdt smr mod-scale (* 7 spread)))
        lfo (* 8 (/ (+ 1.0 (lf-cub:kr scanner-scan-rate)) 2.0))
        a1 (qu/sin-window lfo -2 2)
        a2 (qu/sin-window lfo -1 3)
        a3 (qu/sin-window lfo 0 4)
        a4 (qu/sin-window lfo 1 5)
        a5 (qu/sin-window lfo 2 6)
        a6 (qu/sin-window lfo 3 7)
        a7 (qu/sin-window lfo 4 8)
        a8 (qu/sin-window lfo 5 9)
        scanner-wet-sig-1 (+ 
                           (* a1 z1)
                           (* a2 z2)
                           (* a3 z3)
                           (* a4 z4)
                           (* a5 z5)
                           (* a6 z6)
                           (* a7 z7)
                           (* a8 z8))
        scanner-wet-sig-2 (+ 
                           (* a6 z1)
                           (* a7 z2)
                           (* a8 z3)
                           (* a1 z4)
                           (* a2 z5)
                           (* a3 z6)
                           (* a4 z7)
                           (* a5 z8))
        xmix (- scanner-crossmix 1)
        scanner-wet-sig  [(x-fade2 scanner-wet-sig-1 scanner-wet-sig-2 xmix)
                          (x-fade2 scanner-wet-sig-2 scanner-wet-sig-1 xmix)] 
        scanner-out (qu/efx-mixer [dry-sig dry-sig]
                                  (* 0.707 scanner-wet-sig)
                                  scanner-depth)
        ;; Reverb
        reverb-depth (qu/clamp (+ reverb-mix (in:kr reverb-mix-bus))
                               0 1)
        reverb-wet-sig (free-verb scanner-out 1 room-size reverb-damp)
        reverb-out (qu/efx-mixer scanner-out reverb-wet-sig reverb-depth)
        ;; Master volume
        volume (* (dbamp dbscale) 
                  (in:kr volume-bus))] 
    (out:ar out-bus (* volume reverb-out))))
