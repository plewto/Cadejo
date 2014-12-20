(println "-->    alias data")

(ns cadejo.instruments.alias.data
  (:use [cadejo.instruments.alias.program]))
 
;; --------------------------------------------------------------------------- 0 A Sour God
;;
(save-program 0 "A Sour God" 
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.083 0.233 0.035 0.070  :pk 0.762 :bp 1.000 :sus 0.571 :invert 0)
  (env2   0.029 0.074 0.014 0.052  :pk 0.814 :bp 1.000 :sus 0.736 :invert 0)
  (env3   0.065 0.082 11.584 0.074  :pk 1.000 :bp 0.982 :sus 0.252)
  (lfo1     :fm1   [:con    7.624] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.731] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    3.801] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:env1   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -2 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :div1           :reset :off
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (divider1 :p1 +6.000 :p3 -5.000 :p5 +7.000 :p7 +0.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +7.000 :p4 -7.000 :p6 -7.000 :p8 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    9.508])
  (sh       :rate 3.152 :src :div2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.071] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:period 0.736] :e2 [:con    1.000] 
            :f1 [:div    0.418] :f2 [:press  0.989] 
            :g1 [:ccb    0.539] :g2 [:con    1.000] 
            :h1 [:gate   0.710] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.578 :bias 70.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.015 0.00])
  (osc1-wave 1.000             :w1  [:g      0.000     ] :w2  [:off    0.210     ])
  (osc1-amp  -6 :pan -0.62     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.821 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:a      0.030 0.00])
  (osc2-wave 0.648             :w1  [:b      0.198     ] :w2  [:off    -0.283     ])
  (osc2-amp  +0 :pan +0.46     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.369 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.035 0.00])
  (osc3-wave 0.055             :w1  [:b      0.000     ] :w2  [:g      0.013     ])
  (osc3-amp  -9 :pan -0.45     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -12 :pan +0.14 :crackle 0.34     :lp 10000   :hp   999
                               :am1 [:off    0.000 0.00] :am2 [:c      0.969 0.00])
  (ringmod  -99 :pan -0.84 :carrier -0.4     :modulator -1.0
                               :am1 [:e      0.819 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   809 :fm1  [:c          -3449] :fm2 [:h       3913]
           :mode 0.00              :res  [0.80 :off    0.00] :pan [+0.84 :d      -0.75])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   354 :fm1  [:c           7140] :fm2 [:off    -6365]
                                   :res  [0.87 :off    0.00] :pan [+0.87 :off    -0.95])
  ;; EFX
  (dry       +0)
  (pshifter -15 :ratio [2.230 :e      3.100] :rand 0.03 :spread 0.23)
  (flanger  -99 :lfo [0.032 0.373] :mod [:off    0.466] :fb +0.29 :xmix 0.25)
  (echo1    -99 :delay [1.000 :off    0.000] :fb -0.18 :damp 0.03 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.600 :off    0.000] :fb +0.78 :damp 0.50 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 1 Milwaukee's Best
;;
(save-program   1  "Milwaukee's Best"
 (alias-program
  (common  :amp 0.796  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.149 0.172 0.044 0.378  :pk 0.128 :bp 1.000 :sus 0.802 :invert 0)
  (env2   0.735 0.331 0.180 0.181  :pk 1.000 :bp 0.916 :sus 0.759 :invert 0)
  (env3   0.051 0.226 0.250 0.382  :pk 1.000 :bp 0.840 :sus 0.840)
  (lfo1     :fm1   [:con    5.248] :fm2   [:off    0.000]
            :wave1 [:con    0.350] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.745] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.434])
  (lfo3     :fm1   [:con    0.370] :fm2   [:off    0.000]
            :wave1 [:con    0.276] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max  +4 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.17)
  (stepper2 :trig  :lfo2           :reset :div2
            :min  +2 :max  -8 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.10)
  (divider1 :p1 -8.000 :p3 +8.000 :p5 -1.000 :p7 +4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -8.000 :p4 -3.000 :p6 +8.000 :p8 +2.000 :pw 0.15
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:press  5.767])
  (sh       :rate 8.700 :src :div1   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.086] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfo3   0.374] :e2 [:con    1.000] 
            :f1 [:period 0.306] :f2 [:con    1.000] 
            :g1 [:step1  0.988] :g2 [:con    1.000] 
            :h1 [:period 0.381] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.040 0.09])
  (osc1-wave 1.000             :w1  [:b      5.324     ] :w2  [:off    0.710     ])
  (osc1-amp  +0 :pan +0.52     :am1 [:c      0.326 0.49] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.797 0.00])
  (osc2-wave 0.743             :w1  [:b      0.176     ] :w2  [:c      0.415     ])
  (osc2-amp -15 :pan -0.45     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.031 0.00])
  (osc3-wave 0.199             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -12 :pan -0.64     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.13 :crackle 0.85     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.80 :carrier -0.3     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:h      0.394 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  3561 :fm1  [:c          -1157] :fm2 [:off     2525]
           :mode 0.00              :res  [0.00 :off    0.00] :pan [-0.44 :e      +0.36])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  3482 :fm1  [:c           3656] :fm2 [:off    -4494]
                                   :res  [0.93 :off    0.00] :pan [-0.98 :off    +0.06])
  ;; EFX
  (dry      -15)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.11 :spread 0.19)
  (flanger  -15 :lfo [0.077 0.732] :mod [:off    0.025] :fb -0.92 :xmix 0.25)
  (echo1    -99 :delay [1.400 :b      0.473] :fb +0.38 :damp 0.15 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.800 :off    0.019] :fb -0.10 :damp 0.80 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 2 We Have Qustions
;;
(save-program   2 "We Have Qustions"
 (alias-program
  (common  :amp 0.399  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   3.572 0.450 0.968 0.085  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   0.720 0.535 0.663 0.624  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.063 0.504 0.771 0.786  :pk 0.938 :bp 1.000 :sus 0.948)
  (lfo1     :fm1   [:con    5.514] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    6.724] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    1.974] :fm2   [:env2   0.977]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.603])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -2 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -2 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.25)
  (divider1 :p1 -8.000 :p3 -6.000 :p5 -8.000 :p7 -6.000 :pw 0.67
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +3.000 :p4 -1.000 :p6 +6.000 :p8 -3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:lfo1   1.335])
  (sh       :rate 2.733 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.057] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:sh     0.803] :e2 [:con    1.000] 
            :f1 [:div2   0.531] :f2 [:con    1.000] 
            :g1 [:gate   0.200] :g2 [:con    1.000] 
            :h1 [:env3   0.327] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.097 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    6.862     ])
  (osc1-amp  +0 :pan +0.33     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.200 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.054 0.00])
  (osc2-wave 0.586             :w1  [:d      0.266     ] :w2  [:off    0.107     ])
  (osc2-amp  -6 :pan -0.27     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.200 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.038 0.00])
  (osc3-wave 0.775             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -9 :pan -0.60     :am1 [:b      0.771 0.95] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +1.00 :crackle 0.34     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:d      0.946 0.81])
  (ringmod   -6 :pan -0.35 :carrier +0.8     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 10478 :fm1  [:c            397] :fm2 [:off     5276]
           :mode 0.50              :res  [0.51 :off    0.00] :pan [+0.80 :off    +0.22])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  3787 :fm1  [:c          -3059] :fm2 [:f        762]
                                   :res  [0.92 :off    0.00] :pan [+0.10 :f      -0.31])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.05 :spread 0.08)
  (flanger   -9 :lfo [0.102 0.252] :mod [:off    0.737] :fb +0.73 :xmix 0.25)
  (echo1    -99 :delay [1.700 :off    0.000] :fb +0.19 :damp 0.07 :gate [:b      0.14] :pan -0.50)
  (echo2    -99 :delay [1.200 :off    0.000] :fb +0.59 :damp 0.21 :gate [:off    0.64] :pan +0.50)))

;; --------------------------------------------------------------------------- 3 Driving Through Tunnels
;;
(save-program   3   "Driving Through Tunnels" "Slow Attack"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.789 3.576 10.834 0.732  :pk 0.639 :bp 1.000 :sus 0.974 :invert 0)
  (env2   8.827 9.101 6.703 10.303  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   9.417 7.016 8.923 0.986  :pk 1.000 :bp 0.819 :sus 0.056)
  (lfo1     :fm1   [:con    6.365] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.686] :fm2   [:ccb    1.986]
            :wave1 [:con    0.500] :wave2 [:off    0.049])
  (lfo3     :fm1   [:con    1.080] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.337])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -5 :step  -3 :ivalue  +2 :bias 0.0 :scale 0.14)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max +11 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.08)
  (divider1 :p1 -4.000 :p3 +5.000 :p5 +6.000 :p7 +0.000 :pw 0.27
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +0.000 :p4 +3.000 :p6 +5.000 :p8 -2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    5.731])
  (sh       :rate 2.676 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.066] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:press  0.938] :e2 [:con    1.000] 
            :f1 [:period 0.742] :f2 [:con    1.000] 
            :g1 [:press  0.398] :g2 [:con    1.000] 
            :h1 [:sh     0.765] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 0.849 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.779 0.04])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:d      1.009     ])
  (osc1-amp -99 :pan -0.48     :am1 [:b      0.576 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 3.095 :bias 260.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.129 0.09])
  (osc2-wave 0.718             :w1  [:d      0.412     ] :w2  [:off    0.449     ])
  (osc2-amp -12 :pan +0.56     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.341 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.072 0.00])
  (osc3-wave 0.309             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -9 :pan +0.08     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise     +0 :pan +0.18 :crackle 0.25     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.48 :carrier -0.1     :modulator -1.0
                               :am1 [:d      0.528 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  3524 :fm1  [:c           2146] :fm2 [:off    -2163]
           :mode 0.00              :res  [0.29 :off    0.00] :pan [+0.55 :off    +0.02])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   669 :fm1  [:c           4867] :fm2 [:off    -9902]
                                   :res  [0.46 :off    0.00] :pan [+0.39 :off    +0.52])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 1.00 :spread 0.04)
  (flanger   -6 :lfo [0.082 0.639] :mod [:off    0.281] :fb -0.86 :xmix 0.25)
  (echo1    -99 :delay [2.000 :off    0.000] :fb -0.33 :damp 0.21 :gate [:g      0.71] :pan -0.50)
  (echo2    -99 :delay [0.200 :off    0.000] :fb +0.10 :damp 0.94 :gate [:off    0.19] :pan +0.50)))



