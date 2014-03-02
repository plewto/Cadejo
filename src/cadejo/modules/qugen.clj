(ns cadejo.modules.qugen
  "Defines a few useful quasi ugens"
  (:use [overtone.core]))

(def PI-2 (/ Math/PI 2))

(defmacro u+ [a b]
  `(overtone.sc.ugen-collide/+ ~a ~b))

(defmacro u- [a b]
  `(overtone.sc.ugen-collide/- ~a ~b))

(defmacro u* [a b]
  `(overtone.sc.ugen-collide/* ~a ~b))
  
(defmacro udiv [a b]
  `(overtone.sc.ugen-collide/binary-div-op ~a ~b))

(defmacro min-op [a b]
  `(overtone.sc.ugen-collide/min ~a ~b))

(defmacro max-op [a b]
  `(overtone.sc.ugen-collide/max ~a ~b))

;; mx            /----------------> a 
;;              /                    
;;             /                    
;;            /                     
;;           /                      
;; mn -------                       
;;                                  
;;

(defmacro clamp [a mn mx]
  "[a mn mx] A quasi ugen which returns the value a as long at mn<=a<=mx.
   For a<mn return mn, for a>mx return mx."
  `(min-op (max-op ~a ~mn) ~mx))

;; Return mix of wet/dry signals
;; mix = 0 ---> dry signal only
;; mix = 1 ---> wet signal only
;;
(defcgen efx-mixer [dry wet mix]
  (:ar
   (x-fade2 dry wet (- (* 2 (clamp mix 0 1)) 1))))

;; As efx-mixer except max wet signal is 50%
;; mix = 0 ---> dry signal only
;; mix = 1 ---> 50% wet/dry signal
;;
(defcgen efx-mixer2 [dry wet mix]
  (:ar
   (efx-mixer dry wet (* 2 mix))))

(defcgen amp-modulator-depth [signal depth]
  "Sets amplitude modulator depth at either :ir or :kr.
   signal - The modulating signal 
   depth  - Degree to which signal is present at the output.
            depth = 0.0 --> output constant 1.0
            depth = 1.0 --> output signal
            For intermediate depths output weighted average
            of 1 and signal"
  (:ir
   (+ (- 1 (clamp depth 0 1))
      (* depth signal)))
  (:kr
   (+ (- 1 (clamp depth 0 1))
      (* depth signal)))
  (:default :kr))



;; Return db amplitude scale as function of MIDI note number.
;; note        - The MIDI note number
;; right-key    - The right hand break-point (MIDI note number)
;; right-scale - The right hand scale factor. 
;;             - For every octave note is above right-key return 
;;             - right-scale db.
;; left-key     - The left hand break-point (MIDI note number)
;; left-scale  - The left hand scale factor. 
;;             - For every octave note is below left-key
;;             - return left-scale db.
;; The left and right scale values are summed.
;;
(defcgen keytrack [note left-key left-scale right-key right-scale ]
  (:ir (+ (* right-scale (max (/ (- right-key note) 12) 0))
          (* left-scale (max (/ (- note left-key) 12) 0))))
  (:kr (+ (* right-scale (max (/ (- right-key note) 12) 0))
          (* left-scale (max (/ (- note left-key) 12) 0))))
  (:default :ir))


;; Limit output to interval (gap, 1-gap) 
;; where 0 <= gap < 1
;; Result is always positive.
;;
(defcgen nonlin-limit [sig gap]
  (:kr
   (abs (+ gap (* (- 1 (* 2 gap))(distort sig))))))


;; Convert 'normalized' bi-polar signal (-1,+1) to uni-polar (0,+1)
;;
(defmacro bi->uni [sig]
  `(u+ 0.5 (u* 0.5 ~sig)))

;; Convert 'normalized' uni-polar signal (0,+1) to bi-polar (-1,+1)
;;
(defmacro uni->bi [sig]
  `(u* 2.0 (u- ~sig 0.5)))

;; Return 1 for x > b, 0 otherwise
;;
;; 1            +------------------> x
;;              |
;; 0 -----------o
;;              b
;;
(defmacro thresh-gate [x b]
  `(min-op (thresh ~x ~b), 1))


;; Return 1 for x < b, 0 otherwise
;;
;; 1 ------------o
;;               |
;; 0             +----------------> x
;;               b
;;
(defmacro !thresh-gate [x b]
  `(u- 1 (min-op (thresh ~x ~b) 1)))


;; Return 1 for b < x < c, 0 otherwise
;;
;; 1         +---------+
;;           |         |
;; 0 --------o         o---------> x
;;           b         c
;;
(defmacro square-window [x b c]
  `(u* (thresh-gate ~x ~b)
       (!thresh-gate ~x ~c)))


;; Returns a 1/2 cycle sine-shaped window for b < x < c, 0 otherwise. 
;; NOTE: 0 <= b and b < c must be true.
;; NOTE: Due to rounding errors the upper limit may be slightly negative.
;;       Use max if this is an issue.
;; f(x) = 0  for x <= b
;; f(x) = 1  for x = (b+c)/2
;; f(x) = 0  for x >= c
;;                _
;; 1             . .
;;              /   \
;;             /     \
;;            /       \
;;           /         \
;;          .           . 
;; 0 -------             -------------> x
;;         b             c           
;;
(defmacro sin-window [x b c]
  `(sin (min-op (u* (max-op (u- (thresh ~x ~b) ~b) 0)
                    (udiv Math/PI (u- ~c ~b)))
                Math/PI)))

;; Defines a 'shelf' curve using a cosine transition, where b < c
;; f(x)  = 1 for x <= b
;; f(x)  = 0 for x > c
;; The transition from 1 to 0 between f(b) and f(c) is shaped
;; as the 1st half cycle of a cosine.
;;
;; 1 -------- 
;;           `  
;;            \ 
;;             \ 
;;              \ 
;; 0             '----------------> x
;;          b    c                  
;;                                 
;; 
(defmacro cos-shelf [x b c]
  `(u* (u+ (cos (min-op (u* (max-op (u- (thresh ~x ~b) ~b) 0)
                            (udiv Math/PI (u- ~c ~b)))
                        Math/PI)) 
           1)
       0.5))

;; inverse of cos-shelf
;; f(x) = 0 for x <= b
;; f(x) = 1 for x  > c
;;                                   
;; 1           .-------------------> x 
;;            /                       
;;           /                        
;;          /                         
;;         /
;;        ,
;; 0 -----                           
;;       b    c                       
;;                                   
(defmacro !cos-shelf [x b c]
  `(u- 1 (cos-shelf ~x ~b ~c)))
