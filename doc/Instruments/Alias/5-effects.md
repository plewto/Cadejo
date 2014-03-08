Alias Effects  
=============  
  
For effects Alias provides a pitch shifter, flanger and duel delay
lines. As with the oscillator and filter sections key effects parameters
may be modulated by the control matrix. For lack of a better home the
effects section also includes a few miscellaneous parameters.  

##Common Parameters  

    (common :amp a :port-time pt :cc7->volume v)
    
    amp         - overall linear amplitude
    port-time   - portamento time in seconds
    cc7->volume - sets how much effect MIDI controller 7 (volume) has 
                  on overall Alias amplitude  
    
##Pitch Shifter  

    (pshifter amp :ratio [r src dpth] :rand rand :spread s)
    
    amp    - pitch shifter mix amplitude in db.
    r      - pitch shift ratio between 0 and 4 inclusive
    src    - ratio modulation source, may be anyone of the following 
             :a :b :c :d :e :f :g :h :con or :off
    dpth   - ratio modulation depth
    rand   - amount of ratio randomization between 0 and 1
    spread - amount of time randomization between 0 and 1
    
##Flanger  

The flanger consist of separate left and right delay lines for a pseudo
stereo effect. Delay time modulation is either by an internal LFO and/or 
one of the modulation buses (a..g). Parameters apply to both delay lines
simultaneously but either a phase shift (for internal LFO) or a lag
processor is applied to one of the channels for separate left/right
flanging.  

    (flanger amp :lfo [r d] :mod [src dpth] :fb fb :xmix xmix)
    
    amp  - flanger mix amplitude in db.
    r    - internal LFO rate
    d    - internal LFO modulation depth 
    src  - external modulation source, may be one of the general matrix
           buses :a :b :c :d :e :f :g :con or :off
    dpth - external modulation depth
    fb   - feedback between -1 and +1 inclusive
    xmix - stereo crossmix, 0 for complete l/r separation 1 for mono

##Delay Lines  

Alias provides two independent delay lines with identical properties.  

    (echo1 amp :delay [dly src dpth] :fb fb :damp damp :gate [gsrc gdpth] :pan p)
    (echo2 amp :delay [dly src dpth] :fb fb :damp damp :gate [gsrc gdpth] :pan p)
    
    amp   - echo mix amplitude in db
    dly   - fixed delay time, maximum delay time is 2 seconds.
    src   - delay time modulation source, may be any of the following 
            :a :b :c :d :e :f :g :h :con or :off
    depth - delay modulation depth
    fb    - feedback level between -1.0 and +1.0
    damp  - sets amount of high-frequency dampening of feedback signal with a 
            range between 0.0 and 1.0 inclusive. 1.0 sets maximum dampening. 
    gsrc  - amplitude modulation source, may be any of the following 
            :a :b :c :d :e :f :g :h :con or :off
    gdpth - amplitude modulation depth
    p     - pan position between -1.0 and +1.0 inclusive.


##Dry Signal Level

All effects are applied in parallel  

    (dry amp)
    
    amp - sets mix amplitude of dry (non-effected) signal in db.
