(ns qbits.ash.plugins.echoweb
  (:require [qbits.ash :as ash]))

(defn handler [bot]
  (ash/listen bot :on-webhook
              :get #"^/test1"
              (fn [request]
                (println "HIT" :test1 )))

  (ash/listen bot :on-webhook
              :get #"^/test2"
              (fn [request]
                (println "HIT" :test2 request))))
