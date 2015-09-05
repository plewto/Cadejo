(println "--> Loading MASA organ")

(ns cadejo.instruments.masa.masa-engine
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.poly-rotate-mode])
  (:require [cadejo.midi.mono-exclusive])
  (:require [cadejo.midi.performance])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.instruments.masa.genpatch])
  (:require [cadejo.instruments.masa.program])
  (:require [cadejo.instruments.masa.pp])
  (:require [cadejo.instruments.masa.data])
  (:require [cadejo.instruments.masa.efx :as efx])
  (:require [cadejo.instruments.masa.editor.masa-editor]))

(def clipboard* (atom nil))

(def masa-descriptor
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :masa "MASA Organ" clipboard*)]
    (.add-controller! d :cc1 "Vibrato" 1)
    (.add-controller! d :cc4 "Pedal" 4)  ;; adds to partial amplitude 
                                         ;; if partial amplitud is at max, pedal has no effect
    (.add-controller! d :cc7 "Volume" 7)
    (.add-controller! d :cca "Scanner Mix" 92)
    (.add-controller! d :ccb "Reverb Mix" 93)
    (.set-editor-constructor! d cadejo.instruments.masa.editor.masa-editor/masa-editor)
    (.initial-program! d {:r1 0.5 :r2 1.5 :r3 1.0 :r4 2.0 :r5 3.0 
                          :r6 4.0 :r7 5.0 :r8 6.0 :r9 8.0
                          :a1 0 :a2 0 :a3 8 :a4 0 :a5 0 
                          :a6 0 :a7 0 :a8 0 :a9 0
                          :p1 0.0 :p2 0.0 :p3 0.0 :p4 0.0
                          :p5 0.0 :p6 0.0 :p7 0.0 :p8 0.0 :p9 0.0 
                          :perc1 0 :perc2 0 :perc3 0 :perc4 0 :perc5 0
                          :perc6 0 :perc7 0 :perc8 0 :perc9 0 
                          :decay 0.2 :sustain 0.7
                          :scanner-delay 0.01 :scanner-delay-mod 0.5
                          :scanner-mod-rate 5.0 :scanner-mod-spread 0.0
                          :scanner-scan-rate 0.1 :scanner-mix 0.0 :scanner-crossmix 0.1
                          :reverb-size 0.7 :reverb-damp 0.5 :reverb-mix 0.0
                          :vrate 7.0 :vsens 0.05 :vdepth 0.00 :vdelay 0.0 
                          :amp 0.2})
    (.program-generator! d cadejo.instruments.masa.genpatch/random-masa-program)
    (.help-topic! d :masa)
    d))

(defsynth VibratoBlock [vibrato-depth-bus 0
                        vibrato-bus 0
                        gate 0
                        vrate 7
                        vsens 0.05
                        vdepth 0
                        vdelay 0]
  (let [env (env-gen:kr (envelope [0 0 1 1 0]
                                  [vdelay vdelay 0.01 0.01]
                                  :linear 3)
                        :gate gate
                        :action NO-ACTION)
        amp (min 1 (* vsens 
                      (+ (in:kr vibrato-depth-bus)
                         (* vdepth env))))]
    (out:kr vibrato-bus (* amp (sin-osc:kr vrate)))))

(defcgen drawbar-amp [pos]
  (:ir
   (* (dbamp (* (- 8 (min pos 8)) -3))
      (min (max (thresh pos 1) 0) 1))))

;; pflag sets envelope type
;; pflag 0 --> gate
;; pflag 1 --> ADSR D = decay S = 1-sustain
;; 
;; NOTE: When drawbar percussion enabled, average amplitude is decreased.
;;
(defcgen drawbar-env [pflag decay sustain-attenuation gate]
  (:kr 
   (env-gen:kr (adsr 0 (* pflag decay)(- 1 (* pflag sustain-attenuation)) 0)
               :gate gate
               :action NO-ACTION)))
                   
(defcgen pedal-scale [pedal-position drawbar-amp scale]
  (:kr 
   (qu/clamp (+ drawbar-amp (* pedal-position scale))
             0.0 1.0)))

