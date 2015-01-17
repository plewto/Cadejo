(ns sgwr.indicators.basic-char
  (:require [sgwr.indicators.char])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.elements.element :as elements])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text]))

(def char-width sgwr.indicators.char/char-width)
(def char-height sgwr.indicators.char/char-height)
(def set-attributes! elements/set-attributes!)

(defn basic-char [grp x-offset y-offset]
   (let [inactive* (atom (uc/color [32 32 32]))
         active* (atom (uc/color [255 64 64]))
         x x-offset
         y (+ y-offset char-height)
         elements (let [x0 x-offset
                        y0 (+ y-offset char-height)
                        x1 (+ x0 char-width)
                        y1 y-offset
                        txt-size 22
                        txt (text/text grp [x0 y0] " ")
                        box (rect/rectangle grp [x0 y0][x1 y1])]
                    (set-attributes! txt :active :color @active* :style 0 :size txt-size :hide :no)
                    (set-attributes! txt :inactive :hide true)
                    (set-attributes! box :active :hide :no)
                    (set-attributes! box :inactive :color @inactive* :style 0 :fill false :hide :no)
                    [txt box])
         obj (reify sgwr.indicators.char/CharDisplay

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
