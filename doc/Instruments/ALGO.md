ALGO  
====  

ALGO is an 8-operator FM synthesizer with a fixed "algorithm" of 3 carriers
and 5 modulators. Two of the modulators include feedback. Additional
features include three LFOs and a separate envelope, a built in delay line
and reverb. All envelopes have an ADDSR format with 2 decay stages and some
may be inverted.

<pre>

   +---+                                                                        
   | 3 |                                                                        
   +---+                                                                        
     |                                                                          
     |                                                                          
     |                                                                          
   +---+   +---+  +---+        +---+                                            
   | 2 |   | 5 |  | 6 |<---+   | 8 |<---+                                       
   +---+   +---+  +---+    |   +---+    |                                       
     |       |      |      |     |      |                                       
     |       +--+---+      |     +------+                                       
     |          |          |     |                                              
   +---+      +---+        |   +---+                                            
   | 1 |      | 4 |        |   | 7 |                                            
   +---+      +---+        |   +---+                                            
     |          |          |     |                                              
     |          +----------+     |                                              
     |          |                |                                              
     +----------+----------------+                                              
                |                                                               
               out                                                              

</pre>                                                                         

##ALGO Data File Format
<pre>
00 : (let [enable '[1 1 1 1 1 1 1 1]]
01 :   (save-program 1 "Default"
02 :     (algo (common  :amp  0.20 
03 :                    :lp 10000
04 :                    :port-time 0.00 
05 :                    :cc-volume-depth 0.0
06 :                    :env1->pitch 0.0000
07 :                    :lfo1->pitch 0.0000 
08 :                    :lfo2->pitch 0.0000)
09 :           (env1    0.000 0.100 0.100 0.000   1.00 1.00   :bias 0.00 :scale +1.00)
10 :           (vibrato :freq 7.00 :depth 0.00 :delay 0.00 :sens 0.03)
11 :           (lfo1    :freq 7.00  :cca->freq 0.00 :ccb->freq 0.00
12 :                    :env1  0.00 :pressure 0.00  :cca 0.00 :ccb 0.00
13 :                    :skew 0.50 :env1->skew 0.0)
14 :           (lfo2    :freq 7.00 :cca->freq 0.00 :ccb->freq 0.00
15 :                    :pressure 0.00 :lfo1 0.00 :cca 0.00 :ccb 0.00
16 :                    :skew 0.50  :lfo1->skew 0.00)
17 : 
18 :           (op1 (nth enable 0)  :addsr   [0.00 0.10 0.10 0.00   1.00 1.00] 
19 :                :detune 1.000   :bias +0         :amp 1.000
20 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
21 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
22 :                :lfo1 0.00      :lfo2 0.00)
23 :           (op2 (nth enable 1)  :addsr   [0.00 0.10 0.10 0.00   1.00 1.00] :env-bias 0   :env-scale +1
24 :                :detune 1.000   :bias +0         :amp 0.000    
25 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0 
26 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
27 :                :lfo1 0.00      :lfo2 0.00       :hp  50)
28 :           (op3 (nth enable 2) :addsr   [0.00 0.10 0.10 0.00    1.00 1.00] :env-bias 0  :env-scale +1
29 :                :detune 1.000   :bias +0         :amp 0.000
30 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
31 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
32 :                :lfo1 0.00      :lfo2 0.00)     
33 : 
34 :           (op4 (nth enable 3) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00]
35 :                :detune 1.000   :bias +0        :amp 0.000
36 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
37 :                :velocity 0.00  :pressure 0.00  :cca 0.00    :ccb 0.00
38 :                :lfo1 0.00      :lfo2 0.00)
39 :           (op5 (nth enable 4) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
40 :                :detune 1.000   :bias +0         :amp 0.000
41 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
42 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
43 :                :lfo1 0.00      :lfo2 0.00)
44 :           (op6 (nth enable 5) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
45 :                :detune 1.000   :bias +0         :amp 0.000
46 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
47 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
48 :                :lfo1 0.00      :lfo2 0.00       :hp  50
49 :                :fb   0.00      :env->fb 0.00    :lfo1->fb 0.00 
50 :                :pressure->fb 0.00 :cca->fb 0.00 :ccb->fb 0.00)
51 : 
52 :           (op7 (nth enable 6) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00]
53 :                :detune 1.000   :bias +0        :amp 0.000
54 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
55 :                :velocity 0.00  :pressure 0.00  :cca 0.00    :ccb 0.00
56 :                :lfo1 0.00      :lfo2 0.00)
57 :           (op8 (nth enable 7) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
58 :                :detune 1.000   :bias +0         :amp 0.000
59 :                :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
60 :                :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
61 :                :lfo1 0.00      :lfo2 0.00       :hp  50
62 :                :fb   0.00      :env->fb 0.00    :lfo2->fb 0.00 
63 :                :pressure->fb 0.00 :cca->fb 0.00 :ccb->fb 0.00)
64 :           (echo :delay-1 0.25       :fb 0.50
65 :                 :delay-2 1.00       :damp 0.0   :mix 0.00)
66 :           (reverb :size 0.5 :mix 0.00))))
</pre>  

