(println "--> cadejo.ui.cadejo-frame")

(ns cadejo.ui.cadejo-frame

  "Provides JFrame with common layout divided into three vertical
   sections. The north section contains a tool-bar.  On the left is
   identifying label with optional icon.  On the right are common
   buttons for parent node access and context help. The center of
   tool-bar is reserved for client's use.

   Immediately below the tool-bar is a large empty border-panel reserved
   for the client's use.
  
   The very bottom frame contains a status line, progress bar and
   informative 'path' label"

  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help :as help])
  (:require [seesaw.core :as ss]))

(defprotocol CadejoFrame

  (widgets
    [this]
    "Returns dictionary of frame's components. at a minium the following
     keys are defined
     :jb-parent - button to access parent node
     :jb-help - context help button
     :toolbar - instance of JToolbar, the client's toolbar
     :pan-center - instance of Jpanel with Borderlayout
     :frame - instance of JFrame")

  (widget
    [this key]
    "Returns widget specified by key")

  (show!
    [this]
    "Make frame visible")

  (hide!
    [this]
    "Hide frame")

  (set-icon!
    [this icn]
    "Set frame and id-label icon")

  (set-id-text!
    [this msg]
    "Set id-label text")

  (set-path-text! 
    [this msg])

  (working
    [this flag]
    "If flag true switch progress-bar into 'busy' mode")

  (status!
    [this msg]
    "Set status line text")

  (warning!
    [this msg]
    "Display warning message on status line"))


(defn cadejo-frame [title id]
  (let [lab-id (ss/label :text (str id)
                         :h-text-position :center
                         :v-text-position :bottom)
        jb-parent (factory/button "Parent" :tree :up "Display parent")
        jb-help (factory/button "Help" :general :help "Display context help")
        lab-status (ss/label :text "<status>")
        lab-path (ss/label :text "<path>")
        progbar (ss/progress-bar :indeterminate? false)
        tbar-fixed (ss/toolbar :floatable? false 
                               :items [:separator jb-parent jb-help])
        tbar-client (ss/toolbar :floatable? false 
                                :items [])
        pan-north (ss/border-panel :west lab-id 
                                   :center tbar-client
                                   :east tbar-fixed
                                   :border (factory/padding))
        pan-center (ss/border-panel
                    :border (factory/padding))
        pan-south (ss/horizontal-panel
                   :items [(ss/vertical-panel :items [lab-status] :border (factory/bevel 2))
                           (ss/vertical-panel :items [progbar] :border (factory/bevel 2))
                           (ss/vertical-panel :items [lab-path] :border (factory/bevel 2))]
                   :border (factory/bevel 4))
        pan-main (ss/border-panel 
                  :north pan-north
                  :south pan-south
                  :center pan-center)
        frame (ss/frame :title (format "Cadejo %s" title)
                        :content pan-main
                        :size [700 :by 300])
        cframe (reify CadejoFrame

                 (widgets [this]
                   {:jb-parent jb-parent
                    :jb-help jb-help
                    :toolbar tbar-client
                    :pan-main pan-main
                    :pan-center pan-center
                    :frame frame})

                 (widget [this key]
                   (or (get (.widgets this) key)
                       (umsg/warning (format "CadejoFrame does not have %s widget" key))))

                 (show! [this]
                   (ss/show! frame))

                 (hide! [this]
                   (ss/hide! frame))

                 (set-icon! [this ico]
                   (ss/config! frame :icon ico)
                   (ss/config! lab-id :icon ico))

                 (set-id-text! [this msg]
                   (ss/config! lab-id :text (str msg)))

                 (set-path-text! [this msg]
                   (ss/config! lab-path :text (format " %s " msg)))

                 (working [this flag]
                   (ss/config! progbar :indeterminate? flag))

                 (status! [this msg]
                   (ss/config! lab-status :text (format " %s " msg)))

                 (warning! [this msg]
                   (.status! this (format "WARNING: %s" msg))))]
    cframe)) 
