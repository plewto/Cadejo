;; 2014.05.17  
;; Added function registry to be used as part bank serialization.
;; There is no obvious and easy way to serialize Clojure functions 
;; The registry is to contain functions accessible by unique key. During
;; serialization only the function key is saved. On de-serialization the key
;; is used to extract the proper function from the registry.
;; 
;; 2014.05.18
;; Added program-number mapping
;;

(ns cadejo.midi.bank
  "Defines MIDI program bank"
  (:require [cadejo.midi.program])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot]))

(defprotocol BankProtocol 

  "Defines bank of Program data.
Bank slots are indexed by MIDI program numbers and may contain 
explicit synth parameter list or functions which return parameter list" 

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

  (use-function!
    [this pnum id fn-id args remarks]
    [this pnum id fn-id args]
    [this pnum id fn-id]
    "Similar to set-program! but associates function with MIDI program 
     number pnum
     pnum - MIDI program number
     id - The program name
     fn-id - function-registry id. See register-function!
     args - optional arguments passed to the function
     remarks - optional remarks text

     If fn-id matches an entry in the function-registry the bank slot is 
     updated and true is returned

     If no such function exist in the registry no change is made to the 
     bank contents, a warning message is displayed and false is returned")

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
     patches may then be cut and pasted to the bank data file.")

  (data 
    [this pnum resolve]
    [this pnum]
    "Returns the data for MIDI program pnum. If no such data exists return nil.
     If the data is 'functional' (see set-data!) and resolve is true return 
     the function and any of its arguments un-evaluated, otherwise evaluate
     prior to returning.")
  
  (register-function! 
    [this id fnobj remarks]
    [this id fnobj]
    "Add a function to the bank function-registry.
     id - unique keyword to identify the function
     fnobj - The function
     remarks - optional remarks text")

  (get-function 
    [this id]
    "Return function with id from function-registry.
     If no such function exists display warning and return nil")

  (registered-functions? 
    [this]
    "Diagnostics displays list of registered functions.")

  (clear-program-number-map!
    [this]
    "Sets program number mapping to identity a --> a")

  (map-program-number!
    [this a b]
    "Set MIDI program number a to map to b.
     a and b must be valid MIDI program number  
     0 <= a,b <= 127
     Return true if a and b valid, false otherwise")

  (handle-event 
    [this event synths])

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(defn assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum 128) pnum)
      (umsg/warning (format "%s is not a valid MIDI program number" pnum))))


(deftype Bank [dformat id* remarks* filename* 
               function-registry* 
               program-number-map*
               programs*
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
      (let [pobj (cadejo.midi.program/program dformat id remarks data)]
        (swap! programs* (fn [n](assoc n pnum pobj)))
        true)
      false))

  (set-program! [this pnum id data]
    (.set-program! this pnum id "" data))

  (use-function! [this pnum id fn-id args remarks]
    (let [f (.get-function this fn-id)]
      (if f 
        (.set-program! this pnum id remarks (cons f args))
        false)))

  (use-function! [this pnum id fn-id args]
    (.use-function! this pnum id fn-id args ""))

  (use-function! [this pnum id fn-id]
    (.use-function! this pnum id fn-id '() ""))

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
  
  (register-function! [this id fnobj remarks]
    (swap! function-registry* 
           (fn [n](assoc n id {:function fnobj :remarks (str remarks)}))))

  (register-function! [this id fnobj]
    (.register-function! this id fnobj ""))

  (get-function [this id]
    (let [rs (get @function-registry* id)]
      (if rs
        (:function rs)
        (do
         (umsg/warning (format "%s bank does not contain function %s"
                               (.data-format this) id))
         nil))))

  (registered-functions? [this]
    (printf ";; %s bank registered functions:\n" (.data-format this))
    (doseq [k (sort (keys @function-registry*))]
      (let [p (get @function-registry* k)]
        (printf ";;\t%-12s %s\n" k (:remarks p))))
    (println))
  
  (clear-program-number-map! [this]
    (swap! program-number-map* (fn [n](into '[] (range 128)))))
    
  (map-program-number! [this a b]
    (if (and (assert-midi-program-number a)
             (assert-midi-program-number b))
      (do 
        (swap! program-number-map* (fn [n](assoc n a b)))
        true)
      false))
 
  (handle-event [this event synths]
    (let [pnum (nth @program-number-map* (:data1 event))
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
          pad2 (cadejo.util.string/tab depth2)
          pad3 (cadejo.util.string/tab (inc depth2))]
      (printf "%s%s Program Bank %s\n" pad dformat (.id this))
      (if verbose
        (do 
          (if (not (= (.remarks this) ""))
            (printf "%sremarks  : %s\n" pad2 (.remarks this)))
          (printf "%sfilename : \"%s\"\n" pad2 @filename*)
          (doseq [pnum (sort (keys @programs*))]
            (let [pobj (get @programs* pnum)]
              (printf "%s[%03d] %s\n" pad2 pnum (.id pobj))))
         (doseq [a (range 128)]
           (let [b (nth @program-number-map* a)]
             (if (not (= a b))
               (printf "%smap program [%03d] --> [%03d]\n" pad2 a b))))
          ))))

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
                      (atom {})     ; function-registry
                      (atom [])     ; program-number map
                      (atom {})     ; programs
                      (atom (fn [event pid]
                               (if pid
                                 (printf ";; %s Program chan [%02d] \"%s\"\n"
                                         data-format 
                                         (:channel event)
                                         pid))))
                      (atom (fn [event pid data])))]
       (.clear-program-number-map! bnk)
       bnk))
  ([data-format id]
     (bank data-format id ""))
  ([data-format]
     (bank data-format data-format)))
