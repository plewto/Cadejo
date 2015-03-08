(ns cadejo.instruments.algo.editor.op-observer 
  (:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.tools.multistate-button :as msb]))


(defn- op-observer [n drw p0 ied op-ed*]
  (let [param-enable (keyword (format "op%d-enable" n))
        param-freq (keyword (format "op%d-detune" n))
        param-bias (keyword (format "op%d-bias" n))
        param-amp (keyword (format "op%d-amp" n))
        root (.root drw)
        tools (.tool-root drw)
        id :observer
        [x0 y0] p0
        width 80
        height 110
        x-opnum (+ x0 20)
        x-freq x-opnum
        x-bias x-opnum
        x-amp x-opnum
        x-mute (- x-opnum 10)
        y-mute (- y0 20)
        y-amp (- y-mute 30)
        y-bias (- y-amp 20)
        y-freq (- y-bias 20)
        y-opnum (- y-freq 20)
        txt-op (factory/slider-label root [x-opnum y-opnum] id (format "OP %d" n))
        txt-freq (factory/slider-label root [x-freq y-freq] id (format "F %7.4f" 1.0))
        txt-bias (factory/slider-label root [x-bias y-bias] id (format "B %+7.4f" 1.0))
        txt-amp (factory/slider-label root [x-amp y-amp] id (format "A  %5.3f" 1.0))
        mute-action (fn [b _]
                      (let [selected? (msb/checkbox-selected? b)]
                        (.set-param! ied param-enable (if selected? 0 1))
                        (.sync-ui! ied)
                        (.sync-ui! @op-ed*)
                        (.status! ied (format "%s op %s" 
                                              (if selected? "Mute" "Unmute") n))))
        cb-style (lnf/checkbox)
        cb-mute (msb/checkbox tools [x-mute y-mute] "Mute"
                              :id :mute
                              :rim-radius (:rim-radius cb-style)
                              :rim-color (:rim-color cb-style)
                              :selected-check [(:check-color cb-style)(:check-style cb-style)(:check-size cb-style)]
                              :text-color (lnf/text-color)
                              :click-action mute-action)
        border (factory/inner-border root p0 [(+ x0 width)(- y0 height)])
        text-components (list txt-op txt-freq txt-bias txt-amp 
                             (.get-property cb-mute :text-component))
        components (cons (.get-property cb-mute :rim)(cons border text-components))
        occ (factory/occluder drw [(+ x0 5)(- y0 27)][(+ x0 75)(- y0 85)])
        sync-fn (fn []
                  (let [dmap (.current-data (.bank (.parent-performance ied)))
                        muted? (zero? (param-enable dmap))
                        frq (float (param-freq dmap))
                        bias (float (param-bias dmap))
                        amp (float (param-amp dmap))]
                    (msb/select-checkbox! cb-mute muted? false)
                    (.use-attributes! occ (if muted? :disabled :enabled))
                    (.put-property! txt-freq :text (format "F %7.4f" frq))
                    (.put-property! txt-bias :text (format "B %+7.4f" bias))
                    (.put-property! txt-amp :text (format "A  %5.3f" amp))))
        highlight-fn (fn [flag]
                       (doseq [c components]
                         (.use-attributes! c (if flag :highlight :default))))]
    (doseq [tc text-components]
      (.color! tc :highlight (lnf/text-selected-color))
      (.color! tc :default (lnf/text-color)))
    (.color! border :highlight (lnf/button-selected-border))
    (.color! border :default (lnf/minor-border-color))
    (.color! (.get-property cb-mute :rim) :highlight (lnf/button-selected-border))
    (.color! (.get-property cb-mute :rim) :default (lnf/button-border-color))
    (.put-property! cb-mute :op n)
    (factory/inner-border root p0 [(+ x0 width)(- y0 height)])
    {:sync-fn sync-fn
     :highlight-fn highlight-fn}))


(defn overview-panel [drw p0 ied op-ed*]
  (let [[x0 y0] p0
        x-delta 96
        width (* 8 x-delta)
        height 130
        oo-map (let [acc* (atom {})
                     x* (atom x0)]
                 (doseq [n (range 1 9)]
                   (let [oo (op-observer n drw [@x* y0] ied op-ed*)]
                     (swap! acc* (fn [q](assoc q n oo)))
                     (swap! x* (fn [q](+ q x-delta)))))
                 @acc*)
        sync-fn (fn []
                  (doseq [op (vals oo-map)]
                    ((:sync-fn op))))
        highlight-fn (fn [op flag]
                       (let [hf (:highlight-fn (get oo-map op))]
                         (hf flag)))]
    {:sync-fn sync-fn
     :highlight-fn highlight-fn}))
    
   
                 
                     
                 
