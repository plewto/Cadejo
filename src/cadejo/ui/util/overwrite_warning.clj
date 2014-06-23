(ns cadejo.ui.util.overwrite-warning
  "Pops up confirmation dialog prior to overwriting existing file.
   Overwrite warnings may be disabled by config"
  (:require [cadejo.util.path :as path])
  (:require [cadejo.config :as config])
  (:use [seesaw.core]))

(defn overwrite-warning 
  ([parent file-type filename]
     (if (or (not (config/warn-on-file-overwrite))
             (not (path/exists filename)))
       true
       (let [selection* (atom false)
             msg (format "Overwrite %s file '%s'" file-type filename)
             yes-fn (fn [d] 
                      (swap! selection* (fn [n] true))
                      (return-from-dialog d true))
             no-fn (fn [d] 
                     (swap! selection* (fn [n] false))
                     (return-from-dialog d false))
             dia (dialog 
                  :content msg
                  :option-type :yes-no
                  :type :warning
                  :default-option :no
                  :modal? true
                  :parent parent
                  :success-fn yes-fn
                  :no-fn no-fn)]
         (pack! dia)
         (show! dia)
         @selection*)))
  ([parent filename]
     (overwrite-warning parent "" filename)))
