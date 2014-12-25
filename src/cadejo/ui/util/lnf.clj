(println "--> cadejo.ui.util.lnf")

(ns cadejo.ui.util.lnf
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
  ;(:require [seesaw.font :as ssf]) ;; debug only
  (:require [seesaw.icon])
  (:require [seesaw.swingx :as swingx])
  (:import org.pushingpixels.substance.api.SubstanceLookAndFeel
           org.pushingpixels.substance.api.UiThreadingViolationException
           java.io.File))

(def ^:private skins (SubstanceLookAndFeel/getAllSkins))

(def available-skins (keys skins))

;; Map skin to 'normal' icon prefix
;;
(def ^:private skin-icon-prefix 
  {"Autumn" "gray"
   "Business" "black"
   "Business Black Steel" "black"
   "Business Blue Steel" "black"
   "Cerulean" "gray"
   "Challenger Deep" "white"
   "Creme" "black"
   "Creme Coffee" "black"
   "Dust" "black"
   "Dust Coffee" "gray"
   "Emerald Dusk" "white"
   "Gemini" "black"
   "Graphite" "white"
   "Graphite Aqua" "white"
   "Graphite Glass" "white"
   "Magellan" "black"
   "Mariner" "black"
   "Mist Aqua" "black"
   "Mist Silver" "black"
   "Moderate" "black"
   "Nebula" "black"
   "Nebula Brick Wall" "black"
   "Office Black 2007" "black"
   "Office Blue 2007" "gray"
   "Office Silver 2007" "black"
   "Raven" "black"
   "Sahara" "black"
   "Twilight" "black"})

;; Map skin to 'selected' icon prefix
;;
(def ^:private skin-selected-icon-prefix 
  {"Autumn" "white"
   "Business" "white"
   "Business Black Steel" "white"
   "Business Blue Steel" "white"
   "Cerulean" "white"
   "Challenger Deep" "gray"
   "Creme" "white"
   "Creme Coffee" "white"
   "Dust" "white"
   "Dust Coffee" "white"
   "Emerald Dusk" "black"
   "Gemini" "white"
   "Graphite" "black"
   "Graphite Aqua" "black"
   "Graphite Glass" "black"
   "Magellan" "white"
   "Mariner" "white"
   "Mist Aqua" "white"
   "Mist Silver" "white"
   "Moderate" "white"
   "Nebula" "white"
   "Nebula Brick Wall" "white"
   "Office Black 2007" "white"
   "Office Blue 2007" "white"
   "Office Silver 2007" "white"
   "Raven" "white"
   "Sahara" "white"
   "Twilight" "white"})

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



;; Return java.io.File for icon 
;; prefix - String, one of "black", "gray" or "white"
;;         if not specified derive from current skin
;; group - keyword, major icon grouping  :curve :filter :env ....
;; subgroup - keyword, specific icon within in group 
;;
(defn- get-icon-file 
  ([prefix group subgroup]
     (let [name (format "%s_%s%s.png"
                        prefix 
                        (name group)
                        (if subgroup (format "_%s" (name subgroup)) ""))
           pathname (path/join "resources" "icons" name)]
       (File. pathname)))
  ([group subgroup]
   (let [skin-name (config/current-skin)
         iprefix (get skin-icon-prefix skin-name "black")]
     (get-icon-file iprefix group subgroup nil))))

(defn read-channel-icon [chan]
  (let [fname (format "chan_%02d.png" chan)
        pathname (path/join "resources" "icons" fname)
        icn (seesaw.icon/icon (File. pathname))]
    icn))


;; Return 'normal' un-selected icon
;; prefix    - String, one of "black", "gray" or "white"
;; group    - keyword, major icon group 
;; subgroup - keyword, specific icon within group.
;;
(defn read-icon 
  ([prefix group subgroup]
     (let [f (get-icon-file prefix group subgroup)
           icn (seesaw.icon/icon f)]
       icn))
  ([group subgroup]
   (let [skin-name (config/current-skin)
         iprefix (get skin-icon-prefix skin-name "black")]
     (read-icon iprefix group subgroup))))


;; Return 'selected' icon
;; prefix    - String, one of "black", "gray" or "white"
;; group    - keyword, major icon group 
;; subgroup - keyword, specific icon within group.
;;
(defn read-selected-icon
  ([prefix group subgroup]
     (let [f (get-icon-file prefix group subgroup)
           icn (seesaw.icon/icon f)]
       icn))
  ([group subgroup]
   (let [skin-name (config/current-skin)
         iprefix (get skin-selected-icon-prefix skin-name "black")]
     (read-selected-icon iprefix group subgroup))))

;; disabled icon set currently does not exists
;; for the moment read-disabled-icons always returns a blank icon.
;;
(defn read-disabled-icon 
  ([prefix group subgroup]
   (let [f (get-icon-file prefix group subgroup)
         icn (seesaw.icon/icon f)]
     icn))
  ([group subgroup]
   (read-disabled-icon "black" "general" "blank")))
     


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
                   :center (lnf-selector-panel)))
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
        (umsg/message (format "--> Using '%s' skin"
                              skin-name))))
    (if (and skin-name (not skin))
      (umsg/warning (format "config initial-skin value '%s' is invalid" skin-name)))))
  
;; (defn skin-test []
;;   (let [grp (ss/button-group)
;;         fnt (ssf/font :style :bold :size 36)
;;         b1 (ss/toggle :text "Selected"
;;                       :group grp
;;                       :selected? true
;;                       :font fnt)
;;         b2 (ss/toggle :text "UnSelected"
;;                       :group grp
;;                       :font fnt)
;;         b3 (ss/toggle :text "Disabled"
;;                       :font fnt
;;                       :enabled? false)
;;         pan-south (ss/grid-panel :rows 1 :items [b1 b2 b3])
;;         f (ss/frame :title "Skin Test"
;;                     :content pan-south
;;                     :on-close :dispose)]
;;     (ss/pack! f)
;;     (ss/show! f)
;;     (skin-dialog)))
