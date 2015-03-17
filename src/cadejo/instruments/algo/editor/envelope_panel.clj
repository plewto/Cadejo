(ns cadejo.instruments.algo.editor.envelope-panel 
  (:use [cadejo.instruments.algo.algo-constants])
  ;;(:require [cadejo.instruments.algo.editor.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.math :as math])
  (:require [sgwr.components.drawing :as drw])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.point :as point])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [sgwr.util.color :as uc])
  (:require [sgwr.util.math :as sgwr-math])
  (:require [seesaw.core :as ss])
  (:import javax.swing.Box
           java.awt.event.MouseMotionListener
           java.awt.event.MouseListener))

;; buttons:
;; gate preset
;; percussion preset
;; ADSSR preset
;; dice
;; copy
;; paste
;; zoom-in
;; zoom-out
;; zoom-restore

;; op - operator number [1,2,3,...,8] or :pitch for pitch env

(def width 580)
(def height 400)
(def toolbar-height 60)
(def ^:private current-view* (atom default-envelope-view))

(defn- invertable? [op]
  (or (= op 2)(= op 3)(= op 5)(= op 6)(= op 8)))

(defn- param-key [n param]
  (if (keyword? n)
    (keyword (format "env1-%s" param))
    (keyword (format "op%d-%s" n param))))

(defn- get-data-map [performance]
  (.current-data (.bank performance)))

(defn- preset-gate-env [op ied]
  (let [param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        param-bias (param-key op "bias")
        param-scale (param-key op "scale")
        dmap {param-attack 0.0
              param-decay1 0.2
              param-decay2 0.2
              param-release 0.0
              param-breakpoint 1.0
              param-sustain 1.0
              param-bias 0.0
              param-scale 1.0}]
    (doseq [[p v](seq dmap)] (.set-param! ied p v))))


(defn- preset-percussion-env [op ied]
  (let [param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        param-bias (param-key op "bias")
        param-scale (param-key op "scale")
        dmap {param-attack 0.0
              param-decay1 0.1
              param-decay2 1.0
              param-release 2.0
              param-breakpoint 0.8
              param-sustain 0.0
              param-bias 0.0
              param-scale 1.0}]
    (doseq [[p v](seq dmap)] (.set-param! ied p v))))

(defn- preset-adsr-env [op ied]
  (let [param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        param-bias (param-key op "bias")
        param-scale (param-key op "scale")
        dmap {param-attack 0.05
              param-decay1 0.25
              param-decay2 1.4
              param-release 0.2
              param-breakpoint 0.9
              param-sustain 0.85
              param-bias 0.0
              param-scale 1.0}]
    (doseq [[p v](seq dmap)] (.set-param! ied p v))))

