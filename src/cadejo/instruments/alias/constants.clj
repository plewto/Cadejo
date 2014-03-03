(println "\t--> constants")

(ns cadejo.instruments.alias.constants)

(def alias-parameters
  '[:a-depth1 :a-depth2 :amp :a-source1 :a-source2 :b-depth1 :b-depth2
    :b-source1 :b-source2 :cc7->volume :c-depth1 :c-depth2 :c-source1
    :c-source2 :d-depth1 :d-depth2 :distortion1-mix :distortion1-param
    :distortion1-param-depth :distortion1-param-source :distortion1-pregain
    :distortion2-mix :distortion2-param :distortion2-param-depth
    :distortion2-param-source :distortion2-pregain :divider1-bias :divider1-p1
    :divider1-p3 :divider1-p5 :divider1-p7 :divider1-pw :divider1-scale-depth
    :divider1-scale-source :divider2-bias :divider2-p2 :divider2-p4
    :divider2-p6 :divider2-p8 :divider2-pw :divider2-scale-depth
    :divider2-scale-source :d-source1 :d-source2 :echo1-amp-depth
    :echo1-amp-source :echo1-damp :echo1-delay :echo1-delay-depth
    :echo1-delay-source :echo1-feedback :echo1-mix :echo1-pan :echo2-amp-depth
    :echo2-amp-source :echo2-damp :echo2-delay :echo2-delay-depth
    :echo2-delay-source :echo2-feedback :echo2-mix :echo2-pan :e-depth1
    :e-depth2 :env1-attack :env1-breakpoint :env1-decay1 :env1-decay2
    :env1-invert :env1-peak :env1-release :env1-sustain :env2-attack
    :env2-breakpoint :env2-decay1 :env2-decay2 :env2-invert :env2-peak
    :env2-release :env2-sustain :env3-attack :env3-breakpoint :env3-decay1
    :env3-decay2 :env3-peak :env3-release :env3-sustain :e-source1 :e-source2
    :f-depth1 :f-depth2 :filter1-freq :filter1-freq1-depth
    :filter1-freq1-source :filter1-freq2-depth :filter1-freq2-source
    :filter1-mode :filter1-pan :filter1-pan-depth :filter1-pan-source
    :filter1-postgain :filter1-res :filter1-res-depth :filter1-res-source
    :filter2-freq :filter2-freq1-depth :filter2-freq1-source
    :filter2-freq2-depth :filter2-freq2-source :filter2-pan :filter2-pan-depth
    :filter2-pan-source :filter2-postgain :filter2-res :filter2-res-depth
    :filter2-res-source :flanger-crossmix :flanger-feedback :flanger-lfo-amp
    :flanger-lfo-rate :flanger-mix :flanger-mod-depth :flanger-mod-source
    :f-source1 :f-source2 :g-depth1 :g-depth2 :g-source1 :g-source2 :h-depth1
    :h-depth2 :h-source1 :h-source2 :lfnoise-freq-depth :lfnoise-freq-source
    :lfo1-freq1-depth :lfo1-freq1-source :lfo1-freq2-depth :lfo1-freq2-source
    :lfo1-wave1-depth :lfo1-wave1-source :lfo1-wave2-depth :lfo1-wave2-source
    :lfo2-freq1-depth :lfo2-freq1-source :lfo2-freq2-depth :lfo2-freq2-source
    :lfo2-wave1-depth :lfo2-wave1-source :lfo2-wave2-depth :lfo2-wave2-source
    :lfo3-freq1-depth :lfo3-freq1-source :lfo3-freq2-depth :lfo3-freq2-source
    :lfo3-wave1-depth :lfo3-wave1-source :lfo3-wave2-depth :lfo3-wave2-source
    :noise-amp :noise-amp1-depth :noise-amp1-lag :noise-amp1-src
    :noise-amp2-depth :noise-amp2-lag :noise-amp2-src :noise-hp :noise-lp
    :noise-pan :noise-param :osc1-amp :osc1-amp1-depth :osc1-amp1-lag
    :osc1-amp1-src :osc1-amp2-depth :osc1-amp2-lag :osc1-amp2-src :osc1-bias
    :osc1-detune :osc1-fm1-depth :osc1-fm1-lag :osc1-fm1-source
    :osc1-fm2-depth :osc1-fm2-lag :osc1-fm2-source :osc1-pan :osc1-wave
    :osc1-wave1-depth :osc1-wave1-source :osc1-wave2-depth :osc1-wave2-source
    :osc2-amp :osc2-amp1-depth :osc2-amp1-lag :osc2-amp1-src :osc2-amp2-depth
    :osc2-amp2-lag :osc2-amp2-src :osc2-bias :osc2-detune :osc2-fm1-depth
    :osc2-fm1-lag :osc2-fm1-source :osc2-fm2-depth :osc2-fm2-lag
    :osc2-fm2-source :osc2-pan :osc2-wave :osc2-wave1-depth :osc2-wave1-source
    :osc2-wave2-depth :osc2-wave2-source :osc3-amp :osc3-amp1-depth
    :osc3-amp1-lag :osc3-amp1-src :osc3-amp2-depth :osc3-amp2-lag
    :osc3-amp2-src :osc3-bias :osc3-detune :osc3-fm1-depth :osc3-fm1-lag
    :osc3-fm1-source :osc3-fm2-depth :osc3-fm2-lag :osc3-fm2-source :osc3-pan
    :osc3-wave :osc3-wave1-depth :osc3-wave1-source :osc3-wave2-depth
    :osc3-wave2-source :pitchshift-mix :pitchshift-pitch-dispersion
    :pitchshift-ratio :pitchshift-ratio-depth :pitchshift-ratio-source
    :pitchshift-time-dispersion :port-time :ringmod-amp :ringmod-amp1-depth
    :ringmod-amp1-lag :ringmod-amp1-src :ringmod-amp2-depth :ringmod-amp2-lag
    :ringmod-amp2-src :ringmod-carrier :ringmod-modulator :ringmod-pan
    :sh-bias :sh-rate :sh-scale :sh-source :stepper1-bias :stepper1-max
    :stepper1-min :stepper1-reset :stepper1-reset-value :stepper1-scale
    :stepper1-step :stepper1-trigger :stepper2-bias :stepper2-max
    :stepper2-min :stepper2-reset :stepper2-reset-value :stepper2-scale
    :stepper2-step :stepper2-trigger :dry-mix])


