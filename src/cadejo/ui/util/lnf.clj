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

(declare icon-prefix)
(declare selected-icon-prefix)
(def ^:private skins (SubstanceLookAndFeel/getAllSkins))
(def available-skins (keys skins))

(defn skin-name [i]
  (nth available-skins i))

;; Skin colors are defined hierarchically from the general to the
;; specific. If a specific color is not defined then a more general color
;; is used as a default.  The bare minimum is for each skin to define three
;; general colors, :dark, :light and :medium.
;;
;; General colors
;;   :ultra-dark       default :extra-dark
;;   :extra-dark       default :dark
;;   :dark             default black
;;   :medium           default gray
;;   :light            default white
;;   :extra-light      default :light
;;   :ultra-light      default :extra-light
;;
;; Specific component colors
;;   :background              default :extra-dark     * drawing background
;;   :text                    default :light          * text color              
;;   :selected-text           default :ultra-light    * highlighted text color

;;   :label                   default :text           * general label color     
;;   :title                   default :text           * primary title color                                  
;;   :minor-border            default :text           * minor border color                                  
;;   :major-border            default :selected-text  * major border color                                  
;;   :passive-track           default :medium         * slider background track                                  
;;   :active-track            default :selected-text  * slider active track                                  
;;   :minor-tick              default :passive-track  * slider minor tick color                                
;;   :major-tick              default :minor-tick     * slider major tick color                             
;;   :handle                  default :active-track   * slider handle color                               
;;   :button-border           default :minor-border   * button border color                               
;;   :selected-button-border  default :selected-text  * highlighted button border                                
;;   :checkbox                default :selected-text  * highlighted checkbox color                                 
;;   :checkbox-rim            default :button-border  * normal checkbox color                                
;;   :envelope-background     default :background     *                               
;;   :envelope-border         default :minor-border   *                                
;;   :envelope-segment        default :text           *                        
;;   :envelope-selected       default :selected-text  *                                 
;;   :envelope-handle         default :handle         *                          
;;   :dbar-inactive           default :background     *                              
;;   :dbar-active             default :text           *                                 
;;
;; Additional properties
;;   :icon-prefix             Should be one of "black", "gray" or "white"
;;   :selected-icon-prefix    Should be "black", "gray" or "white" and different from icon-prefix
;;   :checkbox-style          vector defined point style for selected checkboxes, see sgwr checkbox
;;   :checkbox-size           int defines selected checkbox component size in pixels
;;   :checkbox-rim-radius     int sets roundness for checkboxes, if radius = 0, checkboxes are square
;;   :handle-style            default [:dot :fill]
;;   :handle-size             default 4
;;   :occluder                color used to indicate disabled components which should
;;                            have some transparency. 
;;   :dbar-style              Display bar style, one of :matrix, :sixteen or :basic

