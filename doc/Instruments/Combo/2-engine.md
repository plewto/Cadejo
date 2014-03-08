cadejo.instruments.combo.engine
===============================  

combo.engine defines a simple SuperCollider/Overtone “combo” organ and the
structures necessary for using it within a Cadejo context. Instruments for
use with Cadejo are no different from any other Overtone instrument and may
be used separate from Cadejo. However a few additional steps are necessary to
prepare an instrument for use with Cadejo.

###synths  

Cadejo makes a distinction between *’synths’* and *’voices’*. The
difference between a ‘voice’ and a ‘synth’ is that a voice generates
signals in response to MIDI key events while a synth does not. Typically a
Cadejo synth is used in some supporting manner such generation of vibrato
or other control signals or as an effects processor. Form Overtones point
of view both 'synths' and 'voices' are SuperCollider synth objects.

Combo defines three synths using Overtone defsynth: *LFO*, *ToneBlock* and
*EfxBlock*.  As is standard, communication between these objects is via
either control or audio buses.  For any given instance of Combo there is
one each of the LFO and EfxBlock. The number of ToneBlocks is dependent on
the maximum number of simultaneous voices desired (the *polyphony*).

    000 : (println "--> combo engine")
    001 : 
    002 : (ns cadejo.instruments.combo.engine
    003 :   (:use [overtone.core])
    004 :   (:require [cadejo.modules.qugen :as qu])
    005 :   (:require [cadejo.instruments.combo.program])
    006 :   (:require [cadejo.instruments.combo.pp])
    007 :   (:require [cadejo.instruments.combo.data])
    008 :   (:require [cadejo.midi.mono-mode])
    009 :   (:require [cadejo.midi.poly-mode])
    010 :   (:require [cadejo.midi.performance]))  

##LFO  

The LFO provides global vibrato and has 2 user-level parameters,
*vibrato-freq* and *vibrato-sens*, which respectively set the frequency and
maximum vibrato depth. The actual vibrato depth is determined by the value
of the vibrato-depth-bus which by default is updated by the modulation
wheel (MIDI controller 1)

      013 : (defsynth LFO [vibrato-freq 5.00
      014 :                vibrato-sens 0.01
      015 :                vibrato-bus 0
      016 :                vibrato-depth-bus 0]
      017 :   (let [amp (* vibrato-sens
      018 :                (in:kr vibrato-depth-bus))]
      019 :     (out:kr vibrato-bus (+ 1 (* amp (sin-osc:kr vibrato-freq))))))
      020 :   

Lines 020-025 define filter-type constants.  

      021 : (def bypass-filter 0)
      022 : (def lp-filter 1)
      023 : (def hp-filter 2)
      024 : (def bp-filter 3)
      025 : (def br-filter 4)
      026 : 
      027 :   

Lines 028 - 046 define a few ‘utility’ cgens. These items are used by
ToneBlock to map user parameter values to some internal value.

	  028 : ;; (1.0 --> 0db)
	  029 : ;; (0.0 --> -60db)
	  030 : ;;
	  031 : (defcgen amp-scale [amp]
	  032 :   (:ir (dbamp (- (* 36 (qu/clamp amp 0 1)) 36))))
	  033 : 
	  034 : 
	  035 : ;; (0.0 --> 0.5)
	  036 : ;; (1.0 --> 0.1)
	  037 : ;;
	  038 : (defcgen pulse-width [wave]
	  039 :   (:ir (+ (* -0.4 (qu/clamp wave 0 1)) 0.5)))
	  040 : 
	  041 : ;; (0.0 --> 0)
	  042 : ;; (1.0 --> 1.5)
	  043 : ;;
	  044 : (defcgen feedback [wave]
	  045 :   (:ir (* 1.5 (qu/clamp wave 0 1))))
	  046 :  


Lines 047 - 086 define the ToneBlock synth. ToneBlock is the heart of
Combo and Cadejo uses it as a ‘voice’. There are a few things to note about
the parameters to ToneBlock. Upon reception of a MIDI key-down event Cadejo
passes the following values to the active voice(s):

* :freq - The tones frequency in Hertz.  
* :note - The MIDI note number.  
* :gate - The gate parameter is set to 1, triggering any envelopes.  
* :velocity - The MIDI velocity (*normalized* to a value between 0.0 and 1.0).  

On a key-up event Cadejo sets the gate parameter to 0, releasing the
envelopes. In mono mode the freq parameter may also be updated if there are
previous notes still being held.

