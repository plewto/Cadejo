(println "-->    cobalt data")

(ns cadejo.instruments.cobalt.data
  (:use [cadejo.instruments.cobalt.program]))

;; ------------------------------------------------------  0 "Zero"
;;
(save-program  0 "Zero"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.462 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.892 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.088)
           (lfo2    1.338 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    3.569 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  6.044 :dcy1 4.760 :dcy2 0.000 :rel 3.411
                 :peak 1.000 :bp   0.642 :sus  0.642)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  9.178 :dcy1 3.502 :dcy2 1.288 :rel 3.249
                      :peak 0.618 :bp   0.143 :sus  0.353])
           (fm1 0.5000 1.107 :bias +0.000 :env 0.688 :lag 0.582 :left 6   :right 0  )
           (op2 4.0000 0.960
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.481 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  6.004 :dcy1 8.055 :dcy2 0.000 :rel 4.117
                      :peak 1.000 :bp   0.784 :sus  0.784])
           (fm2 0.5000 0.326 :bias +0.000 :env 0.671 :lag 0.702 :left 9   :right -6 )
           (op3 5.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.677 :dcy1 3.241 :dcy2 0.000 :rel 3.947
                      :peak 1.000 :bp   0.979 :sus  0.979])
           (fm3 0.5000 0.000 :bias +0.000 :env 0.416 :lag 0.215 :left 6   :right 0  )
           (op4 5.0037 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  1.123 :dcy1 3.328 :dcy2 0.000 :rel 5.483
                      :peak 1.000 :bp   0.578 :sus  0.578])
           (fm4 0.2500 1.703 :bias +0.000 :env 0.863 :lag 0.000 :left 6   :right -9 )
           (op5 7.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  4.150 :dcy1 4.211 :dcy2 0.000 :rel 1.649
                      :peak 1.000 :bp   0.724 :sus  0.724])
           (fm5 1.0000 0.000 :bias +0.000 :env 0.026 :lag 0.000 :left 0   :right 0  )
           (op6 10.0000 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.272 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  3.133 :dcy1 0.105 :dcy2 0.000 :rel 6.297
                      :peak 1.000 :bp   0.664 :sus  0.664])
           (fm6 3.0000 0.000 :bias +0.000 :env 0.986 :lag 0.000 :left 6   :right 0  )
           (noise 8.0000 0.250 :bw  18
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  5.538 :dcy1 0.213 :dcy2 0.000 :rel 5.501
                        :peak 1.000 :bp   0.701 :sus  0.701])
           (noise2 2.0000 1.000 :bw  16 :lag 0.000)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  5.753 :dcy1 5.061 :dcy2 0.000 :rel 5.998
                       :peak 1.000 :bp   0.730 :sus  0.730])
           (buzz-harmonics  14 :env   2 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.376
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.205 :cca +0.000 :ccb +0.000]
                      :env [:att 3.653 :dcy 7.932 :rel 0.226 :sus 0.960]
                      :mode -0.479)
           (bp-filter :offset 4.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.5603 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-23    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.991 :xenv 0.000]
                      :fb    +0.318 :xfb -0.220)
           (delay2    :time [0.0010 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-48    :lfo2 0.000 :lfo3 0.000 :xenv 0.861]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.567 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  1 One
;;
(save-program  1 "One"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.147 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.529 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.715)
           (lfo2    0.922 :xenv  0.817 :cca  0.537 :ccb   0.000)
           (lfo3    0.115 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.156 :dcy1 0.000 :dcy2 0.000 :rel 7.112
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.975 :dcy1 0.000 :dcy2 0.000 :rel 1.067
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 2.0000 1.631 :bias +0.000 :env 0.659 :lag 0.000 :left 0   :right 0  )
           (op2 5.0059 0.599
                :lfo1  0.370 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.347 :dcy1 0.000 :dcy2 0.000 :rel 1.237
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 3.0000 0.000 :bias +0.000 :env 0.733 :lag 0.000 :left 9   :right -9 )
           (op3 7.0000 0.429
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.984 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.529 :dcy1 0.000 :dcy2 0.000 :rel 10.926
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 0.274 :bias +0.000 :env 0.993 :lag 0.000 :left 9   :right 0  )
           (op4 7.0208 0.427
                :lfo1  0.843 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 2.602 :dcy2 0.000 :rel 5.205
                      :peak 1.000 :bp   0.040 :sus  0.040])
           (fm4 1.0000 0.000 :bias +0.000 :env 0.651 :lag 0.000 :left 0   :right -3 )
           (op5 9.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.245 :dcy1 0.000 :dcy2 0.000 :rel 0.473
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 2.0000 1.592 :bias +0.000 :env 0.578 :lag 0.000 :left 3   :right 0  )
           (op6 11.0000 0.273
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  2.578 :dcy1 0.000 :dcy2 0.000 :rel 0.948
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 3.0000 0.444 :bias +0.000 :env 0.861 :lag 0.000 :left 0   :right 0  )
           (noise 3.0000 1.000 :bw  11
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.826 :dcy1 0.000 :dcy2 0.000 :rel 0.901
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 5.0000 0.600 :bw  16 :lag 0.000)
           (buzz 3.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  1.366 :dcy1 0.000 :dcy2 0.000 :rel 1.506
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  22 :env -21 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.612
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.360 :cca +0.000 :ccb +0.000]
                      :env [:att 2.349 :dcy 0.000 :rel 0.919 :sus 1.000]
                      :mode +0.132)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.4468 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-28    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.859 :xfb +0.045)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.000 :xenv 0.799]
                      :amp  [-15    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.535 :xfb -0.258)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  2 Two