__Line 00__ While working with FM it is convenient to be able to
selectively mute various operators to hear what effect they are having on
the overall sound. The enable array on line 0 contains a flag for each of
the 8 operators. A value of 0 mutes the corresponding operator, a value of 1
enables it.  

__Line 01__ Uses _save-program_ to store an ALGO program into the program 
bank.  

    (save-program pnum name remarks data)
    (save-program pnum name data)

Where pnum is the MIDI program number    
name is the programs name  
remarks are optional remarks text, and  
data is the program data.  

Program data may be either a literal _association-list_ of parameter
key/value pairs or a function which generates a data list.  

__Line 02__ The _algo_ function builds an ALGO program data-list. algo
itself does little more then aggregate the outputs of several more specific
functions and perform a few data integrity test. The functions included in
the call to algo include *common, env1, vibrato, lfo1, lfo2, op1, op2, op3,
op4, op5, op6, op7, op8, echo and reverb*

__Line 02 common__ The common function defines a few parameters which are of a
global nature within an instance of ALGO.  
<pre>
    :amp - Overall linear amplitude, default 0.20
    :lp - Global low-pass filter cutoff in Hertz, default 10000.
    :port-time - Portemento time in seconds, default 0
    :cc-volume-depth - Sets how much effect MIDI volume (controller 7) has 
                       on overall volume. If set to 0 MIDI volume has no 
                       effect. 0<=depth<=1, default 0
    :env1->pitch - Amount of pitch shift from env1, default 0
    :lfo1->pitch - Amount of pitch modulation from lfo1, default 0
    :lfo2->pitch - Amount of pitch modulation from lfo2, default 0
</pre>  


__Line 09 env1__ is a general envelope generator which may be used for
pitch bend or for modulating certain aspects of lfo1 and lfo2. All ALGO
envelopes have the form ADDSR with 2 decay stages. The parameters to env1
are:  
<pre>
    (env1 a d1 d2 r bp s :bias b :scale x)

    a     - Attack time in seconds
    d1    - Decay-1 time in seconds
    d2    - Decay-2 time in seconds
    r     - Final release time in seconds
    bp    - Break-point, the level after first decay stage
    s     - Sustain level, the level after second decay stage
   :bias  - A fixed bias value added to the envelope output, default 0
   :scale - A scaling factor which is applied to the envelope output, default +1
</pre>

An inverted envelope may be produced by setting bias=+1 and scale=-1.  


__Line 10 vibrato__ Vibrato is provided by a separate dedicated
oscillator. The vibrato depth may be set programticaly or by the MIDI
modulation wheel.

<pre>
    (vibrato :freq f :depth d :delay dy :sens s)

    :freq  - vibrato frequency in Hertz.
    :depth - The minimum vibrato applied, even with modulation wheel in 
             the 0 position.
    :delay - Onset delay in seconds of programmed vibrato, delay will
             have no effect if depth is 0.
    :sens  - The vibrato sensitivity as a ratio of the tones frequency.
             If sens is 0 there will be no vibrato, programmed or from the 
             modulation wheels. Sensitivity values of around 0.01 and 
             below are suitable for 'natural' sounding vibratos.
