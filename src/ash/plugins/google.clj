(ns ash.plugins.google
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]))

(defn ask-google
  [term]
  (or (-> (client/get "http://ajax.googleapis.com/ajax/services/search/web"
                   {:query-params {"v" "1.0" "q" term}
                    :throw-exceptions false
                    :as :json})
       :body
       :responseData
       :results
       first
       :unescapedUrl)
      (format "No result for %s" term)))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [{:keys [content channel]} _]
                (when-let [t (second (re-find #"^\?g\W+(.+)" content))]
                  (irc/send-message bot
                                    channel
                                    (ask-google t)
                                    true)))))
