(println "-->    cobalt random-program")
(ns cadejo.instruments.cobalt.randprog.random-program
  (:require [cadejo.util.col :as col])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.program :as prog])
  (:require [cadejo.instruments.cobalt.randprog.config :as config])
  (:require [cadejo.instruments.cobalt.randprog.env :as env])
  (:require [cadejo.instruments.cobalt.randprog.filter :as filter])
  (:require [cadejo.instruments.cobalt.randprog.gamut :as gamut])
  (:require [cadejo.instruments.cobalt.randprog.registration :as reg])
  (:require [cadejo.instruments.cobalt.randprog.timebase :as tb]))


(def coin math/coin)
(defn sign [n] (* n (coin 0.5 -1 1)))

(defn random-cobalt-program []
  (let [enable-pitch-env (coin @config/p-use-pitch-env*)
        envmap (env/select-envelopes enable-pitch-env)
        [gamut fm bias](gamut/select-gamut)
       
        registration (reg/select-registration gamut)
        ref-time (tb/pick-ref-time)
        delay1-flanger (coin @config/p-delay1-flanger*)
        delay2-flanger (coin @config/p-delay2-flanger*)
        uniform-penv-depth (if (coin @config/p-op-uniform-pitch-env*)
                       (sign (rand @config/op-max-pitch-env*))
                       nil)
        
        detune (fn [n](nth gamut (dec n)))
        fm-detune (fn [n](nth fm (dec n)))
        fm-bias (fn [n](nth bias (dec n)))
        reg (fn [n](nth registration (dec n)))
        delay1-time (tb/pick-delay1-time ref-time delay1-flanger)
        delay2-time (tb/pick-delay2-time delay1-time delay2-flanger)
        ]
    (prog/cobalt
     (prog/enable 1 2 3 4 5 6 :noise :buzz)
     (prog/port-time (coin @config/p-use-portamento* (rand 0.5) 0.0))
     (prog/vibrato (tb/pick-vibrato-frequency ref-time))
     (prog/lfo1 (tb/pick-lfo1-frequency ref-time)
                :cca (coin 0.1 (rand) 0.0)
                :bleed (coin @config/p-lfo1-bleed* (rand) 0.0)
                :delay (rand 2))
     (prog/lfo2 (tb/pick-lfo2-frequency ref-time delay1-flanger)
                :xenv (coin 0.2 (rand) 0.0)
                :cca (coin 0.2 (rand) 0.0)
                :ccb 0.0)
     (prog/lfo3 (tb/pick-lfo3-frequency ref-time delay2-flanger)
                :xenv (coin 0.2 (rand) 0.0)
                :cca 0.0
                :ccb (coin 0.2 (rand) 0.0))
     (let [emap (col/alist->map (:xenv envmap))]
       (prog/xenv :att (:att emap) :dcy1 (:dcy1 emap)
                  :dcy2 (:dcy2 emap) :rel (:rel emap)
                  :peak (:peak emap) :bp (:bp emap) :sus (:sus emap)))

     (let [emap (col/alist->map (:penv envmap))]
       (prog/penv :a0 (:a0 emap) :a1 (:a1 emap) :a2 (:a2 emap) :a3 (:a3 emap) 
                  :t1 (:t1 emap) :t2 (:t2 emap) :t3 (:t3 emap)))
     (prog/op1 (detune 1)(reg 1)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 1) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op1 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm1 (fm-detune 1)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))
     (prog/op2 (detune 2)(reg 2)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 2) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op2 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm2 (fm-detune 2)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))
     (prog/op3 (detune 3)(reg 3)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 3) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op3 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm3 (fm-detune 3)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))
     (prog/op4 (detune 4)(reg 4)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 4) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op4 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm4 (fm-detune 4)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))
     (prog/op5 (detune 5)(reg 5)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 5) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op5 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm5 (fm-detune 5)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))     
     (prog/op6 (detune 6)(reg 6)
               :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
               :cca (coin @config/p-op-cca* (rand) 0.0)
               :ccb (coin @config/p-op-ccb* (rand) 0.0)
               :prss (coin @config/p-op-pressure* (rand) 0.0)
               :vel (coin @config/p-op-velocity* (rand) 0.0)
               :key 60
               :left 0
               :right (if (> (detune 6) 4)(rand-nth [-3 -6 -9]) 0)
               :env (:op6 envmap)
               :penv (if enable-pitch-env 
                       (or uniform-penv-depth
                           (coin @config/p-op-use-pitch-env*
                                 (sign (rand @config/op-max-pitch-env*)) 
                                 0.0))
                       0.00))
     (prog/fm6 (fm-detune 6)
               (coin @config/p-op-use-fm*
                     (rand @config/op-max-fm-depth*)
                     0.0)
               :bias (fm-bias 1)
               :env (coin 0.75 (+ 0.5 (rand 0.5))(rand))
               :lag (coin 0.50 (rand) 0)
               :left (coin 0.33 (rand-nth [3 6 9]) 0)
               :right (coin 0.33 (rand-nth [-3 -6 -9]) 0))
     (prog/noise (detune 7)(reg 7)
                 :bw (+ @config/noise-min-bw* 
                        (rand (- @config/noise-max-bw* @config/noise-min-bw*)))
                 :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
                 :cca (coin @config/p-op-cca* (rand) 0.0)
                 :prss (coin @config/p-op-pressure* (rand) 0.0)
                 :key 60
                 :left 0
                 :right (if (> (detune 7) 4)(rand-nth [-3 -6 -9]) 0)
                 :env (:noise envmap)
                 :penv (if enable-pitch-env 
                         (or uniform-penv-depth
                             (coin @config/p-op-use-pitch-env*
                                   (sign (rand @config/op-max-pitch-env*)) 
                                   0.0))
                         0.00))
     (prog/noise2 (detune 8)(reg 8)
                  :bw (+ @config/noise-min-bw* 
                         (rand (- @config/noise-max-bw* @config/noise-min-bw*)))
                  :lag (coin 0.5 0.0 (rand 2)))
     (prog/buzz (detune 9)(reg 9)
           :lfo1 (coin @config/p-op-lfo* (rand) 0.0)
           :cca (coin @config/p-op-cca* (rand) 0.0)
           :ccb (coin @config/p-op-ccb* (rand) 0.0)
           :prss (coin @config/p-op-pressure* (rand) 0.0)
           :vel (coin @config/p-op-velocity* (rand) 0.0)
           :key 60
           :left 0
           :right (if (> (detune 9) 4)(rand-nth [-3 -6 -9]) 0)
           :env (:buzz envmap)
           :penv (if enable-pitch-env 
                   (or uniform-penv-depth
                       (coin @config/p-op-use-pitch-env*
                             (sign (rand @config/op-max-pitch-env*)) 
                             0.0))
                   0.00))
     (let [n (int (rand @config/buzz-max-harmonics*))
           e (cond (> n (* 2/3 @config/buzz-max-harmonics*))
                   (int (- (rand n)))

                   :default
                   (int (rand (- @config/buzz-max-harmonics* n))))
           hp (coin 0.75 1 (rand 4))]
       (prog/buzz-harmonics n :env e :cca 0.0 
                            :hp hp
                            :hp<-env (coin 0.75 0.0 (int (rand 12)))))

     (filter/select-filter (:filter envmap))
     (let [enable (coin @config/p-use-distortion*)]
       (if @config/verbose*
         (println (format ";; Distortion enabled: %s" enable)))
       (prog/fold :wet (if enable (+ 0.25 (rand 0.75)) 0.0)
                  :gain (rand-nth [1 1 1 2 4 8])
                  :cca 0.0
                  :ccb 0.0))

     (let [fb (if delay1-flanger 
                (sign (+ 0.50 (rand 0.4)))
                (rand 0.9))
           xfb-limit (* 3/4 (- 1 (math/abs fb)))
           xfb (coin @config/p-delay1-xfb* 
                     (sign (rand xfb-limit))
                     0.00)
           p-pan-mod (* 1/3 @config/p-delay1-pan-mod*)]
       (prog/delay1 :time [delay1-time 
                           :lfo2 (if delay1-flanger 
                                   (coin 0.75 (rand) 0.0)
                                   (coin @config/p-delay1-time<-lfo2* 
                                         (rand) 0.0))
                           :lfo3 (coin @config/p-delay1-time<-lfo3* (rand) 0.0)
                           :xenv (if delay1-flanger
                                   (coin 0.25 (rand) 0.0)
                                   (coin @config/p-delay1-time<-xenv* 
                                         (rand) 0.0))]
                    :amp [(int (+ @config/delay-min-amp* 
                                  (rand (- @config/delay-max-amp* 
                                           @config/delay-min-amp*))))
                          :lfo2 (coin (* 1/2 @config/p-delay1-amp<-lfo*) 
                                      (rand) 0.0)
                          :lfo3 (coin (* 1/2 @config/p-delay1-amp<-lfo*) 
                                      (rand) 0.0)
                          :xenv (coin @config/p-delay1-amp<-xenv*
                                      (+ 0.5 (rand 0.5)) 0.0)]
                    :pan [-0.7 
                          :lfo2 (coin p-pan-mod (rand) 0.0)
                          :lfo3 (coin p-pan-mod (rand) 0.0)
                          :xenv (coin p-pan-mod (rand 1.5) 0.0)]
                    :fb fb :xfb xfb))

     (let [fb (if delay2-flanger 
                (sign (+ 0.50 (rand 0.4)))
                (rand 0.9))
           xfb-limit (* 3/4 (- 1 (math/abs fb)))
           xfb (coin @config/p-delay2-xfb* 
                     (sign (rand xfb-limit))
                     0.00)
           p-pan-mod (* 1/3 @config/p-delay2-pan-mod*)]
       (prog/delay2 :time [delay2-time 
                           :lfo3 (if delay2-flanger 
                                   (coin 0.60 (rand) 0.0)
                                   (coin @config/p-delay2-time<-lfo2* 
                                         (rand) 0.0))
                           :lfo2 (coin @config/p-delay2-time<-lfo3* (rand) 0.0)
                           :xenv (if delay2-flanger
                                   (coin 0.30 (rand) 0.0)
                                   (coin @config/p-delay2-time<-xenv* 
                                         (rand) 0.0))]
                    :amp [(int (+ @config/delay-min-amp* 
                                  (rand (- @config/delay-max-amp* 
                                           @config/delay-min-amp*))))
                          :lfo2 (coin (* 1/2 @config/p-delay2-amp<-lfo*) 
                                      (rand) 0.0)
                          :lfo3 (coin (* 1/2 @config/p-delay2-amp<-lfo*) 
                                      (rand) 0.0)
                          :xenv (coin @config/p-delay2-amp<-xenv*
                                      (+ 0.5 (rand 0.5)) 0.0)]
                    :pan [+0.7 
                          :lfo2 (coin p-pan-mod (rand) 0.0)
                          :lfo3 (coin p-pan-mod (rand) 0.0)
                          :xenv (coin p-pan-mod (rand -1.5) 0.0)]
                    :fb fb :xfb xfb))
     (prog/amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000))))
