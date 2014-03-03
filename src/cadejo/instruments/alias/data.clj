(println "\t--> data")

(ns cadejo.instruments.alias.data
  (:use [cadejo.instruments.alias.program])
)


;; ------------------------------------------------------------------- 0
;;
(save-program 0 "<name>"
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
  (matrix    :a1 [:env1   1.000] :a2 [:con   1.000]
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

  
(.dump bank)                          
;;(defn rl [](use 'cadejo.instruments.alias.data :reload))
