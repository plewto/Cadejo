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



(def logo-path (path/join "resources" "logos"))
(def logo-extension icon-extension)

(def logo-size {:small 64
                :medium 128
                :large 256})

(defn logo-file [iname size]
  (let [fqn (path/join logo-path 
                       (format "%s_%s.%s" 
                               (.toLowerCase (name iname))
                               (get logo-size size 64)
                               logo-extension))]
    (File. fqn)))

(defn logo 
  ([iname]
     (logo iname :small))
  ([iname size]
     (let [f (logo-file iname size)]
       (seesaw.icon/icon f))))
