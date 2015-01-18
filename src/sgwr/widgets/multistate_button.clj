(ns sgwr.widgets.multistate-button
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))


(defn set-multistate-button-state! 
  ([msb state-index]
   (set-multistate-button-state! msb state-index true))
  ([msb state-index render]
   (let [states (.get-property msb :states)
         state-count (count states)]
     (if (and (integer? state-index)
              (>= state-index 0)
              (< state-index state-count))
       (let [current (nth states state-index)]
         (.put-property! msb :current-state-index state-index)
         (.put-property! msb :current-state current)
         (.use-attributes! msb current)
         (if render (.render (.get-property msb :drawing))))
       (utilities/warning (format "Invalid state index %s for multistate-button %s"
                                  state-index (.get-property msb :id)))))))

;; Returns pair [index keyword] indicating current state
;;
(defn current-multistate-button-stae [msb]
  [(.get-property msb :current-state-index)
   (.get-property msb :current-state)])

      
(defn- compose-pressed-action 
  ([](compose-pressed-action (fn [& _])))
  ([pfn]
   (fn [obj env]
     (let [states (.get-property obj :states)
           csi (rem (inc (.get-property obj :current-state-index))
                    (count states))
           new-state (nth states csi)
           drw (.get-property obj :drawing)]
       (.put-property! obj :current-state-index csi)
       (.put-property! obj :current-state (nth states csi))
       (.use-attributes! obj new-state)
       (if drw (.render drw))
       (pfn obj env)))))
           
(defn- compose-exited-action 
  ([](compose-exited-action (fn [& _])))
  ([xfn]
   (fn [obj ev]
     (let [states (.get-property obj :states)
           csi (.get-property obj :current-state-index)]
       (.use-attributes! obj (nth states csi))
       (xfn obj ev)))))

;; states - an array of keywords - attribute names    
(defn blank-multistate-button [parent state-keys id  & {:keys [drag-action move-action enter-action exit-action
                                                      press-action release-action click-action]
                                               :or {drag-action nil
                                                    move-action nil
                                                    enter-action nil
                                                    exit-action nil
                                                    press-action nil
                                                    release-action nil
                                                    click-action nil}}]
  (let [grp (group/group parent :etype :multistate-button :id id)
        dummy-action (fn [obj ev] nil)]
    (.put-property! grp :states state-keys)
    (.put-property! grp :current-state-index 0)
    (.put-property! grp :current-state state-keys)
    (.put-property! grp :action-mouse-dragged  (or drag-action dummy-action))
    (.put-property! grp :action-mouse-moved    (or move-action dummy-action))
    (.put-property! grp :action-mouse-entered  (or enter-action dummy-action))
    (.put-property! grp :action-mouse-exited   (compose-exited-action (or exit-action dummy-action)))
    (.put-property! grp :action-mouse-pressed  (compose-pressed-action (or press-action dummy-action)))
    (.put-property! grp :action-mouse-released (or release-action dummy-action))
    (.put-property! grp :action-mouse-clicked  (or click-action dummy-action))
    grp))


;; states - a nested array of form 
;;          [[keyword-0 text-0 color-0][keyword-1 text-1 color-1] ... [keyword-n text-n color-n]]
;;          color may be nil to inherit from text-color argument
;;
(defn text-multistate-button [parent p0 states & {:keys [id
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
                                              text-style 0
                                              text-size 8
                                              gap 4
                                              text-x-shift 0
                                              text-y-shift 0
                                              w nil
                                              h nil
                                              box-color (uc/color :gray)
                                              box-style 0
                                              box-width 1.0
                                              box-radius 12
                                              fill-box? :no}}]
  (let [grp (blank-multistate-button parent (map first states) (or id :multistate-button)
                            :drag-action drag-action 
                            :move-action move-action 
                            :enter-action enter-action 
                            :exit-action exit-action 
                            :press-action press-action 
                            :release-action release-action 
                            :click-action click-action)
        state-text (map second states)
        max-text-length (apply max (map count state-text))
        est-tx-width (text/estimate-monospaced-width (* max-text-length text-size))
        est-tx-height (text/estimate-monospaced-height text-size)
        width (or w (+ est-tx-width (* 2 gap)))
        height (or h (+ est-tx-height (* 2 gap)))
        [x0 y0] p0
        x1 (+ x0 gap)
        x2 (+ x0 width text-x-shift)
        y2 (+ y0 height)
        yc (math/mean y0 y2)
        y1 (+ yc (* 1/2 est-tx-height) text-y-shift)
        box (rect/rectangle grp p0 [x2 y2]
                            :id :box
                            :color box-color
                            :style box-style
                            :width box-width
                            :fill fill-box?)
        txtobj-lst (let [acc* (atom [])
                         i* (atom 0)]
                     (doseq [[attkey txt col] states]
                       (let [txobj (text/text grp [x1 y1] (nth state-text @i*)
                                              :id (keyword (format "text-%d" @i*))
                                              :color (uc/color (or col text-color))
                                              :style text-style
                                              :size text-size)]
                         (.hide! txobj :default true)
                         (.color! txobj :rollover (uc/color (or col text-color)))
                         (.put-property! txobj :active-state attkey)
                         (swap! acc* (fn [q](conj q txobj)))
                         (swap! i* inc)
                         ))
                     @acc*)]
    (doseq [k (map first states)]
      (doseq [tx txtobj-lst]
        (let [actvs (.get-property tx :active-state)]
          (if (= actvs k)
            (.hide! tx actvs :no)
            (.hide! tx k true)))))
    (.put-property! box :corner-radius box-radius)
    (.put-property! grp :box box)
    (.put-property! grp :text-objects txtobj-lst)
    (.use-attributes! grp (first (first states)))
    grp))
