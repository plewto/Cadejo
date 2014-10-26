(ns cadejo.instruments.algo.genpatch
  (:use [cadejo.instruments.algo.program])
  (:require [cadejo.util.math :as math]))

(def op-count 8)

(defn is-carrier? [op]
  (or (= op 1)(= op 4)(= op 7)))

(defn is-modulator? [op]
  (not (is-carrier? op)))

(defn has-feedback? [op]
  (or (= op 6)(= op 8)))

(defn has-hp-filter? [op]
  (or (has-feedback? op)
      (= op 2)))

(def harmonic-gamut '[0.50 0.50 0.75                        ; 0.50   7.4%
                      1.00 1.00 1.00 1.00                   ; 0.75   3.7%
                      1.00 1.00 1.00 1.00                   ; 1.00  44.4%
                      1.00 1.00 1.00 1.00                   ; 1.50   3.7%
                      1.50                                  ; 2.00  14.8%
                      2.00 2.00 2.00 2.00                   ; 3.00   7.4%
                      3.00 3.00 4.00 5.00                   ; 4,5,6,7,8 3.7% each
                      6.00 7.00 8.00])

(def semi-harmonic-gamut 
  (let [acc* (atom [])]
    (dotimes [i 8]
      (doseq [j '(0 1/8 1/5 1/4 1/3 1/2 5/8 2/3 3/4 7/8)]
        (let [v (+ i j)]
          (if (>= v 1/2)(swap! acc* (fn [n](conj n v)))))))
    @acc*))


(defn pick-harmonic-op-frequencies [& {:keys [chorus]
                                       :or {chorus 0.012}}]
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (swap! acc* (fn [n](conj n (math/approx (rand-nth harmonic-gamut)
                                              chorus)))))
    @acc*))

(defn pick-semi-harmonic-op-frequencies [& {:keys [p-harmonic chorus]
                                            :or {p-harmonic 0.75
                                                 chorus 0.012}}]
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (let [f (rand-nth (math/coin p-harmonic 
                                   harmonic-gamut 
                                   semi-harmonic-gamut))]
        (swap! acc* (fn [n](conj n (math/approx f chorus))))))
    @acc*))

;; A 0 value indicates op bias should be used.
;;
(defn pick-enharmonic-op-frequencies []
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (let [f (math/coin 0.05 0 (+ 0.25 (rand 8)))]
        (swap! acc* (fn [n] (conj n f)))))
    @acc*))


(defn pick-op-frequencies []
  (let [gamut (rand-nth '[:harmonic :harmonic :harmonic :harmonic 
                           :harmonic :harmonic :harmonic :harmonic 
                           :semi-harmonic  :semi-harmonic :semi-harmonic
                           :enharmonic])]
    (println (format ";; frequency gamut %s" gamut))
    (cond (= gamut :harmonic)(pick-harmonic-op-frequencies)
          (= gamut :semi-harmonic)(pick-semi-harmonic-op-frequencies)
          :default (pick-enharmonic-op-frequencies))))

; Picks random op bias iff corresponding detune is 0
;;
(defn pick-op-bias [frequencies]
  (let [acc* (atom [])]
    (doseq [f frequencies]
      (swap! acc* (fn [n](conj n (if (= f 0)(+ 100 (rand 1000)) 0)))))
    @acc*))


