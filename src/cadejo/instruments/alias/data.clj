(println "\t--> data")

(ns cadejo.instruments.alias.data
  (:use [cadejo.instruments.alias.program])
  (:require [cadejo.instruments.alias.genpatch]))


(save-program 0 "Random" (list cadejo.instruments.alias.genpatch/rand-alias))

;; ------------------------------------------------------------------- 1
;;
(save-program 1 "<name>"
 (alias-program 
  ;; ENVS  A     D1    D2    R
  (env1    3.000 0.000 0.000 0.000   :pk 1.000 :bp 1.000 :sus 1.000 :invert 0) 
  (env2    0.000 0.000 0.000 0.000   :pk 1.000 :bp 1.000 :sus 1.000 :invert 0) 
  (env3    0.000 0.000 0.000 0.000   :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1      :fm1   [:con  6.000]    :fm2   [:con  0.000] 
             :wave1 [:con  0.500]    :wave2 [:con  0.000])
  (lfo2      :fm1   [:con  1.000]    :fm2   [:con  0.000] 
             :wave1 [:con  0.500]    :wave2 [:con  0.000])
  (lfo3      :fm1   [:con  2.000]    :fm2   [:con  0.000] 
             :wave1 [:con  0.500]    :wave2 [:con  0.000])
  (stepper1  :trig  :lfo1            :reset  :off
             :min -10 :max +10 :step +1 :ivalue -10 :bias 0.0 :scale 1.00)
  (stepper2  :trig  :lfo2            :reset  :off
             :min +10 :max -10 :step -1 :ivalue +10 :bias 0.0 :scale 1.00)

  (divider1  :p1 +1.000 :p3 +0.333 :p5 +0.200 :p7 +0.142 :pw 0.50
             :am [:con  1.00])

  (divider2  :p2 +0.500 :p4 +0.250 :p6 +0.167 :p8 +0.125 :pw 0.5
             :am [:con 1.00])
  (lfnoise   :fm [:con 1.00])
  (sh        :rate 7.000 :src :div1  :bias 0.00 :scale 1.00)
  (matrix    
             :a1 [:env1   1.000] :a2 [:con   1.000]
             :b1 [:env2   1.000] :b2 [:con   1.000]
             :c1 [:lfo1   0.010] :c2 [:cca   1.000]
             :d1 [:lfo2   1.000] :d2 [:con   1.000]
             :e1 [:vel    1.000] :e2 [:con   1.000]
             :f1 [:press  1.000] :f2 [:con   1.000]
             :g1 [:step1  1.000] :g2 [:con   1.000]
             :h1 [:div    1.000] :h2 [:con   1.000])
  ;; OSCILLATORS
  (osc1-freq 0.500 :bias    0 :fm1 [:c      1.000 0.00] :fm2 [:f     0.000 0.00])
  (osc1-wave 1.000            :w1  [:a      1.000     ] :w2  [:f     0.000     ])
  (osc1-amp  -00   :pan -0.00 :am1 [:con    0.000 0.00] :am2 [:con   0.000 0.00])

  (osc2-freq 1.000 :bias    0 :fm1 [:c      1.000 0.00] :fm2 [:con   0.000 0.00])
  (osc2-wave 0.500            :w1  [:d      0.200     ] :w2  [:con   0.000     ])
  (osc2-amp  -00   :pan -0.00 :am1 [:con    0.000 0.00] :am2 [:con   0.000 0.00])

  (osc3-freq 1.000 :bias    0 :fm1 [:c      1.000 0.00] :fm2 [:con   0.000 0.00])
  (osc3-wave 1.000            :w1  [:a      1.000     ] :w2  [:con   0.000     ])
  (osc3-amp  -00   :pan -0.00 :am1 [:con    0.000 0.00] :am2 [:con   0.000 0.00])
  ;; NOISE
  (noise     -99   :pan -0.00 :crackle 0.50 :lp 10000   :hp 20
                              :am1 [:con    0.000 0.00] :am2 [:con   0.000 0.00])
  (ringmod   -99   :pan -0.00 :carrier -1.0 :modulator -1.0
                              :am1 [:con    0.000 0.00] :am2 [:con   0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000   :clip [0.00 :con   0.00])
  (filter1 :gain 1.000 :freq 10000   :fm1  [:b          5000]  :fm2 [:con     0]
           :mode :lp                 :res  [0.00 :con   0.00]  :pan [-1.00 :con  0.00])
  (fold    :gain 1.000 :mix -1.000   :clip [0.00 :con   0.00])
  (filter2 :gain 1.000 :freq  10000  :fm1  [:b          5000]  :fm2 [:con     0]
                                     :res  [0.00 :con   0.00]  :pan [+1.00 :con  0.00])
  ;; EFFECTS
  (dry      -00  :amp 0.100  :port-time 0.000 :cc7->volume 0.000)
  (pshifter -99  :ratio [1.00 :con    0.00]   :rand 0.00  :spread 0.30)
  (flanger  -99  :lfo   [0.25 0.00]   :mod [:con    0.000] :fb -0.50 :xmix 0.25)
  (echo1    -99  :delay [0.250 :con   0.00] :fb  0.50 :damp 0.00 :gate [:con   0.00] :pan -0.75)
  (echo2    -99  :delay [0.125 :con   0.00] :fb  0.50 :damp 0.00 :gate [:con   0.00] :pan +0.75)))


;; --------------------------------------------------------------------------- 2 Insects
;;
(save-program   2 "Insects"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.381 0.227 0.471 0.130  :pk 1.000 :bp 0.953 :sus 0.953 :invert 0)
  (env2   0.982 10.813 0.326 1.861  :pk 1.000 :bp 0.955 :sus 0.719 :invert 0)
  (env3   0.181 0.030 0.399 11.313  :pk 1.000 :bp 0.838 :sus 0.838)
  (lfo1     :fm1   [:con    6.407] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.821] :fm2   [:env2   2.318]
            :wave1 [:con    0.500] :wave2 [:env2   0.500])
  (lfo3     :fm1   [:con    7.594] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -8 :step  -5 :ivalue  +2 :bias 0.0 :scale 0.10)
  (stepper2 :trig  :gate           :reset :off
            :min  -2 :max  +4 :step  +3 :ivalue  -2 :bias 0.0 :scale 0.17)
  (divider1 :p1 +0.000 :p3 -5.000 :p5 +7.000 :p7 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +9.000 :p4 -3.000 :p6 +6.000 :p8 -6.000 :pw 0.85
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    4.693])
  (sh       :rate 8.720 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.085] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:cca    0.919] :e2 [:env1   0.217] 
            :f1 [:ccb    0.030] :f2 [:con    1.000] 
            :g1 [:press  0.326] :g2 [:con    1.000] 
            :h1 [:ccc    0.960] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.330 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.066 0.00])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    4.778     ])
  (osc1-amp -15 :pan +0.34     :am1 [:off    0.000 0.00] :am2 [:f      0.112 0.06])

  (osc2-freq 1.200 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.009 0.06])
  (osc2-wave 0.672             :w1  [:d      0.409     ] :w2  [:e      -0.284     ])
  (osc2-amp -15 :pan -0.01     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 3.000 :bias 1.000 :fm1 [:a      1.000 0.00] :fm2 [:b      0.023 0.00])
  (osc3-wave 0.016             :w1  [:b      0.019     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.05     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.85 :crackle 0.56     :lp 10000   :hp  3817
                               :am1 [:f      0.296 0.00] :am2 [:f      0.901 0.33])
  (ringmod  -99 :pan -0.09 :carrier -0.9     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  1941 :fm1  [:e          -4391] :fm2 [:off     4623]
           :mode 0.00              :res  [0.59 :off    0.00] :pan [+0.23 :off    -0.71])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   469 :fm1  [:c           2009] :fm2 [:b      -3883]
                                   :res  [0.76 :off    0.00] :pan [+0.32 :off    +0.54])
  ;; EFX
  (dry      -99 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter  -9 :ratio [3.680 :h      1.001] :rand 0.31 :spread 0.23)
  (flanger  -99 :lfo [0.090 0.347] :mod [:off    0.192] :fb -0.72 :xmix 0.25)
  (echo1    -99 :delay [1.400 :off    0.000] :fb +0.44 :damp 0.43 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [1.800 :off    0.000] :fb +0.09 :damp 0.29 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 3 Thinning with Buzzz
;;
(save-program   3 "Thinning with Buzz" "High thin sound, timbre shift in upper registers"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.923 0.833 0.664 0.358  :pk 1.000 :bp 1.000 :sus 1.000 :invert 0)
  (env2   0.811 0.804 0.765 0.714  :pk 0.653 :bp 1.000 :sus 0.825 :invert 0)
  (env3   0.693 0.601 0.645 0.637  :pk 1.000 :bp 0.949 :sus 0.858)
  (lfo1     :fm1   [:con    6.256] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.859] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.164])
  (lfo3     :fm1   [:con    1.955] :fm2   [:off    0.000]
            :wave1 [:con    0.942] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -7 :step  -3 :ivalue  +2 :bias 0.0 :scale 0.11)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -13 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 -6.000 :p3 +0.000 :p5 -4.000 :p7 +2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +2.000 :p4 +3.000 :p6 +3.000 :p8 -1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    5.694])
  (sh       :rate 7.990 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.099] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:cca    0.075] :e2 [:con    1.000] 
            :f1 [:step2  0.364] :f2 [:press  0.972] 
            :g1 [:sh     0.963] :g2 [:con    1.000] 
            :h1 [:lfo3   0.513] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:g      0.361 0.09])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.453     ])
  (osc1-amp  -9 :pan +0.07     :am1 [:d      0.105 0.17] :am2 [:off    0.000 0.00])

  (osc2-freq 4.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.031 0.00])
  (osc2-wave 0.598             :w1  [:b      0.368     ] :w2  [:h      -0.255     ])
  (osc2-amp  +0 :pan -0.17     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 0.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.055 0.00])
  (osc3-wave 1.000             :w1  [:a      0.000     ] :w2  [:c      0.363     ])
  (osc3-amp -12 :pan +0.14     :am1 [:h      0.129 0.90] :am2 [:f      0.305 0.00])

  (noise    -99 :pan +0.48 :crackle 0.35     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.87 :carrier +0.6     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   432 :fm1  [:c          -4356] :fm2 [:off     2505]
           :mode 0.50              :res  [0.85 :off    0.00] :pan [+0.41 :off    +0.32])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   685 :fm1  [:c           1120] :fm2 [:off    -4360]
                                   :res  [0.75 :off    0.00] :pan [+0.20 :b      +0.67])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.24 :spread 0.04)
  (flanger  -99 :lfo [4.133 0.738] :mod [:off    0.739] :fb -0.31 :xmix 0.25)
  (echo1    -99 :delay [1.500 :off    0.000] :fb -0.90 :damp 0.43 :gate [:f      0.02] :pan -0.50)
  (echo2    -99 :delay [0.750 :off    0.000] :fb -0.63 :damp 0.11 :gate [:off    0.27] :pan +0.50)))

