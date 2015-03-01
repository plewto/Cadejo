(ns cadejo.instruments.algo.editor.op-selection-panel 
  (:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.util.color :as uc]))

(def ^:private op-button-width 30)
(def ^:private op-button-height 30)
(def ^:private aux-button-width 80)
(def ^:private aux-button-height op-button-height)

(defn- op-button [op grp p0 action card-number]
  "Creates sgwr button for FM operator.
   op     - int, operator number {1,2,3,...,8}
   grp    - sgwr Group
   p0     - vector, position [x0 y0] of upper left hand corner
   action - function (fn [b ev] ...) function executed on mouse click
            b  - sgwr button 
            ev - java MouseEvent
   card-number - int, editor's JCardPane number
   
   Returns sgwr button b where (.get-property b) -> op"
  (let [b (button/text-button grp p0 (format " %d " op)
                              :id :op-button
                              :click-action action
                              :w op-button-width
                              :h op-button-height
                              :text-size 7
                              :pad-color [0 0 0 0]
                              :rim-width 1.0
                              :rim-color (lnf/button-border-color)
                              :rim-radius 6
                              :text-color (lnf/text-color))]
    (.put-property! b :op op)
    (.put-property! b :card-number card-number)
    (let [pad (.get-property b :pad)
          occluder (.get-property b :occluder)
          rim (.get-property b :rim)
          txt (.get-property b :text-component) ]
      (.color! occluder :default [0 0 0 0])
      (.color! rim :highlight (lnf/selected-button-border))
      (.color! txt :highlight (lnf/selected-text-color))
      
    
    (.use-attributes! b :default)
    ;(.dump b)
    b)))


(defn- aux-button [id grp p0 text action card-number]
  (let [b (button/text-button grp p0 (format " %6s " text)
                              :id :aux-button
                              :click-action action
                              :w aux-button-width
                              :h aux-button-height
                              :rim-width 1.0
                              :rim-color (lnf/button-border-color)
                              :rim-radius 6
                              :text-color (lnf/text-color))]
    (.put-property! b :card-number card-number)
    (.put-property! b :op id)
    (let [pad (.get-property b :pad)
          occluder (.get-property b :occluder)
          rim (.get-property b :rim)
          txt (.get-property b :text-component) ]
      (.color! occluder :default [0 0 0 0])
      (.color! rim :highlight (lnf/selected-button-border))
      (.color! txt :highlight (lnf/selected-text-color))
      (.use-attributes! b :default)
      b)))
      

(defn op-selection-panel 
  "(op-selection-panel drw p0 action)
   (op-selection-panel drw p0 action draw-border)

   Creates 'algorithm' diagram on drawing using sgwr buttons
   drw         - sgwr Drawing
   p0          - vector, position [x y] of lower left corner of diagram
   action      - Function, function (fn [b ev] ...) executed on mouse
                 click, where b is sgwr button and ev is java MouseEvent
                 (.get-property b :op) --> returns operator number    
  draw-border - flag, if true draw outer borde
   
   Returns map {k1 b1,  k2 b2, ..., k8 b8} where keys ki are operator
   numbers (1,2,3,...,8) and bi are sgwr buttons"
  ([drw p0 action]
   (op-selection-panel drw p0 action true))
  ([drw p0 action draw-border]
   (let [tools (.tool-root drw)
         hw (* 0.5 op-button-width)
         hh (* 0.5 op-button-height)
         horz-gap (* 0.667 op-button-width)
         vert-gap horz-gap
         [x0 y0] p0
         x1 (+ x0 horz-gap)
         x2 (+ x1 hw)
         x3 (+ x1 op-button-width)
         x4 (+ x3 horz-gap)
         x5 (+ x4 hw)
         x6 (+ x4 op-button-width)
         x7 (+ x6 horz-gap)
         x8 (+ x7 hw)
         x9 (+ x7 op-button-width)
         x10 (+ x9 (* 0.5 horz-gap))
         x11 (+ x10 horz-gap)
         x12 (+ x11 hw)
         x13 (+ x11 op-button-width)
         x14 (+ x13 (* 0.5 horz-gap))
         x15 (+ x13 horz-gap)
         x-aux (+ x15 10)
         x16 (+ x-aux aux-button-width horz-gap)
         y1 (- y0 vert-gap)
         y2 (- y1 vert-gap)
         y3 (- y2 hh)
         y4 (- y2 op-button-height)
         y5 (- y4 (* 0.5 vert-gap))
         y6 (- y4 vert-gap)
         y7 (- y6 hh)
         y8 (- y6 op-button-height)
         y9 (- y8 hh)
         y10 (- y8 vert-gap)
         y11 (- y10 op-button-height)
         y12 (- y11 vert-gap)
         y-aux (- y10 25)
         button-map {1 (op-button 1 tools [x1 y4]  action 1)
                     2 (op-button 2 tools [x1 y8]  action 2)
                     3 (op-button 3 tools [x1 y11] action 3)
                     4 (op-button 4 tools [x4 y4]  action 4)
                     5 (op-button 5 tools [x4 y8]  action 5)
                     6 (op-button 6 tools [x7 y8]  action 6)
                     7 (op-button 7 tools [x11 y4] action 7)
                     8 (op-button 8 tools [x11 y8] action 8)
                     :global (aux-button :global tools [x-aux y-aux] "Global" action 0)
                     :lfo (aux-button :lfo tools [x-aux (+ y-aux (* 1 (+ aux-button-height 8)))   ] "LFO  " action 9)
                     :pitch (aux-button :pitch tools [x-aux (+ y-aux (* 2 (+ aux-button-height 8)))   ] "Pitch  " action 10) 
                     :fx (aux-button :fx tools [x-aux (+ y-aux (* 3 (+ aux-button-height 8)))   ] "Fx  " action 11)}
         track-color (uc/complement (uc/color (lnf/text-color)))
         vline (fn [x y1 y2](line/line (.root drw) [x y1][x y2] :color track-color))
         hline (fn [x1 x2 y] (line/line (.root drw) [x1 y][x2 y] :color track-color))]
     (vline x2 y1 y2)
     (vline x2 y4 y6)
     (vline x2 y8 y10)
     (vline x5 y1 y2)
     (vline x5 y4 y6)
     (vline x8 y5 y6)
     (vline x8 y8 y9)
     (vline x10 y3 y9)
     (vline x12 y1 y2)
     (vline x12 y4 y6)
     (vline x12 y8 y9)
     (vline x14 y7 y9)
     (hline x2 x12 y1)
     (hline x6 x10 y3)
     (hline x5 x8 y5)
     (hline x8 x10 y9)
     (hline x13 x14 y7)
     (hline x12 x14 y9)
     (if draw-border (factory/inner-border (.root drw)[x0 y0][x16 y12]))
     button-map)))


(defn highlight! 
  ([op button-map]
   (highlight! op button-map true))
  ([op button-map flag]
   (let [b (get button-map op)]
     (if flag
       (.use-attributes! b :highlight)
       (.use-attribtues! b :default)))))
  
                
    

