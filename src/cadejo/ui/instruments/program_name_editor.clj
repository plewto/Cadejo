(ns cadejo.ui.instruments.program-name-editor
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf])
  (:import java.awt.event.ActionListener
           java.awt.event.FocusListener))

(defn program-name-editor [ied]
  (let [txt-data (ss/text
                   :text "<data>"
                   :multi-line? true
                   :font (ssf/font :name :monospaced :size 16))
        txt-name (ss/text :text "<name>" :multi-line? false)
        ;; jb-evaluate (ss/button :text "Eval")
        ;; jb-update (ss/button :text "Update")
        ;; pan-tools (ss/horizontal-panel 
        ;;            :items [jb-evaluate jb-update])
        pan-center (ss/scrollable txt-data)
        pan-name (ss/horizontal-panel 
                  :items [(ss/label :text "Name")
                          txt-name])
        pan-main (ss/border-panel 
                  :north pan-name
                  :center pan-center
                  ;:south pan-tools
                  )
        widget-map {:txt-name txt-name
                    :txt-data txt-data
                    :pan-main pan-main}
        set-program-name! (fn [pname]
                            (let [bank (.parent-bank ied)
                                  prog (.current-program bank)
                                  slot (.current-slot bank)
                                  bed (.editor bank)]
                              (.program-name! prog (str pname))
                              (.store! bank slot prog)
                              (.sync-ui! bed)
                              (.sync-ui! ied)
                              (.status! ied "Program name changed")))

        ded (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
              (widgets [this] widget-map)
              
              (widget [this key]
                (get widget-map key))
              
              (parent [this] ied)

              (parent! [this _]  nil) ;; ignore

              (status! [this msg]
                (.status! ied msg))

              (warning! [this msg]
                (.warning! ied msg))

              (set-param! [this param value] nil) ;; ignore
              
              (init! [this] )

              (sync-ui! [this]
                (let [bank (.parent-bank ied)
                      pp (.pp-hook bank)
                      prog (.current-program bank)
                      data (:data (.to-map prog))
                      name (.program-name prog)
                      remarks (.program-remarks prog)
                      pnum (or (.current-slot bank) -1)]
                  (ss/config! txt-data :text (pp pnum name data remarks))
                  (ss/config! txt-name :text name) )))]
    (.addActionListener txt-name (proxy [ActionListener][]
                                   (actionPerformed [_]
                                     (set-program-name! (ss/config txt-name :text)))))
    (.addFocusListener txt-name (proxy [FocusListener][]
                                  (focusGained [_])
                                  (focusLost [_]
                                     (set-program-name! (ss/config txt-name :text)))))
    ded))