;; --------------------------------------------------------------------------- 4 Buzzing with Slimm
;;
(save-program  4 "Buzzing with Slimm"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.500 11.991 0.964 11.028  :pk 1.000 :bp 0.916 :sus 0.916 :invert 0)
  (env2   10.251 7.812 10.999 9.607  :pk 1.000 :bp 0.774 :sus 0.774 :invert 0)
  (env3   5.137 7.983 3.531 4.749  :pk 1.000 :bp 0.800 :sus 0.800)
  (lfo1     :fm1   [:con    7.237] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.106] :fm2   [:off    0.000]
            :wave1 [:con    0.871] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    2.450] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfnse  0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max  +2 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -8 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.10)
  (divider1 :p1 -3.000 :p3 -5.000 :p5 +3.000 :p7 -2.000 :pw 0.02
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +3.000 :p4 -8.000 :p6 +6.000 :p8 -8.000 :pw 0.04
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:env2   5.998])
  (sh       :rate 6.416 :src :lfo2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.005] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:cca    0.975] :e2 [:ccc    0.199] 
            :f1 [:sh     0.776] :f2 [:ccd    0.061] 
            :g1 [:ccb    0.631] :g2 [:ccc    0.135] 
            :h1 [:div1   0.981] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.071 0.00])
  (osc1-wave 1.000             :w1  [:e      0.000     ] :w2  [:off    0.307     ])
  (osc1-amp  +0 :pan +0.56     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.070 0.10])
  (osc2-wave 0.701             :w1  [:d      0.133     ] :w2  [:off    -0.496     ])
  (osc2-amp  +0 :pan +0.47     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.059 0.00])
  (osc3-wave 0.406             :w1  [:d      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -9 :pan -0.74     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.57 :crackle 0.67     :lp 10000   :hp  3809
                               :am1 [:off    0.000 0.00] :am2 [:d      0.131 0.00])
  (ringmod  -15 :pan +0.05 :carrier -0.2     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  4455 :fm1  [:c           2337] :fm2 [:off      352]
           :mode 0.50              :res  [0.46 :off    0.00] :pan [+0.57 :off    +0.20])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   185 :fm1  [:c           1636] :fm2 [:off     3378]
                                   :res  [0.62 :off    0.00] :pan [+0.75 :off    +0.54])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.17 :spread 0.63)
  (flanger  -15 :lfo [3.953 0.503] :mod [:c      0.052] :fb +0.49 :xmix 0.25)
  (echo1    -99 :delay [1.300 :b      0.965] :fb +0.71 :damp 0.40 :gate [:c      0.80] :pan -0.50)
  (echo2    -99 :delay [0.975 :off    0.375] :fb -0.76 :damp 0.24 :gate [:off    0.52] :pan +0.50)))

