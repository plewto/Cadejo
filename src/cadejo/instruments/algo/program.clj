(println "\t--> ALGO program")

(ns cadejo.instruments.algo.program
  (:require [cadejo.midi.program])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg]))

(defonce bank (cadejo.midi.program/bank :ALGO "Default Bank"))

(defn save-program 
  ([pnum name remarks data]
     (.set-program! bank pnum name remarks data))
  ([pnum name data]
     (save-program pnum name "" data)))

(defn third [col]
  (nth col 2))

(defn fourth [col]
  (nth col 3))

(defn fifth [col]
  (nth col 4))

(defn sixth [col]
  (nth col 5))

(def algo-parameters 
  '[:port-time :env1->pitch :lfo1->pitch :lfo2->pitch :lp
    :env1-attack :env1-breakpoint :env1-decay1 :env1-decay2 :env1-sustain :env1-release :env1-bias
    :env1-scale    :vfreq :vsens :vdepth :vdelay :lfo1-freq
    :lfo1-skew :env1->lfo1-skew :env1->lfo1-amp :pressure->lfo1-amp
    :lfo2-freq :lfo2-skew :lfo1->lfo2-skew    :lfo1->lfo2-amp :pressure->lfo2-amp  
    :op1-detune :op1-bias :op1-amp
    :op1-attack :op1-attack :op1-breakpoint :op1-decay1 :op1-decay2 :op1-sustain :op1-release :op1-velocity
    :op1-pressure :op1-cca :op1-ccb :op1-lfo1 :op1-lfo2  :op2-detune
    :op2-bias :op2-amp :op2-attack :op2-breakpoint :op2-decay1 :op2-decay2 :op2-sustain :op2-release
    :op2-env-bias :op2-env-scale :op2-velocity :op2-pressure :op2-cca
    :op2-ccb :op2-lfo1 :op2-lfo2 :op2-hp :op3-detune :op3-bias :op3-amp
    :op3-attack :op3-breakpoint :op3-decay1 :op3-decay2 :op3-sustain :op3-release :op3-env-bias
    :op3-env-scale :op3-velocity :op3-pressure :op3-cca :op3-ccb :op3-lfo1
    :op3-lfo2 :op4-detune :op4-bias :op4-amp :op4-attack :op4-breakpoint :op4-decay1 :op4-decay2
    :op4-sustain :op4-release :op4-velocity :op4-pressure :op4-cca
    :op4-ccb :op4-lfo1 :op4-lfo2 :op5-detune :op5-bias :op5-amp
    :op5-attack :op5-breakpoint :op5-decay1 :op5-decay2 :op5-sustain :op5-release :op5-env-bias
    :op5-env-scale :op5-velocity :op5-pressure :op5-cca :op5-ccb :op5-lfo1
    :op5-lfo2 :op6-detune :op6-bias :op6-amp :op6-attack :op6-breakpoint :op6-decay1 :op6-decay2
    :op6-sustain :op6-release :op6-env-bias :op6-env-scale :op6-velocity
    :op6-pressure :op6-cca :op6-ccb :op6-lfo1 :op6-lfo2 :op6-hp :op6-feedback
    :op6-env->feedback :op6-lfo1->feedback :op6-pressure->feedback
    :op6-cca->feedback :op6-ccb->feedback :op7-detune :op7-bias :op7-amp
    :op7-attack :op7-breakpoint :op7-decay1 :op7-decay2 :op7-sustain :op7-release :op7-velocity
    :op7-pressure :op7-cca :op7-ccb :op7-lfo1 :op7-lfo2  :op8-detune
    :op8-bias :op8-amp :op8-attack :op8-breakpoint :op8-decay1 :op8-decay2 :op8-sustain :op8-release
    :op8-env-bias :op8-env-scale :op8-velocity :op8-pressure :op8-cca
    :op8-ccb :op8-lfo1 :op8-lfo2 :op8-hp :op8-feedback :op8-env->feedback
    :op8-lfo2->feedback :op8-pressure->feedback :op8-cca->feedback
    :op8-ccb->feedback :echo-delay-1 :echo-delay-2 :echo-fb :echo-hf-damp
    :echo-mix :room-size :reverb-mix :amp :cc-volume-depth
    :op1-left-key :op1-left-scale :op1-right-key :op1-right-scale
    :op2-left-key :op2-left-scale :op2-right-key :op2-right-scale
    :op3-left-key :op3-left-scale :op3-right-key :op3-right-scale
    :op4-left-key :op4-left-scale :op4-right-key :op4-right-scale
    :op5-left-key :op5-left-scale :op5-right-key :op5-right-scale
    :op6-left-key :op6-left-scale :op6-right-key :op6-right-scale
    :op7-left-key :op7-left-scale :op7-right-key :op7-right-scale
    :op8-left-key :op8-left-scale :op8-right-key :op8-right-scale
    :cca->lfo1-amp :ccb->lfo1-amp :cca->lfo1-freq :ccb->lfo1-freq
    :cca->lfo2-amp :ccb->lfo2-amp :cca->lfo2-freq :ccb->lfo2-freq])

