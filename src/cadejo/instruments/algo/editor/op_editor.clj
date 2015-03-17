(ns cadejo.instruments.algo.editor.op-editor
  ;(:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.instruments.algo.editor.op-amp-panel :as oap])
  (:require [cadejo.instruments.algo.editor.envelope-panel :as ep])
  (:require [cadejo.instruments.algo.editor.op-feedback-panel :as fbp])
  (:require [cadejo.instruments.algo.editor.op-freq-panel :as ofp])
  (:require [cadejo.instruments.algo.editor.op-keyscale-panel :as ksp])
  (:require [cadejo.instruments.algo.editor.op-observer :as ovr])
  (:require [cadejo.instruments.algo.editor.op-selection-panel :as osp])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing :as drw])
  (:require [sgwr.util.color :as uc])
  (:require [seesaw.core :as ss]))


(defn- op-label [drw p0 n]
  (sfactory/text drw p0 (format "OP %d" n)
                 :style :sans-bold
                 :size 18
                 :color (lnf/text)))

(defn- is-carrier? [op]
  (or (= op 1)(= op 4)(= op 7)))

(defn- has-feedback? [op]
  (or (= op 6)(= op 8)))

(def ^:private op-params  
  '(detune bias amp left-key left-scale right-key
           right-scale attack decay1 decay2 breakpoint
           sustain release velocity pressure cca ccb lfo1 lfo2))

;; Feedback parameters excluded
;;
(def ^:private clipboard* (atom nil))

;; Copy op parameters (sans feedback) to clipboard
;
(defn- copy-op [ied op]
  (let [dmap (.current-data (.bank (.parent-performance ied)))
        acc* (atom {})]
    (doseq [p op-params]
      (let [psrc (keyword (format "op%d-%s" op (name p)))
            val (psrc dmap)
            pdst (keyword (name p))]
        (swap! acc* (fn [q](assoc q pdst val)))))
    (reset! clipboard* @acc*)
    (.status! ied (format "OP %d copied to clipboard" op))))
            
(defn- paste-op [ied op]
  (if @clipboard*
    (do 
      (doseq [p op-params]
        (let [src (keyword (name p))
              op (keyword (format "op%d-%s" op p))
              val (src @clipboard*)]
          (.set-param! ied op val)))
      (.sync-ui! ied)
      (.status! ied (format "Clipboard pasted to op %s" op)))
    (.warning! ied "Nothing to paste")))
        
(defn- init-op [ied op]
  (.working ied true)
  (let [dmap {(keyword (format "op%d-detune" op)) 1.00 
              (keyword (format "op%d-bias" op)) 0.0
              (keyword (format "op%d-amp" op)) (if (= op 1) 1.0 0.0)
              (keyword (format "op%d-left-key" op)) 48
              (keyword (format "op%d-left-scale" op)) 0
              (keyword (format "op%d-right-key" op)) 64
              (keyword (format "op%d-right-scale" op)) 0
              (keyword (format "op%d-attack" op)) 0.0
              (keyword (format "op%d-decay1" op)) 0.0
              (keyword (format "op%d-decay2" op)) 0.0
              (keyword (format "op%d-breakpoint" op)) 1.0
              (keyword (format "op%d-sustain" op)) 1.0
              (keyword (format "op%d-release" op)) 0.0
              (keyword (format "op%d-velocity" op)) 0.0
              (keyword (format "op%d-pressure" op)) 0.0
              (keyword (format "op%d-cca" op)) 0.0
              (keyword (format "op%d-ccb" op)) 0.0
              (keyword (format "op%d-lfo1" op)) 0.0
              (keyword (format "op%d-lfo2" op)) 0.0}]
    (doseq [[p v] dmap](.set-param! ied p v)))
  (if (has-feedback? op)
    (let [dmap {(keyword (format "op%d-feedback" op)) 0.0
                (keyword (format "op%d-env" op)) 0.0
                (if (= op 6) :op6-lfo1->feedback :op8-lfo2->feedback) 0
                (keyword (format "op%d-pressure" op)) 0.0
                (keyword (format "op%d-cca" op)) 0
                (keyword (format "op%d-ccb" op)) 0
                (keyword (format "op%d-hp" op)) 40}]
      (doseq [[p v] dmap](.set-param! ied p v))))
  (.sync-ui! ied)
  (.working ied false)
  (.status! ied (format "Init op %s" op)))

