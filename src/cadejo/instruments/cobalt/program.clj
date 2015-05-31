(println "-->    cobalt program")

(ns cadejo.instruments.cobalt.program
  (:require [cadejo.midi.pbank])
  (:require [cadejo.midi.program])
  (:require [cadejo.util.col :as col])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.constants :as con])
)

(defonce bank (cadejo.midi.pbank/pbank :cobalt))

(defn- clamp [a mn mx]
  (float (math/clamp a mn mx)))

(defn- uclamp [n] (float (clamp n 0 1)))

(defn- sclamp [n] (float (clamp n -1 1)))

(defn- continuity-test [dkeys]
   (let [rs* (atom ())]
    (doseq [p con/cobalt-parameters]
      (if (col/not-member? p dkeys)
        (swap! rs* (fn [n](cons p n)))))
    @rs*))

(defn- spurious-key-test [dkeys]
  (let [rs* (atom ())]
    (doseq [k dkeys]
      (if (col/not-member? k con/cobalt-parameters)
        (swap! rs* (fn [n](cons k n)))))
    @rs*))

(defn- integrity-test [data]
  (let [dkeys (col/alist->keys data)
        missing (continuity-test dkeys)
        extra (spurious-key-test dkeys)]
    (if (> (count missing) 0)
      (umsg/warning "Missing keys from Cobalt data" missing))
    (if (> (count extra) 0)
      (umsg/warning "Extra keys in Cobalt data" extra))))

(defn save-program 
  ([slot pname remarks data]
   (println (format "DEBUG savre-program slot %s  name %s" slot pname))
   (let [prog (cadejo.midi.program/program pname remarks (col/alist->map data))]
     (integrity-test data)
     (.store! bank slot prog)))
  ([slot pname data]
   (save-program slot pname "" data)))



;; id one of integer 1,2,3,...,8 or
;;    keyword :bzz, :nse, or :xenv
;;
;; alist assoc list [:att a :dcy1 d1 :dcy2 d2 :rel r :peak p :bp b :sus s]
;;       missing assignments take default values.
;;
(defn envn [id alst]
  (let [pmap (col/alist->map alst)
        att (float (max 0 (get pmap :att 0.00)))
        dcy1 (float (max 0 (get pmap :dcy1 0.00)))
        dcy2 (float (max 0 (get pmap :dcy2 0.00)))
        rel (float (max 0 (get pmap :rel 0.00)))
        pk (sclamp (get pmap :peak 1.00))
        bp (sclamp (get pmap :bp 1.00))
        sus (sclamp (get pmap :sus 1.00))
        prefix (cond (and (integer? id)(< 0 id)(< id 9))
                     (format "op%d-" id)
                     :default
                     (format "%s-" (name id)))
        param (fn [p]
                (keyword (format "%s%s" prefix p)))
        rs [(param "attack") att
            (param "decay1") dcy1
            (param "decay2") dcy2
            (param "release") rel
            (param "peak") pk
            (param "breakpoint") bp
            (param "sustain") sus]]
    rs))

(defn xenv [& {:keys [att dcy1 dcy2 rel peak bp sus]
                :or {att 0.00
                     dcy1 0.00
                     dcy2 0.00
                     rel 0.00
                     peak 1.00
                     bp 1.00
                     sus 1.00}}]
  (envn :xenv [:att att :dcy1 dcy1 :dcy2 dcy2 :rel rel
              :peak peak :bp bp :sus sus]))


(defn penv [& {:keys [a0 a1 a2 a3 t1 t2 t3 cc9]
               :or {a0 0.000
                    a1 0.000
                    a2 0.000
                    a3 0.000
                    t1 1.000
                    t2 1.000
                    t3 1.000
                    cc9 0.00}}]
  (let [rs [:pe-a0 (sclamp a0)
            :pe-a1 (sclamp a1)
            :pe-a2 (sclamp a2)
            :pe-a3 (sclamp a3)
            :pe-t1 (float (max 0 t1))
            :pe-t2 (float (max 0 t2))
            :pe-t3 (float (max 0 t3))
            :pe<-cc9 0.0]]
    rs))


