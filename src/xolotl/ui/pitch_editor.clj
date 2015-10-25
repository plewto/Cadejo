(ns xolotl.ui.pitch-editor
  (:require [xolotl.util :as util])
  (:require [xolotl.ui.factory :as factory])
  (:require [clojure.string :as str])
  (:import java.awt.event.ActionListener))

(def ^:private octaves (range -4 5))
(def ^:private roots {"C" 0, "D" 2, "E" 4, "F" 5, "G" 7, "A" 9, "B" 11})
(def ^:private modifiers {"F" -1, "" 0, "S" 1})
(def ^:private REST-LIMIT 0)            ; Any value less then REST-LIMIT is a rest.
(def REST -1000)                        ; The canonical rest value.

(def ^:private msg00 "Nested Xolotl chord list")
(def ^:private msg01 "Invalid pitch token: %s")
(def ^:private msg02 "Unbalanced chord bracket ]")
(def ^:private msg03 "Missing chord close bracket ]")
(def ^:private msg04 "Pitch pattern is empty")
(def ^:private msg05 "Pitch data error %s")
(def ^:private msg06 "Illegal Xolotl pitch mode '%s'")


;; Map symbolic pitch tokens to int MIDI key-number
;; Pitch symbol name format:   R0 or RM0
;;     where R -> Root key name "white key" : C D E F G A or B
;;           M -> optional modifier         : F = flat, S = sharp
;;           O -> octave number             : 0,1,2,3,4,5,6,7,8
;;                              
(def ^:private pitch-map (let [acc* (atom {})]
                           (doseq [oct octaves]
                             (let [oct-val (* 12 oct)]
                               (doseq [root (seq roots)]
                                 (doseq [mod (seq modifiers)]
                                   (let [sym (keyword (format "%s%s%d" (first root)(first mod) oct))
                                         val (+ oct-val (second root)(second mod))]
                                     (swap! acc* (fn [q](assoc q sym val))))))))
                           (swap! acc* (fn [q](assoc q :R REST)))
                           @acc*))


;; Map int MIDI key-numbers to to symbolic tokens
;; For accidentals "sharp" keys are used.
;;
(def ^:private reverse-map (let [acc* (atom {})]
                             (doseq [val (keys pitch-map)]
                               (let [key (get pitch-map val)
                                     is-flat (= (second (name val)) \F)]
                                 (if (not is-flat)
                                   (swap! acc* (fn [q](assoc q key val))))))
                             @acc*))

;; Convert int to pitch token
;;
(defn- int->pitch [n]
  (if (<= n REST-LIMIT)
    "R"
    (let [token (get reverse-map n)]
      (if token
        (name token)
        (int n)))))


;; Convert list of MIDI key-numbers to string
;; Chords are represented as nested list.
;;
;; RETURNS: list (s err)
;;    Where s is the converted string
;;    and err is an error message.
;;    If no error occurred err is an empty-string.
;;
(defn- pitch-list->str
  ([lst](pitch-list->str lst false))
  ([lst in-chord]
   (let [error (StringBuilder.)
         sb (StringBuilder.)
         line-length* (atom 8)
         end-of-line-test (fn []
                            (if (zero? @line-length*)
                              (do
                                (.append sb "\n")
                                (reset! line-length* 8))
                              (swap! line-length* dec)))
         token* (atom nil)]
     (try
       (doseq [obj lst]
         (reset! token* obj)
         (if (or (list? obj)(vector? obj))
           (if in-chord                    ; process nested chord
             (do
               (.append error msg00)
               (throw (IllegalArgumentException. (.toString error))))
             (let [rs-chord (pitch-list->str obj true)
                   err-chord (second rs-chord)]
               (if (pos? (count err-chord))
                 (do
                   (.append error err-chord)
                   (throw (IllegalArgumentException. (.toString error))))
                 (do 
                   (.append sb "[")
                   (doseq [token (util/tokenize (first rs-chord))]
                     (.append sb (format "%s " token))
                     (end-of-line-test))
                   (.append sb "] ")))))
           (do                             ; process top-level note
             (.append sb (format "%s " (int->pitch obj)))
             (end-of-line-test))))
       (list (.toString sb)(.toString error))
       (catch IllegalArgumentException ex
         (list (.toString sb)(.toString error)))
       (catch ClassCastException ex
         (list (.toString sb) (format "ClassCastException token = '%s'" @token*))) ))))

