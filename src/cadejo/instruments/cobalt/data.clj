(println "-->    cobalt data")

(ns cadejo.instruments.cobalt.data
  (:use [cadejo.instruments.cobalt.program]))

;; ------------------------------------------------------  1 Hydrogen
;;
(save-program  1 "Hydrogen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.603 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.056 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.011)
           (lfo2    0.440 :xenv  0.982 :cca  0.000 :ccb   0.000)
           (lfo3    0.165 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.986 :dcy1 2.416 :dcy2 0.000 :rel 2.807
                 :peak 1.000 :bp   0.848 :sus  0.848)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.5000 0.111
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.135 :dcy1 2.866 :dcy2 0.000 :rel 2.174
                      :peak 1.000 :bp   0.594 :sus  0.594])
           (fm1 2.0000 0.000 :bias +0.000 :env 0.964 :lag 0.000 :left 3   :right 0  )
           (op2 2.5000 0.556
                :lfo1  0.936 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.768 :dcy2 0.000 :rel 3.535
                      :peak 1.000 :bp   0.142 :sus  0.142])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.660 :lag 0.624 :left 0   :right 0  )
           (op3 2.5086 0.557
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.051 :dcy1 1.707 :dcy2 0.000 :rel 4.860
                      :peak 1.000 :bp   0.815 :sus  0.815])
           (fm3 0.5000 0.000 :bias +0.000 :env 0.403 :lag 0.152 :left 0   :right -6 )
           (op4 4.5000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  2.047 :dcy1 1.065 :dcy2 0.000 :rel 1.966
                      :peak 1.000 :bp   0.984 :sus  0.984])
           (fm4 0.5000 0.088 :bias +0.000 :env 0.960 :lag 0.008 :left 0   :right -6 )
           (op5 5.5000 0.818
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  2.790 :dcy1 6.319 :dcy2 2.759 :rel 3.102
                      :peak 0.959 :bp   0.618 :sus  0.530])
           (fm5 0.5000 0.550 :bias +0.000 :env 0.956 :lag 0.679 :left 3   :right 0  )
           (op6 6.5000 0.692
                :lfo1  0.499 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  1.097 :dcy1 1.607 :dcy2 0.354 :rel 1.672
                      :peak 0.848 :bp   0.967 :sus  0.630])
           (fm6 1.0000 1.891 :bias +0.000 :env 0.922 :lag 0.054 :left 3   :right 0  )
           (noise 3.5000 0.910 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.518 :dcy1 5.913 :dcy2 0.000 :rel 4.626
                        :peak 1.000 :bp   0.077 :sus  0.077])
           (noise2 1.5000 0.333 :bw  15 :lag 0.322)
           (buzz 1.5000 0.333
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  2.584 :dcy1 3.641 :dcy2 0.000 :rel 1.699
                       :peak 1.000 :bp   0.012 :sus  0.012])
           (buzz-harmonics  10 :env   5 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.849 :dcy 3.125 :rel 1.878 :sus 0.904]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.5144 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-59    :lfo2 0.000 :lfo3 0.000 :xenv 0.796]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.242 :xfb +0.000)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.512 :xenv 0.000]
                      :amp  [-49    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.655 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  2 Helium
;;
(save-program  2 "Helium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.549 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.569 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.914)
           (lfo2    1.706 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    1.137 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  7.742 :dcy1 6.429 :dcy2 0.000 :rel 10.726
                 :peak 1.000 :bp   0.290 :sus  0.290)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.636 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  11.907 :dcy1 11.737 :dcy2 0.000 :rel 4.809
                      :peak 1.000 :bp   0.551 :sus  0.551])
           (fm1 2.0000 0.088 :bias +0.000 :env 0.554 :lag 0.000 :left 0   :right 0  )
           (op2 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  12.659 :dcy1 8.845 :dcy2 0.000 :rel 5.600
                      :peak 1.000 :bp   0.718 :sus  0.718])
           (fm2 3.0000 0.099 :bias +0.000 :env 0.924 :lag 0.000 :left 0   :right 0  )
           (op3 4.9824 0.602
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  6.008 :dcy1 11.749 :dcy2 0.000 :rel 10.676
                      :peak 1.000 :bp   0.634 :sus  0.634])
           (fm3 0.1498 0.528 :bias +0.000 :env 0.538 :lag 0.000 :left -12   :right 0  )
           (op4 4.9996 0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  3.775 :dcy1 10.846 :dcy2 0.000 :rel 9.092
                      :peak 1.000 :bp   0.785 :sus  0.785])
           (fm4 3.0000 0.096 :bias +0.000 :env 0.552 :lag 0.000 :left 0   :right -3 )
           (op5 5.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.501 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  4.293 :dcy1 10.436 :dcy2 0.000 :rel 6.308
                      :peak 1.000 :bp   0.072 :sus  0.072])
           (fm5 2.0000 1.260 :bias +0.000 :env 0.959 :lag 0.000 :left 0   :right 0  )
           (op6 5.0020 0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.679 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  8.246 :dcy1 0.000 :dcy2 0.000 :rel 7.756
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.915 :lag 0.810 :left 0   :right -6 )
           (noise 7.0000 0.429 :bw  10
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  3.793 :dcy1 9.901 :dcy2 0.000 :rel 12.413
                        :peak 1.000 :bp   0.702 :sus  0.702])
           (noise2 9.0000 0.333 :bw  11 :lag 0.000)
           (buzz 3.0000 1.000
                 :lfo1  0.560 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left  -12 :right  +0
                 :env [:att  10.478 :dcy1 0.568 :dcy2 0.000 :rel 0.172
                       :peak 1.000 :bp   0.642 :sus  0.642])
           (buzz-harmonics  14 :env   7 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.668
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.002 :cca +0.000 :ccb +0.000]
                      :env [:att 5.066 :dcy 7.609 :rel 12.285 :sus 0.465]
                      :mode -0.273)
           (bp-filter :offset 2.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.4397 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-51    :lfo2 0.000 :lfo3 0.000 :xenv 0.721]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 1.000]
                      :fb    +0.831 :xfb -0.113)
           (delay2    :time [0.0006 :lfo2 0.000 :lfo3 0.973 :xenv 0.401]
                      :amp  [-39    :lfo2 0.000 :lfo3 0.000 :xenv 0.997]
                      :pan  [+0.700 :lfo2 0.005 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.557 :xfb -0.305)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------  3 Lithium
;;
(save-program  3 "Lithium" "ccb -> selected op amplitude"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.504 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.275 :bleed 0.000 :cca  0.410 :prss  0.000 :delay 1.040)
           (lfo2    1.376 :xenv  0.000 :cca  0.731 :ccb   0.000)
           (lfo3    0.115 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 5.728 :dcy2 14.962 :rel 8.780
                 :peak 1.000 :bp   0.876 :sus  0.001)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.662 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 4.092 :dcy2 8.377 :rel 11.843
                      :peak 1.000 :bp   0.980 :sus  0.124])
           (fm1 0.2500 0.641 :bias +0.000 :env 0.796 :lag 0.000 :left 0   :right 0  )
           (op2 2.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 2.448 :dcy2 0.000 :rel 4.897
                      :peak 1.000 :bp   0.072 :sus  0.072])
           (fm2 1.0000 3.000 :bias +0.000 :env 0.975 :lag 0.797 :left 0   :right 0  )
           (op3 5.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 6.005 :dcy2 7.097 :rel 17.853
                      :peak 1.000 :bp   0.825 :sus  0.100])
           (fm3 0.5000 0.036 :bias +0.000 :env 0.659 :lag 0.240 :left 6   :right 0  )
           (op4 8.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.695 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.023 :dcy1 2.765 :dcy2 16.919 :rel 19.109
                      :peak 1.000 :bp   0.839 :sus  0.100])
           (fm4 3.0000 2.905 :bias +0.000 :env 0.049 :lag 0.157 :left 0   :right 0  )
           (op5 10.0000 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.423 :vel 0.727 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 1.534 :dcy2 7.731 :rel 3.390
                      :peak 1.000 :bp   0.960 :sus  0.100])
           (fm5 3.0000 6.684 :bias +0.000 :env 0.805 :lag 0.664 :left 0   :right -3 )
           (op6 11.0000 0.091
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.761 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 0.181 :dcy2 9.724 :rel 7.332
                      :peak 1.000 :bp   0.739 :sus  0.100])
           (fm6 0.2500 0.560 :bias +0.000 :env 0.579 :lag 0.000 :left 9   :right 0  )
           (noise 7.0000 0.780 :bw  57
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  0.061 :dcy1 5.008 :dcy2 5.911 :rel 10.657
                        :peak 1.000 :bp   0.875 :sus  0.100])
           (noise2 4.0000 0.250 :bw  73 :lag 0.000)
           (buzz 2.0000 0.500
                 :lfo1  0.024 :cca 0.000 :ccb 0.665 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.022 :dcy1 9.880 :dcy2 6.620 :rel 8.100
                       :peak 1.000 :bp   0.889 :sus  0.087])
           (buzz-harmonics   5 :env  17 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.656
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.562 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 5.234 :rel 11.462 :sus 0.100]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.4534 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-46    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.798 :xfb +0.000)
           (delay2    :time [0.0004 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-54    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.873 :xfb +0.064)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  4 Beryllium
;;
(save-program  4 "Beryllium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.261 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    3.756 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.138)
           (lfo2    1.670 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.157 :xenv  0.305 :cca  0.000 :ccb   0.061)
           (xenv :att  0.000 :dcy1 5.937 :dcy2 0.000 :rel 11.874
                 :peak 1.000 :bp   0.228 :sus  0.228)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.542
                :lfo1  0.876 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.582 :dcy1 1.081 :dcy2 0.000 :rel 4.503
                      :peak 1.000 :bp   0.845 :sus  0.845])
           (fm1 2.0000 1.356 :bias +0.000 :env 0.841 :lag 0.000 :left 9   :right -3 )
           (op2 3.0000 0.208
                :lfo1  0.000 :cca 0.217 :ccb 0.000 :vel 0.767 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  5.709 :dcy1 0.000 :dcy2 0.000 :rel 6.444
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 2.0000 0.658 :bias +0.000 :env 0.648 :lag 0.000 :left 0   :right -9 )
           (op3 5.0386 0.781
                :lfo1  0.000 :cca 0.000 :ccb 0.344 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  2.846 :dcy1 0.448 :dcy2 5.238 :rel 5.040
                      :peak 1.000 :bp   0.359 :sus  0.695])
           (fm3 1.0000 1.520 :bias +0.000 :env 0.966 :lag 0.092 :left 9   :right 0  )
           (op4 6.9717 0.272
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 3.807 :dcy2 0.000 :rel 7.614
                      :peak 1.000 :bp   0.096 :sus  0.096])
           (fm4 0.5000 2.009 :bias +0.000 :env 0.876 :lag 0.000 :left 9   :right 0  )
           (op5 9.0000 0.142
                :lfo1  0.685 :cca 0.000 :ccb 0.031 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  5.086 :dcy1 4.280 :dcy2 1.923 :rel 1.911
                      :peak 1.000 :bp   0.751 :sus  0.184])
           (fm5 0.5000 1.387 :bias +0.000 :env 0.986 :lag 0.322 :left 6   :right 0  )
           (op6 10.9126 0.807
                :lfo1  0.000 :cca 0.333 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  5.696 :dcy1 3.336 :dcy2 5.642 :rel 2.450
                      :peak 0.919 :bp   0.774 :sus  0.508])
           (fm6 1.0000 3.271 :bias +0.000 :env 0.825 :lag 0.000 :left 3   :right 0  )
           (noise 7.0000 0.702 :bw  84
                  :lfo1 0.269   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -3
                  :env [:att  5.198 :dcy1 3.474 :dcy2 4.062 :rel 3.106
                        :peak 1.000 :bp   0.656 :sus  0.330])
           (noise2 5.0000 0.717 :bw  62 :lag 1.003)
           (buzz 3.0000 0.495
                 :lfo1  0.000 :cca 0.000 :ccb 0.223 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  4.886 :dcy1 5.348 :dcy2 2.968 :rel 5.875
                       :peak 1.000 :bp   0.225 :sus  0.757])
           (buzz-harmonics  45 :env -41 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.887
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.571 :cca +0.000 :ccb +0.000]
                      :env [:att 4.293 :dcy 4.578 :rel 3.800 :sus 0.059]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.5973 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-14    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.018 :xfb +0.000)
           (delay2    :time [0.0003 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-14    :lfo2 0.000 :lfo3 0.000 :xenv 0.620]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.619 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  5 Boron
;;
(save-program  5 "Boron" "cca -> buzz amp"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.456 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.746 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.726)
           (lfo2    2.485 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.466 :xenv  0.789 :cca  0.000 :ccb   0.000)
           (xenv :att  0.461 :dcy1 0.790 :dcy2 0.424 :rel 1.540
                 :peak 1.000 :bp   0.590 :sus  0.927)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.9964 0.996
                :lfo1  0.644 :cca 0.000 :ccb 0.000 :vel 0.931 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.409 :dcy1 0.405 :dcy2 1.095 :rel 0.528
                      :peak 1.000 :bp   0.946 :sus  0.882])
           (fm1 0.5000 1.537 :bias +0.000 :env 0.948 :lag 0.000 :left 3   :right 0  )
           (op2 1.0000 1.000
                :lfo1  0.000 :cca 0.060 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.400 :dcy1 0.320 :dcy2 0.837 :rel 0.418
                      :peak 1.000 :bp   0.300 :sus  0.799])
           (fm2 1.0000 3.995 :bias +0.000 :env 0.824 :lag 0.943 :left 0   :right -6 )
           (op3 3.0000 0.333
                :lfo1  0.000 :cca 0.00 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.717 :dcy1 0.787 :dcy2 0.315 :rel 0.459
                      :peak 1.000 :bp   0.638 :sus  0.288])
           (fm3 2.0000 3.286 :bias +0.000 :env 0.645 :lag 0.000 :left 0   :right 0  )
           (op4 5.0000 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.611 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  1.062 :dcy1 0.267 :dcy2 0.751 :rel 0.479
                      :peak 1.000 :bp   0.319 :sus  0.347])
           (fm4 0.2500 2.276 :bias +0.000 :env 0.737 :lag 0.000 :left 3   :right -9 )
           (op5 6.9580 0.144
                :lfo1  0.000 :cca 0.000 :ccb 0.655 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.571 :dcy1 0.505 :dcy2 0.000 :rel 6.047
                      :peak 1.000 :bp   0.533 :sus  0.533])
           (fm5 2.0000 3.050 :bias +0.000 :env 0.735 :lag 0.912 :left 0   :right -6 )
           (op6 6.9708 0.143
                :lfo1  0.681 :cca 0.000 :ccb 0.204 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 0.780 :dcy2 0.000 :rel 1.559
                      :peak 1.000 :bp   0.030 :sus  0.030])
           (fm6 0.5000 5.641 :bias +0.000 :env 0.966 :lag 0.000 :left 0   :right 0  )
           (noise 1.0022 0.998 :bw  30
                  :lfo1 0.309   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.568 :dcy1 0.929 :dcy2 0.503 :rel 0.490
                        :peak 1.000 :bp   0.805 :sus  0.711])
           (noise2 6.9786 0.143 :bw  69 :lag 0.853)
           (buzz 1.0000 1.000
                 :lfo1  0.000 :cca 1.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.652 :dcy2 0.000 :rel 1.304
                       :peak 1.000 :bp   0.027 :sus  0.027])
           (buzz-harmonics  60 :env -37 :cca 0.00 :hp   1 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.412 :dcy 1.188 :rel 3.478 :sus 0.894]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.0730 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-39    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.653 :xfb -0.189)
           (delay2    :time [1.6095 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-13    :lfo2 0.000 :lfo3 0.000 :xenv 0.992]
                      :pan  [+0.700 :lfo2 0.812 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.551 :xfb -0.006)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------  6 Carbon
;;
(save-program  6 "carbon"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.370 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.790 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.779)
           (lfo2    1.342 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.112 :xenv  0.000 :cca  0.000 :ccb   0.786)
           (xenv :att  0.000 :dcy1 0.536 :dcy2 0.000 :rel 1.072
                 :peak 1.000 :bp   0.071 :sus  0.071)
           (penv :a0 +0.0168 :a1 -0.0598 :a2 +0.0311 :a3 +0.0221
                 :t1 0.266   :t2 0.900   :t3 1.018   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  +0
                :env [:att  0.034 :dcy1 0.647 :dcy2 0.000 :rel 1.294
                      :peak 1.000 :bp   0.142 :sus  0.142])
           (fm1 0.5000 0.000 :bias +0.000 :env 0.748 :lag 0.000 :left 0   :right 0  )
           (op2 2.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.698 :dcy2 0.000 :rel 1.396
                      :peak 1.000 :bp   0.051 :sus  0.051])
           (fm2 1.0000 0.000 :bias +0.000 :env 0.621 :lag 0.000 :left 0   :right -3 )
           (op3 4.9909 0.401
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  -3
                :env [:att  0.046 :dcy1 0.502 :dcy2 0.000 :rel 1.004
                      :peak 1.000 :bp   0.033 :sus  0.033])
           (fm3 0.1079 0.000 :bias +0.000 :env 0.523 :lag 0.000 :left 3   :right 0  )
           (op4 4.9914 0.401
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.700 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  -6
                :env [:att  0.042 :dcy1 1.289 :dcy2 0.000 :rel 2.578
                      :peak 1.000 :bp   0.100 :sus  0.100])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.620 :lag 0.000 :left 9   :right 0  )
           (op5 5.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  -3
                :env [:att  0.000 :dcy1 0.145 :dcy2 1.931 :rel 1.747
                      :peak 1.000 :bp   0.677 :sus  0.100])
           (fm5 2.0000 0.000 :bias +0.000 :env 0.621 :lag 0.415 :left 0   :right -3 )
           (op6 7.0000 0.286
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0909  :key  60 :left   +0 :right  -6
                :env [:att  0.016 :dcy1 1.952 :dcy2 0.000 :rel 3.905
                      :peak 1.000 :bp   0.089 :sus  0.089])
           (fm6 3.0000 0.324 :bias +0.000 :env 0.001 :lag 0.000 :left 6   :right 0  )
           (noise 6.0000 0.333 :bw  12
                  :lfo1 0.992   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0909 :key  60   :left   +0 :right  -6
                  :env [:att  0.000 :dcy1 1.076 :dcy2 0.000 :rel 2.152
                        :peak 1.000 :bp   0.164 :sus  0.164])
           (noise2 3.0000 0.667 :bw  11 :lag 1.954)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.613 :prss 0.000
                 :penv +0.0909  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.030 :dcy2 2.466 :rel 3.031
                       :peak 1.000 :bp   0.598 :sus  0.100])
           (buzz-harmonics   1 :env  16 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.933
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.324 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.653 :rel 1.306 :sus 0.156]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.3724 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-21    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.231 :xfb -0.343)
           (delay2    :time [0.0007 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-45    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.568 :xfb +0.163)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------  7 Nitrogen
;;
(save-program  7 "Nitrogen"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.828 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.971 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.576)
           (lfo2    0.486 :xenv  0.195 :cca  0.975 :ccb   0.000)
           (lfo3    0.324 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.171 :dcy1 0.576 :dcy2 0.000 :rel 0.676
                 :peak 1.000 :bp   0.507 :sus  0.507)
           (penv :a0 +0.0673 :a1 -0.0889 :a2 -0.0763 :a3 -0.0358
                 :t1 0.594   :t2 0.329   :t3 0.518   :cc9 0.000)
           (op1 1.0000 0.824
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  +0
                :env [:att  0.487 :dcy1 0.667 :dcy2 0.000 :rel 0.790
                      :peak 1.000 :bp   0.673 :sus  0.673])
           (fm1 1.0000 1.084 :bias +0.000 :env 0.717 :lag 0.000 :left 6   :right 0  )
           (op2 2.0000 0.236
                :lfo1  0.191 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  +0
                :env [:att  0.332 :dcy1 0.584 :dcy2 0.000 :rel 0.573
                      :peak 1.000 :bp   0.990 :sus  0.990])
           (fm2 0.5000 0.486 :bias +0.000 :env 0.508 :lag 0.000 :left 6   :right 0  )
           (op3 3.0000 0.529
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  +0
                :env [:att  0.753 :dcy1 0.638 :dcy2 0.000 :rel 0.763
                      :peak 1.000 :bp   0.761 :sus  0.761])
           (fm3 0.5000 0.510 :bias +0.000 :env 0.863 :lag 0.746 :left 0   :right -9 )
           (op4 4.9755 0.174
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  -3
                :env [:att  1.259 :dcy1 0.594 :dcy2 0.000 :rel 1.329
                      :peak 1.000 :bp   0.880 :sus  0.880])
           (fm4 0.5000 0.496 :bias +0.000 :env 0.658 :lag 0.000 :left 9   :right 0  )
           (op5 5.0000 0.917
                :lfo1  0.555 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  -3
                :env [:att  0.692 :dcy1 0.593 :dcy2 0.000 :rel 1.297
                      :peak 1.000 :bp   0.658 :sus  0.658])
           (fm5 3.0000 0.931 :bias +0.000 :env 0.526 :lag 0.937 :left 0   :right 0  )
           (op6 6.0000 0.141
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0696  :key  60 :left   +0 :right  -6
                :env [:att  0.612 :dcy1 11.819 :dcy2 0.000 :rel 1.361
                      :peak 1.000 :bp   0.914 :sus  0.914])
           (fm6 1.0000 1.466 :bias +0.000 :env 0.887 :lag 0.000 :left 0   :right 0  )
           (noise 4.0000 0.407 :bw  10
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0696 :key  60   :left   +0 :right  +0
                  :env [:att  0.754 :dcy1 1.485 :dcy2 0.000 :rel 2.203
                        :peak 1.000 :bp   0.617 :sus  0.617])
           (noise2 7.0000 0.792 :bw  10 :lag 1.767)
           (buzz 2.0000 0.641
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0696  :key  60 :left   +0 :right  +0
                 :env [:att  0.796 :dcy1 0.701 :dcy2 0.000 :rel 1.663
                       :peak 1.000 :bp   0.603 :sus  0.603])
           (buzz-harmonics   4 :env  26 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  398 :track  1 :env 0.421
                             :prss 0.000 :cca 0.192 :ccb 0.000]
                      :res [0.426 :cca +0.000 :ccb +0.000]
                      :env [:att 0.366 :dcy 0.606 :rel 1.404 :sus 0.894]
                      :mode -0.004)
           (bp-filter :offset 3.000)
           (fold      :wet 0.898 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [1.0295 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-12    :lfo2 0.000 :lfo3 0.000 :xenv 0.502]
                      :pan  [-0.700 :lfo2 0.443 :lfo3 0.630 :xenv 0.000]
                      :fb    +0.890 :xfb -0.104)
           (delay2    :time [0.0010 :lfo2 0.000 :lfo3 0.545 :xenv 0.000]
                      :amp  [-12    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.010 :xenv -1.00]
                      :fb    +0.820 :xfb -0.039)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  8 Oxygen
