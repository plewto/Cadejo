(ns cadejo.ui.util.factory
  (:use [cadejo.ui.util.icon :only [icon]])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
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
(defn bevel 
  ([]
     (BorderFactory/createLoweredBevelBorder))
  ([pad]
     (ssb/compound-border
      (bevel)
      (padding pad))))

(defn line 
  ([padding color]
     (ssb/compound-border
      (ssb/line-border :color color)
      (ssb/empty-border :thickness padding)))
  ([padding]
     (ssb/compound-border
      (ssb/line-border)
      (ssb/empty-border :thickness padding)))
  ([]
     (line 4)))


;; Buttons
;;

;; (defn button-size [n]
;;   (get {0 [48 :by 48]
;;        }
;;        n [48 :by 48]))

;; Return JToggleButton with 2-icons
;;
(defn toggle [i-unselected i-selected]
  (let [b (ss/toggle)]
    (.setIcon b i-unselected)
    (.setSelectedIcon b i-selected)
    b))

;; Return JRadioButton with 2-icons
;;
(defn radio [i-unselected i-selected]
  (let [b (ss/radio)]
    (.setIcon b i-unselected)
    (.setSelectedIcon b i-selected)
    b))


;; Return filter selection button
;; ftype - keyword, filter type one of :bp :br :lp :hp :bypass 

(defn filter-button [ftype]
  (let [size [36 :by 36]
        sub (ftype {:bp :band, :br :notch, :lp :low, :hp :high, :bypass :none})
        i1 (lnf/read-icon :filter sub)
        i2 (lnf/read-selected-icon :filter sub)
        b (radio i1 i2)]
    (if (config/enable-tooltips)
      (.setToolTipText b (cond (= ftype :bypass) "Filter bypass"
                               (= ftype :br) "Notch filter"
                               :default (format "%s-pass filter" (name sub)))))
    (ss/config! b :size size)
    b))

