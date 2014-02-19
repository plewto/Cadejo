(ns cadejo.modules.lfo
  (:use overtone.core)
  (:require [cadejo.modules.env :as cenv])
  (:require [cadejo.util.qugen :as qu]))

;; Sine LFO with integral envelope
;; freq - frequency in Hz
;; depth - amplitude -prior- to envelope application
;; env->depth - amount of envelope applied to amplitude
;; delay - delay and attack time in seconds
;; release - release time in seconds
;; gate - envelope trigger.
;; 
(defcgen sin-lfo [freq depth env->depth delay release gate]
  (:kr 
   (* (+ depth 
         (* env->depth (cenv/delay-env delay release gate)))
      (sin-osc:kr freq))))
