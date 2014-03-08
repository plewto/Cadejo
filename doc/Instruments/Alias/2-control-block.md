Alias Control Block
===================

The Alias control block contains several control-signal generators and a
switching matrix. Control signals are routed via the matrix to one of 8
general control buses. These control buses are then sent to the audio
portions of the instrument where the signals they contain may be used to
modulate a wide variety of parameters.  Control signal sources within the
control block are directly available to other units within the control
block.

##Matrix   

The switching matrix is at the heart of Alias.  

###Sources  

    :env1   - Envelope generators
    :env2
    :env3
    :lfo1   - Low Frequency Oscillators
    :lfo2
    :lfo3
    :step1  - a pulse counter
    :step2  - a pulse counter
    :div1   - a frequency divider with odd division ratios
    :div2   - a frequency divider with even ratios
    :div    - The sum of div1 and div2
    :lfnse  - low frequency noise
    :sh     - sample and Hold
    :freq   - note frequency in Hertz
    :period - period of note frequency (1/frequency)
    :keynum - MIDI key-number
    :gate   - Key gate signal, 0 all keys up, 1 any key down
    :press  - MIDI pressure
    :vel    - MIDI velocity
    :cca    - MIDI controller a
    :ccb    - MIDI controller b
    :ccc    - MIDI controller c
    :ccd    - MIDI controller d
    :a      - general bus a
    :b      - general bus b
    :c      - general bus c
    :d      - general bus d
    :e      - general bus e
    :f      - general bus f
    :g      - general bus g
    :h      - general bus h
    :con    - a constant 1
    :off    - a constant 0

###Destinations within the control block    

    lfo1 frequency 1     -
    lfo1 frequency 2     -
    lfo1 wave 1          -
    lfo1 wave 2          -
    lfo2 frequency 1     -
    lfo2 frequency 2     -
    lfo2 wave 1          -
    lfo2 wave 2          -
    lfo3 frequency 1     -
    lfo3 frequency 2     -
    lfo3 wave 1          -
    lfo3 wave 2          -
    step1 trigger source -
    step1 reset source   -
    step2 trigger source -
    step2 reset source   -
    div1 amplitude       -
    div2 amplitude       -
    lfnoise frequency    -
    sh source            -
    a1                   - Each general control bus (a..h) has 2 inputs.
    a2                   - When both inputs are used the two signals 
    b1                   - are multiplied. This allows for easy amplitude
    b2                   - control of one signal over another without
    c1                   - tying up an additional bus. 
    c2                   -
    d1                   -
    d2                   -
    e1                   -
    e2                   -
    f1                   -
    f2                   -
    g1                   -
    g2                   -
    h1                   -
    h2                   -


Note this matrix destination list only applies to destinations internal to
the control block. The oscillator, filter and effects sections have their
own set of destinations.  

##Envelopes  

Alias uses three nearly identical envelope generators env1, env2 and
env3. Envelope 3 is dedicated to the overall amplitude control. The other
two envelopes have no pre-established connections. The envelopes have an
ADDSR form with 3 level and 4 time parameters.  

    (env1 atk dcy1 dcy2 rel  :pk p :bp bp :sus s :invert i)  
    (env2 atk dcy1 dcy2 rel  :pk p :bp bp :sus s :invert i)  
    (env3 atk dcy1 dcy2 rel  :pk p :bp bp :sus s)  

    atk     - attack time in seconds
    dcy1    - initial decay time in seconds
    dcy2    - second decay time in seconds
    rel     - release time in seconds
    :pk     - "peak" amplitude after attack segment.
    :bp     - "break point" amplitude after initial decay stage.
    :sus    - sustain level after second decay stage
    :invert - 0 or 1 indicating if envelope shape should be inverted.  

Note that envelope 3 does not have an invert option, otherwise the three
envelopes are identical.  

The three level parameters (pk, bp and sus) have a valid range between 0.0
and 1.0 inclusive. By setting pk less then bp the envelope form changes
from ADDSR to AADSR with two attack stages in place of two decay
stages. This feature may be use for delay onset envelopes.  

##LFOs  

The three LFOs are identical with continuously variable wave-shape between
negative slope sawtooth, triangle and positive slope sawtooth. The LFOS
have no fixed use but many of the patches use LFO1 for vibrato. By
assigning both LFO1 and cca (by default MIDI controller 1, the modulation
wheel) to the same control bus the modulation wheel controls the output of
the LFO and thus modulation depth.

    (lfo1 :fm1 [fsrc1 fdth1] :fm2 [fsrc2 fdth2]
          :wave1 [wsrc1 wdpth1] :wave2 [wsrc2 wdpth2])

    (lfo2 :fm1 [fsrc1 fdth1] :fm2 [fsrc2 fdth2]
          :wave1 [wsrc1 wdpth1] :wave2 [wsrc2 wdpth2])

    (lfo3 :fm1 [fsrc1 fdth1] :fm2 [fsrc2 fdth2]
          :wave1 [wsrc1 wdpth1] :wave2 [wsrc2 wdpth2])
    
    
    fsr1   - First frequency modulation source, may be any of the matrix 
             source listed above.
    fdth1  - Modulation depth of fsrc1
    fsrc2  - Second frequency modulation source
    fdth2  - Modulation depth of fsrc2
    wsrc1  - First wave modulation source
    wdpth1 - Modulation depth of wsrc1
    wsrc2  - Second wave modulation source
    wdpth2 - Modulation depth of wsrc2  

