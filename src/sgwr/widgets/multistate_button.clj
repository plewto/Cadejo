(ns sgwr.widgets.multistate-button
  
  "Defines button widgets which may assume any number of states.
   The button advances to the next state upon being clicked."

  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))


(let [counter* (atom 0)]
  (defn- get-button-id [prefix id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "%s-%d" prefix n))))))

(defn- third [col](nth col 2))

(defn set-multistate-button-state! 
  "(set-multistate-button-state! msb state-index)
   (set-multistate-button-state! msb state-index render?)

   Set multistate-button to to state indicated by state-index

   msb         - SgwrElement group containing multistate-button
   state-index - int, the new state, 0 <= index < max
                 The number maximum index value may be determined
                 by (count (.get-property msb :states))
                 A warning message is displayed if index is out of range
      
   render?     - Boolean, if true render drawing containing, 
                 default true

   Returns button-index if index is valid, nil if index is out of range."
  ([msb state-index]
   (set-multistate-button-state! msb state-index true))
  ([msb state-index render]
   (let [states (.get-property msb :states)
         state-count (count states)]
     (if (and (integer? state-index)
              (>= state-index 0)
              (< state-index state-count))
       (let [current (nth states state-index)]
         (.put-property! msb :current-state-index state-index)
         (.put-property! msb :current-state current)
         (.use-attributes! msb current)
         (if render (.render (.get-property msb :drawing)))
         state-index)
       (utilities/warning (format "Invalid state index %s for multistate-button %s"
                                  state-index (.get-property msb :id)))))))

(defn current-multistate-button-state [msb]
  "(current-multistate-button-state msb)
   
    Returns vector [index keyword] of the current button state"   

  [(.get-property msb :current-state-index)
   (.get-property msb :current-state)])
      
(defn- compose-pressed-action 
  ([](compose-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj env]
     (let [states (.get-property obj :states)
           csi (rem (inc (.get-property obj :current-state-index))
                    (count states))
           new-state (nth states csi)
           drw (.get-property obj :drawing)]
       (.put-property! obj :current-state-index csi)
       (.put-property! obj :current-state (nth states csi))
       (.use-attributes! obj new-state)
       (if drw (.render drw))
       (pfn obj env)))))
           
(defn- compose-exited-action 
  ([](compose-exited-action (fn [& _])))
  ([xfn]
   (fn [obj ev]
     (let [states (.get-property obj :states)
           csi (.get-property obj :current-state-index)]
       (.use-attributes! obj (nth states csi))
       (xfn obj ev)))))

;; states - an array of keywords - attribute names    
(defn- blank-multistate-button [parent state-keys id  & {:keys [drag-action move-action enter-action exit-action
                                                                press-action release-action click-action]
                                                         :or {drag-action nil
                                                              move-action nil
                                                              enter-action nil
                                                              exit-action nil
                                                              press-action nil
                                                              release-action nil
                                                              click-action nil}}]
  (let [grp (group/group parent :etype :multistate-button :id id)
        dummy-action (fn [obj ev] nil)]
    (.put-property! grp :states state-keys)
    (.put-property! grp :current-state-index 0)
    (.put-property! grp :current-state state-keys)
    (.put-property! grp :action-mouse-dragged  (or drag-action dummy-action))
    (.put-property! grp :action-mouse-moved    (or move-action dummy-action))
    (.put-property! grp :action-mouse-entered  (or enter-action dummy-action))
    (.put-property! grp :action-mouse-exited   (compose-exited-action (or exit-action dummy-action)))
    (.put-property! grp :action-mouse-pressed  (compose-pressed-action (or press-action dummy-action)))
    (.put-property! grp :action-mouse-released (or release-action dummy-action))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    grp))

