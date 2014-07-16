(ns cadejo.scale.just
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.scale.table])
  ;(:require [cadejo.scale.intonation :as intonation])
  (:require [cadejo.scale.scale-utilities :as scale-util]))

(def table-length cadejo.scale.table/table-length)

(def just-scale-templates 
  {:just-c1 {:template '[1/1 16/15 9/8 6/5 5/4 4/3 36/25 3/2 8/5 5/3 9/5 15/8]
             :name :just-c1
             :notes-per-octave 12
             :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :44-39-12 {:template '[1/1 14/13 44/39 13/11 14/11 4/3 56/39 3/2 11/7 22/13 39/22 21/11]
              :name :44-39-12
              :notes-per-octave 12
              :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :blue-ji {:template '[1 15/14 9/8 6/5 5/4 4/3 7/5 3/2 8/5 5/3 9/5 15/8]
             :name :blue-ji
             :notes-per-octave 12
             :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}

   :pre-archytas {:template '[1 16/15 9/8 6/5 5/4 4/3 64/45 3/2 8/5 5/3 16/9 15/8]
                  :name :pre-archytas
                  :notes-per-octave 12
                  :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}

   :bicycle {:template '[1 13/12 9/8 7/6 5/4 4/3 11/8 3/2 13/8 5/3 7/4 11/6]
             :name :bicycle
             :notes-per-octave 12
             :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :breedball3 {:template '[1 49/48 21/20 15/14 48/40 5/4 7/5 10/7 3/2 49/32 12/7 7/4]
                :name :breedball3
                :notes-per-octave 12
                :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :al-farabi {:template '[1/1 256/243 9/8 32/27 81/64 4/3 1024/729 3/2 128/81 27/16 7/4 16/9]
               :name :al-farabi
               :notes-per-octave 12
               :source "http://www.chrysalis-foundation.org/Al-Farabi-s_Uds.htm"
               :remarks "The source scale is diatonic on C.
The 'black keys' were derived from the original scale with a root on the e-flat key"} 
   :canton {:template '[1/1 14/13 9/8 13/11 14/11 4/3 39/28 3/2 11/7 22/13 16/9 13/7]
            :name :canton
            :notes-per-octave 12
            :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :carlos-harm {:template '[1/1 17/16 9/8 19/16 5/4 21/16 11/8 3/2 13/8 27/16 7/4 15/8]
                 :name :carlos-harm
                 :notes-per-octave 12
                 :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"
                 :remarks "Carlos Harmonic & Ben Johnston's scale of 'Blues' 
from Suite f.micr.piano (1977) & David Beardsley's scale of 'Science Friction'"}
   :centaur {:template '[1/1 21/20 9/8 7/6 5/4 4/3 7/5 3/2 14/9 5/3 7/4 15/8]
             :name :centaur
             :notes-per-octave 12
             :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"
             :remarks "A 7-limit scale"}
   :collapsar {:template '[1/1 15/14 49/44 7/6 5/4 15/11 7/5 3/2 35/22 5/3 7/4 21/11]
               :name :collapsar
               :notes-per-octave 12
               :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"
               :remarks "An 11-limit scale"}
   :major-clus {:template '[1/1 135/128 10/9 9/8 5/4 4/3 45/32 3/2 5/3 27/16 16/9 15/8]
                :name :major-clus
                :notes-per-octave 12
                :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :minor-clus {:template '[1/1 16/15 9/8 6/5 4/3 27/20 46/45 3/2 8/5 27/16 16/9 9/5]
                :name :minor-clus
                :notes-per-octave 12
                :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :thirteendene {:template '[1/1 13/12 9/8 6/5 9/7 27/20 13/9 3/2 8/5 27/16 9/5 27/14]
                  :name :thirteendene
                  :notes-per-octave 12
                  :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   :unimajor {:template '[1/1 22/21 9/8 32/27 14/11 4/3 63/44 3/2 11/7 27/16 16/9 21/11]
              :name :unimajor
              :notes-per-octave 12
              :source "http://xenharmonic.wikispaces.com/Gallery+of+12-tone+Just+Intonation+Scales"}
   })

(defn just-table
  "Returns a new tuning table using just intonation.
   scale-id - keyword, name of just-scale
   a440     - float, frequency of A440
              ISSUE: the value set for key 69 (A440) may be altered by the 
                     scale template
   If scale-id is not recognized, display a warning and return nil" 
  ([scale-id a440]
     (let [srecord (get just-scale-templates scale-id)]
       (if srecord
         (let [template (:template srecord)
               notes-per-octave (get srecord :notes-per-octave (count template))
               octave-size (get srecord :octave-size 2.0)
               c0 (scale-util/a69->c0 a440 notes-per-octave octave-size)
               tt (cadejo.scale.table/create-tuning-table)]
           ;; Build first octave
           (dotimes [i notes-per-octave]
             (.set-key-frequency! tt i (* c0 (nth template i))))
           ;; Copy remaining octaves
           (doseq [start (range 0 table-length notes-per-octave)]
             (scale-util/copy-with-transpose! tt start 
                                       (+ start notes-per-octave)
                                       (+ start notes-per-octave) 2))
           (.put-property! tt :name (keyword (get template :name scale-id)))
           (.put-property! tt :intonation "just")
           (.put-property! tt :template template)
           (.put-property! tt :octave-size octave-size)
           (.put-property! tt :notes-per-octave notes-per-octave)
           (.put-property! tt :source (:source srecord))
           (.put-property! tt :remarks (:remarks srecord))
           tt)
         (umsg/warning (format "%s is not a recognized just scale" scale-id)
                       "Try (show-just-scales)"))))
  ([scale-id]
     (just-table scale-id 440.0)))
             

(defn just-tables []
  (sort (keys just-scale-templates)))

(defn show-just-tables []
  (println ";; Available just scales")
  (doseq [k (just-tables)]
    (println (format ";;    %s" k))))
