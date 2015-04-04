;; Provides editor for ADDSR2 style envelope.
;; TODO: 1) Add clipboard
;;       2) Add initialization function
;;       3) Add randomization function
;;       
(ns cadejo.ui.addsr2-editor
  (:require [cadejo.ui.instruments.subedit :as subed])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.rectangle :as rect])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.util.math :as sgwr-math])
  (:require [seesaw.core :as ss])
  (:import java.awt.event.MouseMotionListener
           java.awt.event.MouseListener))

(def ^:private width 400)
(def ^:private height 300)
(def ^:private fixed-sustain-time 2)

(defn- get-data-map [performance]
  (.current-data (.bank performance)))

;; attack time = t1 - t0                                                   .
;; decay1 time = t2 - (sum t0 t1)                                          .
;; decay2 time = t3 - (sum to t1 t2)                                       .
;; sustain time = constant t4 - t3                                         .
;; release time = t5 - t5 - t4                                             .
;;                                                                         .
;; peak - amplitude after t0 seconds                                       .
;; breakpoint "bp" - amplitude after tt+t2 seconds                         .
;; sustain - amplitude after t1+t2+t3 seconds                              .
;;                                                                         .
;;                          s3                                             .
;;                  .-------------------. sustain                          .
;;                 /                     \                                 . 
;;                /                       \                                .
;;   peak        /                         \                               .
;;      /\    s2/                        s4 \                              .
;;     /  \    /                             \                             .
;;  s0/    \  /                               \                            .
;;   /   s1 \/                                 \                           .
;;  /         breakpoint                        \                          .
;; /                                             \                         .
;; t0  t1   t2    t3                   t4        t5                        .
;; 
;; Variable parameters: attack-time, decay1-time, decay2-time, release-time
;;                       peak, breakpoint, sustain
;; constants: p0 = [t0, 0] = [0 0]
;;            sustain-time
;;            p5 = [? 0]
;; 