;;
(save-program  2 "Two"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.108 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.171 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.697)
           (lfo2    0.128 :xenv  0.000 :cca  0.346 :ccb   0.000)
           (lfo3    0.342 :xenv  0.785 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 5.870 :dcy2 0.000 :rel 11.740
                 :peak 1.000 :bp   0.015 :sus  0.015)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 4.0614 0.217
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 3.663 :dcy2 0.000 :rel 7.325
                      :peak 1.000 :bp   0.025 :sus  0.025])
           (fm1 1.0000 0.000 :bias +0.000 :env 0.910 :lag 0.000 :left 0   :right -6 )
           (op2 1.0000 0.881
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.231 :dcy1 9.439 :dcy2 11.376 :rel 12.435
                      :peak 1.000 :bp   0.953 :sus  0.563])
           (fm2 0.1453 0.336 :bias +0.000 :env 0.501 :lag 0.519 :left 3   :right 0  )
           (op3 0.9724 0.906
                :lfo1  0.608 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 3.430 :dcy2 0.000 :rel 6.860
                      :peak 1.000 :bp   0.046 :sus  0.046])
           (fm3 0.2491 0.000 :bias +0.000 :env 0.671 :lag 0.165 :left 0   :right -9 )
           (op4 0.9441 0.934
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.100 :dcy2 0.000 :rel 0.201
                      :peak 1.000 :bp   0.048 :sus  0.048])
           (fm4 1.0000 0.000 :bias +0.000 :env 0.516 :lag 0.848 :left 6   :right 0  )
           (op5 0.9093 0.969
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 12.096 :dcy2 0.000 :rel 24.193
                      :peak 1.000 :bp   0.006 :sus  0.006])
           (fm5 0.8822 0.000 :bias +0.000 :env 0.888 :lag 0.161 :left 0   :right -9 )
           (op6 0.8814 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.446 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 8.713 :dcy2 0.000 :rel 17.427
                      :peak 1.000 :bp   0.034 :sus  0.034])
           (fm6 0.4679 0.699 :bias +0.000 :env 0.929 :lag 0.000 :left 0   :right -6 )
           (noise 1.0777 0.767 :bw  18
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.034 :dcy1 4.617 :dcy2 0.000 :rel 9.233
                        :peak 1.000 :bp   0.046 :sus  0.046])
           (noise2 1.000  0.000 :bw  13 :lag 0.000)
           (buzz 1.0777 0.818
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.225 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 9.771 :dcy2 0.000 :rel 19.543
                       :peak 1.000 :bp   0.014 :sus  0.014])
           (buzz-harmonics   2 :env  13 :cca   0 :hp   2 :hp<-env   0)
           (lp-filter :freq [  288 :track  0 :env 0.584
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.015 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 5.015 :rel 10.030 :sus 0.096]
                      :mode +0.165)
           (bp-filter :offset 3.000)
           (fold      :wet 0.832 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.9472 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-28    :lfo2 0.000 :lfo3 0.000 :xenv 0.623]
                      :pan  [-0.700 :lfo2 0.822 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.695 :xfb +0.000)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.987 :xenv 0.000]
                      :amp  [-22    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.526 :xfb +0.243)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  3 Three
