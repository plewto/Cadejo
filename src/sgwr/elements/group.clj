(ns sgwr.elements.group
  (:require [sgwr.constants :as constants])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.elements.rectangle :as rect])
  )

(defn- shape-function [obj]
    (if (.hidden? obj)
      constants/null-shape
      (let [acc* (atom [])]
        (doseq [c (.children obj)]
          (if (not (.hidden? c))
            (swap! acc* (fn [q](conj q (.shape c))))))
        (or (and (pos? (count @acc*))(apply utilities/fuse @acc*))
            constants/null-shape))))

;; (defn- bounds-fn [obj points]
;;   (let [acc* (atom [])]
;;     (doseq [c (.children obj)]
;;       (swap! acc* (fn [q](conj q (.bounds c)))))
;;     (if (pos? (count @acc*))
;;       (let [x (map first points)
;;             y (map second points)
;;             x0 (apply min x)
;;             x1 (apply max x)
;;             y0 (apply min y)
;;             y1 (apply max y)]
;;         [[x0 y0][x1 y1]])
;;       [[0 0][0 0]])))

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
  (let [d* (atom 1e999)
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
                                   :bounds-fn bounds-fn
                                   })

(defn group 
  ([id](group nil id))
  ([parent id]
   (let [obj (sgwr.elements.element/create-element :group
                                                   parent
                                                   group-function-map)]
     (.put-property! obj :id id)
     (if parent (.set-parent! obj parent))
     obj)))