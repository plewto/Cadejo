(println "--> xolotl.program")
(ns xolotl.program
  (:require [xolotl.util :as util]))

(def msg00 "XolotlProgram expected seq selector :A or :B, encountered %s")

(defn seq-program [& {:keys [enable ;input-channel output-channel
                             key-reset key-track key-gate transpose
                             rhythm-pattern hold-pattern
                             controller-1-number controller-1-pattern
                             controller-2-number controller-2-pattern
                             velocity-mode velocity-pattern
                             pitch-mode pitch-pattern
                             sr-inject sr-taps sr-seed
                             strum-mode strum-delay]
                      :or {enable true
                           ;; input-channel 0   ;; NOTE To avoid feedback, input and output channels
                           ;; output-channel 0  ;; should not be the same if key-track is enabled.
                           key-reset false
                           key-track true
                           key-gate false
                           transpose 0
                           rhythm-pattern [24]
                           hold-pattern [1.0]
                           controller-1-number -1
                           controller-1-pattern [0 16 32 64 127]
                           controller-2-number -1
                           controller-2-pattern [0 16 32 64 127 64 32 16]
                           velocity-mode :seq
                           velocity-pattern [100]
                           pitch-mode :seq
                           pitch-pattern [0 2 4 5 7 9 11 [0 4 7 12]]
                           sr-inject 0
                           sr-taps 2r10001000
                           sr-seed 2r00000001
                           strum-mode :forward
                           strum-delay 0}}]
  {:enable enable
;   :input-channel input-channel
;   :output-channel output-channel
   :key-reset key-reset
   :key-track key-track
   :key-gate key-gate
   :transpose transpose
   :rhythm-pattern rhythm-pattern
   :hold-pattern hold-pattern
   :controller-1-number controller-1-number
   :controller-1-pattern controller-1-pattern
   :controller-2-number controller-2-number
   :controller-2-pattern controller-2-pattern
   :velocity-mode velocity-mode
   :velocity-pattern velocity-pattern
   :pitch-mode pitch-mode
   :pitch-pattern pitch-pattern
   :sr-inject sr-inject
   :sr-taps sr-taps
   :sr-seed sr-seed
   :strum-mode strum-mode
   :strum-delay strum-delay})
                          

(defprotocol XolotlProgram

  (program-name [this])

  (program-name! [this txt])

  (tempo [this])

  (tempo! [this bpm])

  ;; (clock-source [this])

  ;; (clock-source! [this src])

  ;; Returns parameter map for single sequence
  ;; seq arg selectes which sequence map is returned
  ;; seq = :a or :B
  (seq-params [this seq])
  
  (enabled? [this seq])

  (enable! [this seq flag])

  ;; (input-channel [this seq])      ;; NOTE: Internally MIDI channels are
  ;;                                 ;; zero-indexed [0..15]. Externally
  ;; (input-channel! [this seq c1])  ;; they are one-indexed [1..16].

  ;; (output-channel [this seq])

  ;; (output-channel! [this seq c1])

  (key-reset [this seq])

  (key-reset! [this seq flag])

  (key-track [this seq])

  (key-track! [this seq flag])

  (key-gate [this seq])

  (key-gate! [this seq flag])

  (transpose [this seq])

  (transpose! [this seq n])

  (rhythm-pattern [this seq])

  (rhythm-pattern! [this seq vec])

  (hold-pattern [this seq])

  (hold-pattern! [this seq vec])

  (controller-1-number [this seq])

  (controller-1-number! [this seq n])

  (controller-1-pattern [this seq])

  (controller-1-pattern! [this seq vec])

  (controller-2-number [this seq])

  (controller-2-number! [this seq n])

  (controller-2-pattern [this seq])

  (controller-2-pattern! [this seq vec])

  (velocity-mode [this seq])

  (velocity-mode! [this seq mode])

  (velocity-pattern [this seq])

  (velocity-pattern! [this seq vec])

  (pitch-mode [this seq])

  (pitch-mode! [this seq mode])

  (pitch-pattern [this seq])

  (pitch-pattern! [this seq vec])

  (sr-inject [this seq])

  (sr-inject! [this seq flag])

  (sr-taps [this seq])

  (sr-taps! [this seq n])

  (sr-seed [this seq])

  (sr-seed! [this seq n])

  (strum-mode [this seq])

  (strum-mode! [this seq mode])

  (strum-delay [this seq])

  (strum-delay! [this seq n])

  (dump [this]))


