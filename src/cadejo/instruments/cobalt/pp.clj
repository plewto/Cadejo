(println "-->    cobalt pp")
(ns cadejo.instruments.cobalt.pp
  (:require [cadejo.util.user-message :as umsg])
  ;(:require [cadejo.util.string :as ustr])
  (:require [cadejo.instruments.cobalt.constants :as con]))

(def ^:private pad3 "   ")
(def ^:private pad10 (str pad3 "       "))
(def ^:private pad11 (str pad10 " "))
(def ^:private pad16 (str pad11 "     "))
(def ^:private pad17 (str pad16 " "))
(def ^:private pad18 (str pad17 " "))
(def ^:private pad22 (str pad17 "     "))
(def ^:private pad23 (str pad22 " "))
(def ^:private pad24 (str pad22 "  "))
(def ^:private pad29 (str pad24 "     "))
(def ^:private bar ";; ------------------------------------------------------")


;; (defn- dump-filter [q]
;;   ;(ustr/is-substring? "penv" (name q))
;;   true)

;; (defn- dump [data & args]
;;   (println "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ " args)
;;   (println ";; COBALT PATCH DUMP")
;;   (doseq [k (filter dump-filter (sort (keys data)))]
;;     (let [v (get data k)]
;;       (println (format ";;   [%-18s] -> %s" k v)))))

(defn- fget [key map]
  (let [val (get map key)]
    (if (not (number? val))
      (umsg/warning (format "Cobalt pp key %s returns %s" key (type val)))
      (float val))))

(defn- iget [key map]
  (int (fget key map)))


(defn- pp-enable [dmap]
  (let [op1 (iget :op1-enable dmap)
        op2 (iget :op2-enable dmap)
        op3 (iget :op3-enable dmap)
        op4 (iget :op4-enable dmap)
        op5 (iget :op5-enable dmap)
        op6 (iget :op6-enable dmap)
        nse (iget :nse-enable dmap)
        bzz (iget :bzz-enable dmap)
        sb (StringBuilder. 80)]
    (.append sb "(enable  ")
    (.append sb (if (pos? op1) "1 " "  "))
    (.append sb (if (pos? op2) "2 " "  "))
    (.append sb (if (pos? op3) "3 " "  "))
    (.append sb " ")
    (.append sb (if (pos? op4) "4 " "  "))
    (.append sb (if (pos? op5) "5 " "  "))
    (.append sb (if (pos? op6) "6 " "  "))
    (.append sb " ")
    (.append sb (if (pos? nse) ":noise " " "))
    (.append sb (if (pos? bzz) ":buzz " " "))
    (.append sb ")\n")
    (.toString sb)))

(defn- header [slot name remarks dmap]
  (str (format "%s %3d '%s'\n;;\n" bar slot name)
       (format "(save-program %3d \"%s\"\n" slot name)
       (if (and (string? remarks)(pos? (count remarks)))
         (format "%s\"%s\"\n" pad3 remarks)
         "")
       (format "%s(cobalt " pad3)
       (pp-enable dmap)))

(defn- pp-port [dmap]
  (format "%s(port-time %5.3f :cc5  %5.3f)\n"
          pad11 (fget :port-time dmap)(fget :port-time<-cc5 dmap)))

(defn- pp-vibrato [dmap]
  (format "%s(vibrato %5.3f :sens  %5.3f :prss %5.3f :depth %5.3f :delay %5.3f)\n"
          pad11 
          (fget :vibrato-frequency dmap)
          (fget :vibrato-sensitivity dmap)
          (fget :vibrato<-pressure dmap)
          (fget :vibrato-depth dmap)
          (fget :vibrato-delay dmap)))
          
(defn- pp-lfo1 [dmap]
  (format "%s(lfo1    %5.3f :bleed %5.3f :cca  %5.3f :prss  %5.3f :delay %5.3f)\n"
          pad11
          (fget :lfo1-freq dmap)
          (fget :lfo1-bleed dmap)
          (fget :lfo1<-cca dmap)
          (fget :lfo1<-pressure dmap)
          (fget :lfo1-delay dmap)))


(defn- pp-lfo [n dmap]
  (let [f (fget (keyword (format "lfo%d-freq" n)) dmap)
        cca (fget (keyword (format "lfo%d-amp<-cca" n)) dmap)
        ccb (fget (keyword (format "lfo%d-amp<-ccb" n)) dmap)
        xenv (fget (keyword (format "lfo%d-amp<-xenv" n)) dmap)]
    (format "%s(lfo%d    %5.3f :xenv  %5.3f :cca  %5.3f :ccb   %5.3f)\n"
            pad11 n f xenv cca ccb)))

(defn- pp-xenv [dmap]
  (str (format "%s(xenv :att  %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
               pad11 
               (fget :xenv-attack dmap)
               (fget :xenv-decay1 dmap)
               (fget :xenv-decay2 dmap)
               (fget :xenv-release dmap))
       (format "%s:peak %5.3f :bp   %5.3f :sus  %5.3f)\n"
               pad17
               (fget :xenv-peak dmap)
               (fget :xenv-breakpoint dmap)
               (fget :xenv-sustain dmap))))

