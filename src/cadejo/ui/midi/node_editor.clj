(println "--> cadejo.ui.midi.node-editor")

(ns cadejo.ui.midi.node-editor
  "Defines interface for all 'editor' components"
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.cadejo-frame :as cframe])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssfont]))


(defprotocol NodeEditor

  (frame! 
    [this cframe embed]
    [this cframe])

  (frame
    [this])

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
  ) 

(def id-font-size 24)

 
(defn basic-node-editor 
  ([type-id client-node create-frame]
  "Provides basic frame work for 'editor' panels.
   The basic-editor-panel implements several methods of NodeEditor
   and provides the following components:
   :jb-parent  - JButton used to display/give focus to editor 
                 for the parent node. For root nodes (I.E. Scene)
                 jb-parent should be either removed or disabled.
   :jb-help    - JButton for displaying help
   :lab-id     - JLabel used to identify this editor
   :lab-status - JLabel for status and warning messages
   :lab-info   - JLabel for info text
   :pan-north  - JPanel holding lab-id, jb-parent and jb-help
   :pan-center - JPanel with BorderLayout. pan-center is left empty
                 for use by extending classes.
   :pan-main   - JPanel - the main outer panel holding all other components"
   
  (let [node* (atom client-node)
        frame* (atom (if create-frame
                       (let [cf (cframe/cadejo-frame (format "Cadejo %s" type-id)
                                                     (.get-property client-node :id))]
                         cf)
                       nil))
        widgets* (atom {
                        })
        ed (reify NodeEditor
             
             (frame! [this cframe embed]
               (reset! frame* cframe)
               ;; (if embed
               ;;   (let [pan (.widget cframe :pan-center)]
               ;;     (reset! pan-main* pan)))
               )

             (frame! [this cframe]
               (.frame! this cframe true))

             (widgets [this]
               (if @frame*
                 (assoc @widgets*
                        :frame (.widget @frame* :frame)
                        :pan-main (.widget @frame* :pan-main)
                        :pan-center (.widget @frame* :pan-center)
                        :jb-parent (.widget @frame* :jb-parent)
                        :jb-help (.widget @frame* :jb-help))
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
               (and @frame* (.working @frame* flag)))

             (set-path-text! [this msg]
               (and @frame* (.set-path-text! @frame* msg)))

             ;; (info-text! [this msg]
             ;;   (umsg/warning "NodeEditor.info-text! depreciated use set-path-text!")
             ;;   (.set-path-text! this msg))

             (status! [this msg]
               (and @frame* (.status! @frame* msg))
               msg)

             (warning! [this msg]
               (and @frame* (.warning! @frame* msg))
               msg) )]
    ed))
  ([type-id client-node]
   (basic-node-editor type-id client-node true)))

