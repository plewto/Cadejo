(ns cadejo.ui.util.factory
  (:use [cadejo.ui.util.icon :only [icon]])
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:require [seesaw.border :as ssb])
  (:import javax.swing.BorderFactory
           javax.swing.SwingConstants))

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

(defn config-button [b txt icon-main icon-sub tooltip bgroup]
  (if bgroup (ss/config! b :group bgroup))
  (if (and tooltip (config/enable-tooltips))
    (.setToolTipText b (str tooltip)))
  (if (config/enable-button-text)
    (ss/config! b :text (str txt)))
  (if (and icon-main (config/enable-button-icons))
    (let [i1 (lnf/read-icon icon-main icon-sub)
          i2 (lnf/read-selected-icon icon-main icon-sub)
          i3 (lnf/read-disabled-icon icon-main icon-sub)]
      (.setIcon b i1)
      (.setDisabledIcon b i3)
      (.setSelectedIcon b i2)))
  (if (and (config/enable-button-text)
           (config/enable-button-icons))
    (do
      (.setVerticalTextPosition b SwingConstants/BOTTOM)
      (.setHorizontalTextPosition b SwingConstants/CENTER)))
  (.setOpaque b false)
  (.setContentAreaFilled b false)
  (.setBorderPainted b true)
  b)

(defn radio 
  ([txt icon-main icon-sub tooltip bgroup]
     (let [b (ss/radio)]
       (config-button b txt icon-main icon-sub tooltip bgroup)))
  ([txt icon-main icon-sub tooltip]
     (radio txt icon-main icon-sub tooltip nil))
  ([txt icon-main icon-sub]
     (radio txt icon-main icon-sub nil))
  ([txt]
     (radio txt nil nil)))

(defn toggle
  ([txt icon-main icon-sub tooltip bgroup]
     (let [b (if (config/enable-button-icons)
               (ss/radio)
               (ss/toggle))]
       (config-button b txt icon-main icon-sub tooltip bgroup)))
  ([txt icon-main icon-sub tooltip]
     (toggle txt icon-main icon-sub tooltip nil))
  ([txt icon-main icon-sub]
     (toggle txt icon-main icon-sub nil))
  ([txt]
     (toggle txt nil nil)))                 

(defn button
  ([txt icon-main icon-sub tooltip]
     (let [b (ss/button)]
       (config-button b txt icon-main icon-sub tooltip nil)))
  ([txt icon-main icon-sub]
     (button txt icon-main icon-sub nil))
  ([txt]
     (button txt nil nil)))                 

;; Return filter selection button
;; ftype - keyword, filter type one of :bp :br :lp :hp :bypass 

(defn filter-button [ftype]
  (let [size [36 :by 36]
        sub (ftype {:bp :band, :br :notch, :lp :low, :hp :high, :bypass :none})
        i1 (lnf/read-icon :filter sub)
        i2 (lnf/read-selected-icon :filter sub)
        b (ss/radio)]
    (if (config/enable-tooltips)
      (.setToolTipText b (cond (= ftype :bypass) "Filter bypass"
                               (= ftype :br) "Notch filter"
                               :default (format "%s-pass filter" (name sub)))))
    (.setIcon b i1)
    (.setSelectedIcon b i2)
    (ss/config! b :size size)
    b))


