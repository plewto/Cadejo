(ns cadejo.instruments.algo.editor.freq-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.instruments.algo.editor.overview :as overview])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.color-utilities :as cutil])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.icon :as icon])
  (:require [sgwr.indicators.keypad :as keypad])
  (:require [sgwr.indicators.numberbar :as numberbar])
  (:require [seesaw.core :as ss]))

(defn freq-editor [performance ied]
  (let [op-count 8
        is-carrier (fn [op](or (= op 1)(= op 4)(= op 7)))
        oed (overview/overview-editor performance ied)
        [bg inactive active](let [[cbg cna cav](config/displaybar-colors) 
                                  skin (config/current-skin)]
                              [(cutil/color (or cbg (lnf/get-color skin :text-fg)))
                               (cutil/color (or cna (lnf/get-color skin :text-bg)))
                               (cutil/color (or cav (lnf/get-color skin :text-bg-selected)))])
        [freq-bars bias-bars amp-bars] (let [fcc* (atom [])
                                             bcc* (atom [])
                                             acc* (atom [])]
                                         (dotimes [i op-count]
                                           (let [barf (numberbar/numberbar 6 0.0 99)
                                                 barb (numberbar/numberbar 5 -4000 4000)
                                                 bara (if (is-carrier (inc i))
                                                        (numberbar/numberbar 5 0.0 1.0)
                                                        (numberbar/numberbar 5 0.0 1000.0))]
                                             (swap! fcc* (fn [q](conj q barf)))
                                             (swap! bcc* (fn [q](conj q barb)))
                                             (swap! acc* (fn [q](conj q bara)))
                                             (.colors! barf bg inactive active)
                                             (.colors! barb bg inactive active)
                                             (.colors! bara bg inactive active)))
                                         [@fcc* @bcc* @acc*]) 
        kpad (let [kp (keypad/numeric-keypad true true)]
               (ss/config! (.widget kp :pan-main) :size [383 :by 217])
               (.displaybar! kp (first freq-bars) false)
               kp)

        set-param (fn [param val]
                    (.set-param! ied param (float val))
                    (let [data (.current-data (.bank performance))]
                      ((:syncfn oed) data)))

        selected-button* (atom nil)
        button-action (fn [ev]
                        ;; Update synth param from previous displaybar
                        (let [previous @selected-button*
                              param (.getClientProperty previous :param)
                              numbar (.getClientProperty previous :display)
                              val (.value numbar)]
                          (set-param param val))
                        ;; Switch keypad entery to new numberbar
                        (let [src (.getSource ev)
                              numbar (.getClientProperty src :display)]
                          (.displaybar! kpad numbar false)
                          (reset! selected-button* src))
                        (.sync-ui! ied))

        bgroup (ss/button-group)
        selection-buttons (let [acc* (atom [])]
                            (dotimes [i op-count]
                              (let [op (inc i)
                                    bf (ss/toggle :text (format "OP%d Freq" op)
                                                  :group bgroup)
                                    bb (ss/toggle :text (format "OP%d Bias" op)
                                                  :group bgroup)
                                    ba (ss/toggle :text (format "OP%d Amp" op)
                                                  :group bgroup)]
                                (.putClientProperty bf :display (nth freq-bars i))
                                (.putClientProperty bb :display (nth bias-bars i))
                                (.putClientProperty ba :display (nth amp-bars i))
                                (.putClientProperty bf :op op)
                                (.putClientProperty bb :op op)
                                (.putClientProperty ba :op op)
                                (.putClientProperty bf :param (keyword (format "op%d-%s" op "detune")))
                                (.putClientProperty bb :param (keyword (format "op%d-%s" op "bias")))
                                (.putClientProperty ba :param (keyword (format "op%d-%s" op "amp")))
                                (swap! acc* (fn [q](conj q bf)))
                                (swap! acc* (fn [q](conj q bb)))
                                (swap! acc* (fn [q](conj q ba)))
                                (ss/listen bf :action button-action)
                                (ss/listen bb :action button-action)
                                (ss/listen ba :action button-action)))
                            (.setSelected (first @acc*) true)
                            (reset! selected-button* (first @acc*))
                            (.displaybar! kpad (first freq-bars) false)
                            @acc*)
        op-panel (fn [i]
                   (let [op (inc i)
                         barf (nth freq-bars i)
                         barb (nth bias-bars i)
                         bara (nth amp-bars i)
                         offset (* i 3)
                         rbf (nth selection-buttons (+ offset 0))
                         rbb (nth selection-buttons (+ offset 1))
                         rba (nth selection-buttons (+ offset 2))
                         panf (ss/border-panel :west rbf :center (.drawing-canvas barf))
                         panb (ss/border-panel :west rbb :center (.drawing-canvas barb))
                         pana (ss/border-panel :west rba :center (.drawing-canvas bara))
                         pan (ss/grid-panel :columns 1 :items [panf panb pana]
                                            :border (factory/title (format "OP%d" op))
                                            :size [383 :by 217])]
                     pan)) 
        pan-stack-a (ss/vertical-panel :items [(op-panel 2)(op-panel 1)(op-panel 0)])
        pan-stack-b (ss/vertical-panel :items [(op-panel 5)(op-panel 4)(op-panel 3)])
        pan-stack-c (ss/vertical-panel :items [(.widget kpad :pan-main)(op-panel 7)(op-panel 6)])
        pan-logo (ss/grid-panel :columns 1 
                                :items [(ss/label :icon (icon/logo "algo_graph" nil))]
                                :border (factory/padding 16))
        pan-center (ss/horizontal-panel :items [pan-stack-a pan-stack-b pan-stack-c pan-logo])
       
        
        pan-main (ss/border-panel :center pan-center
                                  :south (:pan-main oed))
        widget-map {:pan-main (ss/scrollable pan-main)}
        ed (reify subedit/InstrumentSubEditor
             
             (widgets [this] widget-map)

             (widget [this key]
               (get widget-map key))

             (parent [this] ied)

             (parent! [this _] ied) ;; ignore

             (status! [this msg]
               (.status! ied msg))

             (warning! [this msg]
               (.warning! ied msg))

             (init! [this]
               (ss/config! (first selection-buttons) :selected? true))

             (sync-ui! [this]
               (let [data (.current-data (.bank performance))
                     freqlst [(:op1-detune data)(:op2-detune data)
                              (:op3-detune data)(:op4-detune data)
                              (:op5-detune data)(:op6-detune data)
                              (:op7-detune data)(:op8-detune data)]
                     biaslst [(:op1-bias data)(:op2-bias data)
                              (:op3-bias data)(:op4-bias data)
                              (:op5-bias data)(:op6-bias data)
                              (:op7-bias data)(:op8-bias data)]
                     amplst [(:op1-amp data)(:op2-amp data)
                             (:op3-amp data)(:op4-amp data)
                             (:op5-amp data)(:op6-amp data)
                             (:op7-amp data)(:op8-amp data)]
                     enablelst [(:op1-enable data)(:op2-enable data)
                                (:op3-enable data)(:op4-enable data)
                                (:op5-enable data)(:op6-enable data)
                                (:op7-enable data)(:op8-enable data)]]
                 ((:syncfn oed) data)
                 (dotimes [i op-count]
                   (let [f (nth freqlst i)
                         b (nth biaslst i)
                         a (nth amplst i)
                         e (nth enablelst i)
                         barf (nth freq-bars i)
                         barb (nth bias-bars i)
                         bara (nth amp-bars i)
                         offset (* i 3)
                         rbf (nth selection-buttons (+ offset 0))
                         rbb (nth selection-buttons (+ offset 1))
                         rba (nth selection-buttons (+ offset 2))]
                     (doseq [rb [rbf rbb rba]]
                       (ss/config! rb :enabled? (not (zero? e))))
                     (if (zero? e)
                       (do
                         (.off! barf true)
                         (.off! barb true)
                         (.clear! bara)
                         (let [i* (atom 0)]
                           (doseq [ch (reverse "MUTE")]
                             (let [cell (.cell bara @i*)]
                               (.set-character! cell ch)
                               (swap! i* inc))))
                         (.render (.drawing bara)))
                       (do 
                         (.off! barf false)
                         (.off! barb false)
                         (.display! barf (str f))
                         (.display! barb (str b))
                         (.display! bara (str a)))))))) )]
    (.enter-action! kpad (fn [_]
                           (let [src @selected-button*
                                 param (.getClientProperty src :param)
                                 numbar (.getClientProperty src :display)
                                 val (.value numbar)]
                             (set-param param val)
                             (.sync-ui! ied))))
    ed)) 
