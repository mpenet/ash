(ns ash.plugins.moviesio
  (:require
   [ash.bot :as irc]
   [clj-http.client :as client]))

(defn search-movie
  [title]
  (or (->> (client/get "http://api.movies.io/movies/search"
                   {:query-params {"q" title}
                    :throw-exceptions false
                    :as :json})
       :body :movies first
       ((fn [{:keys [title year id]
              :as movie}]
          (when movie
            (format "%s (%s): http://movies.io/m/%s"
                    title year id)))))
      (format "No result for %s" title)))

(defn handler [bot]
  (irc/listen bot :on-message
              (fn [m _]
                (when-let [t (second (re-find #"^\?m\W+(.+)" (:content m)))]
                  (irc/send-message bot
                                    (:channel m)
                                    (search-movie t)
                                    true)))))
