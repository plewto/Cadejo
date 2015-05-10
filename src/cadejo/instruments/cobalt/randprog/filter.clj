(ns cadejo.instruments.cobalt.randprog.filter
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.program :as prog])
  (:require [cadejo.instruments.cobalt.randprog.config :as config]))

(def ^:private coin math/coin)

(defn build-mode-selection-list []
  (flatten [(repeat (* 100 @config/p-filter-bypass) :bypass)
            (repeat (* 100 @config/p-filter-lowpass) :low-pass)
            (repeat (* 100 @config/p-filter-mixed) :mixed)
            (repeat (* 100 @config/p-filter-bandpass) :band-pass)]))

(defn pick-filter-mode []
  (rand-nth (build-mode-selection-list)))


(defn bypass-mode [env]
  [(prog/lp-filter :freq [10000 :track 0 :env 0.0 
                          :prss 0.0 :cca 0.00 :ccb 0.00]
                   :res [0.00 :cca 0.00 :ccb 0.00]
                   :env env
                   :mode -1.0)
   (prog/bp-filter :offset 1)])

(defn mixed-mode [env mode offset]
  [(prog/lp-filter :freq [(coin 0.75 10 (rand 400))
                          :track (rand-nth [0 0 0 1 2])
                          :env (+ 0.25 (rand 0.75))
                          :prss (coin @config/p-filter-pressure* (rand) 0.0)
                          :cca (coin @config/p-filter-cca* (rand) 0.0)
                          :ccb (coin @config/p-filter-ccb* (rand) 0.0)]
                   :res [(coin @config/p-filter-hi-res (+ 0.5 (rand 0.4))(rand 0.5))
                         :cca 0.0 :ccb 0.00]
                   :env env
                   :mode mode)
   (prog/bp-filter :offset offset)])


(defn select-filter [fenv]
  (let [m (pick-filter-mode)]
    (if @config/verbose* 
      (println (format ";; Filter mode %s" m)))
    (cond (= m :bypass)
          (bypass-mode fenv)

          (= m :low-pass)
          (mixed-mode fenv -1.0 1.0)

          (= m :band-pass)
          (mixed-mode fenv 1.0 1.0)

          :default
          (mixed-mode fenv (+ -0.5 (rand)) (rand-nth [2 3 4 6])))))