(defn- env-dice [op ied]
  (let [time-scale (math/pick '[1.0 1.0 1.0 1.0 2.0 2.0 4.0 8.0])
        param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        param-bias (param-key op "bias")
        param-scale (param-key op "scale")
        att (rand time-scale)
        dcy1 (rand time-scale)
        dcy2 (rand time-scale)
        rel (rand time-scale)
        bp (math/coin 0.5 (math/rand-range 0.5 1.0)(rand))
        sus (math/coin 0.5 (math/rand-range 0.5 1.0)(rand))
        dmap {param-attack att
              param-decay1 dcy1
              param-decay2 dcy2
              param-release rel
              param-breakpoint bp
              param-sustain sus}]
    (doseq [[p v](seq dmap)] (.set-param! ied p v))))


(def ^:private clipboard* (atom {:attack 0.0
                                 :decay1 0.2
                                 :decay2 0.2
                                 :release 0.0
                                 :breakpoint 1.0
                                 :sustain 1.0}))

(defn- copy-env [op ied]
  (let [dmap (get-data-map (.parent-performance ied))
        param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")]
    (reset! clipboard* {:attack (param-attack dmap)
                        :decay1 (param-decay1 dmap)
                        :decay2 (param-decay2 dmap)
                        :release (param-release dmap)
                        :breakpoint (param-breakpoint dmap)
                        :sustain (param-sustain dmap)})))


(defn- paste-env [op ied]
 (let [clip @clipboard*
       param-attack (param-key op "attack")
       param-decay1 (param-key op "decay1")
       param-decay2 (param-key op "decay2")
       param-release (param-key op "release")
       param-breakpoint (param-key op "breakpoint")
       param-sustain (param-key op "sustain")]
   (.set-param! ied param-attack (:attack clip))
   (.set-param! ied param-decay1 (:decay1 clip))
   (.set-param! ied param-decay2 (:decay2 clip))
   (.set-param! ied param-release (:release clip))
   (.set-param! ied param-breakpoint (:breakpoint clip))
   (.set-param! ied param-sustain (:sustain clip))))


(defn- env-toolbar [op ied callback]
  (let [param-bias (if (= op :pitch) :env1-bias (param-key op "env-bias"))
        param-scale (if (= op :pitch) :env1-scale (param-key op "env-scale"))
        param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        drw (let [d (drw/native-drawing width toolbar-height)]
              (.background! d (lnf/envelope-background))
              d)
        root (.root drw)
        tools (.tool-root drw)
        [x0 y0][0 toolbar-height]
        x-gap 30
        x-space 50
        x-gate (+ x0 x-gap)
        x-percussion (+ x-gate x-space)
        x-adsr (+ x-percussion x-space)
        x-dice (+ x-adsr x-space)
        x-copy (+ x-dice x-space)
        x-paste (+ x-copy x-space)
        x-zoom-in (+ x-paste x-space)
        x-zoom-out (+ x-zoom-in x-space)
        x-zoom-restore (+ x-zoom-out x-space)
        x-invert (+ x-zoom-restore x-space)
        y-buttons (- y0 50)
        pan-main (ss/vertical-panel :items [(.canvas drw)]
                                    :background (lnf/background))
        invert-action (fn [cb _]
                        (if (msb/checkbox-selected? cb)
                          (do
                            (.set-param! ied param-bias 1.0)
                            (.set-param! ied param-scale -1.0)
                            (.status! ied (format "Invert envelope %s" op)))
                          (do
                            (.set-param! ied param-bias 0.0)
                            (.set-param! ied param-scale 1.0)
                            (.status! ied (format "Envelope %s normal" op)))))
        ;; cb-style (lnf/checkbox)
        ;; cb-invert (if (invertable? op)
        ;;             (msb/checkbox tools [x-invert (+ y-buttons 15)] "Invert" 
        ;;                           :id :invert-env
        ;;                           :rim-radius (:rim-radius cb-style)
        ;;                           :rim-color (:rim-color cb-style)
        ;;                           :selected-check [(:check-color cb-style)(:check-style cb-style)(:check-size cb-style)]
        ;;                           :click-action invert-action
        ;;                           :text-color (lnf/text-color)))
        cb-invert (if (invertable? op)
                    (sfactory/checkbox drw [x-invert (+ y-buttons 15)] :invert-env "Invert" invert-action))
        sync-fn (fn [dmap]
                  (if cb-invert
                    (let [bias (get dmap param-bias 0.0)]
                      (msb/select-checkbox! cb-invert (pos? bias) false)
                      (.render drw))))]
    (sfactory/env-button drw [x-gate y-buttons] :gate callback)
    (sfactory/env-button drw [x-percussion y-buttons] :percussion callback)
    (sfactory/env-button drw [x-adsr y-buttons] :adsr callback)
    (sfactory/dice-button drw [x-dice y-buttons] :env-dice callback)
    (sfactory/copy-button drw [x-copy y-buttons] :env-copy callback)
    (sfactory/paste-button drw [x-paste y-buttons] :env-paste callback)
    (sfactory/zoom-in-button drw [x-zoom-in y-buttons] :env-zoom-in callback)
    (sfactory/zoom-out-button drw [x-zoom-out y-buttons] :env-zoom-out callback)
    (sfactory/zoom-restore-button drw [x-zoom-restore y-buttons] :env-zoom-restore callback)
    (.background! drw (lnf/background))
    (.render drw)
    {:pan-main pan-main
     :drawing drw
     :sync-fn sync-fn}))

(defn envelope-panel [op ied]
  (let [param-attack (param-key op "attack")
        param-decay1 (param-key op "decay1")
        param-decay2 (param-key op "decay2")
        param-release (param-key op "release")
        param-breakpoint (param-key op "breakpoint")
        param-sustain (param-key op "sustain")
        zoom-ratio 1.5
        performance (.parent-performance ied)
        drw (let [d (drw/cartesian-drawing width height (first default-envelope-view)(second default-envelope-view))]
              (.background! d (lnf/envelope-background))
              d)
        root (.root drw)
        tools (.tool-root drw)
        major (fn [p0 p1]
                (line/line root p0 p1
                           :id :env-axis
                           :color (lnf/major-tick)
                           :style :solid))
        minor (fn [p0 p1]
                (line/line root p0 p1
                           :id :env-axis
                           :color (lnf/minor-tick)
                           :style :dotted))
        segment (fn [id p0 p1 sty]
                  (let [seg (line/line root p0 p1
                                       :id id
                                       :style sty
                                       :width 1.0
                                       :color (lnf/envelope-segment))]
                    (.color! seg :selected (lnf/envelope-selected))
                    (.color! seg :default (lnf/envelope-segment))
                    seg))
        seg-a (segment :a [0 0] [1 1] :solid)
        seg-b (segment :b [1 1][2 0.7] :solid)
        seg-c (segment :c [2 0.7][4 0.5] :solid)
        seg-d (segment :d [4 0.5][6 0.5] :dash)
        seg-e (segment :e [6 0.5][7 0.0] :solid)
        segments [seg-a seg-b seg-c seg-d seg-e]
        point (fn [id pos adjacent] 
                (let [c1 (uc/color (lnf/envelope-handle))
                      c2 (uc/inversion c1)
                      p (point/point root pos :id id 
                                     :color c1
                                     :style [:dot]
                                     :size 3)]
                  (.put-property! p :adjacent adjacent)
                  (.color! p :default c1)
                  (.color! p :selected c2)
                  p))
        p1 (point :p1 [1 1] [seg-a seg-b])
        p2 (point :p2 [2 0.7] [seg-b seg-c])
        p3 (point :p3 [4 0.5] [seg-c seg-d])
        p4 (point :p4 [6 0.5] [seg-d seg-e])
        p5 (point :p5 [7 0.0] [seg-e])
        points [p1 p2 p3 p4 p5]
        current-point* (atom nil)
        disable-fn (fn [] )
        enable-fn (fn [] )
        toolbar* (atom nil)
        txt-view (text/text root [0.1 -0.04] "View: xxx" 
                            :lock-size true
                            :color (lnf/text)
                            :style :mono
                            :size 6)
        sync-fn (fn []
                  (let [dmap (get-data-map performance)
                        att (param-attack dmap)
                        dcy1 (param-decay1 dmap)
                        dcy2 (param-decay2 dmap)
                        rel (param-release dmap)
                        bp (param-breakpoint dmap)
                        sus (param-sustain dmap)
                        t0 0.0
                        t1 (+ t0 att)
                        t2 (+ t1 dcy1)
                        t3 (+ t2 dcy2)
                        t4 (+ t3 2)
                        t5 (+ t4 rel)]
                    (.set-points! p1 [[t1 1.0]])
                    (.set-points! p2 [[t2 bp]])
                    (.set-points! p3 [[t3 sus]])
                    (.set-points! p4 [[t4 sus]])
                    (.set-points! p5 [[t5 0.0]])
                    (.put-property! p1 :adjacent [seg-a seg-b])
                    (.put-property! p2 :adjacent [seg-b seg-c])
                    (.put-property! p3 :adjacent [seg-c seg-d])
                    (.put-property! p4 :adjacent [seg-d seg-e])
                    (.set-points! seg-a [[0 0][t1 1.0]])
                    (.set-points! seg-b [[t1 1.0][t2 bp]])
                    (.set-points! seg-c [[t2 bp ][t3 sus]])
                    (.set-points! seg-d [[t3 sus][t4 sus]])
                    (.set-points! seg-e [[t4 sus][t5 0]])
                    (.put-property! txt-view :text (format "View: %6.3f seconds"
                                                           (first (second @current-view*))))
                    ((:sync-fn @toolbar*) dmap)
                    (.set-view drw @current-view*)))
        toolbar-callback (fn [b _]
                           (let [id (.get-property b :id)]
                             (cond (= id :gate)
                                   (do (preset-gate-env op ied)
                                       (sync-fn)
                                       (.status! ied (format "Using gate preset, Envelope %s" op)))
                                   
                                   (= id :percussion)
                                   (do (preset-percussion-env op ied)
                                       (sync-fn)
                                       (.status! ied (format "Using percussion preset, Envelope %s" op)))

                                   (= id :adsr)
                                   (do (preset-adsr-env op ied)
                                       (sync-fn)
                                       (.status! ied (format "Using ADSR preset, Envelope %s" op)))

                                   (= id :env-dice)
                                   (do (env-dice op ied)
                                       (sync-fn)
                                       (.status! ied (format "Randomized Envelope %s" op)))

                                   (= id :env-copy)
                                   (do (copy-env op ied)
                                       (.status! ied (format "Envelope %s copied to clipboard" op)))

                                   (= id :env-paste)
                                   (do (paste-env op ied)
                                       (sync-fn)
                                       (.status! ied (format "Clipboard pasted to Envelope %s" op)))
                                     
                                   (= id :env-zoom-in)
                                   (let [[p0 p1] @current-view*
                                         [x1 y1] p1
                                          x2 (/ x1 zoom-ratio)]
                                     (reset! current-view* [p0 [x2 y1]])
                                     (sync-fn)
                                     (.status! ied (format "Env %s zoom-in  %s seconds" op x2)))

                                   (= id :env-zoom-out)
                                   (let [[p0 p1] @current-view*
                                         [x1 y1] p1
                                         x2 (* x1 zoom-ratio)]
                                     (reset! current-view* [p0 [x2 y1]])
                                     (sync-fn)
                                     (.status! ied (format "Env %s zoom-out  %s seconds" op x2)))

                                   (= id :env-zoom-restore)
                                   (do (reset! current-view* default-envelope-view)
                                       (sync-fn)
                                       (.status! ied (format "Env %s restore-view  %s seconds" op (first (second default-envelope-view)))))

                                   :default
                                   (println "env-toolbar calllback id is " id))))
        toolbar (env-toolbar op ied toolbar-callback)
     
        pan-main (let [tb (env-toolbar op ied toolbar-callback)]
                   (reset! toolbar* tb)
                   (ss/border-panel :north (Box/createVerticalStrut 170)
                                    :center (ss/vertical-panel :items [(.canvas drw)
                                                                       (:pan-main tb)])
                                    :east (Box/createHorizontalStrut 100)
                                    :south (Box/createVerticalStrut 16)
                                    :background (lnf/background)))]
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
                                          (doseq [p (cons @current-point* (.get-property @current-point* :adjacent ))]
                                            (.use-attributes! p :selected))
                                          (.render drw)
                                          (.status! ied (format "env [time %5.3f  amp %5.3f]" x y))))
                                      (mouseDragged [ev]
                                        (let [cp @current-point*
                                              dmap (get-data-map performance)
                                              att (param-attack dmap)
                                              dcy1 (param-decay1 dmap)
                                              dcy2 (param-decay2 dmap)
                                              rel (param-release dmap)
                                              bp (param-breakpoint dmap)
                                              sus (param-sustain dmap)
                                              t0 0.0
                                              t1* (atom (+ t0 att))
                                              t2* (atom (+ @t1* dcy1))
                                              t3* (atom (+ @t2* dcy2))
                                              t4* (atom (+ @t3* 2))
                                              t5* (atom (+ @t4* rel))
                                              bp* (atom bp)
                                              sus* (atom sus)
                                              update-curve (fn []
                                                             (.set-points! p1 [[@t1* 1.0]])
                                                             (.set-points! p2 [[@t2* @bp*]])
                                                             (.set-points! p3 [[@t3* @sus*]])
                                                             (.set-points! p4 [[@t4* @sus*]])
                                                             (.set-points! p5 [[@t5* 0.0]])
                                                             (.set-points! seg-a [[0 0][@t1* 1.0]])
                                                             (.set-points! seg-b [[@t1* 1.0][@t2* @bp*]])
                                                             (.set-points! seg-c [[@t2* @bp*][@t3* @sus*]])
                                                             (.set-points! seg-d [[@t3* @sus*][@t4* @sus*]])
                                                             (.set-points! seg-e [[@t4* sus][@t5* 0.0]])
                                                             (.render drw))]
                                          (cond (= cp p1)   ; attack time p1
                                                (let [[att _](.mouse-where drw)]
                                                  (reset! t1* (float (max 0 att)))
                                                  (reset! t2* (+ @t1* dcy1))
                                                  (reset! t3* (+ @t2* dcy2))
                                                  (reset! t4* (+ @t3* 2))
                                                  (reset! t5* (+ @t4* rel))
                                                  (.set-param! ied param-attack @t1*)
                                                  (update-curve))
                                                
                                                (= cp p2)   ; decay1, breakpoint
                                                (let [[x y](.mouse-where drw)
                                                      att @t1*
                                                      dcy-1 (float (max 0 (- x att)))
                                                      bp (float (sgwr-math/clamp y 0.0 1.0))]
                                                  (reset! t2* (+ @t1* dcy1))
                                                  (reset! t3* (+ @t2* dcy2))
                                                  (reset! t4* (+ @t3* 2))
                                                  (reset! t5* (+ @t4* rel))
                                                  (reset! bp* bp)
                                                  (.set-param! ied param-decay1 dcy-1)
                                                  (.set-param! ied param-breakpoint bp)
                                                  (.status! ied (format "[%s] -> %5.3f  [%s] -> %5.3f"
                                                                        param-decay1 dcy1 param-breakpoint bp))
                                                  (update-curve))
                                                      
                                                (= cp p3)   ; decay2, sustain
                                                (let [[x y](.mouse-where drw)
                                                      dcy-2 (float (max 0 (- x @t2*)))
                                                      sus (float (sgwr-math/clamp y 0.0 1.0))]
                                                  (reset! t3* (+ @t2* dcy-2))
                                                  (reset! t4* (+ @t3* 2))
                                                  (reset! t5* (+ @t4* rel))
                                                  (reset! sus* sus)
                                                  (.set-param! ied param-decay2 dcy-2)
                                                  (.set-param! ied param-sustain sus)
                                                  (.status! ied (format "[%s] -> %5.3f  [%s] -> %5.3f"
                                                                        param-decay2 dcy-2 param-sustain sus))
                                                  (update-curve))

                                                (= cp p4)   ; sustain
                                                (let [[_ y](.mouse-where drw)
                                                      sus (float (sgwr-math/clamp y 0.0 1.0))]
                                                  (reset! sus* sus)
                                                  (.set-param! ied param-sustain sus)
                                                  (update-curve))

                                                (= cp p5)   ; release time
                                                (let [[x _](.mouse-where drw)
                                                      rel (float (max 0 (- x @t4*)))]
                                                  (reset! t5* (+ @t4* rel))
                                                  (.set-param! ied param-release rel)
                                                  (update-curve))

                                                :default 
                                                (.warning! ied "Internal ERROR envelope-panel mouse-motion-listener") )))))
    ;; rules
    (point/point root [0 0] :id :env-p0 
                 :color (lnf/envelope-handle)
                 :style [:diag :diag2]
                 :size 3)
    (major [0.0 -0.05][0.0 1.05])
    (major [-0.1 0.00][100 0.00])
    (major [-0.1 1.0][100 1.0])
    (doseq [db (range -3 -36 -3)]
      (let [y (math/db->amp db)]
        (minor [-0.1 y][100 y])))
    {:pan-main pan-main
     :sync-fn sync-fn
     :disable-fn disable-fn
     :enable-fn enable-fn}))
