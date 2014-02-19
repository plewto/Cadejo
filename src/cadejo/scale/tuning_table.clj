(ns cadejo.scale.tuning-table
  "Defines tuning table in foprm of a map between MIDI key-number and 
   frequency."
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math :as math]))


(def step math/step)
(def cent math/cent)

(defn a69->c0 
  "Returns frequency of MIDI key 0 relative to that of key 69
   Key 69 (A4)
   [a440] - frequency of key 69 in hertz.
   [a440 cents] - frequency of key 60 in Hz and cents.
   [] - use standard tuning of A440"
  ([a440]
     (* a440 (math/expt step -69)))
  ([a440 cents]
     (a69->c0 (* a440 (math/expt cent cents))))
  ([]
     (a69->c0 440.0)))

(defn wrap-frequency 
  "Returns frequency f such that mn <= f <= mx.
   f is transposed by octaves as needed."
  ([f mn mx]
     (let [rs (ref f)]
       (dosync
        (while (< @rs mn)(ref-set rs (* @rs 2.0)))
        (while (> @rs mx)(ref-set rs (/ @rs 2.0))))
       (float @rs))))


(defprotocol TuningTableProtocol 

  (set-frequency! 
    [this keynum freq]
    "Sets frequency for specific MIDI key.
     keynum - The MIDI keynumber
     freq   - frequency in Hz. freq will be transposed by octaves 
              such that it will be within the table's limits.
     Returns frequency after any transposition.")

  (get-frequency 
    [this keynum]
    "Returns frequency for MIDI key number")
  
  (set-property! 
    [this key value]
    "Sets key/value property")

  (get-property
    [this key]
    "Returns value for property key.")

  (set-name! 
    [this name]
    "Sets table's name")
  
  (get-name 
    [this])
  
  (set-remarks! 
    [this text]
    "Sets optional remarks text.")
  
  (get-remarks 
    [this])
  
  (set-notes-per-octave! 
    [this npo]
    "Sets the number of notes per octave.
     NOTE: This value is used for user information only and does not
     actually alter the table contents.")
  
  (get-notes-per-octave 
    [this])
  
  (set-octave-size!
    [this r]
    "Sets the ratio used for an octave, which is almost always 2.0.
     NOTES: This value is used for user information only and does not
     actually alter the table contents.")
  
  (get-octave-size 
    [this])

  (set-frequency-limits! 
    [this mn mx]
    "Sets the upper and lower frequency limits. As set-frequency! is called
     the frequency argument f is automatically transposed by octaves such 
     that the actual frequency used is mn <= f <= mx.")

  (get-frequency-limits 
    [this]
    "Returns the frequency limits in the form of a list (min max)")
  
  (set-reference! 
    [this keynum freq]
    "Sets the reference MIDI key number and frequency. The defaults are 
     keynumber 69 'A4' with frequency 440.0.")

  (get-reference 
    [this]
    "Returns the reference keynumber and frequency in the form of a map
     {:refkey n :reffreq f}")

 (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(deftype TuningTable [properties* table*]
  TuningTableProtocol

  (set-frequency! [this keynum freq]
    (let [limits (.get-frequency-limits this)
          min (first limits)
          max (second limits)
          f (wrap-frequency freq min max)]
      (swap! table* (fn [n](assoc n keynum f)))
      f))

  (get-frequency [this keynum]
    (nth @table* keynum))

  (set-property! [this key value]
    (swap! properties* (fn [n](assoc n key value))))

  (get-property [this key]
    (let [value (get @properties* key :fail)]
      (if (= value :fail)
        (do 
          (umsg/warning (format "TuningTable does not have property %s" key))
          nil)
        value)))
  
  (set-name! [this name]
    (.set-property! this :name (str name)))

  (get-name [this]
    (.get-property this :name))

  (set-remarks! [this text]
    (.set-property! this :remarks (str text)))

  (get-remarks [this]
    (.get-property this :remarks))

  (set-notes-per-octave! [this n]
    (.set-property! this :notes-per-octave n))

  (get-notes-per-octave [this]
    (.get-property this :notes-per-octave))

  (set-octave-size! [this r]
    (.set-property! this :octave-size r))

  (get-octave-size [this]
    (.get-property this :octave-size))

  (set-frequency-limits! [this min max]
    (.set-property! this :limits (list min max)))

  (get-frequency-limits [this]
    (.get-property this :limits))

  (set-reference! [this keynum freq]
    (.set-property! this :reference {:refkey keynum :reffreq freq}))

  (get-reference [this]
    (.get-property this :reference))

  (dump [this verbose depth]
    (let [pad1 (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab (inc depth))]
      (printf "%sTuningTable %s\n" pad1 (.get-name this))
      (if verbose
        (do 
          (if (not (= (.get-remarks this) ""))
            (printf "%sremakrs          : %s\n" pad2 (.get-remarks this)))
          (printf "%snotes per octave : %s\n" pad2 (.get-notes-per-octave this))
          (printf "%soctave size      : %s\n" pad2 (.get-octave-size this))
          (printf "%sfrequency limits : %s\n" pad2 (.get-frequency-limits this))
          (printf "%sreference        : %s\n" pad2 (.get-reference this))))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this true)))

(defn tuning-table []
  (let [tt (TuningTable. (atom {})
                         (let [acc* (atom [])]
                           (dotimes [n 128]
                             (swap! acc* (fn [n](conj n 440))))
                           acc*))]
    (.set-name! tt "")
    (.set-remarks! tt "")
    (.set-notes-per-octave! tt 0)
    (.set-octave-size! tt 0)
    (.set-frequency-limits! tt 16 13000)
    (.set-reference! tt 69 440)
    tt))

(defn set-eqtemp-scale! 
  "Sets tuning table to equal-temperment scale.
   [ttab ref-key ref-freq notes-per-octave octave-size]
   [ttab notes-per-octave]
   [ttab]
   ttab             - An instance of TunigTable
   ref-key          - The reference MIDi key number, default 69
   ref-freq         - The frequency in Hz of ref-key, default 440.0
   notes-per-octave - default 12
   octave-size      - The ration of an octave, default 2.0
   Returns ttab"
  ([ttab ref-key ref-freq notes-per-octave octave-size]
     (let [ratio (math/expt octave-size (/ 1.0 notes-per-octave))
           transpose (fn [x](* ref-freq (math/expt ratio x)))
           xpose* (atom (- ref-key))]
       (dotimes [keynum (count @(.table* ttab))]
         (.set-frequency! ttab keynum (transpose @xpose*))
         (swap! xpose* inc))
       (.set-name! ttab "eqtemp")
       (.set-notes-per-octave! ttab notes-per-octave)
       (.set-octave-size! ttab octave-size)
       (.set-reference! ttab ref-key ref-freq)
       ttab))
  ([ttab notes-per-octave]
     (set-eqtemp-scale! ttab 69 440.0 notes-per-octave 2.0))
  ([ttab]
     (set-eqtemp-scale! ttab 12)))

(def default-tuning-table
  (set-eqtemp-scale! (tuning-table)))


(defn get-tuning-table 
  ([scale ref-freq]
     (let [template (get scale :template)
           c0 (a69->c0 ref-freq)
           ttab (tuning-table)]
       (.set-name! ttab (str (get scale :name "?")))
       (.set-notes-per-octave! ttab (get scale :notes-per-octave 12))
       (.set-octave-size! ttab (get scale :octave-size 2.0))
       (.set-reference! ttab 69 ref-freq)
       (dotimes [keynum (count @(.table* ttab))]
         (let [octave (int (/ keynum (count template)))
               ratio (nth template (rem keynum (count template)))
               freq  (* c0 ratio (math/expt 2 octave))]
           ;; (println (format "DEBUG keynum %3d   octave %2s   ratio %5s   freq %f"
           ;;                  keynum octave ratio freq))
           (.set-frequency! ttab keynum freq)))
       ttab))
  ([scale]
     (get-tuning-table scale 440.0)))
           
           
 
