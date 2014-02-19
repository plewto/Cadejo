(ns cadejo.util.queue
  "Provides queue structure.
   NOTE: the queue may not contain duplicates"
  (:gen-class))

(defn- q-contains? [obj q*]
  (some (fn [x](= x obj)) @q*))

(defn create-queue []
  "Creates and returns new queue object."
  (atom '[]))

(defn clear-queue [q*]
  "Remove all items from q"
  (swap! q* (fn [n] [])))

(defn queue-depth [q*]
  "Returns number of items on q."
  (count @q*))

(defn queue-empty? [q*]
  "Predicate, true if q contains no items."
  (zero? (count @q*)))

(defn queue-peek [q*]
  "Return top item from q."
  (first @q*))

(defn enqueue [q* obj]
  "Place new object to end of q."
  (if (not (q-contains? obj q*))
    (swap! q* (fn [n](conj n obj))))
  obj)

(defn dequeue
  "Removes item from q. If obj provided remove it, 
   if obj not provided remove item from head of q."
  ([q* obj]
     (swap! q* (fn [n](remove (fn [x](= x obj)) n)))
     obj)
  ([q*]
     (let [obj (queue-peek q*)]
           (swap! q* rest)
           obj)))

(defn str-queue [q*]
  "Return string representation of q"
  (let [sb (StringBuilder.)]
    (.append sb "queue : ")
    (if (queue-empty? q*)
      (.append sb "<empty>")
      (doseq [obj @q*]
        (.append sb (format "%s " obj))))
    (.toString sb)))

(defn dump-queue [q*]
  (println (str-queue q*)))
