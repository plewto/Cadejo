(ns cadejo.instruments.algo.env-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.ui.util.color-utilities])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.undo-stack])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.drawing :as sgwr])
  (:require [seesaw.core :as ss])
  (:require [seesaw.border :as ssb])
  (:import java.awt.event.MouseListener
           java.awt.event.MouseMotionListener
           javax.swing.SwingConstants))

(def ^:private w 500)
(def ^:private h 192)
(def ^:private sustain-time 2.0)
(def ^:private clipboard* (atom nil))

(defn- button [txt igroup isubgrp tttext]
  (let [b (ss/button)]
    (.setVerticalTextPosition b SwingConstants/BOTTOM)
    (.setHorizontalTextPosition b SwingConstants/CENTER)
    (if (cadejo.config/enable-button-text)
      (ss/config! b :text txt))
    (if (cadejo.config/enable-button-icons)
      (do
        (.setIcon b (lnf/read-icon igroup isubgrp))
        (.setSelectedIcon b (lnf/read-selected-icon igroup isubgrp))))
    (if (and tttext (cadejo.config/enable-tooltips))
      (.setToolTipText b tttext))
    b))

(defn- radio [txt igroup isubgrp tttext]
  (let [b (if (config/enable-button-icons)(ss/radio)(ss/toggle))]
    (.setVerticalTextPosition b SwingConstants/BOTTOM)
    (.setHorizontalTextPosition b SwingConstants/CENTER)
    (if (cadejo.config/enable-button-text)
      (ss/config! b :text txt))
    (if (cadejo.config/enable-button-icons)
      (do
        (.setIcon b (lnf/read-icon igroup isubgrp))
        (.setSelectedIcon b (lnf/read-selected-icon igroup isubgrp))))
    (if (and tttext (cadejo.config/enable-tooltips))
      (.setToolTipText b tttext))
    b))


