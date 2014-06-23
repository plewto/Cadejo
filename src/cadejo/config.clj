
;; Defines global Cadejo configuration values.
;; For the moment (2014.06.17) this is a place holder file
;; with hard-coded configuration values.
;;
;; Eventually configuration values are to be determined by loading
;; configuration files from the user's cadejo folder and/or via 
;; command line arguments. 

(println "--> Loading cadejo.config")

(ns cadejo.config )


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

(defn maximum-undo-count []
  10)


(defn warn-on-file-overwrite []
  false)

(defn enable-tooltips []
  true)

(defn config-path []
  "/home/sj/.cadejo")
