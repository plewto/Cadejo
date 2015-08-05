(ns cadejo.scale.registry
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.string])
  (:require [cadejo.scale.table :as table])
  (:require [cadejo.scale.eqtemp :as eqtemp])
  ;(:require [cadejo.util.trace :as trace])
  (:import java.io.FileNotFoundException
  ))

(def magic-number :cadejo-scale-registry)
(def file-extension "sreg")
(declare create-scale-registry)


(defprotocol ScaleRegistry

  (registered-tables
    [this])

  (table 
    [this id])

  (add-table!
    [this id tt])

  (remove-table!
    [this id])

  (serialize
    [this])

  (clone 
    [this])

  (copy-state!
    [this other])

  (write-registry
    [this filename])

  (read-registry!
    [this filename])

  (dump 
    [this verbose depth]
    [this verbose]
    [this])
)

(defn- create-scale-registry [tables*]
  ;(trace/trace-enter "create-scale-registry")
  (let [reg (reify ScaleRegistry
              
              (registered-tables [this]
                (sort (keys @tables*)))
              
              (table [this id]
                (or (get @tables* id)
                    (umsg/warning 
                     (format "%s is not a registered tuning table, using default" id))
                    eqtemp/default-table))
              
              (add-table! [this id tt]
                (swap! tables* (fn [n](assoc n id tt))))

              (remove-table! [this id]
                (swap! tables* (fn [n](dissoc n id))))
              
              (serialize [this]
                (let [sb (StringBuilder. 7500)]
                  (.append sb (format "{:file-type %s\n" magic-number))
                  (.append sb " :payload {\n")
                  (doseq [[id tt] (seq @tables*)]
                    (.append sb (format " %s\n" id))
                    (.append sb (.serialize tt)))
                  (.append sb "}}")
                  (.toString sb)))

              (clone [this]
                (create-scale-registry (atom @tables*)))

              (copy-state! [this other]
                (reset! tables* {})
                (doseq [k (.registered-tables other)]
                  (.add-table! this k (.clone (.table other k))))
                this)

              (write-registry [this filename]
                (try
                  (spit filename (.serialize this))
                  filename
                  (catch FileNotFoundException ex
                    (umsg/warning "FileNotFoundException"
                                  "(.write-registry cadejo.scale.registry.ScaleRegistry <filename>)"
                                  (format "filename '%s'" filename)))))

              (read-registry! [this filename]
                (try
                  (let [raw-data (read-string (slurp filename))
                        ftype (:file-type raw-data)
                        payload (:payload raw-data)]
                    (if (= ftype magic-number)
                      (do
                        (reset! tables* {})
                        (doseq [k (keys payload)]
                          (let [data (get payload k)
                                tt (table/create-tuning-table (:properties data)
                                                              (:table data))]
                            (.add-table! this k tt)))
                        filename)
                      (umsg/warning "Wrong file type"
                                    "(.read-registry! cadejo.scale.registry.ScaleRegistry <filename>)"
                                    (format "Expected %s, encountered %s" magic-number ftype)
                                    (format "filename '%s'" filename))))
                  (catch FileNotFoundException ex
                      (umsg/warning "FileNotFoundException"
                                    "(.read-registry! cadejo.scale.registry.ScaleRegistry <filename>)"
                                    (format "filename '%s'" filename)))))

              (dump [this verbose depth]
                (let [pad (cadejo.util.string/tab depth)
                      pad2 (str pad pad)]
                  (printf "%ScaleRegistry\n" pad)
                  (doseq [k (.registered-tables this)]
                    (printf "%s%s" pad2 k)
                    (if verbose 
                      (printf " %s" (.get-property (.table this k) :remarks)))
                    (println))))

              (dump [this verbose]
                (.dump this verbose 0))

              (dump [this]
                (.dump this true 0)))]
    ;(trace/trace-exit)
    reg))

(defn scale-registry []
  (create-scale-registry (atom {:eq-12 eqtemp/default-table})))
              
                                  

              
