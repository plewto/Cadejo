(ns cadejo.ui.util.undo-stack
  "Provides undo/redo stack with associated JButton"
  (:require [seesaw.core :as ss])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.config])
  (:import javax.swing.SwingConstants))


(defprotocol UndoStack

  (max-depth
    [this]
    "Return maximum number of undo operations")

  (is-empty?
    [this]
    "Predicate true if stack empty")

  (push-state!
    [this obj]
    "Push obj to stack, enable buttons")

  (pop-state!
    [this]
    "Pop and return top stack item.
     If stack empty print warning, return nil
     Disable buttons if stack empty after popping value")

  (clear-stack!
    [this])

  (get-button 
    [this]
    "Each call to get-button returns a new instance of JButton
     The buttons enable/disable state changes with stack contents."))

(defn undo-stack [label]
  (let [maximum-depth (cadejo.config/maximum-undo-count)
        stack* (atom [])
        buttons* (atom nil)]
    (reify UndoStack

      (max-depth [this] maximum-depth)

      (is-empty? [this]
        (zero? (count @stack*)))

      (push-state! [this obj]
        (swap! stack* (fn [n](conj n obj)))
        (doseq [b @buttons*]
          (.setEnabled b true))
        @stack*)

      (pop-state! [this]
        (if (not (.is-empty? this))
          (let [rs (first @stack*)]
            (swap! stack* rest)
            (doseq [b @buttons*]
              (.setEnabled b (not (.is-empty? this))))
            rs)
          (umsg/warning (format "Nothing to %s" label))))

      (clear-stack! [this]
        (.reset stack* [])
        (doseq [b @buttons*]
          (.setEnabled b false)))

      (get-button [this]
        (let [jb (ss/button)]
          (.setEnabled jb false)
          (swap! buttons* (fn [n](conj n jb)))
          (if (.equalsIgnoreCase label "undo")
            (do
              (if (cadejo.config/enable-button-text)
                (ss/config! jb :text label))
              (if (cadejo.config/enable-button-icons)
                (do
                  (.setIcon jb (lnf/read-icon :general :undo))
                  (.setSelectedIcon jb (lnf/read-selected-icon :general :undo))))
              (if (cadejo.config/enable-tooltips)
                (.setToolTipText jb "Undo")))
            (do
              (if (cadejo.config/enable-button-text)
                (ss/config! jb :text label))
              (if (cadejo.config/enable-button-icons)
                (do
                  (.setIcon jb (lnf/read-icon :general :redo))
                  (.setSelectedIcon jb (lnf/read-selected-icon :general :redo))))
              (if (cadejo.config/enable-tooltips)
                (.setToolTipText jb "Redo"))))
          (.setVerticalTextPosition jb SwingConstants/BOTTOM)
          (.setHorizontalTextPosition jb SwingConstants/CENTER)
          jb)))))
                
     
