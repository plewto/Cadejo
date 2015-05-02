

(println "-->    cobalt pp")
(ns cadejo.instruments.cobalt.pp
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.string :as ustr])
  (:require [cadejo.instruments.cobalt.constants :as con])
)

(def pad3 "   ")
(def pad10 (str pad3 "       "))
(def pad15 (str pad10 "     "))
(def pad17 (str pad15 "  "))
(def pad19 (str pad17 "  "))
(def pad21 (str pad19 "  "))
(def pad33 (str pad21 "            "))
(def bar ";; ------------------------------------------------------")


(defn dump-filter [q]
  (ustr/is-substring? "penv" (name q)))

(defn dump [data & args]
  (println "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ " args)
  (println ";; COBALT PATCH DUMP")
  (doseq [k (filter dump-filter (sort (keys data)))]
    (let [v (get data k)]
      (println (format ";;   [%-18s] -> %s" k v)))))


(defn- header [slot name remarks dmap]
  (str (format "%s %3d '%s'\n;;\n" bar slot name)
       (format "(save-program %3d \"%s\"\n" slot name)
       (if (and (string? remarks)(pos? (count remarks)))
         (format "%s\"%s\"\n" pad3 remarks)
         "")
       (format "%s(cobalt (port-time %4.2f)\n" pad3 (float (get dmap :port-time 0)))))

(defn- env-prefix [id]
  (cond (integer? id)(format "op%d" id)
        (= id :xenv) "xenv"
        :default
        (umsg/warning (format "Illegal env prefix id %s" id))))

;; format general envelope s
;; id -> :xenv
;;
(defn- genv [id dmap]
  (let [prefix (env-prefix id)
        param (fn [p](keyword (format "%s-%s" prefix p)))
        att (float (get dmap (param "attack") 0.00))
        dc1 (float (get dmap (param "decay1") 0.00))
        dc2 (float (get dmap (param "decay2") 0.00))
        rel (float (get dmap (param "release") 0.00))
        pk  (float (get dmap (param "peak") 1.00))
        bp  (float (get dmap (param "breakpoint") 1.00))
        sus (float (get dmap (param "sus") 1.00))]
    (str (format "%s(%-5s :att %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
                 pad10 (name id) att dc1 dc2 rel)
         (format "%s:peak %4.2f :bp   %4.2f  :sus  %4.2f)\n"
                 pad17 pk bp sus))))

(defn- penv [dmap]
  (let [a0 (float (get dmap :pe-a0 0.00))
        a1 (float (get dmap :pe-a1 0.00))
        a2 (float (get dmap :pe-a2 0.00))
        a3 (float (get dmap :pe-a3 0.00))
        t1 (float (get dmap :pe-t1 1.00))
        t2 (float (get dmap :pe-t2 1.00))
        t3 (float (get dmap :pe-t3 1.00))]
    (str (format "%s(penv  :a0 %5.3f :a1 %5.3f :a2 %5.3f :a3 %5.3f\n"
                 pad10 a0 a1 a2 a3)
         (format "%s:t1 %5.3f :t2 %5.3f :t3 %5.3f)\n"
                 pad17 t1 t2 t3))))

(defn- vibrato [dmap]
  (let [freq (float (get dmap :vibrato-frequency 5.00))
        sens (float (get dmap :vibrato-sensitivity 0.10))
        dpth (float (get dmap :vibrato-depth 0.00))
        prss (float (get dmap :vibrato-pressure 0.00))]
    (str (format "%s(vibrato %5.2f :sens  %5.3f :depth %4.2f :prss %4.2f)\n"
                 pad10 freq sens dpth prss))))

