Alias
=====

Alias is a three oscillator subtractive synthesizer with extensive
modulation possibilities. As it's name implies Alias is manifestly a
digital instrument and if it ever produces a warm and fuzzy analog sound a
bug report should be submitted. Alias will never feature in a Eurovision
song contest winner.  

###Overview 

Alias consist of four general sections: controllers, oscillator bank,
filters and effects.  The audio signal path starts with 3 oscillators, a
noise source and ring-modulator. These are mixed and routed to one of two
filters. The filters are then sent to the effects stage and finally to the
general output. The controllers sections contains several control signal
sources. Theses are routed to a matrix with 8 output buses. These buses are
then sent to the control inputs of the three audio blocks.  

See individual documentation files for more detail about each of these
sections.

##Controller Block  

The controller block consist of 3 envelope generators, 3 LFOs, 2 step
counters, 2 frequency dividers, a low frequency noise source and a sample
and hold. The outputs of these units, together with several external
control signals, are sent to an internal control matrix. It is possible for
example to have envelope 3 control the frequency of LFO2 and then use LFO2
to advance one of the step counters.  

##Oscillators  

The oscillator bank contains 3 oscillators, each with different
characteristics, a noise source with integrated filter and a
ring-modulator. Any of the four signal generators may serve as inputs to
the ring-modulator.  

Each oscillator has 6 control inputs (2 frequency , 2 wave-shape, 2
amplitude) which may each be set to any one of the 8 buses coming from the
control block.  

##Filters  

Alias has 2 filter paths with integrated distortion circuits. The two paths
are not identical and each has it's own set of characteristics. As with the
oscillators, several filter parameters may be controlled via the 8
controller buses.  

##Effects  

The Alias effects block consist of a pitch-shifter, a flanger and 2 delay
lines. As with the oscillators and filters, several effects parameters are
exposed to the 8 control buses.  

