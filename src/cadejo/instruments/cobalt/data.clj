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

;; ------------------------------------------------------  11 Eleven
;;
(save-program  11 "Eleven"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.462 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    3.231 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.108)
           (lfo2    0.718 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.359 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.966 :dcy1 0.549 :dcy2 2.163 :rel 2.131
                 :peak 1.000 :bp   0.669 :sus  0.381)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.185
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.138 :dcy1 3.152 :dcy2 1.289 :rel 0.676
                      :peak 1.000 :bp   0.981 :sus  0.199])
           (fm1 1.0000 1.889 :bias +0.000 :env 0.366 :lag 0.000 :left 9   :right 0  )
           (op2 3.0000 0.000
                :lfo1  0.471 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.456 :dcy1 2.207 :dcy2 2.827 :rel 1.331
                      :peak 1.000 :bp   0.159 :sus  0.991])
           (fm2 2.0000 1.672 :bias +0.000 :env 0.754 :lag 0.000 :left 3   :right 0  )
           (op3 5.0247 0.235
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.376 :dcy1 0.247 :dcy2 2.145 :rel 1.636
                      :peak 1.000 :bp   0.756 :sus  0.643])
           (fm3 0.5000 0.410 :bias +0.000 :env 0.851 :lag 0.000 :left 9   :right -3 )
           (op4 7.0000 0.445
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  2.690 :dcy1 5.694 :dcy2 1.981 :rel 2.486
                      :peak 1.000 :bp   0.814 :sus  0.951])
           (fm4 0.2500 1.162 :bias +0.000 :env 0.841 :lag 0.040 :left 0   :right 0  )
           (op5 9.0000 0.516
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.169 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.662 :dcy1 2.720 :dcy2 3.100 :rel 1.118
                      :peak 1.000 :bp   0.638 :sus  0.431])
           (fm5 0.5000 0.348 :bias +0.000 :env 0.599 :lag 0.000 :left 0   :right 0  )
           (op6 10.9751 0.147
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.049 :dcy1 0.182 :dcy2 3.396 :rel 5.941
                      :peak 1.000 :bp   0.563 :sus  0.100])
           (fm6 1.0000 0.000 :bias +0.000 :env 0.987 :lag 0.572 :left 0   :right 0  )
           (noise 1.0007 0.957 :bw  16
                  :lfo1 0.275   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  5.070 :dcy1 1.093 :dcy2 2.520 :rel 2.565
                        :peak 1.000 :bp   0.692 :sus  0.423])
           (noise2 5.0000 0.626 :bw  11 :lag 0.000)
           (buzz 1.0007 0.709
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.229 :dcy1 2.322 :dcy2 2.022 :rel 0.826
                       :peak 1.000 :bp   0.539 :sus  0.170])
           (buzz-harmonics  15 :env   1 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  1 :env 0.955
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.494 :cca +0.000 :ccb +0.000]
                      :env [:att 1.236 :dcy 3.256 :rel 1.813 :sus 0.847]
                      :mode +0.053)
           (bp-filter :offset 3.000)
           (fold      :wet 0.859 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.9286 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-47    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.218 :xfb +0.104)
           (delay2    :time [0.0001 :lfo2 0.000 :lfo3 0.351 :xenv 0.000]
                      :amp  [-14    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.586 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  12 Twelve
;;
(save-program  12 "Twelve"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.546 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.173 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.109)
           (lfo2    2.773 :xenv  0.000 :cca  0.065 :ccb   0.000)
           (lfo3    0.022 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.628 :dcy1 0.000 :dcy2 0.000 :rel 2.391
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.9981 0.245
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.469 :dcy1 0.000 :dcy2 0.000 :rel 3.187
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 3.0000 0.499 :bias +0.000 :env 0.838 :lag 0.000 :left 9   :right -6 )
           (op2 1.0000 0.246
                :lfo1  0.265 :cca 0.000 :ccb 0.000 :vel 0.819 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.816 :dcy1 0.000 :dcy2 0.000 :rel 2.839
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 2.0000 1.484 :bias +0.000 :env 0.751 :lag 0.000 :left 0   :right 0  )
           (op3 2.0000 0.985
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.691 :dcy1 0.000 :dcy2 0.000 :rel 2.307
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 0.2500 0.000 :bias +0.000 :env 0.871 :lag 0.830 :left 0   :right 0  )
           (op4 2.0157 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.139 :dcy1 0.000 :dcy2 0.000 :rel 1.863
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 2.0000 1.522 :bias +0.000 :env 0.436 :lag 0.325 :left 6   :right 0  )
           (op5 4.0000 0.254
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.412 :dcy1 0.000 :dcy2 0.000 :rel 3.212
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.595 :lag 0.577 :left 6   :right -9 )
           (op6 4.9718 0.164
                :lfo1  0.328 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.458 :dcy1 0.000 :dcy2 0.000 :rel 2.752
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 0.933 :bias +0.000 :env 0.064 :lag 0.599 :left 0   :right -6 )
           (noise 2.0182 0.997 :bw  15
                  :lfo1 0.953   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  3.117 :dcy1 0.000 :dcy2 0.000 :rel 1.672
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 1.0033 0.951 :bw  14 :lag 0.845)
           (buzz 1.0000 0.246
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  2.560 :dcy1 0.000 :dcy2 0.000 :rel 3.052
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  15 :env   2 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.325
                             :prss 0.000 :cca 0.593 :ccb 0.000]
                      :res [0.325 :cca +0.000 :ccb +0.000]
                      :env [:att 2.748 :dcy 0.000 :rel 2.552 :sus 1.000]
                      :mode -0.366)
           (bp-filter :offset 2.000)
           (fold      :wet 0.721 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [1.4425 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-35    :lfo2 0.000 :lfo3 0.000 :xenv 0.816]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.775 :xfb +0.000)
           (delay2    :time [0.0010 :lfo2 0.000 :lfo3 0.167 :xenv 0.094]
                      :amp  [-28    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.520 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))



;; ------------------------------------------------------  13 Thirteen
;;
(save-program  13 "Thirteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.927 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.285 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.037)
           (lfo2    3.964 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    2.642 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.003 :dcy1 1.336 :dcy2 0.000 :rel 2.673
                 :peak 1.000 :bp   0.013 :sus  0.013)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.058 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.044 :dcy1 0.379 :dcy2 0.000 :rel 0.758
                      :peak 1.000 :bp   0.226 :sus  0.226])
           (fm1 3.0000 1.587 :bias +0.000 :env 0.898 :lag 0.319 :left 0   :right -9 )
           (op2 2.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.268 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.149 :dcy2 3.067 :rel 2.554
                      :peak 1.000 :bp   0.598 :sus  0.100])
           (fm2 3.0000 0.427 :bias +0.000 :env 0.772 :lag 0.069 :left 0   :right 0  )
           (op3 3.0000 0.750
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 2.156 :dcy2 0.000 :rel 4.313
                      :peak 1.000 :bp   0.054 :sus  0.054])
           (fm3 1.0000 0.000 :bias +0.000 :env 0.579 :lag 0.624 :left 6   :right -6 )
           (op4 3.9903 0.976
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.013 :dcy2 0.000 :rel 2.026
                      :peak 1.000 :bp   0.158 :sus  0.158])
           (fm4 0.5000 0.000 :bias +0.000 :env 0.688 :lag 0.822 :left 0   :right 0  )
           (op5 4.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.778 :dcy2 0.000 :rel 1.557
                      :peak 1.000 :bp   0.140 :sus  0.140])
           (fm5 3.0000 0.598 :bias +0.000 :env 0.862 :lag 0.000 :left 6   :right 0  )
           (op6 5.0000 0.800
                :lfo1  0.200 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 1.030 :dcy2 0.000 :rel 2.060
                      :peak 1.000 :bp   0.018 :sus  0.018])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.324 :lag 0.000 :left 6   :right -3 )
           (noise 6.0000 0.667 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  0.000 :dcy1 1.008 :dcy2 0.000 :rel 2.016
                        :peak 1.000 :bp   0.088 :sus  0.088])
           (noise2 7.0000 0.957 :bw  18 :lag 1.341)
           (buzz 2.0000 0.500
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.100 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.733 :dcy2 0.000 :rel 1.466
                       :peak 1.000 :bp   0.146 :sus  0.146])
           (buzz-harmonics  19 :env   5 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.421
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.275 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 1.060 :rel 2.119 :sus 0.029]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.5138 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-36    :lfo2 0.000 :lfo3 0.000 :xenv 0.898]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.647]
                      :fb    +0.572 :xfb +0.000)
           (delay2    :time [0.0010 :lfo2 0.000 :lfo3 0.388 :xenv 0.419]
                      :amp  [-56    :lfo2 0.000 :lfo3 0.000 :xenv 0.964]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.859 :xfb +0.051)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  14 Fourteen