;; --------------------------------------------------------------------------- 5 Let it wash
;;
(save-program   5 "Let it wash"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.838 0.060 0.566 0.860  :pk 1.000 :bp 0.865 :sus 0.138 :invert 0)
  (env2   0.724 8.856 0.587 1.784  :pk 0.586 :bp 1.000 :sus 0.829 :invert 0)
  (env3   0.737 0.698 0.629 0.855  :pk 1.000 :bp 0.802 :sus 0.802)
  (lfo1     :fm1   [:con    7.483] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    3.586] :fm2   [:off    0.000]
            :wave1 [:con    0.471] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    3.841] :fm2   [:off    0.000]
            :wave1 [:con    0.951] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo3           :reset :off
            :min  +2 :max  -6 :step  -4 :ivalue  +2 :bias 0.0 :scale 0.13)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -7 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.11)
  (divider1 :p1 -9.000 :p3 +0.000 :p5 -8.000 :p7 +1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +6.000 :p4 +0.000 :p6 +8.000 :p8 -4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    0.589])
  (sh       :rate 0.732 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.038] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div2   0.871] :e2 [:con    1.000] 
            :f1 [:step2  0.262] :f2 [:con    1.000] 
            :g1 [:step1  0.051] :g2 [:ccc    0.712] 
            :h1 [:press  0.344] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.209 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.065 0.06])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.474     ])
  (osc1-amp -15 :pan -0.69     :am1 [:off    0.000 0.00] :am2 [:c      0.168 0.00])

  (osc2-freq 0.547 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.036 0.00])
  (osc2-wave 0.744             :w1  [:d      0.337     ] :w2  [:a      0.024     ])
  (osc2-amp  -6 :pan -0.48     :am1 [:a      0.805 0.07] :am2 [:off    0.000 0.00])

  (osc3-freq 1.962 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.082 0.00])
  (osc3-wave 1.408             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp -99 :pan -0.93     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise     +0 :pan -0.93 :crackle 0.67     :lp 10000   :hp  2215
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan +0.84 :carrier +0.3     :modulator -1.0
                               :am1 [:off    0.000 0.12] :am2 [:b      0.972 0.12])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  2900 :fm1  [:a           1721] :fm2 [:off     1531]
           :mode 0.00              :res  [0.47 :off    0.00] :pan [+0.42 :a      -0.43])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   161 :fm1  [:b           5315] :fm2 [:h      -3335]
                                   :res  [0.47 :off    0.00] :pan [+0.62 :off    +0.61])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.04 :spread 0.09)
  (flanger  -99 :lfo [0.110 0.104] :mod [:off    0.123] :fb -0.04 :xmix 0.25)
  (echo1    -99 :delay [0.600 :off    0.000] :fb -0.18 :damp 0.85 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.900 :off    0.000] :fb -0.80 :damp 0.15 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 6 "Merry go round gone off"
