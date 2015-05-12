(ns cadejo.midi.program
  (:require [cadejo.util.string]))

(declare program)

(defprotocol Program

  "A Program object holds information for a single MIDI program
     - name, string 
     - remarks, string
     - data, map"

  (to-map 
    [this]
    "Return contents of this as map")

  (from-map! 
    [this map]
    "Convert map contents to Program fields")

  (program-name!
    [this text]
    "Sets program name, returns text")

  (program-name
    [this])

  (program-remarks!
    [this text]
    "Sets remarks text")

  (program-remarks
    [this])

  (data! 
    [this data]
    "Sets program data 
     data - map with keyword keys and float values")

  (data
    [this]
    "Returns data map")

  (set-param!
    [this param value]
    "Set single data parameter
     param - keyword
     value - float")

  (clone 
    [this])

  (dump 
    [this verbose depth]
    [this depth]
    [this]))

(defn program 
  ([]
     (program "" {}))
  ([name data]
     (program name "" data))
  ([name remarks data]
     (let [name* (atom (str name))
           remarks* (atom (str remarks))
           data* (atom data)]
       (reify Program

         (to-map [this]
           {:name @name*
            :reamrks @remarks*
            :data @data*})

         (from-map! [this map]
           (.program-name! this (get map :name "?"))
           (.program-remarks! this (get map :remarks "?"))
           (reset! data* (get map :data {}))
           this)

         (program-name! [this text]
           (reset! name* (str text)))

         (program-name [this] @name*)

         (program-remarks! [this text]
           (reset! remarks* (str text)))

         (program-remarks [this] @remarks*)

         (data! [this d]
           (reset! data* d))

         (data [this] @data*)

         (set-param! [this param value]
           (swap! data* (fn [n](assoc n param value))))

         (clone [this]
           (program @name* @remarks* @data*))

         (dump [this verbose depth]
           (let [pad (cadejo.util.string/tab depth)
                 pad2 (str pad pad)]
             (printf "%sProgram \"%s\"\n" pad @name*)
             (if verbose
               (printf "%sRemarks %s\n" pad2 @remarks*))))

         (dump [this depth]
           (.dump this false depth))

         (dump [this]
           (.dump this 0))))))

(defn dump-data [program]
  (.dump program true 0)
  (let [data (.data program)]
    (doseq [k (sort (keys data))]
      (let [v (get data k)]
        (printf "[%-16s] --> %-16s %s\n" k v (type v))))
    (println)))