The LFOs do not have explicit frequency and wave parameters. Instead one of
the modulation sources may be set to a constant (:con) and the
corresponding depth parameter used to set the desired value. The wave
parameter has a range between 0.0 and +1.0 inclusive. At 0.5 the
waveform is a triangle, at 0.0 the waveform is a sawtooth with a negative
slope. At 1.0 a positive slope sawtooth is produced.  

##Step Counters  

There are two identical step counters. On reception of an input trigger
moving from non-positive to positive the counters advance one step. Upon
reaching a pre-determined value the output reverts to the initial
value. The nominal output has a stair-step shape an may be either
increasing or decreasing. A second reset input causes the input immediately
to switch back to the initial value and may be used for more complex output
shapes.  


    (stepper1 :trig tsrc :reset rsrc :ivalue iv :min mn :max mx :step st :bias b :scale s)
    (stepper1 :trig tsrc :reset rsrc :ivalue iv :min mn :max mx :step st :bias b :scale s)
    
    :trig   - Trigger input source
    :reset  - Reset input source
    :ivalue - Initial value, upon reset output immediately switches to ivalue.
    :min    - Minimum output
    :max    - Maximum output
    :step   - Step size, output increment on reception of trigger
    :bias   - Fixed value added to output.
    :scale  - Scaling factor applied to output, scaling factor is applied prior to bias.   

The output continuously cycles between min and max with increments set by
step. If min is greater then max then step should be negative. The min,
max, step and ivalue parameters should all be integers.  

##Frequency Dividers  

There are two frequency divider cascades, both driven by LFO3. The first
divider, div1, provides only odd division quotients: 1,3,5 and 7. The
second divider, div2, provides only even number quotients: 2,4,6 and 8. The
matrix source :div is the sum of div1 and div2.  

    (divider1 :p1 p1 :p3 p3 :p5 p5 :p7 p7 :pw pw :am [asrc adpth] :bias b)
    
    :p1    - Relative weight of /1 divider 
    :p3    - Relative weight of /3 divider
    :p5    - Relative weight of /5 divider
    :p7    - Relative weight of /7 divider
    :pw    - Common pulse width
    :asrc  - Amplitude scale source
    :adpth - Amplitude modulation depth
    :bias  - Fixed bias added to output.
    

The outputs of the four frequency dividers are scaled by their respective
scaling factors and then summed. This sum is then scaled to any amplitude
modulation determined by asrc and adpth. Finally the bias value is added to
form the overall divider output signal. The :pw parameter sets the pulse
width of all divider outputs in common. For pw=0.5 the dividers produce a
square wave. pw values slightly off 0.5 produce a kind of 'swing'
rhythm. pw values at the extremes (0 and 1) tend to produce short transient
pulses.  

divider2 is identical to divider1 with exception of the division quotients.  

    (divider1 :p2 p2 :p4 p4 :p6 p6 :p8 p8 :pw pw :am [asrc adpth] :bias b)  

  
##Low Frequency Noise  

    (lfnoise  :fm [src dpth])
    
    :src  - Frequency modulation source
    :dpth - Frequency modulation depth
    

Generates a random low frequency signal which changes at a rate
approximately determined by the fm src and depth parameters.

##Sample And Hold  
  
The sample and hold circuit 'samples' a continuously changing input signal
at a pre determined rate and holds the value until the next sample
period. It is very common to use a noise source as input to a sample and
hold circuit to produce an endless stream of random values. Applying sample
and hold to lfnoise changes it from a continuously changing random value to
a series of discrete random values. For non-random input the sample and
hold can produce anything from regular patterns to highly complex periodic
output.  

    (sh  :rate r :src src :bias b :scale s)
    
    :rate  - Sampling rate in Hertz
    :src   - Sampling input source
    :bias  - Fixed value added to output
    :scale - Scaling factor applied to output
    
The scale factor is applied prior to the bias value.  

##Matrix Details  

The general matrix consist of 8 buses labeled a..h. Within the control
block proper signal routing is more flexible. As a quick illustration env1
and lfo1 are contained in the control block while the audio oscillator osc1
is not. Since both env1 and lfo are in the control block env1 may be
directly used as a modulation source to lfo1 (or any other member of the
control block). In order for env1 to be used with osc1 however it must first
be assigned to one of the general matrix buses a..h.  

Each general matrix bus has 2 inputs and a corresponding sensitivity
value. These two inputs are multiplied which effectively allows amplitude
modulation between the two input buses. If only one input is desired the
unused input __must__ be set to a constant with sensitivity 1. [:con 1]. If
either input source is set to :off the the bus output is 0.  

    (matrix
        :a1 [src sens] :a2 [src sens]
        :b1 [src sens] :b2 [src sens]
        :c1 [src sens] :c2 [src sens]
        :d1 [src sens] :d2 [src sens]
        :e1 [src sens] :e2 [src sens]
        :f1 [src sens] :f2 [src sens]
        :g1 [src sens] :g2 [src sens]
        :h1 [src sens] :h2 [src sens])

Where src is any one of the matrix sources :env1 :env2 :env3 :lfo1 :lfo2
:lfo3 :step1 :step2 :div1 :div2 :div :lfnse :sh :freq :period :keynum :gate
:press :vel :cca :ccb :ccc :ccd :a :b :c :d :e :f :g :h :con or :off  

Note it is possible to set up feedback paths within the matrix, the exact
behavior of Alias using such configurations has not been explored.  
