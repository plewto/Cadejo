(ns cadejo.scale.scale-utilities
  (:require [cadejo.util.math :as math])
  (:import java.util.Collections
           java.util.ArrayList))


(def step math/step)
(def cent math/cent)
(def table-length 128)
(def default-keyrange [0 table-length])
(def default-wrap [16 4000])


(defn a69->c0 
  "Returns frequency of MIDI key 0 relative to that of key 69"
  ([a440 notes-per-octave octave-size]
     (let [ratio (math/expt octave-size (/ 1.0 notes-per-octave))]
       (* a440 (math/expt ratio -69))))
  ([a440 notes-per-octave]
     (a69->c0 a440 notes-per-octave 2.0))
  ([a440]
     (a69->c0 a440 12))
  ([]
     (a69->c0 440.0)))


(defn copy-with-transpose! 
  "Copy portion of tuning table to another location with scaling
   tt    - An instance of TuningTable
   start - int, the lower bounds of the copied region
 
   optional 
   
   end   - int, the upper bounds of the copied region.
           Defaults to an octave (+ start 12). 
   to    - int, the start keynumber for the destination.
           Defaults to an octave above start
   scale - float, scaling factor applied to copied frequencies.
           Defaults to an octave 2.0

   Note that destinations keynumbers are automatically limited 
   to a safe range, the start and end keynumbers are not 
   range tested."
  ([tt start end to scale]
     (let [count (- (min end table-length) start)]
       (dotimes [i count]
         (let [f  (.get-key-frequency tt (+ start i))
               j (+ to i)]
           (if (< j table-length)
             (.set-key-frequency! tt j (* f scale)))))))
  ([tt start]
     (copy-with-transpose! tt start (+ start 12)(+ start 12) 2)))

(defn- wrap-octaves [f wrap]
  (math/wrap-octaves f (first wrap)(second wrap)))


(defn transpose! [tt cents & {:keys [keyrange bias wrap]
                              :or {keyrange default-keyrange
                                   bias 0
                                   wrap default-wrap}}]
  "Transpose portion of TuningTable
   tt        - The TuningTable
   cents     - float, transposition amount in cents
   :keyrange - seg [start end], keyrange of operation
   :wrap     - seq [low high], wrapping frequencies
   :bias     - float, fixed value added to each frequency."
  (let [[start end] keyrange
        ratio (math/expt cent cents)]
    (doseq [key (range start end)]
      (let [f1 (.get-key-frequency tt key)
            f2 (wrap-octaves (+ (* f1 ratio) bias) wrap)]
        (.set-key-frequency! tt key f2)))
    tt))

(defn stretch! [tt & {:keys [keyrange s1 s2 wrap]
                      :or {keyrange default-keyrange
                           s1 1.000
                           s2 1.001
                           wrap default-wrap}}]
  "Stretch portion of TuningTable frequencies
   tt       - TuningTable
   :keyrange - seg [start end], keyrange of operation
   :wrap     - seq [low high], wrapping frequencies
   :s1       - float, stretch factor at start of range, default 1.000
   :s2       - float, stretch factor at end of range, default 1.001"
  (let [[start end] keyrange
        sfn (math/linear-function start s1 end s2)]
    (doseq [key (range start end)]
      (let [f1 (.get-key-frequency tt key)
            f2 (wrap-octaves (* f1 (sfn key)) wrap)]
        (.set-key-frequency! tt key f2)))
    tt))

(defn invert! [tt & {:keys [keyrange]
                     :or {keyrange default-keyrange}}]
  "Invert portion of TuningTable
   tt       - TuningTable
   :keyrange - seg [start end], keyrange of operation"
  (let [[start end] keyrange
        acc* (atom [])
        length (- end start)]
    (doseq [i (range (dec end)(dec start) -1)]
      (swap! acc* (fn [n](conj n (.get-key-frequency tt i)))))
    (doseq [j (range (count @acc*))]
      (let [k (+ j start)]
        (.set-key-frequency! tt k (nth @acc* j)))))
  tt)            

