(ns sgwr.indicators.basic-cell
  (:require [sgwr.indicators.cell])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.elements.element :as elements])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text]))

(def set-attributes! elements/set-attributes!)

(defn basic-cell [grp x-offset y-offset & {:keys [cell-width cell-height font-size]
                                           :or {cell-width 25
                                                cell-height 35
                                                font-size 22}}]
   (let [inactive* (atom (uc/color [32 32 32]))
         active* (atom (uc/color [255 64 64]))
         x x-offset
         y (+ y-offset cell-height)
         elements (let [x0 x-offset
                        y0 (+ y-offset cell-height)
                        x1 (+ x0 cell-width)
                        y1 y-offset
                        txt (text/text grp [x0 y0] " ")
                        box (rect/rectangle grp [x0 y0][x1 y1])]
                    (set-attributes! txt :active :color @active* :style 0 :size font-size :hide :no)
                    (set-attributes! txt :inactive :hide true)
                    (set-attributes! box :active :hide :no)
                    (set-attributes! box :inactive :color @inactive* :style 0 :fill false :hide :no)
                    [txt box])
         obj (reify sgwr.indicators.cell/Cell

               (cell-width [this] cell-width)
               (cell-height [this] cell-height)

               (colors! [this inactive active]
                 (let [[txt box] elements
                       c1 (uc/color inactive)
                       c2 (uc/color active)]
                   (.color! txt :inactive c1)
                   (.color! txt :active c2)
                   (.color! box :active c1)
                   (.color! box :inactive c1)
                   (reset! inactive* c1)
                   (reset! active* c2)
                   [c1 c2]))
               
               (character-set [this]
                 (range 32 128))

              (display! [this c]
                (let [[txt box] elements]
                  (cond (or (= c \space)(= c 32))
                        (do 
                          (.use-attributes! txt :inactive)
                          (.use-attributes! box :active))
                        :default
                        (do 
                          (.use-attributes! txt :active)
                          (.put-property! txt :text (str (char c)))
                          (.use-attributes! box :inactive))))))]
     obj))
