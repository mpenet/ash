(ns qbits.ash.webhooks
  (:require
   [qbits.ash :as ash]
   [aleph.http :as http]
   [lamina.core :as lc]))

(defonce routes (atom []))

(def register (partial swap! routes conj))

(defn handler [ch {:as request
                   :keys [request-method uri]}]
  (let [hits (for [[method route handler] @routes
                   :when (and (= request-method method)
                              (re-find route uri))]
               (handler request))]
    (lc/enqueue ch {:status (if (not-empty hits) 200 404)})))

(defn start-server
  [& options]
  (http/start-http-server handler
                          (merge {:port 9999 :host "localhost"}
                                 (into {} options))))

(defmethod ash/listen :on-webhook
  [bot _ method route handler]
  (register [method route handler])
  bot)