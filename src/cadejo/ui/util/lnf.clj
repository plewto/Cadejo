(ns cadejo.ui.util.lnf
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:require [seesaw.icon])
  (:require [seesaw.swingx :as swingx])
  (:import org.pushingpixels.substance.api.SubstanceLookAndFeel
           org.pushingpixels.substance.api.UiThreadingViolationException
           java.io.File))

(def ^:private skins (SubstanceLookAndFeel/getAllSkins))

(def available-skins (keys skins))

;; Map skin to 'normal' icon index
;;
(def skin-icon-index 
  {"Autumn" 0,
   "Business" 10,
   "Business Black Steel" 12,
   "Business Blue Steel" 12,
   "Cerulean" 11,
   "Challenger Deep" 19,
   "Creme" 9,
   "Creme Coffee" 9,
   "Dust" 9,
   "Dust Coffee" 7,
   "Emerald Dusk" 21,
   "Gemini" 10,
   "Graphite" 15,
   "Graphite Aqua" 14,
   "Graphite Glass" 14,
   "Magellan" 17,
   "Mariner" 9,
   "Mist Aqua" 12,
   "Mist Silver" 12,
   "Moderate" 12,
   "Nebula" 12,
   "Nebula Brick Wall" 12,
   "Office Black 2007" 10,
   "Office Blue 2007" 6,
   "Office Silver 2007" 12,
   "Raven" 22,
   "Sahara" 12,
   "Twilight" 23})

;; Map skin to 'selected' icon index
;;
(def skin-selected-icon-index 
  {"Autumn" 2,
   "Business" 12,
   "Business Black Steel" 5,
   "Business Blue Steel" 5,
   "Cerulean" 6,
   "Challenger Deep" 18,
   "Creme" 6,
   "Creme Coffee" 3,
   "Dust" 8,
   "Dust Coffee" 1,
   "Emerald Dusk" 20,
   "Gemini" 2,
   "Graphite" 10,
   "Graphite Aqua" 15,
   "Graphite Glass" 15,
   "Magellan" 16,
   "Mariner" 2,
   "Mist Aqua" 5,
   "Mist Silver" 4,
   "Moderate" 5,
   "Nebula" 6,
   "Nebula Brick Wall" 6,
   "Office Black 2007" 1,
   "Office Blue 2007" 1,
   "Office Silver 2007" 1,
   "Raven" 12,
   "Sahara" 25,
   "Twilight" 24})

(defn skin-name [i]
  (nth available-skins i))


;; Return java.io.File for icon using the current config/icon-style
;; group - keyword, major icon grouping  :curve :filter :env ....
;; subgroup - keyword, specific icon within in group 
;;
(defn- get-icon-file 
  ([style group subgroup]
     (let [name (format "%02d_%s%s.png"
                        style 
                        (name group)
                        (if subgroup (format "_%s" (name subgroup)) ""))
           pathname (path/join "resources" "icons" name)]
       (File. pathname)))
  ([group subgroup]
     (get-icon-file (config/icon-style) group subgroup nil)))


;; Return 'normal' un-selected icon
;; style    - int or String, style may be either int icon index 
;;            0 <= style <= 25 or skin name as String
;; group    - keyword, major icon group 
;; subgroup - keyword, specific icon within group.
;;
(defn read-icon 
  ([style group subgroup]
     (let [sty (cond (= (type style) java.lang.String)
                     (get skin-icon-index style 11)
                     (= (type style) java.lang.Long)
                     (min (max 0 style) 25)
                     :default 11)
           f (get-icon-file sty group subgroup)]
       (seesaw.icon/icon f)))
  ([group subgroup]
     (read-icon (config/icon-style) group subgroup)))


;; Return 'selected' icon
;; style    - int or String, style may be either int icon index 
;;            0 <= style <= 25 or skin name as String
;; group    - keyword, major icon group 
;; subgroup - keyword, specific icon within group.
;;
(defn read-selected-icon
  ([style group subgroup]
     (let [sty (cond (= (type style) java.lang.String)
                     (get skin-selected-icon-index style)
                     (= (type style) java.lang.Long)
                     (min (max 0 style) 25)
                     :default 6)
           f (get-icon-file sty group subgroup)]
       (seesaw.icon/icon f)))
  ([group subgroup]
     (read-selected-icon (config/selected-icon-style) group subgroup)))

;; Return skin-selectin panel
;;
(defn- lnf-selector-panel []
  (let [grp (ss/button-group)
        buttons (let [acc* (atom [])
                      counter* (atom 0)]
                  (doseq [[k s](seq skins)]
                    (let [tb (ss/toggle :text (format "%2d %s" @counter* k)
                                        :group grp
                                        :selected? (= k (config/current-skin)))]
                      (.putClientProperty tb :skin s)
                      (swap! acc* (fn [n](conj n tb)))
                      (swap! counter* inc)
                      (ss/listen tb :action
                                 (fn [ev]
                                   (let [src (.getSource ev)
                                         skin (.getClientProperty src :skin)]
                                     ;(reset! current-skin-name* (.getDisplayName skin))
                                     (config/current-skin! (.getDisplayName skin))
                                     (ss/invoke-later
                                      (SubstanceLookAndFeel/setSkin (.getClassName skin))))))))
                  @acc*)]
    (ss/grid-panel :rows 6 :columns 5 :items buttons)))
                                        
;; Pop up skin selection dialog.
;;                  
(defn skin-dialog []
  (let [pan-main 
        (swingx/titled-panel
         :title "Substance Skins"
         :content (ss/border-panel 
                   :center (lnf-selector-panel)
                   :south (ss/label 
                           :text (format "Current config icon style is %s" 
                                         (config/icon-style))
                           :border (factory/bevel))))
        jb-dismis (ss/button :text "Dismis")
        dia (ss/dialog :title "Substance Skins"
                       :content pan-main
                       :on-close :dispose
                       :size [1000 :by 500]
                       :options [jb-dismis])]
    (ss/listen jb-dismis :action (fn [_](ss/return-from-dialog dia true)))
    (ss/show! dia)))


;; Set initial skin
;;
(defn set-initial-skin []
  (let [skin-name (config/initial-skin)
        skin (get skins skin-name)]
    (if skin 
      (do 
        (ss/invoke-later
         (SubstanceLookAndFeel/setSkin (.getClassName skin)))
        (config/current-skin! skin-name)
        (umsg/message (format "Using skin '%s' icon-style %s"
                              skin-name (config/icon-style)))))
    (if (and skin-name (not skin))
      (umsg/warning (format "config initial-skin value '%s' is invalid" skin-name)))))
  
      
  
