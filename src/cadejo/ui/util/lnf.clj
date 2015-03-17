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
(declare icon-selected-prefix)
(def ^:private skins (SubstanceLookAndFeel/getAllSkins))
(def available-skins (keys skins))

(defn skin-name [i]
  (nth available-skins i))



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
         iprefix (icon-selected-prefix)]
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
          
;; Skin colors are defined hierarchically from the general to the
;; specific. If a specific color is not defined then a more general color
;; is used as a default.  The bare minimum is for each skin to define three
;; general colors, :dark, :light and :medium.
;;
;; General colors
;;   :ultra-dark       default :extra-dark
;;   :extra-dark       default :dark
;;   :dark             default black
;;   :medium           default :light
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
;;   :minor-border            default :major-border   * minor border color                                  
;;   :major-border            default :text           * major border color                                  
;;   :passive-track           default :medium         * slider background track                                  
;;   :active-track            default :ultra-light    * slider active track                                  
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


(defn create-skin-map [name parent & {:keys [icon-prefix icon-selected-prefix
                                             ultra-dark extra-dark dark
                                             medium
                                             light extra-light ultra-light
                                             background text selected-text
                                             label title minor-border major-border
                                             handle handle-style handle-size
                                             passive-track active-track minor-tick major-tick
                                             button-border button-selected-border 
                                             checkbox checkbox-rim checkbox-style checkbox-size checkbox-rim-radius
                                             env-background env-border env-segment env-selected env-handle
                                             dbar-inactive dbar-active dbar-style
                                             occluder]
                                      :or {icon-prefix nil
                                           icon-selected-prefix nil
                                           ultra-dark :extra-dark
                                           extra-dark :dark
                                           dark nil
                                           medium :light
                                           light nil
                                           extra-light :light
                                           ultra-light :extra-light
                                           background :extra-dark
                                           text :light
                                           selected-text :ultra-light
                                           label :text
                                           title :text
                                           minor-border :major-border
                                           major-border :text
                                           handle :active-track
                                           handle-style nil
                                           handle-size nil
                                           passive-track :text
                                           active-track :ultra-light
                                           minor-tick :passive-track
                                           major-tick :minor-tick
                                           button-border :minor-border
                                           button-selected-border :selected-text
                                           checkbox :selected-text
                                           checkbox-rim :button-border
                                           checkbox-style nil
                                           checkbox-size nil
                                           checkbox-rim-radius nil
                                           env-background :background
                                           env-border :minor-border
                                           env-segment :text
                                           env-selected :selected-text
                                           env-handle :handle
                                           dbar-inactive :background
                                           dbar-active :selected-text
                                           dbar-style nil
                                           occluder :background}}]
  {:skin name
   :parent parent
   :icon-prefix icon-prefix
   :icon-selected-prefix icon-selected-prefix
   :ultra-dark ultra-dark
   :extra-dark extra-dark
   :dark dark
   :medium medium
   :light light
   :extra-light extra-light
   :ultra-light ultra-light
   :background background
   :text text
   :selected-text selected-text
   :label label
   :title title
   :minor-border minor-border
   :major-border major-border
   :handle handle
   :handle-style handle-style
   :handle-size handle-size
   :passive-track passive-track
   :active-track active-track
   :minor-tick minor-tick
   :major-tick major-tick
   :button-border button-border
   :button-selected-border button-selected-border
   :checkbox checkbox
   :checkbox-rim checkbox-rim
   :checkbox-style checkbox-style
   :checkbox-size checkbox-size
   :checkbox-rim-radius checkbox-rim-radius
   :env-background env-background
   :env-border env-border
   :env-segment env-segment
   :env-selected env-selected
   :env-handle env-handle
   :dbar-inactive dbar-inactive
   :dbar-active dbar-active
   :dbar-style dbar-style
   :occluder occluder})
                                      
(def default-skin-map (create-skin-map "Default" nil
                                       :icon-prefix "black"
                                       :icon-selected-prefix "gray"
                                       :dark (uc/color :black)
                                       :medium (uc/color :gray)
                                       :light (uc/color :white)
                                       :handle-style [:dot :fill]
                                       :handle-size 4
                                       :checkbox-style [:dot :fill]
                                       :checkbox-size 3
                                       :checkbox-rim-radius 18
                                       :dbar-style :basic))

