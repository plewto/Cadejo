(println "--> cadejo.midi.scene")
(ns cadejo.midi.scene
  "A scene is the top-level cadejo structure. Each scene connects
   to a single MIDI in port and contains 16 MIDI channels."
  (:require [cadejo.config])
  (:require [cadejo.midi.node])
  (:require [cadejo.midi.channel])
  (:require [cadejo.midi.input-port :as mip])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])  
  (:require [cadejo.scale.registry])
  (:require [cadejo.ui.midi.scene-editor])
  (:require [cadejo.ui.vkbd :as vkbd])
  (:require [overtone.midi :as midi]))


(def channel-count (cadejo.config/channel-count))

(defprotocol SceneProtocol 
  
  (channel 
    [this ci]
    "Returns the channel object with MIDI channel ci 
     0 <= ci < 16, The result is an instance of cadejo.midi.channel/Channel")
  
  (scale-registry
    [this])

  (registered-tables
    [this]
    "Convenience method - returns sorted list of registered tuning tables")

  (reset
    [this]
    "Resets all channels which in turn resets all performances to initial 
     conditions.")

  (dump 
    [this filter verbose depth]
    [this filter verbose]
    [this filter]
    [this]
    "Displays information about this scene and it's child channels.
     The form with the depth argument is used internally.
     filter - Filter selects which channels to included. If filter
              is nil then all 16 channels are dumped. Otherwise channel must 
              be a list holding the channels to display.
     verbose - flag indicating if additional information is to be included."))
 
(deftype Scene [parent* channels* properties* sregistry editor*]
    cadejo.midi.node/Node

    (node-type [this] :scene)

    (is-root? [this] (not @parent*))

    (parent [this] @parent*)

    (children [this] @channels*)

    (is-child? [this obj]
      (ucol/member? obj @channels*))

    (add-child! [this obj]
      (if (and (not (.is-child? this obj))
               (= (.node-type obj) :channel))
        (do (swap! channels* (fn [q](conj q obj)))
            (.set-parent! obj this)
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

    ;; ignore local-only flag
    (properties [this local-only]
      (keys @properties*))

    (properties [this]
      (.properties this true))

    (get-editor [this]
      @editor*)

    (event-dispatcher [this]
      (fn [event]
        (let [ci (:channel event)]
          (if ci
            (let [chanobj (.channel this ci)
                  channel-event-handler (.event-dispatcher chanobj)]
              (channel-event-handler event))
            (do
              ;; FUTURE handle non-channel events here
              )
            ))))
    
    SceneProtocol 

    (channel [this ci]
      (nth @channels* ci))

    (scale-registry [this]
      sregistry)

    (registered-tables [this]
      (.registered-tables (.scale-registry this)))

    (reset [this]
      (doseq [c (.children this)]
        (.reset c)))

    (rep-tree [this depth]
      (let [pad (cadejo.util.string/tab depth)
            sb (StringBuilder. 300)]
        (.append sb (format "%sScene %s\n" pad (.get-property this :id)))
        (doseq [c (.children this)]
          (if (pos? (count (.performance-ids c)))
            (.append sb (.rep-tree c (inc depth)))))
        (.toString sb)))
                            

    (dump [this chan-filter verbose depth]
      (let [depth2 (inc depth)
            pad (cadejo.util.string/tab depth)
            pad2 (cadejo.util.string/tab depth2)
            filter (if chan-filter
                     (fn [n](let [chan (.get-property n :channel)]
                              (ucol/member? chan chan-filter)))
                     (fn [n] true))]
        (printf "%sScene %s\n" pad (.get-property this :id))
        (if verbose 
          (doseq [k (sort (.properties this))]
            (printf "%s[%-12s] --> %s\n" 
                    pad2 k (.get-property this k true))))
        (doseq [chanobj @channels*]
          (if (filter chanobj)
            (.dump chanobj verbose depth2)))))

    (dump [this filter verbose]
      (.dump this filter verbose 0))

    (dump [this filter]
      (.dump this filter true))

    (dump [this]
      (.dump this nil true)))


(defn- load-editor [scene]
  (if (cadejo.config/load-gui)
    (cadejo.ui.midi.scene-editor/scene-editor scene)
    nil))

(defn scene
  "Creates new scene object optionally connected to MIDI input-port or 
other node
Without arguments returns a scene object without parent node.

With single string argument a MIDI input port is created for the device
named in the string. This input port serves as the parent node for the scene.

If the argument is not a string it is assumed to be an appropriate node
which serves as the parent node for scene.

NOTE possible exceptions:  
  IllegalArgumentException if midi-input-device-name does not exists
  MidiUnavailableException if device in use"
  ([parent-or-device-name]
   (let [in-port (cond (string? parent-or-device-name)
                       (mip/midi-input-port parent-or-device-name)

                       (= nil parent-or-device-name)
                       nil 

                       :default ;; assume arg is node
                       parent-or-device-name)]
     (println (format "Creating scene %s" (.get-property in-port :id)))
     (let [sobj (scene)
           vkbd (vkbd/vkbd in-port sobj)]
       (.put-property! sobj :id (.get-property in-port :id))
       (.put-property! sobj :vkbd vkbd)
       sobj)))
  ([]
   (let [channels* (atom [])
         editor* (atom nil)
         properties* (atom {:id :new-scene
                            :velocity-map :linear
                            :scale-id :eq-12
                            :dbscale 0
                            :transpose 0
                            :key-range [0 127]
                            :bend-curve :linear
                            :bend-range 200
                            :pressure-curve :linear
                            :pressure-scale 1.0
                            :pressure-bias 0})
         sregistry (cadejo.scale.registry/scale-registry)
         parent* (atom nil)
         sobj (Scene. parent* channels* properties* sregistry editor*)]
     (reset! editor* (load-editor sobj))
     (dotimes [ci channel-count]
       (let [cobj (cadejo.midi.channel/channel sobj ci)]
         (swap! channels* (fn [n](conj n cobj)))))
     sobj)))