;; --------------------------------------------------------------------------- 4 Angry Flanger
;;
(save-program   4 "Angry Flanger"
 (alias-program
  (common  :amp 0.100  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.050 0.006 0.013 0.033  :pk 1.000 :bp 0.801 :sus 0.086 :invert 1)
  (env2   0.034 0.049 0.014 0.030  :pk 0.693 :bp 1.000 :sus 0.800 :invert 0)
  (env3   0.038 0.025 0.007 0.045  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    4.743] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.539] :fm2   [:gate   5.245]
            :wave1 [:con    0.023] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    5.418] :fm2   [:ccb    7.814]
            :wave1 [:con    0.500] :wave2 [:off    0.850])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +15 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max +12 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -6.000 :p3 -1.000 :p5 -9.000 :p7 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -9.000 :p4 +4.000 :p6 +2.000 :p8 -3.000 :pw 0.41
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    0.146])
  (sh       :rate 1.414 :src :step2  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.010] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:period 0.622] :e2 [:con    1.000] 
            :f1 [:gate   0.973] :f2 [:con    1.000] 
            :g1 [:lfo3   0.773] :g2 [:con    1.000] 
            :h1 [:gate   0.340] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.745 0.07])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    2.242     ])
  (osc1-amp  +0 :pan +0.49     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.021 0.08])
  (osc2-wave 0.702             :w1  [:d      0.167     ] :w2  [:off    -0.461     ])
  (osc2-amp  +0 :pan -0.63     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:e      0.021 0.09])
  (osc3-wave 0.565             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -12 :pan -0.44     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.71 :crackle 0.44     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:c      0.719 0.65])
  (ringmod  -99 :pan +0.61 :carrier -0.2     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:f      0.606 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq    55 :fm1  [:c           4293] :fm2 [:off    -2953]
           :mode 0.00              :res  [0.39 :off    0.00] :pan [+0.10 :b      -0.95])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  4007 :fm1  [:c          -1169] :fm2 [:off    -4915]
                                   :res  [0.43 :off    0.00] :pan [-0.24 :h      +0.33])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.24 :spread 0.91)
  (flanger   -6 :lfo [0.031 0.628] :mod [:c      0.032] :fb -0.99 :xmix 0.25)
  (echo1    -99 :delay [1.700 :off    0.000] :fb +0.09 :damp 0.41 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [2.125 :a      0.000] :fb +0.33 :damp 0.11 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 5 Confused Priest
;;
(save-program   5 "Confused Priest"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.689 0.638 8.591 0.982  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   0.532 0.803 0.768 0.539  :pk 0.214 :bp 1.000 :sus 0.854 :invert 0)
  (env3   0.833 0.652 0.979 0.926  :pk 0.569 :bp 1.000 :sus 0.863)
  (lfo1     :fm1   [:con    5.867] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    4.474] :fm2   [:env2   3.089]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    0.411] :fm2   [:gate   0.946]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -3 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.20)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -8 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.10)
  (divider1 :p1 +9.000 :p3 -9.000 :p5 -1.000 :p7 +9.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +5.000 :p4 -8.000 :p6 -8.000 :p8 +9.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    1.219])
  (sh       :rate 8.121 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.034] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:step2  0.067] :e2 [:env2   0.787] 
            :f1 [:div2   0.889] :f2 [:con    1.000] 
            :g1 [:ccb    0.917] :g2 [:con    1.000] 
            :h1 [:env3   0.945] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 0.889 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.671 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.371     ])
  (osc1-amp -12 :pan -0.42     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 0.802 :bias 224.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.016 0.00])
  (osc2-wave 0.526             :w1  [:d      0.463     ] :w2  [:off    0.404     ])
  (osc2-amp  +0 :pan -0.09     :am1 [:off    0.000 0.00] :am2 [:b      0.094 0.52])

  (osc3-freq 2.231 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.066 0.05])
  (osc3-wave 0.443             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.65     :am1 [:g      0.082 0.00] :am2 [:off    0.000 0.00])

  (noise     -9 :pan +0.96 :crackle 0.70     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:e      0.671 0.00])
  (ringmod  -99 :pan -0.93 :carrier +0.1     :modulator -1.0
                               :am1 [:off    0.000 0.05] :am2 [:a      0.781 0.05])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 11471 :fm1  [:c          -3165] :fm2 [:d      -3842]
           :mode 0.00              :res  [0.29 :off    0.00] :pan [+0.59 :off    +0.39])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   601 :fm1  [:c           4664] :fm2 [:off     3585]
                                   :res  [0.25 :off    0.00] :pan [+0.63 :off    +0.06])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.07 :spread 0.14)
  (flanger  -99 :lfo [0.550 0.000] :mod [:off    0.510] :fb -0.03 :xmix 0.25)
  (echo1     +0 :delay [0.600 :f      0.696] :fb -0.87 :damp 0.78 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.200 :off    0.584] :fb +0.93 :damp 0.23 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 6 Thora Birch
;;
(save-program   6 "Thora Birch"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.967 2.284 1.371 4.350  :pk 0.412 :bp 1.000 :sus 0.856 :invert 0)
  (env2   0.383 3.756 0.088 2.151  :pk 1.000 :bp 0.297 :sus 0.848 :invert 0)
  (env3   3.263 1.706 2.445 3.374  :pk 1.000 :bp 0.930 :sus 0.703)
  (lfo1     :fm1   [:con    4.195] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.847] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    4.864] :fm2   [:lfo1   4.648]
            :wave1 [:con    0.927] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max -15 :step  -6 :ivalue  +2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :div            :reset :div1
            :min  -2 :max +10 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.08)
  (divider1 :p1 +4.000 :p3 +0.000 :p5 +3.000 :p7 -8.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -8.000 :p4 -5.000 :p6 +7.000 :p8 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    0.012])
  (sh       :rate 9.652 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.002] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:sh     0.169] :e2 [:con    1.000] 
            :f1 [:press  0.763] :f2 [:ccb    0.559] 
            :g1 [:gate   0.074] :g2 [:con    1.000] 
            :h1 [:lfo3   0.962] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.425 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.021 0.01])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    6.740     ])
  (osc1-amp  -6 :pan -0.65     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 0.524 :bias 153   :fm1 [:a      1.000 0.00] :fm2 [:off    0.093 0.07])
  (osc2-wave 0.713             :w1  [:d      0.349     ] :w2  [:off    -0.343     ])
  (osc2-amp  +0 :pan +0.30     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.923 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:f      0.058 0.00])
  (osc3-wave 0.567             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.62     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.96 :crackle 0.71     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.21 :carrier -0.3     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  8758 :fm1  [:b           2878] :fm2 [:off     6356]
           :mode 0.50              :res  [0.16 :off    0.00] :pan [-0.50 :off    +0.13])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  8806 :fm1  [:b          -7753] :fm2 [:f       1701]
                                   :res  [0.22 :off    0.00] :pan [+0.50 :off    +0.52])
  ;; EFX
  (dry       -6)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.01 :spread 0.01)
  (flanger  -15 :lfo [0.048 0.576] :mod [:f      0.565] :fb -0.64 :xmix 0.25)
  (echo1    -99 :delay [1.500 :off    0.000] :fb -0.34 :damp 0.79 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.000 :off    0.000] :fb -0.51 :damp 0.27 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 7 Brecon
;;
(save-program   7 "Brecon"
 (alias-program
  (common  :amp 0.564  :port-time 0.312 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   6.310 3.534 8.370 7.290  :pk 1.000 :bp 0.923 :sus 0.811 :invert 1)
  (env2   3.710 0.153 6.667 11.758  :pk 1.000 :bp 0.629 :sus 0.943 :invert 0)
  (env3   0.035 3.920 5.135 11.088  :pk 1.000 :bp 0.930 :sus 0.716)
  (lfo1     :fm1   [:con    3.515] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.816] :fm2   [:env1   1.092]
            :wave1 [:con    0.063] :wave2 [:lfnse  0.500])
  (lfo3     :fm1   [:con    3.351] :fm2   [:off    0.000]
            :wave1 [:con    0.409] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max -10 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.08)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max +13 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -9.000 :p3 -6.000 :p5 +5.000 :p7 +8.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +4.000 :p4 -9.000 :p6 +8.000 :p8 +4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    3.554])
  (sh       :rate 2.740 :src :lfo2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.008] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:sh     0.627] :e2 [:div2   0.423] 
            :f1 [:sh     0.506] :f2 [:con    1.000] 
            :g1 [:lfnse  0.200] :g2 [:con    1.000] 
            :h1 [:lfo2   0.538] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.667 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.007 0.01])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.975     ])
  (osc1-amp  -9 :pan -0.63     :am1 [:a      0.831 0.03] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.054 0.04])
  (osc2-wave 0.610             :w1  [:d      0.263     ] :w2  [:off    -0.157     ])
  (osc2-amp -15 :pan +0.17     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.250 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.091 0.00])
  (osc3-wave 0.258             :w1  [:b      0.701     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.09     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -15 :pan -0.04 :crackle 0.31     :lp 10000   :hp  3300
                               :am1 [:h      0.056 0.36] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.23 :carrier -0.9     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 12543 :fm1  [:c           -325] :fm2 [:off    -2054]
           :mode 0.25              :res  [0.35 :off    0.00] :pan [+0.87 :e      -0.33])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  2497 :fm1  [:c           1494] :fm2 [:off    -3715]
                                   :res  [0.73 :off    0.00] :pan [-0.25 :off    +0.43])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.95 :spread 0.10)
  (flanger   +0 :lfo [0.063 0.737] :mod [:off    0.316] :fb -0.26 :xmix 0.25)
  (echo1    -99 :delay [0.600 :off    0.000] :fb -0.41 :damp 0.99 :gate [:h      0.54] :pan -0.50)
  (echo2     -6 :delay [1.300 :off    0.000] :fb -0.95 :damp 0.85 :gate [:off    0.19] :pan +0.50)))


