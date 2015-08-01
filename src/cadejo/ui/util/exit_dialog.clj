(ns cadejo.ui.util.exit-dialog
  (:require [cadejo.config :as config])
  (:require [seesaw.core :as ss]))

(defn- exit-warning []
  (let [flag* (atom false)]
    (if (config/warn-on-exit)
      (let [dia (ss/dialog :content (ss/label :text "Exit Overtone/Cadejo ?")
                           :option-type :yes-no
                           :default-option :no
                           :success-fn (fn [_](reset! flag* true))
                           :no-fn (fn [_](reset! flag* false)))]
        (ss/config! dia :size [200 :by 200])
        (ss/show! dia)
        @flag*)
      true)))

(defn exit-cadejo [& _]
  "Exit Cadejo/Overtone 
   If (config/warn-on-exit) returns true
   Ask User for confirmation prior to terminating application"
  (if (exit-warning)
    (do
      (println ";; Exiting Cadejo ...")
      (System/exit 0))
    (do
      (println ";; Cadejo Exit Canceled"))))
