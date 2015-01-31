(println "--> Loading Combo organ")

(ns cadejo.instruments.combo.combo-engine
  (:use [cadejo.util.trace])
  (:use [overtone.core])
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.modules.qugen :as qu])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.instruments.combo.genpatch])
  (:require [cadejo.instruments.combo.program])
  (:require [cadejo.instruments.combo.pp])
  (:require [cadejo.instruments.combo.data])
  (:require [cadejo.instruments.combo.editor.combo-editor :as combo-ed])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.performance]))

(def clipboard* (atom nil))

(def combo-descriptor
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :combo "Combo Organ" clipboard*)]
    (.add-controller! d :cc1 "Vibrato" 1)
    (.initial-program! d (cadejo.instruments.combo.program/combo))
    (.set-editor-constructor! d combo-ed/combo-editor)
    (.initial-program! d {:amp1 1.0 :amp2 0.0 :amp3 0.0 :amp4 0.0
                          :wave1 0.0 :wave2 0.0 :wave3 0.0 :wave4 0.0
                          :chorus 0.0 :filter 8.0 :filter-type 0 :amp 0.2
                          :flanger-depth 0.0 :flanger-rate 0.1 :flanger-fb 0.5
                          :flanger-mix 0.0 :reverb-mix 0.0})
    (.program-generator! d cadejo.instruments.combo.genpatch/random-combo-program)
    (.help-topic! d :combo)
    d))

(defsynth LFO [vibrato-freq 5.00
               vibrato-sens 0.01
               vibrato-bus 0
               vibrato-depth-bus 0]
  (let [amp (* vibrato-sens
               (in:kr vibrato-depth-bus))]
    (out:kr vibrato-bus (+ 1 (* amp (sin-osc:kr vibrato-freq))))))

;; (def bypass-filter 0)
;; (def lp-filter 1)
;; (def hp-filter 2)
;; (def bp-filter 3)
;; (def br-filter 4)

;; (1.0 --> 0db)
;; (0.0 --> -60db)
;;
(defcgen amp-scale [amp]
  (:ir (dbamp (- (* 36 (qu/clamp amp 0 1)) 36))))

;; (0.0 --> 0.5)
;; (1.0 --> 0.1)
;;
(defcgen pulse-width [wave]
  (:ir (+ (* -0.4 (qu/clamp wave 0 1)) 0.5)))

;; (0.0 --> 0.0)
;; (1.0 --> 4.0)
;;
(defcgen feedback [wave]
  (:ir (* 4 (qu/clamp wave 0 1))))

(defsynth ToneBlock [freq 100
                     gate 0
                     amp1  1.00
                     wave1 0.00
                     amp2  1.00
                     wave2 0.00
                     amp3  1.00
                     wave3 0.00
                     amp4  1.00
                     wave4 0.00
                     chorus 0.00 
                     filter 8.00 ; range 1, 2, 4, 6 or 8
                     filter-type lp-filter
                     bend-bus 0
                     vibrato-bus 0
                     out-bus 0]
  (let [vibrato (in:kr vibrato-bus) 
        dt2 (+ 1 (* 1/2 chorus))
        dt3 (+ 1 (* 1/3 chorus))
        dt4 (+ 1 (* 1/4 chorus))
        f0 (* freq (in:kr bend-bus))
        f1 (* f0 vibrato)
        f2 (* 2 dt2 f0 vibrato)
        f3 (* 3 dt3 f0 vibrato)
        f4 (* 4 dt4 f0 vibrato)
        w1 (* (amp-scale amp1)
              (pulse:ar f1 (pulse-width wave1)))
        w2 (* (amp-scale amp2)
              (pulse:ar f2 (pulse-width wave2)))
        w3 (* (amp-scale amp3)
              (sin-osc-fb:ar f3 (feedback wave3)))
        w4 (* (amp-scale amp4)
              (sin-osc-fb:ar f4 (feedback wave4)))
        mixer (+ w1 w2 w3 w4)
        filter-freq (* f0 filter)
        rq 0.5
        filter-out (select filter-type [mixer 
                                        (lpf mixer filter-freq)
                                        (hpf mixer filter-freq)
                                        (* 2 (bpf mixer filter-freq rq))
                                        (brf mixer filter-freq rq)])]
    (out:ar out-bus (* gate filter-out))))

(def flanger-max-delay 1/100)
(def flanger-delay (* 1/2 flanger-max-delay))

