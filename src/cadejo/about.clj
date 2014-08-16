(ns cadejo.about
  (:require [cadejo.config]))

(def about-text (format "Cadejo version %s\n

(c) 2014 Steven Jones

Cadejo is released under the terms of the Eclipse Public License

Special thanks to Sam Aaron, Jeff Rose et al for Overtone.
James MaCartney for the awesome piece of software know as SuperCollider.
Rick Hickey for making Clojure a LISP running on the JVM. 
Dave Ray et al for adding sanity to swing in the form of seesaw."
(cadejo.config/cadejo-version)))
