cadejo.instruments.combo.program  
================================  

combo.program provides a program-bank together with functions for the
creation of Combo programs and for saving them into the bank.   

	 000 : (println "\t--> program")
	 001 : 
	 002 : (ns cadejo.instruments.combo.program
	 003 :   (:require [cadejo.midi.program])
	 004 :   (:require [cadejo.util.col :as col])
	 005 :   (:require [cadejo.util.user-message :as umsg]))
	 006 :
	 007 :  

Lines 080-010 Creates an initially empty bank object.  

	 008 : (defonce bank (cadejo.midi.program/bank 
	 009 :                :Combo "Default Combo Bank"))
	 010 :  

Lines 011-016 Defines the save-program function which stores program data
into the bank. The arguments are:  
* pnum - Integer, The MIDI program number. 0<=pnum<=127.  
* name - String, The programs name.  
* remarks - String, Optional remarks text.  
* data - List, The program data.  data may either be in the form of a
literal association-list of parameter key/value pairs or a function. See
documentation for genpatch and data.  

	 011 : (defn save-program 
	 012 :   ([pnum name remarks data]
	 013 :      (.set-program! bank pnum name remarks data))
	 014 :   ([pnum name data]
	 015 :      (save-program pnum name "" data)))
	 016 : 
	 017 :  

Lines 018-040 define functions for validating program data. These are
somewhat optional and mostly used during development to ensure that
program-data both contains entries for all expected values and also does
not contain extra unrecognized parameters. Once the various program
functions are tested these functions can probably be removed.  

	 018 : (def combo-parameters 
	 019 :   '[:vibrato-freq :vibrato-sens :amp1 :wave1 :amp2 :wave2 :amp3 :wave3
	 020 :     :amp4 :wave4 :chorus :filter :filter-type :amp :flanger-depth
	 021 :     :flanger-rate :flanger-fb :flanger-mix :reverb-mix])
	 022 : 
	 023 : (defn ^:private continuity-test [data]
	 024 :   (let [keys (col/alist->keys data)
	 025 :         rs* (atom true)]
	 026 :     (doseq [p combo-parameters]
	 027 :       (if (col/not-member? p keys)
	 028 :         (do 
	 029 :           (umsg/warning (format "Combo %s parameter missing" p))
	 030 :           (swap! rs* (fn [n] false)))))
	 031 :     @rs*))
	 032 : 
	 033 : (defn ^:private spurious-parameter-test [data]
	 034 :   (let [rs* (atom true)]
	 035 :     (doseq [k (col/alist->keys data)]
	 036 :       (if (col/not-member? k combo-parameters)
	 037 :         (do
	 038 :           (umsg/warning (format "Unrecognized Combo parameter %s" k))
	 039 :           (swap! rs* (fn [n] false)))))
	 040 :     @rs*))
	 041 :  

lines 042-047 define filter constants.  

	 042 : (def bypass 0)
	 043 : (def low 1)
	 044 : (def high 2)
	 045 : (def band 3)
	 046 : (def notch 4)
	 047 : 

Lines 048-069  

These private functions are used to simplify the format used by the data
file. As a typical example the combo-vibrato function takes two optional
keyword arguments :freq and :sens and converts them to :vibrato-freq and
:vibrato-sensitivity. The combo-filter and combo-flanger functions performs
similar functions for their domains.  


	 048 : (defn ^:private combo-vibrato [& {:keys [freq sens]
	 049 :                                   :or {freq 5.00
	 050 :                                        sens 0.01}}]
	 051 :   (list :vibrato-freq (float freq)
	 052 :         :vibrato-sens (float sens)))
	 053 : 
	 054 : (defn ^:private combo-filter [& {:keys [freq type]
	 055 :                                  :or {freq 8
	 056 :                                       type bypass}}]
	 057 :   (list :filter (int freq)
	 058 :         :filter-type (int type)))
	 059 : 
	 060 : (defn ^:private combo-flanger [& {:keys [rate depth fb mix]
	 061 :                                   :or {rate 0.25
	 062 :                                        depth 0.25
	 063 :                                        fb 0.50
	 064 :                                        mix 0.0}}]
	 065 :   (list :flanger-depth (float depth)
	 066 :         :flanger-rate (float rate)
	 067 :         :flanger-fb (float fb)
	 068 :         :flanger-mix (float mix)))
	 069 :   

Lines 070-102  

The combo function takes several keyword arguments and aggregates
them into an association-list suitable for a Combo program. All arguments
to combo are optional and default to sensible values. See documentation for
combo.data for details.  

	 070 : (defn combo [& {:keys [a1 a2 a3 a4 w1 w2 w3 w4 chorus amp reverb
	 071 :                        vibrato filter flanger]
	 072 :                 :or {a1 1.00
	 073 :                      a2 0.70
	 074 :                      a3 0.00
	 075 :                      a4 0.00
	 076 :                      w1 0.00
	 077 :                      w2 0.00
	 078 :                      w3 0.00
	 079 :                      w4 0.00
	 080 :                      chorus 0.0
	 081 :                      amp 0.20
	 082 :                      reverb 0.0
	 083 :                      vibrato [:freq 5.0 :sens 0.010]
	 084 :                      filter [:freq 8 :type bypass]
	 085 :                      flanger [:rate 0.25 :depth 0.25 :fb 0.50 :mix 0.00]}}]
	 086 :   (let [data (flatten (list :amp1 (float a1)
	 087 :                             :amp2 (float a2)
	 088 :                             :amp3 (float a3)
	 089 :                             :amp4 (float a4)
	 090 :                             :wave1 (float w1)
	 091 :                             :wave2 (float w2)
	 092 :                             :wave3 (float w3)
	 093 :                             :wave4 (float w4)
	 094 :                             :chorus (float chorus)
	 095 :                             :amp (float amp)
	 096 :                             :reverb-mix (float reverb)
	 097 :                             (apply #'combo-vibrato vibrato)
	 098 :                             (apply #'combo-filter filter)
	 099 :                             (apply #'combo-flanger flanger)))]
	 100 :     (continuity-test data)
	 101 :     (spurious-parameter-test data)
	 102 :     data))