;; --------------------------------------------------------------------------- 8 "Eurosong Contest"
;;
(save-program   8 "Eurosong Contest"
 (alias-program
  (common  :amp 0.100  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.260 0.393 0.462 0.495  :pk 1.000 :bp 0.901 :sus 0.901 :invert 0)
  (env2   0.009 0.242 0.003 0.224  :pk 1.000 :bp 0.995 :sus 0.756 :invert 0)
  (env3   0.321 0.391 0.303 0.143  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    3.310] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.075] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:env1   0.597])
  (lfo3     :fm1   [:con    3.740] :fm2   [:gate   2.789]
            :wave1 [:con    0.500] :wave2 [:env2   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +13 :step  +3 :ivalue  -2 :bias 0.0 :scale 0.07)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -9 :step  -5 :ivalue  +2 :bias 0.0 :scale 0.09)
  (divider1 :p1 +5.000 :p3 +5.000 :p5 -8.000 :p7 +9.000 :pw 0.10
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +7.000 :p4 +0.000 :p6 -3.000 :p8 -3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    8.627])
  (sh       :rate 5.642 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.036] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:step2  0.727] :e2 [:con    1.000] 
            :f1 [:press  0.472] :f2 [:con    1.000] 
            :g1 [:div    0.897] :g2 [:con    1.000] 
            :h1 [:ccc    0.412] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 2.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:e      0.042 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    7.179     ])
  (osc1-amp  +0 :pan -0.84     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.330 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:d      0.281 0.00])
  (osc2-wave 0.669             :w1  [:d      0.301     ] :w2  [:off    -0.319     ])
  (osc2-amp  +0 :pan +0.99     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.330 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.052 0.00])
  (osc3-wave 0.585             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -15 :pan -0.42     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.69 :crackle 0.98     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -15 :pan +0.26 :carrier +0.7     :modulator -1.0
                               :am1 [:c      0.292 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   501 :fm1  [:b           6077] :fm2 [:off     1674]
           :mode 0.25              :res  [0.87 :off    0.00] :pan [+0.70 :off    -0.56])
  (fold    :gain 1.000 :mix -0.823 :clip [0.28 :off    0.00])
  (filter2 :gain 1.000 :freq   143 :fm1  [:c           1387] :fm2 [:off    -7849]
                                   :res  [0.92 :off    0.00] :pan [+0.67 :off    +0.61])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.09 :spread 0.50)
  (flanger   -3 :lfo [0.050 0.607] :mod [:off    0.729] :fb -0.95 :xmix 0.25)
  (echo1    -99 :delay [0.900 :off    0.000] :fb -0.36 :damp 0.61 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.200 :off    0.000] :fb +0.30 :damp 0.80 :gate [:e      0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 9  Ertwig
;;
(save-program   9 "Ertwig"
 (alias-program
  (common  :amp 0.790  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.247 0.066 0.065 0.037  :pk 1.000 :bp 0.955 :sus 0.955 :invert 0)
  (env2   0.358 0.055 0.020 0.091  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.002 0.032 0.049 0.089  :pk 1.000 :bp 0.949 :sus 0.977)
  (lfo1     :fm1   [:con    6.530] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.864] :fm2   [:lfo1   0.466]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.422])
  (lfo3     :fm1   [:con    1.536] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -3 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.20)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max  +6 :step  +4 :ivalue  -2 :bias 0.0 :scale 0.13)
  (divider1 :p1 +8.000 :p3 -1.000 :p5 -1.000 :p7 +2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +2.000 :p4 -8.000 :p6 -1.000 :p8 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    1.454])
  (sh       :rate 5.287 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.067] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div1   0.563] :e2 [:con    1.000] 
            :f1 [:env3   0.834] :f2 [:con    1.000] 
            :g1 [:press  0.296] :g2 [:con    1.000] 
            :h1 [:step1  0.430] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.500 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:d      0.011 0.04])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    1.821     ])
  (osc1-amp  +0 :pan -0.11     :am1 [:g      0.731 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.010 0.00])
  (osc2-wave 0.706             :w1  [:d      0.218     ] :w2  [:off    -0.297     ])
  (osc2-amp  +0 :pan -0.26     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 2.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.009 0.00])
  (osc3-wave 0.247             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.57     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +1.00 :crackle 0.32     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.79 :carrier -0.4     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  5702 :fm1  [:c          -6492] :fm2 [:off    -4933]
           :mode 0.00              :res  [0.08 :off    0.00] :pan [+0.71 :off    +0.90])
  (fold    :gain 1.000 :mix +0.954 :clip [0.03 :off    0.00])
  (filter2 :gain 1.000 :freq 15229 :fm1  [:g          -3600] :fm2 [:off     6606]
                                   :res  [0.84 :off    0.00] :pan [+0.31 :d      -0.51])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.22 :spread 0.10)
  (flanger  -99 :lfo [0.049 0.453] :mod [:off    0.152] :fb +0.29 :xmix 0.25)
  (echo1    -99 :delay [0.500 :off    0.000] :fb +0.66 :damp 0.11 :gate [:d      0.47] :pan -0.50)
  (echo2    -99 :delay [0.667 :d      0.000] :fb -0.95 :damp 0.08 :gate [:off    0.01] :pan +0.50)))


;; --------------------------------------------------------------------------- 10 Nothing On TV
;;
(save-program 10 "Nothing On TV"
 (alias-program
  (common  :amp 0.800  :port-time 0.295 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.141 3.103 1.306 3.093  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   2.202 1.316 3.672 3.012  :pk 1.000 :bp 0.958 :sus 0.079 :invert 0)
  (env3   3.685 2.135 2.294 3.889  :pk 1.000 :bp 0.850 :sus 0.179)
  (lfo1     :fm1   [:con    3.689] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.469] :fm2   [:ccc    3.575]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.500])
  (lfo3     :fm1   [:con    3.102] :fm2   [:env1   0.619]
            :wave1 [:con    0.500] :wave2 [:gate   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max -10 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.08)
  (stepper2 :trig  :gate           :reset :off
            :min  +2 :max -12 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 +1.000 :p3 +3.000 :p5 +0.000 :p7 -1.000 :pw 0.60
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -1.000 :p4 +3.000 :p6 -7.000 :p8 -4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    4.455])
  (sh       :rate 0.715 :src :div2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.075] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:sh     0.291] :e2 [:con    1.000] 
            :f1 [:lfo2   0.111] :f2 [:lfo3   0.057] 
            :g1 [:step2  0.654] :g2 [:con    1.000] 
            :h1 [:div    0.367] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.622 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.068 0.00])
  (osc1-wave 1.000             :w1  [:h      0.000     ] :w2  [:off    7.333     ])
  (osc1-amp  +0 :pan +0.99     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 2.155 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.091 0.00])
  (osc2-wave 0.729             :w1  [:d      0.088     ] :w2  [:off    0.456     ])
  (osc2-amp -99 :pan +0.12     :am1 [:off    0.000 0.00] :am2 [:h      0.216 0.50])

  (osc3-freq 1.747 :bias 190.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.062 0.00])
  (osc3-wave 0.583             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -12 :pan +0.61     :am1 [:h      0.193 0.00] :am2 [:off    0.000 0.00])

  (noise     +0 :pan -0.26 :crackle 0.41     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.64 :carrier -0.4     :modulator -1.0
                               :am1 [:f      0.736 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   258 :fm1  [:c           5036] :fm2 [:off    -5829]
           :mode 0.25              :res  [0.80 :off    0.00] :pan [+0.94 :f      +0.38])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  5468 :fm1  [:c          -7233] :fm2 [:off    -3258]
                                   :res  [0.35 :off    0.00] :pan [+0.43 :off    +0.39])
  ;; EFX
  (dry       +0)
  (pshifter  -9 :ratio [2.807 :c      2.474] :rand 0.14 :spread 0.04)
  (flanger  -99 :lfo [1.271 0.199] :mod [:off    0.564] :fb -0.76 :xmix 0.25)
  (echo1    -12 :delay [0.600 :off    0.000] :fb -0.63 :damp 0.25 :gate [:off    0.00] :pan -0.50)
  (echo2    -12 :delay [0.900 :off    0.000] :fb -0.22 :damp 0.90 :gate [:f      0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 11 Nyquist
;;
(save-program   11 "Nyquist"
 (alias-program
  (common  :amp 0.800  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.133 0.212 0.224 0.361  :pk 1.000 :bp 0.920 :sus 0.920 :invert 0)
  (env2   0.013 0.943 0.232 0.225  :pk 1.000 :bp 0.962 :sus 0.962 :invert 0)
  (env3   11.945 0.323 0.400 3.880  :pk 1.000 :bp 0.959 :sus 0.212)
  (lfo1     :fm1   [:con    6.331] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    6.093] :fm2   [:env1   2.170]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    0.516] :fm2   [:lfo1   3.692]
            :wave1 [:con    0.500] :wave2 [:ccc    0.500])
  (stepper1 :trig  :off            :reset :off
            :min  -2 :max +10 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.08)
  (stepper2 :trig  :lfo2           :reset :div
            :min  -2 :max  +4 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.17)
  (divider1 :p1 -8.000 :p3 +0.000 :p5 +6.000 :p7 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +1.000 :p4 -7.000 :p6 -8.000 :p8 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:ccc    9.480])
  (sh       :rate 9.732 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.031] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div1   0.797] :e2 [:con    1.000] 
            :f1 [:lfnse  0.892] :f2 [:con    1.000] 
            :g1 [:cca    0.825] :g2 [:con    1.000] 
            :h1 [:ccb    0.783] :h2 [:ccb    0.076] )
  ;; OSCILLATORS
  (osc1-freq 4.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:b      0.014 0.00])
  (osc1-wave 1.000             :w1  [:f      5.616     ] :w2  [:h      0.133     ])
  (osc1-amp  +0 :pan +0.13     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.042 0.00])
  (osc2-wave 0.566             :w1  [:d      0.435     ] :w2  [:g      0.268     ])
  (osc2-amp  +0 :pan -0.88     :am1 [:off    0.000 0.00] :am2 [:h      0.905 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.031 0.00])
  (osc3-wave 0.317             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -15 :pan +0.84     :am1 [:h      0.708 0.17] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.42 :crackle 0.19     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:b      0.873 0.00])
  (ringmod  -99 :pan -0.59 :carrier +0.4     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  4096 :fm1  [:c          -1842] :fm2 [:off     3574]
           :mode 0.00              :res  [0.10 :off    0.00] :pan [+0.13 :off    +0.49])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  5829 :fm1  [:c          -6805] :fm2 [:off     1445]
                                   :res  [0.13 :off    0.00] :pan [+0.62 :off    -0.37])
  ;; EFX
  (dry       +0)
  (pshifter  -3 :ratio [3.354 :off    1.844] :rand 0.23 :spread 0.08)
  (flanger  -99 :lfo [0.041 0.000] :mod [:off    0.056] :fb -0.45 :xmix 0.25)
  (echo1    -99 :delay [2.000 :off    0.000] :fb +0.12 :damp 0.66 :gate [:e      0.03] :pan -0.50)
  (echo2    -99 :delay [0.900 :off    0.000] :fb -0.02 :damp 0.76 :gate [:d      0.57] :pan +0.50)))