(defn xolotl-program []
  (let [name* (atom "")
        tempo* (atom 120)
        ;clock-source* (atom :internal)
        seqa* (atom (seq-program))
        seqb* (atom (seq-program))
        get-seq* (fn [key]
                   (let [kn (keyword (.toUpperCase (name key)))]
                     (if (= kn :A)
                       seqa*
                       (if (= kn :B)
                         seqb*
                         (throw (IllegalArgumentException. (format msg00 key)))))))
        xpobj (reify XolotlProgram

                (program-name [this] 
                  @name*)

                (program-name! [this s] 
                  (reset! name* (str s)))

                (tempo [this] 
                  @tempo*)

                (tempo! [this bpm] 
                  (reset! tempo* (float bpm)))

                ;; (clock-source [this] 
                ;;   @clock-source*)

                ;; (clock-source! [this src]
                ;;   (reset! clock-source* src))

                (seq-params [this seq]
                  @(get-seq* seq))
                
                (enabled? [this seq]
                  (:enable @(get-seq* seq)))

                (enable! [this seq flag]
                  (swap! (get-seq* seq) 
                         (fn [q](assoc q :enable (util/->bool flag)))))

                ;; (input-channel [this seq]
                ;;   (inc (:input-channel @(get-seq* seq))))

                ;; (input-channel! [this seq c1]
                ;;   (swap! (get-seq* seq)
                ;;          (fn [q](assoc q :input-channel (dec c1))))
                ;;   c1)

                ;; (output-channel [this seq]
                ;;   (inc (:output-channel @(get-seq* seq))))

                ;; (output-channel! [this seq c1]
                ;;   (swap! (get-seq* seq)
                ;;          (fn [q](assoc q :output-channel (dec c1))))
                ;;   c1)

                (key-reset [this seq]
                  (:key-reset @(get-seq* seq)))

                (key-reset! [this seq flag]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :key-reset (util/->bool flag)))))

                (key-track [this seq]
                  (:key-track @(get-seq* seq)))

                (key-track! [this seq flag]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :key-track (util/->bool flag)))))

                (key-gate [this seq]
                  (:key-gate @(get-seq* seq)))

                (key-gate! [this seq flag]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :key-gate (util/->bool flag)))))

                (transpose [this seq]
                  (:transpose @(get-seq* seq)))

                (transpose! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :transpose (int n)))))

                (rhythm-pattern [this seq]
                  (:rhythm-pattern @(get-seq* seq)))

                (rhythm-pattern! [this seq vec]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :rhythm-pattern vec))))

                (hold-pattern [this seq]
                  (:hold-pattern @(get-seq* seq)))

                (hold-pattern! [this seq pat]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :hold-pattern pat))))

                (controller-1-number [this seq]
                  (:controller-1-number @(get-seq* seq)))

                (controller-1-number! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :controller-1-number (int n)))))

                (controller-1-pattern [this seq]
                  (:controller-1-pattern @(get-seq* seq)))

                (controller-1-pattern! [this seq vec]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :controller-1-pattern vec))))

                (controller-2-number [this seq]
                  (:controller-2-number @(get-seq* seq)))

                (controller-2-number! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :controller-2-number (int n)))))

                (controller-2-pattern [this seq]
                  (:controller-2-pattern @(get-seq* seq)))

                (controller-2-pattern! [this seq vec]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :controller-2-pattern vec))))
                
                (velocity-mode [this seq]
                  (:velocity-mode @(get-seq* seq)))

                (velocity-mode! [this seq mode]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :velocity-mode mode))))

                (velocity-pattern [this seq]
                  (:velocity-pattern @(get-seq* seq)))

                (velocity-pattern! [this seq vec]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :velocity-pattern vec))))

                (pitch-mode [this seq]
                  (:pitch-mode @(get-seq* seq)))

                (pitch-mode! [this seq mode]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :pitch-mode mode))))

                (pitch-pattern [this seq]
                  (:pitch-pattern @(get-seq* seq)))

                (pitch-pattern! [this seq vec]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :pitch-pattern vec))))

                (sr-inject [this seq]
                  (:sr-inject @(get-seq* seq)))

                (sr-inject! [this seq flag]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :sr-inject (util/->bool flag)))))

                (sr-taps [this seq]
                  (:sr-taps @(get-seq* seq)))

                (sr-taps! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :sr-taps (int n)))))

                (sr-seed [this seq]
                  (:sr-seed @(get-seq* seq)))

                (sr-seed! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :sr-seed (int n)))))

                (strum-mode [this seq]
                  (:strum-mode @(get-seq* seq)))

                (strum-mode! [this seq mode]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :strum-mode mode))))

                (strum-delay [this seq]
                  (:strum-delay @(get-seq* seq)))

                (strum-delay! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :strum-delay n))))

                (dump [this]
                  (let [sb (StringBuilder. 400)
                        pad "    "
                        pad2 (str pad pad)]
                    (.append sb "XolotlProgram\n")
                    (.append sb (format "%sname  -> '%s'\n" pad (.program-name this)))
                   ; (.append sb (format "%sclock -> %s\n" pad (.clock-source this)))
                    (.append sb (format "%stempo -> %s\n" pad (.tempo this)))
                    (doseq [id [:A :B]]
                      (.append sb (format "%sseq %s\n" pad id))
                      (let [pmap @(get-seq* id)
                            keys (sort (map first pmap))]
                        (doseq [k keys]
                          (.append sb (format "%s%-21s -> %s\n" pad2 k (get pmap k))))))
                    (println (.toString sb)))) )]
    xpobj))
 


