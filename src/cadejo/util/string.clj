(ns cadejo.util.string
  (:gen-class))


(defn tab 
  ([n]
     (if (> n 0)
       (let [frmt (format "%%%ds" (* n 4))]
         (format frmt ""))
       ""))
  ([](tab 1)))
     
