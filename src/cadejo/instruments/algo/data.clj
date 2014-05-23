(println "\t--> ALGO data")

(ns cadejo.instruments.algo.data
  (:use [cadejo.instruments.algo.program 
         :only [algo common env1 vibrato lfo1 lfo2
                op1 op2 op3 op4 op5 op6 op7 op8
                echo reverb
                save-program bank]])
  (:require [cadejo.instruments.algo.genpatch]))

(.register-function! bank 
                     :random 
                     cadejo.instruments.algo.genpatch/random-algo-program)

;; ------------------------------------------------------------0 FmRhodes
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 0 "FmRhodes" "These are remarks"
    (algo (common  :amp 0.399
                   :lp 10000
                   :port-time 0.00
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 7.000 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0030      :bias   +0
               :addsr [0.005 0.500 3.100 0.120  0.750 0.000]
               :left-key 60   :left-scale +0 :right-key 60  :right-scale +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 0.500 :detune 14.000      :bias   +0
               :addsr [0.003 0.750 2.900 0.120  0.750 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale 0  :right-key  60 :right-scale  +0                       
	       :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 0.000 :detune 14.000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +1.00  :env-scale  +1.00
               :left-key  48  :left-scale +6  :right-key  60 :right-scale  -99                      
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.005 1.000 3.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr  [0.003 0.750 2.900 0.120  0.750 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +3  :right-key  72 :right-scale  -12                       
               :velocity 5.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.000 :detune 1.2340      :bias   +0
               :addsr [0.003 0.750 2.900 0.120  0.750 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale +0  :right-key   48 :right-scale 0                         
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0060      :bias   +0
               :addsr [0.005 1.000 3.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key   72 :right-scale  -9               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 0.993 :detune 1.0000      :bias   +0
               :addsr [0.005 1.000 3.100 0.120  0.750 0.000] 
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key   60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.20))))

;; ------------------------------------------------------------ 1 Celesta
;;
(let [enable '[1 1 1   1 1 0   1 1]]
  (save-program 1 "Celesta"
    (algo (common  :amp 0.283
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 7.000 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0030      :bias   +0
               :addsr [0.001 0.100 4.100 2.000  0.800 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.60 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.000 :detune 2.9970      :bias   +0
               :addsr [0.001 0.500 3.500 2.000  0.800 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +6  :right-key  60 :right-scale  +0                       
               :velocity 0.60 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 0.150 :detune 1.3890      :bias   +0
               :addsr [0.010 0.200 1.100 0.000  0.500 0.200]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.20 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.001 0.200 2.100 2.000  0.600 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 0.400 :detune 1.0470      :bias   +0
               :addsr [0.001 0.100 4.100 2.000  0.800 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +6  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.000 :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.001 0.200 2.100 2.000  0.600 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 0.500 :detune 2.9900      :bias   +0
               :addsr [0.001 0.100 4.100 2.000  0.800 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.50 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.00))))

;; ------------------------------------------------------------ 2 Ice9
;;
(let [enable '[1 1 0   1 1 1   1 1]]
  (save-program 2 "Ice9"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 4.9790      :bias   +0
               :addsr [0.500 0.750 0.500 2.500  0.900 0.800]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.000 :detune 15.020      :bias   +0
               :addsr [0.600 0.850 1.000 2.000  0.850 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  79  :right-scale  -6
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 0.000 :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 5.0000      :bias   +0
               :addsr [0.500 0.650 0.450 2.500  0.900 0.800]
               :left-key  60  :left-scale  +0  :right-key  79  :right-scale  -24
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 1.000 :detune 28.050      :bias   +0
               :addsr [0.600 0.500 0.500 2.100  0.800 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  72  :right-scale  -18
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.500 :detune 14.033      :bias   +0
               :addsr [0.700 0.600 0.600 1.750  0.800 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  72  :right-scale  -18
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 5.0390      :bias   +0
               :addsr [0.600 0.770 0.500 2.900  0.800 0.700]
               :left-key  60  :left-scale  +0  :right-key  79  :right-scale  -24
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.000 :detune 28.000      :bias   +0
               :addsr [0.650 0.600 0.800 1.750  0.950 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  72  :right-scale  -18
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.50 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.70
                   :delay-2 1.000    :damp 0.80   :mix 0.20)
          (reverb  :size 0.80        :mix  0.50))))

;; ------------------------------------------------------------ 3 Fretless
;;
(let [enable '[1 1 1   1 1 0   1 1]]
  (save-program 3   "Fretless"
    (algo (common  :amp 0.399
                   :port-time 0.05
                   :lp  2000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 2.00   :depth 0.00 :delay 0.00 :sens 0.020)
          (lfo1    :freq 7.000  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 7.000 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0030      :bias   +0
               :addsr [0.005 0.500 3.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 0.750 :detune 1.0000      :bias   +0
               :addsr  [0.005 0.400 3.100 0.120  0.750 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.75 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp   5)
          (op3 (nth enable 2)
               :amp 1.500 :detune 0.5000      :bias   +0
               :addsr [0.000 0.250 1.500 0.100  0.700 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.75 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.006 0.500 3.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 0.750 :detune 0.5050      :bias   +0
               :addsr [0.100 0.250 1.500 0.100  0.700 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.000 :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0030      :bias   +0
               :addsr [0.006 0.500 3.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 2.000 :detune 0.5000      :bias   +0
               :addsr [0.205 0.500 3.100 0.120  0.900 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.20))))

;; ------------------------------------------------------------ 4 BassKeys
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 4 "BassKeys"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  2000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 7.000 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.003 0.700 2.100 0.120  0.750 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 3.000 :detune 0.5000      :bias   +0
               :addsr [0.000 0.100 2.100 0.120  0.950 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.75 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 4.000 :detune 0.5000      :bias   +0
               :addsr [0.000 0.250 1.500 0.100  0.700 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.75 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.000 0.300 2.100 0.120  0.700 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 2.750 :detune 1.0005      :bias   +0
               :addsr [0.000 0.250 2.500 0.100  0.700 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 3.000 :detune 1.5000      :bias   +0
               :addsr [0.030 0.250 1.500 0.100  0.600 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +2.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.5050      :bias   +0
               :addsr [0.000 0.500 1.100 0.120  0.500 0.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 8.000 :detune 3.0000      :bias   +0
               :addsr [0.005 0.100 0.500 0.120  0.550 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +2.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.20))))


;; ------------------------------------------------------------ 5 SteamOrgan
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 5 "SteamOrgan"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.177 1.534 0.209 1.970    0.975 0.436
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 5.25   :depth 0.00 :delay 0.72 :sens 0.030)
          (lfo1    :freq 1.852  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 11.113 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.201 :detune 2.9875      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.57 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.01 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 4.890 :detune 0.5024      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.66 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 1.232 :detune 0.9951      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 8.0272      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.79 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 1.567 :detune 2.9979      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 5.609 :detune 2.0172      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.691 :detune 1.0030      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 4.803 :detune 1.0099      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.40 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.500    :fb 0.50
                   :delay-2 0.255    :damp 0.70   :mix 0.05)
          (reverb  :size 0.24        :mix  0.85))))

;; ------------------------------------------------------------ 6 Alcestis
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 6 "Alcestis" "cca-->op4 ccb-->op7"
    (algo (common  :amp 0.560
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.925 1.641 0.249 1.844    0.866 0.402
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 9.28   :depth 0.00 :delay 0.95 :sens 0.030)
          (lfo1    :freq 4.640  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.88   :env1->skew +0.00)
          (lfo2    :freq 13.920 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.467 :detune 0.9993      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 5.807 :detune 0.9926      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 5.092 :detune 1.0092      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.9764      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.50 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 2.764 :detune 0.9915      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.000 :detune 0.9891      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key   2  :left-scale +60  :right-key   0  :right-scale +60
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.21 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.510 :detune 1.0019      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 0.000 :detune 0.9946      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key   4  :left-scale +60  :right-key   0  :right-scale +60
               :velocity 0.86 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.851    :fb 0.20
                   :delay-2 0.776    :damp 0.68   :mix 0.01)
          (reverb  :size 0.56        :mix  0.94))))

;; ------------------------------------------------------------ 7 Admetus
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 7 "Admetus"
    (algo (common  :amp 0.283
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.675 0.216 1.436 1.992    0.235 0.194
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 6.000   :depth 0.00 :delay 0.22 :sens 0.130)
          (lfo1    :freq 7.790  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 54.532 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.516 :detune 1.0087      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 5.973 :detune 3.0039      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.51 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 1.030 :detune 1.0102      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.57 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0107      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 3.365 :detune 4.0361      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.03 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.12)
          (op6 (nth enable 5)
               :amp 1.029 :detune 0.9972      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.16 :pressure 1.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.33 :env->fb +3.76 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.979 :detune 1.0013      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 5.721 :detune 8.0135      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.72 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.755    :fb 0.51
                   :delay-2 1.509    :damp 0.94   :mix 0.100)
          (reverb  :size 0.09        :mix  0.00))))

;; ------------------------------------------------------------ 8 Klaxon
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 8 "Klaxon" "Pressure n-> op6 feedback"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.998 1.265 0.238 0.925    0.170 0.039
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 15.01   :depth 0.00 :delay 0.05 :sens 0.030)
          (lfo1    :freq 7.505  :cca->freq 0.00 :ccb->freq 0.00
                   :env1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :env1->skew +0.00)
          (lfo2    :freq 3.530 :cca->freq 0.00 :ccb->freq 0.00
                   :lfo1 0.000 :pressure 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50   :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.800 :detune 3.0342      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 3.724 :detune 5.9338      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 3.106 :detune 8.0907      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 0.461 :detune 3.9743      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 5.530 :detune 1.0103      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.54 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.56)
          (op6 (nth enable 5)
               :amp 4.183 :detune 1.0090      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.28 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +4.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 2.0102      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0               
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.788 :detune 7.0712      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0  :right-key  60 :right-scale  +0                       
               :velocity 0.13 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.22 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 3.198    :fb 0.00
                   :delay-2 0.917    :damp 0.80   :mix 0.00)
          (reverb  :size 0.85        :mix  0.43))))

;; ------------------------------------------------------------ 9 NoisePad
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 9 "NoisePad"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.455 1.360 1.803 0.151    0.679 0.208
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 10.53   :depth 0.00 :delay 0.27 :sens 0.030)
          (lfo1    :freq 10.530  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.79  :env1->skew +0.00)
          (lfo2    :freq 7.897  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 3.0000      :bias   +0.5
               :addsr [0.331 0.100 0.100 0.751  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.321 :detune 2.0000      :bias   +0.5
               :addsr [0.001 0.100 0.100 0.751  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  55  :left-scale  +3  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 2.000 :detune 1.0000      :bias   +0.5
               :addsr [1.201 0.000 1.100 0.751  1.000 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 0.628 :detune 0.5005      :bias   +0
               :addsr [0.201 0.100 0.100 1.751  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 4.991 :detune 0.5006      :bias   +0
               :addsr [0.201 0.100 0.100 2.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  55  :left-scale  +6  :right-key  60  :right-scale  +0
               :velocity 0.68 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 1.313 :detune 2.0001      :bias   +0
               :addsr [0.351 0.100 0.100 1.251  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.25 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.303 :detune 4.0000      :bias   +0
               :addsr [0.751 0.100 0.100 2.551  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.282 :detune 6.9580      :bias   +0
               :addsr [0.751 0.100 0.100 2.551  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.13 :env->fb +7.54 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.855    :fb 0.33
                   :delay-2 1.000    :damp 0.79   :mix 0.11)
          (reverb  :size 0.86        :mix  0.50))))

;; ------------------------------------------------------------ 10 Poly Something
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 10 "Poly Something"
    (algo (common  :amp 0.141
                   :port-time 0.00
                   :lp  5000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.649 1.914 0.341 1.808    0.041 0.456
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.53   :depth 0.00 :delay 0.17 :sens 0.030)
          (lfo1    :freq 2.745  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 2.059  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.304 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 6.9353      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.56 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 2.174 :detune 0.9906      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 6.905 :detune 1.9877      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 0.552 :detune 1.0024      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 2.405 :detune 1.5026      :bias   +0
               :addsr [0.011 0.500 2.100 0.001  0.500 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 1.000 :detune 5.9627      :bias   +0
               :addsr [0.001 0.100 0.750 0.001  0.900 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key   0  :left-scale +0   :right-key  67  :right-scale +0 
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.534 :detune 2.9728      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.000 :detune 0.9960      :bias   +0
               :addsr [0.001 0.050 2.100 0.301  0.900 0.100]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale +0   :right-key  60  :right-scale +0 
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.72 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 1.107    :fb 0.00
                   :delay-2 5.534    :damp 0.59   :mix 0.00)
          (reverb  :size 0.95        :mix  0.18))))

;; ------------------------------------------------------------ 11 Lindiwe
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 11 "Lindiwe"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.634 0.442 0.192 1.585    0.168 0.851
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 6.00   :depth 0.00 :delay 0.68 :sens 0.030)
          (lfo1    :freq 3.00   :cca->freq 2.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.750 :ccb 0.000 :cca 0.00 :ccb 0.00
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 1.50   :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.909 :detune 0.5013      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 2.472 :detune 2.0000      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.50 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 5.384 :detune 0.5006      :bias   +0
               :addsr [1.001 1.100 1.100 0.001  0.700 0.200]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 0.399 :detune 1.0017      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 3.572 :detune 1.0017      :bias   +0.5
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 6.265 :detune 2.0034      :bias   +0
               :addsr [0.501 0.500 2.100 0.001  0.500 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.07 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.50 :lfo2     0.00 :hp  50
               :fb      +0.25 :env->fb +0.25 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0095      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.091 :detune 7.9483      :bias   +0
               :addsr [0.331 1.100 2.100 0.201  0.500 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.50 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.25 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 1.366    :fb 0.00
                   :delay-2 2.048    :damp 0.72   :mix 0.00)
          (reverb  :size 0.11        :mix  0.00))))

;; ------------------------------------------------------------ 12 HiBells
;;
(let [enable '[1 1 0   1 1 1   1 1]
      openv-1 [0.001 1.520 0.750 2.000  0.000 0.500]
      openv-2 [0.011 1.220 2.750 2.000  0.900 0.500]]
  (save-program 12 "HiBells" "cca --> op6"
    (algo (common  :amp 0.283
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 9.0020      :bias   +0
               :addsr openv-1
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.20 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 0.250 :detune 1.7503      :bias   +0
               :addsr openv-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.70 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 0.000 :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 8.9930      :bias   +0
               :addsr openv-1
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.20 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 0.250 :detune 3.5000      :bias   +0
               :addsr openv-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.700 :detune 18.020      :bias   +0
               :addsr openv-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.50 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.700 :detune 0.0000      :bias +650
               :addsr [0.020 0.750 0.100 0.500  0.000 0.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.500 :detune 2.0000      :bias   +0
               :addsr [0.020 0.330 0.330 0.400  0.000 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.20 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.060    :fb 0.40
                   :delay-2 0.125    :damp 0.73   :mix 0.10)
          (reverb  :size 0.70        :mix  0.50))))

;; ------------------------------------------------------------ 13 Magali
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 13 "Magali"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0100
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.427 1.686 0.992 0.068    0.364 0.485
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.08 :sens 0.010)
          (lfo1    :freq 7.004  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 1.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 1.18   :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.250 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 0.9996      :bias   +0
               :addsr [0.021 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.25 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 4.959 :detune 1.0000      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.62 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.10 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 6.668 :detune 1.0004      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.79 :pressure 0.25 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.121 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.99 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 5.392 :detune 1.0000      :bias   -0.25
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 0.33 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.10 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 2.193 :detune 1.0000      :bias   +0.25
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.75)

          (op7 (nth enable 6)
               :amp 0.960 :detune 1.0001      :bias   +0
               :addsr [0.211 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.91 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 2.179 :detune 2.0000      :bias   +0
               :addsr [0.201 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.26 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.75)
          (echo    :delay-1 1.000    :fb 0.77
                   :delay-2 0.816    :damp 0.86   :mix 0.10)
          (reverb  :size 0.28        :mix  0.40))))

;; ------------------------------------------------------------ 14 'Glass'
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 14 "Glass"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.30 :delay 0.50 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.300 0.300 1.000 2.500  1.000 0.950]
               :left-key  60  :left-scale  +3  :right-key  72  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.000 :detune 3.0290      :bias   +0
               :addsr [0.230 0.000 0.300 1.800  1.000 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +6  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 1.000 :detune 3.0280      :bias   +0
               :addsr [0.000 0.000 0.000 0.000  0.000 0.000]
                       :env-bias  +1.00  :env-scale  +1.00
               :left-key  60  :left-scale  +6  :right-key  60  :right-scale  -99
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0090      :bias   +0
               :addsr [0.500 0.300 1.000 2.300  1.000 0.950]
               :left-key  60  :left-scale  +3  :right-key  72  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 1.000 :detune 1.0100      :bias   +0
               :addsr [0.330 0.000 0.300 1.700  1.000 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 1.000 :detune 1.0200      :bias   +0
               :addsr [0.330 0.000 0.300 1.700  1.000 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  +9  :right-key  48  :right-scale  -99
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.75 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0030      :bias   +0
               :addsr [0.450 0.320 0.750 2.250  1.000 0.950]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.000 :detune 5.5500      :bias   +0
               :addsr [0.230 0.000 0.200 1.750  1.000 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  +9  :right-key  72  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.75 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.60
                   :delay-2 0.750    :damp 0.80   :mix 0.30)
          (reverb  :size 0.70        :mix  0.40))))

;; ------------------------------------------------------------ 15 Clipped Piano
;;
(let [enable '[1 1 1   1 1 1   1 1]
      openv-1 [0.010 0.45 4.00 0.12  0.990 0.000]
      openv-2 [0.010 0.26 4.00 0.12  0.950 0.000]
      openv-3 [0.010 0.45 4.00 0.12  0.950 0.000]
      openv-4 [0.015 0.45 4.00 0.12  0.990 0.000]
      openv-5 [0.015 0.26 4.00 0.12  0.950 0.000]
      brightness 3.000]
  (save-program 15 "Clipped Piano"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr openv-1
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 3.000) :detune 1.0000      :bias   +0
               :addsr openv-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  69  :right-scale  -6
               :velocity 0.70 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp (* brightness 2.000) :detune 5.0000      :bias   +0
               :addsr openv-3
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -6
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr openv-1
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -3
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 1.000) :detune 1.0000      :bias   +0
               :addsr openv-5
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 0.300) :detune 3.0000      :bias   +0
               :addsr openv-1
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  54  :left-scale  +18  :right-key  60  :right-scale  -12
               :velocity 0.90 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.25 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0000      :bias   +0.25
               :addsr openv-1
               :left-key  60  :left-scale  +3  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 1.000) :detune 3.0000      :bias   +0
               :addsr openv-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +6  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb       1.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.00))))

;; ------------------------------------------------------------ 16 'Lfe"
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 16 "Lfe" "cca --> op1 ccb --> op6 w/fb"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.989 0.225 0.723 1.151    0.614 0.390
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 5.35   :depth 0.00 :delay 0.15 :sens 0.030)
          (lfo1    :freq 3.566  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.442 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.29)
          (lfo2    :freq 2.378  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.728 :detune 1.0000      :bias   +0
               :addsr [0.231 0.450 1.100 1.001  0.950 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.34 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.554 :detune 1.9993      :bias   +0
               :addsr [0.501 0.000 1.000 0.751  1.000 0.900]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 1.620 :detune 1.0000      :bias   +0
               :addsr [0.801 0.100 0.100 0.701  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.99 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0010      :bias   +0
               :addsr [0.801 1.100 3.100 3.001  1.000 0.950]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.27 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 3.570 :detune 1.0015      :bias   +0
               :addsr [0.501 0.100 0.100 2.501  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.33 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 2.438 :detune 4.0008      :bias   +0
               :addsr [1.601 0.100 0.100 2.901  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.43 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +4.00 :lfo1->fb  0.60
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.818 :detune 0.9936      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.90 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.095 :detune 1.0019      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.96 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.68 :lfo2     0.00 :hp  50
               :fb      +0.27 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 3.090    :fb 0.50
                   :delay-2 1.500     :damp 0.58   :mix 0.20)
          (reverb  :size 0.50        :mix  0.20))))

;; ------------------------------------------------------------ 17 "FmReed"
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 17 "FmReed"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0100 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.020 0.050 0.050 0.200    0.800 0.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 1.500  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 3.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.750 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.01)
          (op1 (nth enable 0)
               :amp 1.000 :detune 2.0000      :bias   +0
               :addsr [0.100 0.250 1.100 0.100  0.990 0.900]
               :left-key  79  :left-scale  -12  :right-key  72  :right-scale  -3
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.01)
          (op2 (nth enable 1)
               :amp 1.000 :detune 4.0000      :bias   -2
               :addsr [0.300 0.300 1.100 0.100  0.950 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  84  :right-scale  -3
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp  1.00 :detune 3.5040      :bias   +0
               :addsr [0.050 0.050 0.200 0.500  0.000 1.000]
                       :env-bias  +1.00  :env-scale  -1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +1
               :addsr [0.100 0.250 1.100 0.100  0.990 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -9
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.10 :lfo2     0.05)
          (op5 (nth enable 4)
               :amp 1.000 :detune 2.0000      :bias   +0
               :addsr [0.300 0.300 1.100 0.100  0.950 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 1.000 :detune 2.0000      :bias   +3
               :addsr [0.300 0.300 1.100 0.100  0.950 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  48  :right-scale  -99
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +6.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.100 0.250 1.100 0.100  0.990 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.750 :detune 2.0000      :bias   +1
               :addsr [0.300 0.300 1.100 0.100  0.950 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  54  :left-scale  +3  :right-key  60  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +2.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.60        :mix  0.30))))

;; ------------------------------------------------------------ 18 Mojisola
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program  18 "Mojisola" "cca & ccb --> feedback"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.204 1.325 0.211 1.687    0.944 0.966
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 6.54   :depth 0.00 :delay 0.47 :sens 0.030)
          (lfo1    :freq 2.180  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.94  :env1->skew +0.00)
          (lfo2    :freq 2.874  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.313 :detune 5.9559      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.66 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 4.759 :detune 1.0104      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.72 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.72 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 4.842 :detune 1.0081      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 2.9737      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 0.831 :detune 0.7469      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 3.983 :detune 3.0082      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.90 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.08 :env->fb +0.91 :lfo1->fb  0.55
               :cca->fb  1.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.445 :detune 0.9906      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 0.145 :detune 1.0081      :bias   +0
               :addsr [0.001 0.100 0.100 0.001  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.63 :hp  50
               :fb      +0.94 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 1.376    :fb 0.06
                   :delay-2 1.032    :damp 0.52   :mix 0.72)
          (reverb  :size 0.82        :mix  0.00))))

;; ------------------------------------------------------------ 19 Ghost Strings
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 19 "Gost Strings" "cca & ccb --> op w feedback"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     1.105 0.406 1.816 1.380    0.151 0.903
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 5.47   :depth 0.00 :delay 0.38 :sens 0.030)
          (lfo1    :freq 0.273  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 0.068  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 0.691 :detune 1.0000      :bias   +0.0
               :addsr [0.251 0.100 0.100 1.751  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.502 :detune 2.0000      :bias   +0.5
               :addsr [0.501 0.100 0.100 1.201  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 0.87 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 4.006 :detune 1.0000      :bias   +0
               :addsr [0.901 0.100 1.200 1.001  1.000 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.21 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 0.161 :detune 1.0096      :bias   +0
               :addsr [1.001 0.100 0.100 2.001  2.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 3.987 :detune 1.0045      :bias   +0
               :addsr [0.501 0.100 0.100 1.001  1.750 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.26 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.619 :detune 5.0506      :bias   +0
               :addsr [2.001 0.100 0.100 1.501  1.100 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +6.48 :lfo1->fb  0.55
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0091      :bias   +0
               :addsr [0.201 0.100 0.100 0.751  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -6
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.43)
          (op8 (nth enable 7)
               :amp 4.718 :detune 5.9906      :bias   +0
               :addsr [1.001 0.100 0.750 0.401  1.000 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -6
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.50 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 1.372    :fb 0.26
                   :delay-2 0.915    :damp 0.55   :mix 0.00)
          (reverb  :size 0.20        :mix  0.00))))

;; ------------------------------------------------------------ 20 Insane Index
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 20 
                "Insane Index" 
                "Very long & evolving sound - intersting aliasing on high notes with pitchbend"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 0.060  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 0.125  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.50)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [6.000 0.100 0.100 6.000  1.000 1.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 50.00 :detune 1.0000      :bias   +0
               :addsr [12.00 5.100 2.100 5.000  1.000 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 50.00 :detune 1.0000      :bias   +0
               :addsr [8.000 0.100 0.100 8.000  1.000 1.000]
                       :env-bias  +1.00  :env-scale  -1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0010      :bias   +0
               :addsr [8.000 0.100 3.100 7.000  1.000 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 100.0 :detune 0.2500      :bias   +0
               :addsr [12.00 0.100 0.100 5.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 2.000 :detune 0.2500      :bias   +1
               :addsr [16.00 12.00 8.000 5.000  0.000 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +24.0 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.2000      :bias   +0
               :addsr [9.000 0.100 5.100 8.000  1.000 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 100.0 :detune 0.6001      :bias   +0
               :addsr [10.00 0.100 0.100 5.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.00 :lfo2->fb  0.20
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.80
                   :delay-2 1.000    :damp 0.80   :mix 0.30)
          (reverb  :size 0.80        :mix  0.40))))

;; ------------------------------------------------------------ 21  NotGamelan
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 21 "NotGamelan"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.000 0.005 0.550 0.550  0.950 0.000]
               :left-key  60  :left-scale  -12 :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 1.000 :detune 0.0000      :bias   +600
               :addsr [0.000 0.005 0.750 0.750  0.950 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 8.000 :detune  8.123      :bias   +0
               :addsr [0.000 0.005 0.550 0.550  0.950 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.0000      :bias   +960
               :addsr [0.000 0.001 1.250 1.550  0.900 0.000]
               :left-key  72  :left-scale  -18 :right-key  60  :right-scale  +0
               :velocity 1.05 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 2.000 :detune 0.5000      :bias   +0
               :addsr [0.000 0.001 1.250 1.550  0.900 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.80 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.40 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 2.000 :detune 0.0150      :bias   +0
               :addsr [0.000 0.001 1.250 1.550  0.900 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  84  :left-scale  -18 :right-key  60  :right-scale 0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +19.0 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.0000      :bias   +564
               :addsr [0.000 0.005 0.750 0.750  0.950 0.000]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.000 :detune 8.0000      :bias   +0
               :addsr [0.000 0.005 0.750 0.750  0.950 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.50 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.060    :fb 0.50
                   :delay-2 0.125    :damp 1.00   :mix 0.10)
          (reverb  :size 0.50        :mix  0.40))))

;; ------------------------------------------------------------ 22 Low Ensemble
;;
(let [enable '[1 1 1   1 1 1   1 1]
      envop-1 [0.125 0.400 1.100 0.750  0.970 0.800]
      envop-2 [0.200 0.300 0.750 0.750  0.970 0.800]
      envop-3 envop-2
      envop-4 [0.225 0.500 1.000 1.000  0.970 0.800]
      envop-5 [0.425 0.300 0.750 0.900  0.970 0.700]
      envop-6 [0.525 0.300 0.850 1.000  0.970 0.700]
      envop-7 [0.100 0.250 0.750 0.800  0.970 0.900]
      envop-8 envop-7
      brightness 2.00]
  (save-program 22 "Low Ensemble"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 1.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr envop-1 
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 1.000) :detune 1.0000      :bias   +0
               :addsr envop-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +3  :right-key  60  :right-scale  -3
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp (* brightness 2.000) :detune 1.0001      :bias   -0.5
               :addsr envop-3
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +6  :right-key  60  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0010      :bias   +0
               :addsr envop-4
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  -3
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 1.000) :detune 2.0020      :bias   +0
               :addsr envop-5
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  55  :left-scale  +3  :right-key  72  :right-scale  -6
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 1.000) :detune 0.5005      :bias   -1
               :addsr envop-6
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  +3  :right-key  72  :right-scale  -9
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.50 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.5004      :bias   +0
               :addsr envop-7
               :left-key  48  :left-scale -12  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 2.000) :detune 0.2502      :bias   +1
               :addsr envop-8
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale -21  :right-key  60  :right-scale  +0
               :velocity 0.50 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.40 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.25 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.20))))

;; ------------------------------------------------------------ 23 Juan
;;
(let [enable '[1 1 1   1 1 1   1 1]
      brightness 2.00]
  (save-program 23 "Juan"
    (algo (common  :amp 0.283
                   :port-time 0.00
                   :lp  1000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.500 0.500 1.100 1.500  1.000 0.950]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 2.00) :detune 1.000    :bias   +0.5
               :addsr [0.600 0.400 1.100 1.300  1.000 0.800]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +3  :right-key  72  :right-scale  -9
               :velocity 0.90 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp (* brightness 2.00) :detune 1.000 :bias   -0.5
               :addsr [0.850 0.300 1.100 1.000  1.000 0.650]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  72  :left-scale  +3  :right-key  72  :right-scale  -12
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0.4
               :addsr [0.600 0.200 0.800 1.500  1.000 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 2.00) :detune 1.000 :bias   +0.0
               :addsr [0.700 0.200 1.500 1.200  1.000 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +3  :right-key  60  :right-scale  -6
               :velocity 0.80 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 1.00) :detune 1.000    :bias   -0.4
               :addsr [0.700 0.200 1.500 1.200  1.000 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  55  :left-scale  +3  :right-key  72  :right-scale  -9
               :velocity 0.80 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.75 :env->fb -0.70 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 0.089 :detune 1.0000      :bias   +0.6
               :addsr [0.75 0.75 1.00 0.75  1.00 0.80]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 1.00 :detune 1.0000 :bias   +0
               :addsr [0.75 0.75 1.00 0.75  1.00 0.80]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  +0  :right-key  48  :right-scale  -9
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      30.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.333    :fb 0.60
                   :delay-2 0.590    :damp 0.75   :mix 0.10)
          (reverb  :size 0.50        :mix  0.00))))

