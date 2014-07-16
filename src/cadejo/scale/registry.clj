(ns cadejo.scale.registry
  ;(:require [cadejo.scale.just :as just])
  (:require [cadejo.scale.eqtemp :as eqtemp])
  (:require [cadejo.scale.table :as table])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:import java.io.FileNotFoundException))

(def magic-number :cadejo-scale-registry)
(def file-extension "ttreg")
(declare -create-scale-registry)


(defprotocol RegistryProtocol

  (registered-keys
    [this]
    "Return sorted list of registered scale ids (keyword)")

  (get-table
    [this id]
    "Return scale id or nil
     If nil print warning")

  (add-table!
    [this id ttab]
    "Add tuning table to registry with given id, table after insertion")

  (remove-table!
    [this id]
    "Remove given scale from registry
     Return table after deletion")

  (serialize
    [this]
    "Convert registry to serialized format
     return string")
  
  (clone
    [this])

  (copy-state!
    [this other])

  (write-registry
    [this filename]
    "Write registry contents to file
     Return nil if errors encountered, true otherwise")

  (read-registry!
    [this filename]
    "Read registry file contents into this")

  (dump
    [this verbose depth]
    [this depth]
    [this])
)
    
(deftype ScaleRegistry [tables*]
  
  RegistryProtocol

  (registered-keys [this]
    (sort (keys @tables*)))

  (get-table [this id]
    (let [tt (get @tables* id)]
      (or tt
          (umsg/warning "ScaleRegistry does not contain table %s" id)
          eqtemp/default-table)))

  (add-table! [this id tt]
    (swap! tables* (fn [n](assoc n id tt))))

  (remove-table! [this id]
    (swap! tables* (fn [n](dissoc n id))))

  (serialize [this]
    (let [sb (StringBuilder. 7500)]
      (.append sb (format "{:file-type %s\n" magic-number))
      (.append sb " :payload {\n")
      (doseq [p (seq @tables*)]
        (.append sb (format " %s\n" (first p)))
        (.append sb (.serialize (second p))))
      (.append sb "}}")
      (.toString sb)))

  (clone [this]
    (-create-scale-registry @tables*))

  (copy-state! [this other]
    (swap! tables* (fn [n] {}))
    (doseq [k (.registered-keys other)]
      (.add-table! this k (.clone (.get-table other k))))
    this)

  (write-registry [this filename]
    (try
      (spit filename (.serialize this))
      filename
      (catch FileNotFoundException ex
        (umsg/warning "FileNotFoundException"
                      "class cadejo.scale.registry.ScaleRegistry"
                      "method write-registry"
                      (format "filename '%s'" filename))
        nil)))

  (read-registry! [this filename]
    (println (format "DEBUG read-registry! filanem '%s'" filename))
    (try
      (let [raw-data (read-string (slurp filename))
            ftype (:file-type raw-data)
            payload (:payload raw-data)]
        (println "DEBUG read-registry! post let")
        (if (= ftype magic-number)
          (do
            (println "DEBUG read-registry! magic-number OK")
            (swap! tables* (fn [n] {}))
            (doseq [k (keys payload)]
              (let [data (get payload k)
                    ttab (table/create-tuning-table (:properties data)
                                                    (:table data))]
                (.add-table! this k ttab))) 
            true)
          (umsg/warning "Wrong file type"
                        "class cadejo.scale.registry.ScaleRegistry"
                        "method read-registry!"
                        (format "Expected %s, encountered %s" magic-number ftype)
                        (format "filename '%s'" filename))))
      (catch FileNotFoundException ex
        (umsg/warning "FileNotFoundException"
                        "class cadejo.scale.registry.ScaleRegistry"
                        "method read-registry!"
                        (format "filename '%s'" filename)))))

  (dump [this verbose depth]
    (let [depth2 (inc depth)
          pad (cadejo.util.string/tab depth)
          pad2 (cadejo.util.string/tab depth2)]
      (printf "%sScaleRegistry\n" pad)
      (doseq [k (.registered-keys this)]
        (printf "%s%s" pad2 k)
        (if verbose 
          (printf " %s" (.get-property (.get-table this k) :remarks)))
        (println))))

  (dump [this verbose]
    (.dump this verbose 0))

  (dump [this]
    (.dump this false))
)


(defn- -create-scale-registry [tables]
  (ScaleRegistry. (atom tables)))
          
(defn create-scale-registry []
     (-create-scale-registry {:eq-12 eqtemp/default-table}))
