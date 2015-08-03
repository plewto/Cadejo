(println "--> cadejo.ui.midi.node-editor")

(ns cadejo.ui.midi.node-editor
  "Defines interface for all 'editor' components"
  (:require [cadejo.config :as config])
  (:require [cadejo.midi.node])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.cadejo-frame :as cframe])
  (:require [cadejo.ui.util.child-dialog])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssfont]))


(defprotocol NodeEditor

  (cframe! 
    [this cframe embed]
    [this cframe])

  (cframe
    [this])

  (jframe
    [this])
    
  
  (set-icon! 
    [this ico])

  (show!
    [this])

  (hide!
    [this])

  (widgets 
    [this]
    "Returns map of gui widgets")

  (widget
    [this key]
    "Returns specific widget.
     If no such widget exists display warning and return nil")

  (add-widget!
    [this key obj])

  (node 
    [this]
    "Return the node object this editor is operating with")

  (set-node!
    [this n]
    "Set the node object this editor is to operate with.")

  (set-path-text!
    [this msg])

  ;; (info-text!
  ;;   [this msg]
  ;;   "DEPRECIATED Set text of info label")

  (working
    [this flag])

  (status! 
    [this msg]
    "Display status message")

  (warning! 
    [this msg]
    "Display warning message")

  (update-path-text [this]
    "Update the node path label") 
  ) 

(def id-font-size 24)

 
(defn basic-node-editor 
  "Provides basic framework for 'editor' panels."
  ([type-id client-node create-frame]
   (let [node* (atom client-node)
         cframe* (atom (if create-frame
                         (let [cf (cframe/cadejo-frame (format "Cadejo %s" type-id)
                                                       (.get-property client-node :id))
                               jb-parent (.widget cf :jb-parent)
                               jb-child (.widget cf :jb-child)]
                           (if jb-parent
                             (ss/listen jb-parent :action (fn [_]
                                                            (let [pnode (.parent @node*)
                                                                  ped (and pnode (.get-editor pnode))]
                                                              (and ped (.show! ped))))))
                           (if jb-child
                             (ss/listen jb-child :action (fn [_]
                                                           (cadejo.ui.util.child-dialog/child-dialog @node*))))
                           cf)
                         nil))
         widgets* (atom {})
         ed (reify NodeEditor
              
              (cframe! [this cframe embed]
                (reset! cframe* cframe)
                ;; (if embed
                ;;   (let [pan (.widget cframe :pan-center)]
                ;;     (reset! pan-main* pan)))
                )
              
              (cframe! [this cframe]
                (.cframe! this cframe true))

              (cframe [this]
                @cframe*)
              
              (jframe [this]
                (let [cf (.cframe this)]
                  (and cf (.jframe cf))))
              
              (set-icon! [this ico]
                (and @cframe* (.set-icon! @cframe* ico)))
              
              (widgets [this]
                (if @cframe*
                  (assoc @widgets*
                         :cframe @cframe*
                         :jframe (.widget @cframe* :jframe)
                         :pan-main (.widget @cframe* :pan-main)
                         :pan-center (.widget @cframe* :pan-center)
                         :jb-parent (.widget @cframe* :jb-parent)
                         :jb-help (.widget @cframe* :jb-help)
                         :toolbar (.widget @cframe* :toolbar)
                         :lab-id (.widget @cframe* :lab-id))
                  @widgets*))
              
              (widget [this key]
                (or (get (.widgets this) key)
                    (umsg/warning 
                     (format "%s NodeEditor does not have %s widget"
                             type-id key))))
              
              (add-widget! [this key obj]
                (swap! widgets* (fn [n](assoc n key obj))))
              
              (node [this] @node*)
              
              (set-node! [this n]
                (reset! node* n))
              
              (working [this flag]
                (and @cframe* (.working @cframe* flag)))
              
              (set-path-text! [this msg]
                (and @cframe* (.set-path-text! @cframe* msg)))
              
              (status! [this msg]
                (and @cframe* (.status! @cframe* msg))
                msg)
              
              (warning! [this msg]
                (and @cframe* (.warning! @cframe* msg))
                msg)

              (update-path-text [this]
                (let [pt (cadejo.midi.node/rep-path client-node)
                      cf (.cframe this)]
                  (if cf (.set-path-text! pt))
                  (doseq [c (.children client-node)]
                    (let [ced (.get-editor c)]
                      (if ced (update-path-text ced)))))) )]
     ed))
  ([type-id client-node]
   (basic-node-editor type-id client-node true)))

