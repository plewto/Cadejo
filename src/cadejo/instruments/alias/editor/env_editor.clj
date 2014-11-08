(ns cadejo.instruments.alias.editor.env-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.instruments.alias.editor.alias-factory :as factory])
  (:require [cadejo.ui.instruments.subedit :as subedit])
  (:require [cadejo.ui.util.color-utilities :as cutil])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.drawing :as sgwr])
  (:require [seesaw.core :as ss])
  (:require [seesaw.border :as ssb])
  (:import java.awt.event.MouseListener
           java.awt.event.MouseMotionListener
           javax.swing.Box
           javax.swing.SwingConstants))

(def ^:private w 350)
(def ^:private h 150)
(def ^:private sustain-time 0.5)

(defn- enved [prefix ied]
  (let [enable-change-listener* (atom true)
        invertable (not (= prefix 3))
        param (fn [suffix](keyword (format "env%d-%s" prefix suffix)))
        param-attack (param "attack")
        param-decay1 (param "decay1")
        param-decay2 (param "decay2")
        param-release (param "release")
        param-peak (param "peak")
        param-sustain (param "sustain")
        param-breakpoint (param "breakpoint")
        param-invert (param "invert")
        tb-invert (let [b (ss/radio :text "-1")]
                    (.setEnabled b invertable)
                    b)
        lab-position (ss/label :text "[---]")
        [bg fg](config/envelope-colors)
        background (cutil/color (or bg (lnf/get-color (config/current-skin) :text-fg)))
        foreground (cutil/color (or fg (lnf/get-color (config/current-skin) :text-bg)))
        marker-color (cutil/inversion background)
        point-color (cutil/shift foreground 0.05) 
        max-time* (atom 8.0)
        zero-line* (atom nil)
        elements* (atom {})
        attack* (atom 1.0)
        decay1* (atom 1.0)
        decay2* (atom 1.0)
        release* (atom 1.0)
        peak* (atom 1.0)
        breakpoint* (atom 0.9)
        sustain* (atom 0.8)
        invert* (atom false)
        drw (let [d (sgwr.drawing/cartesian-drawing w h [-0.05 -0.125][8.5 1.125])]
              (.paper! d background)
              (.color! d marker-color)
              (.line! d [-1 0][1000 0]) ;; x-axis
              (reset! zero-line* (.line! d [0 -1][0 2]))
              (.color! d point-color)
              (.style! d 0)
              (swap! elements* (fn [n](assoc n :p0 (.point! d [0 0.0]))))
              (swap! elements* (fn [n](assoc n :p1 (.point! d [1 1.0]))))
              (swap! elements* (fn [n](assoc n :p2 (.point! d [2 0.9]))))
              (swap! elements* (fn [n](assoc n :p3 (.point! d [3 0.7]))))
              (swap! elements* (fn [n](assoc n :p4 (.point! d [5 0.7]))))
              (swap! elements* (fn [n](assoc n :p5 (.point! d [6 0.0]))))
              (.color! d foreground)
              (swap! elements* (fn [n](assoc n :s1 (.line! d [0 0.0][1 1.0]))))
              (swap! elements* (fn [n](assoc n :s2 (.line! d [0 1.0][2 0.9]))))
              (swap! elements* (fn [n](assoc n :s3 (.line! d [2 0.9][3 1.7]))))
              (.style! d 2)
              (swap! elements* (fn [n](assoc n :s4 (.line! d [3 1.7][5 0.7]))))
              (.style! d 0)
              (swap! elements* (fn [n](assoc n :s5 (.line! d [5 0.7][6 0.0]))))
              (doseq [[k e](seq @elements*)]
                (.put-property! (.attributes e) :id k)) 
              (let [p0 (:p0 @elements*)
                    p1 (:p1 @elements*)
                    p2 (:p2 @elements*)
                    p3 (:p3 @elements*)
                    p4 (:p4 @elements*)
                    p5 (:p5 @elements*)
                    s1 (:s1 @elements*)
                    s2 (:s2 @elements*)
                    s3 (:s3 @elements*)
                    s4 (:s4 @elements*)
                    s5 (:s5 @elements*)
                    adjacent (fn [p & segments]
                               (.put-property! (.attributes p) :adjacent segments))
                    point-parameters (fn [p time level]
                                       (let [a (.attributes p)]
                                       (.put-property! a :time-param time)
                                       (.put-property! a :level-param level)))]
                (adjacent p0 s1)
                (adjacent p1 s1 s2)
                (adjacent p2 s2 s3)
                (adjacent p3 s3 s4)
                (adjacent p4 s4 s5)
                (adjacent p5 s5)
                (point-parameters p0 nil nil)
                (point-parameters p1 param-attack param-peak)
                (point-parameters p2 param-decay1 param-breakpoint)
                (point-parameters p3 param-decay2 param-sustain)
                (point-parameters p4 param-decay2 param-sustain)
                (point-parameters p5 param-release nil))
              d)

        update-curve (fn []
                        (let [t0 0
                              t1 @attack*
                              t2 (+ t1 @decay1*)
                              t3 (+ t2 @decay2*)
                              t4 (+ t3 sustain-time)
                              t5 (+ t4 @release*)
                              a0 0
                              a1 @peak*
                              a2 @breakpoint*
                              a3 @sustain*
                              a4 @sustain*
                              a5 0]
                          (.position! (:p1 @elements*) [[t1 a1]])
                          (.position! (:p2 @elements*) [[t2 a2]])
                          (.position! (:p3 @elements*) [[t3 a3]])
                          (.position! (:p4 @elements*) [[t4 a4]])
                          (.position! (:p5 @elements*) [[t5 a5]])
                          (.position! (:s1 @elements*) [[t0 a0][t1 a1]])
                          (.position! (:s2 @elements*) [[t1 a1][t2 a2]])
                          (.position! (:s3 @elements*) [[t2 a2][t3 a3]])
                          (.position! (:s4 @elements*) [[t3 a3][t4 a4]])
                          (.position! (:s5 @elements*) [[t4 a4][t5 a5]])
                          (.render drw)))
        cs (.coordinate-system drw)
        selected* (atom nil)
        deselect-all (fn []
                        (doseq [e (vals @elements*)]
                          (.select! (.attributes e) false))
                        (reset! selected* nil))
        select (fn [mouse-position]
                 (deselect-all)
                 (let [mp (.inv-map cs mouse-position)
                       p (.closest drw mp (fn [e](and (= (.element-type e) :point)
                                                      (not (= (.property (.attributes e) :id) :p0)))))]
                   (.select! (.attributes p) true)
                   (doseq [s  (.property (.attributes p) :adjacent)]
                     (.select! (.attributes s) true))
                   (reset! selected* p)
                   (.render drw)
                   @selected*))
        pan-center (ss/horizontal-panel 
                    :items [(.drawing-canvas drw)]
                    :size [w :by h])
        pan-south (ss/grid-panel :rows 1 
                                 :items [tb-invert lab-position]
                                 :size [w :by 34])
        
        pan-main (ss/vertical-panel
                  :items [pan-center pan-south
                          (ss/label :text "Invert button Not Connected")  ;; DEBUG
                          ]
                  :border (factory/title (format "Envelope %d" prefix)))
        syncfn (fn [data]
                 (reset! attack* (param-attack data))
                 (reset! decay1* (param-decay1 data))
                 (reset! decay2* (param-decay2 data))
                 (reset! release* (param-release data))
                 (reset! peak* (param-peak data))
                 (reset! breakpoint* (param-breakpoint data))
                 (reset! sustain* (param-sustain data))
                 (reset! invert* (param-invert data))
                 (update-curve)
                 (if @invert*
                   (.setSelected tb-invert (not (zero? @invert*)))))
        zoomfn (fn [mt]
                 (.remove! drw @zero-line*)
                 (.view! cs [[-0.05 -0.125][mt 1.125]])
                 (.color! drw foreground)
                 (.style! drw 0)
                 (reset! zero-line* (.line! drw [0 0][0 2]))
                 (.render drw))]
    ;; select edit point
    (.add-mouse-listener! drw (proxy [MouseListener][]
                                (mouseEntered [_])
                                (mouseExited [_])
                                (mouseClicked [_])
                                (mousePressed [ev]
                                  (let [u (.getX ev)
                                        v (.getY ev)]
                                    (select [u v])))
                                (mouseReleased [_] 
                                  (deselect-all)
                                  (.render drw)
                                  (reset! selected* nil))))

    ;; display mouse position
    (.add-mouse-motion-listener! drw (proxy [MouseMotionListener][]
                                       (mouseDragged [ev]
                                         (let [u (.getX ev)
                                               v (.getY ev)
                                               [x y](.inv-map cs [u v])
                                               frmt "[%6.3f %6.3f]"]
                                           (ss/config! lab-position :text 
                                                       (format frmt (float x)(float y)))))
                                       (mouseMoved [ev]
                                         (let [u (.getX ev)
                                               v (.getY ev)
                                               [x y](.inv-map cs [u v])
                                               frmt "[%6.3f %6.3f]"]
                                           (ss/config! lab-position :text 
                                                       (format frmt (float x)(float y)))))))
    ;; edit envelope point positions
    (.add-mouse-motion-listener! drw (proxy [MouseMotionListener][]
                                       (mouseMoved [_])
                                       (mouseDragged [ev]
                                         (let [pos (.inv-map cs [(.getX ev)(.getY ev)])
                                               p @selected*
                                               att (and p (.attributes p))
                                               id (and p (.property att :id))
                                               time-param (and p (.property att :time-param))
                                               level-param (and p (.property att :level-param))
                                               amp (float (min 1.0 (max 0.0 (second pos))))]
                                           ;; update time parameters
                                           (cond (= time-param param-attack)
                                                 (let [atime (float (max 0 (first pos)))]
                                                   (reset! attack* atime)
                                                   (.set-param! ied param-attack atime))

                                                 (= time-param param-decay1)
                                                 (let [dtime (float (max 0 (- (first pos) @attack*)))]
                                                   (reset! decay1* dtime)
                                                   (.set-param! ied param-decay1 dtime))

                                                 (= time-param param-decay2)
                                                 (let [dtime (float (max 0 (- (first pos)(+ @attack* @decay1*))))]
                                                   (reset! decay2* dtime)
                                                   (.set-param! ied param-decay2 dtime))

                                                 (= time-param param-release)
                                                 (let [dtime (float (max 0 (- (first pos)(+ @attack* @decay1* @decay2*) sustain-time)))]
                                                   (reset! release* dtime)
                                                   (.set-param! ied param-release dtime)))
                                           ;; update level parameters
                                           (cond (= level-param param-peak)
                                                 (do 
                                                   (reset! peak* amp)
                                                   (.set-param! ied param-peak amp))

                                                 (= level-param param-breakpoint)
                                                 (do 
                                                   (reset! breakpoint* amp)
                                                   (.set-param! ied param-breakpoint amp))

                                                 (= level-param param-sustain)
                                                 (do
                                                   (reset! sustain* amp)
                                                   (.set-param! ied param-sustain amp)))
                                           (update-curve)))))
    {:pan-main pan-main
     :zoomfn zoomfn 
     :syncfn syncfn}))

