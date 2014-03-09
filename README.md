Cadejo  
======  

Cadejo is a MIDI management tool for use with Overtone
Some of its features are:  

* Multitimberal
* Arbitrary key splits and layering
* Tuning tables for alternate scales
* Mapping functions for MIDI velocity, continuous controllers and channel pressure. 
* Separate mono and polyphonic keymodes, additional keymodes may be implemented later.
* Program banks

#Instruments  

The Cadejo repo contains 4 full feature instruments:  
* ALGO - an 8 voice FM synthesizer.  
* Alias - a subtractive synthesizer with extensive modulation routing.  
* MASA - an organ roughly equivalent to the Hammond B3.  
* Combo - a simpler organ chiefly used for demonstration purposes. 

Each instrument includes a program bank containing several program or
'patch' definitions.


## Cadejo Structure  

Cadejo uses a hierarchical tree structure with the general form  


<code>[midi-port] --> scene --> channels --> performance --> instrument </code>  

Each node has a set of properties in the form of key/value pairs and
all nodes automatically inherit the properties of their parent. If a node
defines a property also defined in one of its ancestors the local value
shadows the parent's value and takes priority. As a concrete example each
scene has the property :tuning-table which defaults to a standard 12-note
tuning with MIDI key 69 producing A440. Any alternate tuning table defined
at either the channel or performance level will take precedence over this
default value.  

###Scene  
  
A Scene is the root node for a Cadejo tree. It is always connected to a
single MIDI input port and always has 16 child nodes corresponding to the
16 MIDI channels. A scene may also contain default properties for the local
environment. Two such properties are automatically defined for each scene  

* :tuning-table - A standard 12 notes per octave scale with MIDI key 69 = A440  
* :velocity-map - A linear function which maps MIDI velocity values (0-127) 
to a *normalized* range of (0.0-1.0)  

###Channel  
  
Each scene has 16 child nodes corresponding to the 16 MIDI
channels. Typical properties defined at the channel level include
pitch-bend range, channel-pressure maps, velocity maps, and MIDI continuous
controller assignments. Each channel may have 0 or more *performance* child
nodes. It is the combination of performance nodes under a channel which
allow for key-splits and layering.
  
###Performance  

A performance may be viewed as a container which holds reference to the
various SuperCollider synths, buses, banks and keymodes which define an
instrument.

## Installation  

Cadejo has no requirements other then a working copy of Overtone. 

## Usage  

Prior to first usage a MIDI port must be established by setting the value
of midi-input-port in cadejo/demo.clj. Once the device has been set
navigate to the main Cadejo folder and start a Clojure REPL. If all goes
well Cadejo should bootstrap and after a few seconds be ready to play. As
Cadejo loads several lines of text will be displayed. Once Cadejo is loaded
it should display the text "Ready..."  

The default demonstration program creates instruments on MIDI channels 0,
1, 2 and 3. All instruments have several programs changes available via
MIDI program change messages. Program number 127 is special and produces a
random patch.  

__WARNING__  

    The Alias instrument is very complex and capable of creating speaker
    destroying sub-sonics, particularly when using the random program
    generation feature. For the most part it behaves but please keep
    monitor levels down until you know what it is doing.  


At some point it is hoped that a configuration file feature will be
added. For the moment demo.clj doubles as the configuration file.

## Documentation  

Preliminary documentation may be found in the cadejo/docs folder.
Documentation for the Combo instrument doubles as an example of how to
integrate Overtone instruments into a Cadejo context.  


### Bugs

Cadejo contains a few known bugs. These are tracked in the file cadejo/BUGS.


## License

Copyright © 2014 Steven Jones


