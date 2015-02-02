(ns sgwr.widgets.dual-slider
  "Defines a slider with 2 heads, useful for setting value ranges"
  (:require [sgwr.constants :as constants])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math]))

(let [counter* (atom 0)]
  (defn get-slider-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "dual-slider-%d" n))))))

(defn set-dual-slider-values! 
  "(set-dual-slider-value! obj v1 v2 render?)
   (set-dual-slider-value! obj v1 v2)

   obj - SgwrElement, group containing dual-slider
   v1  - float, minimum value 
   v2  - float, maximum value
   render?  - boolean if true render drawing containing slider, default true
   Returns vector [v1 v2]"
  ([obj v1 v2](set-dual-slider-values! obj v1 v2 :render))
  ([obj v1 v2 render?]
   (let [vertical? (= (.get-property obj :orientation) :vertical)
         cs (.coordinate-system obj)
         vhook (.get-property obj :fn-value-hook)
         val1 (min (vhook v1)(vhook v2))
         val2 (max (vhook v1)(vhook v2))
         mapfn (.get-property obj :fn-val->pos)
         h1 (.get-property obj :handle1)
         h2 (.get-property obj :handle2)
         track1 (.get-property obj :track1)
         track2 (.get-property obj :track2)
         track3 (.get-property obj :track3)
         track4 (.get-property obj :track4)
         [p0 p1](.points track1)]
     (if vertical?
       (let [posa (.inv-map cs [(first p0)(mapfn val1)])
             posb (.inv-map cs [(first p0)(mapfn val2)])
             [x y0] p0
             y1 (second p1)
             y2 (second posa)
             y3 (second posb)]
         (.set-points! h1 [[x y2]])
         (.set-points! h2 [[x y3]])
         (.set-points! track2 [[x y0][x y2]])
         (.set-points! track3 [[x y1][x y3]])
         (.set-points! track4 [[x y2][x y3]]))
       (let [posa (.inv-map cs [(mapfn val1)(second p0)])
             posb (.inv-map cs [(mapfn val2)(second p0)])
             [x0 y] p0
             x1 (first p1)
             x2 (first posa)
             x3 (first posb)]
         (.set-points! h1 [[x2 y]])
         (.set-points! h2 [[x3 y]])
         (.set-points! track2 [[x0 y][x2 y]])
         (.set-points! track3 [[x1 y][x3 y]])
         (.set-points! track4 [[x2 y][x3 y]])))
     (.put-property! h1 :value val1)
     (.put-property! h2 :value val2)
     (.put-property! obj :values [val1 val2])
     (if render? (.render (.get-property obj :drawing)))
     [val1 val2])))

(defn get-dual-slider-values [obj]
  "(get-dual-slider-value obj)
   obj - SgwrElement, the group containing the slider
   Returns current slider value as vector [v1 v2] where 
   v1 <= v2"
  (.get-property obj :values))

(defn- dummy-action [& _] nil)

(defn- select-handle [obj ev]
  (let [cs (.coordinate-system obj)
        pos (.inv-map cs [(.getX ev)(.getY ev)])
        h1 (.get-property obj :handle1)
        h2 (.get-property obj :handle2)
        d1 (.distance h1 pos)
        d2 (.distance h2 pos)
        current* (atom nil)
        inactive* (atom nil)]
    (if (< d1 d2)
      (do 
        (reset! current* h1)
        (reset! inactive* h2))
      (do 
        (reset! current* h2)
        (reset! inactive* h1)))
    (.put-property! obj :current-handle @current*)
    (.put-property! @current* :color (.get-property obj :current-handle-color))
    (.put-property! @current* :style (.get-property obj :current-handle-style))
    (.put-property! @current* :size (.get-property obj :current-handle-size))
    (.use-attributes! @inactive* :default)
    @current*))

(defn- compose-enter-action [efn]
  (fn [obj ev]
    (select-handle obj ev)
    (efn obj ev)
    (.render (.get-property obj :drawing))))

(defn- compose-move-action [mfn]
  (fn [obj ev]
    (select-handle obj ev)
    (mfn obj ev)
    (.render (.get-property obj :drawing))))