(defn vibrato [freq & {:keys [sens prss depth delay]
                       :or {sens 0.01
                            delay 2
                            prss 0.00
                            depth 0.00}}]
  (let [rs [:vibrato-frequency (clamp freq con/min-lfo-frequency con/max-lfo-frequency)
            :vibrato-sensitivity (float (clamp sens con/min-vibrato-sensitivity con/max-vibrato-sensitivity))
            :vibrato<-pressure (uclamp prss)
            :vibrato-delay delay
            :vibrato-depth (uclamp depth)]]
    rs))


(defn lfo1 [freq & {:keys [cca prss bleed delay]
                    :or {cca 0.00
                         prss 0.00
                         bleed 1.00
                         delay 1.0}}]
  (let [rs [:lfo1-freq (clamp freq con/min-lfo-frequency con/max-lfo-frequency)
            :lfo1<-cca (uclamp cca)
            :lfo1<-pressure (uclamp prss)
            :lfo1-bleed (uclamp bleed)
            :lfo1-delay (float (max 0 delay))]]
    rs))


(defn lfo2 [freq & {:keys [xenv cca ccb]
                    :or {xenv 0.00
                         cca 0.00
                         ccb 0.00}}]
  (let [rs [:lfo2-freq (clamp freq con/min-lfo-frequency con/max-lfo-frequency)
            :lfo2-amp<-xenv (uclamp xenv)
            :lfo2-amp<-cca (uclamp cca)
            :lfo2-amp<-ccb (uclamp ccb)]]
    rs))

(defn lfo3 [freq & {:keys [xenv cca ccb]
                    :or {xenv 0.00
                         cca 0.00
                         ccb 0.00}}]
  (let [rs [:lfo3-freq (clamp freq con/min-lfo-frequency con/max-lfo-frequency)
            :lfo3-amp<-xenv (uclamp xenv)
            :lfo3-amp<-cca (uclamp cca)
            :lfo3-amp<-ccb (uclamp ccb)]]
    rs))


;; id --> :op1 :op2 :op3 :op4 :op5 :op6 :bzz :nse
;;
(defn- op-amp-mod [id lfo1 cca ccb vel prss]
  (let [param (fn [p](keyword (format "%s-amp<-%s" 
                                      (name id) p)))
        rs [(param "lfo1")(uclamp lfo1)
            (param "cca")(uclamp cca)
            (param "ccb")(uclamp ccb)
            (param "velocity")(uclamp vel)
            (param "pressure")(uclamp prss)]]
    rs))

(defn- clamp-keyscale-depth [n]
  (or (and (col/member? n con/allowd-keyscale-depths) n) 0))

(defn op1 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op1-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op1-amp (float (min 1.0 amp))
            (op-amp-mod :op1 lfo1 cca ccb vel prss)
            (envn 1 env)
            :op1-keyscale-key (int (clamp key 0 127))
            :op1-keyscale-left (clamp-keyscale-depth left)
            :op1-keyscale-right (clamp-keyscale-depth right)
            :op1<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm1 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 1.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm1-detune (float detune)
            :fm1-bias (float bias)
            :fm1-amp (float amp)
            :fm1<-env (uclamp env)
            :fm1-lag (clamp lag 0.0 con/max-fm-lag)
            :fm1-keyscale-left (clamp-keyscale-depth left)
            :fm1-keyscale-right (clamp-keyscale-depth right)]]
    rs))

(defn op2 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op2-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op2-amp (float (min 1.0 amp))
            (op-amp-mod :op2 lfo1 cca ccb vel prss)
            (envn 2 env)
            :op2-keyscale-key (int (clamp key 0 127))
            :op2-keyscale-left (clamp-keyscale-depth left)
            :op2-keyscale-right (clamp-keyscale-depth right)
            :op2<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm2 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 0.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm2-detune (float detune)
            :fm2-bias (float bias)
            :fm2-amp (float amp)
            :fm2<-env (uclamp env)
            :fm2-lag (clamp lag 0.0 con/max-fm-lag)
            :fm2-keyscale-left (clamp-keyscale-depth left)
            :fm2-keyscale-right (clamp-keyscale-depth right)]]
    rs))

