(ns cadejo.ui.vkbd
  "Provides virtual onscreen keyboard"
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.cadejo-frame :as cframe])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.util.help :as help])
  (:require [cadejo.ui.util.child-dialog :as child])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory :reload true])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.user-message :as umsg])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.multistate-button :as msb])
  (:require [xolotl.xolotl])
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc]))


(def xolotl* (atom nil)) ;; FOR TESTING ONLY
(def vkbd* (atom nil))   ;; FOR TESTING ONLY

(def ^:private width 670)
(def ^:private height 120)
(def ^:private frame-size [640 :by 170])
(def ^:private c1 (lnf/text))           ; Colors
(def ^:private octaves 4)
(def ^:private white-key-width 16)
(def ^:private white-key-height 84)
(def ^:private black-key-width 11)
(def ^:private black-key-height 52)
(def ^:private white-key-count (inc (* octaves 7)))
(def ^:private black-key-count (* octaves 5))
(def ^:private key-count (+ white-key-count black-key-count))
(def ^:private octave-button-states
  [[:0 "0" c1][:1 "1" c1][:2 "2" c1][:3 "3" c1]
   [:5 "4" c1][:6 "5" c1][:6 "6" c1][:7 "7" c1]])
(def ^:private channel-button-states
  [[:1 " 1" c1][:2 " 2" c1][:3 " 3" c1][:4 " 4" c1]
   [:5 " 5" c1][:6 " 6" c1][:7 " 7" c1][:8 " 8" c1]
   [:9 " 9" c1][:10 "10" c1][:11 "11" c1][:12 "12" c1]
   [:13 "13" c1][:14 "14" c1][:15 "15" c1][:16 "16" c1]])

(defn- point+ [p a b]
  "For point p [x y] return new point q [x+a y+b]
   [(+ (first p) a)(+ (second p) b)])"
  [(+ (first p) a)(+ (second p) b)])
  
(def ^:private pos-octave-label [10 50])
(def ^:private pos-octave-button (point+ pos-octave-label 65 -20))
(def ^:private pos-channel-label (point+ pos-octave-label  0  40))
(def ^:private pos-channel-button (point+ pos-channel-label 60 -20))
(def ^:private pos-left-white-key [130 110])
(def ^:private pos-parent-button (point+ pos-left-white-key 475 -90))
(def ^:private pos-child-button (point+ pos-parent-button 30 0))
(def ^:private pos-help-button (point+ pos-child-button 0 30))
(def ^:private pos-panic-button (point+ pos-parent-button 0 30))
(def ^:private pos-panic-label (point+ pos-panic-button 4 32))

(defn- help-action [& _]
  (help/display-topic :virtual-keyboard))

