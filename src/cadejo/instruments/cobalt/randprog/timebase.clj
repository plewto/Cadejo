(ns cadejo.instruments.cobalt.randprog.timebase
  (:require [cadejo.instruments.cobalt.constants :as con])
  (:require [cadejo.instruments.cobalt.randprog.config :as config]))


(defn pick-ref-time []
  (+ 0.1 (rand 0.9)))

;; Returns random vibrato frequency
;; with following properties
;; 4 < vf < 8  and vf = x/ref-time
;; where 0.1 <= ref-time <= 1.0
;; and x is random simple ratio 
;; 
;; if config/sync-lof-frequencies is false
;; return random value 4 <= vf <= 8
;;
(defn pick-vibrato-frequency [ref-time]
  (if @config/sync-lfo-frequencies*
    (let [ref-freq (/ 1.0 ref-time)
          vf* (atom 0)
          guard-dog* (atom 100)]
      (while (and (or (< @vf* 4.0)(> @vf* 8))(pos? @guard-dog*))
        (let [n (rand-nth [1 2 3 4 5 6 8])
              d (rand-nth [1 2 3 4 6 8])
              m (/ n (float d))]
          (swap! guard-dog* dec)
          (swap! vf* (fn [q](* ref-freq m)))))
     @vf*)
    (+ 4 (rand 4))))

;; Return lfo1 frequency
;; If config/sync-lfo-frequencies* is false return random value
;; 0.1 <= freq < 8.1
;;
;; If sync is true
;; Return x/ref-time where x is random simple ratio.
;;
(defn pick-lfo1-frequency [ref-time]
  (if @config/sync-lfo-frequencies*
    (let [ref-freq (/ 1.0 ref-time)
          n (rand-nth [1 2 3 4])
          d (rand-nth [1 2 3 4 5 6 8 12 16])]
      (/ (* n ref-freq) d))
    (+ 0.1 (rand 8.0))))

;; Return lfo2 frequency
;; If flanger-mode true tends to return low frequencies
;; otherwise as pick-lfo1-frequency
;;
(defn pick-lfo2-frequency [ref-time flanger-mode]
  (if flanger-mode
    (let [ref-freq (/ 1.0 ref-time)
          n (rand-nth [1 1 2])
          d (rand-nth [1 2 3 4 6 8 12 16 24 32 48 64])
          vf* (atom (/ (* n ref-freq) d))]
      (while (> @vf* 4)
        (swap! vf* (fn [q](* q 0.5))))
      @vf*)
    (if @config/sync-lfo-frequencies* 
      (let [ref-freq (/ 1.0 ref-time)
            n (rand-nth [1 2 3 4])
            d (rand-nth [1 2 3 4 6 8])]
        (/ (* ref-freq n) d))
      (+ 0.01 (rand 8)))))


(defn pick-lfo3-frequency [ref-time flanger-mode]
  (pick-lfo2-frequency ref-time flanger-mode))


;; Pick delay 1 time
;; If flanger-mode is true return very small value.
;; Otherwise return 2 * ref-time.
;;
(defn pick-delay1-time [ref-time flanger-mode]
  (if flanger-mode 
    0.001
    (* con/max-delay-time ref-time)))


;; Pick delay 2 time
;; If flanger-mode is true return very small value.
;; Otherwise return n * d1-time where n is random simple ratio.
;; 
(defn pick-delay2-time [d1-time flanger-mode]
  (if flanger-mode
    (+ 0.0001 (rand 0.001))
    (let [dt* (atom d1-time)]
      (while (or (= @dt* d1-time)(> @dt* con/max-delay-time))
        (let [n (rand-nth [1 2 3 4])
              d (rand-nth [1 2 3 4 6 8])]
          (swap! dt* (fn [q](/ (* d1-time n) d)))))
      @dt*)))

          

  
