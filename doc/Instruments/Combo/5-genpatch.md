cadejo.instruments.combo.genpatch  
=================================  

cadejo.midi.program/Bank allows both literal program data and for functions
which are executed to generate program data. genpatch defines such a
function which generate random Combo programs. In general these 
*program-functions* should accept an arbitrary number of arguments and
return an association list of parameter key/value pairs.  

The gen-combo function below simply ignores its arguments but in more
complex cases arguments may be used to provide 'hints' to the nature of
program to create.  

For more details see documentation for combo.data.  

<pre>
(ns cadejo.instruments.combo.genpatch
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.combo.program :as program]))

(def bypass program/bypass)
(def low program/low)
(def high program/high)
(def band program/band)
(def notch program/notch)

(def filter-types [bypass bypass bypass bypass
                   low low low low
                   high band notch notch])

(defn gen-combo [& args]
  (let [main-tone (rand-nth '[1 2 3 4])
        a1 (or (and (= main-tone 1) 1.0)(rand))
        a2 (or (and (= main-tone 2) 1.0)(rand))
        a3 (or (and (= main-tone 3) 1.0)(rand))
        a4 (or (and (= main-tone 4) 1.0)(rand))
        w1 (rand)
        w2 (rand)
        w3 (rand)
        w4 (rand)]
    (program/combo :a1 a1 :w1 w1
                   :a2 a2 :w2 w2
                   :a3 a3 :w3 w3
                   :a4 a4 :w4 w4
                   :chorus (math/coin 0.5 0 (rand))
                   :vibrato (list :freq (math/coin 0.75 
                                                   (+ 3 (rand 4))
                                                   (rand 7))
                                  :sens (math/coin 0.90 0.01 (rand)))
                   :filter (list :freq (rand-nth '(1 2 3 3 4 4 4 6 6 6 8 8))
                                 :type (rand-nth filter-types))
                   :flanger (list :rate (math/coin 0.80 (rand 0.5)(rand 5))
                                  :depth (math/coin 0.75 (rand 0.5)(rand))
                                  :fb (* (math/coin 0.5 -1 +1)(rand))
                                  :mix (math/coin 0.25 (rand) 0))
                   :reverb (math/coin 0.25 (rand) 0)
                   :amp 0.20)))
</pre>
