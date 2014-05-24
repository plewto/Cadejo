Combo Organ  
===========  


**NOTE Combo documentation is out of date, particularly in reference to
  program banks and patch pretty-printer hook functions**

Combo is a simple organ simulation implemented in Overtone. It is intended
to provide a useful instrument and also to serve as an illustration for
using Overtone instruments with Cadejo. For illustration purposes heavily
annotated versions of all combo source files are included in this
documentation. My apologies if these are occasionally pedantic. The files
which define Combo are:  

    * engine.clj   - Defines the Overtone synthdefs and the interface to Cadejo.  
    * program.clj  - Provides a program bank and functions for creating Combo 
                     programs (patches?)  
    * data.clj     - Populates the program bank with actual Combo programs.  
    * genpatch.clj - An optional feature used to generate random Combo programs.  
    * pp.clj       - An optional 'pretty-printer' for Combo program data.   

  
###Voice Architecture  

Combo uses 4 oscillators with a fixed frequency ratio of
1:2:3:4. Oscillators 1 and 2 generate pulse waves with widths between 1/2
to 1/10.  Oscillators 3 and 4 use FM feedback for either sine waves or pseudo
sawtooth waves. Each oscillator has an an associated amplitude and wave
control. A chorus parameter stretches the relative frequency ratios form
perfect to something just short of dissonance for a chorusing effect.  

The mixed oscillator signal is then fed to a multi-mode filter for
further timbre control. The filter operates in one of 5 modes: *bypass,
low-pass, high-pass, band-pass and notch*. The band-pass and notch filters
have a fixed q. Combo does not use envelope generates per se but rather
uses the gate signal to directly gate or "key" the signal after coming out
of the filter.   

Vibrato depth is controlled by the MIDI modulation wheel (by default) and
MIDI pitch bend is also detected.  

After the filter the signal is sent to a separate effects section where a
flanger converts the mono signal into pseudo-stereo and reverb is applied.  

Combo is purposely kept simple with relatively few options. It is hoped that
it none the less provides a useful instrument. For a more complete organ
simulation see MASA.   