(defn op3 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op3-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op3-amp (float (min 1.0 amp))
            (op-amp-mod :op3 lfo1 cca ccb vel prss)
            (envn 3 env)
            :op3-keyscale-key (int (clamp key 0 127))
            :op3-keyscale-left (clamp-keyscale-depth left)
            :op3-keyscale-right (clamp-keyscale-depth right)
            :op3<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm3 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 0.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm3-detune (float detune)
            :fm3-bias (float bias)
            :fm3-amp (float amp)
            :fm3<-env (uclamp env)
            :fm3-lag (clamp lag 0.0 con/max-fm-lag)
            :fm3-keyscale-left (clamp-keyscale-depth left)
            :fm3-keyscale-right (clamp-keyscale-depth right)]]
    rs))

(defn op4 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op4-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op4-amp (float (min 1.0 amp))
            (op-amp-mod :op4 lfo1 cca ccb vel prss)
            (envn 4 env)
            :op4-keyscale-key (int (clamp key 0 127))
            :op4-keyscale-left (clamp-keyscale-depth left)
            :op4-keyscale-right (clamp-keyscale-depth right)
            :op4<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm4 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 0.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm4-detune (float detune)
            :fm4-bias (float bias)
            :fm4-amp (float amp)
            :fm4<-env (uclamp env)
            :fm4-lag (clamp lag 0.0 con/max-fm-lag)
            :fm4-keyscale-left (clamp-keyscale-depth left)
            :fm4-keyscale-right (clamp-keyscale-depth right)]]
    rs))


(defn op5 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op5-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op5-amp (float (min 1.0 amp))
            (op-amp-mod :op5 lfo1 cca ccb vel prss)
            (envn 5 env)
            :op5-keyscale-key (int (clamp key 0 127))
            :op5-keyscale-left (clamp-keyscale-depth left)
            :op5-keyscale-right (clamp-keyscale-depth right)
            :op5<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm5 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 0.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm5-detune (float detune)
            :fm5-bias (float bias)
            :fm5-amp (float amp)
            :fm5<-env (uclamp env)
            :fm5-lag (clamp lag 0.0 con/max-fm-lag)
            :fm5-keyscale-left (clamp-keyscale-depth left)
            :fm5-keyscale-right (clamp-keyscale-depth right)]]
    rs))


(defn op6 [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:op6-detune (clamp detune con/min-op-detune con/max-op-detune)
            :op6-amp (float (min 1.0 amp))
            (op-amp-mod :op6 lfo1 cca ccb vel prss)
            (envn 6 env)
            :op6-keyscale-key (int (clamp key 0 127))
            :op6-keyscale-left (clamp-keyscale-depth left)
            :op6-keyscale-right (clamp-keyscale-depth right)
            :op6<-penv (sclamp penv)]]
    (flatten rs)))

(defn fm6 [detune amp & {:keys [bias env lag left right]
                         :or {bias 0.00
                              env 0.00
                              lag 0.00
                              left 0
                              right 0}}]
  (let [rs [:fm6-detune (float detune)
            :fm6-bias (float bias)
            :fm6-amp (float amp)
            :fm6<-env (uclamp env)
            :fm6-lag (clamp lag 0.0 con/max-fm-lag)
            :fm6-keyscale-left (clamp-keyscale-depth left)
            :fm6-keyscale-right (clamp-keyscale-depth right)]]
    rs))

(defn buzz [detune amp & {:keys [lfo1 cca ccb vel prss env penv key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              ccb 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:bzz-detune (clamp detune con/min-op-detune con/max-op-detune)
            :bzz-amp (float (min 1.0 amp))
            (op-amp-mod :bzz lfo1 cca ccb vel prss)
            (envn :bzz env)
            :bzz-keyscale-key (int (clamp key 0 127))
            :bzz-keyscale-left (clamp-keyscale-depth left)
            :bzz-keyscale-right (clamp-keyscale-depth right)
            :bzz<-penv (sclamp penv)]]
    (flatten rs)))

(defn buzz-harmonics [n & {:keys [env cca hp hp<-env]
                           :or {env 0.00
                                cca 0.00
                                hp 1
                                hp<-env 0.00}}]
  (let [rs [:bzz-harmonics (int (clamp n con/min-buzz-harmonics con/max-buzz-harmonics))
            :bzz-harmonics<-env (clamp env -64 64)
            :bzz-harmonics<-cca (clamp cca -64 64)
            :bzz-hp-track (int (clamp hp 0 con/max-buzz-harmonics))
            :bzz-hp-track<-env (clamp hp<-env -64 64)]]
    rs))


