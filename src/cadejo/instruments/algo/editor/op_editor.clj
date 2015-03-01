(ns cadejo.instruments.algo.editor.op-editor
  (:require [cadejo.instruments.algo.editor.factory :as factory])
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
        drw (drw/native-drawing 790 670)
        tools (.tool-root drw)
        [x0 y0] [0 670]
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
        y-over y1
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
        overview-pan (ovr/overview-panel drw [x-over y-over] ied)
        kscale-pan (ksp/op-keyscale op drw [x-keyscale y-keyscale] ied)
        amp-pan (oap/op-amp op drw [x-amp y-amp] ied)
        freq-pan (ofp/op-freq op drw [x-freq y-freq] ied)
        fb-pan (fbp/op-feedback-panel op drw [x-feedback y-feedback] ied)
        env-pan (ep/envelope-panel op ied)
        selection-action (fn [b _]
                           (let [card (.get-property b :card-number)]
                             (.show-card-number! ied card)))
        algo-graph (osp/op-selection-panel drw [x-algo y-algo] selection-action false)
        init-action (fn [b _]
                      (let [temp (.get-property b :rim-color)
                            op (.get-property b :id)]
                        (.put-property! b :rim-color (lnf/selected-button-border))
                        (.render drw)
                        (init-op ied op)
                        (.put-property! b :rim-color [0 0 0 0])
                        (.render drw)))
        dice-action (fn [b _]
                      (let [op (.get-property b :id)]
                        (dice-op ied op)))
                        

        copy-action (fn [b _](copy-op ied op))
                      

        paste-action (fn [b _](paste-op ied op))

        sub-panels (list kscale-pan amp-pan freq-pan fb-pan)
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
                                   :background (uc/color (lnf/background-color)))
                  :hscroll :as-needed
                  :vscroll :as-needed)
        widget-map {:drawing drw
                    :canvas (.canvas drw)
                    :pan-main pan-main}]
    (factory/init-button tools  [x-init  y-init] op init-action)
    (factory/dice-button tools  [x-dice  y-dice] op dice-action)    
    (factory/copy-button tools  [x-copy  y-copy] op copy-action)
    (factory/paste-button tools [x-paste y-paste] op paste-action)
    ;(factory/help-button tools  [x-help  y-help] op help-action)
    (factory/op-label (.root drw) [x-label y-label] op)
    ((:highlight-fn overview-pan) op true)
    (osp/highlight! op algo-graph)
    (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
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
              muted? (zero? (enable-param dmap))]
          (doseq [sp (cons overview-pan sub-panels)]
            ((:sync-fn sp)))
          ((:sync-fn env-pan))
          (if muted? (disable-fn)(enable-fn)))
        (.render drw)))))