(def color-schemes
  {"Autumn"
   {:skin "Autumn"
    :icon-prefix "black"
    :selected-icon-prefix "gray"
    :ultra-dark   (uc/color [ 68  40  24])
    :extra-dark   (uc/color [178 106  64])
    :dark         (uc/color [252 191 122])
    :medium       (uc/color [255 227 197])
    :light        (uc/color [255 227 197])
    :extra-light  nil
    :ultra-light  (uc/color [255 245 234])

    :background    (uc/color [255 227 197])
    :text          (uc/color [178 106  64])
    :selected-text (uc/color [255 245 197])
    :passive-track (uc/color [178 106  64])
    :active-track  (uc/color [ 68  40  24])
    ;; :minor-border (uc/color [ 68  40  24])
    ;; :major-border (uc/color [ 68  40  24])
    ;; :active-track (uc/color [  0   0   0])
    ;; :env-border   (uc/color [ 68  40  24])
    ;; :env-segment  (uc/color [ 68  40  24])
    ;; :env-handle   (uc/color [  0   0  0])
    :dbar-style :matrix
    } ;; End Autumn
   
    "Business"
    {:skin "Business"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Business
    
    "Business Black Steel"
    {:skin "Business Black Steel"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Business Black Steel
    
    "Business Blue Steel"
    {:skin "Business Blue Steel"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Business Blue Steel
    
    "Cerulean"
    {:skin "Cerulean"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Cerulean
    
    "Challenger Deep"
    {:skin "Challenger Deep"
     :icon-prefix "white"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Challenger Deep
    
    "Creme"
    {:skin "Creme"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Creme
    
    "Creme Coffee"
    {:skin "Creme Coffee"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Creme Coffee
    
    "Dust"
    {:skin "Dust"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Dust
    
    "Dust Coffee"
    {:skin "Dust Coffee"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Dust Coffee
    
    "Emerald Dusk"
    {:skin "Emerald Dusk"
     :icon-prefix "white"
     :selected-icon-prefix "black"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Emerald Dusk
    
    "Gemini"
    {:skin "Gemini"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Gemini
    
    "Graphite"
    {:skin "Graphite"
     :icon-prefix "white"
     :selected-icon-prefix "black"
     :ultra-dark (uc/color :black)
     :extra-dark (uc/color [ 50  50  50])
     :dark       (uc/color [ 66  66  66])
     :medium     (uc/color [123 123 123])
     :light      (uc/color [180 180 180])
     :extra-light (uc/color [245 247 251])
     :ultra-light (uc/color :white)
     :dbar-style :matrix
     } ;; End Graphite
    
    "Graphite Aqua"
    {:skin "Graphite Aqua"
     :icon-prefix "white"
     :selected-icon-prefix "black"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Graphite Aqua
    
    "Graphite Glass"
    {:skin "Graphite Glass"
     :icon-prefix "white"
     :selected-icon-prefix "black"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Graphite Glass
    
    "Magellan"
    {:skin "Magellan"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Magellan
    
    "Mariner"
    {:skin "Mariner"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Mariner
    
    "Mist Aqua"
    {:skin "Mist Aqua"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Mist Aqua
    
    "Mist Silver"
    {:skin "Mist Silver"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Mist Silver
    
    "Moderate"
    {:skin "Moderate"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Moderate
    
    "Nebula"
    {:skin "Nebula"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Nebula
    
    "Nebula Brick Wall"
    {:skin "Nebula Brick Wall"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Nebula Brick Wall
    
    "Office Black 2007"
    {:skin "Office Black 2007"
     :icon-prefix "gray"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Office Black 2007
    
    "Office Blue 2007"
    {:skin "Office Blue 2007"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Office Blue 2007
    
    "Office Silver 2007"
    {:skin "Office Silver 2007"
     :icon-prefix "black"
     :selected-icon-prefix "gray"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Office Silver 2007
    
    "Raven"
    {:skin "Raven"
     :icon-prefix "gray"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Raven
    
    "Sahara"
    {:skin "Sahara"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Sahara
    
    "Twilight"
    {:skin "Twilight"
     :icon-prefix "black"
     :selected-icon-prefix "white"
     :ultra-dark nil
     :extra-dark nil
     :dark nil
     :medium nil
     :light nil
     :extra-light nil
     :ultra-light nil
     } ;; End Twilight
    })

(defn- skin-substitution [skin-name]
  (get {"Creme Coffee" "Creme"
        "Graphite Aqua" "Graphite"
        "Graphite Glass" "Graphite"
        "Mist Aqua" "Mist"
        "Mist Silver" "Mist"
        "Nebula Brick Wall" "Nebula"}
       skin-name
       skin-name))

(defn property [skin-name usage default]
  (let [alias (skin-substitution skin-name)
        scm (get color-schemes alias)
        val (usage scm)]
    (or val default)))


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
         iprefix (icon-prefix)]
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
         iprefix (icon-prefix)]
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
         iprefix (selected-icon-prefix)]
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
     


;; Return skin-selection panel
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

(defn- lnf-property [key default]
  (fn []
    (property (config/current-skin) key (default))))

(def icon-prefix            (lnf-property :icon-prefix (constantly "black")))
(def selected-icon-prefix   (lnf-property :selected-icon-prefix (constantly "white")))
(def dark                   (lnf-property :dark (constantly (uc/color :black))))
(def extra-dark             (lnf-property :extra-dark dark))
(def ultra-dark             (lnf-property :ultra-dark extra-dark))
(def light                  (lnf-property :light (constantly (uc/color :white))))
(def extra-light            (lnf-property :extra-light light))
(def ultra-light            (lnf-property :ultra-light extra-light))
(def medium                 (lnf-property :medium (constantly (uc/color :gray))))
(def background             (lnf-property :background extra-dark))
(def text                   (lnf-property :text light))
(def selected-text          (lnf-property :selectred-text ultra-light))
(def label                  (lnf-property :label text))
(def title                  (lnf-property :title text))
(def minor-border           (lnf-property :minor-border text))
(def major-border           (lnf-property :major-border selected-text))
(def passive-track          (lnf-property :passive-track medium))
(def active-track           (lnf-property :active-track selected-text))
(def minor-tick             (lnf-property :minor-tick passive-track))
(def major-tick             (lnf-property :major-tick minor-tick))
(def handle                 (lnf-property :handle active-track))
(def button-border          (lnf-property :button-border minor-border))
(def selected-button-border (lnf-property :selected-button-border selected-text))
(def checkbox               (lnf-property :checkbox selected-text))
(def checkbox-rim-radius    (lnf-property :cb-rim-radius (constantly 18)))
(def checkbox-rim           (lnf-property :cb-rim button-border))
(def checkbox-style         (lnf-property :cb-style (constantly [:dot :fill])))
(def checkbox-size          (lnf-property :cb-size  (constantly 3)))
(def envelope-background    (lnf-property :env-background background))
(def envelope-border        (lnf-property :env-border minor-border))
(def envelope-segment       (lnf-property :env-segment text))
(def envelope-selected      (lnf-property :env-selected selected-text))
(def envelope-handle        (lnf-property :env-handle handle))
(def dbar-inactive          (lnf-property :dbar-inactive background))
(def dbar-active            (lnf-property :dbar-active text))
(def occluder               (lnf-property :occluder (fn [](uc/transparent (background) 200))))
(def handle-style           (lnf-property :handle-style (constantly [:dot :fill])))
(def handle-size            (lnf-property :handle-size (constantly 4)))

;; Returns sgwr displaybar style.
;; Values set by config.clj have priority. if config/displaybar-value returns nil
;; extract style from lnf properties map, if that fails use :basic
;;
(defn dbar-style [] (let [cf (config/displaybar-style)]
                      (or cf (property (config/current-skin) :dbar-style :basic))))




;; ***********************************
;; *** START DEPRECIATED FUNCTIONS ***
;; ***********************************
(defn- depreciated-lnf-property [key default]
  (fn []
    (println (format "DEPRECIATION WARNING  depreciated-lnf-property function executed  key = %s" key))
    (property (config/current-skin) key (apply default nil))))

(defn temp [] (uc/color :gray))
(def title-color            (depreciated-lnf-property :title-color temp))
(def dbar-inactive-color    (depreciated-lnf-property :dbar-inactive-color temp))
(def dbar-active-color      (depreciated-lnf-property :dbar-active-color temp))
(def major-border-color     (depreciated-lnf-property :major-border-color temp))
(def minor-border-color     (depreciated-lnf-property :minor-border-color temp))
(def button-border-color    (depreciated-lnf-property :button-border-color temp))
(def button-selected-border (depreciated-lnf-property :button-selected-border temp))
(def passive-track-color    (depreciated-lnf-property :passive-track-color temp))
(def active-track-color     (depreciated-lnf-property :active-track-color temp))
(def alternate-track-color  (depreciated-lnf-property :alternate-track-color temp))
(def slider-handle-color    (depreciated-lnf-property :slider-handle-color temp))
(def major-tick-color       (depreciated-lnf-property :major-tick-color temp))
(def minor-tick-color       (depreciated-lnf-property :minor-tick-color temp))
(def envelope-background    (depreciated-lnf-property :envelope-background temp))
(def envelope-border-color  (depreciated-lnf-property :envelope-border-color temp))
(def envelope-line-color    (depreciated-lnf-property :envelope-line-color temp))
(def envelope-selected-line-color (depreciated-lnf-property :envelope-selected-line-color temp))
(def envelope-handle-color  (depreciated-lnf-property :envelope-handle-color temp))
(def occluder-color         (depreciated-lnf-property :occluder-color temp))
;; *********************************
;; *** END DEPRECIATED FUNCTIONS ***
;; *********************************


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
