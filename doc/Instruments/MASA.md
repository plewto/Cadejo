MASA Manual  
===========  

MASA is an Overtone organ simulation comparable in features to the Hammond
B3. The name is an acronym for "Multiplexed Acoustical Systems of America",
the fictional organ manufacture in *We Can Build You* by Philip K Dick.  In
addition to organs MASA manufactured Abraham Lincoln simulacra....

#Voice Architecture  

##Partials  

A MASA voice consist of 9 static partials with independent frequency,
amplitude, envelope switch and pedal sensitivity.  

###Partial Frequencies  

By default MASA uses the same frequency gamut as the B3:  

   <pre>
   1 -   16'      - sub fundamental
   2 -   5 1/3'   - sub 3rd
   3 -   8'       - fundamental
   4 -   4'       - 2nd harmonic
   5 -   2 2/3'   - 3rd harmonic
   6 -   2'       - 4th harmonic
   7 -   1 3/5'   - 5th harmonic
   8 -   1 1/3'   - 6th harmonic
   9 -   1'       - 8th harmonic
   </pre>

The Hammond frequencies deviate slightly from the ideal which is either a
compromise or authenticity depending on your view point. MASA uses the
Hammond only as a departure point and does not attempt to accurately
simulate a real B3. Unlike the B3 however the MASA frequency gamut is fully
programmable.

###Partial amplitudes  

Partial amplitudes are scaled to a value between 0 and 8 to simulate
drawbar positions. 0 is full attenuation and 8 is 'full on'. Again not
having a real B3 for comparison the 'drawbar' positions are not intended to
match the real instrument. The amplitude curves were derived through trial and
error.  

###Partial Percussion  

Each partial may have either a gated or percussive envelope. All percussive
envelopes share a common decay and sustain level.  

###Partial Pedal Parameter    

The pedal parameter sets how much each partial amplitudes is effected by
MIDI pedal control messages. Partial amplitudes may either be increased or
decreased by the pedal.  

##Additional Parameters  

    :decay   - Decay time for percussive envelope  
    :sustain - Sustain level for percussive envelope  
    :vrate   - Vibrato rate in Hertz  
    :vdepth  - Programmed vibrato depth (before application of mod wheel)  
    :vdelay  - Onset time for programmed vibrato
    :vsens   - Vibrato sensitivity  
 
##Effects  

MASA contains 2 effects;  *scanner* and *reverb*  

###Scanner  

The scanner is an unusual effect perhaps best characterized as a
multiplexed phase-shifter. The original Hammond engineers faced a challenge
while trying to add vibrato to their instrument. The basic tone generation
was achieved electro-mechanically by a series of meshed gear trains. The
gear trains were rotated by an electric motor which was phase locked to the
mains frequency. It was impossible to add vibrato at 'the front end' by
modulating the speed at which the motor rotated. The solution they arrived at
was an ingenuous electro-mechanical "after vibrato". At the time
(c1935) there were few practical techniques for audio delay. They could
apply a fixed phase shift however by sending a signal through an inductive
coil. Such phase shift would impart a slight (few ms) time delay to the
signal. They then wired a cascaded of fixed coils in series with each coil
adding a few ms of delay. Small metal plates were attached to the circuit
at each node point between the Coils. These plates were arranged in a
circle and another plate attached to a spinning arm was placed in the
center. As this 'scanning' plate passed each stationary plate the two plates
formed a capacitor and the signal was picked up off the spinning plate. To
smooth out the abrupt transition as the scanning arm completed a circle
complementary plates form opposite sides of the circle were mixed
together. As the the scanning plate came into proximity of a
stationary plate it was still physically close to the adjacent
plates. This contributed a considerable amount of bleed from adjacent
plates which helped smooth the effect further. Short of using a 3D printer
it is not possible to build an electro-mechanical scanner with Overtone.

