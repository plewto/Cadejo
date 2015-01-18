(ns sgwr.widgets.button
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:import javax.swing.SwingUtilities))


(defn blank-button [parent id & {:keys [drag-action move-action enter-action exit-action
                                        press-action release-action click-action]
                                 :or {drag-action nil
                                      move-action nil
                                      enter-action nil
                                      exit-action nil
                                      press-action nil
                                      release-action nil
                                      click-action nil}}]
  (let [grp (group/group parent :etype :button :id id)
        dummy-action (fn [obj ev] nil)]
    (.put-property! grp :action-mouse-dragged  (or drag-action dummy-action))
    (.put-property! grp :action-mouse-moved    (or move-action dummy-action))
    (.put-property! grp :action-mouse-entered  (or enter-action dummy-action))
    (.put-property! grp :action-mouse-exited   (or exit-action dummy-action))
    (.put-property! grp :action-mouse-pressed  (or press-action dummy-action))
    (.put-property! grp :action-mouse-released (or release-action dummy-action))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    grp))

(defn icon-button [parent p0 prefix group subgroup & {:keys [id
                                                             drag-action move-action enter-action exit-action
                                                             press-action release-action click-action
                                                             gap w h 
                                                             box-color box-style box-width box-radius fill-box?]
                                                       :or {id nil
                                                            drag-action nil
                                                            move-action nil
                                                            enter-action nil
                                                            exit-action nil
                                                            press-action nil
                                                            release-action nil
                                                            click-action nil
                                                            gap 4
                                                            w 44
                                                            h 44
                                                            box-color (uc/color :gray)
                                                            box-style 0
                                                            box-width 2.0
                                                            box-radius 12
                                                            fill-box? false}}]
  (let [grp (blank-button parent (keyword (format "%s-%s" (name group) (name subgroup)))
                          :drag-action drag-action 
                          :move-action move-action 
                          :enter-action enter-action 
                          :exit-action exit-action 
                          :press-action press-action 
                          :release-action release-action 
                          :click-action click-action)
        [x0 y0] p0
        x1 (+ x0 gap)
        y1 (+ y0 gap)
        box (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :box
                            :color (uc/color box-color)
                            :style box-style
                            :width box-width
                            :fill fill-box?)
        icon (image/read-icon grp [x1 y1] prefix group subgroup)]
    (.put-property! box :corner-radius box-radius)
    (.put-property! grp :box box)
    (.put-property! grp :icon icon)
    (.use-attributes! grp :default)
    grp))
                            
  
(defn mini-icon-button [parent p0 prefix subgroup & {:keys [id
                                                            drag-action move-action enter-action exit-action
                                                            press-action release-action click-action
                                                            gap w h 
                                                            box-color box-style box-width box-radius fill-box?]
                                                     :or {id nil
                                                          drag-action nil
                                                          move-action nil
                                                          enter-action nil
                                                          exit-action nil
                                                          press-action nil
                                                          release-action nil
                                                          click-action nil
                                                          gap 4
                                                          w 26
                                                          h 26
                                                          box-color (uc/color :gray)
                                                          box-style 0
                                                          box-width 1.0
                                                          box-radius 12
                                                          fill-box? false}}]
  (icon-button parent p0 prefix :mini subgroup
               :id id
               :drag-action drag-action 
               :move-action move-action 
               :enter-action enter-action 
               :exit-action exit-action 
               :press-action press-action 
               :release-action release-action 
               :click-action click-action 
               :gap gap 
               :w w 
               :h h 
               :box-color box-color 
               :box-style box-style 
               :box-width box-width 
               :box-radius box-radius 
               :fill-box? fill-box?))


(defn text-button [parent p0 txt & {:keys [id 
                                           drag-action move-action enter-action exit-action
                                           press-action release-action click-action
                                           text-color text-style text-size
                                           gap text-x-shift text-y-shift w h
                                           box-color box-style box-width box-radius fill-box?]
                                    :or {id nil
                                         drag-action nil
                                         move-action nil
                                         enter-action nil
                                         exit-action nil
                                         press-action nil
                                         release-action nil
                                         click-action nil
                                         text-color (uc/color :white)
                                         text-stye 0
                                         text-size 8
                                         gap 4
                                         text-x-shift 0
                                         text-y-shift 0
                                         w nil
                                         h nil
                                         box-color (uc/color :gray)
                                         box-style 0
                                         box-width 2.0
                                         box-radius 12
                                         fill-box? false}}]
  (let [grp (blank-button parent txt
                          :drag-action drag-action 
                          :move-action move-action 
                          :enter-action enter-action 
                          :exit-action exit-action 
                          :press-action press-action 
                          :release-action release-action 
                          :click-action click-action)
        est-tx-width (text/estimate-monospaced-width (* (count txt) text-size))
        est-tx-height (text/estimate-monospaced-height text-size)
        width (or w (+ (* 2 gap) est-tx-width))
        height (or h (+ (* 2 gap) est-tx-height))
        [x0 y0] p0
        x1 (+ x0 gap text-x-shift)
        x3 (+ x0 width)
        y3 (+ y0 height)
        yc (math/mean y0 y3)
        y1 (+ yc (* 1/2 est-tx-height) text-y-shift)
        box (rect/rectangle grp p0 [x3 y3] :id :box
                            :color box-color
                            :style box-style
                            :width box-width
                            :fill fill-box?)
        txobj (text/text grp [x1 y1] txt :id :text
                         :color text-color
                         :style text-style
                         :size text-size)]
    (.color! txobj :rollover text-color)
    (.put-property! box :corner-radius box-radius)
    (.put-property! grp :box box)
    (.put-property! grp :text-element txobj)
    (.use-attributes! grp :default)
    grp))
                         
                            
        
        
        

