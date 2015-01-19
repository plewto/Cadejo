(ns sgwr.elements.rule
  (:require [sgwr.cs.native])
  (:require [sgwr.cs.cartesian])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities])
  (:require [sgwr.elements.drawing :as drw])
  (:require [sgwr.elements.group :as group])
  ;(:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.line :as line])
  (:require [sgwr.elements.rectangle :as rect])
  )
            

(defn- vr [parent cs p0 length & {:keys [id
                                      track-color
                                      track-style
                                      track-width
                                      track-offset
                                      draw-track?
                                      box-color
                                      box-gap 
                                      box-style
                                      box-width
                                      box-fill?
                                      box-radius
                                      draw-box?
                                      major 
                                      major-length
                                      major-offset
                                      major-color
                                      minor 
                                      minor-length
                                      minor-offset
                                      minor-color]
                               :or {id :vruler
                                    track-color :white
                                    track-style 0
                                    track-width 1.0
                                    track-offset 0
                                    draw-track? true
                                    box-color :white
                                    box-gap 4
                                    box-style 0
                                    box-width 1.0
                                    box-fill? false
                                    box-radius 0
                                    draw-box? true
                                    major 4
                                    major-length 8
                                    major-offset 0
                                    major-color :white
                                    minor 20
                                    minor-length 4
                                    minor-offset 0
                                    minor-color :white}}]
  (let [grp (group/group parent :etype :rule :id id)
        [x0 y0] p0
        x1 x0
        y1 (+ length y0)
        p1 [x1 y1]
        box (let [g1 (/ box-gap (.x-scale cs))
                  g2 (* 2 g1)
                  x2 (- x0 g2)
                  x3 (+ x0 g2)
                  y2 (- y0 g1)
                  y3 (+ y1 g1)
                  box (rect/rectangle grp [x2 y2][x3 y3]
                                      :id (keyword (format "%s-box" (name id)))
                                      :color (or box-color track-color)
                                      :style box-style
                                      :width box-width
                                      :fill box-fill?)]
              (.hide! box (not draw-box?))
              (.put-property! box :corner-radius box-radius)
              box)
        trk (let [offset (/ track-offset (.x-scale cs))
                  trk (line/line grp 
                                 [(+ x0 offset) y0]
                                 [(+ x0 offset) y1]
                                 :id (keyword (format "%s-track" (name id)))
                                 :color track-color
                                 :style track-style
                                 :width track-width)]
              (.hide! trk (not draw-track?))
              trk)]
    (if (integer? minor)
      (let [dy (/ (float length) minor)
            tick-length (/ minor-length (.x-scale cs))
            offset (/ minor-offset (.x-scale cs))
            x4 (+ (- x0 tick-length) offset)
            x6 (+ x0 tick-length offset)]
        (doseq [y (range y0 (+ y1 dy) dy)]
          (line/line grp [x4 y][x6 y]
                     :id (keyword (format "%s-minor" (name id)))
                     :color (or minor-color track-color)))))
    (if (integer? major)
      (let [dy (/ (float length) major)
            tick-length (/ major-length (.x-scale cs))
            offset (/ major-offset (.x-scale cs))
            x4 (+ (- x0 tick-length) offset)
            x6 (+ x0 tick-length offset)]
        (doseq [y (range y0 (+ y1 dy) dy)]
          (line/line grp [x4 y][x6 y]
                     :id (keyword (format "%s-major" (name id)))
                     :color (or major-color track-color)))))
    (.use-attributes! grp :default)
    grp))

