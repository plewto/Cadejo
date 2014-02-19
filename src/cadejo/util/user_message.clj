(ns cadejo.util.user-message
  "Provides notification, warnings and error messages to user.")

;; Display warning message
;; args the warning text.
;;
(defn warning [& args]
  (doseq [m args]
    (printf ";; WARNING: %s\n" m))
  (println)
  nil)

;; Display error message and exit Clojure with exit-code
;; args - contents of message
;;
(defn error [exit-code & args]
  (doseq [m args]
    (printf ";; ERROR: %s\n" m))
  (println)
  (System/exit exit-code))

;; Test for expected value
;;
;; predicate - single argument test function
;; test-value - if (predicate test-value) is true, return true
;;              if (predicate test-value) is false, 
;;                 use either warning or error to display message
;;                 see exit-code
;; location - String, description of class/function where test is being made.
;; expected - String description of expected value
;; exit-code - int, if exit code is zero and test fails use warning to 
;;             display message and return false. If exit-code is non-zero
;;             and test fails terminate Clojure using error. (default 0)
(defn expect 
  ([predicate test-value location expected exit-code]
     (if (not (predicate test-value))
       (let [msg [(format "location    : %s" location)
                  (format "expected    : %s" expected)
                  (format "encountered : %s" test-value)]]
         (if (zero? exit-code)
             (apply #'warning msg)
             (apply #'error (cons exit-code msg)))
         false)
       true))
  ([predicate test-value location expected]
     (expect predicate test-value location expected 0)))
  
(defn depreciated [& args]
  (apply warning (cons "DEPRECIATED" args)))
