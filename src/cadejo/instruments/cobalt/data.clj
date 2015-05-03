(println "-->    cobalt data")

(ns cadejo.instruments.cobalt.data
  (:use [cadejo.instruments.cobalt.program]))

;; ------------------------------------------------------ 000 Skinker
;;
(save-program  0 "Skinker"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.974 :sens  0.020 :prss 0.000 :depth 0.000 :delay 2.980)
           (lfo1    5.759 :bleed 0.644 :cca  0.000 :prss  0.000 :delay 1.680)
           (lfo2    4.203 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    3.183 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.014 :dcy1 0.060 :dcy2 0.000 :rel 0.136
                 :peak 1.000 :bp   0.646 :sus  0.646)
           (penv :a0 -0.7672 :a1 -0.1298 :a2 -0.2863 :a3 -0.5680
                 :t1 0.053   :t2 0.081   :t3 0.007   :cc9 0.000)
           (op1 5.000   0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.913
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.047 :dcy1 0.058 :dcy2 0.000 :rel 0.011
                      :peak 1.000 :bp   0.516 :sus  0.516])
           (fm1 2.0000 4.423 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 0.5000 0.167
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.613
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.013 :dcy1 0.038 :dcy2 0.014 :rel 0.315
                      :peak 0.672 :bp   0.542 :sus  0.643])
           (fm2 0.5000 0.260 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 1.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.711
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.068 :dcy1 0.357 :dcy2 0.000 :rel 0.014
                      :peak 1.000 :bp   0.697 :sus  0.697])
           (fm3 1.0000 0.466 :bias +20.517 :env 1.000 :left 0   :right 0  )
           (op4 5.001   0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.038 :dcy1 0.000 :dcy2 0.000 :rel 2.852
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 1.0000 1.979 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 2.5000 0.833
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.052 :dcy1 0.026 :dcy2 0.000 :rel 0.224
                      :peak 1.000 :bp   0.810 :sus  0.810])
           (op6 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.011 :dcy1 0.093 :dcy2 0.091 :rel 0.280
                      :peak 1.000 :bp   0.840 :sus  0.603])
           (op7 3.5000 0.857
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.001
                :penv +0.0000
                :env [:att  0.063 :dcy1 0.011 :dcy2 0.000 :rel 0.173
                      :peak 1.000 :bp   0.901 :sus  0.901])
           (op8 4.5000 0.381
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.019 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.710 :sus  0.100])
           (noise 1.5000 0.500 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.156 :dcy1 0.022 :dcy2 0.000 :rel 0.679
                        :peak 1.000 :bp   0.479 :sus  0.479])
           (buzz 5.50000 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.225 :dcy1 0.040 :dcy2 0.000 :rel 0.155
                       :peak 1.000 :bp   0.767 :sus  0.767])
           (buzz-harmonics   6 :env  41 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.896
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.221 :cca +0.000 :ccb +0.000]
                      :env [:att 0.044 :dcy 0.034 :rel 0.127 :sus 0.346]
                      :mode +0.644)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.3969 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [ -8    :lfo2 0.000 :lfo3 0.000 :xenv 0.996]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.439 :xfb +0.000)
           (delay2    :time [1.8671 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-13    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.590 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 001 Shilo
;;
(save-program  1 "Shilo"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.520 :sens  0.046 :prss 0.000 :depth 0.000 :delay 3.665)
           (lfo1    0.123 :bleed 0.536 :cca  0.000 :prss  0.000 :delay 1.306)
           (lfo2    1.003 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    4.346 :xenv  0.054 :cca  0.000 :ccb   0.000)
           (xenv :att  0.494 :dcy1 0.790 :dcy2 0.697 :rel 0.464
                 :peak 1.000 :bp   0.557 :sus  0.918)
           (penv :a0 -0.0868 :a1 -0.0337 :a2 +0.0325 :a3 +0.0156
                 :t1 0.260   :t2 0.303   :t3 0.187   :cc9 0.000)
           (op1 0.50     1.000
                :lfo1  0.090 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.614 :dcy1 0.000 :dcy2 0.000 :rel 0.271
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 1.0000 0.444
                :lfo1  0.541 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.284 :dcy1 0.230 :dcy2 0.499 :rel 0.501
                      :peak 1.000 :bp   0.679 :sus  0.541])
           (fm2 0.5000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 1.5000 1.000
                :lfo1  0.352 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.518 :dcy1 0.883 :dcy2 0.449 :rel 0.399
                      :peak 1.000 :bp   0.605 :sus  0.844])
           (fm3 0.5000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 4.5000 0.111
                :lfo1  0.520 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.664 :dcy1 0.628 :dcy2 0.000 :rel 0.031
                      :peak 1.000 :bp   0.793 :sus  0.793])
           (fm4 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 2.5000 0.647
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.326 :dcy2 0.000 :rel 0.652
                      :peak 1.000 :bp   0.229 :sus  0.229])
           (op6 3.0000 1.000
                :lfo1  0.830 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.566 :dcy1 0.311 :dcy2 0.512 :rel 0.523
                      :peak 1.000 :bp   0.925 :sus  0.620])
           (op7 3.5000 0.184
                :lfo1  0.694 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  1.558 :dcy1 1.151 :dcy2 0.075 :rel 1.035
                      :peak 1.000 :bp   0.934 :sus  0.554])
           (op8 4.0000 0.141
                :lfo1  0.231 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.416 :dcy1 0.457 :dcy2 0.774 :rel 0.889
                      :peak 0.047 :bp   0.594 :sus  0.513])
           (noise 2.0000 0.650 :bw  90
                  :lfo1 0.375   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.072 :dcy1 1.306 :dcy2 0.552 :rel 0.864
                        :peak 0.712 :bp   0.529 :sus  0.965])
           (buzz 1.5000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.251 :dcy2 1.583 :rel 1.000
                       :peak 1.000 :bp   0.728 :sus  0.100])
           (buzz-harmonics   9 :env  33 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [12000 :track  0 :env -0.865
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.897 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 1.384 :rel 1.496 :sus 0.100]
                      :mode -0.746)
           (bp-filter :offset 1.000)
           (fold      :wet 0.556 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.2045 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-10    :lfo2 0.000 :lfo3 0.000 :xenv 0.644]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.660 :xfb +0.000)
           (delay2    :time [0.0192 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-11    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.718 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------ 002 Green Giant
;;
(save-program 2 "Green Giant"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.194 :sens  0.035 :prss 0.000 :depth 0.000 :delay 0.814)
           (lfo1    0.160 :bleed 0.184 :cca  0.000 :prss  0.000 :delay 1.324)
           (lfo2    2.597 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.708 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.105 :dcy2 0.000 :rel 0.209
                 :peak 1.000 :bp   0.038 :sus  0.038)
           (penv :a0 +0.0387 :a1 -0.1867 :a2 -0.0195 :a3 -0.0497
                 :t1 0.066   :t2 0.063   :t3 0.036   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.132 :dcy1 0.000 :dcy2 0.000 :rel 0.032
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 3.0000 1.513 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 7.9964 0.250
                :lfo1  0.573 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.570
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.175 :dcy1 0.165 :dcy2 0.000 :rel 0.123
                      :peak 1.000 :bp   0.984 :sus  0.984])
           (fm2 3.0000 1.329 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 1.9987 0.999
                :lfo1  0.316 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.169 :dcy1 0.190 :dcy2 0.000 :rel 0.171
                      :peak 1.000 :bp   0.882 :sus  0.882])
           (fm3 1.0000 0.734 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 3.0000 1.000
                :lfo1  0.197 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.038 :dcy2 0.000 :rel 0.077
                      :peak 1.000 :bp   0.017 :sus  0.017])
           (fm4 2.0000 2.594 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 4.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.510
                :penv +0.0000
                :env [:att  0.125 :dcy1 0.231 :dcy2 0.198 :rel 0.100
                      :peak 1.000 :bp   0.575 :sus  0.920])
           (op6 6.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.499 :dcy1 0.159 :dcy2 0.000 :rel 0.254
                      :peak 1.000 :bp   0.422 :sus  0.422])
           (op7 7.0000 0.286
                :lfo1  0.274 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.181 :dcy1 0.558 :dcy2 0.000 :rel 0.074
                      :peak 1.000 :bp   0.403 :sus  0.403])
           (op8 8.0000 0.063
                :lfo1  0.968 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.199 :dcy2 0.000 :rel 0.398
                      :peak 1.000 :bp   0.080 :sus  0.080])
           (noise 2.0000 1.000 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.094 :dcy1 0.069 :dcy2 0.000 :rel 0.122
                        :peak 1.000 :bp   0.364 :sus  0.364])
           (buzz 7.9964 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.159 :dcy1 0.215 :dcy2 4.320 :rel 0.256
                       :peak 1.000 :bp   0.890 :sus  0.697])
           (buzz-harmonics   6 :env  12 :cca   0 :hp   1 :hp<-env   5)
           (lp-filter :freq [12000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.352 :dcy 0.433 :rel 2.530 :sus 0.752]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.2407 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-12    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.813 :xfb +0.000)
           (delay2    :time [0.7701 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-12    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.651 :xfb +0.059)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 003 Imperial
;;
(save-program  3 "Imperial"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.000 :sens  0.020 :prss 0.000 :depth 0.000 :delay 2.746)
           (lfo1    5.054 :bleed 0.380 :cca  0.000 :prss  0.000 :delay 2.637)
           (lfo2    0.010 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    1.685 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  2.625 :dcy1 2.268 :dcy2 0.000 :rel 0.098
                 :peak 1.000 :bp   0.965 :sus  0.965)
           (penv :a0 -0.0542 :a1 +0.0780 :a2 +0.0122 :a3 -0.0533
                 :t1 0.016   :t2 0.036   :t3 0.083   :cc9 0.000)
           (op1 2.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.910 :dcy1 2.290 :dcy2 0.000 :rel 2.565
                      :peak 1.000 :bp   0.930 :sus  0.930])
           (fm1 4.0000 1.087 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.966 :dcy1 2.580 :dcy2 0.000 :rel 2.183
                      :peak 1.000 :bp   0.637 :sus  0.637])
           (fm2 0.2500 4.903 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 2.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.142 :dcy1 2.598 :dcy2 2.740 :rel 0.144
                      :peak 0.016 :bp   0.699 :sus  0.541])
           (fm3 5.0000 91.376 :bias +2.520 :env 1.000 :left 0   :right 0  )
           (op4 9.0000 0.639
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.104 :dcy1 3.111 :dcy2 2.268 :rel 5.874
                      :peak 0.210 :bp   0.497 :sus  0.864])
           (fm4 5.0000 2.394 :bias +1.645 :env 1.000 :left 0   :right 0  )
           (op5 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  2.658 :dcy1 2.067 :dcy2 0.000 :rel 0.771
                      :peak 1.000 :bp   0.523 :sus  0.523])
           (op6 5.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.021 :dcy1 2.657 :dcy2 0.000 :rel 2.967
                      :peak 1.000 :bp   0.822 :sus  0.822])
           (op7 6.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.634 :dcy1 1.840 :dcy2 0.000 :rel 1.121
                      :peak 1.000 :bp   0.548 :sus  0.548])
           (op8 7.0000 0.286
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  2.311 :dcy1 5.095 :dcy2 0.000 :rel 0.950
                      :peak 1.000 :bp   0.399 :sus  0.399])
           (noise 2.0003 0.668 :bw  61
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.150 :dcy1 0.300 :dcy2 0.000 :rel 2.873
                        :peak 1.000 :bp   0.764 :sus  0.764])
           (buzz 10.0000 0.200
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  1.275 :dcy1 2.340 :dcy2 0.000 :rel 2.495
                       :peak 1.000 :bp   0.573 :sus  0.573])
           (buzz-harmonics   2 :env  28 :cca   0 :hp   1 :hp<-env  11)
           (lp-filter :freq [12000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 2.364 :dcy 2.687 :rel 2.685 :sus 0.675]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.0008 :lfo2 0.882 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.885 :xfb +0.000)
           (delay2    :time [0.5936 :lfo2 0.104 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.714 :xfb +0.046)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------ 004 Saline
;;
(save-program 4 "Saline"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.110 :sens  0.035 :prss 0.000 :depth 0.000 :delay 3.833)
           (lfo1    4.627 :bleed 0.948 :cca  0.000 :prss  0.000 :delay 3.314)
           (lfo2    3.530 :xenv  0.844 :cca  0.000 :ccb   0.000)
           (lfo3    0.094 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.355 :dcy1 1.211 :dcy2 0.845 :rel 1.451
                 :peak 1.000 :bp   0.520 :sus  0.628)
           (penv :a0 +0.1492 :a1 -0.2261 :a2 -0.3311 :a3 -0.3690
                 :t1 0.499   :t2 0.932   :t3 0.532   :cc9 0.000)
           (op1 0.5000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.002 :dcy1 0.975 :dcy2 1.159 :rel 0.565
                      :peak 1.000 :bp   0.933 :sus  0.651])
           (fm1 1.0000 0.084 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 8.5000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.532 :dcy1 1.559 :dcy2 1.214 :rel 0.301
                      :peak 0.693 :bp   0.021 :sus  0.762])
           (fm2 2.0000 1.533 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 2.5000 0.066
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.498 :dcy1 2.340 :dcy2 2.945 :rel 1.129
                      :peak 0.241 :bp   0.540 :sus  0.525])
           (fm3 0.5000 0.716 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 3.5000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.885 :dcy1 1.435 :dcy2 1.150 :rel 0.564
                      :peak 0.110 :bp   0.848 :sus  0.598])
           (fm4 0.5000 4.618 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 4.5000 0.111
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  1.147 :dcy1 1.074 :dcy2 0.894 :rel 1.130
                      :peak 0.771 :bp   0.768 :sus  0.790])
           (op6 5.5000 0.273
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  1.337 :dcy1 0.000 :dcy2 0.000 :rel 3.149
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (op7 6.5000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  1.359 :dcy1 1.343 :dcy2 0.000 :rel 1.249
                      :peak 1.000 :bp   0.517 :sus  0.517])
           (op8 7.5000 0.040
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.971 :dcy1 0.000 :dcy2 0.000 :rel 1.598
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (noise 1.5000 1.000 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  3.024 :dcy1 1.343 :dcy2 0.751 :rel 1.991
                        :peak 0.996 :bp   0.666 :sus  0.874])
           (buzz 2.5000 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.765 :dcy1 1.270 :dcy2 2.522 :rel 0.708
                       :peak 0.563 :bp   0.516 :sus  0.505])
           (buzz-harmonics   1 :env  31 :cca   0 :hp   4 :hp<-env   5)
           (lp-filter :freq [   50 :track  0 :env 0.922
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.289 :cca +0.000 :ccb +0.000]
                      :env [:att 0.841 :dcy 1.552 :rel 1.591 :sus 0.914]
                      :mode -0.876)
           (bp-filter :offset 8.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.7201 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-19    :lfo2 0.000 :lfo3 0.000 :xenv 0.532]
                      :pan  [-0.700 :lfo2 0.100 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.815 :xfb +0.010)
           (delay2    :time [0.0015 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [ -9    :lfo2 0.000 :lfo3 0.000 :xenv 0.701]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.100 :xenv 0.055]
                      :fb    +0.899 :xfb +0.100)
           (amp   -3   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


(save-program 5 "Olney"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.923 :sens  0.014 :prss 0.000 :depth 0.016 :delay 1.096)
           (lfo1    0.269 :bleed 0.633 :cca  0.000 :prss  0.000 :delay 2.544)
           (lfo2    5.923 :xenv  0.722 :cca  0.000 :ccb   0.000)
           (lfo3    0.179 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.894 :dcy1 1.055 :dcy2 0.683 :rel 0.496
                 :peak 1.000 :bp   0.301 :sus  0.698)
           (penv :a0 -0.0028 :a1 -0.1911 :a2 -0.0501 :a3 -0.1443
                 :t1 0.192   :t2 0.458   :t3 0.054   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.256 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.316 :dcy1 0.000 :dcy2 0.000 :rel 0.439
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 0.5000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 2.0000 0.667
                :lfo1  0.673 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.520 :dcy1 1.550 :dcy2 0.295 :rel 0.514
                      :peak 1.000 :bp   0.529 :sus  0.653])
           (fm2 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 3.0000 1.000
                :lfo1  0.872 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 2.111 :dcy2 0.000 :rel 4.222
                      :peak 1.000 :bp   0.222 :sus  0.222])
           (fm3 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 5.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.099 :dcy1 0.021 :dcy2 0.635 :rel 0.620
                      :peak 1.000 :bp   0.802 :sus  0.613])
           (fm4 0.2500 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 6.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.556 :dcy1 1.008 :dcy2 0.518 :rel 0.495
                      :peak 1.000 :bp   0.641 :sus  0.914])
           (op6 7.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.247 :dcy1 0.484 :dcy2 0.411 :rel 0.428
                      :peak 1.000 :bp   0.219 :sus  0.929])
           (op7 9.0000 0.000
                :lfo1  0.352 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.313 :dcy1 0.476 :dcy2 0.507 :rel 0.250
                      :peak 1.000 :bp   0.566 :sus  0.839])
           (op8 10.0000 0.303
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.756 :dcy1 0.000 :dcy2 0.000 :rel 0.543
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (noise 11.0000 0.000 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.371 :dcy1 0.256 :dcy2 0.785 :rel 1.524
                        :peak 1.000 :bp   0.880 :sus  0.848])
           (buzz 2.0000 0.667
                 :lfo1  0.293 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.395 :dcy1 0.385 :dcy2 0.365 :rel 0.315
                       :peak 0.329 :bp   0.572 :sus  0.741])
           (buzz-harmonics  11 :env  27 :cca   0 :hp   1 :hp<-env   5)
           (lp-filter :freq [   50 :track  1 :env 0.601
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.174 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.125 :rel 0.250 :sus 0.030]
                      :mode +0.672)
           (bp-filter :offset 0.500)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.2532 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.743 :xfb +0.000)
           (delay2    :time [0.3376 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.233 :lfo3 0.000 :xenv 0.595]
                      :pan  [+0.700 :lfo2 0.252 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.886 :xfb +0.000)
           (amp   -3   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 006 Plop
;;
(save-program 6 "Plop"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 3.888 :sens  0.033 :prss 0.000 :depth 0.000 :delay 1.580)
           (lfo1    2.916 :bleed 0.757 :cca  0.000 :prss  0.000 :delay 2.371)
           (lfo2    0.115 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.118 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.574 :dcy2 0.000 :rel 1.149
                 :peak 1.000 :bp   0.249 :sus  0.249)
           (penv :a0 -0.0636 :a1 -0.1978 :a2 +0.2534 :a3 -0.5654
                 :t1 0.064   :t2 0.078   :t3 0.046   :cc9 0.000)
           (op1 10.0000 0.100
                :lfo1  0.321 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.160 :dcy2 7.855 :rel 1.115
                      :peak 1.000 :bp   0.702 :sus  0.100])
           (fm1 0.5000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 1.0007 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.160 :dcy2 0.000 :rel 0.321
                      :peak 1.000 :bp   0.050 :sus  0.050])
           (fm2 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 2.0000 0.500
                :lfo1  0.737 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.7351  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.256 :dcy2 0.000 :rel 0.512
                      :peak 1.000 :bp   0.045 :sus  0.045])
           (fm3 5.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 3.0000 1.000
                :lfo1  0.519 :cca 0.000 :ccb 0.000 :vel 0.556 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.551 :dcy2 0.000 :rel 1.103
                      :peak 1.000 :bp   0.007 :sus  0.007])
           (fm4 4.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 5.0000 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.598 :dcy1 0.548 :dcy2 0.000 :rel 0.391
                      :peak 1.000 :bp   0.789 :sus  0.789])
           (op6 6.0000 0.167
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv +0.0000
                :env [:att  0.720 :dcy1 0.308 :dcy2 0.000 :rel 0.333
                      :peak 1.000 :bp   0.627 :sus  0.627])
           (op7 7.0000 0.143
                :lfo1  0.690 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv -0.7351
                :env [:att  0.000 :dcy1 0.141 :dcy2 1.323 :rel 3.065
                      :peak 1.000 :bp   0.734 :sus  0.009])
           (op8 9.0000 0.111
                :lfo1  0.089 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.721 :dcy2 0.000 :rel 1.442
                      :peak 1.000 :bp   0.247 :sus  0.247])
           (noise 1.0000 0.999 :bw  71
                  :lfo1 0.110   :cca 0.000 :vel 0.245 :prss 0.000
                  :penv -0.7351 :key  60   :left   +0 :right  +0
                  :env [:att  0.000 :dcy1 0.653 :dcy2 0.000 :rel 1.307
                        :peak 1.000 :bp   0.072 :sus  0.072])
           (buzz 1.0007 0.262
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.621 :prss 0.000
                 :penv +0.7351  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 1.390 :dcy2 0.000 :rel 2.781
                       :peak 1.000 :bp   0.011 :sus  0.011])
           (buzz-harmonics   1 :env  34 :cca   0 :hp   1 :hp<-env   6)
           (lp-filter :freq [   50 :track  3 :env 0.315
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.782 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.231 :rel 0.461 :sus 0.041]
                      :mode -0.158)
           (bp-filter :offset 0.500)
           (fold      :wet 0.463 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.0024 :lfo2 0.765 :lfo3 0.000 :xenv 0.000]
                      :amp  [-10    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.614 :xfb +0.086)
           (delay2    :time [0.0027 :lfo2 0.636 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.978 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.702 :xfb +0.000)
           (amp   -3   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 007 "Millstadt"
;;
(save-program 7 "Millstadt"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.004 :sens  0.024 :prss 0.000 :depth 0.000 :delay 1.693)
           (lfo1    0.222 :bleed 0.048 :cca  0.000 :prss  0.000 :delay 1.147)
           (lfo2    0.218 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.037 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.049 :dcy2 1.000 :rel 1.000
                 :peak 1.000 :bp   0.607 :sus  0.100)
           (penv :a0 -0.0515 :a1 -0.0861 :a2 -0.0572 :a3 +0.0847
                 :t1 0.018   :t2 0.155   :t3 0.156   :cc9 0.000)
           (op1 10.0000 0.752
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.734
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.143 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.634 :sus  0.100])
           (fm1 3.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 1.0000 0.020
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.068 :dcy1 0.143 :dcy2 0.326 :rel 0.151
                      :peak 1.000 :bp   0.575 :sus  0.808])
           (fm2 0.5000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 2.0000 0.086
                :lfo1  0.499 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.797
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.334 :dcy1 0.260 :dcy2 0.305 :rel 0.327
                      :peak 1.000 :bp   0.706 :sus  0.884])
           (fm3 3.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 10.0000 0.700
                :lfo1  0.950 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.247
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.275 :dcy1 0.227 :dcy2 0.000 :rel 0.403
                      :peak 1.000 :bp   0.908 :sus  0.908])
           (fm4 5.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 3.3808 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.947
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.091 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.730 :sus  0.032])
           (op6 7.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.011
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.176 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.727 :sus  0.100])
           (op7 8.5119 0.676
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.527
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.269 :dcy2 0.000 :rel 0.537
                      :peak 1.000 :bp   0.007 :sus  0.007])
           (op8 9.0000 0.605
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.343 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.534 :sus  0.244])
           (noise 3.0000 0.184 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.289
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.000 :dcy1 0.102 :dcy2 1.000 :rel 1.373
                        :peak 1.000 :bp   0.737 :sus  0.100])
           (buzz 11.0000 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.149
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.309 :dcy2 1.000 :rel 1.000
                       :peak 1.000 :bp   0.588 :sus  0.100])
           (buzz-harmonics   9 :env  43 :cca   0 :hp   1 :hp<-env   4)
           (lp-filter :freq [12000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.048 :dcy 1.128 :rel 1.000 :sus 0.100]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.4995 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-13    :lfo2 0.000 :lfo3 0.000 :xenv 0.509]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 1.000 :xenv 0.000]
                      :fb    +0.896 :xfb +0.100)
           (delay2    :time [0.0047 :lfo2 0.000 :lfo3 0.010 :xenv 0.000]
                      :amp  [ -9    :lfo2 0.000 :lfo3 0.000 :xenv 0.790]
                      :pan  [+0.700 :lfo2 0.100 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.822 :xfb +0.100)
           (amp   -3   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 008 Batch Town
;;
(save-program 8 "Batch Town"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.119 :sens  0.024 :prss 0.000 :depth 0.000 :delay 0.633)
           (lfo1    0.134 :bleed 0.370 :cca  0.000 :prss  0.000 :delay 1.630)
           (lfo2    0.588 :xenv  0.540 :cca  0.000 :ccb   0.000)
           (lfo3    0.039 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.269 :dcy2 0.000 :rel 0.538
                 :peak 1.000 :bp   0.092 :sus  0.092)
           (penv :a0 +0.1197 :a1 -0.1059 :a2 -0.0034 :a3 -0.1412
                 :t1 0.066   :t2 0.459   :t3 0.409   :cc9 0.000)
           (op1 1.0000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv -0.5348  :key  60 :left   +0 :right  +0
                :env [:att  0.346 :dcy1 0.000 :dcy2 0.000 :rel 0.231
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 2.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 2.0000 0.286
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.607 :prss 0.000
                :penv -0.3680  :key  60 :left   +0 :right  +0
                :env [:att  0.119 :dcy1 0.348 :dcy2 0.000 :rel 0.598
                      :peak 1.000 :bp   0.487 :sus  0.487])
           (fm2 2.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 4.0000 0.572
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.1864  :key  60 :left   +0 :right  +0
                :env [:att  0.232 :dcy1 0.370 :dcy2 0.232 :rel 0.383
                      :peak 0.892 :bp   0.899 :sus  0.836])
           (fm3 0.2500 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 10.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv -0.6225  :key  60 :left   +0 :right  +0
                :env [:att  0.307 :dcy1 0.274 :dcy2 0.000 :rel 0.269
                      :peak 1.000 :bp   0.650 :sus  0.650])
           (fm4 2.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 7.0000 0.280
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv -0.3117
                :env [:att  0.620 :dcy1 0.388 :dcy2 0.000 :rel 0.310
                      :peak 1.000 :bp   0.353 :sus  0.353])
           (op6 7.0033 0.999
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv -0.5724
                :env [:att  0.336 :dcy1 0.239 :dcy2 0.209 :rel 0.194
                      :peak 1.000 :bp   0.562 :sus  0.841])
           (op7 6.9988 0.882
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv +0.4317
                :env [:att  0.277 :dcy1 0.396 :dcy2 0.333 :rel 0.184
                      :peak 1.000 :bp   0.665 :sus  0.168])
           (op8 8.0000 0.875
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.800 :prss 0.000
                :penv -0.4297
                :env [:att  0.365 :dcy1 0.270 :dcy2 0.000 :rel 0.330
                      :peak 1.000 :bp   0.721 :sus  0.721])
           (noise 5.0000 0.714 :bw 100
                  :lfo1 0.000   :cca 0.000 :vel 0.905 :prss 0.000
                  :penv -0.5814 :key  60   :left   +0 :right  +0
                  :env [:att  0.205 :dcy1 0.264 :dcy2 0.000 :rel 0.199
                        :peak 1.000 :bp   0.939 :sus  0.939])
           (buzz 2.0000 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv -0.6664  :key  60 :left   +0 :right  +0
                 :env [:att  0.276 :dcy1 0.108 :dcy2 0.000 :rel 0.101
                       :peak 1.000 :bp   0.391 :sus  0.391])
           (buzz-harmonics   4 :env   0 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  3 :env 0.167
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.592 :cca +0.000 :ccb +0.000]
                      :env [:att 0.329 :dcy 0.260 :rel 0.711 :sus 0.676]
                      :mode +0.906)
           (bp-filter :offset 2.000)
           (fold      :wet 0.319 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.1214 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [ -1    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.401 :xfb +0.733)
           (delay2    :time [0.1618 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-60    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.459 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------ 009 "Alorton"
;;
(save-program 9 "Alorton"
   (cobalt (enable  1 2 3 4  5 6 7 8  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.352 :sens  0.032 :prss 0.000 :depth 0.000 :delay 2.779)
           (lfo1    0.207 :bleed 0.441 :cca  0.000 :prss  0.000 :delay 0.088)
           (lfo2    0.281 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.202 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.028 :dcy1 0.000 :dcy2 0.000 :rel 0.006
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 -0.1671 :a1 +0.4708 :a2 +0.0957 :a3 -0.2616
                 :t1 0.234   :t2 0.321   :t3 0.349   :cc9 0.000)
           (op1 6.9952 0.020
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.172 :dcy1 0.244 :dcy2 2.975 :rel 1.374
                      :peak 1.000 :bp   0.694 :sus  0.814])
           (fm1 0.2500 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op2 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.078 :dcy1 2.809 :dcy2 0.385 :rel 1.806
                      :peak 1.000 :bp   0.925 :sus  0.830])
           (fm2 3.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op3 0.9996 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.003 :dcy1 1.299 :dcy2 1.158 :rel 1.245
                      :peak 0.888 :bp   0.847 :sus  0.711])
           (fm3 4.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op4 2.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.138 :dcy1 1.750 :dcy2 1.434 :rel 0.907
                      :peak 0.973 :bp   0.923 :sus  0.735])
           (fm4 1.0000 0.000 :bias +0.000 :env 1.000 :left 0   :right 0  )
           (op5 3.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.948 :dcy1 0.939 :dcy2 0.000 :rel 1.105
                      :peak 1.000 :bp   0.677 :sus  0.677])
           (op6 5.0000 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.132 :dcy2 1.843 :rel 2.324
                      :peak 1.000 :bp   0.717 :sus  0.054])
           (op7 6.0000 0.631
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  2.888 :dcy1 0.001 :dcy2 0.000 :rel 1.883
                      :peak 1.000 :bp   0.564 :sus  0.564])
           (op8 7.0000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000
                :env [:att  0.000 :dcy1 0.338 :dcy2 2.100 :rel 5.773
                      :peak 1.000 :bp   0.708 :sus  0.127])
           (noise 1.0000 0.997 :bw  58
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  1.518 :dcy1 0.901 :dcy2 1.430 :rel 1.041
                        :peak 0.907 :bp   0.643 :sus  0.622])
           (buzz 2.0000 0.500
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.912 :dcy1 1.041 :dcy2 0.000 :rel 1.323
                       :peak 1.000 :bp   0.425 :sus  0.425])
           (buzz-harmonics   4 :env  40 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.865
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.325 :cca +0.000 :ccb +0.000]
                      :env [:att 0.035 :dcy 1.306 :rel 0.198 :sus 0.982]
                      :mode +0.760)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.2298 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [ -9    :lfo2 0.000 :lfo3 0.000 :xenv 0.698]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.437 :xfb +0.000)
           (delay2    :time [0.0766 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-19    :lfo2 0.000 :lfo3 0.821 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.323 :xfb +0.719)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