;;
(save-program  8 "Oxygen" "cca -> Filter freq and res"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.558 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.926 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.435)
           (lfo2    1.389 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.463 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  3.967 :dcy1 5.357 :dcy2 3.836 :rel 0.449
                 :peak 1.000 :bp   0.291 :sus  0.306)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.250
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.706 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.302 :dcy1 4.177 :dcy2 0.290 :rel 3.708
                      :peak 1.000 :bp   0.620 :sus  0.742])
           (fm1 2.0000 0.125 :bias +0.000 :env 0.756 :lag 0.456 :left 9   :right -6 )
           (op2 2.0000 1.000
                :lfo1  0.640 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  5.480 :dcy1 4.066 :dcy2 3.996 :rel 3.337
                      :peak 1.000 :bp   0.821 :sus  0.612])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.516 :lag 0.122 :left 9   :right 0  )
           (op3 3.0000 0.444
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.866 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.818 :dcy1 8.313 :dcy2 5.768 :rel 4.204
                      :peak 1.000 :bp   0.513 :sus  0.962])
           (fm3 0.5000 0.617 :bias +0.000 :env 0.971 :lag 0.833 :left 3   :right 0  )
           (op4 5.0000 0.160
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.904 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  4.504 :dcy1 0.577 :dcy2 0.757 :rel 3.357
                      :peak 1.000 :bp   0.574 :sus  0.148])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.948 :lag 0.000 :left 0   :right 0  )
           (op5 7.0000 0.082
                :lfo1  0.494 :cca 0.000 :ccb 0.000 :vel 0.988 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  4.826 :dcy1 5.275 :dcy2 12.147 :rel 2.397
                      :peak 1.000 :bp   0.088 :sus  0.572])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.755 :lag 0.000 :left 0   :right 0  )
           (op6 8.9613 0.050
                :lfo1  0.705 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  3.304 :dcy1 0.829 :dcy2 5.072 :rel 12.079
                      :peak 1.000 :bp   0.905 :sus  0.093])
           (fm6 2.0000 1.723 :bias +0.000 :env 0.821 :lag 0.670 :left 0   :right 0  )
           (noise 6.0000 0.821 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  3.588 :dcy1 11.409 :dcy2 4.873 :rel 0.851
                        :peak 1.000 :bp   0.783 :sus  0.796])
           (noise2 9.0000 0.049 :bw  14 :lag 0.000)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  6.202 :dcy1 5.813 :dcy2 10.059 :rel 3.993
                       :peak 1.000 :bp   0.622 :sus  0.172])
           (buzz-harmonics  24 :env -22 :cca   0 :hp   2 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.973
                             :prss 0.000 :cca 0.300 :ccb 0.000]
                      :res [0.485 :cca +0.300 :ccb +0.000]
                      :env [:att 0.145 :dcy 0.492 :rel 5.108 :sus 0.100]
                      :mode -0.116)
           (bp-filter :offset 3.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.4394 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-24    :lfo2 0.000 :lfo3 0.200 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.492 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.397 :xfb +0.100)
           (delay2    :time [0.7197 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-24    :lfo2 0.000 :lfo3 0.000 :xenv 0.960]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.200 :xenv 0.000]
                      :fb    +0.326 :xfb +0.100)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))



