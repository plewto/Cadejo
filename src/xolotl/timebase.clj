(println "    --> xolotl.timebase")
(ns xolotl.timebase
  (:require [xolotl.util :as util])
  (:import java.util.Timer java.util.TimerTask))

(defprotocol TimeBase

  (action! [this afn])

  (tempo! [this bpm])

  (midi-reset [this])

  (fire [this]) ;; execute timer task 
  
  (stop [this])

  (start [this])

  (kill [this]))

(defn- timebase [action]
  (let [action* (atom action)
        period* (atom 1000)
        timer* (atom nil)
        running* (atom false)
        tbase (reify TimeBase
                
                (action! [this afn]
                  (reset! action* afn))
                
                (tempo! [this bpm]
                  (reset! period* (long (/ 2500 bpm)))
                  (.midi-reset this)
                  bpm)
                
                (midi-reset [this]
                  (if @running*
                    (let [task (proxy [TimerTask][]
                                 (cancel [] true)
                                 (run [] (@action*))
                                 (scheduledExecutionTime [] (long 0)))]
                      (if @timer* (.cancel @timer*))
                      (reset! timer* (Timer. true))
                      (.scheduleAtFixedRate @timer* task (long 0) @period*))
                    (.stop this)))
                
                (kill [this]
                  (if @timer*
                    (do
                      (reset! running* false)
                      (.stop this)
                      (.purge @timer*)
                      (reset! timer* nil))))

                (fire [this]
                  (@action*))
                
                (stop [this]
                  (reset! running* false)
                  (.cancel @timer*))
                
                (start [this]
                  (reset! running* true)
                  (.midi-reset this)))]
    (.start tbase)
    tbase))


(defonce ^:private enslaved-clocks* (atom []))
(defonce ^:private free-clocks* (atom []))

(defn- timebase-action []
  (doseq [clk @enslaved-clocks*]
    (.advance clk)))

(defonce ^:private global-timebase (timebase timebase-action))

(defn stop []
  (.stop global-timebase))

(defn start []
  (.start global-timebase))

(defn fire []
  (.fire global-timebase))

(defn set-tempo [bpm]
  (.tempo! global-timebase bpm)
  (doseq [clk (concat @enslaved-clocks* @free-clocks*)]
    (.tempo! clk bpm))
  (.tempo! global-timebase bpm))

;; Add client clock to enslaved list
;;
(defn sync-clock [clk]
  (if (not (util/member? clk @enslaved-clocks*))
    (swap! enslaved-clocks* (fn [q](conj q clk))))
  (swap! free-clocks* (fn [q] (filter (fn [w](= w clk)) q))))

;; Remove client clock from enslaved list
;;
(defn free-clock [clk]
  (if (not (util/member? clk @free-clocks*))
    (swap! free-clocks*(fn [q](conj q clk))))
  (swap! enslaved-clocks* (fn [q] (filter (fn [w](= w clk)) q))))


(stop)
