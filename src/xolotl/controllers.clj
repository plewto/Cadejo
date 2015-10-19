(println "    --> xolotl.controllers")
(ns xolotl.controllers
  (:require [xolotl.cycle]))

(defn- neither [a b] (not (or a b)))

(defprotocol ControllerBlock

  (controller-count [this]
    "(.controller-count ControllerBlock)
     RETURNS: int, the number of controller patterns")

  (controller-number! [this n ctrl]
    "(.controller-number! ControllerBlock n ctrl)
     Sets MIDI controller number
     ARGS:
       n    - int, index of which controller to change.
              0 <= n < controller-count
       ctrl - int, MIDI controller number, ctrl < 128.
              if ctrl is negative this controller is disabled
     RETURNS: vector [n ctrl]")

  (controller-pattern! [this n vals]
    "(.controller-pattern! ControllerBlock n vals)
     Sets controller value pattern.
     ARGS:
       n    - int, index of which controller to change.
              0 <= n < controller-count
       vals - list or vector of MIDI controller values.
              Values must be integers less then 128.
              Negative values are not transmitted and used 
              to skip an event.
     RETURNS: vals")

  (midi-reset [this]
    "(.midi-reset ControllerBlock)
     Resets controllers to initial state.")

  (callback [this]
    "(.callback ControllerBlock)
     RETURNS: callback function for use by the Xolotl clock,
              See xolotl.clock. The callback takes no arguments
              and any results are ignored. As a side-effect the
              callback request the event-generate to transmit
              controller events."))


(defn controller-block [evntgen]
  "(controller-block eventgen)
   Creates ControllerBlock 
   ARGS:
     evntgen - xolotl.eventgen/transmitter
   RETURNS: ControllerBlock"
  (let [c-count 2
        nor (fn [a b](not (or a b)))
        cycles (let [acc* (atom [])]
                 (dotimes [i c-count]
                   (swap! acc* (fn [q](conj q (xolotl.cycle/cycle [-1])))))
                 @acc*)
        controller-numbers* (atom (into [] (repeat c-count -1)))]
                             
    (reify ControllerBlock
      
      (controller-count [this] c-count)
      
      (controller-number! [this n ctrl]
        (swap! controller-numbers* (fn [q] (assoc q n ctrl))))
      
      (controller-pattern! [this n pat]
        (.values! (nth cycles n) pat))
      
      (midi-reset [this]
        (doseq [c cycles](.midi-reset c)))
      
      (callback [this]
        (fn []
          (dotimes [i c-count]
            (let [ctrl (nth @controller-numbers* i)
                  cyc (nth cycles i)
                  val (.value cyc)]
              (if (neither (neg? ctrl)(neg? val))
                (.generate-controller-event evntgen ctrl val))
              (.next cyc))))))))
   
                           



               
                   
               
                     
