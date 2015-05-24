(ns cadejo.instruments.cobalt.editor.noise-editor
  (:require [cadejo.instruments.cobalt.editor.noise-freq-panel :as nfp])
  (:require [cadejo.instruments.cobalt.editor.noise-amp-panel :as nap :reload true])
  (:require [cadejo.instruments.cobalt.editor.op-env-panel :as env])
  (:require [cadejo.instruments.cobalt.editor.overview :as overview])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.sgwr-factory :as sf])
  (:require [sgwr.components.image :as image])
  (:require [seesaw.core :as ss]))

(def ^:private width 1300)
(def ^:private height 600)
(def ^:private bottom-margin 30)
(def ^:private left-margin 30)
(declare draw-background)
(declare vertical-pos)

(defn noise-editor [ied]
  (let [p0 [0 height]
        [x0 y0] p0
        drw (let [d (sf/sgwr-drawing width height)]
              (draw-background d p0)
              d)
        freq-panel (nfp/freq-panel drw ied p0)
        amp-panel (nap/amp-panel drw ied p0)
        env-panel (env/env-panel :nse drw ied p0)
        overview-panel (let [xv left-margin
                             yv (vertical-pos :overview p0)]
                         (overview/observer drw [xv yv] ied))
        pan-main (ss/scrollable (ss/vertical-panel :items [(.canvas drw)]))
        widget-map {:pan-main pan-main
                    :drawing drw}
        ed (reify subedit/InstrumentSubEditor
             (widgets [this] widget-map)
             (widget [this key](key widget-map))
             (parent [this] ied)
             (parent! [this _] ied) ;; ignore
             (status! [this msg](.status! ied msg))
             (warning! [this msg](.warning! ied msg))
             (set-param! [this param value](.set-param! ied param value))
             (init! [this]  ) ;; not implemented
             (sync-ui! [this]
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (freq-panel dmap)
                 (amp-panel dmap)
                 (env-panel dmap)
                 (.sync-ui! overview-panel)
                 (.render drw))))]
    ed))


(defn- vertical-pos [item p0]
  (let [y0 (second p0)
        y-overview (- y0 bottom-margin)
        y-border (- y0 height)
        y-title (+ y-border 30)
        map {:y0 y0
             :overview y-overview
             :title y-title
             :border y-border}]
    (get map item)))

(defn draw-background [ddrw p0]
 (let [bg (sf/sgwr-drawing width height)
        [x0 y0] p0
       x-border (+ x0 width)
       y-border (vertical-pos :border p0)
       x-title (+ x0 left-margin)
       y-title (vertical-pos :title p0)]
   (nfp/draw-freq-panel bg p0)
   (nap/draw-amp-panel bg p0)
   (env/draw-env-panel :nse bg p0)
   (sf/title bg [x-title y-title] "Noise" :size 12)
   (.render bg)
   (let [iobj (image/image (.root ddrw) [0 0] width height :id :background-omage)]
     (.put-property! iobj :image (.image bg))
     iobj)))

