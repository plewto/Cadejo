;; The cleverly named cadejo.midi.am-bank replaces the depreciated
;; cadejo.midi.program and cadejo.midi.bank name-spaces.
;;
;; program-bank is responsible for holding program or "patch" data for
;; specific Cadejo instruments. Features include:
;;
;; A) 128 slots corresponding to MIDI program numbers. 
;;
;; B) A program number map which maps one MIDI program number to an
;;    an alternate slot. A program number may also map to nil which
;;    effectively disables the specific slot.
;;
;; C) Function-registry. Each program slot contains an identification
;;    indicating which function is to be executed in response to a specific
;;    MIDI program change. The function -MUST- be "registered" with the
;;    bank. This is in contrast to the previous behavior where any arbitrary
;;    function could be executed on program-change. 
;; 
;; D) The program slot also contains a list of parameter key/value pairs
;;    which serves as arguments to the selected function. The default
;;    function is an identity which simply returns it's arguments.
;;
;; E) Basic serialization for bank read and write now implemented. 
;;

(ns cadejo.midi.program-bank
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot]))

(def enable-trace false)

(def program-count 128)

(declare program-bank)

(defn- program-identity [& args] args)

(defn assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum program-count) pnum)
      (umsg/warning (format "%s is not a valid MIDI program number" pnum))))

(defn program 
  ([function-id name args remarks]
     {:function-id (keyword function-id)
      :name (str name)
      :args args
      :remarks (str remarks)})
  ([function-id name args]
     (program function-id name args ""))
  ([name args]
     (program nil name args)))

