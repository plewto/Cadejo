(println "--> xolotl.program")
(ns xolotl.program
  (:require [xolotl.util :as util]))

(def enable-program-dump* (atom true))
(def msg00 "XolotlProgram expected seq selector :A or :B, encountered %s")

(declare xolotl-program)

(defn seq-program [& {:keys [enable
                             key-reset key-track key-gate transpose
                             rhythm-pattern hold-pattern
                             controller-1-number controller-1-pattern
                             controller-2-number controller-2-pattern
                             velocity-mode velocity-pattern
                             pitch-mode pitch-pattern
                             sr-inject sr-taps sr-seed sr-mask
                             strum-mode strum-delay midi-program
                             repeat jump]
                      :or {enable true
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
                           sr-taps 2r100000000000
                           sr-seed 2r000000000001
                           sr-mask 2r111111111111
                           strum-mode :forward
                           strum-delay 0
                           midi-program -1
                           repeat 0
                           jump -1}}]
  {:enable enable
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
   :sr-mask sr-mask
   :strum-mode strum-mode
   :strum-delay strum-delay
   :midi-program midi-program
   :repeat repeat
   :jump jump})
                          

(defprotocol XolotlProgram

  (program-name [this])

  (program-name! [this txt])

  (tempo [this])

  (tempo! [this bpm])

  ;; Returns parameter map for single sequence
  ;; seq arg selectes which sequence map is returned
  ;; seq = :a or :B
  (seq-params [this seq])
  
  (enabled? [this seq])

  (enable! [this seq flag])

  (key-reset [this seq])

  (key-reset! [this seq flag])

  (key-track [this seq])

  (key-track! [this seq flag])

  (key-gate [this seq])

  (key-gate! [this seq flag])

  (transpose [this seq])

  (transpose! [this seq n])

  (repeat [this])

  (repeat! [this n])

  (jump [this])

  (jump! [this prognum])
  
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

  (sr-mask [this seq])

  (sr-mask! [this seq n])
  
  (strum-mode [this seq])

  (strum-mode! [this seq mode])

  (strum-delay [this seq])

  (strum-delay! [this seq n])

  (midi-program [this seq])

  (midi-program! [this seq pnum])
  
  (dump [this])

  (to-map [this])
  )


(defn program-to-map [prog]
  {:data-format :xolotl-program
   :name (.program-name prog)
   :tempo (.tempo prog)
   :A {:enabled (.enabled? prog :A)
       :key-reset (.key-reset prog :A)
       :key-track (.key-track prog :A)
       :key-gate (.key-gate prog :A)
       :transpose (.transpose prog :A)
       :repeat (.repeat prog)
       :jump (.jump prog)
       :rhythm-pattern (.rhythm-pattern prog :A)
       :hold-pattern (.hold-pattern prog :A)
       :controller-1-number (.controller-1-number prog :A)
       :controller-1-pattern (.controller-1-pattern prog :A)
       :controller-2-number (.controller-2-number prog :A)
       :controller-2-pattern (.controller-2-pattern prog :A)
       :velocity-mode (.velocity-mode prog :A)
       :velocity-pattern (.velocity-pattern prog :A)
       :pitch-mode (.pitch-mode prog :A)
       :pitch-pattern (.pitch-pattern prog :A)
       :sr-inject (.sr-inject prog :A)
       :sr-taps (.sr-taps prog :A)
       :sr-seed (.sr-seed prog :A)
       :sr-mask (.sr-mask prog :A)
       :strum-mode (.strum-mode prog :A)
       :strum-delay (.strum-delay prog :A)
       :midi-program (.midi-program prog :A)}
   :B {:enabled (.enabled? prog :B)
       :key-reset (.key-reset prog :B)
       :key-track (.key-track prog :B)
       :key-gate (.key-gate prog :B)
       :transpose (.transpose prog :B)
       :repeat -1
       :jump -1
       :rhythm-pattern (.rhythm-pattern prog :B)
       :hold-pattern (.hold-pattern prog :B)
       :controller-1-number (.controller-1-number prog :B)
       :controller-1-pattern (.controller-1-pattern prog :B)
       :controller-2-number (.controller-2-number prog :B)
       :controller-2-pattern (.controller-2-pattern prog :B)
       :velocity-mode (.velocity-mode prog :B)
       :velocity-pattern (.velocity-pattern prog :B)
       :pitch-mode (.pitch-mode prog :B)
       :pitch-pattern (.pitch-pattern prog :B)
       :sr-inject (.sr-inject prog :B)
       :sr-taps (.sr-taps prog :B)
       :sr-seed (.sr-seed prog :B)
       :sr-mask (.sr-mask prog :B)
       :strum-mode (.strum-mode prog :B)
       :strum-delay (.strum-delay prog :B)
       :midi-program (.midi-program prog :B)}})
       
