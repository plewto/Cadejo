(println "--> cadejo.ui.midi.node-editor")

(ns cadejo.ui.midi.node-editor
  "Defines interface for all 'editor' components"
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:require [seesaw.font :as ssfont]))


(defprotocol NodeEditor

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

  (info-text!
    [this msg]
    "Set text of info label")

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

 
(defn basic-node-editor [type-id client-node]
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
        lab-id (ss/label 
                :text (format " %s %s "
                              (name type-id) (.get-property @node* :id))
                :font (ssfont/font :size id-font-size))
        jb-parent (factory/button "Parent" :tree :up "Display parent window")
        jb-help (factory/button "Help" :general :help "Display context help")

        pan-tools (ss/toolbar :floatable? false
                              :items [ :separator jb-parent jb-help])
        pan-north (ss/border-panel 
                   :west (ss/vertical-panel :items [lab-id]
                                            :border (factory/bevel 4))
                   :center pan-tools
                   :border (factory/padding))
        pan-center (ss/border-panel
                    :border (factory/padding))
        progress-bar (ss/progress-bar :indeterminate? false)
        lab-status (ss/label :text " ")
        pan-status (ss/vertical-panel :items [lab-status]
                                   :border (factory/bevel))
        lab-info (ss/label :text " ")
        pan-info (ss/vertical-panel :items [lab-info]
                                 :border (factory/bevel))
        pan-south (ss/grid-panel :rows 1 :items [pan-status progress-bar pan-info]
                                 :border (factory/bevel 4))
        pan-main (ss/border-panel :north pan-north
                               :center pan-center
                               :south pan-south)
        editor-frame (ss/frame :title (format "Cadejo %s Editor" (name type-id))
                               :content pan-main
                               :size [700 :by 300])
        widgets* (atom {:jb-parent jb-parent
                        :jb-help jb-help
                        :lab-id lab-id
                        :lab-status lab-status
                        :lab-info lab-info
                        :pan-north pan-north
                        :pan-center pan-center
                        :pan-main pan-main
                        :frame editor-frame})
        ed (reify NodeEditor
             
             (widgets [this] 
               @widgets*)

             (widget [this key]
               (or (get @widgets* key)
                   (umsg/warning 
                    (format "%s NodeEditor does not have %s widget"
                            type-id key))))
             
             (add-widget! [this key obj]
               (swap! widgets* (fn [n](assoc n key obj))))

             (node [this] @node*)

             (set-node! [this n]
               (reset! node* n))

             (working [this flag]
               (ss/config! progress-bar :indeterminate? flag))

             (info-text! [this msg]
               (ss/config! lab-info :text (format "PATH: %s" msg)))

             (status! [this msg]
               (ss/config! lab-status :text msg)
               msg)

             (warning! [this msg]
               (.status! this (format "WARNING! %s" msg))) )]
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)

    ;; START DEBUG
    (ss/listen jb-help :action (fn [_]
                                 (println (ss/config editor-frame :size))))
    ;; END DEBUG

    ed))

