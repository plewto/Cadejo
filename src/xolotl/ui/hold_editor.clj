(ns xolotl.ui.hold-editor
  (:require [xolotl.util :as util])
  (:require [xolotl.ui.factory :as factory]))

(def ^:private msg00 "Hold-time parse error: %s")
(def ^:private msg01 "Empty hold-time list")

;; Convert list of floats to string, the reverse of parse-hold-time
;; RETURNS: list (s err)
;;   Where s is the converted string and err is an error message.
;;   If no error has occurred err is an empty string.
;;
(defn- hold-list->str [lst]
  (let [error (StringBuilder.)
        sb (StringBuilder.)
        line-length* (atom 8)]
    (doseq [h lst]
      (if (and (number? h)(pos? h))
        (.append sb (format "%5.3f " h))
        (.append error (format "%s " h)))
      (swap! line-length* dec)
      (if (zero? @line-length*)
        (do
          (.append sb "\n")
          (reset! line-length* 8))))
    (list (.toString sb)(.toString error))))

;; Convert string into list of float hold times.
;; Throws IllegalArgumentException on parse error.
;;
(defn- parse-hold-time [text]
  (let [acc* (atom [])]
    (doseq [token (util/tokenize text)]
      (let [h (util/str->float token)]
        (if h
          (swap! acc* (fn [q](conj q h)))
          (throw (IllegalArgumentException. (format msg00 token))))))
    @acc*))

;; Test for valid hold-time definition.
;; If text is valid return false.
;; If text is invalid return offending token.
;;
(defn- validator [text]
  (try
    (if (pos? (count text))
      (do
        (parse-hold-time text)
        false)
      (throw (IllegalArgumentException. msg01)))
    (catch IllegalArgumentException ex
      (.getMessage ex))))

;; Construct hold-time sub-panel
;; ARGS:
;;   parent-editor - an instance of NodeEditor for Xolotl
;;   seq-id - keyword, either :A or :B
;;
;; RETURNS: map with keys :pan-main -> JPanel
;;                        :sync-fn  -> GUI update function
;;
(defn hold-editor [parent-editor seq-id]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        enter-action (fn [text]
                       (let [data (parse-hold-time text)
                             prog (.current-program bank)]
                         (.hold-pattern! prog seq-id data)
                         (.hold-pattern! xseq data)))
        ted (factory/text-editor "Hold Pattern"
                                 validator enter-action
                                 factory/hold-clipboard*
                                 parent-editor)
        sync-fn (fn [prog]
                  (let [hpat (.hold-pattern prog seq-id)
                        rs (hold-list->str hpat)
                        err (second rs)]
                    (if (zero? (count err))
                      (.setText (:text-area ted)(first rs))
                      (let [msg (format msg00 err)]
                        (.warning! parent-editor msg)
                        (throw (IllegalArgumentException. msg))))))]
    {:pan-main (:pan-main ted)
     :sync-fn sync-fn}))          