(defsynth EfxBlock [amp 0.2
                    dbscale 0
                    flanger-depth 0.25
                    flanger-rate 0.25
                    flanger-fb -0.5
                    flanger-mix 0
                    reverb-mix 0
                    in-bus 0
                    out-bus 0]
  (let [drysig (in:ar in-bus)
        fbsig (* (qu/clamp flanger-fb -0.98 +0.98) 
                 (local-in:ar 2))
        lfo-amp (* flanger-delay 
                   (qu/clamp flanger-depth 0 1))
        lfo1 (* lfo-amp (sin-osc:kr flanger-rate))
        lfo2 (* -1 lfo1)
        flanger1 (delay-c:ar (+ drysig (nth fbsig 0))
                             flanger-max-delay
                             (+ flanger-delay lfo1))
        flanger2 (delay-c:ar (+ drysig (nth fbsig 1))
                             flanger-max-delay
                             (+ flanger-delay lfo2))
        flanger (qu/efx-mixer [drysig drysig]
                              [flanger1 flanger2]
                              (* 1/2 (qu/clamp flanger-mix 0 1)))
        room-size 0.8
        damp 0.5
        reverb (free-verb flanger 
                          (* 0.5 (qu/clamp reverb-mix 0 1))
                          room-size damp)
        gain (* amp (dbamp dbscale))]
    (local-out:ar [flanger1 flanger2])
    (out:ar out-bus (* gain reverb))))

(defn ^:private create-performance [chanobj id keymode cc1]
  (let [bank (.clone cadejo.instruments.combo.program/bank)
        performance (cadejo.midi.performance/performance chanobj id keymode 
                                                         bank combo-descriptor
                                                         [:cc1 cc1 :linear 0.0])]
    (.put-property! performance :instrument-type :combo)
    (.set-bank! performance bank)
    (let [vibrato-bus (control-bus)
          tone-bus (audio-bus)]
      (.pp-hook! bank cadejo.instruments.combo.pp/pp-combo)
      (.add-control-bus! performance :vibrato vibrato-bus)
      (.add-audio-bus! performance :tone tone-bus)
      performance)))

;; cc1 - vibrato
;;
(defn combo-mono 
  ([scene chan id & {:keys [cc1 main-out]
                     :or {cc1 1
                          main-out 0}}]
     (let [chanobj (.channel scene chan)
           keymode (cadejo.midi.mono-mode/mono-keymode :Combo)
           performance (create-performance chanobj id keymode cc1)
           vibrato-bus (.control-bus performance :vibrato)
           vibrato-depth-bus (.control-bus performance cc1)
           bend-bus (.control-bus performance :bend)
           tone-bus (.audio-bus performance :tone)
           lfo (LFO :vibrato-bus vibrato-bus
                    :vibrato-depth-bus vibrato-depth-bus)
           voice (ToneBlock :bend-bus bend-bus
                            :vibrato-bus vibrato-bus
                            :out-bus tone-bus)
           efx (EfxBlock :in-bus tone-bus
                         :out-bus main-out)]
       (.add-synth! performance :lfo lfo)
       (.add-synth! performance :efx efx)
       (.add-voice! performance voice)
       (.reset chanobj)
       (Thread/sleep 100)
       performance)))

(defn combo-poly
  ([scene chan id & {:keys [cc1 voice-count main-out]
                     :or {cc1 1
                          voice-count 8
                          main-out 0}}]
     (let [chanobj (.channel scene chan)
           keymode (cadejo.midi.poly-mode/poly-keymode :Combo voice-count)
           performance (create-performance chanobj id keymode cc1)
           vibrato-bus (.control-bus performance :vibrato)
           vibrato-depth-bus (.control-bus performance cc1)
           bend-bus (.control-bus performance :bend)
           tone-bus (.audio-bus performance :tone)
           lfo (LFO :vibrato-bus vibrato-bus
                    :vibrato-depth-bus vibrato-depth-bus)]
       (dotimes [i voice-count]
         (let [v (ToneBlock :bend-bus bend-bus
                            :vibrato-bus vibrato-bus
                            :out-bus tone-bus)]
           (.add-voice! performance v)
           (Thread/sleep 10)))
       (let [efx (EfxBlock :in-bus tone-bus
                           :out-bus main-out)]
         (.add-synth! performance :lfo lfo)
         (.add-synth! performance :efx efx)
         (.reset chanobj)
         performance))))

(.add-constructor! combo-descriptor :mono combo-mono)
(.add-constructor! combo-descriptor :poly combo-poly)
