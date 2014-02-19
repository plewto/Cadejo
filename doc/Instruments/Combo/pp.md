cadejo.instruments.combo.pp  
===========================  

pp-hook is an optional feature of cadejo.midi.program/Bank and is intended to display
current patch data in response to a MIDI program-change. The format of the
pp output should closely match that used by the program data file and should
be both human and machine readable. The intent is to allow cut and
paste of any interesting randomly generated programs into the data file.    

The pp-hook function should take three arguments: *event, pid and data*.  

* event - The SuperCollider event which generated the program change. event
  may be used to extract the MIDI channel and program numbers.  

* pid - The name of the program as stored in the program bank.  

* data - A flat 'association-list' of program data. The data will have the
  form  

  (:param-0 value-0 :param-1 value-1 ... :param-n value-n)  

Nothing should be assumed about the order of the parameter/value
pairs. Also note that the parameter names will be those used to define the
various synth objects, these may or may not match the parameters used by the
data file. Some mapping between the two sets may be required.  

In true functional programming sacrilege the pp hook function is used solely
for its side effects and its output ignored.  

<pre>
(ns cadejo.instruments.combo.pp
  (:require [cadejo.instruments.combo.program :as program])
  (:require [cadejo.util.col :as col]))

(defn pp-combo [event pid data]
  (let [pad1 "    "
        pad2 (str pad1 "       ")
        sb (StringBuilder.)
        dmap (col/alist->map data)
        fget (fn [p]
               (float (get dmap p)))
        iget (fn [p] 
               (int (fget p)))
        app (fn [frmt & args]
              (.append sb (apply #'format (cons frmt args))))]
    (app ";; program %s name '%s'\n" (:data1 event) pid)
    (app "(save-program 127 \"<name>\"\n")
    (app "%s(combo :a1      %5.3f  :w1  %5.3f\n"
         pad1 (fget :amp1)(fget :wave1))
    (app "%s:a2      %5.3f  :w2 %5.3f\n"
         pad2 (fget :amp2)(fget :wave2))
    (app "%s:a3      %5.3f  :w3 %5.3f\n"
         pad2 (fget :amp3)(fget :wave3))
    (app "%s:a4      %5.3f  :w4 %5.3f\n"
         pad2 (fget :amp4)(fget :wave4))
    (app "%s:chorus  %5.3f\n" 
         pad2 (fget :chorus))
    (app "%s:vibrato [:freq %5.3f :sens %5.3f]\n"
         pad2 (fget :vibrato-freq)(fget :vibrato-sens))
    (app "%s:filter  [:freq %2d    :type %6s]\n"
         pad2
         (iget :filter)
         (get {program/bypass "bypass"
               program/low "low"
               program/high "high"
               program/band "band"
               program/notch "notch"}
              (iget :filter-type) 0))
    (app "%s:flanger [:rate %5.3f :depth %5.3f :fb %+5.2f :mix %5.3f]\n"
         pad2 
         (fget :flanger-rate)
         (fget :flanger-depth)
         (fget :flanger-fb)
         (fget :flanger-mix))
    (app "%s:reverb  %5.3f  :amp %5.3f))"
         pad2
         (fget :reverb-mix)
         (fget :amp))
    (println (.toString sb))))
</pre>