(defsynth ToneBlock [freq 440
                     gate 0
                     out-bus 0
                     bend-bus 0
                     vibrato-bus 0
                     pedal-bus 0
                     amp 0.2
                     ;pedal-sens 0
                     r1 0.5
                     r2 1.5
                     r3 1.0
                     r4 2.0
                     r5 3.0
                     r6 4.0
                     r7 5.0
                     r8 6.0
                     r9 8.0
                     a1 0
                     a2 0
                     a3 8
                     a4 6
                     a5 0
                     a6 4
                     a7 0
                     a8 0
                     a9 2
                     p1 0.0
                     p2 0.0
                     p3 0.0
                     p4 0.0
                     p5 0.0
                     p6 0.0
                     p7 0.0
                     p8 0.0
                     p9 0.0
                     perc1 0
                     perc2 0
                     perc3 0
                     perc4 0
                     perc5 0
                     perc6 0
                     perc7 0
                     perc8 0
                     perc9 0
                     decay 0.2          ;only for percussive env
                     sustain 0.7]        
  (let [vib-sig (+ 1 (in:kr vibrato-bus))
        pedal-position (in:kr pedal-bus)
        perc-sus-attenuation (- 1 sustain)
        f0 (* freq (in:kr bend-bus))
        f1 (* f0 r1 vib-sig)
        f2 (* f0 r2 vib-sig)
        f3 (* f0 r3 vib-sig)
        f4 (* f0 r4 vib-sig)
        f5 (* f0 r5 vib-sig)
        f6 (* f0 r6 vib-sig)
        f7 (* f0 r7 vib-sig)
        f8 (* f0 r8 vib-sig)
        f9 (* f0 r9 vib-sig)
        dba1 (drawbar-amp a1)
        dba2 (drawbar-amp a2)
        dba3 (drawbar-amp a3)
        dba4 (drawbar-amp a4)
        dba5 (drawbar-amp a5)
        dba6 (drawbar-amp a6)
        dba7 (drawbar-amp a7)
        dba8 (drawbar-amp a8)
        dba9 (drawbar-amp a9)
        amp1 (pedal-scale pedal-position dba1 p1)
        amp2 (pedal-scale pedal-position dba2 p2)
        amp3 (pedal-scale pedal-position dba3 p3)
        amp4 (pedal-scale pedal-position dba4 p4)
        amp5 (pedal-scale pedal-position dba5 p5)
        amp6 (pedal-scale pedal-position dba6 p6)
        amp7 (pedal-scale pedal-position dba7 p7)
        amp8 (pedal-scale pedal-position dba8 p8)
        amp9 (pedal-scale pedal-position dba9 p9)
        env1 (drawbar-env perc1 decay perc-sus-attenuation gate)
        env2 (drawbar-env perc2 decay perc-sus-attenuation gate)
        env3 (drawbar-env perc3 decay perc-sus-attenuation gate)
        env4 (drawbar-env perc4 decay perc-sus-attenuation gate)
        env5 (drawbar-env perc5 decay perc-sus-attenuation gate)
        env6 (drawbar-env perc6 decay perc-sus-attenuation gate)
        env7 (drawbar-env perc7 decay perc-sus-attenuation gate)
        env8 (drawbar-env perc8 decay perc-sus-attenuation gate)
        env9 (drawbar-env perc9 decay perc-sus-attenuation gate)
        osc1 (* env1 amp1 (sin-osc:ar f1))
        osc2 (* env2 amp2 (sin-osc:ar f2))
        osc3 (* env3 amp3 (sin-osc:ar f3))
        osc4 (* env4 amp4 (sin-osc:ar f4))
        osc5 (* env5 amp5 (sin-osc:ar f5))
        osc6 (* env6 amp6 (sin-osc:ar f6))
        osc7 (* env7 amp7 (sin-osc:ar f7))
        osc8 (* env8 amp8 (sin-osc:ar f8))
        osc9 (* env9 amp9 (sin-osc:ar f9))
        out-sig (* amp 
                   (+ osc1 osc2 osc3 
                      osc4 osc5 osc6
                      osc7 osc8 osc9))]
    (out:ar out-bus out-sig)))


(defn- create-performance [chanobj id keymode main-out-bus 
                           cc-vibrato cc-pedal cc-volume
                           cc-scanner cc-reverb]
  (let [bank (.clone cadejo.instruments.masa.program/bank)
        performance (cadejo.midi.performance/performance chanobj id keymode 
                                                         bank masa-descriptor
                                                         [:cc1 cc-vibrato :linear 0.0]
                                                         [:cc4 cc-pedal   :linear 0.0]
                                                         [:cc7 cc-volume  :linear 1.0]
                                                         [:cca cc-scanner :linear 0.0]
                                                         [:ccb cc-reverb  :linear 0.0])]
    (.put-property! performance :instrument-type :masa)
    (.set-bank! performance bank)
    (let [bend-bus (.control-bus performance :bend)
          cc-vibrato-bus (.control-bus performance cc-vibrato)
          cc-pedal-bus (.control-bus performance cc-pedal)
          cc-volume-bus (.control-bus performance cc-volume)
          cc-scanner-mix-bus (.control-bus performance cc-scanner)
          cc-reverb-mix-bus (.control-bus performance cc-reverb)
          vibrato-bus (control-bus)
          tone-bus (audio-bus)]
      (.pp-hook! bank cadejo.instruments.masa.pp/pp-masa)
      (.add-control-bus! performance :vibrato-depth cc-vibrato-bus)
      (.add-control-bus! performance :pedal cc-pedal-bus)
      (.add-control-bus! performance :volume cc-volume-bus)
      (.add-control-bus! performance :scanner-mix cc-scanner-mix-bus)
      (.add-control-bus! performance :reverb-mix cc-reverb-mix-bus)
      (.add-control-bus! performance :vibrato vibrato-bus)
      (.add-audio-bus! performance :tone tone-bus)
      (.add-audio-bus! performance :main-out main-out-bus)
      performance)))

