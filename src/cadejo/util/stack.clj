(ns cadejo.util.stack
  "Provides FIFO stack.")

(defn create-stack []
  "Creates object for use as stack."
  (atom []))

(defn clear-stack [stk*]
  "Removes all items from stack."
  (swap! stk* (fn [n] [])))

(defn stack-depth [stk*]
  "Returns count of items on stack."
  (count @stk*))

(defn stack-empty? [stk*]
  "Predicate true if stack contains no items."
  (zero? (count @stk*)))

(defn stack-peek [stk*]
  "Return item at top of stack."
  (first @stk*))

(defn stack-pop 
  "Remove object from stack. If obj not provided remove top stack item."
  ([stk*]
     (let [obj (stack-peek stk*)]
       (swap! stk* rest)
       obj))
  ([stk* obj]
     (swap! stk* (fn [n](remove (fn [x](= x obj)) n)))
     obj))

(defn stack-push [stk* obj]
  "Push obj to top of stack."
  (swap! stk* (fn [n] (cons obj n)))
  obj)

(defn str-stack [stk*]
  "Returns string representation of stack."
  (let [sb (StringBuilder.)]
    (.append sb "Stack : ")
    (if (stack-empty? stk*)
      (.append sb "<empty>")
      (doseq [obj @stk*]
        (.append sb (format "%s " obj))))
    (.toString sb)))

(defn dump-stack [stk*]
  (println (str-stack stk*)))
