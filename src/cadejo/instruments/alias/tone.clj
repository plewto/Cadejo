(println "\t--> tone")

(ns cadejo.instruments.alias.tone
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu]))

;; ToneBlock1 --->[distortion]--->[multi-mode-filter]--->[amp-env]-->
;;
(defsynth ToneBlock1 [distortion1-pregain 1.00
                      distortion1-param 0
                      distortion1-param-source 0
                      distortion1-param-depth 0
                      distortion1-mix 0.00
                      filter1-res 0.0
                      filter1-res-source 0
                      filter1-res-depth 0
                      filter1-freq 10000
                      filter1-freq1-source 0
                      filter1-freq1-depth 0
                      filter1-freq2-source 0
                      filter1-freq2-depth 0
                      filter1-pan -0.75
                      filter1-pan-source 0
                      filter1-pan-depth 0
                      filter1-mode 0 ; 0.0=lp 0.25=lp*hp 0.50=hp 0.75=band 1.00=bypass
                      filter1-postgain 1.00
                      a-bus 0           ; control buses
                      b-bus 0
                      c-bus 0
                      d-bus 0
                      e-bus 0
                      f-bus 0
                      g-bus 0
                      h-bus 0
                      env3-bus 0        ; amplitude envelope
                      in-bus 0          ; 1-channel input bus
                      out-bus 0]        ; 2-channel output bus
  (let [a (in:kr a-bus)
        b (in:kr b-bus)
        c (in:kr c-bus)
        d (in:kr d-bus)
        e (in:kr e-bus)
        f (in:kr f-bus)
        g (in:kr g-bus)
        h (in:kr h-bus)
        env3 (in:kr env3-bus)
        sources [1 a b c d e f g h 0
]
        dry-sig (in:ar in-bus)
        dist-n (qu/clamp (+ distortion1-param 
                            (* distortion1-param-depth
                               (select:kr distortion1-param-source sources)))
                         0.01 0.99)
        dist-gain (/ 1.0 (- 1.0 dist-n))
        dist-in (* distortion1-pregain dry-sig)
        dist-wet (* dist-gain (excess dist-in dist-n))
        filter-in (x-fade2 dry-sig dist-wet distortion1-mix)
        ;; FILTERS
        rq (+ 1.0 (* -0.99 (qu/clamp 
                            (+ filter1-res
                               (* filter1-res-depth
                                  (select:kr filter1-res-source sources)))
                            0 1)))
        ffreq (+ filter1-freq 
                 (* filter1-freq1-depth (select:kr filter1-freq1-source sources))
                 (* filter1-freq2-depth (select:kr filter1-freq2-source sources)))
        fpos (+ filter1-pan
                (* filter1-pan-depth (select:kr sources filter1-pan-source)))
        lp (rlpf filter-in ffreq rq)
        hp (rhpf filter-in ffreq rq)
        bp (resonz filter-in ffreq rq)
        lp*hp (* lp hp)
        mode filter1-mode
        lp-amp (qu/cos-shelf mode 0.0 0.25)
        lphp-amp (qu/sin-window mode 0.0 0.5)
        hp-amp (qu/sin-window mode 0.25 0.75)
        bp-amp (* (dbamp 6)(qu/sin-window mode 0.5 1.0))
        bypass-amp (qu/!cos-shelf mode 0.75 1.0)
        filter-out (+ (* bp-amp bp)
                      (* bypass-amp filter-in)
                      (* lp-amp lp)
                      (* lphp-amp lp*hp)
                      (* hp-amp hp))]
  (out:ar out-bus (* env3 filter1-postgain (pan2:ar filter-out fpos))))) 

(defsynth ToneBlock2 [distortion2-pregain 1.00
                      distortion2-param 0
                      distortion2-param-source 0
                      distortion2-param-depth 0
                      distortion2-mix 0.00
                      filter2-res 0.0
                      filter2-res-source 0
                      filter2-res-depth 0
                      filter2-freq 10000
                      filter2-freq1-source 0
                      filter2-freq1-depth 0
                      filter2-freq2-source 0
                      filter2-freq2-depth 0
                      filter2-pan -0.75
                      filter2-pan-source 0
                      filter2-pan-depth 0
                      filter2-postgain 1.00
                      a-bus 0           ; control buses
                      b-bus 0
                      c-bus 0
                      d-bus 0
                      e-bus 0
                      f-bus 0
                      g-bus 0
                      h-bus 0
                      env3-bus 0        ; amplitude envelope
                      in-bus 0          ; 1-channel input bus
                      out-bus 0]        ; 2-channel output bus
  (let [a (in:kr a-bus)
        b (in:kr b-bus)
        c (in:kr c-bus)
        d (in:kr d-bus)
        e (in:kr e-bus)
        f (in:kr f-bus)
        g (in:kr g-bus)
        h (in:kr h-bus)
        env3 (in:kr env3-bus)
        sources [1 a b c d e f g h 0]
        dry-sig (in:ar in-bus)
        fold-in (* distortion2-pregain dry-sig)
        fold-b (+ 1 
                  (* -0.99 
                     (qu/clamp 
                      (+ distortion2-param 
                         (* distortion2-param-depth
                            (select:kr distortion2-param-source sources)))
                      0.01 1.0)))
        fold-gain (/ 1.0 fold-b)
        dist-wet (* fold-gain (fold2 fold-in fold-b))
        filter-in (x-fade2 dry-sig dist-wet distortion2-mix)
        ;; FILTERS
        res (qu/clamp (* 4 (+ filter2-res
                              (* filter2-res-depth
                                 (select:kr filter2-res-source sources))))
                      0 4)
        ffreq (+ filter2-freq 
                 (* filter2-freq1-depth (select:kr filter2-freq1-source sources))
                 (* filter2-freq2-depth (select:kr filter2-freq2-source sources)))
        fpos (+ filter2-pan
                (* filter2-pan-depth (select:kr sources filter2-pan-source)))
        lp (moog-ff filter-in ffreq res -1)
        filter-out (* filter2-postgain lp)]
    (tap :res 5 res)  ;; DEBUG
    (out:ar out-bus (* env3 filter2-postgain (pan2:ar filter-out fpos)))))
