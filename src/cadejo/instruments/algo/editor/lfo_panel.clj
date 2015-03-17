(ns cadejo.instruments.algo.editor.lfo-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.instruments.algo.editor.lfo-amp-panel :as ap])
  (:require [cadejo.instruments.algo.editor.lfo-freq-panel :as fp])
  (:require [cadejo.instruments.algo.editor.lfo-wave-panel :as wp]))

(defn lfo-panel [n drw p0 ied]
  (let [[x0 y0] p0
        x-amp (+ x0 50)
        x-wave (+ x-amp 230)
        y-amp (- y0 20)
        y-wave y-amp
        x-freq x-amp
        y-freq (- y-amp 230)
        frq-pan (fp/freq-panel n drw [x-freq y-freq] ied)
        amp-pan (ap/amp-panel n drw  [x-amp y-amp] ied)
        wave-pan (wp/wave-panel n drw [x-wave y-wave] ied)
        sync-fn (fn []
                  (doseq [sp (list frq-pan amp-pan wave-pan)]
                  ((:sync-fn sp))))]
    (sfactory/title drw [(+ x0 62)(- y0 433)] (format "LFO %d" n))
    {:sync-fn sync-fn}))
