(ns sgwr.widgets.slider
  (:require [sgwr.elements.element])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
)

(def set-attributes! sgwr.elements.element/set-attributes!)


(defn get-dragged-action 
  ([](get-dragged-action (fn [& _])))
  ([dfn]
   (fn [obj ev]
     (let [orientation (.get-property obj :orientation)
           cs (.coordinate-system obj)
           handle (.get-property obj :handle)
           track1 (.get-property obj :track1)
           [p0 p1](.points track1)
           track2 (.get-property obj :track2)
           track3 (.get-property obj :track3)
           pos (.inv-map cs [(.getX ev)(.getY ev)])]
       (if (= orientation :vertical)
         (let [x (first p0)
               val ((.get-property obj :fn-pos->val) (second pos))]
           (.set-points! handle [[x (second pos)]])
           (.put-property! obj :value (float val))
           (if track2 (.set-points! track2 [p0 [x (second pos)]]))
           (if track3 (.set-points! track3 [[x (second pos)] p1])))
         (let [y (second p0)            ; horizontal
               val ((.get-property obj :fn-pos->val)(first pos))]
           (.set-points! handle [[(first pos) y]])
           (.put-property! obj :value (float val))
           (if track2 (.set-points! track2 [p0 [(first pos) y]]))
           (if track3 (.set-points! track3 [[(first pos) y] p1]))))
       (dfn obj ev)
       (.render (.get-property obj :drawing))))))

(defn set-slider-value! 
  ([sobj val](set-slider-value! sobj val true))
  ([sobj val render]
   (let [ufn (.get-property sobj :fn-set-value)]
     (ufn val)
     (if render (.render (.get-property sobj :drawing))))))

(defn vslider [parent p0 p1 v0 v1 id & {:keys [track1-color track1-style track1-width
                                                track2-color track2-style track2-width
                                                track3-color track3-style track3-width
                                                box-color box-gap box-style box-width fill-box? box-radius
                                                handle-color handle-style handle-size]
                                        :or {track1-color :gray
                                             track1-style :solid
                                             track1-width 1.0
                                             track2-color :yellow
                                             track2-style :solid
                                             track2-width 1.0
                                             track3-color :green
                                             track3-style :solid
                                             track3-width 1.0
                                             box-color :gray
                                             box-gap 6
                                             box-style :solid
                                             box-width 1.0
                                             fill-box? false
                                             box-radius 0
                                             handle-color :white
                                             handle-style :dot
                                             handle-size 2}}]
  (let [[x0 y0] p0
        [x1 y1] p1
        x x1
        pos->val (math/clipped-linear-function y0 v0 y1 v1)
        val->pos (math/clipped-linear-function v0 y0 v1 y1)
        grp (group/group parent :etype :slider :id id)]

      (let [x2 (- x box-gap)
            x3 (+ x box-gap)
            y2 (- (min y0 y1) box-gap)
            y3 (+ (max y0 y1) box-gap)
            box (rect/rectangle grp [x2 y2][x3 y3]
                                :id :box
                                :color (uc/color box-color)
                                :style box-style
                                :width box-width
                                :fill fill-box?)]
            (.put-property! box :corner-radius box-radius)
            (.put-property! grp :box box)
            (.hide! box (not box-color)))

    (let [track1 (line/line grp [x y0][x y1]
                             :id :track1
                             :color (uc/color track1-color)
                             :style track1-style
                             :width track1-width)]
      (.put-property! grp :track1 track1))
    (if track2-color
      (let [c (uc/color track2-color)
            track2 (line/line grp [x y0][x y0]
                              :id :track2
                              :color c
                              :style track2-style
                              :width track2-width)]
        (set-attributes! track2 :rollover :color c :style track2-style :width track2-width)
        (.put-property! grp :track2 track2)))
    (if track3-color
      (let [c (uc/color track3-color)
            track3 (line/line grp [x y0][x y1]
                              :id :track3
                              :color c
                              :style track3-style
                              :width track3-width)]
        (set-attributes! track3 :rollover :color c :style track3-style :width track3-width)
        (.put-property! grp :track3 track3)))
    (let [handle (point/point grp [x y0]
                              :id :handle
                              :color (uc/color handle-color)
                              :style handle-style
                              :size handle-size)]
      (.put-property! grp :handle handle))
    (.put-property! grp :value v0)
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :orientation :vertical)
    (.put-property! grp :fn-set-value (fn [v]
                                        (let [v2 (math/clamp v (min v0 v1)(max v0 v1))
                                              pos (val->pos v2)
                                              handle (.get-property grp :handle)
                                              trk2 (.get-property grp :track2)
                                              trk3 (.get-property grp :track3)]
                                          (.put-property! grp :value v2)
                                          (.set-points! handle [[x pos]])
                                          (if trk2 (.set-points! trk2 [p0 [x pos]]))
                                          (if trk3 (.set-points! trk3 [[x pos] p1])))))
    (.put-property! grp :action-mouse-dragged  (get-dragged-action))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (fn [obj ev] ))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (.use-attributes! grp :default)
    grp))
                                             
