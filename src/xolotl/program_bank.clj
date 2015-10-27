(println "--> xolotl.program-bank")
(ns xolotl.program-bank
  (:require [xolotl.program])
  (:require [xolotl.util :as util]))

(def bank-length 128)

(def msg00 "Invalid Xolotl program bank slot: %s")

(defprotocol ProgramBank

  (init! [this])
  
  (use-program [this slot])

  (current-slot [this])

  (current-program [this])

  (store-program! [this slot p])

  (program-names [this])

  (save-bank [this filename])

  (load-bank! [this filename])

  (dump [this]) )


(defn program-bank []
  (let [programs* (atom {})
        current-slot* (atom nil)
        current-program* (atom nil)
        bank (reify ProgramBank

               (init! [this]
                 (dotimes [slot bank-length]
                   (let [xp (xolotl.program/xolotl-program)]
                     (.program-name! xp "Init Program")
                     (swap! programs* (fn [q](assoc q slot xp)))))
                 (reset! current-slot* (int 0))
                 (reset! current-program* (get @programs* 0)))

               (use-program [this slot]
                 (let [prog (get @programs* slot)]
                   (if prog
                     (do
                       (reset! current-slot* slot)
                       (reset! current-program* prog)
                       prog)
                     (do
                       (util/warning (format msg00 slot))
                       nil))))

               (current-program [this]
                 @current-program*)

               (current-slot [this]
                 (int @current-slot*))

               (store-program! [this slot p]
                 (if (and (util/int? slot)(>= slot 0)(< slot bank-length))
                   (do
                     (reset! current-program* p)
                     (reset! current-slot* slot)
                     (swap! programs* (fn [q](assoc q slot p)))
                     true)
                   (do
                     (util/warning (format msg00 slot))
                     false)))

               (program-names [this]
                 (let [acc* (atom [])]
                   (dotimes [slot bank-length]
                     (let [p (get @programs* slot)]
                       (swap! acc* (fn [q](conj q (format "%-12s" (.program-name p)))))))
                   @acc*))

               (save-bank [this filename]
                 (util/warning "Xolotl ProgramBank.save-bank not implemented")
                 false)

               (load-bank! [this filename]
                 (util/warning "Xolotl ProgramBank.load-bank! not implemented")
                 false)

               (dump [this]
                 (println "Xolotl ProgramBank")
                 (doseq [p (.program-names this)]
                   (println (format "    %s" p)))) )]

    (.init! bank)
    (.store-program! bank 0 xolotl.program/alpha) ; TEST
    (.store-program! bank 1 xolotl.program/beta)  ; TEST
    (.store-program! bank 2 xolotl.program/gamma) ; TEST
    bank))
