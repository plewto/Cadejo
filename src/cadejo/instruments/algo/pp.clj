(ns cadejo.instruments.algo.pp
  ;(:require [cadejo.instruments.algo.program :as program])
  (:require [cadejo.util.col]))

(def pad1 "    ")
(def pad2 (str pad1 "      "))

(defn- summery [pnum pname dmap]
  (printf ";; ---------------------------------------------------- %03d %s"
          pnum pname)
  (println "\n;;")
  (printf ";; [op1 %5.4f%+3.0f  %4.2f %s]"
          (get dmap :op1-detune) 
          (get dmap :op1-bias)
          (get dmap :op1-amp)
          (if (zero? (get dmap :op1-enable)) "MUTE" ""))
  (printf "<--[op2 %5.4f%+3.0f  %4.2f %s]"
          (get dmap :op2-detune) 
          (get dmap :op2-bias)
          (get dmap :op2-amp)
          (if (zero? (get dmap :op2-enable)) "MUTE" ""))
  (printf "<--[op3 %5.4f%+3.0f  %4.2f %s]\n"
          (get dmap :op3-detune) 
          (get dmap :op3-bias)
          (get dmap :op3-amp)
          (if (zero? (get dmap :op3-enable)) "MUTE" ""))
  (printf ";; [op4 %5.4f%+3.0f  %4.2f %s]"
          (get dmap :op4-detune) 
          (get dmap :op4-bias)
          (get dmap :op4-amp)
          (if (zero? (get dmap :op4-enable)) "MUTE" ""))
  (printf "<--[op5 %5.4f%+3.0f  %4.2f %s]\n"
          (get dmap :op5-detune) 
          (get dmap :op5-bias)
          (get dmap :op5-amp)
          (if (zero? (get dmap :op5-enable)) "MUTE" ""))
  (print ";;                      ")
  (printf "<--[op6 %5.4f%+3.0f  %4.2f :fb %4.2f %s]\n"
          (get dmap :op6-detune) 
          (get dmap :op6-bias)
          (get dmap :op6-amp)
          (get dmap :op6-feedback)
          (if (zero? (get dmap :op6-enable)) "MUTE" ""))
  (printf ";; [op7 %5.4f%+3.0f  %4.2f %s]"
          (get dmap :op7-detune) 
          (get dmap :op7-bias)
          (get dmap :op7-amp)
          (if (zero? (get dmap :op7-enable)) "MUTE" ""))
  (printf "<--[op8 %5.4f%+3.0f  %4.2f :fb %4.2f %s]\n"
          (get dmap :op8-detune) 
          (get dmap :op8-bias)
          (get dmap :op8-amp)
          (get dmap :op8-feedback)
          (if (zero? (get dmap :op8-enable)) "MUTE" ""))
  (println))

(defn dump-program [data]
  (let [dmap (cadejo.util.col/alist->map data)]
    (println)
    (doseq [k (sort (keys dmap))]
      (printf "[%-12s] --> %s\n" k (get dmap k)))
    (println)))

(defn- fget [dmap param]
  (let [v (get dmap param)]
    (if (not v)
      (println (format "WARNING unassigned ALGO parameter %s" param)))
    (float v)))

(defn- iget [dmap param]
  (int (fget dmap param)))

