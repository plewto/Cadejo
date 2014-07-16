(ns cadejo.scale.table
  (:require [cadejo.util.user-message :as umsg])
  (:import java.io.FileNotFoundException))
            
(def magic-number :cadejo-tuning-table)
(def file-extension "ttab")
(declare create-tuning-table)

(def table-length 128)

(defprotocol TuningTableProtocol

  (init!
    [this])

  (get-table 
    [this]
    "Return vector of key frequencies")

  (serialize 
    [this]
    "Convert contents into readable string format")

  (write-table
    [this filename]
    "Save table to filename
     Returns nil on error")

  (read-table!
    [this filename]
    "Read table from filename
     Returns nil on error")

  (properties 
    [this]
    "Returns list of property keys")

  (get-property
    [this key]
    "Returns property value for key, or nil if key is not defined")

  (put-property!
    [this key value]
    "Sets property key to value")
 
  (get-key-frequency 
    [this keynum]
    "Return the frequency in Hertz for keynumber key
     where key is an integer 0 <= key < 128")

  (set-key-frequency!
    [this keynum freq]
    "Sets the frequency of keynumber key")

  (clone 
    [this]))
  
(deftype TuningTable [properties* ftab*]

  TuningTableProtocol

  (init! [this]
    (swap! properties* (fn [n]{}))
    (swap! ftab* (fn [](repeat table-length 440))))

  (get-table [this]
    @ftab*)

  (serialize [this]
    (let [sb (StringBuilder. 2000)]
      (.append sb (format "{:file-type %s\n" magic-number))
      (.append sb " :properties {\n")
      (doseq [p @properties*]
        (let [v (second p)]
          (.append sb (format "  %s " (first p)))
          (if (string? v)
            (.append sb (format "\"%s\"\n" v))
            (.append sb (format "%s\n" v)))))
      (.append sb " }\n")
      (.append sb " :table [")
      (let [counter* (atom 0)]
        (doseq [f @ftab*]
          (if (zero? @counter*)
            (do
              (.append sb "\n  ")
              (swap! counter* (fn [n] 6))))
          (.append sb (format "%f " (float f)))
          (swap! counter* dec)))
      (.append sb "]}\n")
      (.toString sb)))
 
  (write-table [this filename]
    (try
        (spit filename (.serialize this))
        filename
        (catch java.io.FileNotFoundException e
          (umsg/warning "FileNotFoundException"
                        "TuningTable.write-table"
                        (format "filename \"%s\"" filename))
          nil)))

  (read-table! [this filename]
    (try
      (let [rec (read-string (slurp filename))
            ftype (:file-type rec)]
        (if (= ftype magic-number)
          (do 
            (swap! properties* (fn [n](:properties rec)))
            (swap! ftab* (fn [n](:table rec)))
            filename)
          (umsg/warning "Wrong File Type"
                        "TuningTable.read-table!"
                        (format "Expecting %s encountered %s" magic-number ftype)
                        (format "filename \"%s\"" filename))))
      (catch java.io.FileNotFoundException e
        (umsg/warning "FileNotFoundException"
                      "TuningTable.read-table!"
                      (format "filename \"%s\"" filename)))))
            
  (properties [this]
    (keys @properties*))

  (get-property [this key]
    (get @properties* (keyword key)))



  (put-property! [this key value]
    (swap! properties* (fn [n](assoc n (keyword key) value))))
 
  (get-key-frequency [this keynum]
    (nth @ftab* keynum))

  (set-key-frequency! [this keynum freq]
    (swap! ftab* (fn [n](assoc n keynum freq))))

  (clone [this]
    (create-tuning-table @properties* @ftab*)))
    


(defn create-tuning-table
  ([properties frequencies]
     (TuningTable. (atom properties)
                   (atom frequencies)))
  ([value]
     (create-tuning-table
      {:name :none
       :remarks ""}
      (into [](repeat table-length value))))
  ([]
     (create-tuning-table 0)))
  