(defn map-to-program
  ([map]
   (map-to-program map (xolotl-program)))
  ([map prog]
   (let [A (get map :A {})
         B (get map :B {})]
     (.program-name! prog (get map :name "Unknown?"))
     (.tempo! prog (get map :tempo))
     (.enable! prog :A (:enabled A true))
     (.key-reset! prog :A (:key-reset A false))
     (.key-track! prog :A (:key-track A true))
     (.key-gate! prog :A (:key-gate A false))
     (.transpose! prog :A (:transpose A 0))
     (.rhythm-pattern! prog :A (:rhythm-pattern A [24]))
     (.hold-pattern! prog :A (:hold-pattern A [1.0]))
     (.controller-1-number! prog :A (:controller-1-number A -1))
     (.controller-1-pattern! prog :A (:controller-1-pattern A [0]))
     (.controller-2-number! prog :A (:controller-2-number A -1))
     (.controller-2-pattern! prog :A (:controller-2-pattern A [0]))
     (.velocity-mode! prog :A (:velocity-mode A :seq))
     (.velocity-pattern! prog :A (:velocity-pattern A [96]))
     (.pitch-mode! prog :A (:pitch-mode A :seq))
     (.pitch-pattern! prog :A (:pitch-pattern A [-1000]))
     (.sr-inject! prog :A (:sr-inject A false))
     (.sr-taps! prog :A (:sr-taps A 2r1000100))
     (.sr-seed! prog :A (:sr-seed A 1))
     (.sr-mask! prog :A (:sr-mask A 2r11111111))
     (.strum-mode! prog :A (:strum-mode A :forward))
     (.strum-delay! prog :A (:strum-delay A 0))
     (.midi-program! prog :A (:midi-program A -1))
     (.repeat! prog :A (:repeat A 0))
     (.jump! prog :A (:jump A -1))
     (.enable! prog :B (:enabled B true))
     (.key-reset! prog :B (:key-reset B false))
     (.key-track! prog :B (:key-track B true))
     (.key-gate! prog :B (:key-gate B false))
     (.transpose! prog :B (:transpose B 0))
     (.rhythm-pattern! prog :B (:rhythm-pattern B [24]))
     (.hold-pattern! prog :B (:hold-pattern B [1.0]))
     (.controller-1-number! prog :B (:controller-1-number B -1))
     (.controller-1-pattern! prog :B (:controller-1-pattern B [0]))
     (.controller-2-number! prog :B (:controller-2-number B -1))
     (.controller-2-pattern! prog :B (:controller-2-pattern B [0]))
     (.velocity-mode! prog :B (:velocity-mode B :seq))
     (.velocity-pattern! prog :B (:velocity-pattern B [96]))
     (.pitch-mode! prog :B (:pitch-mode B :seq))
     (.pitch-pattern! prog :B (:pitch-pattern B [-1000]))
     (.sr-inject! prog :B (:sr-inject B false))
     (.sr-taps! prog :B (:sr-taps B 2r1000100))
     (.sr-seed! prog :B (:sr-seed B 1))
     (.sr-mask! prog :B (:sr-mask B 2r11111111))
     (.strum-mode! prog :B (:strum-mode B :forward))
     (.strum-delay! prog :B (:strum-delay B 0))
     (.midi-program! prog :B (:midi-program B -1))
     ;; (.repeat! prog :B 0)
     ;; (.jump! prog :B -1)
     prog)))

    
