(ns cadejo.ui.util.about-dialog
  (:require [cadejo.about])
  (:require [cadejo.ui.util.icon])
  (:require [seesaw.core :as ss]))

(defn about-dialog [& _]
  (let [txt (ss/text :multi-line? true
                     :editable? false
                     :text cadejo.about/about-text)
        lab (ss/label :icon (cadejo.ui.util.icon/splash-image 4))
        pan-main (ss/grid-panel :rows 2 :columns 2
                                :items [lab txt])
        jb-ok (ss/button :text "OK")
        dia (ss/dialog :content pan-main
                       :options [jb-ok]
                       :title "About Cadejo"
                       :type :plain)]
    (ss/listen jb-ok :action (fn [_](.dispose dia)))
    (ss/config! dia :size [520 :by 400])
    (ss/show! dia)))
        
