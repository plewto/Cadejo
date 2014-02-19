(ns cadejo.midi.instrument)
  

(println "WARNING ***** cadejo.midi.instrument is depreciated! *****")

(defprotocol CadejoInstrument
  "Defines the interface for a Cadejo Instrument.
   See cadejo.instruments.alphaa.engine for example usage"
  (id 
    [this])

  (mono-performance
    [this main-out]
    [this])

  (poly-performance
    [this voice-count main-out]
    [this main-out]
    [this])
)
