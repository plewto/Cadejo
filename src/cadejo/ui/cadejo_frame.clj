(println "--> cadejo.ui.cadejo-frame")
(ns cadejo.ui.cadejo-frame
  "cadejo-frame defines an extension to javax.JFrame with common layout
  and toolbars. The frame is divided into three vertical sections.
  From top to bottom these are a toolbar, central client area and
  status bar. 

  The toolbar optionally contains the following buttons:
  jb-parent - Open frame for parent node. 
              Parent button does not have pre-defined action
  jb-child  - Open dialog to display child nodes for this
              Child button does not have pre-defined action
  jb-skin   - Open a skin dialog.
  jb-help   - Open context help
  jb-about  - Display about dialog
  jb-exit   - Exit Cadejo

  The widget set includes the above buttons as well as the following 

  :lab-id     - JLabel used for id text/icon
  :lab-status - Status line JLabel
  :lab-path   - JLabel used for tree path display
  :progress-bar
  :pan-north  - JPanel holding toolbars
  :pan-south  - JPanel holding progress-bar status and path labels
  :pan-center - JPanel with BorderLayout reserved for client use
  :toolbar    - JToolbar reserved for client use 
  :toolbar-east - JToolbar which holds standard buttons above
  :jframe     - Instance of JFrame

  Widgets which are excluded from the cadejo-frame still have keys in
  the widgets dictionary, their values however will be nil"
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.about-dialog])
  (:require [cadejo.ui.util.exit-dialog])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss]))

