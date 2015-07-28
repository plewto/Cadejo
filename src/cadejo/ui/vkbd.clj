(ns cadejo.ui.vkbd
  (:require [seesaw.core :as ss])
  (:require [seesaw.color :as ssc])
    (:require [cadejo.util.string])
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.image])
  (:require [sgwr.tools.field :as field])
  (:require [sgwr.tools.multistate-button :as msb])
  (:import java.awt.BorderLayout))

(def ^:private width 640)
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
(def ^:private pos-panic-label (point+ pos-left-white-key 470 -55))
(def ^:private pos-panic-button (point+ pos-panic-label -4 -35))

(defprotocol VKbdProtocol

  (channel [this] )

  (channel! [this c])
  
  (status [this msg])

  (panic [this])

  ;; chan0 - MIDI channel 0-indexed [0,15]
  ;; keynum [0, 127]
  ;; vel - Normalized velocity [0.0 1.0]
  (note-on [this chan0 keynum vel])

  (note-off [this chan0 keynum]))
  

(deftype VKbd [parent* children* properties* editor*]

  VKbdProtocol

  (channel [this]
    ((:get-channel @editor*)))

  (channel! [this c1]
    ((:set-channel! @editor*) c1))
  
  (status [this msg]
    ((:status @editor*) msg))

  (panic [this]
    (.status this "All Notes Off")
    (let [c0 (dec (.channel this))]
      (dotimes [k 128]
        (.note-off this c0 k)
        (Thread/sleep 1))))

  (note-on [this chan0 keynum vel]
    (let [ivel (min (max (int (* 128 vel)) 0) 127)
          c0 (bit-and chan0 15)
          kn (min (max keynum 0) 127)
          ev {:channel c0 :command :note-on :note kn :data1 kn :data2 ivel}]
      (.status this (format "Chan %2d  Key %3d  Vel %3d" (inc c0) kn ivel))
      ((.event-dispatcher this) ev)))

  (note-off [this chan0 keynum]
    (let [c0 (bit-and chan0 15)
          kn (min (max keynum 0) 127)
          ev {:channel c0 :command :note-off :note kn :data1 kn :data2 0}]
      ((.event-dispatcher this) ev)))
  
  cadejo.midi.node/Node

  (node-type [this] :VKbd)

  (is-root? [this] (not (.parent this)))

  (find-root [this]
    (if (.is-root? this)
      this
      (.find-root (.parent this))))
  
  (parent [this] @parent*)

  (children [this] @children*)

  (is-child? [this obj]
    (ucol/member? obj @children*))

  (add-child! [this obj]
    (if (not (.is-child? this obj))
      (do (swap! children* (fn [q](conj q obj)))
          (._set-parent! obj this)
          true)
      false))

   (remove-child! [this obj]
      false)

    (_orphan! [this]
      (reset! parent* nil))

    (_set-parent! [this parent]
      (reset! parent* parent))
    
    (put-property! [this key value]
      (let [k (keyword key)]
        (swap! properties* (fn [n](assoc n k value)))
        k))

    (remove-property! [this key]
      (let [k (keyword key)]
        (swap! properties* (fn [n](dissoc n (keyword k))))
        k))

    (get-property [this key default]
      (let [value (get @properties* key default)]
        (if (= value :fail)
          (do 
            (umsg/warning (format "Scene %s does not have property %s"
                                  (get @properties* :id "?") key))
            nil)
          value)))

    (get-property [this key]
      (.get-property this key :fail))

    (local-property [this key]
      (get @properties* key))

   (properties [this local-only]
     (set (concat (keys @properties*)
                  (if (and @parent* (not local-only))
                    (.properties @parent*)
                   nil))))
   
    (properties [this]
      (.properties this true))

    (get-editor [this]
      @editor*)

    (event-dispatcher [this]
      (fn [event]
        (doseq [c (.children this)]
          ((.event-dispatcher c) event))))

    (rep-tree [this depth]
      (let [pad (cadejo.util.string/tab depth)
            sb (StringBuilder.)]
        (.append sb (format "%sVirtual Keyboard\n" pad))
        (doseq [p (.children this)]
          (.append sb (.rep-tree p (inc depth))))
        (.toString sb)))
    )
    
      

    
(defn vkbd [parent child]
  (let [lab-status (ss/label :text "")
        lab-path (ss/label :text (if parent
                                   (format "%s.vkbd" (.get-property parent :id))
                                   "vkbd (no input port)"))
        properties* (atom {:channel 1,
                           :octave 0,
                           :id :VKBD})
        editor* (atom nil)
        vkbd-node (VKbd. (atom parent)
                         (atom (if child [child] []))
                         properties*
                         editor*)
        drw (sfactory/sgwr-drawing width height)
                                        ;octave*  (atom 0)
        octave-action (fn [b _]
                        (let [cbs (msb/current-multistate-button-state b)
                              oct (first cbs)]
                          (swap! properties* (fn [q](assoc q :octave oct)))
                          (ss/config! lab-status :text (format "Octave -> %s" oct))))
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
                           (ss/config! lab-status :text (format "Output channel is %s" (get @properties* :channel)))))
        msb-channel (msb/text-multistate-button (.tool-root drw)
                                                pos-channel-button
                                                channel-button-states
                                                :click-action channel-action
                                                :w 40 :h 30 :gap 10
                                                :rim-radius 0)
        action-panic (fn [b _]
                       (.panic vkbd-node))
        b-panic (sfactory/mini-delete-button drw pos-panic-button
                                             :panic action-panic)
        
        pan-south (ss/grid-panel :rows 1 :columns 2
                                 :items [(ss/vertical-panel :items [lab-status] :border (factory/bevel 2))
                                         (ss/vertical-panel :items [lab-path] :border (factory/bevel 2))])
        pan-main (ss/border-panel
                  :center (.canvas drw)
                  :south pan-south)
        f (ss/frame :title "Cadejo VKbd"
                    :content pan-main
                    :on-close :hide
                    :size frame-size)]
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
                              c0 (dec (get @properties* :channel))]
                          (.note-on vkbd-node c0 keynum vel)))
          
          up-action (fn [b _]
                      (let [id (.get-property b :id)
                            octave (get @properties* :octave)
                            keynum (+ (* 12 octave) id)
                            c0 (dec (get @properties* :channel))]
                        (.note-off vkbd-node c0 keynum)))]
      
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
    (sfactory/label drw pos-octave-label "Octave")
    (sfactory/label drw pos-channel-label "Channel")
    (sfactory/label drw pos-panic-label "Off" :size 5.0)
    (.render drw)
    (ss/show! f)
    (reset! editor* {:show (fn [](ss/show! f))
                     :status (fn [msg](ss/config! lab-status :text (str msg)))
                     :get-channel (fn [](get @properties* :channel))
                     :set-channel! (fn [c1]
                                     (swap! properties* (fn [q](assoc q c1))))})
    vkbd-node))
