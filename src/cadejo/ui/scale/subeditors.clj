(ns cadejo.ui.scale.subeditors
  "Helper functions for registry-editor"
  (:use [cadejo.util.trace])
  (:require [cadejo.ui.util.validated-text-field :as vtf])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.scale.scale-utilities :as scale-util])
  (:use [seesaw.core :only [border-panel button config! config grid-panel 
                            horizontal-panel listen vertical-panel
                            spinner spinner-model]]))

(def msg-1 "No scale selected")
(def msg-2 "Some parameters are invalid")

(defn linear-editor [sced]
  (let [jb-reset (button :text "Reset")
        vtf-f1 (vtf/numeric-text-field :min 0 :max 20000 :value 0
                                       :border "F1")
        vtf-f2 (vtf/numeric-text-field :min 0 :max 20000 :value 20000
                                       :border "F2")
        jb-linear (button :text "Linear")
        jb-invert (button :text "Invert")
        pan-center (grid-panel :columns 1
                               :items [(.widget vtf-f1 :pan-main)
                                       (.widget vtf-f2 :pan-main)
                                       (vertical-panel)])
        pan-linear (border-panel :north jb-reset
                                 :center pan-center
                                 :south jb-linear
                                 :border (factory/title "Linear"))
        pan-main (border-panel :center pan-linear
                               :south jb-invert)]
        (listen jb-linear
                :action
                (fn [_]
                  (let [krange (.keyrange sced)
                        wrap (.wraprange sced)
                        tt (.selected-table sced)
                        f1 (.get-value vtf-f1)
                        f2 (.get-value vtf-f2)]
                    (cond (not tt)
                          (.warning! sced msg-1)
                          
                          (or (not krange)(not wrap)(not f1)(not f2))
                          (.warning! sced msg-2)
                          
                          :default
                          (do
                            (.push-undo-state! sced "Set linear range")
                            (scale-util/linear! tt 
                                                :keyrange krange
                                                :wrap wrap
                                                :f1 f1
                                                :f2 f2)
                            (.status! sced (format 
                                            "Linear segment set for scale %s" 
                                            (.selected-table-id sced))))))))
        (listen jb-invert 
                :action 
                (fn [_]
                  (let [krange (.keyrange sced)
                        tt (.selected-table sced)]
                    (if tt
                      (do (.push-undo-state! sced "Invert table range")
                          (scale-util/invert! tt :keyrange krange)
                          (.status! sced "Key range inverted"))
                      (.warning! sced msg-1)))))
        (listen jb-reset :action (fn [_]
                                   (.set-value vtf-f1 0)
                                   (.set-value vtf-f2 20000)))
    pan-main))


(defn transpose-editor [sced]
  (let [krange (.keyrange sced)
        wrap (.wraprange sced)
        jb-reset (button :text "Reset")
        jb-transpose (button :text "Transpose")
        spin-steps (spinner :model (spinner-model 0 :min -24 :max 24 :step 1))
        spin-cents (spinner :model (spinner-model 0 :min -99 :max 99 :step 1))
        spin-bias (spinner :model (spinner-model 0 :min -100 :max 100 :step 1))
        pan-steps (vertical-panel :items [spin-steps]
                                  :border (factory/title "Steps"))
        pan-cents (vertical-panel :items [spin-cents]
                                  :border (factory/title "Cents"))
        pan-bias (vertical-panel :items [spin-bias]
                                 :border (factory/title "Bias"))
        pan-center (grid-panel :columns 1 
                               :items [pan-steps pan-cents 
                                       pan-bias 
                                       (vertical-panel)])
        pan-main (border-panel :north jb-reset
                               :center pan-center
                               :south jb-transpose
                               :border (factory/title "Transpose"))]
    (listen jb-reset :action (fn [_]
                               (.setValue spin-steps 0)
                               (.setValue spin-cents 0)
                               (.setValue spin-bias 0)))
    (listen jb-transpose :action 
            (fn [_]
              (let [krange (.keyrange sced)
                    wrap (.wraprange sced)
                    tt (.selected-table sced)
                    s (.getValue spin-steps)
                    c (.getValue spin-cents)
                    b (.getValue spin-bias)]
                (cond (not tt)
                      (.warning! sced msg-1)
                      
                      (or (not krange)(not wrap)(not s)(not c)(not b))
                      (.warning! sced msg-2)
                      
                      :default
                      (let [xpose (+ (* 100 s) c)]
                        (.push-undo-state! sced "Transpose")
                        (scale-util/transpose! tt xpose
                                               :keyrange krange
                                               :wrap wrap
                                               :bias b)
                        (.status! sced "Transposed key range"))))))
    pan-main))
        
        
(defn splice-editor [sced lab-src lab-dst]
  (let [jb-clear (button :text "X")
        jb-swap (button :text "<->")
        jb-dup (button :text "=")
        pan-north (grid-panel :rows 1
                              :items [jb-clear jb-swap jb-dup])
        spin-location (spinner :model (spinner-model 0 :min 0 :max 127 :by 1))
        pan-location (vertical-panel :items [spin-location]
                                     :border (factory/title "Location"))
        jb-splice (button :text "Splice")
        pan-scales (grid-panel :columns 1 :items [lab-src
                                                  lab-dst
                                                  pan-location
                                                  (vertical-panel)])
        pan-main (border-panel :north pan-north
                               :center pan-scales
                               :south jb-splice
                               :border (factory/title "Splice"))]
    (listen jb-clear :action (fn [_]
                               (config! lab-src :text " ")
                               (config! lab-dst :text " ")))
    (listen jb-swap :action (fn [_]
                              (let [src (config lab-src :text)
                                    dst (config lab-dst :text)]
                                (config! lab-src :text dst)
                                (config! lab-dst :text src))))
    (listen jb-dup :action (fn [_]
                             (config! lab-dst :text
                                      (config lab-src :text))))
    (listen jb-splice :action (fn [_]
                                (let [src (keyword (config lab-src :text))
                                      dst (keyword (config lab-dst :text))
                                      krange (.keyrange sced)
                                      location (.getValue spin-location)]
                                  (cond (or (= (count (name src)) 1)
                                            (= (count (name dst)) 1))
                                        (.warning! sced "Splice source or destination scales invalid")

                                        (not krange)
                                        (.warning! sced "Splice keyrange not valid")

                                        (not (integer? location))
                                        (.warning! sced "Splice location value not valid")

                                        :default
                                        (let [stt (.table (.registry sced) src)
                                              dtt (.table (.registry sced) dst)]
                                          (.push-undo-state! sced "Splice")
                                          (scale-util/splice! stt krange dtt location)
                                          (.status! sced "Scales spliced"))))))
    pan-main))
    
                               
