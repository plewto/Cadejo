
;; Defines global Cadejo configuration values.
;; For the moment (2014.06.17) this is a place holder file
;; with hard-coded configuration values.
;;
;; Eventually configuration values are to be determined by loading
;; configuration files from the user's cadejo folder and/or via 
;; command line arguments. 

(println "--> cadejo.config")

(ns cadejo.config
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

;; Returns colors in specific sequence used for 
;; performance node visual cues. 
;;
(defn performance-id-background [n]
  (let [specs [[ 65  20 105]
               [ 20  91 105]
               [ 64 105  20]
               [105  22  20]
               [131  25 127]
               [ 25  60 131]
               [ 28 131  25]
               [131  80  25]]
        i (rem n (count specs))
        rgb (nth specs i)
        r (first rgb)
        g (second rgb)
        b (last rgb)]
    (Color. r g b)))
    
(defn performance-id-foreground [n]
  (let [specs [[239 238 204]]
        i (rem n (count specs))
        rgb (nth specs i)
        r (first rgb)
        g (second rgb)
        b (last rgb)]
    (Color. r g b)))


;; Returns the maximum number of child performance which may be attached
;; to any single channel node.
;;
(defn max-channel-children []
  8)
