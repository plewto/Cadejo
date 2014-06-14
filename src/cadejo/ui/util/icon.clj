(ns cadejo.ui.util.icon
  (:require [cadejo.util.path :as path])
  (:require [seesaw,icon])
  (:import java.io.File))


(def icon-path (path/join "resources" "icons"))
(def icon-extension "png")

(defn icon-file [name]
  (let [fqn (path/append-extension 
             (path/join icon-path name)
             icon-extension)]
    (File. fqn)))
    
(defn icon [name]
  (seesaw.icon/icon (icon-file name)))
