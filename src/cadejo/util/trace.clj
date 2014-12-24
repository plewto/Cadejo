(ns cadejo.util.trace)

(def ^:private enable* (atom true))
(def ^:private depth* (atom 0))
(def ^:private pad* (atom ""))
(def ^:private stack* (atom []))

(defn- increment []
  (swap! depth* inc)
  (swap! pad* (fn [n](str n "    "))))

(defn- decrement []
  (swap! depth* (fn [n](max 0 (dec n))))
  (swap! pad* (fn [n]
                (if (pos? (count n))
                  (subs n 0 (- (count n) 4))
                  ""))))

(defn trace-enable [flag]
  (reset! enable* flag))

(defn trace-enter [msg & {:keys [return]
                          :or {return nil}}]
  (if @enable*
    (do
      (println (format ";; %s-->[%d] %s" @pad* @depth* msg))
      (increment)
      (swap! stack* (fn [n](conj n msg))))
    return))

(defn trace-exit [& {:keys [msg return]
                     :or {msg nil
                          return nil}}]
  (if @enable*
    (do
      (decrement)
      (println (format ";; %s<--[%d] %s " @pad* @depth* (or msg (last @stack*))))
      (try
        (swap! stack* pop)
        (catch Exception ex
          (println ";; ERROR trace-exit empty stack")))))
  return)

;; (defn trace-mark [msg & {:keys [return]
;;                          :or {return nil}}]
;;   (if @enable*
;;     (let [d (max 0 (dec @depth*))
;;           p (if (pos? d)(subs @pad* 0 (- (count @pad*) 4)) "")]
;;     (println (format ";; %s   [%d] MARK: %s" p d msg))))
;;   return)


(defn trace-mark [msg & args]
  (if @enable*
    (let [d (max 0 (dec @depth*))
          p (if (pos? d)(subs @pad* 0 (- (count @pad*) 4)) "")]
    (println (format ";; %s   [%d] MARK: %s" p d msg))))
  (first args))
