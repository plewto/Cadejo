(ns cadejo.ui.instruments.program-name-editor
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.util.user-message :as umsg])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.FocusListener))

(defn program-name-editor [parent-editor]
  (let [txt-name (ss/text 
                  :text "<name>"
                  :multi-line? :false)
        txt-remarks (ss/text
                     :text "<remarks"
                     :multi-line? :true)
        pan-main (ss/border-panel 
                  :north (ss/vertical-panel 
                          :items [txt-name]
                          :border (factory/title "Program Name"))
                  :center (ss/vertical-panel 
                           :items [(ss/scrollable txt-remarks)]
                           :border (factory/title "Program Remarks")))
        widget-map {:txt-name txt-name
                    :txt-remarks txt-remarks
                    :pan-main pan-main}
        pne (reify cadejo.ui.instruments.subedit/InstrumentSubEditor

              (widgets [this] widget-map)

              (widget [this key]
                (or (get widget-map key)
                    (umsg/warning (format "program-name-editor does not have %s widget" key))))

              (parent [this] parent-editor)

              (parent! [this other]  nil) ;; ignore

              (status! [this msg]
                (.status! parent-editor msg))

              (warning! [this msg]
                (.warning! parent-editor msg))

              (set-param! [this param value] nil) ;; ignore
              
              (init! [this] )

              (sync-ui! [this]
                (let [bank (.parent-bank parent-editor)
                      prog (.current-program bank)
                      name (.program-name prog)
                      remarks (.program-remarks prog)]
                  (ss/config! txt-name :text name)
                  (ss/config! txt-remarks :text remarks))))]
    
    (.addFocusListener txt-name 
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 pname (ss/config txt-name :text)]
                             (.program-name! (.current-program bank) (str pname))
                             (.status! parent-editor 
                                       "Program name changed")))))
    (.addFocusListener txt-remarks
                       (proxy [FocusListener][]
                         (focusGained [_])
                         (focusLost [_]
                           (let [bank (.parent-bank parent-editor)
                                 remarks (ss/config txt-remarks :text)]
                             (.program-remarks! (.current-program bank)(str remarks))
                             (.status! parent-editor
                                       "Program remarks changed")))))
    pne))
