(ns xolotl.ui.controller-editor
  (:require [xolotl.util :as util])
  (:require [xolotl.ui.factory :as factory])
  (:import javax.swing.event.ChangeListener))

(def ^:private msg00 "Controller parse error: %s")
(def ^:private msg01 "Empty controller pattern")

;; Converts list of MIDI controller values to string
;; RETURNS: list (s err)
;;   Where s is the converted string and err is an error message.
;;   If no error has occurred err is an empty string.
;;
(defn- controller-list->str [lst]
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

;; Convert string into list of MIDI controller values.
;; Throws IllegalArgumentException on error.
;;
(defn- parse-controller [text]
  (let [acc* (atom [])]
    (doseq [token (util/tokenize text)]
      (let [v (util/str->int token)]
        (if v
          (swap! acc* (fn [q](conj q v)))
          (throw (IllegalArgumentException. (format msg00 token))))))
    @acc*))

;; Test for valid controller pattern
;; If pattern is valid return false.
;; If pattern is invalid return offending token.
;;
(defn- validator [text]
  (try
    (if (pos? (count text))
      (do
        (parse-controller text)
        false)
      (throw (IllegalArgumentException. msg01)))
    (catch IllegalArgumentException ex
      (.getMessage ex))))

;; Constructs MIDI cc editor sub-panel
;; Includes * value pattern text field
;;          * Controller number spinner
;; ARGS:
;;   parent-editor - an instance of NodeEditor for Xolotl
;;   seq-id - keyword, either :A or :B
;;   cindex - int, either 0 or 1, indicates which xseq controller to use.
;;
;; RETURNS: map with keys :pan-main -> JPanel
;;                        :sync-fn -> GUI update function
;;
(defn controller-editor [parent-editor seq-id cindex]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        enter-action (fn [text]
                       (let [data (parse-controller text)
                             prog (.current-program bank)]
                         (if (= cindex 0)
                           (do
                             (.controller-1-pattern! prog seq-id data)
                             (.controller-pattern! xseq 0 data))
                           (do
                             (.controller-2-pattern! prog seq-id data)
                             (.controller-pattern! xseq 1 data)))))
        ted (factory/text-editor (format "Controller %s Pattern" (inc cindex))
                                 validator enter-action
                                 factory/int-clipboard*
                                 parent-editor)
        spin-ctrl (factory/spinner -1 127 1)
        pan-ctrl (factory/horizontal-panel spin-ctrl
                                           (factory/label "  CTRL "))
        pan-main (factory/border-panel :center (:pan-main ted)
                                       :south (factory/vertical-panel pan-ctrl
                                                                      (factory/vertical-strut)))
        sync-fn (fn [prog]
                  (let [pat* (atom nil)
                        ctrl* (atom nil)]
                    (if (= cindex 0)
                      (do
                        (reset! pat* (.controller-1-pattern prog seq-id))
                        (reset! ctrl* (.controller-1-number prog seq-id)))
                       (do
                        (reset! pat* (.controller-2-pattern prog seq-id))
                        (reset! ctrl* (.controller-2-number prog seq-id))))
                    (let [rs (controller-list->str @pat*)
                          err (second rs)]
                      (if (zero? (count err))
                        (do 
                          (.setText (:text-area ted) (first rs))
                          (.setValue spin-ctrl (int @ctrl*)))
                        (let [msg (format msg00 err)]
                          (.warning! parent-editor msg)
                          (throw (IllegalArgumentException. msg))))))) ]
    (.addChangeListener spin-ctrl
                        (proxy [ChangeListener][]
                          (stateChanged [_]
                            (let [ctrl (int (.getValue spin-ctrl))
                                  prog (.current-program bank)]
                              (if (= cindex 0)
                                (do
                                  (.controller-1-number! prog seq-id ctrl)
                                  (.controller-number! xseq 0 ctrl))
                                (do
                                  (.controller-2-number! prog seq-id ctrl)
                                  (.controller-number! xseq 1 ctrl)))))))
    {:pan-main pan-main
     :sync-fn sync-fn}))          
