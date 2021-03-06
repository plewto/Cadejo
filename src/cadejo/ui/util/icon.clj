(ns cadejo.ui.util.icon
  (:require [cadejo.util.path :as path])
  (:require [seesaw.icon])
  (:import java.io.File
           javax.imageio.ImageIO))


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

(def logo-size {:tiny    32
                :small   64
                :medium 128
                :large  256})

(defn logo-file [iname size]
  (if size
    (let [fqn (path/join logo-path 
                         (format "%s_%s.%s" 
                                 (.toLowerCase (name iname))
                                 (get logo-size size 64)
                                 logo-extension))]
    (File. fqn))
    (let [fqn (path/join logo-path
                         (format "%s.%s"
                                 (.toLowerCase (name iname))
                                 logo-extension))]
      (File. fqn))))

(defn logo 
  ([iname]
     (logo iname :small))
  ([iname size]
     (let [f (logo-file iname size)]
       (seesaw.icon/icon f))))


;; Filename for splash screen image
;; (def ^:private splash-filename (path/join logo-path
;;                                (format "splash2.%s" logo-extension)))

;(def splash-image (seesaw.icon/icon (File. splash-filename)))

(defn splash-image
  ([n]
   (let [filename (path/join logo-path
                             (format "splash%d.%s" n logo-extension))]
     (seesaw.icon/icon (File. filename))))
  ([]
   (splash-image 1)))
