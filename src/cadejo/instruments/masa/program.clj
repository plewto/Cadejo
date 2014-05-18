(println "\t--> MASA program")

(ns cadejo.instruments.masa.program
  (:require [cadejo.midi.bank]))

;; Some common harmonic gamuts

(def b3 (map float [1/2 3/2 1 2 3 4 5 6 8]))
(def harmonic (map float [1 2 3 4 5 6 7 8 9]))
(def odd (map float [1/2 3/2 5/2 7/2 9/2 11/2 13/2 15/2 17/2]))
(def prime (map float [1/2 3/2 5/2 7/2 11/2 13/2 17/2 19/2 23/2]))

;; Return "flat" assoc list of masa parameter/value pairs
;;
(defn masa [& {:keys [harmonics
                      registration
                      pedals
                      percussion
                      decay sustain 
                      vrate vsens vdepth vdelay
                      amp pedal-sens
                      scanner-delay
                      scanner-delay-mod
                      scanner-mod-rate
                      scanner-mod-spread
                      scanner-scan-rate
                      scanner-crossmix 
                      scanner-mix
                      room-size reverb-damp reverb-mix]
               :or {harmonics b3
                    registration [0 0 8 6 0 4 0 0 2]
                    pedals [0 0 0 0 0 0 0 0 0]
                    percussion [0 0 0 0 0 0 0 0 0]
                    decay 0.2
                    sustain 0.8
                    vrate 7.0
                    vsens 0.05
                    vdepth 0
                    vdelay 0
                    amp 0.3
                    pedal-sens 0
                    scanner-delay 0.01
                    scanner-delay-mod 0.5
                    scanner-mod-rate 7
                    scanner-mod-spread 0
                    scanner-scan-rate 0.1
                    scanner-crossmix 0.2
                    scanner-mix 0
                    room-size 0.5
                    reverb-damp 0.5
                    reverb-mix 0}}]
  (list 
   :r1 (float (nth harmonics 0))                   ; harmonics
   :r2 (float (nth harmonics 1))
   :r3 (float (nth harmonics 2))
   :r4 (float (nth harmonics 3))
   :r5 (float (nth harmonics 4))
   :r6 (float (nth harmonics 5))
   :r7 (float (nth harmonics 6))
   :r8 (float (nth harmonics 7))
   :r9 (float (nth harmonics 8))
   :a1 (int (nth registration 0))                  ; registration
   :a2 (int (nth registration 1))
   :a3 (int (nth registration 2))
   :a4 (int (nth registration 3))
   :a5 (int (nth registration 4))
   :a6 (int (nth registration 5))
   :a7 (int (nth registration 6))
   :a8 (int (nth registration 7))
   :a9 (int (nth registration 8))
   :p1 (float (nth pedals 0))                      ; pedals
   :p2 (float (nth pedals 1))
   :p3 (float (nth pedals 2))
   :p4 (float (nth pedals 3))
   :p5 (float (nth pedals 4))
   :p6 (float (nth pedals 5))
   :p7 (float (nth pedals 6))
   :p8 (float (nth pedals 7))
   :p9 (float (nth pedals 8))
   :perc1 (int (nth percussion 0))
   :perc2 (int (nth percussion 1))
   :perc3 (int (nth percussion 2))
   :perc4 (int (nth percussion 3))
   :perc5 (int (nth percussion 4))
   :perc6 (int (nth percussion 5))
   :perc7 (int (nth percussion 6))
   :perc8 (int (nth percussion 7))
   :perc9 (int (nth percussion 8))
   :decay (float decay)
   :sustain (float sustain)
   :vrate (float vrate)                            ; vibrato
   :vsens (float vsens)
   :vdepth (float vdepth)
   :vdelay (float vdelay)
   :amp (float amp)
   :pedal-sens (float pedal-sens)
   :scanner-delay (float scanner-delay)
   :scanner-delay-mod (float scanner-delay-mod)
   :scanner-mod-rate (float scanner-mod-rate)
   :scanner-mod-spread (float scanner-mod-spread)
   :scanner-scan-rate (float scanner-scan-rate)
   :scanner-crossmix (float scanner-crossmix)
   :scanner-mix (float scanner-mix)
   :reverb-size (float room-size)
   :reverb-damp (float reverb-damp)
   :reverb-mix (float reverb-mix))) 


(def default-program
  (masa :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000]
        :registration [    0     0     8     6     0     4     0     0     2]
        :pedals       [ 0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00]
        :percussion   [    0     0     0     0     0     0     0     0     0]
        :decay        0.2
        :sustain      0.8
        :vrate        7.0
        :vsens        0.05
        :vdepth       0.00
        :vdelay       0.00
        :amp          0.30
        :pedal-sens   0.00
        :scanner-delay      0.01
        :scanner-delay-mod  0.5
        :scanner-mod-rate   7.0
        :scanner-mod-spread 0.0
        :scanner-scan-rate  0.1
        :scanner-crssmix    0.2
        :scanner-mix        0.0
        :room-size   0.5
        :reverb-damp 0.5
        :reverb-mix  0.5))

(def bank (cadejo.midi.bank/bank :MASA
           "Default MASA Program Bank"))

(defn save-program 
  ([pnum name remarks data]
     (.set-program! bank pnum name remarks data))
  ([pnum name data]
     (save-program pnum name "" data)))
