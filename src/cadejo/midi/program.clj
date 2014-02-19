
(ns cadejo.midi.program
  "Defines program bank and response to MIDI program-change"
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot]))


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



(defprotocol BankProtocol 

  "Defines bank of Program data."

  (data-format 
    [this]
    "Returns a keyword indicating the data format.")

  (set-id!
    [this id]
    "Sets the bank name.")

  (id
    [this]
    "Returns the bank name.")

  (set-remarks!
    [this text]
    "Sets optional remarks text.")
  
  (remarks 
    [this]
    "Returns remarks text.")
  
  (save-bank 
    [this filename]
    [this]
    "Save bank to filename. If filename not specified save to 
     previously used filename.
     NOTE: save-bank is not currently implemented.
     ISSUE: How do we save function as data?")
  
  (load-bank!
    [this filename]
    [this]
    "Load bank from filename. If filename not specified use 
     previously used filename.
     NOTE: load-bank is not currently implemented.
     ISSUE: On implementation must make sure format matches.")

  (set-program! 
    [this pnum id remarks data]
    [this pnum id data]
    "Sets program data for MIDI program number pnum.
     Any existing data is overwritten.")

  (remove-program!
    [this pnum]
    "Removes program at MIDI program number pnum.")

  (move-program! 
    [this src dst]
    "Move program data from src to dst, where src and dst are MIDI
     program numbers.")

  (clear-data! 
    [this]
    "Remove all programs from bank.")

  (set-notification-hook! 
    [this hfn]
    "Sets the hook function used to notify user of a program-change.
     The hook function should take two arguments (hfn event pid)
     where event is the MIDI event an pid is the selected program's name.
     As a side effect hfn should then print something meaningful to the 
     terminal. The output should be both human and machine readable.
     (A Clojure comment is best) See also pp-hook")

  (set-pp-hook!
    [this hfn]
    "Sets the hook function to pretty-print selected program data. 
     The function should take three arguments and have the form 
     (hfn event pid data)
     where event is the MIDI program-change event, pid is the 
     selected program's name and data is the program data 
     as an assoc list (:param1 value1 :param2 value2 ...)
     
     As a side effect hfn should then print meaningful information
     about the program data. The format should be both human and machine 
     readable. 

     The purpose of having machine readable output is that it is anticipated
     that some programs will generate random patch data. Any interesting 
     patches can then be cut and pasted over to the banks data file for 
     future use.")

  (data 
    [this pnum resolve]
    [this pnum]
    "Returns the data for MID program pnum. If no such data exists return nil.
     If the data is 'functional' (see set-data!) and resolve is true return 
     the function and any of its arguments un-evaluated, otherwise evaluate
     prior to returning.")
  
  (handle-event 
    [this event synths])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(defn assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum 128) pnum)
      (umsg/warning (format "%s is not a valid MIDI program number" pnum))))

(deftype Bank [dformat id* remarks* filename* programs*
               notification-hook* pp-hook*]
  
  BankProtocol

  (data-format [this] dformat)

  (set-id! [this id]
    (swap! id* (fn [n](str id))))

  (id [this] @id*)

  (set-remarks! [this text]
    (swap! remarks* (fn [n] (str text))))

  (remarks [this] @remarks*)

  (save-bank [this filename]
    (umsg/warning "cadejo.midi.program.Bank.save-program not implemented")
    (swap! filename* (fn [n] filename))
    nil)

  (save-bank [this]
    (if (not (= @filename* ""))
      (.save-bank this @filename*)
      (umsg/warning (format "Bank %s filename not set" @id*))))

  (load-bank! [this filename]
    (umsg/warning "cadejo.midi.program.Bank.load-program not implemented")
    (swap! filename* (fn [n] filename))
    nil)

  (load-bank! [this]
    (if (not (= @filename* ""))
      (.load-bank! this @filename*)
      (umsg/warning (format "Bank %s filename not set" @id*))))

  (set-program! [this pnum id remarks data]
    (if (assert-midi-program-number pnum)
      (let [pobj (program dformat id remarks data)]
        (swap! programs* (fn [n](assoc n pnum pobj)))
        true)
      false))

  (set-program! [this pnum id data]
    (.set-program! this pnum id "" data))

  (remove-program! [this pnum]
    (if (assert-midi-program-number pnum)
      (do 
        (swap! programs* (fn [n](dissoc n pnum)))
        true)
      false))

  (move-program! [this src dst]
    (if (and (assert-midi-program-number src)
             (assert-midi-program-number dst))
      (let [srcobj (get @programs* src)]
        (if srcobj
          (do 
            (swap! programs* (fn [n](assoc n dst srcobj)))
            (.remove-program! this src)
            true)
          false))
      false))

  (clear-data! [this]
    (swap! programs* (fn [n] {})))

  (set-notification-hook! [this hfn]
    (swap! notification-hook* (fn [n] hfn)))

  (set-pp-hook! [this hfn]
    (swap! pp-hook* (fn [n] hfn)))

  (data [this pnum resolve]
    (if (assert-midi-program-number pnum)
      (let [pobj (get @programs* pnum)]
        (and pobj (.data pobj resolve)))
      nil))

  (data [this pnum]
    (.data this pnum true))
  
  (handle-event [this event synths]
    (let [pnum (:data1 event)
          pobj (get @programs* pnum)]
      (if pobj
        (let [pid (.id pobj)
              data (.data pobj)]
          (apply ot/ctl (cons synths data))
          (@notification-hook* event pid)
          (@pp-hook* event pid data)))))
          
  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)]
      (printf "%s%s Program Bank %s\n" pad dformat (.id this))
      (if verbose
        (do 
          (if (not (= (.remarks this) ""))
            (printf "%sremarks  : %s\n" pad2 (.remarks this)))
          (printf "%sfilename : \"%s\"\n" pad2 @filename*)
          (doseq [pnum (sort (keys @programs*))]
            (let [pobj (get @programs* pnum)]
              (printf "%s[%03d] %s\n" pad2 pnum (.id pobj))))))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this true 0)))
    

(defn bank
  "Creates new instance of Bank."
  ([data-format id remarks]
     (let [bnk (Bank. (keyword data-format)
                      (atom (str id))
                      (atom (str remarks))
                      (atom "")     ; filename
                      (atom {})     ; programs
                      (atom (fn [event pid]
                               (if pid
                                 (printf ";; %s Program chan [%02d] \"%s\"\n"
                                         data-format 
                                         (:channel event)
                                         pid))))
                      (atom (fn [event pid data])))]
                              
       bnk))
  ([data-format id]
     (bank data-format id ""))
  ([data-format]
     (bank data-format data-format)))
