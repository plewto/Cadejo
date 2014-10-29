(ns cadejo.midi.pbank
  (:use [cadejo.util.trace])
  (:require [cadejo.midi.program])
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.program])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.string])
  (:require [overtone.core :as ot])
  (:import java.io.FileNotFoundException)
)

(def program-count 128)

(defn- assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum program-count) pnum)
      (umsg/warning (format "%s is not a valid MIDI program number" pnum))))


(defprotocol PBank

  (data-format 
    [this]
    "Returns keyword indicating program data format")

  (parent!
    [this performance]
    "Sets parent performance of this")

  (parent
    [this]
    "Returns parent performance of this or nil") 

  (editor! 
    [this ed]
    "Sets GUI bank editor for this")
  
  (editor
    [this]
    "Returns GUI editor or nil")

  (pp-hook!
    [this pp]
    "Sets pretty-printer hook function
     pp should have the lambda form (pp slot pname data remarks)
     and return a String.")
  
  (pp-hook
    [this]
    "Returns the pp-hook function or nil")
  
  (bank-name!
    [this name]
    "Sets name for this")

  (bank-name
    [this]
    "Returns name of this")

  (bank-remarks!
    [this text]
    "Sets remarks text for this")

  (bank-remarks
    [this]
    "returns remarks text of this")

  (init! 
    [this]
    "Initialize this bank
     Bank name, remarks, current-program and programs list are set to 
     initial values. Synths and editors are not updated.
     Returns this.")

  (current-slot
    [this]
    "Returns the current program slot or nil
     The slot is an integer MIDI program number.")

  (current-program!
    [this prog]
    "Sets current-program
     synths and instument-editor are updated.")

  (current-program
    [this]
    "Returns the current-program, a instance of 
     cadejo.midi.program/Program or nil")

  (current-data
    [this]
    "Returns current-prgoram data or empty-map {}")

  (set-param!
    [this param value]
    "Sets parameter value for current-program 
     Active synths are updated to match the new program data,
     GUI editor is -not- updated and the current-program is marked
     as 'not saved'
     Returns the new program data or nil")

  (current-program-saved? 
    [this]
    "Predicate returns true if current program has modifications which 
     have not been stored into the programs list.")

  (programs
    [this]
    "Returns a sorted map of all programs. The map keys are integer MIDI
     program numbers, the map values are instance of 
     cadejo.midi.program/Program")

  (recall
    [this slot]
    [this]
    "Mark the indicated slot as 'current'. If slot is not specified
     use the current slot number. 
     All active synths are updated to match the new current data
     The current program is marked as 'saved'
     The editor is -NOT- updated.
     Return current-data or nil")

  (program-change                       ; synths updated, editor updated
    [this ev]
    "Process MIDI program change event.
     If the bank contains a program for the indicated program-number,
     make it the current-program. 
     All synths and the GUI editor are updated  to match the new 
     current-program.
     Returns current-data")

  (store!                               ; editor is not updated
    [this slot program]
    [this slot]
    [this]
    "Stores program into bank slot.
     slot - int MIDI program number, if not specified use current slot number
     program - instance of cadejo.midi.program/Program, if not specified
               use current program.
     The current slot and program are updated and the current program 
     is marked as 'saved'. Returns program")

  (read-bank! 
    [this filename]
    "Update bank's content from file")

  (write-bank
    [this filename]
    "Save bank data to file")

  (clone
    [this]
    "Returns duplicate copy of this")

  (copy-state! 
    [this other]
    "Copies state of other into this.
     Returns this or nil.")

  (dump 
    [this verbose depth]
    [this]))
  
  

(defn pbank 
  ([dformat]
     (pbank (keyword dformat) 
            (format "New %s bank" (name (keyword dformat))) 
            ""))
  ([dformat bnk-name bnk-remarks]
     (let [parent* (atom nil)
           programs* (atom (sorted-map))
           pp-hook* (atom (fn [&args] ""))
           editor* (atom nil)
           name* (atom (str bnk-name))
           remarks* (atom (str bnk-remarks))
           current-slot* (atom nil)
           current-program* (atom nil)
           unsaved-data* (atom false)
           synths (fn []
                    (if @parent*
                      (concat (.synths @parent*)
                              (.voices @parent*))
                      []))
           pb (reify PBank
                
                (data-format [this](keyword dformat))
                
                (parent! [this performance]
                  (reset! parent* performance))

                (parent [this] @parent*)
                
                (editor! [this ed]
                  (reset! editor* ed))
                
                (editor [this] @editor*)
                
                (pp-hook! [this hfn]
                  (reset! pp-hook* hfn))
                
                (pp-hook [this] @pp-hook*)
                
                (bank-name! [this text]
                  (reset! name* (str text)))
                
                (bank-name [this] @name*)
                
                (bank-remarks! [this text]
                  (reset! remarks* (str text)))
                
                (bank-remarks [this]
                  @remarks*)
                
                (init! [this]
                  (.bank-name! this (format "New %s Bank" (name (.data-format this))))
                  (.bank-remarks! this "")
                  (reset! current-slot* nil)
                  (reset! current-program* nil)
                  (reset! unsaved-data* false)
                  (reset! programs* (sorted-map))
                  this)
                
                (current-slot [this] @current-slot*)
                
                (current-program [this] @current-program*)
                
                (current-program! [this prog]
                  (reset! current-program* prog)
                  (reset! unsaved-data* false)
                  (apply ot/ctl (synths)(ucol/map->alist (.data prog)))
                  (if @editor* (.sync-ui! @editor*))
                  prog)

                (current-data [this]
                  (let [p (.current-program this)]
                    (if p 
                      (.data p)
                      {})))

                (set-param! [this param value]
                  (let [prog (.current-program this)]
                    (if prog
                      (do 
                        (.set-param! prog param value)
                        (apply ot/ctl (synths)(ucol/map->alist (.data prog)))
                        (reset! unsaved-data* true)
                        (.data prog))
                      nil)))
                
                (current-program-saved? [this]
                  (not @unsaved-data*))
                
                (programs [this] @programs*)
                
                (recall [this slot]
                  (if (assert-midi-program-number slot)
                    (let [p (get @programs* slot)]
                      (if p
                        (let [data (.data p)]
                          (apply ot/ctl (synths)(ucol/map->alist data))
                          (reset! current-slot* slot)
                          (reset! current-program* (.clone p))
                          (reset! unsaved-data* false)
                          (if (and (config/enable-pp) @pp-hook*)
                            (let [pname (.program-name p)
                                  premarks (.program-remarks p)]
                              (println (@pp-hook* slot pname data premarks))))
                          data)
                        nil))
                    nil))
                
                (recall [this]
                  (let [s @current-slot*]
                    (if s 
                      (.recall this s)
                      nil)))
                
                (program-change [this event]
                  (let [slot (:data1 event)
                        rcflag (.recall this slot)
                        ed @editor*]
                    (if ed (.sync-ui! ed))
                    @current-program*))
                
                (store! [this slot program]
                  (if (assert-midi-program-number slot)
                    (do 
                      (swap! programs* (fn [n](assoc n slot program)))
                      (reset! current-slot* slot)
                      (reset! current-program* program)
                      (reset! unsaved-data* false)
                      program)
                    nil))
                
                (store! [this slot]
                  (if @current-program*
                    (.store! this slot @current-program*)))
                
                (store! [this]
                  (.store! this @current-slot*))

                (read-bank! [this filename]
                  (try
                    (let [rec (read-string (slurp filename))
                          ftype (:file-type rec)
                          dform (:data-format rec)]
                      (if (not (and (= ftype :cadejo-bank)
                                    (= dform (.data-format this))))
                        (do
                          (umsg/warning "Wrong file type"
                                        (format "Can not read \"%s\"" filename)
                                        (format "as cadejo %s bank" (.data-format this)))
                          nil)
                        (let [progs (:programs rec)]
                          (.init! this)
                          (.bank-name! this (:name rec))
                          (.bank-remarks! this (:remarks rec))
                          (doseq [[slot pmap] (seq progs)]
                            (let [prog (cadejo.midi.program/program)]
                              (.from-map! prog pmap)
                              (.store! this slot prog)))
                          (.program-change this {:data1 (first (keys @programs*))})
                          filename)))
                    (catch java.io.FileNotFoundException ex
                      (umsg/warning "FileNotFoundException"
                                    (format "PBank.read-bank!  bank-format = %s" (.data-format this))
                                    (format "filename \"%s\"" filename))
                      nil)))
                
                (write-bank [this filename]
                  (try
                    (let [progs (let [acc* (atom {})]
                                  (doseq [[k p] @programs*]
                                    (swap! acc* (fn [q](assoc q k (.to-map p)))))
                                  @acc*)
                          rec {:file-type :cadejo-bank
                               :data-format (.data-format this)
                               :name (.bank-name this)
                               :remarks (.bank-remarks this)
                               :programs progs}]
                      (spit filename (pr-str rec))
                      filename)
                    (catch java.io.FileNotFoundException ex
                      (umsg/warning "FileNotFoundException"
                                    (format "PBank.write-bank!  bank-format = %s" (.data-format this))
                                    (format "filename \"%s\"" filename))
                      nil)))
                
                (clone [this]
                  (let [other (pbank (.data-format this)
                                     (.bank-name this)
                                     (.bank-remarks this))]
                    (doseq [[slot prog](seq @programs*)]
                      (.store! other slot (.clone prog)))
                    (.pp-hook! other (.pp-hook this))
                    other))
                
                (copy-state! [this other]
                  (if (= (.data-format this)(.data-format other))
                    (let [src-programs (.programs other)]
                      (.init! this)
                      (.bank-name! this (.bank-name other))
                      (.bank-remarks! this (.bank-remarks other))
                      (dotimes [slot program-count]
                        (let [p (get src-programs slot)]
                          (if p 
                            (.store! this slot (.clone p)))))
                      this)
                    (do 
                      (umsg/warning (format "Can not copy %s bank to %s bank"
                                            (.data-format other)
                                            (.data-format this)))
                      nil)))
                
                (dump [this verbose depth]
                  (let [pad (cadejo.util.string/tab depth)
                        pad2 (str pad pad)]
                    (printf "%sPBank %s  name '%s'" pad (.data-format this)(.bank-name this))
                    (if verbose
                      (do 
                        (printf "%sRemarks %s\n" pad2 (.bank-remarks this))
                        (printf "%sparent  %s\n" pad2 (.parent this))
                        (printf "%spp-hook %s\n" pad2 (.pp-hook this))
                        (printf "%seditor %s\n" pad2 (.editor this))
                        (printf "%scurrent-slot %s\n" pad2 @current-slot*)
                        (printf "%sunsaved-data %s\n" pad2 @unsaved-data*)
                        (doseq [k (keys @programs*)]
                          (let [p (get @programs* k)]
                            (printf "%s[%3s] '%s'\n" pad2 k (.program-name p))))
                        ))
                    (println)))
                
                (dump [this]
                  (.dump this true 1)))]
       pb)))