(defn- str-common [dmap]
  (let [sb (StringBuilder.)
        pad3 (str pad2 "         ")
        app (fn [& args]
              (.append sb (apply #'format args)))]
    (app "(common  :amp %5.3f\n" (fget dmap :amp))
    (app "%s:port-time %4.2f\n" pad3 (fget dmap :port-time))
    (app "%s:lp  %d\n" pad3 (int (get dmap :lp)))
    (app "%s:cc-volume-depth %4.2f\n" pad3 (fget dmap :cc-volume-depth)) 
    (app "%s:env1->pitch %+7.4f \n" pad3 (fget dmap :env1->pitch))
    (app "%s:lfo1->pitch %+7.4f\n" pad3 (fget dmap :lfo1->pitch))
    (app "%s:lfo2->pitch %+7.4f)" pad3 (fget dmap :lfo2->pitch))
    (.toString sb)))
         
(defn- str-env [enum dmap]
  (let [sb (StringBuilder.)
        pad3 (str pad2 "          ")
        app (fn [& args]
              (.append sb (apply #'format args)))
        key (fn [param]
              (keyword (format "env%d-%s" enum param)))]
    (app "%s;A    D1    D2    R        BP    SUS\n" pad3)
    (app "%s(env%d     " pad2 enum)
    (app "%5.3f %5.3f %5.3f %5.3f    %5.3f %5.3f\n"
         (fget dmap (key "attack"))
         (fget dmap (key "decay1"))
         (fget dmap (key "decay2"))
         (fget dmap (key "release"))
         (fget dmap (key "breakpoint"))
         (fget dmap (key "sustain")))
    (app "%s:bias %+5.2f :scale %+5.2f)"
         pad3
         (fget dmap (key "bias"))
         (fget dmap (key "scale")))
    (.toString sb)))
         
(defn- str-vibrato [dmap]
  (let [sb (StringBuilder.)
        app (fn [& args]
              (.append sb (apply #'format args)))]
    (app "%s(vibrato :freq %4.2f   :depth %4.2f :delay %4.2f :sens %5.3f)"
         pad2
         (fget dmap :vfreq)
         (fget dmap :vdepth)
         (fget dmap :vdelay)
         (fget dmap :vsens))
    (.toString sb)))

(defn- str-lfo1 [dmap]
    (let [sb (StringBuilder.)
        pad3 (str pad2 "         ")
        app (fn [& args]
              (.append sb (apply #'format args)))]
      (app "%s(lfo1    :freq %5.3f  :cca->freq %5.3f  :ccb->freq %5.3f\n"
           pad2
           (fget dmap :lfo1-freq)
           (fget dmap :cca->lfo1-freq)
           (fget dmap :ccb->lfo1-freq))
      (app "%s:env1 %5.3f :pressure %5.3f :cca %5.3f :ccb %5.3f\n"
           pad3
           (fget dmap :env1->lfo1-amp)
           (fget dmap :pressure->lfo1-amp)
           (fget dmap :cca->lfo1-amp)
           (fget dmap :ccb->lfo1-amp))
      (app "%s:skew %4.2f  :env1->skew %+5.2f)"
           pad3
           (fget dmap :lfo1-skew)
           (fget dmap :env1->lfo1-skew))
      (.toString sb)))

(defn- str-lfo2 [dmap]
    (let [sb (StringBuilder.)
        pad3 (str pad2 "         ")
        app (fn [& args]
              (.append sb (apply #'format args)))]
      (app "%s(lfo2    :freq %5.3f  :cca->freq %5.3f  :ccb->freq %5.3f\n"
           pad2
           (fget dmap :lfo2-freq)
           (fget dmap :cca->lfo2-freq)
           (fget dmap :ccb->lfo2-freq))
      (app "%s::pressure %5.3f :lfo1 %5.3f :cca %5.3f :ccb %5.3f\n"
           pad3
           (fget dmap :pressure->lfo2-amp)
           (fget dmap :lfo1->lfo2-amp)
           (fget dmap :cca->lfo2-amp)
           (fget dmap :ccb->lfo2-amp))
      (app "%s:skew %4.2f  :lfo1->skew %+5.2f)"
           pad3
           (fget dmap :lfo2-skew)
           (fget dmap :lfo1->lfo2-skew))
      (.toString sb)))

(defn- str-carrier [op dmap]
  (let [sb (StringBuilder.)
        pad3 (str pad2 "     ")
        app (fn [& args]
              (.append sb (apply #'format args)))
        key (fn [param]
              (keyword (format "op%d-%s" op param)))]
    (app "%s(op%d (nth enable %d)\n" 
         pad2 op (dec op))
    (app "%s:amp %5.3f :detune %6.4f      :bias%+5.0f\n"
         pad3
         (fget dmap (key "amp"))
         (fget dmap (key "detune"))
         (fget dmap (key "bias")))
    (app "%s:addsr [%5.3f %5.3f %5.3f %5.3f  %5.3f %5.3f]\n"
         pad3
         (fget dmap (key "attack"))
         (fget dmap (key "decay1"))
         (fget dmap (key "decay2"))
         (fget dmap (key "release"))
         (fget dmap (key "breakpoint"))
         (fget dmap (key "sustain")))
    (app "%s:left-key %3d  :left-scale %+3.0f  :right-key %3d  :right-scale %+3.0f\n"
         pad3
         (iget dmap (key "left-key"))
         (fget dmap (key "left-scale"))
         (iget dmap (key "right-key"))
         (fget dmap (key "right-scale")))
    (app "%s:velocity %4.2f :pressure %4.2f :cca %4.2f  :ccb %4.2f\n"
         pad3
         (fget dmap (key "velocity"))
         (fget dmap (key "pressure"))
         (fget dmap (key "cca"))
         (fget dmap (key "ccb")))
    (app "%s:lfo1     %4.2f :lfo2     %4.2f)"
         pad3
         (fget dmap (key "lfo1"))
         (fget dmap (key "lfo2")))
    (.toString sb)))
         
(defn- has-hp? [op]
  (or (= op 2)
      (= op 6)
      (= op 8)))

(defn- has-feedback? [op]
  (or (= op 6)
      (= op 8)))

(defn- str-modulator [op dmap]
  (let [sb (StringBuilder.)
        pad3 (str pad2 "     ")
        app (fn [& args]
              (.append sb (apply #'format args)))
        key (fn [param]
              (keyword (format "op%d-%s" op param)))]
    (app "%s(op%d (nth enable %d)\n" pad2 op (dec op))
    (app "%s:amp %5.3f :detune %6.4f      :bias%+5.0f\n"
         pad3
         (fget dmap (key "amp"))
         (fget dmap (key "detune"))
         (fget dmap (key "bias")))
    (app "%s:addsr [%5.3f %5.3f %5.3f %5.3f  %5.3f %5.3f]\n"
         pad3
         (fget dmap (key "attack"))
         (fget dmap (key "decay1"))
         (fget dmap (key "decay2"))
         (fget dmap (key "release"))
         (fget dmap (key "breakpoint"))
         (fget dmap (key "sustain")))
    (app "%s        :env-bias  %+5.2f  :env-scale  %+5.2f\n"
         pad3
         (fget dmap (key "env-bias"))
         (fget dmap (key "env-scale")))
    (app "%s:left-key %3d  :left-scale %+3.0f  :right-key %3d  :right-scale %+3.0f\n"
         pad3
         (iget dmap (key "left-key"))
         (fget dmap (key "left-scale"))
         (iget dmap (key "right-key"))
         (fget dmap (key "right-scale")))
    (app "%s:velocity %4.2f :pressure %4.2f :cca %4.2f  :ccb %4.2f\n"
         pad3
         (fget dmap (key "velocity"))
         (fget dmap (key "pressure"))
         (fget dmap (key "cca"))
         (fget dmap (key "ccb")))
    (app "%s:lfo1     %4.2f :lfo2     %4.2f"
         pad3
         (fget dmap (key "lfo1"))
         (fget dmap (key "lfo2")))
    (if (has-hp? op)
      (app " :hp  %2d" (iget dmap (key "hp"))))
    (if (has-feedback? op)
      (do 
        (app "\n%s:fb      %+5.2f :env->fb %+5.2f :lfo%d->fb %5.2f\n"
             pad3
             (fget dmap (key "feedback"))
             (fget dmap (key "env->feedback"))
             (if (= op 6) 1 2)
             (fget dmap (if (= op 6) 
                          :op6-lfo1->feedback
                          :op8-lfo2->feedback)))
        (app "%s:cca->fb %5.2f :ccb->fb %+5.2f :pressure->fb %+5.2f)"
             pad3
             (fget dmap (key "cca->feedback"))
             (fget dmap (key "ccb->feedback"))
             (fget dmap (key "pressure->feedback"))))
      (app ")"))
    (.toString sb)))

(defn- str-efx [dmap]
  (let [sb (StringBuilder.)
        pad3 (str pad2 "         ")
        app (fn [& args]
              (.append sb (apply #'format args)))]
    (app "%s(echo    :delay-1 %5.3f    :fb %4.2f\n"
         pad2
         (fget dmap :echo-delay-1)
         (fget dmap :echo-fb))
    (app "%s:delay-2 %5.3f    :damp %4.2f   :mix %4.2f)\n"
         pad3
         (fget dmap :echo-delay-2)
         (fget dmap :echo-hf-damp)
         (fget dmap :echo-mix))
    (app "%s(reverb  :size %4.2f        :mix  %4.2f)"
         pad2
         (fget dmap :room-size)
         (fget dmap :reverb-mix))
    (.toString sb)))

(defn pp-algo 
  ([pnum pname data remarks]
     (with-out-str 
       (let [dmap (cadejo.util.col/alist->map data)
             mutefn (fn [op]
                      (let [param (keyword (format "op%d-enable" op))
                            val (get dmap param 1.0)]
                        (if (zero? val) "0" "1")))]
         (summery pnum pname dmap)
         (println (format "(let [enable %s %s %s   %s %s %s   %s %s]"
                          (mutefn 1)(mutefn 2)(mutefn 3)(mutefn 4)
                          (mutefn 5)(mutefn 6)(mutefn 7)(mutefn 8)))
         (printf "  (save-program %3s   \"%s\" \"%s\"\n"
                 pnum pname remarks)
         (print (format "%s(algo " pad1))
         (println (str-common dmap))
         (println (str-env 1 dmap))
         (println (str-vibrato dmap))
         (println (str-lfo1 dmap))
         (println (str-lfo2 dmap))
         (println (str-carrier 1 dmap))
         (println (str-modulator 2 dmap))
         (println (str-modulator 3 dmap))
         (println)
         (println (str-carrier 4 dmap))
         (println (str-modulator 5 dmap))
         (println (str-modulator 6 dmap))
         (println)
         (println (str-carrier 7 dmap))
         (println (str-modulator 8 dmap))
         (print (str-efx dmap))
         (println ")))")))))
