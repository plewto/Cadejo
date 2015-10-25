(ns xolotl.ui.velocity-editor
  (:require [xolotl.util :as util])
  (:require [xolotl.ui.factory :as factory])
  (:import java.awt.event.ActionListener))

(def ^:private msg00 "Velocity parse error: %s")
(def ^:private msg01 "Empty velocity pattern")
(def ^:private msg02 "Illegal Xolotl velocity mode '%s'")

;; Converts list of MIDi velocity values to string
;; RETURNS: list (s err)
;;   Where s is the converted string and err is an error message.
;;   If no error has occurred err is an empty string.
;;
(defn- velocity-list->str [lst]
  (let [error (StringBuilder.)
        sb (StringBuilder.)
        line-length* (atom 8)]
    (doseq [h lst]
      (if (util/int? h 0 127)
        (.append sb (format "%3d " h))
        (.append error (format "%s " h)))
      (swap! line-length* dec)
      (if (zero? @line-length*)
        (do
          (.append sb "\n")
          (reset! line-length* 8))))
    (list (.toString sb)(.toString error))))

;; Convert string into list of MIDI velocity values.
;; Throws IllegalArgumentException on error.
;;
(defn- parse-velocity [text]
  (let [acc* (atom [])]
    (doseq [token (util/tokenize text)]
      (let [v (util/str->int token)]
        (if v
          (swap! acc* (fn [q](conj q v)))
          (throw (IllegalArgumentException. (format msg00 token))))))
    @acc*))

;; Test for valid velocity pattern
;; If pattern is valid return false.
;; If pattern is invalid return offending token.
;;
(defn- validator [text]
  (try
    (if (pos? (count text))
      (do
        (parse-velocity text)
        false)
      (throw (IllegalArgumentException. msg01)))
    (catch IllegalArgumentException ex
      (.getMessage ex))))


(defn velocity-editor [parent-editor seq-id]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        enter-action (fn [text]
                       (let [data (parse-velocity text)
                             prog (.current-program bank)]
                         (.velocity-pattern! prog seq-id data)
                         (.velocity-pattern! xseq data)))
        ted (factory/text-editor "Velocity Pattern"
                                 validator enter-action
                                 factory/int-clipboard*
                                 parent-editor)
        mode-listener (proxy [ActionListener][]
                        (actionPerformed [evn]
                          (let [src (.getSource evn)
                                id (.getClientProperty src :id)
                                prog (.current-program bank)]
                            (.velocity-mode! prog seq-id id)
                            (.velocity-mode! xseq id))))
        rpan-mode (factory/radio '[["SEQ" :seq]["RND" :random]["SR" :sr]] 3 1
                                 :listener mode-listener)
        pan-main (factory/border-panel 
                  :center (:pan-main ted)
                  :east (factory/grid-panel 2 1
                                            (:pan-main rpan-mode)))
        sync-fn (fn [prog]
                  (let [pat (.velocity-pattern prog seq-id)
                        rs (velocity-list->str pat)
                        err (second rs)
                        mode (.velocity-mode prog seq-id)
                        rb-mode (mode (:buttons rpan-mode))]
                    (if (zero? (count err))
                      (.setText (:text-area ted)(first rs))
                      (let [msg (format msg00 err)]
                        (.warning! parent-editor msg)
                        (throw (IllegalArgumentException. msg))))
                    (if rb-mode
                      (.setSelected rb-mode true)
                      (throw (IllegalArgumentException. (format msg02 mode))))))]
    {:pan-main pan-main
     :sync-fn sync-fn}))       
