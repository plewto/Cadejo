(ns cadejo.ui.util.help)


(declare display-topic)

(defn help-listener [ev]
  (let [src (.getSource ev)
        topic (.getClientProperty src :topic)]
    (display-topic topic)))
  

(defn display-topic [topic]
  (println "help topic " topic))