(defn hslider [parent p0 p1 v0 v1 id & {:keys [track1-color track1-style track1-width
                                                track2-color track2-style track2-width
                                                track3-color track3-style track3-width
                                                box-color box-gap box-style box-width fill-box? box-radius
                                                handle-color handle-style handle-size]
                                        :or {track1-color :gray
                                             track1-style :solid
                                             track1-width 1.0
                                             track2-color :yellow
                                             track2-style :solid
                                             track2-width 1.0
                                             track3-color :green
                                             track3-style :solid
                                             track3-width 1.0
                                             box-color :gray
                                             box-gap 6
                                             box-style :solid
                                             box-width 1.0
                                             fill-box? false
                                             box-radius 0
                                             handle-color :white
                                             handle-style :dot
                                             handle-size 2}}]
  (let [[x0 y0] p0
        [x1 y1] p1
        y y0
        pos->val (math/clipped-linear-function x0 v0 x1 v1)
        val->pos (math/clipped-linear-function v0 x0 v1 x1)
        grp (group/group parent :etype :slider :id id)]

    (let [x2 (- (min x0 x1) box-gap)
          x3 (+ (max x0 x1) box-gap)
          y2 (- y box-gap)
          y3 (+ y box-gap)
          box (rect/rectangle grp [x2 y2][x3 y3]
                              :id :box
                              :color (uc/color box-color)
                              :style box-style
                              :width box-width
                              :fill fill-box?)]
      (.put-property! box :corner-radius box-radius)
      (.put-property! grp :box box)
      (.hide! box (not box-color)))

    (let [track1 (line/line grp [x0 y][x1 y]
                             :id :track1
                             :color (uc/color track1-color)
                             :style track1-style
                             :width track1-width)]
      (.put-property! grp :track1 track1))
    (if track2-color
      (let [c (uc/color track2-color)
            track2 (line/line grp [x0 y][x0 y]
                              :id :track2
                              :color c
                              :style track2-style
                              :width track2-width)]
        (set-attributes! track2 :rollover :color c :style track2-style :width track2-width)
        (.put-property! grp :track2 track2)))
    (if track3-color
      (let [c (uc/color track3-color)
            track3 (line/line grp [x0 y][x1 y]
                              :id :track3
                              :color c
                              :style track3-style
                              :width track3-width)]
        (set-attributes! track3 :rollover :color c :style track3-style :width track3-width)
        (.put-property! grp :track3 track3)))
    (let [handle (point/point grp [x0 y]
                              :id :handle
                              :color (uc/color handle-color)
                              :style handle-style
                              :size handle-size)]
      (.put-property! grp :handle handle))
    (.put-property! grp :value v0)
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :orientation :horizontal)
    (.put-property! grp :fn-set-value (fn [v]
                                        (let [v2 (math/clamp v (min v0 v1)(max v0 v1))
                                              pos (val->pos v2)
                                              handle (.get-property grp :handle)
                                              trk2 (.get-property grp :track2)
                                              trk3 (.get-property grp :track3)]
                                          (.put-property! grp :value v2)
                                          (.set-points! handle [[pos y]])
                                          (if trk2 (.set-points! trk2 [p0 [pos y]]))
                                          (if trk3 (.set-points! trk3 [[pos y] p1])))))
    (.put-property! grp :action-mouse-dragged  (get-dragged-action))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (fn [obj ev] ))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (.use-attributes! grp :default)
    grp))
