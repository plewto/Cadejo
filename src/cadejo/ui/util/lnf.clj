(println "--> cadejo.ui.util.lnf")

(ns cadejo.ui.util.lnf
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
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
  {"Autumn" "black"
   "Business" "black"
   "Business Black Steel" "black"
   "Business Blue Steel" "black"
   "Cerulean" "black"
   "Challenger Deep" "white"
   "Creme" "black"
   "Creme Coffee" "black"
   "Dust" "black"
   "Dust Coffee" "black"
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
   "Office Black 2007" "gray"
   "Office Blue 2007" "black"
   "Office Silver 2007" "black"
   "Raven" "gray"
   "Sahara" "black"
   "Twilight" "black"})

;; Map skin to 'selected' icon prefix
;;
(def ^:private skin-selected-icon-prefix 
  {"Autumn" "gray"
   "Business" "gray"
   "Business Black Steel" "gray"
   "Business Blue Steel" "gray"
   "Cerulean" "gray"
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
   "Office Black 2007" "gray"
   "Office Blue 2007" "gray"
   "Office Silver 2007" "gray"
   "Raven" "white"
   "Sahara" "white"
   "Twilight" "white"})

(defn skin-name [i]
  (nth available-skins i))


(def color-schemes
  {"Autumn" 
   {:background (uc/color [255 227 197])
    :text-color (uc/color [172 98 59])
    :text-selected-color (uc/color [1 30 60])
    :dbar-active-color (uc/color [1 30 60])
    :dbar-style :matrix}
   "Business" 
   {:background (uc/color [216 221 225])
    :text-color (uc/color [14 19 24])
    :text-selected-color (uc/color [85 110 138])
    :dbar-active-color (uc/color :black)
    :dbar-style :matrix}
   "Business Black Steel"
   {:background (uc/color [241 246 250])
    :text-color (uc/color [14 19 24])
    :text-selected-color (uc/color [85 110 138])
    :slider-handle-color (uc/color :black)
    :dbar-active-color (uc/color :black)
    :dbar-style :basic}
 "Business Blue Steel"
   {:background (uc/color [226 238 248])
    :text-color (uc/color [21 26 31])
    :text-selected-color (uc/color [85 110 138])
    :slider-handle-color (uc/color :black)
    :dbar-active-color (uc/color :black)
    :dbar-style :basic}
   "Cerulean"
   {:background (uc/color [240 246 251])
    :text-color (uc/color [42  46  54])
    :text-selected-color (uc/color [141 104 124])
    :title-color (uc/color [42 46 54])
    :dbar-active-color (uc/color :black)
    :dbar-inactive-color (uc/color [227 229 229])
    :dbar-style :matrix
    :slider-handle-color (uc/color [42 46 54])
    :active-track-color (uc/color [42 46 54])
    :passive-track-color (uc/color [225 213 207])
    :major-tick-color (uc/color [42 46 54])
    :minor-tick-color (uc/color [42 46 54])
    :occluder-color (uc/color [240 246 251 250])
    :checkbox  {:rim-radius 18
               :rim-color (uc/color [42 46 54])
               :check-color (uc/color [42 46 54])
               :check-style [:dot :fill]
               :check-size 2}}
   "Challenger Deep"
   {:background (uc/color [32 20 67])
    :text-color (uc/color [249 249 249])
    :text-selected-color (uc/color [91 82 116])
    :slider-handle-color (uc/color [255 244 129])
    :active-track-color (uc/color [193 129 255])
    :passive-track-color (uc/color [94 38 130])
    :major-tick-color (uc/color [200 200 200])
    :minor-tick-color (uc/color [200 200 200])
    :dbar-style :sixteen
    :dbar-active-color (uc/color [193 129 255])
    :dbar-inactive-color (uc/color [94 38 130])
    :envelope-selected-line-color (uc/color [193 129 255])}
    "Creme"
    {:background (uc/color [233 233 223])
     :text-color (uc/color :black)
     :text-selected-color (uc/color [91 0 54])
     :slider-handle-color (uc/color :black)
     :active-track-color (uc/color [91 0 54])
     :passive-track-color (uc/color [163 163 156])
     :major-tick-color (uc/color [128 64 64])
     :minor-tick-color (uc/color [128 64 64])
     :dbar-style :matrix
     :dbar-active-color (uc/color :black)
     :dbar-inactive-color (uc/color [233 233 223]) 
     :envelope-line-color (uc/color [91 0 54])
     :envelope-selected-line-color (uc/color [ 0 75 130])} 
    "Dust"
    {:background (uc/color [234 231 226])
     :text-color (uc/color [59 54 57])
     :text-selected-color (uc/color [179 160 64])
     :dbar-style :basic
     :slider-handle-color (uc/color :black)
     :passive-track-color (uc/darker (uc/color [234 231 226]))
     :active-track-color (uc/color [59 54 57])}
    "Dust Coffee"
     {:background (uc/color [232 217 185])
      :text-color (uc/color [59 54 57])
      :text-selected-color (uc/brighter (uc/color [90 54 57]))
      :dbar-style :matrix
      :dbar-active-color (uc/color :black)
      :active-track-color (uc/color :black)
      :passive-track-color (uc/darker (uc/color 217 217 180))
      :slider-handle-color :black}
     "Emerald Dusk"
     {:background (uc/color [14 50 35])
      :text-color (uc/color :white)
      :text-selected-color (uc/color :green)
      :dbar-style :sixteen
      :dbar-active-color (uc/color [0 192 0])
      :dbar-passive-color (uc/color [130 130 130])
      :active-track-color (uc/color [192 192 0])
      :passive-track-color (uc/color [0 96 0])
      :slider-handle-color (uc/color [0 128 128])
      :minor-tick-color (uc/color :green)
      :major-tick-color (uc/color :green)
      :major-border-color (uc/color :green)
      :minor-border-color (uc/color :green)
      :envelope-line-color (uc/color [0 192 0])
      :envelope-selected-line-color (uc/color :white)
      :envelope-handle-color (uc/color :yellow)
      :checkbox {:rim-radius 18
                 :rim-color (uc/color :green)
                 :check-color (uc/color :yellow)
                 :check-style [:dot :fill]
                 :check-size 2}}
     "Gemini"
     {:background (uc/color [210 224 224])
      :text-color (uc/color [48 61 84])
      :text-selected-color (uc/color [56 173 131])
      :title-color (uc/color [48 61 84])
      :dbar-active-color (uc/color :black)
      :dbar-inactive-color (uc/color [210 224 224])
      :dbar-style :matrix
      :passive-track-color (uc/darker (uc/color [210 224 224]))
      :active-track-color (uc/color [48 61 84])
      :slider-handle-color (uc/color :black)
      :major-border-color (uc/color [48 61 84])
      :minor-border-color (uc/color [48 61 84])
      :minor-tick-color (uc/color [48 61 48])
      :major-tick-color (uc/color :black)
      :envelope-line-color (uc/color [27 84 63])
      :evelope-selected-line-color (uc/color [52 27 84])
      :envelope-handle-color (uc/color [171 3 66])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [48 61 84])
                 :check-color (uc/color [52 27 84])
                 :check-style [:dot :fill]
                 :check-size 2}}
     "Graphite"
     {:background (uc/color [77 77 77])
      :text-color (uc/color [180 180 180])
      :text-selected-color (uc/color [111 111 175])
      :title-color (uc/color [180 180 210])
      :major-border-color (uc/color :black)
      :minor-border-color (uc/color :black)
      :dbar-style :matrix
      :dbar-inactive-color (uc/color [77 77 77])
      :dbar-active-color (uc/color [180 180 200])
      :slider-handle-color (uc/color :black)
      :passive-track-color (uc/darker (uc/color [180 180 180]))
      :active-track-color (uc/color :black)
      :minor-tick-color (uc/color :black)
      :major-tick-color (uc/color :black)
      :envelope-selected-line-color (uc/color :yellow)
      :envelope-border-color (uc/color :black)}
     "Magellan"
     {:background (uc/color [12 90 176])
      :text-color (uc/color [143 193 249])
      :text-selected-color (uc/color [140 225 131])
      :title-color (uc/color [143 193 249])
      :major-border-color (uc/color [143 193 249])
      :minor-border-color (uc/color [143 193 249])
      :dbar-style :sixteen
      :dbar-inactive-color (uc/darker (uc/color [12 90 176]) 0.8)
      :dbar-active-color (uc/color [140 225 131])
      :slider-handle-color (uc/color :black)
      :passive-track-color (uc/darker (uc/color [12 90 176]))
      :active-track-color (uc/color [140 225 131])
      :minor-tick-color (uc/color [143 193 249])
      :major-tick-color (uc/brighter (uc/color [143 193 249]))
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [143 193 249])
                 :check-color (uc/color [140 225 131])
                 :check-style [:dot :fill]
                 :check-size 5}}
     "Mariner"
     {:background (uc/color [228 230 230])
      :text-color (uc/color :black)
      :text-selected-color (uc/color [129 40 0])
      :major-border-color (uc/color :black)
      :minor-border-color (uc/color [64 32 32])
      :dbar-style :basic
      :dbar-inactive-color (uc/color [228 230 230])
      :dbar-active-color (uc/brighter (uc/color [74 25 3]))
      :minor-tick-color (uc/color [64 0 0])
      :major-tick-color (uc/color :black)
      :slider-handle-color (uc/color :black)
      :active-track-color (uc/color [74 25 3])
      :passive-track-color (uc/darker (uc/color [228 230 230]))
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [74 25 3])
                 :check-color (uc/color [74 25 3])
                 :check-style [:bar :dash]
                 :check-size 3}}
     "Mist"
     {:background (uc/color [228 233 238])
      :text-color (uc/color [15 20 24])
      :text-selected-color (uc/color [128 30 128])
      :dbar-style :basic}
     "Moderate"
     {:background (uc/color [234 242 248])
      :text-color (uc/color [15 20 25])
      :text-selected-color (uc/color [106 168 206])
      :major-border-color (uc/color [15 20 25])
      :minor-border-color (uc/brighter (uc/color [15 20 25]))
      :minor-tick-color (uc/color [106 168 206])
      :major-tick-color (uc/darker (uc/color [106 168 206]))
      :dbar-style :basic
      :slider-handle-color (uc/color :black)
      :active-track-color (uc/color :black)
      :passive-track-color (uc/color [106 168 206])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [15 20 25])
                 :check-color (uc/color [15 20 25])
                 :check-style [:dot :fill]
                 :check-size 2}}
     "Nebula"
     {:background (uc/color [244 247 252])
      :text-color (uc/color [42 46 54])
      :text-selected-color (uc/color [130 89 151])
      :major-border-color (uc/color [42 46 54])
      :minor-border-color (uc/brighter (uc/color [42 46 54]))
      :minor-tick-color (uc/color [42 46 54])
      :major-tick-color (uc/darker (uc/color [130 89 151]))
      :dbar-style :matrix
      :dbar-inactive-color (uc/color [244 247 252])
      :dbar-active-color (uc/color [42 46 54])
      :slider-handle-color (uc/color :black)
      :active-track-color (uc/color :black)
      :passive-track-color (uc/color [130 89 151])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [15 20 25])
                 :check-color (uc/color [15 20 25])
                 :check-style [:dot :fill]
                 :check-size 2}}
     "Office Black 2007"
     {:background (uc/color [208 213 217])
      :text-color (uc/color [40 40 40])
      :text-selected-color (uc/color [86 2 55])
      :major-border-color (uc/color [86 2 55])
      :minor-border-color (uc/color [40 40 40])
      :dbar-style :matrix
      :dbar-active-color (uc/color [65 1 40])
      :dbar-inactive-color (uc/darker (uc/color [208 213 217]) 0.9)
      :slider-handle-color (uc/color [65 1 40])
      :active-track-color (uc/color [65 1 40])
      :passive-track-color (uc/darker (uc/color [208 213 217]))
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [40 40 40])
                 :check-color (uc/color [65 1 40])
                 :check-style [:dot]
                 :check-size 3}}

     "Office Blue 2007"
     {:background (uc/color [209 221 233])
      :text-color (uc/color [21 66 139])
      :text-selected-color (uc/darker (uc/color [254 182 89]))
      :major-border-color (uc/color [21 66 139])
      :minor-border-color (uc/darker (uc/color [254 182 89]))
      :title-color (uc/complement (uc/color [254 182 89]))
      :dbar-style :matrix
      :slider-handle-color (uc/color [21 66 139])
      :active-track-color (uc/color [21 66 139])
      :passive-track-color (uc/darker (uc/color [254 182 89]) 0.8)
      :envelope-border-color (uc/color [21 66 139])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [21 66 139])
                 :check-color (uc/color [21 63 139])
                 :check-style [:dot :fill]
                 :check-size 2}}

     "Office Silver 2007"
     {:background (uc/color [243 245 245])
      :text-color (uc/color [76 83 92])
      :text-selected-color (uc/darker (uc/color [254 182 89]))
      :title-color (uc/darker (uc/complement (uc/color [254 182 89])))
      :major-border-color (uc/color [76 83 92])
      :minor-border-color (uc/darker (uc/color [254 182 89]))
      :dbar-style :sixteen
      :dbar-active-color (uc/color [76 83 92])
      :dbra-inactive-color (uc/darker (uc/color [243 245 245]) 0.8)
      :slider-handle-color (uc/color [76 83 92])
      :active-track-color (uc/color [76 83 92])
      :passive-track-color (uc/darker (uc/color [254 182 89]) 0.8)
      :envelope-border-color (uc/color [76 83 92])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [76 83 92])
                 :check-color (uc/color [21 63 139])
                 :check-style [:dot :fill]
                 :check-size 2}} 
     "Raven"
      {:background (uc/color [29 25 22])
      :text-color (uc/color [217 229 250])
      :text-selected-color (uc/color [198 201 206])
      :title-color (uc/darker (uc/complement (uc/color [198 201 206])))
      :major-border-color (uc/color [217 229 250])
      :minor-border-color (uc/darker (uc/color [198 201 206]))
      :minor-tick-color (uc/color [93 16 140])
      :major-tick-color (uc/color [140 16 113])
      :dbar-style :sixteen
      :dbar-active-color (uc/color [217 229 250])
      :dbra-inactive-color (uc/darker (uc/color [78 77 75]) 0.7)
      :slider-handle-color (uc/color [217 229 250])
      :active-track-color (uc/color [93 16 140])
      :passive-track-color (uc/color [31 4 47])
      :envelope-border-color (uc/color [217 229 250])
      :checkbox {:rim-radius 18
                 :rim-color (uc/color [217 229 250])
                 :check-color( uc/complement (uc/color [217 229 250]))
                 :check-style [:dot :fill]
                 :check-size 3}} 
      "Sahara"
      {:background (uc/color [239 243 247])
       :text-color (uc/color [15 20 25])
       :text-selected-color (uc/color [168 189 94])
       :title-color (uc/color [21 35 34])
       :minor-border-color (uc/color [21 35 34])
       :major-border-color (uc/color [15 20 25])
       :minor-tick-color (uc/color [21 35 34])
       :major-tick-color (uc/color [15 20 25])
       :dbar-style :basic
       :passive-track-color (uc/darker (uc/color [231 233 236]) 0.8)
       :active-track-color (uc/color [21 35 34])
       :slider-handle-color (uc/color [21 35 34])
       :envelope-border-color (uc/color [15 20 25])}
       "Twilight"
       {:background (uc/color :black) ;  [76 74 65])
        :text-color (uc/color [185 180 158])
        :selected-text-color (uc/color [138 129 95])
        :title-color (uc/color [138 129 95])
        :minor-border-color (uc/color [84 81 75])
        :major-border-color (uc/color [59 57 53])
        :minor-tick-color (uc/color [84 81 75])
        :major-tick-color (uc/color [59 57 53])
        :dbar-style :matrix
        :dbar-inactive-color (uc/color [77 58 83])
        :dbar-active-color (uc/color [245 244 207])
        :envelope-background (uc/color :black)
        :envelope-border-color (uc/color [84 81 75])
        :envelope-line-color (uc/color [77 58 83])
        :envelope-selected-line-color (uc/color [245 244 207])
        :envelope-handle-color (uc/color [76 74 65])
        :slider-handle-color (uc/color [138 129 95])
        :passive-track-color (uc/color [84 81 75])
        :active-track-color (uc/brighter (uc/color [77 58 83]))
        :checkbox {:rim-radius 18
                   :rim-color (uc/color [185 180 158])
                   :check-color (uc/brighter (uc/color [77 58 83]))
                   :check-style [:dot :fill]
                   :check-size 2}} })