(defn xolotl-program []
  (let [name* (atom "")
        tempo* (atom 120)
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

                (seq-params [this seq]
                  @(get-seq* seq))
                
                (enabled? [this seq]
                  (:enable @(get-seq* seq)))

                (enable! [this seq flag]
                  (swap! (get-seq* seq) 
                         (fn [q](assoc q :enable (util/->bool flag)))))

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

                (repeat [this]
                  (:repeat @(get-seq* :A)))


                (repeat! [this n]
                  (swap! (get-seq* :A)
                         (fn [q](assoc q :repeat (int n)))))

                (jump [this]
                  (:jump @(get-seq* :A)))

                (jump! [this n]
                  (swap! (get-seq* :A)
                         (fn [q](assoc q :jump (int n)))))
                
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

                (sr-mask [this seq]
                  (:sr-mask @(get-seq* seq)))

                (sr-mask! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :sr-mask (int n)))))
                
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

                (midi-program [this seq]
                  (:midi-program @(get-seq* seq)))

                (midi-program! [this seq n]
                  (swap! (get-seq* seq)
                         (fn [q](assoc q :midi-program n))))
                
                (to-map [this]
                  (program-to-map this))
                
                (dump [this]
                  (if @enable-program-dump*
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
                      (println (.toString sb)))
                    )) )]
    xpobj)) 
 


; ---------------------------------------------------------------------- 
;                               Test Programs

;; seq a only
;;
(def alpha (let [xp (xolotl-program)]
             (.program-name! xp "Alpha")
             (.tempo! xp 120)
             (.enable! xp :A true)
             (.repeat! xp 2)
             (.jump! xp 1)
             (.key-reset! xp :A false)
             (.key-track! xp :A true)
             (.key-gate! xp :A false)
             (.transpose! xp :A 0)
             (.rhythm-pattern! xp :A [12 12 12])
             (.hold-pattern! xp :A [1.0])
             (.controller-1-number! xp :A -1)
             (.controller-1-pattern! xp :A [0])
             (.controller-2-number! xp :A -1)
             (.controller-2-pattern! xp :A [0])
             (.velocity-mode! xp :A :seq)
             (.velocity-pattern! xp :A [127])
             (.pitch-mode! xp :A :seq)
             (.pitch-pattern! xp :A [0 3 7])
             (.sr-inject! xp :A 0)
             (.sr-taps! xp :A 2r10000000)
             (.sr-seed! xp :A 2r00000001)
             (.sr-mask! xp :A 2r11111111)
             (.strum-mode! xp :A :forward)
             (.strum-delay! xp :A 0)
             (.midi-program! xp :A 0)
             
             (.enable! xp :B false)
             (.key-reset! xp :B false)
             (.key-track! xp :B true)
             (.key-gate! xp :B false)
             (.transpose! xp :B 0)
             (.rhythm-pattern! xp :B [24])
             (.hold-pattern! xp :B [1.0])
             (.controller-1-number! xp :B -1)
             (.controller-1-pattern! xp :B [0])
             (.controller-2-number! xp :B -1)
             (.controller-2-pattern! xp :B [0])
             (.velocity-mode! xp :B :seq)
             (.velocity-pattern! xp :B [127])
             (.pitch-mode! xp :B :seq)
             (.pitch-pattern! xp :B [12 15 19 24 19 15 12])
             (.sr-inject! xp :B 0)
             (.sr-taps! xp :B 2r10000000)
             (.sr-seed! xp :B 2r00000001)
             (.sr-mask! xp :B 2r11111111)
             (.strum-mode! xp :B :reverse)
             (.strum-delay! xp :B 10)
             (.midi-program! xp :B 1)
             xp))
                   
