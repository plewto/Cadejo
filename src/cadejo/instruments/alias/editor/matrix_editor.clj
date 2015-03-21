(ns cadejo.instruments.alias.editor.matrix-editor
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [sgwr.tools.multistate-button :as msb])

  )


(defn source-button [drw ieditor id p0]
  (let [w 40
        h 40
        c1 (lnf/text)
        c2 (lnf/selected-text)
        action (fn [b _] 
                 (let [state (msb/current-multistate-button-state b)
                       ]
                   ;(println (format "[%s] -> %s" id state))
                   (.set-param! ieditor id (first state))
                   (.status! ieditor (format "[%s] -> %s" id (second state)))))
        states [[:con "CON" c1]
                [:a " A " c2][:b " B " c2][:c " C " c2][:d " D " c2]
                [:e " E " c2][:f " F " c2][:g " G " c2][:h " H " c2]
                [:off "OFF" c1]]
        msb (msb/text-multistate-button (.tool-root drw) p0 states
                                        :click-action action
                                        :w w :h h
                                        :gap 4 )
        ]
    msb))
               
  