;; Predicate returns true iff param is a valid algo parameter
;;
(defn- valid-parameter? [param]
  (ucol/member? param algo-parameters))

;; Returns list of missing parameters from data (alist)
;;
(defn- missing-parameters [data]
  (let [acc* (atom [])]
    (doseq [p algo-parameters]
      (if (ucol/not-member? p data)
        (swap! acc* (fn [n](conj n p)))))
    @acc*))

(defn- format-param [prefix param]
  (let [kw (keyword (format "%s-%s" prefix param))]
    (if (not (valid-parameter? kw))
      (umsg/warning (format "Invalid ALGO parameter %s" kw)))
    kw))
  
(defn- format-op [op param]
  (let [kw (keyword (format "op%d-%s" op param))]
    (if (not (valid-parameter? kw))
      (umsg/warning (format "Invalid ALGO parameter %s" kw)))
    kw)) 


;; env1
;;
(defn- env [n attack decay1 decay2 release breakpoint sustain bias scale]
  (let [prefix (format "env%d" n)]
    (list (format-param prefix "attack")(float attack)
          (format-param prefix "decay1")(float decay1)
          (format-param prefix "decay2")(float decay2)
          (format-param prefix "release")(float release)
          (format-param prefix "breakpoint")(float breakpoint)
          (format-param prefix "sustain")(float sustain)
          (format-param prefix "bias")(float bias)
          (format-param prefix "scale")(float scale))))

(defn env1 [attack decay1 decay2 release breakpoint sustain  
            & {:keys [bias scale]:or {bias 0 scale 1}}]
  (env 1 attack decay1 decay2 release breakpoint sustain  bias scale))

(defn vibrato [& {:keys [freq depth sens delay]
                  :or {freq 7
                       depth 0.0
                       sens 0.03
                       delay 0}}]
  (list :vfreq (float freq)
        :vsens (float sens)
        :vdepth (float depth)
        :vdelay (float delay)))

(defn lfo1 [& {:keys [freq skew env1->skew env1 pressure cca ccb cca->freq ccb->freq]
               :or {freq 3.500
                    cca->freq 0
                    ccb->freq 0
                    skew 0.5
                    env1->skew 0.0
                    env1 0.0
                    cca 0
                    ccb 0
                    pressure 0.0}}]
  (list :lfo1-freq (float freq)
        :cca->lfo1-freq (float cca->freq)
        :ccb->lfo1-freq (float ccb->freq)
        :lfo1-skew (float skew)
        :env1->lfo1-skew (float env1->skew)
        :env1->lfo1-amp (float env1)
        :cca->lfo1-amp (float cca)
        :ccb->lfo1-amp (float ccb)
        :pressure->lfo1-amp (float pressure)))

(defn lfo2 [& {:keys [freq skew lfo1->skew lfo1 pressure cca ccb cca->freq ccb->freq]
               :or {freq 1.00
                    cca->freq 0
                    ccb->freq 0
                    skew 0.5
                    lfo1->skew 0
                    lfo1 0
                    cca 0
                    ccb 0
                    pressure 0}}]
  (list :lfo2-freq (float freq)
        :cca->lfo2-freq (float cca->freq)
        :ccb->lfo2-freq (float ccb->freq)
        :lfo2-skew (float skew)
        :lfo1->lfo2-skew (float lfo1->skew)
        :lfo1->lfo2-amp (float lfo1)
        :cca->lfo2-amp (float cca)
        :ccb->lfo2-amp (float ccb)
        :pressure->lfo2-amp (float pressure)))

          
(defn echo [& {:keys [delay-1 delay-2 fb damp mix]
               :or {lp 10000
                    delay-1 0.25
                    delay-2 0.50
                    fb 0.5
                    damp 0
                    mix 0}}]
  (list 
   :echo-delay-1 (float delay-1)
   :echo-delay-2 (float delay-2)
   :echo-fb (float fb)
   :echo-hf-damp (float damp)
   :echo-mix (float mix)))

(defn reverb [& {:keys [size mix]
                 :or {size 0.5
                      mix 0.0}}]
  (list :room-size (float size)
        :reverb-mix (float mix)))