It may appear redundant to send both a :freq and :note parameter however
the two are not equivalent. The value of :note is simply the MIDI key
number of the key-down event. On the other hand Cadejo derives the
frequency by looking note up in a tuning-table. The default tuning-table
happens to map the MIDI note number to the frequency it normally produces
but the frequency may in fact be anything. Instruments are free to use or
ignore either freq or note as is appropriate. The ALGO instrument makes use
of both, freq for the tones frequency and note for amplitude
key-scaling. Combo only uses the :freq parameter.


	  047 : (defsynth ToneBlock [freq 100
	  048 :                      gate 0
	  049 :                      amp1  1.00
	  050 :                      wave1 0.00
	  051 :                      amp2  1.00
	  052 :                      wave2 0.00
	  053 :                      amp3  1.00
	  054 :                      wave3 0.00
	  055 :                      amp4  1.00
	  056 :                      wave4 0.00
	  057 :                      chorus 0.00
	  058 :                      filter 8.00
	  059 :                      filter-type lp-filter
	  060 :                      bend-bus 0
	  061 :                      vibrato-bus 0
	  062 :                      out-bus 0]
	  063 :   (let [vibrato (in:kr vibrato-bus) 
	  064 :         dt (+ 1 (* 1/200 (qu/clamp chorus 0.0 1.0)))
	  065 :         f0 (* freq (in:kr bend-bus))
	  066 :         f1 (* f0 vibrato)
	  067 :         f2 (* 2 dt f0 vibrato)
	  068 :         f3 (* 3 dt dt f0 vibrato)
	  069 :         f4 (* 4 dt dt dt f0 vibrato)
	  070 :         w1 (* (amp-scale amp1)
	  071 :               (pulse:ar f1 (pulse-width wave1)))
	  072 :         w2 (* (amp-scale amp2)
	  073 :               (pulse:ar f2 (pulse-width wave2)))
	  074 :         w3 (* (amp-scale amp3)
	  075 :               (sin-osc-fb:ar f3 (feedback wave3)))
	  076 :         w4 (* (amp-scale amp4)
	  077 :               (sin-osc-fb:ar f4 (feedback wave4)))
	  078 :         mixer (+ w1 w2 w3 w4)
	  079 :         filter-freq (* f0 filter)
	  080 :         rq 0.5
	  081 :         filter-out (select filter-type [mixer 
	  082 :                                         (lpf mixer filter-freq)
	  083 :                                         (hpf mixer filter-freq)
	  084 :                                         (* 2 (bpf mixer filter-freq rq))
	  085 :                                         (brf mixer filter-freq rq)])]
	  086 :     (out:ar out-bus (* gate filter-out))))  

As detailed in the combo.data document Combo uses 4 oscillators each with
an amplitude and waveform parameter. These 4 signals are mixed and then
sent through a multi-mode filter.  

The three xxx-bus parameters set the expected control and audio buses. Note
that all the buses default to 0 and each *must* be set to an appropriate
value when the instrument is constructed.

Lines 088 - 123 define the effects synth. EfxBlock provides flanger, reverb
and general amplitude control. Note there are 2 amplitude related
parameters. :amp sets the linear amplitude and is used in the program data
file to balance the output of one Combo program against another. The
:dbscale parameter is *not* used by the data file but by Cadejo to set the
relative mix of two or more instruments within key splits or layer.  

One final note about parameters. Both LFO and EfxBlock have a parameter
called *something*-rate. For the LFO this is ‘vibrato-rate’ for EfxBlock it
is ‘flanger-rate’ All synths used in a Cadejo instrument must have unique
parameter names. If there were a second LFO it would have to use some other
name for its rate parameter. This is unfortunate because it means the
synthdef for LFO can not be reused in the same instrument. The reason this
is so is because on reception of a MIDI program change a flat list of *all*
parameters is sent to all active synths. Without unique parameter names
there would be no way to distinguish the vibrato rate from the flanger
rate.

	088 : (def flanger-max-delay 1/100)
	089 : (def flanger-delay (* 1/2 flanger-max-delay))
	090 : 
	091 : (defsynth EfxBlock [amp 0.2
	092 :                     dbscale 0
	093 :                     flanger-depth 0.25
	094 :                     flanger-rate 0.25
	095 :                     flanger-fb -0.5
	096 :                     flanger-mix 0
	097 :                     reverb-mix 0
	098 :                     in-bus 0
	099 :                     out-bus 0]
	100 :   (let [drysig (in:ar in-bus)
	101 :         fbsig (* (qu/clamp flanger-fb -0.98 +0.98) 
	102 :                  (local-in:ar 2))
	103 :         lfo-amp (* flanger-delay 
	104 :                    (qu/clamp flanger-depth 0 1))
	105 :         lfo1 (* lfo-amp (sin-osc:kr flanger-rate))
	106 :         lfo2 (* -1 lfo1)
	107 :         flanger1 (delay-c:ar (+ drysig (nth fbsig 0))
	108 :                              flanger-max-delay
	109 :                              (+ flanger-delay lfo1))
	110 :         flanger2 (delay-c:ar (+ drysig (nth fbsig 1))
	111 :                              flanger-max-delay
	112 :                              (+ flanger-delay lfo2))
	113 :         flanger (qu/efx-mixer [drysig drysig]
	114 :                               [flanger1 flanger2]
	115 :                               (* 1/2 (qu/clamp flanger-mix 0 1)))
	116 :         room-size 0.8
	117 :         damp 0.5
	118 :         reverb (free-verb flanger 
	119 :                           (* 0.5 (qu/clamp reverb-mix 0 1))
	120 :                           room-size damp)
	121 :         gain (* amp (dbamp dbscale))]
	122 :     (local-out:ar [flanger1 flanger2])
	123 :     (out:ar out-bus (* gain reverb))))
	124 :  

