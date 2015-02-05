
(ns sgwr.indicators.displaybar
  "Defines general display bar in terms of 'cells'
   Where each cell implements sgwr.indicators.cell/Cell"
  (:use [cadejo.util.trace])
  (:require [sgwr.cs.native :as native])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.indicators.basic-cell :as basic])
  (:require [sgwr.indicators.dot-matrix :as matrix])
  (:require [sgwr.indicators.sixteen :as sixteen])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.utilities :as utilities])
  (:import javax.swing.JOptionPane))

(defprotocol DisplayBar

  (parent 
    [this]
    "(parent this)
     Returns parent group of this")
  
  (colors! 
    [this inactive active]
    "(colors! this active inactive)
     Sets active and inactive component colors
     Color arguments may be Color, keyword or vector, see sgwr.util.color")
 
  (character-count
    [this]
    "(character-count this)
     Returns the character count")

  (disable! 
    [this render?]
    [this])

  (enable!
    [this render?]
    [this])

  (all-off!
    [this render?]
    [this]
    "(all-off! this render?)
     (all-off! this)
     Sets all display components to inactive color
     If render? true render containing drawing
     render? is true by default")

  (current-display
    [this]
    "(current-display this)
     Returns current display content as string")

  (display! 
    [this text render?]
    [this text]
    "(display! this text render?)
     (display! this text)
     Sets display to text, If length of text is greater then character count
     truncate text. 
     If render? is true render drawing containing this, render? is true by default"))

(defn- get-cell-constructor [selector]
  (if (fn? selector) 
      selector
      (get {:basic basic/basic-cell
            :sixteen sixteen/cell-16
            16 sixteen/cell-16
            :matrix matrix/matrix-cell}
           selector
           basic/basic-cell)))

(defn displaybar 
  ;; ctype - one of :basic, 16 or :sixteen, :matrix
  ([grp x-offset y-offset char-count ctype & {:keys [cell-width cell-height] 
                                              :or {cell-width 25
                                                   cell-height 35}}]
   (let [pad 4
         gap 5
         bar-width (+ (* char-count (+ cell-width gap)))
         components (let [chr-fn (get-cell-constructor ctype)
                        acc* (atom [])
                        w (+ cell-width gap)]
                    (dotimes [i char-count]
                      (let [x (+ x-offset (* i w))
                            y y-offset
                            cobj (chr-fn grp x y :cell-width cell-width :cell-height cell-height)] 
                        (swap! acc* (fn [q](conj q cobj)))))
                    @acc*)
         colors* (atom [:black :gray])
         occluder (let [occ (rect/rectangle grp [(- x-offset pad) (- y-offset pad)][(+ x-offset bar-width (- pad))(+ y-offset cell-height pad)]
                                            :color [0 0 0 0]
                                            :fill true)]
                    (.color! occ :disabled (uc/transparent :black 190))
                    (.color! occ :enabled [0 0 0 0])
                    occ)
         current-value* (atom "")
         render-drawing (fn [flag]
                          (let [drw (.get-property grp :drawing)]
                            (if (and drw flag)
                              (.render drw))))
         obj (reify DisplayBar
               
               (parent [this] grp)

               (colors! [this inactive active]
                 (reset! colors* [inactive active])
                 (doseq [e components]
                   (.colors! e inactive active)))
         
               (character-count [this]
                 (count components))
            
               (disable! [this render?]
                 (.use-attributes! occluder :disabled)
                 (render-drawing render?))

               (disable! [this]
                 (.disable! this :render))

               (enable! [this render?]
                 (.use-attributes! occluder :enabled)
                 (render-drawing render?))

               (enable! [this]
                 (.enable! this :render))

               (all-off! [this render?]
                 (doseq [e components]
                   (.display! e \space))
                 (render-drawing render?))
               
               (all-off! [this]
                 (.all-off! this true))
               
               (current-display [this]
                 @current-value*)

               (display! [this text render?]
                 (.all-off! this false)
                 (let [i* (atom 0)
                       limit (min (count text)(.character-count this))]
                   (reset! current-value* "")
                   (while (< @i* limit)
                     (let [c (nth text @i*)]
                       (.display! (nth components @i*)c)
                       (swap! current-value* (fn [q](str q c)))
                       (swap! i* inc)))
                   (render-drawing render?)))
               
               (display! [this text]
                 (.display! this text true)) )]
     obj)))




(defn displaybar-dialog [dbar message & {:keys [validator callback]
                                         :or {validator (constantly true)
                                              callback (fn [src] nil)}}]
  "(displaybar-dialog dbar msg :validator :callback)
   Show pop up modal dialog to set displaybar's contents
   
   dbar       - DisplayBar
   msg        - String, user prompt text
   :validator - Predicate, (fn q) --> Boolean 
                Returns true if entered text is valid,
                If text is not valid the dialog is redisplayed.
   :callback  - Function (fn src), callback is called after text has been entered to 
                inform any interested parties, the single src argument is
                set to dbar"

  (let [jop (JOptionPane.)
        dflt (.current-display dbar)
        rs (JOptionPane/showInputDialog jop message dflt)]
    (if rs
      (if (validator rs)
        (do
          (.display! dbar (str rs) :render)
          (callback dbar))
        (do ;; invalid input
          (displaybar-dialog dbar message :validator validator :callback callback)))
      nil)))