;; Provides editor for algo envelope
;; prefix - keyword indicating specific envelope 
;;          prefix must be one of :env1 :op1 :op2 :op3 :op4 
;;                                :op5 :op6 :op7 or :op8
;; performance - the parent performance
;; ied - the parent instrument editor
;; 
;; returns map with following keys
;; :pan-main - swing panel holding components
;; :synfn - function used to update components
;; :mutefn function (fn flag) disables components if flag is true
;;
;; Implementation note:
;; Originally envelope-editor returned an instance of InstrumentSubEditor
;; which was inconsistent with subsequent code. A simple hack basically wraps
;; aspects of the InstrumentEditor into the returned map. This results
;; in some redundancy such as having both sync-ui! and syncfn functions etc.
;;
(defn envelope-editor [prefix performance ied]
  (let [undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        kw-attack (keyword (format "%s-attack" (name prefix)))
        kw-decay1 (keyword (format "%s-decay1" (name prefix)))
        kw-decay2 (keyword (format "%s-decay2" (name prefix)))
        kw-sustain (keyword (format "%s-sustain" (name prefix)))
        kw-release (keyword (format "%s-release" (name prefix)))
        kw-breakpoint (keyword (format "%s-breakpoint" (name prefix)))
        kw-bias (keyword (format "%s-%s" (name prefix)(if (= prefix :env1) "bias" "env-bias")))
        kw-scale (keyword (format "%s-%s" (name prefix)(if (= prefix :env1) "scale" "env-scale")))
        invertable (or (= prefix :env1)(= prefix :op2)(= prefix :op3)
                       (= prefix :op4)(= prefix :op5)(= prefix :op8))

        tb-invert (let [b (radio "Invert" :env :inverted "Invert envelope")]
                    (.setEnabled b invertable)
                    b)
        jb-init (button "Init" :general :reset "Initialize envelope")
        jb-copy (button "Copy" :general :copy "Copy envelope to clipboard")
        jb-paste (button "Paste" :general :paste "Set envelope from clipboard")
        jb-undo (.get-button undo-stack)
        jb-zoom-in (button "In" :view :in "Zoom in")
        jb-zoom-out (button "Out" :view :out "Zoom out")
        jb-zoom-reset (button "Reset" :view :extent "Set default zoom")
        lab-view (ss/label :text "8.0")
        lab-position (ss/label :text "[ 0.000  0.000]")
        toolbar (ss/toolbar :orientation :horizontal
                            :floatable? false
                            :items [tb-invert jb-init jb-copy jb-paste jb-undo
                                    jb-zoom-in jb-zoom-out jb-zoom-reset])
        pan-view (ss/grid-panel :rows 1
                                :items [lab-position lab-view])
        pan-south (ss/border-panel :north pan-view
                                   :center toolbar)
        background (lnf/get-color (config/current-skin) :text-fg)
        foreground (lnf/get-color (config/current-skin) :text-bg)
        marker-color (cadejo.ui.util.color-utilities/inversion background)
        point-color (cadejo.ui.util.color-utilities/shift foreground 0.05)
        max-time* (atom 8)
        zero-line* (atom nil)
        elements* (atom {})
        attack* (atom 1.0)
        decay1* (atom 1.0)
        decay2* (atom 1.0)
        release* (atom 1.0)
        breakpoint* (atom 0.7)
        sustain* (atom 0.9)
        bias* (atom 0.0)
        scale* (atom 1.0)

        drw (let [d (sgwr.drawing/cartesian-drawing w h [-0.05 -0.125][8.5 1.125])]
              (.paper! d background)
              (.color! d marker-color)
              (.line! d [-1 0][100 0]) ;; x-axis
              (reset! zero-line* (.line! d [0 -1][0 2]))
              (.color! d point-color)
              (.style! d 0)
              (swap! elements* (fn [n](assoc n :p0 (.point! d [0 0.0]))))
              (swap! elements* (fn [n](assoc n :p1 (.point! d [1 1.0]))))
              (swap! elements* (fn [n](assoc n :p2 (.point! d [2 0.7]))))
              (swap! elements* (fn [n](assoc n :p3 (.point! d [3 0.8]))))
              (swap! elements* (fn [n](assoc n :p4 (.point! d [5 0.8]))))
              (swap! elements* (fn [n](assoc n :p5 (.point! d [6 0.0]))))
              (.color! d foreground)
              (swap! elements* (fn [n](assoc n :s1 (.line! d [0 0.0][1 1.0]))))
              (swap! elements* (fn [n](assoc n :s2 (.line! d [1 1.0][2 0.7]))))
              (swap! elements* (fn [n](assoc n :s3 (.line! d [2 0.7][3 0.8]))))
              (swap! elements* (fn [n](assoc n :s5 (.line! d [5 0.8][6 0.0]))))
              (.style! d 2)
              (swap! elements* (fn [n](assoc n :s4 (.line! d [3 0.8][5 0.8]))))
              (doseq [[k e](seq @elements*)]
                (.put-property! (.attributes e) :id k))
              (.put-property! (.attributes (:p0 @elements*)) 
                              :adjacent
                              [(:s1 @elements*)])
              (.put-property! (.attributes (:p1 @elements*))
                              :adjacent
                              [(:s1 @elements*)
                               (:s2 @elements*)])
              (.put-property! (.attributes (:p2 @elements*))
                              :adjacent
                              [(:s2 @elements*)
                               (:s3 @elements*)])
              (.put-property! (.attributes (:p3 @elements*)) 
                              :adjacent
                              [(:s3 @elements*)
                               (:s4 @elements*)])
              (.put-property! (.attributes (:p4 @elements*)) 
                              :adjacent
                              [(:s4 @elements*)
                               (:s5 @elements*)])
              (.put-property! (.attributes (:p5 @elements*)) 
                              :adjacent
                              [(:s5 @elements*)])
              (.put-property! (.attributes (:p1 @elements*))
                              :time-param kw-attack)
              (.put-property! (.attributes (:p1 @elements*))
                              :level-param nil)
              (.put-property! (.attributes (:p2 @elements*))
                              :time-param kw-decay1)
              (.put-property! (.attributes (:p2 @elements*))
                              :level-param kw-breakpoint)
              (.put-property! (.attributes (:p3 @elements*))
                              :time-param kw-decay2)
              (.put-property! (.attributes (:p3 @elements*))
                              :level-param kw-sustain)
              (.put-property! (.attributes (:p4 @elements*))
                              :time-param nil)
              (.put-property! (.attributes (:p4 @elements*))
                              :level-param kw-sustain)
              (.put-property! (.attributes (:p5 @elements*))
                              :time-param kw-release)
              (.put-property! (.attributes (:p5 @elements*))
                              :level-param nil)
              d)

        update-curve (fn []
                       (let [t0 0
                             t1 @attack*
                             t2 (+ t1 @decay1*)
                             t3 (+ t2 @decay2*)
                             t4 (+ t3 sustain-time)
                             t5 (+ t4 @release*)
                             a0 0
                             a1 1.0
                             a2 @breakpoint*
                             a3 @sustain*
                             a4 a3
                             a5 0.0]
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
                       p (.closest drw mp (fn [e] (and (= (.element-type e) :point)
                                                       (not (= (.property (.attributes e) :id) :p0)))))]
                   (.select! (.attributes p) true)
                   (doseq [s (.property (.attributes p) :adjacent)]
                     (.select! (.attributes s) true))
                   (reset! selected* p)
                   (.render drw)
                   @selected*))

        pack (fn [][@attack* @decay1* @decay2* @release* 
                    @breakpoint* @sustain* @bias* @scale*])

        unpack (fn [lst]
                 (reset! attack* (nth lst 0))
                 (reset! decay1* (nth lst 1))
                 (reset! decay2* (nth lst 2))
                 (reset! release* (nth lst 3))
                 (reset! breakpoint* (nth lst 4))
                 (reset! sustain* (nth lst 5))
                 (reset! bias* (nth lst 6))
                 (reset! scale* (nth lst 7)))

        pan-center (ss/vertical-panel :items [(.drawing-canvas drw)]
                                      :border (ssb/line-border :color foreground
                                                               :thickness 1
                                                               :size [w :by h]))
        pan-main (ss/border-panel :center pan-center
                                  :south pan-south)
        
        widget-map {:pan-main pan-main}

        enved (reify cadejo.ui.instruments.subedit/InstrumentSubEditor

                (widgets [this] widget-map)
                
                (widget [this key]
                  (or (get widget-map key)
                      (umsg/warning (format "Algo envelope-editor does not have %s widget" key))))

                (parent [this] ied)

                (parent! [this _]) ;; ignore

                (status! [this txt]
                  (.status! ied txt))

                (warning! [this txt]
                  (.warning! ied txt))

                (set-param! [this param value]
                  (.set-param! ied param value))

                (init! [this]
                  (reset! attack* 0.0)
                  (reset! decay1* 1.0)
                  (reset! decay2* 1.0)
                  (reset! release* 0.0)
                  (reset! breakpoint* 1.0)
                  (reset! sustain* 1.0)
                  (reset! bias* 0.0)
                  (reset! scale* 1.0)
                  (.setSelected tb-invert false)
                  (.set-param! this kw-attack @attack*)
                  (.set-param! this kw-decay1 @decay1*)
                  (.set-param! this kw-decay2 @decay2*)
                  (.set-param! this kw-release @release*)
                  (.set-param! this kw-breakpoint @breakpoint*)
                  (.set-param! this kw-sustain @sustain*)
                  (.set-param! this kw-bias @bias*)
                  (.set-param! this kw-scale @scale*)
                  (.sync-ui! this))

                (sync-ui! [this]
                  (let [data (.current-data (.bank performance))]
                    (reset! attack* (kw-attack data))
                    (reset! decay1* (kw-decay1 data))
                    (reset! decay2* (kw-decay2 data))
                    (reset! release* (kw-release data))
                    (reset! breakpoint* (kw-breakpoint data))
                    (reset! sustain* (kw-sustain data))
                    (reset! bias* (or (kw-bias data) 0.0))
                    (reset! scale* (or (kw-scale data 1.0)))
                    (update-curve)
                    (.setSelected tb-invert (= @scale* -1)))))

        save-undo (fn []
                    (.push-state! undo-stack (pack)))
        
        undo (fn []
               (if (not (.is-empty? undo-stack))
                 (do 
                   (unpack (.pop-state! undo-stack))
                   (.set-param! ied kw-attack @attack*)
                   (.set-param! ied kw-decay1 @decay1*)
                   (.set-param! ied kw-decay2 @decay2*)
                   (.set-param! ied kw-release @release*)
                   (.set-param! ied kw-breakpoint @breakpoint*)
                   (.set-param! ied kw-sustain @sustain*)
                   (if invertable
                     (do
                       (.set-param! ied kw-bias @bias*)
                       (.set-param! ied kw-scale @scale*)))
                   (.sync-ui! enved))))
        syncfn (fn [](.sync-ui! enved))
        mutefn (fn [flag]
                  (let [f (not flag)]
                    (doseq [obj [jb-init jb-copy jb-paste jb-zoom-in jb-zoom-out jb-zoom-reset]]
                      (.setEnabled obj f))
                    ))]

    (.add-mouse-listener! drw (proxy [MouseListener][]
                                (mouseEntered [_])
                                (mouseExited [_])
                                (mouseClicked [_])
                                (mousePressed [ev]
                                  (let [u (.getX ev)
                                        v (.getY ev)]
                                    (save-undo)
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
    ;; update params
    (.add-mouse-motion-listener! drw (proxy [MouseMotionListener][]
                                       (mouseMoved [_])
                                       (mouseDragged [ev]
                                         (let [u (.getX ev)
                                               v (.getY ev)
                                               pos (.inv-map cs [u v])
                                               p @selected*]
                                           (if p
                                             (let [id (.property (.attributes p) :id)
                                                   time-param (.property (.attributes p) :time-param)
                                                   level-param (.property (.attributes p) :level-param)]
                                               (cond (= time-param kw-attack)
                                                     (let [atime (float (max 0 (first pos)))]
                                                       (reset! attack* atime)
                                                       (.set-param! ied kw-attack atime)
                                                       (.status! ied (format "[%s] --> %6.3f" kw-attack atime))
                                                       (update-curve))

                                                     (= time-param kw-decay1)
                                                     (let [dtime (float (max 0 (- (first pos) @attack*)))]
                                                       (reset! decay1* dtime)
                                                       (.set-param! ied kw-decay1 dtime)
                                                       (.status! ied (format "[%s] --> %6.3f [%s] --> %6.3f" kw-decay1 dtime kw-breakpoint @breakpoint*))
                                                       (update-curve))

                                                     (= time-param kw-decay2)
                                                     (let [dtime (float (max 0 (- (first pos) (+ @attack* @decay1*))))]
                                                       (reset! decay2* dtime)
                                                       (.set-param! ied kw-decay2 dtime)
                                                       (.status! ied (format "[%s] --> %6.3f [%s] --> %6.3f" kw-decay2 dtime kw-sustain @sustain*))
                                                       (update-curve))

                                                     (= time-param kw-release)
                                                     (let [dtime (float (max 0 (- (first pos)(+ @attack* @decay1* @decay2*) sustain-time)))]
                                                       (reset! release* dtime)
                                                       (.set-param! ied kw-release dtime)
                                                       (.status! ied (format "[%s] --> %6.3f" kw-release dtime))
                                                       (update-curve))
                                                     :default
                                                     nil)
                                               (cond (= level-param kw-breakpoint)
                                                     (let [a (float (min 1.0 (max 0.0 (second pos))))]
                                                       (reset! breakpoint* a)
                                                       (.set-param! ied kw-breakpoint a)
                                                       (.status! ied (format "[%s] --> %6.3f [%s] --> %6.3f" kw-decay1 @decay1* kw-breakpoint a))
                                                       (update-curve))
                                                     
                                                     (= level-param kw-sustain)
                                                     (let [a (float (min 1.0 (max 0.0 (second pos))))]
                                                       (reset! sustain* a)
                                                       (.set-param! ied kw-sustain a)
                                                       (.status! ied (format "[%s] --> %6.3f [%s] --> %6.3f" kw-decay2 @decay2* kw-sustain a))
                                                       (update-curve))
                                                           
                                                     :default
                                                     nil)))))))
    (ss/listen tb-invert :action (fn [ev]
                                   (let [src (.getSource ev)]
                                     (if (.isSelected src)
                                       (do
                                         (.set-param! ied kw-bias (reset! bias* 1))
                                         (.set-param! ied kw-scale (reset! scale* -1))
                                         (.status! ied (format "Envelope %s inversion enabled" (name prefix))))
                                       (do
                                         (.set-param! ied kw-bias (reset! bias* 0))
                                         (.set-param! ied kw-scale (reset! scale* 1))
                                         (.status! ied (format "Envelope %s inversion disabled" (name prefix))))))))

    (ss/listen jb-init :action (fn [_] 
                                 (save-undo)
                                 (.init! enved)
                                 (.status! enved "Initialized envelope")))

    (ss/listen jb-copy :action (fn [_]
                                 (reset! clipboard* (pack))
                                 (.status! enved "Copied envelope to clipboard")))

    (ss/listen jb-paste :action (fn [_]
                                  (let [clip @clipboard*]
                                    (if clip
                                      (do 
                                        (save-undo)
                                        (unpack clip)
                                        (.set-param! ied kw-attack @attack*)
                                        (.set-param! ied kw-decay1 @decay1*)
                                        (.set-param! ied kw-decay2 @decay2*)
                                        (.set-param! ied kw-release @release*)
                                        (.set-param! ied kw-breakpoint @breakpoint*)
                                        (.set-param! ied kw-sustain @sustain*)
                                        (.set-param! ied kw-bias @bias*)
                                        (.set-param! ied kw-scale @scale*)
                                        (.sync-ui! enved)
                                        (.status! enved "Copied envelope from clipboard"))
                                      (.warning! enved "Nothing to copy, envelope clipboard is empty")))))

    (ss/listen jb-undo :action (fn [_](undo)))
    
    (ss/listen jb-zoom-in :action
               (fn [_]
                 (let [mt (float (swap! max-time* (fn [n](* n 2/3))))]
                   (ss/config! lab-view :text (format "%6.3f" mt))
                   (.remove! drw @zero-line*)
                   (.view! cs [[-0.05 -0.125][mt 1.125]])
                   (.color! drw foreground)
                   (.style! drw 0)
                   (reset! zero-line* (.line! drw [0 0][0 2]))
                   (.render drw))))

    (ss/listen jb-zoom-out :action
               (fn [_]
                 (let [mt (float (swap! max-time* (fn [n](* n 3/2))))]
                   (ss/config! lab-view :text (format "%6.3f" mt))
                   (.remove! drw @zero-line*)
                   (.view! cs [[-0.05 -0.125][mt 1.125]])
                   (.color! drw foreground)
                   (.style! drw 0)
                   (reset! zero-line* (.line! drw [0 0][0 2]))
                   (.render drw))))    

    (ss/listen jb-zoom-reset :action
               (fn [_]
                 (let [mt (reset! max-time* 8.0)]
                   (ss/config! lab-view :text (format "%6.3f" mt))
                   (.remove! drw @zero-line*)
                   (.view! cs [[-0.05 -0.125][mt 1.125]])
                   (.color! drw foreground)
                   (.style! drw 0)
                   (reset! zero-line* (.line! drw [0 0][0 2]))
                   (.render drw))))    
    {:pan-main pan-main
     :syncfn syncfn
     :mutefn mutefn}))