(defn envelope-editor [performance ied]
  (let [env1 (enved 1 ied)
        env2 (enved 2 ied)
        env3 (enved 3 ied)
        envs [env1 env2 env3]
        init-curve {:attack 0.0 :decay1 1.0 :decay2 1.0 :release 0.0
                    :peak 1.0 :breakpoint 1.0 :sustain 1.0}
        view* (atom 8.0)
        jb-zoom-out (factory/button "Out" :view :out "Zoom envelope out")
        jb-zoom-in (factory/button "In" :view :in "Zoom envelope in")
        jb-zoom-reset (factory/button "Reset" :view :reset "Set default zoom")
        lab-view (ss/label :text "8.0")
        pan-center (ss/grid-panel :rows 1
                                  :items [(:pan-main env1)
                                          (:pan-main env2)
                                          (:pan-main env3)])
        pan-east (ss/vertical-panel 
                  :items [jb-zoom-out jb-zoom-in jb-zoom-reset lab-view])
        pan-main (ss/border-panel :center pan-center
                                  :east pan-east
                                  :border (factory/bevel))
        widget-map {:pan-main pan-main}]
    (ss/listen jb-zoom-out :action
               (fn [_]
                 (swap! view* (fn [n](float (* 3/2 n))))
                 (ss/config! lab-view :text (format "%6.3f" @view*))
                 (doseq [e envs]
                   ((:zoomfn e) @view*))))
    (ss/listen jb-zoom-in :action
               (fn [_]
                 (swap! view* (fn [n](float (* 2/3 n))))
                 (ss/config! lab-view :text (format "%6.3f" @view*))
                 (doseq [e envs]
                   ((:zoomfn e) @view*))))
    (ss/listen jb-zoom-reset :action
               (fn [_]
                 (reset! view* 8.0)
                 (ss/config! lab-view :text (format "%6.3f" @view*))
                 (doseq [e envs]
                   ((:zoomfn e) @view*))))
    (reify subedit/InstrumentSubEditor
      (widgets [this] widget-map)
      (widget [this key]
        (get widget-map key))

      (parent [this] ied)
      
      (parent! [this _]) ;; ignore
      
      (status! [this txt]
        (.status! ied txt))
      
      (warning! [this txt]
        (.warning! ied txt))
      
      (set-param! [this param value]
        (.set-param! ied param value))
      
      (init! [this] nil)

      (sync-ui! [this]
        (let [data (.current-data (.bank performance))]
          ((:syncfn env1) data)
          ((:syncfn env2) data)
          ((:syncfn env3) data))))))
