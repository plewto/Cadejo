(ns sgwr.widgets.field
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))

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
            xpos (mapx (first val))
            ypos (mapy (second val))
            xval ((.get-property fobj :fn-x-pos->val) xpos)
            yval ((.get-property fobj :fn-y-pos->val) ypos)
            p [(mapx (first val))(mapy (second val))]]
        (.set-points! ball [p])
        (.put-property! ball :value [xval yval])
        (if render? (.render (.get-property fobj :drawing)))
        ball)
      (do (utilities/warning (format "field %s does not have ball with id %s" 
                                     (.get-property fobj :id) id))
          nil)))))
        
 
(defn- select-ball [obj ev]
  (let [cs (.coordinate-system obj)
        pos (.inv-map cs [(.getX ev)(.getY ev)])
        d* (atom constants/infinity)
        ball* (atom nil)]
    (doseq [b (vals @(.get-property obj :balls*))]
      (let [d (.distance b pos)]
        (.use-attributes! b :default)
        (if (< d @d*)
          (do 
            (reset! d* d)
            (reset! ball* b)))))
    (if @ball*
      (let [b @ball*]
        (reset! (.get-property obj :current-ball*) b)
        (.use-attributes! b :selected)
        b))))

(defn- compose-move-action [mfn]
  (fn [obj ev]
    (select-ball obj ev)
    (mfn obj ev)
    (.render (.get-property obj :drawing))))

(defn- compose-drag-action [dfn]
  (fn [obj ev]
    (select-ball obj ev)
    (let [b @(.get-property obj :current-ball*)]
      (if b
        (let [cs (.coordinate-system obj)
              pos (.inv-map cs [(.getX ev)(.getY ev)])
              mapx (.get-property obj :fn-x-pos->val)
              mapy (.get-property obj :fn-y-pos->val)
              val [(mapx (first pos))(mapy (second pos))]]
          (.set-points! b [pos])
          (.put-property! b :value val)))
      (dfn obj ev)
      (.render (.get-property obj :drawing)))))


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
    (.put-property! grp :current-ball* (atom nil))
    (.put-property! grp :action-mouse-dragged  (compose-drag-action (or drag-action dummy-action)))
    (.put-property! grp :action-mouse-moved    (compose-move-action (or move-action dummy-action)))
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
                                                                     (second range-y)
                                                                     (second p1)
                                                                     (first range-y)))
    (.put-property! grp :fn-y-val->pos (math/clipped-linear-function (second range-y)
                                                                     (second p0)
                                                                     (first range-y)
                                                                     (second p1)))
    grp))
    
    
(defn ball [parent id init-value & {:keys [color style size 
                                           selected-color selected-style]
                                    :or {color :white
                                         style [:dot]
                                         size 2
                                         selected-color constants/default-rollover-color
                                         selected-style [:fill :dot]}}]
  (let [mapx (.get-property parent :fn-x-val->pos)
        mapy (.get-property parent :fn-y-val->pos)
        pnt (point/point parent [(mapx (first init-value))(mapy (second init-value))]
                         :id id
                         :color color
                         :style style
                         :size size)]
    (swap! (.get-property parent :balls*)
           (fn [q](assoc q id pnt)))
    (.put-property! pnt :value init-value)
    (.color! pnt :rollover color)
    (.color! pnt :selected selected-color)
    (.style! pnt :selected selected-style)
    pnt))    
  
  
                                