</pre>  


__Lines 011-013 LFO1__ lfo1 and lfo2 may be used as an additional vibrato
source or for selectively modulating operator amplitudes or feedback
levels. Both lfo1 and lfo2 produce waveforms which are continuously
variable between sawtooth and triangle shapes. Both positive and negative
slope sawtooths are possible.

<pre>
    :freq       - Frequency in Hertz.
    :cca->freq  - MIDI cca controller effect on frequency
    :ccb->freq  - MIDI ccb controller effect on frequency
    :env1       - env1 modulation of lfo amplitude, 0<=env1<=1.
    :pressure   - pressure effect on lfo amplitude
    :cca        - MIDI cca controller effect on lfo amplitude
    :ccb        - MIDI ccb controller effect on lfo amplitude
    :skew       - Oscillator waveshape. For a skew value of 0.5 the lfo
                  produces a triangle wave. As the skew value moves away
                  from 0.5 to either 0.0 or 1.0 the waveshape becomes
                  increasingly sawtooth like. The slope of the sawtooth
                  is dependent on which side of 0.5 the skew value is. 
    :env1->skew - The amount of env1 modulation of skew.
</pre>


__Lines 14-16 LFO2__ lfo2 is similar to lfo1 but with different modulation
sources.  

<pre>
    :freq       - Frequency in Hertz.
    :cca->freq  - MIDI cca controller effect on frequency
    :ccb->freq  - MIDI ccb controller effect on frequency
    :pressure   - pressure effect on lfo amplitude
    :lfo1       - Amount of lfo1 modulation of lfo2 amplitude
    :cca        - MIDI cca controller effect on lfo amplitude
    :ccb        - MIDI ccb controller effect on lfo amplitude
    :skew       - Oscillator waveshape. For a skew value of 0.5 the lfo
                  produces a triangle wave. As the skew value moves away
                  from 0.5 to either 0.0 or 1.0 the waveshape becomes
                  increasingly sawtooth like. The slope of the sawtooth
                  is dependent on which side of 0.5 the skew value is. 
    :lfo1->skew - The amount of lfo1 modulation of skew.
</pre>


__Lines 18-22 op1__  

Operator 1 is a carrier and its options are identical to the other two
carriers (op4 and op7). The first argument to all of the operators is an
enable flag. The call to <nobr>(nth enable 0)</nobr> simply extracts the
relevant flag from the enable array on line 0.

<pre>
    :addsr       - Envelope [a d1 d2 r  bp s]
    :detune      - The relative operator frequency.
    :bias        - A fixed value added to the operators frequency. 
                   An operator can be set to a constant frequency by 
                   setting  detune to 0 and then bias to the desired
                   frequency. 
    :amp         - Linear operator amplitude.
    :left-key    - A MIDI key number for the left key-scale break point
    :left-scale  - The left key-scale factor. For each octave played below
                   left-key the output amplitude is scaled by 
                   left-scale (in db).
    :right-key   - A MIDI key number indicating the right key-scale
                   break point.
    :right-scale - The right key-scale factor. For each octave played 
                   above right-key the output amplitude is scaled by
                   right-scale (in db)
    :velocity    - Determine the velocity influence over operator
                   amplitude. 0 <= velocity <= 1
    :pressure    - Determines the pressure influence over
                   amplitude, 0 <= pressure <= 1
    :cca         - Determines influence of MIDI cca controller over
                   amplitude 0 <= cca <= 1
    :ccb         - Determines influence of MIDI ccb controller over
                   amplitude 0 <= cca <= 1
    :lfo1        - Determines influence of lfo1 over amplitude 0 <= cca <= 1
    :lfo2        - Determines influence of lfo2 over amplitude 0 <= cca <= 1
</pre>  

Carrier envelopes have the same ADDSR form as the general envelope on line
9 except they lack the env-bias and env-scale factors.  


__Lines 23-27 op2__  

Operator 2 is a modulator. All modulators share the same parameters as the
carriers but have some additional features. For brevity modulator
parameters which are identical to carrier parameters will not be
repeated. One common difference between modulators and carriers is that the
envelopes for modulators have the bias and scale factors while carrier
envelopes do not. This allows for inverted modulator envelopes which make
little sense for carriers (an inverted carrier envelope would drone on even
if no keys have been pressed). Operator 2 has one additional feature, a
fixed high-pass filter on its output. The :hp parameter sets the cutoff
for this filter.  

