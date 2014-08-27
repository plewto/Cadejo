(ns cadejo.ui.util.factory
  (:use [cadejo.ui.util.icon :only [icon]])
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
  ([padding]
     (ssb/compound-border
      (ssb/line-border)
      (ssb/empty-border :thickness padding)))
  ([]
     (line 4)))


;; Buttons
;;

;; Return JToggleButton with 2-icons
(defn toggle [i-unselected i-selected]
  (let [b (ss/toggle)]
    (.setIcon b i-unselected)
    (.setSelectedIcon b i-selected)
    b))


;; Return 'pad' style toggle-button
;; n - button style 1,2,...
;;
(defn pad-button 
  ([](pad-button 1))
  ([n]   
     (let [i1 (icon (format "switches/pad_off_%02d.png" n))
           i2 (icon (format "switches/pad_on_%02d.png" n))
           b (toggle i1 i2)]
       (ss/config! b :size [48 :by 48])
       b)))
  
;; Return pad style filter selection button
;; ftype - filter type one of :bp :br :lp :hp :bypass 
;; n - button style 1,2,...
;;
(defn filter-button 
  ([ftype](filter-button ftype 1))
  ([ftype n]
     (let [i1 (icon (format "switches/filter_%s_off_%02d.png" (name ftype) n))
           i2 (icon (format "switches/filter_%s_on_%02d.png" (name ftype) n))
           b (toggle i1 i2)]
       (ss/config! b :size [48 :by 48]))))

;; Return 'rocker' style toggle button
;; n - button-style 1,2,3,...
;; 
(defn rocker-button
  ([](rocker-button 1))
  ([n]
     (let [i1 (icon (format "switches/rocker_off_%02d.png" n))
           i2 (icon (format "switches/rocker_on_%02d.png" n))
           b (toggle i1 i2)]
       (ss/config! b :size [48 :by 84]))))
