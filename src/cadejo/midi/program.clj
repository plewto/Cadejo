;; DEPRECIATED 2014.05.21
;;
(ns cadejo.midi.program
  "Defines encapsulation of MIDI program change data"
  (:require [cadejo.util.string]))

(defprotocol ProgramProtocol 

  (data-format 
    [this]
    "Returns a unique keyword identifying the data format")

  (set-id! 
    [this id]
    "Sets the name of this program.")

  (id 
    [this]
    "Returns the program name.")

 (set-remarks! 
   [this text]
   "Sets optional remarks text.")

 (remarks 
   [this]
   "Returns optional remarks text")

 (set-data!
   [this data]
   "Sets program data.
    data should be either an assoclist of the form 
    (:param1 value1 :param2 value2 ...) or a list of the form 
    (fn args...) 
    If the first element of data is a function then upon a program change
    the function is called with remaining data elements as arguments. The 
    results of calling fn are then used in place of an explicit parameter list.")

 (data
   [this resolve]
   [this]
   "Returns program data.
    If the first element of data is a function and resolve is true the call
    the function and return its results (see set-data!) If resolve is false
    return the data non evaluated.")

 (dump 
   [this verbose depth]
   [this verbose]
   [this]))


(deftype Program [dformat id* remarks* data*]
  ProgramProtocol 

  (data-format [this] dformat)

  (set-id! [this id]
    (swap! id* (fn [n](str id))))

  (id [this] @id*)

  (set-remarks! [this text]
    (swap! remarks* (fn [n] (str text))))

  (remarks [this] @remarks*)

  ;; NOTE data must always be in form of seq 
  ;; If function is to be used [fn arg1 arg2 arg3 ....]
  ;;
  (set-data! [this data]
    (swap! data* (fn [n] data)))

  (data [this resolve]
    (let [dobj @data*]
      (if (and (fn? (first dobj)) resolve)
        (apply (first dobj)(rest dobj))
        dobj)))

  (data [this]
    (.data this true))

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)]
      (printf "%s%s program %s\n"
              pad dformat @id*)
      (if verbose
        (let [rem @remarks*]
          (printf "%sdata type : %s\n" pad2 (type @data*))
          (if (not (= rem ""))
            (printf "%sremarks   : %s\n" pad2 rem))))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false 0)))
    

(defn program 
  "Creates new instance of Program
   form - keyword indicating data format, usually an instruments name.
   id - The program name
   remarks - optional remarks text
   data - the program data, either an assoclist of form 
          (:param1 value1 :param2 value2 ...) or a function with arguments
          with the form (fn arg1 arg2 ...)"
  ([form id remarks data]
     (Program. (keyword form)
               (atom (str id))
               (atom (str remarks))
               (atom data)))
  ([form id data]
     (program form id "" data)))









     


