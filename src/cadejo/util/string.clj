(ns cadejo.util.string
  (:gen-class))

(defn tab 
  ([n]
     (if (> n 0)
       (let [frmt (format "%%%ds" (* n 4))]
         (format frmt ""))
       ""))
  ([](tab 1)))
     
(defn pad-right [s width]
  (let [diff (- width (count s))]
    (if (pos? diff)
      (let [frmt (format "%%-%ds" diff)]
        (format frmt s))
      s)))

;; Return index of substring sub withing string str.
;; -1 indicates sub is not contained in str
;;
(defn index-of [sub s]
  (.indexOf (str s) (str sub) 0))

;; Predicate returns true if sub is substring of str
;;
(defn is-substring? [sub str]
  (let [i (index-of sub str)]
    (not (= i -1))))
  
