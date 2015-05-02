(println "-->    cobalt constants")

(ns cadejo.instruments.cobalt.constants)

(def op-count 10)                       ; ops 1..8, + noise + buzz
(def max-port-time 0.5)
(def min-buzz-hp-freq 1)
(def max-buzz-hp-freq 9999)
(def min-noise-filter-cutoff 50)        ; Must be > 0
(def min-noise-filter-bw 3)
(def max-noise-filter-bw 100)
(def max-delay-time 2.00)
(def max-db 0)
(def min-db -60)
(def max-keyscale-depth 12)             ; in db
(def min-keyscale-depth (* -1 max-keyscale-depth))
(def min-lfo-frequency 0.01)
(def max-lfo-frequency 10)
(def min-vibrato-sensitivity 0.001)
(def max-vibrato-sensitivity 0.1)
(def min-op-detune 0.1)
(def max-op-detune 127)
(def min-buzz-harmonics 1)
(def max-buzz-harmonics 64)
(def min-filter-cutoff 50)
(def max-filter-cutoff 12000)
(def min-filter-track 0)
(def max-filter-track 8)
(def filter-mod-scale 5000)
(def min-filter-res 0)
(def max-filter-res 4)
(def filter-res-scale max-filter-res)
(def noise-amp-boost 6)  ; db

