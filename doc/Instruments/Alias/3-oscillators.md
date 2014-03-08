Alias Oscillators
=================

For signal sources Alias uses 3 audio oscillators and a noise source. The
parameters available for each oscillator are identical although each
oscillator produces a unique waveform. osc1 produces a synced
sawtooth. osc2 generates variable width pulse waves. osc3 use FM feedback
and generates a signals between a sine and saw shapes and may also serve as
a second noise source. The primary noise source uses the "crackle" ugen and
has integrated low and high pass filters. A ring-modulator is also
provided.  


##Oscillator Frequency Control 

    (osc1-freq detune :bias b :fm1 [src1 dpth1 lag1] :fm2 [src2 dpth2 lag2])
    (osc2-freq detune :bias b :fm1 [src1 dpth1 lag1] :fm2 [src2 dpth2 lag2])
    (osc3-freq detune :bias b :fm1 [src1 dpth1 lag1] :fm2 [src2 dpth2 lag2])
    
    detune - Ratio of osc frequency to note frequency
    bias   - Fixed value added to frequency. The bias value is added after 
             scaling by detune. If detune is 0 the oscillator produces a
             fixed frequency determined by bias. 
    src1   - Modulation bus 1 source. Valid values may be one of 
             :a :b :c :d :e :f :g :h or :con
    dpth1  - Modulation bus 1 depth.
    lag1   - Modulation bus 1 lag time
    src2   - Modulation bus 2 source
    dpth2  - Modulation bus 2 depth
    lag2   - Modulation bus 2 lag time
    

##Oscillator Waveshape Control  
  
    (osc1-wave wave :w1 [src1 dpth1] :w2 [src1 dpth1])
    (osc2-wave wave :w1 [src1 dpth1] :w2 [src1 dpth1])
    (osc3-wave wave :w1 [src1 dpth1] :w2 [src1 dpth1])
    
    wave  - Waveshape
    src1  - Waveshape modulation source 1, valid values are 
            :a :b :c :d :e :f :g :h or :con
    dpth1 - Waveshape modulation 1 depth
    src2  - Waveshape modulation source 2
    dpth2 - Waveshape modulation 2 depth.
    

For __osc1__ the Waveshape parameters set the relative frequency of the sync
signal. The sync frequency is automatically limited to be greater then or
equal to 1.  

The __osc2__ waveshape parameters sets the pulse width. The combined values
are automatically limited to a range just above 0 and just below 1. A pulse
width of 0.5 produces a square wave.  

The __osc3__ waveshape parameters sets the level of FM feedback and should
be non-negative with no limit on upper values. A waveshape value of 0
generates a sine wave with increasing values producing a more saw like
wave. At some point around 2 the oscillator becomes chaotic and starts
producing noise.  

##Oscillator amplitude Control  

    (osc1-amp amp :pan p :am1 [src1 dpth1 lag1] :am2 [src2 dpth2 lag2])
    (osc2-amp amp :pan p :am1 [src1 dpth1 lag1] :am2 [src2 dpth2 lag2])
    (osc3-amp amp :pan p :am1 [src1 dpth1 lag1] :am2 [src2 dpth2 lag2])
    
    amp   - Oscillator amplitude in db.
    pan   - Oscillator pan position sets the relative amounts sent to either 
            of the 2 filters. A pan value of -1 sends all output to filter 1.
            A pan value of +1 sends all output to filter2. Intermediate values 
            send output proportionally to both filters.
    src1  - Amplitude modulation source may be any one of 
            :a :b :c :d :e :f :g :h :con or :off
    dpth1 - Modulation depth of src1.
    lag1  - Lag time for modulation source 1.
    src2  - Amplitude modulation source 2
    dpth2 - Modulation depth 2
    lag2  - Modulation Lag time 2
    

##Noise Source  

The noise source is provided by the crackle ugen and may produce either a
smooth or rough signal.  

    (noise amp :pan p :crackle c :lp lp :hp hp :am1 [src1 dpth1 lag1] :am2 [src2 dpth2 lag2])
    
    amp     - Noise amplitude in db
    pan     - Noise pan selects filter path
    crackle - Amount of 'cracle' in signal, valid range is 0.0 for no crackle, 1 for maximum crackle.
    lp      - Lowpass filter cutoff in Hertz.
    hp      - Highpass filter cutoff in Hertz.
    src1    - Amplitude modulation source 1. 
              :a :b :c :d :e :f :g or :con
    dpth1   - Amplitude modulation depth 1
    lag1    - Amplitude modulation lag time 1
    src2    - Amplitude modulation source 2
    dpth2   - Amplitude modulation depth 2
    lag2    - Amplitude modulation 2 lag time.	   
    

##Ring Modulator  

    (ringmod amp :pan p :carrier c :modulator m :am1 [src1 dpth1 lag1] :am2 [src2 dpth2 lag2])
    
    amp       - Ringmodulator amplitude in db.
    pan       - Selects filter path.
    carrier   - Selects carrier input source. -1.0 selects osc1 while +1.0 selects osc2 
    modulator - Selects modulator input source, -1.0 selects osc3 while +1.0 selects the noise source.
    src1      - Amplitude modulation source 1.	
                :a :b :c :d :e :f :g or :con
    dpth1   - Amplitude modulation depth 1
    lag1    - Amplitude modulation lag time 1
    src2    - Amplitude modulation source 2
    dpth2   - Amplitude modulation depth 2
    lag2    - Amplitude modulation 2 lag time.	   
