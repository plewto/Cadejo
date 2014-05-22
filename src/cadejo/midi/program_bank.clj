;; Replacement for midi.program and midi.bank
;; ISSUE: changed contract for pp-hook

(ns cadejo.midi.program-bank
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.core :as ot]))


(defn assert-midi-program-number [pnum]
  (or (and (integer? pnum)(>= pnum 0)(< pnum 128) pnum)
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
     Setting b t nil effectivly disables program number a.")

  (map-program-number 
    [this a]
    "Return program number mapped to a.
     a must be valid MIDI program number
     Result is guaranteed to be valid MIDI program number") 

  (clear-program!
    [this pnum]
    "Remove the program at MIDI program number pnum.
     If pnum is mapped, used the mapped value.")

  (clear-all-programs!
    [this])

  (set-program! 
    [this pnum function-id name args remarks]
    [this pnum function-id name args]
    [this pnum name args]
    "Set bank slot data
     pnum - MIDI program number. If pnum is mapped to another value
     the mapped value is used.
     All other arguments as per the program function
     A new record is created via the program function, stored in slot
     pnum and returned.")

  (get-program 
    [this pnum]
    "Return the program record for selected slot.
     pnum - The un-mapped program number
     If pnum not assigned return nil
     If pnum is nil return nil")

  (get-data 
    [this pobj]
    "Extract and return data frm program record 
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

  (set-pp-hook!
    [this pp]
    "Sets the data pretty-printer function 
    The pp function should have the lambda form
    (pp pnum pname data remarks)
    Where event is the SuperCollider program-change event,
    pname is the selected programs name,
    data is the selected programs data
    remarks are the optional remarks text of the selected program.
    The return value of pp-hook is not used and should be nil")
      
  (program-change
    [this pnum synths]
    "Extract the selected program data and by side-effect update all
     active synths, the current-program-data, current-program-number, 
     call the notification and pp hooks and return the program data.

     pnum - The MIDI program number. if pnum is mapped to another program
     use the mapped value.
     synths - A list of active synths to be updated")

  (handle-event 
    [this event synths]
    "As with program-change except the event argument is a SuperCollider 
     event for a MIDI program change.")

  (dump 
    [this verbose depth]
    [this verbose]
    [this])
     
)



(deftype ProgramBank [dformat 
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
                identity)
              identity))))

  (clear-program-number-map! [this]
    (swap! program-number-map* (fn [n](into '[] (range 128)))))
 
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

  (set-program! [this pnum function-id name args remarks]
    (if (assert-midi-program-number pnum)
      (let [pnum2 (nth @program-number-map* pnum)
            prog (program function-id name args remarks)]
        (swap! programs* (fn [n](assoc n pnum2 prog)))
        prog)
      nil))

  (set-program! [this pnum function-id name args]
    (.set-program! this pnum function-id name args ""))

  (set-program! [this pnum name args]
    (.set-program! this pnum nil name args ""))

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

  (set-pp-hook! [this hfn]
    (swap! pp-hook* (fn [n] hfn)))

 
  (program-change [this pnum synths]
    (let [pnum2 (.map-program-number this pnum)]
      (if pnum2 
        (let [pobj (.get-program this pnum2)
              f (.get-function this (:function-id pobj))
              args (:args pobj)
              data (f args)]
          (if data
            (do
              (apply ot/ctl (cons synths data))
              (swap! current-program-data* (fn [n] data))
              (swap! current-program-number* (fn [n] pnum2))
              (apply @notification-hook* (list pnum this))
              (apply @pp-hook* (list pnum (:name pobj) data (:remarks pobj)))
              data)
            nil))
        nil)))
          

  (handle-event [this event synths]
    (let [pnum (:data1 event)]
      (.program-change this pnum synths)))

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)
          pad3 (cadejo.util.string/tab (inc depth2))
          ]
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
    (.dump this false))
)
          

(defn program-bank 
  ([format name remarks]
     (let [bnk (ProgramBank. (keyword format)
                             (atom (str name))
                             (atom (str remarks))
                             (atom {})            ; function-rregistry
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

;;; ****************************************************************
;;; **************************************************************** TEST ONLY 
;;; **************************************************************** BELOW THIS 
;;; **************************************************************** LINE

;; (println)
;; (println)
;; (def bnk (program-bank :test "Default" "Bank level remarks"))

;; (defn foo [& args]
;;   (cons :fooo args))

;; (defn bar [& args]
;;   (cons :barr args))

;; (.register-function! bnk :foo foo)
;; (.register-function! bnk :bar bar)

;; ;; Some test programs 

;; (.map-program-number! bnk 1 nil)
;; (.map-program-number! bnk 16 1)
;; (.map-program-number! bnk 17 2)
;; (.map-program-number! bnk 32 5)


;; (.set-program! bnk 0 nil "Alpha" '[1 2 3 4] "These are alpha remarks")
;; (.set-program! bnk 1 nil "Odd" '[1 3 5 7] "Thees are odd remarks")
;; (.set-program! bnk 2 :foo "Foo Call" '[] "A call to foo with no arguments")
;; (.set-program! bnk 3 :foo "Foo 2" '[2 3 5 7] "A call to foo with prime args")
;; (.set-program! bnk 4 :bar "Bar Call" '[1 4 9 16] "A call to bar with squares")
;; (.set-program! bnk 32 :fail "A Faild Call" '[100 200])

;; (.set-notification-hook! bnk 
;;                         (fn [pnum bobj]
;;                           (printf "Notification hook [%3d] %s\n" pnum (.bank-name bobj))))

;; (.set-pp-hook! bnk (fn [pnum pname data remarks]
;;                      (printf "pp hook [%3d] name: %s data: %s remarks: %s\n"
;;                              pnum pname data remarks)))




;; (.dump bnk true)
;; (println)