;;
(save-program  14 "Fourteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.127 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.503 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.224)
           (lfo2    4.127 :xenv  0.000 :cca  0.884 :ccb   0.000)
           (lfo3    0.064 :xenv  0.323 :cca  0.000 :ccb   0.000)
           (xenv :att  0.506 :dcy1 0.521 :dcy2 0.668 :rel 1.409
                 :peak 1.000 :bp   0.541 :sus  0.743)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.801
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.583 :dcy1 1.351 :dcy2 1.423 :rel 0.523
                      :peak 1.000 :bp   0.237 :sus  0.994])
           (fm1 2.0000 0.000 :bias +0.000 :env 0.642 :lag 0.755 :left 9   :right -9 )
           (op2 2.0000 0.988
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.810 :dcy1 1.315 :dcy2 1.309 :rel 1.529
                      :peak 1.000 :bp   0.748 :sus  0.429])
           (fm2 2.0000 1.601 :bias +0.000 :env 0.099 :lag 0.567 :left 6   :right 0  )
           (op3 2.0021 0.986
                :lfo1  0.083 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.845 :dcy1 1.386 :dcy2 1.816 :rel 1.412
                      :peak 1.000 :bp   0.400 :sus  0.260])
           (fm3 0.5000 0.909 :bias +0.000 :env 0.636 :lag 0.000 :left 9   :right -3 )
           (op4 2.0151 0.974
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.096 :dcy1 1.599 :dcy2 1.584 :rel 2.109
                      :peak 1.000 :bp   0.987 :sus  0.101])
           (fm4 0.5000 1.384 :bias +0.000 :env 0.635 :lag 0.694 :left 0   :right 0  )
           (op5 3.0000 0.439
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.123 :dcy1 0.950 :dcy2 3.111 :rel 1.242
                      :peak 1.000 :bp   0.724 :sus  0.737])
           (fm5 1.0000 0.000 :bias +0.000 :env 0.920 :lag 0.443 :left 3   :right 0  )
           (op6 6.0000 0.110
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.103 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  2.821 :dcy1 0.260 :dcy2 1.368 :rel 1.506
                      :peak 1.000 :bp   0.892 :sus  0.065])
           (fm6 1.0000 1.927 :bias +0.000 :env 0.113 :lag 0.459 :left 0   :right -9 )
           (noise 5.0000 0.158 :bw  10
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  1.570 :dcy1 2.505 :dcy2 0.257 :rel 1.572
                        :peak 1.000 :bp   0.640 :sus  0.816])
           (noise2 1.9884 1.000 :bw  13 :lag 0.000)
           (buzz 1.9884 1.000
                 :lfo1  0.011 :cca 0.000 :ccb 0.000 :vel 0.758 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.010 :dcy1 0.423 :dcy2 2.345 :rel 1.374
                       :peak 1.000 :bp   0.670 :sus  0.100])
           (buzz-harmonics  19 :env   9 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  1 :env 0.875
                             :prss 0.000 :cca 0.671 :ccb 0.000]
                      :res [0.551 :cca +0.000 :ccb +0.000]
                      :env [:att 8.322 :dcy 1.028 :rel 1.067 :sus 0.468]
                      :mode +0.069)
           (bp-filter :offset 2.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.4846 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-18    :lfo2 0.000 :lfo3 0.000 :xenv 0.879]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 1.000]
                      :fb    +0.634 :xfb +0.100)
           (delay2    :time [0.0009 :lfo2 0.200 :lfo3 0.184 :xenv 0.536]
                      :amp  [-18    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.092 :xenv 0.000]
                      :fb    +0.757 :xfb +0.100)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  15 Fifteen
