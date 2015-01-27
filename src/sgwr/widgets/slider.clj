(ns sgwr.widgets.slider
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.rule :as rule])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math]))

(let [counter* (atom 0)]
  (defn get-slider-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "slider-%d" n))))))

(defn- dummy-action [& _] nil)

(defn set-slider-value! 
  "(set-slider-value! obj v)
   (set-slider-value! obj v render?)

    Sets value of slider

    obj     - SgwrElement group containing slider
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

(defn- compose-drag-action [dfn]
   (fn [obj ev]
     (let [vertical? (= (.get-property obj :orientation) :vertical)
           cs (.coordinate-system obj)
           vhook (.get-property obj :fn-value-hook)
           mapfn (.get-property obj :fn-pos->val)
           handle (.get-property obj :handle)
           track1 (.get-property obj :track1)
           track2 (.get-property obj :track2)
           track3 (.get-property obj :track3)
           [p0 p1](.points track1)
           pos (.inv-map cs [(.getX ev)(.getY ev)])]
       (if vertical?
         (let [x (first p0)
               param (second pos)
               val (vhook (mapfn param))]
           (.set-points! handle [[x (second pos)]])
           (.set-points! track2 [p0 [x (second pos)]])
           (.set-points! track3 [[x (second pos)] p1])
           (.put-property! obj :value val))
         (let [y (second p0)
               param (first pos)
               val (vhook (mapfn param))]
           (.set-points! handle [[(first pos) y]])
           (.set-points! track2 [p0 [(first pos) y]])
           (.set-points! track3 [[(first pos) y] p1])
           (.put-property! obj :value val)))
       (dfn obj ev)
       (.render (.get-property obj :drawing)))))

(defn- compose-release-action [rfn]
  (fn [obj ev]
    (let [vertical? (= (.get-property obj :orientation) :vertical)
          cs (.coordinate-system obj)
          val (.get-property obj :value)
          mapfn (.get-property obj :fn-val->pos)
          handle (.get-property obj :handle)
          track1 (.get-property obj :track1)
          track2 (.get-property obj :track2)
          track3 (.get-property obj :track3)
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
      (rfn obj ev)
      (.render (.get-property obj :drawing)))))

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
                                             drag-action nil
                                             move-action nil
                                             enter-action nil
                                             exit-action nil
                                             press-action nil
                                             release-action nil
                                             click-action nil
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
                                             handle-size 3}}]
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

    parent  - SgwrElement, the parent group
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
       
       Function of form (fn [obj ev] ...) where obj is this widget
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
   :handle-style - vector, sets handle shape, see sgwr.elements.point
   :handle-size  - float, handle point size

   Returns SgwrElement group containing slider components"
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
                  hand)]
    (.put-property! grp :orientation orientation)
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :track1 track1)
    (.put-property! grp :track2 track2)
    (.put-property! grp :track3 track3)
    (.put-property! grp :handle handle)
    (.put-property! grp :action-mouse-dragged  (compose-drag-action (or drag-action dummy-action)))
    (.put-property! grp :action-mouse-moved    (or move-action dummy-action))
    (.put-property! grp :action-mouse-entered  (or enter-action dummy-action))
    (.put-property! grp :action-mouse-exited   (or exit-action dummy-action))
    (.put-property! grp :action-mouse-pressed  (or press-action dummy-action))
    (.put-property! grp :action-mouse-released (compose-release-action (or release-action dummy-action)))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :fn-value-hook value-hook)
    (.use-attributes! grp :default)
    grp))

(defn slider-rule [drawing p0 length v0 v1 & {:keys [id orientation
                                                     drag-action move-action enter-action exit-action
                                                     press-action release-action click-action
                                                     value-hook
                                                     track1 track2 track3
                                                     major minor
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
                                                   track1 [:gray :solid 1.0]
                                                   track2 [[128 0 255 64] :solid 6.0]
                                                   track3 [[0 0 0 0] :solid 1.0]
                                                   major [50 8 :gray 0]
                                                   minor [10 6 [128 32 32] 0]
                                                   gap 12
                                                   pad-color [12 12 12 54]
                                                   rim-color :gray
                                                   rim-style  0
                                                   rim-width 1.0
                                                   rim-radius 12
                                                   handle-color :white
                                                   handle-style [:dot :fill]
                                                   handle-size 3}}]
  "(slider-rule drawing p0 length 
       :id :orientation
       :drag-action :move-action :enter-action :exit-action
       :press-action :release-action :click-action
       :value-hook
       :track1-color :track1-style :track1-width
       :track2-color :track2-style :track2-width
       :track3-color :track3-style :track3-width
       :gap :pad-color
       :rim-color :rim-style :rim-width :rim-radius
       :handle-color :handle-style :handle-size
       :major :minor)

   Creates slider with integrated ruler, see sgwr.elements.rule

   Arguments are identical to slider with exception that the first argument 
   should be a drawing The slider components are placed into the drawing's
   widget group while the ruler components are placed in the drawing's root 
   group.

   The two optional arguments :minor and :major define ruler tick marks
   and have the vector form [steps length color offset] where
       steps  - float, interval between tick marks
       length - float, length of tick marks
       color  - Color, keyword or vector, 
       offset - float, value added to tick mark position to shift them 
                away from the center.
   Returns SgwrElement group holding slider components."
  (let [root (.root drawing)
        widgets (.widget-root drawing)
        rule (let [[c sty w] track1
                   r (rule/ruler root p0 length 
                                 :orientation orientation
                                 :track-color c
                                 :track-style sty
                                 :track-width w
                                 :gap gap
                                 :pad-color pad-color
                                 :rim-color [0 0 0 0]
                                 :rim-radius rim-radius)]
                   r)
       slide (let [[c2 sty2 w2] track2
                   [c3 sty3 w3] track3
                   s (slider widgets p0 length v0 v1
                             :id id
                             :orientation orientation
                             :drag-action drag-action
                             :move-action move-action
                             :enter-action enter-action
                             :exit-action exit-action
                             :press-action press-action
                             :release-action release-action
                             :click-action click-action
                             :value-hook value-hook
                             :track1-color [0 0 0 0]
                             :track2-color c2
                             :track2-style sty2
                             :track2-width w2
                             :track3-color c3
                             :track3-style sty3
                             :track3-width w3
                             :gap gap
                             :pad-color [0 0 0 0]
                             :rim-color rim-color
                             :rim-style rim-style
                             :rim-width rim-width
                             :rim-radius rim-radius
                             :handle-color handle-color
                             :handle-style handle-style
                             :handle-size handle-size)]
               s)]
    (if (first minor)
      (let [[step length color offset] minor]
        (rule/ticks rule step 
                    :length (or length 6) 
                    :color (uc/color (or color :gray))
                    :offset (or offset 0))))
    (if (first major)
      (let [[step length color offset] major]
        (rule/ticks rule step 
                    :length (or length 8)
                    :color (uc/color (or color :gray))
                    :offset (or offset 0))))
    slide))
          



