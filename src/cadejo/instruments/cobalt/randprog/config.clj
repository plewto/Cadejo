
(ns cadejo.instruments.cobalt.randprog.config)

(def test-mode* (atom false))
(def verbose* (atom true))

; ---------------------------------------------------------------------- 
;                                   Gamut
;

(def p-gamut-harmonic* (atom 0.60))
(def p-gamut-semi-harmonic* (atom 0.30))
(def p-gamut-inharmonic* (atom 0.10))
(def p-gamut-cluster* (atom 0.3))
(def test-gamut* (atom [1 2 3 4 5 6  7 8 1]))

; ---------------------------------------------------------------------- 
;                                 Envelope Times
;

(def p-env-time-ultra-fast*  (atom 0.00))
(def p-env-time-extra-fast*  (atom 0.00))
(def p-env-time-fast*        (atom 0.10))
(def p-env-time-medium-fast* (atom 0.20))
(def p-env-time-medium*      (atom 0.20))
(def p-env-time-medium-slow* (atom 0.15))
(def p-env-time-slow*        (atom 0.10))
(def p-env-time-extra-slow*  (atom 0.10))
(def p-env-time-ultra-slow*  (atom 0.07))
(def p-env-alternate-time-span* (atom 0.40))


; ---------------------------------------------------------------------- 
;                              Envelope Styles
;

(def env-style-alternates* 
  (atom  {:adsr  [:addsr :aadsr :asr :perc :perc2]
          :addsr [:adsr  :aadsr :asr :perc :perc2]
          :aadsr [:adsr  :addsr :asr :perc :perc2]
          :asr   [:adsr  :perc  :perc2]
          :perc  (flatten [:adsr  :addsr (repeat 6 :perc2)])
          :perc2 (flatten [:adsr  :addsr (repeat 6 :perc)])
          :perc3 [:perc  :perc2]}))

(def p-env-style-asr    (atom 0.16))
(def p-env-style-adsr   (atom 0.16))
(def p-env-style-addsr  (atom 0.16))
(def p-env-style-aadsr  (atom 0.00))
(def p-env-style-perc   (atom 0.16))
(def p-env-style-perc2  (atom 0.00))
(def p-env-style-perc3  (atom 0.00))
(def p-env-alternate-style* (atom 0.10))


; ---------------------------------------------------------------------- 
;                              Pitch Envelope

(def p-use-pitch-env* (atom 0.1))
(def pitch-env-max-level* (atom 0.1))


; ---------------------------------------------------------------------- 
;                               Registration

;; Sets prop of partial amplitude scaling.
;; In general the further a partials frequency is from the peak 
;; frequency, the lower its amplitude is. There are several methods 
;; used to determin a partials amplitude, where
;;
;;    p = peak frequency
;;    f = partial frequency
;;    r = min(p/f, f/p) <= 1.0
;;    a = partal amplitude
;;
;; For r = 1 -> a = 1.
;; For r < 1
;;       linear  a = r
;;       exp     a = r^2
;;       rand    r/2 <= a <= 2r <= 1
;;
;;
(def p-registration-linear* (atom 0.55))
(def p-registration-exp*    (atom 0.35))
(def p-registration-rand*   (atom 0.10))

(def p-registration-zero*   (atom 0.10)) ; prop of zero amplitude
(def p-registration-peak*   (atom 0.10)) ; prop of high amplitude in addition to peak
(def test-registration* [1.000 0.500 0.250  0.200 0.167 0.142  0.125 0.111 0.100])


; ---------------------------------------------------------------------- 
;                                 Time base

(def sync-lfo-frequencies* (atom true))
(def p-lfo1-bleed* (atom 0.1))


; ---------------------------------------------------------------------- 
;                                 Operators

(def p-op-lfo* (atom 0.30))
(def p-op-cca* (atom 0.00))
(def p-op-ccb* (atom 0.00))
(def p-op-velocity* (atom 0.30))
(def p-op-pressure* (atom 0.00))
(def p-op-uniform-pitch-env* (atom 0.30))
(def p-op-use-pitch-env* (atom 0.3)) ;; if uniform-pitch-env is used this value is ignored
(def p-op-use-fm* (atom 0.5))
(def op-max-fm-depth* (atom 2))
(def op-max-pitch-env* (atom 0.1))

; ---------------------------------------------------------------------- 
;                                   Noise

(def noise-min-bw* (atom  10))
(def noise-max-bw* (atom  20))


; ---------------------------------------------------------------------- 
;                                   Buzz

(def buzz-max-harmonics* (atom 32))



; ---------------------------------------------------------------------- 
;                                  Filter

(def p-filter-bypass   (atom 0.20))
(def p-filter-lowpass  (atom 0.30))
(def p-filter-mixed    (atom 0.40))
(def p-filter-bandpass (atom 0.00))


(def p-filter-cca* (atom 0.3))
(def p-filter-ccb* (atom 0.0))
(def p-filter-pressure* (atom 0.0))
(def p-filter-hi-res (atom 0.2))

; ---------------------------------------------------------------------- 
;                                  Delays

(def delay-min-amp* (atom -60))
(def delay-max-amp* (atom -12))

(def p-delay1-flanger* (atom 0.0))
(def p-delay1-time<-lfo2* (atom 0.0))  ; ignored in flanger mode
(def p-delay1-time<-lfo3* (atom 0.00))
(def p-delay1-time<-xenv* (atom 0.00))  ; ignored in flanger mode
(def p-delay1-amp<-lfo* (atom 0.01))
(def p-delay1-amp<-xenv* (atom 0.5))
(def p-delay1-pan-mod* (atom 0.33))
(def p-delay1-xfb* (atom 0.5))

(def p-delay2-flanger* (atom 0.75)) 
(def p-delay2-time<-lfo2* (atom 0.0))  ; ignored in flanger mode
(def p-delay2-time<-lfo3* (atom 0.00))
(def p-delay2-time<-xenv* (atom 0.00))  ; ignored in flanger mode
(def p-delay2-amp<-lfo* (atom 0.01))
(def p-delay2-amp<-xenv* (atom 0.5))
(def p-delay2-pan-mod* (atom 0.33))
(def p-delay2-xfb* (atom 0.5))


; ---------------------------------------------------------------------- 
;                              General Values

(def p-use-portamento* (atom 0.0))
(def p-use-distortion* (atom 0.1))