(defn- hr [parent cs p0 length & {:keys [id
                                      track-color
                                      track-style
                                      track-width
                                      track-offset
                                      draw-track?
                                      box-color
                                      box-gap 
                                      box-style
                                      box-width
                                      box-fill?
                                      box-radius
                                      draw-box?
                                      major 
                                      major-length
                                      major-offset
                                      major-color
                                      minor 
                                      minor-length
                                      minor-offset
                                      minor-color]
                               :or {id :vruler
                                    track-color :white
                                    track-style 0
                                    track-width 1.0
                                    track-offset 0
                                    draw-track? true
                                    box-color :white
                                    box-gap 4
                                    box-style 0
                                    box-width 1.0
                                    box-fill? false
                                    box-radius 0
                                    draw-box? true
                                    major 4
                                    major-length 8
                                    major-offset 0
                                    major-color :white
                                    minor 20
                                    minor-length 4
                                    minor-offset 0
                                    minor-color :white}}]
  (let [grp (group/group parent :etype :rule :id id)
        [x0 y0] p0
        x1 (+ length x0)
        y1 y0
        p1 [x1 y1]

        box (let [g1 (/ box-gap (.x-scale cs))
                  g2 (/ (* 2 box-gap)(.y-scale cs))
                  x2 (- x0 g1) 
                  x3 (+ x1 g1) 
                  y2 (- y0 g2)
                  y3 (+ y0 g2)
                  box (rect/rectangle grp [x2 y2][x3 y3]
                                      :id (keyword (format "%s-box" (name id)))
                                      :color (or box-color track-color)
                                      :style box-style
                                      :width box-width
                                      :fill box-fill?)]
              (.hide! box (not draw-box?))
              (.put-property! box :corner-radius box-radius)
              box)
        
        trk (let [offset (/ track-offset (.y-scale cs))
                  trk (line/line grp 
                                 [x0 (- y0 offset)]
                                 [x1 (- y1 offset)]
                                 :id (keyword (format "%s-track" (name id)))
                                 :color track-color
                                 :style track-style
                                 :width track-width)]
              (.hide! trk (not draw-track?))
              trk)]
    (if (integer? minor)
      (let [dx (/ (float length) minor)
            tick-length (/ minor-length (.y-scale cs))
            offset (- (/ minor-offset (.y-scale cs)))
            y4 (+ (- y0 tick-length) offset)
            y6 (+ y0 tick-length offset)]
        (doseq [x (range x0 (+ x1 dx) dx)]
          (line/line grp [x y4][x y6]
                     :id (keyword (format "%s-minor" (name id)))
                     :color (or minor-color track-color)))))
    (if (integer? major)
      (let [dx (/ (float length) major)
            tick-length (/ major-length (.y-scale cs))
            offset (- (/ major-offset (.y-scale cs)))
            y4 (+ (- y0 tick-length) offset)
            y6 (+ y0 tick-length offset)]
        (doseq [x (range x0 (+ x1 dx) dx)]
          (line/line grp [x y4][x y6]
                     :id (keyword (format "%s-major" (name id)))
                     :color (or major-color track-color)))))
    (.use-attributes! grp :default)
    grp))

;; (defn ruler->drawing [robj]
;;   (let [cs (.coordinate-system robj)
;;         m0 (.inv-map cs [0 0])
;;         offsets m0 ; [(- (first m0))(- (second m0))]
;;         ]
;;     (println (format "DEBUG cs            -> %s " (.to-string cs)))
;;     (println (format "DEBUG m0            -> %s " m0))
;;     (println (format "DEBUG offsets       -> %s " offsets))
;;     (.translate! robj offsets)
;;     (.render (.get-property robj :drawing))
;;     ))


(defn ruler [parent p0 length & {:keys [id
                                        as-image
                                        orientation
                                        track-color
                                        track-style
                                        track-width
                                        track-offset
                                        draw-track?
                                        box-color
                                        box-gap 
                                        box-style
                                        box-width
                                        box-fill?
                                        box-radius
                                        draw-box?
                                        major 
                                        major-length
                                        major-offset
                                        major-color
                                        minor 
                                        minor-length
                                        minor-offset
                                        minor-color]
                                 :or {id :ruler
                                      as-image false
                                      orientation :vertical
                                      track-color :red
                                      track-style 0
                                      track-width 1.0
                                      track-offset 0
                                      draw-track? true
                                      box-color :white
                                      box-gap 4
                                      box-style 0
                                      box-width 1.0
                                      box-fill? false
                                      box-radius 0
                                      draw-box? true
                                      major 4
                                      major-length 8
                                      major-offset 0
                                      major-color :yellow
                                      minor 20
                                      minor-length 4
                                      minor-offset 0
                                      minor-color :cyan}}]
  (let [cs (.coordinate-system parent)
        robj (let [rfn (cond (= orientation :vertical) vr
                             (= orientation :horizontal) hr
                             :default vr)]
               (rfn parent
                    cs
                    p0 length :id id
                    :track-color track-color :track-style track-style
                    :track-width track-width :track-offset track-offset
                    :draw-track? draw-track?
                    :box-color box-color :box-gap box-gap :box-style box-style
                    :box-width box-width :box-radius box-radius 
                    :box-fill? box-fill? :draw-box? draw-box? 
                    :major major :major-length major-length 
                    :major-offset major-offset :major-color major-color 
                    :minor minor :minor-length minor-length 
                    :minor-offset minor-offset :minor-color minor-color))]
    robj)) 
