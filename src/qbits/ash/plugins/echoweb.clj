(ns qbits.ash.plugins.echoweb
  (:require [qbits.ash.bot :as irc]))

(defn handler [bot]
  (irc/listen bot :on-webhook
              :get #"^/test1"
              (fn [request]
                (println "HIT" :test1 )))

  (irc/listen bot :on-webhook
              :get #"^/test2"
              (fn [request]
                (println "HIT" :test2 request))))