;; cc1 - vibrato
;; cc4 - pedal
;; cc7 - volume
;; cca - scanner mix
;; ccb - reverb mix

(defn- --masa-mono [scene chan keymode id cc1 cc4 cc7 cca ccb main-out]
     (let [chanobj (.channel scene chan)
           performance (create-performance chanobj id keymode main-out
                                           cc1 cc4 cc7
                                           cca ccb)
           vibrato-block (VibratoBlock
                          :vibrato-depth-bus (.control-bus performance :vibrato-depth)
                          :vibrato-bus (.control-bus performance :vibrato))
           voice (ToneBlock :out-bus (.audio-bus performance :tone)
                            :bend-bus (.control-bus performance :bend)
                            :vibrato-bus (.control-bus performance :vibrato)
                            :pedal-bus (.control-bus performance :pedal))
           efx-block (efx/EfxBlock :in-bus (.audio-bus performance :tone)
                                   :out-bus main-out
                                   :volume-bus (.control-bus performance :volume)
                                   :scanner-mix-bus (.control-bus performance :scanner-mix)
                                   :reverb-mix-bus (.control-bus performance :reverb-mix))]
       (.add-synth! performance :vibrato vibrato-block)
       (.add-synth! performance :efx efx-block)
       (.add-voice! performance voice)
       (Thread/sleep 100) 
       (.reset chanobj)
       performance))

(defn masa-mono [scene chan id & {:keys [cc1 cc4 cc7 cca ccb main-out]
                                  :or {cc1 1
                                       cc4 4
                                       cc7 7
                                       cca 92
                                       ccb 93
                                       main-out 0}}]
  (let [km (cadejo.midi.mono-mode/mono-keymode :MASA)]
    (--masa-mono scene chan km id cc1 cc4 cc7 cc1 ccb main-out)))

(defn masa-exclusive [scene chan id & {:keys [cc1 cc4 cc7 cca ccb main-out]
                                       :or {cc1 1
                                            cc4 4
                                            cc7 7
                                            cca 92
                                            ccb 93
                                            main-out 0}}]
  (let [km (cadejo.midi.mono-exclusive/mono-exclusive-keymode :MASA)]
    (--masa-mono scene chan km id cc1 cc4 cc7 cc1 ccb main-out)))

(defn- --masa-poly [scene chan id  keymode cc1 cc4 cc7 cca ccb voice-count main-out]
     (let [chanobj (.channel scene chan)
           performance (create-performance chanobj id keymode main-out
                                           cc1 cc4 cc7
                                           cca ccb)
           vibrato-block (VibratoBlock
                          :vibrato-depth-bus (.control-bus performance :vibrato-depth)
                          :vibrato-bus (.control-bus performance :vibrato))
           voices (let [acc* (atom [])] 
                    (dotimes [i voice-count]
                      (let  [v (ToneBlock 
                                :out-bus (.audio-bus performance :tone)
                                :bend-bus (.control-bus performance :bend)
                                :vibrato-bus (.control-bus performance :vibrato)
                                :pedal-bus (.control-bus performance :pedal))]
                        (swap! acc* (fn [n](conj n v)))
                        (Thread/sleep 100)))
                    @acc*)
           efx-block (efx/EfxBlock 
                      :in-bus (.audio-bus performance :tone)
                      :out-bus main-out
                      :volume-bus (.control-bus performance :volume)
                      :scanner-mix-bus (.control-bus performance :scanner-mix)
                      :reverb-mix-bus (.control-bus performance :reverb-mix))]
       (.add-synth! performance :vibrato vibrato-block)
       (.add-synth! performance :efx efx-block)
       (doseq [v voices](.add-voice! performance v))
       (.reset chanobj)
       performance))

(defn masa-poly [scene chan id & {:keys [cc1 cc4 cc7 cca ccb voice-count main-out]
                                  :or {cc1 1, cc4 16, cc7 7, cca 92, ccb 93,
                                       voice-count 8, main-out 0}}]
  (let [km (cadejo.midi.poly-mode/poly-keymode :MASA voice-count)]
    (--masa-poly scene chan id km cc1 cc4 cc7 cca ccb voice-count main-out)))


(defn masa-poly-rotate [scene chan id & {:keys [cc1 cc4 cc7 cca ccb voice-count main-out]
                                         :or {cc1 1, cc4 16, cc7 7, cca 92, ccb 93,
                                              voice-count 8, main-out 0}}]
  (let [km (cadejo.midi.poly-rotate-mode/poly-rotate-mode :MASA voice-count)]
    (--masa-poly scene chan id km cc1 cc4 cc7 cca ccb voice-count main-out)))


(.add-constructor! masa-descriptor :exclusive masa-exclusive)
(.add-constructor! masa-descriptor :mono masa-mono)
(.add-constructor! masa-descriptor :rotate masa-poly-rotate)
(.add-constructor! masa-descriptor :poly masa-poly)
