(println "--> cadejo.ui.midi.program-bar")

(ns cadejo.ui.midi.program-bar
  (:require [cadejo.config :as config])
  (:require [cadejo.ui.util.sgwr-factory :as sf :reload true ])
  (:require [cadejo.util.math :as math])
  (:require [seesaw.core :as ss])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.ui.util.overwrite-warning])
  (:require [clojure.string])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [sgwr.components.line :as line]))

(def ^:private width 1800)
(def ^:private height 140)
(def ^:private program-name-count 16)
(def ^:private x-slot 40)
(def ^:private x-inc8 (+ x-slot 90))
(def ^:private x-inc1 (+ x-inc8 30))
(def ^:private x-store (+ x-inc1 40))
(def ^:private x-name (+ x-slot 220))
(def ^:private x-modified (- x-name 0))
(def ^:private x-init (+ x-slot 720))
(def ^:private x-open (+ x-init 50))
(def ^:private x-save (+ x-open 50))
(def ^:private x-undo (+ x-save 50))
(def ^:private x-redo (+ x-undo 50))
(def ^:private y-dbar 50)
(def ^:private y-inc (- y-dbar 15))
(def ^:private y-dec (+ y-dbar 15))
(def ^:private y-store (- y-inc 0))
(def ^:private y-modified (- y-dbar 10))
(def ^:private y-init (- y-dbar 10))
(def ^:private y-open y-init)
(def ^:private y-save y-init)
(def ^:private y-undo y-init)
(def ^:private y-redo y-init)
(def ^:private update-period 4000)

(defn program-bar [bank-editor]
  (let [drw (sf/sgwr-drawing width height)
        bank (.bank bank-editor)
        file-extension (.toLowerCase (name (.data-format bank)))
        current-slot* (atom 0)
        increment-slot (fn [x]
                         (swap! current-slot* (fn [q]
                                                (math/clamp (+ q x) 0 127))))
        dbar-slot (let [db (sf/displaybar drw [x-slot y-dbar] 3)]
                    (.display! db (format "%3d" @current-slot*) false)
                    db)
        
        txt-modified (let [txtobj (sf/label drw [x-modified y-modified] "Modified"
                                           :size 5.0)]
                       (.color! txtobj :dirty (lnf/text))
                       (.color! txtobj :clean (lnf/background))
                       txtobj)

        program-store-action (fn [& _] 
                               (let [prog (.current-program bank)
                                     slot @current-slot*]
                                 (.push-undo-state! bank-editor (format "Store Program %s" slot))
                                 (.store! bank slot (.clone prog))
                                 (.sync-ui! bank-editor)
                                 (.status! bank-editor (format "Stored Program %s" slot))))


        dbar-name (let [db (sf/displaybar drw [x-name y-dbar] program-name-count)]
                    (.display! db "CADEJO" false)
                    db)

        init-action (fn [& _]
                      (.init-bank! bank-editor))

        
        open-action (fn [& _]
                      (.open-bank-dialog! bank-editor))
        
        save-action (fn [& _]
                      (.save-bank-dialog bank-editor))


        undo-action (fn [& _](.undo! bank-editor))
        
        redo-action (fn [& _](.redo! bank-editor))

        syncfn (fn [] 
                 (let [modified (.modified? bank)
                       prog (.current-program bank)
                       pname (clojure.string/upper-case (or (and prog (.program-name prog)) "---"))
                       name-limit (min program-name-count (count pname))
                       prognum (or (.current-slot bank) 0)]
                   (reset! current-slot* prognum)
                   (.display! dbar-slot (format "%3d" prognum) false)
                   (.display! dbar-name (subs pname 0 name-limit) false)
                   (.render drw)))

        update-thread (proxy [Thread][]
                          (run []
                            (while true
                              (.use-attributes! txt-modified (if (.modified? bank) :dirty :clean))
                              (.render drw)
                              (Thread/sleep update-period))))]

    ;; slot increment buttons
    (let [slot-action (fn [b _]
                         (let [id (.get-property b :id)
                               value (get {:up1 1, :up8 8, :down1 -1, :down8 -8} id)]
                           (increment-slot value)
                           (.display! dbar-slot (format "%3d" @current-slot*) true)))]
         (sf/mini-chevron-up-button  drw [x-inc1 y-inc] :up1 slot-action)
         (sf/mini-chevron-up2-button drw [x-inc8 y-inc] :up8 slot-action)
         (sf/mini-chevron-down-button drw [x-inc1 y-dec] :down1 slot-action)
         (sf/mini-chevron-down2-button drw [x-inc8 y-dec] :down8 slot-action))
    (sf/program-store-button drw [x-store y-store] :progam-store program-store-action)
    (sf/init-button drw [x-init y-init] :bank-init init-action)
    (sf/open-button drw [x-open y-open] :bank-open open-action)
    (sf/save-button drw [x-save y-save] :bank-save save-action)
    (sf/undo-button drw [x-undo y-undo] :bank-undo undo-action)
    (sf/redo-button drw [x-redo y-redo] :bank-redo redo-action)
    (sf/label drw [x-slot y-dbar] "Program Slot" :size 5.0 :offset [0 50])
    (sf/label drw [x-store y-store] "Store" :size 5.0 :offset [0 50])
    (sf/label drw [x-init y-init] "Init" :size 5.0 :offset [12 55])
    (sf/label drw [x-open y-open] "Open" :size 5.0 :offset [12 55])
    (sf/label drw [x-save y-save] "Save" :size 5.0 :offset [12 55])
    (sf/label drw [x-undo y-undo] "Undo" :size 5.0 :offset [12 55])
    (sf/label drw [x-redo y-redo] "Redo" :size 5.0 :offset [12 55])
    (sf/label drw [x-init y-init] "Bank" :size 7.0 :offset [105 80])
    (let [x1 x-init
          x2 (- x-save 10)
          x3 (+ x-save 50)
          x4 (+ x-redo 43)
          y1 (+ y-init 76)
          c (lnf/minor-border)]
      (line/line (.root drw) [x1 y1][x2 y1] :color c)
      (line/line (.root drw) [x3 y1][x4 y1] :color c))
    (.render drw)
    (.setName update-thread "ProgramBar-update")
    (.setPriority update-thread 1)
    (.setDaemon update-thread true)
    (.start update-thread)
    (let [pbar {:pan-main (ss/border-panel :center (.canvas drw))
                :drawing drw
                :syncfn syncfn}]
      (.set-program-bar! bank-editor pbar)
      pbar))) 
