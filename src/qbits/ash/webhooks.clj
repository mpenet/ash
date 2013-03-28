(ns qbits.ash.webhooks
  (:require
   [qbits.ash :as ash]
   [ring.adapter.jetty :as http]))

(defonce routes (atom []))

(def register (partial swap! routes conj))

(defn handler [{:as request
                :keys [request-method uri]}]
  (let [hits (for [[method route handler] @routes
                   :when (and (= request-method method)
                              (re-find route uri))]
               (handler request))]
    {:status (if (not-empty hits) 200 404)}))

(defn start-server
  [& options]
  (future (http/run-jetty handler
                          (merge {:port 9999 :host "localhost"}
                                 (into {} options)))))

(defmethod ash/listen :on-webhook
  [bot _ method route handler]
  (register [method route handler])
  bot)
