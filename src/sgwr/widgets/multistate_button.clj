(ns sgwr.widgets.multistate-button
  (:require [sgwr.constants :as constants])  
  (:use [sgwr.elements.element :only [set-attributes!]])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.elements.image :as image])
  (:require [sgwr.elements.point :as point])
  (:require [sgwr.elements.rectangle :as rect])
  (:require [sgwr.elements.text :as text])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.util.utilities :as utilities]))


(let [counter* (atom 0)]
  (defn- get-button-id [prefix id]
    (let [n @counter*]
      (swap! counter* inc)
      (or id (keyword (format "%s-%d" prefix n))))))

(defn- third [col](nth col 2))

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

(defn select-checkbox! [cb flag]
  (if flag
    (do 
      (set-multistate-button-state! cb 1)
      (.select! cb true))
    (do 
      (set-multistate-button-state! cb 0)
      (.select! cb false))))

(def select-toggle-button! select-checkbox!)

      
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
                                                         pad-color
                                                         box-color box-style box-width box-radius]
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
                                                       pad-color [0 0 0 0]
                                                       box-color (uc/color :gray)
                                                       box-style 0
                                                       box-width 1.0
                                                       box-radius 12}}]
  (let [grp (blank-multistate-button parent (map first states) (get-button-id "multistate-button" id)
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
        pad (rect/rectangle grp p0 [x2 y2]
                            :id :box
                            :color pad-color
                            :style 0
                            :width 1.0
                            :fill true)
        box (rect/rectangle grp p0 [x2 y2]
                            :id :box
                            :color box-color
                            :style box-style
                            :width box-width
                            :fill :no)
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
                         (swap! i* inc)))
                     @acc*)]
    (doseq [k (map first states)]
      (doseq [tx txtobj-lst]
        (let [actvs (.get-property tx :active-state)]
          (if (= actvs k)
            (.hide! tx actvs :no)
            (.hide! tx k true)))))
    (.color! pad :rollover pad-color)
    (.put-property! pad :corner-radius box-radius)
    (.put-property! box :corner-radius box-radius)
    (.put-property! grp :pad pad)
    (.put-property! grp :box box)
    (.put-property! grp :text-objects txtobj-lst)
    (.use-attributes! grp (first (first states)))
    grp))

