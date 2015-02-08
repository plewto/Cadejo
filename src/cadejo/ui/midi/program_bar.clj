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
  (:require [sgwr.components.drawing])
  (:require [sgwr.components.point])
  (:require [sgwr.indicators.displaybar])
  (:require [sgwr.tools.button :as sb])
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
        [bg inactive active alt](config/displaybar-colors)
        drawing (let [drw (sgwr.components.drawing/native-drawing 611 65)]
                  (.background! drw bg)
                  drw)
        root (.root drawing)
        tool-root (.tool-root drawing)
        modified-marker (let [pnt (sgwr.components.point/point root [234 16] 
                                                             :color inactive
                                                             :style [:triangle]
                                                             :size 3)]
                          (.color! pnt :dirty active)
                          (.color! pnt :clean inactive)
                          (.use-attributes! pnt :clean)
                          pnt)

        dbar-prognum (sgwr.indicators.displaybar/displaybar root 6 16 3 :matrix)
        dbar-name (sgwr.indicators.displaybar/displaybar root 250 16 name-length :matrix)
       
        pan-center (ss/horizontal-panel :items [(.canvas drawing)]
                                        :border (factory/padding))
        pan-main (ss/border-panel :center pan-center)
        display-prognum (fn [render?]
                          (.display! dbar-prognum (format "%03d" @prognum*) render?))

        inc-prognum (fn [n]
                      (swap! prognum* (fn [p](min (+ p n) 127)))
                      (display-prognum :render))
        
        dec-prognum (fn [n]
                      (swap! prognum* (fn [p](max (- p n) 0)))
                      (display-prognum :render))

        store-program (fn [& _]
                        (let [bank-ed (.editor bank)
                              prog (.current-program bank)
                              slot @prognum*]
                          (.push-undo-state! bank-ed
                                             (format "Store program %s" slot))
                          (.store! bank slot (.clone prog))
                          (.sync-ui! bank-ed)
                          (.status! (.get-editor performance)
                                    (format "Stored program %s" slot))))

        sb-prefix :gray
        sb-inc (sb/mini-icon-button tool-root [107 4] sb-prefix :up1 
                                    :click-action (fn [& _](inc-prognum 1)))
        sb-dec (sb/mini-icon-button tool-root [107 34] sb-prefix :down1
                                    :click-action (fn [& _](dec-prognum 1)))
        sb-inc-page (sb/mini-icon-button tool-root [137 4] sb-prefix :up2
                                         :click-action (fn [& _](inc-prognum 8)))
        sb-dec-page (sb/mini-icon-button tool-root [137 34] sb-prefix :down2
                                         :click-action (fn [& _](dec-prognum 8)))
        sb-store (sb/icon-button tool-root [170 10] sb-prefix :general :bankstore
                                 :click-action store-program)
        

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
                  (display-prognum false)
                  (.display! dbar-name (subs pname 0 name-limit) false)
                  (.render drawing))))

        update-thread (proxy [Thread][]
                             (run []
                               (while true
                                 (let [modified (.modified? bank)
                                       current-color (.color modified-marker)]
                                   (if modified
                                     (.use-attributes! modified-marker :dirty)
                                     (.use-attributes! modified-marker :clean))
                                   (if (not (= (.color modified-marker) current-color))
                                     (.render drawing))
                                   (Thread/sleep update-period)))))
        ]
 
    ;; (ss/listen jb-inc :action (fn [_](inc-prognum 1)))
    ;; (ss/listen jb-inc-page :action (fn [_](inc-prognum 8)))
    ;; (ss/listen jb-dec :action (fn [_](dec-prognum 1)))
    ;; (ss/listen jb-dec-page :action (fn [_](dec-prognum 8)))
    ;; (ss/listen jb-store :action
    ;;            (fn [_]
    ;;              (let [bank-ed (.editor bank)
    ;;                    prog (.current-program bank)
    ;;                    slot @prognum*]
    ;;                (.push-undo-state! bank-ed
    ;;                                   (format "Store program %s" slot))
    ;;                (.store! bank slot (.clone prog))
    ;;                (.sync-ui! bank-ed)
    ;;                (.status! (.get-editor performance) (format "Stored program %s" slot)))))
    (let [[bg inactive active alt](config/displaybar-colors)]
      (.colors! dbar-name inactive active)
      (.colors! dbar-prognum inactive active)
      (.setName update-thread "ProgramBar-update")
      (.setPriority update-thread 1)
      (.setDaemon update-thread true)
      (.start update-thread)
      cpb)))
