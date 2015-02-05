(ns sgwr.widgets.radio
   "Defines sets of mutually exclusive buttons"
  (:require [sgwr.components.circle :as circle])
  (:require [sgwr.components.group :as group])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.util.color :as uc]))

(let [counter* (atom 0)]
  (defn- get-button-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "radio-button-%d" n))))))

(defn- dummy-action [& _])

(defn clear-radio-button-list! [rbl*]
  "(clear-radio-button-list! rbl*)
   Sets all buttons in button list as 'unselected'
   
   rbl* - atom holding list of radio-buttons
   Returns nil"
  (doseq [b @rbl*]
    (.select! b false)
    (.use-attributes! b :default))
  nil)

(defn select-radio-button! [rb]
  "(select-radio-button! rb)
   Set radio-button rb state to 'selected'
   Extract radiobutton-list to which rb belongs from rb 
   and set all other buttons in the list to unselected.
   Returns rb"
  (let [rbl* (.get-property rb :radio-button-list*)]
    (clear-radio-button-list! rbl*)
    (.select! rb true)
    (.use-attributes! rb :selected)
    rb))

(defn- compose-action [afn]
  (fn [rb ev]
    (let [flag (.local-property rb :enabled)]
      (if flag (afn rb ev)))))

(defn- compose-pressed-action 
  ([](compose-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj ev]
     (if (.local-property obj :enabled)
       (let [rbl* (.get-property obj :radio-button-list* (atom []))
             drw (.get-property obj :drawing)]
         (clear-radio-button-list! rbl*)
         (.select! obj true)
         (.use-attributes! obj :selected)
         (if drw (.render drw))
         (pfn obj ev))))))

(defn- compose-exited-action 
  ([](compose-exited-action (fn [& _])))
  ([xfn]
   (fn [obj ev]
   (if (.local-property obj :enabled)
     (let [flag (.selected? obj)
           drw (.get-property obj :drawing)]
       (if flag
         (.use-attributes! obj :selected))
       (xfn obj ev))))))

(defn- blank-radio-button [parent rbl* id & {:keys [drag-action move-action enter-action exit-action
                                                    press-action release-action click-action]
                                             :or {drag-action dummy-action
                                                  move-action dummy-action
                                                  enter-action dummy-action
                                                  exit-action dummy-action
                                                  press-action dummy-action
                                                  release-action dummy-action
                                                  click-action dummy-action}}]
  (let [grp (group/group parent :etype :radio-button :id id)]
    (.put-property! grp :radio-button-list* rbl*)
    (.put-property! grp :action-mouse-dragged  (compose-action drag-action))
    (.put-property! grp :action-mouse-moved    (compose-action move-action))
    (.put-property! grp :action-mouse-entered  (compose-action enter-action))
    (.put-property! grp :action-mouse-exited   (compose-exited-action exit-action))
    (.put-property! grp :action-mouse-pressed  (compose-pressed-action press-action))
    (.put-property! grp :action-mouse-released (compose-action release-action))
    (.put-property! grp :action-mouse-clicked  (compose-action click-action))
    (swap! rbl* (fn [q](conj q grp)))
    grp))

(defn radio-button [parent p0 txt rbl* & {:keys [id
                                                 drag-action move-action enter-action exit-action
                                                 press-action release-action click-action
                                                 text-color text-style text-size 
                                                 gap text-x-shift text-y-shift
                                                 c1-color c1-radius
                                                 c2-color c2-radius]
                                          :or {drag-action dummy-action
                                               move-action dummy-action
                                               enter-action dummy-action
                                               exit-action dummy-action
                                               press-action dummy-action
                                               release-action dummy-action
                                               click-action dummy-action
                                               text-color :white
                                               text-style 0
                                               text-size 8
                                               gap 4
                                               text-x-shift 0
                                               text-y-shift 0
                                               c1-color :gray   ;; outer circle
                                               c1-radius 8
                                               c2-color [64 191 64]  ;; inner circle
                                               c2-radius 4}}]
  "(radio-button parent p0 txt rbl* :id
       :drag-action :move-action :enter-action :exit-action 
       :press-action :release-action :click-action 
       :text-color :text-style :text-size
       :text-x-shift :text-y-shift :gap
       :c1-color :c1-radius
       :c2-color :c2-radius

   parent - SgwrComponent, parent group
   p0     - vector [x y], position of button
   txt    - String, button text
   rbl*   - Atom holding button list, Only one button in list may be
            selected at any given time.
   :id    - keyword, if not specified a unique id will be generated.
   
    Actions

      :drag-action, :move-action, :enter-action, :exit-action,
      :press-action, :release-action, :click-action
       
       Function of form (fn [obj ev] ...) where obj is this widget
       and ev is an instance of java.awt.event.MouseEvent 

   :text-color - Color, keyword or vector, see swr.util.color
   :text-style - keyword, Text font, default :mono
   :text-size  - float, font size, default 8
   :gap          - float, space between text and 'button', default 4
   :text-x-shift - float, value added to text horizontal position, default 0
   :text-y-shift - float, value added to text vertical position, default 0

   :c1-color  - Color of outer circle
   :c1-radius - Radius of outer circle, default 8
   :c2-color  - Color of inner 'selected' circle
   :c2-radius - Radius of inner 'selected' circle, default 4

   Returns SgwrComponent group holding radio-button components"
     
  (let [grp (blank-radio-button parent rbl* (get-button-id id)
                                :drag-action drag-action 
                                :move-action move-action 
                                :enter-action enter-action 
                                :exit-action exit-action
                                :press-action press-action 
                                :release-action release-action 
                                :click-action click-action)
        est-tx-width (text/estimate-monospaced-width (* (count txt) text-size))
        est-tx-height (text/estimate-monospaced-height text-size)
        [x0 y0] p0
        xc (+ x0 c1-radius)
        x2 (+ xc c1-radius)
        x3 (+ x2 gap text-x-shift)
        yc (+ y0 c1-radius)
        y3 (+ yc (* 1/2 est-tx-height) text-y-shift)
        c1 (circle/circle-r grp [xc yc] c1-radius :color c1-color :id :c1 :fill false)
        c2 (circle/circle-r grp [xc yc] c2-radius :color c2-color :id :c2 :fill false)
        txobj (text/text grp [x3 y3] txt 
                         :color text-color
                         :id :text
                         :style text-style
                         :size text-size)
        occluder (let [occ (circle/circle-r grp [xc yc] c1-radius 
                                            :id :occluder 
                                            :fill true)]
                   (.color! occ :disabled (uc/transparent :black 190))
                   (.color! occ :enabled [0 0 0 0])
                   (.color! occ :default [0 0 0 0])
                   (.color! occ :rollover [0 0 0 0])
                   (.use-attributes! occ :enabled)
                   occ)]
        (.put-property! grp :c1 c1)
        (.put-property! grp :c2 c2)
        (.put-property! grp :text-component txobj)
        (.fill! c2 :default :no)
        (.fill! c2 :selected true)
        (.put-property! grp :occluder occluder)
        (.use-attributes! grp :default)
        grp))