(defn linear! [tt & {:keys [keyrange f1 f2 wrap]
                     :or {keyrange default-keyrange
                          wrap default-wrap
                          f1 1
                          f2 100}}]
  "Set TuningTable frequencies to linear progression
   tt        - TuningTable
   :keyrange - seg [start end], keyrange of operation
   :wrap     - seq [low high], wrapping frequencies
   f1        - float, frequency at start of range, default 1
   f2        - float, frequency at end of range, default 100
   NOTE: If wrapping is used neither f1 or f2 may be 0"
  (let [[start end] keyrange
        linfn (math/linear-function start f1 end f2)]
    (doseq [k (range start end)]
      (.set-key-frequency! tt k (wrap-octaves (linfn k) wrap))))
  tt)

(defn random! [tt & {:keys [keyrange minmax]
                     :or {keyrange default-keyrange
                          minmax [200 800]}}]
  "Set TuningTable frequencies to random values.
   tt        - TuningTable
   :keyrange - seg [start end], keyrange of operation
   :minmax   - seg [min max], the range of possible value, default [200 800]"
  (let [[start end] keyrange
        [min max] minmax
        diff (- max min)]
    (doseq [k (range start end)]
      (.set-key-frequency! tt k (+ min (rand diff)))))
  tt)

(defn detune! [tt & {:keys [keyrange prob max wrap]
                     :or {keyrange default-keyrange
                          wrap default-wrap
                          prob 0.25
                          max 0.01}}]
  "Detune TuningTable frequencies
   tt        - TuningTable
   :keyrange - seg [start end], keyrange of operation
   :wrap     - seq [low high], wrapping frequencies
   :prob     - float, probability key will be altered, 0 <= p <= 1, 
               default 0.25
   :max      - float, maximum deviation 0<= max <= 1, default 0.01"
  (let [[start end] keyrange]
    (doseq [k (range start end)]
      (if (math/coin prob)
        (let [f1 (.get-key-frequency tt k)
              f2 (wrap-octaves (math/approx f1 max) wrap)]
          (.set-key-frequency! tt k f2)))))
  tt)


(defn shuffle! [tt & {:keys [keyrange]
                      :or {keyrange default-keyrange}}]
  "Rearrange TuningTable frequencies
   tt        - TuningTable
   :keyrange - seg [start end], keyrange of operation"
  (let [[start end] keyrange
        acc* (atom [])]
    (doseq [k (range start end)]
      (swap! acc* (fn [n](conj n (.get-key-frequency tt k)))))
    (let [arr (ArrayList. @acc*)]
      (Collections/shuffle arr)
      (dotimes [i (count arr)]
        (.set-key-frequency! tt (+ i start)(nth arr i)))))
  tt)

(defn extract [tt keyrange]
  "Extract portion of TuningTable into a vector.
   tt        - TuningTable
   keyrange  - seg [start end], keyrange of operation
   Returns vector."
  (let [[start end] keyrange
        acc* (atom [])]
    (doseq [i (range (- end start))]
      (swap! acc* (fn [n](conj n (.get-key-frequency tt (+ i start))))))
    @acc*))

(defn insert! 
  "Insert array into TuningTable
   tt        - TuningTable
   ary       - seq of floats
   location  - int, location in tt where insertion begins. 
               Any out of bounds indexes are automatically eliminated."
  ([dst ary location]
     (let [i* (atom 0)
           j* (atom location)
           limit (count ary)]
       (while (and (< @j* table-length)(< @i* limit))
         (.set-key-frequency! dst @j* (nth ary @i*))
         (swap! i* inc)
         (swap! j* inc)))
     dst))

(defn splice! [src keyrange dst location]
  "Splice portion of one TuningTabel into another
   splice! is a combination of extract and insert!
   src        - The source TuningTable
   keyrange   - seq [start end], region of src to be copied
   dst        - The destination TuningTable
   location   - int the location in dst where insertion is made"
  (let [ary (extract src keyrange)]
    (insert! dst ary (or location (first keyrange)))
    dst))
        

  
