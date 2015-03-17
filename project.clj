(defproject cadejo "0.2.1-SNAPSHOT"
  :description "MIDI management tool for Overtone"
  :url "https://github.com/plewto/cadejo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [seesaw "1.4.4"]
                 [com.github.insubstantial/substance "7.1"]
                 [overtone "0.9.1"]]
  :main cadejo.core
  ;:main cadejo.ui.util.lnf
  )
