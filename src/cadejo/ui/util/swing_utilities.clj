(ns cadejo.ui.util.swing-utilities
  (:import java.lang.IllegalArgumentException)
  )

;; Convert collection to sequence.
;; For maps a seq of map values ais returned.
;;
(defn- to-seq [col]
  (cond (map? col)(map second col)
        :default col))

;; Set enable status for all swing JComponents in collection.
;;
(defn enable-all [col flag]
  (doseq [jc (to-seq col)]
    (try
      (.setEnabled jc flag)
      (catch IllegalArgumentException ex
        (.printStackTrace ex)))))


;; Set visible status for all swing JComponents in collection.
;;
(defn set-visible-all [col flag]
  (doseq [jc (to-seq col)]
    (try
      (.setVisible jc flag)
      (catch IllegalArgumentException ex
        (.printStackTrace ex)))))