;;
(save-program  6 "Merry go round gone off"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   3.216 7.503 0.299 9.793  :pk 0.479 :bp 1.000 :sus 0.582 :invert 0)
  (env2   0.325 4.512 9.083 3.011  :pk 1.000 :bp 0.969 :sus 0.888 :invert 0)
  (env3   3.793 10.980 0.086 8.694  :pk 0.343 :bp 1.000 :sus 0.682)
  (lfo1     :fm1   [:con    3.244] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    2.721] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    5.498] :fm2   [:lfnse  4.514]
            :wave1 [:con    0.500] :wave2 [:env2   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  +2 :max  -2 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -2 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.25)
  (divider1 :p1 -3.000 :p3 +7.000 :p5 -7.000 :p7 +5.000 :pw 0.55
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +6.000 :p4 +0.000 :p6 +6.000 :p8 -5.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    8.478])
  (sh       :rate 5.725 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.069] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:gate   0.069] :e2 [:con    1.000] 
            :f1 [:ccb    0.684] :f2 [:con    1.000] 
            :g1 [:step1  0.807] :g2 [:con    1.000] 
            :h1 [:div1   0.178] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.081 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.077 0.00])
  (osc1-wave 1.000             :w1  [:h      0.000     ] :w2  [:off    5.466     ])
  (osc1-amp  +0 :pan -0.01     :am1 [:e      0.547 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 2.354 :bias 266.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.066 0.00])
  (osc2-wave 0.568             :w1  [:d      0.356     ] :w2  [:off    0.137     ])
  (osc2-amp -15 :pan -0.32     :am1 [:c      0.837 0.93] :am2 [:off    0.000 0.00])

  (osc3-freq 3.387 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.047 0.00])
  (osc3-wave 0.426             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.43     :am1 [:off    0.000 0.00] :am2 [:b      0.845 0.00])

  (noise    -99 :pan +0.26 :crackle 0.77     :lp 10000   :hp    50
                               :am1 [:e      0.721 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.50 :carrier -0.9     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 11502 :fm1  [:c          -6559] :fm2 [:off     4076]
           :mode 0.00              :res  [0.00 :off    0.00] :pan [+0.53 :off    -0.99])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  2230 :fm1  [:c           4408] :fm2 [:f       2325]
                                   :res  [0.59 :off    0.00] :pan [+0.24 :off    +0.27])
  ;; EFX
  (dry        0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter   0 :ratio [1.496 :d      2.414] :rand 0.40 :spread 0.20)
  (flanger  -99 :lfo [0.142 0.400] :mod [:off    0.168] :fb -0.78 :xmix 0.25)
  (echo1    -99 :delay [1.900 :c      0.148] :fb -0.22 :damp 0.63 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [3.800 :off    0.380] :fb +0.84 :damp 0.69 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 7 The Sound of Music
;;
(save-program   7 "The Sound Of Music"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.048 0.256 0.036 0.187  :pk 1.000 :bp 0.771 :sus 0.771 :invert 0)
  (env2   0.013 0.012 0.001 0.048  :pk 1.000 :bp 0.419 :sus 0.760 :invert 0)
  (env3   0.001 0.000 0.048 0.030  :pk 1.000 :bp 0.899 :sus 0.919)
  (lfo1     :fm1   [:con    7.407] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    7.346] :fm2   [:env1   5.361]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.500])
  (lfo3     :fm1   [:con    2.983] :fm2   [:env2   1.197]
            :wave1 [:con    0.506] :wave2 [:env3   0.500])
  (stepper1 :trig  :lfo3           :reset :lfo1
            :min  -2 :max  +4 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.17)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -5 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.14)
  (divider1 :p1 +2.000 :p3 +5.000 :p5 +6.000 :p7 +6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +1.000 :p4 -4.000 :p6 -6.000 :p8 +3.000 :pw 0.20
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    6.965])
  (sh       :rate 1.798 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.001] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:div2   0.029] :e2 [:con    1.000] 
            :f1 [:sh     0.338] :f2 [:con    1.000] 
            :g1 [:sh     0.115] :g2 [:sh     0.777] 
            :h1 [:step1  0.463] :h2 [:ccc    0.773] )
  ;; OSCILLATORS
  (osc1-freq 1.505 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:c      0.084 0.06])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    0.268     ])
  (osc1-amp  -9 :pan -0.65     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 2.464 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.078 0.00])
  (osc2-wave 0.710             :w1  [:d      0.330     ] :w2  [:a      -0.085     ])
  (osc2-amp  +0 :pan +0.30     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.005 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.021 0.00])
  (osc3-wave 0.485             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  -6 :pan -0.95     :am1 [:off    0.000 0.00] :am2 [:h      0.308 0.00])

  (noise    -99 :pan -0.26 :crackle 0.68     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:b      0.197 0.00])
  (ringmod  -12 :pan -0.38 :carrier -0.7     :modulator -1.0
                               :am1 [:c      0.318 0.00] :am2 [:g      0.027 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq 13862 :fm1  [:e          -2752] :fm2 [:off     1625]
           :mode 0.00              :res  [0.57 :g      -0.79] :pan [+0.35 :off    -0.52])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   114 :fm1  [:c           7831] :fm2 [:g       3714]
                                   :res  [0.92 :off    0.00] :pan [+0.92 :off    -0.17])
  ;; EFX
  (dry       -6 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.05 :spread 0.03)
  (flanger  -99 :lfo [0.092 0.537] :mod [:off    0.340] :fb +0.74 :xmix 0.25)
  (echo1    -99 :delay [0.600 :e      0.222] :fb +0.26 :damp 0.66 :gate [:off    0.00] :pan -0.50)
  (echo2      0 :delay [1.000 :d      0.915] :fb -0.13 :damp 0.52 :gate [:off    0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 8 Back away slowly
;;
(save-program   8 "Back away slowly"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.983 0.526 0.893 0.765  :pk 1.000 :bp 0.952 :sus 0.886 :invert 1)
  (env2   6.630 0.777 0.803 0.708  :pk 0.064 :bp 1.000 :sus 0.705 :invert 0)
  (env3   0.677 0.788 8.581 0.952  :pk 1.000 :bp 0.926 :sus 0.295)
  (lfo1     :fm1   [:con    3.389] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.658] :fm2   [:lfo1   6.429]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (lfo3     :fm1   [:con    3.295] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max +12 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.07)
  (stepper2 :trig  :lfo2           :reset :off
            :min  -2 :max  +2 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.25)
  (divider1 :p1 -8.000 :p3 +0.000 :p5 -2.000 :p7 +1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -7.000 :p4 +5.000 :p6 +6.000 :p8 +8.000 :pw 0.62
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    7.390])
  (sh       :rate 2.503 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.016] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:gate   0.766] :e2 [:div1   0.866] 
            :f1 [:ccc    0.660] :f2 [:con    1.000] 
            :g1 [:step2  0.981] :g2 [:con    1.000] 
            :h1 [:gate   0.908] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 2.606 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.064 0.06])
  (osc1-wave 1.000             :w1  [:h      0.000     ] :w2  [:off    0.331     ])
  (osc1-amp  +0 :pan +0.55     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 2.947 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.013 0.00])
  (osc2-wave 0.664             :w1  [:d      0.344     ] :w2  [:off    -0.064     ])
  (osc2-amp  -3 :pan +0.96     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 2.763 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.089 0.00])
  (osc3-wave 0.427             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.88     :am1 [:off    0.000 0.00] :am2 [:b      0.686 0.00])

  (noise    -99 :pan +0.08 :crackle 0.53     :lp 10000   :hp    50
                               :am1 [:f      0.994 0.56] :am2 [:off    0.000 0.00])
  (ringmod   -3 :pan -0.59 :carrier -0.0     :modulator -1.0
                               :am1 [:f      0.455 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix +0.810 :clip [0.04 :off    0.00])
  (filter1 :gain 1.000 :freq   624 :fm1  [:c           6558] :fm2 [:off     4125]
           :mode 0.00              :res  [0.66 :off    0.00] :pan [+0.53 :off    +0.92])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  1409 :fm1  [:c           2975] :fm2 [:off     4038]
                                   :res  [0.09 :off    0.00] :pan [+0.31 :off    -0.46])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.66 :spread 0.16)
  (flanger  -99 :lfo [0.086 0.132] :mod [:off    0.698] :fb -0.47 :xmix 0.25)
  (echo1     -3 :delay [0.200 :off    0.000] :fb +0.97 :damp 0.00 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.250 :off    0.000] :fb -0.17 :damp 0.92 :gate [:g      0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 9 Milwaukee's Best"
;;
(save-program   9 "Milwaukee's Best"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   3.520 0.032 0.099 1.087  :pk 1.000 :bp 0.987 :sus 0.271 :invert 0)
  (env2   0.001 0.022 0.014 1.587  :pk 1.000 :bp 0.982 :sus 0.982 :invert 0)
  (env3   3.439 0.077 0.033 0.223  :pk 1.000 :bp 0.949 :sus 0.800)
  (lfo1     :fm1   [:con    4.528] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.767] :fm2   [:lfo1   3.241]
            :wave1 [:con    0.500] :wave2 [:off    0.659])
  (lfo3     :fm1   [:con    4.620] :fm2   [:ccc    4.190]
            :wave1 [:con    0.936] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max  +6 :step  +4 :ivalue  -2 :bias 0.0 :scale 0.13)
  (stepper2 :trig  :div2           :reset :off
            :min  -2 :max  +7 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.11)
  (divider1 :p1 -5.000 :p3 -7.000 :p5 +5.000 :p7 +7.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +4.000 :p4 +4.000 :p6 -8.000 :p8 -1.000 :pw 0.66
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:ccd    6.082])
  (sh       :rate 5.442 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.010] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:ccb    0.937] :e2 [:con    1.000] 
            :f1 [:div2   0.621] :f2 [:con    1.000] 
            :g1 [:period 0.679] :g2 [:con    1.000] 
            :h1 [:cca    0.997] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 0.680 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.009 0.06])
  (osc1-wave 1.000             :w1  [:e      0.000     ] :w2  [:off    4.950     ])
  (osc1-amp  -9 :pan +0.50     :am1 [:off    0.000 0.00] :am2 [:b      0.238 0.06])

  (osc2-freq 3.467 :bias 286.000 :fm1 [:a      1.000 0.00] :fm2 [:b      0.082 0.05])
  (osc2-wave 0.533             :w1  [:d      0.160     ] :w2  [:c      -0.489     ])
  (osc2-amp  +0 :pan +0.22     :am1 [:g      0.184 0.09] :am2 [:off    0.000 0.00])

  (osc3-freq 0.678 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:e      0.028 0.00])
  (osc3-wave 0.575             :w1  [:b      1.994     ] :w2  [:a      0.104     ])
  (osc3-amp  -3 :pan +0.55     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan +0.04 :crackle 0.03     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.61 :carrier +0.6     :modulator -1.0
                               :am1 [:h      0.150 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  3162 :fm1  [:c          -1142] :fm2 [:off      -66]
           :mode 0.50              :res  [0.50 :f      0.16] :pan [+0.52 :e      -0.44])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   693 :fm1  [:c           9070] :fm2 [:off    -5493]
                                   :res  [0.61 :off    0.00] :pan [+0.73 :c      -0.79])
  ;; EFX
  (dry        0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -99 :ratio [1.000 :off    0.000] :rand 0.11 :spread 0.08)
  (flanger   -6 :lfo [1.804 0.000] :mod [:e      0.712] :fb -0.32 :xmix 0.25)
  (echo1     -9 :delay [0.523 :b      0.200] :fb +0.75 :damp 0.88 :gate [:ccb    1.00] :pan -0.50)
  (echo2     -9 :delay [1.000 :f      0.100] :fb +0.75 :damp 0.38 :gate [:ccb    1.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 10 
;;
(save-program   10 "Belgrade"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   3.539 0.033 0.581 1.739  :pk 0.575 :bp 1.000 :sus 0.508 :invert 0)
  (env2   0.090 3.238 7.672 1.285  :pk 1.000 :bp 0.919 :sus 0.978 :invert 0)
  (env3   3.603 1.281 2.766 2.437  :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1     :fm1   [:con    7.988] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.254] :fm2   [:lfo1   0.044]
            :wave1 [:con    0.500] :wave2 [:lfo1   0.500])
  (lfo3     :fm1   [:con    1.778] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.500])
  (stepper1 :trig  :lfo1           :reset :div
            :min  -2 :max  +3 :step  +1 :ivalue  -2 :bias 0.0 :scale 0.20)
  (stepper2 :trig  :lfo2           :reset :lfo1
            :min  +2 :max -12 :step  -7 :ivalue  +2 :bias 0.0 :scale 0.07)
  (divider1 :p1 +0.000 :p3 -6.000 :p5 +9.000 :p7 -2.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +7.000 :p4 -1.000 :p6 -6.000 :p8 -6.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    1.391])
  (sh       :rate 2.359 :src :lfo2   :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.100] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfo2   0.052] :e2 [:con    1.000] 
            :f1 [:press  0.833] :f2 [:con    1.000] 
            :g1 [:press  0.783] :g2 [:con    1.000] 
            :h1 [:ccc    0.027] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 2.190 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.097 0.02])
  (osc1-wave 1.000             :w1  [:b      0.000     ] :w2  [:off    5.855     ])
  (osc1-amp  +0 :pan +0.98     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.429 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.097 0.06])
  (osc2-wave 0.712             :w1  [:d      0.003     ] :w2  [:c      -0.181     ])
  (osc2-amp  -3 :pan +0.29     :am1 [:off    0.000 0.00] :am2 [:e      0.760 0.65])

  (osc3-freq 2.198 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:g      0.036 0.09])
  (osc3-wave 1.071             :w1  [:e      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan +0.00     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -12 :pan -0.14 :crackle 0.71     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.41 :carrier -0.3     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq  1545 :fm1  [:c            269] :fm2 [:e      -2813]
           :mode 0.00              :res  [0.56 :off    0.00] :pan [+0.27 :off    +0.46])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq    50 :fm1  [:c           1274] :fm2 [:off    -2067]
                                   :res  [0.84 :f      0.17] :pan [+0.59 :off    -0.54])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.25   :cc7->volume 0.000)
  (pshifter  -9 :ratio [1.898 :a      2.838] :rand 0.20 :spread 0.11)
  (flanger  -99 :lfo [0.025 0.571] :mod [:off    0.187] :fb -0.85 :xmix 0.25)
  (echo1     -3 :delay [0.500 :off    0.000] :fb +0.55 :damp 0.04 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.300 :off    0.000] :fb +0.44 :damp 0.03 :gate [:e      0.00] :pan +0.50)))