;;
(save-program  15 "Fifteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.745 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.582 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.536)
           (lfo2    1.186 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.099 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.006 :dcy1 0.000 :dcy2 0.000 :rel 0.086
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.253
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.176 :dcy1 0.000 :dcy2 0.000 :rel 0.226
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 2.0000 1.000 :bias +0.000 :env 0.848 :lag 0.209 :left 9   :right 0  )
           (op2 1.9864 0.997
                :lfo1  0.804 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.137 :dcy1 0.000 :dcy2 0.000 :rel 0.076
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 1.0000 1.000 :bias +0.000 :env 0.857 :lag 0.369 :left 9   :right 0  )
           (op3 1.9891 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.131 :dcy1 0.000 :dcy2 0.000 :rel 0.124
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 1.758 :bias +0.000 :env 0.608 :lag 0.575 :left 0   :right 0  )
           (op4 1.9943 0.995
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.177 :dcy1 0.000 :dcy2 0.000 :rel 0.105
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 0.5000 0.208 :bias +0.000 :env 0.880 :lag 0.000 :left 0   :right 0  )
           (op5 2.0000 0.989
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.126 :dcy1 0.000 :dcy2 0.000 :rel 0.069
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 1.0000 0.989 :bias +0.000 :env 0.764 :lag 0.422 :left 3   :right 0  )
           (op6 3.0000 0.440
                :lfo1  0.852 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.321 :dcy1 0.000 :dcy2 0.000 :rel 0.166
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 0.900 :bias +0.000 :env 0.597 :lag 0.000 :left 0   :right 0  )
           (noise 2.0191 0.971 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.142 :dcy1 0.000 :dcy2 0.000 :rel 2.732
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 4.0000 0.247 :bw  18 :lag 0.277)
           (buzz 1.9864 0.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.156 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.181 :dcy1 0.000 :dcy2 0.000 :rel 0.396
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics   7 :env  19 :cca   0 :hp   1 :hp<-env   5)
           (lp-filter :freq [  161 :track  2 :env 0.339
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.844 :cca +0.000 :ccb +0.000]
                      :env [:att 0.177 :dcy 0.000 :rel 0.324 :sus 1.000]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.6859 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-48    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.879 :xfb +0.082)
           (delay2    :time [0.0007 :lfo2 0.000 :lfo3 0.781 :xenv 0.000]
                      :amp  [-48    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.733 :xfb -0.072)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  16 Sixteen
;;
(save-program  16 "Sixteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.207 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.131 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.150)
           (lfo2    0.526 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.044 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.672 :dcy1 0.000 :dcy2 0.000 :rel 1.100
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 3.0000 0.335
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.030 :dcy2 1.520 :rel 1.433
                      :peak 1.000 :bp   0.640 :sus  0.100])
           (fm1 1.0000 0.172 :bias +0.000 :env 0.713 :lag 0.000 :left 0   :right 0  )
           (op2 5.0000 0.201
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.131 :dcy1 0.000 :dcy2 0.000 :rel 0.172
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.918 :lag 0.563 :left 0   :right -9 )
           (op3 5.0245 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  2.414 :dcy1 0.000 :dcy2 0.000 :rel 0.207
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 0.0386 1.516 :bias +0.000 :env 0.970 :lag 0.000 :left 0   :right 0  )
           (op4 7.0000 0.143
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.376 :dcy1 0.000 :dcy2 0.000 :rel 1.275
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.676 :lag 0.500 :left 0   :right 0  )
           (op5 9.0000 0.112
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.153 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.469 :dcy1 0.000 :dcy2 0.000 :rel 0.524
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 3.0000 1.470 :bias +0.000 :env 0.733 :lag 0.000 :left 0   :right -3 )
           (op6 11.0000 0.091
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.421 :dcy1 0.000 :dcy2 0.000 :rel 0.240
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 0.451 :bias +0.000 :env 0.692 :lag 0.000 :left 6   :right -6 )
           (noise 1.0043 1.000 :bw  13
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.000 :dcy1 0.526 :dcy2 0.000 :rel 1.052
                        :peak 1.000 :bp   0.072 :sus  0.072])
           (noise2 1.0000 0.996 :bw  11 :lag 0.485)
           (buzz 1.0043 1.000
                 :lfo1  0.141 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.591 :dcy1 0.000 :dcy2 0.000 :rel 1.073
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  27 :env  -6 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 5.584 :dcy 0.000 :rel 1.449 :sus 1.000]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.9508 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-30    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 1.000]
                      :fb    +0.254 :xfb -0.029)
           (delay2    :time [0.0010 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-44    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.584 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  17 Seventeen
;;
(save-program  17 "Seventeen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.348 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    6.418 :bleed 0.000 :cca  0.099 :prss  0.000 :delay 0.368)
           (lfo2    1.070 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    2.139 :xenv  0.000 :cca  0.000 :ccb   0.301)
           (xenv :att  0.283 :dcy1 0.000 :dcy2 0.000 :rel 0.137
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.275 :cca 0.000 :ccb 0.000 :vel 0.708 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.701 :dcy1 0.000 :dcy2 0.000 :rel 0.253
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 2.0000 0.000 :bias +0.000 :env 0.528 :lag 0.000 :left 0   :right 0  )
           (op2 1.0007 0.896
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.960 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.237 :dcy1 0.000 :dcy2 0.000 :rel 12.643
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 0.5000 0.623 :bias +0.000 :env 0.504 :lag 0.000 :left 0   :right -9 )
           (op3 1.0037 0.335
                :lfo1  0.617 :cca 0.000 :ccb 0.000 :vel 0.276 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.209 :dcy1 0.000 :dcy2 0.000 :rel 0.352
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 2.0000 0.016 :bias +0.000 :env 0.536 :lag 0.842 :left 0   :right 0  )
           (op4 1.9848 0.662
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.318 :dcy1 0.000 :dcy2 0.000 :rel 0.277
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 3.0000 1.648 :bias +0.000 :env 0.162 :lag 0.864 :left 9   :right 0  )
           (op5 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.201 :dcy1 0.000 :dcy2 0.000 :rel 0.274
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.581 :lag 0.380 :left 6   :right 0  )
           (op6 3.9631 0.757
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.686 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.259 :dcy1 0.000 :dcy2 0.000 :rel 0.200
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 0.000 :bias +0.000 :env 0.976 :lag 0.936 :left 9   :right 0  )
           (noise 1.9852 0.662 :bw  12
                  :lfo1 0.553   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.136 :dcy1 0.000 :dcy2 0.000 :rel 0.370
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 2.0000 0.667 :bw  12 :lag 0.000)
           (buzz 1.0037 0.335
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.127 :dcy1 0.000 :dcy2 0.000 :rel 0.270
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  25 :env  -5 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  1 :env 0.476
                             :prss 0.000 :cca 0.400 :ccb 0.000]
                      :res [0.806 :cca +0.000 :ccb +0.000]
                      :env [:att 0.204 :dcy 0.000 :rel 0.255 :sus 1.000]
                      :mode +0.072)
           (bp-filter :offset 6.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.6232 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-44    :lfo2 0.000 :lfo3 0.000 :xenv 0.734]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.432 :xfb -0.403)
           (delay2    :time [0.3116 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-28    :lfo2 0.000 :lfo3 0.000 :xenv 0.961]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.244 :xfb -0.002)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  18 Eighteen
;;
(save-program  18 "Eighteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.059 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    10.000 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.654)
           (lfo2    10.000 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.843 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  2.137 :dcy1 0.000 :dcy2 0.000 :rel 7.014
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.8456 0.300
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  6.005 :dcy1 0.000 :dcy2 0.000 :rel 5.263
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 0.4776 0.227 :bias +0.000 :env 0.883 :lag 0.896 :left 0   :right 0  )
           (op2 1.1327 0.488
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.155 :dcy1 0.000 :dcy2 0.000 :rel 3.483
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 1.0000 1.556 :bias +0.000 :env 0.398 :lag 0.000 :left 9   :right -3 )
           (op3 1.0000 0.553
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.985 :dcy1 0.000 :dcy2 0.000 :rel 3.902
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 0.000 :bias +0.000 :env 0.556 :lag 0.984 :left 9   :right 0  )
           (op4 0.6926 0.798
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  12.714 :dcy1 0.000 :dcy2 0.000 :rel 4.467
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 1.0000 0.152 :bias +0.000 :env 0.003 :lag 0.000 :left 6   :right 0  )
           (op5 0.5645 0.979
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.680 :dcy1 0.000 :dcy2 0.000 :rel 11.945
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 0.0395 0.000 :bias +0.000 :env 0.556 :lag 0.000 :left 9   :right 0  )
           (op6 0.5528 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.488 :dcy1 0.000 :dcy2 0.000 :rel 5.310
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 1.415 :bias +0.000 :env 0.585 :lag 0.244 :left 0   :right 0  )
           (noise 0.8818 0.627 :bw  18
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  11.202 :dcy1 0.000 :dcy2 0.000 :rel 3.484
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 0.6710 0.824 :bw  11 :lag 0.000)
           (buzz 1.1327 0.488
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  5.079 :dcy1 0.000 :dcy2 0.000 :rel 7.099
                       :peak 1.000 :bp   1.000 :sus  1.000])
           (buzz-harmonics  26 :env -24 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.718
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.097 :cca +0.000 :ccb +0.000]
                      :env [:att 5.105 :dcy 0.000 :rel 4.427 :sus 1.000]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [0.3954 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-31    :lfo2 0.000 :lfo3 0.000 :xenv 0.969]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.256 :xfb +0.000)
           (delay2    :time [0.0001 :lfo2 0.000 :lfo3 0.137 :xenv 0.000]
                      :amp  [-18    :lfo2 0.000 :lfo3 0.000 :xenv 0.651]
                      :pan  [+0.700 :lfo2 0.405 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.702 :xfb +0.190)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  19 Nineteen
;;
(save-program  19 "Nineteen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.821 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.782 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.103)
           (lfo2    1.466 :xenv  0.031 :cca  0.000 :ccb   0.000)
           (lfo3    0.978 :xenv  0.983 :cca  0.000 :ccb   0.000)
           (xenv :att  0.666 :dcy1 1.405 :dcy2 1.476 :rel 0.545
                 :peak 1.000 :bp   0.665 :sus  0.925)
           (penv :a0 -0.0958 :a1 +0.0384 :a2 -0.0433 :a3 -0.0313
                 :t1 1.484   :t2 1.194   :t3 1.067   :cc9 0.000)
           (op1 0.9970 0.997
                :lfo1  0.126 :cca 0.000 :ccb 0.000 :vel 0.618 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  +0
                :env [:att  1.317 :dcy1 0.664 :dcy2 1.420 :rel 1.629
                      :peak 1.000 :bp   0.558 :sus  0.639])
           (fm1 0.5000 0.000 :bias +0.000 :env 0.544 :lag 0.276 :left 0   :right 0  )
           (op2 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  +0
                :env [:att  1.294 :dcy1 1.001 :dcy2 2.551 :rel 1.519
                      :peak 0.829 :bp   0.841 :sus  0.535])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.560 :lag 0.454 :left 9   :right -9 )
           (op3 3.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.913 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  +0
                :env [:att  0.935 :dcy1 1.497 :dcy2 0.812 :rel 1.483
                      :peak 1.000 :bp   0.641 :sus  0.951])
           (fm3 2.0000 0.417 :bias +0.000 :env 0.693 :lag 0.000 :left 6   :right -3 )
           (op4 7.0000 0.143
                :lfo1  0.658 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  -6
                :env [:att  2.333 :dcy1 1.484 :dcy2 1.194 :rel 1.067
                      :peak 1.000 :bp   0.704 :sus  0.536])
           (fm4 0.5000 1.476 :bias +0.000 :env 0.819 :lag 0.142 :left 0   :right 0  )
           (op5 9.0000 0.111
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.973 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  -3
                :env [:att  1.557 :dcy1 0.323 :dcy2 0.000 :rel 1.296
                      :peak 1.000 :bp   0.927 :sus  0.927])
           (fm5 0.5000 1.296 :bias +0.000 :env 0.834 :lag 0.743 :left 3   :right 0  )
           (op6 9.0230 0.111
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.776 :prss 0.000
                :penv -0.0705  :key  60 :left   +0 :right  -6
                :env [:att  0.578 :dcy1 1.082 :dcy2 0.000 :rel 0.805
                      :peak 1.000 :bp   0.637 :sus  0.637])
           (fm6 0.5000 0.000 :bias +0.000 :env 0.946 :lag 0.423 :left 0   :right 0  )
           (noise 5.0000 0.000 :bw  13
                  :lfo1 0.099   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv -0.0705 :key  60   :left   +0 :right  -3
                  :env [:att  0.000 :dcy1 0.601 :dcy2 0.000 :rel 1.201
                        :peak 1.000 :bp   0.038 :sus  0.038])
           (noise2 3.0052 0.847 :bw  11 :lag 0.225)
           (buzz 0.9970 0.997
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.846 :prss 0.000
                 :penv -0.0705  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.162 :dcy2 3.239 :rel 1.708
                       :peak 1.000 :bp   0.732 :sus  0.100])
           (buzz-harmonics  23 :env -11 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  383 :track  0 :env 0.725
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.480 :cca +0.000 :ccb +0.000]
                      :env [:att 1.160 :dcy 0.785 :rel 0.443 :sus 0.430]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.5115 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-52    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.806 :xfb +0.000)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-29    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.626 :xfb +0.000)
           (amp   0   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

