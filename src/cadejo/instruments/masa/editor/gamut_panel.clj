(ns cadejo.instruments.masa.editor.gamut-panel
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.util.col :as ucol])
  (:require [cadejo.util.math :as math])
  ;(:require [sgwr.components.line :as line])
  ;(:require [sgwr.components.rule :as rule])
  (:require [sgwr.components.text :as text])
  ;(:require [sgwr.tools.slider :as slider])
  (:require [sgwr.tools.button :as button])
  (:require [sgwr.tools.multistate-button :as msb])
  ;(:require [sgwr.util.color :as uc])
  (:import javax.swing.JOptionPane))


;; (defn- help-action [& _]
;;   (println "MASA gamut-panel help not implemented")
;;   )

(def ^:private param-list [:r1 :r2 :r3 :r4 :r5 :r6 :r7 :r8 :r9])


;; (defn- zip [a b]
;;   (map vector a b))

(defn- use-b3-gamut [ied drw]
  (let [flst [0.5 1.5 1.0 2.0 3.0 4.0 5.0 6.0 8.0]]
    (doseq [[p f](ucol/zip param-list flst)]
      (.set-param! ied p f))
    (.sync-ui! ied)
    (.status! ied "Using B3 gamut")
    (.render drw)))
    
(defn- use-harmonic-gamut [ied drw]
  (let [flst [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]]
    (doseq [[p f](ucol/zip param-list flst)]
      (.set-param! ied p f))
    (.status! ied "Using harmonic gamut")
    (.sync-ui! ied)))

(defn- use-odd-gamut [ied drw]
  (let [flst [0.5 1.5 2.5 3.5 4.5 6.5 7.5 8.5 9.5]]
    (doseq [[p f](ucol/zip param-list flst)]
      (.set-param! ied p f))
    (.status! ied "Using odd gamut")
    (.sync-ui! ied)))

(defn- use-prime-gamut [ied drw]
  (let [flst [0.5 1.0 1.5 2.5 3.5 5.5 6.5 8.5 9.5]]
    (doseq [[p f](ucol/zip param-list flst)]
      (.set-param! ied p f))
    (.status! ied "Using prime gamut")
    (.sync-ui! ied)))

;; Mostly harmonic
(defn- random-1-gamut [ied drw]
  (let [p-harmonic 0.80
        pick (fn [f]
               (math/coin p-harmonic f (math/approx f 0.2)))
        flst [(pick 1.0) (pick 2.0) (pick 3.0) 
              (pick 4.0) (pick 5.0) (pick 6.0) 
              (pick 7.0) (pick 8.0) (pick 9.0)]]
    (doseq [[p f](ucol/zip param-list (sort flst))]
      (.set-param! ied p f))
    (.status! ied "Using random-1 gamut (mostly harmonic)")
    (.sync-ui! ied)))

(defn- random-2-gamut [ied drw]
  (let [p-harmonic 0.80
        pick (fn [](+ 0.5 (rand 8)))
        flst* (atom [])]
    (dotimes [i 9](swap! flst* (fn [q](conj q (pick)))))
    (doseq [[p f](ucol/zip param-list (sort @flst*))]
      (.set-param! ied p f))
    (.status! ied "Using random-2 gamut (inharmonic)")
    (.sync-ui! ied)))

(defn- cluster-gamut [ied drw]
  (let [h* (atom 1) ;; current harmonic
        p 0.8 ;; prop of keeping current harmonic
        flst* (atom [])]
    (dotimes [i 9]
      (let [f (math/approx @h* 0.005)]
        (swap! flst* (fn [q](conj q f)))
        (swap! h* (fn [q](math/coin p q (inc q))))))
    (doseq [[p f](ucol/zip param-list (sort @flst*))]
      (.set-param! ied p f))
    (.status! ied "Using kluster gamut")
    (.sync-ui! ied)))

(defn- detune-gamut [ied drw]
  (let [dmap (.current-data (.bank (.parent-performance ied)))
        pr 0.75 ;; prop of not changing frequency
        dev 0.01 ;; Maximum deviation
        flst* (atom [])]
    (doseq [p param-list]
      (let [f (get dmap p)]
        (swap! flst* (fn [q](conj q (math/coin pr f (math/approx f dev)))))))
    (doseq [[p f](ucol/zip param-list (sort @flst*))]
      (.set-param! ied p f))
    (.status! ied "Detuned gamut")
    (.sync-ui! ied))) 

(def ^:private gamut-preset-map 
  {:b3 use-b3-gamut
   :harmonic use-harmonic-gamut
   :odd use-odd-gamut
   :prime use-prime-gamut
   :rnd1 random-1-gamut
   :rnd2 random-2-gamut
   :cluster cluster-gamut
   :detune detune-gamut})

;; Create label text
;;    
(defn- freq-text [drw p0 id]
  (let [x-offset 15
        y-offset 495
        x (+ (first p0) x-offset)
        y (- (second p0) y-offset)
        txt (text/text (.root drw) [x y] "x.xxxx"
                       :id id
                       :color (lnf/text-color)
                       :style :mono
                       :size 6.0)]
    txt))

