(ns sgwr.indicators.sixteen
  (:require [sgwr.indicators.char])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as math])
  (:require [sgwr.elements.element :as elements])
  (:require [sgwr.elements.circle :as circle])
  (:require [sgwr.elements.line :as line]))
            

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
;

;; (def char-width sgwr.indicators.char/char-width)
;; (def char-height sgwr.indicators.char/char-height)

(def ^:private charmap 
  (let [acc* (atom {(int \space) []
                    (int \.) [:dp]
                    (int \0) [:a :b :c :d :e :f :g :h :k :o]
                    (int \1) [:j :n] ; [:c :d :k]
                    (int \2) [:a :b :c :l :p :g :f :e]
                    (int \3) [:a :b :c :d :e :f :l]
                    (int \4) [:h :p :l :c :d]
                    (int \5) [:a :b :h :p :l :d :e :f]
                    (int \6) [:a :h :p :l :d :e :f :g]
                    (int \7) [:a :b :c :d]
                    (int \8) [:a :b :c :d :e :f :g :h :p :l]
                    (int \9) [:a :b :c :l :p :h :d :e]
                    (int \A) [:a :b :c :l :p :h :g :d]
                    (int \B) [:a :b :c :d :e :f :j :l :n]
                    (int \C) [:a :b :h :g :e :f]
                    (int \D) [:a :b :c :d :e :f :j :n]
                    (int \E) [:a :b :h :p :g :f :e]
                    (int \F) [:a :b :h :p :g]
                    (int \G) [:a :b :h :g :f :e :d :l]
                    (int \H) [:h :g :c :d :p :l]
                    (int \I) [:a :b :j :n :e :f]
                    (int \J) [:c :d :e :f :g]
                    (int \K) [:h :g :p :k :m]
                    (int \L) [:h :g :f :e]
                    (int \M) [:h :g :c :d :i :k]
                    (int \N) [:h :g :c :d :i :m]
                    (int \O) [:a :b :c :d :e :f :g :h]
                    (int \P) [:a :b :c :l :p :h :g]
                    (int \Q) [:a :b :c :d :e :f :g :h :m]
                    (int \R) [:a :b :c :l :p :h :g :m]
                    (int \S) [:a :b :h :p :l :d :e :f]
                    (int \T) [:a :b :j :n]
                    (int \U) [:h :g :f :e :c :d]
                    (int \V) [:h :g :o :k]
                    (int \W) [:h :g :c :d :o :m]
                    (int \X) [:i :m :o :k]
                    (int \Y) [:h :p :l :c :n]
                    (int \Z) [:a :b :o :k :e :f]
                    (int \") [:j :c]
                    (int \$) [:a :b :h :p :l :d :e :f :j :n]
                    (int \%) [:a :j :p :h :l :d :e :n :k :o]
                    (int \') [:k]
                    (int \&) [:a :i :j :p :g :f :e :m]
                    (int \*) [:i :j :k :p :l :o :n :m]
                    (int \+) [:j :l :n :p]
                    (int \`) [:i]
                    (int \-) [:p :l]
                    (int \/) [:k :o]
                    (int \() [:k :m]
                    (int \)) [:i :o]
                    (int \[) [:a :h :g :f]
                    (int \]) [:b :c :d :e]
                    (int \{) [:b :j :n :e :p]
                    (int \}) [:a :j :n :f :l]
                    (int \=) [:p :l :f :e]
                    (int \_) [:f :e]
                    (int \\) [:i :m]
                    (int \^) [:o :m]
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
      (swap! acc* (fn [q](assoc q (int low)(get q (int up))))))
    @acc*))

(defn char-16 [grp x-offset y-offset & {:keys [cell-width cell-height]
                                        :or {cell-width 25
                                             cell-height 35}}]
  (let [inactive* (atom (uc/color [32 32 32]))
        active* (atom (uc/color [255 64 64]))
        x0 x-offset
        x1 (+ x-offset cell-width)
        xc (math/mean x0 x1)
        y0 y-offset
        y1 (+ y0 cell-height)
        yc (math/mean y0 y1)
        elements (let [emap {:a (line/line grp [x0 y0][xc y0] :id :a)
                             :b (line/line grp [xc y0][x1 y0] :id :b)
                             :c (line/line grp [x1 y0][x1 yc] :id :c)
                             :d (line/line grp [x1 yc][x1 y1] :id :d)
                             :e (line/line grp [xc y1][x1 y1] :id :e)
                             :f (line/line grp [x0 y1][xc y1] :id :f)
                             :g (line/line grp [x0 y1][x0 yc] :id :g)
                             :h (line/line grp [x0 yc][x0 y0] :id :h)
                             :i (line/line grp [x0 y0][xc yc] :id :i)
                             :j (line/line grp [xc y0][xc yc] :id :j)
                             :k (line/line grp [x1 y0][xc yc] :id :k)
                             :l (line/line grp [xc yc][x1 yc] :id :l)
                             :m (line/line grp [xc yc][x1 y1] :id :m)
                             :n (line/line grp [xc y1][xc yc] :id :n)
                             :o (line/line grp [x0 y1][xc yc] :id :o)
                             :p (line/line grp [x0 yc][xc yc] :id :p)
                             :dp (circle/circle-r grp [xc y1] 3 :id :dp)}]
                   (doseq [e (vals emap)]
                     (if (= e (:dp emap))
                       (do 
                         (elements/set-attributes! e :inactive :color @inactive* :style :solid :width 1.0 :fill :no :hide :no)
                         (elements/set-attributes! e :active   :color @active*   :style :solid :width 1.0 :fill :no :hide :no))
                       (do 
                         (elements/set-attributes! e :inactive :color @inactive* :style :solid :width 1.0 :fill :no :hide :no)
                         (elements/set-attributes! e :active   :color @active*   :style :solid :width 1.0 :fill :no :hide :no))))
                   emap)
        all-off (fn []
                  (doseq [p (vals elements)]
                    (.use-attributes! p :inactive)))
        obj (reify sgwr.indicators.char/CharDisplay
             
              (cell-width [this] cell-width)

              (cell-height [this] cell-height)

              (colors! [this inactive active]
                (let [c1 (uc/color inactive)
                      c2 (uc/color active)]
                  (reset! inactive* c1)
                  (reset! active* c2)
                  (doseq [p (vals elements)]
                    (elements/set-attributes! p :inactive :color c1 :fill :no :hide :no)
                    (elements/set-attributes! p :active   :color c2 :fill :no :hide :no))
                  [c1 c2]))
              
              (character-set [this]
                (keys (sort charmap)))

              (display! [this c]
                (all-off)
                (let [klst (get charmap (int c) [])]
                  (doseq [k klst]
                    (let [e (get elements k)]
                      (.use-attributes! e :active)))))) ]
              
    obj))