###Cadejo Interface  

The remaining code provides the interface for using Combo  within
Cadejo. At a minimum constructor functions should be provided for each
keymode the instrument is to utilize. Currently Cadejo implements 2
keymodes: *mono* and *poly*. By convention the instrument constructor
functions are named *iname-km* where iname is the instrument's name and km
is the keymode.  The functions *combo-mono* and *combo-poly* create
instances of Combo for the indicated mono or poly keymode.  

####create-performance  

Lines 125-136 define the *create-performance* function. create-performance
is boiler-plate for building a Cadejo Performance object which is needed by
both combo-mono and combo-poly.  

     125 : (defn ^:private create-performance [chanobj keymode cc1]
     126 :   (.add-controller! chanobj cc1 0.0 1.0 0.0)
     127 :   (let [performance (cadejo.midi.performance/performance chanobj :Combo keymode)
     128 :         vibrato-bus (control-bus)
     129 :         tone-bus (audio-bus)
     130 :         bank cadejo.instruments.combo.program/bank]
     131 :     (.set-pp-hook! bank cadejo.instruments.combo.pp/pp-combo)
     132 :     (.set-bank! performance bank)
     133 :     (.add-control-bus! performance :vibrato vibrato-bus)
     134 :     (.add-audio-bus! performance :tone tone-bus)
     135 :     performance))  

Line 125 The arguments to create-performance are:  

* chanobj -  An instance of cadejo.midi.channel/Channel  
* keymode -  An object implementing cadejo.midi.keymode/Keymode  
* cc1 - Integer, the MIDI controller number used for vibrato-depth (default 1)   

Line 126  (.add-controller! chanobj ctrlnum y0 y1 ivalue)  

* chanobj -  
* ctrlnum - Integer, MIDI controller number 0 <= ctrlnum <= 127.  
* y0 and y1 - Together y0 and y1 set the output values for the minimum and
  maximum controller positions. Behind the scenes these two values define a
  linear function through the points (0,y0) and (127,y1). It is also
  possible to set the mapping function directly if more interesting curves
  are desired, see cadejo.midi.channel.  
* ivalue - Sets the initial output value. ivalue is also the value the
  output reverts to on a reset.  

add-controller! automatically creates a SuperCollider control-bus which is
available under the key ctrlnum via the control-bus method of Performance.  

Lines 127-129 Creates the Performance object and any additional required
buses.  

Line 130 Assigns a convenient label for the program bank.  

Line 131 Sets the 'pretty-printer' hook for the program bank. Bank objects
have two hook functions which are executed whenever a program change
occurs. The 'notification-hook' is intended to print a short message
indicating what program is currently being used. For the most part the
default notification-hook is adequate and is not altered by Combo. The
second hook is the 'pp-hook' (for 'pretty-printer').  The pp-hook is
intended to display the actual program data in use after a
program-change. Either hook function may display anything but the intent of
the pp hook is to display the data in both a human and machine readable
form. The output of the pp hook should exactly match (or at least be very
close to) the format used to define programs in the data file. This feature
allows any interesting patches generated randomly to be cut and pasted into
the data file.  
   

Line 132 Includes the bank object in the performance.  

Lines 133-134 Registers the control and audio buses with the
performance. These may later be extracted using their keys :vibrato and
:tone.   

####combo-mono  

Lines 137 - 160 define the monophonic Combo constructor  

      137 : (defn combo-mono 
      138 :   ([scene chan main-out & {:keys [cc1]
      139 :                            :or {cc1 1}}]
      140 :      (let [chanobj (.channel scene chan)
      141 :            keymode (cadejo.midi.mono-mode/mono-keymode :Combo)
      142 :            performance (create-performance chanobj keymode cc1)
      143 :            vibrato-bus (.control-bus performance :vibrato)
      144 :            vibrato-depth-bus (.control-bus performance cc1)
      145 :            bend-bus (.control-bus performance :bend)
      146 :            tone-bus (.audio-bus performance :tone)
      147 :            lfo (LFO :vibrato-bus vibrato-bus
      148 :                     :vibrato-depth-bus vibrato-depth-bus)
      149 :            voice (ToneBlock :bend-bus bend-bus
      150 :                             :vibrato-bus vibrato-bus
      151 :                             :out-bus tone-bus)
      152 :            efx (EfxBlock :in-bus tone-bus
      153 :                          :out-bus main-out)]
      154 :        (.add-synth! performance :lfo lfo)
      155 :        (.add-synth! performance :efx efx)
      156 :        (.add-voice! performance voice)
      157 :        (Thread/sleep 100)
      158 :        performance))
      159 :   ([scene chan]
      160 :      (combo-mono scene chan 0)))  