The MASA scanner uses a cascade of 8 short delay lines. A scanning LFO then
selects one of these delays for output using a sine-windowing function. As
with the original the sine window allows bleed from adjacent delays. The
delay times may also be modulated which is something the original scanner
could not achieve.

      :scanner-delay      - The fixed delay time, delay < 0.01  
      :scanner-delay-mod  - Depth of delay time modulation, 0<= mod <=1   
      :scanner-mod-rate   - Rate of delay time modulation   
      :scanner-mod-spread - Amount of delay time variation between  
                            adjacent delays,  0<= spread <=1  
      :scanner-scan-rate  - The rate of the scanner LFO  
      :scanner-crossmix   - The amount of stereo crossmix in the result 0<= xmix <=1
      :scanner-mix        - Mix of wet and dry signals  0<= mix <=1  

###Reverb  

freeverb provides a simple reverb effect for MASA.  


      :room-size   - Reverb room size 0<= size <=1  
      :reverb-damp - High frequency dampening 0<= damp <=1  
      :reverb-mix  - Reverb Wet/Dry mix 0<= mix <=1  

###Additional Parameters  

      :amp - Linear amplitude
      :pedal-sens - Sensitivity to pedal controller, for a value of 0   
                    the pedal has no effect.  0<= sensitivity <=1.  

##Data File Format  

<pre>
00 : (save-program 2 "Jimmy Smith" "Pedal control drawbars 4 & 9"
01 :    (masa
02 :       :harmonics    [0.500 1.500 1.000 2.000 3.000 4.000 5.000 6.000 8.000 ]  
03 :       :registration [    8     8     8     0     0     0     0     0     0 ]  
04 :       :pedals       [+0.00 +0.00 +0.00 +1.00 +0.00 +0.00 +0.00 +0.00 +0.25 ]  
05 :       :percussion   [    0     0     0     0     0     0     0     0     0 ]  
06 :       :decay 0.20   :sustain 0.80  
07 :       :vrate 7.000  :vdepth 0.000    :vsens 0.020      :vdelay 0.000  
08 :       :scanner-:delay-mod 0.200      :scanner-delay 0.010  
09 :       :scanner-mod-rate  1.000       :scanner-mod-spread 0.000  
10 :       :scanner-scan-rate 7.000       :scanner-crossmix 0.200  
11 :       :scanner-mix 0.200  
12 :       :room-size 0.500               :reverb-damp 0.500  
13 :       :reverb-mix 0.200  
14 :       :amp 0.20                      :pedal-sens 0.00))  
</pre>  

The **save-program** function saves the MASA program data to the program bank  

<pre>
    (save-program pnum name rem data)
    (save-program pnum name data)

    pnum - MIDI program number 0<= pnum <=127
    name - Programs name
    rem  - Optional remarks text
    data - Program data as a list.
</pre>

The **masa** function provides the correct data format for save-program,
The values for the *:harmonic, :registration, :pedals* and *:percussion*
parameters must be 9-element arrays; an element each for the 9
partials. 

<pre>
    :harmonics     - Relative partial frequencies
    :registration  - Partial amplitudes as an integer between 0 and 8
    :pedals        - Partial sensitivity to the pedal controller -1<=pedal<=+1
    :percussion    - Boolean indicates if percussive envelope is used. 0=gate 1=percussion.
</pre>

The vibrato feature is separate from the scanner effect and controlled by
the modulation wheel. All other parameters a fairly self-evident from the
descriptions above. 


##Random Program generation  

The MASA data file assigns program slot 127 to a random patch
generator. Calling up this program via a MIDI program change generates a
random MASA program and also displays the generated data in a format
suitable for pasting into the data file.  

##Creating a MASA instrument  

The two functions cadejo.instruments.masa.engine/masa-mono and
cadejo.instruments.masa.engine/masa-poly are used to create a MASA instruments
and register them with a Cadejo performance. Both functions take a Cadejo
scene and a MIDI channel number. By default polyphonic voice count is 8.  

MIDI controller numbers may also be set by either the mono or poly
constructors. The default controllers are:  

<pre>
    :cc-vibrato - MIDI ctrl  1  - vibrato depth
    :cc-pedal   - MIDI ctrl  4  - 
    :cc-volume  - MIDI ctrl  7  -
    :cc-scanner - MIDI ctrl 92  - scanner effect mix
    :cc-reverb  - MIDI ctrl 93  - reverb effect mix
</pre>
