(ns cadejo.midi.cc.local
  "Provides cascading mnemonic mapping to local MIDI cc controllers."
  (:require [clojure.set :as set])
  (:require [cadejo.util.user-message :as umsg]))


(defprotocol LocalCC

  (clear! 
    [this]
    "Remove all assignments")

  (assign!
    [this key ctrl]
    "Create new alias between mnemonic key and controller ctrl
     key - keyword
     ctrl - integer controller number 0 <= ctrl < 128")
  
  (unassign!
    [this key]
    "Removes specified assignment
     key - mnemonic keyword")

  (controller
    [this key]
    "Returns controller number associated with key.
     If key is not assigned directly in this look for it in the parent.
     Finally if key is not located display warning and return default 
     controller number 1.")

  (assignments
    [this local-only]
    [this]
    "Returns a set of all assigned aliases.
     If local-only is true return only those directly assigned in this,
     otherwise return all assignments made in this and the parent of this.")

  (controllers 
    [this local-only]
    [this]
    "Returns a set of assigned controller numbers.
     If local-only is true return only controllers directly assigned in this,
     otherwise return all assignments in this and the parent of this.")

  (cc-alias 
    [this ctrl local-only]
    [this ctrl]
    "Returns the mnemonic alias for controller number ctrl
     Returns nil if ctrl has not been assigned.")
)



(defn local-cc-assignments
  ([](local-cc-assignments nil))
  ([parent]
     (let [map* (atom {})
           reverse* (atom {})]
       (reify LocalCC

         (clear! [this]
           (reset! map* {})
           (reset! reverse* {}))

         (assign! [this key ctrl]
           (swap! reverse* (fn [n](assoc n ctrl key)))
           (swap! map* (fn [n](assoc n key ctrl))))

         (unassign! [this key]
           (let [ctrl (get reverse* key)]
             (swap! reverse* (fn [n](dissoc n ctrl)))
             (swap! map* (fn [n](dissoc n key)))))

         (controller [this key]
           (let [ctrl (get @map* key)]
             (or ctrl
                 (and parent (.controller parent))
                 (umsg/warning (format "%s is not an assigned controller, using default 1" key))
                 1)))

         (assignments [this local-only]
           (set/union
            (into #{} (keys @map*))
            (if local-only
              nil
              (and parent (.assignments parent false)))))

         (assignments [this]
           (.assignments this false))

         (controllers [this local-only]
           (set/union
            (into #{} (keys @reverse*))
            (if local-only
              nil
              (and parent (.controllers parent false)))))

         (controllers [this]
           (.controllers this false))

         (cc-alias [this ctrl local-only]
           (or (get @reverse* ctrl)
               (and (not local-only)
                    parent
                    (.get-alias parent ctrl false))))

         (cc-alias [this ctrl]
           (.cc-alias this ctrl false))))))



