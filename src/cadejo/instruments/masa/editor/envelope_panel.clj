(ns cadejo.instruments.masa.editor.envelope-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.tools.field :as field]))

(defn envelope-panel [drw ied p0 ]
  (let [data (fn [param]
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        [x0 y0] p0
        x1 (+ x0 envelope-panel-width)
        y1 (- y0 envelope-panel-height)
        p1 [x1 y1]
        xc (* 0.5 (+ x0 x1))
        yc (* 0.5 (+ y0 y1))
        drag-action (fn [fld _] 
                      (let [b (.get-property fld :ball)
                            pos (first (.points b))
                            [dcy sus](.get-property b :value)
                            s1 (.get-property fld :segment-1)
                            s2 (.get-property fld :segment-2)
                            p0 [x0 y1]
                            p1 [(int (first pos))(int (second pos))]
                            p2 [x1 (second p1)]
                            segment-1 (.get-property fld :segment-1)
                            segment-2 (.get-property fld :segment-2)]
                        (.set-points! segment-1 [p0 p1])
                        (.set-points! segment-2 [p1 p2])
                        (.set-param! ied :decay dcy)
                        (.set-param! ied :sustain sus)
                        (.status! ied (format "[:decay] -> %5.3f [:sustain] -> %5.3f" dcy sus))
                        (.render drw)))

        fld (field/field (.tool-root drw) p0 p1[0.0 max-decay-time] [1.0 0.0]
                         :drag-action drag-action
                         :rim-color (lnf/envelope-border-color))
        bll (let [b (field/ball fld :ball [0.5 0.5] 
                                :color (lnf/envelope-handle-color)
                                :selected-color (lnf/envelope-handle-color)
                                :style [:dot]
                                :size 3
                                :selected-style [:dot])]
              (.put-property! fld :ball b)
              b)
        segment-1 (let [s (line/line fld [x0 y1][xc yc]
                                     :id :segment-1
                                     :color (lnf/envelope-line-color))]
                    (.put-property! fld :segment-1 s)
                    s)
        segment-2 (let [s (line/line fld [xc yc][x1 yc]
                                     :id :segmnet-2
                                     :color (lnf/envelope-line-color))]
                    (.put-property! fld :segment-2 s)
                    s)
        widget-map {}]
    (.put-property! fld :segment-1 segment-1)
    (.put-property! fld :segment-2 segment-2)
    (text/text (.root drw) [(+ x0 55)(- y0 157)] "Envelope"
               :style :sans
               :size 8
               :color (lnf/text-color))
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      
      (widget [this key]
        (get widget-map key))
      
      (parent [this] ied)
      
      (parent! [this _] ied) 
      
      (status! [this msg]
        (.status! ied msg))
      
      (warning! [this msg]
        (.warning! ied msg))
      
      (set-param! [this param value]
        (.status! this (format "[%s] --> %s" param val))
        (.set-param! ied param val))
      
      (init! [this]
        (.set-param! ied :decay 0.2)
        (.set-param! ied :sustain 0.8))
      
      (sync-ui! [this]
        (let [decay (math/clamp (data :decay) 0 max-decay-time)
              sustain (math/clamp (data :sustain) 0 1)]
          (field/set-ball-value! fld :ball [decay sustain] false)
          (let [pos (first (.points bll))
                p0 [x0 y1]
                p1 [(int (first pos))(int (second pos))]
                p2 [x1 (second  p1)]]
            (.set-points! segment-1 [p0 p1])
            (.set-points! segment-2 [p1 p2]))))))) 