(defn- compose-drag-action [dfn]
  (fn [obj ev]
    (let [vertical? (= (.get-property obj :orientation) :vertical)
          cs (.coordinate-system obj)
          vhook (.get-property obj :fn-value-hook)
          mapfn (.get-property obj :fn-pos->val)
          invmap (.get-property obj :fn-val->pos)
          handle (.get-property obj :current-handle)
          track1 (.get-property obj :track1)
          track2 (.get-property obj :track2)
          track3 (.get-property obj :track3)
          track4 (.get-property obj :track4)
          [p0 p1](.points track1)
          minmax (if (= handle (.get-property obj :handle1))
                   [(first (.get-property obj :minmax))
                    (.get-property (.get-property obj :handle2) :value)]
                   [(.get-property (.get-property obj :handle1) :value)
                    (second (.get-property obj :minmax))])]
      (if handle
        (let [clamp (fn [x](min (second minmax)(max (first minmax) x)))
              pos (.inv-map cs [(.getX ev)(.getY ev)])]
          (if vertical?
            (let [x (first p0)
                  param (second pos)
                  val (clamp (vhook (mapfn param)))]
              (.set-points! handle [[x (invmap val)]])
              (.put-property! handle :value val))
            (let [y (second p0)
                  param (first pos)
                  val (clamp (vhook (mapfn param)))]
              (.set-points! handle [[(invmap val) y]])
              (.put-property! handle :value val)))
          (let [h1 (.get-property obj :handle1)
                h2 (.get-property obj :handle2)
                ph1 (first (.points h1))
                ph2 (first (.points h2))
                v1 (.get-property h1 :value)
                v2 (.get-property h2 :value)]
            (.put-property! obj :values [v1 v2])
            (if vertical?
              (let [[x  y0] p0
                    y1 (second p1)
                    y2 (min (second ph1)(second ph2))
                    y3 (max (second ph1)(second ph2))]
                (.set-points! track2 [[x y0][x y2]])
                (.set-points! track3 [[x y1][x y3]])
                (.set-points! track4 [[x y2][x y3]]))
              (let [[x0 y] p0
                    x1 (first p1)
                    x2 (min (first ph1)(first ph2))
                    x3 (max (first ph1)(first ph2))]
                (.set-points! track2 [[x0 y][x2 y]])
                (.set-points! track3 [[x1 y][x3 y]])
                (.set-points! track4 [[x2 y][x3 y]]))))
          (dfn obj ev)
          (.render (.get-property obj :drawing)))) )))

(defn- compose-release-action [rfn]
  (fn [obj ev]
    (let [vertical? (= (.get-property obj :orientation) :vertical)
          cs (.coordinate-system obj)
          [v1 v2](.get-property obj :values)
          mapfn (.get-property obj :fn-val->pos)
          handle1 (.get-property obj :handle1)
          handle2 (.get-property obj :handle2)
          track1 (.get-property obj :track1)
          track2 (.get-property obj :track2)
          track3 (.get-property obj :track3)
          track4 (.get-property obj :track4)
          [p0 p1](.points track1)
          [x0 y0] p0
          [x1 y1] p1]
      (if vertical?
        (let [y2 (mapfn v1)
              y3 (mapfn v2)]
          (.set-points! handle1 [[x0 y2]])
          (.set-points! handle2 [[x0 y3]])
          (.set-points! track2 [p0 [x0 y2]])
          (.set-points! track3 [[x0 y3] p1])
          (.set-points! track4 [[x0 y3][x0 y2]]))
        (let [x2 (mapfn v1)
              x3 (mapfn v2)]
          (.set-points! handle1 [[x2 y0]])
          (.set-points! handle2 [[x3 y0]])
          (.set-points! track2 [p0 [x2 y0]])
          (.set-points! track3 [[x3 y0] p1])
          (.set-points! track4 [[x3 y0][x2 y0]])))
      (rfn obj ev)
      (.render (.get-property obj :drawing)))))
        
(defn- compose-exit-action [efn]
  (fn [obj ev]
    (.use-attributes! obj :default)
    (.put-property! obj :current-handle nil)
    (efn obj ev)
    (.render (.get-property obj :drawing))))