Lines 138-139 and 159 The arguments to combo-mono are:  
* scene - An instance of cadejo.midi.scene/Scene.  
* chan - Integer, The MIDI channel number within scene to use. Within
  Cadejo MIDI channel numbers are indexed from 0. 0 <= chan <= 15.  
* main-out - Integer, optional audio output bus. Combo outputs a pseudo
  stereo signal so 2 buses are actually used. Default 0.  
* :cc1 - Integer, optional MIDI controller number for vibrato depth,
  default 1.  

Lines 140 - 142 Create the Performance object.  

lines 143 - 146 Retrieve the control and audio buses from the
performance. Note that vibrato-depth-bus was created automatically by line
126 and assigned the key cc1. All Performance objects automatically create
pitch-bend and channel-pressure control buses with the keys :bend and
:pressure respectively.  

Lines 147 - 153 Create the synth and voice objects lfo, tone and efx. The
order of creation is important. Briefly synths with provide input to other
synths must be created first. As the lfo generates the vibrato signal for
tone, lfo must be created first. Likewise since tone sends audio to efx,
tone must be created first.  

Lines 154 - 155 Register the 2 synths with the performance. Synth objects
are assigned a unique key in the performance.  

Line 156 Register the 'voice' object with the performance. Voices are not
assigned a key, in some ways they are anonymous. They may however be
retrieved from the performance using a numeric index.  

Line 157 Adds a 100ms delay before combo-mono returns. The reason this is
necessary is not exactly clear (See BUG 0001). The guess is that Clojure at
times executes code faster then the SuperCollider server can keep
pace. Without the delay some synths appear to have been created yet they
are broken and non-functional. This happens particularly with more complex
synthdefs and seemingly at random. In this particular case the delay is not
intended to protect the Combo instrument but any other instruments which
may be created immediately after it.  


####combo-poly

Lines 163 - 187 define the polyphonic combo constructor.  

      163 : (defn combo-poly
      164 :   ([scene chan voice-count main-out & {:keys [cc1]
      165 :                                        :or {cc1 1}}]
      166 :      (let [chanobj (.channel scene chan)
      167 :            keymode (cadejo.midi.poly-mode/poly-keymode :Combo voice-count)
      168 :            performance (create-performance chanobj keymode cc1)
      169 :            vibrato-bus (.control-bus performance :vibrato)
      170 :            vibrato-depth-bus (.control-bus performance cc1)
      171 :            bend-bus (.control-bus performance :bend)
      172 :            tone-bus (.audio-bus performance :tone)
      173 :            lfo (LFO :vibrato-bus vibrato-bus
      174 :                     :vibrato-depth-bus vibrato-depth-bus)]
      175 :        (dotimes [i voice-count]
      176 :          (let [v (ToneBlock :bend-bus bend-bus
      177 :                             :vibrato-bus vibrato-bus
      178 :                             :out-bus tone-bus)]
      179 :            (.add-voice! performance v)
      180 :            (Thread/sleep 10)))
      181 :        (let [efx (EfxBlock :in-bus tone-bus
      182 :                            :out-bus main-out)]
      183 :          (.add-synth! performance :lfo lfo)
      184 :          (.add-synth! performance :efx efx)
      185 :          performance)))
      186 :   ([scene chan]
      187 :      (combo-poly scene chan 8 0)))

Lines 164-165 and 186 The arguments to combo-poly are:  
* scene - An instance of cadejo.midi.scene/Scene.  
* chan - Integer, MIDI channel number within scene (indexed from 0).  
* voice-count - Integer, maximum number of simultaneous notes, default 8.  
* main-out - Integer, optional audio output bus. Combo outputs a pseudo
  stereo signal so 2 buses are actually used. Default 0.  
* :cc1 - Integer, optional MIDI controller number for vibrato depth,
  default 1.    

combo-poly is almost identical to combo-mono. The major difference is that
lines 149-151 in combo-mono are replaced by a loop (lines 175-180) in
combo-poly. The loop is responsible for creating the multiple instance of
ToneBlock needed for polyphonic playback. Note that line 180 places the
Thread/sleep clause inside the loop instead of at the end of the function.  