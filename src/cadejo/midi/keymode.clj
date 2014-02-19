(ns cadejo.midi.keymode)

(defprotocol Keymode
  
  "Defines performance keymode interface.
   See mono-mode and poly-mode for concrete implementations."

  (set-parent! 
    [this performance]
    "Sets parent performance for this keymode.")

  (reset
    [this]
    "Set all notes off and reset all internal 
     values to a known initial state.")

  (key-down 
    [this event]
    "Handle MIDI note-on events.")

  (key-up
    [this event]
    "Handle MIDI note-off events")

  (trace!
    [this flag]
    "Enable diagnostic tracing of j]key on/off events")

  (dump 
    [this depth]
    [this]))
