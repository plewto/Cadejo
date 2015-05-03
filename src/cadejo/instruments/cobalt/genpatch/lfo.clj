(println "-->    cobalt genpatch lfo")
(ns cadejo.instruments.cobalt.genpatch.lfo
  (:require [cadejo.instruments.cobalt.program :as prog])
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.util.math :as math]))

(def ^:private coin math/coin)
(def ^:private clamp math/clamp)

(defn- select-vibrato-frequency []
  (+ 3 (rand 4)))

(defn- select-lfo-frequency [vf sync?]
  (float (if sync?
           (let [n (rand-nth [1 2 3 4])
                 du (rand-nth [1 2 3 4 5 6 8 9])
                 dt (rand-nth [0 0 0 0 
                               10 20 30 40 50 60 
                               70 80 90 100])]
             (* vf n (/ (+ du dt))))
           (+ con/min-lfo-frequency (rand (- con/max-lfo-frequency 
                                             con/min-lfo-frequency))))))

(defn- select-delay-time [vf sync range]
  (let [period (/ 1.0 vf)
        dt (clamp (float (cond (= range :flanger)(max 0.0001 (rand 0.005))
                               (= range :short)(if sync 
                                                 (* period (rand-nth [1/2 1/3 1/4 1/6 1/8]))
                                                 (+ 0.1 (rand 0.5)))
                               (= range :medium)(if sync
                                                  (* period (rand-nth [2/3 3/4 1 5/4 4/3 3/2 2]))
                                                  (+ 0.25 (rand 1)))
                               :default (if sync (* period (rand-nth [1 2 3 4]))
                                            (+ 0.75 (rand (- con/max-delay-time 0.75))))))
                  0.0001 con/max-delay-time)]
    dt))
                        
(defn select-lfo-and-delay []
  (let [range1 (rand-nth [:long :medium :medium :medium :short :flanger])
        flanger1 (= range1 :flanger)
        range2 (rand-nth [:long :medium :medium :short :flanger :flanger :flanger])
        flanger2 (= range2 :flanger)
        use-delay (rand-nth (flatten [(repeat 50 nil)
                                      (repeat 10 1)
                                      (repeat 10 2)
                                      (repeat 30 3)]))
        sync (coin 0.75)
        vf (select-vibrato-frequency)
        lf1 (select-lfo-frequency vf sync)
        lf2 (cond (and sync flanger1)(float (max 0.001 (* (coin 0.5 1/100 1)
                                                          (select-lfo-frequency vf sync))))
                  flanger1 (float (max 0.001 (coin 0.75 (rand 0.1)(rand 5))))
                  :default (select-lfo-frequency vf sync))
        lf3 (cond (and sync flanger2)(float (max 0.001 (* (coin 0.5 1/100 1)
                                                          (select-lfo-frequency vf sync))))
                  flanger2 (float (max 0.001 (coin 0.75 (rand 0.1)(rand 5))))
                  :default (select-lfo-frequency vf sync))
        

        ;; delay 1
        dt1 (select-delay-time vf sync range1)
        dl1-amp (if (or (= use-delay 1)(= use-delay 3))(int (* -3 (rand 8))) con/min-db)
        dl1-amp<-lfo2 (coin 0.1 (rand) 0.0)
        dl1-amp<-lfo3 (coin 0.1 (rand) 0.0)
        dl1-amp<-xenv (cond (or (= range1 :long)(= range1 :medium))(coin 0.5 (+ 0.5 (rand 0.5)) 0.0)
                            :default 0.0)
        dl1-pan<-lfo2 (coin 0.10 (* (coin 0.5 -1 +1)(rand 1.5)) 0.0)
        dl1-pan<-lfo3 (coin 0.10 (* (coin 0.5 -1 +1)(rand 1.5)) 0.0)
        dl1-pan<-xenv (coin 0.10 (rand 1.5) 0)
        dl1-fb (cond flanger1 (* (coin 0.5 -1 +1)(+ 0.5 (rand 0.4)))
                     :default (coin 0.7 (+ 0.4 (rand 0.55))(rand 0.5)))
        dl1-xfb (coin 0.25 (cond (> (math/abs dl1-fb) 0.8) 0.0
                                 (> (math/abs dl1-fb) 0.5)(rand 0.5)
                                 :default (rand 0.75))
                      0.0)

        ;; delay 2
        dt2 (select-delay-time vf sync range2)
        dl2-amp (if (or (= use-delay 2)(= use-delay 3))(int (* -3 (rand 8))) con/min-db)
        dl2-amp<-lfo2 (coin 0.1 (rand) 0.0)
        dl2-amp<-lfo3 (coin 0.1 (rand) 0.0)
        dl2-amp<-xenv (cond (or (= range1 :long)(= range1 :medium))(coin 0.5 (+ 0.5 (rand 0.5)) 0.0)
                            :default 0.0)
        dl2-pan<-lfo2 (coin 0.10 (* (coin 0.5 -1 +1)(rand 1.5)) 0.0)
        dl2-pan<-lfo3 (coin 0.10 (* (coin 0.5 -1 +1)(rand 1.5)) 0.0)
        dl2-pan<-xenv (coin 0.10 (rand 1.5) 0)
        dl2-fb (cond flanger1 (* (coin 0.5 -1 +1)(+ 0.5 (rand 0.4)))
                     :default (coin 0.7 (+ 0.4 (rand 0.55))(rand 0.5)))
        dl2-xfb (coin 0.25 (cond (> (math/abs dl2-fb) 0.8) 0.0
                                 (> (math/abs dl2-fb) 0.5)(rand 0.5)
                                 :default (rand 0.75))
                      0.0)]
    [(prog/vibrato vf 
                   :sens (coin 0.80 (+ 0.001 (rand 0.05))(rand))
                   :delay (rand 4)
                   :depth (coin 0.20 (rand 0.1) 0.0))
     (prog/lfo1 lf1 :bleed (rand 1) :delay (rand 4))
     (prog/lfo2 lf2 :xenv (coin 0.20 (rand) 0.0))
     (prog/lfo3 lf3 :xenv (coin 0.20 (rand) 0.0))

     (prog/delay1 :time [dt1
                         :lfo2 (if flanger1 (rand) 0.0)
                         :lfo3 (if flanger1 (coin 0.2 (rand) 0.0) 0.0)
                         :xenv (if flanger1 (coin 0.1 (rand) 0.0) 0.0)]
                  :amp [dl1-amp
                        :lfo2 dl1-amp<-lfo2
                        :lfo3 dl1-amp<-lfo3
                        :xenv dl1-amp<-xenv]
                  :pan [-0.7 :lfo2 dl1-pan<-lfo2 :lfo3 dl1-pan<-lfo3 :xenv dl1-pan<-xenv]
                  :fb dl1-fb
                  :xfb dl1-xfb)

     (prog/delay2 :time [dt2
                         :lfo2 (if flanger1 (rand) 0.0)
                         :lfo3 (if flanger1 (coin 0.2 (rand) 0.0) 0.0)
                         :xenv (if flanger1 (coin 0.1 (rand) 0.0) 0.0)]
                  :amp [dl2-amp
                        :lfo2 dl2-amp<-lfo2
                        :lfo3 dl2-amp<-lfo3
                        :xenv dl2-amp<-xenv]
                  :pan [0.7 :lfo2 dl2-pan<-lfo2 :lfo3 dl2-pan<-lfo3 :xenv dl2-pan<-xenv]
                  :fb dl2-fb
                  :xfb dl2-xfb)]))