; ---------------------------------------------------------------------- 
;                               Test Programs

;; seq a only
;;
(def alpha (let [xp (xolotl-program)]
                   (.program-name! xp "Alpha")
                   (.tempo! xp 60)
                   (.enable! xp :A true)
                   (.key-reset! xp :A false)
                   (.key-track! xp :A false)
                   (.key-gate! xp :A false)
                   (.transpose! xp :A 0)
                   (.rhythm-pattern! xp :A [96 48 24 12 6 3])
                   (.hold-pattern! xp :A [0.9])
                   (.controller-1-number! xp :A -1)
                   (.controller-2-number! xp :A -1)
                   (.velocity-mode! xp :A :seq)
                   (.velocity-pattern! xp :A [127])
                   (.pitch-mode! xp :A :seq)
                   (.pitch-pattern! xp :A [0 2 4 5 7 9 11])
                   (.sr-inject! xp :A 0)
                   (.sr-taps! xp :A 2r00010001)
                   (.sr-seed! xp :A 2r00000001)
                   (.strum-mode! xp :A :forward)
                   (.strum-delay! xp :A 0)

                   (.enable! xp :B false)
                   (.key-reset! xp :B false)
                   (.key-track! xp :B false)
                   (.key-gate! xp :B true)
                   (.transpose! xp :B 0)
                   (.rhythm-pattern! xp :B [98 47 25 12 7 2])
                   (.hold-pattern! xp :B [1.0])
                   (.controller-1-number! xp :B -1)
                   (.controller-2-number! xp :B -1)
                   (.velocity-mode! xp :B :seq)
                   (.velocity-pattern! xp :B [127])
                   (.pitch-mode! xp :B :seq)
                   (.pitch-pattern! xp :B [-1000])
                   (.sr-inject! xp :B 0)
                   (.sr-taps! xp :B 2r00010001)
                   (.sr-seed! xp :B 2r00000001)
                   (.strum-mode! xp :B :reverse)
                   (.strum-delay! xp :B 10)
                   xp))
                   
