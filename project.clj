(defproject cc.qbits/ash "0.2.7"
  :description "clojure irc client based on pircbotx, originally forked from clj-irc"
  :repositories [["sonatype" "https://oss.sonatype.org/content/repositories/snapshots/"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.pircbotx/pircbotx "1.9"]

                 ;; plugins deps
                 [clojail "1.0.6"]
                 [ring "1.1.8"]
                 [clj-http "0.7.0"]
                 [org.jsoup/jsoup "1.6.3"]
                 [org.mapdb/mapdb "0.9.1"]]
  ;; :warn-on-reflection true
  )
