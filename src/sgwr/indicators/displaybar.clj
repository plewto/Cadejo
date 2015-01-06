(ns sgwr.indicators.displaybar
  (:require [sgwr.cs.native :as native])
  (:require [sgwr.elements.group :as group])
  (:require [sgwr.indicators.char])
  (:require [sgwr.indicators.sixteen :as sixteen])
  (:require [sgwr.indicators.basic-char :as basic])
  (:require [sgwr.indicators.dot-matrix :as matrix])
  ;(:require [sgwr.util.color :as uc])
)

(def char-width sgwr.indicators.char/char-width)
(def char-height sgwr.indicators.char/char-height)
(def gap 5)
(def pad 4)

(defprotocol DisplayBar

  (colors! 
    [this inactive active])

  (character-count
    [this])

  (all-off!
    [this render?]
    [this])

  (display! 
    [this text render?]
    [this text])

  (get-group
    [this])
  
  

  
  )


(defn- get-char-constructor [selector]
  (if (fn? selector) 
      selector
      (get {:basic basic/basic-char
            :sixteen sixteen/char-16
            16 sixteen/char-16
            :matrix matrix/matrix-char}
           selector
           basic/basic-char)))

(defn displaybar 

  ([char-count ctype] 
   (let [w (+ (* 2 pad)(* char-count (+ char-width gap)))
         h (+ (* 2 pad) char-height)
         x-offset pad
         y-offset pad
         cs (native/native-coordinate-system w h)
         grp (group/group nil :id :displaybar)]
     (displaybar grp x-offset y-offset char-count ctype)))

  ([grp x-offset y-offset char-count ctype]
   (let [elements (let [chr-fn (get-char-constructor ctype)
                        acc* (atom [])
                        w (+ char-width gap)]
                    (dotimes [i char-count]
                      (let [x (+ x-offset (* i w))
                            y y-offset
                            cobj (chr-fn grp x y)]
                        (swap! acc* (fn [q](conj q cobj)))))
                    @acc*)
         render-drawing (fn [flag]
                          (let [drw (.get-property grp :drawing)]
                            (if (and drw flag)
                              (.render drw))))
         obj (reify DisplayBar
               
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
               
               (display! [this text render?]
                 (.all-off! this false)
                 (let [i* (atom 0)
                       limit (min (count text)(.character-count this))]
                   (while (< @i* limit)
                     (.display! (nth elements @i*)(nth text @i*))
                     (swap! i* inc))
                   (render-drawing render?)))
               
               (display! [this text]
                 (.display! this text true))
               
               (get-group [this] grp))]
     obj)))
                  
;;; TEST TEST TEST TEST TEST TEST   ;;; TEST TEST TEST TEST TEST TEST   ;;; TEST TEST TEST TEST TEST TEST   

(require '[seesaw.core :as ss])
(require '[sgwr.elements.drawing :as drw])

(def drw (drw/native-drawing 600 600))
(def root (.root drw))
(.background! drw :black)

;; (def dbar1 (displaybar root 20 100 8 :matrix))
;; (def dbar2 (displaybar root 20 200 8 16))
;; (def dbar3 (displaybar root 20 300 8 :basic))


(def dbar1 (displaybar 8 :matrix))

(.set-parent! (.get-group dbar1) root)

(def barlst [dbar1   ])

(defn foo [text]
  (doseq [db barlst]
    (.display! db text))
  (.render drw))



(.render drw)
(def f (ss/frame :content (.canvas drw)
                 :on-close :dispose
                 :size [600 :by 600]))
(ss/show! f)


(defn rl [] (use 'sgwr.indicators.displaybar :reload))
(defn rla [] (use 'sgwr.indicators.displaybar :reload-all))