(defn- pp-penv [dmap]
  (str (format "%s(penv :a0 %+7.4f :a1 %+7.4f :a2 %+7.4f :a3 %+7.4f\n"
               pad11
               (fget :pe-a0 dmap)
               (fget :pe-a1 dmap)
               (fget :pe-a2 dmap)
               (fget :pe-a3 dmap))
       (format "%s:t1 %5.3f   :t2 %5.3f   :t3 %5.3f   :cc9 %5.3f)\n"
               pad17
               (fget :pe-t1 dmap)
               (fget :pe-t2 dmap)
               (fget :pe-t3 dmap)
               (fget :pe<-cc9 dmap))))

(defn- op-a [n dmap]
  (let [param (fn [p](keyword (format "op%d%s" n p)))
        fval (fn [p](fget (param p) dmap))
        ival (fn [p](iget (param p) dmap))]
    (str (format "%s(op%d %6.4f %5.3f\n" pad11 n (fval "-detune")(fval "-amp"))
         (format "%s:lfo1  %5.3f :cca %5.3f :ccb %5.3f :vel %5.3f :prss %5.3f\n"
                 pad16 
                 (fval "-amp<-lfo1")
                 (fval "-amp<-cca")
                 (fval "-amp<-ccb")
                 (fval "-amp<-velocity")
                 (fval "-amp<-pressure"))
         (format "%s:penv %+7.4f  :key %3d :left  %+3d :right %+3d\n"
                 pad16 
                 (fval "<-penv")
                 (ival "-keyscale-key")
                 (ival "-keyscale-left")
                 (ival "-keyscale-right"))
         (format "%s:env [:att  %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
                 pad16
                 (fval "-attack")
                 (fval "-decay1")
                 (fval "-decay2")
                 (fval "-release"))
         (format "%s:peak %5.3f :bp   %5.3f :sus  %5.3f])\n"
                 pad22
                 (fval "-peak")
                 (fval "-breakpoint")
                 (fval "-sustain"))

         (format "%s(fm%d %6.4f %5.3f :bias %+6.3f :env %5.3f :lag %5.3f :left %-3d :right %-3d)\n"
                 pad11 
                 n
                 (fget (keyword (format "fm%d-detune" n)) dmap)
                 (fget (keyword (format "fm%d-amp" n)) dmap)
                 (fget (keyword (format "fm%d-bias" n)) dmap)
                 (fget (keyword (format "fm%d<-env" n)) dmap)
                 (fget (keyword (format "fm%d-lag" n)) dmap)
                 (iget (keyword (format "fm%d-keyscale-left" n)) dmap)
                 (iget (keyword (format "fm%d-keyscale-right" n)) dmap)))))

(defn- pp-noise [dmap]
  (str (format "%s(noise %6.4f %5.3f :bw %3d\n"
               pad11
               (fget :nse-detune dmap)
               (fget :nse-amp dmap)
               (iget :nse-bw dmap))
       (format "%s:lfo1 %5.3f   :cca %5.3f :vel %5.3f :prss %5.3f\n"
               pad18
               (fget :nse-amp<-lfo1 dmap)
               (fget :nse-amp<-cca dmap)
               (fget :nse-amp<-velocity dmap)
               (fget :nse-amp<-pressure dmap))
       (format "%s:penv %+7.4f :key %3d   :left  %+3d :right %+3d\n"
               pad18 
               (fget :nse<-penv dmap)
               (iget :nse-keyscale-key dmap)
               (iget :nse-keyscale-left dmap)
               (iget :nse-keyscale-right dmap))
       (format "%s:env [:att  %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
               pad18
               (fget :nse-attack dmap)
               (fget :nse-decay1 dmap)
               (fget :nse-decay2 dmap)
               (fget :nse-release dmap))
       (format "%s:peak %5.3f :bp   %5.3f :sus  %5.3f])\n"
               pad24
               (fget :nse-peak dmap)
               (fget :nse-breakpoint dmap)
               (fget :nse-sustain dmap))))

(defn- pp-noise2 [dmap]
  (format "%s(noise2 %6.4f %5.3f :bw %3d :lag %5.3f)\n"
          pad11 
          (fget :nse2-detune dmap)
          (fget :nse2-amp dmap)
          (iget :nse2-bw dmap)
          (fget :nse2-lag dmap)))

