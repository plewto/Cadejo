(println "-->    cobalt constants")

(ns cadejo.instruments.cobalt.constants)

(def op-count 6)                        ; Does not include noise or buzz
(def max-port-time 1.0)
(def min-buzz-hp-freq 1)
(def max-buzz-hp-freq 9999)
(def min-noise-filter-cutoff 50)        ; Must be > 0
(def min-noise-filter-bw 10)
(def max-noise-filter-bw 100)
(def max-delay-time 2.00)
(def max-db 0)
(def min-db -60)
(def max-keyscale-depth 12)             ; in db
(def min-keyscale-depth (* -1 max-keyscale-depth))
(def min-lfo-frequency 0.001)
(def max-lfo-frequency 10)
(def min-vibrato-sensitivity 0.0)
(def max-vibrato-sensitivity 0.1)
(def min-op-detune 0.1)
(def max-op-detune 127)
(def min-buzz-harmonics 1)
(def max-buzz-harmonics 64)
(def min-filter-cutoff 1)
(def max-filter-cutoff 12000)
(def min-filter-track 0)
(def max-filter-track 8)
(def filter-mod-scale 5000)
(def min-filter-res 0)
(def max-filter-res 4)
(def filter-res-scale max-filter-res)
(def noise-amp-boost 15)  ; db
(def max-fm-lag 4.0)
(def max-noise-lag max-fm-lag)

