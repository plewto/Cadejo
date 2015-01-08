(ns sgwr.elements.group
  (:require [sgwr.constants :as constants])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.elements.rectangle :as rect]))

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

(def ^:private group-function-map {:shape-fn shape-function
                                   :contains-fn contains-fn
                                   :distance-fn distance-fn
                                   :update-fn (fn [& _] [])
                                   :points-fn points-fn
                                   :bounds-fn bounds-fn})

(def locked-group-properties [])

(defn group [parent & {:keys [id color style size width fill hide]
                       :or {id :new-group
                            color (uc/color :white)
                            style 0
                            size 1.0
                            width 1.0
                            fill false
                            hide false}}]
  (let [obj (sgwr.elements.element/create-element :group
                                                   parent
                                                   group-function-map
                                                   locked-group-properties)]
    (if parent (.set-parent! obj parent))
    (.color! obj :default color)
    (.style! obj :default style)
    (.size! obj :default size)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.hide! obj :default hide)
    (.use-attributes! obj :default)
    obj))




;; Similer as greouop function above but used to construct widgets
;;
(def locked-widget-properties 
  (flatten (merge locked-group-properties
                 [:action-mouse-enterd 
                  :action-mouse-exited
                  :action-mouse-clicked
                  :action-mouse-released
                  :action-mouse-pressed])))



(defn widget-group [parent widget-type & {:keys [id color style size width fill hide]
                                          :or {id :new-group
                                               color (uc/color :white)
                                               style 0
                                               size 1.0
                                               width 1.0
                                               fill false
                                               hide false}}]
  (let [obj (sgwr.elements.element/create-element widget-type
                                                  parent
                                                  group-function-map
                                                  locked-widget-properties)]
    (if parent (.set-parent! obj parent))
    (.color! obj :default color)
    (.style! obj :default style)
    (.size! obj :default size)
    (.width! obj :default width)
    (.fill! obj :default fill)
    (.hide! obj :default hide)
    (.use-attributes! obj :default)
    (.put-property! obj :action-mouse-entered  (fn [obj mev] (println (format "Entered id %s" (.get-property obj :id)))))
    (.put-property! obj :action-mouse-exited   (fn [obj mev] (println (format "Exited  id %s" (.get-property obj :id)))))
    (.put-property! obj :action-mouse-clicked  (fn [obj mev] (println "Click")))
    (.put-property! obj :action-mouse-released (fn [obj mev] (println "Released")))
    (.put-property! obj :action-mouse-pressed  (fn [obj mev] (println "Pressed")))
    obj))


