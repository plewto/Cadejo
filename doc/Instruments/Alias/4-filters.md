Alias Filters  
=============  

Alias provides 2 filter paths each with an integrated waveshapers or
distortion circuit. The parameters available for these two paths are nearly
identical however each path has a unique design. The filter1 waveshaper
uses simple bi-lateral clipping via the excess ugen and the filter proper
is multi-modal with low, high, band and a combination low*high modes. The
filter2 waveshaper uses folding which may produce more drastic effects then
that if filter1. Filter2 proper is a simulated Moog lowpass filter.  


#Filter Path 1  

##Clipper

    (clip :gain g :mix m :clip [n src dpth])
    
    gain - Sets the signal gain entering the waveshaper. Greater gain values 
           produce higher levels of clipping.
    mix  - Sets relative wet/dry mix. A mix value of -1.0 bypasses the clipping circuit.
    n    - Sets the clipping level between 0 and 1, higher values produce 
           greater levels of distortion and output amplitude is automatically compensated.
    src  - Modulation source for clipping threshold may be any one of 
           :a :b :c :d :e :f :g or :con
    dpth - Clipping level modulation depth.

##Filter1 

    (filter1 :freq f :mode m :gain g :fm1 [src1 dpth1] :fm2 [src2 dpth2] :res [r rsrc rdepth] :pan [p psrc pdpth])
    
    freq   - filter frequency in Hertz
    mode   - filter mode as a float between 0.0 and 1.0 
             0.00 - lowpass
             0.25 - lowpass*highpass
             0.50 - highpass
             0.75 - bandpass
             1.00 - bypass
             Intermediate values mix proportionally. Filter mode may also be set using the symbolic keywords :lp :lp*hp :hp :bp and :bypass
    gain   - makeup gain applied after filter stage. 
    src1   - frequency modulation source 1, valid values are :a :b :c :d :e :f :g :con and :off
    dpth1  - modulation depth of src1 in Hertz.
    src2   - frequency modulation source 2
    dpth2  - modulation depth of source src2
    r      - resonance between 0.0 and 1.0
    rsrc   - resonance modulation source
    rdepth - resonance modulation depth
    p      - pan position between -1 and +1
    psrc   - pan modulation source
    pdpth  - pan modulation depth.

#Filter Path 2  

Filter path 2 has the same parameter set as filter path 1 except that
filter mode is missing. The path 2 distortion circuit uses folding instead
of clipping.  

    (fold    :gain g :mix m :clip [c src dpth])  
    
    (filter2 :freq f :gain g :fm1 [src1 dpth1] :fm2 [src2 dpth2] :res [r rsrc rdepth] :pan [p psrc pdpth])
    