(defn text-multistate-button [parent p0 states & {:keys [id
                                                         drag-action move-action enter-action exit-action
                                                         press-action release-action click-action
                                                         text-color text-style text-size
                                                         gap text-x-shift text-y-shift w h
                                                         pad-color
                                                         rim-color rim-style rim-width rim-radius]
                                                  :or {id nil
                                                       drag-action nil
                                                       move-action nil
                                                       enter-action nil
                                                       exit-action nil
                                                       press-action nil
                                                       release-action nil
                                                       click-action nil
                                                       text-color (uc/color :white)
                                                       text-style 0
                                                       text-size 8
                                                       gap 4
                                                       text-x-shift 0
                                                       text-y-shift 0
                                                       w nil
                                                       h nil
                                                       pad-color [0 0 0 0]
                                                       rim-color (uc/color :gray)
                                                       rim-style 0
                                                       rim-width 1.0
                                                       rim-radius 12}}]
  "(text-multistate-button parent p0 states :id
      :drag-action :move-action :enter-action :exit-action 
      :press-action :release-action :click-action 
      :text-color :text-style :text-size
      :text-x-shift :text-y-shift
      :gap :w :H
      :pad-color
      :rim-color :rim-style :rim-width :rim-radius)

   Create multistate-button with text content

   parent - SgwrElement, the parent group
   p0     - vector [x0 y0], the buttons position
   states - nested vector, [[key-0 text-0 color-0][key-1 text-1 color-1]...[key-n text-n color-n]]
            Defines the possible states the button may be in.  Where

            key-i   - Unique keyword
            text-i  - String
            color-i - Color, keyword or vector, see sgwr.util.color

   :id    - keyword, if not specified a unique id will be created

    Actions

      :drag-action, :move-action, :enter-action, :exit-action,
      :press-action, :release-action, :click-action
       
       Function of form (fn [obj ev] ...) where obj is this widget
       and ev is an instance of java.awt.event.MouseEvent 

    :text-color - Color, keyword or vector
    :text-style - Text font, default :mono
    :text-size  - float, text font size, default 8
    :text-x-shift - float, value added to horizontal text position, default 0 
    :text-y-shift - float, value added to vertical text position, default 0
    
    :gap - float, amount of space between text and rim 
    :w   - float, button width. If not specified the width is guessed using 
           text-size and maximum character count
    :h   - float, button height. If not specified the height is guessed 
           using text-size.

    :pad-color  - Color, keyword or vector
    :rim-color  - Color, keyword or vector
    :rim-style  - int or keyword, the rim line dash pattern, default :solid
    :rim-width  - float, the rim line width, default 1.0
    :rim-radius - int, rim/pad corner radius, default 12

    Returns SgwrElement group"

  (let [grp (blank-multistate-button parent (map first states) (get-button-id "multistate-button" id)
                                     :drag-action drag-action 
                                     :move-action move-action 
                                     :enter-action enter-action 
                                     :exit-action exit-action 
                                     :press-action press-action 
                                     :release-action release-action 
                                     :click-action click-action)
        state-text (map second states)
        max-text-length (apply max (map count state-text))
        est-tx-width (text/estimate-monospaced-width (* max-text-length text-size))
        est-tx-height (text/estimate-monospaced-height text-size)
        width (or w (+ est-tx-width (* 2 gap)))
        height (or h (+ est-tx-height (* 2 gap)))
        [x0 y0] p0
        x1 (+ x0 gap)
        x2 (+ x0 width text-x-shift)
        y2 (+ y0 height)
        yc (math/mean y0 y2)
        y1 (+ yc (* 1/2 est-tx-height) text-y-shift)
        pad (rect/rectangle grp p0 [x2 y2]
                            :id :rim
                            :color pad-color
                            :style 0
                            :width 1.0
                            :fill true)
        rim (rect/rectangle grp p0 [x2 y2]
                            :id :rim
                            :color rim-color
                            :style rim-style
                            :width rim-width
                            :fill :no)
        txtobj-lst (let [acc* (atom [])
                         i* (atom 0)]
                     (doseq [[attkey txt col] states]
                       (let [txobj (text/text grp [x1 y1] (nth state-text @i*)
                                              :id (keyword (format "text-%d" @i*))
                                              :color (uc/color (or col text-color))
                                              :style text-style
                                              :size text-size)]
                         (.hide! txobj :default true)
                         (.color! txobj :rollover (uc/color (or col text-color)))
                         (.put-property! txobj :active-state attkey)
                         (swap! acc* (fn [q](conj q txobj)))
                         (swap! i* inc)))
                     @acc*)]
    (doseq [k (map first states)]
      (doseq [tx txtobj-lst]
        (let [actvs (.get-property tx :active-state)]
          (if (= actvs k)
            (.hide! tx actvs :no)
            (.hide! tx k true)))))
    (.color! pad :rollover pad-color)
    (.put-property! pad :corner-radius rim-radius)
    (.put-property! rim :corner-radius rim-radius)
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :text-objects txtobj-lst)
    (.use-attributes! grp (first (first states)))
    grp))