(def beta (let [xp (xolotl-program)]
            (.program-name! xp "Beta")
                     (.tempo! xp 120)
                     (.enable! xp :A true)
                     (.repeat! xp 2)
                     (.jump! xp 0)
                     (.key-reset! xp :A false)
                     (.key-track! xp :A true)
                     (.key-gate! xp :A false)
                     (.transpose! xp :A -24)
                     (.rhythm-pattern! xp :A [24 12 12])
                     (.hold-pattern! xp :A [1.0])
                     (.controller-1-number! xp :A -1)
                     (.controller-2-number! xp :A -1)
                     (.velocity-mode! xp :A :seq)
                     (.velocity-pattern! xp :A [127])
                     (.pitch-mode! xp :A :seq)
                     (.pitch-pattern! xp :A [9 6 4])
                     (.sr-inject! xp :A 0)
                     (.sr-taps! xp :A 2r11000000)
                     (.sr-seed! xp :A 2r00000011)
                     (.sr-mask! xp :A 2r11111111)
                     (.strum-mode! xp :A :alternate)
                     (.strum-delay! xp :A 100)
                     (.midi-program! xp :A 2)

                     (.enable! xp :B false)
                     (.key-reset! xp :B false)
                     (.key-track! xp :B true)
                     (.key-gate! xp :B false)
                     (.transpose! xp :B 24)
                     (.rhythm-pattern! xp :B [6])
                     (.hold-pattern! xp :B [1.0])
                     (.controller-1-number! xp :B -1)
                     (.controller-2-number! xp :B -1)
                     (.velocity-mode! xp :B :seq)
                     (.velocity-pattern! xp :B [127])
                     (.pitch-mode! xp :B :seq)
                     (.pitch-pattern! xp :B [9 12 15 18 21])
                     (.sr-inject! xp :B 0)
                     (.sr-taps! xp :B 2r10000000)
                     (.sr-seed! xp :B 2r00000001)
                     (.sr-mask! xp :B 2r11111111)
                     (.strum-mode! xp :B :random)
                     (.strum-delay! xp :B 200)
                     (.midi-program! xp :B 3)
                     xp)) 


(def gamma (let [xp (xolotl-program)]
                     (.program-name! xp "Gamma")
                     (.tempo! xp 60)
                     
                     (.enable! xp :A true)
                     (.key-reset! xp :A false)
                     (.key-track! xp :A true)
                     (.key-gate! xp :A false)
                     (.transpose! xp :A 0)
                     (.rhythm-pattern! xp :A [96 48 24 12 6 3])  ; ERROR ERROR ERROR
                     (.hold-pattern! xp :A [1.0])
                     (.controller-1-number! xp :A -1)
                     (.controller-2-number! xp :A -1)
                     (.velocity-mode! xp :A :seq)
                     (.velocity-pattern! xp :A [127])
                     (.pitch-mode! xp :A :seq)
                     (.pitch-pattern! xp :A [0 2 4 5 7 9 11 [4 7 12] 11 9 7 5 4 2])
                     (.sr-inject! xp :A 0)
                     (.sr-taps! xp :A 2r10100000)
                     (.sr-seed! xp :A 2r00000111)
                     (.sr-mask! xp :A 2r10101011)
                     (.strum-mode! xp :A :forward) 
                     (.strum-delay! xp :A 0)
                     (.midi-program! xp :A 4)

                     (.enable! xp :B true)
                     (.key-reset! xp :B false)
                     (.key-track! xp :B true)
                     (.key-gate! xp :B false)
                     (.transpose! xp :B 0)
                     (.rhythm-pattern! xp :B [24])
                     (.hold-pattern! xp :B [1.0])
                     (.controller-1-number! xp :B -1)
                     (.controller-2-number! xp :B -1)
                     (.velocity-mode! xp :B :seq)
                     (.velocity-pattern! xp :B [127])
                     (.pitch-mode! xp :B :seq)
                     (.pitch-pattern! xp :B [[-12 -8 -5] -1000 [12 16 19 22 24] -1000])
                     (.sr-inject! xp :B 0)
                     (.sr-taps! xp :B 2r10000000)
                     (.sr-seed! xp :B 2r00000001)
                     (.sr-mask! xp :B 2r11111111)
                     (.strum-mode! xp :B :forward)
                     (.strum-delay! xp :B 0)
                     (.midi-program! xp :B 5)
                     xp))
