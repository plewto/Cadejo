;; Defines global Cadejo configuration values.
;; For the moment (2014.06.17) this is a place holder file
;; with hard-coded configuration values.
;;
;; Eventually configuration values are to be determined by loading
;; configuration files from the user's cadejo folder and/or via 
;; command line arguments. 

(ns cadejo.config )

(defn load-gui []
  "Returns true if GUI components are to be loaded.
   Cadejo should be able to operate in a 'headless' mode without GUI 
   components.

   When operating 'headless' code expecting to interact with GUI components
   should gracefully handle nil."
  true)
