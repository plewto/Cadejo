;; A ProgramBank is a map between MIDI program numbers and "program"
;; objects where a program is a simple map as returned by the
;; create-program function. Each program has the following fields: 
;;   :function-id - a keyword or nil indicating which function executes
;;                  to generate actual program data.
;;   :name        - a string giving the program's name
;;   :remarks     - an optional string holding remarks text
;;   :args        - A list of arguments passed to the program function.
;;                  If :function-id is nil the :args field is the 
;;                  literal program data as an association-list of
;;                  parameter/value pairs.
;;
;; A fixed number of upper program numbers are reserved for programs with
;; non-nil function-ids. All programs below this cutoff should have a
;; function-id of nil. Programs above the cutoff may have non-nil
;; function-ids and the function-registry is a map of permissible
;; functions. 
;;

(ns cadejo.midi.program-bank
  (:require [cadejo.config])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot])
  (:import java.io.FileNotFoundException) )

(def program-count 128)
(def reserved-slots 8)  ;; Number of program slots reserved for 
                        ;; functions. Reserved slots appear at end
                        ;; of the bank
(def start-reserved (- program-count reserved-slots))

(declare program-bank)

(defn- program-identity [& args] args)

(defn- assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum program-count) pnum)
      (umsg/warning (format "%s is not a valid MIDI program number" pnum))))

(defn create-program 
  ([function-id name remarks args]
     {:function-id (keyword function-id)
      :name (str name)
      :args args
      :remarks (str remarks)})
  ([function-id name args]
     (create-program function-id name "" args ))
  ([name args]
     (create-program nil name args)))

(def ^:private null-program (create-program nil "nil" "" []))

(defn dump-program [prog]
  (println "dump-program")
  (println (format "\t:function-id  %s" (:function-id prog)))
  (println (format "\t:name        '%s'" (:name prog)))
  (println (format "\t:remarks     '%s'" (:remarks prog)))
  (println (format "\t:args         %s" (:args prog))))