(def beta (let [xp (xolotl-program)]
                     (.program-name! xp "Beta")
                     (.tempo! xp 60)
                     (.enable! xp :A true)
                     (.key-reset! xp :A false)
                     (.key-track! xp :A true)
                     (.key-gate! xp :A false)
                     (.transpose! xp :A -24)
                     (.rhythm-pattern! xp :A [64 32 16 8 4 2  100 101 102 103 104 105 106 107 108 109 110])
                     (.hold-pattern! xp :A [1.0])
                     (.controller-1-number! xp :A -1)
                     (.controller-2-number! xp :A -1)
                     (.velocity-mode! xp :A :seq)
                     (.velocity-pattern! xp :A [127])
                     (.pitch-mode! xp :A :seq)
                     (.pitch-pattern! xp :A [0 2 4 5 7 9 11])
                     (.sr-inject! xp :A 0)
                     (.sr-taps! xp :A 2r00010001)
                     (.sr-seed! xp :A 2r00000001)
                     (.strum-mode! xp :A :alternate)
                     (.strum-delay! xp :A 100)

                     (.enable! xp :B true)
                     (.key-reset! xp :B true)
                     (.key-track! xp :B true)
                     (.key-gate! xp :B true)
                     (.transpose! xp :B 24)
                     (.rhythm-pattern! xp :B [12])
                     (.hold-pattern! xp :B [1.0])
                     (.controller-1-number! xp :B -1)
                     (.controller-2-number! xp :B -1)
                     (.velocity-mode! xp :B :seq)
                     (.velocity-pattern! xp :B [127])
                     (.pitch-mode! xp :B :seq)
                     (.pitch-pattern! xp :B [12 11 9 7 5 4 2 0 2 3 5 7 8 10])
                     (.sr-inject! xp :B 0)
                     (.sr-taps! xp :B 2r00010001)
                     (.sr-seed! xp :B 2r00000001)
                     (.strum-mode! xp :B :random)
                     (.strum-delay! xp :B 200)
                     xp)) 


(def gamma (let [xp (xolotl-program)]
                     (.program-name! xp "Gamma")
                     (.tempo! xp 60)
                     
                     (.enable! xp :A true)
                     (.key-reset! xp :A false)
                     (.key-track! xp :A true)
                     (.key-gate! xp :A false)
                     (.transpose! xp :A 0)
                     (.rhythm-pattern! xp :A [:ape 96 48 24 12 6 3])
                     (.hold-pattern! xp :A [1.0])
                     (.controller-1-number! xp :A -1)
                     (.controller-2-number! xp :A -1)
                     (.velocity-mode! xp :A :seq)
                     (.velocity-pattern! xp :A [127])
                     (.pitch-mode! xp :A :seq)
                     (.pitch-pattern! xp :A [0 2 4 5 7 9 11 [4 7 12] 11 9 7 5 4 2])
                     (.sr-inject! xp :A 0)
                     (.sr-taps! xp :A 2r00010001)
                     (.sr-seed! xp :A 2r00000001)
                     (.strum-mode! xp :A :forward) 
                     (.strum-delay! xp :A 0)

                     (.enable! xp :B true)
                     (.key-reset! xp :B false)
                     (.key-track! xp :B true)
                     (.key-gate! xp :B false)
                     (.transpose! xp :B 0)
                     (.rhythm-pattern! xp :B [:bat :cat :dog])
                     (.hold-pattern! xp :B [1.0])
                     (.controller-1-number! xp :B -1)
                     (.controller-2-number! xp :B -1)
                     (.velocity-mode! xp :B :seq)
                     (.velocity-pattern! xp :B [127])
                     (.pitch-mode! xp :B :seq)
                     (.pitch-pattern! xp :B [[-12 -8 -5] -1000 [12 16 19 22 24] -1000])
                     (.sr-inject! xp :B 0)
                     (.sr-taps! xp :B 2r00010001)
                     (.sr-seed! xp :B 2r00000001)
                     (.strum-mode! xp :B :forward)
                     (.strum-delay! xp :B 0)
                     xp))
