(ns cadejo.instruments.alias.editor.envelope-editor
    (:require [cadejo.ui.instruments.subedit :as subedit])
    (:require [cadejo.ui.addsr2-editor :as addsr2])
    (:require [cadejo.ui.util.lnf :as lnf])
    (:require [seesaw.core :as ss]))

(def ^:private width 500)
(def ^:private height 400)

(def ^:private params-1 {:attack :env1-attack 
                         :decay1 :env1-decay1
                         :decay2 :env1-decay2
                         :release :env1-release
                         :peak :env1-peak
                         :breakpoint :env1-breakpoint
                         :sustain :env1-sustain})

(def ^:private params-2 {:attack :env2-attack 
                         :decay1 :env2-decay1
                         :decay2 :env2-decay2
                         :release :env2-release
                         :peak :env2-peak
                         :breakpoint :env2-breakpoint
                         :sustain :env2-sustain})

(def ^:private params-3 {:attack :env3-attack 
                         :decay1 :env3-decay1
                         :decay2 :env3-decay2
                         :release :env3-release
                         :peak :env3-peak
                         :breakpoint :env3-breakpoint
                         :sustain :env3-sustain})

(defn env-editor [ied]
  (let [view* (atom [[-0.1 -0.1][8.0 1.1]])
        env1 (addsr2/addsr2-editor params-1 :env1 ied :group-view* view* :w width :h height)
        env2 (addsr2/addsr2-editor params-2 :env2 ied :group-view* view* :w width :h height)
        env3 (addsr2/addsr2-editor params-3 :env3 ied :group-view* view* :w width :h height)
        pan1 (ss/border-panel :center (.widget env1 :canvas)
                              :north (ss/label :text "Env 1" :halign :center))
        pan2 (ss/border-panel :center (.widget env2 :canvas)
                              :north (ss/label :text "Env 2" :halign :center))
        pan3 (ss/border-panel :center (.widget env3 :canvas)
                              :north (ss/label :text "Env 3 (main amp)" :halign :center))
        pan-center (ss/horizontal-panel :items [pan1 pan2 pan3])
        jb-zoom-out (ss/button :text "Zoom Out" :icon (lnf/read-icon :view :out))
        jb-zoom-in (ss/button :text "Zoom In" :icon (lnf/read-icon :view :in))
        jb-zoom-reset (ss/button :text "Default View" :icon (lnf/read-icon :view :reset))
        pan-toolbar (ss/toolbar :items [jb-zoom-out jb-zoom-in jb-zoom-reset])
        pan-main (ss/scrollable (ss/border-panel :center pan-center
                                                 :south pan-toolbar))]
    (ss/listen jb-zoom-out :action (fn [_] 
                                     (let [t0 (first (second @view*))]
                                       (reset! view* [[-0.1 -0.1][(* 1.5 t0) 1.1]])
                                       (doseq [e [env1 env2 env3]]
                                         ((.widget e :sync-to-view))))))
    (ss/listen jb-zoom-in :action (fn [_] 
                                    (let [t0 (first (second @view*))]
                                      (reset! view* [[-0.1 -0.1][(* 0.75 t0) 1.1]])
                                      (doseq [e [env1 env2 env3]]
                                        ((.widget e :sync-to-view))))))
    (ss/listen jb-zoom-reset :action (fn [_] 
                                       (let [t0 8.0]
                                         (reset! view* [[-0.1 -0.1][t0 1.1]])
                                         (doseq [e [env1 env2 env3]]
                                           ((.widget e :sync-to-view))))))
    (subedit/subeditor-wrapper [env1 env2 env3] pan-main)))
        
       
        