(defn- dice-op [ied op]
  (let [f (math/approx (math/coin 0.70 
                                  (math/pick [0.25 0.50 0.50 
                                              1.0 1.0 1.0 1.0
                                              2.0 2.0
                                              3.0 4.0 5.0 6.0])
                                  (math/rand-range 0.25 8.0)))
        b (math/coin 0.70 0 (math/coin 0.5 (rand 10)(rand 300)))
        a (if (is-carrier? op) 
            (math/rand-range 0.333 1.0)
            (math/coin 0.80 (math/rand-range 0.5 2.0)(+ 1.0 (rand 10))))
        time-scale (math/pick '[0.5 1.0 1.0 1.0 1.0 2.0 2.0 4.0 8.0])
        att (* time-scale (rand))
        dcy1 (* time-scale (rand))
        dcy2 (* time-scale (rand))
        rel (* time-scale (rand))
        breakpoint (math/coin 0.75 (math/rand-range 0.5 1.0)(rand))
        sustain (math/coin 0.75 (math/rand-range 0.5 1.0)(rand))
        sign (fn [n](* n (math/coin 0.5 -1 1)))
        velocity (math/coin 0.50 (rand) 0.0)
        pressure (sign (math/coin 0.75 0.0 (rand)))
        cca (sign (math/coin 0.75 0.0 (rand)))
        ccb (sign (math/coin 0.75 0.0 (rand)))
        lfo1 (math/coin 0.75 0.0 (rand))
        lfo2 (math/coin 0.75 0.0 (rand))
        dmap {(keyword (format "op%d-detune" op)) f
              (keyword (format "op%d-bias" op)) b
              (keyword (format "op%d-amp" op)) a
              (keyword (format "op%d-attack" op)) att
              (keyword (format "op%d-decay1" op)) dcy1
              (keyword (format "op%d-decay2" op)) dcy2
              (keyword (format "op%d-breakpoint" op)) breakpoint
              (keyword (format "op%d-sustain" op)) sustain
              (keyword (format "op%d-release" op)) rel
              (keyword (format "op%d-velocity" op)) velocity
              (keyword (format "op%d-pressure" op)) pressure
              (keyword (format "op%d-cca" op)) cca
              (keyword (format "op%d-ccb" op)) ccb
              (keyword (format "op%d-lfo1" op)) lfo1
              (keyword (format "op%d-lfo2" op)) lfo2}]
    (doseq [[p v] (seq dmap)](.set-param! ied p v))
    (if (has-feedback? op)
      (let [sign (fn [n](* (math/coin 0.5 -1 1) op))
            dmap {(keyword (format "op%d-feedback" op))(math/coin 0.5 0.0 (* 4 (rand)))
                  (keyword (format "op%d-env->feedback" op))(sign (math/coin 0.75 0 (rand 4)))
                  (if (= op 6) :op6-lfo1->feedback :op8-lfo2->feedback)(sign (math/coin 0.75 0 (rand 4)))
                  (keyword (format "op%d-pressure->feedback" op))(math/coin 0.5 0.0 (* 4 (rand)))
                  (keyword (format "op%d-cca->feedback" op))(math/coin 0.5 0.0 (* 4 (rand)))
                  (keyword (format "op%d-ccb->feedback" op))(math/coin 0.5 0.0 (* 4 (rand)))}]
        (doseq [[p v](seq dmap)](.set-param! ied p v))))
    (.sync-ui! ied)
    (.status! ied (format "Randomize OP %s" op))))


(defn op-editor [op ied]
  (println (format "-->     op %d" (- 9 op)))
  (let [enable-param (keyword (format "op%d-enable" op))
        drw (let [d (drw/native-drawing 790 690)]
              (.background! d (lnf/background))
              d)
        tools (.tool-root drw)
        [x0 y0] [0 670]
        dummy-1 (sfactory/fpo drw [x0 y0] :style [:dot] :size 3 :color :blue) ;; DEBUG
        x-gap 20
        y-gap 20
        x1 (+ x0 x-gap)
        y1 (- y0 y-gap)
        x-over (+ x1 0)
        x-keyscale x1
        x-amp x1
        x-freq x1
        x-feedback (+ x-freq 350)
        x-algo (+ x-feedback 40)
        x-label (+ x0 10) 
        x-init (+ x-label 100)
        x-dice (+ x-init 50)
        x-copy (+ x-dice 50)
        x-paste (+ x-copy 50)
        ;x-help (+ x-paste 50)
        y-over (+ y1 30)
        y-keyscale (- y-over 120)
        y-amp (- y-keyscale 100)
        y-freq (- y-amp 230)
        y-feedback y-amp
        y-algo (- y-feedback 240)
        y-label (- y0 605)
        y-init (- y-label 40)
        y-dice y-init
        y-copy y-init
        y-paste y-init
        ;y-help y-init
        this* (atom nil)
        amp-pan (oap/op-amp op drw [x-amp y-amp] ied)
        overview-pan (ovr/overview-panel drw [x-over y-over] ied this*)
        kscale-pan (ksp/op-keyscale op drw [x-keyscale y-keyscale] ied)
        freq-pan (ofp/op-freq op drw [x-freq y-freq] ied)
        fb-pan (fbp/op-feedback-panel op drw [x-feedback y-feedback] ied)
        env-pan (ep/envelope-panel op ied)

        ;; occ-1 (sfactory/occluder drw [(+ x0  24)(- y0 143)][(+ x0 763)(- y0 237)]) ;; keyscale
        ;; occ-2 (sfactory/occluder drw [(+ x0  24)(- y0 245)][(+ x0 365)(- y0 465)]) ;; amp
        ;; occ-3 (sfactory/occluder drw [(+ x0 372)(- y0 245)][(+ x0 768)(- y0 465)]) ;; fb panel
        ;; occ-4 (sfactory/occluder drw [(+ x0  24)(- y0 472)][(+ x0 365)(- y0 566)]) ;; freq panel

        occ-1 (sfactory/occluder drw [(+ x0  24)(- y0 109)][(+ x0 763)(- y0 203)]) ;; keyscale
        occ-2 (sfactory/occluder drw [(+ x0  24)(- y0 211)][(+ x0 365)(- y0 431)]) ;; amp
        occ-3 (sfactory/occluder drw [(+ x0 372)(- y0 211)][(+ x0 768)(- y0 431)]) ;; fb panel
        occ-4 (sfactory/occluder drw [(+ x0  24)(- y0 438)][(+ x0 365)(- y0 532)]) ;; freq panel

        ;; occ-op1 (sfactory/occluder drw [(+ x0  429)(- y0 519)][(+ x0 462)(- y0 552)]) 
        ;; occ-op2 (sfactory/occluder drw [(+ x0  429)(- y0 569)][(+ x0 462)(- y0 602)]) 
        ;; occ-op3 (sfactory/occluder drw [(+ x0  429)(- y0 619)][(+ x0 462)(- y0 652)]) 
        ;; occ-op4 (sfactory/occluder drw [(+ x0  479)(- y0 519)][(+ x0 512)(- y0 552)]) 
        ;; occ-op5 (sfactory/occluder drw [(+ x0  479)(- y0 569)][(+ x0 512)(- y0 602)]) 
        ;; occ-op6 (sfactory/occluder drw [(+ x0  529)(- y0 569)][(+ x0 562)(- y0 602)]) 
        ;; occ-op7 (sfactory/occluder drw [(+ x0  589)(- y0 519)][(+ x0 622)(- y0 552)]) 
        ;; occ-op8 (sfactory/occluder drw [(+ x0  589)(- y0 569)][(+ x0 622)(- y0 602)]) 

        occ-op1 (sfactory/occluder drw [(+ x0  429) (- y0 493) ][(+ x0 462)  (- y0 518)]) 
        occ-op2 (sfactory/occluder drw [(+ x0  429) (- y0 543) ][(+ x0 462)  (- y0 568)]) 
        occ-op3 (sfactory/occluder drw [(+ x0  429) (- y0 593) ][(+ x0 462)  (- y0 618)]) 
        occ-op4 (sfactory/occluder drw [(+ x0  479) (- y0 493) ][(+ x0 512)  (- y0 518)]) 
        occ-op5 (sfactory/occluder drw [(+ x0  479) (- y0 543) ][(+ x0 512)  (- y0 568)]) 
        occ-op6 (sfactory/occluder drw [(+ x0  529) (- y0 543) ][(+ x0 562)  (- y0 568)]) 
        occ-op7 (sfactory/occluder drw [(+ x0  589) (- y0 493) ][(+ x0 622)  (- y0 518)]) 
        occ-op8 (sfactory/occluder drw [(+ x0  589) (- y0 543) ][(+ x0 622)  (- y0 568)]) 

        

        selection-action (fn [b _]
                           (let [card (.get-property b :card-number)]
                             (.show-card-number! ied card)))
        algo-graph (osp/op-selection-panel drw [x-algo y-algo] selection-action false)
        init-action (fn [b _]
                      (let [temp (.get-property b :rim-color)
                            op (.get-property b :id)]
                        (.put-property! b :rim-color (lnf/button-selected-border))
                        (.render drw)
                        (init-op ied op)
                        (.put-property! b :rim-color [0 0 0 0])
                        (.render drw)))
        dice-action (fn [b _]
                      (let [op (.get-property b :id)]
                        (dice-op ied op)))
                        

        copy-action (fn [b _](copy-op ied op))
                      

        paste-action (fn [b _](paste-op ied op))

        sub-panels (list amp-pan freq-pan kscale-pan fb-pan)
        disable-fn (fn []
                     (doseq [sp sub-panels]((:disable-fn sp)))
                     ((:disable-fn env-pan))
                     (.render drw))
        enable-fn (fn []
                    (doseq [sp sub-panels]((:enable-fn sp)))
                    ((:enable-fn env-pan))
                    (.render drw))
        pan-main (ss/scrollable 
                  (ss/border-panel :center (.canvas drw)
                                   :east (:pan-main env-pan)
                                   :background (uc/color (lnf/background)))
                  :hscroll :as-needed
                  :vscroll :as-needed)
        widget-map {:drawing drw
                    :canvas (.canvas drw)
                    :pan-main pan-main}]
    (sfactory/init-button drw  [x-init  y-init] op init-action)
    (sfactory/dice-button drw  [x-dice  y-dice] op dice-action)    
    (sfactory/copy-button drw  [x-copy  y-copy] op copy-action)
    (sfactory/paste-button drw [x-paste y-paste] op paste-action)
    (op-label drw [x-label y-label] op)
    ((:highlight-fn overview-pan) op true)
    (osp/highlight! op algo-graph)
    (reset! this* (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
                    (widgets [this] widget-map)
                    (widget [this key] (get widget-map key))
                    (parent [this] ied)
                    (parent! [this _] ied) ;; ignore
                    (status! [this msg](.status! ied msg))
                    (warning! [this msg](.warning! ied msg))
                    (set-param! [this param value](.set-param! ied param value))
                    (init! [this] 
                      ;; ISSUE op init not implemented
                      )
                    (sync-ui! [this]
                      (let [dmap (.current-data (.bank (.parent-performance ied)))
                            muted? (zero? (enable-param dmap))
                            mute-1 (zero? (:op1-enable dmap))
                            mute-2 (zero? (:op2-enable dmap))
                            mute-3 (zero? (:op3-enable dmap))
                            mute-4 (zero? (:op4-enable dmap))
                            mute-5 (zero? (:op5-enable dmap))
                            mute-6 (zero? (:op6-enable dmap))
                            mute-7 (zero? (:op7-enable dmap))
                            mute-8 (zero? (:op8-enable dmap))
                            mute-op (fn [occ flag]
                                      (.use-attributes! occ (if flag :disabled :enabled)))]
                        (mute-op occ-op1 mute-1)
                        (mute-op occ-op2 mute-2)
                        (mute-op occ-op3 mute-3)
                        (mute-op occ-op4 mute-4)
                        (mute-op occ-op5 mute-5)
                        (mute-op occ-op6 mute-6)
                        (mute-op occ-op7 mute-7)          
                        (mute-op occ-op8 mute-8)
                        (doseq [occ [occ-1 occ-2 occ-3 occ-4]]
                          (.use-attributes! occ (if muted? :disabled :enabled)))
                        (doseq [sp (cons overview-pan sub-panels)]
                          ((:sync-fn sp)))
                        ((:sync-fn env-pan))
                        (if muted? (disable-fn)(enable-fn)))
                      (.render drw))))
    @this*))
            
