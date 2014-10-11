(ns cadejo.instruments.algo.op-editor
  (:require [cadejo.instruments.algo.op-freq-editor])
  (:require [cadejo.instruments.algo.op-amp-editor])
  (:require [cadejo.instruments.algo.env-editor])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [seesaw.core :as ss])
  (:import javax.swing.event.ChangeListener))

(defn op-editor [op performance ied]
  (let [enable-change-listener* (atom true)
        has-highpass (or (= op 2)(= op 6)(= op 8))
        param-highpass (keyword (format "op%d-hp" op))
        frqed (cadejo.instruments.algo.op-freq-editor/op-freq-editor op performance ied)
        amped (cadejo.instruments.algo.op-amp-editor/op-amp-editor op performance ied)
        enved (cadejo.instruments.algo.env-editor/envelope-editor (keyword (format "op%d" op)) performance ied)
        slide-hp (let [s (ss/slider :orientation :vertical
                                    :value 1 :min 1 :max 100
                                    :minor-tick-spacing 5
                                    :major-tick-spacing 25
                                    :paint-labels? false)]
                   (.addChangeListener s (proxy [ChangeListener][]
                                           (stateChanged [ev]
                                             (if @enable-change-listener*
                                               (let [src (.getSource ev)
                                                     pos (.getValue src)]
                                                 (.set-param! ied param-highpass (float pos)))))))
                   (.setEnabled s has-highpass)
                   s)

        lab-hp (let [lab (ss/label :text "HP" :halign :center)]
                 (.setEnabled lab has-highpass)
                 lab)
        pan-hp (ss/border-panel :center slide-hp :south lab-hp)
        pan-main (ss/horizontal-panel :items [(:pan-main frqed)
                                              (:pan-main amped)
                                              ;; pan-hp
                                              ;; (:pan-main enved)
                                              ]
                                      :border (factory/title (format "OP %d" op)))
        syncfn (fn []
                 (reset! enable-change-listener* false)
                 ((:syncfn frqed))
                 ((:syncfn amped))
                 ((:syncfn enved))
                 (if has-highpass
                   (let [data (.current-data (.bank performance))
                         hp (int (get data param-highpass 1))]
                     (.setValue slide-hp hp)))
                 (reset! enable-change-listener* true))
        mutefn (fn [flag]
                 ((:mutefn frqed) flag)
                 ((:mutefn amped) flag)
                 ((:mutefn enved) flag)
                 (if has-highpass
                   (do
                     (.setEnabled slide-hp (not flag))
                     (.setEnabled lab-hp (not flag)))))]
    {:pan-main pan-main
     :syncfn syncfn
     :mutefn mutefn}))

(defn op123 [performance ied]
  (let [op1 (op-editor 1 performance ied)
        op2 (op-editor 2 performance ied)
        op3 (op-editor 3 performance ied)
        pan-main (ss/scrollable (ss/vertical-panel :items [(:pan-main op3)
                                                           (:pan-main op2)
                                                           (:pan-main op1)]))
        mutefn (fn [n flag]
                 (let [ed (get {1 op1 2 op2 3 op3} n)]
                   (if ed ((:mutefn ed) flag))))

        widget-map {:pan-main pan-main
                    :mutefn mutefn
                    :op1 op1
                    :op2 op2
                    :op3 op3}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo op123 does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo op123 editor init method not implemented"))
               )
            
             (sync-ui! [this]
               ((:syncfn op1))
               ((:syncfn op2))
               ((:syncfn op3))) )]
    (.add-sub-editor! ied "OP 1 2 3" ed)
    ed))

(defn op456 [performance ied]
  (let [op4 (op-editor 4 performance ied)
        op5 (op-editor 5 performance ied)
        op6 (op-editor 6 performance ied)
        pan-main (ss/scrollable (ss/vertical-panel :items [(:pan-main op5)
                                                           (:pan-main op6)
                                                           (:pan-main op4)]))
        mutefn (fn [n flag]
                 (let [ed (get {4 op4 5 op5 6 op6} n)]
                   (if ed ((:mutefn ed) flag))))

        widget-map {:pan-main pan-main
                    :mutefn mutefn
                    :op4 op4
                    :op5 op5
                    :op6 op6}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo op456 does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo op456 editor init method not implemented"))
               )
            
             (sync-ui! [this]
               ((:syncfn op4))
               ((:syncfn op5))
               ((:syncfn op6))) )]
    (.add-sub-editor! ied "OP 4 5 6" ed)
    ed))


(defn op78 [performance ied]
  (let [op7 (op-editor 7 performance ied)
        op8 (op-editor 8 performance ied)
        pan-main (ss/scrollable (ss/vertical-panel :items [(:pan-main op8)
                                                           (:pan-main op7)]))
        mutefn (fn [n flag]
                 (let [ed (get {7 op7 8 op8} n)]
                   (if ed ((:mutefn ed) flag))))

        widget-map {:pan-main pan-main
                    :mutefn mutefn
                    :op7 op7
                    :op8 op8}
        ed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
              
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "algo op78 does not have %s widget" key))))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (set-param! [this param val]
               (.set-param! ied param val))

             (init! [this] ;; ISSUE: not implemented
               (umsg/warning (format "algo op78 editor init method not implemented"))
               )
            
             (sync-ui! [this]
               ((:syncfn op7))
               ((:syncfn op8))) )]
    (.add-sub-editor! ied "OP 7 8" ed)
    ed))
