(ns sgwr.widgets.slider
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
)


(defn get-dragged-action 
  ([] (get-dragged-action (fn [& _])))
  ([dfn]
   (fn [obj ev]
     (let [orientation (.get-property obj :orientation)
           cs (.coordinate-system obj)
           handle (first (.children obj (fn [q](= (.get-property q :id) :handle))))
           pos (.inv-map cs [(.getX ev)(.getY ev)])
           param (if (= orientation :vertical)(second pos)(first pos))
           val ((.get-property obj :fn-pos->val) param)]
       (cond (= orientation :vertical)
             (let [x (.get-property obj :lock-x)]
               (.set-points! handle [[x (second pos)]]))
             
             (= orientation :horizontal)
             (let [y (.get-property obj :lock-y)]
               (.set-points! handle [[(first pos) y]]))

             :default                   ; no defaults
             nil )
       (.put-property! obj :value val)
       (.render (.get-property obj :drawing))
       (dfn obj ev)))))


(defn set-slider-value! 
  ([slider value](set-slider-value! slider value true))
  ([slider value render]
   (let [orientation (.get-property slider :orientation)
         pos ((.get-property slider :fn-val->pos) value)
         handle (first (.children slider (fn [q](= (.get-property q :id) :handle))))
         x (if (= orientation :vertical) (.get-property slider :lock-x) pos)
         y (if (= orientation :horizontal) (.get-property slider :lock-y) pos)]
     (.set-points! handle [[x y]])
     (.put-property! slider :value (math/clamp value (.get-property slider :minmax)))
     (if render (.render (.get-property slider :drawing)))
     value)))

;; Points as vector [x y0 y1] 
;; Values as vector [n0 n1]
;;
(defn vslider [parent points values id & {:keys [paint-track? track-color track-style track-width
                                                 paint-box? box-gap box-color box-style box-width box-radius fill-box?
                                                 handle-color handle-style handle-size]
                                          :or {paint-track? true
                                               track-color :gray
                                               track-style :solid
                                               track-width 1.0
                                               paint-box? true
                                               box-color :gray
                                               box-gap 6
                                               box-style :solid
                                               box-width 1.0
                                               box-radius 0
                                               fill-box? false
                                               handle-color :green
                                               handle-style :dot
                                               handle-size 2}}]
  (let [[x y0 y1] points
        [n0 n1] values
        pos->val (math/clipped-linear-function y0 n0 y1 n1)
        val->pos (math/clipped-linear-function n0 y0 n1 y1)
        grp (group/group parent :etype :slider :id id)
        box (let [x2 (- x box-gap)
                  x3 (+ x box-gap)
                  y2 (- y0 box-gap)
                  y3 (+ y1 box-gap)
                  box (rect/rectangle grp [x2 y2][x3 y3]
                                      :id (keyword (format "%s-box" (name id)))
                                      :color (uc/color box-color)
                                      :style box-style
                                      :width box-width
                                      :fill fill-box?)]
              (.put-property! box :corner-radius box-radius)
              (.hide! box (not paint-box?))
              box)
        track (let [track (line/line grp [x y0][x y1]
                                     :id (keyword (format "%s-track" (name id)))
                                     :color (uc/color track-color)
                                     :style track-style
                                     :width track-width)]
                (.hide! track (not paint-track?))
                track)
        handle (let [h (point/point grp [x y0]
                                    :id (keyword (format "handle"))
                                    :color (uc/color handle-color)
                                    :style handle-style
                                    :size handle-size)]
                 h)]
    (.put-property! grp :orientation :vertical)
    (.put-property! grp :lock-x x)
    (.put-property! grp :lock-y nil)
    (.put-property! grp :minmax [(min n0 n1)(max n0 n1)])
    (.put-property! grp :value (float n0))
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :action-mouse-dragged  (get-dragged-action))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (fn [obj ev] ))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (.use-attributes! grp :default)
    grp))


;; points [y x0 x1]
;; values   [n0 n1]
(defn hslider [parent points values id & {:keys [paint-track? track-color track-style track-width
                                                 paint-box? box-gap box-color box-style box-width box-radius fill-box?
                                                 handle-color handle-style handle-size]
                                          :or {paint-track? true
                                               track-color :gray
                                               track-style :solid
                                               track-width 1.0
                                               paint-box? true
                                               box-color :gray
                                               box-gap 6
                                               box-style :solid
                                               box-width 1.0
                                               box-radius 0
                                               fill-box? false
                                               handle-color :green
                                               handle-style :dot
                                               handle-size 2}}]
  (let [[y x0 x1] points
        [n0 n1] values
        pos->val (math/clipped-linear-function x0 n0 x1 n1)
        val->pos (math/clipped-linear-function n0 x0 n1 x1)
        grp (group/group parent :etype :slider :id id)
        box (let [x2 (- x0 box-gap)
                  x3 (+ x1 box-gap)
                  y2 (- y box-gap)
                  y3 (+ y box-gap)
                  box (rect/rectangle grp [x2 y2][x3 y3]
                                      :id (keyword (format "%s-box" (name id)))
                                      :color (uc/color box-color)
                                      :style box-style
                                      :width box-width
                                      :fill fill-box?)]
              (.put-property! box :corner-radius box-radius)
              (.hide! box (not paint-box?))
              box)
        track (let [track (line/line grp [x0 y][x1 y] 
                                     :id (keyword (format "%s-track" (name id)))
                                     :color (uc/color track-color)
                                     :style track-style
                                     :width track-width)]
                (.hide! track (not paint-track?))
                track)
        handle (let [h (point/point grp [x0 y]
                                    :id (keyword (format "handle"))
                                    :color (uc/color handle-color)
                                    :style handle-style
                                    :size handle-size)]
                 h)]
    (.put-property! grp :orientation :horizontal)
    (.put-property! grp :lock-x nil)
    (.put-property! grp :lock-y y)
    (.put-property! grp :minmax [(min n0 n1)(max n0 n1)])
    (.put-property! grp :value (float n0))
    (.put-property! grp :fn-pos->val pos->val)
    (.put-property! grp :fn-val->pos val->pos)
    (.put-property! grp :action-mouse-dragged  (get-dragged-action (fn [obj ev](println "H " (.get-property obj :value)))))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (fn [obj ev] ))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (.use-attributes! grp :default)
    grp))


                                                                                    
