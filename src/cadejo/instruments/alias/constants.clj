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
   :freq 14, :period 15, :keynum 16 :press 17, :vel 18,
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


(def initial-program {:osc1-detune 1.00
                       :osc1-bias 0.0
                       :osc1-fm1-source 1
                       :osc1-fm1-depth 0.1
                       :osc1-fm1-lag 0
                       :osc1-fm2-source 0
                       :osc1-fm2-depth 0
                       :osc1-fm2-lag 0
                       :osc1-wave 0.00
                       :osc1-wave1-source 3
                       :osc1-wave1-depth 0
                       :osc1-wave2-source 0
                       :osc1-wave2-depth 0
                       :osc1-amp 0
                       :osc1-amp1-src 0
                       :osc1-amp1-depth 0
                       :osc1-amp1-lag 0
                       :osc1-amp2-src 0
                       :osc1-amp2-depth 0
                       :osc1-amp2-lag 0
                       :osc1-pan 0.0
                       
                       :osc2-detune 1.00
                       :osc2-bias 0.0
                       :osc2-fm1-source 1
                       :osc2-fm1-depth 0.1
                       :osc2-fm1-lag 0
                       :osc2-fm2-source 0
                       :osc2-fm2-depth 0
                       :osc2-fm2-lag 0
                       :osc2-wave 0.50
                       :osc2-wave1-source 3
                       :osc2-wave1-depth 0
                       :osc2-wave2-source 0
                       :osc2-wave2-depth 0
                       :osc2-amp 0
                       :osc2-amp1-src 0
                       :osc2-amp1-depth 0
                       :osc2-amp1-lag 0
                       :osc2-amp2-src 0
                       :osc2-amp2-depth 0
                       :osc2-amp2-lag 0
                       :osc2-pan 0.0
                       
                       :osc3-detune 1.00
                       :osc3-bias 0.0
                       :osc3-fm1-source 1
                       :osc3-fm1-depth 0.1
                       :osc3-fm1-lag 0
                       :osc3-fm2-source 0
                       :osc3-fm2-depth 0
                       :osc3-fm2-lag 0
                       :osc3-wave 0.00
                       :osc3-wave1-source 3
                       :osc3-wave1-depth 0
                       :osc3-wave2-source 0
                       :osc3-wave2-depth 0
                       :osc3-amp 0
                       :osc3-amp1-src 0
                       :osc3-amp1-depth 0
                       :osc3-amp1-lag 0
                       :osc3-amp2-src 0
                       :osc3-amp2-depth 0
                       :osc3-amp2-lag 0
                       :osc3-pan 0.0
                       
                       :noise-param 0.0
                       :noise-lp 10000
                       :noise-hp 10
                       :noise-amp -99
                       :noise-amp1-src 0
                       :noise-amp1-depth 0
                       :noise-amp1-lag 0
                       :noise-amp2-src 0
                       :noise-amp2-depth 0
                       :noise-amp2-lag 0
                       :noise-pan 0.5
                       
                       :ringmod-carrier -1.0
                       :ringmod-modulator -1.0
                       :ringmod-amp -99
                       :ringmod-amp1-src 0
                       :ringmod-amp1-depth 0
                       :ringmod-amp1-lag 0
                       :ringmod-amp2-src 0
                       :ringmod-amp2-depth 0
                       :ringmod-amp2-lag 0
                       :ringmod-pan 0.5
                       
                       :distortion1-pregain 1.00
                       :distortion1-param 0
                       :distortion1-param-source 3
                       :distortion1-param-depth 0
                       :distortion1-mix 0.00
                       
                       :filter1-res 0.0
                       :filter1-res-source 4
                       :filter1-res-depth 0
                       :filter1-freq 10000
                       :filter1-freq1-source 0
                       :filter1-freq1-depth 0
                       :filter1-freq2-source 0
                       :filter1-freq2-depth 0
                       :filter1-pan -0.75
                       :filter1-pan-source 0
                       :filter1-pan-depth 0
                       :filter1-mode 0
                       :filter1-postgain 1.00
                       
                       :distortion2-pregain 1.00
                       :distortion2-param 0
                       :distortion2-param-source 3
                       :distortion2-param-depth 0
                       :distortion2-mix 0.00
                       
                       :filter2-res 0.0
                       :filter2-res-source 0
                       :filter2-res-depth 0
                       :filter2-freq 10000
                       :filter2-freq1-source 4
                       :filter2-freq1-depth 0
                       :filter2-freq2-source 0
                       :filter2-freq2-depth 0
                       :filter2-pan -0.75
                       :filter2-pan-source 0
                       :filter2-pan-depth 0
                       :filter2-postgain 1.00
                       
                       :pitchshift-ratio 1.00
                       :pitchshift-ratio-source 0
                       :pitchshift-ratio-depth 0
                       :pitchshift-pitch-dispersion 0
                       :pitchshift-time-dispersion 0
                       :pitchshift-mix -99
                       
                       :flanger-mod-source 0
                       :flanger-mod-depth 0
                       :flanger-lfo-amp 0.1
                       :flanger-lfo-rate 1.0
                       :flanger-feedback 0.5
                       :flanger-mix -99
                       :flanger-crossmix 0.0
                       
                       :echo1-delay 0.25
                       :echo1-delay-source 0
                       :echo1-delay-depth 0
                       :echo1-feedback 0
                       :echo1-damp 0.0
                       :echo1-pan -0.25
                       :echo1-amp-source 0
                       :echo1-amp-depth 0
                       :echo1-mix -99
                       
                       :echo2-delay 0.125
                       :echo2-delay-source 0
                       :echo2-delay-depth 0
                       :echo2-feedback 0.5
                       :echo2-damp 0.0
                       :echo2-mix -99
                       :echo2-amp-source 0
                       :echo2-amp-depth 0
                       :echo2-pan -0.25
                       
                       :env1-attack 0.000
                       :env1-decay1 0.500
                       :env1-decay2 0.500
                       :env1-release 0.001
                       :env1-peak 1.000
                       :env1-breakpoint 1.000
                       :env1-sustain 1.000
                       :env1-invert 0
                       
                       :env2-attack 0.000
                       :env2-decay1 0.500
                       :env2-decay2 0.500
                       :env2-release 0.001
                       :env2-peak 1.000
                       :env2-breakpoint 1.000
                       :env2-sustain 1.000
                       :env2-invert 0
                       
                       :env3-attack 0.000
                       :env3-decay1 0.500
                       :env3-decay2 0.500
                       :env3-release 0.001
                       :env3-peak 1.000
                       :env3-breakpoint 1.000
                       :env3-sustain 1.000
                       
                       :lfo1-freq1-source 0
                       :lfo1-freq1-depth  7
                       :lfo1-freq2-source 0
                       :lfo1-freq2-depth  0
                       :lfo1-wave1-source 0
                       :lfo1-wave1-depth  0.5
                       :lfo1-wave2-source 0
                       :lfo1-wave2-depth  0
                       
                       :lfo2-freq1-source 0
                       :lfo2-freq1-depth  3.5
                       :lfo2-freq2-source 0
                       :lfo2-freq2-depth  0
                       :lfo2-wave1-source 0
                       :lfo2-wave1-depth  0.5
                       :lfo2-wave2-source 0
                       :lfo2-wave2-depth  0
                       
                       :lfo3-freq1-source 0
                       :lfo3-freq1-depth  1.75
                       :lfo3-freq2-source 0
                       :lfo3-freq2-depth  0
                       :lfo3-wave1-source 0
                       :lfo3-wave1-depth  0.5
                       :lfo3-wave2-source 0
                       :lfo3-wave2-depth  0
                       
                       :stepper1-trigger 4
                       :stepper1-reset 32
                       :stepper1-min -10
                       :stepper1-max 10
                       :stepper1-step 1
                       :stepper1-reset-value -10
                       :stepper1-bias 0
                       :stepper1-scale 1/10
                       
                       :stepper2-trigger 5
                       :stepper2-reset 32
                       :stepper2-min -10
                       :stepper2-max 10
                       :stepper2-step 1
                       :stepper2-reset-value -10
                       :stepper2-bias 0
                       :stepper2-scale 1/10
                       
                       :divider1-pw 0.5
                       :divider1-p1 0.000
                       :divider1-p3 0.000
                       :divider1-p5 0.000
                       :divider1-p7 1.000
                       :divider1-bias 0.000
                       :divider1-scale-source 0
                       :divider1-scale-depth 1.0
                       
                       :divider2-pw 0.5
                       :divider2-p2 0.000
                       :divider2-p4 0.000
                       :divider2-p6 0.000
                       :divider2-p8 1.000
                       :divider2-bias 0
                       :divider2-scale-source 0
                       :divider2-scale-depth 1.0
                       
                       :lfnoise-freq-source 0
                       :lfnoise-freq-depth 1.0
                       
                       :sh-source 12
                       :sh-rate 7
                       :sh-bias 0
                       :sh-scale 1
                       
                       :a-source1  4 ; LFO 1
                       :a-depth1   1
                       :a-source2 19 ; MIDI CC A
                       :a-depth2   1
                       :b-source1  5 ; LFO 2
                       :b-depth1   1
                       :b-source2  0
                       :b-depth2   1
                       :c-source1  1 ; ENV 1
                       :c-depth1   1
                       :c-source2  0
                       :c-depth2   1
                       :d-source1  2 ; ENV 2
                       :d-depth1   1
                       :d-source2  0
                       :d-depth2   1
                       :e-source1  7 ; Stepper 1
                       :e-depth1   1
                       :e-source2  0
                       :e-depth2   1
                       :f-source1 13 ; sample and Hold
                       :f-depth1   0
                       :f-source2  0
                       :f-depth2   1
                       :g-source1 20 ; MIDI CC B
                       :g-depth1   0
                       :g-source2  0
                       :g-depth2   1
                       :h-source1 18 ; Velocity
                       :h-depth1   0
                       :h-source2  0
                       :h-depth2   1
                       
                       :dry-mix 0
                       :port-time 0.00
                       :amp 0.20
                       :cc7->volume 0})