(defn noise [detune amp & {:keys [lfo1 cca vel prss env penv bw key left right]
                         :or {lfo1 0.00
                              cca 0.00
                              vel 0.00
                              prss 0.00
                              env [:att 0.0 :dcy1 0.0 :dcy2 0.0 :rel 0.0
                                   :peak 1.0 :bp 1.0 :sus 1.0]
                              bw 10
                              key 60
                              left 0
                              right 0
                              penv 0.00}}]
  (let [rs [:nse-detune (clamp detune con/min-op-detune con/max-op-detune)
            :nse-amp (float (min 1.0 amp))
            (op-amp-mod :nse lfo1 cca 0 vel prss)
            (envn :nse env)
            :nse-bw (int (clamp bw con/min-noise-filter-bw con/max-noise-filter-bw)) 
            :nse-keyscale-key (int (clamp key 0 127))
            :nse-keyscale-left (clamp-keyscale-depth left)
            :nse-keyscale-right (clamp-keyscale-depth right)
            :nse<-penv (sclamp penv)]]
    (flatten rs)))

(defn noise2 [detune amp & {:keys [bw lag]
                            :or {bw 10
                                 lag 0.00}}]
  (let [rs [:nse2-detune (clamp detune con/min-op-detune con/max-op-detune)
            :nse2-amp (float (min 1.0 amp))
            :nse2-bw (int (clamp bw con/min-noise-filter-bw con/max-noise-filter-bw))
            :nse2-lag (float (clamp lag 0.0 con/max-noise-lag))]]
    rs))


(defn lp-filter [& {:keys [freq env res mode]
                    :or {freq [9999 :track 0 :env +0.00 :prss +0.00 :cca +0.00 :ccb +0.00]
                         env [:att 0.00 :dcy 0.00 :rel 0.00 :sus 1.00]
                         res [0.00 :cca 0.00 :ccb 0.00]
                         mode -1.00}}]
  (let [fmap (col/alist->map (rest freq))
        emap (col/alist->map env)
        rmap (col/alist->map (rest res))
        rs [:filter-freq (int (clamp (first freq) con/min-filter-cutoff con/max-filter-cutoff))
            :filter-track (int (clamp (get fmap :track 0) con/min-filter-track con/max-filter-track))
            :filter<-env (sclamp (get fmap :env 0.0))
            :filter<-pressure (sclamp (get fmap :prss 0.0))
            :filter<-cca (sclamp (get fmap :cca 0.0))
            :filter<-ccb (sclamp (get fmap :ccb 0.0))
            :filter-res (uclamp (first res))
            :filter-res<-cca (sclamp (get rmap :cca 0.0))
            :filter-res<-ccb (sclamp (get rmap :ccb 0.0))
            :filter-attack (float (max 0 (get emap :att 0.0)))
            :filter-decay (float (max 0 (get emap :dcy 0.0)))
            :filter-release (float (max 0 (get emap :rel 0.0)))
            :filter-sustain (uclamp (get emap :sus 1.0))
            :filter-mode (sclamp mode)]]
    rs))

(defn bp-filter [& {:keys [offset lag]
                    :or {offset 2
                         lag 0.0}}]
  [:filter2-detune (float (max 0.125 offset))
   :filter2-lag (float (max lag 0.0))])

(defn fold [& {:keys [wet gain cca ccb]
               :or {wet 0.0
                    gain 2.0
                    cca 0.0
                    ccb 0.0}}]
  [:dist-mix (uclamp wet)
   :dist-pregain (float (max 1 gain))
   :dist<-cca (float (max 0 cca))
   :dist<-ccb (float (max 0 ccb))])