;; Return a list of op amplitudes
;; At least one carrier amp should have amplitude 1.0
;;
(defn pick-op-amplitudes []
  (let [acc* (atom [])
        main-op (rand-nth '(1 4 7))
        mod-amp-range (math/coin 0.5 :low :high)]
    (println (format ";; amps main %d mod-op-range %s" main-op mod-amp-range))
    (dotimes [i op-count]
      (let [op (inc i)]
        (swap! acc* (fn [n](conj n (cond (= op main-op) 1.0
                                         (is-carrier? op) (+ 0.1 (rand 0.9))
                                         (= range :low)
                                         (math/coin 0.80 (rand 2)(rand 4))
                                         :default
                                         (math/coin 0.80 (inc (rand 6))(rand 2))))))))
    @acc*))
  
(defn addsr-env [max-time]
  (list (rand max-time)
        (rand max-time)
        (rand max-time)
        (rand max-time)
        (+ 0.20 (rand 0.80))
        (+ 0.20 (rand 0.80))))

(defn adsr-env [max-time]
  (let [s (+ 0.20 (rand 0.80))]
    (list (rand max-time)
          (rand max-time)
          0
          (rand max-time)
          s s)))

(defn asr-env [max-time]
  (list (rand max-time)
        0 0 
        (rand max-time)
        1 1))

(defn percussive-env [max-time]
  (list 0
        (rand max-time)
        (rand max-time)
        (rand max-time)
        (+ 0.5 (rand 0.5))
        (math/coin 0.75 0 (rand 0.2))))

(defn mostly-adsr-env [max-time]
  (math/coin 0.80 
             (math/coin 0.5 
                        (addsr-env max-time)
                        (adsr-env max-time))
             (math/coin 0.75 
                        (asr-env max-time)
                        (percussive-env max-time))))

(defn mostly-asr-env [max-time]
  (math/coin 0.80 
             (asr-env max-time)
             (math/coin 0.75 
                        (adsr-env max-time)
                        (percussive-env max-time))))

(defn mostly-percussive-env [max-time]
  (math/coin 0.80 
             (percussive-env max-time)
             (adsr-env max-time)))

;; Returns list of envelope parameters for each operator.
(defn pick-op-envelopes []
  (let [range (math/coin 0.5 :slow :fast)
        hint (rand-nth '[:adsr :asr :perc])
        max-time (if (= range :slow) 6 0.5)
        acc* (atom [])]
    (println (format ";; env hint %s range %s max-time %s"
                     hint range max-time))
    (dotimes [i op-count]
      (let [op (inc i)
            env-args*  (atom (cond (= hint :adsr)(mostly-adsr-env max-time)
                                   (= hint :asr)(mostly-asr-env max-time)
                                   :default (mostly-percussive-env max-time)))]
        (swap! acc* (fn [n](conj n @env-args*)))))
    @acc*))
 
(defn pick-op-velocities []
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (let [op (inc i)]
        (swap! acc* (fn [n](conj n (if (is-carrier? op)
                                     (math/coin 0.75 0.0 (+ 0.25 (rand 0.75)))
                                     (math/coin 0.50 0.0 (rand))))))))
    @acc*))
           
(defn pick-op-pressures []
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (let [op (inc i)]
        (swap! acc* (fn [n](conj n (if (is-carrier? op)
                                     (math/coin 0.80 0.0 (+ 0.25 (rand 0.75)))
                                     (math/coin 0.75 0.0 (rand))))))))
    @acc*))
        
(defn pick-op-cca-depths []
  '[0.0 0.0 0.0 0.0
    0.0 0.0 0.0 0.0])

(defn pick-op-ccb-depths []
  (pick-op-cca-depths))
        
(defn pick-op-lfo-depths []
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (swap! acc* (fn [n](conj n (math/coin 0.05 (rand) 0)))))
    @acc*))

(defn pick-op-hp-cutoff []
  (let [acc* (atom [])]
    (dotimes [i op-count]
      (let [op (inc i)]
        (swap! acc* (fn [n](conj n (if (has-hp-filter? op)
                                     (int (math/coin 0.03 1 (+ 1 (rand 50))))
                                     nil))))))
    @acc*))


(defn pick-op-feedback [op]
  (let [hint (math/coin 0.1 :rude :mild)]
    (println (format ";; op %d feddback hint %s" op hint))
    (if (= hint :mild)
           {:fb (math/coin 0.5 0 (rand))
            :env (math/coin 0.03 (rand) 0)
            :lfo (math/coin 0.03 (rand) 0)
            :pressure (math/coin 0.03 (rand) 0)
            :cca (math/coin 0.03 (rand) 0)
            :ccb (math/coin 0.03 (rand) 0)}
           {:fb (math/coin 0.75 (rand 2)(rand 4))
            :env (math/coin 0.75 (rand 2)(rand 10))
            :lfo (math/coin 0.80 (rand 1)(rand 10))
            :pressure (math/coin 0.05 (rand 10) 0 )
            :cca (math/coin 0.01 (rand) 0)
            :ccb (math/coin 0.01 (rand) 0)})))
    
