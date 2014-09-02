(println "--> cadejo.config")
(ns cadejo.config
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.path :as path])
  (:require [cadejo.util.midi])
  (:import java.awt.Color))

(def ^:private +VERSION+ "0.1.1-SNAPSHOT")

(defprotocol CadejoConfig

  (version 
    [this]
    "Returns Cadejo version as string")
  
  (channel-count 
    [this]
    "Returns number of MIDI channels (16)")

  (midi-input-ports 
    [this]
    "Returns list of available MIDI input ports
    Note that (at least for the moment, 2014.08.15) MIDI ports are extracted
    from the JVM which is not Jack aware")

  (midi-input-port
    [this n]
    "Return the nth MIDI input device")

  (load-gui 
    [this]
    "Returns Boolean indicating if GUI components are to be used.")

  (load-gui!
    [this flag]
    "Sets flag indicating whether GUI components are to be use are not.")

  (enable-pp
    [this])

  (enable-pp!
    [this flag])

  (maximum-undo-count 
    [this]
    "Returns maximum depth of undo/redo operations")

  (maximum-undo-count!
    [this n]
    "Sets maximum depth of undo/redo operations")

  (warn-on-file-overwrite
    [this]
    "Returns Boolean indicating that a warning is to be displayed whenever 
    a file is about to be overwritten")

  (warn-on-file-overwrite!
    [this flag]
    "Sets overwrite warning status")

  (warn-on-unsaved-data
    [this])

  (warn-on-unsaved-data!
    [this flag])

  (enable-tooltips
    [this]
    "Returns Boolean indicating that GUI tooltips are to be used.
     Note that currently (2014.08.15) this flag is not universally enforced")

  (enable-tooltips!
    [this flag])

  (config-path 
    [this]
    "Returns file-system path to Cadejo configuration directory.
     The default directory is $home/.cadejo")

  (config-path!
    [this path]
    "Change path to configuration directory.")

  ;; (maximum-channel-children 
  ;;   [this])

  ;; (maximum-channel-children!
  ;;   [this n])

  (add-instrument! 
    [this descriptor]
    "Adds InstrumentDescriptor to the list of available instruments.")

  (instruments
    [this]
    "Returns list of available instruments")

  (instrument-descriptor 
    [this iname]
    "Return specific InstrumentDescriptor for iname
    If no such descriptor exist display warning and return nil")

  (create-instrument 
    [this iname mode args]
    "Create instance of instrument and link it into a Cadejo process tree.")

  )


(defn cadejo-config []
  (let [input-ports* (atom [])
        load-gui* (atom false)
        enable-pp* (atom false)
        max-undo-count* (atom 10)
        overwrite-warn* (atom true)
        unsaved-warn* (atom true)
        enable-tooltips* (atom true)
        config-path* (atom nil)
        instruments* (atom nil)
        cnfig (reify CadejoConfig

                (version [this] +VERSION+)

                (channel-count [this] 16)

                (midi-input-ports [this]
                  (cadejo.util.midi/transmitters))

                (midi-input-port [this n]
                  (let [tlst (.midi-input-ports this)]
                    (if (< n (count tlst))
                      (nth tlst n)
                      (umsg/warning "CadejoConfig.midi-input-port"
                                    (format "IndexdOutOfBounds %s" n)))))

                (load-gui [this] @load-gui*)

                (load-gui! [this flag]
                  (reset! load-gui* flag))
                  
                (enable-pp [this] @enable-pp*)

                (enable-pp! [this flag]
                  (reset! enable-pp* flag))

                (maximum-undo-count [this]
                  @max-undo-count*)

                (maximum-undo-count! [this n]
                  (reset! max-undo-count* (int n)))

                (warn-on-file-overwrite [this]
                  @overwrite-warn*)

                (warn-on-file-overwrite! [this flag]
                  (reset! overwrite-warn* flag))

                (warn-on-unsaved-data [this]
                  @unsaved-warn*)

                (warn-on-unsaved-data! [this flag]
                  (reset! unsaved-warn* flag))

                (enable-tooltips [this]
                  @enable-tooltips*)

                (enable-tooltips! [this flag]
                  (reset! enable-tooltips* flag))

                (config-path[this]
                  @config-path*)

                (config-path! [this p]
                  (reset! config-path* p))
         
                (add-instrument! [this descriptor]
                  (swap! instruments* (fn [n](assoc n
                                               (keyword (.instrument-name descriptor))
                                               descriptor))))
                (instruments [this]
                  (keys @instruments*))

                (instrument-descriptor [this iname]
                  (let [key (keyword iname)]
                    (or (get @instruments* key)
                        (umsg/warning (format "Instrument %s not defined" iname)))))

                (create-instrument [this iname mode args]
                  (let [ides (.instrument-descriptor this iname)]
                    (if ides
                      (let [s (first args)
                            sed (and (.load-gui this)(.get-editor s))]
                        (.create ides mode args)
                        (if sed (.sync-ui! sed))
                        s)))) )]


    (.config-path! cnfig (let [p (System/getProperties)
                               h (.getProperty p "user.home")]
                           (path/join h "cadejoConfig")))
    cnfig))



                
(def ^:private current-config* (atom (cadejo-config)))

(defn set-config! [this cnfg]
  (reset! current-config* cnfg))


(defn cadejo-version []
  (.version @current-config*))

(defn channel-count [] 
  (.channel-count @current-config*))


(defn midi-input-ports []
  (.midi-input-ports @current-config*))


(defn midi-input-port [n]
  (.midi-input-port @current-config* n))

(defn load-gui []
  (.load-gui @current-config*))

(defn load-gui! [flag]
  (.load-gui! @current-config* flag))


(defn enable-pp []
  (.enable-pp @current-config*))

(defn enable-pp! [flag]
  (.enable-pp! @current-config* flag))

(defn maximum-undo-count []
  (.maximum-undo-count @current-config*))

(defn maximum-undo-count! [n]
  (.maximum-undo-count! @current-config* n))

(defn warn-on-file-overwrite []
  (.warn-on-file-overwrite @current-config*))

(defn warn-on-file-overwrite! [flag]
  (.warn-on-file-overwrite! @current-config* flag))

(defn warn-on-unsaved-data []
  (.warn-on-unsaved-data @current-config*))

(defn warn-on-unsaved-data! [flag]
  (.warn-on-unsaved-data! @current-config* flag))

(defn enable-tooltips []
  (.enable-tooltips @current-config*))

(defn enable-tooltips! [flag]
  (.enable-tooltips! @current-config* flag))

(defn config-path []
  (.config-path @current-config*))

(defn config-path! [p]
  (.config-path! @current-config* p))

(defn add-instrument! [descriptor]
  (.add-instrument! @current-config* descriptor))

(defn instruments []
  (.instruments @current-config*))

(defn instrument-descriptor [iname]
  (.instrument-descriptor @current-config* iname))

(defn create-instrument [iname mode & args]
  (.create-instrument @current-config* iname mode args))


