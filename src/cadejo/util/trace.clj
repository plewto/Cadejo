(ns cadejo.util.trace)

(def ^:private depth* (atom 0))

(defn set-depth [d]
  (swap! depth* (fn [n] d)))

(defn inc-depth []
  (swap! depth* inc))

(defn dec-depth []
  (let [d (max 0 (dec @depth*))]
    (set-depth d)))

(defn- pad []
  (let [frmt (format "%%%ds" (max 1 (* 4 @depth*)))]
    (format frmt " ")))

(defn trace-reset []
  (set-depth 0))

(defn trace-enter [& args]
  (print (pad))
  (print ">>> ")
  (doseq [a args](print (format "%s " a)))
  (println)
  (inc-depth))

(defn trace-mark [& args]
  (print (pad))
  (print "--- MARK ")
  (doseq [a args](print (format "%s " a)))
  (println))

(defn trace-is-null? [text obj]
  (print (pad))
  (printf "--- is-null? %s %s" text (if obj "no" "yes"))
  (println))

(defn trace-exit [& args]
  (dec-depth)
  (print (pad))
  (print "<<< ")
  (doseq [a args](print (format "%s " a)))
  (println)
  (dec-depth))
  