;; id -> 1, 2, 3 or 4
;;
(defn- lfo [id dmap]
  (let [fname (format "lfo%d" id)
        p-freq (keyword (format "lfo%d-frequency" id))
        [p-mod1 p-mod2 p-mod3](cond (= id 1)[:lfo1<-genv1 :lfo1<-cca :lfo1<-pressure]
                                    (= id 2)[:lfo2<-xenv  :lfo2<-cca :lfo2<-ccb]
                                    :default [:lfo3<-xenv :lfo3<-cca :lfo3<-ccb])
        freq (float (get dmap p-freq 5.00))
        mod1 (float (get dmap p-mod1 0.00))
        mod2 (float (get dmap p-mod2 0.00))
        mod3 (float (get dmap p-mod3 0.00))
        [k-mod1 k-mod2 k-mod3](cond (= id 1)[:genv1 :cca :prss]
                                    (= id 2)[:genv2 :ccb :prss]
                                    :default [:xenv :cca :ccb])]
    (format "%s(%s    %5.3f %s %4.2f %s %4.2f %s %4.2f)\n"
            pad10 fname freq k-mod1 mod1 k-mod2 mod2 k-mod3 mod3)))


(defn- op [n dmap]
  (let [fname (format "op%d" n)
        param (fn [p](keyword (format "op%d%s" n p)))
        detune (float (get dmap (param "-detune") n))
        amp (float (get dmap (param "-amp") 0))
        ge1 (float (get dmap (param "-amp<-genv1") 0))
        lf1 (float (get dmap (param "-amp<-lfo1") 0))
        cca (float (get dmap (param "-amp<-cca") 0))
        ccb (float (get dmap (param "-amp<-ccb") 0))
        vel (float (get dmap (param "-amp<-velocity") 0))
        prss (float (get dmap (param "-amp<-pressure") 0))
        penv (float (get dmap (param "<-penv") 0))
        att (float (get dmap (param "-attack") 0))
        dc1 (float (get dmap (param "-decay1") 0))
        dc2 (float (get dmap (param "-decay2") 0))
        rel (float (get dmap (param "-release") 0))
        pk (float (get dmap (param "-peak") 1))
        bp (float (get dmap (param "-breakpoint") 1))
        sus (float (get dmap (param "-sustain") 1))
        key (int (get dmap (param "-keyscale-key") 60))
        left (int (get dmap (param "-keyscale-left") 0))
        right (int (get dmap (param "-keyscale-right") 0))]
    (str (format "%s(%s %5.3f %5.3f\n" pad10 fname detune amp)
         ;; (format "%s:genv1 %4.2f :lfo1 %4.2f\n"
         ;;         pad15 ge1 lf1)
         ;; (format "%s:cca   %4.2f :ccb  %4.2f  :vel  %4.2f :prss %4.2f\n"
         ;;         pad15 cca ccb vel prss) 
         ;; (format "%s:penv  %5.3f\n" pad15 penv)
         (format "%s:env  [:att %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
                 pad15 att dc1 dc2 rel)
         (format "%s:peak %4.3f :bp  %4.3f  :sus  %4.3f]"
                 pad21 pk bp sus)
         (if (> n 4)
           ")\n"
           (format "\n%s:key %3d    :left %3d  :right %3d)\n"
                   pad15 key left right)))))

(defn- fm [n dmap]
  (let [fname (format "fm%d" n)
        param (fn [p](keyword (format "fm%d%s" n p)))
        detune (float (get dmap (param "-detune") 1.0))
        bias (float (get dmap (param "-bias") 0.0))
        amp (float (get dmap (param "-amp") 0.0))
        env (float (get dmap (param "<-env") 0.0))
        left (int (get dmap (param "-keyscale-left") 0))
        right (int (get dmap (param "-keyscale-right") 0))]
    (format "%s(%s %5.3f %5.3f :bias %+6.3f :env %4.2f :left %3d :right %3d)\n"
            pad10 fname detune amp bias env left right)))