;; ------------------------------------------------------  11 "Sodium"
;;
(save-program  11 "Sodium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.052 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    3.039 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.184)
           (lfo2    1.520 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    2.026 :xenv  0.000 :cca  0.000 :ccb   0.682)
           (xenv :att  0.000 :dcy1 1.069 :dcy2 0.000 :rel 2.137
                 :peak 1.000 :bp   0.220 :sus  0.220)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.250 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.120 :dcy2 0.000 :rel 2.240
                      :peak 1.000 :bp   0.152 :sus  0.152])
           (fm1 3.0000 0.000 :bias +0.000 :env 0.641 :lag 0.000 :left 0   :right -6 )
           (op2 2.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.012 :dcy1 1.903 :dcy2 0.000 :rel 3.807
                      :peak 1.000 :bp   0.189 :sus  0.189])
           (fm2 0.2208 0.000 :bias +0.000 :env 0.814 :lag 0.000 :left 3   :right -3 )
           (op3 4.0000 0.500
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.500 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.091 :dcy2 1.905 :rel 3.034
                      :peak 1.000 :bp   0.647 :sus  0.100])
           (fm3 2.0000 0.115 :bias +0.000 :env 0.964 :lag 0.000 :left 0   :right 0  )
           (op4 5.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.746 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 0.792 :dcy2 0.000 :rel 1.585
                      :peak 1.000 :bp   0.176 :sus  0.176])
           (fm4 3.0000 0.000 :bias +0.000 :env 0.153 :lag 0.470 :left 3   :right 0  )
           (op5 5.0025 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.019 :dcy1 2.475 :dcy2 0.000 :rel 4.950
                      :peak 1.000 :bp   0.014 :sus  0.014])
           (fm5 0.0508 0.000 :bias +0.000 :env 0.540 :lag 0.000 :left 0   :right 0  )
           (op6 8.0000 0.250
                :lfo1  0.480 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.060 :dcy1 1.485 :dcy2 0.000 :rel 2.970
                      :peak 1.000 :bp   0.013 :sus  0.013])
           (fm6 0.6220 0.424 :bias +0.000 :env 0.514 :lag 0.361 :left 9   :right 0  )
           (noise 7.0000 0.100 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -3
                  :env [:att  0.000 :dcy1 0.879 :dcy2 0.000 :rel 1.758
                        :peak 1.000 :bp   0.105 :sus  0.105])
           (noise2 4.0210 0.100 :bw  10 :lag 0.409)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.344 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 1.240 :dcy2 0.000 :rel 2.481
                       :peak 1.000 :bp   0.068 :sus  0.068])
           (buzz-harmonics  17 :env   9 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  2 :env 0.659
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.076 :cca +0.000 :ccb +0.000]
                      :env [:att 0.064 :dcy 0.882 :rel 1.764 :sus 0.165]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  4 :cca  +0 :ccb  +0)
           (delay1    :time [0.4936 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-13    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.338 :xfb -0.394)
           (delay2    :time [0.0004 :lfo2 0.000 :lfo3 0.192 :xenv 0.000]
                      :amp  [-27    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.686 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  12  Magnesium
;;
(save-program  12 "Magnesium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 7.635 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.727 :bleed 0.000 :cca  0.432 :prss  0.000 :delay 1.830)
           (lfo2    10.000 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    1.909 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.420 :dcy2 0.000 :rel 0.840
                 :peak 1.000 :bp   0.006 :sus  0.006)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.200
                :lfo1  0.589 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.697 :dcy2 0.000 :rel 1.395
                      :peak 1.000 :bp   0.229 :sus  0.229])
           (fm1 3.0000 0.000 :bias +0.000 :env 0.886 :lag 0.000 :left 0   :right 0  )
           (op2 2.0000 0.400
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.979 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.743 :dcy1 0.479 :dcy2 0.584 :rel 0.404
                      :peak 1.000 :bp   0.356 :sus  0.175])
           (fm2 2.0000 0.358 :bias +0.000 :env 0.683 :lag 0.526 :left 0   :right 0  )
           (op3 3.0000 0.600
                :lfo1  0.762 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.398 :dcy2 0.000 :rel 0.795
                      :peak 1.000 :bp   0.032 :sus  0.032])
           (fm3 1.0000 1.304 :bias +0.000 :env 0.916 :lag 0.885 :left 9   :right 0  )
           (op4 4.9999 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  0
                :env [:att  0.000 :dcy1 0.518 :dcy2 0.000 :rel 1.037
                      :peak 1.000 :bp   0.013 :sus  0.013])
           (fm4 1.0000 0.200 :bias +0.000 :env 0.914 :lag 0.000 :left 0   :right -6 )
           (op5 5.0000 0.000
                :lfo1  0.801 :cca 0.000 :ccb 0.000 :vel 0.506 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  0
                :env [:att  0.000 :dcy1 0.417 :dcy2 0.000 :rel 0.834
                      :peak 1.000 :bp   0.017 :sus  0.017])
           (fm5 1.0000 1.386 :bias +0.000 :env 0.782 :lag 0.161 :left 0   :right 0  )
           (op6 5.0244 0.995
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  0
                :env [:att  0.000 :dcy1 1.459 :dcy2 0.000 :rel 2.917
                      :peak 1.000 :bp   0.021 :sus  0.021])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.834 :lag 0.346 :left 0   :right -3 )
           (noise 5.0241 0.995 :bw  16
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  0
                  :env [:att  0.000 :dcy1 0.116 :dcy2 1.378 :rel 1.923
                        :peak 1.000 :bp   0.629 :sus  0.100])
           (noise2 4.9600 0.992 :bw  13 :lag 0.000)
           (buzz 2.0000 0.400
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.524 :dcy2 0.000 :rel 1.048
                       :peak 1.000 :bp   0.058 :sus  0.058])
           (buzz-harmonics  22 :env  -9 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  1 :env 0.445
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.712 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.451 :rel 0.902 :sus 0.005]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.5239 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-53    :lfo2 0.000 :lfo3 0.000 :xenv 0.732]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.570 :xfb +0.000)
           (delay2    :time [0.0011 :lfo2 0.000 :lfo3 0.061 :xenv 0.998]
                      :amp  [-13    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.650 :xfb +0.241)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  13 Aluminium
;;
(save-program  13 "Aluminium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.697 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.424 :bleed 0.000 :cca  0.302 :prss  0.000 :delay 0.205)
           (lfo2    1.424 :xenv  0.000 :cca  0.007 :ccb   0.000)
           (lfo3    2.848 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  2.360 :dcy1 0.000 :dcy2 0.000 :rel 0.742
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.500
                :lfo1  0.132 :cca 0.000 :ccb 0.000 :vel 0.481 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.528 :dcy1 0.000 :dcy2 0.000 :rel 0.492
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm1 0.2500 0.000 :bias +0.000 :env 0.857 :lag 0.000 :left -15 :right 0  )
           (op2 3.0000 0.667
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.127 :dcy2 1.000 :rel 1.999
                      :peak 1.000 :bp   0.625 :sus  0.100])
           (fm2 0.2500 0.000 :bias +0.000 :env 0.682 :lag 0.000 :left -15 :right 0  )
           (op3 5.0000 0.780
                :lfo1  0.150 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.423 :dcy1 0.000 :dcy2 0.000 :rel 1.020
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 1.0000 1.609 :bias +0.000 :env 0.735 :lag 0.000 :left -9  :right 0  )
           (op4 7.0000 0.286
                :lfo1  0.518 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.317 :dcy1 0.000 :dcy2 0.000 :rel 0.245
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm4 2.0000 1.726 :bias +0.000 :env 0.685 :lag 0.000 :left -9  :right 0  )
           (op5 9.0000 0.222
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.785 :dcy1 0.000 :dcy2 0.000 :rel 0.521
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm5 1.0000 0.000 :bias +0.000 :env 0.675 :lag 0.329 :left 0   :right -3 )
           (op6 10.0000 0.200
                :lfo1  0.715 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.624 :dcy1 0.000 :dcy2 0.000 :rel 0.739
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 1.0000 0.332 :bias +0.000 :env 0.501 :lag 0.908 :left 0   :right 0  )
           (noise 2.0000 1.000 :bw  11
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left  -12 :right  +0
                  :env [:att  0.561 :dcy1 0.000 :dcy2 0.000 :rel 0.645
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 6.0000 0.333 :bw  10 :lag 0.000)
           (buzz 2.0000 1.000
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 1.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.160 :dcy2 1.543 :rel 2.576
                       :peak 1.000 :bp   0.520 :sus  0.077])
           (buzz-harmonics  28 :env -17 :cca   0 :hp   1 :hp<-env   9)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.681 :dcy 0.000 :rel 0.312 :sus 1.000]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.7022 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-56    :lfo2 0.000 :lfo3 0.000 :xenv 0.539]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.123 :xfb +0.000)
           (delay2    :time [0.0005 :lfo2 0.000 :lfo3 0.809 :xenv 0.000]
                      :amp  [-31    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.596 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.672 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  14 Silicon
;;
(save-program  14 "Silicon"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.490 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.372 :bleed 0.215 :cca  0.000 :prss  0.000 :delay 0.185)
           (lfo2    1.372 :xenv  0.000 :cca  0.926 :ccb   0.000)
           (lfo3    5.490 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.371 :dcy1 0.209 :dcy2 0.508 :rel 0.347
                 :peak 1.000 :bp   0.621 :sus  0.845)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.9943 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.060 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.387 :dcy1 0.391 :dcy2 0.210 :rel 0.256
                      :peak 1.000 :bp   0.845 :sus  0.375])
           (fm1 2.0000 1.221 :bias +0.000 :env 0.750 :lag 0.000 :left 6   :right -6 )
           (op2 1.0000 0.994
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.060 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.415 :dcy1 0.255 :dcy2 0.588 :rel 0.429
                      :peak 1.000 :bp   0.849 :sus  0.439])
           (fm2 2.0000 0.000 :bias +0.000 :env 0.836 :lag 0.000 :left 9   :right -6 )
           (op3 1.9809 0.502
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.614 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.012 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.742 :sus  0.100])
           (fm3 3.0000 0.000 :bias +0.000 :env 0.631 :lag 0.017 :left 0   :right 0  )
           (op4 2.0000 0.497
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.223 :dcy1 0.704 :dcy2 0.180 :rel 0.221
                      :peak 1.000 :bp   0.971 :sus  0.394])
           (fm4 2.0000 0.116 :bias +0.000 :env 0.666 :lag 0.222 :left 0   :right 0  )
           (op5 2.0063 0.496
                :lfo1  0.813 :cca 0.000 :ccb 0.000 :vel 0.413 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.562 :dcy1 0.269 :dcy2 1.310 :rel 0.352
                      :peak 1.000 :bp   0.417 :sus  0.822])
           (fm5 0.9419 0.170 :bias +0.000 :env 0.787 :lag 0.000 :left 3   :right 0  )
           (op6 5.0000 0.199
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.196 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.261 :dcy1 0.282 :dcy2 0.123 :rel 0.211
                      :peak 1.000 :bp   0.608 :sus  0.076])
           (fm6 3.0000 0.000 :bias +0.000 :env 0.852 :lag 0.405 :left 6   :right 0  )
           (noise 4.0000 0.249 :bw  15
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.235 :dcy1 0.391 :dcy2 0.324 :rel 0.394
                        :peak 1.000 :bp   0.880 :sus  0.455])
           (noise2 5.0026 0.199 :bw  18 :lag 0.000)
           (buzz 1.0000 0.994
                 :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.183 :dcy1 0.428 :dcy2 0.255 :rel 0.275
                       :peak 1.000 :bp   0.749 :sus  0.293])
           (buzz-harmonics   6 :env   7 :cca   0 :hp   1 :hp<-env   4)
           (lp-filter :freq [   50 :track  2 :env 0.430
                             :prss 0.000 :cca 0.459 :ccb 0.000]
                      :res [0.036 :cca +0.000 :ccb +0.000]
                      :env [:att 0.245 :dcy 0.393 :rel 0.127 :sus 0.996]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.7286 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-45    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.253 :xfb +0.000)
           (delay2    :time [0.5465 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-54    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.681 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  15 Phosphorus
;;
(save-program  15 "Phosphorus"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.973 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.746 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.627)
           (lfo2    3.730 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.026 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.000 :dcy1 0.490 :dcy2 0.000 :rel 0.981
                 :peak 1.000 :bp   0.034 :sus  0.034)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.250
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.972 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.048 :dcy1 4.563 :dcy2 0.000 :rel 9.125
                      :peak 1.000 :bp   0.168 :sus  0.168])
           (fm1 1.0000 1.538 :bias +0.000 :env 0.899 :lag 0.044 :left 0   :right 0  )
           (op2 2.0000 1.000
                :lfo1  0.815 :cca 0.000 :ccb 0.000 :vel 0.746 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.180 :dcy2 7.301 :rel 18.240
                      :peak 1.000 :bp   0.724 :sus  0.100])
           (fm2 0.5000 0.000 :bias +0.000 :env 0.208 :lag 0.000 :left 0   :right -6 )
           (op3 4.0000 0.250
                :lfo1  0.078 :cca 0.000 :ccb 0.000 :vel 0.549 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 9.564 :dcy2 0.000 :rel 19.128
                      :peak 1.000 :bp   0.122 :sus  0.122])
           (fm3 3.0000 0.000 :bias +0.000 :env 0.855 :lag 0.630 :left 6   :right 0  )
           (op4 7.0000 0.082
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.061 :dcy1 5.895 :dcy2 0.000 :rel 11.790
                      :peak 1.000 :bp   0.005 :sus  0.005])
           (fm4 2.0000 0.000 :bias +0.000 :env 0.850 :lag 0.625 :left 3   :right 0  )
           (op5 7.0238 0.081
                :lfo1  0.776 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 10.012 :dcy2 0.000 :rel 20.025
                      :peak 1.000 :bp   0.005 :sus  0.005])
           (fm5 0.5000 0.000 :bias +0.000 :env 0.683 :lag 0.830 :left 0   :right 0  )
           (op6 8.0000 0.063
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.327 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 8.138 :dcy2 0.000 :rel 16.275
                      :peak 1.000 :bp   0.078 :sus  0.078])
           (fm6 2.0000 0.000 :bias +0.000 :env 0.688 :lag 0.928 :left 0   :right 0  )
           (noise 5.0000 0.160 :bw  14
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  0.028 :dcy1 9.036 :dcy2 0.000 :rel 18.072
                        :peak 1.000 :bp   0.218 :sus  0.218])
           (noise2 9.9778 0.040 :bw  10 :lag 1.492)
           (buzz 2.0000 1.000
                 :lfo1  0.786 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 4.584 :dcy2 0.000 :rel 9.168
                       :peak 1.000 :bp   0.020 :sus  0.020])
           (buzz-harmonics  30 :env  -1 :cca   0 :hp   3 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 10.047 :rel 20.094 :sus 0.231]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  8 :cca  +0 :ccb  +0)
           (delay1    :time [1.6086 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-17    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.018 :lfo3 0.000 :xenv 0.650]
                      :fb    +0.045 :xfb -0.032)
           (delay2    :time [0.0007 :lfo2 0.000 :lfo3 0.730 :xenv 0.000]
                      :amp  [-42    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.845 :xfb +0.000)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  16 Sulfur
;;
(save-program  16 "Sulfer"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.450 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.417 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.002)
           (lfo2    3.337 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.139 :xenv  0.713 :cca  0.000 :ccb   0.538)
           (xenv :att  2.730 :dcy1 1.786 :dcy2 3.104 :rel 2.576
                 :peak 1.000 :bp   0.980 :sus  0.871)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0003 0.333
                :lfo1  0.338 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  2.709 :dcy1 1.193 :dcy2 0.125 :rel 2.046
                      :peak 1.000 :bp   0.589 :sus  0.388])
           (fm1 0.5000 1.824 :bias +0.000 :env 0.590 :lag 0.000 :left 0   :right 0  )
           (op2 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.359 :dcy1 0.000 :dcy2 0.000 :rel 1.958
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 1.0000 1.215 :bias +0.000 :env 0.658 :lag 0.234 :left 0   :right 0  )
           (op3 5.0000 0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.000 :dcy1 0.143 :dcy2 3.571 :rel 6.840
                      :peak 1.000 :bp   0.603 :sus  0.100])
           (fm3 2.0000 0.487 :bias +0.000 :env 0.220 :lag 0.003 :left 0   :right 0  )
           (op4 7.0000 0.429
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  1.695 :dcy1 0.994 :dcy2 2.696 :rel 1.707
                      :peak 1.000 :bp   0.713 :sus  0.946])
           (fm4 0.5636 0.942 :bias +0.000 :env 0.850 :lag 0.000 :left 6   :right 0  )
           (op5 8.9483 0.335
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  3.168 :dcy1 1.692 :dcy2 2.890 :rel 2.657
                      :peak 1.000 :bp   0.579 :sus  0.116])
           (fm5 0.1401 1.613 :bias +0.000 :env 0.843 :lag 0.000 :left 0   :right 0  )
           (op6 9.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.941 :dcy1 2.883 :dcy2 0.459 :rel 2.483
                      :peak 1.000 :bp   0.648 :sus  0.713])
           (fm6 0.5000 0.222 :bias +0.000 :env 0.673 :lag 0.566 :left 0   :right -6 )
           (noise 1.0000 0.766 :bw  11
                  :lfo1 0.491   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  2.648 :dcy1 1.966 :dcy2 0.834 :rel 4.026
                        :peak 1.000 :bp   0.548 :sus  0.396])
           (noise2 6.9680 0.431 :bw  18 :lag 1.244)
           (buzz 1.0003 0.333
                 :lfo1  0.724 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  3.348 :dcy1 1.889 :dcy2 1.331 :rel 3.780
                       :peak 1.000 :bp   0.668 :sus  0.626])
           (buzz-harmonics   5 :env   6 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  111 :track  2 :env 0.361
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.373 :cca +0.000 :ccb +0.000]
                      :env [:att 3.066 :dcy 1.661 :rel 4.904 :sus 0.504]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [1.2996 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-20    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.100 :lfo3 0.000 :xenv 1.000]
                      :fb    +0.519 :xfb -0.280)
           (delay2    :time [0.0006 :lfo2 0.000 :lfo3 0.500 :xenv 0.000]
                      :amp  [-20    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 -0.10 :lfo3 0.368 :xenv 0.000]
                      :fb    +0.514 :xfb -0.357)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------  57 Lanthanum
