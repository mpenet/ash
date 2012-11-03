(ns ash.plugins.google
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]))

(defn ask-google
  [term]
  (or (-> (client/get "http://ajax.googleapis.com/ajax/services/search/web"
                   {:query-params {"v" "1.0" "q" term}
                    :as :json})
       :body
       :responseData
       :results
       first
       :unescapedUrl)
      (format "No result for %" term)))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [m _]
                (when (re-find #"^\?.+" (:content m))
                  (irc/send-message bot
                                    (:channel m)
                                    (ask-google (.substring (:content m) 1))
                                    true)))))