;; --------------------------------------------------------------------------- 11 Eurosong contest
;;
(save-program   11 "Eurosong contest"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.453 0.124 0.187 0.242  :pk 0.560 :bp 1.000 :sus 0.914 :invert 0)
  (env2   0.129 0.498 0.008 0.188  :pk 1.000 :bp 0.913 :sus 0.861 :invert 0)
  (env3   0.034 0.794 0.182 0.161  :pk 1.000 :bp 0.771 :sus 0.771)
  (lfo1     :fm1   [:con    7.190] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    1.795] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:env3   0.167])
  (lfo3     :fm1   [:con    2.966] :fm2   [:off    0.000]
            :wave1 [:con    0.762] :wave2 [:env3   0.500])
  (stepper1 :trig  :lfo1           :reset :off
            :min  -2 :max  +3 :step  +2 :ivalue  -2 :bias 0.0 :scale 0.20)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max -11 :step  -3 :ivalue  +2 :bias 0.0 :scale 0.08)
  (divider1 :p1 -3.000 :p3 -1.000 :p5 +0.000 :p7 -8.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 +9.000 :p4 +0.000 :p6 +4.000 :p8 -4.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    1.499])
  (sh       :rate 0.574 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.026] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:step2  0.151] :e2 [:con    1.000] 
            :f1 [:div    0.203] :f2 [:press  0.567] 
            :g1 [:div1   0.268] :g2 [:con    1.000] 
            :h1 [:div    0.137] :h2 [:cca    0.695] )
  ;; OSCILLATORS
  (osc1-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.033 0.01])
  (osc1-wave 1.000             :w1  [:c      0.000     ] :w2  [:off    4.773     ])
  (osc1-amp  -6 :pan -0.15     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc2-freq 1.250 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:d      0.114 0.07])
  (osc2-wave 0.745             :w1  [:d      0.270     ] :w2  [:f      0.234     ])
  (osc2-amp -15 :pan +0.35     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.050 0.00])
  (osc3-wave 0.420             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.92     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.21 :crackle 0.92     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.23 :carrier +0.1     :modulator -1.0
                               :am1 [:off    0.000 0.00] :am2 [:d      0.922 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   257 :fm1  [:g           6883] :fm2 [:off    -6697]
           :mode 0.00              :res  [0.79 :off    0.00] :pan [+0.99 :g      +0.24])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq   693 :fm1  [:c           1709] :fm2 [:off    -7237]
                                   :res  [0.81 :off    0.00] :pan [+0.79 :off    -0.52])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter  -9 :ratio [0.296 :d      2.653] :rand 0.90 :spread 0.10)
  (flanger  -99 :lfo [0.038 0.269] :mod [:off    0.489] :fb -0.10 :xmix 0.25)
  (echo1    -99 :delay [0.700 :off    0.000] :fb +0.65 :damp 0.78 :gate [:off    0.00] :pan -0.50)
  (echo2    -15 :delay [0.525 :off    0.000] :fb -0.74 :damp 0.19 :gate [:a      0.00] :pan +0.50)))