(defn- noise [dmap]
  (let [fname "noise"
        param (fn [p](keyword (format "noise%s" p)))
        detune (float (get dmap (param "-detune") 9))
        amp (float (get dmap (param "-amp") 0))
        lf1 (float (get dmap (param "-amp<-lfo1") 0))
        cca (float (get dmap (param "-amp<-cca") 0))
        vel (float (get dmap (param "-amp<-velocity") 0))
        prss (float (get dmap (param "-amp<-pressure") 0))
        penv (float (get dmap (param "<-penv") 0))
        att (float (get dmap (param "-attack") 0))
        dc1 (float (get dmap (param "-decay1") 0))
        dc2 (float (get dmap (param "-decay2") 0))
        rel (float (get dmap (param "-release") 0))
        pk (float (get dmap (param "-peak") 1))
        bp (float (get dmap (param "-breakpoint") 1))
        sus (float (get dmap (param "-sustain") 1))
        key (int (get dmap (param "-keyscale-key") 60))
        left (int (get dmap (param "-keyscale-left") 0))
        right (int (get dmap (param "-keyscale-right") 0))
        bw (int (get dmap (param "-bw") 10))]
    (str (format "%s(%s %5.3f %5.3f\n" pad10 fname detune amp)
         (format "%s:lfo1 %4.2f :cca %4.2f  :vel %4.2f  :prss %4.2f\n"
                 pad15 lf1 cca vel prss)
         (format "%s:penv  %5.3f\n" pad15 penv)
         (format "%s:env  [:att %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
                 pad15 att dc1 dc2 rel)
         (format "%s:peak %4.3f :bp  %4.3f  :sus  %4.3f]\n"
                 pad21 pk bp sus)
         (format "%s:key %3d    :left %3d  :right %3d\n"
                 pad15 key left right)
         (format "%s:bw %d)\n" pad15 bw))))

(defn- buzz [dmap]
  (let [fname "buzz"
        param (fn [p](keyword (format "buzz%s" p)))
        detune (float (get dmap (param "-detune") 1))
        amp (float (get dmap (param "-amp") 0))
        ge1 (float (get dmap (param "-amp<-genv1") 0))
        lf1 (float (get dmap (param "-amp<-lfo1") 0))
        cca (float (get dmap (param "-amp<-cca") 0))
        ccb (float (get dmap (param "-amp<-ccb") 0))
        vel (float (get dmap (param "-amp<-velocity") 0))
        prss (float (get dmap (param "-amp<-pressure") 0))
        penv (float (get dmap (param "<-penv") 0))
        att (float (get dmap (param "-attack") 0))
        dc1 (float (get dmap (param "-decay1") 0))
        dc2 (float (get dmap (param "-decay2") 0))
        rel (float (get dmap (param "-release") 0))
        pk (float (get dmap (param "-peak") 1))
        bp (float (get dmap (param "-breakpoint") 1))
        sus (float (get dmap (param "-sustain") 1))
        key (int (get dmap (param "-keyscale-key") 60))
        left (int (get dmap (param "-keyscale-left") 0))
        right (int (get dmap (param "-keyscale-right") 0))]
    (str (format "%s(%s %5.3f %5.3f\n" pad10 fname detune amp)
         (format "%s:genv1 %4.2f :lfo1 %4.2f\n"
                 pad15 ge1 lf1)
         (format "%s:cca   %4.2f :ccb  %4.2f  :vel  %4.2f :prss %4.2f\n"
                 pad15 cca ccb vel prss) 
         (format "%s:penv  %5.3f\n" pad15 penv)
         (format "%s:env  [:att %5.3f :dcy1 %5.3f :dcy2 %5.3f :rel %5.3f\n"
                 pad15 att dc1 dc2 rel)
         (format "%s:peak %4.3f :bp  %4.3f  :sus  %4.3f]\n"
                 pad21 pk bp sus)
         (format "%s:key %3d    :left %3d  :right %3d)\n"
                 pad15 key left right))))

(defn- buzz-harmonics [dmap]
  (format "%s(buzz-harmonics %3d :env %4d :cca %4d :hp %4d :hp<-env %4d)\n"
          pad15 
          (int (get dmap :bzz-harmonics 1))
          (int (get dmap :bzz-harmonics<-env 0))
          (int (get dmap :bzz-harmonics<-cca 0))
          (int (get dmap :bzz-hp-track 1))
          (int (get dmap :bzz-hp-track<-env 0))))