(defn delay1 [& {:keys [time amp pan fb xfb]
                 :or {time [1.00 :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      amp  [-60  :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      pan  [-0.7 :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      fb   0.5
                      xfb  0.00}}]
  (let [tmap (col/alist->map (rest time))
        amap (col/alist->map (rest amp))
        pmap (col/alist->map (rest pan))
        dt (clamp (first time) 0.00 con/max-delay-time)
        dt<-lfo2 (uclamp (get tmap :lfo2 0.0))
        dt<-lfo3 (uclamp (get tmap :lfo3 0.0))
        dt<-xenv (uclamp (get tmap :xenv 0.0))
        a (int (clamp (first amp) con/min-db con/max-db))
        a<-lfo2 (uclamp (get amap :lfo2 0.0))
        a<-lfo3 (uclamp (get amap :lfo3 0.0))
        a<-xenv (uclamp (get amap :xenv 0.0))
        p (clamp (first pan) -1.0 1.0)
        p<-lfo2 (uclamp (get pmap :lfo2 0.0))
        p<-lfo3 (uclamp (get pmap :lfo3 0.0))
        p<-xenv (uclamp (get pmap :xenv 0.0))
        rs [:delay1-time dt
            :delay1-time<-lfo2 dt<-lfo2 
            :delay1-time<-lfo3 dt<-lfo3 
            :delay1-time<-xenv dt<-xenv 
            :delay1-amp a
            :delay1-amp<-lfo2 a<-lfo2 
            :delay1-amp<-lfo3 a<-lfo3 
            :delay1-amp<-xenv a<-xenv 
            :delay1-pan p
            :delay1-pan<-lfo2 p<-lfo2 
            :delay1-pan<-lfo3 p<-lfo3 
            :delay1-pan<-xenv p<-xenv 
            :delay1-fb (sclamp fb)
            :delay1-xfb (sclamp xfb)]]
    rs))

(defn delay2 [& {:keys [time amp pan fb xfb]
                 :or {time [1.00 :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      amp  [-60  :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      pan  [+0.7 :lfo2 0.00 :lfo3 0.00 :xenv 0.00]
                      fb   0.5
                      xfb  0.00}}]
  (let [tmap (col/alist->map (rest time))
        amap (col/alist->map (rest amp))
        pmap (col/alist->map (rest pan))
        dt (clamp (first time) 0.00 con/max-delay-time)
        dt<-lfo2 (uclamp (get tmap :lfo2 0.0))
        dt<-lfo3 (uclamp (get tmap :lfo3 0.0))
        dt<-xenv (uclamp (get tmap :xenv 0.0))
        a (int (clamp (first amp) con/min-db con/max-db))
        a<-lfo2 (uclamp (get amap :lfo2 0.0))
        a<-lfo3 (uclamp (get amap :lfo3 0.0))
        a<-xenv (uclamp (get amap :xenv 0.0))
        p (clamp (first pan) -1.0 1.0)
        p<-lfo2 (uclamp (get pmap :lfo2 0.0))
        p<-lfo3 (uclamp (get pmap :lfo3 0.0))
        p<-xenv (uclamp (get pmap :xenv 0.0))
        rs [:delay2-time dt
            :delay2-time<-lfo2 dt<-lfo2 
            :delay2-time<-lfo3 dt<-lfo3 
            :delay2-time<-xenv dt<-xenv 
            :delay2-amp a
            :delay2-amp<-lfo2 a<-lfo2 
            :delay2-amp<-lfo3 a<-lfo3 
            :delay2-amp<-xenv a<-xenv 
            :delay2-pan p
            :delay2-pan<-lfo2 p<-lfo2 
            :delay2-pan<-lfo3 p<-lfo3 
            :delay2-pan<-xenv p<-xenv 
            :delay2-fb (sclamp fb)
            :delay2-xfb (sclamp xfb)]]
    rs))


(defn amp [n & {:keys [vel cc7 dry dry-pan]
               :or {vel 0.00
                    cc7 0.00
                    dry con/max-db
                    dry-pan 0.0}}]
  (let [rs [:amp (int (clamp n con/min-db con/max-db))
            :amp<-velocity (uclamp vel)
            :amp<-cc7 (uclamp cc7)
            :dry-amp (int (clamp dry con/min-db con/max-db))
            :dry-pan (sclamp dry-pan)]]
    rs))

(defn port-time [n & {:keys [cc5] :or {cc5 0.0}}]
  [:port-time (clamp n 0.0 con/max-port-time)
   :port-time<-cc5 (uclamp cc5)])

(defn enable [& args]
  (let [enabled? (fn [obj](if (col/member? obj args) 1.0 0.0))
       rs [:op1-enable (enabled? 1)
           :op2-enable (enabled? 2)
           :op3-enable (enabled? 3)
           :op4-enable (enabled? 4)
           :op5-enable (enabled? 5)
           :op6-enable (enabled? 6)
           :nse-enable (enabled? :noise)
           :bzz-enable (enabled? :buzz)]]
    rs))

(defn cobalt [& data]
  (flatten data))
