(ns xolotl.ui.factory
  (:require [xolotl.util :as util])
  (:require [seesaw.font :as ssf])
  (:require [seesaw.core :as ss])
  (import
   java.awt.BorderLayout
   java.awt.Color
   java.awt.GridLayout
   java.awt.event.ActionListener
   javax.swing.BorderFactory
   javax.swing.Box
   javax.swing.BoxLayout
   javax.swing.JPanel
   javax.swing.SwingConstants
   javax.swing.event.CaretListener))

(def msg00 "Text saved to undo")
(def msg01 "Text saved to clipboard")
(def msg02 "Text pasted from clipboard")
(def msg03 "Text Undo, click undo again to restore")
(def msg04 "%s values entered")

(def error-background (Color. 255 206 205))
(def int-clipboard* (atom "")) ;; for controllers & velocity
(def pitch-clipboard* (atom ""))
(def rhythm-clipboard* (atom ""))
(def hold-clipboard* (atom ""))

(def large-font (ssf/font :name :serif :style :plain :size 16))
(def normal-font (ssf/font :name :sans-serif :style :plain :size 12))
(def small-font (ssf/font :name :sans-serif :style :plain :size 10))
(def pattern-font (ssf/font :name :monospaced :style :bold :size 12))
(def small-mono (ssf/font :name :monospaced :style :bold :size 8))   ; 9
(def bold-mono (ssf/font :name :monospaced :style :bold :size 12))

(def font-map {:small small-font
               :normal normal-font
               :large large-font
               :small-mono small-mono
               :bold-mono bold-mono})

(def size-map {:small [64 :by 18]     ; 64 x 20 
               :normal [64 :by 24]
               :large [64 :by 64]})


;; Returns empty border
;;
(defn padding
  ([thickness]
   (BorderFactory/createEmptyBorder thickness thickness thickness thickness))
  ([]
   (padding 4)))

(defn border [title]
  (BorderFactory/createTitledBorder (str title))
  )


(defn horizontal-strut
  ([n]
   (Box/createHorizontalStrut n))
  ([]
   (horizontal-strut 16)))


(defn vertical-strut
  ([n]
   (Box/createVerticalStrut n))
  ([]
   (vertical-strut 16)))


(defn horizontal-panel [& items]
  (let [pan (JPanel.)
        bx (BoxLayout. pan BoxLayout/X_AXIS)]
    (.setLayout pan bx)
    (doseq [q items]
      (.add pan q))
    pan))

(defn vertical-panel [& items]
  (let [pan (JPanel.)
        bx (BoxLayout. pan BoxLayout/Y_AXIS)]
    (.setLayout pan bx)
    (doseq [q items]
      (.add pan q))
    pan))

(defn grid-panel [rows cols & items]
  (let [pan (JPanel. (GridLayout. rows cols))]
    (doseq [q items]
      (.add pan q))
    (.setBorder pan (padding))
    pan))

(defn border-panel [& {:keys [north east south west center border]
                       :or {north nil
                            east nil
                            south nil
                            west nil
                            center nil
                            border nil}}]
  (let [layout (BorderLayout. 8 8)
        pan (JPanel. layout)]
    (if north (.add pan north BorderLayout/NORTH))
    (if east (.add pan east BorderLayout/EAST))
    (if south (.add pan south BorderLayout/SOUTH))
    (if west (.add pan west BorderLayout/WEST))
    (if center (.add pan center BorderLayout/CENTER))
    (if border (.setBorder pan border))
    pan))


(defn label [text & {:keys [font center]
                     :or {font :normal
                          center false}}]
  (let [lab (ss/label :text text :font (font font-map))]
    (if center
      (.setHorizontalAlignment lab SwingConstants/CENTER))
    lab))


(defn spinner[mn mx step & {:keys [listener]
                             :or {listener nil}}]
  (let [nm (ss/spinner-model mn :from mn :to mx :by step)
        sp (ss/spinner :model nm)]
     (if listener (.addChangeListener sp listener))
     sp))

(defn spinner-panel [lab-text mn mx step & {:keys [font listener]
                                         :or {font :normal
                                              listener nil}}]
  (let [sp (spinner mn mx step :listener listener)
        lab (label lab-text :font font)
        pan (border-panel :center sp :east lab)]
    {:spinner sp
     :label lab
     :pan-main pan}))

(defn button [label & {:keys [font size]
                       :or {font :normal
                            size nil}}]
  (let [fnt (font font-map)
        sz (cond (vector? size) size
                 (keyword? size)(size size-map)
                 :else (font size-map))
        b (ss/button :text (str label) :font fnt :size sz)]
    b))

(defn checkbox [label & {:keys [font]
                         :or {font :normal}}]
  (ss/checkbox :text (str label) :font (font font-map)))