;; Create button for drawbar edit selection
;;
(defn- drawbar-selection-button [drw ied rbl* p0 id]
  (let [click-action (fn [b _] 
                      (let [dmap (.current-data (.bank (.parent-performance ied)))
                            param (.get-property b :id)
                            freq (get dmap param)
                            msg (format "Set drawbar %s frequency" (name param))
                            jop (JOptionPane.)
                            rs (math/str->float (JOptionPane/showInputDialog jop msg freq))]
                        (if (and rs (pos? rs))
                          (do 
                            (.set-param! ied param rs)
                            (.sync-ui! (.get-property b :editor))
                            (.render drw))
                          (.warning! ied (format "Invalid drawbar frequency %s" rs)))))
        w 32
        h 24
        x-shift 8
        y-shift 125
        x (+ (first p0) x-shift)
        y (- (second p0) y-shift)
        unselected-color (lnf/text-color)
        selected-color (lnf/text-selected-color)
        states [[:unselected (str id) unselected-color]
                [:selected (str id) selected-color]]
        b (msb/text-multistate-button (.tool-root drw) [x y] states
                                      :id (keyword (format "r%d" id))
                                      :click-action click-action
                                      :rim-color (lnf/button-border-color)
                                      :w w :h h
                                      :gap 12)]
    b))

;; Create button for gamut presets
;;
(defn- preset-button [drw p0 row col txt id action]
  (let [w 60
        h 26
        col-gap 4
        col-width (+ col-gap w)
        row-gap 12
        row-height (* -1 (+ row-gap h))
        pos [(+ (first p0)(* col col-width)) (- (second p0)(* row row-height))]
        b (button/text-button (.tool-root drw) pos txt
                              :id id
                              :click-action action
                              :w w :h h :gap 4
                              :rim-width 1
                              :rim-color (lnf/button-border-color)
                              :text-style :mono 
                              :text-size 6
                              :text-color (lnf/text-color))]
    b))
  


(defn gamut-panel [drw ied p0]
  (let [data (fn [param]
               (let [dmap (.current-data (.bank (.parent-performance ied)))]
                 (get dmap param)))
        [x0 y0] p0
        pan-w 458
        pan-h 140
        p1 [(+ x0 pan-w)(- y0 pan-h)]
        rbl* (atom [])
        txt-freq (let [acc* (atom [])
                       y (+ y0 100)]
                   (dotimes [i 9]
                     (let [x (+ x0 0 (* i slider-spacing))
                           txobj (freq-text drw [x y] i)]
                       (swap! acc* (fn [q](conj q txobj)))))
                   @acc*)]
    (dotimes [i 9]
      (let [x (+ x0 (* i slider-spacing))
            y y0
            b (drawbar-selection-button drw ied rbl* [x y] (inc i))]
        (swap! rbl* (fn [q](conj q b)))))
    (let [x1 (+ x0 190)
          y1 (- y0 85)
          pos [x1 y1]
          click-action (fn [b _]
                         (let [id (.get-property b :id)
                               gfn (get gamut-preset-map id use-b3-gamut)]
                           (gfn ied drw)))
          widget-map {}]
      (preset-button drw pos 0 0 "   B3  " :b3  click-action)
      (preset-button drw pos 0 1 "  Harm " :harmonic click-action)
      (preset-button drw pos 0 2 "  Odd  " :odd click-action)
      (preset-button drw pos 0 3 " Prime " :prime click-action)
      (preset-button drw pos 1 0 " Rnd 1 " :rnd1 click-action)
      (preset-button drw pos 1 1 " Rnd 2 " :rnd2 click-action)
      (preset-button drw pos 1 2 " Klstr "  :cluster click-action)
      (preset-button drw pos 1 3 " Detune"  :detune click-action)
      ;; Help button
      ;; (button/mini-icon-button (.tool-root drw) [(+ x0 8)(- y0 47)]
      ;;                          (lnf/icon-prefix) :help
      ;;                          :click-action help-action
      ;;                          :rim-color (lnf/button-border-color)
      ;;                          :action (fn [& _] ))
      (let [subed (reify cadejo.ui.instruments.subedit/InstrumentSubEditor
                    (widgets [this] widget-map)
                    
                    (widget [this key]
                      (get widget-map key))
                    
                    (parent [this] ied)
                    
                    (parent! [this _] ied) 
                    
                    (status! [this msg]
                      (.status! ied msg))
                    
                    (warning! [this msg]
                      (.warning! ied msg))
                    
                    (set-param! [this param value]
                      (.status! this (format "[%s] --> %s" param val))
                      (.set-param! ied param val))
                    
                    (init! [this]
                      (use-b3-gamut ied drw))
                    
                    (sync-ui! [this]
                      (let [flst [(data :r1)(data :r2)(data :r3)
                                  (data :r4)(data :r5)(data :r6)
                                  (data :r7)(data :r8)(data :r9)]]
                        (doseq [[tx freq](map vector txt-freq flst)]
                          (.put-property! tx :text (format "%6.4f" (float freq))))
                        (doseq [r @rbl*](msb/set-multistate-button-state! r 0 false)))))]
        (doseq [r @rbl*]
          (.put-property! r :editor subed))
        subed)))) 
