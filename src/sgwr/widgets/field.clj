(ns sgwr.widgets.field
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  
  )

(let [counter* (atom 0)]
  (defn- get-field-id [id]
    (let [n @counter*]
      (swap! counter* inc))
    (or id (keyword (format "field-%d" @counter*)))))
    
(defn- dummy-action [& _] nil)

(defn set-ball-value! 
  ([fobj id val]
   (set-ball-value! fobj id val :render))
  ([fobj id val render?]
  (let [ball (get @(.get-property fobj :balls*) id)]
    (if ball 
      (let [mapx (.get-property fobj :fn-x-val->pos)
            mapy (.get-property fobj :fn-y-val->pos)
            p [(mapx (first val))(mapy (second val))]]
        (.set-points! ball [p])
        (if render? (.render (.get-property fobj :drawing)))
        ball)
      (do (utilities/warning (format "field %s does not have ball with id %s" 
                                     (.get-property fobj :id) id))
          nil)))))
        
      


(defn field [parent p0 p1 range-x range-y & {:keys [id
                                                    drag-action move-action enter-action exit-action
                                                    press-action release-action click-action
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
                                                  pad-color [0 0 0 0]
                                                  rim-color :gray
                                                  rim-style 0
                                                  rim-width 1.0
                                                  rim-radius 0}}]
  (let [grp (group/group parent 
                         :etype :field
                         :id (get-field-id id))
        pad (let [pad (rect/rectangle grp p0 p1
                                      :id :pad
                                      :color pad-color
                                      :fill true)]
              (.put-property! pad :corner-radius rim-radius)
              (.color! pad :rollover pad-color)
              pad)
        rim (let [rim (rect/rectangle grp p0 p1
                                      :id :rim
                                      :color rim-color
                                      :style rim-style
                                      :width rim-width
                                      :fill :no)]
              (.put-property! rim :corner-radius rim-radius)
              rim)]
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :balls* (atom {}))
    (.put-property! grp :action-mouse-dragged  (or drag-action dummy-action))
    (.put-property! grp :action-mouse-moved    (or move-action dummy-action))
    (.put-property! grp :action-mouse-entered  (or enter-action dummy-action))
    (.put-property! grp :action-mouse-exited   (or exit-action dummy-action))
    (.put-property! grp :action-mouse-pressed  (or press-action dummy-action))
    (.put-property! grp :action-mouse-released (or release-action dummy-action))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    (.put-property! grp :range-x range-x)
    (.put-property! grp :range-y range-y)
    (.put-property! grp :fn-x-pos->val (math/clipped-linear-function (first p0)
                                                                     (first range-x)
                                                                     (first p1)
                                                                     (second range-x)))
    (.put-property! grp :fn-x-val->pos (math/clipped-linear-function (first range-x)
                                                                     (first p0)
                                                                     (second range-x)
                                                                     (first p1)))
    (.put-property! grp :fn-y-pos->val (math/clipped-linear-function (second p0)
                                                                     (first range-y)
                                                                     (second p1)
                                                                     (second range-y)))
    (.put-property! grp :fn-y-val->pos (math/clipped-linear-function (first range-y)
                                                                     (second p0)
                                                                     (second range-y)
                                                                     (second p1)))
    grp))
    


;; (defn ball [parent-field id init-value & {:keys [drag-action press-action release-action click-action
;;                                                  value-hook
;;                                                  color style size
;;                                                  selected-color selected-style]
;;                                           :or {drag-action nil
;;                                                press-action nil
;;                                                release-action nil
;;                                                click-action nil
;;                                                value-hook identity
;;                                                color :white
;;                                                style :x
;;                                                size 2
;;                                                selected-color :white
;;                                                selected-style :dot}}]
;;   (let [grp (group/group parent-field :etype :ball :id id)
;;         mapx (.get-property parent-field :fn-x-val->pos)
;;         mapy (.get-property parent-field :fn-y-val->pos)
;;         pnt (point/point grp 
;;                          [(mapx (first init-value))(mapy (second init-value))]
;;                          ;init-value 
;;                          :id id
;;                          :color (uc/color color)
;;                          :style (utilities/map-style style)
;;                          :size size)]
;;     (swap! (.get-property parent-field :balls*)
;;            (fn [q](assoc q id grp)))
;;     (.color! pnt :rollover (uc/color color)) 
;;    
;;     grp))
    
    
  
  
                                
