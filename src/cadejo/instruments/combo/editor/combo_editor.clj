(ns cadejo.instruments.combo.editor.combo-editor
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.instruments.combo.editor.panel])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.tools.slider]))


(def ^:private msg1 "Combo editor does not have %s tool")

(def ^:private vibrato-freq->pos 
  (math/linear-function min-vibrato-frequency 0
                        max-vibrato-frequency 100))

(def ^:private vibrato-sens->pos
  (math/linear-function min-vibrato-sensitivity 0
                        max-vibrato-sensitivity 100))

(def ^:private db->pos 
  (math/linear-function min-amp-db 0 max-amp-db 100))

(def ^:private flanger-rate->pos (math/linear-function min-flanger-rate 0 
                                                       max-flanger-rate 100))

(def ^:private flanger-fb->pos (math/linear-function min-flanger-fb -1
                                                     max-flanger-fb 1))

(defn- slider-value! [s v]
  (sgwr.tools.slider/set-slider-value! s v nil))

(defn- slider-value [s]
  (sgwr.tools.slider/get-slider-value s))

(def ^:private filter-freq-index-map {1 0, 2 1, 3 2, 4 3, 6 4, 8 5})

(defn combo-editor [performance]
  (let [ied (ied/instrument-editor performance)
        panel (cadejo.instruments.combo.editor.panel/editor-panel ied)
        render (fn [](.render (:drawing panel)))
        combo-ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
                   
                   (widgets [this] {:pan-main (.canvas (:drawing panel))})

                   (widget [this key]
                     (or (get (.widgets this) key)
                         (umsg/warning (format msg1 key))))

                   (parent [this] ied)

                   (parent! [this _] ied)

                   (status! [this msg]
                     (.status! ied msg))

                   (warning! [this msg]
                     (.warning! ied msg))

                   (set-param! [this param val]
                     (.status! this (format "[%s] --> %s" param val))
                     (.set-param! ied param val))

                   (init! [this]
                     (.set-param! this :amp1 1.0)
                     (.set-param! this :amp2 0.0)
                     (.set-param! this :amp3 0.0)
                     (.set-param! this :amp4 0.0)
                     (.set-param! this :wave1 0.0)
                     (.set-param! this :wave2 0.0)
                     (.set-param! this :wave3 0.0)
                     (.set-param! this :wave4 0.0)
                     (.set-param! this :chorus 0.0)
                     (.set-param! this :filter-type 0)
                     (.set-param! this :filter 8.0)
                     (.set-param! this :vibrato-freq 5.0)
                     (.set-param! this :vibrato-sens 0.01)
                     (.set-param! this :flanger-rate 0.25)
                     (.set-param! this :flanger-depth 0.25)
                     (.set-param! this :flanger-fb -0.5)
                     (.set-param! this :flanger-mix 0.0)
                     (.set-param! this :reverb-mix 0.0)
                     (.set-param! this :amp 0.2)
                     (.sync-ui! this))

                   (sync-ui! [this]
                     (let [data (.current-data (.bank performance))
                           [s-a1 s-a2 s-a3 s-a4][(:s-amp1 panel)
                                                 (:s-amp2 panel)
                                                 (:s-amp3 panel)
                                                 (:s-amp4 panel)]
                           [s-w1 s-w2 s-w3 s-w4][(:s-wave1 panel)
                                                 (:s-wave2 panel)
                                                 (:s-wave3 panel)
                                                 (:s-wave4 panel)]
                           [s-dt1 s-dt2][(:s-detune-coarse panel)
                                         (:s-detune-fine panel)]
                           [s-vfreq s-vsens][(:s-vrate panel)
                                             (:s-vsens panel)]
                           [s-reverb s-amp][(:s-reverb panel)
                                            (:s-amp panel)]
                           [f-flanger 
                            s-flanger-depth 
                            s-flanger-mix][(:field-flanger panel)
                                           (:s-flanger-depth panel)
                                           (:s-flanger-mix panel)]
                           msb-ft (:msb-filter-curve panel)
                           msb-ff (:msb-filter-freq panel)
                           a1 (int (* 100 (get data :amp1 0)))
                           a2 (int (* 100 (get data :amp2 0)))
                           a3 (int (* 100 (get data :amp3 0)))
                           a4 (int (* 100 (get data :amp4 0)))
                           w1 (int (* 100 (get data :wave1 0)))
                           w2 (int (* 100 (get data :wave2 0)))
                           w3 (int (* 100 (get data :wave3 0)))
                           w4 (int (* 100 (get data :wave4 0)))
                           filter-type (int (get data :filter-type 0))
                           filter-freq (int (get data :filter 1))
                           filter-freq-index (get filter-freq-index-map filter-freq 0)
                           vfreq (get data :vibrato-freq 5.0)
                           vsens (get data :vibrato-sens 0.01)
                           rvmix (get data :reverb-mix 0.0)
                           amp (get data :amp 1.0)
                           a-db (max -48 (min 0 (math/amp->db amp)))
                           flng-rate (get data :flanger-rate 0.25)
                           flng-fb (get data :flanger-fb -0.5)
                           flng-depth (get data :flanger-depth 0.25)
                           flng-mix (get data :flanger-mix 0.0)
                           flng-ball-pos [(flanger-fb->pos flng-fb)(flanger-rate->pos flng-rate)]
                           [detune-coarse detune-fine](let [c (* 100 (get data :chorus 0))
                                                            r (int c)
                                                            f (int (* 100 (- c r)))]
                                                        [r f])]
                       (slider-value! s-a1 a1)
                       (slider-value! s-a2 a2)
                       (slider-value! s-a3 a3)
                       (slider-value! s-a4 a4)
                       (slider-value! s-w1 w1)
                       (slider-value! s-w2 w2)
                       (slider-value! s-w3 w3)
                       (slider-value! s-w4 w4)
                       (slider-value! s-dt1 detune-coarse)
                       (slider-value! s-dt2 detune-fine)
                       (msb/set-multistate-button-state! msb-ft filter-type false)
                       (msb/set-multistate-button-state! msb-ff filter-freq-index false)
                       (slider-value! s-vfreq (int (vibrato-freq->pos vfreq)))
                       (slider-value! s-vsens (int (vibrato-sens->pos vsens)))
                       (slider-value! s-reverb (* 100 rvmix))
                       (slider-value! s-amp (db->pos a-db))
                       (field/set-ball-value! f-flanger :b1 flng-ball-pos false)
                       (slider-value! s-flanger-depth (* 100  flng-depth))
                       (slider-value! s-flanger-mix (* 100 flng-mix))
                       (render))))]
    (.add-sub-editor! ied "Edit" :general :mixer "Combo Editor" combo-ed)
    (.show-card-number! ied 1)
    ied))
