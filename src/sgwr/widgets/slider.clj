(ns sgwr.widgets.slider
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
      (or id (keyword (format "slider-%d" n))))))

(defn- dummy-action [& _] nil)

(defn set-slider-value! 
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
                                             track3-color :green
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
        
                        
        
                                        
         
        
                                              