;; Provides sgwr GUI editor for addsr2 style envelope
;; See cadejo.modules.env
;; (addsr2-editor env-id ied & {:keys [w h group-view*]})
;;
;; params - Map, defines mapping between generic envelope parameters and 
;;          specific synth parameters. The map MUST define values for the 
;;          following keys  
;;              :attack :decay1 :decay2 :release :peak :breakpoint & :sustain
;;                    
;; env-id - keyword, a name for the client envelope, env-id is used for 
;;          informational purposes only.
;; ied    - InstrumentEditor, an instance of 
;;          cadejo.ui.instruments.instrument-editor/InstrumentEditor
;; :w     - int, drawing width, default 400
;; :h     - int, height, drawing height, default 300
;; :group-view* - Atom holding vector of two points  [[x0 y0][x1 y1]]
;;                where x0 < x1 and y0 < y1. These points define the 
;;                current view or zoom level of the envelope drawing.
;;                Multiple related envelope editors which share the same 
;;                group-view will display the same zoom level.         
;;
;; Returns an instance of cadejo.ui.instruments.subedit/InstrumentSubEditor
;;         
(defn addsr2-editor [params env-id ied & {:keys [w h group-view*]
                                          :or {w width
                                               h height
                                               group-view* (atom [[-0.1 -0.1][8.0 1.1]])
                                               }}]
  (let [param-attack (:attack params)
        param-decay1 (:decay1 params)
        param-decay2 (:decay2 params)
        param-release (:release params)
        param-peak (:peak params)
        param-breakpoint (:breakpoint params)
        param-sustain (:sustain params)
        drw (sgwr.components.drawing/cartesian-drawing w h [-0.1 -0.1][8.0 1.1])
        root (.root drw)
        performance (.parent-performance ied)
        segment-color (lnf/envelope-segment)
        selected-color (lnf/envelope-selected)
        handle-color (lnf/envelope-handle)
        selected-handle selected-color
        ;; Temp values
        [t0 t1 t2 t3 t4 t5][0.0 2.0 4.0 5.0 (+ 5.0 fixed-sustain-time)(+ 5.0 fixed-sustain-time)]
        [a0 a1 a2 a3 a4 a5][0.0 0.3 0.8 1.0 1.0 0.0]
        s0 (line/line root [t0 a0][t1 a1] :color segment-color)
        s1 (line/line root [t1 a1][t2 a2] :color segment-color)
        s2 (line/line root [t2 a2][t3 a3] :color segment-color)
        s3 (line/line root [t3 a4][t4 a4] :color segment-color :style :dotted)
        s4 (line/line root [t4 a4][t5 a5] :color segment-color)
        segments [s0 s1 s2 s3 s4]
        p0 (point/point root [t0 a0] :color handle-color :style [:diag :diag2] :size 3)
        p1 (point/point root [t1 a1] :color handle-color :style [:dot] :size 3)
        p2 (point/point root [t2 a2] :color handle-color :style [:dot] :size 3)
        p3 (point/point root [t3 a3] :color handle-color :style [:dot] :size 3)
        p4 (point/point root [t4 a4] :color handle-color :style [:dot] :size 3)
        p5 (point/point root [t5 a5] :color handle-color :style [:dot] :size 3)
        points [p1 p2 p3 p4 p5]
        current-point* (atom nil)
        sync-fn (fn []
                  (let [dmap (get-data-map performance)
                        att (param-attack dmap)
                        dcy1 (param-decay1 dmap)
                        dcy2 (param-decay2 dmap)
                        rel (param-release dmap)
                        peak (param-peak dmap)
                        brkpnt (param-breakpoint dmap)
                        sus (param-sustain dmap)
                        t0 0
                        t1 (+ t0 att)
                        t2 (+ t1 dcy1)
                        t3 (+ t2 dcy2)
                        t4 (+ t3 fixed-sustain-time)
                        t5 (+ t4 rel)]
                    (.set-points! p1 [[t1 peak]])
                    (.set-points! p2 [[t2 brkpnt]])
                    (.set-points! p3 [[t3 sus]])
                    (.set-points! p4 [[t4 sus]])
                    (.set-points! p5 [[t5 0.0]])
                    (.set-points! s0 [[0 0][t1 peak]])
                    (.set-points! s1 [[t1 peak][t2 brkpnt]])
                    (.set-points! s2 [[t2 brkpnt][t3 sus]])
                    (.set-points! s3 [[t3 sus][t4 sus]])
                    (.set-points! s4 [[t4 sus][t5 0.0]])
                    (.render drw)))
        sync-to-view (fn []
                       (.set-view drw @group-view*))
                      
        ]
        ;; Set adjacent point segments
    (doseq [obj [s0 s1 s2 s3 s4]]
      (.color! obj :default segment-color)
      (.color! obj :selected selected-color))
    (doseq [obj [p1 p2 p3 p4 p5]]
      (.color! obj :default handle-color)
      (.color! obj :selected selected-handle))
    (.put-property! p1 :adjacent [s0 s1])
    (.put-property! p1 :time-param param-attack)
    (.put-property! p1 :level-param param-peak)
    (.put-property! p2 :adjacent [s1 s2])
    (.put-property! p2 :time-param param-decay1)
    (.put-property! p2 :level-param param-breakpoint)
    (.put-property! p3 :adjacent [s2 s3])
    (.put-property! p3 :time-param param-decay2)
    (.put-property! p3 :level-param param-sustain)
    (.put-property! p4 :adjacent [s3 s4])
    (.put-property! p4 :time-param nil)
    (.put-property! p4 :level-param param-sustain)
    (.put-property! p5 :adjacent [s4])
    (.put-property! p5 :time-param param-release)
    (.put-property! p5 :level-param nil)
    (doseq [s segments]
      (.color! s :default segment-color)
      (.color! s :selected selected-color))
    (.background! drw (lnf/envelope-background))
    ;; Reference-lines
    (line/line root [0.0 -0.05][0.0 1.05] :color (lnf/envelope-border))
    (line/line root [-0.05 0.0][200.0 0.0] :color (lnf/envelope-border))
    (line/line root [0.0 1.0][200.0 1.0] :color (lnf/envelope-border) :style :dotted)
    (doseq [t (range 0 64 1)]
      (point/point root [t 0.0] :style [:bar] :size 3 :color (lnf/envelope-border)))
    (.use-attributes! root :default)
    
    (.add-mouse-listener drw (proxy [MouseListener][]
                               (mouseExited [_]
                                 (.use-attributes! root :default)
                                 (.render drw))
                               (mouseEntered [_])
                               (mouseClicked [_])
                               (mousePressed [_])
                               (mouseReleased [_])))
    (.add-mouse-motion-listener drw (proxy [MouseMotionListener][]
                                      (mouseMoved [ev]
                                        (let [[x y](.mouse-where drw)
                                              d* (atom 1e6)]
                                          (reset! current-point* nil)
                                          (doseq [s segments](.use-attributes! s :default))
                                          (doseq [p points]
                                            (.use-attributes! p :default)
                                            (let [pos (first (.points p))
                                                  dp (sgwr-math/distance [x y] pos)]
                                              (if (< dp @d*)
                                                (do
                                                  (reset! d* dp)
                                                  (reset! current-point* p)))))
                                          (doseq [p (cons @current-point* (.get-property @current-point* :adjacent))]
                                            (.use-attributes! p :selected))
                                          (.render drw)
                                          (.status! ied (format "env [time %5.3f  level %5.3f]" x y))))

                                      (mouseDragged [ev]
                                        (let [cp @current-point*
                                              dmap (get-data-map performance)
                                              attack (param-attack dmap)
                                              decay1 (param-decay1 dmap)
                                              decay2 (param-decay2 dmap)
                                              release (param-release dmap)
                                              t0 0.0 
                                              t1* (atom (+ t0 attack))
                                              t2* (atom (+ @t1* decay1))
                                              t3* (atom (+ @t2* decay2))
                                              t4* (atom (+ @t3* fixed-sustain-time))
                                              t5* (atom (+ @t4* release))
                                              peak* (atom (param-peak dmap))
                                              breakpoint* (atom (param-breakpoint dmap))
                                              sustain* (atom (param-sustain dmap))
                                              update-curve (fn []
                                                             (.set-points! p1 [[@t1* @peak*]])
                                                             (.set-points! p2 [[@t2* @breakpoint*]])
                                                             (.set-points! p3 [[@t3* @sustain*]])
                                                             (.set-points! p4 [[@t4* @sustain*]])
                                                             (.set-points! p5 [[@t5* 0.0]])
                                                             (.set-points! s0 [[0 0][@t1* @peak*]])
                                                             (.set-points! s1 [[@t1* @peak*][@t2* @breakpoint*]])
                                                             (.set-points! s2 [[@t2* @breakpoint*][@t3* @sustain*]])
                                                             (.set-points! s3 [[@t3* @sustain*][@t4* @sustain*]])
                                                             (.set-points! s4 [[@t4* @sustain*][@t5* 0.0]])
                                                             (.render drw))]
                                          (cond (= cp p1) ; attack time / peak level
                                                (let [[att pk](.mouse-where drw)]
                                                  (reset! t1* (float (max 0 att)))
                                                  (reset! t2* (+ @t1* decay1))
                                                  (reset! t3* (+ @t2* decay2))
                                                  (reset! t4* (+ @t3* fixed-sustain-time))
                                                  (reset! t5* (+ @t4* release))
                                                  (reset! peak* (float (min 1.0 (max 0.0 pk))))
                                                  (.set-param! ied param-attack att)
                                                  (.set-param! ied param-peak @peak*)
                                                  (.status! ied (format "%s attack %5.3f   peak level %5.3f" env-id att @peak*))
                                                  (update-curve))

                                                (= cp p2) ; decay1 time / breakpoint level
                                                (let [[x y](.mouse-where drw)
                                                      att @t1*
                                                      pk @peak*
                                                      dcy-1 (float (max 0 (- x att)))
                                                      bp (float (min 1.0 (max 0.0 y)))]
                                                  (reset! t2* (+ @t1* dcy-1))
                                                  (reset! t3* (+ @t2* decay2))
                                                  (reset! t4* (+ @t3* fixed-sustain-time))
                                                  (reset! t5* (+ @t4* release))
                                                  (reset! breakpoint* bp)
                                                  (.set-param! ied param-decay1 dcy-1)
                                                  (.set-param! ied param-breakpoint bp)
                                                  (.status! ied (format "%s decay-1 %5.3f   breakpoint %5.3f" env-id dcy-1 bp))
                                                  (update-curve))

                                                (= cp p3) ; decay2 time / sustain level
                                                (let [[x y](.mouse-where drw)
                                                      dcy-2 (float (max 0 (- x @t2*)))
                                                      sus (float (min 1.0 (max 0.0 y)))]
                                                  (reset! t3* (+ @t2* dcy-2))
                                                  (reset! t4* (+ @t3* fixed-sustain-time))
                                                  (reset! t5* (+ @t4* release))
                                                  (reset! sustain* sus)
                                                  (.set-param! ied param-decay2 dcy-2)
                                                  (.set-param! ied param-sustain sus)
                                                  (.status! ied (format "%s decay-2 %5.3f   sustain %5.3f" env-id dcy-2 sus))
                                                  (update-curve))

                                                (= cp p4) ; sustain level
                                                (let [[_ y](.mouse-where drw)
                                                      sus (float (min 1.0 (max 0.0 y)))]
                                                  (reset! sustain* sus)
                                                  (.set-param! ied param-sustain sus)
                                                  (.status! ied (format "%s sustain %5.3f" env-id sus))
                                                  (update-curve))

                                                (= cp p5) ; release time
                                                (let [[x _](.mouse-where drw)
                                                      rel (float (max 0 (- x @t4*)))]
                                                  (reset! t5* (+ @t4* rel))
                                                  (.set-param! ied param-release rel)
                                                  (.status! ied (format "%s release %5.3f" env-id rel))
                                                  (update-curve))

                                                :default
                                                (.warning! ied "Internal Error addsr2-envelope-editor mouse-motion-listener") )))))
    
    (let [widget-map {:drawing drw
                      :sync-to-view sync-to-view
                      :canvas (.canvas drw)}]
      (reify subed/InstrumentSubEditor
        (widgets [this] widget-map)
        (widget [this key] (get widget-map key))
        (parent [this] ied)
        (parent! [this _] nil) ; ignore
        (status! [this msg](.status! (.parent this) msg))
        (warning! [this msg](.warning! (.parent this) msg))
        (set-param! [this param val](.set-param! (.parent this) param val))
        (init! [this] nil) ;; not implemented
        (sync-ui! [this] (sync-fn))))))
          