;; states a nested list of form 
;; [[key-0 igroup-0 isubgroup-0][key-1 igroup-1 isubgroup-1] ...]
;;
(defn icon-multistate-button [parent p0 states  & {:keys [id
                                                          drag-action move-action enter-action exit-action
                                                          press-action release-action click-action
                                                          icon-prefix gap w h
                                                          pad-color
                                                          rim-color rim-style rim-width rim-radius]
                                                  :or {id nil
                                                       drag-action nil
                                                       move-action nil
                                                       enter-action nil
                                                       exit-action nil
                                                       press-action nil
                                                       release-action nil
                                                       click-action nil
                                                       icon-prefix :white
                                                       gap 4
                                                       w 44
                                                       h 44
                                                       pad-color [0 0 0 0]
                                                       rim-color [0 0 0 0] ; (uc/color :gray)
                                                       rim-style 0
                                                       rim-width 1.0
                                                       rim-radius 12}}]
  "(icon-multistate-button parent p0 states :id
      :drag-action :move-action :enter-action :exit-action 
      :press-action :release-action :click-action 
      :icon-prefix
      :gap :w :h 
      :pad-color
      :rim-color :rim-style :rim-width :rim-radius)

   Creates multistate button using icons from resource directory

   states - a nested vector of form

            [[key-0 igroup-0 isub-0][key-1 igroup-1 isub-1]...[key-n igroup-n isub-n]]

   :icon-prefix - keyword, either :black, :gray or :white"
  (let [grp (blank-multistate-button parent (map first states) (get-button-id "multistate-button" id)
                                     :drag-action drag-action 
                                     :move-action move-action 
                                     :enter-action enter-action 
                                     :exit-action exit-action 
                                     :press-action press-action 
                                     :release-action release-action 
                                     :click-action click-action)
        [x0 y0] p0
        x1 (+ x0 gap)
        y1 (+ y0 gap)
        pad (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :rim
                            :color pad-color
                            :style 0
                            :width 1.0
                            :fill true)
        rim (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :rim
                            :color rim-color
                            :style rim-style
                            :width rim-width
                            :fill :no)
        iobj-lst (let [acc* (atom [])
                       i* (atom 0)]
                   (doseq [[attkey igroup isubgroup] states]
                     (let [iobj (image/read-icon grp [x1 y1] icon-prefix igroup isubgroup)]
                       (.put-property! iobj :active-state attkey)
                       (.hide! iobj :default true)
                       (swap! acc* (fn [q](conj q iobj)))))
                   @acc*)]
    (doseq [k (map first states)]
      (doseq [iobj iobj-lst]
        (let [active (.get-property iobj :active-state)]
          (if (= active k)
            (.hide! iobj k :no)
            (.hide! iobj k true)))))
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.color! pad :rollover pad-color)
    (.put-property! pad :corner-radius rim-radius)
    (.color! rim :unselected rim-color)
    (.use-attributes! grp (first (first states)))
    grp)) 


; ---------------------------------------------------------------------- 
;                       Checkboxes and Toggle buttons


(defn select-checkbox! 
  "(select-checkbox! cb flag render?)
   (select-checkbox! cb flag)
   Sets selection state of checkbox to flag
   If render? true render drawing containing button
   render? is true by default"
  ([cb flag]
   (select-checkbox! cb flag :render))
  ([cb flag render?]
   (if flag
     (do 
       (set-multistate-button-state! cb 1 render?)
       (.select! cb true))
     (do 
       (set-multistate-button-state! cb 0 render?)
       (.select! cb false)))))
  
  (def select-toggle-button! select-checkbox!)

