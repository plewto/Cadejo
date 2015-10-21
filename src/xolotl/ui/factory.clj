(ns xolotl.ui.factory
  (:require [xolotl.util :as util])
  ;(:require [seesaw.border :as ssb])
  (import
   java.awt.BorderLayout
   java.awt.Color
   java.awt.GridLayout
   java.awt.event.ActionListener
   javax.swing.BorderFactory
   javax.swing.Box
   javax.swing.BoxLayout
   javax.swing.JButton
   javax.swing.ButtonGroup
   javax.swing.JCheckBox
   javax.swing.JLabel
   javax.swing.JPanel
   javax.swing.JRadioButton
   javax.swing.JScrollPane
   javax.swing.JSpinner
   javax.swing.JTextArea
   javax.swing.JTextField
   javax.swing.JToggleButton
   javax.swing.SpinnerNumberModel
   javax.swing.event.CaretListener
   ))


(def error-background (Color. 255 206 205))
(def clipboard* (atom ""))


;; Returns empty border
;;
(defn padding
  ([thickness]
   (BorderFactory/createEmptyBorder thickness thickness thickness thickness))
  ([]
   (padding 4)))

;; (defn horizontal-panel
;;   ([]
;;    (let [pan (JPanel.)
;;          bx (BoxLayout. pan BoxLayout/Y_AXIS)]
;;      (.setLayout pan bx)
;;      (.setBorder pan (padding))
;;      pan))
;;   ([& items]
;;    (let [pan (horizontal-panel)]
;;      (doseq [q items]
;;        (.add pan q))
;;      pan)))

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


(defn border-panel [& {:keys [north east south west center]
                       :or {north nil
                            east nil
                            south nil
                            west nil
                            center nil}}]
  (let [layout (BorderLayout. 8 8)
        pan (JPanel. layout)]
    (if north (.add pan north BorderLayout/NORTH))
    (if east (.add pan east BorderLayout/EAST))
    (if south (.add pan south BorderLayout/SOUTH))
    (if west (.add pan west BorderLayout/WEST))
    (if center (.add pan center BorderLayout/CENTER))
    pan))

(defn spinner[mn mx step & {:keys [listener]
                             :or {listener nil}}]
   (let [nm (SpinnerNumberModel. (int mn)(int mn)(int mx)(int step))
         sp (JSpinner. nm)]
     (if listener (.addChangeListener sp listener))
     sp))

(defn spinner-panel [label mn mx step & {:keys [listener]
                                         :or {listener nil}}]
  (let [sp (spinner mn mx step :listener listener)
        lab (JLabel. (str label))
        pan (border-panel :center sp :east lab)]
    {:spinner sp
     :label lab
     :pan-main pan}))


(defn button [label & {:keys [listener]
                       :or {listener nil}}]
  (let [b (JButton. (str label))]
    (if listener
      (.addActionListener b listener))
    b))

(defn checkbox [label & {:keys [listener]
                         :or {listener nil}}]
  (let [cb (JCheckBox. (str label))]
    (if listener (.addActionListener cb listener))
    cb))

;; specs - nested list [[lab1 key1][lab2 key2]....]
;; radio button created for each sublist
;;   lab - lable text
;;   key - unique keyword
;;
;; btype may be :radio or :toggle (default :radio)
;;
(defn radio [specs rows cols & {:keys [listener btype]
                                :or {listener nil
                                     btype :radio}}]
  (let [pan (JPanel. (GridLayout. rows cols))
        buttons* (atom {})
        grp (ButtonGroup.)]
    (doseq [s (seq specs)]
      (let [label (first s)
            key (second s)
            rb (if (= btype :toggle)
                 (JToggleButton. (str label))
                 (JRadioButton. (str label)))]
        (.add grp rb)
        (.putClientProperty rb :id key)
        (if listener (.addActionListener rb listener))
        (.add pan rb)
        (swap! buttons* (fn [q](assoc q key rb)))))
    {:pan-main pan
     :group grp
     :buttons @buttons*}))


(defn text-field
  ([label]
   (let [lab (JLabel. (str label))
         tf (JTextField.)
         pan (border-panel :center tf
                           :east lab)]
     {:pan-main pan
      :text-field tf
      :label lab}))
  ([](text-field "")))

;; validator is function (fn text)
;;    returns nil if text valid
;;    returns string error message is text not valid
;; enter-action is function (fn text)
;; 
(defn text-area [label validator enter-action editor]
  (let [lab (JLabel. (str label))
        ta (JTextArea. "")
        safe-background (.getBackground ta)
        error* (atom false)
        sp (JScrollPane. ta)
        jb-clear (JButton. "Clear")
        jb-copy (JButton. "Copy")
        jb-paste (JButton. "Paste")
        jb-enter (JButton. "Enter")
        pan-south (JPanel. (GridLayout. 1 4))
        pan-main (border-panel :center sp :south pan-south :north lab)]
    (doseq [b [jb-clear jb-copy jb-paste jb-enter]]
      (.add pan-south b))
    (.addCaretListener ta (proxy [CaretListener][]
                            (caretUpdate [& _]
                              (let [txt (.getText ta)
                                    err (validator txt)]
                                (if err
                                  (do
                                    (reset! error* err)
                                    (.setBackground ta error-background))
                                  (do
                                    (reset! error* false)
                                    (.setBackground ta safe-background)))))))
    (.addActionListener jb-enter (proxy [ActionListener][]
                                   (actionPerformed [_]
                                     (if @error*
                                       (.warning! editor @error*)
                                       (do
                                         (enter-action (.getText ta))
                                         (.status! editor (format "%s updated" label)))))))

    (.addActionListener jb-clear (proxy [ActionListener][]
                                   (actionPerformed [_]
                                     (let [text (.getText ta)]
                                       (if (pos? (count text))
                                         (do 
                                           (reset! clipboard* text)
                                           (.setText ta "")
                                           (.status! editor (format "%s copied to clipboard" label))))))))

    (.addActionListener jb-copy (proxy [ActionListener][]
                                  (actionPerformed [_]
                                    (let [text (.getText ta)]
                                      (if (pos? (count text))
                                        (do
                                          (reset! clipboard* text)
                                          (.status! editor (format "%s copied to clipboard" label))))))))
    (.addActionListener jb-paste (proxy [ActionListener][]
                                   (actionPerformed [_]
                                     (let [old (.getText ta)
                                           new @clipboard*]
                                       (.setText ta new)
                                       (reset! clipboard* old)
                                       (.status! editor (format "Clipboard pasted to %s, original text saved to clipboard." label))))))
    {:pan-main pan-main
     :text-area ta
     :scroll-pane sp}))
    




