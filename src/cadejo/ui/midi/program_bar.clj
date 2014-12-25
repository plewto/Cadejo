(println "--> cadejo.ui.midi.program-bar")

(ns cadejo.ui.midi.program-bar
  "The program bar is a JPanel with a large display for the current 
   program name and number. It also includes contans buttons for storing 
   the current program into a program bank slot. A low priority thread
   updates the program-name display to indicate if the current program has
   unsaved data."
  (:use [cadejo.util.trace])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.color-utilities :as cutil])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.config :as config])
  (:require [seesaw.core :as ss])
  (:require [sgwr.drawing])
  (:require [sgwr.indicators.displaybar])
  (:require [clojure.string])
  (:import javax.swing.Box))

(def ^:private name-length 12)
(def ^:private name-drawing-width 370)
(def ^:private name-drawing-height 65)
(def ^:private prognum-drawing-width 100)
(def ^:private prognum-drawing-height name-drawing-height)
(def ^:private pan-east-size [385 :by 65])
(def ^:private update-period 10000) ;; 'modified' indicator update period in ms

(defprotocol ProgramBar

  (widgets
    [this])

  (widget 
    [this key])
  
  (sync-ui!
    [this]))

(defn program-bar [performance]
  (let [prognum* (atom 0)
        bank (.bank performance)
        ;; jb-init (factory/button "Init" :general :reset "Initialize program")
        ;; jb-dice (factory/button "Random" :general :dice "Generate random program")
        jb-inc (factory/icon-button :mini :up1 "Increment program number")
        jb-dec (factory/icon-button :mini :down1 "Decrement program number")
        jb-inc-page (factory/icon-button :mini :up2 "Increment program number page")
        jb-dec-page (factory/icon-button :mini :down2 "Decrement program number page")
        jb-store (factory/button "Store" :general :bankstore "Store current program")
        [bg inactive active alt](config/displaybar-colors)
        drawing (let [drw (sgwr.drawing/native-drawing 570 65)]
                  (.paper! drw bg)
                  (.color! drw inactive)
                  (.style! drw 8)
                  drw)
        modified-marker (.point! drawing [140 16])
        dbar-name (sgwr.indicators.displaybar/displaybar drawing name-length :matrix 150 8)
        dbar-prognum (sgwr.indicators.displaybar/displaybar drawing 3 :matrix 8 8)

        pan-buttons (ss/grid-panel :rows 2 :columns 2
                                   :items [jb-inc jb-inc-page 
                                           jb-dec jb-dec-page])
        pan-west (ss/horizontal-panel :items [;; jb-init jb-dice 
                                              ;; (Box/createHorizontalStrut 16)
                                              pan-buttons jb-store]
                                      :border (factory/padding))

        pan-center (ss/horizontal-panel :items [(.drawing-canvas drawing)]
                                        :border (factory/padding))
   
        pan-main (ss/border-panel :west pan-west
                                  :center pan-center)
        display-prognum (fn []
                          (.display! dbar-prognum (format "%03d" @prognum*)))
        
        inc-prognum (fn [n]
                      (swap! prognum* (fn [p](min (+ p n) 127)))
                      (display-prognum))
        
        dec-prognum (fn [n]
                      (swap! prognum* (fn [p](max (- p n) 0)))
                      (display-prognum))

        cpb (reify ProgramBar
              
              (widgets [this]
                {:pan-main pan-main})

              (widget [this key]
                (or (get (.widgets this) key)
                    (umsg/warning (format "ProgramBar does not have %s widget" key))))

              (sync-ui! [this]
                (let [modified (.modified? bank)
                      prog (.current-program bank)
                      pname (clojure.string/upper-case (or (and prog (.program-name prog)) "---"))
                      name-limit (min name-length (count pname))]
                  (reset! prognum* (or (.current-slot bank) 0))
                  (.color! (.attributes modified-marker)
                           (if modified active inactive))
                  (display-prognum)
                  (.display! dbar-name (subs pname 0 name-limit))
                  (.pad! dbar-name))))

        update-thread (proxy [Thread][]
                             (run []
                               (while true
                                 (let [modified (.modified? bank)
                                       att (.attributes modified-marker)
                                       new-color (cutil/color (if modified active inactive))
                                       old-color (cutil/color (.color att))]
                                   (if (not (= old-color new-color))
                                     (do 
                                       (.color! att new-color)
                                       (.render drawing)))
                                 (Thread/sleep update-period))))) ]

    (ss/listen jb-inc :action (fn [_](inc-prognum 1)))
    (ss/listen jb-inc-page :action (fn [_](inc-prognum 8)))
    (ss/listen jb-dec :action (fn [_](dec-prognum 1)))
    (ss/listen jb-dec-page :action (fn [_](dec-prognum 8)))
    (ss/listen jb-store :action
               (fn [_]
                 (let [bank-ed (.editor bank)
                       prog (.current-program bank)
                       slot @prognum*]
                   (.push-undo-state! bank-ed
                                      (format "Store program %s" slot))
                   (.store! bank slot (.clone prog))
                   (.sync-ui! bank-ed)
                   (.status! (.get-editor performance) (format "Stored program %s" slot)))))
    ;; (ss/listen jb-init :action (fn [_]
    ;;                              (.init-program (.get-editor performance))))
    ;; (ss/listen jb-dice :action (fn [_]
    ;;                              (.random-program (.get-editor performance))))
    (let [[bg inactive active alt](config/displaybar-colors)]
      (.colors! dbar-name bg inactive active)
      (.colors! dbar-prognum bg inactive active))
    (.setName update-thread "ProgramBar-update")
    (.setPriority update-thread 1)
    (.setDaemon update-thread true)
    (.start update-thread)
    cpb))