;; ------------------------------------------------------------ 24 TubeBell
;;
(let [enable '[1 1 0   1 1 0   1 1]
      envop-1 [0.003 0.50 1.5 3.0  0.00 0.20]
      envop-2 [0.001 0.70 1.5 3.0  0.00 0.20]
      envop-4 envop-1
      envop-5 envop-2
      envop-7 [0.010 0.50 0.0 2.0 0.00 0.00]
      envop-8 [0.003 0.04 0.0 1.0 0.00 0.00]
      brightness 2.0]
  (save-program 24 "TubeBell"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0030      :bias   0
               :addsr envop-1
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 1.000) :detune 3.5000      :bias   +1
               :addsr envop-2
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp (* brightness 0.000) :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.9960      :bias   +0
               :addsr envop-4
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 1.000) :detune 3.5000      :bias   +1
               :addsr envop-5
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 0.000) :detune 1.0000      :bias   +0
               :addsr [0.000 0.100 0.100 0.000  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.0000      :bias   323
               :addsr envop-7
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 1.000) :detune 1.9970      :bias   +1
               :addsr envop-8
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.00))))

;; ------------------------------------------------------------ 25 StackFive
;;
(let [enable '[1 1 1   1 1 1   1 1]
      brightness 7.0]
  (save-program 25 "StackFive" "cca --> op1 ccb --> op7 pressure --> vibrato"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0200
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 1.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.100 0.100 0.330 0.120  0.980 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 1.000) :detune 1.0000      :bias   +0.25
               :addsr [0.250 0.100 0.330 0.120  0.980 0.900]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  +3  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 1.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp (* brightness 1.000) :detune 2.0000      :bias   +0.50
               :addsr [0.350 0.100 0.330 0.120  0.980 0.900]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +3  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.7500      :bias   +0
               :addsr [0.100 0.100 0.330 0.120  0.980 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 1.000) :detune 1.5000      :bias   +0.5
               :addsr [0.250 0.100 1.330 0.120  0.980 0.700]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 1.000) :detune 1.5000      :bias   -0.5
               :addsr [0.500 0.100 1.330 0.120  0.980 0.600]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.50 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.40)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.1250      :bias   +0.5
               :addsr [0.120 0.100 0.330 0.120  0.980 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.70 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 1.000) :detune 1.1250      :bias   -0.5
               :addsr [0.240 0.100 0.330 0.120  0.980 0.900]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  48  :left-scale  -9  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 1.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.75)
          (echo    :delay-1 0.150    :fb 0.60
                   :delay-2 0.075    :damp 0.75   :mix 0.07)
          (reverb  :size 0.70        :mix  0.30))))