;;
(save-program  3 "Three" "CCA -> filter cutoff"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.178 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.236 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.967)
           (lfo2    0.824 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    1.236 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  10.907 :dcy1 0.000 :dcy2 0.000 :rel 2.933
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  5.206 :dcy1 0.000 :dcy2 0.000 :rel 2.039
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 3.0000 1.830 :bias +0.000 :env 0.958 :lag 0.000 :left 9   :right 0  )
           (op2 3.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  6.271 :dcy1 0.000 :dcy2 0.000 :rel 4.380
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 3.0000 0.000 :bias +0.000 :env 0.547 :lag 0.000 :left 0   :right 0  )
           (op3 5.0000 0.752
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 5.807 :dcy2 0.000 :rel 11.614
                      :peak 1.000 :bp   0.068 :sus  0.068])
           (fm3 2.0000 0.915 :bias +0.000 :env 0.851 :lag 0.000 :left 9   :right 0  )
           (op4 7.0000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.323 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  3.146 :dcy1 0.000 :dcy2 0.000 :rel 4.350
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 2.0000 0.629 :bias +0.000 :env 0.260 :lag 0.000 :left 3   :right 0  )
           (op5 9.0000 0.804
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  5.618 :dcy1 7.921 :dcy2 0.000 :rel 5.498
                      :peak 1.000 :bp   0.583 :sus  0.583])
           (fm5 1.0000 0.383 :bias +0.000 :env 0.868 :lag 0.000 :left 0   :right 0  )
           (op6 9.0082 0.000
                :lfo1  0.335 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  2.198 :dcy1 0.000 :dcy2 0.000 :rel 9.014
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 0.000 :bias +0.000 :env 0.745 :lag 0.000 :left 0   :right 0  )
           (noise 4.9871 0.201 :bw  14
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  0.000 :dcy1 0.096 :dcy2 1.224 :rel 7.917
                        :peak 1.000 :bp   0.706 :sus  0.100])
           (noise2 9.0889 0.110 :bw  10 :lag 1.046)
           (buzz 3.0000 0.333
                 :lfo1  0.106 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  5.554 :dcy1 0.000 :dcy2 0.000 :rel 2.695
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  22 :env -16 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  151 :track  1 :env 0.265
                             :prss 0.000 :cca 0.339 :ccb 0.000]
                      :res [0.471 :cca +0.000 :ccb +0.000]
                      :env [:att 3.624 :dcy 0.000 :rel 0.302 :sus 1.000]
                      :mode +0.001)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.6188 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-37    :lfo2 0.000 :lfo3 0.000 :xenv 0.595]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.398 :xfb +0.017)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.613 :xenv 0.000]
                      :amp  [-43    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.830 :xfb +0.093)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  4 Four
