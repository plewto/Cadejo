(ns cadejo.instruments.alias.pp
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.instruments.alias.constants :as constants]))

(def pad1 " ")
(def pad2 (str pad1 " "))
(def pad3 (str pad2 "          "))

(defn- fget [dmap param]
  (let [v (get dmap param)]
    (if (not v)
      (do 
        (umsg/warning (format "Alis pp missing parameter %s" param))
        0.0)
      (do
        (float v)))))

(defn- iget [dmap param]
  (int (fget dmap param)))

(defn- pp-common [dmap]
  (let [amp (fget dmap :amp)
        ptime (fget dmap :port-time)
        cc7 (fget dmap :cc7->volume)]
    (printf "%s(common  :amp %5.3f  :port-time %5.3f :cc7->volume %5.3f)\n"
            pad2 amp ptime cc7)))

(defn- pp-env [n dmap] 
  (let [val (fn [param]
                (let [key (keyword (format "env%d-%s" n param))]
                  (fget dmap key)))
        att (val 'attack)
        dc1 (val 'decay1)
        dc2 (val 'decay2)
        rel (val 'release)
        pk (val 'peak)
        bp (val 'breakpoint)
        sus (val 'sustain)
        invert (if (or (= n 1)(= n 2))
                 (int (val 'invert))
                 nil)]
    (printf "%s(env%d   %5.3f %5.3f %5.3f %5.3f  :pk %5.3f :bp %5.3f :sus %5.3f"
            pad2 n att dc1 dc2 rel pk bp sus)
    (if (= n 3)
      (printf ")\n")
      (printf " :invert %d)\n" invert))))

(defn- pp-lfo [n dmap]
  (let [key (fn [param](keyword (format "lfo%d-%s" n param)))
        val (fn [param](fget dmap (key param)))
        fs1 (constants/get-control-bus-name (val 'freq1-source))
        fd1 (val 'freq1-depth)
        fs2 (constants/get-control-bus-name (val 'freq2-source))
        fd2 (val 'freq2-depth)
        ws1 (constants/get-control-bus-name (val 'wave1-source))
        wd1 (val 'wave1-depth)
        ws2 (constants/get-control-bus-name (val 'wave2-source))
        wd2 (val 'wave2-depth)]
    (printf "%s(lfo%d     :fm1   [%-7s %5.3f] :fm2   [%-7s %5.3f]\n"
            pad2 n fs1 fd1 fs2 fd2)
    (printf "%s:wave1 [%-7s %5.3f] :wave2 [%-7s %5.3f])\n"
            pad3 ws1 wd1 ws2 wd2)))

(defn- pp-stepper [n dmap]
  (let [key (fn [param](keyword (format "stepper%d-%s" n param)))
        val (fn [param](fget dmap (key param)))
        trig (constants/get-control-bus-name (val 'trigger))
        reset (constants/get-control-bus-name (val 'reset))
        min (int (val 'min))
        max (int (val 'max))
        step (int (val 'step))
        ival (int (val 'reset-value))
        bias (val 'bias)
        scale (val 'scale)]
    (printf "%s(stepper%d :trig  %-15s :reset %s\n"
            pad2 n trig reset)
    (printf "%s:min %+3d :max %+3d :step %+3d :ivalue %+3d :bias %3.1f :scale %4.2f)\n"
            pad3 min max step ival bias scale)))

(defn- pp-divider1 [dmap]
  (let [key (fn [param](keyword (format "divider1-%s" param)))
        val (fn [param](fget dmap (key param)))
        p1 (val 'p1)
        p3 (val 'p3)
        p5 (val 'p5)
        p7 (val 'p7)
        pw (val 'pw)
        src (constants/get-control-bus-name (val 'scale-source))
        depth (val 'scale-depth)
        bias (val 'bias)]
    (printf "%s(divider1 :p1 %+6.3f :p3 %+6.3f :p5 %+6.3f :p7 %+6.3f :pw %4.2f\n"
            pad2 p1 p3 p5 p7 pw)
    (printf "%s:am [%-7s %5.3f]   :bias %+6.3f)\n"
            pad3 src depth bias)))

(defn- pp-divider2 [dmap]
  (let [key (fn [param](keyword (format "divider2-%s" param)))
        val (fn [param](fget dmap (key param)))
        p2 (val 'p2)
        p4 (val 'p4)
        p6 (val 'p6)
        p8 (val 'p8)
        pw (val 'pw)
        src (constants/get-control-bus-name (val 'scale-source))
        depth (val 'scale-depth)
        bias (val 'bias)]
    (printf "%s(divider2 :p2 %+6.3f :p4 %+6.3f :p6 %+6.3f :p8 %+6.3f :pw %4.2f\n"
            pad2 p2 p4 p6 p8 pw)
    (printf "%s:am [%-7s %5.3f]   :bias %+6.3f)\n"
            pad3 src depth bias)))

(defn- pp-lfnoise [dmap]
  (let [src (constants/get-control-bus-name (iget dmap :lfnoise-freq-source))
        dpth (fget dmap :lfnoise-freq-depth)]
    (printf "%s(lfnoise  :fm [%-7s %5.3f])\n"
            pad2 src dpth)))

(defn- pp-sh [dmap]
  (let [rate (fget dmap :sh-rate)
        src (constants/get-control-bus-name (iget dmap :sh-source))
        bias (fget dmap :sh-bias)
        scale (fget dmap :sh-scale)]
    (printf "%s(sh       :rate %5.3f :src %-7s :bias %5.3f :scale %5.3f)\n"
            pad2 rate src bias scale)))

(defn- pp-matrix [dmap]
  (printf  "%s(matrix\n" pad2)
  (doseq [bs '(a b c d e f g h)]
    (printf "%s" pad3)
    (doseq [n '(1 2)]
      (let [bus-name (keyword (format "%s-source%d" bs n))
            src-name (constants/get-control-bus-name (iget dmap bus-name))
            depth-name (keyword (format "%s-depth%d" bs n))
            depth (fget dmap depth-name)
            mnemonic (keyword (format "%s%d" bs n))]
        (printf "%s [%-7s %5.3f] "
                mnemonic src-name depth)))
    (if (not (= bs 'h))
      (print "\n")
      (print ")\n"))))

(defn- pp-osc [n dmap]
  (let [key (fn [param](keyword (format "osc%d-%s" n param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (int (val param))))
        detune (val 'detune)
        bias (val 'bias)
        fm1-src (src 'fm1-source)
        fm1-depth (val 'fm1-depth)
        fm1-lag (val 'fm1-lag)
        fm2-src (src 'fm2-source)
        fm2-depth (val 'fm2-depth)
        fm2-lag (val 'fm2-lag)
        wave (val 'wave)
        w1-src (src 'wave1-source)
        w1-depth (val 'wave1-depth)
        w2-src (src 'wave2-source)
        w2-depth (val 'wave2-depth)
        amp (int (val 'amp))
        a1-src (src 'amp1-src)
        a1-depth (val 'amp1-depth)
        a1-lag (val 'amp1-lag)
        a2-src (src 'amp2-src)
        a2-depth (val 'amp2-depth)
        a2-lag (val 'amp2-lag)
        pan (val 'pan)]
    (printf "%s(osc%d-freq %5.3f :bias %5.3f :fm1 [%-7s %5.3f %4.2f] :fm2 [%-7s %5.3f %4.2f])\n"
            pad2 n detune bias 
            fm1-src fm1-depth fm1-lag 
            fm2-src fm2-depth fm2-lag)
    (printf "%s(osc%d-wave %5.3f             :w1  [%-7s %5.3f     ] :w2  [%-7s %5.3f     ])\n"
            pad2 n wave
            w1-src w1-depth
            w2-src w2-depth)
    (printf "%s(osc%d-amp %+3d :pan %+5.2f     :am1 [%-7s %5.3f %4.2f] :am2 [%-7s %5.3f %4.2f])\n" 
            pad2 n amp pan
            a1-src a1-depth a1-lag
            a2-src a2-depth a2-lag)))

(defn- pp-noise [dmap]
  (let [key (fn [param](keyword (format "noise-%s" param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (int (val param))))
        crack (val 'param)
        lp (int (val 'lp))
        hp (int (val 'hp))
        amp (int (val 'amp))
        pan (val 'pan)
        s1 (src 'amp1-src)
        d1 (val 'amp1-depth)
        l1 (val 'amp1-lag)
        s2 (src 'amp2-src)
        d2 (val 'amp2-depth)
        l2 (val 'amp2-lag)
        pan (val 'pan)
        pad (str pad2 "                             ")]
    (printf "%s(noise   %+4d :pan %+5.2f :crackle %4.2f     :lp %5d   :hp %5d\n"
            pad2 amp pan crack lp hp)
    (printf "%s:am1 [%-7s %5.3f %4.2f] :am2 [%-7s %5.3f %4.2f])\n"
            pad s1 d1 l1 s2 d2 l2)))

(defn- pp-ringmod [dmap]
  (let [key (fn [param](keyword (format "ringmod-%s" param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (int (val param))))
        car (val 'carrier)
        mod (val 'modulator)
        amp (int (val 'amp))
        s1 (src 'amp1-src)
        d1 (val 'amp1-depth)
        l1 (val 'amp1-lag)
        s2 (src 'amp2-src)
        d2 (val 'amp2-depth)
        l2 (val 'amp2-lag)
        pan (val 'pan)
        pad (str pad2 "                             ")]
    (printf "%s(ringmod %+4d :pan %+5.2f :carrier %+4.1f     :modulator %4.1f\n"
            pad2 amp pan car mod)
    (printf "%s:am1 [%-7s %5.3f %4.2f] :am2 [%-7s %5.3f %4.2f])\n"
            pad s1 d1 l1 s2 d2 l2)))

(defn- pp-clipper [n dmap]
  (let [prefix (if (= n 'clip) 'distortion1 'distortion2)
        key (fn [param](keyword (format "%s-%s" prefix param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (int (val param))))
        gain (val 'pregain)
        param (val 'param)
        src (src 'param-source)
        dpth (val 'param-depth)
        mix (val 'mix)]
    (printf "%s(%s    :gain %5.3f :mix %+6.3f :clip [%4.2f %-7s %4.2f])\n"
            pad2 n gain mix param src dpth)))

(defn- pp-filter [n dmap]
  (let [key (fn [param](keyword (format "filter%d-%s" n param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (int (val param))))
        gain (val 'postgain)
        freq (int (val 'freq))
        fs1 (src 'freq1-source)
        fd1 (int (val 'freq1-depth))
        fs2 (src 'freq2-source)
        fd2 (int (val 'freq2-depth))
        res (val 'res)
        res-src (src 'res-source)
        res-depth (val 'res-depth)
        pan (val 'pan)
        ps (src 'pan-source)
        pd (val 'pan-depth)
        mode (if (= n 1)(val 'mode) nil)
        pad3 (str pad2 "         ")]
    (printf "%s(filter%d :gain %5.3f :freq %5d :fm1  [%-7s     %5d] :fm2 [%-7s %5d]\n"
            pad2 n gain freq fs1 fd1 fs2 fd2)
    (if (= n 1)
      (printf "%s:mode %4.2f" pad3 mode)
      (printf "%21s" ""))
    (printf "%14s:res  [%4.2f %-7s %4.2f] :pan [%+5.2f %-7s %+5.2f])\n"
            "" res res-src res-depth pan ps pd)))

(defn- pp-dry [dmap]
  (let [dry (int (fget dmap :dry-mix))]
    (printf "%s(dry      %+3d)\n"
            pad2 dry)))
            
(defn- pp-pshifter [dmap]
  (let [ratio (fget dmap :pitchshift-ratio)
        src (constants/get-general-bus-name (iget dmap :pitchshift-ratio-source))
        dpth (fget dmap :pitchshift-ratio-depth)
        pd (fget dmap :pitchshift-pitch-dispersion)
        td (fget dmap :pitchshift-time-dispersion)
        mix (iget dmap :pitchshift-mix)]
    (printf "%s(pshifter %+3d :ratio [%5.3f %-7s %5.3f] :rand %4.2f :spread %4.2f)\n"
            pad2 mix ratio src dpth pd td)))

(defn- pp-flanger [dmap]
  (let [src (constants/get-general-bus-name (iget dmap :flanger-mod-source))
        dpth (fget dmap :flanger-mod-depth)
        lfo-amp (fget dmap :flanger-lfo-amp)
        lfo-rate (fget dmap :flanger-lfo-rate)
        fb (fget dmap :flanger-feedback)
        mix (iget dmap :flanger-mix)
        xmix (fget dmap :flanger-crossmix)]
    (printf "%s(flanger  %+3d :lfo [%5.3f %5.3f] :mod [%-7s %5.3f] :fb %+5.2f :xmix %4.2f)\n"
            pad2 mix lfo-rate lfo-amp src dpth fb xmix)))

(defn- pp-echo [n dmap]
  (let [key (fn [param](keyword (format "echo%d-%s" n param)))
        val (fn [param](fget dmap (key param)))
        src (fn [param](constants/get-general-bus-name (val param)))
        delay (val 'delay)
        dsrc (src 'delay-source)
        ddth (val 'delay-depth)
        fb (val 'feedback)
        damp (val 'damp)
        pan (val 'pan)
        gsrc (src 'amp-source)
        gdpth (val 'amp-depth)
        mix (int (val 'mix))]
    (printf "%s(echo%d    %+3d :delay [%5.3f %-7s %5.3f] :fb %+5.2f :damp %4.2f :gate [%-7s %4.2f] :pan %+5.2f)"
            pad2 n mix delay dsrc ddth fb damp gsrc gdpth pan)))

(defn pp-alias 
  ([pnum pname data remarks]
     (with-out-str
       (let [dmap (ucol/alist->map data)]
         (println)
         (printf ";; Alias ------------------------------- %3s %s\n" pnum pname)
         (printf "(save-program %3s \"%s\" \"%s\"\n" pnum pname remarks)
         (printf "%s(alias-program\n" pad1)
         (pp-common dmap)
         (printf "%s;; ENVS A     D1    D2    R\n" pad2)
         (pp-env 1 dmap)
         (pp-env 2 dmap)
         (pp-env 3 dmap)
         (pp-lfo 1 dmap)
         (pp-lfo 2 dmap)
         (pp-lfo 3 dmap)
         (pp-stepper 1 dmap)
         (pp-stepper 2 dmap)
         (pp-divider1 dmap)
         (pp-divider2 dmap)
         (pp-lfnoise dmap)
         (pp-sh dmap)
         (pp-matrix dmap)
         (printf "%s;; OSCILLATORS\n" pad2)
         (pp-osc 1 dmap)(print "\n")
         (pp-osc 2 dmap)(print "\n")
         (pp-osc 3 dmap)(print "\n")
         (pp-noise dmap)
         (pp-ringmod dmap)
         (printf "%s;; FILTERS\n" pad2)
         (pp-clipper 'clip dmap)
         (pp-filter 1 dmap)
         (pp-clipper 'fold dmap)
         (pp-filter 2 dmap)
         (printf "%s;; EFX\n" pad2)
         (pp-dry dmap)
         (pp-pshifter dmap)
         (pp-flanger dmap)
         (pp-echo 1 dmap)(print "\n")
         (pp-echo 2 dmap)(println "))")
         (println)))))
