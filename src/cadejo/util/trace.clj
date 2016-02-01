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

(defn trace-reset []
  (reset! depth* 0))

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

(defn trace-exit [& {:keys [msg silent return]
                     :or {msg nil
                          silent nil
                          return nil}}]
  (if @enable*
    (do
      (decrement)
      (if (not silent)
        (println (format ";; %s<--[%d] %s " @pad* @depth* (or msg (last @stack*))))
        (println))
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


;; (defn trace-event [where event & {:keys [permissive]
;;                                   :or {permissive false}}]
;;   (if (or permissive (and (= (:status event) :note-on)
;;                           (pos? (:data2 event)))) ;; ignore note-on running-status masquerading as note-off           
;;     (let [note (:data1 event)
;;           velocity (:data2 event)
;;           chan (+ 1 (:channel event))
;;           msg (format "%s %s chan %2d note %3d vel %3d"
;;                       where (:status event) chan note velocity)]
;;       (trace-enter msg))))
      

;; (defn trace-event-exit [event & {:keys [permissive]
;;                                  :or {permissive false}}]
;;   (if (or permissive (and (= (:status event) :note-on)
;;                           (pos? (:data2 event))))
;;     (trace-exit)))



(defn trace-event [where event]
  (if (not (= (:status event) :active-sensing))
    (let [command (:command event)
          chan (+ 1 (:channel event))
          data1 (:data1 event)
          data2 (:data2 event)
          msg (format "%s %s chan %2d  data1 %3s data2 %3s"
                      where command chan data1 data2)]
      (trace-enter msg))))

(defn trace-event-exit [event]
  (if (not (= (:status event) :active-sensing))
    (trace-exit :silent true)))
      
