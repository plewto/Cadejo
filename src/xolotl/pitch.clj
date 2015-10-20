(println "    --> xolotl.pitch")
(ns xolotl.pitch
  (:require [xolotl.counter])
  (:require [xolotl.cycle])
  (:require [xolotl.shift-register])
  (:require [xolotl.util :as util]))

(defn- neg-number? [n]
  "(neg-number? n)
   Predicate returns true if n is a number and it is negative"
  (and (number? n)(neg? n)))

(defprotocol PitchBlock

  (midi-reset [this])

  (velocity-mode! [this vmode]
    "(.velocity-mode! PitchBlock vmode)
     Sets velocity pattern index mode.
     ARGS:
       vmode - keyword, one of :seq, :random or :sr
     RETURNS: vmode")

  (velocity-pattern! [this vpat]
    "(.velocity-pattern! PitchBlock vpat)
     Set velocity pattern.
     ARGS:
       vpat - list or vector of int MIDI velocity values.
              0 <= vel < 128.
     RETURNS: vpat")

  (pitch-mode! [this pmode]
    "(.pitch-mode! PitchBlock pmode)
     Sets pitch pattern index mode.
     ARGS:
       vmode - keyword, one of :seq, :random or :sr
     RETURNS: pmode")

  (pitch-pattern! [this ppat]
    "(.pitch-pattern! PitchBlock ppat)
     Sets pitch pattern.
     ARGS:
       ppat - list or vector of int MIDI key-numbers.
              These values are relative to the transposition key and
              may be negative or positive. If after transposition the
              resulting key-number is out of range (0 <= kn < 128) no
              event is generated. This fact is used to introduce rest 
              by including excessively large negative numbers in the 
              sequence.

              Nested list may be include to produce chords. The 
              following example pattern produces a major scale followed by
              dominate 7th chord. [0 2 4 5 7 9 11 12 [0 4 7 10 12]]
    RETURNS: ppat")

  (taps!
    [this tval inject]
    [this tval]
    "(.taps! PitchBlock tval inject)
     (.taps! PitchBlock tval)
     Sets shift-register feedback taps. 
     ARGS:
       tval   - int, the taps value. See xolotl.shift-register
       inject - optional int, either 0 or 1, the value shifted into the 
                register. Default 1
     RETURNS: tval")

  (seed! [this sval]
    "(.seed! PitchBlock sval)
     Sets shift-register seed
     ARGS:
       sval - int, the seed value, See xolotl.shift-register
     RETURNS: sval")

  (dump-state [this])
  
  (callback [this]
    "(.callback PitchBlock)
     RETURNS: callback function for use by the Xolotl clock.
              See xolotl.clock.  The callback has the form 

              (fn [gate transpose hold-time]) where
              
              gate      - Boolean, if true, generate note events
                                   if false, do nothing
              transpose - int, transposition amount. 
              hold-time - float, sets how long each not is help in seconds"))

(defn pitch-block [evntgen]
  "(pitch-block evntgen)
   Creates instance of PitchBlock
   ARGS:
     evntgen - xolotl.eventgen/Transmitter
   RETURNS: PitchBlock"
  (let [velocity-mode* (atom :seq)
        pitch-mode* (atom :seq)
        velocity-cycle (xolotl.cycle/cycle [127])
        pitch-cycle (xolotl.cycle/cycle [0 2 4 5 7 9 11])
        sregister (xolotl.shift-register/shift-register 8 1)
        sr-inject* (atom 0)
        velocity-counter (xolotl.counter/counter (.period velocity-cycle))
        pitch-counter (xolotl.counter/counter (.period pitch-cycle))]
    (reify PitchBlock

      (midi-reset [this]
        (doseq [c (list velocity-cycle pitch-cycle sregister
                        velocity-counter pitch-counter)]
          (.midi-reset c)))

      (velocity-mode! [this vmode]
        (reset! velocity-mode* vmode))

      (velocity-pattern! [this vpat]
        (.values! velocity-cycle vpat)
        (.period! velocity-counter (.period velocity-cycle))
        vpat)

      (pitch-mode! [this pmode]
        (reset! pitch-mode* pmode))

      (pitch-pattern! [this ppat]
        (.values! pitch-cycle ppat)
        (.period! pitch-counter (.period pitch-cycle))
        ppat)

      (taps! [this t inject]
        (reset! sr-inject* (cond (zero? inject) 0
                                 inject 1
                                 :else 0))
        (.taps! sregister t))

      (taps! [this t]
        (.taps! this t 1))

      (seed! [this s]
        (.seed! sregister s))

      (dump-state [this]
        (let [sb (StringBuilder.)
              pad "  "
              pad2 (str pad pad)]
          (.append sb (format "%sPitchBlock\n" pad))
          (.append sb (format "%svelocity-mode     -> %s\n" pad2 @velocity-mode*))
          (.append sb (format "%svelocity-pattern  -> %s\n" pad2 (.values velocity-cycle)))
          (.append sb (format "%spitch-mode        -> %s\n" pad2 @pitch-mode*))
          (.append sb (format "%spitch-pattern     -> %s\n" pad2 (.values pitch-cycle)))
          (.append sb (format "%ssr-inject         -> %s\n" pad2 @sr-inject*))
          (.append sb (format "%s[shit-register   ]-> " pad2))
          (doseq [s (.stages sregister)] (.append sb (format "%s " s)))
          (.append sb "\n")
          (.append sb (format "%s[velocity-counter]-> %s\n" pad2 (.value velocity-counter)))
          (.append sb (format "%s[pitch-counter   ]-> %s\n" pad2 (.value pitch-counter)))
          (.toString sb)))
      
      (callback [this]
        (fn [gate transpose hold-time]
          (if gate
            (let [sr (.value sregister)
                  pmode @pitch-mode*
                  pp (.period pitch-cycle)
                  pi (cond (= pmode :random)(rand-int pp)
                           (= pmode :sr) sr
                           :else (- pp 1 (.value pitch-counter)))
                  p (.value pitch-cycle pi)
                  klst (map (fn [q] (+ q transpose))(util/->list p))
                  vmode @velocity-mode*
                  vp (.period velocity-cycle)
                  vi (cond (= vmode :random)(rand-int vp)
                           (= vmode :sr) sr
                           :else (- vp 1 (.value velocity-counter)))
                  vel (.value velocity-cycle vi)]
              (.generate-key-events evntgen klst vel hold-time)
              (.step velocity-counter)
              (.step pitch-counter)
              (.shift sregister @sr-inject* ))))) )))