(defprotocol ProgramBankProtocol

  (data-format
    [this]
    "Return keyword indicating data format of bank")

  (set-parent-performance! 
    [this obj])

  (get-parent-performance
    [this])

  (bank-name!
    [this name]
    "Set bank name")

  (bank-name
    [this]
    "Return bank name")

  (bank-remarks!
    [this text]
    "Set bank level remarks text")

  (bank-remarks
    [this]
    "Return bank level remarks text")

  ;; Function Registry
  ;;
  (register-function!
    [this id function]
    "Register a function with the bank.
     id - keyword, unique identification
     function - the function")

  (get-function 
    [this id]
    "Returned registered function
     id - The function id.
     If id is nil return an identity
     if id is non-nil and no such function is registered display warning and 
     return identity.  Otherwise return the registered function.")
  
  (function-keys 
    [this]
    "Return list of registered function keys")

  ;; Program number mapping
  ;;
  (clear-program-number-map! 
    [this]
    "Set all program numbers to map to themselves")

  (map-program-number! 
    [this a b]
    "Set program number a to map to program number b.
     a must be a valid MIDI program number.
     b must either be a valid MIDI program number or nil.
     Setting b to nil effectively disables program number a.")

  (map-program-number 
    [this a]
    "Return program number mapped to a.
     a must be valid MIDI program number
     Result is guaranteed to be valid MIDI program number or nil")

  (clear-program!
    [this pnum]
    "Remove the program at MIDI program number pnum.
     If pnum is mapped, used the mapped value.")

  (clear-all-programs!
    [this])

  (set-program! 
    [this pnum prog]
    [this pnum function-id name args remarks]
    [this pnum function-id name args]
    [this pnum name args]
    "Set bank slot data
     For (set-program! this pnum prog)
     Set bank slot pnum to program prog where
     pnum is a MIDI program number and prog is a map as returned by the 
     program function.
          
     For all other argument combinations a prog map is created
     Returns the added program map on success, returns nil on failure.")

  (set-current-program!
    [this pnum name remarks]
    [this pnum name]
    "Set data slot pnum to data in current-program.
     pnum - MIDI program number. If pnum is mapped to another value
     the mapped value is used.
     name - name applied to data
     remarks - optional remarks applied to data.
     If pnum is valid return record of the new program
     If pnum is invalid display warning and return nil")

  (get-program 
    [this pnum]
    "Return the program record for selected slot.
     pnum - The un-mapped program number
     If pnum not assigned return nil
     If pnum is nil return nil")

  (get-data 
    [this pobj]
    "Extract and return data from program record 
     pobj - Record as returned by (program)
     If pobj nil, return nil")

  (current-program-data
    [this]
    "Returns the current program data")

  (current-program-number
    [this]
    "Return the current program number")

  ;; Hook functions
  ;;
  (set-notification-hook!
    [this hfn]
    "Set the notification hook function.
    The function should have the lambda form 
    (hfn pnum bank)
    Where pnum is the mapped program number and bank is this
    The function should return both its arguments as a list")
  
  (notification-hook 
    [this]
    "Return notification hook function")

  (set-pp-hook!
    [this pp]
    "Sets the data pretty-printer function 
    The pp function should have the lambda form
    (pp pnum pname data remarks)
    Where event is the SuperCollider program-change event,
    pname is the selected programs name,
    data is the selected programs data
    remarks are the optional remarks text of the selected program.

    The return value of the hook function should be a string which 
    is both machine and human readable.")    
  
  (pp-hook
    [this]
    "Return pp-hook function")

  (program-change
    [this pnum synths]
    "Extract selected program data and by side effect update all
     active synths, current-program-data, current-program-number
     and call notification and pp hooks. Returns the program data

     pnum - The MIDI program number. if pnum is mapped to another program
            use the mapped value

     synths - A list of active synths to be updated.
              synths may be nil for testing which allows a simulated
              program change without an active SC server.")

  (handle-event 
    [this event synths]
    "As with program-change except the event argument is a SuperCollider 
     event for a MIDI program change.")

  (exchange-programs! 
    [this a b]
    "Exchange positions of program a and b
     Both a and b must be valid MIDI program numbers and program-number
     mapping is ignored. Returns true if exchange successful, 
     false otherwise.")

  (move-program!
    [this a b]
    "Move program from slot a to slot b
     Both a and b must be valid MIDI program numbers and program-number
     mapping is ignored. A null program is placed in slot a.
     Returns true if exchange successful, false otherwise.")
     
  (write-bank
    [this filename]
    "Write bank data to file.
     If successful return true.
     Display warning and return false if on error")

  (read-bank! 
    [this filename]
    "Read bank data from file
     The file must be a cadejo bank data file with proper format.
     bank-name, bank-remarks, program slots and program number mapping
     are updated. data-format, function-registry and current-data are
     not altered.
     Return this if successful
     Print warning message and return false on error.")
  
  (clone
    [this]
    "Create new bank with identical state to this")

  (copy-state! 
    [this other]
    "Copy state of other bank into this.
     data format of both banks must be identical")

  (dump 
    [this verbose depth]
    [this verbose]
    [this]))

(deftype ProgramBank [dformat 
                      parent*
                      name* 
                      remarks*
                      function-registry*
                      program-number-map*
                      programs*
                      current-program-data*
                      current-program-number*
                      notification-hook*
                      pp-hook*]
  ProgramBankProtocol

  (data-format [this] dformat)

  (set-parent-performance! [this obj]
    (swap! parent* (fn [n] obj)))

  (get-parent-performance [this]
    @parent*)

  (bank-name! [this text]
    (swap! name* (fn [n](str text))))

  (bank-name [this] @name*)

  (bank-remarks! [this text]
    (swap! remarks* (fn [n](str text))))

  (bank-remarks [this] @remarks*)

  (register-function! [this id f]
    (swap! function-registry* (fn [n](assoc n (keyword id) f))))

  (get-function [this id]
    (let [f (get @function-registry* (keyword id))]
      (or f (if id
              (do 
                (umsg/warning 
                 (format "%s bank does not recognize function %s"
                         (.data-format this) id))
                program-identity)
              program-identity))))

  (function-keys [this]
    (cons nil (keys @function-registry*)))

  (clear-program-number-map! [this]
    (swap! program-number-map* (fn [n](into '[] (range program-count)))))
 
  (map-program-number! [this a b]
    (if (assert-midi-program-number a)
      (do 
        (if b (assert-midi-program-number b))
        (swap! program-number-map* (fn [n](assoc n a b)))
        @program-number-map*)
      nil))

  (map-program-number [this a]
    (if (assert-midi-program-number a)
      (nth @program-number-map* a)))

  (clear-program! [this pnum]
    (if (assert-midi-program-number pnum)
      (let [pnum2 (nth @program-number-map* pnum)]
        (swap! programs* (fn [n](assoc n pnum2 nil))))))

  (clear-all-programs! [this]
    (swap! programs* (fn [n] {})))

  (set-program! [this pnum prog]
    (if (assert-midi-program-number pnum)
      (do 
        (swap! programs* (fn [n](assoc n pnum prog)))
        prog)
      nil))

  (set-program! [this pnum function-id name args remarks]
    (let [prog (program function-id name args remarks)]
      (.set-program! pnum prog)))


  (set-program! [this pnum function-id name args]
    (.set-program! this pnum function-id name args ""))

  (set-program! [this pnum name args]
    (.set-program! this pnum nil name args ""))

  (set-current-program! [this pnum name remarks]
    (let [pnum2 (.map-program-number this pnum)]
      (if pnum2
        (let [pobj (program nil name (.current-program-data this) remarks)]
          (swap! programs* (fn [n](assoc n pnum2 pobj))))
        nil)))

  (set-current-program! [this pnum name]
    (.set-current-program this pnum name ""))

  (get-program [this pnum]
    (and pnum (get @programs* pnum)))

  (get-data [this pobj]
    (if pobj 
      (let [f (.get-function this (:function pobj))]
        (apply f (:args pobj)))
      nil))

  (current-program-data [this] @current-program-data*)

  (current-program-number [this] @current-program-number*)

  (set-notification-hook! [this hfn]
    (swap! notification-hook* (fn [n] hfn)))

  (notification-hook [this] @notification-hook*)

  (set-pp-hook! [this hfn]
    (swap! pp-hook* (fn [n] hfn)))
 
  (pp-hook [this] @pp-hook*)

  (program-change [this pnum synths]
    (let [pnum2 (.map-program-number this pnum)]
      (if enable-trace
        (printf "%s program-bank.program-change [%3d] --> [%3d]" 
                (.data-format this) pnum pnum2))
      (if pnum2 
        (let [pobj (.get-program this pnum2)
              f (.get-function this (:function-id pobj))
              args (:args pobj)
              data (apply f args)]
          (if data
            (do
              (if synths (apply ot/ctl (cons synths data)))
              (swap! current-program-data* (fn [n] data))
              (swap! current-program-number* (fn [n] pnum2))
              (apply @notification-hook* (list pnum this))
              (println (apply @pp-hook* (list pnum (:name pobj) 
                                              data (:remarks pobj))))
              data)
            nil))
        nil)))

  (handle-event [this event synths]
    (let [pnum (:data1 event)]
      (.program-change this pnum synths)))

  (exchange-programs! [this a b]
    (if (and (assert-midi-program-number a)
             (assert-midi-program-number b))
      (let [ap (.get-program this a)
            bp (.get-program this b)]
        (swap! programs* (fn [n](assoc n a bp)))
        (swap! programs* (fn [n](assoc n b ap)))
        true)
      false))
             
  (move-program! [this a b]
    (if (and (assert-midi-program-number a)
             (assert-midi-program-number b))
      (let [ap (.get-program this a)]
        (swap! programs* (fn [n](assoc n b ap)))
        (swap! programs* (fn [n](assoc n a nil)))
        true)
      false))
        
  (write-bank [this filename]
    (try
      (let [rec {:file-type :cadejo-bank
                 :data-format (.data-format this)
                 :name (.bank-name this)
                 :remarks (.bank-remarks this)
                 :programs @programs*
                 :program-map @program-number-map*}]
        (spit filename (pr-str rec))
        filename)
      (catch java.io.FileNotFoundException e
        (umsg/warning "File Not Found Exception"
                      (format "write-bank  bank format  %s" (.data-format this))
                      (format "filename \"%s\"" filename))
        nil)))

  (read-bank! [this filename]
    (try
      (let [rec (read-string (slurp filename))
            ftype (:file-type rec)
            dformat (:data-format rec)]
        (if (not (and (= ftype :cadejo-bank)
                      (= dformat (.data-format this))))
          (do 
            (umsg/warning "Wrong File Type"
                          (format "%s is not cadejo-bank with %s format"
                                  filename (.data-format this)))
            nil)
          (do 
            (.clear-program-number-map! this)
            (.clear-all-programs! this)
            (swap! current-program-number* (fn [n] nil))
            (.bank-name! this (:name rec))
            (.bank-remarks! this (:remarks rec))
            (doseq [pnum (keys (:programs rec))]
              (let [pobj (get (:programs rec) pnum)]
                (.set-program! this (int pnum)
                               (:function-id pobj)
                               (:name pobj)
                               (:args pobj)
                               (:remarks pobj))))
            (let [pnmap (:program-map rec)]
              (doseq [a (range (count pnmap))]
                (.map-program-number! this a (nth pnmap a))))
            this)))
      (catch java.io.FileNotFoundException e
        (umsg/warning "File Not Found Exception"
                      (format "read-bank!  bank format  %s" (.data-format this))
                      (format "filename \"%s\"" filename))
        nil)))

  (clone [this]
    (let [other (program-bank (.data-format this)
                              (.bank-name this)
                              (.bank-remarks this))]
      (doseq [fid (.function-keys this)]
        (let [f (.get-function this fid)]
          (.register-function! other fid f)))
      (dotimes [pnum program-count]
        (let [b (.map-program-number this pnum)
              prog (.get-program this pnum)]
          (.map-program-number! other pnum b)
          (if prog
            (.set-program! other pnum
                           (:function-id prog)
                           (:name prog)
                           (:args prog)
                           (:remarks prog)))))
      (.set-notification-hook! other (.notification-hook this))
      (.set-pp-hook! other (.pp-hook this))
      other))

  (copy-state! [this other]
    (if (= (.data-format this)(.data-format other))
      (do (.clear-program-number-map! this)
          (.clear-all-programs! this)
          (.bank-name! this (.bank-name other))
          (.bank-remarks! this (.bank-remarks other))
          (dotimes [pnum program-count]
            (let [b (.map-program-number other pnum)
                  prog (.get-program other pnum)]
              (.map-program-number! this pnum b)
              (if prog (.set-program! this pnum
                                      (:function-id prog)
                                      (:name prog)
                                      (:args prog)
                                      (:remarks prog)))))
          this)
      (umsg/warning (format "Can not copy %s bank data to %s bank"
                            (.data-format other)
                            (.data-format this)))))


  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)
          pad3 (cadejo.util.string/tab (inc depth2))]
      (printf "%sProgramBank :format %s :name %s\n" 
              pad (.data-format this)(.bank-name this))
      (if verbose (printf "%sRemarks: %s\n" pad2 (.bank-remarks this)))
      (doseq [pnum (sort (keys @programs*))]
        (let [pobj (get @programs* pnum)]
          (if verbose
            (printf "%s[%3d] :fn %-8s :name %-12s :remarks %s\n"
                    pad2 pnum (:function-id pobj)(:name pobj)(:remarks pobj))
            (printf "%s[%3d] %s\n" pad2 pnum (:name pobj)))))
      (printf "%sMapped program numbers:\n" pad2)
      (doseq [a (range (count @program-number-map*))]
        (let [b (nth @program-number-map* a)]
          (if (not (= a b))
            (printf "%s[%3d] --> %s\n" pad3 a b))))
      (if verbose
        (do
          (printf "%sRegistered functions:\n" pad2)
          (doseq [k (keys @function-registry*)]
            (printf "%s%s\n" pad3 k))
          (printf "%sNotification hook: %s\n" pad2 @notification-hook*)
          (printf "%spp hook: %s\n" pad2 @pp-hook*)))
      (println)))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false)))

(defn program-bank 
  "Create and return new ProgramBank object"
  ([format name remarks]
     (let [bnk (ProgramBank. (keyword format)
                             (atom nil)           ; parent performance
                             (atom (str name))
                             (atom (str remarks))
                             (atom {})            ; function-registry
                             (atom nil)           ; program-number-map
                             (atom {})            ; programs
                             (atom nil)           ; current-program data
                             (atom nil)           ; current-program number
                             (atom identity)      ; notification-hook
                             (atom identity))]
       (.clear-program-number-map! bnk)
       (.set-notification-hook! bnk (fn [& args] nil))
       (.set-pp-hook! bnk (fn [& args] nil))
       bnk))
  ([format]
     (program-bank format 
                   (format "New %s bank" format) "")))
