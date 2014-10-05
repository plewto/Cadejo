(ns cadejo.ui.scale.addscale
  (:require [cadejo.config :as config])
  (:require [cadejo.scale.eqtemp])
  (:require [cadejo.scale.just])
  (:require [cadejo.scale.table])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.validated-text-field :as vtf])
  (:require  [seesaw.core :as ss]) 
  (:import javax.swing.JLabel
           javax.swing.event.CaretListener
           javax.swing.event.ListSelectionListener))

(def msg-1 "Scale id must be provided before adding new scale")
(def msg-2 "No just scale selected")
(def msg-3 "A440 tuning value is invalid")
(def msg-4 "Eq-temp parameters are invalid")

(defn- scale-id-validator [s]
  (and (pos? (count s))
       (= (.indexOf s " ") -1)
       (= (.indexOf s ":") -1)))

(defn- scale-id-postfn [s]
  (if (zero? (count s))
    nil
    (keyword s)))

(defn- add-blank [sced vtf-id]
  (let [jb-blank (factory/button "Blank" :keyboard :blank "Add empty table") 
        pan-blank (ss/border-panel :south jb-blank
                                   :border (factory/title "Blank"))]
    (ss/listen jb-blank :action
            (fn [_]
              (let [id (.get-value vtf-id)
                    registry (.registry sced)]
                (if id 
                  (let [tt (cadejo.scale.table/create-tuning-table)]
                    (.push-undo-state! sced "Add blank scale")
                    (.put-property! tt :name id)
                    (.add-table! registry id tt)
                    (.sync-ui! sced)
                    (.status! sced (format "Added blank tuning table %s" id)))
                  (.warning! sced msg-1)))))
 
    pan-blank))
                    
(defn- add-just [sced vtf-id vtf-tune]
  (let [lst-scales (ss/listbox :model (cadejo.scale.just/just-tables))
        jb-just (factory/button "Just" :keyboard :just "Add table with just scale")
        pan-center (ss/vertical-panel :items [(ss/scrollable lst-scales)]
                                      :border (factory/padding))
        pan-just (ss/border-panel :center pan-center
                                  :south jb-just
                                  :border (factory/title "Just"))]
    (.addListSelectionListener
     lst-scales
     (proxy [ListSelectionListener][]
       (valueChanged [_]
         (let [js-key (name (.getSelectedValue lst-scales))]
           (.set-value vtf-id js-key)))))

     (ss/listen jb-just :action
             (fn [_]
               (let [id (.get-value vtf-id)
                     js-key (.getSelectedValue lst-scales)
                     a440 (.get-value vtf-tune)]
                 (cond (not id)
                       (.warning! sced msg-1)

                       (not js-key)
                       (.warning! sced msg-2)

                       (not a440)
                       (.warning! sced msg-3)

                       :default
                       (let [tt (cadejo.scale.just/just-table
                                 (keyword js-key) a440)]
                         (.push-undo-state! sced (format "Add Just Scale %s" id))
                         (.put-property! tt :name id)
                         (.add-table! (.registry sced) (keyword id) tt)
                         (.sync-ui! sced)
                         (.status! sced (format "Added just scale %s" id)))))))
     ;; (if (config/enable-button-text)
     ;;   (ss/config! jb-just :text "Add Just Scale"))
     ;; (if (config/enable-button-icons)
     ;;   (do
     ;;     (.setIcon jb-just (lnf/read-icon :keyboard :just))
     ;;     (.setSelectedIcon jb-just (lnf/read-selected-icon :keyboard :just))))
     ;; (if (config/enable-tooltips)
     ;;   (.setToolTipText jb-just "Add tuning table with just scale"))
     pan-just))


(defn- add-eqtemp [sced vtf-id vtf-tune]
  (let [vtf-npo (vtf/integer-text-field :min 6 :max 360 :value 12
                                        :border "Notes Per Octave")
        txt-npo (.widget vtf-npo :text-field)
        vtf-octave (vtf/numeric-text-field :min 1.5 :max 3.0 :value 2.0
                                           :border "Octave Size")
        jb-eq (factory/button "Eq Temp" :keyboard :eqtemp "Add table with equal tempered scale") ; (ss/button)
        pan-north (ss/grid-panel :columns 1
                              :items [(.widget vtf-npo :pan-main)
                                      (.widget vtf-octave :pan-main)])
        pan-eq (ss/border-panel :north pan-north
                                :south jb-eq
                                :border (factory/title "Equal Temp"))]
    (.addCaretListener txt-npo
                       (proxy [CaretListener][]
                         (caretUpdate [_]
                           (let [npo (.get-value vtf-npo)
                                 id (format "eq-%s" npo)]
                             (.set-value vtf-id id)))))
    (ss/listen jb-eq :action 
            (fn [_]
              (let [id (.get-value vtf-id)
                    npo (.get-value vtf-npo)
                    octave (.get-value vtf-octave)
                    a440 (.get-value vtf-tune)]
                (cond (not id)
                      (.warning! sced msg-1)

                      (not a440)
                      (.warning! sced msg-3)

                      (or (not npo)(not octave))
                      (.warning! sced msg-4)

                      :default
                      (let [tt (cadejo.scale.eqtemp/eqtemp-table npo a440 octave)]
                        (.push-undo-state! sced "Add eqtemp scale")
                        (.put-property! tt :name id)
                        (.add-table! (.registry sced)(keyword id) tt)
                        (.sync-ui! sced)
                        (.status! sced (format "Added equal temp scale %s" id)))))))
    ;; (if (config/enable-button-text)
    ;;   (ss/config! jb-eq :text "Add Eq Temp Scale"))
    ;; (if (config/enable-button-icons)
    ;;   (do
    ;;     (.setIcon jb-eq (lnf/read-icon :keyboard :eqtemp))
    ;;     (.setSelectedIcon jb-eq (lnf/read-selected-icon :keyboard :eqtemp))))
    ;; (if (config/enable-tooltips)
    ;;   (.setToolTipText jb-eq "Add tuning table with equal tempered scale"))
    pan-eq))



(defn addscale [sced]
  (let [vtf-id (vtf/validated-text-field :border "Scale ID"
                                         :validator scale-id-validator
                                         :post scale-id-postfn)
        vtf-tune (vtf/numeric-text-field :border "Tune A440"
                                         :min 400.0 :max 500.0 :value 444.0)
        pan-north (ss/horizontal-panel :items [(.widget vtf-id :pan-main)
                                               (.widget vtf-tune :pan-main)])
        pan-center (ss/grid-panel 
                    :rows 1
                    :items [(add-blank sced vtf-id)
                            (add-just sced vtf-id vtf-tune)
                            (add-eqtemp sced vtf-id vtf-tune)])
        pan-main (ss/border-panel :north pan-north
                                  :center pan-center)]
    pan-main))

            