;; selected-check & unselected-check arguments are vectors of form
;; [color [style..] size]
;;                      
(defn checkbox [parent p0 txt  & {:keys [id
                                         drag-action move-action enter-action exit-action
                                         press-action release-action click-action
                                         text-color text-style text-size 
                                         gap text-x-shift text-y-shift
                                         rim-color rim-style rim-size rim-radius
                                         selected-check unselected-check]
                                  :or {id nil
                                       drag-action nil
                                       move-action nil
                                       enter-action nil
                                       exit-action nil
                                       press-action nil
                                       release-action nil
                                       click-action nil
                                       text-color (uc/color :white)
                                       text-style 0
                                       text-size 8
                                       gap 6
                                       text-x-shift 0
                                       text-y-shift 0
                                       rim-color (uc/color :gray)
                                       rim-style 0
                                       rim-size 12 ;; in pixels
                                       rim-radius 0
                                       selected-check [:white [:diag :diag2] 4]
                                       unselected-check [[0 0 0 0] [:pixel] 1]}}]
  "(checkbox parent p0 txt :id
      :drag-action :move-action :enter-action :exit-action 
      :press-action :release-action :click-action 
      :text-color :text-style :text-size
      :text-x-shift :text-y-shift
      :gap :pad-color
      :rim-color :rim-style :rim-width :rim-radius)
  
   parent - SgwrElement, the parent group
   p0     - vector [x0 y0], the buttons position
   txt    - String, the text
   :id    - keyword, if not specified a unique id will be generated
   
    Actions

      :drag-action, :move-action, :enter-action, :exit-action,
      :press-action, :release-action, :click-action
       
       Function of form (fn [obj ev] ...) where obj is this widget
       and ev is an instance of java.awt.event.MouseEvent 

    :text-color - Color, keyword or vector
    :text-style - Text font, default :mono
    :text-size  - float, text font size, default 8
    :text-x-shift - float, value added to horizontal text position, default 0 
    :text-y-shift - float, value added to vertical text position, default 0

    :gap - float, amount of space between text and rim 
    :rim-color  - Color, keyword or vector
    :rim-style  - int or keyword, the rim line dash pattern, default :solid
    :rim-width  - float, the rim line width, default 1.0
    :rim-radius - int, rim/pad corner radius, default 12"
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "checkbox" id)
                                    :drag-action drag-action 
                                    :move-action move-action 
                                    :enter-action enter-action 
                                    :exit-action exit-action 
                                    :press-action press-action 
                                    :release-action release-action 
                                    :click-action click-action)
        [x0 y0] p0
        x1 (+ x0 rim-size)
        y1 (+ y0 rim-size)
        xc (math/mean x0 x1)
        yc (math/mean y0 y1)
        est-tx-height (text/estimate-monospaced-height text-size)
        x2 (+ x1 gap text-x-shift)
        y2 (+ yc (* 1/2 est-tx-height) text-y-shift)
        rim (let [bx (rect/rectangle grp p0 [x1 y1] :id :rim
                                     :color rim-color
                                     :style rim-style
                                     :fill :no)]
              (.put-property! bx :corner-radius rim-radius)
              bx)
        pnt (let [sc (or (first selected-check) :white)
                  st (or (second selected-check) [:diag :diag2])
                  sz (or (third selected-check) 2)
                  uc (or (first unselected-check) :gray)
                  ut (or (second unselected-check) :pixel)
                  uz (or (third unselected-check) 1)
                  pnt (point/point grp [xc yc] :id :point)]
              (.color! pnt :selected sc)
              (.style! pnt :selected st)
              (.size!  pnt :selected sz)
              (.color! pnt :unselected uc)
              (.style! pnt :unselected ut)
              (.size!  pnt :unselected uz)
              pnt)
        txobj (text/text grp [x2 y2] txt
                         :color text-color
                         :id :text
                         :style text-style
                         :size text-size)]
    (.put-property! grp :rim rim)
    (.put-property! grp :point pnt)
    (.put-property! grp :text-element txobj)
    (.use-attributes! grp :unselected)
    grp))
    
                    