(defn random-algo-program [& args]
  (let [op-frequencies (pick-op-frequencies)
        op-biases (pick-op-bias op-frequencies)
        op-amps (pick-op-amplitudes)
        op-envs (pick-op-envelopes)
        op-velocities (pick-op-velocities)
        op-cca-depths (pick-op-cca-depths)
        op-ccb-depths (pick-op-ccb-depths)
        op-lfo1-depths (pick-op-lfo-depths)
        op-lfo2-depths (pick-op-lfo-depths)
        op6-feedback (pick-op-feedback 6)
        op8-feedback (pick-op-feedback 8)
        time-base (+ 1 (rand))
        period (/ time-base)
        vibrato-freq (math/coin 0.75 
                                (* time-base (rand-nth '[2 3 4 5 6 7 8]))
                                (+ 3 (rand 5)))
        lfo1-freq (math/coin 0.75 
                             (* time-base (rand-nth '[0.25 0.333 0.5 0.667 0.75
                                                      1 1.5 2 2.5 3 4 5 6 7]))
                             (rand 4))
        lfo2-freq (math/coin 0.75 
                             (* lfo1-freq (rand-nth '[0.25 0.333 0.5 0.667 0.75
                                                      1.5 2 2.5 3 4 5 6 7]))
                             (rand 4))
        delay1 (math/coin 0.75
                          (* period (rand-nth '[0.5 
                                                1 1.333 1.5 1.667 1.75 
                                                2 2.25 2.5 2.75
                                                3 4 5 6]))
                          (+ 0.25 (rand 3)))
        delay2 (math/coin 0.75
                          (* delay1 (rand-nth '[0.5 0.667 0.75 
                                                1.50 2 2.5 3 4 5]))
                          (+ 0.25 (rand 3)))
        use-echo (math/coin 0.33 true false)]
    (println (format ";; time base %5.3f" time-base))
    (let [rs (algo 
              (op1 true :detune (nth op-frequencies 0) 
                   :bias (nth op-biases 0)
                   :addsr (nth op-envs 0) 
                   :amp (nth op-amps 0)
                   :velocity (nth op-velocities 0) 
                   :pressure 0
                   :cca (nth op-cca-depths 0) 
                   :ccb (nth op-ccb-depths 0)
                   :lfo1 (nth op-lfo1-depths 0) 
                   :lfo2 (nth op-lfo2-depths 0))
              (op2 true :detune (nth op-frequencies 1) :bias (nth op-biases 1)
                   :addsr (nth op-envs 1) :amp (nth op-amps 1)
                   :velocity (nth op-velocities 1) :pressure 0
                   :cca (nth op-cca-depths 1) :ccb (nth op-ccb-depths 1)
                   :lfo1 (nth op-lfo1-depths 1) :lfo2 (nth op-lfo2-depths 1)
                   :hp 50)
              (op3 true :detune (nth op-frequencies 2) :bias (nth op-biases 2)
                   :addsr (nth op-envs 2) :amp (nth op-amps 2)
                   :velocity (nth op-velocities 2) :pressure 0
                   :cca (nth op-cca-depths 2) :ccb (nth op-ccb-depths 2)
                   :lfo1 (nth op-lfo1-depths 2) :lfo2 (nth op-lfo2-depths 2))
              (op4 true :detune (nth op-frequencies 3) :bias (nth op-biases 3)
                   :addsr (nth op-envs 3) :amp (nth op-amps 3)
                   :velocity (nth op-velocities 3) :pressure 0
                   :cca (nth op-cca-depths 3) :ccb (nth op-ccb-depths 3)
                   :lfo1 (nth op-lfo1-depths 3) :lfo2 (nth op-lfo2-depths 3))
              (op5 true :detune (nth op-frequencies 4) :bias (nth op-biases 4)
                   :addsr (nth op-envs 4) :amp (nth op-amps 4)
                   :velocity (nth op-velocities 4) :pressure 0
                   :cca (nth op-cca-depths 4) :ccb (nth op-ccb-depths 4)
                   :lfo1 (nth op-lfo1-depths 4) :lfo2 (nth op-lfo2-depths 4))
              (op6 true :detune (nth op-frequencies 5) :bias (nth op-biases 5)
                   :addsr (nth op-envs 5) :amp (nth op-amps 5)
                   :velocity (nth op-velocities 5) :pressure 0
                   :cca (nth op-cca-depths 5) :ccb (nth op-ccb-depths 5)
                   :lfo1 (nth op-lfo1-depths 5) :lfo2 (nth op-lfo2-depths 5)
                   :hp 50
                   :fb (get op6-feedback :fb)
                   :env->fb (get op6-feedback :env)
                   :lfo1->fb (get op6-feedback :lfo)
                   :pressure->fb (get op6-feedback :pressure)
                   :cca->fb (get op6-feedback :cca)
                   :ccb->fb (get op6-feedback :ccb))
              (op7 true :detune (nth op-frequencies 6) :bias (nth op-biases 6)
                   :addsr (nth op-envs 6) :amp (nth op-amps 6)
                   :velocity (nth op-velocities 6) :pressure 0
                   :cca (nth op-cca-depths 6) :ccb (nth op-ccb-depths 6)
                   :lfo1 (nth op-lfo1-depths 6) :lfo2 (nth op-lfo2-depths 6))
              (op8 true :detune (nth op-frequencies 7) :bias (nth op-biases 7)
                   :addsr (nth op-envs 7) :amp (nth op-amps 7)
                   :velocity (nth op-velocities 7) :pressure 0
                   :cca (nth op-cca-depths 7) :ccb (nth op-ccb-depths 7)
                   :lfo1 (nth op-lfo1-depths 7) :lfo2 (nth op-lfo2-depths 7)
                   :hp 50
                   :fb (get op8-feedback :fb)
                   :env->fb (get op8-feedback :env)
                   :lfo1->fb (get op8-feedback :lfo)
                   :pressure->fb (get op8-feedback :pressure)
                   :cca->fb (get op8-feedback :cca)
                   :ccb->fb (get op8-feedback :ccb))
              (common :port-time (math/coin 0.01 (rand) 0)
                      :lp 16000
                      :env1->pitch (* (math/coin 0.5 -1 +1)(math/coin 0.01 (rand) 0))
                      :lfo1->pitch (math/coin 0.01 (rand) 0)
                      :lfo2->pitch (math/coin 0.01 (rand) 0))
              (env1 (rand 2)(rand 2)(rand 2)(rand 2)(rand)(rand) :bias 0 :scale 1)
              (vibrato :freq vibrato-freq :depth (math/coin 0.01 (rand) 0)
                       :delay (rand) :sens 0.03)
              (lfo1 :freq lfo1-freq 
                    :env1 (math/coin 0.05 (rand) 0)
                    :pressure (math/coin 0.05 (rand) 0)
                    :skew (math/coin 0.75 0.5 (rand))
                    :env1->skew (math/coin 0.10 (rand) 0))
              (lfo2 :freq lfo2-freq
                    :pressure (math/coin 0.03 (rand) 0)
                    :lfo1 (math/coin 0.03 (rand) 0)
                    :skew (math/coin 0.10 (rand) 0.5)
                    :lfo1->skew (math/coin 0.05 (rand) 0))
              (echo :delay-1 delay1 
                    :delay-2 delay2 
                    :fb (if use-echo (math/coin 0.75 (rand 0.5)(rand)) 0)
                    :damp (rand)
                    :mix (if use-echo (rand) 0))
              (reverb :size (rand)
                      :mix (math/coin 0.33 (rand) 0)))]
      rs)))