(def initial-cobalt-program 
     {:port-time 0.0
      :pe-a0 0.00                ; pitch env          
      :pe-a1 0.00                ;     amp values -/+ 1.0 
      :pe-a2 0.00
      :pe-a3 0.00
      :pe-t1 1.00                ;     time a0->a1
      :pe-t2 1.00
      :pe-t3 1.00
      :lfo1-freq 5.00              ; lfo1
      :lfo1<-cca 0.0
      :lfo1<-pressure 0.0
      :lfo1-bleed 0.0
      :lfo1-delay 2.0
      ;; OP1
      :op1-amp 1.000 
      :op1-amp<-lfo1 0.00
      :op1-amp<-cca 0.00
      :op1-amp<-ccb 0.00
      :op1-amp<-velocity 0.00
      :op1-amp<-pressure 0.00
      :op1-keyscale-key 60 
      :op1-keyscale-left 0 
      :op1-keyscale-right 0
      :op1-detune 1.00
      :op1<-penv 0.00
      :op1-attack 0.00
      :op1-decay1 0.00
      :op1-decay2 0.00
      :op1-release 0.00
      :op1-peak 1.00
      :op1-breakpoint 1.00
      :op1-sustain 1.00
      :fm1-detune 1.00
      :fm1-bias 0.00
      :fm1-amp 0.00
      :fm1<-env 0.00
      :fm1-keyscale-left 0
      :fm1-keyscale-right 0
      ;; OP2
      :op2-amp 0.500 
      :op2-amp<-lfo1 0.00
      :op2-amp<-cca 0.00
      :op2-amp<-ccb 0.00
      :op2-amp<-velocity 0.00
      :op2-amp<-pressure 0.00
      :op2-keyscale-key 60 
      :op2-keyscale-left 0 
      :op2-keyscale-right 0
      :op2-detune 2.00
      :op2<-penv 0.00
      :op2-attack 0.00
      :op2-decay1 0.00
      :op2-decay2 0.00
      :op2-release 0.00
      :op2-peak 1.00
      :op2-breakpoint 1.00
      :op2-sustain 1.00
      :fm2-detune 1.00
      :fm2-bias 0.00
      :fm2-amp 0.00
      :fm2<-env 0.00
      :fm2-keyscale-left 0
      :fm2-keyscale-right 0
      ;; OP3
      :op3-amp 0.333
      :op3-amp<-lfo1 0.00
      :op3-amp<-cca 0.00
      :op3-amp<-ccb 0.00
      :op3-amp<-velocity 0.00
      :op3-amp<-pressure 0.00
      :op3-keyscale-key 60 
      :op3-keyscale-left 0 
      :op3-keyscale-right 0
      :op3-detune 3.00
      :op3<-penv 0.00
      :op3-attack 0.00
      :op3-decay1 0.00
      :op3-decay2 0.00
      :op3-release 0.00
      :op3-peak 1.00
      :op3-breakpoint 1.00
      :op3-sustain 1.00
      :fm3-detune 1.00
      :fm3-bias 0.00
      :fm3-amp 0.00
      :fm3<-env 0.00
      :fm3-keyscale-left 0
      :fm3-keyscale-right 0
      ;; OP4
      :op4-amp 0.250 
      :op4-amp<-lfo1 0.00
      :op4-amp<-cca 0.00
      :op4-amp<-ccb 0.00
      :op4-amp<-velocity 0.00
      :op4-amp<-pressure 0.00
      :op4-keyscale-key 60 
      :op4-keyscale-left 0 
      :op4-keyscale-right 0
      :op4-detune 4.00
      :op4<-penv 0.00
      :op4-attack 0.00
      :op4-decay1 0.00
      :op4-decay2 0.00
      :op4-release 0.00
      :op4-peak 1.00
      :op4-breakpoint 1.00
      :op4-sustain 1.00
      :fm4-detune 1.00
      :fm4-bias 0.00
      :fm4-amp 0.00
      :fm4<-env 0.00
      :fm4-keyscale-left 0
      :fm4-keyscale-right 0
      ;; OP5
      :op5-amp 0.200               ; linear
      :op5-amp<-lfo1 0.00
      :op5-amp<-cca 0.00
      :op5-amp<-ccb 0.00
      :op5-amp<-velocity 0.00
      :op5-amp<-pressure 0.00
      :op5-detune 5.00
      :op5<-penv 0.00             ; -/+ 1
      :op5-attack 0.00
      :op5-decay1 0.00
      :op5-decay2 0.00
      :op5-release 0.00
      :op5-peak 1.00
      :op5-breakpoint 1.00
      :op5-sustain 1.00
      ;; OP6
      :op6-amp 0.167 
      :op6-amp<-lfo1 0.00
      :op6-amp<-cca 0.00
      :op6-amp<-ccb 0.00
      :op6-amp<-velocity 0.00
      :op6-amp<-pressure 0.00
      :op6-detune 6.00
      :op6<-penv 0.00
      :op6-attack 0.00
      :op6-decay1 0.00
      :op6-decay2 0.00
      :op6-release 0.00
      :op6-peak 1.00
      :op6-breakpoint 1.00
      :op6-sustain 1.00
      ;; OP7
      :op7-amp 0.143 
      :op7-amp<-lfo1 0.00
      :op7-amp<-cca 0.00
      :op7-amp<-ccb 0.00
      :op7-amp<-velocity 0.00
      :op7-amp<-pressure 0.00
      :op7-detune 7.00
      :op7<-penv 0.00
      :op7-attack 0.00
      :op7-decay1 0.00
      :op7-decay2 0.00
      :op7-release 0.00
      :op7-peak 1.00
      :op7-breakpoint 1.00
      :op7-sustain 1.00
      ;; OP8
      :op8-amp 0.125 
      :op8-amp<-lfo1 0.00
      :op8-amp<-cca 0.00
      :op8-amp<-ccb 0.00
      :op8-amp<-velocity 0.00
      :op8-amp<-pressure 0.00
      :op8-detune 8.00
      :op8<-penv 0.00
      :op8-attack 0.00
      :op8-decay1 0.00
      :op8-decay2 0.00
      :op8-release 0.00
      :op8-peak 1.00
      :op8-breakpoint 1.00
      :op8-sustain 1.00
      :bzz-amp 0.050               ; linear
      :bzz-amp<-lfo1 0.00
      :bzz-amp<-cca 0.00
      :bzz-amp<-ccb 0.00
      :bzz-amp<-velocity 0.00
      :bzz-amp<-pressure 0.00
      :bzz-keyscale-key 60        ; MIDI key number
      :bzz-keyscale-left 0        ; db/octave
      :bzz-keyscale-right 0
      :bzz-detune 1.00
      :bzz<-penv 0.00             ; -/+ 1
      :bzz-attack 0.00
      :bzz-decay1 0.00
      :bzz-decay2 0.00
      :bzz-release 0.00
      :bzz-peak 1.00
      :bzz-breakpoint 1.00
      :bzz-sustain 1.00
      :bzz-harmonics 16           ; int > 0
      :bzz-harmonics<-env 0       
      :bzz-harmonics<-cca 0
      :bzz-hp-track 1.00          ; relative to f0
      :bzz-hp-track<-env  0.00    ; env adds to tracking
      :nse-amp 0.111 
      :nse-amp<-lfo1 0.00
      :nse-amp<-cca 0.00
      :nse-amp<-velocity 0.00
      :nse-amp<-pressure 0.00
      :nse-keyscale-key 60
      :nse-keyscale-left 0
      :nse-keyscale-right 0
      :nse-detune 9.00
      :nse<-penv 0.00
      :nse-attack 0.00
      :nse-decay1 0.00
      :nse-decay2 0.00
      :nse-release 0.00
      :nse-peak 1.00
      :nse-breakpoint 1.00
      :nse-sustain 1.00
      :nse-bw 10
      :filter-freq 9999
      :filter-track 0
      :filter-res 0
      :filter-attack 0
      :filter-decay 0
      :filter-sustain 1
      :filter-release 0
      :filter<-env 0
      :filter<-pressure 0
      :filter<-cca 0
      :filter<-ccb 0
      :filter-res<-cca 0
      :filter-res<-ccb 0
      :filter-mode -1    ; -/+1   -1 -> lp   +1 -> bp
      :filter2-detune 1  ; bp filter freq rel to lp filter
      :vibrato-frequency 7.00
      :vibrato-sensitivity 0.10
      :vibrato-depth 0.00
      :vibrato<-pressure 0.00
      :vibrato-delay 1.00
      :amp -18        ; overall amplitude (db)
      :amp<-velocity 0.00
      :amp<-cc7 0.00
      :xenv-attack 0.00
      :xenv-decay1 0.00
      :xenv-decay2 0.00
      :xenv-release 0.00
      :xenv-peak 1.00
      :xenv-breakpoint 1.00
      :xenv-sustain 1.00
      :lfo2-freq 1.00
      :lfo2-amp<-cca 0
      :lfo2-amp<-ccb 0
      :lfo2-amp<-xenv 0
      :lfo3-freq 0.50
      :lfo3-amp<-cca 0
      :lfo3-amp<-ccb 0
      :lfo3-amp<-xenv 0
      :dry-amp 0
      :dry-pan 0
      :delay1-time 0.500
      :delay1-time<-lfo2 0
      :delay1-time<-lfo3 0
      :delay1-time<-xenv 0
      :delay1-fb 0.5
      :delay1-xfb 0
      :delay1-amp min-db ; db
      :delay1-amp<-lfo2 0
      :delay1-amp<-lfo3 0
      :delay1-amp<-xenv 0
      :delay1-pan -0.7 ; +/- 1
      :delay1-pan<-lfo2 0
      :delay1-pan<-lfo3 0
      :delay1-pan<-xenv 0
      :delay2-time 0.500
      :delay2-time<-lfo2 0
      :delay2-time<-lfo3 0
      :delay2-time<-xenv 0
      :delay2-fb 0.5
      :delay2-xfb 0
      :delay2-amp min-db ; db
      :delay2-amp<-lfo2 0
      :delay2-amp<-lfo3 0
      :delay2-amp<-xenv 0
      :delay2-pan -0.7 ; +/- 1
      :delay2-pan<-lfo2 0
      :delay2-pan<-lfo3 0
      :delay2-pan<-xenv 0})

(def ignore-extra-parameters 
  [:nse-amp<-ccb])

(def cobalt-parameters (flatten 
                        [ignore-extra-parameters
                         (map first (seq initial-cobalt-program))]))
