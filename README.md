# cadejo

Cadejo is a MIDI management tool for use with Overtone
instruments. Some of its features are:  

* Multitimberal
* Arbitrary key splits and layering
* Pluggable tuning tables for alternate scales
* Pluggable mapping functions for MIDI velocity, continuous controllers and channel pressure. 
* Separate mono and polyphonic keymodes, additional keymodes may be implemented later.
* Program banks


Cadejo uses a hierarchical tree structure with the general form  


<code>[midi-port] --> scene --> channels --> performance --> synth </code>  

Each node has a set of properties in the form of key/value pairs and
all nodes automatically inherit the properties of their parent. If a node
defines a property also defined in one of its ancestors the local value
shadows the parents value and takes priority. As a concrete example each
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

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar cadejo-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
