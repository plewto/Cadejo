(println "\t--> pp")

(ns cadejo.instruments.combo.pp
  (:require [cadejo.instruments.combo.program :as program])
  (:require [cadejo.util.col :as col]))
     
(defn pp-combo [pnum pname data remarks]
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
    (app ";; Combo ------------------------------- %3d \"%s\"\n" pnum pname)
    (app "(save-prgoram %3d \"%s\" \"%s\"\n" pnum pname remarks)
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
 
