(println "-->    cobalt genpatch")
(ns cadejo.instruments.cobalt.genpatch.genpatch
  (:use [cadejo.instruments.cobalt.program])
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.instruments.cobalt.genpatch.amp :as amp])
  (:require [cadejo.instruments.cobalt.genpatch.env :as env])
  (:require [cadejo.instruments.cobalt.genpatch.filter :as filter])
  (:require [cadejo.instruments.cobalt.genpatch.gamut :as gamut])
  (:require [cadejo.instruments.cobalt.genpatch.lfo :as lfo])
  (:require [cadejo.util.col :as col])
  (:require [cadejo.util.math :as math]))

(def approx math/approx)
(def coin math/coin)


(defn third [s](nth s 2))
(defn fourth [s](nth s 3))


(defn display-gamut [gamut]
  (print ";; Gamut [")
  (dotimes [i 8]
    (printf "%6.3f " (float (nth gamut i))))
  (printf "nse %6.3f bzz %6.3f]" (float (nth gamut 8))(float (nth gamut 9)))
  (println))

(defn display-registration [registration]
  (print ";; Registration [")
  (dotimes [i 8]
    (printf "%5.3f " (float (nth registration i))))
  (printf "nse %5.3f bzz %5.3f]" (float (nth registration 8))(float (nth registration 9)))
  (println))



