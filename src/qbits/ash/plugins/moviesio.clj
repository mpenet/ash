(ns qbits.ash.plugins.moviesio
  (:require
   [qbits.ash :as ash]
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
  (ash/listen bot :on-message
              (fn [event]
                (when-let [t (second (re-find #"^\?m\s+(.+)" (:content event)))]
                  (ash/reply bot event (search-movie t) true)))))