;; selected-rim & unselected-rim  [color style width]
;;
(defn text-toggle-button [parent p0 txt & {:keys [id 
                                                  drag-action move-action enter-action exit-action
                                                  press-action release-action click-action
                                                  selected-text-color unselected-text-color text-style text-size
                                                  gap text-x-shift text-y-shift w h
                                                  selected-pad-color unselected-pad-color
                                                  selected-rim unselected-rim rim-radius]
                                           :or {id nil
                                                drag-action nil
                                                move-action nil
                                                enter-action nil
                                                exit-action nil
                                                press-action nil
                                                release-action nil
                                                click-action nil
                                                selected-text-color :white
                                                unselected-text-color :white
                                                text-style 0
                                                text-size 8
                                                gap 4
                                                text-x-shift 0
                                                text-y-shift 0
                                                w nil
                                                h nil
                                                selected-pad-color [0 128 128 64]
                                                unselected-pad-color [0 0 0 0]
                                                selected-rim [:green :solid 2.0]
                                                unselected-rim [:gray :solid 1.0]
                                                rim-radius 12}}]
  "(text-toggle-button parent p0 text
      :drag-action :move-action :enter-action :exit-action 
      :press-action :release-action :click-action 
      :text-color :text-style :text-size
      :gap :text-x-shift :text-y-shift
      :selected-pad-color :unselected-pad-color
      :selected-rim-color :unselected-rim-color :rim-radius)"
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "toggle-button" id)
                                     :drag-action drag-action 
                                     :move-action move-action 
                                     :enter-action enter-action 
                                     :exit-action exit-action 
                                     :press-action press-action 
                                     :release-action release-action 
                                     :click-action click-action)
        est-tx-width (text/estimate-monospaced-width (* (count txt) text-size))
        est-tx-height (text/estimate-monospaced-height text-size)
        width (or w (+ (* 2 gap) est-tx-width))
        height (or h (+ (* 2 gap) est-tx-height))
        [x0 y0] p0
        x1 (+ x0 gap text-x-shift)
        x3 (+ x0 width)
        y3 (+ y0 height)
        yc (math/mean y0 y3)
        y1 (+ yc (* 1/2 est-tx-height) text-y-shift)
        pad (let [pad (rect/rectangle grp p0 [x3 y3] :id :pad
                                      :color [0 0 0 0]
                                      :style 0
                                      :width 1.0
                                      :fill true)]
              (.color! pad :unselected unselected-pad-color)
              (.color! pad :selected selected-pad-color)
              (.color! pad :rollover selected-pad-color)
              (.put-property! pad :corner-radius rim-radius)
              pad)
        rim (let [bx (rect/rectangle grp p0 [x3 y3] :id :rim
                                     :color :gray
                                     :style 0
                                     :width 1.0
                                     :fill :no)]
              (.color! bx :unselected (or (first unselected-rim) [64 64 64]))
              (.style! bx :unselected (or (second unselected-rim) :solid))
              (.width! bx :unselected (or (third unselected-rim) 1.0))
              (.color! bx :selected (or (first selected-rim) :white))
              (.style! bx :selected (or (second selected-rim) :solid))
              (.width! bx :selected (or (third selected-rim) 2.0))
              (.put-property! bx :corner-radius rim-radius)
              bx)
        txobj (let [txobj (text/text grp [x1 y1] txt :id :text
                                     :color :white
                                     :style text-style
                                     :size text-size)]
                (.color! txobj :unselected unselected-text-color)
                (.color! txobj :selected selected-text-color)
                (.color! txobj :rollover selected-text-color)
                txobj)]
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :text-element txobj)
    (.use-attributes! grp :unselected)
    grp))
                     

;; selected-rim & unselected-rim  [color style width]
;;
(defn icon-toggle-button [parent p0 prefix group subgroup  & {:keys [id 
                                                                     drag-action move-action enter-action exit-action
                                                                     press-action release-action click-action
                                                                     gap w h
                                                                     selected-pad-color unselected-pad-color
                                                                     selected-rim unselected-rim rim-radius]
                                                              :or {id nil
                                                                   drag-action nil
                                                                    move-action nil
                                                                    enter-action nil
                                                                    exit-action nil
                                                                    press-action nil
                                                                    release-action nil
                                                                    click-action nil
                                                                    gap 7
                                                                    w 44
                                                                    h 44
                                                                    selected-pad-color [0 128 128 64]
                                                                    unselected-pad-color [0 0 0 0]
                                                                    selected-rim [:green :solid 2.0]
                                                                    unselected-rim [[0 0 0 0] :solid 1.0]
                                                                    rim-radius 12}}]
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "toggle-button" id)
                                     :drag-action drag-action 
                                     :move-action move-action 
                                     :enter-action enter-action 
                                     :exit-action exit-action 
                                     :press-action press-action 
                                     :release-action release-action 
                                     :click-action click-action)
        [x0 y0] p0
        x1 (+ x0 gap)
        y1 (+ y0 gap)
        x2 (+ x1 w)
        y2 (+ y1 h)
        pad (let [pad (rect/rectangle grp p0 [x2 y2] :id :pad
                                      :color [0 0 0 0]
                                      :style 0
                                      :width 1.0
                                      :fill true)]
              (.color! pad :unselected unselected-pad-color)
              (.color! pad :selected selected-pad-color)
              (.color! pad :rollover selected-pad-color)
              (.put-property! pad :corner-radius rim-radius)
              pad)
        rim (let [bx (rect/rectangle grp p0 [x2 y2] :id :rim
                                     :color :gray
                                     :style 0
                                     :width 1.0
                                     :fill :no)]
              (.color! bx :unselected (or (first unselected-rim) [64 64 64]))
              (.style! bx :unselected (or (second unselected-rim) :solid))
              (.width! bx :unselected (or (third unselected-rim) 1.0))
              (.color! bx :selected (or (first selected-rim) :white))
              (.style! bx :selected (or (second selected-rim) :solid))
              (.width! bx :selected (or (third selected-rim) 2.0))
              (.put-property! bx :corner-radius rim-radius)
              bx)
        icon (image/read-icon grp [x1 y1] prefix group subgroup)]
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :icon icon)
    (.use-attributes! grp :unselected)
    grp))

               
