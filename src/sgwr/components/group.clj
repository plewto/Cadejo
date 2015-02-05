(ns sgwr.components.group
  "Defines group components
   A group is an component (implements sgwr.components.SgwrComponent) which
   holds other components, possibly other groups. 

   Each group may have it's own coordinate system which makes it
   possible to overlay multiple coordinate systems onto a single
   drawing. The only restriction is that all coordinates systems in a
   drawing must match the canvas bounds of the drawing."
  (:require [sgwr.constants :as constants])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.components.rectangle :as rect]))

(defn- shape-function [obj]
    (if (.hidden? obj)
      constants/null-shape
      (let [acc* (atom [])]
        (doseq [c (.children obj)]
          (if (not (.hidden? c))
            (swap! acc* (fn [q](conj q (.shape c))))))
        (or (and (pos? (count @acc*))(apply utilities/fuse @acc*))
            constants/null-shape))))

(defn- bounds-fn [obj points]
  (if (.has-children? obj)
    (let [x (map first points)
          y (map second points)
          x0 (apply min x)
          x1 (apply max x)
          y0 (apply min y)
          y1 (apply max y)]
      [[x0 y0][x1 y1]])
    [[nil nil][nil nil]]))

(defn- contains-fn [obj g]
  (let [flag* (atom false)
        i* (atom (dec (.child-count obj)))]
    (while (and (not @flag*)(>= @i* 0))
      (let [c (nth (.children obj) @i*)]
        (reset! flag* (.contains? c g))
        (swap! i* dec)))
    @flag*))

(defn distance-fn [obj g]
  (let [d* (atom constants/infinity)
        i* (atom (dec (.child-count obj)))]
    (while (and (>= @d* 0)(>= @i* 0))
      (let [c (nth (.children obj) @i*)]
        (swap! d* (fn [q](min q (.distance c g))))
        (swap! i* dec)))
    @d*))

(defn- points-fn [obj pnts]
  (let [acc* (atom [])]
    (doseq [c (.children obj)]
      (swap! acc* (fn [q](conj q (.points c)))))
    (partition 2 (flatten @acc*))))

(defn- translation-fn [obj offsets]
  (doseq [c (.children obj)]
    (.translate! c offsets)))

(defn- scale-fn [obj factors ref-point]
  (doseq [c (.children obj)]
    (.scale! c factors ref-point)))


(def ^:private group-function-map {:shape-fn shape-function
                                   :contains-fn contains-fn
                                   :distance-fn distance-fn
                                   :update-fn (fn [& _] [])
                                   :points-fn points-fn
                                   :translation-fn translation-fn
                                   :scale-fn scale-fn
                                   :bounds-fn bounds-fn
                                   :style-fn (constantly 0)})

(def locked-group-properties [])

(defn group [parent & {:keys [etype id color style size width fill hide cs]
                       :or {etype :group
                            id :new-group
                            color (uc/color :white)
                            style 0
                            size 1.0
                            width 1.0
                            fill false
                            hide false
                            cs nil}}]
  (let [obj (sgwr.components.component/create-component etype
                                                   parent
                                                   group-function-map
                                                   locked-group-properties)]
    (if parent (.set-parent! obj parent))
    (.put-property! obj :id id)
    (.color! obj :default color)
    (.style! obj :default style)
    (.size! obj :default size)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.hide! obj :default hide)
    (.use-attributes! obj :default)
    (if cs (.set-coordinate-system! obj cs))
    obj))