;; ------------------------------------------------------------ 26 FmReed2
;;
(let [enable '[1 1 1   1 1 1   1 1]
      brightness 1.00]
  (save-program 26 "FmReed2" "pressure --> vibrato"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0050
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 1.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 3.500  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 1.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.1000 0.200 0.750 0.050  1.000 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 1.000) :detune 1.0000      :bias   +0
               :addsr [0.150 0.000 0.800 0.100  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.70 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.30 :hp  30)
          (op3 (nth enable 2)
               :amp (* brightness 3.000) :detune 1.0000      :bias   +0
               :addsr [0.010 0.200 0.300 0.100  0.950 0.200]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.5000      :bias   +1
               :addsr [0.1000 0.200 0.750 0.050  1.000 0.900]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 1.000) :detune 1.0000      :bias   +1
               :addsr [0.150 0.000 0.800 0.100  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.30 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp (* brightness 1.000) :detune 0.5000      :bias   +1
               :addsr [0.020 0.100 0.300 0.100  1.000 0.300]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +2.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.5000      :bias   -1
               :addsr [0.300 0.200 0.700 0.050  0.950 0.600]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 1.500) :detune 1.0000      :bias   -1
               :addsr [0.350 0.100 0.600 0.050  1.000 0.600]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.30))))

;; ------------------------------------------------------------ 27 FmBrass
;;
(let [enable '[1 1 1   1 1 1   1 1]
      brightness 1.00]
  (save-program 27 "FmBrass"
    (algo (common  :amp 0.283
                   :port-time 0.01
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0100
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 1.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 4.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.1000 0.100 0.750 0.050  0.900 0.700]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 4.000) :detune 0.5000      :bias   +0
               :addsr [0.050 0.250 1.000 0.050  0.800 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.70 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  30)
          (op3 (nth enable 2)           ; tremolo
               :amp 0.10 :detune 0.5000      :bias   +0
               :addsr [2.510 0.600 0.600 0.100  1.000 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     1.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.5000      :bias   +0.25
               :addsr [0.1000 0.100 0.750 0.050  0.900 0.700]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 6.000) :detune 0.5000      :bias   +0.25
               :addsr [0.050 0.250 1.000 0.050  0.800 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 0.200 :detune 0.1250      :bias   +0.25
               :addsr [0.020 0.100 0.200 0.100  0.500 0.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  10
               :fb      +0.00 :env->fb +1.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.25)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.5000      :bias   +0.25
               :addsr [0.200 0.200 0.700 0.050  0.950 0.600]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness 4.000) :detune 0.5000      :bias   +0.26
               :addsr [0.250 0.100 0.600 0.050  1.000 0.600]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.40 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +0.75 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.25)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.60        :mix  0.40))))

