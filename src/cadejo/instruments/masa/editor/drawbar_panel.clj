(ns cadejo.instruments.masa.editor.drawbar-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.instruments.masa.editor.envelope-panel :as envpan])
  (:require [cadejo.instruments.masa.editor.gamut-panel :as gpan ])
  (:require [cadejo.instruments.masa.editor.vibrato-panel :as vibpan ])
  (:require [cadejo.instruments.masa.editor.registration-panel :as regpan ])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.text :as text])
  (:require [seesaw.core :as ss]))

(defn- text [drw p0 txt]
  (text/text (.root drw) p0 txt
             :style :sans
             :size 6
             :color (lnf/text-color)))

(defn drawbar-panel [ied]
  (let [drw (let [d (sgwr.components.drawing/native-drawing drawing-width drawing-height)]
              (.background! d (lnf/background))
              d)
        regpan (regpan/registration-panel drw ied [50 275])
        gpan (gpan/gamut-panel drw ied [50 410])
        envpan (envpan/envelope-panel drw ied [545 175])
        vibpan (vibpan/vibrato-panel drw ied [545 390])
        sub-panels [regpan gpan envpan vibpan]
        widget-map {:drawing drw
                    :canvas (.canvas drw)
                    :pan-main (ss/horizontal-panel :items [(.canvas drw)]
                                                   :background (lnf/background))}]
    (text drw [10 210] "Pedals")
    (text drw [10 250] "Env")
    (text drw [10 300] "F Edit")
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      
      (widget [this key]
        (or (get widget-map key)
            (umsg/warning (format "MASA drawbar-panel does not have %s widget" key))))

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
        (.render drw)
        )
      
      )))
