(println "-->    cobalt genpatch")
(ns cadejo.instruments.cobalt.genpatch
  (:use [cadejo.instruments.cobalt.program])
  (:require [cadejo.instruments.cobalt.constants :as con])
  ;(:require [cadejo.modules.addsr2-helper :as addsr])
  (:require [cadejo.util.math :as math]))

(def approx math/approx)
(def coin math/coin)

;; Random invert value n 
;; 
(defn scoin 
  ([n]
   (let [s (math/coin 0.5 -1 1)]
     (* s n)))
  ([p range]
   (scoin (math/coin p range 0))))






;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST ;;; TEST TEST TEST TEST 

(defn rl [](use 'cadejo.instruments.cobalt.genpatch :reload))