(defn common [& {:keys [lp port-time env1->pitch 
                        lfo1->pitch lfo2->pitch
                        amp cc-volume-depth]
                 :or {lp 10000
                      port-time 0
                      env1->pitch 0
                      lfo1->pitch 0
                      lfo2->pitch 0
                      amp 0.2
                      cc-volume-depth 0}}]
  (list :lp (int lp)
        :port-time (float port-time)
        :env1->pitch (float env1->pitch)
        :lfo1->pitch (float lfo1->pitch)
        :lfo2->pitch (float lfo2->pitch)
        :amp (float amp)
        :cc-volume-depth cc-volume-depth))

(defn- mute-op? [flag]
  (cond (= flag :mute) 0
        (= flag 0) 0
        (not flag) 0
        :default 1))

(defn- carrier [op enable detune bias amp 
               left-key left-scale right-key right-scale
               velocity pressure addsr cca ccb lfo1 lfo2]
  (let [mute-amp (mute-op? enable)]
    (list 
     (format-op op "detune")(float detune)
     (format-op op "bias")(float bias)
     (format-op op "amp")(float (* mute-amp amp))
     (format-op op "left-key")(int left-key)
     (format-op op "left-scale")(float left-scale)
     (format-op op "right-key")(int right-key)
     (format-op op "right-scale")(float right-scale)
     (format-op op "attack")(float (first addsr))
     (format-op op "decay1")(float (second addsr))
     (format-op op "decay2")(float (third addsr))
     (format-op op "release")(float (fourth addsr))
     (format-op op "breakpoint")(float (fifth addsr))
     (format-op op "sustain")(float (sixth addsr))
     (format-op op "velocity")(float velocity)
     (format-op op "pressure")(float pressure)
     (format-op op "cca")(float cca)
     (format-op op "ccb")(float ccb)
     (format-op op "lfo1")(float lfo1)
     (format-op op "lfo2")(float lfo2))))

  
(defn op1 [enable & {:keys [detune bias amp 
                            left-key left-scale
                            right-key right-scale
                            addsr velocity pressure cca ccb lfo1 lfo2]
              :or {detune 1.000
                   bias 0
                   amp 1
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0}}]
  (carrier 1 enable detune bias amp
           left-key left-scale right-key right-scale
           velocity pressure addsr cca ccb lfo1 lfo2))

(defn op4 [enable & {:keys [detune bias amp 
                            left-key left-scale
                            right-key right-scale
                            addsr velocity pressure cca ccb lfo1 lfo2]
              :or {detune 1.000
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0
                   pan 0}}]
  (carrier 4 enable detune bias amp
           left-key left-scale
           right-key right-scale
           velocity pressure addsr cca ccb lfo1 lfo2))

(defn op7 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr velocity pressure cca ccb lfo1 lfo2]
              :or {detune 1.000
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0
                   pan 0}}]
  (carrier 7 enable detune bias amp
           left-key left-scale
           right-key right-scale
           velocity pressure addsr cca ccb lfo1 lfo2))



(defn- simple-modulator [op enable detune bias amp
                        left-key left-scale
                        right-key right-scale
                        addsr env-bias env-scale
                        velocity pressure cca ccb lfo1 lfo2 hp]
  (let [mute-amp (mute-op? enable)
        rs (list (format-op op "detune")(float detune)
                 (format-op op "bias")(float bias)
                 (format-op op "amp")(float (* mute-amp amp))
                 (format-op op "left-key")(int left-key)
                 (format-op op "left-scale")(float left-scale)
                 (format-op op "right-key")(int right-key)
                 (format-op op "right-scale")(float right-scale)
                 (format-op op "attack")(float (first addsr))
                 (format-op op "decay1")(float (second addsr))
                 (format-op op "decay2")(float (third addsr))
                 (format-op op "release")(float (fourth addsr))
                 (format-op op "breakpoint")(float (fifth addsr))
                 (format-op op "sustain")(float (sixth addsr))
                 (format-op op "env-bias")(float env-bias)
                 (format-op op "env-scale")(float env-scale)
                 (format-op op "velocity")(float velocity)
                 (format-op op "pressure")(float pressure)
                 (format-op op "cca")(float cca)
                 (format-op op "ccb")(float ccb)
                 (format-op op "lfo1")(float lfo1)
                 (format-op op "lfo2")(float lfo2))]
    (if (or (= op 2)(= op 6)(= op 8))
      (concat rs (list (format-op op "hp")(int hp)))
      rs)))