;; ------------------------------------------------------------ 28 Ibta
;;
(let [enable '[1 1 1   1 1 1   1 1]
      brightness 1.00]
  (save-program 28 "Ibta"
    (algo (common  :amp 0.399
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.001 0.020 0.450 0.450   0.960 0.200]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp (* brightness 2.000) :detune 1.0001      :bias   +0
               :addsr [0.001 0.250 0.350 0.350   0.960 0.300]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2) 
               :amp 2.00 :detune 1.0000      :bias   +0
               :addsr [0.001 0.250 0.000 0.100   0.200 0.200]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 1.0000      :bias   +0.00
               :addsr [0.001 0.020 0.550 0.550   0.960 0.200]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp (* brightness 2.000) :detune 3.0000      :bias   +0.00
               :addsr [0.001 0.250 0.350 0.350   0.960 0.300]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 1.000 :detune 1.0000      :bias   +0.00
               :addsr [0.001 0.100 0.120 0.150   0.860 0.200]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +3  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.00 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 1.0000      :bias   +0.1
               :addsr [0.001 0.020 0.450 0.450   0.960 0.200]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp (* brightness  4.00) :detune 1.0000      :bias   +0.00
               :addsr [0.001 0.250 0.350 0.350   0.960 0.300]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  55  :left-scale  +6  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.00 :env->fb +1.00 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.60        :mix  0.40))))

