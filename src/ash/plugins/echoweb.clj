(ns ash.plugins.echoweb
  (:require [ash.bot :as irc]))

(defn handler [bot]
  (irc/listen bot :on-web-hook
              :get #"^/test1"
              (fn [request]
                (println "HIT" :test1 )))

  (irc/listen bot :on-web-hook
              :get #"^/test2"
              (fn [request]
                (println "HIT" :test2 request))))
