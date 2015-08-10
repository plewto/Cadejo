(println "--> cadejo.ui.util.lnf")
(ns cadejo.ui.util.lnf
  (:require [cadejo.config :as config])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.util.color :as uc])
  (:import org.pushingpixels.substance.api.SubstanceLookAndFeel
           org.pushingpixels.substance.api.UiThreadingViolationException
           java.io.File))

(println "-->    seesaw.core ...")
(require '[seesaw.core :as ss])
(require '[seesaw.color :as ssc])
(require '[seesaw.icon])
(require '[seesaw.swingx :as swingx])

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
                  @acc*)
        sampler-icon (ss/label :icon (seesaw.icon/icon (File. "resources/skin_sampler.png")))
        pan-sampler (ss/vertical-panel :items [(ss/scrollable sampler-icon)])
        pan-selection (ss/grid-panel :rows 6 :columns 5 :items buttons)
        jb-sampler (ss/button :text "Samples")
        jb-selection (ss/button :text "Select Skin")
        pan-north (ss/horizontal-panel :items [jb-selection jb-sampler])
        pan-cards (ss/card-panel :items [[pan-selection :selection][pan-sampler :sampler]])
        pan-main (ss/border-panel :north pan-north
                                  :center pan-cards)]
    (ss/listen jb-sampler :action (fn [& _]
                                    (ss/show-card! pan-cards :sampler)))
    (ss/listen jb-selection :action (fn [& _]
                                      (ss/show-card! pan-cards :selection)))
    pan-main))
    
                                        
