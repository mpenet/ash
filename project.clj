(defproject cc.qbits/ash "0.2.2"
  :description "clojure irc client based on pircbotx, originally forked from clj-irc"
  :repositories [["sonatype" "https://oss.sonatype.org/content/repositories/snapshots/"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.pircbotx/pircbotx "1.7"]
                 [aleph "0.3.0-beta7"]

                 ;; plugins deps
                 [clojail "1.0.3"]
                 [clj-http "0.5.7"]
                 [org.jsoup/jsoup "1.6.3"]
                 [org.mapdb/mapdb "0.9-SNAPSHOT"]
                 ]
  ;; :warn-on-reflection true
  )
