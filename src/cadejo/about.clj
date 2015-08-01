(ns cadejo.about
  (:require [cadejo.config]))

(def about-text (format "Cadejo version %s\n

(c) 2014/2015 Steven Jones

Cadejo is released under the terms of the Eclipse Public License"

(cadejo.config/cadejo-version)))