(defn diff-programs [p1 p2]
  (require 'clojure.set)
  (println "diff-programs")
  (let [id1 (:function-id p1)
        name1 (:name p1)
        rem1 (:remarks p1)
        data1 (ucol/alist->map (:args p1))
        id2 (:function-id p2)
        name2 (:name p2)
        rem2 (:remarks p2)
        data2 (ucol/alist->map (:args p2))]
    (if (and (= id1 id2)(= name1 name2)(= rem1 rem2)(= data1 data2))
      (println "Programs are identical")
      (do
        (if (not (= id1 id2))
          (println (format "\tid1 = %s  id2 = %s" id1 id2)))
        (if (not (= name1 name2))
          (println (format "\tname1 = '%s'  name2 = '%s'" name1 name2)))
        (if (not (= rem1 rem2))
          (println (format "\trem1 = '%s'  rem2 = '%s'" rem1 rem2)))
        (doseq [key (clojure.set/union (keys data1)(keys data2))]
          (let [v1 (get data1 key)
                v2 (get data2 key)]
            (if (not (= v1 v2))
              (println (format "\tdata key = %s  v1 = %s  v2 = %s" key v1 v2)))))))))
          
        



(defprotocol ProgramBank

  (data-format 
    [this]
    "Returns keyword")

  (parent!
    [this performance]
    "Set parent performance for this")

  (parent
    [this]
    "Returns parent performance for this")

  (bank-name!
    [this name]
    "Set bank's name")

  (bank-name 
    [this]
    "Return bank's name")

  (bank-remarks!
    [this rem]
    "Set bank's remarks text")

  (bank-remarks
    [this]
    "return bank's remarks text")

  (register-function!
    [this id function]
    "Add a function to the bank's function registry
     id - keyword unique identification
     function - a function with arbitrary arity and returns a list of 
     program parameter/value pairs")

  (registered-functions
    [this]
    "Returns list of registered function keywords")

  (init-bank! 
    [this]
    "Sets bank to initial condition")

  (pp-hook!
    [this pp]
    "Sets the pretty-printer hook function
     pp should have the form (pp pnum pname data remarks)
     and return a string. 
     It is convenient, but not necessary, that the format of 
     the pp function be machine readable")

  (pp-hook
    [this]
    "Returns the pretty-printer function. If a pp function has not been set
     returns a default function which returns an empty string")

  (store-program! 
    [this pnum prog]
    [this pnum fid pname remarks args]
    "Store program into designated slot.

     [this pnum prog] 
       pnum - int slot number, 0 <= pnum < start-reserved
       prog - map as returned by program function
     
     [this pnum prog]
       pnum    - int slot number as above
       fid     - function id, either keyword or nil
       pname   - String, the program name
       remarks - String, the program remarks text
       args    - List of arguments to function fid.
                 If fid is nil args is the literal program data as an 
                 association-list (:param1 value1 :param2 value2 ...)
                 If fid is a keyword args is a list of arguments
                 to the identified function

       gui-editor, if any, is -not- updated to reflect change.
       Returns prog")

  (current-program-number 
    [this]
    "Return the current program number as int, 0 <= cpn < 128")

  (current-program 
    [this]
    "Returns map representing current-program. 
     The map has the following keys :function-id :name :remarks :args")

  (current-program!
    [this prog]
    "Sets current-program to prog
     GUI editor, if any, is not updated
     synths are updated")

  (current-data
    [this]
    "Returns the current data as a map. Note that the current data may differ
     from the :args feild of the current program")     

  (current-data! 
    [this dmap]
    "Sets current-data to dmap
     GUI editor, if any, is not updated
     synths are updated")

  (set-param! 
    [this param value]
    "Set current-data parameter to value
     update all synths, gui editor is -not- updated")

  (store-current-data!
    [this pnum name remarks]
    [this pnum name]
    "Store current-data into slot pnum 

    pnum - int MIDI program number, 0 <= pnum < start-reserved
    name - string, program's name
    remarks - string program's remarks

    If 0 <= pnum < start-reserved the current data is saved into program slot 
    with the given name and remarks and the corresponding program map is 
    returned.  If pnum is outside this range the bank's contents remain 
    unchanged and nil is returned.

    current-program-number and current-program are updated
    the GUI editor, if any, is -NOT- updated.")

  (program 
    [this pnum make-current]
    [this pnum]
    "Returns the program map at slot pnum
     Returns nil if slot is empty.
     If make-current is true, make pnum the current-program-number
     make-current is true by default

     gui editor, if any, is -not- updated to reflect change
     synths or -not- updated.
     Returns either the new current-program or nil")

  (program-change
    [this pnum]
     "Update synths to match program at slot pnum

     [this pnum]
       pnum   - int, the MIDI program number 0 <= pnum < 128
       synths - list of SC synths   
 
     If slot pnum is empty do nothing and return nil.
     Otherwise make pnum the current-program-number and the associated program 
     the current-program, update synths, execute the pp-hook function. If a 
     bank-editor is defined call it's sync-ui! method, return true")

  (handle-event 
    [this event]
    "Same as program-change but takes a SC 'event' as argument")

  (write-bank 
    [this filename]
    "Write bank's contents to filename
     Returns filename if successful, returns nil on error")

  (read-bank!
    [this filename]
    "Set bank's data from file filename.
     The file must be a cadejo-bank file and have the same format as this.
     Returns filename if successful, returns nil on error")

  (clone 
    [this]
    "Return a new ProgramBank with identical contents to this.")

  (copy-state!
    [this other]
    "Sets the contents of this to match those ot other.
     other must be a cadejo ProgramBank with the same format as this
     Returns this if successful, returns nil on error.")

  (editor! 
    [this ed]
    "Set's the BankEditor for this. ed must implement the 
     cadejo.ui.midi.bank-editor/BankEditor protocol")

  (editor
    [this]
    "Returns the GUI editor for this, if no editor has been set return nil")

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))


(defn program-bank
  ([dformat](program-bank dformat (format "New %s bank" dformat) ""))
  ([dformat name remarks]
     (let [parent* (atom nil)
           name* (atom (str name))
           remarks* (atom (str remarks))
           current-program-number* (atom 0)
           current-program* (atom null-program)
           current-data* (atom {})
           function-registry* (atom {})
           programs* (atom (sorted-map))
           pp-hook* (atom (fn [& args] ""))
           editor* (atom nil)
           synths (fn [] (concat (.synths @parent*)
                                 (.voices @parent*)))

           bank (reify ProgramBank
                  
                  (data-format [this] dformat)

                  (parent! [this performance]
                    (reset! parent* performance))

                  (parent [this]
                    @parent*)

                  (bank-name! [this txt]
                    (reset! name* (str txt)))

                  (bank-name [this]
                    @name*)
                  
                  (bank-remarks! [this txt]
                    (reset! remarks* (str txt)))

                  (bank-remarks [this]
                    @remarks*)

                  (register-function! [this id f]
                    (swap! function-registry* (fn [n](assoc n (keyword id) f))))

                  (registered-functions [this]
                    (keys @function-registry*))

                  (init-bank! [this]
                    (doseq [pnum (range start-reserved)]
                      (swap! programs* (fn [n](dissoc n pnum))))
                    (reset! current-program-number* 0)
                    (reset! current-program* null-program)
                    (reset! current-data* {})
                    (reset! name* "New Bank")
                    (reset! remarks* ""))

                  (pp-hook! [this ppfn]
                    (reset! pp-hook* ppfn))

                  (pp-hook [this]
                    @pp-hook*)

                  (store-program! [this pnum prog]
                    (swap! programs* (fn [n](assoc n pnum prog)))
                    (reset! current-program-number* pnum)
                    (reset! current-program* prog)
                    (reset! current-data* (ucol/alist->map (:args prog)))
                    prog)

                  (store-program! [this pnum fid pname rem args]
                    (.store-program! this pnum (create-program fid pname rem args)))

                  (current-program-number [this]
                    @current-program-number*)

                  (current-program [this]
                    @current-program*)

                  (current-program! [this prog]
                    (reset! current-program* prog)
                    (.current-data! this (ucol/alist->map (:args prog))))

                  (current-data [this]
                    @current-data*)

                  (current-data! [this dmap]
                    (reset! current-data* dmap)
                    (apply ot/ctl (synths) (ucol/map->alist dmap)))

                  (set-param! [this param value]
                    (ot/ctl (synths) param value)
                    (swap! current-data* (fn [n](assoc n param value))))
                  
                  (store-current-data! [this pnum name remarks]
                    (let [prog (create-program nil 
                                               name 
                                               remarks 
                                               (ucol/map->alist @current-data*))]
                      (if (and (>= pnum 0)(< pnum start-reserved))
                        (do 
                          (reset! current-program-number* pnum)
                          (reset! current-program* prog)
                          (swap! programs* (fn [n](assoc n pnum prog)))
                          prog)
                        nil)))

                  (store-current-data! [this pnum name]
                    (.store-current-data! this pnum name ""))

                  (program [this pnum make-current]
                    (let [prog (get @programs* pnum)]
                      (if prog
                        (let [fid (:function-id prog)
                              f (get function-registry* fid nil)
                              args (:args prog)
                              data (ucol/alist->map (if f (f args) args))]
                          (if make-current 
                            (do
                              (reset! current-program-number* pnum)
                              (reset! current-program* prog)
                              (reset! current-data* data)))
                          prog)
                        nil)))

                  (program [this pnum]
                    (.program this pnum true))

                  (program-change [this pnum]
                    (let [prog (.program this pnum)
                          ped (.get-editor @parent*)]
                      (if prog
                        (let [fid (:function-id prog)
                              f (get @function-registry* fid nil)
                              args (:args prog)
                              data (if f (f args) args)]
                          (apply ot/ctl (cons (synths) data)) 
                          (if (cadejo.config/enable-pp)
                            (println (apply @pp-hook* (list pnum (:name prog) data (:remarks prog)))))
                          (reset! current-program-number* pnum)
                          (reset! current-program* prog)
                          (reset! current-data* (ucol/alist->map data))
                          (if ped (.sync-ui! ped))
                          true)
                        nil)))
               
                  (handle-event [this event]
                    (let [pnum (:data1 event)]
                      (.program-change this pnum)))

                  (write-bank [this filename]
                    (try
                      (let [rec {:file-type :cadejo-bank
                                 :data-format (.data-format this)
                                 :name (.bank-name this)
                                 :remarks (.bank-remarks this)
                                 :programs @programs*}]
                        (spit filename (pr-str rec))
                        filename)
                      (catch java.io.FileNotFoundException ex
                        (umsg/warning "FileNotFoundException"
                                      (format "ProgramBank.write-bank   bank-format = %s" (.data-format this))
                                      (format "filename \"%s\"" filename)))))

                  (read-bank! [this filename]
                    (try
                      (let [rec (read-string (slurp filename))
                            ftype (:file-type rec)
                            dformat (:data-format rec)]
                        (if (not (and (= ftype :cadejo-bank)
                                      (= dformat (.data-format this))))
                          (do
                            (umsg/warning "Wrong file type"
                                          (format "Can not read \"%s\"" filename)
                                          (format "as cadejo %s bank" (.data-format this)))
                            nil)
                          (do
                            (.init-bank! this)
                            (.bank-name! this (:name rec))
                            (.bank-remarks! this (:remarks rec))
                            (doseq [pnum (keys (:programs rec))]
                              (let [prog (get (:programs rec) pnum)]
                                (store-program! this pnum prog)))
                            (.program this (or (first (keys @programs*)) 0))
                            filename)))
                      (catch java.io.FileNotFoundException ex
                        (umsg/warning "FileNotFoundException"
                                      (format "ProgramBank.read-bank!    bank-format = %s" (.data-format this))
                                      (format "filename \"%s\"" filename))
                        nil)))

                 (clone [this]
                   (let [other (program-bank dformat @name* @remarks*)]
                     (doseq [[fid f](seq @function-registry*)]
                       (.register-function! other fid f))
                     (doseq [[pnum prog](seq @programs*)]
                       (.store-program! other pnum prog))
                     (.pp-hook! other (.pp-hook this))
                     (.program other (.current-program-number this))
                     other))

                 (copy-state! [this other]
                   (if (= (.data-format this)(.data-format other))
                     (do
                       (.init-bank! this)
                       (.bank-name! this (.bank-name other))
                       (.bank-remarks! this (.bank-remarks other))
                       (dotimes [pnum program-count]
                         (let [prog (.program other pnum)]
                           (if prog 
                             (.store-program! this pnum prog))))
                       this)
                     (umsg/warning (format "Can not copy %s bank into %s bank"
                                           (.data-format other)
                                           (.data-format this)))))
                 (editor! [this ed]
                   (reset! editor* ed))

                 (editor [this] @editor*)

                 (dump [this verbose depth]
                   (let [pad (cadejo.util.string/tab depth)
                         pad2 (str pad pad)
                         pad3 (str pad2 pad)]
                     (printf "%sProgramBank :format %s :name \"%s\"\n" 
                             pad (.data-format this)(.bank-name this))
                     (printf "%sRemarks: %s\n" pad2 (.bank-remarks this))
                     (doseq [[pnum prog](seq @programs*)]
                       (printf "%s[%3d] :fn %-8s :name %s\n"
                               pad2 pnum (:function-id prog)(:name prog)))
                     (if verbose
                       (do
                         (printf "%sRegistered functions %s\n"
                                 pad2 (keys @function-registry*))
                         (printf "%scurrent-program-number %s\n"
                                 pad2 @current-program-number*)
                         (printf "%scurrent-program:\n" pad2)
                         (printf "%s:function-id  %s\n" pad3 (:function-id @current-program*))
                         (printf "%s:name         %s\n" pad3 (:name @current-program*))
                         (printf "%s:remarks      %s\n" pad3 (:remarks @current-program*))
                         (printf "%s:args         %s\n" pad3 (:args @current-program*))
                         (printf "%spp-hook %s\n" 
                                 pad2 @pp-hook*)))
                     (println)))

                 (dump [this verbose]
                   (.dump this verbose 1))

                 (dump [this]
                   (.dump this false 1)) )]
       bank)))