(defprotocol CadejoFrame
  (widgets
    [this]
    "Returns dictionary with following keys
     :jb-exit
     :jb-about
     :jb-parent
     :jb-child
     :jb-help
     :jb-skin
     :lab-id
     :lab-status
     :lab-path
     :progress-bar
     :pan-north
     :pan-south
     :pan-center
     :toolbar
     :toolbar-east
     :jframe")

  (widget
    [this key]
    "Returns widget specified by key")

  (help-topic!
    [this topic]
    "Sets help topic")
  
  (jframe
    [this]
    "Returns underlying javax.JFrame")
  
  (show!
    [this]
    "Make frame visible")

  (hide!
    [this]
    "Hide frame")

  (size!
    [this w h]
    "Set size of JFrame")
  
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


(defn cadejo-frame
  "Creates new instance of CadejoFrame
   title   - String 
   id      - String or keyword
   exclude - list of keywords indicating which standard elements are to be 
             excluded. Possible values include
             :id     - Do not use ID label
             :exit   - Do not include exit button
             :about  - Do not include about button
             :help   - Do not include help button
             :parent - Do not include parent button
             :child  - Do not include child button
             :skin   - Do not include skin button
             :status - Do not include status label
             :path   - Do not include path label
             :progress-bar - Do not include progress bar
             :toolbar-east - Do not include east toolbar.
                             Excluding the east toolbar implicitly
                             excludes the exit, about, help, parent,
                             and skin buttons.
             :toolbar      - Do not include client toolbar
             :pan-north    - Do not include north panel. 
                             Excluding the north panel implicitly
                             excludes all toolbars
             :pan-south    - Do not include the south panel.
                             Excluding the south panel implicitly
                             excludes the progress bar and status and
                             path labels."
  ([title]
   (cadejo-frame title nil [:lab-id]))

  ([title id]
   (cadejo-frame title id nil))

   ([title id exclude]
    (let [exclude? (fn [key obj]
                     (if (ucol/member? key exclude)
                       nil
                       obj))
          lab-id (exclude? :id (ss/label :text (str id)
                                         :h-text-position :center
                                         :v-text-position :bottom))
          jb-exit (exclude? :exit (let [jb (factory/button "Exit" :general :exit "Quit Cadejo/Overtone")]
                                    (ss/listen jb :action cadejo.ui.util.exit-dialog/exit-cadejo)
                                    jb))
          jb-about (exclude? :about (let [jb (factory/button "About" :general :info "About Cadejo")]
                                      (ss/listen jb :action cadejo.ui.util.about-dialog/about-dialog)
                                      jb))
          jb-help (exclude? :help (let [jb (factory/button "Help" :general :help "Display context help")]
                                    (ss/listen jb :action cadejo.ui.util.help/help-action)
                                    jb))
          jb-parent (exclude? :parent (factory/button "Parent" :tree :up "Display parent"))
          jb-child (exclude? :child (factory/button "Child" :tree :down "Display child"))
          jb-skin (exclude? :skin (let [jb (factory/button "Skin" :general :skin "Open skin selector")]
                                    (ss/listen jb :action lnf/skin-dialog)
                                    jb))
          lab-status (exclude? :status (ss/label :text " "))
          pan-status (and lab-status (ss/vertical-panel :items [lab-status] :border (factory/bevel 2)))
                                        
          lab-path (exclude? :path (ss/label :text " "))
          pan-path (and lab-path (ss/vertical-panel :items [lab-path] :border (factory/bevel 2)))
          progbar (exclude? :progress-bar (ss/progress-bar :indeterminate? false))
          pan-progbar (and progbar (ss/vertical-panel :items [progbar] :border (factory/bevel 2)))
          tbar-east (exclude? :toolbar-east (ss/toolbar :floatable? false 
                                                     :items (filter (fn [q] q)
                                                                    [:separator jb-parent jb-child jb-skin jb-help jb-about jb-exit nil])))
          tbar-client (exclude? :toolbar (ss/toolbar :floatable? false 
                                                         :items []))
          pan-north (exclude? :pan-north (let [pn (ss/border-panel)]
                                           (if lab-id (ss/config! pn :west lab-id))
                                           (if tbar-client (ss/config! pn :center tbar-client))
                                           (if tbar-east (ss/config! pn :east tbar-east))
                                           (ss/config! pn :border (factory/padding))
                                           pn))
          pan-center (ss/border-panel
                      :border (factory/padding))
          pan-south (exclude? :pan-south
                              (ss/grid-panel 
                               :rows 1
                               :items (filter (fn [q] q)
                                              [pan-status pan-progbar pan-path])
                               :border (factory/bevel 4)))
          pan-main (let [pn (ss/border-panel :center pan-center)]
                     (if pan-north (ss/config! pn :north pan-north))
                     (if pan-south (ss/config! pn :south pan-south))
                     pn)
                             
          jframe (ss/frame :title (format "Cadejo %s" title)
                           :content pan-main
                           :size [700 :by 300])
          widgets* (atom {:jb-exit jb-exit
                          :jb-about jb-about
                          :jb-parent jb-parent
                          :jb-child jb-child
                          :jb-help jb-help
                          :jb-skin jb-skin
                          :lab-id lab-id
                          :lab-status lab-status
                          :lab-path lab-path
                          :progress-bar progbar
                          :pan-north pan-north
                          :pan-south pan-south
                          :pan-main pan-main
                          :pan-center pan-center
                          :toolbar tbar-client
                          :toolbar-east tbar-east
                          :jframe jframe})
          cframe (reify CadejoFrame
                   
                   (widgets [this] @widgets*)
                   
                   (widget [this key]
                     (or (get (.widgets this) key)
                         (umsg/warning (format "CadejoFrame does not have %s widget" key))))
                   
                   (help-topic! [this topic]
                     (and jb-help (.putClientProperty jb-help :topic topic)))
                   
                   (jframe [this]
                     (:jframe @widgets*))
                   
                   (show! [this]
                     (ss/show! jframe))
                   
                   (hide! [this]
                     (ss/hide! jframe))

                   (size! [this w h]
                     (ss/config! jframe :size [w :by h]))
                   
                   (set-icon! [this ico]
                     (ss/config! jframe :icon ico)
                     (and lab-id (ss/config! lab-id :icon ico)))
                   
                   (set-id-text! [this msg]
                     (and lab-id (ss/config! lab-id :text (str msg))))
                   
                   (set-path-text! [this msg]
                     (and
                      lab-path
                      (ss/config! lab-path :text (format "%s " msg))))
                   
                   (working [this flag]
                     (and progbar
                          (ss/config! progbar :indeterminate? flag)))
                   
                   (status! [this msg]
                     (and lab-status
                          (ss/config! lab-status :text (format " %s " msg))))
                   
                   (warning! [this msg]
                     (.status! this (format "WARNING: %s" msg))))]
      cframe)))