;;
(save-program  57 "Lanthanum" 
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.486 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    5.982 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 0.094)
           (lfo2    5.982 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    1.994 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  1.349 :dcy1 0.000 :dcy2 0.000 :rel 1.362
                 :peak 1.000 :bp   1.000 :sus  1.000)
           (penv :a0 -0.0499 :a1 +0.0210 :a2 +0.0899 :a3 -0.0920
                 :t1 1.116   :t2 0.960   :t3 0.695   :cc9 0.000)
           (op1 0.9913 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.048 :dcy1 0.483 :dcy2 5.656 :rel 2.900
                      :peak 1.000 :bp   0.505 :sus  0.100])
           (fm1 0.5000 5.961 :bias +0.000 :env 0.141 :lag 0.000 :left 0   :right 0  )
           (op2 2.9784 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  1.829 :dcy1 0.000 :dcy2 0.000 :rel 1.176
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm2 2.0000 5.011 :bias +0.000 :env 0.547 :lag 0.727 :left 6   :right -3 )
           (op3 3.0000 0.991
                :lfo1  0.000 :cca 0.974 :ccb 0.111 :vel 0.353 :prss 0.000
                :penv +0.0395  :key  60 :left   +0 :right  +0
                :env [:att  1.390 :dcy1 0.000 :dcy2 0.000 :rel 0.149
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm3 3.0000 7.584 :bias +0.000 :env 0.721 :lag 0.000 :left 9   :right 0  )
           (op4 3.0287 1.000
                :lfo1  0.603 :cca 0.000 :ccb 0.000 :vel 0.124 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.693 :dcy1 1.116 :dcy2 0.000 :rel 0.960
                      :peak 1.000 :bp   0.714 :sus  0.714])
           (fm4 0.5000 1.071 :bias +0.000 :env 0.930 :lag 0.851 :left 0   :right 0  )
           (op5 5.0000 0.606
                :lfo1  0.839 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv -0.0669  :key  60 :left   +0 :right  -9
                :env [:att  1.107 :dcy1 0.319 :dcy2 0.000 :rel 1.565
                      :peak 1.000 :bp   0.601 :sus  0.601])
           (fm5 0.2500 2.511 :bias +0.000 :env 0.615 :lag 0.657 :left 0   :right 0  )
           (op6 7.0000 0.433
                :lfo1  0.682 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  1.089 :dcy1 1.040 :dcy2 0.000 :rel 0.920
                      :peak 1.000 :bp   0.712 :sus  0.712])
           (fm6 0.5000 3.197 :bias +0.000 :env 0.851 :lag 0.851 :left 0   :right 0  )
           (noise 1.0000 0.000 :bw  84
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  1.371 :dcy1 0.000 :dcy2 0.000 :rel 1.151
                        :peak 1.000 :bp   1.000 :sus  1.000])
           (noise2 9.0000 0.337 :bw  58 :lag 0.779)
           (buzz 1.0000 0.330
                 :lfo1  0.000 :cca 0.000 :ccb 0.537 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.055 :dcy1 0.284 :dcy2 2.597 :rel 15.284
                       :peak 1.000 :bp   0.705 :sus  0.100])
           (buzz-harmonics  37 :env  16 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [  397 :track  1 :env 0.992
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.823 :cca +0.000 :ccb +0.000]
                      :env [:att 1.484 :dcy 0.000 :rel 0.571 :sus 1.000]
                      :mode +0.456)
           (bp-filter :offset 6.000)
           (fold      :wet 0.280 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.3343 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-31    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.899 :xfb +0.000)
           (delay2    :time [0.0008 :lfo2 0.000 :lfo3 0.941 :xenv 0.625]
                      :amp  [-55    :lfo2 0.000 :lfo3 0.000 :xenv 0.610]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.565 :xfb -0.321)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))