;;
(save-program  4 "Four"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.687 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.486 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.433)
           (lfo2    0.557 :xenv  0.000 :cca  0.046 :ccb   0.000)
           (lfo3    0.186 :xenv  0.906 :cca  0.000 :ccb   0.000)
           (xenv :att  1.355 :dcy1 0.920 :dcy2 1.506 :rel 0.651
                 :peak 1.000 :bp   0.903 :sus  0.246)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.842
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.738 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.676 :dcy1 1.345 :dcy2 0.777 :rel 0.897
                      :peak 1.000 :bp   0.639 :sus  0.006])
           (fm1 0.2500 0.040 :bias +0.000 :env 0.882 :lag 0.000 :left 0   :right 0  )
           (op2 1.9902 0.979
                :lfo1  0.702 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.001 :dcy1 0.912 :dcy2 1.393 :rel 1.401
                      :peak 1.000 :bp   0.865 :sus  0.739])
           (fm2 1.0000 0.000 :bias +0.000 :env 0.552 :lag 0.000 :left 9   :right -3 )
           (op3 2.0083 0.468
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.721 :dcy1 1.971 :dcy2 1.223 :rel 1.025
                      :peak 1.000 :bp   0.744 :sus  0.280])
           (fm3 2.0000 0.493 :bias +0.000 :env 0.814 :lag 0.000 :left 0   :right -6 )
           (op4 3.0133 0.929
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.309 :dcy1 0.814 :dcy2 3.713 :rel 1.016
                      :peak 1.000 :bp   0.567 :sus  0.555])
           (fm4 3.0000 0.431 :bias +0.000 :env 0.908 :lag 0.000 :left 0   :right -9 )
           (op5 3.0288 0.346
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.528 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.210 :dcy1 1.479 :dcy2 0.677 :rel 1.384
                      :peak 1.000 :bp   0.734 :sus  0.727])
           (fm5 3.0000 0.000 :bias +0.000 :env 0.852 :lag 0.000 :left 0   :right 0  )
           (op6 5.0000 0.760
                :lfo1  0.147 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.405 :dcy1 1.089 :dcy2 1.446 :rel 4.841
                      :peak 1.000 :bp   0.745 :sus  0.136])
           (fm6 0.5000 0.157 :bias +0.000 :env 0.534 :lag 0.000 :left 0   :right 0  )
           (noise 3.0000 0.271 :bw  19
                  :lfo1 0.721   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  1.044 :dcy1 12.029 :dcy2 0.639 :rel 2.256
                        :peak 1.000 :bp   0.635 :sus  0.081])
           (noise2 2.0000 0.958 :bw  14 :lag 0.000)
           (buzz 1.9902 0.100
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  1.362 :dcy1 0.709 :dcy2 0.794 :rel 0.908
                       :peak 1.000 :bp   0.615 :sus  0.340])
           (buzz-harmonics  22 :env -14 :cca   0 :hp   2 :hp<-env   6)
           (lp-filter :freq [  228 :track  0 :env 0.291
                             :prss 0.000 :cca 0.582 :ccb 0.000]
                      :res [0.182 :cca +0.000 :ccb +0.000]
                      :env [:att 1.491 :dcy 1.775 :rel 1.426 :sus 0.735]
                      :mode -0.038)
           (bp-filter :offset 4.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.8972 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-59    :lfo2 0.000 :lfo3 0.000 :xenv 0.735]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.277 :xfb +0.115)
           (delay2    :time [0.0006 :lfo2 0.000 :lfo3 0.000 :xenv 0.713]
                      :amp  [-22    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.506 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.640 :xfb -0.205)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  5 Five
;;
(save-program  5 "Five"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.722 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    2.361 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.207)
           (lfo2    0.590 :xenv  0.746 :cca  0.000 :ccb   0.000)
           (lfo3    2.361 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.485 :dcy1 1.003 :dcy2 0.000 :rel 0.440
                 :peak 1.000 :bp   0.881 :sus  0.881)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.299 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.089 :dcy2 0.000 :rel 2.178
                      :peak 1.000 :bp   0.064 :sus  0.064])
           (fm1 2.0000 0.102 :bias +0.000 :env 0.223 :lag 0.212 :left 0   :right 0  )
           (op2 2.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.153 :dcy1 9.004 :dcy2 0.000 :rel 0.985
                      :peak 1.000 :bp   0.126 :sus  0.126])
           (fm2 2.0000 1.951 :bias +0.000 :env 0.813 :lag 0.097 :left 0   :right 0  )
           (op3 3.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.642 :dcy1 0.462 :dcy2 0.000 :rel 0.257
                      :peak 1.000 :bp   0.943 :sus  0.943])
           (fm3 0.5000 0.000 :bias +0.000 :env 0.564 :lag 0.000 :left 6   :right 0  )
           (op4 4.0000 0.250
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.464 :dcy1 1.174 :dcy2 0.000 :rel 0.517
                      :peak 1.000 :bp   0.717 :sus  0.717])
           (fm4 1.0000 0.000 :bias +0.000 :env 0.743 :lag 0.000 :left 0   :right -3 )
           (op5 7.0000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.099 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 0.317 :dcy2 0.000 :rel 0.633
                      :peak 1.000 :bp   0.163 :sus  0.163])
           (fm5 1.0000 0.440 :bias +0.000 :env 0.743 :lag 0.000 :left 0   :right -3 )
           (op6 8.0000 0.125
                :lfo1  0.456 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.068 :dcy1 1.352 :dcy2 0.000 :rel 0.300
                      :peak 1.000 :bp   0.704 :sus  0.704])
           (fm6 1.0000 0.923 :bias +0.000 :env 0.828 :lag 0.880 :left 0   :right 0  )
           (noise 6.0000 0.167 :bw  16
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  0.357 :dcy1 0.708 :dcy2 11.003 :rel 0.675
                        :peak 0.732 :bp   0.115 :sus  0.652])
           (noise2 5.0000 0.000 :bw  11 :lag 1.704)
           (buzz 2.0000 0.500
                 :lfo1  0.105 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.203 :dcy1 1.433 :dcy2 0.000 :rel 0.613
                       :peak 1.000 :bp   0.919 :sus  0.919])
           (buzz-harmonics   8 :env  16 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.044 :rel 1.000 :sus 0.241]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.8471 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-45    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.091 :xfb +0.000)
           (delay2    :time [0.5647 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-16    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.438 :xfb -0.415)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  6 Six
;;
(save-program  6 "Six"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.571 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    3.154 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.410)
           (lfo2    3.943 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.493 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.377 :dcy1 0.000 :dcy2 0.000 :rel 0.288
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.5000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.359 :dcy1 0.243 :dcy2 0.000 :rel 0.394
                      :peak 1.000 :bp   0.657 :sus  0.657])
           (fm1 2.0000 1.433 :bias +0.000 :env 0.931 :lag 0.000 :left 6   :right 0  )
           (op2 1.5000 0.429
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.557 :dcy1 0.000 :dcy2 0.000 :rel 0.121
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.616 :lag 0.671 :left 6   :right 0  )
           (op3 2.5000 0.714
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.658 :dcy1 0.000 :dcy2 0.000 :rel 0.273
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 2.0000 0.912 :bias +0.000 :env 0.636 :lag 0.519 :left 9   :right -9 )
           (op4 3.5000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.103 :dcy1 0.000 :dcy2 0.000 :rel 0.315
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.891 :lag 0.210 :left 3   :right 0  )
           (op5 5.5000 0.636
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.780 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.642 :dcy1 0.000 :dcy2 0.000 :rel 0.780
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 1.0000 0.704 :bias +0.000 :env 0.063 :lag 0.365 :left 0   :right 0  )
           (op6 7.5000 0.467
                :lfo1  0.377 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.309 :dcy1 0.000 :dcy2 0.000 :rel 0.231
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 0.000 :bias +0.000 :env 0.539 :lag 0.000 :left 0   :right 0  )
           (noise 6.5000 0.000 :bw  13
                  :lfo1 0.071   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -3
                  :env [:att  0.304 :dcy1 0.000 :dcy2 0.000 :rel 0.317
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 4.5000 0.933 :bw  17 :lag 0.000)
           (buzz 1.5000 0.429
                 :lfo1  0.141 :cca 0.000 :ccb 0.000 :vel 0.002 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.324 :dcy1 0.212 :dcy2 0.000 :rel 0.316
                       :peak 1.000 :bp   0.678 :sus  0.678])
           (buzz-harmonics  12 :env  17 :cca   0 :hp   0 :hp<-env   0)
           (lp-filter :freq [   50 :track  1 :env 0.910
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.784 :cca +0.000 :ccb +0.000]
                      :env [:att 0.199 :dcy 0.000 :rel 0.415 :sus 1.000]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.808 :gain  8 :cca  +0 :ccb  +0)
           (delay1    :time [0.5073 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-37    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.553 :xfb +0.051)
           (delay2    :time [0.0008 :lfo2 0.000 :lfo3 0.819 :xenv 0.000]
                      :amp  [-15    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.798 :xfb -0.029)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  7 "Seven"
;;
(save-program  7 "Seven"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.822 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.564 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.170)
           (lfo2    3.129 :xenv  0.000 :cca  0.753 :ccb   0.000)
           (lfo3    0.049 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.708 :dcy1 0.531 :dcy2 0.788 :rel 0.245
                 :peak 1.000 :bp   0.502 :sus  0.553)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.9966 0.110
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.029 :dcy1 0.658 :dcy2 0.214 :rel 0.949
                      :peak 1.000 :bp   0.437 :sus  0.152])
           (fm1 1.0000 0.687 :bias +0.000 :env 0.942 :lag 0.079 :left 0   :right -9 )
           (op2 1.0000 0.925
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.211 :dcy1 0.520 :dcy2 0.584 :rel 0.786
                      :peak 1.000 :bp   0.046 :sus  0.297])
           (fm2 1.0000 0.729 :bias +0.000 :env 0.512 :lag 0.000 :left 0   :right 0  )
           (op3 2.9974 0.998
                :lfo1  0.900 :cca 0.000 :ccb 0.000 :vel 0.965 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.406 :dcy1 0.000 :dcy2 0.000 :rel 0.572
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 0.000 :bias +0.000 :env 0.597 :lag 0.690 :left 0   :right 0  )
           (op4 5.0000 0.360
                :lfo1  0.202 :cca 0.000 :ccb 0.000 :vel 0.475 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.750 :dcy1 0.987 :dcy2 1.319 :rel 0.711
                      :peak 1.000 :bp   0.338 :sus  0.077])
           (fm4 3.0000 1.100 :bias +0.000 :env 0.470 :lag 0.000 :left 0   :right -3 )
           (op5 6.9405 0.187
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.109 :dcy1 0.770 :dcy2 0.680 :rel 1.143
                      :peak 1.000 :bp   0.510 :sus  0.395])
           (fm5 0.0817 0.000 :bias +0.000 :env 0.648 :lag 0.000 :left 0   :right -6 )
           (op6 7.0000 0.184
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.540 :dcy1 0.822 :dcy2 0.423 :rel 0.404
                      :peak 1.000 :bp   0.976 :sus  0.435])
           (fm6 1.0000 1.358 :bias +0.000 :env 0.745 :lag 0.000 :left 0   :right 0  )
           (noise 6.9702 0.185 :bw  19
                  :lfo1 0.120   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -3
                  :env [:att  1.103 :dcy1 0.789 :dcy2 0.636 :rel 0.235
                        :peak 1.000 :bp   0.760 :sus  0.531])
           (noise2 3.0000 1.000 :bw  17 :lag 0.000)
           (buzz 1.0000 0.111
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.325 :dcy1 0.547 :dcy2 0.965 :rel 7.642
                       :peak 1.000 :bp   0.536 :sus  0.842])
           (buzz-harmonics  15 :env  13 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  377 :track  0 :env 0.574
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.177 :cca +0.000 :ccb +0.000]
                      :env [:att 0.432 :dcy 0.373 :rel 0.281 :sus 0.682]
                      :mode -0.166)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.2785 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-52    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.278 :xfb +0.237)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.452 :xenv 0.000]
                      :amp  [-30    :lfo2 0.000 :lfo3 0.000 :xenv 0.903]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.684 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  8 Eight