;; --------------------------------------------------------------------------- 12 "Highpass Swarm"
;;
(save-program   12  "Highpass Swarm"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.669 0.809 7.641 0.698  :pk 1.000 :bp 0.829 :sus 0.150 :invert 0)
  (env2   0.693 0.067 0.571 0.843  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.919 0.965 0.797 0.515  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    5.446] :fm2   [:off    0.000]
            :wave1 [:con    0.612] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.386] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    1.880] :fm2   [:off    0.000]
            :wave1 [:con    0.519] :wave2 [:lfnse  0.500])
  (stepper1 :trig  :div1           :reset :off
            :min  +2 :max -14 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -4 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.17)
  (divider1 :p1 +2.000 :p3 -3.000 :p5 +0.000 :p7 -8.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +9.000 :p4 +9.000 :p6 -4.000 :p8 -4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    0.465])
  (sh       :rate 9.574 :src :step1  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.102] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div    0.501] :e2 [:con    1.000] 
            :f1 [:period 0.265] :f2 [:con    1.000] 
            :g1 [:ccb    0.349] :g2 [:con    1.000] 
            :h1 [:step1  0.728] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 2.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.073 0.07])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.811     ])
  (osc1-amp -99 :pan +0.84     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.490 0.00])
  (osc2-wave 0.627             :w1  [:d      0.225     ] :w2  [:off    -0.259     ])
  (osc2-amp  +0 :pan +0.32     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 2.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.070 0.00])
  (osc3-wave 0.838             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -3 :pan -0.36     :am1 [:c      0.934 0.71] :am2 [:f      0.886 0.64])

  (noise    -15 :pan +0.22 :crackle 0.85     :lp 10000   :hp  4031
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.65 :carrier +0.1     :modulator -1.0
                               :am1 [:f      0.754 0.16] :am2 [:e      0.645 0.16])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  5194 :fm1  [:c           5940] :fm2 [:off    -9501]
           :mode 0.50              :res  [0.57 :off    0.00] :pan [+0.52 :e      +0.39])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   755 :fm1  [:c           2270] :fm2 [:b       6166]
                                   :res  [0.56 :off    0.00] :pan [+0.31 :off    +0.51])
  ;; EFX
  (dry       +0)
  (pshifter  -3 :ratio [3.300 :f      3.199] :rand 0.20 :spread 0.12)
  (flanger   -9 :lfo [0.392 0.531] :mod [:off    0.174] :fb +0.72 :xmix 0.25)
  (echo1    -99 :delay [1.900 :off    0.000] :fb +0.48 :damp 0.55 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.100 :off    0.000] :fb +0.62 :damp 0.11 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 13 More Gold
;;
(save-program  13 "More Gold"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   2.771 2.580 1.275 3.336  :pk 0.265 :bp 1.000 :sus 0.836 :invert 0)
  (env2   2.020 1.731 0.758 0.332  :pk 0.607 :bp 1.000 :sus 0.708 :invert 0)
  (env3   2.644 0.609 3.999 3.093  :pk 1.000 :bp 0.916 :sus 0.733)
  (lfo1     :fm1   [:con    4.657] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.543] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfnse  0.500])
  (lfo3     :fm1   [:con    5.903] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:gate   0.984])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max -14 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :off            :reset :sh
            :min  +2 :max -15 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.06)
  (divider1 :p1 -8.000 :p3 -2.000 :p5 +3.000 :p7 -6.000 :pw 0.98
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +3.000 :p4 +9.000 :p6 +5.000 :p8 +3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:lfo3   0.449])
  (sh       :rate 7.767 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.031] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:ccc    0.674] :e2 [:con    1.000] 
            :f1 [:lfo3   0.125] :f2 [:con    1.000] 
            :g1 [:div2   0.325] :g2 [:con    1.000] 
            :h1 [:ccb    0.173] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 3.128 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.592 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.288     ])
  (osc1-amp  -3 :pan -0.19     :am1 [:f      0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 2.596 :bias 246.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.051 0.00])
  (osc2-wave 0.723             :w1  [:d      0.231     ] :w2  [:off    -0.499     ])
  (osc2-amp  +0 :pan -0.63     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 2.073 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.038 0.06])
  (osc3-wave 0.303             :w1  [:b      0.000     ] :w2  [:f      0.554     ])
  (osc3-amp -12 :pan -0.56     :am1 [:off    0.000 0.00] :am2 [:d      0.174 0.97])

  (noise     -9 :pan -0.61 :crackle 0.34     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:d      0.490 0.97])
  (ringmod  -15 :pan +0.11 :carrier -0.2     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix +0.757 :clip [0.59 :off    0.00])
  (filter1 :gain 1.000 :freq   253 :fm1  [:g            508] :fm2 [:off    -2214]
           :mode 0.00              :res  [0.81 :off    0.00] :pan [+0.26 :off    +0.30])
  (fold    :gain 1.000 :mix +0.658 :clip [0.98 :b      0.15])
  (filter2 :gain 1.000 :freq  6267 :fm1  [:c          -6055] :fm2 [:off    -2245]
                                   :res  [0.37 :off    0.00] :pan [+0.92 :off    -0.46])
  ;; EFX
  (dry       +0)
  (pshifter  +0 :ratio [1.614 :off    0.004] :rand 0.18 :spread 0.62)
  (flanger  -12 :lfo [0.099 0.046] :mod [:g      0.685] :fb -0.37 :xmix 0.25)
  (echo1    -99 :delay [1.100 :off    0.000] :fb -0.64 :damp 0.50 :gate [:c      0.30] :pan -0.50)
  (echo2    -99 :delay [1.100 :g      0.000] :fb +0.92 :damp 0.15 :gate [:off    0.15] :pan +0.50)))