;; states a nested list of form 
;; [[key-0 igroup-0 isubgroup-0][key-1 igroup-1 isubgroup-1] ...]
;;
(defn icon-multistate-button [parent p0 states  & {:keys [id
                                                          drag-action move-action enter-action exit-action
                                                          press-action release-action click-action
                                                          icon-prefix gap w h
                                                          pad-color
                                                          box-color box-style box-width box-radius]
                                                  :or {id nil
                                                       drag-action nil
                                                       move-action nil
                                                       enter-action nil
                                                       exit-action nil
                                                       press-action nil
                                                       release-action nil
                                                       click-action nil
                                                       icon-prefix :white
                                                       gap 4
                                                       w 44
                                                       h 44
                                                       pad-color [0 0 0 0]
                                                       box-color (uc/color :gray)
                                                       box-style 0
                                                       box-width 2.0
                                                       box-radius 12}}]
  (let [grp (blank-multistate-button parent (map first states) (get-button-id "multistate-button" id)
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
        pad (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :box
                            :color pad-color
                            :style 0
                            :width 1.0
                            :fill true)
        box (rect/rectangle grp p0 [(+ x0 w)(+ y0 h)] :id :box
                            :color box-color
                            :style box-style
                            :width box-width
                            :fill :no)
        iobj-lst (let [acc* (atom [])
                       i* (atom 0)]
                   (doseq [[attkey igroup isubgroup] states]
                     (let [iobj (image/read-icon grp [x1 y1] icon-prefix igroup isubgroup)]
                       (.put-property! iobj :active-state attkey)
                       (.hide! iobj :default true)
                       (swap! acc* (fn [q](conj q iobj)))))
                   @acc*)]
    (doseq [k (map first states)]
      (doseq [iobj iobj-lst]
        (let [active (.get-property iobj :active-state)]
          (if (= active k)
            (.hide! iobj k :no)
            (.hide! iobj k true)))))
    (.color! pad :rollover pad-color)
    (.put-property! pad :corner-radius box-radius)
    (.put-property! box :corner-radius box-radius)
    (.put-property! grp :pad pad)
    (.put-property! grp :box box)
    (.use-attributes! grp (first (first states)))
    grp)) 
                     



;; selected-check & unselected-check arguments are vectors of form
;; [color style size]
;;                      
(defn checkbox [parent p0 txt  & {:keys [id
                                         drag-action move-action enter-action exit-action
                                         press-action release-action click-action
                                         text-color text-style text-size 
                                         gap text-x-shift text-y-shift
                                         box-color box-style box-size box-radius
                                         selected-check unselected-check]
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
                                       gap 6
                                       text-x-shift 0
                                       text-y-shift 0
                                       box-color (uc/color :gray)
                                       box-style 0
                                       box-size 12 ;; in pixels
                                       box-radius 0
                                       selected-check [:white :x 2]
                                       unselected-check [:gray :pixel 1]}}]
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "checkbox" id)
                                    :drag-action drag-action 
                                    :move-action move-action 
                                    :enter-action enter-action 
                                    :exit-action exit-action 
                                    :press-action press-action 
                                    :release-action release-action 
                                    :click-action click-action)
        [x0 y0] p0
        x1 (+ x0 box-size)
        y1 (+ y0 box-size)
        xc (math/mean x0 x1)
        yc (math/mean y0 y1)
        est-tx-height (text/estimate-monospaced-height text-size)
        x2 (+ x1 gap text-x-shift)
        y2 (+ yc (* 1/2 est-tx-height) text-y-shift)
        box (let [bx (rect/rectangle grp p0 [x1 y1] :id :box
                                     :color box-color
                                     :style box-style
                                     :fill :no)]
              (.put-property! bx :corner-radius box-radius)
              bx)
        pnt (let [sc (or (first selected-check) :white)
                  st (or (second selected-check) :x)
                  sz (or (third selected-check) 2)
                  uc (or (first unselected-check) :gray)
                  ut (or (second unselected-check) :pixel)
                  uz (or (third unselected-check) 1)
                  pnt (point/point grp [xc yc] :id :point)]
              (.color! pnt :selected sc)
              (.style! pnt :selected st)
              (.size!  pnt :selected sz)
              (.color! pnt :unselected uc)
              (.style! pnt :unselected ut)
              (.size!  pnt :unselected uz)
              pnt)
        txobj (text/text grp [x2 y2] txt
                         :color text-color
                         :id :text
                         :style text-style
                         :size text-size)]
    (.put-property! grp :box box)
    (.put-property! grp :point pnt)
    (.put-property! grp :text-element txobj)
    (.use-attributes! grp :unselected)
    grp))
    
                    