(defprotocol VKbdProtocol

  (channel
    [this]
    "Returns MIDI transmit channel (1..16)")

  (channel!
    [this c1]
    "Sets MIDI transmit channel (1..16) 
    Returns channel number")

  (panic
    [this]
    "Send all notes off on current channel
     Returns true")

  (note-on
    [this keynum vel]
    "Transmits note-on event current channel
     for given keynum (0..127) and velocity (0..1).
     Returns map {:channel c0 :data1 keynum :note keynum :vel (* vel 127)}")
     

  (note-off
    [this keynum]
    "Transmits note-off event on current channel
     Returns map {:channel c0 :data1 keynum :note keynum :data2 127}")

  (show-xolotl! [this]))


  

(deftype VKbd [parent* children* properties* cframe xo]

  cadejo.ui.midi.node-editor/NodeEditor

  (cframe! [this cframe embed] nil) ;; Not implemented

  (cframe [this] cframe)

  (jframe [this] (.jframe cframe))

  (set-icon! [this ico]
    (.set-icon! cframe ico))

  (show! [this]
    (.show! cframe))

  (hide! [this]
    (.hide! cframe))

  (widgets [this] {})

  (widget [this key]
    (let [rs (get (.widgets this) key)]
      (if (not rs)
        (umsg/warning (format "VKbd does not have %s widget" key)))
      rs))

  (add-widget! [this key obj] nil) ;; not implemented

  (node [this] this)

  (set-node! [this ignore] nil) ;; not implemented

  (working [this flag] nil) ;; not implemented

  (status! [this msg]
    (.status! cframe msg))

  (warning! [this msg]
    (.warning! cframe msg))

  (update-path-text [this]
    (let [pt (cadejo.midi.node/rep-path this)]
      (.set-path-text! cframe pt) 
      (doseq [c (.children this)]
        (let [ced (.get-editor c)]
          (if ced (.update-path-text ced))))))

  (sync-ui! [this]
    (.update-path-text this))
  
  VKbdProtocol
  
  (channel [this]
    (:channel @properties*))
 
  (channel! [this c1]
    (let [c0 (rem (math/abs (dec c1)) 15)]
      (swap! properties* (fn [q](assoc q :channel (inc c0))))
      (inc c0)))

  (panic [this]
    (dotimes [kn 127]
      (.note-off this kn)
      (Thread/sleep 1))
    (.status! this "All notes off"))

  (note-on [this kn vel]
    (let [c0 (dec (.channel this))
          v127 (int (math/clamp (* vel 127) 0 127))
          ev {:channel c0 :command :note-on :note kn :data1 kn :data2 v127}]
      ((.event-dispatcher this) ev)
      ev))

  (note-off [this kn]
    (let [c0 (dec (.channel this))
          ev {:channel c0 :command :note-off :note kn :data1 kn :data2 127}]
      ((.event-dispatcher this) ev)
      ev))

  (show-xolotl! [this]
    (let [xed (.get-editor xo)]
      (.show! xed)))
  
  cadejo.midi.node/Node

  (node-type [this] :vkbd)

  (is-root? [this]
    (not @parent*))

  (find-root [this]
    (if (.is-root? this)
      this
      (.find-root @parent*)))

  (parent [this]
    @parent*)

  (children [this]
    @children*)

  (is-child? [this other]
    (ucol/member? other (.children this)))

  (add-child! [this other]
    (if (not (.is-child? this other))
      (do
        (swap! children* (fn [q](conj q other)))
        (._set-parent! other this)
        true)
      false))

  (remove-child! [this other]
    (if (.is-child? this other)
      (let [predicate (fn [r](not (= r other)))]
        (swap! children* (fn [q](into [](filter predicate q))))
        (._orphan! other)
        true)
      false))

  (_set-parent! [this p]
    (reset! parent* p))
  
  (_orphan! [this]
    (._set-parent! this nil))

  (put-property! [this key value]
    (let [k (keyword key)]
      (swap! properties* (fn [n](assoc n k value)))
      k))

  (remove-property! [this key]
    (let [k (keyword key)]
      (swap! properties* (fn [n](dissoc n k)))
      k))
 
  (get-property [this key default]
    (let [value (or (get @properties* key)
                    (and (.parent this)
                         (.get-property (.parent this) key default))
                    default)]
      (if (= value :fail)
        (umsg/warning (format "vkbd does not have property %s" key))
        value)))
  
  (get-property [this key]
    (.get-property this key :fail))
  
  (local-property [this key]
    (get @properties* key))
  
  (properties [this local-only]
    (set (concat (keys @properties*)
                 (if (and (.parent this) (not local-only))
                   (.properties (.parent this))
                   nil))))
  
  (properties [this]
    (.properties this false))
  
  (event-dispatcher [this]
    (fn [event]
      (doseq [c (.children this)]
        ((.event-dispatcher c) event))))
  
  (get-editor [this]
    this)

  (set-editor! [this _] nil) ;; not implemented
  
  )


(defn vkbd [parent child]
  (let [cframe (cframe/cadejo-frame "Virtual Keyboard" "" [:progress-bar :pan-north])
        properties* (atom {:channel 1,
                           :octave 3,
                           :id :VKBD})
        parent* (atom nil)
        children* (atom [])
        xolotl (xolotl.xolotl/create-sequencer children*)
        vnode (VKbd. parent* children* properties* cframe xolotl)
        drw (sfactory/sgwr-drawing width height)

        octave-action (fn [b _]
                        (let [cbs (msb/current-multistate-button-state b)
                              oct (first cbs)]
                          (swap! properties* (fn [q](assoc q :octave oct)))
                          (.status! vnode (format "Octave -> %s" oct))))
        msb-octave (msb/text-multistate-button (.tool-root drw)
                                               pos-octave-button
                                               octave-button-states
                                               :click-action octave-action
                                               :w 30 :h 30 :gap 10
                                               :rim-radius 0)
        channel-action (fn [b _]
                         (let [cbs (msb/current-multistate-button-state b)
                               chan0 (first cbs)]
                           (swap! properties* (fn [q](assoc q :channel (inc chan0))))
                           (.status! vnode (format "Transmit Channel -> %s" 
                                                   (get @properties* :channel)))))
        msb-channel (msb/text-multistate-button (.tool-root drw)
                                                pos-channel-button
                                                channel-button-states
                                                :click-action channel-action
                                                :w 40 :h 30 :gap 10
                                                :rim-radius 0)
        b-panic (sfactory/mini-delete-button drw pos-panic-button
                                             :panic (fn [& _](.panic vnode)))
        b-help (sfactory/mini-help-button drw pos-help-button
                                          :help help-action)
        b-parent (sfactory/mini-chevron-up-button drw pos-parent-button
                                                  :parent (fn [& _]
                                                            (let [p (.parent vnode)
                                                                  ped (and p (.get-editor p))]
                                                              (and ped (.show! ped)))))
        b-child (sfactory/mini-chevron-down-button drw pos-child-button
                                                   :child (fn [& _]
                                                            (cadejo.ui.util.child-dialog/child-dialog vnode)))]
    ;; Draw keys
    (let [rim-color (ssc/color 0 0 0 0)
          x* (atom (first pos-left-white-key))
          y0 (second pos-left-white-key)
          y1 (- y0 white-key-height)
          y2 (- y0 32)  ;; black keys
          white-seq [ 0  2  4  5  7  9 11
                     12 14 16 17 19 21 23
                     24 26 28 29 31 33 35
                     36 38 40 41 43 45 47
                     48 50 52 53 55 57 59]
          black-seq [ 1  3  6 8 10
                     13 15 18 20 22
                     25 27 30 32 34
                     37 39 42 44 46]
          black-offset [ 18  34  66  82 98
                        130 146 178 194 210
                        242 258 290 306 322
                        354 370 402 418 434]
          down-action (fn [b ev]
                        (let [id (.get-property b :id)
                              octave (get @properties* :octave)
                              keynum (+ (* 12 octave) id)
                              mouse-y (.getY ev)
                              map-y (.get-property b :fn-y-pos->val)
                              vel (map-y mouse-y)
                              v127 (int (* vel 127))
                              c1 (get @properties* :channel)]
                          (.note-on vnode keynum vel)
                          (.status! vnode (format "Chan %2d  Key %3d  Vel %3d" c1 keynum v127))))
          up-action (fn [b _]
                      (let [id (.get-property b :id)
                            octave (get @properties* :octave)
                            keynum (+ (* 12 octave) id)
                            c1 (get @properties* :channel)]
                        (.note-off vnode keynum)))]
      (dotimes [i white-key-count]
        (let [p0 [@x* y0]
              p1 [(+ @x* white-key-width) y1]
              wfld (field/field (.tool-root drw)
                                p0 p1
                                [0.0 1.0][0.0 1.0]
                                :id (nth white-seq i)
                                :rim-color rim-color
                                :press-action down-action
                                :release-action up-action
                                :exit-action up-action)]
          (sgwr.components.image/read-image (.root drw)
                                            (point+ p0 0 (- white-key-height))
                                            "resources/keys/up_white.png")
          (swap! x* (fn [q](+ q white-key-width)))))
      (reset! x* (- (first pos-left-white-key) 9))
      (dotimes [i black-key-count]
        (let [x-offset (nth black-offset i)
              p0 [(+ @x* x-offset) y2]
              p1 (point+ p0 black-key-width (- black-key-height))
              bfld (field/field (.tool-root drw)
                                p0 p1
                                [0.0 1.0][0.0 1.0]
                                :id (nth black-seq i)
                                :rim-color rim-color
                                :press-action down-action
                                :release-action up-action
                                :exit-action up-action)]
          (sgwr.components.image/read-image (.root drw)
                                            (point+ p0 0 (- black-key-height))
                                            "resources/keys/up_black.png"))) )
    (msb/set-multistate-button-state! msb-octave 3 false)
    (if parent (.add-child! parent vnode))
    (if child (.add-child! vnode child))
    (.put-property! b-help :rim-radius 0)
    (.put-property! b-parent :rim-radius 0)
    (.size! cframe (+ width 10)(+ height 70))
    (sfactory/label drw pos-octave-label "Octave")
    (sfactory/label drw pos-channel-label "Channel")
    (sfactory/label drw pos-panic-label "Off" :size 5.0)
    (ss/config! (.widget cframe :pan-center) :center (.canvas drw))
    (.render drw)
    (.add-child! vnode xolotl)
    (reset! xolotl* xolotl) ;; ISSUE for testing only
    (reset! vkbd* vnode)    ;; ISSUE for testing only
    vnode))