A high-pass filter is required on the outputs of certain modulators due to
the possible presence of a significant DC component in their output. If
this DC were allowed to pass to the carrier the result would be a linear
shift in the carrier frequency which would throw it out of tune. High-pass
filters are used to block this DC component. By default these filters have
a fixed cutoff of 50hz, typically well below the modulator frequency. If
the cutoff frequency is set low enough (between 1 and 10hz) the filter
takes a few milliseconds to settle down after its input has changed. The
result of this settling is a clearly audible 'blip' in the carrier.  At
times a blip is just the thing needed to add realistic complexity to an
attack. For this reason the high-pass cutoffs are made available as a user
parameter.  

__Lines 28-32 op3__ Operator 3 is a modulator with identical features to
op2 except that it lacks a high-pass filter.  

__Lines 34-38 op4__ Operator 4 is a carrier with identical feature to
operator 1.  

__Lines 39-43 op5__ Operator 5 is a modulator with identical feature to
operator 2, except that it lacks a high-pass filter.  

__Lines 44-50 op6__ Operator 6 is another modulator. It has identical
features as op2 (including high-pass filter) and also includes feedback
from op4. The parameters related to feedback are:  

<pre>
    :fb           - Fixed feedback amount, values greater then about 2
                    start to become chaotic. 
    :env->fb      - Amount of envelope modulation to feedback. Note this
                    is the op6 envelope and not the global envelope.
    :lfo1->fb     - Amount of lfo1 modulation to feedback
    :pressure->fb - Amount of MIDI pressure modulation to feedback
    :cca->fb      - Amount of MIDI controller cca modulation to feedback
    :ccb->fb      - Amount of MIDI controller ccb modulation to feedback
</pre>  

The feedback modulators are additive so if set to a negative value may be
used to reduce feedback amounts. The quality of the signals generated by
op6 feedback are quite complex. They often seem to increase as notes are
played higher up the keyboard. If this is undesirable the key-scaling
feature may be useful to tame things down a bit.


__lines 52-56 op7__ Operator 7 is a carrier with identical feature as op1.  


__lines 57-63 op8__ Operator 8 is another modulator with feedback. The
method used for generating op8 feedback is different from that used with
op6 and somewhat easier to control. The parameters for op8 are identical to
those of op6 with the exception that op6 uses lfo1 for feedback modulation
while op8 uses lfo2.

__Lines 64-65 echo__  ALGO has an integral duel delay-line effect.   

<pre>
    :delay-1 - Delay time 1, <= 1 second
    :delay-2 - Delay time 2, <= 1 second
    :fb      - Feedback 0 <= fb <= 1
    :damp    - Feedback dampening 0 <= damp <= 1
    :mix     - Wet/dry echo mix, 0 = dry, 1=wet
</pre>

__Line 66 reverb__  ALGO has a simple reverb effect.  

<pre>
   :size - Room size 0 <= size <= 1
   :mix  - Wet/dry mix, 0 = dry, 1 = wet
</pre>

##Random Program Generation  

Sending MIDI program change 127 to an ALGO instrument will generate a
random program. The generated data will also be displayed to the terminal
in a form suitable for inclusion in the program data file.  

##Creating an ALGO instrument  

The two functions cadejo.instruments.algo.engine/algo-mono and
cadejo.instruments.algo.engine/algo-poly are used to create ALGO
instruments and register them for use with Cadejo. Both functions take a
Cadejo scene and a MIDI channel number. By default the polyphonic voice
count is 8. These two functions are also used to assign midi controller
numbers. ALGO recognizes the following MIDI controllers:

<pre>
   cc-vibrato - Vibrato depth, default 1, modulation wheel  
   cc-volume  - Default 7
   cca        - General purpose controller, default 16
   ccb        - General purpose controller, default 17
   cc-echo    - Echo mix, default 91
   cc-reverb  - Reverb mix, default 92
</pre>