;; ------------------------------------------------------------ 29 Bass Poly Something
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 29 "Bass Poly Somenthing"
    (algo (common  :amp 0.200
                   :port-time 0.00
                   :lp  10000
                   :cc-volume-depth 0.00
                   :env1->pitch +0.0000 
                   :lfo1->pitch +0.0000
                   :lfo2->pitch +0.0000)
                    ;A    D1    D2    R        BP    SUS
          (env1     0.000 0.100 0.100 0.000    1.000 1.000
                    :bias +0.00 :scale +1.00)
          (vibrato :freq 7.00   :depth 0.00 :delay 0.00 :sens 0.030)
          (lfo1    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   :env1 0.000 :pressure 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :env1->skew +0.00)
          (lfo2    :freq 7.000  :cca->freq 0.000  :ccb->freq 0.000
                   ::pressure 0.000 :lfo1 0.000 :cca 0.000 :ccb 0.000
                   :skew 0.50  :lfo1->skew +0.00)
          (op1 (nth enable 0)
               :amp 1.000 :detune 1.0000      :bias   +0
               :addsr [0.011 0.040 1.000 0.070  0.980 0.750]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op2 (nth enable 1)
               :amp 4.000 :detune 1.0010      :bias   +0
               :addsr [0.010 1.760 1.500 0.070  0.600 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +12 :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50)
          (op3 (nth enable 2)
               :amp 4.000 :detune 1.0000      :bias   +0
               :addsr [0.010 1.760 1.600 0.070  0.600 1.000]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)

          (op4 (nth enable 3)
               :amp 1.000 :detune 0.5000      :bias   +0
               :addsr [0.010 0.200 0.300 0.150  0.900 0.850]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op5 (nth enable 4)
               :amp 4.000 :detune 1.0000      :bias   +0.5
               :addsr [0.100 0.200 0.750 0.250  0.900 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op6 (nth enable 5)
               :amp 4.000 :detune 0.5000      :bias   0
               :addsr [0.010 0.500 1.000 0.100  0.700 0.600]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +6  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +1.00 :env->fb +0.75 :lfo1->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)

          (op7 (nth enable 6)
               :amp 1.000 :detune 0.5000      :bias   +0.75
               :addsr [0.010 0.200 0.300 0.150  0.900 0.850]
               :left-key  60  :left-scale  +0  :right-key  60  :right-scale  +0
               :velocity 0.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00)
          (op8 (nth enable 7)
               :amp 2.000 :detune 1.0000      :bias   +0.756
               :addsr [0.100 1.750 0.750 0.250  0.400 0.750]
                       :env-bias  +0.00  :env-scale  +1.00
               :left-key  60  :left-scale  +9  :right-key  60  :right-scale  +0
               :velocity 1.00 :pressure 0.00 :cca 0.00  :ccb 0.00
               :lfo1     0.00 :lfo2     0.00 :hp  50
               :fb      +0.50 :env->fb +0.75 :lfo2->fb  0.00
               :cca->fb  0.00 :ccb->fb +0.00 :pressure->fb +0.00)
          (echo    :delay-1 0.250    :fb 0.50
                   :delay-2 1.000    :damp 0.00   :mix 0.00)
          (reverb  :size 0.50        :mix  0.00))))

