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
