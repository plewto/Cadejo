(ns cadejo.ui.scale.registry-editor
  "Provides ScaleRegistry GUI"
  (:require [cadejo.config :as config])
  (:require [cadejo.scale.registry])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [cadejo.ui.util.undo-stack])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.help])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.scale.eqtemp])
  (:require [cadejo.ui.scale.addscale])
  (:require [cadejo.ui.scale.subeditors :as subeditors])
  (:require [seesaw.chooser])
  (:require [seesaw.border :as ssb])
  (:require [seesaw.core :as ss]))

(def current-filename* (atom ""))

(def filefilter (let [ext cadejo.scale.registry/file-extension
                      description "Cadejo scale registry files"]
                  (seesaw.chooser/file-filter
                   description
                   (fn [f]
                     (path/has-extension? (.getAbsolutePath f) ext)))))

(defn open-dialog [ed registry]
  (let [ext cadejo.scale.registry/file-extension
        success (fn [jfc f]
                  (let [abs (path/replace-extension (.getAbsolutePath f) ext)]
                    (.push-undo-state! ed (format "Open %s" abs))
                    (if (.read-registry! registry abs)
                      (do
                        (reset! current-filename* abs)
                        (.sync-ui! ed)
                        (.status! ed (format "Opened registry '%s'"  abs)))
                      (.warning! ed (format "Can not open '%s' as registry" 
                                            abs)))))
        cancel (fn [jfc]
                 (.status! ed "Open registry canceled"))
        dia (seesaw.chooser/choose-file
             :type :open
             :dir @current-filename*
             :multi? false
             :selection-mode :files-only
             :filters [filefilter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]))

(defn save-dialog [ed registry]
  (let [ext cadejo.scale.registry/file-extension
        success (fn [jfc f]
                  (let [abs (path/append-extension (.getAbsolutePath f) ext)]
                    (if (cadejo.ui.util.overwrite-warning/overwrite-warning
                         (.widget ed :pan-main) "Scale registry" abs)
                      (if (.write-registry registry abs)
                        (do 
                          (reset! current-filename* abs)
                          (.status! ed (format "Saved registry file %s" abs)))
                        (.warning! ed (format "Can not save registry to '%s'"
                                             abs)))
                      (.status! ed "Save canceled"))))
        cancel (fn [jfc]
                 (.status! ed "Registry save canceled"))
        default-file @current-filename*
        dia (seesaw.chooser/choose-file
             :type :save
             :dir default-file
             :multi? false
             :selection-mode :files-only
             :filters [filefilter]
             :remember-directory? true
             :success-fn success
             :cancel-fn cancel)]))

(defprotocol RegistryEditor

  (widgets 
    [this])

  (widget 
    [this key])

  (sync-ui!
    [this])

  (selected-table-id
    [this])

  (selected-table
    [this])

  (registry 
    [this])

  (push-undo-state!
    [this action])

  (push-redo-state!
    [this action])

  (undo! 
    [this])

  (redo! 
    [this])

  (status!
    [this msg])

  (warning!
    [this msg])

  (keyrange
    [this]
    "Returns pair [lower upper]")

  (enable-keyrange!
    [this flag])

  (wraprange
    [this]
    "Returns pair [lower upper]")

  (enable-wrap!
    [this flag]))

(defn registry-editor [scene]
  (let [sregistry (.scale-registry scene)
        undo-stack (cadejo.ui.util.undo-stack/undo-stack "Undo")
        redo-stack (cadejo.ui.util.undo-stack/undo-stack "Redo")
        enable-change-listeners* (atom true)
        ;; North toolbar
        jb-init (ss/button)
        jb-open (ss/button)
        jb-save (ss/button)
        jb-undo (.get-button undo-stack)
        jb-redo (.get-button redo-stack)
        jb-help (ss/button)
        jb-delete (ss/button)
        group (ss/button-group)
        jtb-add (ss/radio :group group :selected? true)
        jtb-edit (ss/radio :group group)
        jtb-detail (ss/radio :group group)

        pan-toolbar (ss/toolbar :floatable? false
                                :items [jb-init jb-open jb-save 
                                        :separator jb-undo jb-redo
                                        :separator jb-delete jtb-add jtb-edit jtb-detail
                                        :separator jb-help])
        ;; West registered scale list
        lst-registry (ss/listbox :model (.registered-tables sregistry)
                              :size [200 :by 300])
        pan-west (ss/vertical-panel :items [(ss/scrollable lst-registry)]
                                 :border (factory/title "Registered Scales"))
        ;; Edit limits
        spin-range-low (ss/spinner :model (ss/spinner-model
                                           0 :from 0 :to 127 :by 1))
        spin-range-high (ss/spinner :model (ss/spinner-model
                                            127 :from 0 :to 127 :by 1))
        spin-wrap-low (ss/spinner :model (ss/spinner-model
                                          0 :from 0 :to 200 :by 10))
        spin-wrap-high (ss/spinner :model (ss/spinner-model
                                           20000
                                           :from 4000 :to 20000 :by 1000))
        pan-range-low (ss/vertical-panel :items [spin-range-low]
                                         :border (factory/title "Low"))
        pan-range-high (ss/vertical-panel :items [spin-range-high]
                                          :border (factory/title "High"))
        pan-range (ss/vertical-panel :items [pan-range-low pan-range-high]
                                     :border (factory/title "Key range"))
        pan-wrap-low (ss/vertical-panel :items [spin-wrap-low]
                                        :border (factory/title "Low"))
        pan-wrap-high (ss/vertical-panel :items [spin-wrap-high]
                                         :border (factory/title "High"))
        pan-wrap (ss/vertical-panel :items [pan-wrap-low pan-wrap-high]
                                    :border (factory/title "Wrap"))
        pan-limits (ss/vertical-panel :items [pan-range pan-wrap])
        ;; South toolbar
        group (ss/button-group)
        ;; jb-delete (ss/button)
        ;; jtb-add (ss/toggle :group group :selected? true)
        ;; jtb-edit (ss/toggle :group group)
        ;; jtb-detail (ss/toggle :group group)
        ;; pan-south (ss/toolbar :floatable? false
        ;;                       :items [jb-delete jtb-add 
        ;;                               jtb-edit jtb-detail])
        ;; Main Panels
        pan-subedit (ss/card-panel :border (factory/line))
        pan-center (ss/border-panel :west pan-limits
                                 :center pan-subedit)
        pan-main (ss/border-panel :north pan-toolbar
                               ;:south pan-south
                               :west pan-west
                               :center pan-center)
        ;; splice labels
        lab-splice-source (ss/label :text " "
                                    :border (factory/title "Source"))
        lab-splice-destination (ss/label :text " "
                                      :border (factory/title "Destination"))
        widget-map {:jb-init jb-init
                    :jb-open jb-open
                    :jb-save jb-save
                    :jb-undo jb-undo
                    :jb-redo jb-redo
                    :jb-delete jb-delete
                    :jtb-add jtb-add
                    :jtb-edit jtb-edit
                    :jtb-detail jtb-detail
                    :lab-splice-source lab-splice-source
                    :lab-splice-destination lab-splice-destination
                    :lst-registry lst-registry
                    :pan-main pan-main
                    :pan-range pan-range
                    :pan-range-low pan-range-low
                    :pan-range-high pan-range-high
                    :pan-wrap pan-wrap
                    :pan-wrap-low pan-wrap-low
                    :pan-wrap-high pan-wrap-high
                    :spin-range-low spin-range-low
                    :spin-range-high spin-range-high
                    :spin-wrap-low spin-wrap-low
                    :spin-wrap-high spin-wrap-high}
        ed (reify RegistryEditor
             
             (widgets [this] widget-map)

             (widget [this key]
               (or (get widget-map key)
                   (umsg/warning (format "RegistryEditor does not have %s widget" key))))

             (sync-ui! [this]
               (reset! enable-change-listeners* false)
               (ss/config! lst-registry :model (.registered-tables sregistry))
               (reset! enable-change-listeners* true))

             (selected-table-id [this]
               (.getSelectedValue lst-registry))

             (selected-table [this]
               (let [id (.selected-table-id this)]
                 (if id
                   (.table sregistry id)
                   nil)))

             (registry [this] sregistry)

             (push-undo-state! [this action]
               (let [state (.clone sregistry)]
                 (.push-state! undo-stack [action state])))

             (push-redo-state! [this action]
               (let [state (.clone sregistry)]
                 (.push-state! redo-stack [action state])))

             (undo! [this]
               (if (not (.is-empty? undo-stack))
                 (let [[action state](.pop-state! undo-stack)]
                   (.push-redo-state! this action)
                   (.copy-state! sregistry state)
                   (.sync-ui! this)
                   (.status! this (format "Undo %s" action)))
                 (.warning! this "Nothing to undo")))

             (redo! [this]
               (if (not (.is-empty? redo-stack))
                 (let [[action state](.pop-state! redo-stack)]
                   (.push-undo-state! this action)
                   (.copy-state! sregistry state)
                   (.sync-ui! this)
                   (.status! this (format "Redo %s" action)))
                 (.warning! this "Nothing to redo")))

             (status! [this msg]
               (let [sed (.get-editor scene)]
                 (.status! sed msg)))

             (warning! [this msg]
               (let [sed (.get-editor scene)]
                 (.warning! sed msg)))

             (keyrange [this]
               (let [a (.getValue spin-range-low)
                     b (.getValue spin-range-high)]
                 [(min a b)(max a b)]))

             (enable-keyrange! [this flag]
               (.setEnabled spin-range-low flag)
               (.setEnabled spin-range-high flag)
               (.setEnabled pan-range-low flag)
               (.setEnabled pan-range-high flag)
               (.setEnabled pan-range flag))

             (wraprange [this]
               (let [a (.getValue spin-wrap-low)
                     b (.getValue spin-wrap-high)]
                 [(min a b)(max a b)]))

             (enable-wrap! [this flag]
               (.setEnabled spin-wrap-low flag)
               (.setEnabled spin-wrap-high flag)
               (.setEnabled pan-wrap-low flag)
               (.setEnabled pan-wrap-high flag)
               (.setEnabled pan-wrap flag)))]

    (if (config/enable-button-text)
      (do
        (ss/config! jb-init :text "Reset")   
        (ss/config! jb-open :text "Open")   
        (ss/config! jb-save :text "Save")   
        (ss/config! jb-delete :text "Delete")   
        (ss/config! jtb-add :text "Add Tables")
        (ss/config! jtb-edit :text "Edit")
        (ss/config! jtb-detail :text "Detail")
        (ss/config! jb-help :text "Help")))

    (if (config/enable-button-icons)
      (do 
        (.setIcon jb-init (lnf/read-icon :general :reset))
        (.setIcon jb-open (lnf/read-icon :general :open))
        (.setIcon jb-save (lnf/read-icon :general :save))
        (.setIcon jb-delete (lnf/read-icon :general :delete))
        (.setIcon jtb-add (lnf/read-icon :general :add))
        (.setIcon jtb-edit (lnf/read-icon :edit nil))
        (.setIcon jtb-detail (lnf/read-icon :general :detail))
        (.setIcon jb-help (lnf/read-icon :general :help))
        (.setSelectedIcon  jb-init (lnf/read-selected-icon :general :reset))
        (.setSelectedIcon  jb-open (lnf/read-selected-icon :general :open))
        (.setSelectedIcon  jb-save (lnf/read-selected-icon :general :save))
        (.setSelectedIcon  jb-delete (lnf/read-selected-icon :general :delete))
        (.setSelectedIcon  jtb-add (lnf/read-selected-icon :general :add))
        (.setSelectedIcon  jtb-edit (lnf/read-selected-icon :edit nil))
        (.setSelectedIcon  jtb-detail (lnf/read-selected-icon :general :detail))
        (.setSelectedIcon  jb-help (lnf/read-selected-icon :general :help))))
    
    (if (config/enable-tooltips)
      (do
        (.setToolTipText jb-init "Initialize scale registry")
        (.setToolTipText jb-open "Read scale registry file")
        (.setToolTipText jb-save "Save scale registry file")
        (.setToolTipText jb-delete "Remove tuning-table from registry")
        (.setToolTipText jtb-add "Add tuning-tables to registry")
        (.setToolTipText jtb-edit "Edit tuning-table")
        (.setToolTipText jtb-detail "Detail edito tuning-table")
        (.setToolTipText jb-help "Scale registry help")))

    (ss/listen jb-init :action
            (fn [_]
              (.push-undo-state! ed "Initialize Registry")
              (doseq [k (.registered-tables sregistry)]
                (.remove-table! sregistry k))
              (.add-table! sregistry :eq-12 cadejo.scale.eqtemp/default-table)
              (ss/config! lab-splice-source :text " ")
              (ss/config! lab-splice-destination :text " ")
              (.sync-ui! ed)
              (.status! ed "Initialized Scale Registry")))

    (ss/listen jb-open :action (fn [_]
                              (open-dialog ed (.registry ed))))

    (ss/listen jb-save :action (fn [_]
                              (save-dialog ed (.registry ed))))

    (ss/listen jb-undo :action (fn [_](.undo! ed)))

    (ss/listen jb-redo :action (fn [_](.redo! ed)))
              
    (ss/listen jb-delete :action
            (fn [_]
              (let [id (.selected-table-id ed)]
                (if id 
                  (do 
                    (.push-undo-state! ed (format "Delete %s" id))
                    (.remove-table! sregistry id)
                    (ss/config! lab-splice-source :text " ")
                    (ss/config! lab-splice-destination :text " ")
                    (.sync-ui! ed)
                    (.status! ed (format "Deleted scale %s" id)))
                  (warning! ed "No scale selected for deletion")))))
                                      
    
    (let [addscale-subeditor (cadejo.ui.scale.addscale/addscale ed)
          linear-subeditor (subeditors/linear-editor ed)
          transpose-subeditor (subeditors/transpose-editor ed)
          splice-subeditor (subeditors/splice-editor ed lab-splice-source
                                                     lab-splice-destination)
          pan-edit (ss/grid-panel :rows 1
                                    :items [linear-subeditor
                                            transpose-subeditor
                                            splice-subeditor])]
      (ss/add! pan-subedit [addscale-subeditor :addscale])
      (ss/add! pan-subedit [pan-edit :transpose])
      (ss/add! pan-subedit [(ss/label :text "Nothing to see here") :detail]))

    ;; update splice source & destination labels
    (ss/listen lst-registry :selection 
            (fn [_] 
              (if (and @enable-change-listeners*
                       (not (.getValueIsAdjusting lst-registry)))
                (do
                  (ss/config! lab-splice-destination :text
                           (ss/config lab-splice-source :text))
                  (ss/config! lab-splice-source :text
                           (name (.getSelectedValue lst-registry)))))))

    (ss/listen jtb-add :action 
            (fn [_]
              (.enable-keyrange! ed false)
              (.enable-wrap! ed false)
              (ss/show-card! pan-subedit :addscale)))
    
    (ss/listen jtb-edit :action 
            (fn [_]
              (.enable-keyrange! ed true)
              (.enable-wrap! ed true)
              (ss/show-card! pan-subedit :transpose)))

    (ss/listen jtb-detail :action
            (fn [_]
              (ss/show-card! pan-subedit :detail)
              (.warning! ed "Detail editor not implemented")))

    (.putClientProperty jb-help :topic :scale-registry)
    (ss/listen jb-help :action cadejo.ui.util.help/help-listener)

    ed))
