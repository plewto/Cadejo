(ns cadejo.midi.cc.ot-dummy
)


(def bus* (atom 0))

(defn control-bus-get [_] @bus*)


(defn control-bus-set! [_ value]
  (reset! bus* value))


(defn control-bus [_] nil)
