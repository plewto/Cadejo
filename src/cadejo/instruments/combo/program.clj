(ns cadejo.instruments.combo.program
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.util.col :as col])
  (:require [cadejo.util.user-message :as umsg]))

(defonce bank (cadejo.midi.program-bank/program-bank :Combo))


(.bank-remarks! bank "These are bank level remarks")

(defn save-program 
  ([pnum function-id pname remarks data]
     (.set-program! bank pnum function-id pname data remarks))
  ([pnum pname remarks data]
     (save-program pnum nil pname remarks data))
  ([pnum pname data]
     (save-program pnum pname "" data)))

(def combo-parameters 
  '[:vibrato-freq :vibrato-sens :amp1 :wave1 :amp2 :wave2 :amp3 :wave3
    :amp4 :wave4 :chorus :filter :filter-type :amp :flanger-depth
    :flanger-rate :flanger-fb :flanger-mix :reverb-mix])

(defn- continuity-test [data]
  (let [keys (col/alist->keys data)
        rs* (atom true)]
    (doseq [p combo-parameters]
      (if (col/not-member? p keys)
        (do 
          (umsg/warning (format "Combo %s parameter missing" p))
          (swap! rs* (fn [n] false)))))
    @rs*))

(defn- spurious-parameter-test [data]
  (let [rs* (atom true)]
    (doseq [k (col/alist->keys data)]
      (if (col/not-member? k combo-parameters)
        (do
          (umsg/warning (format "Unrecognized Combo parameter %s" k))
          (swap! rs* (fn [n] false)))))
    @rs*))

(def bypass bypass-filter)
(def low lp-filter)
(def high hp-filter)
(def band bp-filter)
(def notch br-filter)

(defn- combo-vibrato [& {:keys [freq sens]
                                  :or {freq 5.00
                                       sens 0.01}}]
  (list :vibrato-freq (float freq)
        :vibrato-sens (float sens)))

(defn- combo-filter [& {:keys [freq type]
                                 :or {freq 8
                                      type bypass-filter}}]
  (list :filter (int freq)
        :filter-type (int type)))

(defn- combo-flanger [& {:keys [rate depth fb mix]
                                  :or {rate 0.25
                                       depth 0.25
                                       fb 0.50
                                       mix 0.0}}]
  (list :flanger-depth (float depth)
        :flanger-rate (float rate)
        :flanger-fb (float fb)
        :flanger-mix (float mix)))

(defn combo [& {:keys [a1 a2 a3 a4 w1 w2 w3 w4 chorus amp reverb
                       vibrato filter flanger]
                :or {a1 1.00
                     a2 0.70
                     a3 0.00
                     a4 0.00
                     w1 0.00
                     w2 0.00
                     w3 0.00
                     w4 0.00
                     chorus 0.0
                     amp 0.20
                     reverb 0.0
                     vibrato [:freq 5.0 :sens 0.010]
                     filter [:freq 8 :type bypass-filter]
                     flanger [:rate 0.25 :depth 0.25 :fb 0.50 :mix 0.00]}}]
  (let [data (flatten (list :amp1 (float a1)
                            :amp2 (float a2)
                            :amp3 (float a3)
                            :amp4 (float a4)
                            :wave1 (float w1)
                            :wave2 (float w2)
                            :wave3 (float w3)
                            :wave4 (float w4)
                            :chorus (float chorus)
                            :amp (float amp)
                            :reverb-mix (float reverb)
                            (apply #'combo-vibrato vibrato)
                            (apply #'combo-filter filter)
                            (apply #'combo-flanger flanger)))]
    (continuity-test data)
    (spurious-parameter-test data)
    data))
