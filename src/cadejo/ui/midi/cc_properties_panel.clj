(ns cadejo.ui.midi.cc-properties-panel
  "Provides editor for MIDI CC parameters.
   Up to 18 controllers are supported."
  (:use [cadejo.ui.midi.properties-panel 
         :only [curve-button edit-button 
                title text inherited-text
                border displaybar
                inv-curve-map]])
  (:require [cadejo.ui.node-observer])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.multistate-button :as msb])  
  (:require [seesaw.core :as ss]))
  
(def ^:private subpan-w 175)
(def ^:private subpan-h 200)
(def ^:private x-margin 10)
(def ^:private x-gap 6)
(def ^:private y-margin 10)
(def ^:private y-gap 6)
(def ^:private rows 3)
(def ^:private columns 6)
(def ^:private max-count (* rows columns))
(def ^:private drawing-width (+ (* 2 x-margin)(* columns (+ subpan-w x-gap))))
(def ^:private drawing-height (+ (* 2 y-margin)(* rows (+ subpan-h y-gap))))

(defn- cc-subpanel [drw p0 ctrl]
  (let [editor* (atom nil)
        get-node (fn [](.node @editor*))
        cc-suite (fn [](.controllers (get-node)))
        status (fn [msg](.status! @editor* msg))
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0] p0
        b-curve (curve-button drw :curve [(+ x0 93)(+ y0 40)]
                              :click-action (fn [b _]
                                              (let [crv (second (msb/current-multistate-button-state b))]
                                                (status (format "[%s curve] -> %s" ctrl crv))
                                                (.set-curve! (cc-suite) ctrl crv))))
        

        dbar-bias (displaybar root (+ x0 32)(+ y0 92) 4)
        bias-validator (fn [s](let [n (math/str->float s)]
                                (if n
                                  (and (<= -1.0 n)(<= n 1.0))
                                  false)))
        bias-callback (fn [dbar]
                        (let [r (math/str->float (.current-display dbar))]
                          (status (format "[%s bias] -> %s" ctrl r))
                          (.set-bias! (cc-suite) ctrl r)))
        b-edit-bias (edit-button tools [(+ x0 125)(+ y0 90)] dbar-bias
                                 (format "%s Bias +/- 1.0" ctrl)
                                 bias-validator
                                 bias-callback)

        dbar-scale (displaybar root (+ x0 32)(+ y0 142) 4)
        scale-validator (fn [s](let [n (math/str->float s)]
                                 (if n
                                   (and (<= 0 n)(<= n 4))
                                   false)))
        scale-callback (fn [dbar]
                         (let [r (math/str->float (.current-display dbar))]
                           (status (format "[%s scale] -> %s" ctrl r))
                           (.set-scale! (cc-suite) ctrl r)))
        
        b-edit-scale (edit-button tools [(+ x0 125)(+ y0 142)] dbar-scale
                                  (format "%s Scale (0.0 4.0)" ctrl)
                                  scale-validator
                                  scale-callback)]
    (border root p0 [(+ x0 subpan-w)(+ y0 subpan-h)])
    (title drw [(+ x0 70)(+ y0 25)](format "%s" ctrl))
    (text drw [(+ x0 26)(+ y0 65)] "Curve" :style :sans :size 6)
    (text drw [(+ x0 28)(+ y0 132)] "Bias" :style :sans :size 6)
    (text drw [(+ x0 24)(+ y0 188)] "Scale" :style :sans :size 6)
    (.display! dbar-bias "+0.0" false)
    (.display! dbar-scale "1.0" false)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! editor* ed))
      
      (widgets [this] {})
      
      (widget [this key] nil)
      
      (sync-ui! [this]
        (let [ccobj (cc-suite)
              crv (.curve ccobj ctrl)
              bias (.bias ccobj ctrl)
              scale (.scale ccobj ctrl)]
          (msb/set-multistate-button-state! b-curve (get inv-curve-map crv 0) false)
          (.display! dbar-bias (str bias) false)
          (.display! dbar-scale (str scale) false))) )))

(defn cc-properties-panel [descriptor]
  (let [editor* (atom nil)
        sub-panels* (atom nil)
        drw (sgwr.components.drawing/native-drawing drawing-width drawing-height)
        pan-main (ss/horizontal-panel :items [(.canvas drw)])
        row* (atom 0)
        col* (atom 0)
        ctrl-list (.controllers descriptor)
        count (min (count ctrl-list) max-count)]

    (dotimes [i count]
      (let [ctrl (nth ctrl-list i)
            x (+ x-margin (* (+ x-gap subpan-w) @col*))
            y (+ y-margin (* (+ y-gap subpan-h) @row*))
            sp (cc-subpanel drw [x y] ctrl)]
        (if (< @col* (dec columns))
          (swap! col* inc)
          (do
            (reset! col* 0)
            (swap! row* inc)))
        (swap! sub-panels* (fn [q](conj q sp))))) 
            
    (.background! drw (lnf/background))
    (.render drw)
    (reify cadejo.ui.node-observer/NodeObserver
      
      (set-parent-editor! [this ed]
        (reset! editor* ed)
        (doseq [sp @sub-panels*]
          (.set-parent-editor! sp ed)))
      
      (widgets [this] {:drawing drw
                       :canvas (.canvas drw)
                       :pan-main pan-main})
      
      (widget [this key]
        (get (.widgets this) key))
      
      (sync-ui! [this]
        (doseq [s @sub-panels*]
          (.sync-ui! s))
        (.render drw))) ))
