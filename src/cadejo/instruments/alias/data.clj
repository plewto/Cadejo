(println "\t--> data")

(ns cadejo.instruments.alias.data
  (:use [cadejo.instruments.alias.program])
)




;; ISSUES durring shakeout:
;;
;; 1) Is filter pan working?
;; 2) echo delay is wrong
;; 3) Need ability to attenuate dry signal vs effects.
;;    change effects amplitude mix to db to match that of osc.

;; ------------------------------------------------------------------- 0
;;
(save-program 0 "<name>"
 (alias-program 
  (common    :amp 0.100 :dry-mix   0  :port-time 0.000 :cc7->volume 0.000)
  ;; ENVS  A     D1    D2    R
  (env1    3.000 0.000 0.000 3.000   :pk 1.000 :bp 1.000 :sus 1.000 :invert 0) 
  (env2    0.000 0.000 0.000 0.000   :pk 1.000 :bp 1.000 :sus 1.000 :invert 0) 
  (env3    0.000 0.000 0.000 0.000   :pk 1.000 :bp 1.000 :sus 1.000)
  (lfo1      :fm1   [:con  1.000] :fm2   [:off  0.000] 
             :wave1 [:con  0.000] :wave2 [:b    1.000])
  (lfo2      :fm1   [:con  4.000] :fm2   [:off  0.000] 
             :wave1 [:con  0.500] :wave2 [:off  0.000])
  (lfo3      :fm1   [:con  4.000] :fm2   [:off  0.000] 
             :wave1 [:con  0.500] :wave2 [:off  0.000])
  (stepper1  :trig  :lfo1     :reset  :lfo2
             :min -10 :max +10 :step +1 :ivalue -10 :bias 0.00 :scale 1.0)
  (stepper2  :trig  :lfo2         :reset  :off
             :min +10 :max -10 :step -1 :ivalue +10 :bias 0.0 :scale 1.00)
  (divider1  :p1 1.000 :p3 0.000 :p5 0.000 :p7 -0.00 :pw 0.5
             :am [:con  1.00])

  (divider2  :p2 1.000 :p4 0.000 :p6 0.000 :p8 -2.00 :pw 0.50
             :am [:con 1.00])

  (lfnoise   :fm [:con  15.0])
  (sh        :rate 7.000 :src :div1  :bias 0.00 :scale 1.00)
  (matrix    :a1 [:env3   1.000] :a2 [:con   1.000]
             :b1 [:off   0.000] :b2 [:con   1.000]
             :c1 [:off   0.000] :c2 [:off   0.000]
             :d1 [:off   0.000] :d2 [:off   0.000]
             :e1 [:off   0.000] :e2 [:off   0.000]
             :f1 [:off   0.000] :f2 [:off   0.000]
             :g1 [:off   0.000] :g2 [:off   0.000]
             :h1 [:off   0.000] :h2 [:off   0.000])
  ;; OSCILATORS
  (osc1-freq 0.500 :bias    0 :fm1 [:a    0.500 0.00] :fm2 [:off 0.000 0.00])
  (osc1-wave 1.000            :w1  [:off  0.000     ] :w2  [:off 0.000     ])
  (osc1-amp  -99   :pan -0.00 :am1 [:off  0.000 0.00] :am2 [:off 0.000 0.00])
 
  (osc2-freq 1.000 :bias    0 :fm1 [:off  1.000 0.00] :fm2 [:off 0.000 0.00])
  (osc2-wave 0.500            :w1  [:off  0.000     ] :w2  [:off 0.000     ])
  (osc2-amp    0   :pan -0.00 :am1 [:off  0.000 0.00] :am2 [:off 0.000 0.00])

  (osc3-freq 1.000 :bias    0 :fm1 [:off  0.000 0.00] :fm2 [:off 0.000 0.00])
  (osc3-wave 1.000            :w1  [:off  1.000     ] :w2  [:b   0.000     ])
  (osc3-amp     0  :pan -0.00 :am1 [:off  0.000 0.00] :am2 [:off 0.000 0.00])

  (noise     -99   :pan -0.00 :crackle 1.00 :lp 10000 :hp 20
                              :am1 [:b    1.000 0.00] :am2 [:off 0.000 0.00])
  (ringmod   -99   :pan -0.00 :carrier -1.0 :modulator +1.0
                              :am1 [:off  0.000 0.00] :am2 [:off 0.000 0.00])

  (clip    :gain 1.000 :mix -1.000   :clip [0.00 :off 0.00])
  (filter1 :gain 1.000 :freq 10000   :fm1  [:off         0]  :fm2 [:off   0]
           :mode :lp                 :res  [0.00 :off 0.00]  :pan [ 0.00 :off -1.00])

  (fold    :gain 1.000 :mix -1.000   :clip [0.50 :b   0.00])
  (filter2 :gain 1.000 :freq  10000  :fm1  [:off         0]  :fm2 [:off   0]
                                     :res  [0.00 :off 0.50] :pan [-0.50 :off -0.00])
  
  (pitch-shifter :ratio [1.00 :off  0.00] :rand 0.00 :spread 0.30 :mix  -99 )
  (flanger :lfo [0.25 0.00] :mod [:a    1.000] :fb -0.95 :xmix 0.25 :mix -99  )
  (echo1   :delay [0.125 :con 1.00] :fb 0.9 :damp 0.0 :gate [:a 1.00] :pan +1.00 :mix 0     )
  (echo2   :delay [1.125 :con 1.00] :fb 0.9 :damp 0.0 :gate [:a 1.00] :pan -0.75 :mix 0    )))

  
(.dump bank)                          
;;(defn rl [](use 'cadejo.instruments.alias.data :reload))
