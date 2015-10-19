(ns xolotl.cycle
  (:require [xolotl.util :as util]))

;; Defines collection whose elements may be accessed either sequentially, by
;; index or randomly.
;;
;; * Cyclical access, use next method.
;; * By index, use value.
;; * Random element, use pick.

(defprotocol Cycle

  (values! [this vlst]
    "(.values! Cycle vlst)
     Set elements of this Cycle.
     ARGS:
       vlst - non-empty list or vector
     RETURNS: vector holding contents of vlst.")
  
  (values [this]
    "(values Cycle)
     RETURNS: vector holding cycle elements.")

  (period [this]
    "(.period Cycle)
     RETURNS: int, number of elements in cycle.")
  
  (value
    [this]
    [this n]
    "(.value Cycle)
     (.value Cycle index)
     Retrieve selected element from cycle.
     ARGS:
       n - optional int. If specified returns the nth element from cycle.
           Indexing is cyclical so that if n > cycle period or n < 0, the 
           actual index is modulo the cycle period.
           If not n not specified the most previous index is used.
     RETURNS: Object, the selected element.")
  
  (next [this]
    "(.next Cycle)
     RETURNS: Object, the next element in cycle. Once end of cycle has been 
     reached start over from beginning.")

  (pick [this]
    "(.pick Cycle)
     RETURNS: Object, a random element from cycle.")
  
  (midi-reset [this]
    "(.midi-reset Cycle)
     Sets current element index to 0.
     RETURNS: int, 0"))


(defn cycle
  "(cycle vals)
   Creates Cycle with initial elements. 
   ARGS:
     vals - non-empty list or vector.
   RETURNS:
     instance of Cycle"
  ([vals]
   (let [pointer* (atom 0)
         values* (atom (into [] vals))
         cy (reify Cycle

              (values! [this vlst]
                (reset! pointer* 0)
                (reset! values* (into [] vlst)))

              (values [this] @values*)

              (period [this]
                (count @values*))

              (value [this]
                (nth @values* @pointer*))

              (value [this n]
                (let [index (util/abs (rem n (.period this)))]
                  (reset! pointer* (rem (inc index)(.period this)))
                  (nth @values* index)))

              (next [this]
                (let [rs (nth @values* @pointer*)]
                  (swap! pointer* (fn [q](rem (inc q) (.period this))))
                  rs))

              (pick [this]
                (let [i (rand-int (.period this))]
                  (.value this i)))
              
              (midi-reset [this]
                (reset! pointer* 0)))]
     cy)))
