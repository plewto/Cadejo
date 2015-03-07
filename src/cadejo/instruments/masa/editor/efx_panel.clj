(ns cadejo.instruments.masa.editor.efx-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.instruments.masa.editor.scanner-panel :as scanpan])
  (:require [cadejo.instruments.masa.editor.reverb-panel :as revpan])
  (:require [cadejo.instruments.masa.editor.amp-panel :as amppan ])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing])
  (:require [sgwr.tools.button :as button])
  (:require [seesaw.core :as ss]))

(defn efx-panel [ied]
  (let [drw (sgwr.components.drawing/native-drawing drawing-width drawing-height)
        scanpan (scanpan/scanner-panel drw ied [10 410])
        revpan (revpan/reverb-panel drw ied [520 300])
        amppan (amppan/amp-panel drw ied [730 300])
        b-help (button/mini-icon-button (.tool-root drw) [520 320] (lnf/icon-prefix) :help
                                    :id :help
                                    :rim-color (lnf/button-border-color))
        sub-panels [scanpan revpan amppan]
        widget-map {:drawing drw
                    :canvas (.canvas drw)
                    :pan-main (ss/horizontal-panel :items [(.canvas drw)] 
                                                   :background (lnf/background))}]
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor 
      
      (widgets [this] widget-map)

      (widget [this key]
        (or (get widget-map key)
            (umsg/warning (format "MASA efx-panel does not have %s widget" key))))
      
      (parent [this] ied)

      (parent! [this _] ied)

      (status! [this msg]
        (.status! ied msg))
      
      (warning! [this msg]
        (.warning! ied msg))
      
      (set-param! [this param value]
        (.status! this (format "[%s] --> %s" param val))
        (.set-param! ied param val))
      
      (init! [this]
        (doseq [sp sub-panels](.init! sp)))

      (sync-ui! [this]
        (doseq [sp sub-panels](.sync-ui! sp))
        (.render drw)) )))
