(println "-->    combo pp")

(ns cadejo.instruments.combo.pp
  (:use [cadejo.instruments.combo.constants])
  (:require [cadejo.instruments.combo.program :as program])
  (:require [cadejo.util.user-message :as umsg]))



 
(defn pp-combo [pnum pname data remarks]
  (let [;data (:data prog)
        pad1 "    "
        pad2 (str pad1 "       ")
        sb (StringBuilder.)
        fget (fn [p]
               (let [v (get data p)]
                 (if v
                   (float v)
                   (do
                     (umsg/warning (format "Combo %s parameter missing" p))
                     -999.0))))
        iget (fn [p] 
               (int (fget p)))
        app (fn [frmt & args]
              (.append sb (apply #'format (cons frmt args))))]
    (app ";; Combo ------------------------------- %3s \"%s\"\n" pnum pname)
    (app "(save-program %3s \"%s\" \"%s\"\n" pnum pname remarks)
    (app "%s(combo :a1      %5.3f  :w1 %5.3f\n"
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
         (get {bypass-filter "bypass"
               lp-filter "low"
               hp-filter "high"
               bp-filter "band"
               br-filter "notch"}
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
    (.append sb "\n\n")
    (.toString sb)))

;; (defn combo-dump [pnum pname data remarks]
;;   (let [sb (StringBuilder. 1024)]
;;     (.append sb (format "Combo data dump\n"))
;;     (.append sb (format "\tpnum = %s  pname = '%s'\n" pnum pname))
;;     (.append sb (format "\tremarks are '%s'\n" remarks))
;;     (.append sb (format "\tdata\n"))
;;     (doseq [k (sort (keys data))]
;;       (.append sb (format "\t\t[%-16s] --> %s\n" k (get data k))))
;;     (.toString sb)))
        
