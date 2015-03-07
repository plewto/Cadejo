(ns sgwr.tools.button
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.components.component :only [set-attributes!]])
  (:require [sgwr.components.group :as group])
  (:require [sgwr.components.image :as image])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:import javax.swing.SwingUtilities))

(let [counter* (atom 0)]
  (defn- get-button-id [prefix id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "%s-%d" prefix n))))))

(defn- dummy-action [& _])

(defn- compose-action [afn]
  (fn [button ev]
    (let [flag (.local-property button :enabled)]
      (if flag (afn button ev)))))

(defn- blank-button [parent id & {:keys [drag-action move-action enter-action exit-action
                                        press-action release-action click-action]
                                  :or {drag-action dummy-action
                                       move-action dummy-action
                                       enter-action dummy-action
                                       exit-action dummy-action
                                       press-action dummy-action
                                       release-action dummy-action
                                       click-action dummy-action}}]
  (let [grp (group/group parent :etype :button :id id)]
    (.put-property! grp :action-mouse-dragged  (compose-action drag-action))
    (.put-property! grp :action-mouse-moved    (compose-action move-action))
    (.put-property! grp :action-mouse-entered  (compose-action enter-action))
    (.put-property! grp :action-mouse-exited   (compose-action exit-action))
    (.put-property! grp :action-mouse-pressed  (compose-action press-action))
    (.put-property! grp :action-mouse-released (compose-action release-action))
    (.put-property! grp :action-mouse-clicked  (compose-action click-action))
    grp))

(defn icon-button [parent p0 prefix group subgroup & {:keys [id
                                                             drag-action move-action enter-action exit-action
                                                             press-action release-action click-action
                                                             gap w h 
                                                             rim-color rim-style rim-width rim-radius]
                                                       :or {id nil
                                                            drag-action dummy-action
                                                            move-action dummy-action
                                                            enter-action dummy-action
                                                            exit-action dummy-action
                                                            press-action dummy-action
                                                            release-action dummy-action
                                                            click-action dummy-action
                                                            gap 4
                                                            w 44
                                                            h 44
                                                            rim-color :gray
                                                            rim-style 0
                                                            rim-width 2.0
                                                            rim-radius 12}}]
  "(icon-button parent p0 prefix group subgroup
         :id :drag-action :move-action :enter-action :exit-action 
         :press-action :release-action :click-action
         :gap :w :h :rim-color :rim-style :rim-width :rim-radius)

   Creates button with icon from resource directory.

   parent     - SgwrComponent, the parent group
   p0         - vector [x0 y0] position of button
   prefix     - keyword, icon name prefix, one of :black :gray or :white
   group      - keyword, icon name main-group, :general :wave, :filter etc...
   subgroup   - keyword, icon specific icon, :skin, :open, :save etc...
   :id        - keyword, button id, if not specified an id is automatically generated
   :gap       - number, space around icon, default 4
   :w         - number, button width default 44
   :h         - number, button height default 44
   :rim-color - Color, keyword, vector, see sgwr.util.color/color
   :rim-style - int, keyword, rim line style, default :solid
   :rim-width - float, rim line width, default 1.0
   :rim-radius - int, rim corner radius, default 12

    Actions

       :drag-action, :move-action, :enter-action, :exit-action, 
       :press-action, :release-action, :click-action     

       Function of form (fn [obj ev] ...) where obj is this tool
       and ev is an instance of java.awt.event.MouseEvent 

   Returns SgwrComponent group"   
   
  (let [grp (blank-button parent (or id (keyword (format "%s-%s" (name group) (name subgroup))))
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
        rim (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :rim
                            :color rim-color
                            :style rim-style
                            :width rim-width
                            :fill :no)
        icon (image/read-icon grp [x1 y1] prefix group subgroup)]
    (.put-property! rim :corner-radius rim-radius)
    (.put-property! grp :rim rim)
    (.put-property! grp :icon icon)
    (.use-attributes! grp :default)
    grp))
                            
  