(defn op2 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2 hp]
              :or {detune 1
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.000]
                   env-bias 0
                   env-scale 1
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0
                   hp 50}}]
  (simple-modulator 2 enable detune bias amp
                    left-key left-scale
                    right-key right-scale
                    addsr env-bias env-scale
                    velocity pressure cca ccb lfo1 lfo2 hp))

(defn op3 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2]
              :or {detune 1
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   env-bias 0
                   env-scale 1
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0}}]
  (simple-modulator 3 enable detune bias amp
                    left-key left-scale
                    right-key right-scale
                    addsr env-bias env-scale
                    velocity pressure cca ccb lfo1 lfo2 nil))

(defn op5 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2]
              :or {detune 1
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   env-bias 0
                   env-scale 1
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0}}]
  (simple-modulator 5 enable detune bias amp
                    left-key left-scale
                    right-key right-scale
                    addsr env-bias env-scale
                    velocity pressure cca ccb lfo1 lfo2 nil))




(defn- complex-modulator [op enable detune bias amp
                         left-key left-scale
                         right-key right-scale
                         addsr env-bias env-scale
                         velocity pressure cca ccb lfo1 lfo2 hp
                         & {:keys [fb env-fb lfo-fb pressure-fb cca-fb ccb-fb]
                            :or {fb 0
                                 env-fb 0
                                 lfo-fb 0
                                 pressure-fb 0
                                 cca-fb 0
                                 ccb-fb 0}}]
  (concat (simple-modulator op enable detune bias amp 
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2 hp)
          (list (format-op op "feedback")(float fb)
                (format-op op "env->feedback")(float env-fb)
                (format-op op (if (= op 6) "lfo1->feedback" "lfo2->feedback"))(float lfo-fb)
                (format-op op "pressure->feedback")(float pressure-fb)
                (format-op op "cca->feedback")(float cca-fb)
                (format-op op "ccb->feedback")(float ccb-fb))))

(defn op6 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2 hp
                            fb env->fb lfo1->fb pressure->fb cca->fb ccb->fb]
              :or {detune 1
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   env-bias 0
                   env-scale 1
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0
                   hp 50
                   fb 0
                   env->fb 0
                   lfo1->fb 0
                   pressure->fb 0
                   cca->fb 0
                   ccb->fb 0}}]
  (complex-modulator 6 enable detune bias amp
                     left-key left-scale
                     right-key right-scale
                     addsr env-bias env-scale
                     velocity pressure cca ccb lfo1 lfo2 hp
                     :fb fb
                     :env-fb env->fb
                     :lfo-fb lfo1->fb 
                     :pressure-fb pressure->fb
                     :cca-fb cca->fb
                     :ccb-fb ccb->fb))


(defn op8 [enable & {:keys [detune bias amp
                            left-key left-scale
                            right-key right-scale
                            addsr env-bias env-scale
                            velocity pressure cca ccb lfo1 lfo2 hp
                            fb env->fb lfo2->fb pressure->fb cca->fb ccb->fb]
              :or {detune 1
                   bias 0
                   amp 0
                   left-key 60
                   left-scale 0
                   right-key 60
                   right-scale 0
                   addsr [0.001 0.100 0.100 0.001  1.00 1.00]
                   env-bias 0
                   env-scale 1
                   velocity 0
                   pressure 0
                   cca 0
                   ccb 0
                   lfo1 0
                   lfo2 0
                   hp 50
                   fb 0
                   env->fb 0
                   lfo2->fb 0
                   pressure->fb 0
                   cca->fb 0
                   ccb->fb 0}}]
  (complex-modulator 8 enable detune bias amp
                     left-key left-scale
                     right-key right-scale
                     addsr env-bias env-scale
                     velocity pressure cca ccb lfo1 lfo2 hp
                     :fb fb
                     :env-fb env->fb
                     :lfo-fb lfo2->fb 
                     :pressure-fb pressure->fb
                     :cca-fb cca->fb
                     :ccb-fb ccb->fb))

                     
(defn algo [& args]
  (let [data (flatten args)
        params (keys (ucol/alist->map data))]
    (doseq [p params]
      (if (ucol/not-member? p algo-parameters)
        (umsg/warning (format "Invalid ALGO parameter %s" p))))
    (doseq [p algo-parameters]
      (if (ucol/not-member? p params)
        (umsg/warning (format "Missing ALGO parameter %s" p))))
    data))
                            

(def default-program 
  (algo 
   (common)
   (env1 0.00 0.10 0.10 0.00  1.00 1.00 :bias 0 :scale 1)
   (vibrato)
   (lfo1)
   (lfo2)
   (op1 true)(op2 true)(op3 true)
   (op4 true)(op5 true)(op6 true)
   (op7 true)(op8 true)
   (echo)
   (reverb)))
   
