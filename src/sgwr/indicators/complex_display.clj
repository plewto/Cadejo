(ns sgwr.indicators.complex-display
  "Defines multi-segmented display elements.
   See sgwr.indicators.displaybar for aggregate sets of complex-display elements"
  (:require [sgwr.util.color :as uc])
  (:import java.lang.Math))


(defprotocol ComplexDisplay

  (colors! 
    [this inactive active]
    "Set the inactive and active element colors.
     See sgwr.util.color/color")

  (elements 
    [this]
    "Returns map of display elements")

  (supported-characters 
    [this]
    "Returns list of supported characters. 
     charters are either clojure char or integer.")

  (set-character!
    [this c]
    "Set this to display character c, if c is not supported display a space")

  (width 
    [this]
    "Returns width of this in pixels")

  (height
    [this]
    "Returns height of this in pixels"))


; ---------------------------------------------------------------------- 
;                               Sign Display
;      |
;    --+--  
;      |

(defn sign-display 
  ([drw x y]
     (sign-display drw x y 25 35))
  ([drw x y w h]
     (.style! drw 0)
     (.width! drw 1)
     (.color! drw (uc/color [32 32 32]))
     (let [inactive* (atom (uc/color [32 32 32]))
           active* (atom (uc/color :red))
           x0 (int (+ x (* 1/3 w)))
           x1 (int (+ x (* 2/3 w)))
           xc (int (* 1/2 (+ x0 x1)))
           y0 (int (+ y (* 1/3 h)))
           y1 (int (+ y (* 2/3 h)))
           yc (int (* 1/2 (+ y0 y1)))
           neg (.line! drw [x0 yc][x1 yc])
           pos (.line! drw [xc y0][xc y1])
           char-map {\space []
                     \+ [neg pos]
                     \- [neg]}
           all-off (fn []
                     (doseq [e [neg pos]]
                       (.color! (.attributes e) @inactive*)))
           sed (reify ComplexDisplay

                 (width [this] w)

                 (height [this] h)

                 (colors! [this inactive active]
                   (reset! inactive* (uc/color inactive))
                   (reset! active* (uc/color active))
                   [@inactive* @active*])

                 (elements [this]
                   {:pos pos :neg neg})

                 (supported-characters [this]
                   (keys char-map))

                 (set-character! [this c]
                   (all-off)
                   (doseq [e (get char-map c [])]
                     (.color! (.attributes e) @active*))))])
     ssd))

; ---------------------------------------------------------------------- 
;                             7 Segment Display
;
;         .....
;         .   .
;         .   .
;         .....
;         .   .
;         .   .
;         ..o..

(defn seven-segment-display 
  "Create 7-segment display and place on sgwr drawing
   drw - Instance of sgwr.drawing/Drawing. drw should have native 
         coordinate-system 
   x   - int, horizontal position of left-hand display edge 
   y   - int, vertical position of top-edge of display
   w   - int, width of display in pixels, default 24
   h   - int, height of display in pixels, default 38"
  ([drw x y]
     (seven-segment-display drw x y 24 38))
  ([drw x y w h]
     (let [inactive* (atom (uc/color [32 32 32]))
           active* (atom (uc/color :red))
           theta (Math/toRadians 8.0)
           tan-theta (Math/tan theta)
           dx1 (* 1/2 h tan-theta)
           dx2 (* h tan-theta)
           x0 x
           x1 (+ x0 dx1)
           x2 (+ x0 dx2)
           hbar-length w
           x3 (+ x0 hbar-length)
           x4 (+ x1 hbar-length)
           x5 (+ x2 hbar-length)
           x6 (* 1/2 (+ x0 x3))
           y0 y
           y2 (+ y h)
           y1 (* 1/2 (+ y0 y2))]
       (.style! drw 0)
       (.width! drw 1)
       (.color! drw @inactive*)
       (let [dp (do
                  (.fill! drw true)
                  (.circle! drw [x6 y2] 3))
             a (.line! drw [x2 y0][x5 y0])
             b (.line! drw [x5 y0][x4 y1])
             c (.line! drw [x4 y1][x3 y2])
             d (.line! drw [x0 y2][x3 y2])
             e (.line! drw [x0 y2][x1 y1])
             f (.line! drw [x1 y1][x2 y0])
             g (.line! drw [x1 y1][x4 y1])
             char-map {\space []
                       \. [dp]
                       \0 [a b c d e f]
                       \1 [b c]
                       \2 [a b g e d]
                       \3 [a b c d g]
                       \4 [f g b c]
                       \5 [a f g c d]
                       \6 [a f g e d c]
                       \7 [a b c]
                       \8 [a b c d e f g]
                       \9 [a b g f c d]
                       \a [a b g f e c]
                       \b [f e g c d]
                       \c [a f e d]
                       \d [b c g e d]
                       \e [a f e d g]
                       \f [a f g e]
                       \g [a b g f c d]
                       \h [f e b c g]
                       \i [b c]
                       \j [b c e d]
                       \l [f e d]
                       \n [e f a b c]
                       \o [a b c d e f]
                       \p [e f a b g]
                       \r [a b f e]
                       \q [a b g f c]
                       \s [a f g c d]
                       \u [f e d c b]
                       \A [a b g f e c]
                       \B [f e g c d]
                       \C [a f e d]
                       \D [b c g e d]
                       \E [a f e d g]
                       \F [a f g e]
                       \G [a b g f c d]
                       \H [f e b c g]
                       \I [b c]
                       \J [b c e d]
                       \L [f e d]
                       \N [e f a b c]
                       \O [a b c d e f]
                       \P [e f a b g]
                       \R [a b f e]
                       \Q [a b g f c]
                       \S [a f g c d]
                       \U [f e d c b]
                       \- [g]
                       \[ [a f e d]
                       \] [a b c d]}
             
             all-off (fn []
                       (doseq [q [a b c d e f g dp]]
                         (.color! (.attributes q) @inactive*)))

             ssd (reify ComplexDisplay

                   (width [this] w)

                   (height [this] h)

                   (colors! [this inactive active]
                     (reset! inactive* (uc/color inactive))
                     (reset! active* (uc/color active))
                     [@inactive* @active*])

                   (elements [this]
                     {:a a :b b :c c :d d :e e :f f :g g :dp dp})

                   (supported-characters [this]
                     (keys char-map))

                   (set-character! [this c]
                     (all-off)
                     (doseq [e (get char-map c [])]
                       (.color! (.attributes e) @active*))) )]
             ssd))))


; ---------------------------------------------------------------------- 
;                            16 Segment Display
;
;    .......
;    .\ . /.
;    . \./ .
;    ...x...
;    . /.\ .
;    ./ . \.
;    ...o...

(def ^:private sixteen-charmap 
  (let [acc* (atom {\space []
                    \. [:dp]
                    \0 [:a :b :c :d :e :f :g :h :k :o]
                    \1 [:c :d :k]
                    \2 [:a :b :c :l :p :g :f :e]
                    \3 [:a :b :c :d :e :f :l]
                    \4 [:h :p :l :c :d]
                    \5 [:a :b :h :p :l :d :e :f]
                    \6 [:a :h :p :l :d :e :f :g]
                    \7 [:a :b :c :d]
                    \8 [:a :b :c :d :e :f :g :h :p :l]
                    \9 [:a :b :c :l :p :h :d :e]
                    \A [:a :b :c :l :p :h :g :d]
                    \B [:a :b :c :d :e :f :j :l :n]
                    \C [:a :b :h :g :e :f]
                    \D [:a :b :c :d :e :f :j :n]
                    \E [:a :b :h :p :g :f :e]
                    \F [:a :b :h :p :g]
                    \G [:a :b :h :g :f :e :d :l]
                    \H [:h :g :c :d :p :l]
                    \I [:a :b :j :n :e :f]
                    \J [:c :d :e :f :g]
                    \K [:h :g :p :k :m]
                    \L [:h :g :f :e]
                    \M [:h :g :c :d :i :k]
                    \N [:h :g :c :d :i :m]
                    \O [:a :b :c :d :e :f :g :h]
                    \P [:a :b :c :l :p :h :g]
                    \Q [:a :b :c :d :e :f :g :h :m]
                    \R [:a :b :c :l :p :h :g :m]
                    \S [:a :b :h :p :l :d :e :f]
                    \T [:a :b :j :n]
                    \U [:h :g :f :e :c :d]
                    \V [:h :g :o :k]
                    \W [:h :g :c :d :o :m]
                    \X [:i :m :o :k]
                    \Y [:h :p :l :c :n]
                    \Z [:a :b :o :k :e :f]
                    \" [:j :c]
                    \$ [:a :b :h :p :l :d :e :f :j :n]
                    \% [:a :j :p :h :l :d :e :n :k :o]
                    \' [:k]
                    \& [:a :i :j :p :g :f :e :m]
                    \* [:i :j :k :p :l :o :n :m]
                    \+ [:j :l :n :p]
                    \` [:i]
                    \- [:p :l]
                    \/ [:k :o]
                    \( [:k :m]
                    \) [:i :o]
                    \[ [:a :h :g :f]
                    \] [:b :c :d :e]
                    \{ [:b :j :n :e :p]
                    \} [:a :j :n :f :l]
                    \= [:p :l :f :e]
                    \_ [:f :e]
                    \\ [:i :m]
                    \^ [:o :m]
                    256 [:a :b]
                    257 [:h :g]
                    258 [:e :f]
                    259 [:c :d]
                    260 [:j :n]
                    261 [:p :l]
                    262 [:g :p :l]
                    263 [:g :e :f]
                    264 [:f :e :d]
                    265 [:h :p :l :d]
                    266 [:g :p :l :c]
                    267 [:g :h :a :b]
                    268 [:h :g :e :f]
                    269 [:e :f :c :d]
                    270 [:a :b :c :d]
                    271 [:a :b :j :n]
                    272 [:h :g :p :l]
                    273 [:e :f :j :n]
                    274 [:p :l :c :d]
                    275 [:j :n :p :l]
                    276 [:i :m]
                    277 [:o :k]
                    278 [:i :k :o :m]
                    279 [:o :n :m]
                    280 [:i :j :k]
                    281 [:k :l :m]
                    282 [:i :p :o]
                    283 [:a :b :c :d :e :f :g :h]
                    287 [:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p]
                    290 [:h :g :j :n :c :d]
                    291 [:a :b :p :l :e :f]
                    293 [:i :o]
                    294 [:k :m]})]
    (doseq [[up low][[\A \a][\B \b][\C \c][\D \d][\E \e][\F \f][\G \g]
                  [\H \h][\I \i][\J \j][\K \k][\L \l][\M \m][\N \n]
                  [\O \o][\P \p][\Q \q][\R \r][\S \s][\T \t][\U \u]
                  [\V \v][\W \w][\X \x][\Y \y][\Z \z]]]
      (swap! acc* (fn [q](assoc q low (get q up)))))
    @acc*))


(defn sixteen-segment-display 
  "Create 16-segment display and place on sgwr drawing
   drw - Instance of sgwr.drawing/Drawing. drw should have native 
         coordinate-system 
   x   - int, horizontal position of left-hand display edge 
   y   - int, vertical position of top-edge of display
   w   - int, width of display in pixels, default 24
   h   - int, height of display in pixels, default 38"
  ([drw x y]
     (sixteen-segment-display drw x y 24 38))
  ([drw x y w h]
     (let [inactive* (atom (uc/color [32 32 32]))
           active* (atom (uc/color :red))
           x0 x
           x2 (+ x w)
           x1 (* 1/2 (+ x0 x2))
           y0 y
           y2 (+ y h)
           y1 (* 1/2 (+ y0 y2))]
       (.style! drw 0)
       (.width! drw 1)
       (.color! drw @inactive*)
       (.fill! drw true)
       (.suspend-render! drw true)
       (let [dp (.circle! drw [x1 y2] 3)
             a (.line! drw [x0 y0][x1 y0])
             b (.line! drw [x1 y0][x2 y0])
             c (.line! drw [x2 y0][x2 y1])
             d (.line! drw [x2 y1][x2 y2])
             e (.line! drw [x1 y2][x2 y2])
             f (.line! drw [x0 y2][x1 y2])
             g (.line! drw [x0 y2][x0 y1])
             h (.line! drw [x0 y1][x0 y0])
             i (.line! drw [x0 y0][x1 y1])
             j (.line! drw [x1 y0][x1 y1])
             k (.line! drw [x2 y0][x1 y1])
             l (.line! drw [x1 y1][x2 y1])
             m (.line! drw [x1 y1][x2 y2])
             n (.line! drw [x1 y2][x1 y1])
             o (.line! drw [x0 y2][x1 y1])
             p (.line! drw [x0 y1][x1 y1])
             emap {:dp dp :a a :b b :c c :d d :e e :f f :g g 
                   :h h :i i :j j :k k :l l :m m :n n :o o :p p}
             all-off (fn []
                       (doseq [q (vals emap)]
                         (.color! (.attributes q) @inactive*)))
             ssd (reify ComplexDisplay
                   
                   (width [this] w)
                   
                   (height [this] h)
                   
                   (colors! [this inactive active]
                     (reset! inactive* (uc/color inactive))
                     (reset! active* (uc/color active))
                     [@inactive* @active*])
                   
                   (elements [this] emap)
                     
                   (supported-characters [this]
                     (keys sixteen-charmap))

                   (set-character! [this c]
                     (all-off)
                     (doseq [q (get sixteen-charmap c [])]
                       (.color! (.attributes (q emap)) @active*))) )]
         ssd))))
           
; ---------------------------------------------------------------------- 
;                         7 x 5 dot matrix display
;
;    *****
;    *****
;    *****
;    *****
;    *****
;    *****
;    *****

(def ^:private matrix-charmap* (atom {}))
(def ^:private matrix-rows 7)
(def ^:private matrix-columns 5)

(defn- matrix-key [r c]
  (keyword (format "%d%d" r c)))

(defn- matrix-defrow [r cols]
  (let [acc* (atom [])]
    (dotimes [c (count cols)]
      (if (= (nth cols c) '*)
        (swap! acc* (fn [q](conj q (matrix-key r c))))))
    @acc*))

;; Define dot-matrix character
;; chr - character key 
;; r0, r1, ... r6 - row vectors [. . . . . .]  . indicates off, * indicates on
;;
(defn matrix-defchar [chr r0 r1 r2 r3 r4 r5 r6]
  (let [acc (flatten (list (matrix-defrow 0 r0)
                           (matrix-defrow 1 r1)
                           (matrix-defrow 2 r2)
                           (matrix-defrow 3 r3)
                           (matrix-defrow 4 r4)
                           (matrix-defrow 5 r5)
                           (matrix-defrow 6 r6)))]
    (swap! matrix-charmap* (fn [q](assoc q chr acc)))))


(matrix-defchar (int \space)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])

(matrix-defchar (int \!)
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \")
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \#)
                '[ . * . * . ]
                '[ * * * * * ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ * * * * * ]
                '[ . * . * . ])
    
(matrix-defchar (int \$)
                '[ . . * . . ]
                '[ . * * * . ]
                '[ * . * . . ]
                '[ . * * * . ]
                '[ . . * . * ]
                '[ . * * * . ]
                '[ . . * . . ])
    
(matrix-defchar (int \%)
                '[ . * * . . ]
                '[ . * * . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . * * . ]
                '[ . . * * . ])
    
(matrix-defchar (int \&)
                '[ * * * . . ]
                '[ * . . . . ]
                '[ . * . . * ]
                '[ . * * . * ]
                '[ * . . * . ]
                '[ * . * . * ]
                '[ . * . . . ])
    
(matrix-defchar (int \')
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \()
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ . * . . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \))
                '[ . . * . . ]
                '[ . . . * . ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ])
    
(matrix-defchar (int \*)
                '[ . . * . . ]
                '[ * . * . * ]
                '[ . * * * . ]
                '[ . . * . . ]
                '[ . * * * . ]
                '[ * . * . * ]
                '[ . . * . . ])
    
(matrix-defchar (int \+)
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ * * * * * ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . . . . ])

(matrix-defchar (int \-)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])

(matrix-defchar (int \,)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ . . * . . ])
    
(matrix-defchar (int \.)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . . * * . ])
    
(matrix-defchar (int \/)
                '[ . . . . . ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \0)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . * * ]
                '[ * . * . * ]
                '[ * * . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \1)
                '[ . . * . . ]
                '[ . * * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . * * * . ])
    
(matrix-defchar (int \2)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ . . . . * ]
                '[ . . * * . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * * * * * ])
    
(matrix-defchar (int \3)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ . . . . * ]
                '[ . . * * . ]
                '[ . . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \4)
                '[ . . . * . ]
                '[ . . * * . ]
                '[ . * . * . ]
                '[ * . . * . ]
                '[ * * * * * ]
                '[ . . . * . ]
                '[ . . . * . ])
    
(matrix-defchar (int \5)
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * * * * . ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \6)
                '[ . . * * . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \7)
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ . * . . . ]
                '[ . * . . . ])
    
(matrix-defchar (int \8)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \9)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . * * . . ])
    
(matrix-defchar (int \:)
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . . * * . ]
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . . * * . ]
                '[ . . . . . ])
    
(matrix-defchar (int \;)
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . . * * . ]
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . * * . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \<)
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ . * . . . ]
                '[ . . * . . ]
                '[ . . . * . ])
    
(matrix-defchar (int \=)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \>)
                '[ . * . . . ]
                '[ . . * . . ]
                '[ . . . * . ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ])
    
(matrix-defchar (int \?)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \@)
                '[ . * * * . ]
                '[ * . * * * ]
                '[ * * . . * ]
                '[ * * . . * ]
                '[ * * . . * ]
                '[ * . * * . ]
                '[ . * . . . ])
    
(matrix-defchar (int \A)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * * ]
                '[ * . . . * ]
                '[ * . . . * ])
    
(matrix-defchar (int \B)
                '[ * * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * . ])
    
(matrix-defchar (int \C)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \D)
                '[ * * * . . ]
                '[ * . . * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . * . ]
                '[ * * * . . ])
    
(matrix-defchar (int \E)
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ])
    
(matrix-defchar (int \F)
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])
    
(matrix-defchar (int \G)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . . ]
                '[ * . * * * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \H)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ])
    
(matrix-defchar (int \I)
                '[ . * * * . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . * * * . ])
    
(matrix-defchar (int \J)
                '[ . . * * * ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ . * * . . ])
    
(matrix-defchar (int \K)
                '[ * . . . * ]
                '[ * . . * . ]
                '[ * . * . . ]
                '[ * * . . . ]
                '[ * . * . . ]
                '[ * . . * . ]
                '[ * . . . * ])
    
(matrix-defchar (int \L)
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ])
    
(matrix-defchar (int \M)
                '[ * . . . * ]
                '[ * * . * * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ])
    
(matrix-defchar (int \N)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * . . * ]
                '[ * . * . * ]
                '[ * . . * * ]
                '[ * . . . * ]
                '[ * . . . * ])
    
(matrix-defchar (int \O)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \P)
                '[ * * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])
    
(matrix-defchar (int \Q)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . * . * ]
                '[ . * * * . ]
                '[ . . . . * ])
    
(matrix-defchar (int \R)
                '[ * * * * . ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * . ]
                '[ * . * . . ]
                '[ * . . * . ]
                '[ * . . . * ])
    
(matrix-defchar (int \S)
                '[ . * * * . ]
                '[ * . . . * ]
                '[ * . . . . ]
                '[ . * * * . ]
                '[ . . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \T)
                '[ * * * * * ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \U)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * * * . ])
    
(matrix-defchar (int \V)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * . * . ]
                '[ . . * . . ])
    
(matrix-defchar (int \W)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ . * . * . ])
    
(matrix-defchar (int \X)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * . * . ]
                '[ . . * . . ]
                '[ . * . * . ]
                '[ * . . . * ]
                '[ * . . . * ])
    
(matrix-defchar (int \Y)
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * . * . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \Z)
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * * * * * ])
    
(matrix-defchar (int \[)
                '[ * * . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * . . . ])
    
(matrix-defchar (int \\)
                '[ . . . . . ]
                '[ * . . . . ]
                '[ . * . . . ]
                '[ . . * . . ]
                '[ . . . * . ]
                '[ . . . . * ]
                '[ . . . . . ])
    
(matrix-defchar (int \])
                '[ . . . * * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . * * ])
    
(matrix-defchar (int \^)
                '[ . . * . . ]
                '[ . * . * . ]
                '[ * . . . * ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])
    
(matrix-defchar (int \_)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ])
    
(matrix-defchar (int \{)
                '[ . . * * . ]
                '[ . * . . . ]
                '[ . * . . . ]
                '[ * * . . . ]
                '[ . * . . . ]
                '[ . * . . . ]
                '[ . . * * . ])
    
(matrix-defchar (int \|)
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])
    
(matrix-defchar (int \})
                '[ . * * . . ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ . . . * * ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ . * * . . ])
    
(matrix-defchar (int \~)
                '[ . . . . . ]
                '[ . * . . . ]
                '[ * . * . * ]
                '[ . . . * . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])


(matrix-defchar (int \a)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . . . * . ]
                '[ * * * * . ]
                '[ * . . * . ]
                '[ . * * . * ])

(matrix-defchar (int \b)
                '[ . . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * . . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * * * . . ])

(matrix-defchar (int \c)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ . * * . . ])

(matrix-defchar (int \d)
                '[ . . . . . ]
                '[ . . . * . ]
                '[ . . . * . ]
                '[ . * * * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ . * * * . ])

(matrix-defchar (int \e)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ * . . * . ]
                '[ * * * * . ]
                '[ * . . . . ]
                '[ . * * . . ])

(matrix-defchar (int \f)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ . * . . . ]
                '[ * * * . . ]
                '[ . * . . . ]
                '[ . * . . . ])

(matrix-defchar (int \g )
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ * . . * . ]
                '[ * * * * . ]
                '[ . . . * . ]
                '[ . * * . . ])

(matrix-defchar (int \h)
                '[ . . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * . . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ])

(matrix-defchar (int \i)
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])

(matrix-defchar (int \j)
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ * . * . . ]
                '[ . * . . . ])

(matrix-defchar (int \k)
                '[ . . . . . ]
                '[ * . . * . ]
                '[ * . * . . ]
                '[ * * . . . ]
                '[ * * . . . ]
                '[ * . * . . ]
                '[ * . . * . ])

(matrix-defchar (int \l)
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * * . ])


(matrix-defchar (int \m)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * . * . ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ])

(matrix-defchar (int \n)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ])
      

(matrix-defchar (int \o)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ . * * . . ])

(matrix-defchar (int \p)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * . . ]
                '[ . * . * . ]
                '[ . * * . . ]
                '[ . * . . . ]
                '[ . * . . . ])

(matrix-defchar (int \q)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . * * . ]
                '[ . * . * . ]
                '[ . . * * . ]
                '[ . . . * . ]
                '[ . . . * . ])

(matrix-defchar (int \r)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . * * . ]
                '[ * * . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar (int \s)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * * . ]
                '[ * . . . . ]
                '[ . * * * . ]
                '[ . . . . * ]
                '[ . * * * . ])

(matrix-defchar (int \t)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . * * * . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])

(matrix-defchar (int \u)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ * . . * . ]
                '[ . * * . . ])

(matrix-defchar (int \v)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . . * . . ])

(matrix-defchar (int \w)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . . . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ . * . * . ])

(matrix-defchar (int \x)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . . * . ]
                '[ . * * . . ]
                '[ . * * . . ]
                '[ * . . * . ])

(matrix-defchar (int \y)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . . * . ]
                '[ . * . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ])

(matrix-defchar (int \z)
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * . . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * * * . . ])
   
(matrix-defchar 256
                '[ * * * * * ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])

(matrix-defchar 257
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 258
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ])

(matrix-defchar 259
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 260
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])

(matrix-defchar 261
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ])

(matrix-defchar 262
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 263
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ])

(matrix-defchar 263
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ * * * * * ])

(matrix-defchar 264
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 265
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 266
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 267
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 268
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ])

(matrix-defchar 269
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ * * * * * ])

(matrix-defchar 270
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 271
                '[ * * * * * ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])

(matrix-defchar 272
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * * * * * ]
                '[ * . . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 273
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ * * * * * ])

(matrix-defchar 274
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ * * * * * ]
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 275
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ * * * * * ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ])

(matrix-defchar 276
                '[ * . . . . ]
                '[ * . . . . ]
                '[ . * . . . ]
                '[ . . * . . ]
                '[ . . . * . ]
                '[ . . . . * ]
                '[ . . . . * ])

(matrix-defchar 277
                '[ . . . . * ]
                '[ . . . . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . . . . ]
                '[ * . . . . ])

(matrix-defchar 278
                '[ * . . . * ]
                '[ * . . . * ]
                '[ . * . * . ]
                '[ . . * . . ]
                '[ . * . * . ]
                '[ * . . . * ]
                '[ * . . . * ])

(matrix-defchar 279
                '[ . . * . . ]
                '[ . * . * . ]
                '[ * . * . * ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . . . . ])

(matrix-defchar 280
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ . . * . . ]
                '[ * . * . * ]
                '[ . * . * . ]
                '[ . . * . . ])

(matrix-defchar 281
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . * . . . ]
                '[ * . * * * ]
                '[ . * . . . ]
                '[ . . * . . ]
                '[ . . . . . ])

(matrix-defchar 282
                '[ . . . . . ]
                '[ . . * . . ]
                '[ . . . * . ]
                '[ * * * . * ]
                '[ . . . * . ]
                '[ . . * . . ]
                '[ . . . . . ])


(matrix-defchar 283
                '[ * * * * * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * * ])

(matrix-defchar 284
                '[ . . . . . ]
                '[ * * * * * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * . . . * ]
                '[ * * * * * ]
                '[ . . . . . ])

(matrix-defchar 285
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * * . ]
                '[ . * . * . ]
                '[ . * . * . ]
                '[ . * * * . ]
                '[ . . . . . ])

(matrix-defchar 286
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * * . ]
                '[ . * . * . ]
                '[ . * * * . ]
                '[ . . . . . ]
                '[ . . . . . ])


(matrix-defchar 287
                '[ * * * * * ]
                '[ * * * * * ]
                '[ * * * * * ]
                '[ * * * * * ]
                '[ * * * * * ]
                '[ * * * * * ]
                '[ * * * * * ])

(matrix-defchar 288
                '[ . . . . . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . . . . . ])

(matrix-defchar 289
                '[ . . . . . ]
                '[ . . . . . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . * * * . ]
                '[ . . . . . ]
                '[ . . . . . ])

(matrix-defchar 290
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ]
                '[ * . * . * ])

(matrix-defchar 291
                '[ * * * * * ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ * * * * * ]
                '[ . . . . . ]
                '[ * * * * * ])

(matrix-defchar 292
                '[ * . * . * ]
                '[ . * . * . ]
                '[ * . * . * ]
                '[ . * . * . ]
                '[ * . * . * ]
                '[ . * . * . ]
                '[ * . * . * ])

(matrix-defchar 293 
                '[ . * . . . ]
                '[ * . * . . ]
                '[ . * . * . ]
                '[ . . * . * ]
                '[ . * . * . ]
                '[ * . * . . ]
                '[ . * . . . ])

(matrix-defchar 294
                '[ . . . * . ]
                '[ . . * . * ]
                '[ . * . * . ]
                '[ * . * . . ]
                '[ . * . * . ]
                '[ . . * . * ]
                '[ . . . * . ])

(defn dot-matrix-display
  "Create 7 x 5 matrix display and place on sgwr drawing
   drw - Instance of sgwr.drawing/Drawing. drw should have native 
         coordinate-system 
   x   - int, horizontal position of left-hand display edge 
   y   - int, vertical position of top-edge of display
   w   - int, width of display in pixels, default 25
   h   - int, height of display in pixels, default 35"
  ([drw x y]
     (dot-matrix-display drw x y 25 35))
  ([drw x y w h]
     (let [inactive* (atom (uc/color [32 32 32]))
           active* (atom (uc/color :red))
           row-height (/ h matrix-rows)
           column-width (/ w matrix-columns)
           radius 2
           dotkey (fn [r c](keyword (format "%d%d" r c)))
           dots (let [acc* (atom {})]
                  (.style! drw 0)
                  (.width! drw 1)
                  (.color! drw @inactive*)
                  (.fill! drw true)
                  (dotimes [r matrix-rows]
                    (let [dy (int (+ y (* r row-height)))]
                      (dotimes [c matrix-columns]
                        (let [dx (+ x (* c column-width))
                              p (.circle! drw [dx dy] radius)]
                          (swap! acc* (fn [q](assoc q (dotkey r c) p)))))))
                  @acc*)
           all-off (fn []
                     (doseq [d (vals dots)]
                       (.color! (.attributes d) @inactive*)))
           dmd (reify ComplexDisplay

                 (width [this] w)

                 (height [this] h)

                 (colors! [this inactive active]
                   (reset! inactive* (uc/color inactive))
                   (reset! active* (uc/color active))
                   [@inactive* @active*])
           
                 (elements [this] dots)

                 (supported-characters [this]
                   (keys @matrix-charmap*))

                 (set-character! [this c]
                   (all-off)
                   (let [klst (get @matrix-charmap* (int c) [])]
                     (doseq [k klst]
                       (let [e (get dots k)]
                         (.color! (.attributes e) @active*))))) )]
           dmd)))