;; --------------------------------------------------------------------------- 1 Working
;;
(save-program   1 "working"
 (alias-program
  ;; ENVS A     D1    D2    R
  (env1   0.251 6.044 7.687 8.657   :pk 1.000 :bp 1.000 :sus 0.539 :invert 0)
  (env2   4.472 8.024 7.717 11.731  :pk 1.000 :bp 1.000 :sus 0.661 :invert 0)
  (env3   0.253 3.516 11.921 3.276  :pk 1.000 :bp 0.896 :sus 0.929)
  (lfo1     :fm1   [:con    4.020] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:off    0.000])
  (lfo2     :fm1   [:con    0.046] :fm2   [:ccc    2.965]
            :wave1 [:con    0.500] :wave2 [:gate   0.500])
  (lfo3     :fm1   [:con    1.580] :fm2   [:off    0.000]
            :wave1 [:con    0.500] :wave2 [:lfnse  0.500])
  (stepper1 :trig  :gate           :reset :off
            :min  +2 :max  -2 :step  -2 :ivalue  +2 :bias 0.0 :scale 0.25)
  (stepper2 :trig  :lfo2           :reset :off
            :min  +2 :max  -7 :step  -1 :ivalue  +2 :bias 0.0 :scale 0.11)
  (divider1 :p1 +8.000 :p3 +8.000 :p5 -8.000 :p7 +1.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (divider2 :p2 -1.000 :p4 +9.000 :p6 -4.000 :p8 -8.000 :pw 0.50
            :am [:env1   1.000]   :bias +0.000)
  (lfnoise  :fm [:con    5.304])
  (sh       :rate 4.769 :src :lfnse  :bias 0.000 :scale 1.000)
  (matrix
            :a1 [:lfo1   0.056] :a2 [:cca    1.000] 
            :b1 [:env2   1.000] :b2 [:con    1.000] 
            :c1 [:env3   1.000] :c2 [:con    1.000] 
            :d1 [:lfo2   1.000] :d2 [:con    1.000] 
            :e1 [:lfo2   0.243] :e2 [:con    1.000] 
            :f1 [:step1  0.370] :f2 [:ccb    0.332]   ;; div
            :g1 [:ccb    1.000] :g2 [:con    1.000] 
            :h1 [:sh     0.035] :h2 [:con    1.000] )
  ;; OSCILLATORS
  (osc1-freq 1.500 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:g      1.000 0.00])
  (osc1-wave 1.000             :w1  [:a      0.000     ] :w2  [:off    5.353     ])
  (osc1-amp  +0 :pan -0.57     :am1 [:c      0.809 0.98] :am2 [:off    0.000 0.00])

  (osc2-freq 0.750 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:off    0.049 0.03])
  (osc2-wave 0.589             :w1  [:d      0.224     ] :w2  [:f      0.012     ])
  (osc2-amp  +0 :pan +0.57     :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])

  (osc3-freq 1.000 :bias 0.000 :fm1 [:a      1.000 0.00] :fm2 [:d      0.004 0.03])
  (osc3-wave 0.713             :w1  [:b      0.000     ] :w2  [:off    0.000     ])
  (osc3-amp  +0 :pan -0.81     :am1 [:b      0.053 0.00] :am2 [:off    0.000 0.00])

  (noise    -99 :pan -0.71 :crackle 0.91     :lp 10000   :hp    50
                               :am1 [:off    0.000 0.00] :am2 [:off    0.000 0.00])
  (ringmod  -99 :pan -0.11 :carrier -0.3     :modulator -1.0
                               :am1 [:h      0.636 0.00] :am2 [:h      0.346 0.00])
  ;; FILTERS
  (clip    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter1 :gain 1.000 :freq   711 :fm1  [:h           8975] :fm2 [:off    -4920]
           :mode 0.25              :res  [0.91 :off    0.00] :pan [+0.35 :d      -0.25])
  (fold    :gain 1.000 :mix -1.000 :clip [0.00 :off    0.00])
  (filter2 :gain 1.000 :freq  4163 :fm1  [:c          -3179] :fm2 [:off    -1148]
                                   :res  [0.86 :off    0.00] :pan [-0.45 :d      +0.25])
  ;; EFX
  (dry       +0 :amp 0.200 :port-time 0.000  :cc7->volume 0.000)
  (pshifter -12 :ratio [1.000 :off    0.000] :rand 0.01 :spread 0.62)
  (flanger  -99 :lfo [3.665 0.406] :mod [:off    0.172] :fb -0.32 :xmix 0.25)
  (echo1    -99 :delay [0.700 :off    0.000] :fb +0.35 :damp 0.39 :gate [:off    0.00] :pan -0.50)
  (echo2    -99 :delay [0.350 :off    0.000] :fb -0.10 :damp 0.67 :gate [:g      0.00] :pan +0.50)))




(.dump bank)                          
(defn rl [](use 'cadejo.instruments.alias.data :reload))