;; --------------------------------------------------------------------------- 14 Alian
;;
(save-program   14 "Alian"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   8.516 7.511 11.497 3.108  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   0.033 5.444 0.015 6.971  :pk 1.000 :bp 0.290 :sus 0.998 :invert 0)
  (env3   0.517 5.311 8.373 0.025  :pk 1.000 :bp 0.936 :sus 0.276)
  (lfo1     :fm1   [:con    4.556] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.944] :fm2   [:lfnse  3.513]
            :wave1 [:con    0.500] :wave2 [:env3   0.500])
  (lfo3     :fm1   [:con    1.369] :fm2   [:env2   2.277]
            :wave1 [:con    0.500] :wave2 [:ccc    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +15 :step  +8 :ivalue  -2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -12 :step  -7 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 +0.000 :p3 -3.000 :p5 +4.000 :p7 +6.000 :pw 0.92
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +9.000 :p4 +4.000 :p6 +7.000 :p8 +4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    2.024])
  (sh       :rate 7.782 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.081] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div    0.436] :e2 [:con    1.000] 
            :f1 [:press  0.891] :f2 [:con    1.000] 
            :g1 [:ccb    0.804] :g2 [:con    1.000] 
            :h1 [:lfo3   0.936] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 2.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.164 0.02])
  (osc1-wave 1.000             :w1  [:c      15.810     ] :w2  [:off    7.917     ])
  (osc1-amp  -6 :pan -0.72     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.088 0.09])
  (osc2-wave 0.518             :w1  [:d      0.450     ] :w2  [:off    -0.386     ])
  (osc2-amp  -6 :pan -0.71     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.018 0.00])
  (osc3-wave 0.496             :w1  [:f      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.76     :am1 [:a      0.562 0.82] :am2 [:g      0.897 0.37])

  (noise    -99 :pan -0.25 :crackle 0.14     :lp 10000   :hp    50
                               :am1 [:c      0.440 0.46] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.47 :carrier +0.7     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  4139 :fm1  [:c           3012] :fm2 [:off    -2454]
           :mode 0.25              :res  [0.94 :f      0.69] :pan [+0.92 :c      +0.62])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   319 :fm1  [:c           9402] :fm2 [:off     7307]
                                   :res  [0.76 :off    0.00] :pan [+0.14 :h      +0.64])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.06 :spread 0.07)
  (flanger   -9 :lfo [0.097 0.350] :mod [:off    0.495] :fb -0.46 :xmix 0.25)
  (echo1    -99 :delay [1.100 :off    0.000] :fb +0.17 :damp 0.36 :gate [:off    0.00] :pan -0.50)
  (echo2     +0 :delay [1.200 :off    0.000] :fb -0.66 :damp 0.51 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 15 Piasa Bird
;;
(save-program   15 "Piasa Bird"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.024 7.047 7.944 0.741  :pk 1.000 :bp 0.918 :sus 0.828 :invert 0)
  (env2   9.623 4.716 5.873 0.059  :pk 1.000 :bp 0.846 :sus 0.846 :invert 0)
  (env3   7.916 9.688 10.154 5.724  :pk 1.000 :bp 0.040 :sus 0.264)
  (lfo1     :fm1   [:con    5.915] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    7.109] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfnse  0.143])
  (lfo3     :fm1   [:con    1.210] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +10 :step  +6 :ivalue  -2 :bias 0.0 :scale 0.08)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -12 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -3.000 :p3 +8.000 :p5 +5.000 :p7 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -4.000 :p4 +5.000 :p6 -2.000 :p8 +3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    8.306])
  (sh       :rate 9.880 :src :step2  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.062] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfo3   0.936] :e2 [:step1  0.673] 
            :f1 [:sh     0.887] :f2 [:con    1.000] 
            :g1 [:sh     0.310] :g2 [:con    1.000] 
            :h1 [:period 0.181] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.012 0.09])
  (osc1-wave 1.000             :w1  [:b      6.388     ] :w2  [:off    6.191     ])
  (osc1-amp  -6 :pan -0.00     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.084 0.10])
  (osc2-wave 0.699             :w1  [:d      0.026     ] :w2  [:off    -0.222     ])
  (osc2-amp  -6 :pan +0.01     :am1 [:c      0.885 0.70] :am2 [:g      0.425 0.53])

  (osc3-freq 2.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.026 0.07])
  (osc3-wave 0.689             :w1  [:c      0.784     ] :w2  [:off    0.000     ])
  (osc3-amp -12 :pan -0.44     :am1 [:e      0.657 0.59] :am2 [:c      0.253 0.94])

  (noise     +0 :pan -0.45 :crackle 0.09     :lp 10000   :hp  1230
                               :am1 [:off    0.000 0.00] :am2 [:f      0.636 0.37])
  (ringmod  -99 :pan +0.84 :carrier -0.3     :modulator -1.0
                               :am1 [:d      0.137 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  1456 :fm1  [:c           4103] :fm2 [:a        383]
           :mode 0.00              :res  [0.86 :off    0.00] :pan [+0.38 :f      -0.33])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   717 :fm1  [:c           5590] :fm2 [:off     4986]
                                   :res  [0.95 :off    0.00] :pan [+0.79 :off    -0.41])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.13 :spread 0.10)
  (flanger  -99 :lfo [0.048 0.543] :mod [:g      0.484] :fb +0.92 :xmix 0.25)
  (echo1    -99 :delay [0.600 :off    0.000] :fb +0.46 :damp 0.53 :gate [:off    0.00] :pan -0.50)
  (echo2    -12 :delay [0.600 :off    0.000] :fb +0.47 :damp 0.59 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 16 Osage
;;
(save-program   16 "Osage"
 (alias-program
  (common  :amp 0.800  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   2.371 0.001 1.046 1.040  :pk 1.000 :bp 0.972 :sus 0.051 :invert 0)
  (env2   2.021 2.887 3.921 1.953  :pk 1.000 :bp 0.371 :sus 0.584 :invert 0)
  (env3   1.372 2.692 2.220 3.383  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    5.038] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.970] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.155])
  (lfo3     :fm1   [:con    5.570] :fm2   [:ccc    1.530]
            :wave1 [:con    0.478] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo3           :reset :off
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -8 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.10)
  (divider1 :p1 +1.000 :p3 -7.000 :p5 -2.000 :p7 +3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +0.000 :p4 +8.000 :p6 +9.000 :p8 +0.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    2.886])
  (sh       :rate 1.083 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.010] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfnse  0.118] :e2 [:con    1.000] 
            :f1 [:ccc    0.070] :f2 [:step2  0.233] 
            :g1 [:div1   0.169] :g2 [:con    1.000] 
            :h1 [:ccc    0.717] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:a      0.063 0.09])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.488     ])
  (osc1-amp -15 :pan -0.46     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.016 0.00])
  (osc2-wave 0.714             :w1  [:d      0.167     ] :w2  [:off    0.130     ])
  (osc2-amp  -9 :pan +0.68     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.054 0.00])
  (osc3-wave 0.657             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.52     :am1 [:h      0.562 0.00] :am2 [:e      0.689 0.00])

  (noise    -99 :pan +0.55 :crackle 0.84     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -15 :pan +0.25 :carrier -0.8     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  3871 :fm1  [:c          -5734] :fm2 [:off     3776]
           :mode 0.00              :res  [0.69 :off    0.00] :pan [+0.42 :off    +0.01])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  2579 :fm1  [:c           2302] :fm2 [:off    -3919]
                                   :res  [0.25 :off    0.00] :pan [+0.90 :off    +0.36])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.24 :spread 0.23)
  (flanger  -99 :lfo [0.080 0.231] :mod [:off    0.343] :fb +0.35 :xmix 0.25)
  (echo1    -99 :delay [0.300 :off    0.000] :fb +0.92 :damp 0.33 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.200 :off    0.000] :fb -0.56 :damp 0.60 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 17 Cahokia
;;
(save-program   17 "Cahokia"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.949 1.315 3.692 0.143  :pk 1.000 :bp 0.805 :sus 0.230 :invert 0)
  (env2   3.658 2.595 2.073 2.326  :pk 0.631 :bp 1.000 :sus 0.953 :invert 0)
  (env3   2.605 3.568 1.110 3.943  :pk 1.000 :bp 0.989 :sus 0.757)
  (lfo1     :fm1   [:con    4.870] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.171] :fm2   [:lfnse  3.623]
            :wave1 [:con    0.500] :wave2 [:env3   0.500])
  (lfo3     :fm1   [:con    0.273] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfnse  0.246])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +15 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.06)
  (stepper2 :trig  :off            :reset :off
            :min  -2 :max +13 :step  +7 :ivalue  -2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -1.000 :p3 -1.000 :p5 -4.000 :p7 -1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +6.000 :p4 -9.000 :p6 +2.000 :p8 +4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:press  1.528])
  (sh       :rate 1.570 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.029] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:press  0.892] :e2 [:con    1.000] 
            :f1 [:lfo3   0.705] :f2 [:env2   0.823] 
            :g1 [:press  0.637] :g2 [:con    1.000] 
            :h1 [:lfo2   0.278] :h2 [:div    0.426] )
  ;; OSCILLATORS
  (osc1-freq 2.865 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.480 0.09])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.027     ])
  (osc1-amp -12 :pan -0.07     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 3.183 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.039 0.00])
  (osc2-wave 0.609             :w1  [:d      0.199     ] :w2  [:off    0.159     ])
  (osc2-amp  +0 :pan +0.66     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.439 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.074 0.09])
  (osc3-wave 0.656             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -12 :pan +0.85     :am1 [:off    0.000 0.00] :am2 [:b      0.562 0.00])

  (noise    -99 :pan -0.26 :crackle 0.36     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:g      0.286 0.31])
  (ringmod  -99 :pan -0.19 :carrier +0.3     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   157 :fm1  [:b          -4170] :fm2 [:off    -3483]
           :mode 0.50              :res  [0.88 :off    0.00] :pan [+0.70 :off    +0.64])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq 13804 :fm1  [:c          -4117] :fm2 [:a      -9154]
                                   :res  [0.26 :off    0.00] :pan [+0.92 :off    +0.73])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.21 :spread 0.05)
  (flanger  -12 :lfo [0.061 0.000] :mod [:g      0.208] :fb -0.24 :xmix 0.25)
  (echo1    -99 :delay [1.300 :off    0.000] :fb -0.69 :damp 0.17 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.300 :c      0.000] :fb -0.58 :damp 0.95 :gate [:h      0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 18 Illini
;;
(save-program   18 "Illini"
  (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.250 2.000 2.000 1.000  :pk 1.000 :bp 0.250 :sus 1.000 :invert 0)
  (env2   2.000 1.000 2.000 1.000  :pk 1.000 :bp 0.100 :sus 1.000 :invert 0)
  (env3   0.020 0.000 0.000 0.200  :pk 1.000 :bp 1.000 :sus 1.000 )

  (lfo1     :fm1   [:con    7.000] :fm2   [:cca   -2.000]
            :wave1 [:con    0.500] :wave2 [:cca   -0.300])
  (lfo2     :fm1   [:con    0.500] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo3     :fm1   [:con    5.000] :fm2   [:off    0.000]
            :wave1 [:con    0.433] :wave2 [:off    0.000])

  (stepper1 :trig  :lfo1           :reset :off
            :min -10 :max +10 :step  +1 :ivalue -10 :bias 0.0 :scale 0.10)
  (stepper2 :trig  :lfo2           :reset :off
            :min +10 :max -10 :step  -1 :ivalue +10 :bias 0.0 :scale 0.10)
  (divider1 :p1 +1.000 :p3 +0.333 :p5 +0.200 :p7 +0.142 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (divider2 :p2 +0.500 :p4 +0.250 :p6 +0.167 :p8 +0.125 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (lfnoise  :fm [:con    15.00])
  (sh       :rate 5.000 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.050] :a2 [:cca    1.000] 
            :b1 [:env1   1.000] :b2 [:con    1.000] 
            :c1 [:env2   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:ccb    1.000] :e2 [:con    1.000] 
            :f1 [:env3   1.000] :f2 [:con    1.000] 
            :g1 [:period 1.000] :g2 [:con    1.000] 
            :h1 [:lfo3   1.000] :h2 [:con    1.000])
  ;; OSCILLATORS
  (osc1-freq 0.250 :bias -0.50 :fm1 [:a      1.000 0.00] :fm2 [:off    0.0004 0.00])
  (osc1-wave 1.000             :w1  [:b      8.000     ] :w2  [:off    0.000      ])
  (osc1-amp -12 :pan +1.00     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00 ])

  (osc2-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.000 0.00 ])
  (osc2-wave 0.100             :w1  [:b      0.800     ] :w2  [:d      0.100      ])
  (osc2-amp   0 :pan +0.50     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00 ])

  (osc3-freq 1.500 :bias +0.50 :fm1 [:a      1.000 0.00] :fm2 [:off    0.000 0.00 ])
  (osc3-wave 0.000             :w1  [:b      1.500     ] :w2  [:off    0.000      ])
  (osc3-amp  -9 :pan +0.00     :am1 [:b      0.750 0.20] :am2 [:d      0.250 0.10 ])

  (noise    -99 :pan -0.50 :crackle 0.50     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.50 :carrier +0.0     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [1.00 :off    0.00])
  (filter1 :gain 1.000 :freq   100 :fm1  [:c           5000] :fm2 [:off        0]
           :mode :lp               :res  [0.80 :off    0.00] :pan [+0.50 :off    +0.00])
  (fold    :gain 1.000 :mix  0.000 :clip [0.00 :c      0.20])
  (filter2 :gain 1.000 :freq 10000 :fm1  [:c          -8000] :fm2 [:off        0]
                                   :res  [0.80 :off    0.00] :pan [+0.50 :off    +0.00])
  ;; EFX
  (dry       +0 )
  (pshifter  -9 :ratio [2.000 :h      0.500] :rand 0.01 :spread 0.20)
  (flanger  -99 :lfo [3.149 0.520]    :mod [:off    0.000] :fb -0.50 :xmix 0.25)
  (echo1     -9 :delay [0.100 :g      1.000] :fb +0.90 :damp 0.00 :gate [:off    0.00] :pan -0.75)
  (echo2    -99 :delay [0.101 :g      1.000] :fb -0.90 :damp 0.00 :gate [:f      1.00] :pan +0.75)))