;; Pre-process text string for parsing by adding white space around all
;; square brackets.
;;
(defn- pre-parse [text]
  (str/replace
   (str/replace text "[" " [ ")
   "]" " ] "))

;; Convert string to list of MIDI key-numbers.
;; Throws IllegalArgumentException on parsing error.
;;
(defn- parse-pitch [text]
  (let [tokens (util/tokenize (pre-parse text))
        acc* (atom [])
        chord* (atom nil)
        in-chord* (atom false)]
    (doseq [token tokens]
      (cond (= token "[")               ; enter chord
            (if @in-chord*
              (throw (IllegalArgumentException. msg00))
              (do
                (reset! chord* [])
                (reset! in-chord* true)))

            (= token "]")               ; exit chord
            (if @in-chord*
              (do
                (swap! acc* (fn [q](conj q @chord*)))
                (reset! in-chord* false))
              (throw (IllegalArgumentException. msg02)))

            :else
            (let [value (get pitch-map
                             (keyword token)
                             (util/str->int token))]
              (if value
                (swap! (if @in-chord* chord* acc*)
                       (fn [q](conj q value)))
                (throw (IllegalArgumentException. (format msg01 token)))))))
    (if @in-chord*
      (throw (IllegalArgumentException. msg03)))
    @acc*))
                  
;; Validate text as pitch pattern
;; If pattern is correct return false
;; If there is an error return error message.
;;
(defn- validator [text]
  (try
    (if (pos? (count text))
      (do
        (parse-pitch text)
        false)
      (throw (IllegalArgumentException. msg04)))
    (catch IllegalArgumentException ex
      (.getMessage ex))))

;; Construct pitch-editor sub-panel
;; ARGS:
;;   parent-editor - an instance of NodeEditor for Xolotl
;;   seq-id - keyword, either :A or :B
;;
;; RETURNS: map with keys :pan-main -> JPanel
;;                        :sync-fn  -> GUI update function
;;
(defn pitch-editor [parent-editor seq-id]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        enter-action (fn [text]
                       (let [data (parse-pitch text)
                             prog (.current-program bank)]
                         (.pitch-pattern! prog seq-id data)
                         (.pitch-pattern! xseq data)))
        
        ted (factory/text-editor "Pitch Pattern"
                                 validator enter-action
                                 factory/pitch-clipboard*
                                 parent-editor)
        mode-listener (proxy [ActionListener][]
                        (actionPerformed [evn]
                          (let [src (.getSource evn)
                                id (.getClientProperty src :id)
                                prog (.current-program bank)]
                            (.pitch-mode! prog seq-id id)
                            (.pitch-mode! xseq id))))
        rpan-mode (factory/radio '[["SEQ" :seq]["RND" :random]["SR" :sr]] 3 1
                                 :listener mode-listener)
        pan-main (factory/border-panel :center (:pan-main ted)
                                       :east (factory/grid-panel 2 1
                                              (:pan-main rpan-mode)))
        sync-fn (fn [prog]
                  (let [pat (.pitch-pattern prog seq-id)
                        rs (pitch-list->str pat)
                        err (second rs)
                        mode (.pitch-mode prog seq-id)
                        rb-mode (mode (:buttons rpan-mode))]
                    (if (zero? (count err))
                      (.setText (:text-area ted)(first rs))
                      (let [msg (format msg05 err)]
                        (.warning! parent-editor msg)
                        (throw (IllegalArgumentException. msg))))
                    (if rb-mode
                      (.setSelected rb-mode true)
                      (throw (IllegalArgumentException. (format msg06 mode))))))]
    {:pan-main pan-main
     :sync-fn sync-fn}))       
        


 
