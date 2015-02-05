(ns sgwr.tools.slider
  (:require [sgwr.components.group :as group])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math]))

(let [counter* (atom 0)]
  (defn get-slider-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "slider-%d" n))))))


(defn set-slider-value! 
  "(set-slider-value! obj v)
   (set-slider-value! obj v render?)

    Sets value of slider

    obj     - SgwrComponent group containing slider
    v       - float, the new slider value
    render? - boolean, if true render drawing containing the slider, 
              default true

    Returns actual value slider was set to which may be different 
    from v argument."
  ([obj v](set-slider-value! obj v :render))
  ([obj v render?]
   (let [vertical? (= (.get-property obj :orientation) :vertical)
         cs (.coordinate-system obj)
         vhook (.get-property obj :fn-value-hook)
         val (vhook v)
         mapfn (.get-property obj :fn-val->pos)
         invmap (.get-property obj :fn-pos->val)
         handle (.get-property obj :handle)
         track1 (.get-property obj :track1)
         track2 (.get-property obj :track2)
         track3 (.get-property obj :track3)
         [p0 p1](.points track1)]
     (if vertical?
       (let [pos (.inv-map cs [(first p0)(mapfn val)])]
         (.set-points! handle [pos])
         (.set-points! track2 [p0 pos])
         (.set-points! track3 [pos p1])
         (.put-property! obj :value (invmap (second pos))))
       (let [pos (.inv-map cs [(mapfn val)(second p0)])]
         (.set-points! handle [pos])
         (.set-points! track2 [p0 pos])
         (.set-points! track3 [pos p1])
         (.put-property! obj :value (invmap (first pos)))))
      (if render? (.render (.get-property obj :drawing)))
      (.get-property obj :value))))

(defn get-slider-value [obj]
  "(get-slider-value obj)
   Returns value of slider"
  (.get-property obj :value))


(defn- dummy-action [& _] nil)

(defn- compose-action [afn]
  (fn [slider ev]
    (let [flag (.local-property slider :enabled)]
      (if flag (afn slider ev)))))

(defn- compose-drag-action [dfn]
   (fn [slider ev]
     (if (.local-property slider :enabled)
       (let [vertical? (= (.get-property slider :orientation) :vertical)
             cs (.coordinate-system slider)
             vhook (.get-property slider :fn-value-hook)
             mapfn (.get-property slider :fn-pos->val)
             handle (.get-property slider :handle)
             track1 (.get-property slider :track1)
             track2 (.get-property slider :track2)
             track3 (.get-property slider :track3)
             [p0 p1](.points track1)
             pos (.inv-map cs [(.getX ev)(.getY ev)])]
         (if vertical?
           (let [x (first p0)
                 param (second pos)
                 val (vhook (mapfn param))]
             (.set-points! handle [[x (second pos)]])
             (.set-points! track2 [p0 [x (second pos)]])
             (.set-points! track3 [[x (second pos)] p1])
             (.put-property! slider :value val))
           (let [y (second p0)
                 param (first pos)
                 val (vhook (mapfn param))]
             (.set-points! handle [[(first pos) y]])
             (.set-points! track2 [p0 [(first pos) y]])
             (.set-points! track3 [[(first pos) y] p1])
             (.put-property! slider :value val)))
         (dfn slider ev)
         (.render (.get-property slider :drawing))))))

(defn- compose-release-action [rfn]
  (fn [slider ev]
    (if (.local-property slider :enabled)
      (let [vertical? (= (.get-property slider :orientation) :vertical)
            cs (.coordinate-system slider)
            val (.get-property slider :value)
            mapfn (.get-property slider :fn-val->pos)
            handle (.get-property slider :handle)
            track1 (.get-property slider :track1)
            track2 (.get-property slider :track2)
            track3 (.get-property slider :track3)
            [p0 p1](.points track1)]
        (if vertical?
          (let [pos (.inv-map cs [(first p0)(mapfn val)])]
            (.set-points! handle [pos])
            (.set-points! track2 [p0 pos])
            (.set-points! track3 [pos p1]))
          (let [pos (.inv-map cs [(mapfn val)(second p0)])]
            (.set-points! handle [pos])
            (.set-points! track2 [p0 pos])
            (.set-points! track3 [pos p1])))
        (rfn slider ev)
        (.render (.get-property slider :drawing))))))

