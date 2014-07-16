(ns cadejo.ui.util.factory
  (:require [seesaw.core :as ss])
  (:require [seesaw.border :as ssb])
  (:import javax.swing.BorderFactory))

;; Returns empty border
;;
(defn padding 
  ([thickness]
     (ssb/empty-border :thickness thickness))
  ([]
     (padding 4)))

;; Returns compound border with title and standard padding
;;
(defn title 
  ([text pad]
     (ssb/compound-border
      (ssb/to-border (str text))
      (padding pad)))
  ([text]
     (title text 4)))

;; Returns bevel border
;;
(defn bevel []
  (BorderFactory/createLoweredBevelBorder))

(defn line 
  ([padding]
     (ssb/compound-border
      (ssb/line-border)
      (ssb/empty-border :thickness padding)))
  ([]
     (line 4)))


;; Buttons
;;

(defn init-button [& {:keys [id-suffix]
                      :or {id-suffix ""}}]
  (ss/button :text "Init"
             :id (keyword (format "jb-init-%s" id-suffix))))

(defn delete-button [& {:keys [id-suffix]
                        :or {id-suffix ""}}]
  (ss/button :text "X" 
             :id (keyword (format "jb-delete-%s" id-suffix))))

(defn reset-button [& {:keys [id-suffix]
                       :or {id-suffix ""}}]
  (ss/button :text "--"
             :id (keyword (format "jb-reset-%s" id-suffix))))
