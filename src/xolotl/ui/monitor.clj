(ns xolotl.ui.monitor
  (:require [xolotl.shift-register])
  (:require [xolotl.util :as util])
  (:require [xolotl.ui.rhythm-editor])
  (:require [seesaw.core :as ss])
  
  )

;; clock
;;    rhythm
;;    hold
;;    reset
;; pitch
;;   velocity
;;   shift-register
;;   pitch
;; controllers
;;   ctrl num & val
;;   ctrl num & val



(defn format-rhythm [r]
  (let [s (get xolotl.ui.rhythm-editor/reverse-map r r)]
    (format "%3s " s)))

(defn format-hold [h]
  (format "%5.3f " h))

(defn format-sr [v]
  (let [bits xolotl.shift-register/default-length
        sb (StringBuilder. bits)
        weight* (atom 1)]
    (dotimes [b bits]
      (if (pos? (bit-and v @weight*))
        (.append sb "X")
        (.append sb "_"))
      (swap! weight* (fn [q](* q 2))))
    (format "SR:%s " (.toString sb))))

(def pitch-class-map {0 "C", 1 "CS", 2 "D",
                      3 "DS", 4 "E", 5 "F",
                      6 "FS", 7 "G", 8 "GS",
                      9 "A", 10 "AS", 11 "B"})

;; (defn format-keylist [klst]
;;   (let [sb (StringBuilder. 12)]
;;     (.append sb "[")
;;     (doseq [k (util/->list klst)]
;;       (if (neg? k)
;;         (.append sb "REST")
;;         (let [pc (rem k 12)
;;               oct (int (/ k 12))]
;;           (.append sb (get pitch-class-map pc))
;;           (.append sb (format "%d " oct)))))
;;     (.append sb "]")
;;     (format "%12s " (.toString sb))))


(defn format-keylist [klst]
  (let [sb (StringBuilder. 12)]
    (.append sb "[")
    (doseq [k (util/->list klst)]
      (if (neg? k)
        (.append sb "REST")
        (.append sb (format "%3s " k))))
    (.append sb "]")
    (format "%12s " (.toString sb))))
        
(defn format-velocity [vel]
  (format "VEL:%3d " vel))

(defn format-controller [n ctrl val]
  (format "CC%d %3d " n val))

(defn monitor [seq-id clock pitch controllers transmitter]
  (let [enable* (atom false)
        format-sample (fn []
                        (let [s1 (format-rhythm (.current-rhythm-value clock))
                              s2 (format-hold (.current-hold-value clock))
                              s3 (format-sr (.current-shift-register-value pitch))
                              s4 (format-keylist (.current-keylist pitch))
                              s5 (format-velocity (.current-velocity-value pitch))
                              s6 (format-controller 1
                                                    (.controller-number controllers 0)
                                                    (.current-value controllers 0))
                              s7 (format-controller 2
                                                    (.controller-number controllers 1)
                                                    (.current-value controllers 1))
                              s8 (format-keylist (.current-keylist transmitter))

                              ]
                          (str s1 s2 s3 s4 s5 s6 s8)))
        sample (fn []
                 (if @enable*
                   (println (format-sample))))
        midi-reset (fn []
                     (if @enable* 
                       (println "RESET")))
        ]
    {:fn-sample sample
     :fn-reset midi-reset
     :fn-enable (fn [flag](reset! enable* flag))
     }))
        
        
                              
        
