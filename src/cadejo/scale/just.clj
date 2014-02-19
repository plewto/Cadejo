(ns cadejo.scale.just
  (:require [cadejo.scale.tuning-table :as ttab]))

(def just-c1 {:template '[1/1 16/15 9/8 6/5 5/4 4/3 36/25 3/2 8/5 5/3 9/5 15/8]
              :name 'just-c1
              :notes-per-octave 12
              :octave-size 2.0})



(defn get-tuning-table 
  ([scale ref]
     (ttab/get-tuning-table scale ref))
  ([scale]
     (get-tuning-table scale 440))
  ([]
     (get-tuning-table just-c1)))
