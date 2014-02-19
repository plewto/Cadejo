(ns cadejo.modules.env
  (:use overtone.core))

;; ISSUE BUGGY!
;;                                                                         .
;;  1.0 ---------------  (bp,1)                                            .
;;                     \                                                   .
;;                      \                                                  .
;;                       \                                                 .
;;                        \                                                .
;;                         \                                               .
;;  s                       \ 1(,s)                                        .
;;                                                                         .
;;                                                                         .
;;                                                                         .
;;  0                   bp   1                                             .
;;       velocity --->                                                     .
;;                                                                         .
;;
;; Calculate time-scale factor as function of velocity.
;; Velocity "normalized" 0 <= velocity <= 1
;; For velocity <= break-point return 1.0
;; For break-point < velocity <= 1 return point on line (bp,1)(1,s)
;;  
;; velocity      0 <= velocity <= 1
;; break-point   0 <= break-point <= 1
;; scale         0 < scale < 1
;;
(defcgen velocity->tscale [velocity break-point scale]
  (:ir
   (min (+ (* (/ (- break-point 1) scale)
              velocity)
           (- scale (/ (- break-point 1) scale)))
        1.0)))


;;                                                                         .
;;                  a2                                                     .
;;                 /\                                                      .
;;                /  \                                                     .
;; gate          /    \                                gate                .
;; hi           /      \                               low                 .
;;             /        \                                                  .
;;            /          \                                                 .
;;           /            \                               a4               .
;;          /              -------------      ------------                 .
;; a0      /               a3                             \                .
;; \      /                                                \               .
;;  \    /                                                  \              .
;;   \  /                                                    \             .
;;    \/                                                      \ a5         .
;;    a1                                                                   .
;; t1      t2     t3                t4                   t5                .
;;                                                                         .

(defcgen env6 [a0 a1 a2 a3 a4 a5 t1 t2 t3 t4 t5 release-node loop-node gate]
  (:kr
   (env-gen:kr (envelope [a0 a1 a2 a3 a4 a5]
                         [t1 t2 t3 t4 t5]
                         :linear
                         release-node loop-node)
               :gate gate
               :action NO-ACTION)))




;;                                                                         .
;; gate                                   gate                             .
;; hi                                     low                              .
;;                                                                         .
;;              /-------          -----------\                             .
;;             /                              \                            .
;;            /                                \                           .
;;           /                                  \                          .
;;          /                                    \                         .
;;  --------                                      \                        .
;;  delay    delay                          release


(defcgen delay-env [delay release gate]
  (:kr
   (env-gen:kr (envelope [0 0 1 1 0]
                         [delay delay 0 release]
                         :linear 3)
               :gate gate
               :action NO-ACTION)))

;;                                                                         .
;;                                                                         .
;;                 /\             hold                                     .
;;                /  \decay1  /-----------\ sustain                        .
;;               /    \      /             \                               .
;;              /      \    /decay2         \                              .
;;             /        \  /                 \                             .
;;     attack /          \/                   \ release                    .
;;           /         break-point             \                           . 
;;          /                                   \                          .
;;         /                                     \                         .
;;        /                                       \                        .
;;       /                                         \                       .
;;      /                                           \                      .
;;                                                                         .
;;                                                                         .
;;                                                                         .
;;                                                                         .

(defcgen addsr [attack decay-1 decay-2 release breakpoint sustain gate]
  (:kr
   (env-gen:kr (envelope [0 1 breakpoint sustain sustain 0]
                         [attack decay-1 decay-2 0 release]
                         :linear
                         4)
               :gate gate
               :action NO-ACTION)))
