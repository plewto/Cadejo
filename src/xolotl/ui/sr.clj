(println "    --> xolotl.ui.sr")
(ns xolotl.ui.sr
  (:require [xolotl.ui.factory :as factory])
  (:require [xolotl.ui.pitch-map :as pmap])
  (:require [xolotl.util :as util])
  (:import java.awt.event.ActionListener
           javax.swing.JRadioButton))

(def bits 12)

(defn button-bar [id]
  (let [value* (atom 0)
        buttons* (atom '())
        test-action (proxy [ActionListener][]
                      (actionPerformed [evn]
                        (let [src (.getSource evn)
                              id (.getClientProperty src :id)
                              n (.getClientProperty src :bit)]
                          (println (format "%5s %s" id n)))))]
    (dotimes [i bits]
      (let [j (util/expt 2 i)
            cb (JRadioButton.)]
        (.putClientProperty cb :bit i)
        (.putClientProperty cb :value j)
        (.putClientProperty cb :id id)
        (.addActionListener cb test-action)
        (swap! buttons* (fn [q](conj q cb)))))
    (reverse @buttons*)))

(defn set-bar-value! [bbar val]
  (let [bval* (atom 1)]
    (doseq [b bbar]
      (.setSelected b (pos? (bit-and @bval* val)))
      (swap! bval* (fn [q](* 2 q))))))

(defn get-bar-value [bbar]
  (let [acc* (atom 0)
        weight* (atom 1)]
    (doseq [b bbar]
      (if (.isSelected b)
        (swap! acc* (fn [q](+ q @weight*))))
      (swap! weight* (fn [q](* 2 q))))
    @acc*))

(defn sr-editor [parent-editor seq-id]
  (let [xobj (.node parent-editor)
        xseq (.get-xseq xobj (if (= seq-id :A) 0 1))
        bank (.program-bank xobj)
        cb-taps (button-bar :tap)
        cb-seed (button-bar :seed)
        cb-mask (button-bar :mask)
        cb-inject (factory/checkbox "Inject")
        ;jb-period (factory/button "Periiod ?")
        jb-pattern (factory/button "Pattern ?")
        labs (let [acc* (atom [])]
               (dotimes [i bits]
                 (let [lb (factory/label (format "%2d" (inc i)))]
                   (swap! acc* (fn [q](conj q lb)))))
               @acc*)
        pan-buttons (factory/grid-panel 4 bits)
        pan-west (factory/grid-panel 4 1
                                     (factory/label "Taps")
                                     (factory/label "Seed")
                                     (factory/label "Mask")
                                     (factory/label ""))
        pan-north (factory/border-panel
                   :center (factory/grid-panel 1 2 jb-pattern cb-inject))
        pan-main (factory/border-panel :north pan-north
                                       :center pan-buttons
                                       :west pan-west
                                       :border (factory/border "Shift Register"))
        add-listener (fn [blst listener]
                       (doseq [b blst]
                         (.addActionListener b listener)))
        sync-fn (fn [prog]
                  (let [taps (.sr-taps prog seq-id)
                        seed (.sr-seed prog seq-id)
                        mask (.sr-mask prog seq-id)
                        inject (.sr-inject prog seq-id)]
                    (set-bar-value! cb-taps taps)
                    (set-bar-value! cb-seed seed)
                    (set-bar-value! cb-mask mask)
                    (.setSelected cb-inject (util/->bool inject))))]
    ;; (.addActionListener jb-period (proxy [ActionListener][]
    ;;                                 (actionPerformed [_]
    ;;                                   (let [sr (.get-shift-register xseq)
    ;;                                         inj (if (.isSelected cb-inject) 0 1)
    ;;                                         p (.period sr inj)]
    ;;                                     (.status! parent-editor
    ;;                                               (format "Shift Register period is %s" p))))))
    (.addActionListener jb-pattern (proxy [ActionListener][]
                                     (actionPerformed [_]
                                       (let [sr (.get-shift-register xseq)
                                             pcycle (.get-pitch-cycle xseq)
                                             pperiod (.period pcycle)
                                             inj (if (.isSelected cb-inject) 0 1)
                                             p (.period sr inj)
                                             sb1 (StringBuilder. (* p 16))
                                             sb2 (StringBuilder. (* p 4))]
                                         (.midi-reset sr)
                                         (dotimes [i p]
                                           (let [v (.value sr)
                                                 keynum (.value pcycle v)
                                                 pitch (pmap/int->pitch keynum)]
                                             (.append sb1 (format "[%12s] %%[%3d] -> %3s\n"
                                                                  (util/format-binary v 12)
                                                                  (rem v pperiod) pitch))
                                             (.append sb2 (format "%3s " pitch))
                                             (.shift sr inj)))
                                         (.append sb2 "\n")
                                         (.midi-reset sr)
                                         (.status! parent-editor
                                                   (format "Shift Register Period is %s" p))
                                         (println)
                                         (println (.toString sb1))
                                         (println (format "Period is %s" p))
                                         (println (.toString sb2))))))

    (add-listener (cons cb-inject cb-taps)
                  (proxy [ActionListener][]
                    (actionPerformed [evn]
                      (let [prog (.current-program bank)
                            val (get-bar-value cb-taps)
                            inject (.isSelected cb-inject)]
                        (.sr-taps! prog seq-id val)
                        (.sr-inject! prog seq-id inject)
                        (.taps! xseq val inject)))))
    (add-listener cb-seed (proxy [ActionListener][]
                            (actionPerformed [evn]
                              (let [prog (.current-program bank)
                                    val (get-bar-value cb-seed)]
                                (.sr-seed! prog seq-id val)
                                (.seed! xseq val)))))
    (add-listener cb-mask (proxy [ActionListener][]
                            (actionPerformed [evn]
                              (let [prog (.current-program bank)
                                    val (get-bar-value cb-mask)]
                                (.sr-mask! prog seq-id val)
                                (.sr-mask! xseq val)))))
    (doseq [bar [cb-taps cb-seed cb-mask labs]]
      (doseq [cb bar]
        (.add pan-buttons cb)))
    {:pan-main pan-main
     :sync-fn sync-fn}))
