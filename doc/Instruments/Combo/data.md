cadejo.combo.data
=================

cadejo.combo.data defines the contents of the Combo program bank. Most of
the functions and objects in data are imported from cadejo.combo.program.

    000 : (println "\t--> data")
    001 : 
    002 : (ns cadejo.instruments.combo.data
    003 :   (:use [cadejo.instruments.combo.program 
    004 :          :only [bank combo save-program bypass low high band notch]])
    005 :   (:require [cadejo.instruments.combo.genpatch]))
    006   

###save-program  

The save-program function places program (or ‘patch’) data into the program
bank with optional remarks text.

     (save-program pnum name rem data)
     (save-program pnum name data)  

__pnum__ - MIDI program number 0 <= pnum <= 127  
__name__ - String, the program name.  
__rem__ - String, optional remarks text.  
__data__ - Program data.  

Data is always a list of one of two forms. It is either an ‘association-list’  

     (:param-0 value-0 :param-1 value-1 … :param-n value-n)  

or a function with optional arguments. 

    (fn optional-args….)  

Upon reception of a MIDI program change Cadejo pulls the data from the bank
and updates *all* synths associated with the instrument. If the program
data is a function then that function is called with any optional arguments
and the result used to update the synth parameters. The function should
accept an arbitrary number of arguments and return an association list
suitable as literal data for the instrument. The primary reason for
supporting functions as data is to allow random patch generators, though
other uses likely exists.

Lines 008 and 009 stores such a function to program slot 0.  
  
      008 : (save-program 0 "Random" "Generate random Combo program"  
      009 :               (list cadejo.instruments.combo.genpatch/gen-combo))  

Lines 011 - 020 define a program named “Bright” using the
cadejo.instruments.combo.program/combo function. The arguments to combo
are:

__:a1 :a2 :a3 :a4__ - Oscillator amplitudes 0.0 <= a <=1.0   
__:w1 :w2 :w3 :w4__ - Oscillator wave shape 0.0 <= w <= 1.0  
__:chorus__ - Oscillator detuning for chorusing effect. 0.0 <= chorus <= 1.0

__:vibrato [:freq vf :sens vs]__  
* :freq vf- Vibrato frequency in Hertz.  
* :sens vs - Maximum vibrato depth as ratio of fundamental.  

__:filter [:freq ff :type ft]__    
* :freq ff - Filter frequency relative to fundamental. Note filter frequency 
  is an integer and may also be considered harmonic number.  
* :ftype ft - Filter type, valid values are *bypass, low, high, band* and *notch*.  

__:flanger [:rate fr :depth fd :fb fb :mix x]__  
* :rate - Flanger rate in Hertz.  
* :depth - Flanger modulation depth, 0.0 <= depth <= 1.0  
* :fb - Flanger feedback. -1.0 <= fb <= +1.0  
* :mix - Flanger mix level 0.0 (dry) <= mix <= 1.0 (50% wet).  

__reverb__ - Reverb  mix 0.0 (dry) <= mix <= 1.0 (50% wet).  
__amp__ - Overall linear amplitude.  

	011 : (save-program 1 "Bright"
	012 :    (combo :a1      1.000  :w1 0.000
	013 :           :a2      0.800  :w2 0.000
	014 :           :a3      0.000  :w3 0.000
	015 :           :a4      0.000  :w4 0.000
	016 :           :chorus  0.000
	017 :           :vibrato [:freq 5.000 :sens 0.010]
	018 :           :filter  [:freq  8    :type bypass]
	019 :           :flanger [:rate 0.250 :depth 0.250 :fb +0.50 :mix 0.000]
	020 :           :reverb  0.000  :amp 0.200))  

Lines 021-176 define additional programs and are not shown.  

Line 177 uses the bank dump method to display a list of the banks content.  

    177 : (.dump bank)  

Line 178 defines an optional “reload” function intended as a convenient way
to interactively reload the data file.

    178 : (defn rl [](use 'cadejo.instruments.combo.data :reload))