(defn- filters [dmap]
  (let [freq (int (get dmap :filter-freq 10000))
        track (int (get dmap :filter-track 1))
        f<-env (float (get dmap :filter<-env 0))
        f<-prss (float (get dmap :filter<-pressure 0))
        f<-cca (float (get dmap :filter<-cca 0))
        f<-ccb (float (get dmap :filter<-ccb 0))
        res (float (get dmap :filter-res 0))
        r<-cca (float (get dmap :filter-res<-cca 0))
        r<-ccb (float (get dmap :filter-res<-ccb 0))
        att (float (get dmap :filter-attack 0))
        dcy (float (get dmap :filter-decay 0))
        rel (float (get dmap :filter-release 0))
        sus (float (get dmap :filter-sustain 1))
        mode (float (get dmap :filter-mode -1))
        offset (float (max 0 (get dmap :filter2-detune 1)))]
  (str (format "%s(filters :freq  [%5d :track %2d :env %+5.2f :prss %+5.2f\n"
               pad15 freq track f<-env f<-prss)
       (format "%s:cca %+5.2f :ccb %+5.2f]\n"
               pad33 f<-cca f<-ccb)
       (format "%s:res [%4.2f :cca %+5.2f :ccb %+5.2f]\n"
               pad19 res r<-cca r<-ccb)
       (format "%s:env [:att %4.2f :dcy %4.2f :rel %4.2f :sus %4.2f]\n"
               pad19 att dcy rel sus)
       (format "%s:mode %+5.2f :offset %5.3f)\n"
               pad19 mode offset))))

(defn- delay [n dmap]
  (let [fname (format "delay%d" n)
        param (fn [p](keyword (format "%s-%s" fname p)))
        dt (float (get dmap (param "time") 0.1))
        dt<-lfo2 (float (get dmap (param "time<-lfo2") 0))
        dt<-lfo3 (float (get dmap (param "time<-lfo3") 0))
        dt<-env (float (get dmap (param "time<-xenv") 0))
        amp (int (get dmap (param "amp") con/min-db))
        amp<-lfo2 (float (get dmap (param "amp<-lfo2") 0))
        amp<-lfo3 (float (get dmap (param "amp<-lfo3") 0))
        amp<-env (float (get dmap (param "amp<-xenv") 0))
        pan (float (get dmap (param "pan") 0))
        pan<-lfo2 (float (get dmap (param "pan<-lfo2") 0))
        pan<-lfo3 (float (get dmap (param "pan<-lfo3") 0))
        pan<-env (float (get dmap (param "pan<-xenv") 0))
        fb (float (get dmap (param "fb") 0.5))
        xfb (float (get dmap (param "xfb") 0))]
    (str 
     (format "%s(%s  :time [%6.4f :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
             pad10 fname dt dt<-lfo2 dt<-lfo3 dt<-env)
     (format "%s:amp  [%+3d  :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
             pad19 amp amp<-lfo2 amp<-lfo3 amp<-env)
     (format "%s:pan  [%+5.2f :lfo2 %5.3f :lfo3 %5.3f :xenv %5.3f]\n"
             pad19 pan pan<-lfo2 pan<-lfo3 pan<-env)
     (format "%s:fb   %+5.2f  :xfb %5.2f)\n"
             pad19 fb xfb)))) 

(defn- amp [dmap]
  (format "%s(amp %+3d  :vel %3.2f :genv1 %3.2f :dry %+3d :dry-pan %+5.2f :cc7 %4.2f)"
          pad10 
          (int (get dmap :amp -6))
          (float (get dmap :amp<-velocity 0))
          (float (get dmap :amp<-genv1 0))
          (int (get dmap :dry-amp 0))
          (float (get dmap :dry-pan 0))
          (float (get dmap :amp<-cc7 0))))

(defn pp-cobalt [slot name dmap remarks]
  ;(dump dmap)
  (str (header slot name remarks dmap)
       ;; (genv :genv1 dmap)
       ;; (genv :xenv dmap)
       ;; (penv dmap)
       ;; (vibrato dmap)
       ;; (lfo 1 dmap)
       ;; (lfo 2 dmap)
       ;; (lfo 3 dmap)
       ;; (op 1 dmap)(fm 1 dmap)
       ;; (op 2 dmap)(fm 2 dmap)
       ;; (op 3 dmap)(fm 3 dmap)
       ;; (op 4 dmap)(fm 4 dmap)
       ;; (op 5 dmap)
       ;; (op 6 dmap)
       ;; (op 7 dmap)
       ;; (op 8 dmap)
       ;; (noise dmap)
       ;; (buzz dmap)
       ;; (buzz-harmonics dmap)
       ;; (filters dmap)
       ;; (delay 1 dmap)
       ;; (delay 2 dmap)
       (amp dmap)
       "))\n\n"))
