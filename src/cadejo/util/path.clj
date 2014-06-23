(ns cadejo.util.path
  "Provides utilities for filename manipulation"
  (:import java.io.File))

(def home-token "~")              ; token representing user's home
(def current-directory-token ".")
(def parent-directory-token "..")
(def cadejo-directory-token "!")  ; token for user's cadejo directory

(def user-home (System/getProperty "user.home"))


;; ISSUE: PORTABILITY
;; cadejo-directory should hold the absolute path to the user's cadejo 
;; configuration directory.
;; The following assignment ultimately is set to cadejo-directory according
;; to the operating system.  On Linux (and presumably OSx) this
;; directory is ~/.cadejo
;; On windows the proposed directory is ~/AppData/cadejo
;; Currently only Linux us supported.
;;
(def cadejo-directory
  (let [os (System/getProperty "os.name")]
        (cond
         :default
         (format "%s/.cadejo" user-home))))

(def file-separator (System/getProperty "file.separator"))

(defn- process-path-element [s]
  (cond (= s home-token)
        (process-path-element user-home)

        (= s cadejo-directory-token)
        (process-path-element cadejo-directory)

        (.startsWith s file-separator)
        (process-path-element (subs s 1))

        (.endsWith s file-separator)
        (process-path-element (subs s 0 (dec (count s))))

        :default
        s))

(defn join [& elements]
  "join filename elements 
   (join foo bar baz) --> foo/bar/baz
   User's home token ~ and cadejo directory token ! are replaced with
   appropriate locations.

   All arguments should be strings,
   Returns string."
  (let [sb (StringBuilder. 80)]
    (doseq [a elements]
      (let [ele (process-path-element a)]
        (if ele
          (.append sb (format "%s%s" file-separator ele)))))
    (let [rs (.toString sb)]
      (if (.startsWith rs (format "%s" file-separator file-separator))
        (subs rs 1)
        rs))))

(defn split [str]
  "Inverse of join
   Takes filename string and returns vector of path components
   (split foo/bar/baz) --> [foo bar baz]"
  (clojure.string/split str (re-pattern file-separator)))

(defn split-extension [filename]
  "Splits filename at location of terminating extension. 
   The extension component begins the text following the final period
   Returns a two element list (head tail)

   (split-extension foo)         --> (foo '')
   (split-extension foo.bar)     --> (foo bar)
   (split-extension foo.bar.baz) --> (foo.bar baz)
   (split-extension .foo)        --> ('' foo)"
  (let [sb (StringBuilder. filename)
        pos (- (.length filename)(.indexOf (.toString (.reverse sb)) "."))]
    (if (not (= (dec pos)(count filename)))
      (list (subs filename 0 (dec pos))
            (subs filename pos))
      (list filename ""))))

(defn has-extension? [filename ext]
  "Predicate returns true if filename has extension ext.
   Do not include period in ext

   (has-extension? foo.bar bar) --> true
   (has-extension? foo.bar baz) --> false"
  (.endsWith filename (str "." ext)))

(defn append-extension [filename ext]
  "Appends extension ext to filename if it does not already 
   exists.
 
   (append-extension foo.bar bar) --> foo.bar
   (append-extension foo.bar baz) --> foo.bar.baz"
  (if (not (has-extension? filename ext))
    (str filename "." ext)
    filename))

(defn replace-extension [filename ext]
  "Replace current extension with extension ext
   
   (replace-extension foo bar)     --> foo.bar
   (replace-extension foo.bar bar) --> foo.bar
   (replace-extension foo.bar baz) --> foo.baz"
  (append-extension (first (split-extension filename)) ext))

(defn exists [filename]
  "Predicate returns true if filename exists"
  (let [f (File. filename)]
    (.exists f)))
