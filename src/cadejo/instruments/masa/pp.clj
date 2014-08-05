(ns cadejo.instruments.masa.pp
  (:use [cadejo.instruments.masa.program])
  (:require [cadejo.util.col])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg]))

(def pad1 "  ")
(def pad2 (str pad1 "      "))

(defn- extract [param dmap default]
  (float (or (get dmap param)
             (umsg/warning (format "MASA parameter %s missing" param))
             default)))

(defn- str-harmonics [dmap]
  (let [sb (StringBuilder.)]
    (.append sb ":harmonics    [")
    (doseq [k '(:r1 :r2 :r3 :r4 :r5 :r6 :r7 :r8 :r9)]
      (let [h (extract k dmap 1.0)]
        (.append sb (format "%5.3f" h))
        (if (= k :r9)
          (.append sb "]\n")
          (.append sb " "))))
    (.toString sb)))

(defn- str-registration [dmap]
  (let [sb (StringBuilder.)]
    (.append sb (format "%s:registration [" pad2))
    (doseq [k '(:a1 :a2 :a3 :a4 :a5 :a6 :a7 :a8 :a9)]
      (let [h (extract k dmap 0)]
        (.append sb (format "%5d" (int h)))
        (if (= k :a9)
          (.append sb "]\n")
          (.append sb " "))))
    (.toString sb)))

(defn- str-pedals [dmap]
  (let [sb (StringBuilder.)]
    (.append sb (format "%s:pedals       [" pad2))
    (doseq [k '(:p1 :p2 :p3 :p4 :p5 :p6 :p7 :p8 :p9)]
      (let [h (extract k dmap 0)]
        (.append sb (format " %4.2f" h))
        (if (= k :p9)
          (.append sb "]\n")
          (.append sb " "))))
    (.toString sb)))

(defn- str-percussion [dmap]
  (let [sb (StringBuilder.)]
    (.append sb (format "%s:percussion   [" pad2))
    (doseq [k '(:perc1 :perc2 :perc3 :perc4 :perc5 
                       :perc6 :perc7 :perc8 :perc9)]
      (let [h (extract k dmap 0)]
        (.append sb (format "%5d" (int h)))
        (if (= k :perc9)
          (.append sb "]\n")
          (.append sb " "))))
    (.toString sb)))

(defn- str-common [dmap]
  (let [sb (StringBuilder.)]
    (doseq [k '(:amp :pedal-sens :decay :sustain :vrate :vsens :vdepth :vdelay)]
      (.append sb (format "%s%-13s %4.2f\n"
                          pad2 k (extract k dmap 1.0))))
    (.toString sb)))

(defn- str-scanner [dmap]
  (let [sb (StringBuilder.)]
    (doseq [k '(:scanner-delay :scanner-delay-mod :scanner-mod-rate 
                               :scanner-mod-spread :scanner-scan-rate
                               :scanner-crossmix :scanner-mix)]
      (.append sb (format "%s%-20s %4.2f\n"
                          pad2 k (extract k dmap 1.0))))
    (.toString sb)))

(defn- str-reverb [dmap]
  (let [sb (StringBuilder.)]
    (doseq [k '(:reverb-size :reverb-damp :reverb-mix)]
      (.append sb (format "%s%-20s %4.2f"
                          pad2 k (extract k dmap 1.0)))
      (if (not (= k :reverb-mix))
        (.append sb "\n")))
    (.toString sb)))

(defn pp-masa 
  ([pnum pname data remarks]
     (with-out-str
       (let [dmap (cadejo.util.col/alist->map data)]
         (printf ";; MASA ---------------------------------- %s %s\n"
                 pnum pname)
         (printf "(save-program %3s \"%s\" \"%s\"\n"
                 pnum pname remarks)
         (printf "%s(masa " pad1)
         (print (str-harmonics dmap))
         (print (str-registration dmap))
         (print (str-pedals dmap))
         (print (str-percussion dmap))
         (print (str-common dmap))
         (print (str-scanner dmap))
         (print (str-reverb dmap))
         (println "))")))))