(defn random-cobalt-program []
  (let [envmap (env/gen-envelopes)
        [gamut fm fm-bias](let [g (gamut/create-gamut)]
                             [(:gamut g)(:fm g)(:fm-bias g)])
        fmi (amp/create-fm-indexes)
        registration (amp/create-registration gamut)
        mean-registration (/ (apply #'+ registration)(count registration))
        nbw (coin 0.0 (rand 50)(+ 50 (coin 0.70 (rand 100)(rand 400))))
        op-velocities (amp/select-op-velocity-sensitivities gamut)
        op-pressures (amp/select-op-pressure-sensitivities)
        op-lfo-depths (amp/select-op-lfo-depths)
        bzz-harmonics (int (+ 1 (rand (* 1/4 con/max-buzz-harmonics))))
        bzz-harmonics<-env (int (rand (- con/max-buzz-harmonics bzz-harmonics)))
        bzz-hp-track (int (coin 0.75 1 (rand-nth [2 3 4 6 8])))
        bzz-hp<-env (int (rand (- 16 bzz-hp-track)))
        pitch-env-depth (rand)
        pitch-env-mode (coin 0.9 :off
                             (rand-nth (flatten [(repeat 6 :uniform)
                                                 (repeat 2 :select-uniform)
                                                 (repeat 2 :divergent)
                                                 (repeat 2 :select-divergent)
                                                 (repeat 2 :random)])))
        set-pitchenv (fn []
                       (let [sign (fn [n](* n (coin 0.5 -1 +1)))]
                         (cond (= pitch-env-mode :off) 0.0
                               (= pitch-env-mode :uniform) pitch-env-depth
                               (= pitch-env-mode :select-uniform)(coin 0.5 pitch-env-depth 0)
                               (= pitch-env-mode :divergent)(sign pitch-env-depth)
                               (= pitch-env-mode :select-divergent)(coin 0.5 (sign pitch-env-depth) 0)
                               :default (sign (rand pitch-env-depth)))))]
    ;; (display-gamut gamut)
    (display-registration registration)
    (println (format "Mean registration -> %4.2f   (db %s)" mean-registration (math/amp->db mean-registration)))
    ;; (println (format "noise bw = %s" nbw))
    ;; (println (format "Velocities %s" op-velocities))
    ;; (println (format "Pressures  %s" op-pressures))
    ;; (println (format "LFO        %s" op-lfo-depths))
    ;; (println (format "bzz-harmonics    %s   env %s" bzz-harmonics bzz-harmonics<-env))
    ;; (println (format "bzz-hp-track     %s   env %s" bzz-hp-track bzz-hp<-env))
    (cobalt (port-time (coin 0.1 (rand 0.5) 0.0))
            (let [map (col/alist->map (:xenv envmap))]
              (xenv :att (:att map) :dcy1 (:dcy1 map) :dcy2 (:dcy2 map)
                     :rel (:rel map) :peak (:peak map) :bp (:bp map) 
                     :sus (:sus map)))
            (env/select-pitch-env)
            (lfo/select-lfo-and-delay)
            (op1 (first gamut)(first registration)
                 :vel (first op-velocities)
                 :prss (first op-pressures)
                 :lfo1 (first op-lfo-depths)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op1 envmap)
                 :key 60 :left   0 :right   0)
            (op2 (second gamut)(second registration)
                 :vel (second op-velocities )
                 :prss (second op-pressures )
                 :lfo1 (second op-lfo-depths )
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op2 envmap) 
                 :key 60 :left   0 :right   0)
            (op3 (nth gamut 2)(nth registration 2)
                 :vel (nth op-velocities  2)
                 :prss (nth op-pressures  2)
                 :lfo1 (nth op-lfo-depths  2)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op3 envmap) 
                 :key 60 :left   0 :right   0)
            (op4 (nth gamut 3)(nth registration 3)
                 :vel (nth op-velocities  3)
                 :prss (nth op-pressures  3)
                 :lfo1 (nth op-lfo-depths  3)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op4 envmap) 
                 :key 60 :left   0 :right   0)
            (op5 (nth gamut 4)(nth registration 4)
                 :vel (nth op-velocities  4)
                 :prss (nth op-pressures  4)
                 :lfo1 (nth op-lfo-depths  4)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op5 envmap) 
                 :key 60 :left   0 :right   0)
            (op6 (nth gamut 5)(nth registration 5)
                 :vel (nth op-velocities  5)
                 :prss (nth op-pressures  5)
                 :lfo1 (nth op-lfo-depths  5)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op6 envmap) 
                 :key 60 :left   0 :right   0)
            (op7 (nth gamut 6)(nth registration 6)
                 :vel (nth op-velocities  6)
                 :prss (nth op-pressures  6)
                 :lfo1 (nth op-lfo-depths  6)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op7 envmap) 
                 :key 60 :left   0 :right   0)
            (op8 (nth gamut 7)(nth registration 7)
                 :vel (nth op-velocities  7)
                 :prss (nth op-pressures  7)
                 :lfo1 (nth op-lfo-depths  7)
                 :cca   0.00 :ccb   0.00 
                 :penv (set-pitchenv)
                 :env (:op8 envmap) 
                 :key 60 :left   0 :right   0)
            (noise (nth gamut 8)(nth registration 8) 
                  :vel (nth op-velocities 8)
                  :prss (nth op-pressures 8)
                  :lfo1 (nth op-lfo-depths 8)
                  :cca 0.000 
                  :penv (set-pitchenv)
                  :env (:noise envmap)
                  :key 60 :left   0 :right   0
                  :bw nbw)
           (buzz  (nth gamut 9)
                  (nth registration 9)
                  :vel (nth op-velocities 9)
                  :prss (nth op-pressures 9)
                  :lfo1 (nth op-lfo-depths 9)
                  :cca   0.00 :ccb   0.00 
                  :penv (set-pitchenv)
                  :env (:buzz envmap)
                  :key 60 :left   0 :right   0)
           (filter/select-filter (:filter envmap))
           (fm1 (first fm)  (first fmi)  :bias (first fm-bias)  :env 1.0)
           (fm2 (second fm) (second fmi) :bias (second fm-bias) :env 1.0)
           (fm3 (third fm)  (third fmi)  :bias (third fm-bias)  :env 1.0)
           (fm4 (fourth fm) (fourth fmi) :bias (fourth fm-bias) :env 1.0)
           (buzz-harmonics bzz-harmonics 
                           :env bzz-harmonics<-env
                           :hp bzz-hp-track
                           :hp<-env bzz-hp<-env)
           (amp (cond (< mean-registration 0.5) -3
                      :default -6))
           ))) 

