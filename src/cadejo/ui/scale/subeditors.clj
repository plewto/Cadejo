(ns cadejo.ui.scale.subeditors
  "Helper functions for registry-editor"
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.validated-text-field :as vtf])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.scale.scale-utilities :as scale-util])
  (:require [seesaw.core :as ss]))

(def msg-1 "No scale selected")
(def msg-2 "Some parameters are invalid")

(defn linear-editor [sced]
  (let [vtf-f1 (vtf/numeric-text-field :min 0 :max 20000 :value 0
                                       :border "F1")
        vtf-f2 (vtf/numeric-text-field :min 0 :max 20000 :value 20000
                                       :border "F2")
        jb-reset (factory/button "Reset" :general :reset "Reset linear parameters")
        jb-linear (factory/button "Linear" :general :linear "Set linear table values")
        jb-invert (factory/button "Invert" :general :invert "Invert table range")
        ;; pan-north (ss/toolbar :orientation :horizontal
        ;;                       :floatable? false
        ;;                       :items [jb-reset jb-linear jb-invert])

        pan-north (ss/horizontal-panel :items [jb-reset jb-linear jb-invert])
        
        pan-center (ss/vertical-panel :items [(.widget vtf-f1 :pan-main)
                                                (.widget vtf-f2 :pan-main)])
        pan-main (ss/border-panel :north pan-north
                                  :center pan-center
                                  :border (factory/title "Linear"))]
    (ss/listen jb-linear
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
                                            "Linear segment set for table %s" 
                                            (.selected-table-id sced))))))))
        (ss/listen jb-invert 
                :action 
                (fn [_]
                  (let [krange (.keyrange sced)
                        tt (.selected-table sced)]
                    (if tt
                      (do (.push-undo-state! sced "Invert table range")
                          (scale-util/invert! tt :keyrange krange)
                          (.status! sced "Key range inverted"))
                      (.warning! sced msg-1)))))
        (ss/listen jb-reset :action (fn [_]
                                   (.set-value vtf-f1 0)
                                   (.set-value vtf-f2 20000)))
    pan-main))


(defn transpose-editor [sced]
  (let [krange (.keyrange sced)
        wrap (.wraprange sced)
        jb-reset (factory/button "Reset" :general :reset "Reset transpose parameters")
        jb-transpose (factory/button "Transpose" :general :transpose "Transpose table range")
        spin-steps (ss/spinner :model (ss/spinner-model 0 :min -24 :max 24 :step 1 :size [114 :by 24]))
        spin-cents (ss/spinner :model (ss/spinner-model 0 :min -99 :max 99 :step 1 :size [114 :by 24]))
        spin-bias (ss/spinner :model (ss/spinner-model 0 :min -100 :max 100 :step 1 :size [114 :by 24]))
        pan-steps (ss/vertical-panel :items [spin-steps]
                                     :size [114 :by 36]
                                     :border (factory/title "Steps"))
        pan-cents (ss/vertical-panel :items [spin-cents]
                                     :size [114 :by 36]
                                     :border (factory/title "Cents"))
        pan-bias (ss/vertical-panel :items [spin-bias]
                                    :size [114 :by 36]
                                    :border (factory/title "Bias"))
        pan-north (ss/horizontal-panel :items [jb-reset jb-transpose])
        pan-center (ss/grid-panel :columns 1 :items [pan-steps pan-cents pan-bias])
        pan-main (ss/border-panel :north pan-north
                                  :center pan-center
                                  :border (factory/title "Transpose"))]
    (ss/listen jb-reset :action (fn [_]
                               (.setValue spin-steps 0)
                               (.setValue spin-cents 0)
                               (.setValue spin-bias 0)))
    (ss/listen jb-transpose :action 
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
  (let [jb-clear (ss/button :text "X")
        jb-swap (ss/button :text "<->")
        jb-dup (ss/button :text "=")
        jb-splice (factory/button "Splice" :general :splice "Splce selected tables")
        pan-north (ss/grid-panel :rows 1
                              :items [jb-clear jb-swap jb-dup jb-splice])
        spin-location (ss/spinner :model (ss/spinner-model 0 :min 0 :max 127 :by 1))
        pan-location (ss/vertical-panel :items [spin-location]
                                     :border (factory/title "Location"))
        
        pan-scales (ss/grid-panel :columns 1 :items [lab-src
                                                  lab-dst
                                                  pan-location
                                                  (ss/vertical-panel)])
        pan-main (ss/border-panel :north pan-north
                               :center pan-scales
                               :border (factory/title "Splice"))]
    (ss/listen jb-clear :action (fn [_]
                               (ss/config! lab-src :text " ")
                               (ss/config! lab-dst :text " ")))
    (ss/listen jb-swap :action (fn [_]
                              (let [src (ss/config lab-src :text)
                                    dst (ss/config lab-dst :text)]
                                (ss/config! lab-src :text dst)
                                (ss/config! lab-dst :text src))))
    (ss/listen jb-dup :action (fn [_]
                             (ss/config! lab-dst :text
                                      (ss/config lab-src :text))))
    (ss/listen jb-splice :action (fn [_]
                                (let [src (keyword (ss/config lab-src :text))
                                      dst (keyword (ss/config lab-dst :text))
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
    ;; (if (config/enable-button-text)
    ;;   (do
    ;;     (ss/config! jb-splice :text "Splice")))
    ;; (if (config/enable-button-icons)
    ;;   (do
    ;;     (.setIcon jb-splice (lnf/read-icon :general :splice))
    ;;     (.setSelectedIcon jb-splice (lnf/read-selected-icon :general :splice))))
    (if (config/enable-tooltips)
      (do
        (.setToolTipText jb-clear "Clear splice parameters")
        (.setToolTipText jb-swap "Exchange splice source and destination tables")
        (.setToolTipText jb-dup "Set splice source and destination to same table")))
    pan-main))
    
                               
