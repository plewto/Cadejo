(ns cadejo.ui.util.lnf
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  ;(:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
  (:require [seesaw.font :as ssf]) ;; debug only
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

;; Map skin name to color scheme
;; Each scheme is a map with keys
;;    :text-fg          - 'normal' button/label foreground
;;    :text-fg-selected - selected buttopn/label foreground
;;    :text-bg          - 'normal' button/lable background
;;    :text-bg-selected - selected button/label background
;;    :bg               - pannel background
;;
(def color-schemes
  {"Autumn" {:text-fg (ssc/color 172  98  59)
             :text-fg-selected (ssc/color 172  98  59)
             :text-bg (ssc/color 254 229 201)
             :text-bg-selected (ssc/color 252 200 126)
             :bg (ssc/color 255 227 197)}
   "Business" {:text-fg (ssc/color  14  19  24)
               :text-fg-selected (ssc/color 14  19  24)
               :text-bg (ssc/color 182 188 193)
               :text-bg-selected (ssc/color 236 239 243)
               :bg (ssc/color 216 221 225)}
   "Business Black Steel" {:text-fg (ssc/color  21  26  31)
                           :text-fg-selected (ssc/color 19  19  19)
                           :text-bg (ssc/color 209 214 220)
                           :text-bg-selected (ssc/color 122 177 212)
                           :bg (ssc/color 241 246 250)}
   "Business Blue Steel" {:text-fg (ssc/color  21  26  31)
                          :text-fg-selected (ssc/color 19  19  19)
                          :text-bg (ssc/color 209 214 220)
                          :text-bg-selected (ssc/color 122 177 212)
                          :bg (ssc/color 241 246 250)}
   "Cerulean" {:text-fg (ssc/color  42  46  54)
               :text-fg-selected (ssc/color 85  88  94)
               :text-bg (ssc/color 245 245 245)
               :text-bg-selected (ssc/color 189 219 236)
               :bg (ssc/color 251 252 252)}
   "Challenger Deep" {:text-fg (ssc/color 255 255 255)
                      :text-fg-selected (ssc/color 255 255 255)
                      :text-bg (ssc/color  50  30 113)
                      :text-bg-selected (ssc/color 46   8  69)
                      :bg (ssc/color  31  20  67)}
   "Creme" {:text-fg (ssc/color   0   0   0)
            :text-fg-selected (ssc/color 38  38  38)
            :text-bg (ssc/color 242 243 237)
            :text-bg-selected (ssc/color 160 224 248)
            :bg (ssc/color 238 243 230)}
   "Creme Coffee" {:text-fg (ssc/color   0   0   0)
                   :text-fg-selected (ssc/color 50  34  15)
                   :text-bg (ssc/color 246 246 241)
                   :text-bg-selected (ssc/color 236 207 142)
                   :bg (ssc/color 238 243 230)}
   "Dust" {:text-fg (ssc/color  59  54  57)
           :text-fg-selected (ssc/color 34  38  41)
           :text-bg (ssc/color 230 228 221)
           :text-bg-selected (ssc/color 174 155 123)
           :bg (ssc/color 234 231 226)}
   "Dust Coffee" {:text-fg (ssc/color  67  60  50)
                  :text-fg-selected (ssc/color 50  34  15)
                  :text-bg (ssc/color 222 211 181)
                  :text-bg-selected (ssc/color 236 205 141)
                  :bg (ssc/color 232 217 185)}
   "Emerald Dusk" {:text-fg (ssc/color 255 255 255)
                   :text-fg-selected (ssc/color 255 255 255)
                   :text-bg (ssc/color  20  59  33)
                   :text-bg-selected (ssc/color 38  90  16)
                   :bg (ssc/color  14  50  35)}
   "Gemini" {:text-fg (ssc/color   0   0   0)
             :text-fg-selected (ssc/color 112  67  11)
             :text-bg (ssc/color 181 191 190)
             :text-bg-selected (ssc/color 255 230  95)
             :bg (ssc/color 210 224 224)}
   "Graphite" {:text-fg (ssc/color 180 180 180)
               :text-fg-selected (ssc/color 200 200 200)
               :text-bg (ssc/color  66  66  66)
               :text-bg-selected (ssc/color 107 107 107)
               :bg (ssc/color  77  77  77)}
   "Graphite Aqua" {:text-fg (ssc/color 180 180 180)
                    :text-fg-selected (ssc/color 200 200 200)
                    :text-bg (ssc/color  63  63  63)
                    :text-bg-selected (ssc/color 89  89  89)
                    :bg (ssc/color  77  77  77)}
   "Graphite Glass" {:text-fg (ssc/color  69  69  69)
                     :text-fg-selected (ssc/color 200 200 200)
                     :text-bg (ssc/color  64  64  64)
                     :text-bg-selected (ssc/color 103 103 103)
                     :bg (ssc/color  77  77  77)}
   "Magellan" {:text-fg (ssc/color 143 193 249)
               :text-fg-selected (ssc/color 2  28  58)
               :text-bg (ssc/color   3  68 133)
               :text-bg-selected (ssc/color 15 112 219)
               :bg (ssc/color  12  90 176)}
   "Mariner" {:text-fg (ssc/color   0   0   0)
              :text-fg-selected (ssc/color 74  25   3)
              :text-bg (ssc/color 226 228 227)
              :text-bg-selected (ssc/color 251 215 137)
              :bg (ssc/color 217 219 223)}
   "Mist Aqua" {:text-fg (ssc/color  15  20  24)
                :text-fg-selected (ssc/color 0   0   0)
                :text-bg (ssc/color 216 221 225)
                :text-bg-selected (ssc/color 125 204 236)
                :bg (ssc/color 228 233 238)}
   "Mist Silver" {:text-fg (ssc/color  15  20  24)
                  :text-fg-selected (ssc/color 64  64  64)
                  :text-bg (ssc/color 216 221 225)
                  :text-bg-selected (ssc/color 204 220 230)
                  :bg (ssc/color 228 233 238)}
   "Moderate" {:text-fg (ssc/color  15  20  24)
               :text-fg-selected (ssc/color 0   0   0)
               :text-bg (ssc/color 228 232 237)
               :text-bg-selected (ssc/color 106 166 204)
               :bg (ssc/color 240 245 250)}
   "Nebula" {:text-fg (ssc/color  42  46  54)
             :text-fg-selected (ssc/color 0   0   0)
             :text-bg (ssc/color 237 238 241)
             :text-bg-selected (ssc/color 193 213 232)
             :bg (ssc/color 244 247 252)}
   "Nebula Brick Wall" {:text-fg (ssc/color  42  46  54)
                        :text-fg-selected (ssc/color 0   0   0)
                        :text-bg (ssc/color 237 238 241)
                        :text-bg-selected (ssc/color 193 213 232)
                        :bg (ssc/color 244 247 252)}
   "Office Black 2007" {:text-fg (ssc/color  40  40  40)
                        :text-fg-selected (ssc/color 76  83  92)
                        :text-bg (ssc/color 195 202 208)
                        :text-bg-selected (ssc/color 254 199 120)
                        :bg (ssc/color 208 213 217)}
   "Office Blue 2007" {:text-fg (ssc/color  21  66 139)
                       :text-fg-selected (ssc/color 29  71 139)
                       :text-bg (ssc/color 203 221 244)
                       :text-bg-selected (ssc/color 252 211 156)
                       :bg (ssc/color 200 219 238)}
   "Office Silver 2007" {:text-fg (ssc/color  76  83  92)
                         :text-fg-selected (ssc/color 76  83  92)
                         :text-bg (ssc/color 240 242 242)
                         :text-bg-selected (ssc/color 253 208 147)
                         :bg (ssc/color 243 245 245)}
   "Raven" {:text-fg (ssc/color 255 255 255)
            :text-fg-selected (ssc/color 27  32  37)
            :text-bg (ssc/color  57  47  38)
            :text-bg-selected (ssc/color 232 235 240)
            :bg (ssc/color  25  18  12)}
   "Sahara" {:text-fg (ssc/color  15  20  25)
             :text-fg-selected (ssc/color 0   0   0)
             :text-bg (ssc/color 225 229 234)
             :text-bg-selected (ssc/color 181 204 100)
             :bg (ssc/color 240 245 250)}
   "Twilight" {:text-fg (ssc/color 185 180 158)
               :text-fg-selected (ssc/color 0   0   0)
               :text-bg (ssc/color  58  58  51)
               :text-bg-selected (ssc/color 138 129  94)
               :bg (ssc/color  76  74  65)}})

(defn get-color [skin-name usage]
  (let [cmap (get color-schemes skin-name
                  {:text-fg (ssc/color 51 51 51)
                   :text-fg-selected (ssc/color 51 51 51)
                   :text-bg (ssc/color 243 247 250)
                   :text-bg-selected (ssc/color 184 207 229)
                   :bg (ssc/color 238 238 238)})]
    (get cmap usage (ssc/color 127 127 127))))



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
                                         (config/icon-style)))))
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
  
      
  
(defn skin-test []

  (let [grp (ss/button-group)
        fnt (ssf/font :style :bold :size 36)
        b1 (ss/toggle :text "Selected"
                      :group grp
                      :selected? true
                      :font fnt)
        b2 (ss/toggle :text "UnSelected"
                      :group grp
                      :font fnt)
        b3 (ss/toggle :text "Disabled"
                      :font fnt
                      :enabled? false)
        pan-south (ss/grid-panel :rows 1 :items [b1 b2 b3])
        f (ss/frame :title "Skin Test"
                    :content pan-south
                    :on-close :dispose)]
    (ss/pack! f)
    (ss/show! f)
    (skin-dialog)))