(defn- skin-substitution [skin-name]
  (get {"Creme Coffee" "Creme"
        "Graphite Aqua" "Graphite"
        "Graphite Glass" "Graphite"
        "Mist Aqua" "Mist"
        "Mist Silver" "Mist"
        "Nebula Brick Wall" "Nebula"}
       skin-name
       skin-name))

(defn property 
  ([skin-name usage default]
   (let [alias (skin-substitution skin-name)
         scm (get color-schemes alias)]
     (get scm usage default)))
  ([skin-name usage]
   (property skin-name usage (uc/color :gray))))


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


(defn icon-prefix [] (get skin-icon-prefix (config/current-skin) :gray))



(defn- lnf-property [key default]
  (fn [](property (config/current-skin) key (apply default nil))))


(defn background [](property (config/current-skin) :background (uc/color [191 191 191])))
(defn text-color [](property (config/current-skin) :text-color (uc/color :black)))
(defn text-selected-color [](property (config/current-skin) :text-selected-color (uc/color :green)))



(def title-color            (lnf-property :title-color text-selected-color))
(def dbar-inactive-color    (lnf-property :dbar-inactive-color background))
(def dbar-active-color      (lnf-property :dbar-active-color text-color))
;(def dbar-style             (lnf-property :dbar-style (constantly :matrix)))
(def major-border-color     (lnf-property :major-border-color text-selected-color))
(def minor-border-color     (lnf-property :minor-border-color text-color))
(def button-border-color    (lnf-property :button-border-color text-color))
(def button-selected-border (lnf-property :button-selected-border text-selected-color))
(def passive-track-color    (lnf-property :passive-track-color text-color))
(def active-track-color     (lnf-property :active-track-color text-selected-color))
(def alternate-track-color  (lnf-property :alternate-track-color text-selected-color))
(def slider-handle-color    (lnf-property :slider-handle-color text-selected-color))
(def major-tick-color       (lnf-property :major-tick-color text-selected-color))
(def minor-tick-color       (lnf-property :minor-tick-color text-color))
(def envelope-background    (lnf-property :envelope-background background))
(def envelope-border-color  (lnf-property :envelope-border-color text-selected-color))
(def envelope-line-color    (lnf-property :envelope-line-color text-color))
(def envelope-selected-line-color (lnf-property :envelope-selected-line-color text-selected-color))
(def envelope-handle-color  (lnf-property :envelope-handle-color text-selected-color))
(def occluder-color         (lnf-property :occluder-color (fn [](uc/transparent (background) 200))))

;; Returns sgwr displaybar style.
;; Values set by config.clj have priority. if config/displaybar-value returns nil
;; extract style from lnf properties map, if that fails use :basic
;;
(defn dbar-style [] (let [cf (config/displaybar-style)]
                      (or cf (property (config/current-skin) :dbar-style :basic))))

;; Returns map for sgwr checkbox attributes using current skin
;; {:rim-radius r
;;  :rim-color  rc
;;  :check-color cc
;;  :check-style [vector] ; see sgwr point styles
;;  :check-size cs}
;;
(defn checkbox [](property (config/current-skin) :checkbox
                           {:rim-radius 18
                            :rim-color (text-color)
                            :check-color (text-selected-color)
                            :check-style [:dot :fill]
                            :check-size 3}))
                            




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


