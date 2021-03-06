(defproject cadejo "0.4.0"
  :description "MIDI management tool for Overtone"
  :url "https://github.com/plewto/cadejo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {
                 :timeout 120000
                 }
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [seesaw "1.4.4"]
                 [sgwr "0.2.0"]
                 [com.github.insubstantial/substance "7.1"]
                 [overtone "0.9.1"]]
  :jvm-opts ^:replace []
  :main cadejo.core)
