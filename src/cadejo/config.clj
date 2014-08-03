
;; Defines global Cadejo configuration values.
;; For the moment (2014.06.17) this is a place holder file
;; with hard-coded configuration values.
;;
;; Eventually configuration values are to be determined by loading
;; configuration files from the user's cadejo folder and/or via 
;; command line arguments. 

(println "--> cadejo.config")

(ns cadejo.config
  (:use [cadejo.util.trace])
  (:require [cadejo.util.user-message :as umsg])
  (:import java.awt.Color))
            


(defn cadejo-version []
  "0.1.1-SNAPSHOT")

(defn channel-count [] 
  "Returns number of MIDI channels per scene"
  16)
  

(defn midi-input-ports []
  "Returns list of names for available MIDI input ports"
  '("UM1SX"))
    

(defn midi-input-port 
  "(midi-input-port n)
   (midi-input-port)
   Returns name for nth MIDI input port, the default port is 0"
  ([n]
     (nth (midi-input-ports) n))
  ([](midi-input-port 0)))


(defn load-gui []
  "Returns true if GUI components are to be loaded.
   Cadejo should be able to operate in a 'headless' mode without GUI 
   components.

   When operating 'headless' code expecting to interact with GUI components
   should gracefully handle nil."
  true)


;; ISSUE: Update ui.util.undo-stack to self limit based on config value
;; instead of limiting size in client space.
(defn maximum-undo-count []  
  10)

(defn warn-on-file-overwrite []
  false)

(defn enable-tooltips []
  true)

(defn config-path []
  "/home/sj/.cadejo")



;; Returns the maximum number of child performance which may be attached
;; to any single channel node.
;;
(defn max-channel-children []
  8)



;; Available Instruments
;;

(def ^:private instruments* (atom (sorted-map)))

(defn add-instrument! [descriptor]
  (swap! instruments* (fn [n](assoc n 
                               (keyword (.instrument-name descriptor))
                               descriptor))))

(defn instruments []
  (keys @instruments*))

(defn instrument-descriptor [iname]
  (let [key (keyword iname)
        d (get @instruments* key)]
    (or d
        (umsg/warning (format "Instrument %s is not available" iname)))))

(defn create-instrument [iname mode & args]
  (trace-enter "config/create-instrument")
  (let [ides (instrument-descriptor iname)]
    (if ides
      (let [s (first args)
            dummy-1 (trace-mark "s" s "   type s" (type s))
            sed (.get-editor s)
            dummy-2 (trace-mark "sed" sed)
            ]
        (trace-mark "post let")
        (.create ides mode args)
        (if sed (.sync-ui! sed))
        )))
  (trace-exit))
        