;; selected-box & unselected-box  [color style width]
;;
(defn text-toggle-button [parent p0 txt & {:keys [id 
                                                  drag-action move-action enter-action exit-action
                                                  press-action release-action click-action
                                                  selected-text-color unselected-text-color text-style text-size
                                                  gap text-x-shift text-y-shift w h
                                                  selected-pad-color unselected-pad-color
                                                  selected-box unselected-box box-radius]
                                           :or {id nil
                                                drag-action nil
                                                move-action nil
                                                enter-action nil
                                                exit-action nil
                                                press-action nil
                                                release-action nil
                                                click-action nil
                                                selected-text-color :white
                                                unselected-text-color :white
                                                text-stye 0
                                                text-size 8
                                                gap 4
                                                text-x-shift 0
                                                text-y-shift 0
                                                w nil
                                                h nil
                                                selected-pad-color [0 128 128 64]
                                                unselected-pad-color [0 0 0 0]
                                                selected-box [:green :solid 2.0]
                                                unselected-box [:gray :solid 1.0]
                                                box-radius 12}}]
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "toggle-button" id)
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
        pad (let [pad (rect/rectangle grp p0 [x3 y3] :id :pad
                                      :color [0 0 0 0]
                                      :style 0
                                      :width 1.0
                                      :fill true)]
              (.color! pad :unselected unselected-pad-color)
              (.color! pad :selected selected-pad-color)
              (.color! pad :rollover selected-pad-color)
              (.put-property! pad :corner-radius box-radius)
              pad)
        box (let [bx (rect/rectangle grp p0 [x3 y3] :id :box
                                     :color :gray
                                     :style 0
                                     :width 1.0
                                     :fill :no)]
              (.color! bx :unselected (or (first unselected-box) [64 64 64]))
              (.style! bx :unselected (or (second unselected-box) :solid))
              (.width! bx :unselected (or (third unselected-box) 1.0))
              (.color! bx :selected (or (first selected-box) :white))
              (.style! bx :selected (or (second selected-box) :solid))
              (.width! bx :selected (or (third selected-box) 2.0))
              (.put-property! bx :corner-radius box-radius)
              bx)
        txobj (let [txobj (text/text grp [x1 y1] txt :id :text
                                     :color :white
                                     :style text-style
                                     :size text-size)]
                (.color! txobj :unselected unselected-text-color)
                (.color! txobj :selected selected-text-color)
                (.color! txobj :rollover selected-text-color)
                txobj)]
    (.put-property! grp :pad pad)
    (.put-property! grp :box box)
    (.put-property! grp :text-element txobj)
    (.use-attributes! grp :unselected)
    grp))
                                      

;; selected-box & unselected-box  [color style width]
;;
(defn icon-toggle-button [parent p0 prefix group subgroup  & {:keys [id 
                                                                     drag-action move-action enter-action exit-action
                                                                     press-action release-action click-action
                                                                     gap w h
                                                                     selected-pad-color unselected-pad-color
                                                                     selected-box unselected-box box-radius]
                                                              :or {id nil
                                                                   drag-action nil
                                                                    move-action nil
                                                                    enter-action nil
                                                                    exit-action nil
                                                                    press-action nil
                                                                    release-action nil
                                                                    click-action nil
                                                                    gap 7
                                                                    w 44
                                                                    h 44
                                                                    selected-pad-color [0 128 128 64]
                                                                    unselected-pad-color [0 0 0 0]
                                                                    selected-box [:green :solid 2.0]
                                                                    unselected-box [:gray :solid 1.0]
                                                                    box-radius 12}}]
  (let [states [:unselected :selected]
        grp (blank-multistate-button parent states 
                                     (get-button-id "toggle-button" id)
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
        x2 (+ x1 w)
        y2 (+ y1 h)
        dummy-100 (println (format "DEBUG [x0 y0] -> %s   gap = %s    [x1 y1] -> %s" p0 gap [x1 y1]))
        pad (let [pad (rect/rectangle grp p0 [x2 y2] :id :pad
                                      :color [0 0 0 0]
                                      :style 0
                                      :width 1.0
                                      :fill true)]
              (.color! pad :unselected unselected-pad-color)
              (.color! pad :selected selected-pad-color)
              (.color! pad :rollover selected-pad-color)
              (.put-property! pad :corner-radius box-radius)
              pad)
        box (let [bx (rect/rectangle grp p0 [x2 y2] :id :box
                                     :color :gray
                                     :style 0
                                     :width 1.0
                                     :fill :no)]
              (.color! bx :unselected (or (first unselected-box) [64 64 64]))
              (.style! bx :unselected (or (second unselected-box) :solid))
              (.width! bx :unselected (or (third unselected-box) 1.0))
              (.color! bx :selected (or (first selected-box) :white))
              (.style! bx :selected (or (second selected-box) :solid))
              (.width! bx :selected (or (third selected-box) 2.0))
              (.put-property! bx :corner-radius box-radius)
              bx)
        icon (image/read-icon grp [x1 y1] prefix group subgroup)]
    (.put-property! grp :pad pad)
    (.put-property! grp :box box)
    (.put-property! grp :icon icon)
    (.use-attributes! grp :unselected)
    grp))

               