(def initial-cobalt-program 
  {:vibrato-frequency 7.0     ; 
   :vibrato-sensitivity 0.01  ;
   :vibrato-delay 3.0         ;
   :vibrato-depth 0.0         ;
   :vibrato<-pressure 0.0     ;
   :amp -9                    ; Overall amp in db
   :amp<-velocity 0.0         ;
   :amp<-cc7 0.0              ;
   :xenv-attack 0.0           ;
   :xenv-decay1 0.0           ;
   :xenv-decay2 0.0           ;
   :xenv-release 0.0          ;
   :xenv-peak 1.0             ;
   :xenv-breakpoint 1.0       ;
   :xenv-sustain 1.0          ;
   :lfo2-freq 5.0             ;
   :lfo2-amp<-cca 0.0         ;
   :lfo2-amp<-ccb 0.0         ;
   :lfo2-amp<-xenv 0.0        ;
   :lfo3-freq 1.0             ;
   :lfo3-amp<-cca 0.0         ;
   :lfo3-amp<-ccb 0.0         ;
   :lfo3-amp<-xenv 0.0        ;
   :dry-amp 0                 ; Mix dry signal in db
   :dry-pan 0.0               ;
   :delay1-time 1.0           ;
   :delay1-time<-lfo2 0.0     ;
   :delay1-time<-lfo3 0.0     ;
   :delay1-time<-xenv 0.0     ;
   :delay1-fb  0.5            ;
   :delay1-xfb 0.0            ;
   :delay1-amp -60            ; Delay 1 amp in db
   :delay1-amp<-lfo2 0.0      ;
   :delay1-amp<-lfo3 0.0      ;
   :delay1-amp<-xenv 0.0      ;
   :delay1-pan -0.7           ;
   :delay1-pan<-lfo2 0.0      ;
   :delay1-pan<-lfo3 0.0      ;
   :delay1-pan<-xenv 0.0      ;
   :delay2-time 0.75          ;
   :delay2-time<-lfo2 0.0     ;
   :delay2-time<-lfo3 0.0     ;
   :delay2-time<-xenv 0.0     ;
   :delay2-fb 0.5             ;
   :delay2-xfb 0.0            ;
   :delay2-amp -60            ; Delay 2 amp in db
   :delay2-amp<-lfo2 0.0      ;
   :delay2-amp<-lfo3 0.0      ;
   :delay2-amp<-xenv 0.0      ;
   :delay2-pan 0.7            ;
   :delay2-pan<-lfo2 0.0      ;
   :delay2-pan<-lfo3 0.0      ;
   :delay2-pan<-xenv 0.0      ;
   :port-time 0.0             ;
   :port-time<-cc5 0.0        ;
   :pe-a0 0.0                 ; Pitceh envelope
   :pe-a1 0.0                 ;
   :pe-a2 0.0                 ;
   :pe-a3 0.0                 ;
   :pe-t1 1.0                 ;
   :pe-t2 1.0                 ;
   :pe-t3 1.0                 ;
   :pe<-cc9 0.0               ;
   :lfo1-freq 5.0             ; LFO 1
   :lfo1<-cca 0.0             ;
   :lfo1<-pressure 0.0        ;
   :lfo1-bleed 0.0            ;
   :lfo1-delay 0.0            ;
   :op1-amp 1.000             ; OP1 -------------------
   :op1-amp<-lfo1 0.0         ;    linear amp
   :op1-amp<-cca 0.0          ;
   :op1-amp<-ccb 0.0          ;
   :op1-amp<-velocity 0.0     ;
   :op1-amp<-pressure 0.0     ;
   :op1-keyscale-key 60       ;    MIDI key number
   :op1-keyscale-left 0       ;    Left keyscale db/oct    
   :op1-keyscale-right 0      ;    Right keyscale db/octave
   :op1-detune 1.000          ;    Pitch env depth
   :op1<-penv 0.0             ;
   :op1-attack 0.0            ;
   :op1-decay1 0.0            ;
   :op1-decay2 0.0            ;
   :op1-release 0.0           ;
   :op1-peak 1.0              ;
   :op1-breakpoint 1.0        ;
   :op1-sustain 1.0           ;
   :fm1-detune 1.0            ;    FM modulation ratio   
   :fm1-bias 0.0              ;
   :fm1-amp 1.0               ;    FM modulation depth
   :fm1<-env 1.0              ;
   :fm1-lag 0.00
   :fm1-keyscale-left 0       ;    FM keyscale db/oct
   :fm1-keyscale-right 0      ;
   :op2-amp 0.5               ; OP2 -------------------
   :op2-amp<-lfo1 0.0         ;
   :op2-amp<-cca 0.0          ;
   :op2-amp<-ccb 0.0          ;
   :op2-amp<-velocity 0.0     ;
   :op2-amp<-pressure 0.0     ;
   :op2-keyscale-key 60       ;
   :op2-keyscale-left 0       ;
   :op2-keyscale-right 0      ;
   :op2-detune 2.00           ;
   :op2<-penv 0.0             ;
   :op2-attack 0.00           ;
   :op2-decay1 0.0            ;
   :op2-decay2 0.0            ;
   :op2-release 0.0           ;
   :op2-peak 1.0              ;
   :op2-breakpoint 1.0        ;
   :op2-sustain 1.0           ;
   :fm2-detune 1.00           ;
   :fm2-bias 0.0              ;
   :fm2-amp 1.0               ;
   :fm2<-env 1.0              ;
   :fm2-lag 0.00
   :fm2-keyscale-left 0       ;
   :fm2-keyscale-right 0      ;
   :op3-amp 0.333             ; OP3 -------------------
   :op3-amp<-lfo1 0.0         ;
   :op3-amp<-cca 0.0          ;
   :op3-amp<-ccb 0.0          ;
   :op3-amp<-velocity 0.0     ;
   :op3-amp<-pressure 0.0     ;
   :op3-keyscale-key 60       ;
   :op3-keyscale-left 0       ;
   :op3-keyscale-right 0      ;
   :op3-detune 3.00           ;
   :op3<-penv 0.0             ;
   :op3-attack 0.00           ;
   :op3-decay1 0.0            ;
   :op3-decay2 0.0            ;
   :op3-release 0.0           ;
   :op3-peak 1.0              ;
   :op3-breakpoint 1.0        ;
   :op3-sustain 1.0           ;
   :fm3-detune 1.00           ;
   :fm3-bias 0.0              ;
   :fm3-amp 1.0               ;
   :fm3<-env 1.0              ;
   :fm3-lag 0.00
   :fm3-keyscale-left 0       ;
   :fm3-keyscale-right 0      ;
   :op4-amp 0.2500            ; OP4 -------------------
   :op4-amp<-lfo1 0.0         ;
   :op4-amp<-cca 0.0          ;
   :op4-amp<-ccb 0.0          ;
   :op4-amp<-velocity 0.0     ;
   :op4-amp<-pressure 0.0     ;
   :op4-keyscale-key 60       ;
   :op4-keyscale-left 0       ;
   :op4-keyscale-right 0      ;
   :op4-detune 4.00           ;
   :op4<-penv 0.0             ;
   :op4-attack 0.00           ;
   :op4-decay1 0.0            ;
   :op4-decay2 0.0            ;
   :op4-release 0.0           ;
   :op4-peak 1.0              ;
   :op4-breakpoint 1.0        ;
   :op4-sustain 1.0           ;
   :fm4-detune 1.00           ;
   :fm4-bias 0.0              ;
   :fm4-amp 1.0               ;
   :fm4<-env 1.0              ;
   :fm4-lag 0.00
   :fm4-keyscale-left 0       ;
   :fm4-keyscale-right 0      ;
   :op5-amp 0.2500            ; OP5 -------------------
   :op5-amp<-lfo1 0.0         ;
   :op5-amp<-cca 0.0          ;
   :op5-amp<-ccb 0.0          ;
   :op5-amp<-velocity 0.0     ;
   :op5-amp<-pressure 0.0     ;
   :op5-keyscale-key 60       ;
   :op5-keyscale-left 0       ;
   :op5-keyscale-right 0      ;
   :op5-detune 4.00           ;
   :op5<-penv 0.0             ;
   :op5-attack 0.00           ;
   :op5-decay1 0.0            ;
   :op5-decay2 0.0            ;
   :op5-release 0.0           ;
   :op5-peak 1.0              ;
   :op5-breakpoint 1.0        ;
   :op5-sustain 1.0           ;
   :fm5-detune 1.00           ;
   :fm5-bias 0.0              ;
   :fm5-amp 1.0               ;
   :fm5<-env 1.0              ;
   :fm5-lag 0.00
   :fm5-keyscale-left 0       ;
   :fm5-keyscale-right 0      ;
   :op6-amp 0.2500            ; OP6 -------------------
   :op6-amp<-lfo1 0.0         ;
   :op6-amp<-cca 0.0          ;
   :op6-amp<-ccb 0.0          ;
   :op6-amp<-velocity 0.0     ;
   :op6-amp<-pressure 0.0     ;
   :op6-keyscale-key 60       ;
   :op6-keyscale-left 0       ;
   :op6-keyscale-right 0      ;
   :op6-detune 4.00           ;
   :op6<-penv 0.0             ;
   :op6-attack 0.00           ;
   :op6-decay1 0.0            ;
   :op6-decay2 0.0            ;
   :op6-release 0.0           ;
   :op6-peak 1.0              ;
   :op6-breakpoint 1.0        ;
   :op6-sustain 1.0           ;
   :fm6-detune 1.00           ;
   :fm6-bias 0.0              ;
   :fm6-amp 1.0               ;
   :fm6<-env 1.0              ;
   :fm6-lag 0.00
   :fm6-keyscale-left 0       ;
   :fm6-keyscale-right 0      ;
   :bzz-amp 0.100             ; Buzz ------------------
   :bzz-amp<-lfo1 0.0         ;
   :bzz-amp<-cca 0.0          ;
   :bzz-amp<-ccb 0.0          ;
   :bzz-amp<-velocity 0.0     ;
   :bzz-amp<-pressure 0.0     ;
   :bzz-keyscale-key 60       ;
   :bzz-keyscale-left 0       ;
   :bzz-keyscale-right 0      ;
   :bzz-detune 1.00           ;
   :bzz<-penv 0.0             ;
   :bzz-attack 0.00           ;
   :bzz-decay1 0.0            ;
   :bzz-decay2 0.0            ;
   :bzz-release 0.0           ;
   :bzz-peak 1.0              ;
   :bzz-breakpoint 1.0        ;
   :bzz-sustain 1.0           ;
   :bzz-harmonics 16          ;    Harmonic count  
   :bzz-harmonics<-env 0.0    ;    Harmonic count modulation
   :bzz-harmonics<-cca 0.0    ;
   :bzz-hp-track 1.0          ;    HighPass filter keytrack
   :bzz-hp-track<-env 0.0     ;    Highpass filter modulation
   :nse-amp 1.000             ; Noise -----------------
   :nse-amp<-lfo1 0.0         ;
   :nse-amp<-cca 0.0          ;
   :nse-amp<-velocity 0.0     ;
   :nse-amp<-pressure 0.0     ;
   :nse-keyscale-key 60       ;
   :nse-keyscale-left 0       ;
   :nse-keyscale-right 0      ;
   :nse-detune 6.00           ;
   :nse-bw 10                 ;    Noise Band Width
   :nse<-penv 0.0             ;
   :nse-attack 0.00           ;
   :nse-decay1 0.0            ;
   :nse-decay2 0.0            ;
   :nse-release 1.0           ;
   :nse-peak 1.0              ;
   :nse-breakpoint 1.0        ;
   :nse-sustain 1.0           ;

   :nse2-amp 1.000
   :nse2-detune 1.000
   :nse2-bw 10
   :nse2-lag 0.00

   :filter-freq 10000         ; Filter ----------------
   :filter-track 0            ;
   :filter-res 0.0            ;
   :filter-attack 0.00        ;
   :filter-decay 0.00         ;
   :filter-sustain 1.0        ;
   :filter-release 0.0        ;
   :filter<-env 0.0           ;
   :filter<-pressure 0.0      ;
   :filter<-cca 0.0           ;
   :filter<-ccb 0.0           ;
   :filter-res<-cca 0.0       ;
   :filter-res<-ccb 0.0       ;
   :filter-mode -1.0          ;     -1.0 -> Lowpass   +1.0 -> Bandpass
   :filter2-detune 1.0        ;      Bandpass center freq relative to lowpass
   :filter2-lag 0.0
   :dist-mix 0.0              ; Distortion ------------ 
   :dist-pregain 1.0          ;      mix = 0 -> dry   mix = 1 -> wet
   :dist<-cca 0.0             ;
   :dist<-ccb 0.0             ;
   :op1-enable 1.0            ; Op enable flags
   :op2-enable 1.0            ;      0.0 -> disable   1.0 -> enable
   :op3-enable 1.0            ;
   :op4-enable 1.0            ;
   :op5-enable 1.0            ;
   :op6-enable 1.0            ;
   :nse-enable 0.0            ;
   :bzz-enable 0.0})

(def ignore-extra-parameters 
  [:nse-amp<-ccb])

(def cobalt-parameters (flatten 
                        [ignore-extra-parameters
                         (map first (seq initial-cobalt-program))]))
