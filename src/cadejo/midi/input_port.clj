(ns cadejo.midi.input-port
  (:require [cadejo.midi.node])
  (:require [cadejo.ui.cadejo-frame])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.midi :as midi-util])
  (:require [cadejo.util.string])
  (:require [cadejo.util.user-message :as umsg])
  (:require [overtone.midi :as ot-midi])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssf])
  )


(def ^:private font (ssf/font :name :monospaced :style :bold :size 16))

(deftype MidiInputPort [parent* children* properties*]
  cadejo.midi.node/Node

  (node-type [this] :midi-input-port)

  (is-root? [this] (= (.parent this) nil))

  (find-root [this]
    (if (.is-root? this)
      this
      (.find-root (.parent this))))
  
  (parent [this] @parent*)

  (children [this] @children*)

  (is-child? [this obj]
    (ucol/member? obj (.children this)))

  (add-child! [this child]
    (if (not (.is-child? this child))
      (do
        (swap! children* (fn [q](conj q child)))
        (._set-parent! child this)
        true)
      false))
        
  (remove-child! [this child]
    (if (.is-child? this child)
      (let [predicate (fn [q](not (= q child)))]
        (swap! children* (fn [q](into [] (filter predicate q))))
        (._orphan! child)
        true)
      false))

  (_set-parent! [this obj]
    (reset! parent* obj))

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
        (umsg/warning (format "Channel %s does not have property %s"
                              (.channel-number this) key))
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
  
  (get-editor [this] nil)

  ;; (rep-tree [this depth]
  ;;   (let [pad (cadejo.util.string/tab depth)
  ;;         sb (StringBuilder.)]
  ;;     (.append sb (format "%sMidiInputPort +%s\n" pad (.get-property this :id)))
  ;;     (doseq [p (.children this)]
  ;;       (.append sb (.rep-tree p (inc depth))))
  ;;     (.toString sb)))
)


(defn mip-editor [node]
  (let [cframe (cadejo.ui.cadejo-frame/cadejo-frame "MIDI Input" ""
                                                    [:progress-bar :status
                                                     ])
        device (.get-property node :midi-device)
        device-info (fn []
                      (let [info (.getDeviceInfo device)
                            dname (.getName info)
                            description (.getDescription info)
                            vendor (.getVendor info)
                            version (.getVersion info)
                            flag (.isOpen device)]
                        (str (format "MIDI Input\n\n" flag)
                             (format "Open        : %s\n" flag)
                             (format "Name        : %s\n" dname)
                             (format "Description : %s\n" description)
                             (format "Vendor      : %s\n" vendor)
                             (format "Version     : %s\n" version))))
        tree-info (fn []
                    (cadejo.midi.node/rep-tree node 0))
        txt (ss/text :text (device-info)
                     :multi-line? true :editable? false :font font)
        jb-tree (factory/icon-button :tree :info "Update tree info")
        ]
    (ss/listen jb-tree :action (fn [& _]
                                 (let [s (str (device-info)
                                              "\n\nCadejo Process Tree:\n"
                                              (tree-info))
                                       p (cadejo.midi.node/rep-path node)]
                                   (ss/config! txt :text s)
                                   (.set-path-text! cframe p))))
    (ss/config! (.widget cframe :toolbar) :items [jb-tree])
    (ss/config! (.widget cframe :pan-center) :center txt)
    (.show! cframe)
  ))

(defn midi-input-port
  ([parent device-name]
   (let [parent* (atom parent)
         children* (atom [])
         properties* (atom {})
         transmitter (ot-midi/midi-in device-name)
         port-node (MidiInputPort. parent* children* properties*)]
     (.put-property! port-node :id device-name)
                                        ;(.put-property! port-node :midi-device (.getMidiDevice transmitter))
     (.put-property! port-node :midi-device (:device transmitter))
     (ot-midi/midi-handle-events transmitter (.event-dispatcher port-node))
     (println (format "DEBUG %s" (type transmitter)))
     port-node))
  ([device-name]
   (midi-input-port nil device-name)))

;; TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST
;; TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST

(defn rl [](use 'cadejo.midi.input-port :reload))

(println (midi-util/list-midi-info))
(defonce mip (midi-input-port nil "[hw:1,0,0]"))

(def ed (mip-editor mip))
