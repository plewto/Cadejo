Cadejo Overview  
===============  

As an organizing principle Cadejo uses an hierarchical tree structure. The
top level object is a __scene__. Each scene contains 16 __channels__ and
each channel zero or more __performance__. Each node has a set of
properties in the form of key/value pairs. A node automatically inherits
the properties of all the nodes above it. If a node defines a property
which is also defined higher up the tree the local assignment shadows the
higher one and takes priority.  

#Scene  

A scene object (cadejo.midi.scene/Scene) forms the root of a Cadejo
tree. Each scene is connected to a single MIDI input port. If system
resource allow it should be possible to have any number of separate
scenes, each with its own tree. The primary duty of a scene is distribute
incoming MIDI messages based on channel. Currently Cadejo does not support
non-channel messages though these could be added later.  

The other duty of scene is to contain default property values. As a
concrete example each scene has the property :tuning-table which by default
defines a standard 12-note scale.  

Each scene has 16 child nodes corresponding to the 16 MIDI channels.  

#Channel  

Channel objects (cadejo.midi.channel/Channel) define the properties common
to any one MIDI channel. Each channel has a single scene as parent and zero
or more performances as children. Common properties defined at the channel
level include pitch-bend sensitivity and mapping functions for pressure,
velocity and continuous controllers.  

#Performance  

A performance (cadejo.midi.performance/Performance) contains properties for
a single Overtone instrument. Each performance has a single parent channel
and implements a specific keymode. Performance objects may also be viewed
as containers. They hold reference to all the SuperCollider synths and
buses which define an instrument. A performance has a specific key-range
over which it responds and may be transposed. Each performance includes a
program bank.

#Keymodes  

Each performance has an associated keymode which is an object that
implemented the Keymode protocol defined in cadejo.midi.keymode. Currently
there are two separate keymodes; *mono-mode* and *poly-mode*. mono-mode
keeps track of all keys held so that whenever a key is released the
pitch automatically jumps to the value of any other key which happens to be
still be down. poly-mode uses a voice stealing technique which first
attempts to use a free voice if one is available. If no free voices are
available the oldest occupied voice is reallocated to the new key. Other
keymodes are possible, such as poly-rotation scheme or the use of layering
with detune but are yet to be implemented.