;; track1 - fixed background
;; track2 - from bottom|left to current bottom|left handle position
;; track3 - from top|right to current top|right handle position
;; track4 - track between two handles
;;
(defn dual-slider [parent p0 length v0 v1 & {:keys [id orientation
                                                    drag-action move-action enter-action exit-action
                                                    press-action release-action click-action
                                                    value-hook
                                                    track1-color track1-style track1-width
                                                    track2-color track2-style track2-width
                                                    track3-color track3-style track3-width
                                                    track4-color track4-style track4-width
                                                    gap
                                                    pad-color 
                                                    rim-color rim-style rim-width rim-radius
                                                    handle1-color handle1-style handle1-size 
                                                    handle2-color handle2-style handle2-size 
                                                    current-handle-color current-handle-style current-handle-size]
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
                                                  track2-color [0 0 0 0]
                                                  track2-style :solid
                                                  track2-width 1.0
                                                  track3-color [0 0 0 0]
                                                  track3-style :solid
                                                  track3-width 1.0
                                                  track4-color :yellow
                                                  track4-style :solid
                                                  track4-width 1.0
                                                  gap 8
                                                  pad-color [0 0 0 0]
                                                  rim-color :gray
                                                  rim-style :solid
                                                  rim-width 1.0
                                                  rim-radius 12
                                                  handle1-color :white
                                                  handle1-style nil
                                                  handle1-size 3
                                                  handle2-color :white
                                                  handle2-style nil
                                                  handle2-size 3
                                                  current-handle-color :white
                                                  current-handle-style [:dot]
                                                  current-handle-size 3}}]
  "(dual-slider parent p0 length v0 v1
         :id :orientation
         :drag-action :move-action :enter-action :exit-action 
         :press-action :release-action :click-action 
         :value-hook
         :track1-color :track1-style :track1-width
         :track2-color :track2-style :track2-width
         :track3-color :track3-style :track3-width
         :track4-color :track4-style :track4-width
         :handle1-color :handle1-style :handle1-size 
         :handle2-color :handle2-style :handle2-size 
         :current-handle-color :current-handle-style :current-handle-size
         :gap :pad-color
         :rim-color :rim-style :rim-width :rim-radius)

    Creates dual-slider 

    parent  - SgwrElement, the parent group
    p0      - vector [x y] bottom or left hand position of slider
    length  - float, length of slider
    v0      - float, minimum value
    v1      - float, maximum value
    :id     - keyword, if not specified id will be generated automatically    
    :orientation - keyword, either :vertical or :horizontal, default :vertical
    :value-hook  - function applied to value when slider is updated.
                   (fn [v] ...) v - float, returns float, default identity
    Tracks

    There are four tracks, 
    track1 is a static line background between v0 and v1
    track2 is a dynamic line between v0 and the current low value
    track3 is a dynamic line between current high value and v1
    track4 is a dynamic line between current low and high values

    :track(i)-color - Color, keyword, vector, see sgwr.util.color
    :track(i)-style - int or keyword, line dash pattern, default :solid
    :track(i)-width - float, line width, default 1.0

    Handles

    There are two handle which set|display current low and high values.
    The handles are implemented by point elements.

    :handle(i)-color - Color, keyword or vector
    :handle(i)-style - vector, sets handle shape, see sgwr.elements.point
    :handle(i)-size  - float, handle point size

   :current-handle-color -   Sets color, style and size
   :current-handle-style -   of active handle
   :current-handle-size  - 

   :gap       - float, space between track and outer rim
   :pad-color - Color, keyword or vector, background color
   :rim-color  - Outer rim color
   :rim-style  - Rim dash pattern, default :solid
   :rim-width  - Rim line width, default 1.0
   :rim-radius - int, Rim|pad corner radius, default 12

   Actions

   :drag-action, :move-action, :enter-action, :exit-action,
   :press-action, :release-action, :click-action
       
   Function of form (fn [obj ev] ...) where obj is this widget
   and ev is an instance of java.awt.event.MouseEvent 

   Returns SgwrElement group"
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
         grp (group/group parent :etype :dual-slider :id (get-slider-id id))
         pad (let [pad (rect/rectangle grp [x2 y2][x3 y3] 
                                       :id :pad
                                       :color (uc/color pad-color)
                                       :fill true)]
               (.put-property! pad :corner-radius rim-radius)
               (.color! pad :rollover (uc/color pad-color))
               pad)
         rim (let [rim (rect/rectangle grp [x2 y2][x3 y3]
                                       :id :rim
                                       :color (uc/color rim-color)
                                       :style rim-style
                                       :width rim-width
                                       :fill :no)]
               (.put-property! rim :corner-radius rim-radius)
               rim)
         track1 (let [t1 (line/line grp [x0 y0][x1 y1] 
                                    :id :track1
                                    :color (uc/color track1-color)
                                    :style track1-style
                                    :width track1-width)]
                  (.color! t1 :rollover (uc/color track1-color))
                  (.hide! t1 :default :no)
                  (.fill! t1 :default :no)
                  t1)
         track2 (let [t2 (line/line grp [x0 y0][x0 y0]
                                    :id :track2
                                    :color (uc/color track2-color)
                                    :style track2-style
                                    :width track2-width)]
                  (.color! t2 :rollover (uc/color track2-color))
                  (.hide! t2 :default :no)
                  (.fill! t2 :default :no)
                  t2)
         track3 (let [t3 (line/line grp [x1 y1][x1 y1]
                                    :id :track3
                                    :color (uc/color track3-color)
                                    :style track3-style
                                    :width track3-width)]
                  (.color! t3 :rollover (uc/color track3-color))
                  (.hide! t3 :default :no)
                  (.fill! t3 :default :no)
                  t3)
        track4 (let [t4 (line/line grp [x0 y0][x1 y1]
                                    :id :track4
                                    :color (uc/color track4-color)
                                    :style track4-style
                                    :width track4-width)]
                  (.color! t4 :rollover (uc/color track4-color))
                  (.hide! t4 :default :no)
                  (.fill! t4 :default :no)
                  t4)
        handle1 (let [hand (point/point grp [x0 y0]
                                        :id :handle1
                                        :color (uc/color handle1-color)
                                        :style (or handle1-style 
                                                   (if (= orientation :vertical)
                                                     [:edge-w :edge-s :edge-e]
                                                     [:edge-n :edge-w :edge-s]))
                                        :size handle1-size)]
                  (.put-property! hand :value v0)
                  hand)
        handle2 (let [hand (point/point grp [x1 y1]
                                        :id :handle2
                                        :color (uc/color handle2-color)
                                        :style (or handle2-style
                                                   (if (= orientation :vertical)
                                                     [:edge-w :edge-n :edge-e]
                                                     [:edge-n :edge-e :edge-s]))
                                        :size handle2-size)]
                  (.put-property! hand :value v1)
                  hand)]
    (.put-property! grp :orientation orientation)
    (.put-property! grp :pad pad)
    (.put-property! grp :rim rim)
    (.put-property! grp :track1 track1)
    (.put-property! grp :track2 track2)
    (.put-property! grp :track3 track3)
    (.put-property! grp :track4 track4)
    (.put-property! grp :handle1 handle1)
    (.put-property! grp :handle2 handle2)
    (.put-property! grp :current-handle nil)
    (.put-property! grp :current-handle-color (uc/color current-handle-color))
    (.put-property! grp :current-handle-style (apply point/style-fn current-handle-style))
    (.put-property! grp :current-handle-size current-handle-size)
    (.put-property! grp :action-mouse-dragged  (compose-drag-action (or drag-action dummy-action)))
    (.put-property! grp :action-mouse-moved    (compose-move-action (or move-action dummy-action)))
    (.put-property! grp :action-mouse-entered  (compose-enter-action (or enter-action dummy-action)))
    (.put-property! grp :action-mouse-exited   (compose-exit-action (or exit-action dummy-action)))
    (.put-property! grp :action-mouse-pressed  (or press-action dummy-action))
    (.put-property! grp :action-mouse-released (compose-release-action (or release-action dummy-action)))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :fn-value-hook value-hook)
    (.put-property! grp :minmax [(min v0 v1)(max v0 v1)])
    (.put-property! grp :values [v0 v1])
    (.use-attributes! grp :default)
    grp))