;; ------------------------------------------------------  58 "Cerium"
;;
(save-program  58 "Cerium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.757 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    2.159 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.583)
           (lfo2    0.720 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    2.879 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (xenv :att  0.061 :dcy1 0.134 :dcy2 1.000 :rel 1.000
                 :peak 1.000 :bp   0.596 :sus  0.100)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.069 :dcy1 0.140 :dcy2 0.000 :rel 0.115
                      :peak 1.000 :bp   0.911 :sus  0.911])
           (fm1 1.0000 6.105 :bias +2.912 :env 0.826 :lag 0.244 :left 3   :right 0  )
           (op2 3.0000 1.000
                :lfo1  0.000 :cca 0.000 :ccb 0.495 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.269 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.744 :sus  0.040])
           (fm2 2.0000 6.426 :bias +2.912 :env 0.971 :lag 0.000 :left 0   :right 0  )
           (op3 5.0000 0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.576 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 0.022 :dcy2 0.000 :rel 0.043
                      :peak 1.000 :bp   0.133 :sus  0.133])
           (fm3 0.5000 3.741 :bias +2.912 :env 0.803 :lag 0.000 :left 0   :right -3 )
           (op4 7.0591 0.425
                :lfo1  0.946 :cca 0.000 :ccb 0.177 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 0.100 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.642 :sus  0.100])
           (fm4 0.5000 6.400 :bias +2.912 :env 0.986 :lag 0.000 :left 3   :right 0  )
           (op5 9.0000 0.000
                :lfo1  0.000 :cca 0.271 :ccb 0.630 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 0.089 :dcy2 1.000 :rel 1.000
                      :peak 1.000 :bp   0.596 :sus  0.100])
           (fm5 1.0000 0.420 :bias +2.912 :env 0.466 :lag 0.920 :left 0   :right 0  )
           (op6 9.0260 0.332
                :lfo1  0.173 :cca 0.000 :ccb 0.537 :vel 0.266 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.040 :dcy1 0.077 :dcy2 2.569 :rel 1.000
                      :peak 1.000 :bp   0.592 :sus  0.100])
           (fm6 1.0000 5.322 :bias +2.912 :env 0.747 :lag 0.000 :left 3   :right 0  )
           (noise 7.0000 0.429 :bw  44
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -3
                  :env [:att  0.000 :dcy1 0.180 :dcy2 1.000 :rel 1.000
                        :peak 1.000 :bp   0.630 :sus  0.100])
           (noise2 11.0000 0.273 :bw  68 :lag 1.909)
           (buzz 3.0000 1.000
                 :lfo1  0.450 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 0.093 :dcy2 1.000 :rel 1.000
                       :peak 1.000 :bp   0.674 :sus  0.100])
           (buzz-harmonics  12 :env  42 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [10000 :track  0 :env 0.000
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.000 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.147 :rel 1.000 :sus 0.100]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.000 :gain  1 :cca  +0 :ccb  +0)
           (delay1    :time [0.9264 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-37    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.678 :lfo3 0.000 :xenv 0.241]
                      :fb    +0.078 :xfb +0.378)
           (delay2    :time [0.4632 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-15    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.103 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.432 :xfb +0.424)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  59 Praseodymium
;;
(save-program  59 "Praseodymium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 4.467 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.117 :bleed 0.000 :cca  0.630 :prss  0.000 :delay 0.694)
           (lfo2    0.419 :xenv  0.000 :cca  0.709 :ccb   0.000)
           (lfo3    0.279 :xenv  0.282 :cca  0.000 :ccb   0.000)
           (xenv :att  0.278 :dcy1 0.255 :dcy2 0.375 :rel 0.384
                 :peak 0.042 :bp   0.587 :sus  0.046)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.345
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.390 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.439 :dcy1 0.389 :dcy2 0.188 :rel 0.354
                      :peak 0.764 :bp   0.632 :sus  0.389])
           (fm1 1.0000 7.017 :bias +0.000 :env 0.933 :lag 0.000 :left 0   :right -9 )
           (op2 3.0000 0.881
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.557 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  4.468 :dcy1 0.203 :dcy2 0.153 :rel 0.502
                      :peak 1.000 :bp   0.804 :sus  0.234])
           (fm2 0.5000 7.663 :bias +0.000 :env 0.640 :lag 0.000 :left 0   :right -6 )
           (op3 4.9738 0.000
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.371 :dcy1 0.329 :dcy2 0.268 :rel 0.167
                      :peak 1.000 :bp   0.748 :sus  0.834])
           (fm3 0.5000 0.093 :bias +0.000 :env 0.626 :lag 0.651 :left 0   :right -6 )
           (op4 7.0000 0.592
                :lfo1  0.000 :cca 0.002 :ccb 0.000 :vel 0.954 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.707 :dcy1 0.387 :dcy2 0.166 :rel 0.387
                      :peak 1.000 :bp   0.699 :sus  0.399])
           (fm4 3.0000 7.284 :bias +0.000 :env 0.818 :lag 0.608 :left 0   :right -3 )
           (op5 8.9286 0.904
                :lfo1  0.988 :cca 0.000 :ccb 0.541 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.340 :dcy1 0.408 :dcy2 0.410 :rel 0.370
                      :peak 1.000 :bp   0.961 :sus  0.278])
           (fm5 0.5000 2.665 :bias +0.000 :env 0.837 :lag 0.000 :left 0   :right 0  )
           (op6 9.0000 0.416
                :lfo1  0.000 :cca 0.000 :ccb 0.081 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.368 :dcy1 0.000 :dcy2 0.000 :rel 0.452
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 7.183 :bias +0.000 :env 0.965 :lag 0.784 :left 0   :right 0  )
           (noise 5.0000 0.781 :bw  16
                  :lfo1 0.027   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -6
                  :env [:att  0.678 :dcy1 0.267 :dcy2 0.619 :rel 0.325
                        :peak 1.000 :bp   0.728 :sus  0.900])
           (noise2 11.0000 0.970 :bw  65 :lag 0.000)
           (buzz 3.0000 0.682
                 :lfo1  0.246 :cca 0.000 :ccb 0.374 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.242 :dcy1 0.525 :dcy2 0.389 :rel 0.299
                       :peak 1.000 :bp   0.907 :sus  0.759])
           (buzz-harmonics  29 :env  34 :cca   0 :hp   1 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.980
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.245 :cca +0.000 :ccb +0.000]
                      :env [:att 0.435 :dcy 0.334 :rel 0.217 :sus 0.526]
                      :mode -0.067)
           (bp-filter :offset 4.000)
           (fold      :wet 0.000 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [1.7910 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-49    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.149 :xfb +0.362)
           (delay2    :time [0.0002 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-33    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.684 :xfb -0.077)
           (amp   -3   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  60 Neodymium
;;
(save-program  60 "Neodymium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 6.814 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    1.136 :bleed 0.000 :cca  0.000 :prss  0.000 :delay 1.216)
           (lfo2    10.000 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.213 :xenv  0.401 :cca  0.000 :ccb   0.000)
           (xenv :att  0.390 :dcy1 0.344 :dcy2 0.000 :rel 0.130
                 :peak 1.000 :bp   0.663 :sus  0.663)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 1.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.263 :dcy1 0.261 :dcy2 0.000 :rel 0.540
                      :peak 1.000 :bp   0.521 :sus  0.521])
           (fm1 0.2500 4.839 :bias +0.000 :env 0.771 :lag 0.980 :left 0   :right 0  )
           (op2 2.0000 0.667
                :lfo1  0.000 :cca 0.225 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.230 :dcy1 0.156 :dcy2 0.000 :rel 0.272
                      :peak 1.000 :bp   0.047 :sus  0.047])
           (fm2 1.0000 3.135 :bias +0.000 :env 0.599 :lag 0.133 :left 0   :right 0  )
           (op3 3.0000 1.000
                :lfo1  0.767 :cca 0.000 :ccb 0.203 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  3.697 :dcy1 0.368 :dcy2 0.746 :rel 0.279
                      :peak 1.000 :bp   0.770 :sus  0.308])
           (fm3 1.0000 3.045 :bias +0.000 :env 0.857 :lag 0.000 :left 0   :right 0  )
           (op4 5.0000 0.600
                :lfo1  0.000 :cca 0.000 :ccb 0.693 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.304 :dcy1 0.189 :dcy2 0.000 :rel 0.360
                      :peak 1.000 :bp   0.837 :sus  0.837])
           (fm4 2.0000 0.810 :bias +0.000 :env 0.521 :lag 0.719 :left 0   :right -9 )
           (op5 7.0000 0.429
                :lfo1  0.000 :cca 0.989 :ccb 0.224 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -9
                :env [:att  0.395 :dcy1 0.206 :dcy2 0.000 :rel 0.182
                      :peak 1.000 :bp   0.713 :sus  0.713])
           (fm5 0.5000 7.754 :bias +0.000 :env 0.069 :lag 0.848 :left 9   :right 0  )
           (op6 9.0000 0.333
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -3
                :env [:att  0.322 :dcy1 0.000 :dcy2 0.000 :rel 1.762
                      :peak 1.000 :bp   1.000 :sus  1.000])
           (fm6 2.0000 0.337 :bias +0.000 :env 0.913 :lag 0.000 :left 0   :right 0  )
           (noise 6.0000 0.500 :bw  80
                  :lfo1 0.000   :cca 0.000 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  -9
                  :env [:att  0.179 :dcy1 0.327 :dcy2 0.000 :rel 0.252
                        :peak 1.000 :bp   0.523 :sus  0.523])
           (noise2 10.0000 0.300 :bw  68 :lag 0.000)
           (buzz 5.0000 0.600
                 :lfo1  0.000 :cca 0.942 :ccb 0.000 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  -6
                 :env [:att  0.000 :dcy1 0.352 :dcy2 0.000 :rel 0.705
                       :peak 1.000 :bp   0.012 :sus  0.012])
           (buzz-harmonics  49 :env -41 :cca   0 :hp   1 :hp<-env   1)
           (lp-filter :freq [   50 :track  0 :env 0.306
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.648 :cca +0.000 :ccb +0.000]
                      :env [:att 0.185 :dcy 0.109 :rel 0.380 :sus 0.797]
                      :mode -1.000)
           (bp-filter :offset 1.000)
           (fold      :wet 0.603 :gain  2 :cca  +0 :ccb  +0)
           (delay1    :time [0.5870 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-45    :lfo2 0.000 :lfo3 0.000 :xenv 0.626]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.336 :xenv 1.000]
                      :fb    +0.371 :xfb +0.000)
           (delay2    :time [0.0004 :lfo2 0.000 :lfo3 0.749 :xenv 0.000]
                      :amp  [-32    :lfo2 0.000 :lfo3 0.000 :xenv 0.650]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.872 :xfb +0.020)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

