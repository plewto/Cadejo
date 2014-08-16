(ns cadejo.ui.util.lnf
  (:use [seesaw.core])
  (:require [seesaw.swingx :as swingx])
  (:import org.pushingpixels.substance.api.SubstanceLookAndFeel
           org.pushingpixels.substance.api.UiThreadingViolationException
           java.awt.BorderLayout
           javax.swing.BorderFactory
           javax.swing.border.BevelBorder
           java.awt.Color))

;; Code shamelessly lifted from seesaw examples
;;
(defn create-lnf-selector []
  (swingx/titled-panel :title "Substance Skins"
                       :content (combobox 
                                 :model    (vals (SubstanceLookAndFeel/getAllSkins))
                                 :renderer (fn [this {:keys [value]}]
                                             (text! this (.getClassName value)))
                                 :listen   [:selection (fn [e]
                                        ; Invoke later because CB doens't like changing L&F while
                                        ; it's doing stuff.
                                                         (invoke-later
                                                          (-> e
                                                              selection
                                                              .getClassName
                                                              SubstanceLookAndFeel/setSkin)))])))

;; A few example panels
;;

(def greek (map str '(Alpha Beta Gamma Delta Epsilon Zeta Eta Theta Iota Kappa Lambda)))


(def pan-border
  (let [lab1 (label "Raised Bevel")
        lab2 (label "Lowered Bevel")
        lab3 (label "Raised Etched")
        lab4 (label "Lowered Etched")
        lab5 (label "Raised Soft Bevel")
        lab6 (label "Lowered Soft Bevel")
        lab7 (label "Black Line Border")
        lab8 (label "Matte Border 4")
        lab9 (label "Title Border")
        pan-demos (grid-panel :columns 4 :rows 4 :hgap 8 :vgap 8
                              :items [lab1 lab2 lab3 
                                      lab4 lab5 lab6
                                      lab7 lab8 lab9])
        pan-main (border-panel :north "Border Examples"
                               :center pan-demos)]
    (.setBorder lab1 (BorderFactory/createBevelBorder BevelBorder/RAISED))
    (.setBorder lab2 (BorderFactory/createBevelBorder BevelBorder/LOWERED))
    (.setBorder lab3 (BorderFactory/createEtchedBorder BevelBorder/RAISED))
    (.setBorder lab4 (BorderFactory/createEtchedBorder BevelBorder/LOWERED))
    (.setBorder lab5 (BorderFactory/createSoftBevelBorder BevelBorder/RAISED))
    (.setBorder lab6 (BorderFactory/createSoftBevelBorder BevelBorder/LOWERED))
    (.setBorder lab7 (BorderFactory/createLineBorder Color/BLACK))
    (.setBorder lab8 (BorderFactory/createMatteBorder 4 4 4 4 Color/GRAY))
    (.setBorder lab9 (BorderFactory/createTitledBorder "Foo"))
    pan-main))

(def pan-buttons (let [count 4
                       pan-simple (horizontal-panel 
                                   :items (let [acc* (atom [])]
                                            (dotimes [i count]
                                              (swap! acc* (fn [n](conj n (button :text (nth greek i))))))
                                            @acc*))
                       pan-checkbox (horizontal-panel
                                     :items (let [acc* (atom [])]
                                              (dotimes [i count]
                                                (swap! acc* (fn [n](conj n (checkbox :text (nth greek i))))))
                                              (.setSelected (first @acc*) true)
                                              @acc*))
                       grp1 (button-group)
                       pan-toggle (horizontal-panel 
                                   :items (let [acc* (atom [])]
                                            (dotimes [i count]
                                              (swap! acc* (fn [n](conj n (toggle :text (nth greek i) :group grp1)))))
                                            (.setSelected (second @acc*) true)
                                            @acc*))
                       grp2 (button-group)
                       pan-radio (horizontal-panel 
                                  :items (let [acc* (atom [])]
                                           (dotimes [i count]
                                             (swap! acc* (fn [n](conj n (radio :text (nth greek i) :group grp2)))))
                                           (.setSelected (nth @acc* 2) true)
                                           @acc*))]
                   (.setBorder pan-simple (BorderFactory/createTitledBorder "Button"))
                   (.setBorder pan-checkbox (BorderFactory/createTitledBorder "Checkbox"))
                   (.setBorder pan-toggle (BorderFactory/createTitledBorder "Toggle Button"))
                   (.setBorder pan-radio (BorderFactory/createTitledBorder "Radio Button"))
                   (grid-panel :rows 4 :vgap 4 :items [pan-simple pan-checkbox pan-toggle pan-radio])))
  


(defn skin-dialog []
  (let [pan-tabs (tabbed-panel :tabs [{:title :buttons :content pan-buttons}
                                      {:title :borders :content pan-border}])
        pan-main (border-panel :north (create-lnf-selector)
                               ;:center pan-tabs
                               )
        jb-dismis (button :text "Dismis")
        dia (dialog :title "Substance Skins"
                    :type :plain
                    :content pan-main
                    :on-close :dispose
                    :size [500 :by 400]
                    :options [jb-dismis])]
    (listen jb-dismis :action (fn [_]
                                (return-from-dialog dia true)))
    (show! dia)))
