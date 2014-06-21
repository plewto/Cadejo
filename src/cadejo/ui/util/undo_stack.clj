(ns cadejo.ui.util.undo-stack
  "Provides undo/redo stack with associated JButton"
  (:require [seesaw.core :as ss])
  (:require [cadejo.ui.util.icon])
  (:require [cadejo.util.user-message])
  (:require [cadejo.config]))

(defprotocol UndoStackProtocol

  (max-depth 
    [this]
    "Returns the maximum number of undos")
  
  (is-empty?
    [this]
    "Boolean true if stack is empty")

  (push-state
    [this obj]
    "Push object to stack, enable button")
  
  (pop-state
    [this]
    "Pop and return top stack item.
     If stack initially empty print warning and return nil
     If stack becomes empty as result of popping disable button")

  (get-button 
    [this]
    "Returns instance of JButton.
     The button is enabled/disabled state changes automatically with 
     stack contents. It is up to the client to add listeners to the button
     for extracting stack elements."))

;(def default-maximum-undo-depth 10)

(defrecord UndoStack [maximum-depth stack* button]
  UndoStackProtocol

  (max-depth [this]
    maximum-depth)

  (is-empty? [this]
    (= 0 (count @stack*)))
  
  (push-state [this obj]
    (swap! stack* (fn [n](conj n obj)))
    (while (> (count @stack*) maximum-depth)
      (swap! stack* butlast))
    (ss/config! button :enabled? true)
    @stack*)

  (pop-state [this]
    (if (not (.is-empty? this))
      (let [rs (first @stack*)]
        (swap! stack* rest)
        (ss/config! button :enabled? (not (.is-empty? this)))
        rs)
      (do 
        (cadejo.util.user-message/warning "Nothing to undo")
        nil)))

  (get-button [this]
    button))


(defn undo-stack [label & {:keys [as-icon max-depth]
                           :or {as-icon false
                                max-depth (cadejo.config/maximum-undo-count)}}]
  "Creates instance of UndoStack.
   label     - button text or icon name
   as-icon   - Boolean, if as-icon is false button text is set to label
               argument. If as-icon is true label is used as icon name 
               relative to resources/icon. Default false.
   max-depth - int, maximum undos, default 10"

  (let [b (if as-icon 
            (ss/button :icon (cadejo.ui.util.icon/icon label) :enabled? false)
            (ss/button :text label :enabled? false))]
    (UndoStack. max-depth 
                (atom '())
                b)))
