(ns ash.plugins.duckduckgo
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]))

(defn search
  [term]
  (or (-> (client/get "http://api.duckduckgo.com/"
                   {:query-params {"q" term
                                   "format" "json"}
                    :throw-exceptions false
                    :as :json})
       :body
       :Results
       first
       ((fn [{text :Text url :FirstURL}]
          (when url (format "%s : %s" text url)))))
      (format "No result for %s" term)))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [{:keys [content channel]} _]
                (when-let [t (second (re-find #"^\?d\W+(.+)" content))]
                  (irc/send-message bot
                                    channel
                                    (search t)
                                    true)))))