(defn- pp-buzz [dmap]
  (str (format "%s(buzz %6.4f %5.3f\n" 
               pad11 
               (fget :bzz-detune dmap)
               (fget :bzz-amp dmap))
       (format "%s:lfo1  %5.3f :cca %5.3f :ccb %5.3f :vel %5.3f :prss %5.3f\n"
               pad17 
               (fget :bzz-amp<-lfo1 dmap)
               (fget :bzz-amp<-cca dmap)
               (fget :bzz-amp<-ccb dmap)
               (fget :bzz-amp<-velocity dmap)
               (fget :bzz-amp<-pressure dmap))
       (format "%s:penv %+7.4f  :key %3d :left  %+3d :right %+3d\n"
               pad17 
               (fget :bzz<-penv dmap)
               (iget :bzz-keyscale-key dmap)
               (iget :bzz-keyscale-left dmap)
               (iget :bzz-keyscale-right dmap))
       (format "%s:env [:att  %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
               pad17
               (fget :bzz-attack dmap)
               (fget :bzz-decay1 dmap)
               (fget :bzz-decay2 dmap)
               (fget :bzz-release dmap))
       (format "%s:peak %5.3f :bp   %5.3f :sus  %5.3f])\n"
               pad23
               (fget :bzz-peak dmap)
               (fget :bzz-breakpoint dmap)
               (fget :bzz-sustain dmap))
       
       (format "%s(buzz-harmonics %3d :env %3d :cca %3d :hp %3d :hp<-env %3d)\n"
               pad11
               (iget :bzz-harmonics dmap)
               (iget :bzz-harmonics<-env dmap)
               (iget :bzz-harmonics<-cca dmap)
               (iget :bzz-hp-track dmap)
               (iget :bzz-hp-track<-env dmap)) ))

(defn- pp-filter [dmap]
  (str (format "%s(lp-filter :freq [%5d :track %2d :env %5.3f\n"
               pad11 
               (iget :filter-freq dmap)
               (iget :filter-track dmap)
               (fget :filter<-env dmap))
       (format "%s:prss %5.3f :cca %5.3f :ccb %5.3f]\n"
               pad29
               (fget :filter<-pressure dmap)
               (fget :filter<-cca dmap)
               (fget :filter<-ccb dmap))
       (format "%s:res [%5.3f :cca %+6.3f :ccb %+6.3f]\n"
               pad22
               (fget :filter-res dmap)
               (fget :filter-res<-cca dmap)
               (fget :filter-res<-ccb dmap))
       (format "%s:env [:att %5.3f :dcy %5.3f :rel %5.3f :sus %5.3f]\n"
               pad22
               (fget :filter-attack dmap)
               (fget :filter-decay dmap)
               (fget :filter-release dmap)
               (fget :filter-sustain dmap))
       (format "%s:mode %+6.3f)\n"
               pad22
               (fget :filter-mode dmap))
       (format "%s(bp-filter :offset %5.3f :lag %5.3f)\n"
               pad11
               (fget :filter2-detune dmap)
               (fget :filter2-lag dmap))))

(defn- pp-fold [dmap]
  (format "%s(fold      :wet %5.3f :gain %2d :cca %+3d :ccb %+3d)\n"
          pad11
          (fget :dist-mix dmap)
          (iget :dist-pregain dmap)
          (iget :dist<-cca dmap)
          (iget :dist<-ccb dmap)))

(defn- pp-delay [n dmap]
  (let [param (fn [p](keyword (format "delay%d%s" n p)))
        fval (fn [p](fget (param p) dmap))]
    (str (format "%s(delay%d    :time [%6.4f :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
                 pad11
                 n
                 (fval "-time")
                 (fval "-time<-lfo2")
                 (fval "-time<-lfo3")
                 (fval "-time<-xenv"))
         (format "%s:amp  [%+3d    :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
                 pad22
                 (int (fval "-amp"))
                 (fval "-amp<-lfo2")
                 (fval "-amp<-lfo3")
                 (fval "-amp<-xenv"))
         (format "%s:pan  [%+4.3f :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
                 pad22
                 (fval "-pan")
                 (fval "-pan<-lfo2")
                 (fval "-pan<-lfo3")
                 (fval "-pan<-xenv"))
         (format "%s:fb    %+5.3f :xfb %+5.3f)\n"
                 pad22 
                 (fval "-fb")
                 (fval "-xfb")))))

(defn- pp-amp [dmap]
  (format "%s(amp  %+3d   :dry %+3d :dry-pan %+6.3f :cc7 %5.3f)"
          pad11
          (iget :amp dmap)
          (iget :dry-amp dmap)
          (fget :dry-pan dmap)
          (fget :amp<-cc7 dmap)))

(defn pp-cobalt [slot name dmap remarks]
  (str (header slot name remarks dmap)
       (pp-port dmap)
       (pp-vibrato dmap)
       (pp-lfo1 dmap)
       (pp-lfo 2 dmap)
       (pp-lfo 3 dmap)
       (pp-xenv dmap)
       (pp-penv dmap)
       (op-a 1 dmap)
       (op-a 2 dmap)
       (op-a 3 dmap)
       (op-a 4 dmap)
       (op-a 5 dmap)
       (op-a 6 dmap)
       (pp-noise dmap)
       (pp-noise2 dmap)
       (pp-buzz dmap)
       (pp-filter dmap)
       (pp-fold dmap)
       (pp-delay 1 dmap)
       (pp-delay 2 dmap)
       (pp-amp dmap)
       "))\n"))
