(println "-->    cobalt data")

(ns cadejo.instruments.cobalt.data
  (:use [cadejo.instruments.cobalt.program]))

(save-program 0 "Alpha"
   (cobalt (port-time 0.00)
           (genv1 :att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                  :peak 1.00 :bp   1.00  :sus  1.00)
           (genv2 :att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                  :peak 1.00 :bp   1.00  :sus  1.00)
           (xenv  :att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                  :peak 1.00 :bp   1.00  :sus  1.00)
           (penv  :a0 0.000 :a1 0.000 :a2 0.000 :a3 0.000
                  :t1 1.000 :t2 1.000 :t3 1.000)
           (vibrato 5.000 :sens  0.01 :depth 0.00 :prss 0.00)
           (lfo1    5.000 :genv1 0.00 :cca   0.00 :prss 0.00)
           (lfo2    2.500 :genv2 0.00 :ccb   0.00 :prss 0.00)
           (lfo3    1.000 :xenv  0.00 :cca   0.00 :ccb  0.00)
           (lfo4    0.100 :xenv  0.00 :cca   0.00 :ccb  0.00)
           (op1 1.000 1.000
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00])
           (op2 2.000 0.500
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00])
           (op3 3.000 0.333
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00])
           (op4 4.000 0.250
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00])
           (op5 5.000 0.200
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00]
                :key 60 :left   0 :right   0)
           (op6 6.000 0.167
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00]
                :key 60 :left   0 :right   0)
           (op7 7.000 1.430
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00]
                :key 60 :left   0 :right   0)
           (fm7 1.000 0.000 :bias 0 :env 0.00 :left 0 :right 0)
           (op8 8.000 0.125
                :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                :cca   0.00 :ccb   0.00 
                :penv  0.00
                :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                      :peak 1.00 :bp   1.00  :sus  1.00]
                :key 60 :left   0 :right   0)
           (fm8 1.000 0.000 :bias 0 :env 0.00 :left 0 :right 0)
           (noise 9.000 0.111
                  :lfo1 0.000 :cca 0.000 :vel 0.000 :prss 0.000
                  :penv 0.00
                  :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                        :peak 1.00 :bp   1.00  :sus  1.00]
                  :key 60 :left   0 :right   0
                  :bw 10)
           (buzz  12.000 0.010 
                  :genv1 0.00 :genv2 0.00 :lfo1 0.00 :lfo2 0.00 
                  :cca   0.00 :ccb   0.00 
                  :penv  0.00
                  :env [:att 0.000 :dcy1 0.000 :dcy2 0.000 :rel 0.000
                        :peak 1.00 :bp   1.00  :sus  1.00]
                  :key 60 :left   0 :right   0)
           (buzz-harmonics 1 :env 0.000 :cca 0.000 :hp 1 :hp<-env 0)
           (delay1  :time [1.0000 :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :amp  [-60    :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :pan  [-0.7   :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :fb   0.50 :xfb 0.00)
           (delay2  :time [0.3333 :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :amp  [-60    :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :pan  [+0.7   :lfo3 0.000 :lfo4 0.000 :xenv 0.000]
                    :fb   0.50 :xfb 0.00)
           (amp -6  :vel 0.00 :genv1 0.00 :dry 0 :dry-pan 0.0 :cc7 0)))




(save-program 1 "Beta" ""
   (cobalt (port-time 0.00)
           (genv1)
           (genv2)
           (xenv)
           (penv)
           (vibrato 5.00)
           (lfo1 5.00)
           (lfo2 2.50)
           (lfo3 1.00)
           (lfo4 0.10)
           (op1 1.000 1.000)
           (op2 3.000 0.333)
           (op3 5.000 0.200)
           (op4 7.000 0.143)
           (op5 9.000 0.111)
           (op6 11.00 0.090)
           (op7 13.00 0.077)
           (fm7 1.000 0.000)
           (op8 15.00 0.067)
           (fm8 1.000 0.059)
           (noise 17.0 0.111 :bw 10)
           (buzz  2.00 0.050)
           (buzz-harmonics 19 :hp 10)
           (delay1)
           (delay2)
           (amp -6)))