;; --------------------------------------------------------------------------- 19 Ritual Nights
;;
(save-program  20 "Ritual Nights"
 (alias-program
  (common  :amp 0.500  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.177 3.430 2.950 0.054  :pk 0.796 :bp 1.000 :sus 0.786 :invert 0)
  (env2   3.149 1.733 2.176 2.798  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.004 3.692 2.623 7.928  :pk 1.000 :bp 0.027 :sus 0.428)
  (lfo1     :fm1   [:con    3.666] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.780] :fm2   [:off    0.000]
            :wave1 [:con    0.271] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    0.376] :fm2   [:lfo1   6.603]
            :wave1 [:con    0.500] :wave2 [:ccb    0.500])
  (stepper1 :trig  :lfo1           :reset :lfo2
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :div2           :reset :off
            :min  -2 :max +11 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.08)
  (divider1 :p1 -9.000 :p3 -9.000 :p5 -1.000 :p7 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -5.000 :p4 -4.000 :p6 -9.000 :p8 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    9.407])
  (sh       :rate 2.741 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.038] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div    0.122] :e2 [:con    1.000] 
            :f1 [:lfnse  0.893] :f2 [:con    1.000] 
            :g1 [:step2  0.106] :g2 [:lfo1   0.417] 
            :h1 [:ccc    0.793] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 3.208 :bias 98.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.059 0.05])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.250     ])
  (osc1-amp  +0 :pan -0.37     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.818 :bias 12.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.074 0.05])
  (osc2-wave 0.620             :w1  [:d      0.416     ] :w2  [:off    0.046     ])
  (osc2-amp -15 :pan -0.88     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 0.583 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:c      0.036 0.00])
  (osc3-wave 0.939             :w1  [:b      0.000     ] :w2  [:e      1.349     ])
  (osc3-amp  +0 :pan -0.25     :am1 [:h      0.568 0.00] :am2 [:f      0.381 0.00])

  (noise    -99 :pan -0.10 :crackle 0.22     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.07 :carrier -1.0     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   135 :fm1  [:g           3437] :fm2 [:a        612]
           :mode 0.00              :res  [0.05 :off    0.00] :pan [+0.77 :f      -0.14])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   213 :fm1  [:c           1726] :fm2 [:off    -5946]
                                   :res  [0.49 :off    0.00] :pan [+0.38 :off    +0.24])
  ;; EFX
  (dry      -99)
  (pshifter   0 :ratio [2.466 :off    1.536] :rand 0.10 :spread 0.01)
  (flanger    0 :lfo   [0.027 0.128] :mod [:b      0.299] :fb +0.80 :xmix 0.25)
  (echo1    -99 :delay [1.100 :off    0.000] :fb -0.98 :damp 0.20 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.375 :e      0.000] :fb +0.43 :damp 0.08 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 21 Chickasaw
;;
(save-program   20 "Chickasaw"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   6.046 9.094 10.404 0.049  :pk 0.436 :bp 1.000 :sus 0.557 :invert 0)
  (env2   3.041 6.566 3.897 0.833  :pk 0.658 :bp 1.000 :sus 0.583 :invert 0)
  (env3   6.621 1.742 10.870 6.317  :pk 1.000 :bp 0.959 :sus 0.055)
  (lfo1     :fm1   [:con    3.865] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.524] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:env2   0.127])
  (lfo3     :fm1   [:con    3.687] :fm2   [:off    0.000]
            :wave1 [:con    0.277] :wave2 [:off    0.551])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -2 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -12 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -6.000 :p3 -3.000 :p5 +7.000 :p7 +2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -2.000 :p4 -4.000 :p6 +0.000 :p8 +0.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:ccb    9.736])
  (sh       :rate 6.538 :src :div2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.077] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div    0.350] :e2 [:con    1.000] 
            :f1 [:cca    0.003] :f2 [:con    1.000] 
            :g1 [:div2   0.127] :g2 [:con    1.000] 
            :h1 [:gate   0.637] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 3.046 :bias 11.000 :fm1 [:a      1.000 0.00] :fm2 [:a      0.079 0.03])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.687     ])
  (osc1-amp  +0 :pan +0.89     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 3.376 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.061 0.00])
  (osc2-wave 0.675             :w1  [:d      0.126     ] :w2  [:a      0.122     ])
  (osc2-amp  -3 :pan -0.30     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 2.274 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.053 0.00])
  (osc3-wave 0.348             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.35     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.79 :crackle 0.73     :lp 10000   :hp    50
                               :am1 [:h      0.359 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.40 :carrier -0.8     :modulator -1.0
                               :am1 [:h      0.985 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -0.496 :clip [0.84 :off    0.00])
  (filter1 :gain 1.000 :freq  3912 :fm1  [:c           1111] :fm2 [:off    -4614]
           :mode 0.00              :res  [0.21 :a      0.16] :pan [+0.96 :off    +0.63])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  4064 :fm1  [:c           3771] :fm2 [:off     5400]
                                   :res  [0.94 :off    0.00] :pan [+0.98 :off    +0.44])
  ;; EFX
  (dry       -6)
  (pshifter  +0 :ratio [2.934 :off    2.963] :rand 0.01 :spread 0.16)
  (flanger  -99 :lfo [0.042 0.040] :mod [:a      0.069] :fb +0.74 :xmix 0.25)
  (echo1    -99 :delay [0.600 :off    0.000] :fb +0.24 :damp 0.50 :gate [:a      0.19] :pan -0.50)
  (echo2    -99 :delay [1.200 :off    0.000] :fb +0.95 :damp 0.19 :gate [:off    0.96] :pan +0.50)))

;; --------------------------------------------------------------------------- 22 intractable
;;
(save-program  22 "Intractable"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   4.859 10.274 5.856 8.471  :pk 1.000 :bp 0.712 :sus 0.722 :invert 0)
  (env2   10.491 8.950 6.986 0.888  :pk 1.000 :bp 0.788 :sus 0.833 :invert 0)
  (env3   0.060 0.036 5.630 3.533  :pk 0.161 :bp 1.000 :sus 0.973)
  (lfo1     :fm1   [:con    5.036] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    5.986] :fm2   [:ccb    0.301]
            :wave1 [:con    0.500] :wave2 [:env1   0.500])
  (lfo3     :fm1   [:con    2.930] :fm2   [:ccc    0.983]
            :wave1 [:con    0.766] :wave2 [:off    0.500])
  (stepper1 :trig  :off            :reset :off
            :min  -2 :max  +8 :step  +4 :ivalue  -2 :bias 0.0 :scale 0.10)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -12 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -7.000 :p3 +5.000 :p5 +0.000 :p7 -3.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -4.000 :p4 +9.000 :p6 +7.000 :p8 -9.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:lfo3   3.728])
  (sh       :rate 4.855 :src :div1   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.008] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfnse  0.359] :e2 [:con    1.000] 
            :f1 [:env3   0.359] :f2 [:con    1.000] 
            :g1 [:ccc    0.528] :g2 [:con    1.000] 
            :h1 [:sh     0.583] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.667 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.006 0.01])
  (osc1-wave 1.000             :w1  [:b      15.666     ] :w2  [:off    3.931     ])
  (osc1-amp  +0 :pan +0.01     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.053 0.00])
  (osc2-wave 0.512             :w1  [:b      0.220     ] :w2  [:off    0.238     ])
  (osc2-amp -12 :pan -0.68     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:h      0.052 0.00])
  (osc3-wave 0.381             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -9 :pan +0.42     :am1 [:off    0.000 0.00] :am2 [:d      0.257 0.00])

  (noise    -99 :pan +0.24 :crackle 0.79     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.37 :carrier +0.9     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   274 :fm1  [:c           7961] :fm2 [:off    -6281]
           :mode 0.25              :res  [0.94 :off    0.00] :pan [+0.31 :off    +0.54])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   685 :fm1  [:c           2515] :fm2 [:off     -717]
                                   :res  [0.16 :off    0.00] :pan [+0.28 :b      -0.32])
  ;; EFX
  (dry       +0)
  (pshifter -12 :ratio [1.845 :off    3.742] :rand 0.71 :spread 0.22)
  (flanger  -99 :lfo [0.102 0.501] :mod [:off    0.306] :fb -0.77 :xmix 0.25)
  (echo1    -99 :delay [1.200 :off    0.000] :fb +0.69 :damp 0.68 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.900 :d      0.000] :fb +0.24 :damp 0.26 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 23 Quapaw
