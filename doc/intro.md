Cadejo Introduction   
====================    

Cadejo is a MIDI configuration tool for use with Overtone. It provides a
basic infrastructure for controlling Overtone instruments from an external
MIDI source with support for all MIDI channel messages with the exception
of polyphonic after-touch. Some of its key features are:

* Modular keymodes - Currently two separate keymodes are implemented; mono
  and poly. Additional keymodes are planned in the future.  

* Tuning tables - MIDI key-numbers may be arbitrarily associated to
  frequency. The default table defines the standard 12-tone equal
  temperament. A just intonation table is also provided.  

* Pitch-bend calibrated in cents.  

* Channel pressure with optional mapping function.    

* Continuous controller support also with optional mapping functions.  

* Velocity mapping function.  

* Program banks with the ability to execute arbitrary functions via MIDI
  program changes.

* key-splits    

* layering  

#Instruments  

Instruments for use with Cadejo are no different from any other Overtone
instrument as long as they follow a few basic conventions. However
additional boilerplate code is required to *register* an instruments with
Cadejo. Details on how to do this can be found in the documentation for the
Combo instrument.

Four full-feature instruments are included in the Cadejo package.  

__ALGO__ is an 8-operator FM synthesizer with some unique features.  

__ALIAS__ is a mean little 3 oscillator subtractive instrument with extensive modulation possibilities. 

__MASA__ is an organ simulation loosely based on the Hammond B3.  

__Combo__ is another organ simulation somewhat simpler then MASA. The
documentation for Combo includes heavily annotated copies of the source
files to illustrate the steps necessary for using an instrument with
Cadejo.

#Requirements  

Cadejo requires only that Overtone/SuperCollider be installed. It was
developed/tested on a 64-bit Linux system running Fedora-17 using the
internal Overtone server.

#Configuration  

Currently configuration is hard-coded into the Cadejo source. This too
shale pass. Eventually it is hoped to add user-configuration files. For the
moment the file cadejo/demo.clj serves as configuration. It is necessary to
set the value of midi-input-port to match a port available on your machine.
demo also contains examples of using Cadejo in different ways.  

#Additional Documentation  

Additional documentation may be found in the cadejo/doc folder.
cadejo/doc/Instruments contains overviews of the various instruments and
cadejo/doc/Instruments/Combo extensive annotation.  

cadejo/doc/Manual contains detailed information about Cadejo.  
 


