(ns xolotl.counter)


(defprotocol Counter

  (period! [this p]
    "(.period! Counter p)
     Sets counter length.
     ARGS: 
       p - int, the counter length.
     RETURNS: int, the counter period")

  (action! [this afn]
    "(.action! Counter afn)
     Sets function to execute when counter reaches 0.
     ARGS:
       afn - function with signature (fn [] ...) 
             afn is called whenever the count reaches 0
             It takes no arguments and any results are ignored,
             and is presumably used for side-effects.
     RETURNS: afn")

  (value [this]
    "(.value Counter)
     RETURNS: int, the current count, 0 <= count < period")
  
  (midi-reset [this]
    "(.midi-reset Counter)
     Resets count.
     RETURNS: int,the counter period.")

  (step [this]
    "(.step Counter)
     Decrements count. If count reaches 0 call action function.
     RETURNS: int, the count after decrement. 0 <= counter < period.")

  (dump [this]))


(defn counter
  "(counter)
   (counter period)
   
   Creates new Counter

   ARGS:
     period - optional int, the counter period, default 4.
   RETURNS: A Counter."
  ([]
   (counter 4))
  ([period]
   (let [period* (atom period)
         count* (atom period)
         action* (atom (fn []))]
     (reify Counter

       (period! [this p]
         (reset! period* p)
         (.midi-reset this))

       (action! [this afn]
         (reset! action* afn))

       (value [this] (dec @count*))
       
       (midi-reset [this]
         (reset! count* @period*))

       (step [this]
         (swap! count* dec)
         (if (zero? @count*)
           (do 
             (reset! count* @period*)
             (@action*)
             0)
           @count*))

       (dump [this]
         (println (format "Counter value: %d  period: %d"
                          @count* @period*)))))))

