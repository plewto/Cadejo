(println "--> cadejo.core")
 (ns cadejo.core
  (:require [seesaw.core])
  (:use [overtone.core])


  (:require [cadejo.midi.scene])
  (:require [cadejo.midi.channel])
  (:require [cadejo.midi.performance])
  (:require [cadejo.midi.program-bank])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.config])  
  )


;; ISSUE:
;; We are loading -all- instrument definitions. There should be some sort of
;; configuarable conditional load instead!

; ---------------------------------------------------------------------- 
;                                   Algo

(require '[cadejo.instruments.algo.algo-engine :as algo])
(def algo-descriptor (cadejo.instruments.descriptor/instrument-descriptor :algo "FM Synthesizer"))
(.add-controller! algo-descriptor :cc1 "Vibrato" 1)
(.add-controller! algo-descriptor :cc7 "Volume" 7)
(.add-controller! algo-descriptor :cca "A" 16)
(.add-controller! algo-descriptor :ccb "B" 17)
(.add-controller! algo-descriptor :ccc "Echo mix" 91)
(.add-controller! algo-descriptor :ccc "Reverb mix" 92)
(.add-constructor! algo-descriptor :mono algo/algo-mono)
(.add-constructor! algo-descriptor :poly algo/algo-poly)
(cadejo.config/add-instrument! algo-descriptor)

; ---------------------------------------------------------------------- 
;                                   Alias

(require '[cadejo.instruments.alias.alias-engine :as alias])
(def alias-descriptor (cadejo.instruments.descriptor/instrument-descriptor :alias "Mono Synth"))
(.add-controller! alias-descriptor :cc7 "Volume" 7)
(.add-controller! alias-descriptor :cca "A"  1)
(.add-controller! alias-descriptor :ccb "B" 16)
(.add-controller! alias-descriptor :ccc "C" 17)
(.add-controller! alias-descriptor :ccd "D"  4)
(.add-constructor! alias-descriptor :mono alias/alias-mono)
(cadejo.config/add-instrument! alias-descriptor)


; ---------------------------------------------------------------------- 
;                                   Combo

(require '[cadejo.instruments.combo.combo-engine :as combo])
(def combo-descriptor (cadejo.instruments.descriptor/instrument-descriptor :combo "Simple Organ"))
(.add-controller! combo-descriptor :cc1 "Vibrato" 1)
(.add-constructor! combo-descriptor :mono combo/combo-mono)
(.add-constructor! combo-descriptor :poly combo/combo-poly)
(cadejo.config/add-instrument! combo-descriptor)


; ---------------------------------------------------------------------- 
;                                   Masa

(require '[cadejo.instruments.masa.masa-engine :as masa])
(def masa-descriptor (cadejo.instruments.descriptor/instrument-descriptor :masa "Organ"))
(.add-controller! masa-descriptor :cc1 "Vibrato" 1)
(.add-controller! masa-descriptor :cc4 "Pedal" 4)
(.add-controller! masa-descriptor :cc7 "Volume" 7)
(.add-controller! masa-descriptor :cca "Scanner Mix" 92)
(.add-controller! masa-descriptor :ccb "Reverb Nix" 93)
(.add-constructor! masa-descriptor :mono masa/masa-mono)
(.add-constructor! masa-descriptor :poly masa/masa-poly)
(cadejo.config/add-instrument! masa-descriptor)


(require 'cadejo.demo2)