;;
(save-program  8 "Eight"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.122 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    2.049 :bleed 0.088 :cca  0.000 :prss  0.000 :delay 0.253)
           (lfo2    1.024 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.512 :xenv  0.956 :cca  0.000 :ccb   0.000)
           (xenv :att  0.030 :dcy1 3.673 :dcy2 0.000 :rel 7.345
                 :peak 1.000 :bp   0.038 :sus  0.038)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 4.634 :dcy2 0.000 :rel 9.268
                      :peak 1.000 :bp   0.014 :sus  0.014])
           (fm1 1.0000 0.218 :bias +0.000 :env 0.508 :lag 0.015 :left 0   :right 0  )
           (op2 3.0000 0.111
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 4.089 :dcy2 0.000 :rel 8.177
                      :peak 1.000 :bp   0.057 :sus  0.057])
           (fm2 1.0000 0.758 :bias +0.000 :env 0.558 :lag 0.608 :left 0   :right 0  )
           (op3 5.0000 0.784
                :lfo1  0.990 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 3.619 :dcy2 0.000 :rel 7.238
                      :peak 1.000 :bp   0.194 :sus  0.194])
           (fm3 2.0000 0.000 :bias +0.000 :env 0.773 :lag 0.058 :left 6   :right 0  )
           (op4 7.0000 0.020
                :lfo1  0.430 :cca 0.000 :ccb 0.000 :vel 0.500 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 5.310 :dcy2 0.000 :rel 10.621
                      :peak 1.000 :bp   0.060 :sus  0.060])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.734 :lag 0.210 :left 0   :right -6 )
           (op5 9.0000 0.012
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.011 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 4.008 :dcy2 0.000 :rel 8.015
                      :peak 1.000 :bp   0.040 :sus  0.040])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.849 :lag 0.000 :left 0   :right -6 )
           (op6 9.0768 0.012
                :lfo1  0.823 :cca 0.000 :ccb 0.000 :vel 0.497 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 4.842 :dcy2 0.000 :rel 9.684
                      :peak 1.000 :bp   0.063 :sus  0.063])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.528 :lag 0.913 :left 0   :right 0  )
           (noise 7.0016 0.020 :bw  13
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  0.000 :dcy1 9.306 :dcy2 0.000 :rel 18.613
                        :peak 1.000 :bp   0.219 :sus  0.219])
           (noise2 5.0295 0.964 :bw  12 :lag 0.000)
           (buzz 3.0000 0.111
                 :lfo1  0.463 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 1.857 :dcy2 0.000 :rel 3.715
                       :peak 1.000 :bp   0.061 :sus  0.061])
           (buzz-harmonics  18 :env   6 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.461
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.075 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 3.392 :rel 6.784 :sus 0.214]
                      :mode -0.242)
           (bp-filter :offset 6.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [0.9762 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-17    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.755 :xfb +0.000)
           (delay2    :time [0.0009 :lfo2 0.000 :lfo3 0.683 :xenv 0.000]
                      :amp  [-19    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.697 :xfb -0.106)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  9 Nine
;;
(save-program  9 "Nine"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.077 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.152 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.137)
           (lfo2    0.608 :xenv  0.000 :cca  0.311 :ccb   0.000)
           (lfo3    2.431 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.534 :dcy1 0.000 :dcy2 0.000 :rel 0.355
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0966 :a1 -0.0000 :a2 -0.0950 :a3 +0.0286
                 :t1 0.118   :t2 0.395   :t3 0.341   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.119 :dcy1 0.000 :dcy2 0.000 :rel 0.108
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 1.0000 0.655 :bias +0.000 :env 0.006 :lag 0.812 :left 0   :right -3 )
           (op2 2.0000 1.000
                :lfo1  0.745 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0794  :key  60 :left   +0 :right  +0
                :env [:att  0.346 :dcy1 0.000 :dcy2 0.000 :rel 0.674
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 0.5000 0.000 :bias +0.000 :env 0.686 :lag 0.931 :left 0   :right -3 )
           (op3 2.0185 0.991
                :lfo1  0.050 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.199 :dcy1 0.000 :dcy2 0.000 :rel 0.244
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 0.000 :bias +0.000 :env 0.763 :lag 0.000 :left 0   :right -9 )
           (op4 3.0000 0.667
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.226 :dcy1 0.000 :dcy2 0.000 :rel 0.118
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 0.5000 1.870 :bias +0.000 :env 0.750 :lag 0.347 :left 0   :right 0  )
           (op5 4.0000 0.500
                :lfo1  0.952 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0071  :key  60 :left   +0 :right  +0
                :env [:att  0.150 :dcy1 0.000 :dcy2 0.000 :rel 0.122
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 2.0000 1.152 :bias +0.000 :env 0.729 :lag 0.430 :left 3   :right 0  )
           (op6 7.0000 0.286
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.346 :dcy1 0.000 :dcy2 0.000 :rel 0.232
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.928 :lag 0.053 :left 0   :right 0  )
           (noise 6.0000 0.830 :bw  15
                  :lfo1 0.276   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0013 :key  60   :left   +0 :right  -9
                  :env [:att  0.000 :dcy1 0.151 :dcy2 1.000 :rel 1.000
                        :peak 1.000 :bp   0.648 :sus  0.100])
           (noise2 5.0000 0.400 :bw  12 :lag 0.000)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  -9
                 :env [:att  0.329 :dcy1 0.000 :dcy2 0.000 :rel 0.734
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  22 :env -19 :cca   0 :hp   1 :hp<-env   8)
           (lp-filter :freq [   50 :track  0 :env 0.314
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.235 :cca +0.000 :ccb +0.000]
                      :env [:att 0.466 :dcy 0.000 :rel 0.368 :sus 1.000]
                      :mode -0.031)
           (bp-filter :offset 6.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.8228 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-51    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.682 :xfb +0.161)
           (delay2    :time [0.4114 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-25    :lfo2 0.000 :lfo3 0.000 :xenv 0.714]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.753 :xfb -0.020)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  9 Nine
;;
(save-program  9 "Nine"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.530 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.283 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.963)
           (lfo2    4.530 :xenv  0.475 :cca  0.000 :ccb   0.000)
           (lfo3    0.283 :xenv  0.549 :cca  0.000 :ccb   0.000)
           (xenv :att  0.506 :dcy1 1.504 :dcy2 0.000 :rel 0.542
                 :peak 1.000 :bp   0.674 :sus  0.674)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.382 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.243 :dcy1 0.541 :dcy2 0.000 :rel 0.697
                      :peak 1.000 :bp   0.722 :sus  0.722])
           (fm1 2.0000 1.496 :bias +0.000 :env 0.737 :lag 0.000 :left 0   :right 0  )
           (op2 2.0000 1.000
                :lfo1  0.429 :cca 0.000 :ccb 0.000 :vel 0.384 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.732 :dcy1 0.758 :dcy2 0.000 :rel 0.429
                      :peak 1.000 :bp   0.635 :sus  0.635])
           (fm2 0.5000 0.000 :bias +0.000 :env 0.685 :lag 0.611 :left 0   :right -9 )
           (op3 5.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.860 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.394 :dcy1 0.522 :dcy2 0.000 :rel 0.786
                      :peak 1.000 :bp   0.848 :sus  0.848])
           (fm3 1.0000 1.139 :bias +0.000 :env 0.818 :lag 0.492 :left 0   :right -9 )
           (op4 6.9825 0.286
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.108 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.702 :dcy1 0.653 :dcy2 0.000 :rel 0.511
                      :peak 1.000 :bp   0.547 :sus  0.547])
           (fm4 3.0000 0.000 :bias +0.000 :env 0.535 :lag 0.752 :left 9   :right 0  )
           (op5 7.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.570 :dcy1 9.930 :dcy2 0.000 :rel 0.459
                      :peak 1.000 :bp   0.193 :sus  0.193])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.660 :lag 0.000 :left 0   :right 0  )
           (op6 8.0000 0.250
                :lfo1  0.218 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.718 :dcy1 0.469 :dcy2 0.000 :rel 0.745
                      :peak 1.000 :bp   0.511 :sus  0.511])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.488 :lag 0.880 :left 3   :right 0  )
           (noise 4.0000 0.500 :bw  11
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.411 :dcy1 0.656 :dcy2 0.452 :rel 0.602
                        :peak 1.000 :bp   0.654 :sus  0.520])
           (noise2 5.0462 0.396 :bw  19 :lag 0.000)
           (buzz 2.0000 1.000
                 :lfo1  0.066 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.707 :dcy1 0.790 :dcy2 0.000 :rel 0.757
                       :peak 1.000 :bp   0.560 :sus  0.560])
           (buzz-harmonics  31 :env -25 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.539
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.163 :cca +0.000 :ccb +0.000]
                      :env [:att 0.638 :dcy 0.957 :rel 0.546 :sus 0.051]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.7661 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-20    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.495 :xfb +0.000)
           (delay2    :time [0.4415 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-55    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.558 :xfb +0.254)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  10 Ten
;;
(save-program  10 "Ten"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.594 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.063 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.649)
           (lfo2    0.844 :xenv  0.941 :cca  0.000 :ccb   0.000)
           (lfo3    0.053 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.913 :dcy1 1.506 :dcy2 0.000 :rel 11.531
                 :peak 1.000 :bp   0.824 :sus  0.824)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.753 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.802 :dcy1 1.089 :dcy2 0.000 :rel 1.106
                      :peak 1.000 :bp   0.564 :sus  0.564])
           (fm1 1.0000 0.003 :bias +0.000 :env 0.908 :lag 0.000 :left 6   :right 0  )
           (op2 2.0000 1.000
                :lfo1  0.051 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.019 :dcy1 1.340 :dcy2 0.000 :rel 1.285
                      :peak 1.000 :bp   0.554 :sus  0.554])
           (fm2 0.5000 0.000 :bias +0.000 :env 0.798 :lag 0.000 :left 0   :right -9 )
           (op3 7.0000 0.082
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.460 :dcy1 1.582 :dcy2 0.000 :rel 5.716
                      :peak 1.000 :bp   0.774 :sus  0.774])
           (fm3 3.0000 0.111 :bias +0.000 :env 0.152 :lag 0.000 :left 0   :right -6 )
           (op4 8.0000 0.063
                :lfo1  0.972 :cca 0.000 :ccb 0.000 :vel 0.336 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.395 :dcy1 2.161 :dcy2 0.000 :rel 1.503
                      :peak 1.000 :bp   0.435 :sus  0.435])
           (fm4 2.0000 0.487 :bias +0.000 :env 0.900 :lag 0.319 :left 6   :right 0  )
           (op5 10.0000 0.040
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.846 :dcy1 0.904 :dcy2 0.000 :rel 2.806
                      :peak 1.000 :bp   0.296 :sus  0.296])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.663 :lag 0.000 :left 0   :right 0  )
           (op6 10.0359 0.040
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.629 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.076 :dcy1 0.779 :dcy2 0.000 :rel 1.919
                      :peak 1.000 :bp   0.707 :sus  0.707])
           (fm6 1.0000 0.000 :bias +0.000 :env 0.600 :lag 0.266 :left 0   :right 0  )
           (noise 4.0000 0.250 :bw  17
                  :lfo1 0.528   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  2.514 :dcy1 1.117 :dcy2 0.000 :rel 0.134
                        :peak 1.000 :bp   0.595 :sus  0.595])
           (noise2 5.0000 0.000 :bw  10 :lag 1.391)
           (buzz 2.0000 1.000
                 :lfo1  0.293 :cca 0.000 :ccb 0.000 :vel 0.006 :prss 0.000
                 :penv +0.0000  :key  48 :left   +0 :right  -9
                 :env [:att  0.913 :dcy1 1.007 :dcy2 0.000 :rel 1.325
                       :peak 1.000 :bp   0.705 :sus  0.705])
           (buzz-harmonics  27 :env -20 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  208 :track  0 :env 0.546
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.172 :cca +0.000 :ccb +0.000]
                      :env [:att 1.091 :dcy 1.302 :rel 0.987 :sus 0.903]
                      :mode -0.090)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.5802 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-32    :lfo2 0.000 :lfo3 0.000 :xenv 0.890]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.644 :xfb +0.000)
           (delay2    :time [0.0009 :lfo2 0.000 :lfo3 0.553 :xenv 0.000]
                      :amp  [-45    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.842 :xfb -0.063)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