(def control-bus-map
  {:con 0, 
   :env1 1, :env2 2, :env3 3, :lfo1 4, :lfo2 5, :lfo3 6,
   :step1 7, :step2 8, :div1 9, :div2 10, :div 11,
   :lfnse 12, :sh 13, 
   :ferq 14, :period 15, :keynum 16 :press 17, :vel 18,
   :cca 19, :ccb 20, :ccc 21, :ccd 22,
   :a 23 :b 24 :c 25 :d 26 :e 27 :f 28 :g 28 :h 30
   :gate 31 :off 32})

(def reverse-control-bus-map
  (let [acc* (atom {})]
    (doseq [k (keys control-bus-map)]
      (swap! acc* (fn [n](assoc n (get control-bus-map k) k))))
    @acc*))

(defn get-control-bus-name [n]
  (get reverse-control-bus-map (int n)))

(def general-bus-map
  {:con 0 :a 1 :b 2 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 :off 9})

(def reverse-general-bus-map
  (let [acc* (atom {})]
    (doseq [k (keys general-bus-map)]
      (swap! acc* (fn [n](assoc n (get general-bus-map k) k))))
    @acc*))

(defn get-general-bus-name [n]
  (get reverse-general-bus-map (int n)))

(def filter-modes
  {:lp 0.0 :lp*hp 0.25 :hp 0.50 :band 0.75 :bypass 1.0})
