(ns sgwr.indicators.basic-cell
  "Defines displaybar cell in terms of text objects."
  (:require [sgwr.indicators.cell])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.components.component :as components])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text]))

(def set-attributes! components/set-attributes!)

(defn basic-cell [grp x-offset y-offset & {:keys [cell-width cell-height]
                                           :or {cell-width 25
                                                cell-height 35}}]
   "(basic-cell grp x-offset y-offset :cell-width :cell-height)
    Creates displaybar cell using text object.
    grp         - parent group
    x-offset    - float, horizontal offset of cell in grp
    y-offset    - float, vertical offset of cell in grp
    cell-width  - int, default 25
    cell-height - int, default 35"
   (let [inactive* (atom (uc/color [32 32 32]))
         active* (atom (uc/color [255 64 64]))
         x x-offset
         y (+ y-offset cell-height)
         components (let [x0 x-offset
                        y0 (+ y-offset cell-height)
                        x1 (+ x0 cell-width)
                        y1 y-offset
                        font-size (int (text/estimate-monospaced-font-size cell-width))
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
                 (let [[txt box] components
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
                (let [[txt box] components]
                  (cond (or (= c \space)(= c 32))
                        (do 
                          (.use-attributes! txt :inactive)
                          (.use-attributes! box :active))
                        :default
                        (do 
                          (.use-attributes! txt :active)
                          (.put-property! txt :text (str (char c)))
                          (.use-attributes! box :inactive))))) )]
     obj))
