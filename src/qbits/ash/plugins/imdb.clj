(ns qbits.ash.plugins.imdb
  (:require
   [qbits.ash :as ash]
   [clj-http.client :as client]))

(defn search
  [term]
  (or (-> (client/get "http://www.omdbapi.com/?"
                      {:query-params {"t" term}
                       :throw-exceptions false
                       :as :json})
          :body
          ((fn [{title :Title
                 year :Year
                 id :imdbID
                 votes :imdbVotes
                 rating :imdbRating}]
             (when id
               (format "%s (%s): rated %s/10 by %s users ⇒ http://www.imdb.com/title/%s"
                       title year
                       rating
                       votes
                       id)))))
      (format "No result for %s" term)))

(defn handler [bot]
  (ash/listen bot :on-message
              (fn [event]
                (when-let [t (second (re-find #"^\?i\s+(.+)" (:content event)))]
                  (ash/reply bot event (search t) true)))))