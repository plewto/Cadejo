
(ns sgwr.indicators.displaybar
  "Defines general display bar in terms of 'cells'
   Where each cell implements sgwr.indicators.cell/Cell"
  (:require [sgwr.cs.native :as native])
  (:require [sgwr.indicators.basic-cell :as basic])
  (:require [sgwr.indicators.dot-matrix :as matrix])
  (:require [sgwr.indicators.sixteen :as sixteen])
  (:require [sgwr.util.utilities :as utilities])
  (:import javax.swing.JOptionPane))

(defprotocol DisplayBar

  (parent 
    [this])
  
  (colors! 
    [this inactive active])
 
  (character-count
    [this])

  (all-off!
    [this render?]
    [this])

  (current-display
    [this])

  (display! 
    [this text render?]
    [this text]))

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
   (let [elements (let [chr-fn (get-cell-constructor ctype)
                        acc* (atom [])
                        pad 4
                        gap 5
                        w (+ cell-width gap)]
                    (dotimes [i char-count]
                      (let [x (+ x-offset (* i w))
                            y y-offset
                            cobj (chr-fn grp x y :cell-width cell-width :cell-height cell-height)] 
                        (swap! acc* (fn [q](conj q cobj)))))
                    @acc*)
         current-value* (atom "")
         render-drawing (fn [flag]
                          (let [drw (.get-property grp :drawing)]
                            (if (and drw flag)
                              (.render drw))))
         obj (reify DisplayBar
               
               (parent [this] grp)

               (colors! [this inactive active]
                 (doseq [e elements]
                   (.colors! e inactive active)))
         
               (character-count [this]
                 (count elements))
               
               (all-off! [this render?]
                 (doseq [e elements]
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
                       (.display! (nth elements @i*)c)
                       (swap! current-value* (fn [q](str q c)))
                       (swap! i* inc)))
                   (render-drawing render?)))
               
               (display! [this text]
                 (.display! this text true)) )]
     obj)))




(defn displaybar-dialog [dbar message & {:keys [validator callback]
                                         :or {validator (constantly true)
                                              callback (fn [src] nil)}}]
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


;; TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST 

(require '[seesaw.core :as ss])
(require '[sgwr.elements.drawing :as drawing])
(require '[sgwr.widgets.button :as button])

(def drw (drawing/native-drawing 600 600))
(def dbar (displaybar (.root drw) 100 100 4 :basic))

(defn callback [obj]
  (println (format "Callback executed: %s" (.current-display obj))))

(defn validator [q]
  (not (= q "ERROR")))

(def b1 (button/text-button (.widget-root drw) [100 200] "Edit"
                            :click-action (fn [src _]
                                            (let [dia (displaybar-dialog dbar "Why Ask?"
                                                                         :callback callback
                                                                         :validator validator)
                                                  ]
                                              ))))
(.render drw)
(def pan-main (ss/horizontal-panel :items [(.canvas drw)]))

(def f (ss/frame
        :content pan-main
        :on-close :dispose
        :size [600 :by 600]))

(ss/show! f)
         
(defn rl [](use 'sgwr.indicators.displaybar :reload))