;; ------------------------------------------------------  61 "Promethium
;;
(save-program  61 "Promethium"
   (cobalt (enable  1 2 3  4 5 6  :noise :buzz )
           (port-time 0.000 :cc5  0.000)
           (vibrato 5.156 :sens  0.010 :prss 0.000 :depth 0.000 :delay 2.000)
           (lfo1    0.644 :bleed 0.239 :cca  0.000 :prss  0.000 :delay 1.091)
           (lfo2    0.286 :xenv  0.000 :cca  0.000 :ccb   0.000)
           (lfo3    0.859 :xenv  0.096 :cca  0.000 :ccb   0.000)
           (xenv :att  0.309 :dcy1 2.680 :dcy2 2.257 :rel 2.506
                 :peak 1.000 :bp   0.958 :sus  0.722)
           (penv :a0 +0.0000 :a1 +0.0000 :a2 +0.0000 :a3 +0.0000
                 :t1 1.000   :t2 1.000   :t3 1.000   :cc9 0.000)
           (op1 0.9997 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.276 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.927 :dcy2 0.000 :rel 3.855
                      :peak 1.000 :bp   0.034 :sus  0.034])
           (fm1 3.0000 5.964 :bias +0.000 :env 0.724 :lag 0.711 :left 9   :right 0  )
           (op2 1.0000 0.200
                :lfo1  0.000 :cca 0.000 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.158 :dcy2 4.429 :rel 2.967
                      :peak 1.000 :bp   0.594 :sus  0.100])
           (fm2 0.2500 0.044 :bias +0.000 :env 0.537 :lag 0.396 :left 0   :right 0  )
           (op3 2.0000 0.400
                :lfo1  0.000 :cca 0.768 :ccb 0.000 :vel 0.845 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.099 :dcy2 3.559 :rel 5.359
                      :peak 1.000 :bp   0.692 :sus  0.100])
           (fm3 0.2500 5.431 :bias +0.000 :env 0.346 :lag 0.393 :left 0   :right -3 )
           (op4 3.9728 0.795
                :lfo1  0.912 :cca 0.136 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 1.943 :dcy2 0.000 :rel 3.887
                      :peak 1.000 :bp   0.209 :sus  0.209])
           (fm4 1.0000 1.006 :bias +0.000 :env 0.963 :lag 0.556 :left 0   :right -9 )
           (op5 4.0000 0.800
                :lfo1  0.000 :cca 0.405 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  +0
                :env [:att  0.000 :dcy1 0.102 :dcy2 6.225 :rel 5.722
                      :peak 1.000 :bp   0.718 :sus  0.100])
           (fm5 3.0000 2.086 :bias +0.000 :env 0.182 :lag 0.000 :left 6   :right -3 )
           (op6 5.0000 1.000
                :lfo1  0.000 :cca 0.590 :ccb 0.000 :vel 0.000 :prss 0.000
                :penv +0.0000  :key  60 :left   +0 :right  -6
                :env [:att  0.000 :dcy1 1.389 :dcy2 0.000 :rel 2.779
                      :peak 1.000 :bp   0.030 :sus  0.030])
           (fm6 0.2500 1.783 :bias +0.000 :env 0.947 :lag 0.197 :left 6   :right -9 )
           (noise 3.0000 0.600 :bw  56
                  :lfo1 0.000   :cca 0.970 :vel 0.000 :prss 0.000
                  :penv +0.0000 :key  60   :left   +0 :right  +0
                  :env [:att  0.000 :dcy1 0.160 :dcy2 4.700 :rel 7.007
                        :peak 1.000 :bp   0.515 :sus  0.242])
           (noise2 5.0274 0.995 :bw  78 :lag 0.000)
           (buzz 3.0000 0.600
                 :lfo1  0.000 :cca 0.000 :ccb 0.222 :vel 0.000 :prss 0.000
                 :penv +0.0000  :key  60 :left   +0 :right  +0
                 :env [:att  0.000 :dcy1 1.810 :dcy2 0.000 :rel 3.620
                       :peak 1.000 :bp   0.172 :sus  0.172])
           (buzz-harmonics  56 :env -19 :cca   0 :hp   0 :hp<-env   0)
           (lp-filter :freq [   50 :track  0 :env 0.715
                             :prss 0.000 :cca 0.000 :ccb 0.000]
                      :res [0.579 :cca +0.000 :ccb +0.000]
                      :env [:att 0.000 :dcy 0.160 :rel 4.990 :sus 0.104]
                      :mode +0.085)
           (bp-filter :offset 4.000)
           (fold      :wet 0.445 :gain  8 :cca  +0 :ccb  +0)
           (delay1    :time [1.1637 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :amp  [-52    :lfo2 0.000 :lfo3 0.000 :xenv 0.602]
                      :pan  [-0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    +0.562 :xfb -0.178)
           (delay2    :time [0.0004 :lfo2 0.000 :lfo3 0.933 :xenv 0.060]
                      :amp  [-37    :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :pan  [+0.700 :lfo2 0.000 :lfo3 0.000 :xenv 0.000]
                      :fb    -0.753 :xfb -0.087)
           (amp   -6   :dry  +0 :dry-pan +0.000 :cc7 0.000)))

