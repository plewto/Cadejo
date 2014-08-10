(ns cadejo.ui.midi.cceditor-tab
  "Provides GUI for up to 4 MIDI controllers"
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.midi.ccproperties-panel :as ccp])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss]))


;; Create Properties editor for up to 4 MIDI controllers.
;; descriptor - an instance of  cadejo.instrument.descriptor/InstrumentDescrptor
;; offset - int, indicate initial controller to include from 
;; those available in descriptor.
;;
(defn cceditor-tab [descriptor offset]
  (let [parent* (atom nil)
        subpans (let [acc* (atom [])
                      aval (.controllers descriptor)]
                  (doseq [i (range offset (+ offset 4))]
                    (if (< i (count aval))
                      (swap! acc* (fn [n](conj n (ccp/ccproperties-panel descriptor (nth aval i)))))
                      (swap! acc* (fn [n](conj n nil)))))
                  @acc*)
        pan-main (let [acc* (atom [])]
                   (doseq [p subpans]
                     (if p
                       (swap! acc* (fn [n](conj n (.widget p :pan-main))))
                       (swap! acc* (fn [n](conj n (ss/vertical-panel))))))
                   (ss/grid-panel :rows 1 :items @acc*))
        widget-map {:pan-main pan-main}
        edpan (reify cadejo.ui.midi.properties-editor/PropertyEditor

                (widgets [this] widget-map)
                
                (widget [this key]
                  (or (get widget-map key)
                      (umsg/warning (format "cceditor-tab does not have %s widget" key))))
                
                (set-parent-editor! [this ed]
                  (doseq [sp subpans]
                    (if sp (.set-parent-editor! sp ed))))

                (node [this]
                  (.node (first subpans)))

                (sync-ui! [this]
                  (doseq [sp subpans]
                    (if sp (.sync-ui! sp)))) )]
    edpan))
                  