;; Creates grid panel of radio/toggle buttons
;;
;; specs - nested list [[lab1 key1][lab2 key2]....]
;; radio button created for each sublist
;;   lab - lable text
;;   key - unique keyword
;;
;; btype may be :radio or :toggle (default :radio)
;;
(defn radio [specs rows cols & {:keys [listener btype font size]
                                :or {listener nil
                                     font :normal
                                     size nil
                                     btype :radio}}]
  (let [pan (JPanel. (GridLayout. rows cols))
        buttons* (atom {})
        grp (ss/button-group)
        fnt (font font-map)
        sz (cond (vector? size) size
                 (keyword? size)(size size-map)
                 :else (size-map font))]
    (doseq [s (seq specs)]
      (let [label (first s)
            key (second s)
            rb (if (= btype :toggle)
                 (ss/toggle :text  (str label) :group grp :font fnt :size sz)
                 (ss/radio :text (str label) :group grp :font fnt))]
        (.add grp rb)
        (.putClientProperty rb :id key)
        (if listener (.addActionListener rb listener))
        (.add pan rb)
        (swap! buttons* (fn [q](assoc q key rb)))))
    {:pan-main pan
     :group grp
     :buttons @buttons*}))

(defn text-field
  ([lab-text]
   (let [lab (label lab-text)
         tf (ss/text :multi-line? false :font pattern-font :margin 5
                     :size [270 :by 24])
         pan (border-panel :center tf
                           :east lab)]
     {:pan-main pan
      :text-field tf
      :label lab}))
  ([](text-field "")))


;; Creates composite text field and toolbar
;; ARGS:
;;   lab-text     - String, the panel's title text
;;   validator    - Function used to test validity of text.
;;                  Takes a single String argument and returns nil
;;                  if text is valid. If text is not valid it should
;;                  return a String indicating what the problem is.
;;                  If the text is invalid the background is set to an
;;                  alarming color and clicking the enter-key will
;;                  cause an error message on the status line. 
;;   enter-action - Function executed when user clicks enter-key.
;;                  It should take a single String argument which is
;;                  presumably parsed and then by side-effect updates
;;                  some state.
;;   clipboard*   - Atom String, use one of int-clipboard* pitch-clipboard*
;;                  rhythm-clipboard* or hold-clipboard*
;;   editor       - An instance of xolotl editor where this component lives.
;;
;; RETURNS: map with the following keys
;;           :pan-main -> JPanel
;;           :text-area -> JTextArea
;;           :scroll-pane -> JScrollPane
;;
(defn text-editor [lab-text validator enter-action clipboard* editor]
  (let [undo* (atom "")
        error* (atom true)
        lab (label lab-text :font :normal)
        ta (ss/text :multi-line? true :editable? true
                    :margin 10 :font pattern-font)
        safe-background (.getBackground ta)
                                        ;sp (JScrollPane. ta)
        sp (ss/scrollable ta)
        jb-clear (button "Clear" :font :small)
        jb-copy (button "Copy" :font :small)
        jb-paste (button "Paste" :font :small)
        jb-undo (button "Undo" :font :small)
        jb-enter (button "Enter" :font :small)
        pan-tools (grid-panel 3 2 jb-clear jb-copy jb-paste jb-undo jb-enter)
        pan-south (vertical-panel pan-tools)
        pan-main (border-panel :center sp
                               :north lab
                               :south pan-south)
        clear-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (reset! undo* (.getText ta))
                         (.setText ta "")
                         (.status! editor msg00)))
        copy-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (reset! clipboard* (.getText ta))
                        (.status! editor msg01)))
        paste-action (proxy [ActionListener][]
                       (actionPerformed [_]
                         (reset! undo* (.getText ta))
                         (.setText ta @clipboard*)
                         (.status! editor msg02)))
        undo-action (proxy [ActionListener][]
                      (actionPerformed [_]
                        (let [tx (.getText ta)]
                          (.setText ta @undo*)
                          (reset! undo* tx)
                          (.status! editor msg03))))]
    (.addCaretListener ta (proxy [CaretListener][]
                            (caretUpdate [& _]
                              (let [txt (.getText ta)
                                    err (validator txt)]
                                (if err
                                  (do
                                    (reset! error* err)
                                    (.warning! editor err)
                                    (.setBackground ta error-background))
                                  (do
                                    (reset! error* false)
                                    (.status! editor "")
                                    (.setBackground ta safe-background)))))))
    (.addActionListener jb-clear clear-action)
    (.addActionListener jb-copy copy-action)
    (.addActionListener jb-paste paste-action)
    (.addActionListener jb-undo undo-action)
    (.addActionListener jb-enter
                        (proxy [ActionListener][]
                          (actionPerformed [_]
                            (if @error*
                              (.warning! editor @error*)
                              (do
                                (enter-action (.getText ta))
                                (.status! editor (format msg04 lab-text)))))))
    {:pan-main pan-main
     :text-area ta
     :scroll-pane sp})) 
