(ns sgwr.widgets.compass
  (:require [sgwr.cs.polar :as polar-cs])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.util.color :as uc])
)

(defn get-dragged-action
  ([](get-dragged-action (fn [& _])))
  ([dfn]
   (fn [obj ev]
     (let [cs (.coordinate-system obj)
           handle (.get-property obj :handle)
           p0 [(.getX ev)(.getY ev)]
           ]
       (println p0)
       ))))


(defn compass [parent center radius id & {:keys [origin-color origin-style origin-size
                                                 circle-color circle-style circle-width fill-circle?
                                                 handle-color handle-style handle-size handle-init-amp 
                                                 ray-color ray-style ray-width ]
                                          :or {origin-color :white
                                               origin-style :dot
                                               origin-size 1
                                               circle-color :gray
                                               circle-style :solid
                                               circle-width 1.0
                                               fill-circle? false
                                               handle-color :green
                                               handle-style :dot
                                               handle-size 2
                                               handle-init-amp 1
                                               ray-color :red
                                               ray-style :solid
                                               ray-width 1.0}}]
  (let [drw (.get-property parent :drawing)
        [w h](.canvas-bounds drw)
        cs (polar-cs/polar-coordinate-system w h radius :origin center :unit :turn)

        grp (group/group parent :etype :compass :id id :cs cs)
        ]

        
    ;; (let [c (circle/circle-r grp [0 0] radius
    ;;                          :id :circle
    ;;                          :color (uc/color circle-color)
    ;;                          :style circle-style
    ;;                          :width circle-width
    ;;                          :fill fill-circle?)]
    ;;   (.put-property! grp :circle c)
    ;;   (.hide! c (not circle-color)))
    (let [origin (point/point grp [0 0]
                              :id :origin
                              :color (uc/color origin-color)
                              :style origin-style
                              :size origin-size)]
      (.put-property! grp :origin origin))

    ;; (if ray-color
    ;;   (let [ray (line/line grp [0 0] [radius 0]
    ;;                        :id :ray
    ;;                        :color (uc/color ray-color)
    ;;                        :style ray-style
    ;;                        :width ray-width)]
    ;;     (.put-property! grp :ray ray)))

    (let [handle (point/point grp [radius 0]
                              :id :handle
                              :color (uc/color handle-color)
                              :style handle-style
                              :size handle-size)]
      (.put-property! grp :handle handle))
    (.put-property! grp :action-mouse-dragged  (get-dragged-action))
    (.put-property! grp :action-mouse-moved    (fn [obj ev] ))
    (.put-property! grp :action-mouse-entered  (fn [obj ev] ))
    (.put-property! grp :action-mouse-exited   (fn [obj ev] ))
    (.put-property! grp :action-mouse-pressed  (fn [obj ev] ))
    (.put-property! grp :action-mouse-released (fn [obj ev] ))
    (.put-property! grp :action-mouse-clicked  (fn [obj ev] ))
    (.use-attributes! grp :default)
    grp))

    
   
                              
        