(defn mini-icon-button [parent p0 prefix subgroup & {:keys [id
                                                            drag-action move-action enter-action exit-action
                                                            press-action release-action click-action
                                                            gap w h 
                                                            rim-color rim-style rim-width rim-radius]
                                                     :or {id nil
                                                          drag-action dummy-action
                                                          move-action dummy-action
                                                          enter-action dummy-action
                                                          exit-action dummy-action
                                                          press-action dummy-action
                                                          release-action dummy-action
                                                          click-action dummy-action
                                                          gap 4
                                                          w 26
                                                          h 26
                                                          rim-color :gray
                                                          rim-style 0
                                                          rim-width 1.0
                                                          rim-radius 12}}]
  "(mini-icon-button parent p0 prefix subgroup 
         :id :drag-action :move-action :enter-action :exit-action 
         :press-action :release-action :click-action
         :gap :w :h :rim-color :rim-style :rim-width :rim-radius)
 
   Convenience function for creating 'mini' icons. Same as calling 
   (icon-button parent p0 prefix :mini subgroup :w 24 :h 24)

   Returns SgwrComponent group"
                                        
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
               :rim-color rim-color 
               :rim-style rim-style 
               :rim-width rim-width 
               :rim-radius rim-radius 
               :fill-rim? :no))

(defn text-button [parent p0 txt & {:keys [id 
                                           drag-action move-action enter-action exit-action
                                           press-action release-action click-action
                                           text-color text-style text-size
                                           gap text-x-shift text-y-shift w h
                                           rim-color rim-style rim-width rim-radius]
                                    :or {id nil
                                         drag-action dummy-action
                                         move-action dummy-action
                                         enter-action dummy-action
                                         exit-action dummy-action
                                         press-action dummy-action
                                         release-action dummy-action
                                         click-action dummy-action
                                         text-color :white
                                         text-style 0
                                         text-size 8
                                         gap 4
                                         text-x-shift 0
                                         text-y-shift 0
                                         w nil
                                         h nil
                                         rim-color :gray
                                         rim-style 0
                                         rim-width 2.0
                                         rim-radius 12}}]
  "(text-button parent p0 txt 
         :id :drag-action :move-action :enter-action :exit-action
         :press-action :release-action :click-action
         :text-color :text-style :text-size 
         :text-x-shift :text-y-shift :gap :w :h
         :rim-color :rim-style :rim-width :rim-radius)

    Creates text button

    parent - SgwrComponent, the parent group
    p0     - vector [x0 y0], position of button
    txt    - String, the text

    :text-color   - Color, keyword or vector, see sgwr.util.color/color
    :text-style   - int or keyword, sets font, default :mono
    :text-size    - float, sets text size, default 8
    :text-x-shift - float, value added to text x coordinate, default 0
    :text-y-shift - float, value added to text y coordinate, default 0
    
    :gap - float, space around text, default 4
    :w   - float, button width. Unless specified button width is 
           guessed using font size and character count, default nil
    :h   - float, button height. Unless specified button height
           is guessed using font size, default nil

    :rim-color  - Color, keyword or vector
    :rim-style  - int or keyword, the rim line dash pattern, default :solid
    :rim-width  - float, the rim line width, default 1.0
    :rim-radius - int, rim corner radius, default 12

    Actions

      :drag-action, :move-action, :enter-action, :exit-action,
      :press-action, :release-action, :click-action
       
       Function of form (fn [obj ev] ...) where obj is this tool
       and ev is an instance of java.awt.event.MouseEvent 

   Returns SgwrComponent group"    


  (let [grp (blank-button parent (get-button-id "text-button" id)
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
        rim (rect/rectangle grp p0 [x3 y3] :id :rim
                            :color rim-color
                            :style rim-style
                            :width rim-width
                            :fill :no)
        txobj (text/text grp [x1 y1] txt :id :text
                         :color text-color
                         :style text-style
                         :size text-size)]
    (.color! txobj :rollover text-color)
    (.put-property! rim :corner-radius rim-radius)
    (.put-property! grp :rim rim)
    (.put-property! grp :text-component txobj)
    (.use-attributes! grp :default)
    grp))