;; track1 - fixed background
;; track2 - from botom/left to current handle position
;; track3 - from top/right to current handle position
;;
(defn slider [parent p0 length v0 v1 & {:keys [id orientation
                                               drag-action move-action enter-action exit-action
                                               press-action release-action click-action
                                               value-hook
                                               track1-color track1-style track1-width
                                               track2-color track2-style track2-width
                                               track3-color track3-style track3-width
                                               gap
                                               pad-color 
                                               rim-color rim-style rim-width rim-radius
                                               handle-color handle-style handle-size]
                                        :or {id nil
                                             orientation :vertical
                                             drag-action dummy-action
                                             move-action dummy-action
                                             enter-action dummy-action
                                             exit-action dummy-action
                                             press-action dummy-action
                                             release-action dummy-action
                                             click-action dummy-action
                                             value-hook identity
                                             track1-color :gray
                                             track1-style :solid
                                             track1-width 1.0
                                             track2-color :yellow
                                             track2-style :solid
                                             track2-width 1.0
                                             track3-color [0 0 0 0]
                                             track3-style :solid
                                             track3-width 1.0
                                             gap 8
                                             pad-color [0 0 0 0]
                                             rim-color :gray
                                             rim-style :solid
                                             rim-width 1.0
                                             rim-radius 12
                                             handle-color :white
                                             handle-style [:fill :dot]
                                             handle-size 4}}]
  "(slider parent p0 length v0 v1 
       :id :orientation
       :drag-action :move-action :enter-action :exit-action
       :press-action :release-action :click-action
       :value-hook
       :track1-color :track1-style :track1-width
       :track2-color :track2-style :track2-width
       :track3-color :track3-style :track3-width
       :gap :pad-color
       :rim-color :rim-style :rim-width :rim-radius
       :handle-color :handle-style :handle-size)

    parent  - SgwrComponent, the parent group
    p0      - vector [x y] bottom or left hand position of slider
    length  - float, length of slider
    v0      - float, minimum value
    v1      - float, maximum value
    :id     - keyword, if not specified id will be generated automatically    
    :orientation - keyword, either :vertical or :horizontal, default :vertical
    :value-hook  - function applied to value when slider is updated.
                   (fn [v] ...) v - float, returns float, default identity
   Actions

      :drag-action, :move-action, :enter-action, :exit-action,
      :press-action, :release-action, :click-action
       
       Function of form (fn [slider ev] ...) where slider is this tool
       and ev is an instance of java.awt.event.MouseEvent 

   Tracks

    There are three tracks, 
    track1 is a static line background between v0 and v1
    track2 is a dynamic line between v0 and the current low value
    track3 is a dynamic line between current high value and v1

    :track(i)-color - Color, keyword, vector, see sgwr.util.color
    :track(i)-style - int or keyword, line dash pattern, default :solid
    :track(i)-width - float, line width, default 1.0

   :gap       - float, space between track and outer rim
   :pad-color - Color, keyword or vector, background color
   :rim-color  - Outer rim color
   :rim-style  - Rim dash pattern, default :solid
   :rim-width  - Rim line width, default 1.0
   :rim-radius - int, Rim|pad corner radius, default 12

   :handle-color - Color, keyword or vector
   :handle-style - vector, sets handle shape, see sgwr.components.point
   :handle-size  - float, handle point size

   Returns SgwrComponent group containing slider components"
  (let [vertical? (= orientation :vertical)
        [x0 y0] p0
        [x1 y1] (if vertical? [x0 (- y0 length)] [(+ x0 length) y0]) 
        [x2 y2 
         x3 y3 
         pos->val val->pos]  (if vertical?
                               [(- x0 gap)(+ y0 gap)
                                (+ x0 gap)(- y1 gap)
                                (math/clipped-linear-function y0 v0 y1 v1)
                                (math/clipped-linear-function v0 y0 v1 y1)]
                               [(- x0 gap)(+ y0 gap)
                                (+ x1 gap)(- y0 gap)
                                (math/clipped-linear-function x0 v0 x1 v1)
                                (math/clipped-linear-function v0 x0 v1 x1)])
         grp (group/group parent :etype :slider :id (get-slider-id id))
         pad (let [pad (rect/rectangle grp [x2 y2][x3 y3] 
                                       :id :pad
                                       :color pad-color
                                       :fill true)]
               (.put-property! pad :corner-radius rim-radius)
               (.color! pad :rollover pad-color)
               pad)
         rim (let [rim (rect/rectangle grp [x2 y2][x3 y3]
                                       :id :rim
                                       :color rim-color
                                       :style rim-style
                                       :width rim-width
                                       :fill :no)]
               (.put-property! rim :corner-radius rim-radius)
               rim)
         track1 (let [t1 (line/line grp [x0 y0][x1 y1] 
                                    :id :track1
                                    :color track1-color
                                    :style track1-style
                                    :width track1-width)]
                  (.color! t1 :rollover track1-color)
                  (.hide! t1 :default :no)
                  (.fill! t1 :default :no)
                  t1)
         track2 (let [t2 (line/line grp [x0 y0][x0 y0]
                                    :id :track2
                                    :color track2-color
                                    :style track2-style
                                    :width track2-width)]
                  (.color! t2 :rollover track2-color)
                  (.hide! t2 :default :no)
                  (.fill! t2 :default :no)
                  t2)
         track3 (let [t3 (line/line grp [x0 y0][x1 y1]
                                    :id :track3
                                    :color track3-color
                                    :style track3-style
                                    :width track3-width)]
                  (.color! t3 :rollover track3-color)
                  (.hide! t3 :default :no)
                  (.fill! t3 :default :no)
                  t3)
         handle (let [hand (point/point grp [x0 y0]
                                        :id :handle
                                        :color handle-color
                                        :style handle-style
                                        :size handle-size)]
                  hand)
        occluder (let [occ (rect/rectangle grp [x2 (+ y2 3)][(+ x3 3)(- y3 3)]
                                           :id :occluder
                                           :color [0 0 0 0]
                                           :fill true)]
                   (.color! occ :enabled [0 0 0 0])
                   (.color! occ :rollover [0 0 0 0])
                   (.color! occ :disabled (uc/transparent :black 190))
                   occ)]
    (.put-property! grp :orientation orientation)
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :track1 track1)
    (.put-property! grp :track2 track2)
    (.put-property! grp :track3 track3)
    (.put-property! grp :handle handle)
    (.put-property! grp :occluder occluder)
    (.put-property! grp :action-mouse-dragged  (compose-drag-action drag-action))
    (.put-property! grp :action-mouse-moved    (compose-action move-action)) 
    (.put-property! grp :action-mouse-entered  (compose-action enter-action)) 
    (.put-property! grp :action-mouse-exited   (compose-action exit-action)) 
    (.put-property! grp :action-mouse-pressed  (compose-action press-action)) 
    (.put-property! grp :action-mouse-released (compose-release-action release-action))
    (.put-property! grp :action-mouse-clicked  (compose-action click-action))
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :fn-value-hook value-hook)
    (.use-attributes! grp :default)
    (.use-attributes! occluder :enabled)
    grp))