;;
(save-program   23 "Quapaw"
  (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.000 0.000 0.000 0.000  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   0.000 0.000 0.000 0.000  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.000 0.000 0.000 0.000  :pk 1.000 :bp 1.000 :sus 1.000 )

  (lfo1     :fm1   [:con    7.000] :fm2   [:cca    1.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.500] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo3     :fm1   [:con    0.500] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (stepper1 :trig  :lfo1           :reset :off
            :min -10 :max +10 :step  +1 :ivalue -10 :bias 0.0 :scale 0.10)
  (stepper2 :trig  :lfo2           :reset :off
            :min +10 :max -10 :step  -1 :ivalue +10 :bias 0.0 :scale 0.10)
  (divider1 :p1 +1.000 :p3 +0.333 :p5 +0.200 :p7 +0.142 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (divider2 :p2 +0.500 :p4 +0.250 :p6 +0.167 :p8 +0.125 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (lfnoise  :fm [:con    15.00])
  (sh       :rate 5.000 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.100] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:con    1.000] :e2 [:con    1.000] 
            :f1 [:con    1.000] :f2 [:con    1.000] 
            :g1 [:freq   1.000] :g2 [:con    1.000] 
            :h1 [:period 1.000] :h2 [:con    1.000])
  ;; OSCILLATORS
  (osc1-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.000  0.00])
  (osc1-wave 1.000             :w1  [:b      1.000     ] :w2  [:off    0.000      ])
  (osc1-amp -99 :pan -0.50     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00 ])

  (osc2-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.000 0.00 ])
  (osc2-wave 0.500             :w1  [:d      0.200     ] :w2  [:off    0.000      ])
  (osc2-amp   0 :pan +0.00     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00 ])

  (osc3-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.000 0.00 ])
  (osc3-wave 0.500             :w1  [:b      1.000     ] :w2  [:off    0.000      ])
  (osc3-amp -99 :pan +0.00     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00 ])

  (noise    -99 :pan -0.00 :crackle 0.50     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.50 :carrier +0.0     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [1.00 :off    0.00])
  (filter1 :gain 1.000 :freq 10000 :fm1  [:off            1] :fm2 [:off        0]
           :mode :lp               :res  [0.50 :off    0.00] :pan [+0.50 :off    +0.00])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq 10000 :fm1  [:off            0] :fm2 [:off        0]
                                   :res  [0.50 :off    0.00] :pan [+0.50 :off    +0.00])
  ;; EFX
  (dry        0 )
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.00 :spread 0.00)
  (flanger  -99 :lfo [3.149 0.520]    :mod [:off    0.000] :fb -0.50 :xmix 0.25)
  (echo1      0 :delay [0.010 :h      2.000] :fb -0.95 :damp 0.00 :gate [:c      1.00] :pan -0.50)
  (echo2     -6 :delay [0.250 :off    0.000] :fb +0.50 :damp 0.00 :gate [:c      1.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 24 Room 213
;;
(save-program   24 "Room 213"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   4.897 0.489 5.334 10.474  :pk 1.000 :bp 0.914 :sus 0.874 :invert 0)
  (env2   10.147 11.154 11.570 8.958  :pk 1.000 :bp 0.937 :sus 0.808 :invert 0)
  (env3   5.154 2.169 6.276 0.786  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    7.863] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.118] :fm2   [:lfnse  2.071]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    1.784] :fm2   [:lfo1   0.020]
            :wave1 [:con    0.500] :wave2 [:env3   0.500])
  (stepper1 :trig  :lfo1           :reset :gate
            :min  -2 :max  +9 :step  +3 :ivalue  -2 :bias 0.0 :scale 0.09)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -6 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.13)
  (divider1 :p1 +8.000 :p3 +4.000 :p5 -5.000 :p7 +0.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +6.000 :p4 +3.000 :p6 -7.000 :p8 +1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:press  4.824])
  (sh       :rate 6.884 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.087] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div1   0.443] :e2 [:con    1.000] 
            :f1 [:step1  0.366] :f2 [:con    1.000] 
            :g1 [:period 0.930] :g2 [:con    1.000] 
            :h1 [:step1  0.298] :h2 [:cca    0.594] )
  ;; OSCILLATORS
  (osc1-freq 2.275 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.809 0.04])
  (osc1-wave 1.000             :w1  [:a      0.000     ] :w2  [:off    7.967     ])
  (osc1-amp  -3 :pan -0.65     :am1 [:off    0.000 0.00] :am2 [:a      0.479 0.94])

  (osc2-freq 3.199 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.042 0.00])
  (osc2-wave 0.536             :w1  [:d      0.043     ] :w2  [:off    0.128     ])
  (osc2-amp  -9 :pan -0.30     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 0.824 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.093 0.00])
  (osc3-wave 0.796             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.89     :am1 [:off    0.000 0.00] :am2 [:h      0.465 0.63])

  (noise    -99 :pan -0.72 :crackle 0.76     :lp 10000   :hp  1056
                               :am1 [:off    0.000 0.00] :am2 [:b      0.972 0.17])
  (ringmod  -99 :pan -0.31 :carrier +0.8     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  9475 :fm1  [:h           9205] :fm2 [:off     4254]
           :mode 0.00              :res  [0.77 :off    0.00] :pan [-0.82 :e      -0.76])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  1934 :fm1  [:c           1698] :fm2 [:off     5173]
                                   :res  [0.51 :off    0.00] :pan [+0.62 :h      -0.65])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.04 :spread 0.03)
  (flanger  -99 :lfo [3.250 0.323] :mod [:off    0.529] :fb -0.73 :xmix 0.25)
  (echo1     -9 :delay [0.800 :e      0.039] :fb -0.60 :damp 0.83 :gate [:c      0.11] :pan -0.50)
  (echo2    -99 :delay [1.400 :off    0.896] :fb -0.56 :damp 0.39 :gate [:off    0.49] :pan +0.50)))


;; --------------------------------------------------------------------------- 25 Yuanjiang
;;
(save-program  26   "Yuanjiang"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.677 3.091 0.718 0.984  :pk 1.000 :bp 0.931 :sus 0.846 :invert 0)
  (env2   0.686 0.918 0.850 0.973  :pk 1.000 :bp 0.608 :sus 0.973 :invert 0)
  (env3   0.934 0.597 0.840 0.534  :pk 0.966 :bp 1.000 :sus 0.716)
  (lfo1     :fm1   [:con    6.742] :fm2   [:off    0.000]
            :wave1 [:con    0.225] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.323] :fm2   [:off    0.000]
            :wave1 [:con    0.148] :wave2 [:gate   0.500])
  (lfo3     :fm1   [:con    3.953] :fm2   [:off    0.000]
            :wave1 [:con    0.558] :wave2 [:env2   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo3           :reset :off
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (divider1 :p1 -2.000 :p3 +4.000 :p5 -7.000 :p7 +5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +3.000 :p4 +8.000 :p6 +1.000 :p8 +5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:lfo2   5.325])
  (sh       :rate 3.600 :src :div1   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.066] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:press  0.906] :e2 [:con    1.000] 
            :f1 [:lfnse  0.439] :f2 [:ccc    0.337] 
            :g1 [:gate   0.259] :g2 [:press  0.270] 
            :h1 [:lfnse  0.115] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.330 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.073 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    1.792     ])
  (osc1-amp -15 :pan -0.68     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.330 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.088 0.08])
  (osc2-wave 0.556             :w1  [:d      0.027     ] :w2  [:off    0.214     ])
  (osc2-amp  +0 :pan -0.36     :am1 [:off    0.000 0.00] :am2 [:e      0.236 0.00])

  (osc3-freq 4.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.180 0.08])
  (osc3-wave 0.994             :w1  [:f      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -9 :pan +0.58     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.28 :crackle 0.58     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.59 :carrier -0.3     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix +0.040 :clip [0.87 :c      0.67])
  (filter1 :gain 1.000 :freq 11127 :fm1  [:c           1055] :fm2 [:a      -1520]
           :mode 0.50              :res  [0.64 :off    0.00] :pan [-0.38 :e      -0.54])
  (fold    :gain 1.000 :mix +0.323 :clip [0.57 :off    0.00])
  (filter2 :gain 1.000 :freq  3893 :fm1  [:c           2341] :fm2 [:off     1519]
                                   :res  [0.27 :off    0.00] :pan [+0.75 :off    +0.36])
  ;; EFX
  (dry       +0)
  (pshifter  -9 :ratio [3.849 :c      1.730] :rand 0.10 :spread 0.52)
  (flanger  -99 :lfo [0.027 0.000] :mod [:b      0.695] :fb -0.93 :xmix 0.25)
  (echo1    -99 :delay [0.500 :off    0.000] :fb +0.23 :damp 0.98 :gate [:c      0.59] :pan -0.50)
  (echo2    -99 :delay [1.400 :off    0.000] :fb -0.86 :damp 0.36 :gate [:off    0.45] :pan +0.50)))