;; ------------------------------------------------------------ 126 Intial program 
;;
(let [enable '[1 1 1   1 1 1   1 1]]
  (save-program 126 "Default Program"
    (algo (common  :amp  0.20 
                   :lp 10000
                   :port-time 0.00 
                   :cc-volume-depth 0.0
                   :env1->pitch 0.0000
                   :lfo1->pitch 0.0000 
                   :lfo2->pitch 0.0000)
          (env1    0.000 0.100 0.100 0.000   1.00 1.00   :bias 0.00 :scale +1.00)
          (vibrato :freq 7.00 :depth 0.00 :delay 0.00 :sens 0.03)
          (lfo1    :freq 7.00  :cca->freq 0.00 :ccb->freq 0.00
                   :env1  0.00 :pressure 0.00  :cca 0.00 :ccb 0.00
                   :skew 0.50 :env1->skew 0.0)
          (lfo2    :freq 7.00 :cca->freq 0.00 :ccb->freq 0.00
                   :pressure 0.00 :lfo1 0.00 :cca 0.00 :ccb 0.00
                   :skew 0.50  :lfo1->skew 0.00)

          (op1 (nth enable 0)  :addsr   [0.00 0.10 0.10 0.00   1.00 1.00] 
               :detune 1.000   :bias +0         :amp 1.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00)
          (op2 (nth enable 1)  :addsr   [0.00 0.10 0.10 0.00   1.00 1.00] :env-bias 0   :env-scale +1
               :detune 1.000   :bias +0         :amp 0.000    
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0 
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00       :hp  50)
          (op3 (nth enable 2) :addsr   [0.00 0.10 0.10 0.00    1.00 1.00] :env-bias 0  :env-scale +1
               :detune 1.000   :bias +0         :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00)     

          (op4 (nth enable 3) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00]
               :detune 1.000   :bias +0        :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00  :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00)
          (op5 (nth enable 4) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
               :detune 1.000   :bias +0         :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00)
          (op6 (nth enable 5) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
               :detune 1.000   :bias +0         :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00       :hp  50
               :fb   0.00      :env->fb 0.00    :lfo1->fb 0.00 
               :pressure->fb 0.00 :cca->fb 0.00 :ccb->fb 0.00)

          (op7 (nth enable 6) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00]
               :detune 1.000   :bias +0        :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00  :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00)
          (op8 (nth enable 7) :addsr   [0.00 0.10 0.10 0.00  1.00 1.00] :env-bias 0  :env-scale +1
               :detune 1.000   :bias +0         :amp 0.000
               :left-key 60    :left-scale +0   :right-key 60 :right-scale +0
               :velocity 0.00  :pressure 0.00   :cca 0.00    :ccb 0.00
               :lfo1 0.00      :lfo2 0.00       :hp  50
               :fb   0.00      :env->fb 0.00    :lfo2->fb 0.00 
               :pressure->fb 0.00 :cca->fb 0.00 :ccb->fb 0.00)
          (echo :delay-1 0.25       :fb 0.50
                :delay-2 1.00       :damp 0.0   :mix 0.00)
          (reverb :size 0.5 :mix 0.00))))

;; ------------------------------------------------------------ 127 random program 
;;
(save-program 127 :random "Random" "Generate random ALGO program" nil)

(.map-program-number! bank 63 127)
(.dump bank)



