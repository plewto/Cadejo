(ns sgwr.elements.rule
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.util.math :as math]))
  
(let [counter* (atom 0)]
  (defn- get-rule-id [id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "rule-%d" n))))))
          
(defn ruler [parent p0 length  & {:keys [orientation id
                                         track-color track-style track-width track-offset
                                         gap pad-color
                                         rim-color rim-style rim-width rim-radius]
                                  :or {orientatin :vertical
                                       id nil
                                       track-color :red
                                       track-style :solid
                                       track-width 1.0
                                       track-offset 0
                                       gap nil
                                       pad-color [0 0 0 0]
                                       rim-color :gray
                                       rim-style 1.0
                                       rim-width 1.0
                                       rim-radius 0}}]
  (let [vertical (= orientation :vertical)
        native (= (.cs-type (.coordinate-system parent)) :native)
        gap (or gap (if native 12 4))
        [x0 y0] p0
        [x1 y1] (if native 
                  (if vertical 
                    [x0 (- y0 length)]
                    [(+ x0 length) y0])
                  (if vertical
                    [x0 (+ y0 length)]
                    [(+ x0 length) y0]))
        [x2 y2] (if native 
                  [(- x0 gap)(+ y0 gap)]
                  [(- x0 gap)(- y0 gap)])
        [x3 y3] (if native 
                  [(+ x1 gap)(- y1 gap)]
                  [(+ x1 gap)(+ y1 gap)])
        grp (group/group parent :id (get-rule-id id))
        pad (let [pad (rect/rectangle grp [x2 y2][x3 y3] :id :pad
                                      :color pad-color
                                      :fill true)]
              (.put-property! pad :corner-radius rim-radius)
              pad)
        rim (let [rim (rect/rectangle grp [x2 y2][x3 y3] :id :rim
                                      :color rim-color
                                      :style rim-style
                                      :width rim-width
                                      :fill :no)]
              (.put-property! rim :corner-radius rim-radius)
              rim)
        track (if vertical
                (let [x (+ x0 track-offset)]
                  (line/line grp [x y0][x y1] :id :track))
                (let [y (+ y0 track-offset)]
                  (line/line grp [x0 y][x1 y] :id :track)))]
    (.color! track :default track-color)
    (.style! track :default track-style)
    (.width! track :default track-width)
    (.use-attributes! grp :default)
    (.put-property! grp :orientation orientation)
    (.put-property! grp :p0 p0)
    (.put-property! grp :length length)
    (.put-property! grp :p1 [x1 y1])
    grp))

(defn ticks [ntvruler step & {:keys [id length offset color style]
                              :or {id :tick
                                   length 8
                                   offset 0
                                   color :white
                                   style 0}}]
  (let [vertical (= (.get-property ntvruler :orientation) :vertical)
        p0 (.get-property ntvruler :p0)
        p1 (.get-property ntvruler :p1)
        [start end] (if vertical
                      [(second p0)(second p1)]
                      [(first p0)(first p1)])
        half (* 1/2 length)]
    (if vertical
      
      (let [x1 (+ offset (- (first p0) half))
            x2 (+ offset (first p0) half)]
        (doseq [y (range (min start end)(+ (max start end) step) step)]
          (if (math/in-range? y start end)
            (line/line ntvruler [x1 y][x2 y] :id id :color color :style style))))
      (let [y1 (+ offset (- (second p0) half))
            y2 (+ offset (second p0) half)]
        (doseq [x (range (min start end)(+ (max start end) start) step)]
          (if (math/in-range? x start end)
            (line/line ntvruler [x y1][x y2] :id id :color color :style style)))))))
            
     