(def autumn-map (create-skin-map "Autumn" 
                                 default-skin-map
                                 :ultra-dark     (uc/color [ 68  40  24])
                                 :extra-dark     (uc/color [178 106  64])
                                 :dark           (uc/color [252 191 122])
                                 :light          (uc/color [255 227 197])
                                 :ultra-light    (uc/color [255 245 234])
                                 ))

(def graphite-map (create-skin-map "Graphite"
                                   default-skin-map
                                   :icon-prefix "white"
                                   :icon-selected-prefix "black"
                                   :ultra-dark  (uc/color :black)
                                   :extra-dark  (uc/color [ 50  50  50])
                                   :dark        (uc/color [ 66  66  66])
                                   :medium      (uc/color [123 123 123])
                                   :light       (uc/color [180 180 180])
                                   :extra-light (uc/color [245 247 251])
                                   :ultra-light (uc/color [201 179 208])
                                   :major-tick  (uc/color [180 180 180])
                                   :dbar-style :matrix
                                   ))

(def graphite-aqua-map (create-skin-map "Graphite Aqua"
                                        graphite-map
                                        :ultra-light  (uc/color [ 80 114 237])
                                        :background   (uc/color [ 87  87  87])
                                        :dbar-style   :sixteen
                                        ))

(def schemes 
  {"Default" default-skin-map
   "Autumn" autumn-map
   "Graphite" graphite-map
   "Graphite Aqua" graphite-aqua-map
   })

                                           
(defn skin-color
  ([skin-map key]
   (let [val (get skin-map key)]
     (cond (keyword? val)(skin-color skin-map val)
           val val
           :default (skin-color (:parent skin-map) key))))
  ([key]
   (skin-color (get schemes (config/current-skin)) key)))

;; DON NOT USE to extract color from skin-map!
(defn skin-value 
  ([skin-map key]
   (let [val (get skin-map key)]
     (or val (skin-value (:parent skin-map) key))))
  ([key]
   (skin-value (get schemes (config/current-skin)) key)))


;; (defn- def-lnf-color [key]
;;   (fn [](skin-color key)))

(defn lnf-color [key]
  (fn [](skin-color key)))
          
(def dark (lnf-color                    :dark))
(def extra-dark (lnf-color              :extra-dark))
(def ultra-dark (lnf-color              :ultra-dark))
(def light (lnf-color                   :light))
(def extra-light (lnf-color             :extra-light))
(def ultra-light (lnf-color             :ultra-light))
(def medium (lnf-color                  :medium))
(def background (lnf-color              :background))
(def occluder (lnf-color                :occluder))
(def text (lnf-color                    :text))
(def selected-text (lnf-color           :selected-text))
(def label (lnf-color                   :label))
(def title (lnf-color                   :title))
(def minor-border (lnf-color            :minor-border))
(def major-border (lnf-color            :major-border))
(def passive-track (lnf-color           :passive-track))
(def active-track (lnf-color            :active-track))
(def minor-tick (lnf-color              :minor-tick))
(def major-tick (lnf-color              :major-tick))
(def handle (lnf-color                  :handle))
(def button-border (lnf-color           :button-border))
(def button-selected-border (lnf-color  :button-selected-border))
(def checkbox (lnf-color                :checkbox))
(def checkbox-rim-radius (lnf-color     :checkbox-rim-radius))
(def checkbox-rim (lnf-color            :checkbox-rim))
(def checkbox-style (lnf-color          :checkbox-style))
(def checkbox-size (lnf-color           :checkbox-size))
(def envelope-background (lnf-color     :env-background))
(def envelope-border (lnf-color         :env-border))
(def envelope-segment (lnf-color        :env-segment))
(def envelope-selected (lnf-color       :env-selected))
(def envelope-handle (lnf-color         :env-handle))
(def dbar-inactive (lnf-color           :dbar-inactive))
(def dbar-active (lnf-color             :dbar-active))
(def occluder (lnf-color                :occluder))
           
(defn selected-button-border []
  (umsg/warning "lnf/selected-button-border is DEPRECIATED, use button-selected-border")
  (button-selected-border))

(defn lnf-value [key]
  (fn [](skin-value key)))

(def icon-prefix (lnf-value              :icon-prefix))
(def icon-selected-prefix (lnf-value     :icon-selected-prefix))
(def checkbox-rim-radius (lnf-value      :checkbox-rim-radius))
(def checkbox-style (lnf-value           :checkbox-style))
(def checkbox-size (lnf-value            :checkbox-size))
(def dbar-style  (lnf-value  :dbar-style))
(def handle-style (lnf-value             :handle-style))
(def handle-size (lnf-value              :handle-size))


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
