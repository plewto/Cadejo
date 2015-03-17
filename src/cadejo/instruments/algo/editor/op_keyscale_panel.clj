(ns cadejo.instruments.algo.editor.op-keyscale-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.dual-slider :as dslider])
  (:require [sgwr.util.color :as uc]))

(defn op-keyscale [n drw p0 ied]
  (let [param-left-key (keyword (format "op%d-left-key" n))
        param-left-scale (keyword (format "op%d-left-scale" n))
        param-right-key (keyword (format "op%d-right-key" n))
        param-right-scale (keyword (format "op%d-right-scale" n))
        op-id (keyword (format "op%d" n))
        root (.root drw)
        tools (.tool-root drw)
        width 750
        height 100
        [x0 y0] p0
        x1 (+ x0 width)
        y1 (- y0 height)
        x-breakpoint (+ x0 25)
        y-breakpoint (+ y1 40)
        length-breakpoint (- width 50)

        x-left (+ x-breakpoint 30)
        y-left (- y0 40)

        x-right (- x1 130)
        y-right y-left
        action-breakpoint (fn [s _]
                            (let [[left right](dslider/get-dual-slider-values s)]
                              (.set-param! ied param-left-key left)
                              (.set-param! ied param-right-key right)
                              (.status! ied (format "[%s] -> %3d  [%s] -> %3d"
                                                    param-left-key left param-right-key right))))
        s-breakpoints (dslider/dual-slider tools [x-breakpoint y-breakpoint] length-breakpoint 0 127 
                                           :id (keyword (format "op%d-keyscale-slider" n))
                                           :orientation :horizontal
                                           :rim-color [0 0 0 0]
                                           :track1-color (lnf/passive-track)
                                           :track2-color (lnf/passive-track)
                                           :track3-color (lnf/passive-track)
                                           :track4-color (lnf/active-track)
                                           :handle1-color (lnf/handle)
                                           :handle2-color (lnf/handle)
                                           :drag-action action-breakpoint
                                           :value-hook (fn [n](int n)))
        dbar-left (sfactory/displaybar drw [x-left y-left] 3)
        left-scale-action (fn [b _]
                            (dbar/displaybar-dialog dbar-left
                                                    (format "Left key scale op %d  (db/octave)" n)
                                                    :validator (fn [q]
                                                                 (math/str->int q))
                                                    :callback (fn [_]
                                                                (let [s (.current-display dbar-left)
                                                                      q (math/str->int s)]
                                                                  (.set-param! ied param-left-scale q)))))
        b-left (sfactory/mini-edit-button drw [(+ x-left 95)(+ y-left 3)] op-id left-scale-action)

        dbar-right (sfactory/displaybar drw [x-right y-right] 3)

        right-scale-action (fn [b _]
                             (dbar/displaybar-dialog dbar-right
                                                     (format "Right key scale op %d  (db/octave)" n)
                                                     :validator (fn [q]
                                                                  (math/str->int q))
                                                     :callback (fn [_]
                                                                 (let [s (.current-display dbar-right)
                                                                       q (math/str->int s)]
                                                                   (.set-param! ied param-right-scale q)))))


        b-right (sfactory/mini-edit-button drw [(+ x-right 95)(+ y-right 3)] op-id right-scale-action)

        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        left-key (int (param-left-key dmap))
                        left-scale (int (param-left-scale dmap))                        
                        right-key (int (param-right-key dmap))
                        right-scale (int (param-right-scale dmap))]
                    (dslider/set-dual-slider-values! s-breakpoints left-key right-key false)
                    (.display! dbar-left (str left-scale) false)
                    (.display! dbar-right (str right-scale) false)))

        disable-fn (fn [] 
                     (doseq [q (list s-breakpoints dbar-left b-left dbar-right b-right)]
                       (.disable! q false)))
        enable-fn (fn [] 
                     (doseq [q (list s-breakpoints dbar-left b-left dbar-right b-right)]
                       (.enable! q false)))]

    ;; Draw breakpoint major tick marks
    (let [kn* (atom 0)
          x* (atom x-breakpoint)
          delta-x (* 12 (/ length-breakpoint 128.0))
          y1 (- y-breakpoint (* 0.5 major-tick-length))
          y2 (+ y1 major-tick-length)
          c (lnf/major-tick)]
      (while (< @kn* 128)
        (line/line root [@x* y1][@x* y2] :id op-id :color c)
        (if (= @kn* 60)(sfactory/label drw [(- @x* 6)(- y1 8)] "60"))
        (swap! kn* (fn [q](+ q 12)))
        (swap! x* (fn [q](+ q delta-x)))))
    (sfactory/label drw [(- x-left 50)(+ y-left 21)] "Left")
    (sfactory/label drw [(- x-right 50)(+ y-right 21)] "Right")
    (sfactory/minor-border drw p0 [x1 y1])
    (sfactory/text drw [(+ x0 10)(+ y1 20)] "Keyscale")
    (.display! dbar-left "XXX")
    (.display! dbar-right "XXX")
    {:sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))

    