;; --------------------------------------------------------------------------- 26 Send No Money
;;
(save-program 26 "Send No Money"
 (alias-program
  (common  :amp 0.400  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.040 0.015 0.007 0.024  :pk 1.000 :bp 0.407 :sus 0.693 :invert 0)
  (env2   0.045 0.024 0.074 0.792  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env3   0.054 9.499 0.026 0.083  :pk 0.901 :bp 1.000 :sus 0.855)
  (lfo1     :fm1   [:con    6.931] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.086] :fm2   [:lfo1   0.405]
            :wave1 [:con    0.279] :wave2 [:env3   0.616])
  (lfo3     :fm1   [:con    4.255] :fm2   [:env3   2.772]
            :wave1 [:con    0.500] :wave2 [:gate   0.289])
  (stepper1 :trig  :lfo1           :reset :div
            :min  +2 :max  -9 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.09)
  (stepper2 :trig  :div            :reset :off
            :min  -2 :max  +5 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.14)
  (divider1 :p1 +4.000 :p3 -4.000 :p5 +6.000 :p7 -2.000 :pw 0.92
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +7.000 :p4 +8.000 :p6 +7.000 :p8 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    2.104])
  (sh       :rate 4.891 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.065] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:step1  0.069] :e2 [:con    1.000] 
            :f1 [:ccc    0.072] :f2 [:div    0.086] 
            :g1 [:div2   0.699] :g2 [:con    1.000] 
            :h1 [:ccb    0.237] :h2 [:env2   0.684] )
  ;; OSCILLATORS
  (osc1-freq 3.037 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.005 0.08])
  (osc1-wave 1.000             :w1  [:b      10.297     ] :w2  [:off    0.316     ])
  (osc1-amp -15 :pan +0.40     :am1 [:e      0.800 0.50] :am2 [:off    0.000 0.00])

  (osc2-freq 3.494 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.065 0.00])
  (osc2-wave 0.733             :w1  [:d      0.366     ] :w2  [:h      0.038     ])
  (osc2-amp  +0 :pan +0.98     :am1 [:off    0.000 0.00] :am2 [:a      0.917 0.00])

  (osc3-freq 3.057 :bias 115.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.059 0.00])
  (osc3-wave 0.032             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -6 :pan -0.22     :am1 [:off    0.000 0.00] :am2 [:b      0.810 0.00])

  (noise    -99 :pan +0.60 :crackle 0.93     :lp 10000   :hp    50
                               :am1 [:g      0.916 0.43] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.29 :carrier +0.1     :modulator -1.0
                               :am1 [:off    0.000 0.14] :am2 [:g      0.063 0.14])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   220 :fm1  [:c           2451] :fm2 [:off     2146]
           :mode 0.00              :res  [0.32 :off    0.00] :pan [-0.03 :off    +0.89])
  (fold    :gain 1.000 :mix +0.844 :clip [0.68 :f      0.11])
  (filter2 :gain 1.000 :freq 14414 :fm1  [:c           -613] :fm2 [:off    -2787]
                                   :res  [0.47 :off    0.00] :pan [+0.29 :off    +0.56])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.22 :spread 0.10)
  (flanger  -99 :lfo [3.607 0.470] :mod [:a      0.343] :fb -0.29 :xmix 0.25)
  (echo1     -9 :delay [1.900 :off    0.000] :fb +0.22 :damp 0.66 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.900 :off    0.000] :fb -0.18 :damp 0.64 :gate [:off    0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 27 Too Late
;;
(save-program  27 "Too Late" ""
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   1.656 2.421 1.395 2.006  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   8.382 2.693 2.849 3.133  :pk 1.000 :bp 0.936 :sus 0.950 :invert 0)
  (env3   3.233 2.357 1.014 3.957  :pk 1.000 :bp 0.940 :sus 0.854)
  (lfo1     :fm1   [:con    5.573] :fm2   [:off    0.000]
            :wave1 [:con    0.436] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.479] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    3.400] :fm2   [:ccc    0.554]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.500])
  (stepper1 :trig  :gate           :reset :div
            :min  +2 :max  -6 :step  -4 :ivalue  +2 :bias 0.0 :scale 0.13)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max +14 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.06)
  (divider1 :p1 +0.000 :p3 +8.000 :p5 +6.000 :p7 +2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +8.000 :p4 -7.000 :p6 -4.000 :p8 -4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    8.460])
  (sh       :rate 2.101 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.087] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:period 0.881] :e2 [:con    1.000] 
            :f1 [:ccc    0.558] :f2 [:con    1.000] 
            :g1 [:cca    0.267] :g2 [:con    1.000] 
            :h1 [:div1   0.558] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.192 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.784 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.150     ])
  (osc1-amp  -3 :pan -0.38     :am1 [:off    0.000 0.00] :am2 [:g      0.054 0.15])

  (osc2-freq 1.535 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.026 0.05])
  (osc2-wave 0.705             :w1  [:d      0.087     ] :w2  [:off    -0.203     ])
  (osc2-amp -15 :pan +0.84     :am1 [:off    0.000 0.00] :am2 [:f      0.377 0.05])

  (osc3-freq 1.021 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.098 0.02])
  (osc3-wave 0.033             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.15     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.35 :crackle 0.16     :lp 10000   :hp    50
                               :am1 [:g      0.960 0.92] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.63 :carrier -0.4     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  4287 :fm1  [:c          -1818] :fm2 [:off    -1321]
           :mode 0.00              :res  [0.07 :off    0.00] :pan [-0.40 :off    +0.80])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  4361 :fm1  [:c           4917] :fm2 [:off    -2022]
                                   :res  [0.33 :off    0.00] :pan [+0.54 :g      -0.38])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.45 :spread 0.17)
  (flanger   +0 :lfo [0.057 0.192] :mod [:off    0.724] :fb -0.94 :xmix 0.25)
  (echo1    -99 :delay [1.700 :off    0.000] :fb -0.50 :damp 0.65 :gate [:c      0.75] :pan -0.50)
  (echo2    -99 :delay [0.500 :off    0.000] :fb +0.44 :damp 0.83 :gate [:off    0.07] :pan +0.50)))

;; --------------------------------------------------------------------------- 28 Bonng
;;
(save-program  28 "Bonng" ""
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.681 0.748 0.463 0.575  :pk 1.000 :bp 0.840 :sus 0.922 :invert 0)
  (env2   0.893 0.617 0.864 0.555  :pk 1.000 :bp 0.832 :sus 0.233 :invert 0)
  (env3   0.017 0.075 0.970 0.712  :pk 1.000 :bp 0.986 :sus 0.145)
  (lfo1     :fm1   [:con    7.660] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    6.302] :fm2   [:env1   6.157]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    0.870] :fm2   [:ccb    1.055]
            :wave1 [:con    0.027] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -7 :step  -4 :ivalue  +2 :bias 0.0 :scale 0.11)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max +14 :step  +3 :ivalue  -2 :bias 0.0 :scale 0.06)
  (divider1 :p1 +5.000 :p3 +2.000 :p5 -7.000 :p7 -1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +0.000 :p4 -2.000 :p6 +7.000 :p8 +0.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    9.839])
  (sh       :rate 7.192 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.073] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfo2   0.648] :e2 [:con    1.000] 
            :f1 [:sh     0.019] :f2 [:con    1.000] 
            :g1 [:period 0.983] :g2 [:con    1.000] 
            :h1 [:ccc    0.584] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 0.940 :bias 204.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.066 0.00])
  (osc1-wave 1.000             :w1  [:d      0.000     ] :w2  [:h      4.231     ])
  (osc1-amp  -3 :pan -0.48     :am1 [:off    0.000 0.00] :am2 [:b      0.464 0.62])

  (osc2-freq 1.649 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.164 0.00])
  (osc2-wave 0.501             :w1  [:d      0.209     ] :w2  [:g      0.320     ])
  (osc2-amp  +0 :pan -0.37     :am1 [:b      0.528 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 0.677 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.037 0.09])
  (osc3-wave 0.623             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.92     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.61 :crackle 0.05     :lp 10000   :hp  1917
                               :am1 [:a      0.363 0.43] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.36 :carrier -0.2     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:b      0.520 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   122 :fm1  [:c           -585] :fm2 [:off    -2105]
           :mode 0.50              :res  [0.92 :off    0.00] :pan [+0.54 :off    +0.09])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   559 :fm1  [:c           9394] :fm2 [:off    -5887]
                                   :res  [0.47 :off    0.00] :pan [-0.01 :off    -0.38])
  ;; EFX
  (dry       +0)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.08 :spread 0.11)
  (flanger  -99 :lfo [0.275 0.293] :mod [:h      0.738] :fb +0.16 :xmix 0.25)
  (echo1    -99 :delay [1.000 :off    0.000] :fb -0.41 :damp 0.73 :gate [:d      0.11] :pan -0.50)
  (echo2    -99 :delay [1.200 :e      0.000] :fb -0.45 :damp 0.14 :gate [:f      0.46] :pan +0.50)))

;; --------------------------------------------------------------------------- 29 GhostShift1
;;
(save-program  29 "GhostShift1" "Uses gated delay lines"
 (alias-program
  (common  :amp 0.200  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS A     D1    D2    R
  (env1   0.000 0.500 0.500 0.001  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   2.116 0.822 0.500 0.283  :pk 1.000 :bp 0.732 :sus 1.000 :invert 0)
  (env3   0.000 0.500 0.500 0.001  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    7.000] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    7.000] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo3     :fm1   [:con    7.000] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (stepper1 :trig  :lfo1           :reset :off
            :min -10 :max +10 :step  +1 :ivalue -10 :bias 0.0 :scale 0.10)
  (stepper2 :trig  :lfo2           :reset :off
            :min -10 :max +10 :step  +1 :ivalue -10 :bias 0.0 :scale 0.10)
  (divider1 :p1 +0.000 :p3 +0.000 :p5 +0.000 :p7 +1.000 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (divider2 :p2 +0.000 :p4 +0.000 :p6 +0.000 :p8 +1.000 :pw 0.50
            :am [:con    1.000]   :bias +0.000)
  (lfnoise  :fm [:con    1.000])
  (sh       :rate 7.000 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   1.000] :a2 [:cca    1.000] 
            :b1 [:lfo2   1.000] :b2 [:con    1.000] 
            :c1 [:env1   1.000] :c2 [:con    1.000] 
            :d1 [:env2   1.000] :d2 [:con    1.000] 
            :e1 [:step1  1.000] :e2 [:con    1.000] 
            :f1 [:sh     1.000] :f2 [:con    1.000] 
            :g1 [:env3   1.000] :g2 [:con    1.000] 
            :h1 [:period 1.000] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      0.100 0.00] :fm2 [:con    1.000 0.00])
  (osc1-wave 0.400             :w1  [:d      1.000     ] :w2  [:con    1.000     ])
  (osc1-amp  +0 :pan +0.00     :am1 [:con    1.000 0.00] :am2 [:con    0.000 0.00])

  (osc2-freq 2.000 :bias 0.000 :fm1 [:a      0.100 0.00] :fm2 [:con    1.000 0.00])
  (osc2-wave 0.500             :w1  [:b      0.520     ] :w2  [:off    0.000     ])
  (osc2-amp  +0 :pan +0.00     :am1 [:con    1.000 0.00] :am2 [:con    1.000 0.00])

  (osc3-freq 0.300 :bias 0.000 :fm1 [:a      0.100 0.00] :fm2 [:off    0.000 0.00])
  (osc3-wave 0.000             :w1  [:d      0.660     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.00     :am1 [:con    1.000 0.00] :am2 [:con    1.000 0.00])

  (noise    -99 :pan +0.00 :crackle 0.00     :lp 10000   :hp    10
                               :am1 [:con    1.000 0.00] :am2 [:a      0.000 0.00])
  (ringmod  -99 :pan +0.00 :carrier -1.0     :modulator -1.0
                               :am1 [:con    1.000 0.00] :am2 [:con    1.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix +0.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 10000 :fm1  [:b              1] :fm2 [:off        0]
           :mode 0.00              :res  [0.00 :off    0.00] :pan [-0.00 :off    +0.00])
  (fold    :gain 1.000 :mix +0.000 :clip [0.00 :con    0.00])
  (filter2 :gain 1.000 :freq 10000 :fm1  [:b              0] :fm2 [:off        0]
                                   :res  [0.00 :off    0.00] :pan [+0.00 :off    +0.00])
  ;; EFX
  (dry       +0)
  (pshifter  -4 :ratio [0.750 :off    0.000] :rand 0.04 :spread 0.00)
  (flanger  -99 :lfo [1.000 0.100] :mod [:off    0.000] :fb +0.50 :xmix 0.00)
  (echo1     +0 :delay [0.031 :h      1.000] :fb -0.94 :damp 0.00 :gate [:g      1.00] :pan +0.10)
  (echo2     +0 :delay [0.100 :h      0.980] :fb +0.90 :damp 0.00 :gate [:g      1.00] :pan +0.50)))