;; Pop up skin selection dialog.
;;                  
(defn skin-dialog [& _]
  (let [pan-main 
        (swingx/titled-panel
         :title "Substance Skins"
         :content (ss/border-panel 
                   :center (lnf-selector-panel)))
        jb-dismiss (ss/button :text "Dismiss")
        dia (ss/dialog :title "Substance Skins"
                       :content pan-main
                       :on-close :dispose
                       :size [1000 :by 500]
                       :options [jb-dismiss])]
    (ss/listen jb-dismiss :action (fn [_](ss/return-from-dialog dia true)))
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
;;
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


(defn- create-skin-map [name parent & {:keys [icon-prefix icon-selected-prefix
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

(defn- create-sparse-skin-map [name parent & {:keys [icon-prefix icon-selected-prefix
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
                                                  ultra-dark nil
                                                  extra-dark nil
                                                  dark nil
                                                  medium nil
                                                  light nil
                                                  extra-light nil
                                                  ultra-light nil
                                                  background nil
                                                  text nil
                                                  selected-text nil
                                                  label nil
                                                  title nil
                                                  minor-border nil
                                                  major-border nil
                                                  handle nil
                                                  handle-style nil
                                                  handle-size nil
                                                  passive-track nil
                                                  active-track nil
                                                  minor-tick nil
                                                  major-tick nil
                                                  button-border nil
                                                  button-selected-border nil
                                                  checkbox nil
                                                  checkbox-rim nil
                                                  checkbox-style nil
                                                  checkbox-size nil
                                                  checkbox-rim-radius nil
                                                  env-background nil
                                                  env-border nil
                                                  env-segment nil
                                                  env-selected nil
                                                  env-handle nil
                                                  dbar-inactive nil
                                                  dbar-active nil
                                                  dbar-style nil
                                                  occluder nil}}]
  (create-skin-map name parent
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
   :occluder occluder))
                                      
(def ^:private default-skin-map (create-skin-map "Default" nil
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

(def ^:private autumn-map (create-skin-map "Autumn" 
                                           default-skin-map
                                           :ultra-dark     (uc/color [103  64  38])
                                           :extra-dark     (uc/color [186 115  70])
                                           :dark           (uc/color [236 178 138])
                                           :light          (uc/color [254 236 214])
                                           :ultra-light    (uc/color :white)
                                           :background     (uc/color [255 227 197])
                                           :text           :extra-dark
                                           :checkbox       :ultra-dark
                                           :title          :ultra-dark
                                           :active-track   :ultra-dark
                                           :minor-tick     :dark
                                           :major-tick     :extra-dark
                                           :dbar-style     :matrix
                                           :dbar-active    :ultra-dark))

(def ^:private business-map (create-skin-map "Business"
                                             default-skin-map
                                             :ultra-dark     (uc/color :black)
                                             :extra-dark     (uc/color [ 69  74  78])
                                             :dark           (uc/color [ 00 130 191])
                                             :medium         (uc/color [176 180 184])
                                             :light          (uc/color [255 255 255])
                                             :extra-light    nil
                                             :ultra-light    (uc/color :white)
                                             :background     (uc/color [204 208 211])
                                             :text           :extra-dark
                                             :checkbox       :extra-dark
                                             :selected-text  :medium
                                             :active-track   :extra-dark
                                             :minor-tick     :text
                                             :env-border     :extra-dark
                                             :dbar-active    :ultra-dark
                                             :dbar-style     :basic))


(def ^:private business-black (create-sparse-skin-map "Business Black Steel"
                                                      business-map
                                                      :medium     (uc/color [ 44  65  81])
                                                      :background (uc/color [241 246 250])
                                                      :dbar-style :matrix
                                                      :dbar-inactive :background
                                                      :dbar-active (uc/color :black)
                                                      :active-track :medium
                                                      :checkbox :medium
                                                      ))

(def ^:private business-blue (create-sparse-skin-map "Business Blue Steel"
                                                     business-map
                                                     :dark      (uc/color  [ 44  65  81])
                                                     :medium    (uc/color  [149 180 200])
                                                     :background (uc/color [232 237 241])
                                                     :passive-track :medium
                                                     :active-track :text
                                                     :handle :text
                                                     :env-handle :text
                                                     :dbar-style :sixteen
                                                     :dbar-inactive :background
                                                     :dbar-active (uc/color :black)
                                                     :checkbox :dark))

(def ^:private cerulean-map (create-skin-map "Cerulean"
                                             default-skin-map
                                             :ultra-dark    (uc/color :black)
                                             :dark          (uc/color [42 46 54])
                                             :medium        (uc/color [194 219 225])
                                             :light         (uc/color [240 240 240])
                                             :extra-light   (uc/color [251 252 252])
                                             :background    :extra-light
                                             :text          :dark
                                             :selected-text (uc/color [ 68 151 221])
                                             :passive-track (uc/color [179 218 218])
                                             :active-track  :dark
                                             :minor-tick    :dark
                                             :dbar-style    :matrix
                                             :dbar-inactive (uc/color [235 235 235])
                                             :dbar-active   :ultra-dark))

(def ^:private challenger-deep-map (create-skin-map "Challenger Deep"
                                                    default-skin-map
                                                    :icon-prefix    "gray"
                                                    :icon-selected-index "white"
                                                    :ultra-dark     (uc/color [ 15  19  32])
                                                    :extra-dark     (uc/color [ 34  27  51])
                                                    :dark           (uc/color [ 55  19  63])
                                                    :medium         (uc/color [119 112 225])
                                                    :light          (uc/color [249 249 249])
                                                    :ultra-light    (uc/color [225 112 128])
                                                    :minor-border   (uc/color [ 84  82 111])
                                                    :button-border  :medium
                                                    :passive-track  :medium
                                                    :active-track   :dbar-active
                                                    :handle         (uc/color [250 232 75])
                                                    :handle-style   [:dot]
                                                    :env-segment    :medium
                                                    :dbar-inactive  (uc/color [  0  0 174])
                                                    :dbar-active    (uc/color [218 75 249])
                                                    :dbar-style     :sixteen
                                                    :checkbox       (uc/color [234 231 242])
                                                    :occluder       (uc/color [64 32 128 128])))

(def ^:private creme-map (create-skin-map "Creme"
                                          default-skin-map
                                          :icon-selected-prefix  "white"
                                          :ultra-dark    (uc/color :black)
                                          :extra-dark    (uc/color [ 55  99 181])
                                          :medium        (uc/color [179 182 176])
                                          :light         (uc/color [238 243 230])
                                          :background    :light
                                          :text          :ultra-dark
                                          :selected-text (uc/color [180 140 0])
                                          :passive-track :ultra-dark
                                          :active-track  :extra-dark
                                          :minor-tick    (uc/color [112  37  0])
                                          :major-border  :minor-tick
                                          :checkbox :extra-dark
                                          :dbar-style :basic
                                          :dbar-active :ultra-dark))

(def ^:private creme-coffee-map (create-sparse-skin-map "Creme Coffee" 
                                                        creme-map
                                                        :dbar-style :matrix
                                                        :dbar-inactive :background
                                                        :dbar-active :ultra-dark
                                                        :active-track (uc/color [150 106  62])
                                                        :handle-style [:triangle]
                                                        :handle :ultra-dark
                                                        :handle-size 5
                                                        :checkbox :active-track
                                                        :checkbox-style [:dot :fill]
                                                        :checkbix-size 2))

(def ^:private dust-map (create-sparse-skin-map "Dust"
                                                default-skin-map
                                                :icon-prefix "black"
                                                :icon-selected-prefix :black
                                                :ultra-dark (uc/color [ 18  18  18])
                                                :extra-dark (uc/color [ 45  45  45])
                                                :dark   (uc/color  [209 203 203])
                                                :medium (uc/color  [234 231 226])
                                                :light  (uc/color  [250 247 255])
                                                :extra-light (uc/color [247 247 240])
                                                :ultra-light :extra-light
                                                :background :medium
                                                :text :extra-dark
                                                :selected-text :dark
                                                :label :text
                                                :title :text
                                                :minor-border :text
                                                :major-border nil
                                                :handle :active-track
                                                :handle-style [:dot :fill]
                                                :handle-size nil
                                                :passive-track :dark
                                                :active-track :extra-dark
                                                :minor-tick :extra-dark
                                                :major-tick :extra-dark
                                                :button-border nil
                                                :button-selected-border nil
                                                :checkbox :extra-dark
                                                :checkbox-rim :extra-dark
                                                :checkbox-style nil
                                                :checkbox-size 2
                                                :checkbox-rim-radius nil
                                                :env-background :background
                                                :env-border :dark
                                                :env-segment :extra-dark
                                                :env-selected (uc/color :yellow)
                                                :env-handle ::env-segment
                                                :dbar-inactive :background
                                                :dbar-active :extra-dark
                                                :dbar-style :basic
                                                ))


(def ^:private dust-coffee-map (create-sparse-skin-map "Dust Coffee"
                                                       dust-map
                                                       :background (uc/color [232 217 185])
                                                       :passive-track (uc/color [207 193 165])
                                                       :dbar-inactive :background
                                                       
                                                       ))
                                                       



(def ^:private emerald-dusk-map (create-skin-map "Emerald Dusk"
                                                 default-skin-map
                                                 :icon-prefix "gray"
                                                 :icon-selected-prefix "white"
                                                 :ultra-dark     (uc/color [  4  10   8])
                                                 :extra-dark     (uc/color [ 20  38  31])
                                                 :dark           (uc/color [ 47  90  74])
                                        ;:medium         (uc/color [ 92 113 104])
                                                 :medium         (uc/color [ 18  82  41])
                                                 :light          (uc/color [156 164 143])
                                                 :extra-light    (uc/color [255 255 200])
                                                 :selected-text  (uc/color [155 147 167])
                                                 :passive-track  :dark
                                                 :active-track   (uc/color [ 25 150  57])
                                                 :handle         :extra-light ; (uc/color [155 147 167])
                                                 :handle-style   [:dot]
                                                 :handle-size    3
                                                 :minor-border   :light
                                                 :major-border   :light
                                                 :dbar-style     :sixteen
                                                 :dbar-inactive  :ultra-dark
                                                 :dbar-active    :extra-light
                                                 :checkbox       :extra-light
                                                 :checkbox-size  2
                                                 :env-background :ultra-dark
                                                 :env-segment    :light
                                                 :env-selected   (uc/color [155 147 167])
                                                 :env-handle     :light))

(def ^:private gemini-map (create-skin-map "Gimini"
                                           default-skin-map
                                           :icon-prefix  "gray"
                                           :icon-selected-prefix "white"
                                           :ultra-dark        (uc/color [ 17  21   29])
                                           :dark              (uc/color [ 47  60   82])
                                           :medium            (uc/color [134 138 141])
                                           :light             (uc/color [179 195 220])
                                           :extra-light       (uc/color [237 229 183])
                                           :ultra-light       (uc/color [255 231 101])
                                           :minor-border      :medium
                                           :major-border      :ultra-dark
                                           :passive-track     :ultra-dark ; :extra-light
                                           :active-track      (uc/color [ 68 113 120]) ;:ultra-light
                                           :handle            :active-track
                                           :handle-style      [:triangle]
                                           :button-border     :light
                                           :checkbox          :ultra-light
                                           :dbar-inactive     :ultra-dark
                                           :dbar-active       (uc/color [ 83 136 145]) ;:active-track ;:ultra-light
                                           :dbar-style        :matrix
                                           :env-background    :ultra-dark
                                           :env-border        :medium
                                           :env-segment       :light
                                           :env-selected      :ultra-light
                                           :env-handle        :ultra-light))

(def ^:private graphite-map (create-skin-map "Graphite"
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
                                             :dbar-style :matrix))

(def ^:private graphite-aqua-map (create-sparse-skin-map "Graphite Aqua"
                                                         graphite-map
                                                         :background   (uc/color [ 77  77  77])
                                                         :ultra-light  (uc/color [ 80 114 237])
                                                         :passive-track :light
                                                         :active-track :ultra-dark
                                                         :handle :active-track
                                                         :handle-style  [:dot :fill]
                                                         :dbar-style   :sixteen
                                                         :dbar-inactive :extra-dark
                                                         :dbar-active :extra-light
                                                         :checkbox :extra-light
                                                         :checkbox-size 2))

(def ^:private graphite-glass-map (create-sparse-skin-map "Graphite Glass"
                                                          graphite-map
                                                          :background (uc/color [ 25  25  25])
                                                          :active-track (uc/color :blue)
                                                          :dbar-style :matrix
                                                          :dbar-inactive (uc/color :black)
                                                          :dbar-active (uc/color :blue)
                                                          :checkbox (uc/color [100 250 255])
                                                          :checkbox-size 2
                                                          ))

(def ^:private nebula-map (create-skin-map "Nebula"
                                           default-skin-map
                                           :dark    (uc/color [ 42  46  54])
                                           :medium  (uc/color [230 232 237])
                                           :light   (uc/color [244 244 252])
                                           :background   :light
                                           :text         :dark
                                           :selected-text (uc/color [196 0 250])
                                           :passive-track :medium
                                           :active-track :ultra-dark
                                           :minor-tick   :dark
                                           :checkbox :dark
                                           :dbar-style   :basic
                                           :dbar-active  :ultra-dark))

(def ^:private office-black-map (create-skin-map "Office Black 2007"
                                                 default-skin-map
                                                 :icon-prefix  "black"
                                                 :icon-selected-prefx "white"
                                                 :ultra-dark    (uc/color :black)
                                                 :extra-dark    (uc/color [ 64  64  64])
                                                 :dark          (uc/color [127 155 204])
                                                 :medium        (uc/color [190 197 204])
                                                 :light         (uc/color [230 232 237])
                                                 :extra-light   (uc/color :white)
                                                 :background    :medium
                                                 :text          :extra-dark
                                                 :passive-track :dark
                                                 :active-track  :extra-dark
                                                 :minor-tick :ultra-dark
                                                 :dbar-active :ultra-dark))

(def ^:private office-blue-map (create-skin-map "Office Blue 2007"
                                                default-skin-map
                                                :icon-prefix  "black"
                                                :icon-selected-prefx "gray"
                                                :ultra-dark    (uc/color [ 34   4  51])
                                                :dark          (uc/color [ 21  66 139])
                                                :medium        (uc/color [163 194 234])
                                                :light         (uc/color [190 216 252])
                                                :extra-light   (uc/color [200 219 238])
                                                :ultra-light   (uc/color [255 255 169])
                                                :background    (uc/color [200 219 238])
                                                :text          :extra-dark
                                                :passive-track :dark
                                                :active-track  :extra-dark
                                                :minor-tick :ultra-dark
                                                :dbar-active :ultra-dark))

(def ^:private office-silver-map (create-skin-map "Office Silver 2007"
                                                  default-skin-map
                                                  :ultra-dark    (uc/color :black)
                                                  :dark          (uc/color [ 76  83  92])
                                                  :medium        (uc/color [205 222 243])
                                                  :light         (uc/color [243 245 245])
                                                  :extra-light   (uc/color [200 219 238])
                                                  :ultra-light   (uc/color [186 140  74])
                                                  :background    :light
                                                  :text          :extra-dark
                                                  :passive-track :dark
                                                  :active-track  :extra-dark
                                                  :minor-tick :ultra-dark
                                                  :dbar-active :ultra-dark))

(def ^:private raven-map (create-skin-map "Raven"
                                          default-skin-map
                                          :icon-prefix "gray"
                                          :icon-selected-prefix "white"
                                          :ultra-dark    (uc/color :black)
                                          :extra-dark    (uc/color [ 29  25  22])
                                          :dark          (uc/color [ 55  46  37])
                                          :medium        (uc/color [ 69  59  54])
                                          :light         (uc/color [ 74  70  65])
                                          :extra-light   (uc/color [128   0 255])
                                          :ultra-light   (uc/color [231 235 231])
                                          :background    :extra-dark
                                          :text          :ultra-light
                                          :selected-text :extra-light
                                          :passive-track :medium
                                          :active-track  :extra-light
                                          :minor-tick    (uc/color [193 204 214])
                                          :handle        :ultra-light
                                          :minor-border  :light
                                          :major-border  :light
                                          :button-border (uc/color [193 204 214])
                                          :checkbox      :ultra-light
                                          :checkbox-size 2
                                          :dbar-style     :sixteen
                                          :dbar-inactive  :ultra-dark
                                          :dbar-active    :extra-light))

(def ^:private twilight-map (create-skin-map "Twilight"
                                             default-skin-map
                                             :icon-prefix "gray"
                                             :icon-selected-prefix "white"
                                             :ultra-dark    (uc/color :black)
                                             :extra-dark    (uc/color [ 57  57  52])
                                             :dark          (uc/color [ 69  67  59])
                                             :medium        (uc/color [140 132  95])
                                             :light         (uc/color [185 180 158])
                                             :ultra-light   (uc/color [218 224 235])
                                             :background    :ultra-dark
                                             :passive-track :dark
                                             :active-track  :light
                                             :handle-style  [:triangle]
                                             :minor-tick    :medium
                                             :env-background :ultra-dark
                                             :dbar-style    :twilight
                                             :dbar-inactive :dark
                                             :dbar-active   :light))
(def ^:private schemes 
  {"Default" default-skin-map
   "Autumn" autumn-map
   "Business" business-map
   "Business Black Steel" business-black
   "Business Blue Steel" business-blue
   "Cerulean" cerulean-map
   "Challenger Deep" challenger-deep-map
   "Creme" creme-map
   "Creme Coffee" creme-coffee-map
   "Dust" dust-map
   "Dust Coffee" dust-coffee-map
   "Emerald Dusk" emerald-dusk-map
   "Gemini" gemini-map
   "Graphite" graphite-map
   "Graphite Aqua" graphite-aqua-map
   "Graphite Glass" (create-sparse-skin-map "Graphite Glass" graphite-glass-map)
   "Magellan"       (create-sparse-skin-map  "Magellan" challenger-deep-map)
   "Mariner" (create-sparse-skin-map "Mariner" creme-coffee-map 
                                     :background (uc/color [217 218 217])
                                     :occluder :background
                                     :dbar-style :basic)
   "Mist Aqua" (create-sparse-skin-map "Mist Aqua" business-blue)
   "Mist Silver" (create-sparse-skin-map "Mist Silver" business-black)
   "Moderate" (create-sparse-skin-map "Moderate" business-blue)
   "Nebula" nebula-map
   "Nebula Brick Wall" (create-sparse-skin-map "Nebula Brick Wall"
                                               nebula-map
                                               :dbar-style :matrix
                                               :checkbox-size 2)
   "Office Black 2007" office-black-map
   "Office Blue 2007" office-blue-map
   "Office Silver 2007" office-silver-map
   "Raven" raven-map
   "Sahara" (create-sparse-skin-map "Sahara" creme-coffee-map
                                    :background (uc/color [217 218 217])
                                    :occluder :background
                                    :handle-style [:dot :fill]
                                    :dbar-style :basic)
   "Twilight" twilight-map})


(defn skin-color
  ([skin-map key counter]
   (let [val (get skin-map key)
         rs (cond (keyword? val)
                  (do 
                    (if (pos? counter)
                      (skin-color skin-map val (dec counter))
                      (do 
                        (println (format "skin-color terminated by guard counter, key = %s   val = %s" key val))
                        (System/exit 0))))
                        
                  val val
                  
                  :default 
                  (do 
                    (if (pos? counter)
                      (skin-color (:parent skin-map) key (dec counter))
                      (do 
                        (println (format "skin-color terminated by guard counter, key = %s   val = %s" key val))
                        (System/exit 0))) ))]
     rs))
  ([key]
   (skin-color (get schemes (config/current-skin)) key 10)))

;; DON NOT USE to extract color from skin-map!
(defn skin-value 
  ([skin-map key]
   (let [val (get skin-map key)]
     (or val (skin-value (:parent skin-map) key))))
  ([key]
   (skin-value (get schemes (config/current-skin)) key)))


(defn- lnf-color [key]
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
           
(defn- lnf-value [key]
  (fn [](skin-value key)))

(def icon-prefix (lnf-value              :icon-prefix))
(def icon-selected-prefix (lnf-value     :icon-selected-prefix))
(def checkbox-rim-radius (lnf-value      :checkbox-rim-radius))
(def checkbox-style (lnf-value           :checkbox-style))
(def checkbox-size (lnf-value            :checkbox-size))
(def handle-style (lnf-value             :handle-style))
(def handle-size (lnf-value              :handle-size))
(defn dbar-style []
  (or (config/displaybar-style)
      (skin-value :dbar-style)))

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
